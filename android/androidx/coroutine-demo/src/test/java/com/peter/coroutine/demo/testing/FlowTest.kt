package com.peter.coroutine.demo.testing

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

/**
 * Flow 测试
 *
 * 本测试类展示如何测试 Kotlin Flow：
 * - 测试 Flow 发射
 * - 测试 Flow 操作符
 * - 测试 StateFlow
 * - 测试异常处理
 *
 * ## Flow 测试的关键点
 * 1. 使用 toList() 收集所有发射的值
 * 2. 使用 first() 或 last() 获取特定值
 * 3. 使用 take() 限制收集的值数量
 * 4. 使用 runTest 在测试中运行协程
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FlowTest {

    // ==================== Flow 发射测试 ====================

    /**
     * 测试 flowOf 发射多个值
     */
    @Test
    fun testFlowOfEmission() = runTest {
        val flow: Flow<Int> = flowOf(1, 2, 3, 4, 5)

        val result = flow.toList()

        assertEquals(listOf(1, 2, 3, 4, 5), result)
    }

    /**
     * 测试使用 flow 构建器
     */
    @Test
    fun testFlowBuilder() = runTest {
        val flow = flow {
            emit(1)
            emit(2)
            emit(3)
        }

        val result = flow.toList()

        assertEquals(listOf(1, 2, 3), result)
    }

    /**
     * 测试空 Flow
     */
    @Test
    fun testEmptyFlow() = runTest {
        val emptyFlow: Flow<Int> = flowOf()

        val result = emptyFlow.toList()

        assertTrue("空 Flow 应该返回空列表", result.isEmpty())
    }

    /**
     * 测试 asFlow 转换
     */
    @Test
    fun testAsFlow() = runTest {
        val flow = listOf("a", "b", "c").asFlow()

        val result = flow.toList()

        assertEquals(listOf("a", "b", "c"), result)
    }

    // ==================== Flow 操作符测试 ====================

    /**
     * 测试 map 操作符
     */
    @Test
    fun testMapOperator() = runTest {
        val flow = flowOf(1, 2, 3).map { it * 2 }

        val result = flow.toList()

        assertEquals(listOf(2, 4, 6), result)
    }

    /**
     * 测试 filter 操作符
     */
    @Test
    fun testFilterOperator() = runTest {
        val flow = flowOf(1, 2, 3, 4, 5).filter { it % 2 == 0 }

        val result = flow.toList()

        assertEquals(listOf(2, 4), result)
    }

    /**
     * 测试链式操作符
     */
    @Test
    fun testChainedOperators() = runTest {
        val flow = flowOf(1, 2, 3, 4, 5)
            .filter { it > 2 }
            .map { it * 10 }

        val result = flow.toList()

        assertEquals(listOf(30, 40, 50), result)
    }

    /**
     * 测试 take 操作符
     */
    @Test
    fun testTakeOperator() = runTest {
        val flow = flowOf(1, 2, 3, 4, 5).take(3)

        val result = flow.toList()

        assertEquals(listOf(1, 2, 3), result)
    }

    /**
     * 测试 onEach 操作符
     */
    @Test
    fun testOnEachOperator() = runTest {
        val sideEffects = mutableListOf<String>()

        val flow = flowOf(1, 2, 3).onEach {
            sideEffects.add("Processing: $it")
        }

        val result = flow.toList()

        assertEquals(listOf(1, 2, 3), result)
        assertEquals(listOf("Processing: 1", "Processing: 2", "Processing: 3"), sideEffects)
    }

    /**
     * 测试 onStart 和 onCompletion
     */
    @Test
    fun testOnStartOnCompletion() = runTest {
        val events = mutableListOf<String>()

        val flow = flowOf(1, 2, 3)
            .onStart { events.add("Started") }
            .onCompletion { events.add("Completed") }

        flow.collect { events.add("Value: $it") }

        assertEquals(listOf("Started", "Value: 1", "Value: 2", "Value: 3", "Completed"), events)
    }

    // ==================== Flow 收集测试 ====================

    /**
     * 测试 first() 获取第一个值
     */
    @Test
    fun testFirst() = runTest {
        val flow = flowOf(1, 2, 3)

        val result = flow.first()

        assertEquals(1, result)
    }

    /**
     * 测试 last() 获取最后一个值
     */
    @Test
    fun testLast() = runTest {
        val flow = flowOf(1, 2, 3)

        val result = flow.last()

        assertEquals(3, result)
    }

    /**
     * 测试带条件的 first
     */
    @Test
    fun testFirstWithPredicate() = runTest {
        val flow = flowOf(1, 2, 3, 4, 5)

        val result = flow.first { it > 2 }

        assertEquals(3, result)
    }

    // ==================== StateFlow 测试 ====================

    /**
     * 测试 MutableStateFlow
     */
    @Test
    fun testMutableStateFlow() = runTest {
        val stateFlow = MutableStateFlow(0)
        val results = mutableListOf<Int>()

        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            stateFlow.collect { results.add(it) }
        }

        stateFlow.value = 1
        stateFlow.value = 2
        stateFlow.value = 3

        job.cancel()

        // StateFlow 会先发射初始值 0，然后发射更新的值
        assertTrue("应该包含初始值 0", results.contains(0))
        assertTrue("应该包含更新后的值 1", results.contains(1))
        assertTrue("应该包含更新后的值 2", results.contains(2))
        assertTrue("应该包含更新后的值 3", results.contains(3))
    }

    /**
     * 测试 StateFlow 只保留最新值
     */
    @Test
    fun testStateFlowLatestValue() = runTest {
        val stateFlow = MutableStateFlow("initial")

        // 快速更新多次
        stateFlow.value = "a"
        stateFlow.value = "b"
        stateFlow.value = "c"

        // 立即收集，只能看到最新值
        val result = stateFlow.value

        assertEquals("c", result)
    }

    /**
     * 测试 StateFlow 的订阅
     */
    @Test
    fun testStateFlowSubscription() = runTest {
        val stateFlow = MutableStateFlow(0)

        // 先设置值
        stateFlow.value = 10

        // 然后收集，应该立即收到当前值
        val firstValue = stateFlow.first()

        assertEquals(10, firstValue)
    }

    // ==================== 异常处理测试 ====================

    /**
     * 测试 Flow 中的异常
     */
    @Test
    fun testFlowException() = runTest {
        val flow = flow {
            emit(1)
            throw RuntimeException("Test exception")
        }

        val result = mutableListOf<Int>()
        var exceptionCaught = false

        flow
            .catch { exceptionCaught = true }
            .collect { result.add(it) }

        assertEquals(listOf(1), result)
        assertTrue("异常应该被捕获", exceptionCaught)
    }

    /**
     * 测试 catch 操作符后继续发射
     */
    @Test
    fun testCatchAndEmit() = runTest {
        val flow = flow {
            emit(1)
            throw RuntimeException("Test exception")
        }

        val result = flow
            .catch { emit(-1) } // 发生异常时发射 -1
            .toList()

        assertEquals(listOf(1, -1), result)
    }

    /**
     * 测试 onCompletion 在异常时也会调用
     */
    @Test
    fun testOnCompletionWithException() = runTest {
        var completionCalled = false
        var exception: Throwable? = null

        val flow = flow<Int> {
            emit(1)
            throw RuntimeException("Test exception")
        }.onCompletion { e ->
            completionCalled = true
            exception = e
        }

        try {
            flow.collect()
        } catch (e: RuntimeException) {
            // 预期的异常
        }

        assertTrue("onCompletion 应该被调用", completionCalled)
        assertNotNull("异常应该传递给 onCompletion", exception)
    }

    // ==================== 冷流特性测试 ====================

    /**
     * 测试 Flow 是冷流
     *
     * Flow 是冷流，每次 collect 都会重新执行。
     */
    @Test
    fun testColdFlow() = runTest {
        var executionCount = 0

        val coldFlow = flow {
            executionCount++
            emit(executionCount)
        }

        val result1 = coldFlow.first()
        val result2 = coldFlow.first()

        assertEquals(1, result1)
        assertEquals(2, result2) // 每次收集都会重新执行
    }

    /**
     * 测试多次收集
     */
    @Test
    fun testMultipleCollections() = runTest {
        val flow = flowOf(1, 2, 3)

        val result1 = flow.toList()
        val result2 = flow.toList()

        assertEquals(listOf(1, 2, 3), result1)
        assertEquals(listOf(1, 2, 3), result2)
    }

    // ==================== 异步操作测试 ====================

    /**
     * 测试 Flow 中的异步操作
     */
    @Test
    fun testAsyncInFlow() = runTest {
        val flow = flow {
            delay(100)
            emit(1)
            delay(100)
            emit(2)
            delay(100)
            emit(3)
        }

        val startTime = testScheduler.currentTime
        val result = flow.toList()
        val elapsed = testScheduler.currentTime - startTime

        assertEquals(listOf(1, 2, 3), result)
        assertEquals(300, elapsed) // 虚拟时间应该前进了 300ms
    }

    /**
     * 测试并发收集
     */
    @Test
    fun testConcurrentCollection() = runTest {
        val flow = flowOf(1, 2, 3)
        val results = mutableListOf<Int>()
        val jobs = mutableListOf<Job>()

        // 启动多个收集器
        repeat(3) { index ->
            jobs.add(launch {
                flow.collect { value ->
                    synchronized(results) {
                        results.add(value * 10 + index)
                    }
                }
            })
        }

        jobs.forEach { it.join() }

        // 每个收集器应该收集到 3 个值
        assertEquals(9, results.size)
    }
}
