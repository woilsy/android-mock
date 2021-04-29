package com.woilsy.mock;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.woilsy.mock.service.MockService;

public class MockLauncher {

    public static void start(Context context, Class<?>... classes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, MockService.class));
        } else {
            context.startService(new Intent(context, MockService.class));
        }
    }

}
