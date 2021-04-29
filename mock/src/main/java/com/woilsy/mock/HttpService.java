package com.woilsy.mock;

import android.util.Log;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.protocols.http.response.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.nanohttpd.protocols.http.response.Response.newFixedLengthResponse;

public class HttpService extends NanoHTTPD {

    private static final String TAG = "HttpService";

    public HttpService(int port) {
        super(port);
    }

    public HttpService(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    protected Response serve(IHTTPSession session) {
        try {
            // 这一句话必须要写，否则在获取数据时，获取不到数据
            session.parseBody(new HashMap<>());
        } catch (IOException | ResponseException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        Method method = session.getMethod();
        String uri = session.getUri();
        Map<String, String> parms = session.getParms();
        String data = parms.get("data");//这里的data是POST提交表单时key
        Log.i(TAG, "uri: " + uri);//如果有uri,会打印出uri
        Log.i(TAG, "data: " + data);
        builder.append("任意内容");// 反馈给调用者的数据
        return newFixedLengthResponse(builder.toString());
    }
}
