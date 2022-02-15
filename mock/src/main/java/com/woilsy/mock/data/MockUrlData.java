package com.woilsy.mock.data;

import com.woilsy.mock.entity.MockData;
import com.woilsy.mock.utils.GsonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockUrlData {

    private static final MockUrlData INSTANCE = new MockUrlData();

    /**
     * key值为path，value为path对应的json数据
     */
    private final Map<String, String> urlDataMap = new HashMap<>();

    private MockUrlData() {
    }

    public static MockUrlData getInstance() {
        return INSTANCE;
    }

    /**
     * 手动添加到map中，适合动态url的场合
     */
    public static void add(String url, Object object) {
        if (url != null && object != null) {
            INSTANCE.urlDataMap.put(url, GsonUtil.toJson(object));
        }
    }

    public static void add(DataSource dataSource) {
        if (dataSource != null) {
            List<MockData> mockData = dataSource.getMockData();
            if (mockData != null) {
                for (MockData md : mockData) {
                    add(md.path, md.data);
                }
            }
        }
    }

    public static void add(DataSource[] dataSources) {
        if (dataSources != null) {
            for (DataSource ds : dataSources) {
                add(ds);
            }
        }
    }

    public static boolean contain(String key) {
        return getInstance().urlDataMap.containsKey(key);
    }

    public static String get(String key) {
        return getInstance().urlDataMap.get(key);
    }

    public static Map<String, String> getMap() {
        return getInstance().urlDataMap;
    }

}
