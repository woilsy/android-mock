package com.woilsy.mock.parse;

import com.woilsy.mock.generate.Rule;
import com.woilsy.mock.options.MockOptions;

import java.util.List;

public class RealMockOptionsAgent implements MockOptionsAgent {

    private final MockOptions mockOptions;

    public RealMockOptionsAgent(MockOptions mockOptions) {
        this.mockOptions = mockOptions;
    }

    @Override
    public List<Rule> getRules() {
        return mockOptions.getRules();
    }

    @Override
    public boolean isShowParseLog() {
        return mockOptions.isShowParseLog();
    }

    @Override
    public boolean isMockListCountRandom() {
        return mockOptions.isMockListCountRandom();
    }

    @Override
    public int getMockListSize() {
        return mockOptions.getMockListSize();
    }

    @Override
    public int getMinMockListSize() {
        return mockOptions.getMinMockListSize();
    }

    @Override
    public int getMaxMockListSize() {
        return mockOptions.getMaxMockListSize();
    }
}
