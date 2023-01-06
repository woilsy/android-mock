package com.woilsy.mock.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * value: choose one at random
 * from&to: [from,to]
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MockLongRange {

    long[] value() default {};

    long from() default Long.MIN_VALUE;

    long to() default Long.MAX_VALUE;

}
