package org.peter.http;

import com.sun.net.httpserver.HttpServer;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * 演示入口：起一个本地 HTTP 服务，再用 {@link HttpFetcher} 请求它。
 * <p>
 * 运行：{@code ./gradlew :http:okhttp:demo}
 * <p>
 * 原来写死 {@code https://wwww.baidu.com}（四个 w，域名根本不存在），
 * demo 从来没成功过。现在用 JDK 自带的 {@code com.sun.net.httpserver} 起服务：
 * 不依赖外网、不引入额外依赖、输出确定。
 */
public final class HttpDemo {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/ping", exchange -> {
            byte[] body = "pong".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        });
        server.start();
        try {
            int port = server.getAddress().getPort();
            System.out.print(new HttpDemo().run("http://127.0.0.1:" + port + "/ping"));
        } finally {
            server.stop(0);
        }
    }

    /** 对给定 URL 发一次 GET，返回可读报告。 */
    public String run(String url) throws IOException {
        HttpFetcher fetcher = new HttpFetcher(new OkHttpClient());
        HttpFetcher.Result result = fetcher.fetch(url);

        return "OkHttp GET demo\n"
                + "url: " + url + "\n"
                + "status: " + result.statusCode() + "\n"
                + "body: " + result.body() + "\n";
    }
}
