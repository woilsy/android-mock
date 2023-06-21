package com.woilsy.mock.annotations;

import com.woilsy.mock.strategy.MockPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 包含网络请求接口中的某个函数，配合{@link MockObj}使用
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MockInclude {

    /**
     * mock优先级，请查看{@link MockPriority}
     */
    MockPriority priority() default MockPriority.DEFAULT;

}
