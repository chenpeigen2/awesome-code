package org.peter.vertx.coroutines

import io.vertx.core.Vertx

fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(CounterVerticle())
}

