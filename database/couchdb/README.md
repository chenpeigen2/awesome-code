# database/couchdb

CouchDB 的 HTTP API 客户端示例：认证、建库、插文档、Mango 查询、建索引。

## 跑起来

```bash
# 需要真实 CouchDB
COUCHDB_URL=http://localhost:5984 COUCHDB_USER=admin COUCHDB_PASSWORD=pass \
  ./gradlew :database:couchdb:demo

# 测试不需要 CouchDB（用进程内假服务器）
./gradlew :database:couchdb:test    # 10 个测试
```

## 这个模块以前的问题

`DbUtils` 是典型「能跑就行」的写法：

| 问题 | 后果 |
|---|---|
| **9 处 `printStackTrace()`** | 错误打印后吞掉，调用方拿不到任何信号 |
| **所有方法返回 `void`** | 建库成功和失败看起来完全一样 |
| **`static Cookie` + `static Map`** | 无法多实例、无法并行测试、状态跨用例泄漏 |
| **硬编码 `localhost:5984` + `admin/123456`** | 换环境要改源码 |
| **凭据错误时 `headers().values("Set-Cookie").get(0)`** | 抛 `IndexOutOfBoundsException`，完全误导人 |

## 现在的结构

`CouchDbClient`：

- **错误统一抛 `CouchDbException`**，带 HTTP 状态码和响应体；
  网络层失败（连不上/超时）状态码是 `-1` 且保留原始 `IOException` 作 cause ——
  这两类失败需要完全不同的处理，混在一起就没法区分。
- **方法返回解析后的 `JsonObject`**，调用方自己判断。
- **认证状态是实例字段**，每个 client 一份 cookie，可并行测试。
- **连接参数从环境变量读**（`COUCHDB_URL` / `COUCHDB_USER` / `COUCHDB_PASSWORD`），
  默认值仍指向 localhost，但可覆盖。
- **只用 JDK 自带的 `java.net.http.HttpClient`** —— 原来为了一次 HTTP 调用
  引入了 okhttp + lombok + guava 三个依赖，全都不必要。
- **建库幂等**：CouchDB 对已存在的库返回 `412 Precondition Failed`，
  这里当作成功处理并标记 `existed: true`。

## 测试怎么做到不需要 CouchDB

用 JDK 自带的 `com.sun.net.httpserver.HttpServer` 起一个假 CouchDB，
记录每个请求的 method / path / body / headers，供断言使用。

一个细节：路由键是 **`"METHOD path"`** 而不是 `path`。同一个路径上
`PUT /demo`（建库）和 `GET /demo/doc1`（取文档）结果不同，只按 path 路由会互相覆盖 ——
这是写这个测试时真的踩到的，第一版就因为路由被覆盖而失败。

覆盖的行为：

- 认证后 cookie 被缓存，后续请求带上 `Cookie` 头；
- 凭据错误抛 `CouchDbException`（而不是 `IndexOutOfBoundsException`）；
- HTTP 5xx 不被静默吞掉，异常里带状态码和响应体；
- 网络失败 → 状态码 `-1` + `IOException` cause；
- 建库返回解析后的 JSON；412 视作成功；
- baseUrl 末尾斜杠不会拼出 `//`；
- demo 端到端跑通（三个请求分别路由）。

## 文件

| 文件 | 作用 |
|---|---|
| `org/peter/couchdb/CouchDbClient.java` | HTTP 客户端 |
| `org/peter/couchdb/CouchDbDemo.java` | 建库 → 插文档 → 取文档 |
| `org/peter/couchdb/CouchDbClientTest.java` | 10 个测试（含假服务器） |
