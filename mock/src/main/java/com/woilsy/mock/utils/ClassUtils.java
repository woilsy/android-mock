package com.woilsy.mock.utils;

import com.google.gson.internal.UnsafeAllocator;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;

public class ClassUtils {

    private static final UnsafeAllocator UNSAFE_ALLOCATOR = UnsafeAllocator.create();

    public static Type getSuspendFunctionReturnType(Method method) {
        if (method.getGenericReturnType() == Object.class) {
            Type[] types = method.getGenericParameterTypes();
            for (Type parameterType : types) {
                if (parameterType instanceof ParameterizedType) {
                    ParameterizedType params = (ParameterizedType) parameterType;
                    Type rawType = params.getRawType();
                    try {
                        if (rawType == Continuation.class) {
                            Type[] actualTypeArguments = params.getActualTypeArguments();
                            if (actualTypeArguments.length > 0 && actualTypeArguments[0] instanceof WildcardType) {
                                return actualTypeArguments[0];
                            }
                        }
                    } catch (NoClassDefFoundError ignored) {

                    }
                }
            }
        }
        return null;
    }

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

    @Nullable
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

    @Nullable
    public static Object stringToClass(String s, Class<?> cls) {
        if (cls == String.class) {
            return s;
        } else {
            return stringToBaseType(s, cls);
        }
    }

    @Nullable
    public static <T> Object allocateInstance(Class<T> cls) {
        try {
            return UNSAFE_ALLOCATOR.newInstance(cls);
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public static Class<?> getClassByName(String className) {
        try {
            if (className == null || className.isEmpty()) return null;
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Nullable
    public static Object newClassInstance(String className) {
        return newClassInstance(getClassByName(className));
    }

    /**
     * 通过class生成一个对象
     */
    @Nullable
    public static Object newClassInstance(Class<?> cls) {
        if (cls == null) return null;
        String name = cls.getSimpleName();
        try {//默认构造器创建
            Constructor<?>[] constructors = cls.getDeclaredConstructors();
            for (Constructor<?> constructor : constructors) {
                int len = constructor.getParameterTypes().length;
                if (len == 0) {
                    constructor.setAccessible(true);
                    LogUtil.i("已通过构造器创建->" + name);
                    return constructor.newInstance();
                }
            }
        } catch (Exception e) {//使用不安全的方式创建
            LogUtil.e("构造器创建失败，尝试使用Unsafe创建->" + name);
        }
        return unsafeCreate(cls);
    }

    /**
     * 使用Gson UNSAFE方式直接操作内存创建对象
     */
    @Nullable
    public static Object unsafeCreate(Class<?> cls) {
        try {
            return ClassUtils.allocateInstance(cls);
        } catch (Exception e2) {
            LogUtil.e("尝试使用Unsafe创建失败:", e2);
            return null;
        }
    }

}
