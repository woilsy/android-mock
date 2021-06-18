package com.woilsy.mock.utils;

import com.google.gson.internal.UnsafeAllocator;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;

public class ClassUtils {

    private static final UnsafeAllocator UNSAFE_ALLOCATOR = UnsafeAllocator.create();

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

    public static Object stringToBaseType(String s, Class<?> cls) {
        Class<?> aClass = getEncapsulationType(cls);
        try {
            if (aClass == Long.class) {
                return Long.valueOf(s);
            } else if (aClass == Integer.class) {
                return Integer.valueOf(s);
            } else if (aClass == Boolean.class) {
                return Boolean.valueOf(s);
            } else if (aClass == Float.class) {
                return new BigDecimal(s).floatValue();
            } else if (aClass == Character.class) {
                return s.toCharArray()[0];
            } else if (aClass == Double.class) {
                return new BigDecimal(s).doubleValue();
            } else if (aClass == Date.class) {
                return DateFormat.getDateTimeInstance().parse(s);
            } else if (aClass == Byte.class) {
                return Byte.valueOf(s);
            } else if (aClass == BigDecimal.class) {
                return new BigDecimal(s);
            } else if (aClass == Short.class) {
                return Short.valueOf(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object stringToClass(String s, Class<?> cls) {
        if (cls == String.class) {
            return s;
        } else {
            return stringToBaseType(s, cls);
        }
    }

    public static <T> Object allocateInstance(Class<T> cls) {
        try {
            return UNSAFE_ALLOCATOR.newInstance(cls);
        } catch (Exception e) {
            return null;
        }
    }

}
