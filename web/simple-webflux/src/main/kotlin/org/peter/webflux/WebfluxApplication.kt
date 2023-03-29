package org.peter.webflux

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebfluxApplication

fun main(args: Array<String>) {
    println(KotlinVersion.CURRENT)
    println("main " + Thread.currentThread().id)
    runApplication<WebfluxApplication>(*args)
}
