package com.woilsy.mock.interceptor;

import android.net.Uri;
import android.util.Log;
import android.webkit.URLUtil;
import com.woilsy.mock.Mocker;
import com.woilsy.mock.annotations.MockObj;
import com.woilsy.mock.constants.HttpMethod;
import com.woilsy.mock.constants.MockDefault;
import com.woilsy.mock.entity.HttpInfo;
import com.woilsy.mock.parse.MockDataParse;
import com.woilsy.mock.strategy.MockPriority;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Invocation;
import retrofit2.http.Url;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * mock拦截器，初始化的时候添加到拦截器链中。
 */
public class MockInterceptor implements Interceptor {

    private static final String TAG = "MockInterceptor";

    private final Map<Class<?>, com.woilsy.mock.entity.MockObj> clsMap = new HashMap<>();

    private final List<String> dynamicUrls = new ArrayList<>();

    @SuppressWarnings("NullableProblems")
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //获取到原始地址 然后根据配置切换到本地地址
        HttpUrl httpUrl = request.url();
        //尝试解析接口对象后插入数据
        Invocation invocation = request.tag(Invocation.class);
        if (invocation != null) {
            //请求对对应的函数
            Method method = invocation.method();
            //请求函数对应的接口类
            Class<?> declaringClass = method.getDeclaringClass();
            MockObj mockObj = declaringClass.getAnnotation(MockObj.class);
            if (mockObj != null && !clsMap.containsKey(declaringClass)) {//该对象需要被解析
                com.woilsy.mock.entity.MockObj obj = new com.woilsy.mock.entity.MockObj(
                        declaringClass,
                        mockObj.value()
                );
                MockDataParse.parseObjs(obj);
                clsMap.put(declaringClass, obj);
            }
            //处理动态url数据
            putDynamicUrlData(invocation.arguments(), method, mockObj, request.method());
        }
        //判定及转发
        if (Mocker.isInit() && !MockDefault.HOST_NAME.equals(httpUrl.host())) {//如果不一致 将根据配置进行请求 否则直接不处理
            //查询是否被包含
            HttpInfo httpInfo = MockDataParse.findHttpInfo(httpUrl.encodedPath(), request.method());
            if (httpInfo != null) {
                MockPriority mockPriority = httpInfo.getMockPriority();
                if (mockPriority == null) {//如果为null 表示没有优先级 表示不处理的函数 直接返回原始结果
                    return chain.proceed(request);
                } else if (mockPriority == MockPriority.DEFAULT) {
                    return getMockResponse(chain, request, httpUrl);
                } else {//需要等待结果
                    Response response = chain.proceed(request);
                    if (mockPriority == MockPriority.ENABLE_ON_ERROR) {
                        if (response.code() == 200) {
                            return response;
                        } else {
                            return getMockResponse(chain, request, httpUrl);
                        }
                    }
                }
            }
        }
        return chain.proceed(request);
    }

    /**
     * 将动态url类型的数据手动插入
     *
     * @param params     请求参数
     * @param method     请求函数
     * @param mockObj    请求接口对象
     * @param methodName 请求类型，比如POST/GET，作为key的一部分，用于去重。
     */
    private void putDynamicUrlData(List<?> params, Method method, MockObj mockObj, String methodName) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            for (Annotation annotation : annotations) {
                //Url注解本身作用不大 主要是通过它的索引去获取实际的参数值
                if (annotation instanceof Url) {
                    String url = params.get(i).toString();
                    String key = url + methodName;
                    if (!dynamicUrls.contains(key) && URLUtil.isNetworkUrl(url)) {
                        dynamicUrls.add(key);
                        //根据规则 手动插入数据
                        insertMethodData(method, mockObj, methodName, url);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 将该请求函数进行解析后插入其类型
     */
    private static void insertMethodData(Method method, MockObj mockObj, String methodName, String url) {
        if (mockObj != null) {
            MockPriority mockPriority = MockDataParse.getMockPriority(mockObj.value(), method);
            String encodedPath = Uri.parse(url).getEncodedPath();
            if (mockPriority != null) {
                HttpInfo httpInfo = new HttpInfo(
                        HttpMethod.valueOf(methodName.toUpperCase()),
                        encodedPath,
                        MockPriority.DEFAULT
                );
                MockDataParse.putMethodData(method, httpInfo);
            }
        }
    }

    /**
     * 得到新的Request进行请求
     */
    private Response getMockResponse(Chain chain, Request request, HttpUrl httpUrl) throws IOException {
        HttpUrl httpUrl1 = httpUrl
                .newBuilder()
                .scheme("http")
                .host(MockDefault.HOST_NAME)
                .port(Mocker.getMockOption().getPort())
                .build();
        Log.d(TAG, "将重定向到：" + httpUrl1.url());
        Request newRequest = request.newBuilder().url(httpUrl1).build();
        return chain.proceed(newRequest);
    }

}
