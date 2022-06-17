package com.woilsy.mock.generate;

public interface Rule {

    Object getImpl(Class<?> cls, String name);

}
