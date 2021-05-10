package com.woilsy.mock

import android.app.Application
import com.woilsy.mock.api.ApiService
import com.woilsy.mock.entity.MockObj
import com.woilsy.mock.http.HttpManager
import com.woilsy.mock.options.MockOptions
import com.woilsy.mock.type.MockStrategy

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //初始化mock
        MockLauncher.start(
            this,
            MockOptions.Builder()
                .setDebug(true)
                .setDataSource("mock.json")
                .setBackupBaseUrl("https://www.wanandroid.com")
                .build(),
            MockObj(ApiService::class.java, MockStrategy.RESOLVE_WITH_EXCLUDE)
        )
        //初始化http
        HttpManager.init(this, MockOptions.BASE_URL)
    }

}