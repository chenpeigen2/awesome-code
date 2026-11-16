# vertx/vertx-grpc

Vert.x 5 的 gRPC 服务端 + 客户端。**不用** grpc-java 生成的 `*ImplBase` 继承骨架，
而是用 Vert.x 的「一个通用服务器 + 按方法名分发」模型。

## 跑起来

```bash
./gradlew :vertx:vertx-grpc:demo    # 起服务端 → 客户端调用 → 打印响应
./gradlew :vertx:vertx-grpc:test    # 6 个测试，真跑 HTTP/2
```

## 和 grpc-java 的用法差异

这是本模块最值得看的地方。

| | grpc-java | Vert.x gRPC |
|---|---|---|
| 服务端 | 继承 `GreeterImplBase`，`ServerBuilder.addService(...)` | `GrpcServer.callHandler(serviceMethod, handler)` |
| 监听端口 | `ServerBuilder.forPort(p).build().start()` | **`GrpcServer` 自己不监听**，要挂到 `HttpServer` 上 |
| 消息类型 | 生成代码直接给强类型 | 靠 `ServiceMethod` 里的 encoder/decoder |
| 方法全名 | 生成代码里拼好 | **自己拼，拼错不报错** |

### `GrpcServer` 不是 `HttpServer`

```java
// 错：GrpcServer 没有 listen 方法
GreeterService.server().listen(0);

// 对：它是 Handler<HttpServerRequest>，得挂到真正的 HttpServer 上
vertx.createHttpServer()
     .requestHandler(GreeterService.server())
     .listen(0);
```

### 客户端的 `ServiceMethod` 泛型是反的

```java
// 服务端：<Req, Resp>
ServiceMethod<HelloRequest, HelloReply> server =
        ServiceMethod.server(name, "SayHello", replyEncoder, requestDecoder);

// 客户端：<Resp, Req> —— 出参在前
ServiceMethod<HelloReply, HelloRequest> client =
        ServiceMethod.client(name, "SayHello", requestEncoder, replyDecoder);
```

不是笔误。`GrpcClient.request` 的签名就是
`<Req, Resp> Future<...> request(Address, ServiceMethod<Resp, Req>)`，
客户端 `ServiceMethod` 用「产出在前」表达它产出 `Resp`、消耗 `Req`。

### 方法全名拼错 = 静默失败

`ServiceMethod.fullMethodName()` 必须和 `.proto` 里的 `service` / `rpc` 名字完全一致。
拼错时服务端不报错，客户端只会收到 `UNIMPLEMENTED`——这类 bug 极难查。
所以 `SAY_HELLO` 是公开常量，测试直接断言全名：

```java
assertEquals("helloworld.Greeter/SayHello", GreeterService.SAY_HELLO.fullMethodName());
```

### 不要在 event loop 里 `join()`

`GreeterDemo.main` 走全异步链（`.compose(...).onSuccess(...)`）。
如果在回调里调 `.join()`，回调跑在 event loop 线程上，Vert.x 的 `BlockedThreadChecker`
会在 2 秒后打印 "Thread blocked" 并抛 `VertxException`。

`GreeterDemo.run(int)` 是同步版本，供测试使用——测试跑在普通 JUnit 线程上，`join()` 没问题。

## 文件

| 文件 | 作用 |
|---|---|
| `src/main/proto/helloworld.proto` | 服务定义，`protoc` 在构建时生成 Java |
| `GreeterService` | 服务端实现 + 编解码器 + 两个 `ServiceMethod` 常量 |
| `GreeterDemo` | 异步 `main` + 同步 `run(int)` |
| `GreeterServiceTest` | 6 个端到端测试 |

## 测试为什么不用 mock

gRPC 的坑几乎全在协议层：方法全名拼错、编解码器接反、消息类型不匹配。
mock 掉传输层就全测不出来。所以测试真的起服务端（端口 `0` 让系统分配，避免端口冲突）、
真的用客户端发 HTTP/2 请求、断言真的响应内容。
