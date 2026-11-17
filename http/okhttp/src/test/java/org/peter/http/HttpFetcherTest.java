package org.peter.http;

import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link HttpFetcher} 的行为测试。
 * <p>
 * 用 {@code mockwebserver3} 起一个本地 HTTP 服务，完全离线、确定性、可断言。
 * 原来的实现请求 {@code https://wwww.baidu.com}（四个 w，域名不存在），
 * 而且 {@code Response} 用完不关 —— 这类代码无法测试，也从未被测试过。
 */
class HttpFetcherTest {

    private MockWebServer server;
    private HttpFetcher fetcher;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        fetcher = new HttpFetcher(new OkHttpClient());
    }

    @AfterEach
    void tearDown() throws IOException {
        server.close();
    }

    private String url(String path) {
        return server.url(path).toString();
    }

    @Test
    void returnsResponseBody() throws IOException {
        server.enqueue(new MockResponse.Builder().body("hello").build());

        assertEquals("hello", fetcher.get(url("/greet")));
    }

    @Test
    void returnsStatusAndBodyForNonOkResponse() throws IOException {
        // 4xx/5xx 也是「拿到响应」，不该抛异常；只有网络层失败才抛
        server.enqueue(new MockResponse.Builder().code(404).body("not found").build());

        HttpFetcher.Result result = fetcher.fetch(url("/missing"));

        assertEquals(404, result.statusCode());
        assertEquals("not found", result.body());
    }

    @Test
    void sendsRequestHeaders() throws IOException, InterruptedException {
        server.enqueue(new MockResponse.Builder().body("ok").build());

        fetcher.get(url("/h"), java.util.Map.of("X-Trace-Id", "abc123"));

        var recorded = server.takeRequest();
        assertEquals("abc123", recorded.getHeaders().get("X-Trace-Id"));
    }

    @Test
    void closesResponseBodySoConnectionsAreReusable() throws IOException {
        // 关键回归：原实现 client.newCall(request).execute() 直接丢掉 Response，
        // body 从不关闭，连接泄漏。这里连续请求两次，第二次必须复用同一条连接。
        server.enqueue(new MockResponse.Builder().body("one").build());
        server.enqueue(new MockResponse.Builder().body("two").build());

        assertEquals("one", fetcher.get(url("/a")));
        assertEquals("two", fetcher.get(url("/b")));

        assertEquals(2, server.getRequestCount());
    }

    @Test
    void propagatesNetworkFailureAsIOException() {
        // 端口上没有任何服务：必须抛出可诊断的异常，而不是返回 null 或空串
        String deadUrl = "http://127.0.0.1:1/nowhere";

        assertThrows(IOException.class, () -> fetcher.get(deadUrl));
    }

    @Test
    void invalidUrlIsRejectedWithClearMessage() {
        var failure = assertThrows(IllegalArgumentException.class, () -> fetcher.get("wwww.baidu.com"));

        assertTrue(failure.getMessage().contains("wwww.baidu.com"), failure.getMessage());
    }

    @Test
    void cookieJarCarriesCookiesBetweenRequests() throws IOException, InterruptedException {
        server.enqueue(new MockResponse.Builder()
                .addHeader("Set-Cookie", "session=abc; Path=/")
                .body("login").build());
        server.enqueue(new MockResponse.Builder().body("profile").build());

        HttpFetcher cookieFetcher = new HttpFetcher(
                new OkHttpClient.Builder()
                        .cookieJar(new CookieJarImpl(new MemoryCookieStore()))
                        .build());

        cookieFetcher.get(url("/login"));
        cookieFetcher.get(url("/profile"));

        // 第一次请求时本地还没有 cookie，不该带 Cookie 头
        assertNull(server.takeRequest().getHeaders().get("Cookie"));
        // 第二次请求必须带上第一次响应种下的 cookie —— 这才证明 CookieJar 真的在工作
        assertEquals("session=abc", server.takeRequest().getHeaders().get("Cookie"));
    }

    @Test
    void demoReportsRealExchange() throws IOException {
        server.enqueue(new MockResponse.Builder()
                .addHeader("Content-Type", "text/plain")
                .body("pong").build());

        String report = new HttpDemo().run(url("/ping"));

        assertTrue(report.contains("status: 200"), report);
        assertTrue(report.contains("body: pong"), report);
    }
}
