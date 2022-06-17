package com.woilsy.mock.entity;

import com.woilsy.mock.strategy.MockStrategy;

public class MockGroup {

    private final MockStrategy mockStrategy;

    private final Class<?>[] objs;

    public MockGroup(MockStrategy mockStrategy, Class<?>... objs) {
        this.mockStrategy = mockStrategy;
        this.objs = objs;
    }

    public MockStrategy getMockStrategy() {
        return mockStrategy;
    }

    public Class<?>[] getObjs() {
        return objs;
    }

}
