package com.woilsy.mock.generate;

import com.woilsy.mock.utils.ClassUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 生成器，根据某种规则生成内容。
 */
public class Generator implements Rule {

    private final Rule rule;

    public Generator(Rule rule) {
        this.rule = rule;
    }

    public String getString() {
        return rule.getString();
    }

    public Integer getInt() {
        return rule.getInt();
    }

    public Long getLong() {
        return rule.getLong();
    }

    public Boolean getBoolean() {
        return rule.getBoolean();
    }

    public Float getFloat() {
        return rule.getFloat();
    }

    public Character getCharacter() {
        return rule.getCharacter();
    }

    public Double getDouble() {
        return rule.getDouble();
    }

    public Short getShort() {
        return rule.getShort();
    }

    public Byte getByte() {
        return rule.getByte();
    }

    public BigDecimal getBigDecimal() {
        return rule.getBigDecimal();
    }

    @Override
    public Date getDate() { return rule.getDate(); }

    public Object get(Class<?> cls) {
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
