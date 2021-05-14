package com.woilsy.mock.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.woilsy.mock.MockLauncher;
import com.woilsy.mock.constants.MockDefault;
import com.woilsy.mock.server.HttpServer;
import com.woilsy.mock.utils.LogUtil;

public class MockService extends Service {

    private static final String CHANNEL_ID = "channel_mock_service";
    private static final String CHANNEL_NAME = "Mock服务器通知渠道";

    private static final String ACTION_WANT_STOP = "stop_service";

    private HttpServer httpServer;

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
                    new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            );
            //
            Notification notification = new Notification.Builder(this, CHANNEL_ID)
                    .setChannelId(CHANNEL_ID)
                    .setContentTitle("Mock服务器已启用")
                    .setContentIntent(getIntent())
                    .setContentText("Mock服务器正在运行...点击可关闭")
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setSubText("Mock")
                    .setSmallIcon(android.R.drawable.presence_online)
                    .build();
            startForeground(1, notification);
        }
        new Thread(() -> {
            String host = MockDefault.HOST_NAME;
            int port = MockDefault.PORT;
            try {
                httpServer = new HttpServer(host, port);
                httpServer.start();
                LogUtil.i("已启动" + port + "mock服务器");
            } catch (Exception e) {
                LogUtil.e("mock服务器启动失败", e);
            }
        })
                .start();
    }

    private PendingIntent getIntent() {
        Intent intent = new Intent(this, MockService.class);
        intent.setAction(ACTION_WANT_STOP);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_WANT_STOP.equals(action)) {
                MockLauncher.stop(this);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (httpServer != null) {
            httpServer.stop();
        }
        LogUtil.i("MockService已销毁");
    }
}
