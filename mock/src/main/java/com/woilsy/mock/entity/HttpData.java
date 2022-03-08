package com.woilsy.mock.entity;

import com.woilsy.mock.Mocker;

import java.lang.reflect.Type;

public class HttpData {

    private String jsonData;

    private Type typeData;

    public HttpData(String jsonData) {
        this.jsonData = jsonData;
    }

    public HttpData(Type typeData) {
        this.typeData = typeData;
    }

    public String getJsonData() {
        return jsonData;
    }

    public Type getTypeData() {
        return typeData;
    }

    public static String getData(HttpData httpData) {
        if (httpData == null) return null;
        String jsonData = httpData.getJsonData();
        Type typeData = httpData.getTypeData();
        if (jsonData == null || jsonData.isEmpty()) {
            return typeData == null ? null : Mocker.parseType(typeData);
        } else {
            return jsonData;
        }
    }
}
