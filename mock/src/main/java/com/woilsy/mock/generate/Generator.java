package com.woilsy.mock.generate;

import com.woilsy.mock.options.MockOptions;
import com.woilsy.mock.utils.ClassUtils;

import java.math.BigDecimal;

/**
 * 生成器，根据某种规则生成内容。
 */
public class Generator implements Rule {

    public String getString() {
        return MockOptions.DATE_GENERATOR_RULE.getString();
    }

    public Integer getInt() {
        return MockOptions.DATE_GENERATOR_RULE.getInt();
    }

    public Long getLong() {
        return MockOptions.DATE_GENERATOR_RULE.getLong();
    }

    public Boolean getBoolean() {
        return MockOptions.DATE_GENERATOR_RULE.getBoolean();
    }

    public Float getFloat() {
        return MockOptions.DATE_GENERATOR_RULE.getFloat();
    }

    public Character getCharacter() {
        return MockOptions.DATE_GENERATOR_RULE.getCharacter();
    }

    public Double getDouble() {
        return MockOptions.DATE_GENERATOR_RULE.getDouble();
    }

    public Short getShort() {
        return MockOptions.DATE_GENERATOR_RULE.getShort();
    }

    public Byte getByte() {
        return MockOptions.DATE_GENERATOR_RULE.getByte();
    }

    public BigDecimal getBigDecimal() {
        return MockOptions.DATE_GENERATOR_RULE.getBigDecimal();
    }

    public Object get(Class<?> cls) {
        Class<?> realClass = ClassUtils.getEncapsulationType(cls);
        if (realClass == String.class) return getString();
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
