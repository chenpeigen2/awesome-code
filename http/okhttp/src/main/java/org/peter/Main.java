package org.peter;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello world!");

        var cookieJar = new CookieJarImpl(new MemoryCookieStore());

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cookieJar(cookieJar);

        var client = builder.build();

        String url = "https://wwww.baidu.com";
        Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        client.newCall(request).execute();

        url = "https://www.iqiyi.com/";
        request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Thread.currentThread().sleep(1000L);
        client.newCall(request).execute();
    }
}