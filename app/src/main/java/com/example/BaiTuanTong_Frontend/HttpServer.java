package com.example.BaiTuanTong_Frontend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Cookie;
import okhttp3.CookieJar;

public class HttpServer {
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    private static final String SERVERURL = "http://47.92.233.174:5000/";
    private static final String LOCALURL = "http://10.0.2.2:5000/";
    public static final String CURRENTURL = SERVERURL;

    private static final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    public static final OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                    // 从响应头中保存 cookie
                    cookieStore.put(httpUrl.host(), list);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                    // 添加 cookie
                    List<Cookie> cookies = cookieStore.get(httpUrl.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            })
            .build();
}
