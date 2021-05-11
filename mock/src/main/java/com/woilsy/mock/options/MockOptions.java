package com.woilsy.mock.options;

import com.google.gson.Gson;
import com.woilsy.mock.entity.MockData;
import com.woilsy.mock.generate.Generator;
import com.woilsy.mock.generate.Rule;
import com.woilsy.mock.strategy.MockStrategy;
import com.woilsy.mock.utils.GsonUtil;
import com.woilsy.mock.utils.LogUtil;

import java.io.File;
import java.util.List;

import static com.woilsy.mock.constants.MockDefault.HOST_NAME;
import static com.woilsy.mock.constants.MockDefault.PORT;

public class MockOptions {

    private MockOptions() {
    }

    /**
     * Mock服务器Base地址
     */
    public static String BASE_URL = "http://" + HOST_NAME + ":" + PORT;
    /**
     * Mock服务器备用地址，当服务器停止时会切换到改地址，需要过滤请求重定向时也需要使用
     */
    public static String BASE_URL_BACK_UP;

    /**
     * 生成规则
     */
    private Rule rule;
    /**
     * mock策略
     */
    private MockStrategy mockStrategy;

    /*======mock数据导入的几种来源======*/
    /**
     * mock数据之集合，可提前导入
     */
    private List<MockData> mockData;
    /**
     * mock数据来源之assets路径，需要Context，所以延迟加载
     */
    private String mockDataAssetsPath;

    /**
     * mock数据来源之File
     */
    private File mockDataFile;

    /**
     * mock数据来源之json
     */
    private String mockDataJson;

    public static MockOptions getDefault() {
        return new MockOptions
                .Builder()
                .setDebug(true)
                .build();
    }

    public static class Builder {

        private boolean debug = false;

        private List<MockData> mockData;

        private String mockDataAssetsPath;

        private File mockDataFile;

        private String mockDataJson;

        private String backupBaseUrl;

        private Gson gson;

        private Rule rule;

        public Builder setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder setRule(Rule rule) {
            this.rule = rule;
            return this;
        }

        public Builder setBackupBaseUrl(String url) {
            this.backupBaseUrl = url;
            return this;
        }

        public Builder setData(List<MockData> mockData) {
            this.mockData = mockData;
            return this;
        }

        public Builder setData(String json) {
            this.mockDataJson = json;
            return this;
        }

        public Builder setDataSource(File file) {
            this.mockDataFile = file;
            return this;
        }

        public Builder setDataSource(String assetsFilePath) {
            this.mockDataAssetsPath = assetsFilePath;
            return this;
        }

        public Builder setGson(Gson gson) {
            this.gson = gson;
            return this;
        }

        public MockOptions build() {
            MockOptions options = new MockOptions();
            options.rule = this.rule == null ? new Generator() : rule;
            LogUtil.setDebug(this.debug);
            MockOptions.BASE_URL_BACK_UP = this.backupBaseUrl == null ? BASE_URL : this.backupBaseUrl;
            if (this.gson != null) {
                GsonUtil.replaceGson(this.gson);
            }
            //数据源
            options.mockDataAssetsPath = this.mockDataAssetsPath;
            options.mockDataFile = this.mockDataFile;
            options.mockDataJson = this.mockDataJson;
            options.mockData = this.mockData;
            return options;
        }
    }

    public Rule getRule() {
        return rule;
    }

    public List<MockData> getMockData() {
        return mockData;
    }

    public String getMockDataAssetsPath() {
        return mockDataAssetsPath;
    }

    public File getMockDataFile() {
        return mockDataFile;
    }

    public String getMockDataJson() {
        return mockDataJson;
    }

}
