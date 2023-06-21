package com.woilsy.mock.annotations;

import com.woilsy.mock.strategy.MockStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记某个网络请求接口类为Mock对象，配合{@link MockExclude}和{@link MockInclude}实现对类中请求函数的控制。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MockObj {

    MockStrategy value() default MockStrategy.EXCLUDE;

}
