package org.peter.vertx.grpc;

import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.vertx.grpc.common.GrpcMessage;
import io.vertx.grpc.common.GrpcMessageDecoder;
import io.vertx.grpc.common.GrpcMessageEncoder;
import io.vertx.grpc.common.ServiceMethod;
import io.vertx.grpc.common.ServiceName;
import io.vertx.grpc.server.GrpcServer;
import io.vertx.grpc.server.GrpcServerResponse;

/**
 * {@code Greeter} 服务的 Vert.x 实现。
 * <p>
 * 与 {@code grpc-java} 的 {@code *ImplBase} 不同，Vert.x 走「一个通用服务器 + 按方法名分发」，
 * 不需要为每个 proto 服务生成 Java 继承骨架。代价是<b>方法全名要自己拼对</b>：
 * {@code ServiceMethod.fullMethodName()} 必须和 .proto 里的 {@code service/rpc} 名字一致，
 * 否则服务端不报错、客户端只收到 UNIMPLEMENTED。
 * <p>
 * 这里把 {@link #SAY_HELLO} 暴露成常量，测试就能直接断言全名，而不是靠人肉核对。
 */
public final class GreeterService {

    public static final ServiceName SERVICE_NAME = ServiceName.create("helloworld", "Greeter");

    /** 编解码器：protobuf 消息 ↔ gRPC 字节帧。接反了会静默失败，所以单独暴露供测试。 */
    public static final GrpcMessageEncoder<HelloRequest> REQUEST_ENCODER =
            GrpcMessageEncoder.encoder();

    public static final GrpcMessageDecoder<HelloRequest> REQUEST_DECODER =
            GrpcMessageDecoder.decoder(HelloRequest.getDefaultInstance());

    public static final GrpcMessageEncoder<HelloReply> REPLY_ENCODER =
            GrpcMessageEncoder.encoder();

    public static final GrpcMessageDecoder<HelloReply> REPLY_DECODER =
            GrpcMessageDecoder.decoder(HelloReply.getDefaultInstance());

    /** 服务端视角：收到 HelloRequest，回 HelloReply。 */
    public static final ServiceMethod<HelloRequest, HelloReply> SAY_HELLO =
            ServiceMethod.server(SERVICE_NAME, "SayHello", REPLY_ENCODER, REQUEST_DECODER);

    /**
     * 客户端视角：同一组编解码器，但泛型参数顺序是反的（{@code <Resp, Req>}）。
     * <p>
     * 这不是笔误：{@code GrpcClient.request} 的签名就是
     * {@code <Req, Resp> Future<...> request(Address, ServiceMethod<Resp, Req>)}，
     * 客户端 ServiceMethod 用「出参在前」表示它产出 Resp、消耗 Req。
     */
    public static final ServiceMethod<HelloReply, HelloRequest> SAY_HELLO_CLIENT =
            ServiceMethod.client(SERVICE_NAME, "SayHello", REQUEST_ENCODER, REPLY_DECODER);

    private GreeterService() {
    }

    /**
     * 构造 gRPC 服务器（尚未 listen）。
     * <p>
     * {@code GrpcServer} 不是 {@code HttpServer}，它只是 {@code Handler<HttpServerRequest>}；
     * 必须挂到一个真正的 {@code HttpServer} 上才能监听端口。这是 Vert.x gRPC 与
     * grpc-java 的 {@code ServerBuilder} 最大的用法差异。
     */
    public static GrpcServer server() {
        return GrpcServer.server(null)
                .callHandler(SAY_HELLO, request -> {
                    // last() 拿到的已经是解码后的 HelloRequest，不需要自己再解一遍：
                    // ServiceMethod 里挂的 decoder 已经替我们做完了
                    GrpcServerResponse<HelloRequest, HelloReply> response = request.response();
                    request.last().onSuccess(helloRequest -> response.end(
                            HelloReply.newBuilder()
                                    .setMessage("Hello, " + helloRequest.getName() + "!")
                                    .build()));
                });
    }

    public static HelloRequest decodeRequest(GrpcMessage message) {
        return REQUEST_DECODER.decode(message);
    }

    public static HelloReply decodeReply(GrpcMessage message) {
        return REPLY_DECODER.decode(message);
    }
}
