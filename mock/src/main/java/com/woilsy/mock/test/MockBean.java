package com.woilsy.mock.test;

import com.woilsy.mock.type.Image;

import java.util.Map;

public class MockBean<T> {

    public T data;

    public Map<String, Integer> map;

    public String a;

    public Bean2 bean2;

    @Image
    public String userAvatar;

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
                ", map=" + map +
                ", a='" + a + '\'' +
                ", bean2=" + bean2 +
                '}';
    }
}


