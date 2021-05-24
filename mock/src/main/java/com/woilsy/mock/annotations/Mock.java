package com.woilsy.mock.annotations;

import com.woilsy.mock.type.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Mock {

    Type type() default Type.BASE;

    String value() default "";

}
