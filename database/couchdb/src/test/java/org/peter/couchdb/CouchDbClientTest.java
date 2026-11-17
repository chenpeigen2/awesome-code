package org.peter.couchdb;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link CouchDbClient} 的行为测试。
 * <p>
 * 用 JDK 自带的 {@code com.sun.net.httpserver} 起一个假 CouchDB：
 * 不需要真的装 CouchDB，测试离线、确定、可断言请求细节。
 * <p>
 * 原来的 {@code DbUtils} 无法这样测：全静态状态、返回 void、错误被 printStackTrace 吞掉。
 */
class CouchDbClientTest {

    /** 记录收到的请求，供断言使用。 */
    record Recorded(String method, String path, String body, Map<String, List<String>> headers) {
        String header(String name) {
            List<String> values = headers.get(name.toLowerCase());
            return values == null || values.isEmpty() ? null : values.get(0);
        }
    }

    private HttpServer server;
    private final List<Recorded> recorded = new ArrayList<>();
    /** 路径 → (状态码, body, 额外响应头)。 */
    private final Map<String, Object[]> routes = new ConcurrentHashMap<>();
    private CouchDbClient client;

    @BeforeEach
    void startFakeCouchDb() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> handle(exchange));
        server.start();

        int port = server.getAddress().getPort();
        client = new CouchDbClient("http://127.0.0.1:" + port, "admin", "secret");
    }

    @AfterEach
    void stop() {
        server.stop(0);
    }

    private void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        recorded.add(new Recorded(
                exchange.getRequestMethod(), path, body, exchange.getRequestHeaders()));

        // 路由键是 "METHOD path"：同一个路径上 PUT(建库) 和 GET(取文档) 结果不同，
        // 只按 path 路由会互相覆盖
        String key = exchange.getRequestMethod() + " " + path;
        Object[] route = routes.getOrDefault(key, new Object[]{404, "{\"error\":\"not_found\"}", Map.of()});
        int status = (int) route[0];
        String responseBody = (String) route[1];
        @SuppressWarnings("unchecked")
        Map<String, String> extraHeaders = (Map<String, String>) route[2];

        extraHeaders.forEach((k, v) -> exchange.getResponseHeaders().add(k, v));
        byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    private void route(String methodAndPath, int status, String body) {
        route(methodAndPath, status, body, Map.of());
    }

    private void route(String methodAndPath, int status, String body, Map<String, String> headers) {
        routes.put(methodAndPath, new Object[]{status, body, headers});
    }

    private void authenticateSuccessfully() {
        route("POST /_session", 200, "{\"ok\":true,\"name\":\"admin\"}",
                Map.of("Set-Cookie", "AuthSession=abc123; Version=1; Path=/; HttpOnly"));
    }

    @Test
    void authenticateStoresSessionCookie() {
        authenticateSuccessfully();

        client.authenticate();

        Recorded session = recorded.get(0);
        assertEquals("POST", session.method());
        assertEquals("/_session", session.path());
        assertTrue(session.body().contains("name=admin"), session.body());
    }

    @Test
    void authenticatedRequestsCarryTheCookie() {
        authenticateSuccessfully();
        route("GET /books", 200, "{\"db_name\":\"books\"}");

        client.databaseInfo("books");

        // 第一个请求是认证，第二个才是业务请求
        assertEquals(2, recorded.size());
        assertEquals("AuthSession=abc123", recorded.get(1).header("Cookie"));
    }

    @Test
    void badCredentialsThrowInsteadOfIndexOutOfBounds() {
        // 原实现直接 headers().values("Set-Cookie").get(0)，凭据错时会抛
        // IndexOutOfBoundsException —— 完全误导人的异常类型
        route("POST /_session", 401, "{\"error\":\"unauthorized\"}");

        var failure = assertThrows(CouchDbClient.CouchDbException.class, client::authenticate);

        assertEquals(401, failure.statusCode());
        assertTrue(failure.getMessage().contains("unauthorized"), failure.getMessage());
    }

    @Test
    void createDatabaseReturnsParsedResponse() {
        authenticateSuccessfully();
        route("PUT /books", 201, "{\"ok\":true}");

        JsonObject result = client.createDatabase("books");

        assertTrue(result.get("ok").getAsBoolean());
    }

    @Test
    void existingDatabaseIsTreatedAsSuccess() {
        authenticateSuccessfully();
        // CouchDB 对已存在的库返回 412 Precondition Failed
        route("PUT /books", 412, "{\"error\":\"file_exists\"}");

        JsonObject result = client.createDatabase("books");

        assertTrue(result.get("existed").getAsBoolean(), result.toString());
    }

    @Test
    void httpErrorIsNotSilentlySwallowed() {
        // 关键回归：原实现 catch (IOException e) { e.printStackTrace(); }
        // 调用方完全不知道失败了。现在必须抛异常并带上状态码和响应体。
        authenticateSuccessfully();
        route("GET /books", 500, "{\"error\":\"internal\"}");

        var failure = assertThrows(CouchDbClient.CouchDbException.class,
                () -> client.databaseInfo("books"));

        assertEquals(500, failure.statusCode());
        assertTrue(failure.getMessage().contains("internal"), failure.getMessage());
    }

    @Test
    void networkFailureBecomesCouchDbExceptionWithCause() {
        // 端口上没人监听：网络层失败，状态码是 -1，且 cause 保留原始 IOException
        CouchDbClient dead = new CouchDbClient("http://127.0.0.1:1", "admin", "x");

        var failure = assertThrows(CouchDbClient.CouchDbException.class, dead::authenticate);

        assertEquals(-1, failure.statusCode());
        assertTrue(failure.getCause() instanceof IOException, String.valueOf(failure.getCause()));
    }

    @Test
    void insertDocumentPostsJsonBody() {
        authenticateSuccessfully();
        route("POST /books", 201, "{\"ok\":true,\"id\":\"abc\"}");

        JsonObject result = client.insertDocument("books", "{\"title\":\"CouchDB\"}");

        assertEquals("abc", result.get("id").getAsString());
        Recorded insert = recorded.get(1);
        assertEquals("POST", insert.method());
        assertEquals("{\"title\":\"CouchDB\"}", insert.body());
        assertEquals("application/json", insert.header("Content-Type"));
    }

    @Test
    void trailingSlashInBaseUrlDoesNotProduceDoubleSlash() {
        authenticateSuccessfully();
        route("GET /books", 200, "{\"db_name\":\"books\"}");
        int port = server.getAddress().getPort();

        new CouchDbClient("http://127.0.0.1:" + port + "/", "admin", "secret")
                .databaseInfo("books");

        assertEquals("/books", recorded.get(1).path());
    }

    @Test
    void demoRunsAgainstFakeServer() {
        authenticateSuccessfully();
        // demo 用的是 "demo" 库，三个请求分别路由
        route("PUT /demo", 201, "{\"ok\":true}");
        route("POST /demo", 201, "{\"ok\":true,\"id\":\"doc1\"}");
        route("GET /demo/doc1", 200, "{\"_id\":\"doc1\",\"title\":\"CouchDB 入门\"}");

        String report = new CouchDbDemo(client).run();

        assertTrue(report.contains("create: ok"), report);
        assertTrue(report.contains("id=doc1"), report);
        assertTrue(report.contains("CouchDB 入门"), report);
    }
}
