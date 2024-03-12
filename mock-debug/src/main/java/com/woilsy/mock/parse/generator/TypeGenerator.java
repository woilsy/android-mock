package com.woilsy.mock.parse.generator;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public interface TypeGenerator {

    /**
     * 难点：有时需要返回值，有时需要直接设置到Field中，如何区分？以及如何在递归中进行合适的逻辑处理？
     * 解：每一层只需要处理自己与上一级的关系就好了
     *
     * @param type      对象的Type
     * @param typeField 对象对应的字段，可能为null
     * @param parent    某些情况下需要使用到parent，比如List<T>，需要知道T的具体类型。
     * @return 通过type生成的对象
     */
    Object generateType(Type type, Field typeField, Object parent);

    default Object startGenerateType(Type type) {
        return generateType(type, null, null);
    }

}
