package com.peter.coroutine.demo.channel

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 生产者-消费者模式演示
 *
 * 本 Activity 展示 Kotlin 协程中 produce 构建器和消费者模式：
 *
 * ## 什么是生产者-消费者模式？
 * 一种经典的并发模式，生产者生成数据，消费者处理数据，
 * 通过 Channel 实现两者的解耦和通信。
 *
 * ## 核心概念：
 * 1. **produce {}**: 构建器，创建一个生产者协程，返回 ReceiveChannel
 * 2. **consumeEach**: 扩展函数，消费 Channel 中的所有元素
 * 3. **多个消费者**: 可以有多个协程同时消费一个 Channel
 *
 * ## produce 构建器的优势：
 * - 自动管理 Channel 的生命周期
 * - 协程取消时自动关闭 Channel
 * - 异常时自动关闭 Channel
 * - 代码更简洁，符合结构化并发
 *
 * ## 消费者模式：
 * - for-in 循环消费
 * - consumeEach 扩展函数
 * - 多个消费者竞争消费
 *
 * @see kotlinx.coroutines.channels.produce
 * @see kotlinx.coroutines.channels.consumeEach
 * @see kotlinx.coroutines.channels.ReceiveChannel
 */
class ProduceConsumeActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.produce_consume)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateProduceConsume()
    }

    /**
     * 演示生产者-消费者模式
     */
    private fun demonstrateProduceConsume() {
        log("========== 生产者-消费者模式演示 ==========\n")

        // 演示1: 使用 produce 构建器
        demonstrateProduceBuilder()

        // 演示2: 使用 consumeEach 消费
        demonstrateConsumeEach()

        // 演示3: 多个消费者
        demonstrateMultipleConsumers()

        // 演示4: 关闭 Channel
        demonstrateChannelClosing()
    }

    /**
     * 演示1: 使用 produce 构建器创建生产者
     *
     * produce 是一个协程构建器，专门用于创建生产者协程。
     * 它返回一个 ReceiveChannel，可以用于接收数据。
     *
     * 特点：
     * - 自动创建 Channel
     * - 协程结束时自动关闭 Channel
     * - 支持指定 Channel 容量
     */
    private fun demonstrateProduceBuilder() {
        log("--- 演示1: produce 构建器 ---")

        lifecycleScope.launch {
            // 使用 produce 创建生产者
            val numbers = produce<Int>(capacity = Channel.BUFFERED) {
                log("生产者: 开始生产数据...")
                for (i in 1..5) {
                    log("生产者: 发送 $i")
                    send(i)
                    delay(200) // 模拟生产耗时
                }
                log("生产者: 生产完成，自动关闭 Channel")
            }

            // 消费数据
            log("消费者: 开始消费\n")
            for (num in numbers) {
                log("消费者: 收到 $num")
            }
            log("消费者: 消费完成\n")
        }
    }

    /**
     * 演示2: 使用 consumeEach 消费 Channel
     *
     * consumeEach 是一个扩展函数，用于消费 Channel 中的所有元素。
     * 它会在 Channel 关闭后自动结束，无需手动循环。
     *
     * 特点：
     * - 简洁的消费方式
     * - 自动处理 Channel 关闭
     * - 支持 lambda 表达式
     */
    private fun demonstrateConsumeEach() {
        log("--- 演示2: consumeEach 消费 ---")

        lifecycleScope.launch {
            // 创建一个简单的生产者
            val messages = produce<String> {
                val items = listOf("Kotlin", "Coroutines", "Channel", "Flow", "StateFlow")
                items.forEach { item ->
                    send(item)
                    delay(100)
                }
            }

            // 使用 consumeEach 消费
            log("使用 consumeEach 消费:")
            messages.consumeEach { message ->
                log("  -> 收到: $message")
            }
            log("consumeEach 完成\n")
        }
    }

    /**
     * 演示3: 多个消费者同时消费
     *
     * 一个 Channel 可以被多个消费者同时消费。
     * 每个元素只会被一个消费者获取（竞争消费）。
     *
     * 这是一种常见的负载均衡模式：
     * - 多个工作线程同时处理任务
     * - 任务自动分配给空闲的消费者
     */
    private fun demonstrateMultipleConsumers() {
        log("--- 演示3: 多个消费者 ---")

        lifecycleScope.launch {
            // 创建任务 Channel
            val tasks = produce<Int>(capacity = Channel.UNLIMITED) {
                repeat(10) { i ->
                    send(i)
                }
                log("生产者: 已发送 10 个任务\n")
            }

            // 创建 3 个消费者
            val consumers = listOf("消费者A", "消费者B", "消费者C").map { name ->
                async(Dispatchers.Default) {
                    var count = 0
                    try {
                        for (task in tasks) {
                            log("$name: 处理任务 $task")
                            count++
                            delay(100) // 模拟处理时间
                        }
                    } catch (e: Exception) {
                        // Channel 已关闭
                    }
                    "$name 处理了 $count 个任务"
                }
            }

            // 等待所有消费者完成
            val results = consumers.awaitAll()
            delay(200)
            log("\n统计结果:")
            results.forEach { log("  $it") }
            log("\n注意: 每个任务只被一个消费者处理\n")
        }
    }

    /**
     * 演示4: Channel 的关闭
     *
     * Channel 可以被关闭，关闭后：
     * - 不能再发送新元素
     * - 可以继续接收已发送的元素
     * - 接收完所有元素后，for 循环会正常结束
     *
     * 关闭 Channel 的方式：
     * - channel.close(): 手动关闭
     * - produce 协程结束时自动关闭
     */
    private fun demonstrateChannelClosing() {
        log("--- 演示4: Channel 关闭 ---")

        lifecycleScope.launch {
            // 创建 Channel
            val channel = Channel<String>()

            // 发送者
            launch {
                log("发送者: 发送数据...")
                channel.send("First")
                channel.send("Second")
                channel.send("Third")
                log("发送者: 关闭 Channel")
                channel.close() // 手动关闭
                log("发送者: isClosedForSend = ${channel.isClosedForSend}")

                // 关闭后发送会抛出异常
                try {
                    channel.send("Fourth")
                } catch (e: Exception) {
                    log("发送者: 关闭后发送抛出异常 - ${e::class.simpleName}")
                }
            }

            delay(100)

            // 接收所有数据（包括关闭前发送的）
            log("\n接收者: 开始接收...")
            log("接收者: isClosedForReceive = ${channel.isClosedForReceive}")

            // 使用 for 循环接收，会自动处理关闭
            for (item in channel) {
                log("接收者: 收到 '$item'")
            }

            log("接收者: for 循环正常结束")
            log("接收者: isClosedForReceive = ${channel.isClosedForReceive}")

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
