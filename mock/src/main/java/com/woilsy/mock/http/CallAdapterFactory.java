package com.woilsy.mock.http;


import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class CallAdapterFactory extends CallAdapter.Factory {

    @SuppressWarnings("NullableProblems")
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {

        return null;
    }


    static class MyCallAdapter implements CallAdapter<Object, Object> {

        @Override
        public Type responseType() {
            return null;
        }

        @Override
        public Object adapt(Call<Object> call) {
            return null;
        }

    }
}
