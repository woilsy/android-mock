package com.woilsy.mock.test

import android.app.Application
import com.woilsy.mock.Mocker
import com.woilsy.mock.data.AssetFileDataSource
import com.woilsy.mock.entity.MockGroup
import com.woilsy.mock.generate.DictionaryRule
import com.woilsy.mock.options.MockOptions
import com.woilsy.mock.strategy.MockStrategy
import com.woilsy.mock.test.api.ApiService
import com.woilsy.mock.test.api.ApiService2
import com.woilsy.mock.test.http.HttpManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //初始化mock
        Mocker.init(
            this,
            MockOptions.Builder()
                .setDebug(true)
                .setMockListCount(4, true)
                .setDynamicAccess(true, false)
                .setRule(DictionaryRule())
                .setDataSource(AssetFileDataSource(this, "mock.json"))
                .build(),
            MockGroup(
                MockStrategy.EXCLUDE,
                ApiService::class.java,
                ApiService2::class.java
            )
        )
        //初始化http
        HttpManager.init(this, "https://www.wanandroid.com")
    }

}