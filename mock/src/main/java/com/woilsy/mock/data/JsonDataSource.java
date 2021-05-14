package com.woilsy.mock.data;

import com.google.gson.reflect.TypeToken;
import com.woilsy.mock.entity.MockData;
import com.woilsy.mock.utils.GsonUtil;

import java.util.List;

/**
 * Mock data from json<br/>
 * [<br/>
 * {<br/>
 * "url":"request1",<br/>
 * "data":["one","two","three"]<br/>
 * }<br/>
 * ] <br/>
 */
public class JsonDataSource implements DataSource {

    private final String json;

    public JsonDataSource(String json) {
        this.json = json;
    }

    @Override
    public List<MockData> getMockData() {
        return GsonUtil.jsonToObj(json, new TypeToken<List<MockData>>() {
        }.getType());
    }
}
