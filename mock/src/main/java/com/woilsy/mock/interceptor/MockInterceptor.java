package com.woilsy.mock.interceptor;

import android.util.Log;

import com.woilsy.mock.Mocker;
import com.woilsy.mock.constants.MockDefault;
import com.woilsy.mock.entity.HttpInfo;
import com.woilsy.mock.strategy.MockPriority;

import java.io.IOException;
import java.net.URL;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * mock拦截器，初始化的时候添加到拦截器链中。
 */
public class MockInterceptor implements Interceptor {

    private static final String TAG = "MockInterceptor";

    @SuppressWarnings("NullableProblems")
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //获取到原始地址 然后根据配置切换到本地地址
        HttpUrl httpUrl = request.url();
        if (!MockDefault.HOST_NAME.equals(httpUrl.host())) {//如果不一致 将根据配置进行请求 否则直接不处理
            URL url = httpUrl.url();
            Log.d(TAG, "请求地址：" + url);
            String originBaseUrl = httpUrl.scheme() + "://" + httpUrl.host() + ":" + httpUrl.port();
            Mocker.getMockOption().setOriginalBaseUrl(originBaseUrl);
            //还存在一种场景 就是没有添加任何apiService 那么就不知道其策略 这类接口需要排除
            boolean mockUrlOrOriginalUrl = Mocker.isMockUrlOrOriginalUrl();
            if (mockUrlOrOriginalUrl) {//mock或者不进行mock
                //查询是否被包含
                HttpInfo httpInfo = Mocker.findHttpInfo(httpUrl.encodedPath(), request.method());
                if (httpInfo != null) {
                    MockPriority mockPriority = httpInfo.getMockPriority();
                    if (mockPriority == null || mockPriority == MockPriority.DEFAULT) {
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
        }
        return chain.proceed(request);
    }

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
