package org.peter.coroutines.advanced

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * Advanced Coroutine Examples
 *
 * CoroutineStart modes, exception handlers, yield, and non-cancellable blocks.
 */
object AdvancedExamples {

    /**
     * Example 1: CoroutineStart.LAZY — deferred until start/await
     */
    fun example1LazyStart() = runBlocking {
        println("Example 1: CoroutineStart.LAZY")

        val deferred = async(start = CoroutineStart.LAZY) {
            delay(500L)
            42
        }
        println("Created but not started, isActive=${deferred.isActive}")
        delay(100L)
        println("Calling await() starts execution...")
        println("Result: ${deferred.await()}")
        println("---")
    }

    /**
     * Example 2: CoroutineStart.ATOMIC — body runs even if cancelled immediately
     */
    fun example2AtomicStart() = runBlocking {
        println("Example 2: CoroutineStart.ATOMIC")

        val job = launch(start = CoroutineStart.ATOMIC) {
            println("Entered coroutine body (isActive=$isActive)")
            try {
                delay(1000L)
            } catch (e: CancellationException) {
                println("Cancelled during delay")
            } finally {
                println("Finally block always runs with ATOMIC")
            }
        }
        job.cancelAndJoin()
        println("---")
    }

    /**
     * Example 3: CoroutineStart.UNDISPATCHED — runs on current thread until first suspend
     */
    fun example3UndispatchedStart() = runBlocking {
        println("Example 3: CoroutineStart.UNDISPATCHED")

        println("Main thread: ${Thread.currentThread().name}")
        val job = launch(Dispatchers.Default, start = CoroutineStart.UNDISPATCHED) {
            println("Before suspend: ${Thread.currentThread().name}")
            delay(1L)
            println("After suspend: ${Thread.currentThread().name}")
        }
        job.join()
        println("---")
    }

    /**
     * Example 4: CoroutineExceptionHandler — uncaught exception in launch
     */
    fun example4ExceptionHandler() = runBlocking {
        println("Example 4: CoroutineExceptionHandler")

        val handler = CoroutineExceptionHandler { _, exception ->
            println("Handler caught: ${exception.message}")
        }

        val scope = CoroutineScope(SupervisorJob() + handler)

        scope.launch {
            delay(100L)
            throw RuntimeException("Unhandled in launch")
        }

        delay(500L)
        scope.cancel()
        println("---")
    }

    /**
     * Example 5: yield() cooperatively gives up the thread
     */
    fun example5Yield() = runBlocking {
        println("Example 5: yield()")

        val job1 = launch {
            repeat(5) { i ->
                println("Job1: $i")
                yield()
            }
        }
        val job2 = launch {
            repeat(5) { i ->
                println("Job2: $i")
                yield()
            }
        }
        job1.join()
        job2.join()
        println("---")
    }

    /**
     * Example 6: ensureActive() — explicit cancellation check
     */
    fun example6EnsureActive() = runBlocking {
        println("Example 6: ensureActive()")

        val job = launch {
            repeat(10) { i ->
                ensureActive()
                // Simulate CPU work without suspend points
                var sum = 0
                for (j in 1..100_000) sum += j
                println("Iteration $i, sum=$sum")
            }
        }
        delay(50L)
        job.cancelAndJoin()
        println("Cancelled after ensureActive check")
        println("---")
    }

    /**
     * Example 7: withContext(NonCancellable) for cleanup during cancellation
     */
    fun example7NonCancellableCleanup() = runBlocking {
        println("Example 7: NonCancellable Cleanup")

        val job = launch {
            try {
                repeat(5) { i ->
                    delay(200L)
                    println("Working $i")
                }
            } finally {
                withContext(NonCancellable) {
                    delay(300L)
                    println("Critical cleanup completed (NonCancellable)")
                }
            }
        }
        delay(450L)
        job.cancelAndJoin()
        println("---")
    }

    /**
     * Example 8: compare sequential LAZY async initialization
     */
    fun example8LazyAsyncInit() = runBlocking {
        println("Example 8: Lazy Async Initialization")

        class ConfigLoader {
            private val config = async(start = CoroutineStart.LAZY) {
                delay(800L)
                mapOf("host" to "localhost", "port" to "8080")
            }

            suspend fun getConfig() = config.await()
        }

        val loader = ConfigLoader()
        val time = measureTimeMillis {
            coroutineScope {
                val jobs = List(5) {
                    launch { println("Config: ${loader.getConfig()}") }
                }
                jobs.joinAll()
            }
        }
        println("5 concurrent callers, single init, total: $time ms")
        println("---")
    }

    fun runAll() = runBlocking {
        println("=== Running All Advanced Examples ===")
        println()

        example1LazyStart()
        example2AtomicStart()
        example3UndispatchedStart()
        example4ExceptionHandler()
        example5Yield()
        example6EnsureActive()
        example7NonCancellableCleanup()
        example8LazyAsyncInit()

        println("=== All Advanced Examples Completed ===")
    }
}
