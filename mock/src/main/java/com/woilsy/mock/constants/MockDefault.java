package com.woilsy.mock.constants;

public class MockDefault {

    /**
     * Mock默认服务器HostName
     */
    public static final String HOST_NAME = "127.0.0.1";
    /**
     * Mock默认服务器端口
     */
    public static final int PORT = 8080;

    //Mock默认数据 可通过实现Rule 传入后自定义
    public static final String IMAGE = "https://bkimg.cdn.bcebos.com/pic/cb8065380cd79123b9bc93cba7345982b2b78034?x-bce-process=image/resize,m_lfit,h_500,limit_1/format,f_auto";
    public static final int AGE = 18;
    public static final String NAME = "张三";
    public static final String ADDRESS = "xx省xx市xx区xx镇1234号";
    public static final String NICKNAME = "我上我也行";
    public static final String PHONE = "18688888888";

    public static String formatMockUrl(int port) {
        return "http://" + HOST_NAME + ":" + port;
    }

}
