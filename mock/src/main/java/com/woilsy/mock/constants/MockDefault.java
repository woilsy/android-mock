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
        return formatMockUrl(HOST_NAME, port);
    }

    public static String formatMockUrl(String host, int port) {
        return "http://" + host + ":" + port;
    }

}
