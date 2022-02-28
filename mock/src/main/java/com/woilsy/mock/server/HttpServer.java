package com.woilsy.mock.server;

import com.woilsy.mock.data.MockUrlData;
import com.woilsy.mock.utils.LogUtil;
import com.woilsy.mock.utils.UriUtil;

import java.util.Map;

public class HttpServer extends NanoHTTPD {

    public HttpServer(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        //uri /hotkey/json
        String uri = session.getUri();
        String uriKey = UriUtil.getUriKey(uri);
        String data = MockUrlData.get(uriKey);
        LogUtil.i("客户端请求path->" + uri + ",数据key->" + uriKey + ",将返回mock数据->" + data);
        //Method get post delete put
        Method method = session.getMethod();
        String contentType = getContentType(session.getHeaders());
        if (method == Method.GET) {
            return newFixedLengthResponse(Response.Status.OK, contentType, data);
        } else if (method == Method.POST) {
            return newFixedLengthResponse(Response.Status.OK, contentType, data);
        } else if (method == Method.PUT) {
            return newFixedLengthResponse(Response.Status.OK, contentType, data);
        } else if (method == Method.DELETE) {
            return newFixedLengthResponse(Response.Status.OK, contentType, data);
        } else {
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, contentType, "Unsupported request type");
        }
    }

    private String getContentType(Map<String, String> headers) {
        String key1 = "content-type";
        String key2 = "Content-Type";
        String h1 = headers.get(key1);
        return h1 == null ? headers.get(key2) : h1;
    }

}
