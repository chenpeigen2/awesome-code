package org.peter.couchdb;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * CouchDB 的 HTTP API 客户端。
 * <p>
 * 原来的实现有几个结构性问题，这里一并解决：
 * <ul>
 *   <li><b>9 处 {@code printStackTrace()}</b> —— 错误被打印后吞掉，调用方拿不到任何信号，
 *       只能盯着控制台猜。现在统一抛 {@link CouchDbException}。</li>
 *   <li><b>全部方法返回 {@code void}</b> —— 建库成功和失败看起来一样。现在返回解析后的
 *       {@link JsonObject}，调用方能自己判断。</li>
 *   <li><b>认证状态藏在静态字段里</b> —— {@code static Cookie} + {@code static Map} 让这个类
 *       无法多实例、无法并行测试。现在每个实例持有自己的 cookie。</li>
 *   <li><b>硬编码 admin/123456</b> —— 现在由调用方传入，并允许用环境变量覆盖。</li>
 * </ul>
 * 只用 JDK 自带的 {@link HttpClient}，不引入 okhttp，减少一层依赖。
 */
public final class CouchDbClient {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient http;
    private final String baseUrl;
    private final String username;
    private final String password;

    /** 认证成功后拿到的 Cookie 头值。null 表示尚未认证。 */
    private String cookie;

    public CouchDbClient(String baseUrl, String username, String password) {
        this(HttpClient.newBuilder().connectTimeout(TIMEOUT).build(), baseUrl, username, password);
    }

    public CouchDbClient(HttpClient http, String baseUrl, String username, String password) {
        this.http = http;
        // 统一去掉末尾斜杠，避免拼出 http://host//db
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.username = username;
        this.password = password;
    }

    /** 从环境变量构造：{@code COUCHDB_URL} / {@code COUCHDB_USER} / {@code COUCHDB_PASSWORD}。 */
    public static CouchDbClient fromEnvironment() {
        return new CouchDbClient(
                envOr("COUCHDB_URL", "http://localhost:5984"),
                envOr("COUCHDB_USER", "admin"),
                envOr("COUCHDB_PASSWORD", ""));
    }

    private static String envOr(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }

    /**
     * 认证并缓存 cookie。
     * <p>
     * CouchDB 的 {@code /_session} 返回 {@code Set-Cookie: AuthSession=...}，
     * 后续请求带上它即可。这里显式检查状态码：凭据错误时 CouchDB 返回 401 且
     * body 里没有 Set-Cookie，原实现直接 {@code get(0)} 会抛 IndexOutOfBounds。
     */
    public void authenticate() {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/_session"))
                .timeout(TIMEOUT)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "name=" + encode(username) + "&password=" + encode(password)))
                .build();

        HttpResponse<String> response = send(request, "_session");
        if (response.statusCode() != 200) {
            throw new CouchDbException("authentication failed", response.statusCode(), response.body());
        }

        this.cookie = response.headers().firstValue("Set-Cookie")
                .map(value -> value.split(";", 2)[0])
                .orElseThrow(() -> new CouchDbException(
                        "authentication response carried no Set-Cookie", 200, response.body()));
    }

    private static String encode(String value) {
        return java.net.URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /** 建库。库已存在时 CouchDB 返回 412，这里视作成功（幂等）。 */
    public JsonObject createDatabase(String database) {
        HttpResponse<String> response = call("PUT", "/" + database, null);
        if (response.statusCode() == 412) {
            return JsonParser.parseString("{\"ok\":true,\"existed\":true}").getAsJsonObject();
        }
        return expectOk(response, "create database " + database);
    }

    /** 查库信息。 */
    public JsonObject databaseInfo(String database) {
        return expectOk(call("GET", "/" + database, null), "database info " + database);
    }

    /** 删库。 */
    public JsonObject deleteDatabase(String database) {
        return expectOk(call("DELETE", "/" + database, null), "delete database " + database);
    }

    /** 插入文档，body 必须是 JSON 字符串。 */
    public JsonObject insertDocument(String database, String json) {
        return expectOk(call("POST", "/" + database, json), "insert into " + database);
    }

    /** 按 id 取文档。 */
    public JsonObject findDocument(String database, String documentId) {
        return expectOk(call("GET", "/" + database + "/" + documentId, null),
                "find " + documentId);
    }

    /** 列出全部文档 id。 */
    public JsonObject allDocs(String database) {
        return expectOk(call("GET", "/" + database + "/_all_docs", null), "all docs");
    }

    /** 用 Mango selector 查询。 */
    public JsonObject find(String database, String selectorJson) {
        return expectOk(call("POST", "/" + database + "/_find", selectorJson), "find");
    }

    /** 建索引。 */
    public JsonObject createIndex(String database, String indexJson) {
        return expectOk(call("POST", "/" + database + "/_index", indexJson), "create index");
    }

    private JsonObject expectOk(HttpResponse<String> response, String what) {
        if (response.statusCode() / 100 != 2) {
            throw new CouchDbException(what + " failed", response.statusCode(), response.body());
        }
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    /** 发请求；需要认证时先自动认证。 */
    private HttpResponse<String> call(String method, String path, String jsonBody) {
        if (cookie == null) {
            authenticate();
        }
        HttpRequest.BodyPublisher body = jsonBody == null
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8);

        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .timeout(TIMEOUT)
                .header("Accept", "application/json")
                .header("Cookie", cookie)
                .method(method, body);
        if (jsonBody != null) {
            builder.header("Content-Type", "application/json");
        }
        return send(builder.build(), path);
    }

    private HttpResponse<String> send(HttpRequest request, String what) {
        try {
            return http.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException cause) {
            // 网络层失败（连不上、超时）单独区分：它不是 HTTP 错误，没有状态码
            throw new CouchDbException("I/O failure calling " + what + ": " + cause.getMessage(), cause);
        } catch (InterruptedException cause) {
            Thread.currentThread().interrupt();
            throw new CouchDbException("interrupted calling " + what, cause);
        }
    }

    /** CouchDB 调用失败。带状态码和响应体，便于定位。 */
    public static class CouchDbException extends RuntimeException {
        private final int statusCode;

        public CouchDbException(String message, int statusCode, String body) {
            super(message + " (status " + statusCode + "): " + body);
            this.statusCode = statusCode;
        }

        public CouchDbException(String message, Throwable cause) {
            super(message, cause);
            this.statusCode = -1;
        }

        /** HTTP 状态码；网络层失败时为 -1。 */
        public int statusCode() {
            return statusCode;
        }
    }
}
