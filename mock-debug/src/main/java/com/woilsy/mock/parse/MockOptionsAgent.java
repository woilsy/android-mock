package com.woilsy.mock.parse;

import com.woilsy.mock.generate.Rule;

import java.util.List;

public interface MockOptionsAgent {

    List<Rule> getRules();

    boolean isShowParseLog();

    boolean isMockListCountRandom();

    int getMockListSize();

    int getMinMockListSize();

    int getMaxMockListSize();

}
