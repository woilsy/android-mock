package com.woilsy.mock.debug;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import okhttp3.Interceptor;

public class MockGetter {

    public static Interceptor getMockInterceptor() {
        try {
            Class<?> aClass = Class.forName("com.woilsy.mock.interceptor.MockInterceptor");
            Constructor<?> constructor = aClass.getConstructor();
            return (Interceptor) constructor.newInstance();
        } catch (Exception ignore) {
            return new EmptyInterceptor();
        }
    }

    public static String getMockBaseUrl() {
        try {
            Class<?> aClass = Class.forName("com.woilsy.mock.Mocker");
            Method method = aClass.getMethod("getMockBaseUrl");
            return (String) method.invoke(null);
        } catch (Exception ignore) {
            return "";
        }
    }
}