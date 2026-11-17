package org.peter.http;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.Map;

/**
 * 对 OkHttp 的一层薄封装：发请求、拿状态码和 body、保证 body 一定被关闭。
 * <p>
 * 为什么需要这层封装：OkHttp 的 {@link Response} 实现了 {@link AutoCloseable}，
 * 但「拿到响应就丢掉」是极其常见的写法，而它会造成连接泄漏 ——
 * 连接池里的连接永远不归还，最终超时。把 {@code try (Response ...)} 收进一个方法，
 * 调用方就没有机会写错。
 */
public final class HttpFetcher {

    private final OkHttpClient client;

    public HttpFetcher(OkHttpClient client) {
        this.client = client;
    }

    /** 一次 HTTP 交换的结果。状态码 + body 文本。 */
    public record Result(int statusCode, String body) {
    }

    /** 发 GET 请求，返回响应正文。不检查状态码——4xx/5xx 也算「成功拿到响应」。 */
    public String get(String url) throws IOException {
        return fetch(url).body();
    }

    /** 带自定义请求头的 GET。 */
    public String get(String url, Map<String, String> headers) throws IOException {
        return fetch(url, headers).body();
    }

    public Result fetch(String url) throws IOException {
        return fetch(url, Map.of());
    }

    /**
     * 发 GET 并返回状态码 + body。
     * <p>
     * {@code try (Response response = ...)} 是这里的关键：它保证 body 被关闭，
     * 连接才会归还连接池。缺了它，连续请求同一主机会因为连接耗尽而卡住。
     */
    public Result fetch(String url, Map<String, String> headers) throws IOException {
        HttpUrl parsed = HttpUrl.parse(url);
        if (parsed == null) {
            // OkHttp 的 Request.Builder.url(String) 会抛 IllegalArgumentException，
            // 但消息里不带原始输入；这里先自己校验，好让报错能指回调用方写错的那个串
            throw new IllegalArgumentException("not a valid absolute http(s) URL: " + url);
        }

        Request.Builder builder = new Request.Builder().url(parsed).get();
        headers.forEach(builder::addHeader);

        try (Response response = client.newCall(builder.build()).execute()) {
            ResponseBody body = response.body();
            return new Result(response.code(), body == null ? "" : body.string());
        }
    }
}
