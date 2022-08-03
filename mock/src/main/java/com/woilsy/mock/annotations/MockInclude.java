package com.woilsy.mock.annotations;

import com.woilsy.mock.strategy.MockPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MockInclude {

    /**
     * mock优先级，请查看MockPriority
     */
    MockPriority priority() default MockPriority.DEFAULT;

}
