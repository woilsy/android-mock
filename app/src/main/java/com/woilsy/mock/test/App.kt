package com.woilsy.mock.test

import android.app.Application
import com.woilsy.mock.Mocker
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
        Mocker.start(
            this,
            MockOptions.Builder()
                .setDebug(true)
                .setMockListCount(4)
                .setDynamicAccess(true, false)
                .setDataSource(AssetFileDataSource(this, "mock.json"))
                .build(),
            MockObj(ApiService::class.java, MockStrategy.RESOLVE_WITH_EXCLUDE),
        )
        //初始化http
        HttpManager.init(this, "https://www.wanandroid.com")
    }

}