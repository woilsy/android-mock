package com.woilsy.mock.server;

import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;
import com.woilsy.mock.data.MockUrlData;
import com.woilsy.mock.options.MockOptions;
import com.woilsy.mock.utils.LogUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fi.iki.elonen.NanoHTTPD;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class HttpServer extends NanoHTTPD {

    private OkHttpClient mOkHttpClient;

    public HttpServer(int port) {
        super(port);
    }

    public HttpServer(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        HashMap<String, String> bodyMap = new HashMap<>();
        try {
            session.parseBody(bodyMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //uri
        String uri = session.getUri();
        //获取BaseUrl
        String s = MockUrlData.excludeUrlMap.get(uri);
        if (s != null && !s.isEmpty()) {
            boolean needRedirect = MockOptions.BASE_URL_BACK_UP.equals(s);
            if (needRedirect) {//需要重定向 并返回该数据
                return synRequest(session, bodyMap);
            }
        }
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

    private Response synRequest(IHTTPSession session, HashMap<String, String> bodyMap) {
        String uri = session.getUri();
        String backUrl = MockOptions.BASE_URL_BACK_UP;
        if (!uri.isEmpty() && !backUrl.isEmpty()) {
            String url = backUrl + uri;
            LogUtil.i("请求BackUpUrl：" + url);
            //创建builder
            Request.Builder builder = new Request.Builder();
            //header
            Map<String, String> headers = session.getHeaders();
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            for (Map.Entry<String, String> en : entries) {
                builder.addHeader(en.getKey(), en.getValue());
            }
            //build
            Method method = session.getMethod();
            Request request;

            String contentType = getContentType(headers);
            if (method == Method.GET || method == Method.DELETE) {
                String queryParameterString = session.getQueryParameterString();
                String params = (queryParameterString == null || queryParameterString.isEmpty()) ? "" : "?" + queryParameterString;
                request = builder.url(url + params).get().build();
            } else {//PUT and POST
                String content = getBody(session.getParms(), bodyMap, contentType, method == Method.PUT);
                RequestBody body = RequestBody.create(MediaType.get(contentType),
                        Objects.requireNonNull(content));
                request = builder.url(url).post(body).build();
            }
            try {
                Call call = getHttpClient().newCall(request);
                okhttp3.Response response = call.execute();
                int code = response.code();
                ResponseBody body = response.body();
                String content = body == null ? "" : body.string();
                if (code == 200) {
                    return newFixedLengthResponse(Response.Status.OK, contentType, content);
                } else {
                    Response.Status[] values = Response.Status.values();
                    Response.Status status = Response.Status.REDIRECT;
                    for (Response.Status rs : values) {
                        if (rs.getRequestStatus() == code) {
                            status = rs;
                            break;
                        }
                    }
                    return newFixedLengthResponse(status, MIME_PLAINTEXT, content);
                }
            } catch (Exception e) {
                LogUtil.e("重定向网络请求异常，将向外抛出该异常");
                throw new RuntimeException(e);
            }
        }
        return newFixedLengthResponse(Response.Status.NO_CONTENT, MIME_PLAINTEXT, "");
    }

    private String getContentType(Map<String, String> headers) {
        String key1 = "content-type";
        String key2 = "Content-Type";
        String h1 = headers.get(key1);
        return h1 == null ? headers.get(key2) : h1;
    }

    private String getBody(Map<String, String> params, HashMap<String, String> bodyMap, String contentType, boolean putOrPost) {
        String content;
        if ("application/x-www-form-urlencoded".equals(contentType)) {//从params获取
            StringBuilder sb = new StringBuilder();
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> en : entrySet) {
                sb.append(en.getKey()).append("=").append(en.getValue()).append("&");
            }
            content = sb.toString();
        } else {//从body获取后转换为json
            if (putOrPost) {
                content = bodyMap.get("content");
            } else {
                content = bodyMap.get("postData");
            }
        }
        return content;
    }

    private OkHttpClient getHttpClient() {
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient.Builder()
                    .readTimeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .connectTimeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .callTimeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .addInterceptor(new LogInterceptor(LogUtil::i))
                    .retryOnConnectionFailure(false)
                    .build();
        }
        return mOkHttpClient;
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
