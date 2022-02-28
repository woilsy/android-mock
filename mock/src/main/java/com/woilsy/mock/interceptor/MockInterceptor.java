package com.woilsy.mock.interceptor;

import android.util.Log;

import com.woilsy.mock.MockLauncher;
import com.woilsy.mock.constants.MockDefault;
import com.woilsy.mock.entity.HttpInfo;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

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
        if (!MockDefault.HOST_NAME.equals(httpUrl.host())) {//如果不一致 将根据配置进行请求 否则直接不处理
            URL url = httpUrl.url();
            Log.d(TAG, "请求地址：" + url);
            String originBaseUrl = httpUrl.scheme() + "://" + httpUrl.host() + ":" + httpUrl.port();
            MockLauncher.getMockOption().setOriginalBaseUrl(originBaseUrl);
            //还存在一种场景 就是没有添加任何apiService 那么就不知道其策略 这类接口需要排除
            boolean mockUrlOrOriginalUrl = MockLauncher.isMockUrlOrOriginalUrl();
            if (mockUrlOrOriginalUrl) {//mock或者不进行mock
                //查询是否被包含
                HttpInfo includeInfo = findHttpInfo(httpUrl.encodedPath(), MockLauncher.includeInfoMap);
                HttpInfo excludeInfo = findHttpInfo(httpUrl.encodedPath(), MockLauncher.excludeInfoMap);
                if (includeInfo != null && excludeInfo == null) {
                    HttpUrl httpUrl1 = httpUrl
                            .newBuilder()
                            .scheme("http")
                            .host(MockDefault.HOST_NAME)
                            .port(MockLauncher.getMockOption().getPort())
                            .build();
                    Log.d(TAG, "将重定向到：" + httpUrl1.url());
                    Request newRequest = request.newBuilder().url(httpUrl1).build();
                    return chain.proceed(newRequest);
                }
            }
        }
        return chain.proceed(request);
    }

    private HttpInfo findHttpInfo(String encodedPath, Map<String, HttpInfo> httpInfoMap) {
        //encodedPath是一个实际地址 例如 /op/globalModuleConfig/{location} 但其实际地址 /op/globalModuleConfig/5 所以需要进行匹配
        Set<String> keySet = httpInfoMap.keySet();
        String[] split1 = encodedPath.split("/");
        String mapKey = "";
        for (String s : keySet) {//op/globalModuleConfig/{location}
            String[] split2 = s.split("/");
            int len = split2.length;
            if (split1.length == len) {//长度一致
                int count = 0;
                for (int i = 0; i < len; i++) {
                    if (split2[i].startsWith("{") || split1[i].equals(split2[i])) {//匹配元素
                        count++;
                    }
                }
                if (count == len) {//匹配成功
                    mapKey = s;
                    break;
                }
            }
        }
        return mapKey.isEmpty() ? null : httpInfoMap.get(mapKey);
    }

}
