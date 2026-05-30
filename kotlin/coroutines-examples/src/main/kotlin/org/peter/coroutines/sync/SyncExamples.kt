package org.peter.coroutines.sync

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

/**
 * Coroutine Synchronization Examples
 *
 * Mutex, Semaphore, and safe shared-state patterns.
 */
object SyncExamples {

    /**
     * Example 1: Mutex protects shared mutable state
     */
    fun example1MutexCounter() = runBlocking {
        println("Example 1: Mutex Counter")

        var counter = 0
        val mutex = Mutex()

        val jobs = List(100) {
            launch {
                repeat(100) {
                    mutex.withLock {
                        counter++
                    }
                }
            }
        }
        jobs.joinAll()

        println("Final count: $counter (expected 10000)")
        println("---")
    }

    /**
     * Example 2: Unsafe counter without Mutex (race condition demo)
     */
    fun example2RaceCondition() = runBlocking {
        println("Example 2: Race Condition Without Mutex")

        var counter = 0

        val jobs = List(100) {
            launch {
                repeat(100) {
                    counter++ // Not atomic across threads
                }
            }
        }
        jobs.joinAll()

        println("Final count: $counter (expected 10000, often less due to race)")
        println("---")
    }

    /**
     * Example 3: Semaphore limits concurrent access
     */
    fun example3SemaphoreLimit() = runBlocking {
        println("Example 3: Semaphore Concurrency Limit")

        val semaphore = Semaphore(3)
        val active = AtomicInteger(0)
        var maxConcurrent = 0

        val jobs = List(10) { id ->
            launch {
                semaphore.withPermit {
                    val current = active.incrementAndGet()
                    maxConcurrent = maxOf(maxConcurrent, current)
                    println("Task $id started (active=$current)")
                    delay(200L)
                    active.decrementAndGet()
                    println("Task $id finished")
                }
            }
        }
        jobs.joinAll()

        println("Max concurrent tasks: $maxConcurrent (limit was 3)")
        println("---")
    }

    /**
     * Example 4: Mutex vs synchronized block
     */
    fun example4MutexVsSynchronized() = runBlocking {
        println("Example 4: Mutex vs synchronized")

        val mutexTime = measureTimeMillis {
            val mutex = Mutex()
            var sum = 0
            coroutineScope {
                val jobs = List(1000) { i ->
                    launch(Dispatchers.Default) {
                        mutex.withLock { sum += i }
                    }
                }
                jobs.joinAll()
            }
        }

        val syncTime = measureTimeMillis {
            var sum = 0
            val lock = Any()
            coroutineScope {
                val jobs = List(1000) { i ->
                    launch(Dispatchers.Default) {
                        synchronized(lock) { sum += i }
                    }
                }
                jobs.joinAll()
            }
        }

        println("Mutex approach: $mutexTime ms")
        println("synchronized approach: $syncTime ms")
        println("Prefer Mutex in coroutines — it suspends instead of blocking threads")
        println("---")
    }

    /**
     * Example 5: Fine-grained locking with per-key Mutex
     */
    fun example5FineGrainedLock() = runBlocking {
        println("Example 5: Fine-grained Locking")

        val balances = mutableMapOf("alice" to 1000, "bob" to 1000)
        val locks = mutableMapOf<String, Mutex>()

        suspend fun transfer(from: String, to: String, amount: Int) {
            val fromLock = locks.getOrPut(from) { Mutex() }
            val toLock = locks.getOrPut(to) { Mutex() }

            fromLock.withLock {
                toLock.withLock {
                    balances[from] = balances.getValue(from) - amount
                    balances[to] = balances.getValue(to) + amount
                }
            }
        }

        coroutineScope {
            repeat(50) {
                launch { transfer("alice", "bob", 10) }
                launch { transfer("bob", "alice", 10) }
            }
        }

        println("Alice: ${balances["alice"]}, Bob: ${balances["bob"]} (both should be 1000)")
        println("---")
    }

    /**
     * Example 6: Semaphore as connection pool
     */
    fun example6ConnectionPool() = runBlocking {
        println("Example 6: Connection Pool with Semaphore")

        val poolSize = 2
        val pool = Semaphore(poolSize)

        suspend fun useConnection(id: Int): String = pool.withPermit {
            val connId = id % poolSize
            delay(300L)
            "result from connection-$connId"
        }

        val results = coroutineScope {
            List(6) { id ->
                async { useConnection(id) }
            }.awaitAll()
        }

        println("Processed ${results.size} requests with pool size $poolSize")
        results.forEachIndexed { i, r -> println("  Request $i: $r") }
        println("---")
    }

    fun runAll() = runBlocking {
        println("=== Running All Sync Examples ===")
        println()

        example1MutexCounter()
        example2RaceCondition()
        example3SemaphoreLimit()
        example4MutexVsSynchronized()
        example5FineGrainedLock()
        example6ConnectionPool()

        println("=== All Sync Examples Completed ===")
    }
}
