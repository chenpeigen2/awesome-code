package com.peter.coroutine.demo.testing

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

/**
 * Flow 测试演示 Activity
 *
 * 本 Activity 展示如何测试 Kotlin Flow。
 *
 * ## Flow 测试的关键点
 * 1. **收集验证** - 验证 Flow 发射的值
 * 2. **操作符测试** - 测试 map、filter 等操作符
 * 3. **异常处理** - 测试 Flow 中的异常
 * 4. **StateFlow 测试** - 测试状态流
 *
 * ## 常用测试方法
 * - toList() - 收集所有值到列表
 * - first() - 获取第一个值
 * - take(n) - 只取前 n 个值
 * - catch {} - 捕获异常
 *
 * @see kotlinx.coroutines.flow.Flow
 * @see kotlinx.coroutines.flow.toList
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FlowTestActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.flow_test)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateFlowTesting()
    }

    /**
     * 演示 Flow 测试
     */
    private fun demonstrateFlowTesting() {
        log("========== Flow 测试演示 ==========\n")

        demonstrateBasicFlowTest()
        demonstrateOperatorTest()
        demonstrateStateFlowTest()
        demonstrateExceptionTest()
    }

    /**
     * 演示基本 Flow 测试
     */
    private fun demonstrateBasicFlowTest() {
        log("--- 1. 基本 Flow 测试 ---")

        // 模拟测试代码
        lifecycleScope.launch {
            // 测试 flowOf
            val flow1 = flowOf(1, 2, 3, 4, 5)
            val result1 = flow1.toList()
            log("flowOf(1,2,3,4,5).toList() = $result1")

            // 测试 flow 构建器
            val flow2 = flow {
                emit("A")
                emit("B")
                emit("C")
            }
            val result2 = flow2.toList()
            log("flow { emit A, B, C }.toList() = $result2")

            // 测试 first
            val flow3 = flowOf(10, 20, 30)
            val result3 = flow3.first()
            log("flowOf(10,20,30).first() = $result3")

            log("")
            demonstrateOperatorTestActual()
        }
    }

    /**
     * 演示操作符测试
     */
    private fun demonstrateOperatorTest() {
        log("--- 2. 操作符测试 ---")
        log("Flow 操作符可以链式调用和测试:")
    }

    private suspend fun demonstrateOperatorTestActual() {
        // 测试 map
        val mapped = flowOf(1, 2, 3)
            .map { it * 2 }
            .toList()
        log("flowOf(1,2,3).map { it*2 } = $mapped")

        // 测试 filter
        val filtered = flowOf(1, 2, 3, 4, 5)
            .filter { it % 2 == 0 }
            .toList()
        log("flowOf(1,2,3,4,5).filter { it%2==0 } = $filtered")

        // 测试链式操作符
        val chained = flowOf(1, 2, 3, 4, 5)
            .filter { it > 2 }
            .map { it * 10 }
            .toList()
        log("filter { >2 }.map { *10 } = $chained")

        // 测试 take
        val taken = flowOf(1, 2, 3, 4, 5)
            .take(3)
            .toList()
        log("flowOf(1..5).take(3) = $taken")

        log("")
        demonstrateStateFlowTestActual()
    }

    /**
     * 演示 StateFlow 测试
     */
    private fun demonstrateStateFlowTest() {
        log("--- 3. StateFlow 测试 ---")
        log("StateFlow 测试示例:")
    }

    private suspend fun demonstrateStateFlowTestActual() {
        val stateFlow = MutableStateFlow(0)
        val results = mutableListOf<Int>()

        // 收集状态变化
        val job = kotlinx.coroutines.GlobalScope.launch {
            stateFlow.collect { results.add(it) }
        }

        // 更新状态
        stateFlow.value = 1
        stateFlow.value = 2
        stateFlow.value = 3

        delay(100) // 等待收集
        job.cancel()

        log("StateFlow 收集结果: $results")
        log("  (包含初始值 0 和后续更新)")

        log("")
        demonstrateExceptionTestActual()
    }

    /**
     * 演示异常测试
     */
    private fun demonstrateExceptionTest() {
        log("--- 4. 异常处理测试 ---")
        log("Flow 异常处理测试示例:")
    }

    private suspend fun demonstrateExceptionTestActual() {
        // 测试 catch 操作符
        val flowWithError = flow {
            emit(1)
            throw RuntimeException("Test exception")
        }

        val result = flowWithError
            .catch { emit(-1) } // 发生异常时发射 -1
            .toList()

        log("catch { emit(-1) } 结果: $result")

        // 测试 onStart 和 onCompletion
        val events = mutableListOf<String>()
        flowOf(1, 2, 3)
            .onStart { events.add("Started") }
            .onEach { events.add("Value: $it") }
            .collect()

        log("onStart/onEach 事件: $events")

        log("\n========== 演示完成 ==========")
        log("\n提示: 查看测试文件了解 Flow 测试的详细示例:")
        log("  coroutine-demo/src/test/java/com/peter/coroutine/demo/testing/FlowTest.kt")
    }

    /**
     * 添加日志
     */
    private fun log(message: String) {
        runOnUiThread {
            logBuilder.append(message).append("\n")
            tvLog.text = logBuilder.toString()
        }
    }
}
