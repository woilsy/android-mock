package com.woilsy.mock.entity;

import com.woilsy.mock.constants.MockDataPriority;
import com.woilsy.mock.parse.MockDataParse;

import java.lang.reflect.Type;

public class HttpData {

    private String jsonData;

    private Type typeData;

    private final MockDataPriority priority;

    public HttpData(String jsonData, MockDataPriority priority) {
        this.jsonData = jsonData;
        this.priority = priority;
    }

    public HttpData(Type typeData, MockDataPriority priority) {
        this.typeData = typeData;
        this.priority = priority;
    }

    public MockDataPriority getPriority() {
        return priority;
    }

    public String getJsonData() {
        return jsonData;
    }

    public Type getTypeData() {
        return typeData;
    }

    public static String getJsonData(HttpData httpData) {
        if (httpData == null) return null;
        String jsonData = httpData.getJsonData();
        Type typeData = httpData.getTypeData();
        if (jsonData == null || jsonData.isEmpty()) {
            return typeData == null ? null : MockDataParse.parseType(typeData);
        } else {
            return jsonData;
        }
    }
}
