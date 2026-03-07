package com.peter.coroutine.demo.flow

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Flow 基础概念演示
 *
 * 本 Activity 展示 Kotlin Flow 的核心概念和基本用法。
 *
 * ## 什么是 Flow？
 * Flow 是 Kotlin 协程中的数据流 API，用于处理异步数据流。
 * 它是一个可以顺序发出多个值的协程构建器，类似于 RxJava 的 Observable。
 *
 * ## Flow 的核心特性：
 *
 * ### 1. 冷流 (Cold Flow)
 * - Flow 是冷流，只有在被收集(collect)时才会执行
 * - 每次收集都会重新执行 Flow 的代码块
 * - 没有收集者时，Flow 不会产生任何数据
 *
 * ### 2. 结构化并发
 * - Flow 遵循协程的结构化并发原则
 * - 当协程被取消时，Flow 也会自动取消
 *
 * ### 3. 上下文保持
 * - Flow 的收集发生在调用者的协程上下文中
 * - 可以使用 flowOn 操作符切换上游流的执行上下文
 *
 * ## Flow 构建器：
 * - `flow { ... }`: 最基本的构建器，可以使用 emit() 发送值
 * - `flowOf()`: 从固定值创建 Flow
 * - `asFlow()`: 将集合或序列转换为 Flow
 *
 * ## 收集器：
 * - `collect`: 终端操作符，收集 Flow 发出的所有值
 * - `collectLatest`: 只收集最新的值，如果新值到来则取消之前的处理
 * - `toList`: 将 Flow 收集为 List
 *
 * @see kotlinx.coroutines.flow.Flow
 * @see kotlinx.coroutines.flow.flow
 * @see kotlinx.coroutines.flow.collect
 */
class FlowBasicsActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.flow_basics)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateFlowBasics()
    }

    /**
     * 演示 Flow 的基本用法
     */
    private fun demonstrateFlowBasics() {
        log("========== Flow 基础演示 ==========\n")

        lifecycleScope.launch {
            // 演示1: 基本的 Flow 创建和收集
            log("--- 演示1: 基本 Flow ---")
            val simpleFlow = createSimpleFlow()
            log("Flow 已创建，但尚未执行（冷流特性）")
            log("开始收集...")
            simpleFlow.collect { value ->
                log("  收到值: $value")
            }
            log("收集完成\n")

            // 演示2: 冷流特性 - 多次收集会重新执行
            log("--- 演示2: 冷流特性 ---")
            log("第一次收集:")
            simpleFlow.collect { value ->
                log("  第一次: $value")
            }
            log("第二次收集:")
            simpleFlow.collect { value ->
                log("  第二次: $value")
            }
            log("注意: 每次收集都会重新执行 Flow 代码块\n")

            // 演示3: flowOn 切换执行上下文
            log("--- 演示3: flowOn 切换上下文 ---")
            val ioFlow = createFlowWithIO()
            ioFlow.collect { value ->
                log("  收到: $value (收集线程: ${Thread.currentThread().name})")
            }
            log("\n")

            // 演示4: Flow 的自动取消
            log("--- 演示4: 结构化并发 ---")
            log("当协程取消时，Flow 也会自动取消")
            log("这保证了资源的正确释放\n")

            log("========== 演示完成 ==========")
        }
    }

    /**
     * 创建一个简单的 Flow
     *
     * 使用 flow {} 构建器创建 Flow：
     * - emit() 用于发送值给收集者
     * - 可以使用挂起函数如 delay()
     * - 代码块只在被收集时执行
     *
     * @return 一个发出 1, 2, 3 的 Flow
     */
    private fun createSimpleFlow(): Flow<Int> = flow {
        log("  [Flow] 开始执行")
        for (i in 1..3) {
            log("  [Flow] 正在发送: $i")
            emit(i) // 发送值
            delay(100) // 模拟耗时操作
        }
        log("  [Flow] 执行完成")
    }

    /**
     * 创建在 IO 线程执行的 Flow
     *
     * flowOn 操作符：
     * - 用于指定 Flow 上游的执行上下文
     * - 只影响 flowOn 之前的操作符
     * - 收集仍然发生在调用者的上下文中
     *
     * @return 在 IO 线程执行的 Flow
     */
    private fun createFlowWithIO(): Flow<String> = flow {
        for (i in 1..3) {
            log("  [Flow-IO] 发送前，线程: ${Thread.currentThread().name}")
            emit("数据-$i")
            delay(200)
        }
    }.flowOn(Dispatchers.IO) // 在 IO 线程执行上游操作

    /**
     * 添加日志并更新 UI
     */
    private fun log(message: String) {
        // 确保在主线程更新 UI
        if (Thread.currentThread().name == "main") {
            logBuilder.append(message).append("\n")
            tvLog.text = logBuilder.toString()
        } else {
            runOnUiThread {
                logBuilder.append(message).append("\n")
                tvLog.text = logBuilder.toString()
            }
        }
    }
}
