package com.woilsy.mock.strategy;

/**
 * mock处理策略
 */
public enum MockStrategy {

    /**
     * 默认解析：除了被@MockExclude标记的函数都解析
     */
    RESOLVE_WITH_EXCLUDE,
    /**
     * 默认不解析：仅解析被@MockInclude标记的函数
     */
    RESOLVE_NOT_WITH_INCLUDE

}
