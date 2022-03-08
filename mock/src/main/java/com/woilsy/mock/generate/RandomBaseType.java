package com.woilsy.mock.generate;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Date;

public class RandomBaseType implements MockBaseType {

    private final SecureRandom random = new SecureRandom();

    private final char[] chars = new char[]{
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    @Override
    public String getString() {
        return getInt() + "";
    }

    @Override
    public Integer getInt() {
        return random.nextInt();
    }

    @Override
    public Long getLong() {
        return random.nextLong();
    }

    @Override
    public Boolean getBoolean() {
        return random.nextBoolean();
    }

    @Override
    public Float getFloat() {
        return random.nextFloat();
    }

    @Override
    public Character getCharacter() {
        int index = random.nextInt(chars.length);
        return chars[index];
    }

    @Override
    public Double getDouble() {
        return random.nextDouble();
    }

    //-32768-32767
    @Override
    public Short getShort() {
        //-32768-0
        int max1 = Math.abs(Short.MIN_VALUE);
        int stage1 = -random.nextInt(max1 + 1);
        //0-32767
        int stage2 = random.nextInt(Short.MAX_VALUE + 1);
        return ((short) (stage1 + stage2));
    }

    @Override
    public Byte getByte() {
        //-128-0
        int max1 = Math.abs(Byte.MIN_VALUE);
        int stage1 = -random.nextInt(max1 + 1);
        //0-127
        int stage2 = random.nextInt(Byte.MAX_VALUE + 1);
        return ((byte) (stage1 + stage2));
    }

    @Override
    public BigDecimal getBigDecimal() {
        return BigDecimal.valueOf(getDouble());
    }

    @Override
    public Date getDate() {
        return new Date();
    }

}
