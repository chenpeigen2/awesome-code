package org.peter.coroutines.context

import kotlinx.coroutines.*
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

private class RequestId(val id: String) : AbstractCoroutineContextElement(RequestId) {
    companion object Key : CoroutineContext.Key<RequestId>
}

/**
 * Coroutine Context Examples
 *
 * Job hierarchy, naming, context elements, and withContext.
 */
object ContextExamples {

    /**
     * Example 1: Job hierarchy — parent cancellation propagates to children
     */
    fun example1JobHierarchy() = runBlocking {
        println("Example 1: Job Hierarchy")

        val parent = Job()
        val scope = CoroutineScope(parent + Dispatchers.Default)

        val child = scope.launch {
            try {
                delay(5000L)
            } catch (e: CancellationException) {
                println("Child cancelled: ${e.message}")
            }
        }

        delay(100L)
        parent.cancel()
        child.join()
        println("Parent cancelled → child cancelled")
        println("---")
    }

    /**
     * Example 2: SupervisorJob — sibling failures don't cancel each other
     */
    fun example2SupervisorJob() = runBlocking {
        println("Example 2: SupervisorJob")

        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

        val job1 = scope.launch {
            delay(100L)
            throw RuntimeException("Job1 failed")
        }
        val job2 = scope.launch {
            repeat(5) { i ->
                delay(100L)
                println("Job2 still running: $i")
            }
        }

        delay(600L)
        println("Job1 cancelled=${job1.isCancelled}, Job2 active=${job2.isActive}")
        scope.cancel()
        println("---")
    }

    /**
     * Example 3: CoroutineName for debugging
     */
    fun example3CoroutineName() = runBlocking {
        println("Example 3: CoroutineName")

        launch(CoroutineName("fetch-user")) {
            println("Running in: ${coroutineContext[CoroutineName]?.name}")
            launch(CoroutineName("validate")) {
                println("Nested: ${coroutineContext[CoroutineName]?.name}")
            }
        }.join()
        println("---")
    }

    /**
     * Example 4: Custom CoroutineContext element
     */
    fun example4CustomContext() = runBlocking {
        println("Example 4: Custom Context Element")

        suspend fun logWithRequest() {
            val requestId = coroutineContext[RequestId]?.id ?: "unknown"
            println("[$requestId] processing...")
        }

        withContext(RequestId("req-001")) {
            logWithRequest()
            launch {
                logWithRequest()
            }.join()
        }
        println("---")
    }

    /**
     * Example 5: withContext switches dispatcher and restores on exit
     */
    fun example5WithContext() = runBlocking {
        println("Example 5: withContext")

        println("Before: ${Thread.currentThread().name}")

        val result = withContext(Dispatchers.IO) {
            println("Inside IO: ${Thread.currentThread().name}")
            delay(100L)
            "IO result"
        }

        println("After: ${Thread.currentThread().name}, result=$result")
        println("---")
    }

    /**
     * Example 6: Dispatchers.Unconfined vs Default
     */
    fun example6UnconfinedVsDefault() = runBlocking {
        println("Example 6: Unconfined vs Default")

        launch(Dispatchers.Unconfined) {
            println("Unconfined start: ${Thread.currentThread().name}")
            delay(1L)
            println("Unconfined after delay: ${Thread.currentThread().name}")
        }.join()

        launch(Dispatchers.Default) {
            println("Default start: ${Thread.currentThread().name}")
            delay(1L)
            println("Default after delay: ${Thread.currentThread().name}")
        }.join()
        println("---")
    }

    fun runAll() = runBlocking {
        println("=== Running All Context Examples ===")
        println()

        example1JobHierarchy()
        example2SupervisorJob()
        example3CoroutineName()
        example4CustomContext()
        example5WithContext()
        example6UnconfinedVsDefault()

        println("=== All Context Examples Completed ===")
    }
}
