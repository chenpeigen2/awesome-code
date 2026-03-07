package com.peter.coroutine.demo.testing

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

/**
 * TestDispatcher 测试
 *
 * 本测试类展示如何使用测试调度器来控制协程执行：
 * - StandardTestDispatcher: 标准测试调度器，需要手动推进时间
 * - UnconfinedTestDispatcher: 急切执行调度器，立即执行协程
 *
 * ## 为什么需要 TestDispatcher？
 * 1. 使测试可重复和可预测
 * 2. 控制虚拟时间，跳过 delay
 * 3. 避免在测试中使用真实的线程池
 *
 * ## TestDispatcher 类型
 * - StandardTestDispatcher: 协程不会立即执行，需要调用 scheduler.advanceTimeBy() 或 runCurrent()
 * - UnconfinedTestDispatcher: 协程会立即执行（类似 Dispatchers.Unconfined）
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatcherTest {

    // ==================== StandardTestDispatcher 测试 ====================

    /**
     * 测试 StandardTestDispatcher 基本使用
     *
     * StandardTestDispatcher 创建的协程不会立即执行，
     * 需要显式调用 advanceUntilIdle() 或在 runTest 中使用。
     */
    @Test
    fun testStandardTestDispatcher() = runTest {
        var executed = false

        launch {
            executed = true
        }.join()

        assertTrue("协程应该已执行", executed)
    }

    /**
     * 测试 StandardTestDispatcher 的时间控制
     *
     * 可以使用 testScheduler 控制虚拟时间。
     */
    @Test
    fun testStandardTestDispatcherTimeControl() = runTest {
        val results = mutableListOf<String>()
        val job = launch {
            results.add("Start")
            delay(1000)
            results.add("After 1s")
            delay(1000)
            results.add("After 2s")
        }

        job.join()

        assertEquals(listOf("Start", "After 1s", "After 2s"), results)
    }

    // ==================== UnconfinedTestDispatcher 测试 ====================

    /**
     * 测试 UnconfinedTestDispatcher
     *
     * UnconfinedTestDispatcher 会立即执行协程，
     * 类似于 Dispatchers.Unconfined。
     */
    @Test
    fun testUnconfinedTestDispatcher() = runTest {
        val results = mutableListOf<String>()

        launch(UnconfinedTestDispatcher(testScheduler)) {
            results.add("Inside coroutine")
        }

        // UnconfinedTestDispatcher 会立即执行
        assertEquals("Inside coroutine", results.last())
    }

    /**
     * UnconfinedTestDispatcher 与 delay
     *
     * 即使使用 UnconfinedTestDispatcher，delay 仍会挂起协程。
     */
    @Test
    fun testUnconfinedTestDispatcherWithDelay() = runTest {
        val results = mutableListOf<String>()

        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            results.add("Before delay")
            delay(100)
            results.add("After delay")
        }

        job.join()

        assertEquals(listOf("Before delay", "After delay"), results)
    }

    // ==================== runTest 测试 ====================

    /**
     * 测试 runTest 基本用法
     *
     * runTest 是测试协程的推荐方式。
     * 它会自动创建 TestScope 并等待所有协程完成。
     */
    @Test
    fun testRunTest() = runTest {
        var result = 0

        launch {
            delay(100)
            result = 42
        }.join()

        assertEquals(42, result)
    }

    /**
     * 测试 runTest 中的多个协程
     */
    @Test
    fun testRunTestMultipleCoroutines() = runTest {
        val results = mutableListOf<Int>()

        launch {
            delay(100)
            results.add(1)
        }.join()

        launch {
            delay(50)
            results.add(2)
        }.join()

        launch {
            results.add(3)
        }.join()

        assertEquals(listOf(1, 2, 3), results)
    }

    /**
     * 测试 TestScope
     *
     * TestScope 提供了对测试调度器的访问。
     */
    @Test
    fun testTestScope() = runTest {
        val currentTime = testScheduler.currentTime

        launch {
            delay(1000)
        }.join()

        // 虚拟时间应该前进了 1000ms
        assertTrue("虚拟时间应该前进了 1000ms", testScheduler.currentTime >= currentTime + 1000)
    }

    // ==================== setMain 测试 ====================

    /**
     * 测试替换 Main dispatcher
     *
     * 在 Android 测试中，经常需要替换 Dispatchers.Main。
     *
     * 注意：这个测试演示概念，实际使用需要配合 AndroidX Test。
     */
    @Test
    fun testSetMainDispatcher() = runTest {
        // 演示概念：在测试中使用协程
        var result = 0

        launch {
            result = 42
        }.join()

        assertEquals("协程应该执行完成", 42, result)
    }

    // ==================== 时间控制测试 ====================

    /**
     * 测试虚拟时间
     *
     * 使用测试调度器时，delay 不会真正等待，
     * 而是使用虚拟时间。
     */
    @Test
    fun testVirtualTime() = runTest {
        val realStartTime = System.currentTimeMillis()
        val virtualStartTime = testScheduler.currentTime

        launch {
            delay(10000) // 虚拟延迟 10 秒
        }.join()

        val realElapsed = System.currentTimeMillis() - realStartTime
        val virtualElapsed = testScheduler.currentTime - virtualStartTime

        // 虚拟时间应该前进了 10000ms
        assertEquals(10000, virtualElapsed)
        // 实际时间应该非常短（毫秒级别）
        assertTrue("实际耗时应该很短", realElapsed < 1000)
    }

    /**
     * 测试时间跳跃
     *
     * testScheduler 可以手动推进时间。
     */
    @Test
    fun testTimeAdvance() = runTest {
        val results = mutableListOf<String>()
        val testDispatcher = StandardTestDispatcher(testScheduler)

        launch(testDispatcher) {
            results.add("Start")
            delay(100)
            results.add("100ms")
            delay(100)
            results.add("200ms")
        }

        // 手动推进时间
        testScheduler.advanceTimeBy(50)
        testScheduler.runCurrent()
        assertEquals(listOf("Start"), results)

        testScheduler.advanceTimeBy(50)
        testScheduler.runCurrent()
        assertEquals(listOf("Start", "100ms"), results)

        testScheduler.advanceTimeBy(100)
        testScheduler.runCurrent()
        assertEquals(listOf("Start", "100ms", "200ms"), results)
    }

    /**
     * 测试 runCurrent
     *
     * runCurrent 执行所有已准备好的协程。
     */
    @Test
    fun testRunCurrent() = runTest {
        val results = mutableListOf<String>()
        val testDispatcher = StandardTestDispatcher(testScheduler)

        launch(testDispatcher) {
            results.add("Executed")
        }

        // 此时协程还未执行
        assertTrue(results.isEmpty())

        // 运行当前待执行的任务
        testScheduler.runCurrent()

        assertEquals(listOf("Executed"), results)
    }

    // ==================== 协程间通信测试 ====================

    /**
     * 测试协程间的协作
     */
    @Test
    fun testCoroutineCooperation() = runTest {
        var job1Complete = false
        var job2Complete = false

        val job1 = launch {
            delay(100)
            job1Complete = true
        }

        val job2 = launch {
            delay(200)
            job2Complete = true
        }

        job1.join()
        job2.join()

        assertTrue(job1Complete)
        assertTrue(job2Complete)
    }

    /**
     * 测试协程的父子关系
     */
    @Test
    fun testParentChildRelationship() = runTest {
        var childExecuted = false

        val parentJob = launch {
            launch {
                delay(50)
                childExecuted = true
            }.join()
            delay(100)
        }

        parentJob.join()

        assertTrue("子协程应该执行", childExecuted)
    }
}
