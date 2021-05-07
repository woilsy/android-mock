package com.woilsy.mock.options;

import com.woilsy.mock.entity.MockData;
import com.woilsy.mock.generate.Rule;
import com.woilsy.mock.generate.rule.RandomRule;
import com.woilsy.mock.type.Images;

import java.util.ArrayList;
import java.util.List;

public class MockOptions {

    private MockOptions() {

    }

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
    public boolean debug = true;
    /**
     * 生成规则
     */
    public Rule rule;
    /**
     * Mock图片库，可修改
     */
    public List<String> images = new ArrayList<>();
    /**
     * mock数据集合，可提前导入
     */
    public List<MockData> mockData = new ArrayList<>();
    /**
     * mock数据来源文件路径，需要Context，所以延迟加载
     */
    public String mockDataAssetsPath;

    public static MockOptions getDefault() {
        return new MockOptions
                .Builder()
                .setDebug(true)
                .setGenerateRule(new RandomRule())
                .setImages(Images.get())
                .build();
    }

    public static class Builder {

        private boolean debug;

        private List<String> images;

        private Rule generateRule;

        private List<MockData> mockData;

        private String mockDataAssetsPath;

        public Builder setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder setImages(List<String> images) {
            this.images = images;
            return this;
        }

        public Builder setGenerateRule(Rule generateRule) {
            this.generateRule = generateRule;
            return this;
        }

        public Builder setMockData(List<MockData> mockData) {
            this.mockData = mockData;
            return this;
        }

        public Builder setMockDataFromAssets(String path) {
            this.mockDataAssetsPath = path;
            return this;
        }

        public MockOptions build() {
            MockOptions options = new MockOptions();
            options.rule = this.generateRule == null ? new RandomRule() : this.generateRule;
            options.debug = this.debug;
            options.mockDataAssetsPath = this.mockDataAssetsPath;
            if (this.images != null) options.images.addAll(this.images);
            if (this.mockData != null) options.mockData.addAll(this.mockData);
            return options;
        }
    }

}
