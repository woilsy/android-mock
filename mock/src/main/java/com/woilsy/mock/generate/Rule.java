package com.woilsy.mock.generate;

public interface Rule {

    /**
     * @param cls  被mock类的class对象
     * @param name 被mock对象在父类中的字段名称，可能为null
     */
    Object getImpl(Class<?> cls, String name);

}
