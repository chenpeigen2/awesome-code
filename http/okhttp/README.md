# http/okhttp

OkHttp 5 的 HTTP 客户端示例，附一个 CookieJar 实现。

## 跑起来

```bash
./gradlew :http:okhttp:demo    # 起本地 HTTP 服务并发请求（完全离线）
./gradlew :http:okhttp:test    # 8 个测试
```

## 这个模块以前的问题

`Main.java` 请求了一个**不存在的域名**：

```java
String url = "https://wwww.baidu.com";   // 四个 w
client.newCall(request).execute();       // Response 直接丢掉
```

两个问题：

1. `wwww.baidu.com` 不存在，这个 demo 从来没成功过。
2. `Response` 实现 `AutoCloseable`，不关闭 = **连接泄漏**。连接池里的连接永远不归还，
   连续请求同一主机会把池耗尽然后卡住。原代码还配了 `Thread.sleep(1000)` 来「缓解」，
   实际上是掩盖症状。

## 现在的结构

**`HttpFetcher`** —— 把 `try (Response ...)` 收进方法内部，调用方没有机会写错：

```java
public Result fetch(String url, Map<String, String> headers) throws IOException {
    try (Response response = client.newCall(builder.build()).execute()) {
        ResponseBody body = response.body();
        return new Result(response.code(), body == null ? "" : body.string());
    }
}
```

**URL 先自己校验**。`Request.Builder.url(String)` 会抛 `IllegalArgumentException`，
但消息里不带原始输入；先调 `HttpUrl.parse` 就能让报错指回调用方写错的那个串。

**4xx/5xx 不抛异常**。它们也是「成功拿到响应」，由调用方按 `statusCode()` 判断。
只有网络层失败（连不上、超时）才抛 `IOException`。

## CookieJar 里修掉的真 bug

`MemoryCookieStore.add` 原来第一行是：

```java
if (!cookie.persistent()) return;   // ← 会话 cookie 全被丢掉
```

**会话 cookie（没有 `Max-Age` / `Expires`）天生就是非持久化的**，而登录 session
恰恰几乎都是会话 cookie。结果就是：所有登录态被静默丢弃，后续请求永远不带 `Cookie` 头。

`isCookieExpired` 已经能正确处理过期（OkHttp 把会话 cookie 的 `expiresAt` 设成
`Long.MAX_VALUE`），所以直接删掉那句 `return` 即可。

`cookieJarCarriesCookiesBetweenRequests` 是这条的回归测试：第一个请求断言
`Cookie` 头为 `null`，第二个请求断言它等于 `session=abc`。

## 测试为什么用 mockwebserver3

gRPC/HTTP 这类协议层的坑（连接泄漏、Cookie 传递、状态码处理）mock 掉传输层就全测不出来。
`mockwebserver3` 是 OkHttp 官方的进程内 HTTP 服务器，离线、确定性、能断言收到的请求头。

demo 则用 JDK 自带的 `com.sun.net.httpserver`，避免把 test 作用域的依赖带进 main。

## 文件

| 文件 | 作用 |
|---|---|
| `HttpFetcher` | 薄封装：发请求、返回状态码 + body、保证关闭 |
| `HttpDemo` | 起本地服务再请求它 |
| `CookieStore` / `MemoryCookieStore` / `CookieJarImpl` | 内存 Cookie 存储与 OkHttp 桥接 |
| `HttpFetcherTest` | 8 个测试 |
