package com.woilsy.mock.entity;

import com.woilsy.mock.constants.HttpMethod;
import com.woilsy.mock.strategy.MockPriority;
import com.woilsy.mock.utils.LogUtil;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

public class HttpInfo {

    private final HttpMethod httpMethod;

    private final String path;

    private MockPriority mockPriority;

    public HttpInfo(HttpMethod httpMethod, String path, MockPriority mockPriority) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.mockPriority = mockPriority;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public void setMockPriority(MockPriority mockPriority) {
        this.mockPriority = mockPriority;
    }

    public MockPriority getMockPriority() {
        return mockPriority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpInfo httpInfo = (HttpInfo) o;
        return httpMethod == httpInfo.httpMethod && Objects.equals(path, httpInfo.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, path);
    }

    //分析静态url
    public static HttpInfo getHttpInfo(Method m, MockPriority mockPriority) {
        return createHttpInfo(m, mockPriority);
    }

    private static HttpInfo createHttpInfo(Method m, MockPriority mockPriority) {
        Annotation[] annotations = m.getAnnotations();
        for (Annotation a : annotations) {
            if (a instanceof GET) {
                return new HttpInfo(HttpMethod.GET, transString(((GET) a).value()), mockPriority);
            } else if (a instanceof POST) {
                return new HttpInfo(HttpMethod.POST, transString(((POST) a).value()), mockPriority);
            } else if (a instanceof DELETE) {
                return new HttpInfo(HttpMethod.DELETE, transString(((DELETE) a).value()), mockPriority);
            } else if (a instanceof PUT) {
                return new HttpInfo(HttpMethod.PUT, transString(((PUT) a).value()), mockPriority);
            } else {
                LogUtil.i(a.getClass() + "不支持的注解类型，目前仅支持GET POST DELETE PUT");
            }
        }
        return null;
    }

    private static String transString(String s) {
        return s == null || s.isEmpty() ? null : s;
    }
}
