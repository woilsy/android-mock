package com.woilsy.mock;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.woilsy.mock.entity.MockData;
import com.woilsy.mock.utils.GsonUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockUrlData {

    private static final MockUrlData INSTANCE = new MockUrlData();

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

    public static void addFromAssets(Context context, String fileUrl) {
        try {
            InputStream is = context.getAssets().open(fileUrl);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            while (br.ready()) {
                String line = br.readLine();
                sb.append(line.trim().replaceAll(" ", ""));
            }
            br.close();
            String json = sb.toString();
            System.out.println("addFromAssets()->即将导入：" + json);
            List<MockData> mockDatas = GsonUtil.jsonToObj(json, new TypeToken<List<MockData>>() {
            }.getType());
            if (mockDatas != null) {
                for (MockData md : mockDatas) {
                    add(md.url, md.data);
                }
            } else {
                System.out.println("addFromAssets()->转换mock数据列表失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean contain(String key) {
        return INSTANCE.urlDataMap.containsKey(key);
    }

    public static String get(String key) {
        return INSTANCE.urlDataMap.get(key);
    }

    public static Map<String, String> getMap() {
        return INSTANCE.urlDataMap;
    }

}
