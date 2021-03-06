package com.woilsy.mock.options;

import com.google.gson.Gson;
import com.woilsy.mock.constants.MockDefault;
import com.woilsy.mock.data.DataSource;
import com.woilsy.mock.generate.BaseTypeGenerator;
import com.woilsy.mock.generate.DictionaryRule;
import com.woilsy.mock.generate.Rule;
import com.woilsy.mock.utils.GsonUtil;
import com.woilsy.mock.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

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
    private List<Rule> rules;

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
                .addRule(new DictionaryRule())
                .addRule(new BaseTypeGenerator())
                .setMockListCount(4, true)
                .setDynamicAccess(true, false)
                .setPort(MockDefault.PORT)
                .build();
    }

    public List<Rule> getRules() {
        return rules;
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

    public void setRules(List<Rule> rules) {
        this.rules = rules;
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

        private final List<Rule> rules = new ArrayList<>();

        private int port;

        private boolean dynamicAccess;

        private boolean showParseLog;

        private DataSource[] dataSources;

        private int mockListCount = 1;

        private boolean mockListCountRandom = false;

        /**
         * 设置日志开启
         *
         * @param debug 是否开启日志
         */
        public Builder setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        /**
         * 添加数据mock的规则
         *
         * @param rule 具体规则，如DictionaryRule、BaseTypeGenerator
         */
        public Builder addRule(Rule rule) {
            this.rules.add(rule);
            return this;
        }

        /**
         * 设置mock列表的数量
         *
         * @param mockListCount 具体数量
         * @param random        是否使用随机，如果使用，那么最终数量会在0-mockListCount之间随机
         */
        public Builder setMockListCount(int mockListCount, boolean random) {
            this.mockListCount = mockListCount;
            this.mockListCountRandom = random;
            return this;
        }

        /**
         * 设置数据来源
         *
         * @param dataSources 支持FileData、AssetFile、JsonData等
         */
        public Builder setDataSource(DataSource... dataSources) {
            this.dataSources = dataSources;
            return this;
        }

        /**
         * 设置动态访问
         *
         * @param dynamicAccess 是一直使用第一次生成后的数据，还是每次都生成新的数据。
         * @param showLog       显示生成日志
         */
        public Builder setDynamicAccess(boolean dynamicAccess, boolean showLog) {
            this.dynamicAccess = dynamicAccess;
            this.showParseLog = showLog;
            return this;
        }

        /**
         * 设置Mock服务器端口
         *
         * @param port 必须大于等于1024
         */
        public Builder setPort(int port) {
            if (port < 1024) {
                throw new IllegalArgumentException("The port must be greater than or equal to 1024.");
            }
            this.port = port;
            return this;
        }

        /**
         * 替换默认使用的gson
         *
         * @param gson gson
         */
        public Builder setGson(Gson gson) {
            this.gson = gson;
            return this;
        }

        public MockOptions build() {
            MockOptions options = new MockOptions();
            options.rules = this.rules;
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
