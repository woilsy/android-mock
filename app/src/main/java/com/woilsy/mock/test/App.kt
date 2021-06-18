package com.woilsy.mock.test

import android.app.Application
import com.woilsy.mock.MockLauncher
import com.woilsy.mock.data.AssetFileDataSource
import com.woilsy.mock.entity.MockObj
import com.woilsy.mock.options.MockOptions
import com.woilsy.mock.strategy.MockStrategy
import com.woilsy.mock.test.api.ApiService
import com.woilsy.mock.test.http.HttpManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //初始化mock
        MockLauncher.start(
            this,
            MockOptions.Builder()
                .setDebug(true)
                .setDataSource(AssetFileDataSource(this, "mock.json"))
                .setOriginalBaseUrl("https://www.wanandroid.com")
                .setPort(8899)
                .build(),
            MockObj(ApiService::class.java, MockStrategy.RESOLVE_WITH_EXCLUDE),
        )
        //初始化http
        HttpManager.init(this, MockLauncher.getMockBaseUrl())
    }

}