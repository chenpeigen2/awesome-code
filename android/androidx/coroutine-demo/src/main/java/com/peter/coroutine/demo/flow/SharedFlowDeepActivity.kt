package com.peter.coroutine.demo.flow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.launch

/**
 * SharedFlowDeepActivity - SharedFlow 进阶
 *
 * 学习目标：
 * 1. MutableSharedFlow 参数详解 (replay, extraBufferCapacity, onBufferOverflow)
 * 2. SharedFlow vs StateFlow 对比实验
 * 3. Event Bus 模式 — 一次性事件
 */
/** 模拟一次性 UI 事件 */
private sealed interface UiEvent {
    data class ShowToast(val message: String) : UiEvent
    data object NavigateBack : UiEvent
    data class ShowSnackbar(val message: String) : UiEvent
}

class SharedFlowDeepActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, SharedFlowDeepActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.shared_flow_deep)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)
        demonstrateSharedFlowDeep()
    }

    private fun demonstrateSharedFlowDeep() {
        log("========== SharedFlow 进阶演示 ==========\n")

        lifecycleScope.launch {
            demonstrateSharedFlowParameters()
            demonstrateSharedFlowVsStateFlow()
            demonstrateEventBusPattern()

            log("\n========== 演示完成 ==========")
        }
    }

    /**
     * 1. MutableSharedFlow 参数详解
     *
     * SharedFlow 的三个核心参数：
     * - replay: 新订阅者能收到的历史值数量
     * - extraBufferCapacity: 额外缓冲区大小（不含 replay）
     * - onBufferOverflow: 缓冲区满时的策略
     *
     * 总缓冲区 = replay + extraBufferCapacity
     */
    private suspend fun demonstrateSharedFlowParameters() {
        log("━━━ 1. MutableSharedFlow 参数详解 ━━━\n")

        // --- 1a. SUSPEND 策略 ---
        log("【1a】replay=0, extraBufferCapacity=2, onBufferOverflow=SUSPEND")
        log("  缓冲区大小 = 0 + 2 = 2")
        log("  缓冲区满时：emit() 挂起，等待缓冲区腾出空间\n")

        val suspendFlow = MutableSharedFlow<Int>(
            replay = 0,
            extraBufferCapacity = 2,
            onBufferOverflow = BufferOverflow.SUSPEND
        )

        val startTime = System.currentTimeMillis()

        coroutineScope {
            // 先启动收集者（慢速消费）
            val collectorJob = launch {
                suspendFlow.collect { value ->
                    log("  [${elapsed(startTime)}] 收集: $value")
                    delay(200) // 慢速消费
                }
            }

            delay(100) // 等待收集者就绪

            // 发送端：快速发射
            log("  [${elapsed(startTime)}] 开始快速发射...")
            for (i in 1..6) {
                log("  [${elapsed(startTime)}] 发射: $i")
                suspendFlow.emit(i)
                log("  [${elapsed(startTime)}] 发射 $i 完成")
            }
            log("  [${elapsed(startTime)}] 全部发射完成\n")

            collectorJob.cancel()
        }
        delay(200)

        // --- 1b. DROP_LATEST 策略 ---
        log("【1b】replay=0, extraBufferCapacity=2, onBufferOverflow=DROP_LATEST")
        log("  缓冲区满时：丢弃最新尝试发射的值，emit() 不挂起\n")

        val dropLatestFlow = MutableSharedFlow<Int>(
            replay = 0,
            extraBufferCapacity = 2,
            onBufferOverflow = BufferOverflow.DROP_LATEST
        )

        val startTime2 = System.currentTimeMillis()

        coroutineScope {
            // 慢速收集者
            val collectorJob2 = launch {
                dropLatestFlow.collect { value ->
                    log("  [${elapsed(startTime2)}] 收集: $value")
                    delay(300)
                }
            }

            delay(100)

            log("  [${elapsed(startTime2)}] 快速发射 1..6:")
            for (i in 1..6) {
                dropLatestFlow.emit(i)
                log("  [${elapsed(startTime2)}] 发射 $i（立即返回，不挂起）")
            }

            delay(1500) // 等待收集者处理完
            collectorJob2.cancel()
        }
        log("  结果: 部分值被丢弃（DROP_LATEST 丢弃新值）\n")

        // --- 1c. DROP_OLDEST 策略 ---
        log("【1c】replay=0, extraBufferCapacity=2, onBufferOverflow=DROP_OLDEST")
        log("  缓冲区满时：丢弃缓冲区中最旧的值，为新值腾出空间\n")

        val dropOldestFlow = MutableSharedFlow<Int>(
            replay = 0,
            extraBufferCapacity = 2,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

        val startTime3 = System.currentTimeMillis()

        coroutineScope {
            // 慢速收集者
            val collectorJob3 = launch {
                dropOldestFlow.collect { value ->
                    log("  [${elapsed(startTime3)}] 收集: $value")
                    delay(300)
                }
            }

        delay(100)

        log("  [${elapsed(startTime3)}] 快速发射 1..6:")
        for (i in 1..6) {
            dropOldestFlow.emit(i)
            log("  [${elapsed(startTime3)}] 发射 $i（立即返回，不挂起）")
        }

        delay(2000) // 等待收集者处理完
            collectorJob3.cancel()
        }
        log("  结果: 旧值被丢弃，保留最新值（DROP_OLDEST 丢弃旧值）\n")

        // --- 总结 ---
        log("【参数总结】")
        log("  SUSPEND:      缓冲区满时挂起发射端，不丢数据")
        log("  DROP_LATEST:  缓冲区满时丢弃新值（正在发射的值）")
        log("  DROP_OLDEST:  缓冲区满时丢弃旧值（缓冲区中最旧的值）")
        log("  StateFlow 内部使用 replay=1 + DROP_OLDEST\n")
    }

    /**
     * 2. SharedFlow vs StateFlow 对比实验
     *
     * 核心区别：
     * - StateFlow: replay=1，始终有值，conflate（去重）
     * - SharedFlow: replay 可配置，无初始值，不去重
     */
    private suspend fun demonstrateSharedFlowVsStateFlow() {
        log("━━━ 2. SharedFlow vs StateFlow 对比实验 ━━━\n")

        // --- 2a. 初始值与 replay ---
        log("【2a】初始值与 replay 对比\n")

        val sharedFlow = MutableSharedFlow<String>(replay = 0)
        val stateFlow = MutableStateFlow("初始值")

        // 先发射一些值（此时无收集者）
        log("  发射值（无收集者）: A, B, C")
        sharedFlow.emit("A")
        sharedFlow.emit("B")
        sharedFlow.emit("C")
        stateFlow.value = "X"
        stateFlow.value = "Y"
        stateFlow.value = "Z"

        delay(100)

        // 后添加收集者
        log("\n  添加收集者（延迟订阅）:")
        log("  SharedFlow(replay=0): 收不到历史值")
        log("  StateFlow: 总是持有最新值 = ${stateFlow.value}")

        // 通过 replayCache 验证
        log("  SharedFlow.replayCache = ${sharedFlow.replayCache}")
        log("  StateFlow.value = ${stateFlow.value}\n")

        // --- 2b. 去重（conflation）对比 ---
        log("【2b】去重（Conflation）对比\n")

        val sharedFlow2 = MutableSharedFlow<String>(replay = 0)
        val stateFlow2 = MutableStateFlow("A")

        val sharedValues = mutableListOf<String>()
        val stateValues = mutableListOf<String>()

        coroutineScope {
            val sharedJob = launch {
                sharedFlow2.collect { sharedValues.add(it) }
            }
            val stateJob = launch {
                stateFlow2.collect { stateValues.add(it) }
            }

            delay(100)

            // 发射重复值
            log("  发射: A, A, B, B, B, C")
            sharedFlow2.emit("A")
            sharedFlow2.emit("A")
            sharedFlow2.emit("B")
            sharedFlow2.emit("B")
            sharedFlow2.emit("B")
            sharedFlow2.emit("C")

            stateFlow2.value = "A" // 与当前相同
            stateFlow2.value = "A" // 与当前相同
            stateFlow2.value = "B"
            stateFlow2.value = "B" // 与当前相同
            stateFlow2.value = "B" // 与当前相同
            stateFlow2.value = "C"

            delay(200)

            sharedJob.cancel()
            stateJob.cancel()
        }

        log("  SharedFlow 收集到: $sharedValues")
        log("  StateFlow 收集到: $stateValues")
        log("  → SharedFlow 发射 6 次，收集 6 次（不去重）")
        log("  → StateFlow 发射 6 次，收集 3 次（去重：相同值不重复通知）\n")

        // --- 2c. 总结对比表 ---
        log("【对比总结】")
        log("  ┌─────────────────┬──────────────────┬──────────────────┐")
        log("  │     特性        │    SharedFlow    │    StateFlow     │")
        log("  ├─────────────────┼──────────────────┼──────────────────┤")
        log("  │ 初始值          │ 不需要           │ 必须有           │")
        log("  │ replay          │ 可配置 (0,1,N)   │ 固定为 1         │")
        log("  │ 去重            │ 不去重           │ 相同值不通知     │")
        log("  │ value 属性      │ 无               │ 有               │")
        log("  │ 适用场景        │ 事件流           │ 状态表示         │")
        log("  └─────────────────┴──────────────────┴──────────────────┘\n")
    }

    /**
     * 3. Event Bus 模式 — 一次性事件
     *
     * 一次性事件（如 Toast、导航、Snackbar）不应在配置变更后重放。
     * 使用 SharedFlow(replay=0) 是理想选择。
     *
     * 如果用 StateFlow，配置变更后事件会被重放 — 这是 bug！
     */
    private suspend fun demonstrateEventBusPattern() {
        log("━━━ 3. Event Bus 模式（一次性事件）━━━\n")

        // --- 3a. 正确做法：SharedFlow(replay=0) ---
        log("【3a】正确做法: SharedFlow(replay=0)\n")

        val eventBus = MutableSharedFlow<UiEvent>(
            replay = 0,          // 不重放！配置变更后不重复处理
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

        // 模拟发送事件
        log("  模拟发送一次性事件:")
        eventBus.emit(UiEvent.ShowToast("保存成功"))
        log("  → 发送: ShowToast(\"保存成功\")")

        eventBus.emit(UiEvent.NavigateBack)
        log("  → 发送: NavigateBack")

        eventBus.emit(UiEvent.ShowSnackbar("操作完成"))
        log("  → 发送: ShowSnackbar(\"操作完成\")")

        delay(100)

        // 模拟配置变更后的新收集者
        log("\n  模拟配置变更（Activity 重建后的新收集者）:")
        val lateEvents = mutableListOf<UiEvent>()
        coroutineScope {
            val lateJob = launch {
                eventBus.collect { lateEvents.add(it) }
            }
            delay(200)
            log("  新收集者收到的事件: $lateEvents")
            log("  → 结果: 空列表！旧事件不会重放 ✓\n")
            lateJob.cancel()
        }

        // --- 3b. 错误做法：StateFlow 用于事件 ---
        log("【3b】错误做法: StateFlow 用于事件\n")

        val badEventBus = MutableStateFlow<UiEvent?>(null)

        log("  发送事件:")
        badEventBus.value = UiEvent.ShowToast("保存成功")
        log("  → 发送: ShowToast(\"保存成功\")")

        badEventBus.value = UiEvent.NavigateBack
        log("  → 发送: NavigateBack")

        // 此时 StateFlow 持有 NavigateBack
        log("\n  模拟配置变更（Activity 重建）:")
        log("  StateFlow.value = ${badEventBus.value}")
        log("  → 新收集者立即收到 NavigateBack！")
        log("  → 用户会看到重复导航！ ✗\n")

        // --- 3c. 总结 ---
        log("【Event Bus 总结】")
        log("  一次性事件（Toast/Snackbar/导航）:")
        log("    → 用 SharedFlow(replay=0)")
        log("    → 配置变更后不会重放")
        log("")
        log("  持续状态（Loading/登录状态/数据）:")
        log("    → 用 StateFlow")
        log("    → 配置变更后能恢复最新状态")
        log("")
        log("  如果必须用 StateFlow 做事件:")
        log("    → 收集后手动清除: _event.value = null")
        log("    → 但这有并发问题，不推荐")
    }

    private fun elapsed(startTime: Long): String {
        return "${System.currentTimeMillis() - startTime}ms"
    }

    private fun log(message: String) {
        runOnUiThread {
            logBuilder.append(message).append("\n")
            tvLog.text = logBuilder.toString()
        }
    }
}
