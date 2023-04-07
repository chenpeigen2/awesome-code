package org.peter.grpc.t1;

import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import t1.T1Grpc;
import t1.T1OuterClass;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Server {

    static class TImpl extends T1Grpc.T1ImplBase {
        @Override
        public void sayHello(T1OuterClass.Request request, StreamObserver<T1OuterClass.Reply> responseObserver) {
            T1OuterClass.Reply reply = T1OuterClass.Reply.newBuilder().setMessage("fucking amazing").build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }

    private static final Logger logger = Logger.getLogger(Server.class.getName());

    private io.grpc.Server server;

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new TImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    Server.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        final Server server = new Server();
        server.start();
        server.blockUntilShutdown();
    }
}
