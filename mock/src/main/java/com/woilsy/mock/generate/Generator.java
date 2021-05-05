package com.woilsy.mock.generate;

import java.math.BigDecimal;
import java.util.Random;

/**
 * 生成器，根据某种规则生成内容。
 */
public class Generator {

    private static final Rule dictionary = new Dictionary();

    public static String getString() {
        return dictionary.getString();
    }

    public static Integer getInt() {
        return dictionary.getInt();
    }

    public static Long getLong() {
        return dictionary.getLong();
    }

    public static Boolean getBoolean() {
        return dictionary.getBoolean();
    }

    public static Float getFloat() {
        return dictionary.getFloat();
    }

    public static Character getCharacter() {
        return dictionary.getCharacter();
    }

    public static Double getDouble() {
        return dictionary.getDouble();
    }

    public static Short getShort() {
        return dictionary.getShort();
    }

    public static Byte getByte() {
        return dictionary.getByte();
    }

    public static BigDecimal getBigDecimal() {
        return dictionary.getBigDecimal();
    }

    private static class Dictionary implements Rule {

        private final Random random = new Random();

        private final char[] chars = new char[]{
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
        };

        @Override
        public String getString() {
            return getInt() + "";
        }

        @Override
        public Integer getInt() {
            return random.nextInt(Integer.MAX_VALUE);
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

    }
}
