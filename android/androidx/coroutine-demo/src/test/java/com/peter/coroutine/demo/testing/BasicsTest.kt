package com.peter.coroutine.demo.testing

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.*
import org.junit.Test

/**
 * 协程基础测试
 *
 * 本测试类展示如何测试协程的基本功能：
 * - launch 和 async 的测试
 * - withContext 调度器切换测试
 * - 协程取消测试
 *
 * ## 测试协程的关键点
 * 1. 使用 runBlocking 在测试中创建协程作用域
 * 2. 使用 TestDispatcher 控制协程执行（见 TestDispatcherTest）
 * 3. 测试挂起函数的行为
 */
class BasicsTest {

    // ==================== launch 和 async 测试 ====================

    /**
     * 测试 launch 协程构建器
     *
     * launch 启动一个新协程并返回 Job。
     * 可以通过 Job.join() 等待协程完成。
     */
    @Test
    fun testLaunch() = runBlocking {
        var executed = false

        val job = launch {
            executed = true
        }

        // 等待协程完成
        job.join()

        assertTrue("launch 协程应该已执行", executed)
    }

    /**
     * 测试 async 协程构建器
     *
     * async 启动一个新协程并返回 Deferred<T>。
     * 可以通过 Deferred.await() 获取结果。
     */
    @Test
    fun testAsync() = runBlocking {
        val deferred = async {
            delay(100)
            "Hello, Async!"
        }

        val result = deferred.await()

        assertEquals("async 应该返回正确的结果", "Hello, Async!", result)
    }

    /**
     * 测试并行执行
     *
     * 多个 async 可以并行执行，提高效率。
     */
    @Test
    fun testParallelExecution() = runBlocking {
        val startTime = System.currentTimeMillis()

        val deferred1 = async {
            delay(100)
            "Result1"
        }

        val deferred2 = async {
            delay(100)
            "Result2"
        }

        val result1 = deferred1.await()
        val result2 = deferred2.await()

        val totalTime = System.currentTimeMillis() - startTime

        assertEquals("Result1", result1)
        assertEquals("Result2", result2)
        // 并行执行应该在 200ms 以内完成
        assertTrue("并行执行应该在 200ms 以内完成", totalTime < 200)
    }

    // ==================== withContext 调度器切换测试 ====================

    /**
     * 测试 withContext 切换调度器
     *
     * withContext 用于切换协程的执行上下文。
     */
    @Test
    fun testWithContext() = runBlocking {
        val mainThreadId = Thread.currentThread().id

        // 切换到 IO 调度器
        val ioThreadId = withContext(Dispatchers.IO) {
            Thread.currentThread().id
        }

        // 切换回主线程后
        val backToMainThreadId = Thread.currentThread().id

        // IO 线程应该与主线程不同
        assertNotEquals("IO 线程应该与主线程不同", mainThreadId, ioThreadId)
        // 切换回来后应该是主线程
        assertEquals("切换回来后应该是主线程", mainThreadId, backToMainThreadId)
    }

    /**
     * 测试 withContext 保留返回值
     */
    @Test
    fun testWithContextReturnValue() = runBlocking {
        val result = withContext(Dispatchers.Default) {
            // 模拟计算
            1 + 2 + 3
        }

        assertEquals("withContext 应该返回计算结果", 6, result)
    }

    // ==================== 协程取消测试 ====================

    /**
     * 测试协程取消
     *
     * 协程可以通过 Job.cancel() 取消。
     * 取消是协作的，需要在协程内检查取消状态。
     */
    @Test
    fun testCancellation() = runBlocking {
        var cancelled = false

        val job = launch {
            try {
                repeat(1000) {
                    // delay 会检查取消状态
                    delay(100)
                }
            } catch (e: CancellationException) {
                cancelled = true
                throw e
            }
        }

        delay(150) // 让协程执行一会儿
        job.cancel() // 取消协程
        job.join() // 等待协程完成

        assertTrue("协程应该被取消", cancelled)
    }

    /**
     * 测试 cancelAndJoin
     *
     * cancelAndJoin 同时取消并等待协程完成。
     */
    @Test
    fun testCancelAndJoin() = runBlocking {
        var executionCount = 0

        val job = launch {
            repeat(100) { i ->
                executionCount = i + 1
                delay(50)
            }
        }

        delay(120) // 让协程执行 2-3 次
        job.cancelAndJoin() // 取消并等待

        // 协程应该被取消，执行次数应该少于 100
        assertTrue("协程应该被取消", executionCount < 10)
    }

    /**
     * 测试协程取消
     *
     * 取消的协程不会再执行后续代码
     */
    @Test
    fun testCancellationBehavior() = runBlocking {
        var executed = false

        val job = launch {
            delay(1000)
            executed = true
        }

        delay(50)
        job.cancel()
        job.join()

        // 取消后不应该执行后续代码
        assertFalse("取消后不应该执行", executed)
    }

    /**
     * 测试父协程取消时子协程也被取消
     */
    @Test
    fun testParentCancellationCancelsChildren() = runBlocking {
        var childCancelled = false

        val parentJob = launch {
            launch {
                try {
                    delay(10000)
                } catch (e: CancellationException) {
                    childCancelled = true
                    throw e
                }
            }
            delay(10000)
        }

        delay(50)
        parentJob.cancelAndJoin()

        assertTrue("子协程应该被取消", childCancelled)
    }

    // ==================== 协程异常测试 ====================

    /**
     * 测试协程中的异常
     *
     * launch 中的未捕获异常会传播到父协程。
     */
    @Test(expected = RuntimeException::class)
    fun testExceptionInLaunch(): Unit = runBlocking {
        val job = launch {
            delay(10)
            throw RuntimeException("Test exception")
        }

        job.join()
    }

    /**
     * 测试 async 中的异常
     *
     * async 中的异常会在 await() 时抛出。
     */
    @Test(expected = RuntimeException::class)
    fun testExceptionInAsync(): Unit = runBlocking {
        val deferred = async {
            delay(10)
            throw RuntimeException("Test exception")
        }

        // 异常会在 await 时抛出
        deferred.await()
    }
}
