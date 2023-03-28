package com.woilsy.mock.test

import android.app.Application
import com.woilsy.mock.test.http.HttpManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //初始化http
        HttpManager.init(this, "https://www.wanandroid.com")
    }

}