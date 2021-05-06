package com.woilsy.mock

import android.app.Application
import com.woilsy.mock.api.ApiService
import com.woilsy.mock.http.HttpManager
import com.woilsy.mock.options.MockOptions

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //导入已有的Mock数据
        MockUrlData.addFromAssets(this, "mock.json");
        MockLauncher.start(this, ApiService::class.java)
        //初始化http
        HttpManager.init(this, MockOptions.BASE_URL)
    }

}