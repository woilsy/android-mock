package com.woilsy.mock.utils;

public class ArrayUtil {

    public static <T> boolean contain(T t, T[] array) {
        for (T t1 : array) {
            if (t == t1) {
                return true;
            }
        }
        return false;
    }

}
