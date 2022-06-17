package com.woilsy.mock.options;

import com.google.gson.Gson;
import com.woilsy.mock.constants.MockDefault;
import com.woilsy.mock.data.DataSource;
import com.woilsy.mock.generate.Generator;
import com.woilsy.mock.generate.Rule;
import com.woilsy.mock.utils.GsonUtil;
import com.woilsy.mock.utils.LogUtil;

public class MockOptions {

    private MockOptions() {
    }

    /**
     * 原始地址，当服务器停止时会切换到改地址，需要过滤请求重定向时也需要使用
     */
    private String originalBaseUrl;
    /**
     * 生成规则
     */
    private Rule rule;

    /**
     * mock数据来源
     */
    private DataSource[] dataSources;

    /**
     * mock服务器监听端口
     */
    private int port;

    /**
     * 对于自动生成的bean数据，是否每次都重新生成访问
     */
    private boolean dynamicAccess;

    /**
     * 是否显示解析log
     */
    private boolean showParseLog;

    /**
     * 生成的List数量，默认为1
     */
    private int mockListCount = 1;

    /**
     * 生产的List数量随机，从0到mockListCount
     */
    private boolean mockListCountRandom = false;

    public static MockOptions getDefault() {
        return new MockOptions
                .Builder()
                .setDebug(true)
                .setRule(new Generator())
                .setDynamicAccess(true, false)
                .setPort(MockDefault.PORT)
                .build();
    }

    public Rule getRule() {
        return rule;
    }

    public DataSource[] getDataSources() {
        return dataSources;
    }

    public void setOriginalBaseUrl(String originalBaseUrl) {
        this.originalBaseUrl = originalBaseUrl;
    }

    public int getMockListCount() {
        return mockListCount;
    }

    public void setMockListCount(int mockListCount) {
        this.mockListCount = mockListCount;
    }

    public String getOriginalBaseUrl() {
        return originalBaseUrl;
    }

    public int getPort() {
        return port;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public void setDataSources(DataSource[] dataSources) {
        this.dataSources = dataSources;
    }

    public boolean isShowParseLog() {
        return showParseLog;
    }

    public void setShowParseLog(boolean showParseLog) {
        this.showParseLog = showParseLog;
    }

    public void setDynamicAccess(boolean dynamicAccess) {
        this.dynamicAccess = dynamicAccess;
    }

    public boolean isDynamicAccess() {
        return dynamicAccess;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isMockListCountRandom() {
        return mockListCountRandom;
    }

    public static class Builder {

        private boolean debug = false;

        private Gson gson;

        private Rule rule;

        private int port;

        private boolean dynamicAccess;

        private boolean showParseLog;

        private DataSource[] dataSources;

        private int mockListCount = 1;

        private boolean mockListCountRandom = false;

        public Builder setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder setRule(Rule rule) {
            this.rule = rule;
            return this;
        }

        public Builder setMockListCount(int mockListCount, boolean random) {
            this.mockListCount = mockListCount;
            this.mockListCountRandom = random;
            return this;
        }

        public Builder setDataSource(DataSource... dataSources) {
            this.dataSources = dataSources;
            return this;
        }

        public Builder setDynamicAccess(boolean dynamicAccess, boolean showLog) {
            this.dynamicAccess = dynamicAccess;
            this.showParseLog = showLog;
            return this;
        }

        public Builder setPort(int port) {
            if (port < 1024) {
                throw new IllegalArgumentException("The port must be greater than or equal to 1024.");
            }
            this.port = port;
            return this;
        }

        public Builder setGson(Gson gson) {
            this.gson = gson;
            return this;
        }

        public MockOptions build() {
            MockOptions options = new MockOptions();
            options.rule = this.rule == null ? new Generator() : rule;
            options.port = this.port == 0 ? MockDefault.PORT : this.port;
            options.dataSources = this.dataSources;
            options.mockListCount = this.mockListCount;
            options.dynamicAccess = this.dynamicAccess;
            options.showParseLog = this.showParseLog;
            options.mockListCountRandom = this.mockListCountRandom;
            LogUtil.setDebug(this.debug);
            if (this.gson != null) {
                GsonUtil.replaceGson(this.gson);
            }
            return options;
        }
    }


}
