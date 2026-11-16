package org.peter.vertx.grpc;

import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.vertx.core.Future;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.Vertx;
import io.vertx.grpc.client.GrpcClient;
import io.vertx.grpc.common.GrpcMessageDecoder;
import io.vertx.grpc.common.GrpcMessageEncoder;
import io.vertx.grpc.common.ServiceName;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 端到端测试：真的起一个 Vert.x gRPC 服务端，用真的客户端调用，断言响应内容。
 * <p>
 * 不用 mock 的理由：gRPC 的坑几乎都在协议层——方法全名拼错、编解码器接反、
 * 消息类型不匹配，这些 mock 全都发现不了。只有真跑一遍 HTTP/2 才有效。
 * <p>
 * 端口用 0 让系统分配，避免并行测试/本机已占用端口导致的假失败。
 */
class GreeterServiceTest {

    private Vertx vertx;
    private int port;

    @BeforeEach
    void startServer() {
        vertx = Vertx.vertx();
        // GrpcServer 是 Handler<HttpServerRequest>，必须挂到 HttpServer 上才监听得到端口
        port = vertx.createHttpServer()
                .requestHandler(GreeterService.server())
                .listen(0)
                .toCompletionStage().toCompletableFuture()
                .join()
                .actualPort();
    }

    @AfterEach
    void stopServer() {
        if (vertx != null) {
            vertx.close().toCompletionStage().toCompletableFuture().join();
        }
    }

    private Future<HelloReply> call(String name) {
        GrpcClient client = GrpcClient.client(vertx);
        SocketAddress address = SocketAddress.inetSocketAddress(port, "localhost");
        // response.last() 已经是解码后的 HelloReply：ServiceMethod 里的 decoder 已经生效，
        // 再手动 decodeReply 就是解第二遍（会直接抛异常）
        return client.request(address, GreeterService.SAY_HELLO_CLIENT)
                .compose(request -> request
                        .send(HelloRequest.newBuilder().setName(name).build())
                        .compose(response -> response.last()));
    }

    @Test
    void sayHelloReturnsGreeting() {
        HelloReply reply = call("张三").toCompletionStage().toCompletableFuture()
                .orTimeout(10, TimeUnit.SECONDS).join();

        assertEquals("Hello, 张三!", reply.getMessage());
    }

    @Test
    void serverEchoesDifferentNames() {
        // 同一个服务连续处理多个请求，验证没有把状态错误地留在服务端
        assertEquals("Hello, A!", call("A").toCompletionStage().toCompletableFuture().join().getMessage());
        assertEquals("Hello, B!", call("B").toCompletionStage().toCompletableFuture().join().getMessage());
    }

    @Test
    void emptyNameStillProducesWellFormedReply() {
        HelloReply reply = call("").toCompletionStage().toCompletableFuture()
                .orTimeout(10, TimeUnit.SECONDS).join();

        assertEquals("Hello, !", reply.getMessage());
    }

    @Test
    void serviceMethodNameMatchesProtoDefinition() {
        // 方法全名必须与 .proto 里 service Greeter / rpc SayHello 完全一致，
        // 拼错时服务端不报错、客户端只会得到 UNIMPLEMENTED —— 最难查的一类 bug
        assertEquals("helloworld.Greeter/SayHello", GreeterService.SAY_HELLO.fullMethodName());
        // ServiceName 没实现 toString()，要断言 fullyQualifiedName()
        assertEquals("helloworld.Greeter", GreeterService.SAY_HELLO.serviceName().fullyQualifiedName());
        assertEquals("helloworld", GreeterService.SAY_HELLO.serviceName().packageName());
        assertEquals("Greeter", GreeterService.SAY_HELLO.serviceName().name());
    }

    @Test
    void protoCodecRoundTripsMessages() {
        // 编解码器接反（encoder/decoder 互换）是另一个静默失败点，这里单独验证
        GrpcMessageEncoder<HelloRequest> encoder = GreeterService.REQUEST_ENCODER;
        GrpcMessageDecoder<HelloRequest> decoder = GreeterService.REQUEST_DECODER;

        io.vertx.grpc.common.GrpcMessage encoded = encoder.encode(
                HelloRequest.newBuilder().setName("往返").build(),
                io.vertx.grpc.common.WireFormat.PROTOBUF);
        HelloRequest decoded = decoder.decode(encoded);

        assertEquals("往返", decoded.getName());
    }

    @Test
    void demoRunsAgainstRealServer() {
        String report = new GreeterDemo().run(port);

        assertTrue(report.contains("reply: Hello, Vert.x!"), report);
    }
}
