package com.woilsy.mock.utils;

import com.woilsy.mock.annotations.*;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

public class MockRangeUtil {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public static boolean booleanRange(MockBooleanRange range) {
        if (range == null) return false;
        boolean[] value = range.value();
        if (value.length == 0) {
            return false;
        } else {
            return value[RANDOM.nextInt(value.length)];
        }
    }

    public static String stringRange(MockStringRange range) {
        if (range == null) return "";
        String[] value = range.value();
        if (value.length == 0) {
            return "";
        } else {
            return value[RANDOM.nextInt(value.length)];
        }
    }

    public static char charRange(MockCharRange range) {
        if (range == null) return Character.MIN_VALUE;
        char[] value = range.value();
        if (value.length == 0) {//没有设置
            char from = range.from();
            char to = range.to();
            return getRandomCharacter(from, to);
        } else {
            return value[RANDOM.nextInt(value.length)];
        }
    }

    public static char getRandomCharacter(char ch1, char ch2) {
        return (char) (ch1 + RANDOM.nextDouble() * (ch2 - ch1 + 1));
    }

    public static int intRange(MockIntRange range) {
        if (range == null) return Integer.MIN_VALUE;
        int[] value = range.value();
        if (value.length == 0) {
            int min = range.from();
            int max = range.to();
            return RANDOM.nextInt((max - min) + 1) + min;
        } else {
            return value[RANDOM.nextInt(value.length)];
        }
    }

    public static long longRange(MockLongRange range) {
        if (range == null) return Long.MIN_VALUE;
        long[] value = range.value();
        if (value.length == 0) {
            long min = range.from();
            long max = range.to();
            return RANDOM.nextLong(min, max + 1);
        } else {
            return value[RANDOM.nextInt(value.length)];
        }
    }

    public static double doubleRange(MockDoubleRange range) {
        if (range == null) return Double.MIN_VALUE;
        double[] value = range.value();
        if (value.length == 0) {
            double min = range.from();
            double max = range.to();
            return RANDOM.nextDouble(min, max);
        } else {
            return value[RANDOM.nextInt(value.length)];
        }
    }

    public static float floatRange(MockFloatRange range) {
        if (range == null) return Float.MIN_VALUE;
        float[] value = range.value();
        if (value.length == 0) {
            float min = range.from();
            float max = range.to();
            double nextDouble = RANDOM.nextDouble(min, max);
            return new BigDecimal(nextDouble).floatValue();
        } else {
            return value[RANDOM.nextInt(value.length)];
        }
    }

}

