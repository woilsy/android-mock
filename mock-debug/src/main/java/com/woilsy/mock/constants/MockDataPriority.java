package com.woilsy.mock.constants;

/**
 * 设定数据的优先级，以区别不同的数据来源，还可用于高级替换低级数据源。
 */
public enum MockDataPriority {

    TOP(1),
    MIDDLE(2),
    LOW(3);

    final int level;

    MockDataPriority(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

}