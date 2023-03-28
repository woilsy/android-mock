package com.woilsy.mock.constants;

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