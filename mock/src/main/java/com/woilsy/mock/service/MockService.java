package com.woilsy.mock.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.woilsy.mock.HttpService;
import com.woilsy.mock.options.MockOptions;

public class MockService extends Service {

    private static final String TAG = "MockService";

    private static final String CHANNEL_ID = "channel_mock_service";
    private static final String CHANNEL_NAME = "Mock服务器通知渠道";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //
            NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            );
            //
            Notification notification =
                    new Notification.Builder(this, CHANNEL_ID)
                            .setContentText("Mock服务器正在运行...")
                            .setSubText("Mock")
                            .build();
            startForeground(100, notification);
        }
        new Thread(() -> {
            int port = MockOptions.PORT;
            HttpService httpService = new HttpService(port);
            try {
                httpService.start();
                Log.d(TAG, "已启动" + port + "mock服务器");
            } catch (Exception e) {
                Log.e(TAG, "mock服务器启动失败", e);
            }
        })
                .start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

}
