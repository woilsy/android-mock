package com.woilsy.mock.exe;

import com.woilsy.mock.Mocker;
import com.woilsy.mock.constants.MockDefault;
import com.woilsy.mock.parse.MockDataParse;
import com.woilsy.mock.server.HttpServer;
import com.woilsy.mock.utils.LogUtil;

import java.io.IOException;
import java.net.BindException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 用于执行HttpServer
 */
public class MockServerExecutor {

    private HttpServer httpServer;

    private final ExecutorService pool = Executors.newCachedThreadPool();

    public void runMockServer(int port) {
        pool.execute(() -> startMockServer(port));
    }

    private void startMockServer(int port) {
        try {
            String host = MockDefault.HOST_NAME;
            httpServer = new HttpServer(host, port, MockDataParse::getHttpData);
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
    }

    public void stopMockServer() {
        if (httpServer != null) {
            httpServer.stop();
            httpServer = null;
        }
        pool.shutdown();
    }
}
