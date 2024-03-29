package com.woilsy.mock;

import android.content.Context;

import com.woilsy.mock.constants.HttpMethod;
import com.woilsy.mock.constants.MockDataPriority;
import com.woilsy.mock.constants.MockDefault;
import com.woilsy.mock.data.DataSource;
import com.woilsy.mock.entity.HttpData;
import com.woilsy.mock.entity.HttpInfo;
import com.woilsy.mock.entity.MockData;
import com.woilsy.mock.exe.MockServerExecutor;
import com.woilsy.mock.options.MockOptions;
import com.woilsy.mock.parse.MockDataStore;
import com.woilsy.mock.parse.MockParse;
import com.woilsy.mock.service.MockService;
import com.woilsy.mock.strategy.MockPriority;
import com.woilsy.mock.utils.GsonUtil;
import com.woilsy.mock.utils.LogUtil;
import com.woilsy.mock.utils.NetUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Mocker {

    private static final MockerInternal MOCKER = new MockerInternal();

    private Mocker() {

    }

    private static class MockerInternal {

        /**
         * mock数据管理
         */
        private MockDataStore mDataStore;

        /**
         * mock配置
         */
        private MockOptions mMockOptions;

        /**
         * 当前运行服务
         */
        private MockServerExecutor mockServerExecutor;

        private MockerInternal() {

        }

        private void initByOptions(Context context, MockOptions options) {
            this.mMockOptions = options == null ? MockOptions.getDefault() : options;
            this.mDataStore = new MockDataStore(new MockParse(mMockOptions));
            //导入数据 优先级较高 其他方式均为次要优先级
            DataSource[] dataSources = mMockOptions.getDataSources();
            if (dataSources != null) {
                for (DataSource dataSource : dataSources) {
                    List<MockData> mockData = dataSource.getMockData();
                    for (MockData mockDatum : mockData) {
                        if (mockDatum.method == null || mockDatum.method.isEmpty()) {
                            LogUtil.e("没有在数据源path" + mockDatum.path + "中发现请求方式，将被忽略");
                        } else {
                            HttpInfo httpInfo = new HttpInfo(
                                    HttpMethod.valueOf(mockDatum.method), mockDatum.path, MockPriority.DEFAULT
                            );
                            mDataStore.put(httpInfo, new HttpData(GsonUtil.toJson(mockDatum.data), MockDataPriority.MIDDLE));
                        }
                    }
                }
            }
            //如果已运行 先停止
            if (mockServerExecutor != null) {
                mockServerExecutor.stopMockServer();
            }
            //开启服务或者不启动
            if (mMockOptions.isEnableNotification()) {
                if (context == null) {
                    LogUtil.e("由于Context为null，无法开启MockService，将使用MockServerExecutor");
                    initMockServer();
                } else {
                    MockService.start(context, mMockOptions);
                }
            } else {
                initMockServer();
            }
        }

        private void initMockServer() {
            mockServerExecutor = new MockServerExecutor();
            mockServerExecutor.runMockServer(mMockOptions.getPort());
        }
    }

    public static void init(MockOptions options) {
        MOCKER.initByOptions(null, options);
    }

    public static void init(@Nullable Context context, MockOptions options) {
        MOCKER.initByOptions(context, options);
    }

    @NotNull
    public static MockDataStore getMockDataStore() {
        if (MOCKER.mDataStore == null) {
            MOCKER.mDataStore = new MockDataStore(new MockParse(getMockOption()));
        }
        return MOCKER.mDataStore;
    }

    @NotNull
    public static MockOptions getMockOption() {
        if (MOCKER.mMockOptions == null) {
            MOCKER.mMockOptions = MockOptions.getDefault();
        }
        return MOCKER.mMockOptions;
    }

    @NotNull
    public static String getMockBaseUrl() {
        return MockDefault.formatMockUrl(getMockOption().getPort());
    }

    @NotNull
    public static String getLocalUrl(Context context) {
        String ip = NetUtil.getIp(context);
        String host = ip == null ? MockDefault.HOST_NAME : ip;
        return MockDefault.formatMockUrl(host, getMockOption().getPort());
    }

    public static boolean isInit() {
        return MOCKER.mMockOptions != null;
    }
}
