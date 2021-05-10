package com.woilsy.mock.test

import android.app.Application
import com.woilsy.mock.MockLauncher
import com.woilsy.mock.entity.MockObj
import com.woilsy.mock.options.MockOptions
import com.woilsy.mock.test.api.ApiService
import com.woilsy.mock.test.api.ApiService2
import com.woilsy.mock.test.http.HttpManager
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
                .setBackupBaseUrl("http://www.baidu.com")
                .build(),
            MockObj(ApiService::class.java, MockStrategy.RESOLVE_WITH_EXCLUDE),
            MockObj(ApiService2::class.java, MockStrategy.RESOLVE_NOT_WITH_INCLUDE)
        )
        //初始化http
        HttpManager.init(this, MockOptions.BASE_URL)
    }

}