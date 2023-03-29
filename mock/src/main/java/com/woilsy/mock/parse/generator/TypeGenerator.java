package com.woilsy.mock.parse.generator;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public interface TypeGenerator {

    /**
     * 难点：有时需要返回值，有时需要直接设置到Field中，如何区分？以及如何在递归中进行合适的逻辑处理？
     * 解：每一层只需要处理自己与上一级的关系就好了
     *
     * @param type         对象的Type
     * @param parent       父类对象，为了parentField传参设置字段
     * @param parentField  父类字段对象，为了type实例化后设置到该对象中
     * @param selfOrParent 如果是true，表示只需要处理自己逻辑，false则表示需要设置到父类的字段中
     * @return 通过type生成的对象
     */
    Object generateType(Type type, Object parent, Field parentField, boolean selfOrParent);

    default Object startGenerateType(Type type) {
        return generateType(type, null, null, true);
    }

}
