package com.woilsy.mock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.woilsy.mock.annotations.MockExclude;
import com.woilsy.mock.annotations.MockInclude;
import com.woilsy.mock.constants.HttpMethod;
import com.woilsy.mock.constants.MockDefault;
import com.woilsy.mock.data.DataSource;
import com.woilsy.mock.entity.*;
import com.woilsy.mock.options.MockOptions;
import com.woilsy.mock.parse.MockDataStore;
import com.woilsy.mock.parse.MockParse;
import com.woilsy.mock.service.MockService;
import com.woilsy.mock.strategy.MockPriority;
import com.woilsy.mock.strategy.MockStrategy;
import com.woilsy.mock.utils.GsonUtil;
import com.woilsy.mock.utils.LogUtil;
import com.woilsy.mock.utils.NetUtil;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Mocker {

    /**
     * 启动器单例
     */
    private static final Mocker MOCKER = new Mocker();

    /**
     * mock数据中心
     */
    private MockDataStore mDataStore;

    /**
     * mock配置
     */
    private MockOptions mMockOptions;

    /**
     * 存储配置相关
     */
    private SharedPreferences spConfig;

    private Mocker() {

    }

    private void startMockService(Context context, MockOptions options) {
        MockService.start(context, options);
    }

    private void initByOptions(MockOptions options) {
        this.mMockOptions = options == null ? MockOptions.getDefault() : options;
        this.mDataStore = new MockDataStore(new MockParse(mMockOptions));
        //导入数据
        DataSource[] dataSources = mMockOptions.getDataSources();
        if (dataSources != null) {
            for (DataSource dataSource : dataSources) {
                List<MockData> mockData = dataSource.getMockData();
                for (MockData mockDatum : mockData) {
                    if (mockDatum.method == null || mockDatum.method.isEmpty()) {
                        LogUtil.e("没有在数据源path" + mockDatum.path + "中发现请求方式，将被忽略");
                    } else {
                        HttpInfo httpInfo = new HttpInfo(HttpMethod.valueOf(mockDatum.method), mockDatum.path);
                        mDataStore.put(httpInfo, new HttpData(GsonUtil.toJson(mockDatum.data)));
                    }
                }
            }
        }
    }

    private void parseObjs(MockObj... objs) {
        for (MockObj mockObj : objs) {
            try {
                Class<?> mockClass = mockObj.getMockClass();
                handleMockObj(mockClass, mockObj.getMockStrategy());
            } catch (Exception e) {
                LogUtil.e("解析Service失败，以下为错误信息↓", e);
            }
        }
    }

    private void parseGroups(MockGroup... mockGroups) {
        for (MockGroup group : mockGroups) {
            try {
                MockStrategy mockStrategy = group.getMockStrategy();
                for (Class<?> mockObj : group.getObjs()) {
                    handleMockObj(mockObj, mockStrategy);
                }
            } catch (Exception e) {
                LogUtil.e("解析Service失败，以下为错误信息↓", e);
            }
        }
    }

    private void handleMockObj(Class<?> mockClass, MockStrategy mockStrategy) {
        if (mockClass.isInterface()) {
            parseClass(mockClass, mockStrategy);
        } else {
            LogUtil.e(mockClass.getName() + "非接口，无法处理");
        }
    }

    private void parseClass(Class<?> cls, MockStrategy mockStrategy) {
        Method[] methods = cls.getMethods();
        String ss = mockStrategy == MockStrategy.EXCLUDE ?
                "默认解析：除了被@MockExclude标记的函数都解析，其他Method将拦截" :
                "默认不解析：仅解析被@MockInclude标记的函数，其他Method将放行";
        LogUtil.i(cls.getSimpleName() + "当前Method解析策略为->" + ss);
        for (Method m : methods) {
            if (mockStrategy == MockStrategy.EXCLUDE) {
                MockExclude mockExclude = m.getAnnotation(MockExclude.class);
                if (mockExclude == null) {
                    parseMethod(m, MockPriority.DEFAULT);
                } else {
                    parseMethod(m, null);//被排除的函数
                }
            } else if (mockStrategy == MockStrategy.INCLUDE) {
                MockInclude mockInclude = m.getAnnotation(MockInclude.class);
                if (mockInclude != null) {
                    parseMethod(m, mockInclude.priority());
                } else {
                    parseMethod(m, null);//不被包含的函数
                }
            }
            LogUtil.i("---------------分割线---------------");
        }
    }

    private void parseMethod(Method m, MockPriority mockPriority) {
        LogUtil.i("====== 开始解析 " + m.getName() + " ======");
        //类型本身一般没有什么意义 需要注意的是该类型中的泛型 以及ResponseBody的处理
        HttpInfo httpInfo = getHttpInfo(m);
        if (httpInfo != null) {
            httpInfo.setMockPriority(mockPriority);//设置优先级
            String path = httpInfo.getPath();
            if (path == null || path.isEmpty()) {//需要动态解析
                LogUtil.i("暂不支持的url");
            } else {
                LogUtil.i("path:" + path);
                //添加到集合
                if (mDataStore.containsKey(httpInfo)) {//此httpInfo不和原先的HttpInfo相同但hashcode一致 故需要重新插入
                    HttpData data = mDataStore.get(httpInfo);
                    mDataStore.remove(httpInfo);
                    mDataStore.put(httpInfo, data);
                } else {
                    if (mMockOptions.isDynamicAccess()) {
                        LogUtil.i("动态访问，只存储返回类型");
                        mDataStore.put(httpInfo, new HttpData(m.getGenericReturnType()));
                    } else {
                        String json = parseType(m.getGenericReturnType());
                        LogUtil.i("非动态访问，数据:" + json);
                        mDataStore.put(httpInfo, new HttpData(json));
                    }
                }
            }
        }
        LogUtil.i("====== 停止解析 " + m.getName() + " ======");
    }

    //分析静态url
    private HttpInfo getHttpInfo(Method m) {
        Annotation[] annotations = m.getAnnotations();
        for (Annotation a : annotations) {
            if (a instanceof GET) {
                return new HttpInfo(HttpMethod.GET, transString(((GET) a).value()));
            } else if (a instanceof POST) {
                return new HttpInfo(HttpMethod.POST, transString(((POST) a).value()));
            } else if (a instanceof DELETE) {
                return new HttpInfo(HttpMethod.DELETE, transString(((DELETE) a).value()));
            } else if (a instanceof PUT) {
                return new HttpInfo(HttpMethod.PUT, transString(((PUT) a).value()));
            } else {
                LogUtil.i(a.getClass() + "不支持的注解类型，目前仅支持GET POST DELETE PUT");
            }
        }
        return null;
    }

    private String transString(String s) {
        return s == null || s.isEmpty() ? null : s;
    }

    private void createSharedPreferences(Context context) {
        spConfig = context.getSharedPreferences("mock_config", Context.MODE_PRIVATE);
    }

    public static void init(Context context, MockOptions options, MockObj... objs) {
        Mocker mocker = MOCKER;
        mocker.createSharedPreferences(context);
        mocker.initByOptions(options);
        mocker.startMockService(context, options);
        mocker.parseObjs(objs);
    }

    public static void init(Context context, MockOptions options, MockGroup... groups) {
        Mocker mocker = MOCKER;
        mocker.createSharedPreferences(context);
        mocker.initByOptions(options);
        mocker.startMockService(context, options);
        mocker.parseGroups(groups);
    }

    public static void release(Context context) {
        context.stopService(new Intent(context, MockService.class));
    }

    public static String getHttpData(String path, String method) {
        HttpInfo httpInfo = findHttpInfo(path, method);
        if (httpInfo == null) {
            return null;
        } else {
            return HttpData.getData(getMockDataStore().get(httpInfo));
        }
    }

    public static HttpInfo findHttpInfo(String encodedPath, String method) {
        //encodedPath是一个实际地址 例如 /op/globalModuleConfig/{location} 但其实际地址 /op/globalModuleConfig/5 所以需要进行匹配
        String[] split1 = encodedPath.split("/");
        Set<HttpInfo> httpInfos = getMockDataStore().keySet();
        List<HttpInfo> matchList = new ArrayList<>();
        for (HttpInfo httpInfo : httpInfos) {
            String path = httpInfo.getPath();
            String[] split2 = path.split("/");
            int len = split2.length;
            if (split1.length == len) {//长度一致
                int count = 0;
                int dCount = 0;
                for (int i = 0; i < len; i++) {
                    if (split1[i].equals(split2[i])) {
                        count++;
                    } else if (split2[i].startsWith("{")) {
                        dCount++;
                    }
                }
                if (method.equalsIgnoreCase(httpInfo.getHttpMethod().name())) {//方法一致
                    if (count == len) {//完全匹配
                        matchList.add(0, httpInfo);//添加到起始点 结束循环
                        break;
                    } else if (len == count + dCount) {//部分匹配 继续循环
                        matchList.add(httpInfo);
                    }
                }
            }
        }
        return matchList.size() > 0 ? matchList.get(0) : null;
    }

    public static String parseType(Type type) {
        Object o = getMockDataStore().parseType(type);
        if (o != null) {
            return GsonUtil.toJson(o);
        } else {
            return null;
        }
    }

    public static MockDataStore getMockDataStore() {
        if (MOCKER.mDataStore == null) {
            MOCKER.mDataStore = new MockDataStore(new MockParse(getMockOption()));
        }
        return MOCKER.mDataStore;
    }

    public static MockOptions getMockOption() {
        if (MOCKER.mMockOptions == null) {
            MOCKER.mMockOptions = MockOptions.getDefault();
        }
        return MOCKER.mMockOptions;
    }

    public static void setMockUrlOrOriginalUrl(boolean baseUrlOrOriginalUrl) {
        if (MOCKER.spConfig != null) {
            MOCKER.spConfig.edit().putBoolean("mockOrOriginal", baseUrlOrOriginalUrl).apply();
        }
    }

    public static boolean isMockUrlOrOriginalUrl() {
        return MOCKER.spConfig != null && MOCKER.spConfig.getBoolean("mockOrOriginal", true);
    }

    public static String getMockBaseUrl() {
        return MockDefault.formatMockUrl(getMockOption().getPort());
    }

    public static String getLocalUrl(Context context) {
        String ip = NetUtil.getIp(context);
        String host = ip == null ? MockDefault.HOST_NAME : ip;
        return MockDefault.formatMockUrl(host, getMockOption().getPort());
    }

    public static boolean isInit() {
        return MOCKER.mMockOptions != null;
    }
}
