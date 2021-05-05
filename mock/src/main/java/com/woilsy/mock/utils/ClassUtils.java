package com.woilsy.mock.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClassUtils {

    public static Class<?> getEncapsulationType(Class<?> cls) {
        String name = cls.getName();
        switch (name) {
            case "byte":
                return Byte.class;
            case "short":
                return Short.class;
            case "int":
                return Integer.class;
            case "long":
                return Long.class;
            case "char":
                return Character.class;
            case "float":
                return Float.class;
            case "double":
                return Double.class;
            case "boolean":
                return Boolean.class;
            case "void":
                return Void.class;
        }
        return cls;
    }

    public static <T> Object allocateInstance(Class<T> cls) {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Object unsafe = unsafeField.get(null);
            Method method = unsafeClass.getMethod("allocateInstance", Class.class);
            return method.invoke(unsafe, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
