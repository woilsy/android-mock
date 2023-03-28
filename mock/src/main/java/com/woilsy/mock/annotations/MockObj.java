package com.woilsy.mock.annotations;

import com.woilsy.mock.strategy.MockStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MockObj {

    MockStrategy value() default MockStrategy.EXCLUDE;

}
