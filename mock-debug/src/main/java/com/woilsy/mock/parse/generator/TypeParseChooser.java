package com.woilsy.mock.parse.generator;

import com.woilsy.mock.parse.MockOptionsAgent;
import com.woilsy.mock.utils.LogUtil;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeParseChooser {

    static Map<String, List<Type>> typeListMap = new HashMap<>();

    public static TypeGenerator findType(Type type, MockOptionsAgent mockOptionsAgent) {
        /*
         * key值存的是xxx.xxx.A，value为A<xxx>泛型中的xxx，通过clsTb可以获取到A的实际泛型，共用一个
         */
        if (type instanceof ParameterizedType) {
            return new ParameterizedTypeGenerator(mockOptionsAgent);
        } else if (type instanceof Class<?>) {
            return new ClassGenerator(mockOptionsAgent);
        } else if (type instanceof TypeVariable) {
            return new TypeVariableGenerator(mockOptionsAgent);
        } else if (type instanceof GenericArrayType) {
            /*
             * GenericArrayType表示的是数组类型且组成元素时ParameterizedType或TypeVariable，例如List<T>或T[]，
             * 该接口只有Type getGenericComponentType()一个方法，它返回数组的组成元素类型。
             */
            if (mockOptionsAgent.isShowParseLog()) {
                LogUtil.i("()->GenericArrayType类型" + type + "暂不处理");
            }
            return null;
        } else if (type instanceof WildcardType) {
            return new WildcardTypeGenerator(mockOptionsAgent);
        } else {
            if (mockOptionsAgent.isShowParseLog()) {
                LogUtil.i("()->暂不处理的类型:" + type);
            }
            return null;
        }
    }

}
