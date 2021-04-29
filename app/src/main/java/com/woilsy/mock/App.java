package com.woilsy.mock;

import android.app.Application;

import com.woilsy.mock.api.ApiService;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MockLauncher.start(this, ApiService.class);
    }
}
