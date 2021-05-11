package com.woilsy.mock.generate;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 生成器，根据某种规则生成内容。
 */
public class Generator implements Rule {

    private final MockBaseType mockBaseType;

    private final MockExpend mockExpend;

    public Generator() {
        this(new RandomBaseType(), new DefaultMockExpend());
    }

    public Generator(MockBaseType mockBaseType, MockExpend mockExpend) {
        this.mockBaseType = mockBaseType == null ? new RandomBaseType() : mockBaseType;
        this.mockExpend = mockExpend == null ? new DefaultMockExpend() : mockExpend;
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
        return mockExpend.getDate();
    }

    @Override
    public String getImage() {
        return mockExpend.getImage();
    }

    @Override
    public int getAge() {
        return mockExpend.getAge();
    }

    @Override
    public String getName() {
        return mockExpend.getName();
    }

    @Override
    public String getAddress() {
        return mockExpend.getAddress();
    }

    @Override
    public String getNickName() {
        return mockExpend.getNickName();
    }

    @Override
    public String getPhone() {
        return mockExpend.getPhone();
    }

}
