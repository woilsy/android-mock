package com.woilsy.mock;

import android.util.Log;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.protocols.http.response.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        String uriKey = getUriKey(uri);
        String data = MockUrlData.get(uriKey);
        Log.d(TAG, "客户端请求url->" + uri + ",数据key->" + uriKey + ",将返回mock数据->" + data);
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
        } else {
            return newFixedLengthResponse("Unsupported request type");
        }
    }

    //通过一定的对比规则返回实际有效的key值
    private String getUriKey(String uri) {
        if (uri == null || uri.isEmpty()) return null;
        String[] split1 = uri.split("/");
        Set<Map.Entry<String, String>> entries = MockUrlData.getMap().entrySet();
        for (Map.Entry<String, String> en : entries) {
            String key = en.getKey();
            if (key.contains("{") && split1.length > 2) {//表示有@Path形式的url，那么需要进行匹配 且
                String[] split2 = key.split("/");
                if (split1.length == split2.length) {//可比较
                    int max = split2.length;//允许max-1
                    int count = 0;
                    for (int j = 0; j < max; j++) {
                        if (split1[j].equals(split2[j])) {
                            count++;
                        }
                    }
                    if (count >= max - 1) {
                        return key;
                    }
                }
            }
        }
        return uri;
    }

    @Override
    public Response handle(IHTTPSession session) {
        return super.handle(session);
    }
}
