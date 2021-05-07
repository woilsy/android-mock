package com.woilsy.mock

import android.app.Application
import com.woilsy.mock.api.ApiService
import com.woilsy.mock.http.HttpManager
import com.woilsy.mock.options.MockOptions

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //初始化mock
        MockLauncher.start(
            this,
            MockOptions.Builder()
                .setDebug(true)
                .setMockDataFromAssets("mock.json")
                .build(),
            ApiService::class.java
        )
        //初始化http
        HttpManager.init(this, MockOptions.BASE_URL)
    }

}