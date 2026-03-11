package org.peter.ksc.api

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.peter.ksc.service.BlockingWorkService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.time.Duration.Companion.milliseconds

@RestController
class HelloController(
    private val workService: BlockingWorkService,
) {
    @GetMapping("/hello")
    fun hello(): Map<String, String> = mapOf("message" to "hello")

    @GetMapping("/suspend/hello")
    suspend fun suspendHello(): Map<String, String> {
        val r = workService.nonBlockingCall("hello", 50.milliseconds)
        return mapOf("message" to "hello", "detail" to r)
    }

    @GetMapping("/fanout")
    suspend fun fanout(): Map<String, Any> = coroutineScope {
        val a = async { workService.blockingCall("a", 120.milliseconds) }
        val b = async { workService.nonBlockingCall("b", 80.milliseconds) }
        mapOf(
            "a" to a.await(),
            "b" to b.await(),
        )
    }
}

