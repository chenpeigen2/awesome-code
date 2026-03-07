package com.peter.coroutine.demo.channel

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.selects.selectUnbiased
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.random.Random

/**
 * Select 表达式演示
 *
 * 本 Activity 展示 Kotlin 协程中 select 表达式的核心概念：
 *
 * ## 什么是 Select 表达式？
 * select 允许同时等待多个挂起操作，哪个先完成就执行哪个。
 * 它类似于 Go 语言中的 select 语句，实现多路复用。
 *
 * ## 核心概念：
 * 1. **select<T>**: 同时等待多个操作，返回最先完成的结果
 * 2. **onReceive**: Channel 接收的子句
 * 3. **onSend**: Channel 发送的子句
 * 4. **onTimeout**: 超时的子句
 *
 * ## 使用场景：
 * - 从多个数据源获取数据，使用最快的
 * - 实现带超时的操作
 * - 多个 Channel 竞争消费
 * - 公平调度
 *
 * ## Select 子句：
 * - onReceive: 接收成功时执行
 * - onReceiveOrNull: Channel 关闭时返回 null
 * - onSend: 发送成功时执行
 * - onTimeout: 超时时执行
 *
 * ## select vs selectUnbiased：
 * - select: 按子句顺序优先选择
 * - selectUnbiased: 随机选择（公平调度）
 *
 * @see kotlinx.coroutines.selects.select
 * @see kotlinx.coroutines.selects.selectUnbiased
 * @see kotlinx.coroutines.channels.Channel
 */
class SelectExpressionActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.select_expression)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateSelectExpression()
    }

    /**
     * 演示 Select 表达式
     */
    private fun demonstrateSelectExpression() {
        log("========== Select 表达式演示 ==========\n")

        // 演示1: 基本的 select onReceive
        demonstrateSelectOnReceive()

        // 演示2: select onSend
        demonstrateSelectOnSend()

        // 演示3: 带超时的 select
        demonstrateSelectWithTimeout()

        // 演示4: 公平调度 selectUnbiased
        demonstrateSelectUnbiased()
    }

    /**
     * 演示1: 基本的 select onReceive
     *
     * 同时等待多个 Channel，接收最先到达的数据。
     * 这是一种"竞速"模式，使用最快的响应。
     */
    private fun demonstrateSelectOnReceive() {
        log("--- 演示1: select onReceive ---")

        lifecycleScope.launch {
            // 创建两个生产者 Channel
            val fastChannel = produce<String>(capacity = Channel.BUFFERED) {
                delay(100) // 快速响应
                send("来自快速通道")
            }

            val slowChannel = produce<String>(capacity = Channel.BUFFERED) {
                delay(500) // 慢速响应
                send("来自慢速通道")
            }

            log("同时等待两个 Channel...")

            // 使用 select 等待最先到达的数据
            repeat(2) {
                val result = select<String> {
                    fastChannel.onReceive { value ->
                        value
                    }
                    slowChannel.onReceive { value ->
                        value
                    }
                }
                log("select 结果: $result")
            }

            log("\n解释: 快速通道总是先被选中\n")
        }
    }

    /**
     * 演示2: select onSend
     *
     * 同时尝试向多个 Channel 发送数据，
     * 哪个 Channel 先准备好接收，就发送到哪个。
     *
     * 这是一种"负载均衡"模式，将任务分配给空闲的消费者。
     */
    private fun demonstrateSelectOnSend() {
        log("--- 演示2: select onSend ---")

        lifecycleScope.launch {
            // 创建两个缓冲 Channel
            val channelA = Channel<String>(1)
            val channelB = Channel<String>(1)

            // 消费者 A - 处理快
            launch {
                while (true) {
                    delay(100)
                    val msg = channelA.receive()
                    log("消费者A: 处理 '$msg'")
                }
            }

            // 消费者 B - 处理慢
            launch {
                while (true) {
                    delay(300)
                    val msg = channelB.receive()
                    log("消费者B: 处理 '$msg'")
                }
            }

            // 生产者使用 select 发送
            delay(50)
            repeat(6) { i ->
                val message = "任务$i"

                // 使用 select 发送到先准备好的 Channel
                val sentTo = select<String> {
                    channelA.onSend(message) { "A" }
                    channelB.onSend(message) { "B" }
                }

                log("生产者: '$message' 发送到 Channel $sentTo")
            }

            log("\n解释: 消费者A处理快，所以更多任务被发送到A\n")
        }
    }

    /**
     * 演示3: 带超时的 select
     *
     * 使用 select 实现带超时的操作。
     * 如果在超时时间内没有收到数据，执行超时处理。
     *
     * 这比 withTimeout 更灵活，可以同时等待多个操作。
     */
    private fun demonstrateSelectWithTimeout() {
        log("--- 演示3: 带超时的 select ---")

        lifecycleScope.launch {
            // 创建一个慢速 Channel
            val slowChannel = Channel<String>()

            // 启动一个协程延迟发送
            launch {
                delay(2000) // 2秒后发送
                slowChannel.send("迟到的数据")
            }

            log("等待数据（超时 500ms）...")

            // 带超时的 select - 使用 withTimeoutOrNull 包装
            val result = withTimeoutOrNull(500) {
                select<String> {
                    slowChannel.onReceive { value ->
                        "收到: $value"
                    }
                }
            } ?: "超时! 500ms 内未收到数据"

            log("结果: $result")

            // 再次等待，这次能收到数据
            delay(1600) // 等待数据到达
            log("\n再次等待...")
            val result2 = withTimeoutOrNull(100) {
                select<String> {
                    slowChannel.onReceiveCatching { result ->
                        result.getOrNull()?.let { "收到: $it" } ?: "Channel 已关闭"
                    }
                }
            } ?: "超时!"
            log("结果: $result2\n")
        }
    }

    /**
     * 演示4: 公平调度 selectUnbiased
     *
     * - select: 按子句声明顺序优先选择（有偏见）
     * - selectUnbiased: 随机选择（无偏见，公平调度）
     *
     * 当多个 Channel 同时就绪时，select 会按顺序选择第一个，
     * 而 selectUnbiased 会随机选择一个。
     */
    private fun demonstrateSelectUnbiased() {
        log("--- 演示4: selectUnbiased 公平调度 ---")

        lifecycleScope.launch {
            // 创建两个同时就绪的 Channel
            val channelA = Channel<Int>()
            val channelB = Channel<Int>()

            // 同时发送数据
            launch {
                repeat(20) {
                    channelA.send(1)
                    channelB.send(2)
                }
            }

            log("使用 select (有偏见):")
            var countA = 0
            var countB = 0
            repeat(10) {
                val result = select<Int> {
                    channelA.onReceive { it }
                    channelB.onReceive { it }
                }
                if (result == 1) countA++ else countB++
            }
            log("  Channel A 被选中: $countA 次")
            log("  Channel B 被选中: $countB 次")

            // 重置 Channel
            val channelC = Channel<Int>()
            val channelD = Channel<Int>()

            launch {
                repeat(20) {
                    channelC.send(3)
                    channelD.send(4)
                }
            }

            log("\n使用 selectUnbiased (无偏见):")
            var countC = 0
            var countD = 0
            repeat(10) {
                val result = selectUnbiased<Int> {
                    channelC.onReceive { it }
                    channelD.onReceive { it }
                }
                if (result == 3) countC++ else countD++
            }
            log("  Channel C 被选中: $countC 次")
            log("  Channel D 被选中: $countD 次")

            log("\n解释:")
            log("  select 按顺序优先选择，所以 A 更频繁")
            log("  selectUnbiased 随机选择，分布更均匀")

            log("\n========== 演示完成 ==========")
        }
    }

    /**
     * 添加日志并更新 UI
     */
    private fun log(message: String) {
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
