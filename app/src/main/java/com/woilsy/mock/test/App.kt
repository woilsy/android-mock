package com.woilsy.mock.test

import android.app.Application
import com.woilsy.mock.Mocker
import com.woilsy.mock.data.AssetFileDataSource
import com.woilsy.mock.generate.BaseTypeGenerator
import com.woilsy.mock.generate.MatchRule
import com.woilsy.mock.options.MockOptions
import com.woilsy.mock.test.http.HttpManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //初始化mock
        Mocker.init(
            this,
            MockOptions.Builder()
                .enableLog(false)
                .enableNotification(false)
                .setMockListRandomSize(1, 3)
                .setDynamicAccess(true, false)
                .addRule(MatchRule())
                .addRule(BaseTypeGenerator())
                .setDataSource(AssetFileDataSource(this, "mock.json"))
                .build()
        )
        //初始化http
        HttpManager.init(this, "https://www.wanandroid.com")
    }

}