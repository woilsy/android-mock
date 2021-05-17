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
import android.widget.Toast;

import com.woilsy.mock.MockLauncher;
import com.woilsy.mock.constants.MockDefault;
import com.woilsy.mock.server.HttpServer;
import com.woilsy.mock.utils.LogUtil;

public class MockService extends Service {

    private static final String CHANNEL_ID = "channel_mock_service";

    private static final String CHANNEL_NAME = "Mock服务器通知渠道";

    private static final String ACTION_TRANS_BASEURL = "trans_base_url";

    private static final int NOTIFICATION_ID = 1;

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
            startForeground(NOTIFICATION_ID, getNotification(MockDefault.BASE_URL));
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

    private Notification getNotification(String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new Notification.Builder(this, CHANNEL_ID)
                    .setChannelId(CHANNEL_ID)
                    .setContentTitle("Mock服务器正在运行...")
                    .setContentIntent(getIntent())
                    .setContentText(content)
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setSubText("Mock")
                    .setSmallIcon(android.R.drawable.presence_online)
                    .build();
        } else {
            return new Notification.Builder(this)
                    .setContentTitle("Mock服务器正在运行...")
                    .setContentIntent(getIntent())
                    .setContentText(content)
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setSubText("Mock")
                    .setSmallIcon(android.R.drawable.presence_online)
                    .build();
        }
    }

    private PendingIntent getIntent() {
        Intent intent = new Intent(this, MockService.class);
        intent.setAction(ACTION_TRANS_BASEURL);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_TRANS_BASEURL.equals(action)) {
                boolean newValue = !MockLauncher.isBaseUrlOrOriginalUrl();
                MockLauncher.setBaseUrlOrOriginalUrl(newValue);
                String originalBaseUrl = MockLauncher.getMockOption().getOriginalBaseUrl();
                String newUrl = newValue ? MockDefault.BASE_URL : originalBaseUrl;
                Toast.makeText(this, newUrl, Toast.LENGTH_LONG).show();
                startForeground(NOTIFICATION_ID, getNotification(newUrl));
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
