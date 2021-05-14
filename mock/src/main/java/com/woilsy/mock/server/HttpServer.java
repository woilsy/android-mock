package com.woilsy.mock.server;

import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;
import com.woilsy.mock.MockLauncher;
import com.woilsy.mock.data.MockUrlData;
import com.woilsy.mock.entity.ExcludeInfo;
import com.woilsy.mock.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

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
        //uri
        String uri = session.getUri();
        //使用哪一个BaseUrl？
        boolean baseUrlOrOriginalUrl = MockLauncher.isBaseUrlOrOriginalUrl();
        ExcludeInfo s = MockLauncher.excludeInfoMap.get(uri);
        if (!baseUrlOrOriginalUrl || (s != null && s.isNeedRedirect())) {
            //需要重定向 并返回该数据
            String url = (!baseUrlOrOriginalUrl) ? MockLauncher.getMockOption().getOriginalBaseUrl() : s.getRedirectBaseUrl();
            return synRequest(url, session);
        } else {
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
    }

    private Response synRequest(String redirectBaseUrl, IHTTPSession session) {
        String uri = session.getUri();
        if (!uri.isEmpty() && !redirectBaseUrl.isEmpty()) {
            //get body map
            HashMap<String, String> bodyMap = new HashMap<>();
            try {
                session.parseBody(bodyMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String url = redirectBaseUrl + uri;
            LogUtil.i("正在重定向：" + url);
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
            Request request = null;
            String contentType = getContentType(headers);
            try {
                if (method == Method.GET) {
                    String queryParameterString = session.getQueryParameterString();
                    String params = (queryParameterString == null || queryParameterString.isEmpty()) ? "" : "?" + queryParameterString;
                    request = builder.url(url + params).get().build();
                } else if (method == Method.DELETE) {
                    if (session instanceof HTTPSession) {
                        long bodySize = ((HTTPSession) session).getBodySize();
                        LogUtil.i("bodySize：" + bodySize);
                    }
                    String queryParameterString = session.getQueryParameterString();
                    String content = getContent(session.getParms(), bodyMap, contentType, "");
                    String params = (queryParameterString == null || queryParameterString.isEmpty()) ? "" : "?" + queryParameterString;
                    if (isEmpty(contentType) || isEmpty(content)) {
                        request = builder.url(url + params).delete().build();
                    } else {
                        RequestBody requestBody = RequestBody.create(MediaType.get(contentType), Objects.requireNonNull(content));
                        request = builder.url(url + params).delete(requestBody).build();
                    }
                } else if (method == Method.PUT) {//PUT and POST
                    String content = getContent(session.getParms(), bodyMap, contentType, "content");
                    RequestBody body = RequestBody.create(MediaType.get(contentType),
                            Objects.requireNonNull(content));
                    request = builder.url(url).put(body).build();
                } else if (method == Method.POST) {
                    String content = getContent(session.getParms(), bodyMap, contentType, "postData");
                    RequestBody body = RequestBody.create(MediaType.get(contentType),
                            Objects.requireNonNull(content));
                    request = builder.url(url).post(body).build();
                }
            } catch (Exception e) {
                LogUtil.e("请求处理异常，将向外抛出该异常", e);
                throw new RuntimeException(e);
            }
            try {
                if (request != null) {
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

    private boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    private String getContent(Map<String, String> params, HashMap<String, String> bodyMap, String contentType, String key) {
        LogUtil.i("params:" + params);
        String content;
        if ("application/x-www-form-urlencoded".equals(contentType)) {//从params获取
            StringBuilder sb = new StringBuilder();
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> en : entrySet) {
                sb.append(en.getKey()).append("=").append(en.getValue()).append("&");
            }
            content = sb.toString();
        } else {//从body获取后转换为json put "content" post "postData"
            LogUtil.i("bodyMap:" + bodyMap);
            if (isEmpty(key)) {//将bodyMap转换后返回
                JSONObject jo = new JSONObject();
                Set<Map.Entry<String, String>> entries = bodyMap.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    try {
                        jo.put(entry.getKey(), entry.getValue());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                content = jo.toString();
            } else {
                content = bodyMap.get(key);
            }
            LogUtil.i("content:" + content);
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
