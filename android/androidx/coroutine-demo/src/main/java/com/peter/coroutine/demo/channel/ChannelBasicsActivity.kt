package com.peter.coroutine.demo.channel

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.channels.Channel.Factory.RENDEZVOUS
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Channel 基础演示
 *
 * 本 Activity 展示 Kotlin 协程中 Channel 的核心概念：
 *
 * ## 什么是 Channel？
 * Channel 是协程之间传递数据的管道，类似于 BlockingQueue，但是非阻塞的。
 * 它实现了生产者-消费者模式，允许协程之间安全地传递数据。
 *
 * ## 核心概念：
 * 1. **SendChannel**: 发送端，用于发送数据
 * 2. **ReceiveChannel**: 接收端，用于接收数据
 * 3. **send()**: 挂起函数，发送数据到通道
 * 4. **receive()**: 挂起函数，从通道接收数据
 *
 * ## Channel 容量策略：
 * - **RENDEZVOUS (0)**: 默认策略，发送者和接收者必须同时准备好
 * - **BUFFERED (64)**: 有缓冲区，容量为 64（可配置）
 * - **UNLIMITED (Int.MAX_VALUE)**: 无限容量，发送永不挂起
 * - **CONFLATED (-1)**: 只保留最新元素，新元素覆盖旧元素
 *
 * ## Channel 特点：
 * - 支持多生产者和多消费者
 * - 关闭后不能再发送，但可以继续接收剩余数据
 * - 是热流（Hot Stream），无论是否有消费者都会生产
 *
 * @see kotlinx.coroutines.channels.Channel
 * @see kotlinx.coroutines.channels.SendChannel
 * @see kotlinx.coroutines.channels.ReceiveChannel
 */
class ChannelBasicsActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.channel_basics)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateChannelBasics()
    }

    /**
     * 演示 Channel 的基本使用
     */
    private fun demonstrateChannelBasics() {
        log("========== Channel 基础演示 ==========\n")

        // 演示1: 基本的 send 和 receive
        demonstrateBasicSendReceive()

        // 演示2: RENDEZVOUS 策略
        demonstrateRendezvousChannel()

        // 演示3: BUFFERED 策略
        demonstrateBufferedChannel()

        // 演示4: UNLIMITED 策略
        demonstrateUnlimitedChannel()

        // 演示5: CONFLATED 策略
        demonstrateConflatedChannel()
    }

    /**
     * 演示1: 基本的 send 和 receive 操作
     *
     * Channel 最基本的使用方式：
     * - send() 发送数据
     * - receive() 接收数据
     * - 两者都是挂起函数
     */
    private fun demonstrateBasicSendReceive() {
        log("--- 演示1: 基本 send/receive ---")

        lifecycleScope.launch {
            // 创建一个默认容量的 Channel
            val channel = Channel<String>()

            // 启动发送者协程
            launch {
                log("发送者: 准备发送 'Hello'")
                channel.send("Hello")
                log("发送者: 发送完成")

                log("发送者: 准备发送 'World'")
                channel.send("World")
                log("发送者: 发送完成")

                channel.close() // 关闭通道
                log("发送者: 通道已关闭")
            }

            // 稍微延迟，让发送者先尝试发送
            delay(100)

            // 接收数据
            log("接收者: 准备接收")
            log("接收者: 收到 '${channel.receive()}'")
            log("接收者: 收到 '${channel.receive()}'")

            // 检查是否还有更多数据
            log("接收者: 通道是否已关闭? ${channel.isClosedForReceive}")
            log("")
        }
    }

    /**
     * 演示2: RENDEZVOUS 策略（默认，容量为 0）
     *
     * - 发送者必须等待接收者准备好才能发送
     * - 接收者必须等待发送者发送才能接收
     * - 类似于"握手"机制
     */
    private fun demonstrateRendezvousChannel() {
        log("--- 演示2: RENDEZVOUS 策略 (容量=0) ---")

        lifecycleScope.launch {
            // 显式指定 RENDEZVOUS 容量
            val channel = Channel<Int>(RENDEZVOUS)
            log("Channel 容量: RENDEZVOUS (0)")
            log("发送者必须等待接收者准备好\n")

            var sendTime = 0L

            // 发送者
            launch {
                sendTime = System.currentTimeMillis()
                log("发送者: 尝试发送 1...")
                channel.send(1)
                log("发送者: 发送 1 成功! 耗时: ${System.currentTimeMillis() - sendTime}ms")
            }

            // 延迟接收，模拟接收者还未准备好
            delay(1000)
            log("接收者: 1秒后开始接收...")

            log("接收者: 收到 ${channel.receive()}")
            log("")
        }
    }

    /**
     * 演示3: BUFFERED 策略（容量为 64 或指定值）
     *
     * - 有固定大小的缓冲区
     * - 缓冲区满时发送者会挂起
     * - 缓冲区空时接收者会挂起
     */
    private fun demonstrateBufferedChannel() {
        log("--- 演示3: BUFFERED 策略 ---")

        lifecycleScope.launch {
            // 创建容量为 3 的缓冲 Channel
            val channel = Channel<Int>(3)
            log("Channel 容量: 3")

            // 发送者
            launch {
                repeat(5) { i ->
                    log("发送者: 发送 $i")
                    channel.send(i)
                    log("发送者: 发送 $i 成功")
                }
                channel.close()
            }

            // 延迟让缓冲区填满
            delay(500)

            log("\n接收者开始接收:")
            for (value in channel) {
                log("接收者: 收到 $value")
                delay(100)
            }
            log("")
        }
    }

    /**
     * 演示4: UNLIMITED 策略（无限容量）
     *
     * - 缓冲区无限大
     * - send() 永远不会挂起
     * - 可能导致内存溢出，谨慎使用
     */
    private fun demonstrateUnlimitedChannel() {
        log("--- 演示4: UNLIMITED 策略 ---")

        lifecycleScope.launch {
            val channel = Channel<Int>(UNLIMITED)
            log("Channel 容量: UNLIMITED (${Int.MAX_VALUE})")
            log("send() 永远不会挂起\n")

            // 快速发送大量数据
            launch {
                repeat(10) { i ->
                    channel.send(i)
                    log("发送者: 立即发送 $i 成功")
                }
                channel.close()
            }

            delay(100)

            // 接收所有数据
            log("\n接收者开始接收:")
            while (!channel.isClosedForReceive) {
                try {
                    log("接收者: 收到 ${channel.receive()}")
                } catch (e: Exception) {
                    break
                }
            }
            log("")
        }
    }

    /**
     * 演示5: CONFLATED 策略（只保留最新）
     *
     * - 缓冲区大小为 1
     * - 新元素会覆盖旧元素
     * - 适用于只关心最新数据的场景（如 UI 状态更新）
     */
    private fun demonstrateConflatedChannel() {
        log("--- 演示5: CONFLATED 策略 ---")

        lifecycleScope.launch {
            val channel = Channel<Int>(CONFLATED)
            log("Channel 容量: CONFLATED (-1)")
            log("只保留最新的元素，旧元素会被覆盖\n")

            // 快速发送多个元素
            launch {
                repeat(5) { i ->
                    log("发送者: 发送 $i")
                    channel.send(i)
                    delay(50) // 快速发送
                }
                channel.close()
            }

            // 延迟接收，模拟处理慢的情况
            delay(300)
            log("接收者: 开始接收（注意丢失的数据）")

            for (value in channel) {
                log("接收者: 收到 $value")
                delay(100)
            }

            log("\n注意: 由于 CONFLATED 策略，中间的数据被覆盖了")
            log("========== 演示完成 ==========")
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
