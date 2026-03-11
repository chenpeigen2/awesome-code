package org.peter.ksc.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import kotlin.time.Duration

@Service
class BlockingWorkService {

    suspend fun blockingCall(name: String, sleep: Duration): String =
        withContext(Dispatchers.IO) {
            Thread.sleep(sleep.inWholeMilliseconds)
            "blocking:$name:${sleep.inWholeMilliseconds}ms"
        }

    suspend fun nonBlockingCall(name: String, delay: Duration): String {
        delay(delay)
        return "nonBlocking:$name:${delay.inWholeMilliseconds}ms"
    }
}

