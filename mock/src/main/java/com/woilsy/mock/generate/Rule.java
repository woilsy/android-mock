package com.woilsy.mock.generate;

import com.woilsy.mock.utils.ClassUtils;

import java.math.BigDecimal;
import java.util.Date;

public interface Rule extends MockBaseType, MockExpend {

    default Object get(Class<?> cls) {
        Class<?> realClass = ClassUtils.getEncapsulationType(cls);
        if (realClass == String.class) return getString();
        if (realClass == Date.class) return getDate();
        if (realClass == Integer.class) return getInt();
        if (realClass == Long.class) return getLong();
        if (realClass == Byte.class) return getByte();
        if (realClass == Character.class) return getCharacter();
        if (realClass == Double.class) return getDouble();
        if (realClass == Float.class) return getFloat();
        if (realClass == Short.class) return getShort();
        if (realClass == Boolean.class) return getBoolean();
        if (realClass == BigDecimal.class) return getBigDecimal();
        return null;
    }
}
