package com.woilsy.mock.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 一般情况下通过字段可以自动获取其类型，但部分情况例外。如可用于字段是个Object类型的时候，显式指定其类型。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MockClass {

    Class<?> value();

}
