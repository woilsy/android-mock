package com.woilsy.mock.parse;

import com.woilsy.mock.entity.HttpData;
import com.woilsy.mock.entity.HttpInfo;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * mock数据中心，负责数据管理
 */
public final class MockDataStore implements TypeParser, Map<HttpInfo, HttpData> {

    private final Map<HttpInfo, HttpData> httpDataMap = new HashMap<>();

    private final MockParse mockParse;

    public MockDataStore(MockParse mockParse) {
        this.mockParse = mockParse;
    }

    @Override
    public int size() {
        return httpDataMap.size();
    }

    @Override
    public boolean isEmpty() {
        return httpDataMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return httpDataMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return httpDataMap.containsValue(value);
    }

    @Override
    public HttpData get(Object key) {
        return httpDataMap.get(key);
    }

    public HttpData put(HttpInfo info, HttpData data) {
        return httpDataMap.put(info, data);
    }

    @Override
    public HttpData remove(Object key) {
        return httpDataMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends HttpInfo, ? extends HttpData> m) {
        httpDataMap.putAll(m);
    }

    @Override
    public void clear() {
        httpDataMap.clear();
    }

    @NotNull
    @Override
    public Set<HttpInfo> keySet() {
        return httpDataMap.keySet();
    }

    @NotNull
    @Override
    public Collection<HttpData> values() {
        return httpDataMap.values();
    }

    @NotNull
    @Override
    public Set<Entry<HttpInfo, HttpData>> entrySet() {
        return httpDataMap.entrySet();
    }

    @Override
    public Object parseType(Type type) {
        return mockParse.parseType(type);
    }
}
