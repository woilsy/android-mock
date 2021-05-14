package com.woilsy.mock.data;

import com.woilsy.mock.entity.MockData;

import java.util.List;

public class SimpleDataSource implements DataSource {

    private final List<MockData> mockDataList;

    public SimpleDataSource(List<MockData> mockDataList) {
        this.mockDataList = mockDataList;
    }

    @Override
    public List<MockData> getMockData() {
        return mockDataList;
    }

}
