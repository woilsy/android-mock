package com.woilsy.mock.generate;

import com.woilsy.mock.utils.ClassUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 基本类型生成器，根据基本类型规则生成内容。
 */
public class BaseTypeGenerator implements Rule, MockBaseType {

    private final MockBaseType mockBaseType;

    public BaseTypeGenerator() {
        this(new RandomBaseType());
    }

    public BaseTypeGenerator(MockBaseType mockBaseType) {
        this.mockBaseType = mockBaseType == null ? new RandomBaseType() : mockBaseType;
    }

    public String getString() {
        return mockBaseType.getString();
    }

    public Integer getInt() {
        return mockBaseType.getInt();
    }

    public Long getLong() {
        return mockBaseType.getLong();
    }

    public Boolean getBoolean() {
        return mockBaseType.getBoolean();
    }

    public Float getFloat() {
        return mockBaseType.getFloat();
    }

    public Character getCharacter() {
        return mockBaseType.getCharacter();
    }

    public Double getDouble() {
        return mockBaseType.getDouble();
    }

    public Short getShort() {
        return mockBaseType.getShort();
    }

    public Byte getByte() {
        return mockBaseType.getByte();
    }

    public BigDecimal getBigDecimal() {
        return mockBaseType.getBigDecimal();
    }

    @Override
    public Date getDate() {
        return mockBaseType.getDate();
    }

    //这里只处理基本类型
    @Override
    public Object getImpl(Class<?> cls, String name) {
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
