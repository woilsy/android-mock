package com.woilsy.mock.test.entity.test;

public class MockBean<T> {

    public T data;

    public String a;

    public Bean bean;

    public static class Bean {

        public int code;

        @Override
        public String toString() {
            return "Bean{" +
                    "code=" + code +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MockBean{" +
                "data=" + data +
                ", a='" + a + '\'' +
                '}';
    }
}


