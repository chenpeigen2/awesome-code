package org.peter.vertx.coroutines

import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.CoroutineVerticle
import java.util.concurrent.atomic.AtomicLong

class CounterVerticle : CoroutineVerticle() {

    private val hits = AtomicLong(0)
    private val errors = AtomicLong(0)

    override suspend fun start() {
        val router = Router.router(vertx)

        router.route().handler { ctx ->
            hits.incrementAndGet()
            ctx.next()
        }

        router.get("/health").handler { ctx ->
            respondJson(ctx, 200, """{"status":"UP"}""")
        }

        router.get("/metrics").handler { ctx ->
            val body =
                """{"hits":${hits.get()},"errors":${errors.get()}}"""
            respondJson(ctx, 200, body)
        }

        router.post("/metrics/hits/increment").handler { ctx ->
            val value = hits.incrementAndGet()
            respondJson(ctx, 200, """{"hits":$value}""")
        }

        router.post("/metrics/errors/increment").handler { ctx ->
            val value = errors.incrementAndGet()
            respondJson(ctx, 200, """{"errors":$value}""")
        }

        router.route().last().handler { ctx ->
            respondJson(ctx, 404, """{"error":"Not Found"}""")
        }

        val server: HttpServer = vertx
            .createHttpServer()
            .requestHandler(router)

        server.listen(8080)
    }

    private fun respondJson(ctx: RoutingContext, statusCode: Int, body: String) {
        ctx.response()
            .setStatusCode(statusCode)
            .putHeader("Content-Type", "application/json")
            .end(body)
    }
}

