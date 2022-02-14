package com.woilsy.mock.entity;

import com.woilsy.mock.constants.HttpMethod;

public class HttpInfo {

    private final HttpMethod httpMethod;

    private final String path;

    public HttpInfo(HttpMethod httpMethod, String path) {
        this.httpMethod = httpMethod;
        this.path = path;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }
}
