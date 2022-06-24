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

    public static String formatMockUrl(int port) {
        return "http://" + HOST_NAME + ":" + port;
    }

}
