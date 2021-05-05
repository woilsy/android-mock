package com.woilsy.mock

import android.app.Application
import com.woilsy.mock.api.ApiService
import com.woilsy.mock.http.HttpManager
import com.woilsy.mock.options.MockOptions

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        MockLauncher.start(this, ApiService::class.java)
        HttpManager.init(this, MockOptions.BASE_URL)
    }

}