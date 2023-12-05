package org.peter.vertx.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class HelloWorldVerticle extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(StaticHandler.create());

        System.out.println(Thread.currentThread()); // Thread[#33,vert.x-eventloop-thread-1,5,main]

        router.get("/hello").handler(new Handler<RoutingContext>() {
            @Override
            public void handle(RoutingContext ctx) {
                out(ctx, Thread.currentThread().toString()); // Thread[#33,vert.x-eventloop-thread-1,5,main]
            }
        });

        router.get("/world").blockingHandler(new Handler<RoutingContext>() {
            @Override
            public void handle(RoutingContext ctx) {
                out(ctx, Thread.currentThread().toString()); // Thread[#64,vert.x-worker-thread-15,5,main]
            }
        });

        server.requestHandler(router).listen(8080);
    }

    private void out(RoutingContext ctx, String msg) {
        if (msg == null) {
            ctx.fail(400);
            return;
        }
        ctx.response().putHeader("Content-Type", "application/json; charset=utf-8")
                .end(msg);
    }
}