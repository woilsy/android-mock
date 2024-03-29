package com.woilsy.mock.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * value: choose one at random
 * from&to: [from,to)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MockFloatRange {

    float[] value() default {};

    /**
     *  the least value returned
     */
    float from() default Float.MIN_VALUE;

    /**
     *  the upper bound (exclusive)
     */
    float to() default Float.MAX_VALUE;

}
