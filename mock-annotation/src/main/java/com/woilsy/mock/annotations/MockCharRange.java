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
public @interface MockCharRange {

    char[] value() default {};

    /**
     *  the least value returned
     */
    char from() default Character.MIN_VALUE;

    /**
     *  the upper bound (exclusive)
     */
    char to() default Character.MAX_VALUE;

}