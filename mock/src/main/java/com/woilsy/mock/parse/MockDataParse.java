package com.woilsy.mock.parse;

import com.woilsy.mock.Mocker;
import com.woilsy.mock.annotations.MockExclude;
import com.woilsy.mock.annotations.MockInclude;
import com.woilsy.mock.constants.MockDataPriority;
import com.woilsy.mock.entity.HttpData;
import com.woilsy.mock.entity.HttpInfo;
import com.woilsy.mock.entity.MockObj;
import com.woilsy.mock.options.MockOptions;
import com.woilsy.mock.strategy.MockPriority;
import com.woilsy.mock.strategy.MockStrategy;
import com.woilsy.mock.utils.ClassUtils;
import com.woilsy.mock.utils.GsonUtil;
import com.woilsy.mock.utils.LogUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MockDataParse {

    public static String parseType(Type type) {
        Object o = Mocker.getMockDataStore().parseType(type);
        if (o != null) {
            return GsonUtil.toJson(o);
        } else {
            return null;
        }
    }

    public static String getHttpData(String path, String method) {
        HttpInfo httpInfo = findHttpInfo(path, method);
        if (httpInfo == null) {
            return null;
        } else {
            return HttpData.getJsonData(Mocker.getMockDataStore().get(httpInfo));
        }
    }

    public static HttpInfo findHttpInfo(String encodedPath, String method) {
        //encodedPath是一个实际地址 例如 /op/globalModuleConfig/{location} 但其实际地址 /op/globalModuleConfig/5 所以需要进行匹配
        String[] split1 = encodedPath.split("/");
        Set<HttpInfo> httpInfos = Mocker.getMockDataStore().keySet();
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

    private static void parseMethod(Method m, MockPriority mockPriority) {
        LogUtil.i("====== 开始解析 " + m.getName() + " ======");
        //类型本身一般没有什么意义 需要注意的是该类型中的泛型 以及ResponseBody的处理
        HttpInfo httpInfo = HttpInfo.getHttpInfo(m, mockPriority);
        if (httpInfo != null) {
            String path = httpInfo.getPath();
            if (path == null || path.isEmpty()) {//需要动态解析
                LogUtil.i("暂不支持的url");
            } else {
                LogUtil.i("path:" + path);
                //添加到集合
                putMethodData(m, httpInfo);
            }
        }
        LogUtil.i("====== 停止解析 " + m.getName() + " ======");
    }

    public static void putMethodData(Method m, HttpInfo httpInfo) {
        Type suspendType = ClassUtils.getSuspendFunctionReturnType(m);
        MockOptions mockOption = Mocker.getMockOption();
        Type realType = suspendType == null ? m.getGenericReturnType() : suspendType;
        if (mockOption.isDynamicAccess()) {
            LogUtil.i("动态访问，只存储返回类型" + realType);
            putDataToDataStore(httpInfo, new HttpData(realType, MockDataPriority.LOW));
        } else {
            String json = parseType(realType);
            LogUtil.i("非动态访问，数据:" + json);
            putDataToDataStore(httpInfo, new HttpData(json, MockDataPriority.LOW));
        }
    }

    public static void putDataToDataStore(HttpInfo httpInfo, HttpData httpData) {
        MockDataStore dataStore = Mocker.getMockDataStore();
        if (dataStore.containsKey(httpInfo)) {
            HttpData data = dataStore.get(httpInfo);
            if (data == null) {
                dataStore.put(httpInfo, httpData);
            } else {
                int level = data.getPriority().getLevel();
                int newLevel = httpData.getPriority().getLevel();
                if (newLevel < level) {//由于是对象 此httpInfo不和原先的HttpInfo相同但hashcode一致 会导致插入失败 故需要移除后插入
                    dataStore.remove(httpInfo);
                    dataStore.put(httpInfo, httpData);
                }
            }
        } else {
            dataStore.put(httpInfo, httpData);
        }
    }

    public static void parseObjs(MockObj... objs) {
        for (MockObj mockObj : objs) {
            try {
                Class<?> mockClass = mockObj.getMockClass();
                handleMockObj(mockClass, mockObj.getMockStrategy());
            } catch (Exception e) {
                LogUtil.e("解析Service失败，以下为错误信息↓", e);
            }
        }
    }

    private static void handleMockObj(Class<?> mockClass, MockStrategy mockStrategy) {
        if (mockClass.isInterface()) {
            parseClass(mockClass, mockStrategy);
        } else {
            LogUtil.e(mockClass.getName() + "非接口，无法处理");
        }
    }

    private static void parseClass(Class<?> cls, MockStrategy mockStrategy) {
        Method[] methods = cls.getMethods();
        String ss = mockStrategy == MockStrategy.EXCLUDE ? "默认解析：除了被@MockExclude标记的函数都解析，其他Method将拦截" : "默认不解析：仅解析被@MockInclude标记的函数，其他Method将放行";
        LogUtil.i(cls.getSimpleName() + "当前Method解析策略为->" + ss);
        for (Method m : methods) {
            parseMethod(mockStrategy, m);
            LogUtil.i("---------------分割线---------------");
        }
    }

    public static void parseMethod(MockStrategy mockStrategy, Method m) {
        parseMethod(m, getMockPriority(mockStrategy, m));
    }

    public static MockPriority getMockPriority(MockStrategy mockStrategy, Method m) {
        if (mockStrategy == MockStrategy.EXCLUDE) {
            MockExclude mockExclude = m.getAnnotation(MockExclude.class);
            if (mockExclude == null) {
                return MockPriority.DEFAULT;
            } else {
                return null;//被排除的函数
            }
        } else if (mockStrategy == MockStrategy.INCLUDE) {
            MockInclude mockInclude = m.getAnnotation(MockInclude.class);
            if (mockInclude != null) {
                return mockInclude.priority();
            } else {
                return null;//不被包含的函数
            }
        } else {
            return null;
        }
    }


}