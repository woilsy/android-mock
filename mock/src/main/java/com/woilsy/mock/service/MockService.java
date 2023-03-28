package com.woilsy.mock.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import com.woilsy.mock.Mocker;
import com.woilsy.mock.exe.MockServerExecutor;
import com.woilsy.mock.options.MockOptions;
import com.woilsy.mock.utils.LogUtil;

public class MockService extends Service {

    private static final String CHANNEL_NAME = "Mock服务器通知渠道";

    private static final String ACTION_MOCK_START = "start_mock_server";

    private static final String ACTION_TRANS_BASEURL = "trans_base_url";

    private String channelId;

    private int notificationId;

    private MockServerExecutor mockServerExecutor;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //可能存在没有执行初始化 但是服务重启的情况 这种情况下运行服务是没有意义的
        if (!Mocker.isInit()) {
            stopSelf();
            return;
        }
        channelId = this.getApplication().getPackageName();
        notificationId = channelId.hashCode();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(
                    new NotificationChannel(channelId, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            );
        }
        startForeground(notificationId, getNotification(getNotifyContent()));
    }

    private String getNotifyContent() {
        String mockUrl = Mocker.getLocalUrl(this);
        return mockUrl + "已开启";
    }

    private void startMockServer(int port) {
        if (mockServerExecutor == null) {
            mockServerExecutor = new MockServerExecutor();
        }
        mockServerExecutor.runMockServer(port);
    }

    private Notification getNotification(String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new Notification.Builder(this, channelId)
                    .setChannelId(channelId)
                    .setContentTitle("Mock服务运行中")
                    .setContentText(content)
                    .setWhen(System.currentTimeMillis())
                    .setSubText("Mock")
                    .setSmallIcon(android.R.drawable.presence_online)
                    .build();
        } else {
            return new Notification.Builder(this)
                    .setContentTitle("Mock服务运行中")
                    .setContentText(content)
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setSubText("Mock")
                    .setSmallIcon(android.R.drawable.presence_online)
                    .build();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_MOCK_START.equals(action)) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int port = extras.getInt("port");
                    startMockServer(port);
                }
            } else if (ACTION_TRANS_BASEURL.equals(action)) {
                startForeground(notificationId, getNotification(getNotifyContent()));
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mockServerExecutor != null) {
            mockServerExecutor.stopMockServer();
            mockServerExecutor = null;
        }
        LogUtil.i("MockService已销毁");
    }

    public static void start(Context context, MockOptions options) {
        Intent intent = new Intent(context, MockService.class);
        intent.putExtra("port", options.getPort());
        intent.setAction(MockService.ACTION_MOCK_START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
}
