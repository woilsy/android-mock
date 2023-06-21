package com.woilsy.mock.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class NetUtil {

    public static String getIp(Context context) {
        try {
            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            //检查Wifi状态
            if (wm.isWifiEnabled()) {
                WifiInfo wi = wm.getConnectionInfo();
                //获取32位整型IP地址
                int ipAdd = wi.getIpAddress();
                //把整型地址转换成“*.*.*.*”地址
                return ipAdd == 0 ? null : intToIp(ipAdd);
            }
        } catch (Exception e) {
            Log.e("NetUtil", "getIp error", e);
        }
        return null;
    }

    private static String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

}
