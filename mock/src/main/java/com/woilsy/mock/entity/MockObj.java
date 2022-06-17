package com.woilsy.mock.entity;

import com.woilsy.mock.strategy.MockStrategy;

public class MockObj {

    private final Class<?> mockClass;

    private final MockStrategy mockStrategy;

    public MockObj(Class<?> mockClass) {
        this.mockClass = mockClass;
        this.mockStrategy = MockStrategy.EXCLUDE;
    }

    public MockObj(Class<?> mockClass, MockStrategy mockStrategy) {
        this.mockClass = mockClass;
        this.mockStrategy = mockStrategy;
    }

    public Class<?> getMockClass() {
        return mockClass;
    }

    public MockStrategy getMockStrategy() {
        return mockStrategy;
    }
}
