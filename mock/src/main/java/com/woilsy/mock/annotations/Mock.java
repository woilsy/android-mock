package com.woilsy.mock.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记字段来Mock想要的数据，比如<br/>
 * &#064;Mock("18")<br/>
 * int age;<br/>
 * 来表示将age字段的值Mock为18
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Mock {

    String value() default "";

}
