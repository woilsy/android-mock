package com.woilsy.mock;

import android.app.Application;

import com.woilsy.mock.api.ApiService;
import com.woilsy.mock.http.HttpManager;
import com.woilsy.mock.options.MockOptions;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MockLauncher.start(this, ApiService.class);
        HttpManager.INSTANCE.init(this, MockOptions.BASE_URL);
    }
}
