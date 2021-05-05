package com.woilsy.mock.options;

import com.woilsy.mock.generate.Rule;
import com.woilsy.mock.generate.rule.RandomRule;
import com.woilsy.mock.type.Images;

import java.util.List;

public class MockOptions {

    /**
     * Mock服务器端口
     */
    public static final int PORT = 8080;

    /**
     * Mock服务器Base地址
     */
    public static final String BASE_URL = "http://127.0.0.1:" + PORT;

    /**
     * 日志开关
     */
    public static boolean DEBUG = true;

    /**
     * Mock数据生成规则，可修改
     */
    public static Rule DATE_GENERATOR_RULE = new RandomRule();

    /**
     * Mock图片库，可修改
     */
    public static List<String> IMAGES = Images.get();

}
