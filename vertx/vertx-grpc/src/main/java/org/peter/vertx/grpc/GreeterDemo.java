package org.peter.vertx.grpc;

import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.net.SocketAddress;
import io.vertx.grpc.client.GrpcClient;
import io.vertx.grpc.server.GrpcServer;

/**
 * 演示入口：起服务端 → 客户端调用 → 打印响应，然后关闭。
 * <p>
 * 运行：{@code ./gradlew :vertx:vertx-grpc:demo}
 * <p>
 * {@code main} 走全异步链，绝不在回调里 {@code join()}：
 * 回调运行在 event loop 线程上，一旦阻塞，Vert.x 的 BlockedThreadChecker 就会报
 * "Thread blocked" 并抛出 VertxException。这是 Vert.x 最容易踩的一脚。
 * 需要同步结果的场景（比如测试）才用 {@link #run(int)}，它跑在普通线程上。
 */
public final class GreeterDemo {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        // GrpcServer 只是 Handler<HttpServerRequest>，得挂到真正的 HttpServer 上
        GrpcServer grpcServer = GreeterService.server();
        vertx.createHttpServer()
                .requestHandler(grpcServer)
                .listen(0)
                .compose(httpServer -> call(vertx, httpServer.actualPort()))
                .onSuccess(reply -> System.out.print(report(reply)))
                .onFailure(error -> System.err.println("demo failed: " + error))
                .eventually(() -> vertx.close());
    }

    /** 对一个已在监听的端口发起一次调用，返回可读报告。调用方须不在 event loop 上。 */
    public String run(int port) {
        Vertx vertx = Vertx.vertx();
        try {
            return report(call(vertx, port).toCompletionStage().toCompletableFuture().join());
        } finally {
            vertx.close().toCompletionStage().toCompletableFuture().join();
        }
    }

    private static Future<HelloReply> call(Vertx vertx, int port) {
        GrpcClient client = GrpcClient.client(vertx);
        SocketAddress address = SocketAddress.inetSocketAddress(port, "localhost");
        return client.request(address, GreeterService.SAY_HELLO_CLIENT)
                .compose(request -> request
                        .send(HelloRequest.newBuilder().setName("Vert.x").build())
                        .compose(response -> response.last()));
    }

    private static String report(HelloReply reply) {
        return "Vert.x gRPC client/server demo\n"
                + "service: " + GreeterService.SAY_HELLO.fullMethodName() + "\n"
                + "request: name=Vert.x\n"
                + "reply: " + reply.getMessage() + "\n";
    }
}
