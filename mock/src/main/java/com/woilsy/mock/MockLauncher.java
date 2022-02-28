package com.woilsy.mock;

import android.content.Context;
import android.content.Intent;

import com.woilsy.mock.annotations.MockExclude;
import com.woilsy.mock.annotations.MockInclude;
import com.woilsy.mock.constants.HttpMethod;
import com.woilsy.mock.constants.MockDefault;
import com.woilsy.mock.data.MockUrlData;
import com.woilsy.mock.entity.HttpInfo;
import com.woilsy.mock.entity.MockObj;
import com.woilsy.mock.options.MockOptions;
import com.woilsy.mock.parse.MockParse;
import com.woilsy.mock.service.MockService;
import com.woilsy.mock.strategy.MockStrategy;
import com.woilsy.mock.utils.GsonUtil;
import com.woilsy.mock.utils.LogUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * 启动器，目前支持POST/DELETE/GET/PUT 四种请求<br/>
 * <br/>
 * 如果需要修改部分配置，如log打印，可以通过MockOptions设置<br/>
 * <br/>
 * 某些策略说明：<br/>
 * 1,如果字段有默认值，那么不会处理该字段，直接返回默认值数据。<br/>
 * 2,返回值为ResponseBody时或需要自定义mock数据时，需要自行新建一个mock数据文件，以<br/>
 * [<br/>
 * {<br/>
 * "path":"/xxx"<br/>
 * "data":{} <br/>
 * }<br/>
 * ]<br/>
 * 的形式传入，可以放在assets文件中，上线前删除该文件，在启动前调用MockUrlData.addFromXXX导入。<br/>
 * 3,如果使用了动态url(@Url String url)，由于其可能不访问MockOptions.BASE_URL，所以暂时无法处理。<br/>
 */
public class MockLauncher {

    /**
     * 启动器单例
     */
    private static final MockLauncher LAUNCHER = new MockLauncher();

    /**
     * 被包含的url结合，将使用mock url请求
     */
    public static final Map<String, HttpInfo> includeInfoMap = new HashMap<>();
    /**
     * 被排除的url集合，将使用原始url请求
     */
    public static final Map<String, HttpInfo> excludeInfoMap = new HashMap<>();

    /**
     * mock数据解析器
     */
    private MockParse mockParse;

    /**
     * mock配置
     */
    private MockOptions mockOptions;

    /**
     * mock地址或者原始url
     * mock地址：127.0.0.1
     * 原始url：http://xxx.xxx.com:8080
     */
    private boolean baseUrlOrOriginalUrl = true;

    private MockLauncher() {
    }

    public static MockOptions getMockOption() {
        return LAUNCHER.mockOptions;
    }

    public static void setMockUrlOrOriginalUrl(boolean baseUrlOrOriginalUrl) {
        LAUNCHER.baseUrlOrOriginalUrl = baseUrlOrOriginalUrl;
    }

    public static boolean isMockUrlOrOriginalUrl() {
        return LAUNCHER.baseUrlOrOriginalUrl;
    }

    public static void start(Context context, MockOptions options, MockObj... objs) {
        LAUNCHER.initByOptions(options);
        LAUNCHER.startMockService(context, options);
        LAUNCHER.parseClasses(objs);
    }

    private void startMockService(Context context, MockOptions options) {
        MockService.start(context, options);
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, MockService.class));
    }

    public static String getMockBaseUrl() {
        return MockDefault.formatMockUrl(LAUNCHER.mockOptions.getPort());
    }

    private void initByOptions(MockOptions options) {
        MockOptions mMockOptions = options == null ? MockOptions.getDefault() : options;
        this.mockOptions = mMockOptions;
        this.mockParse = new MockParse(mMockOptions);
        //导入数据
        MockUrlData.add(mMockOptions.getDataSources());
    }

    private void parseClasses(MockObj... objs) {
        for (MockObj mockObj : objs) {
            try {
                Class<?> mockClass = mockObj.getMockClass();
                if (mockClass.isInterface()) {
                    parse(mockClass, mockObj.getMockStrategy());
                } else {
                    LogUtil.e(mockClass.getName() + "非接口，无法处理");
                }
            } catch (Exception e) {
                LogUtil.e("解析Service失败，以下为错误信息↓", e);
            }
        }
    }

    private void parse(Class<?> cls, MockStrategy mockStrategy) {
        Method[] methods = cls.getMethods();
        String ss = mockStrategy == MockStrategy.RESOLVE_WITH_EXCLUDE ?
                "默认解析：除了被@MockExclude标记的函数都解析，其他Method将访问" + mockOptions.getOriginalBaseUrl() :
                "默认不解析：仅解析被@MockInclude标记的函数，其他Method将访问" + mockOptions.getOriginalBaseUrl();
        LogUtil.i(cls.getSimpleName() + "当前Method解析策略为->" + ss);
        for (Method m : methods) {
            if (mockStrategy == MockStrategy.RESOLVE_WITH_EXCLUDE) {
                MockExclude mockExclude = m.getAnnotation(MockExclude.class);
                parseStart(m, mockExclude == null);
            } else if (mockStrategy == MockStrategy.RESOLVE_NOT_WITH_INCLUDE) {
                MockInclude annotation = m.getAnnotation(MockInclude.class);
                parseStart(m, annotation != null);
            } else {
                parseStart(m, true);
            }
            LogUtil.i("---------------分割线---------------");
        }
    }

    private void parseStart(Method m, boolean localOrBackup) {
        LogUtil.i("====== 开始解析 " + m.getName() + " ======");
        //类型本身一般没有什么意义 需要注意的是该类型中的泛型 以及ResponseBody的处理
        HttpInfo httpInfo = getHttpInfo(m);
        if (httpInfo != null) {
            String path = httpInfo.getPath();
            if (path == null || path.isEmpty()) {//需要动态解析
                LogUtil.i("暂不支持的url");
            } else {
                LogUtil.i("path:" + path);
                if (localOrBackup) {
                    //添加到包含集合
                    includeInfoMap.put(path, httpInfo);
                    //解析Data
                    boolean containsKey = MockUrlData.contain(path);
                    if (containsKey) {
                        LogUtil.i("该url已由其他mock数据占用，无需静态解析");
                    } else {
                        Object o = actType(m);
                        LogUtil.i("data:" + (o == null ? "null" : GsonUtil.toJson(o)));
                        MockUrlData.add(path, o);
                    }
                } else {
                    //添加到排除集合
                    excludeInfoMap.put(path, httpInfo);
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
                LogUtil.i(a.getClass() + "不支持的注解类型，目前只支持GET POST DELETE PUT");
            }
        }
        return null;
    }

    private String transString(String s) {
        return s == null || s.isEmpty() ? null : s;
    }

    private Object actType(Method m) {
        return mockParse.parseType(m.getGenericReturnType());
    }

}
