package com.woilsy.mock.entity;

import com.woilsy.mock.constants.HttpMethod;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpInfo httpInfo = (HttpInfo) o;
        return httpMethod == httpInfo.httpMethod && Objects.equals(path, httpInfo.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, path);
    }
}
