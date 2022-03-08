package com.woilsy.mock.server;

import com.koushikdutta.async.http.Headers;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.woilsy.mock.Mocker;
import com.woilsy.mock.utils.LogUtil;

import java.io.IOException;

public class HttpServer implements IMockServer, HttpServerRequestCallback {

    private final String host;

    private final int port;

    private AsyncHttpServer httpServer;

    public HttpServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private String getContentType(Headers headers) {
        String key1 = "content-type";
        String key2 = "Content-Type";
        String h1 = headers.get(key1);
        return h1 == null ? headers.get(key2) : h1;
    }

    @Override
    public void start() throws IOException {
        httpServer = new AsyncHttpServer();
        httpServer.addAction("DELETE", "[\\d\\D]*", this);
        httpServer.addAction("PUT", "[\\d\\D]*", this);
        httpServer.addAction("GET", "[\\d\\D]*", this);
        httpServer.addAction("POST", "[\\d\\D]*", this);
        httpServer.listen(port);
    }

    @Override
    public void stop() {
        if (httpServer != null) {
            httpServer.stop();
        }
    }

    @Override
    public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
        String path = request.getPath();
        Headers headers = request.getHeaders();
        String method = request.getMethod();
        String contentType = getContentType(headers);
        String data = Mocker.getHttpData(path, method);
        LogUtil.i("客户端请求path->" + path + ",将返回mock数据->" + data);
        if (data == null) {//部分情况没有mock数据，例如定义返回类型为ResponseBody且没有为其填充数据
            int code = 404;
            AsyncHttpServerResponse httpServerResponse = response.code(404);
            String message = "没有找到" + path + "的mock数据，" + "错误原因：如果定义返回类型为非Bean类，那么需要为其填充数据";
            String sbJson = "{" + "\"code\"" + ":" + code + ",\"message\"" + ":\"" + message + "\"" + "}";
            httpServerResponse.send(sbJson);
        } else {
            response.send(contentType, data);
        }
    }
}
