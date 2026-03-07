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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * 冷流与热流演示
 *
 * 本 Activity 展示 Kotlin Flow 中冷流（Cold Flow）和热流（Hot Flow）的区别。
 *
 * ## 冷流 (Cold Flow) - Flow
 *
 * ### 特点：
 * - 只有被收集时才会开始执行
 * - 每次收集都会创建一个新的流实例
 * - 不同收集者之间互不影响
 * - 数据从生产者到消费者是点对点的
 *
 * ### 使用场景：
 * - 网络请求
 * - 数据库查询
 * - 文件读取
 *
 * ## 热流 (Hot Flow) - StateFlow / SharedFlow
 *
 * ### 特点：
 * - 无论是否有收集者，都会持续产生数据
 * - 多个收集者共享同一个流
 * - 新的收集者会收到当前/最新的值
 * - 可以在多个协程间广播数据
 *
 * ### SharedFlow 参数：
 * - `replay`: 新订阅者能收到的历史值数量
 * - `extraBufferCapacity`: 额外的缓冲区大小
 * - `onBufferOverflow`: 缓冲区满时的处理策略
 *
 * ### StateFlow vs SharedFlow：
 * - StateFlow: 始终有初始值，replay=1，必须有值
 * - SharedFlow: 可以没有初始值，replay 可配置
 *
 * @see kotlinx.coroutines.flow.flow
 * @see kotlinx.coroutines.flow.StateFlow
 * @see kotlinx.coroutines.flow.SharedFlow
 */
class ColdHotFlowActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.cold_hot_flow)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateColdHotFlow()
    }

    private fun demonstrateColdHotFlow() {
        log("========== 冷流与热流演示 ==========\n")

        // 演示1: 冷流特性
        demonstrateColdFlow()

        // 演示2: SharedFlow 特性
        demonstrateSharedFlow()

        // 演示3: StateFlow vs SharedFlow
        demonstrateStateFlowVsSharedFlow()

        // 演示4: replay 参数
        demonstrateReplayParameter()

        log("\n========== 演示完成 ==========")
    }

    /**
     * 演示冷流特性
     */
    private fun demonstrateColdFlow() {
        log("--- 1. 冷流 (Flow) 特性 ---\n")

        val coldFlow = createColdFlow()

        log("Flow 已创建，但尚未执行")

        log("\n第一个收集者开始收集:")
        lifecycleScope.launch {
            coldFlow.collect { value ->
                log("  收集者1: $value")
            }
        }

        // 等待第一个收集完成
        lifecycleScope.launch {
            delay(1500)

            log("\n第二个收集者开始收集:")
            // 再次收集会重新执行 Flow
            launch {
                coldFlow.collect { value ->
                    log("  收集者2: $value")
                }
            }
        }
    }

    /**
     * 创建冷流
     */
    private fun createColdFlow(): Flow<Int> = flow {
        log("  [冷流] 开始执行 - 只在有收集者时才运行")
        for (i in 1..3) {
            delay(300)
            log("  [冷流] 发送: $i")
            emit(i)
        }
        log("  [冷流] 完成")
    }

    /**
     * 演示 SharedFlow 特性
     */
    private fun demonstrateSharedFlow() {
        log("\n--- 2. SharedFlow 特性 ---\n")

        // 创建 SharedFlow，replay=2 表示新订阅者能收到最近2个值
        val sharedFlow = MutableSharedFlow<String>(replay = 2)

        // 发送一些数据（此时没有收集者）
        log("发送数据（无收集者）:")
        lifecycleScope.launch {
            sharedFlow.emit("消息1")
            log("  发送: 消息1")
            sharedFlow.emit("消息2")
            log("  发送: 消息2")
            sharedFlow.emit("消息3")
            log("  发送: 消息3")
        }

        // 延迟后添加收集者
        lifecycleScope.launch {
            delay(500)
            log("\n收集者订阅（由于 replay=2，会收到最近2条消息）:")
            sharedFlow.collect { message ->
                log("  收到: $message")
            }
        }
    }

    /**
     * 演示 StateFlow vs SharedFlow
     */
    private fun demonstrateStateFlowVsSharedFlow() {
        log("\n--- 3. StateFlow vs SharedFlow ---\n")

        // StateFlow: 必须有初始值
        val stateFlow = MutableStateFlow("初始状态")
        log("StateFlow 初始值: ${stateFlow.value}")

        // SharedFlow: 可以没有初始值
        val sharedFlow = MutableSharedFlow<String>()
        log("SharedFlow 可以没有初始值")

        log("\n区别:")
        log("  StateFlow: 始终有值，适合表示状态")
        log("  SharedFlow: 适合表示事件流")
        log("  StateFlow 的 value 属性可以随时读取当前值")
        log("  SharedFlow 没有 value 属性")
    }

    /**
     * 演示 replay 参数
     */
    private fun demonstrateReplayParameter() {
        log("\n--- 4. replay 参数演示 ---\n")

        lifecycleScope.launch {
            // replay = 0: 新订阅者收不到历史值
            val noReplay = MutableSharedFlow<Int>(replay = 0)
            // replay = 1: 新订阅者收到最近1个值
            val replay1 = MutableSharedFlow<Int>(replay = 1)
            // replay = 3: 新订阅者收到最近3个值
            val replay3 = MutableSharedFlow<Int>(replay = 3)

            // 发送数据
            log("发送数据: 1, 2, 3")
            repeat(3) { i ->
                val value = i + 1
                noReplay.emit(value)
                replay1.emit(value)
                replay3.emit(value)
            }

            log("\n新收集者订阅后的结果:")

            // 订阅 noReplay
            launch {
                val values = mutableListOf<Int>()
                noReplay.collect { values.add(it) }
            }

            // StateFlow 相当于 replay=1 的 SharedFlow
            log("  replay=0: 收不到历史值")
            log("  replay=1: 收到 ${replay1.replayCache}")
            log("  replay=3: 收到 ${replay3.replayCache}")
            log("  StateFlow 相当于 replay=1，但必须有初始值")
        }
    }

    private fun log(message: String) {
        runOnUiThread {
            logBuilder.append(message).append("\n")
            tvLog.text = logBuilder.toString()
        }
    }
}
