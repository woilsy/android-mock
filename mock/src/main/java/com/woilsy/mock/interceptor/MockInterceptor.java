package com.woilsy.mock.interceptor;

import android.util.Log;

import com.woilsy.mock.MockLauncher;
import com.woilsy.mock.constants.MockDefault;
import com.woilsy.mock.entity.HttpInfo;

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

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //获取到原始地址 然后根据配置切换到本地地址
        HttpUrl httpUrl = request.url();
        if (MockDefault.HOST_NAME.equals(httpUrl.host())) {//如果一致
            return chain.proceed(request);
        } else {//不一致 根据配置进行请求
            URL url = httpUrl.url();
            Log.d(TAG, "请求地址：" + url);
            String originBaseUrl = httpUrl.scheme() + "://" + httpUrl.host() + ":" + httpUrl.port();
            MockLauncher.getMockOption().setOriginalBaseUrl(originBaseUrl);
            boolean mockUrlOrOriginalUrl = MockLauncher.isMockUrlOrOriginalUrl();
            if (mockUrlOrOriginalUrl) {//mock或者不进行mock
                //处理特殊项
                HttpInfo httpInfo = MockLauncher.excludeInfoMap.get(httpUrl.encodedPath());
                String method = request.method();
                if (httpInfo != null && httpInfo.getHttpMethod().name().equalsIgnoreCase(method)) {//被排除的url+method
                    return chain.proceed(request);
                } else {//其他项
                    HttpUrl httpUrl1 = httpUrl
                            .newBuilder()
                            .scheme("http")
                            .host(MockDefault.HOST_NAME)
                            .port(MockLauncher.getMockOption().getPort())
                            .build();
                    Log.d(TAG, "重定向到：" + httpUrl1.url());
                    Request request1 = request.newBuilder().url(httpUrl1).build();
                    return chain.proceed(request1);
                }
            } else {
                return chain.proceed(request);
            }
        }
    }
}
