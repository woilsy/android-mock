package com.woilsy.mock;

import java.util.HashMap;
import java.util.Map;

public class UrlManager {

    private static final UrlManager INSTANCE = new UrlManager();

    public Map<String, String> urlDataMap = new HashMap<>();

    private UrlManager() {

    }

    public static UrlManager getInstance() {
        return INSTANCE;
    }

}
