package com.woilsy.mock.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.woilsy.mock.Mocker;
import com.woilsy.mock.constants.MockDefault;
import com.woilsy.mock.options.MockOptions;
import com.woilsy.mock.server.HttpServer;
import com.woilsy.mock.utils.LogUtil;

import java.io.IOException;
import java.net.BindException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MockService extends Service {

    private static final String CHANNEL_NAME = "Mock服务器通知渠道";

    private static final String ACTION_MOCK_START = "start_mock_server";

    private static final String ACTION_TRANS_BASEURL = "trans_base_url";

    private HttpServer httpServer;

    private String channelId;

    private int notificationId;

    private final ExecutorService pool = Executors.newCachedThreadPool();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean isMockRunning() {
        return httpServer != null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
        boolean mockUrlOrOriginalUrl = Mocker.isMockUrlOrOriginalUrl();
        String mockUrl = Mocker.getLocalUrl(this);
        return mockUrlOrOriginalUrl ? (mockUrl + "运行中") : (mockUrl + "已关闭");
    }

    private void startMockServer(int port) {
        pool.execute(() -> {
            try {
                String host = MockDefault.HOST_NAME;
                httpServer = new HttpServer(host, port);
                httpServer.start();
                Mocker.getMockOption().setPort(port);
                LogUtil.i("已启动mock服务器，端口：" + port);
            } catch (BindException e) {
                int newPort = port + 1;
                LogUtil.e("Mock服务器启动失败，尝试切换到端口：" + newPort, e);
                startMockServer(newPort);
            } catch (IOException e) {
                LogUtil.e("Mock服务器启动失败，请重新尝试", e);
            }
        });
    }

    private Notification getNotification(String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new Notification.Builder(this, channelId)
                    .setChannelId(channelId)
                    .setContentTitle("Mock服务运行中,点击切换")
                    .setContentIntent(getIntent())
                    .setContentText(content)
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setSubText("Mock")
                    .setSmallIcon(android.R.drawable.presence_online)
                    .build();
        } else {
            return new Notification.Builder(this)
                    .setContentTitle("Mock服务运行中,点击切换")
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
            if (ACTION_MOCK_START.equals(action)) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int port = extras.getInt("port");
                    startMockServer(port);
                }
            } else if (ACTION_TRANS_BASEURL.equals(action)) {
                boolean newValue = !Mocker.isMockUrlOrOriginalUrl();
                String originalBaseUrl = Mocker.getMockOption().getOriginalBaseUrl();
                if (originalBaseUrl != null && !originalBaseUrl.isEmpty()) {
                    Mocker.setMockUrlOrOriginalUrl(newValue);
                    startForeground(notificationId, getNotification(getNotifyContent()));
                    Toast.makeText(this, newValue ? "已开启mock" : "已关闭mock", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "请在至少请求一次网络后再尝试切换", Toast.LENGTH_LONG).show();
                }
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
        pool.shutdown();
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
