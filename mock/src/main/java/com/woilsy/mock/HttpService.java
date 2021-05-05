package com.woilsy.mock;

import android.util.Log;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.protocols.http.response.Response;

import java.io.IOException;
import java.util.HashMap;

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
        //uri
        String uri = session.getUri();
        String data = UrlManager.getInstance().urlDataMap.get(uri);
        Log.d(TAG, "客户端请求url-> " + uri + " 将返回Mock数据->" + data);
        //
        //Method get post delete put
        Method method = Method.lookup(session.getMethod().name());
        if (method == Method.GET) {
            return newFixedLengthResponse(data);
        } else if (method == Method.POST) {
            return newFixedLengthResponse(data);
        } else if (method == Method.PUT) {
            return newFixedLengthResponse(data);
        } else if (method == Method.DELETE) {
            return newFixedLengthResponse(data);
        }
        return newFixedLengthResponse("Unsupported request type");
    }

    @Override
    public Response handle(IHTTPSession session) {
        return super.handle(session);
    }
}
