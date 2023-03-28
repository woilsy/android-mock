package com.woilsy.mock.strategy;

/**
 * mock优先级
 */
public enum MockPriority {

    /**
     * 默认，直接替换掉原始接口数据。
     */
    DEFAULT,

    /**
     * 备用，仅在原始接口报错时(非200)有效
     */
    ENABLE_ON_ERROR

}
