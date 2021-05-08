package com.woilsy.mock.test;

public class MockBean<T> {

    public T data;

    public String a;

    public static class Bean2 {
        public int code;

        @Override
        public String toString() {
            return "Bean2{" +
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


