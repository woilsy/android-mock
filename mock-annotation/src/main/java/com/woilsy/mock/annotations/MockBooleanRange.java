package com.woilsy.mock.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mock一个boolean的范围，可以用来做随机。<br/>
 * 比如：@MockBooleanRange(true, false)可以表示为50%的true或者false
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MockBooleanRange {

    boolean[] value();

}
