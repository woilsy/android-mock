package com.woilsy.mock.test

import android.app.Application
import com.woilsy.mock.MockLauncher
import com.woilsy.mock.test.api.ApiService
import com.woilsy.mock.test.http.HttpManager
import com.woilsy.mock.options.MockOptions

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //初始化mock
        MockLauncher.start(
            this,
            MockOptions.Builder()
                .setDebug(true)
                .setDataSource("mock.json")
                .setBackupBaseUrl("http://www.baidu.com")
                .build(),
            ApiService::class.java
        )
        //初始化http
        HttpManager.init(this, MockOptions.BASE_URL)
    }

}