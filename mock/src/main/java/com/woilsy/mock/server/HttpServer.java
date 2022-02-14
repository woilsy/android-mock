package com.woilsy.mock.server;

import com.woilsy.mock.data.MockUrlData;
import com.woilsy.mock.utils.LogUtil;

import java.util.Map;
import java.util.Set;

public class HttpServer extends NanoHTTPD {

    public HttpServer(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        //uri /hotkey/json
        String uri = session.getUri();
        String uriKey = getUriKey(uri);
        String data = MockUrlData.get(uriKey);
        LogUtil.i("客户端请求url->" + uri + ",数据key->" + uriKey + ",将返回mock数据->" + data);
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

    //通过一定的对比规则返回实际有效的key值
    private String getUriKey(String uri) {
        if (uri == null || uri.isEmpty()) return null;
        String[] split1 = uri.split("/");
        Set<Map.Entry<String, String>> entries = MockUrlData.getMap().entrySet();
        for (Map.Entry<String, String> en : entries) {
            String key = en.getKey();//key中可能包含{}
            //如果包含{，则表示有{code}之类的，那么分割里面比较肯定就不会相等，直接跳过。
            if (key.contains("{") && split1.length > 2) {//表示有@Path形式的url，那么需要进行匹配 且
                String[] split2 = key.split("/");
                if (split1.length == split2.length) {//可比较
                    int max = split2.length;//允许max-1
                    int count = 0;
                    for (int j = 0; j < max; j++) {
                        String s2j = split2[j];
                        if (split1[j].equals(s2j)) {
                            count++;
                        } else if (s2j.startsWith("{") && s2j.endsWith("}")) {
                            count++;
                        } else {
                            //do nothing
                        }
                    }
                    if (count == max) {
                        return key;
                    }
                }
            }
        }
        return uri;
    }

}
