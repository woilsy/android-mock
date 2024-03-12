package com.woilsy.mock.options;

import com.google.gson.Gson;
import com.woilsy.mock.constants.MockDefault;
import com.woilsy.mock.data.DataSource;
import com.woilsy.mock.generate.BaseTypeGenerator;
import com.woilsy.mock.generate.MatchRule;
import com.woilsy.mock.generate.Rule;
import com.woilsy.mock.utils.GsonUtil;
import com.woilsy.mock.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class MockOptions {

    /**
     * mock服务器监听端口
     */
    private int port;

    /**
     * 生成规则
     */
    private final List<Rule> rules;

    /**
     * mock数据来源
     */
    private final DataSource[] dataSources;

    /**
     * 对于自动生成的bean数据，是否每次都重新生成访问
     */
    private final boolean dynamicAccess;

    /**
     * 是否显示解析log
     */
    private final boolean showParseLog;

    /**
     * 生成的List数量，默认为1
     */
    private final int mockListSize;

    /**
     * 最小mock列表数量
     */
    private final int minMockListSize;

    /**
     * 最大mock列表数量
     */
    private final int maxMockListSize;

    /**
     * 生产的List数量随机，从0到mockListCount
     */
    private final boolean mockListCountRandom;

    /**
     * 是否开启通知栏
     */
    private final boolean enableNotification;

    public static MockOptions getDefault() {
        return new MockOptions
                .Builder()
                .enableLog(false)
                .enableNotification(false)
                .addRule(new MatchRule())
                .addRule(new BaseTypeGenerator())
                .setMockListRandomSize(0, 10)
                .setDynamicAccess(true, false)
                .setPort(MockDefault.PORT)
                .build();
    }

    private MockOptions(List<Rule> rules, DataSource[] dataSources, int port, boolean dynamicAccess, boolean showParseLog,
                        int mockListSize, int minMockListSize, int maxMockListSize, boolean mockListCountRandom,
                        boolean enableNotification) {
        this.rules = rules;
        this.dataSources = dataSources;
        this.port = port;
        this.dynamicAccess = dynamicAccess;
        this.showParseLog = showParseLog;
        this.mockListSize = mockListSize;
        this.minMockListSize = minMockListSize;
        this.maxMockListSize = maxMockListSize;
        this.mockListCountRandom = mockListCountRandom;
        this.enableNotification = enableNotification;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public DataSource[] getDataSources() {
        return dataSources;
    }

    public int getMockListSize() {
        return mockListSize;
    }

    public int getPort() {
        return port;
    }

    public boolean isShowParseLog() {
        return showParseLog;
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

    public boolean isEnableNotification() {
        return enableNotification;
    }

    public int getMinMockListSize() {
        return minMockListSize;
    }

    public int getMaxMockListSize() {
        return maxMockListSize;
    }

    public static class Builder {

        private boolean debug = false;

        private Gson gson;

        private final List<Rule> rules = new ArrayList<>();

        private int port;

        private boolean dynamicAccess;

        private boolean showParseLog;

        private DataSource[] dataSources;

        private int minMockListSize;

        private int maxMockListSize;

        private int mockListSize = 1;

        private boolean mockListCountRandom = false;

        private boolean enableNotification = false;

        /**
         * 设置日志开启
         *
         * @param enable 是否开启日志
         */
        public Builder enableLog(boolean enable) {
            this.debug = enable;
            return this;
        }

        /**
         * 是否开启通知栏显示，若开启则会启动MockService
         */
        public Builder enableNotification(boolean enable) {
            this.enableNotification = enable;
            return this;
        }

        /**
         * 添加数据mock的规则
         *
         * @param rule 具体规则，如MatchRule、BaseTypeGenerator
         */
        public Builder addRule(Rule rule) {
            this.rules.add(rule);
            return this;
        }

        /**
         * 将列表的Mock数量设置为固定
         *
         * @param mockListSize 具体固定数量
         */
        public Builder setMockListSize(int mockListSize) {
            assert mockListSize > 0;
            this.mockListCountRandom = false;
            this.mockListSize = mockListSize;
            return this;
        }

        /**
         * 将列表的Mock数量设置为随机，最终数量会在min-max之间随机
         *
         * @param min 最终数量会在min-max之间随机，大于等于0
         * @param max 最终数量会在min-max之间随机
         */
        public Builder setMockListRandomSize(int min, int max) {
            assert min >= 0 & max >= 0;
            this.mockListCountRandom = true;
            this.minMockListSize = min;
            this.maxMockListSize = max;
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
            assert port >= 1024;
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
            //设置log开启
            LogUtil.setDebug(this.debug);
            //替换内部的gson
            if (this.gson != null) {
                GsonUtil.replaceGson(this.gson);
            }
            return new MockOptions(
                    this.rules,
                    this.dataSources,
                    this.port == 0 ? MockDefault.PORT : this.port,
                    this.dynamicAccess,
                    this.showParseLog,
                    this.mockListSize,
                    this.minMockListSize,
                    this.maxMockListSize,
                    this.mockListCountRandom,
                    this.enableNotification
            );
        }
    }


}
