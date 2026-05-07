package com.peter.coroutine.demo.flow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * FlowBufferActivity - Flow 缓冲与背压
 *
 * 学习目标：
 * 1. buffer() 并发收集
 * 2. conflate() 跳过中间值
 * 3. debounce() 防抖
 * 4. collectLatest 取消前一个收集
 */
class FlowBufferActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, FlowBufferActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.flow_buffer)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)
        demonstrateFlowBuffer()
    }

    private fun demonstrateFlowBuffer() {
        log("========== Flow 缓冲与背压演示 ==========\n")

        lifecycleScope.launch {
            demonstrateNoBuffer()
            demonstrateBuffer()
            demonstrateConflate()
            demonstrateDebounce()
            demonstrateCollectLatest()

            log("\n========== 演示完成 ==========")
        }
    }

    /**
     * 1. 无缓冲 — 生产者和消费者串行执行
     * 消费者处理慢时，生产者会被阻塞
     */
    private suspend fun demonstrateNoBuffer() {
        log("━━━ 1. 无缓冲（串行）━━━\n")

        val startTime = System.currentTimeMillis()
        var count = 0

        flow {
            for (i in 1..5) {
                log("  [${elapsed(startTime)}] 发送: $i")
                emit(i)
            }
        }
            .onEach { value ->
                log("  [${elapsed(startTime)}] 开始处理: $value")
                delay(300) // 模拟耗时处理
                count++
                log("  [${elapsed(startTime)}] 处理完成: $value")
            }
            .collect {}

        log("  总计处理 $count 项，耗时约 ${elapsed(startTime)}ms\n")
    }

    /**
     * 2. buffer() — 生产者和消费者并发执行
     * 生产者可以继续发送，不必等消费者处理完
     */
    private suspend fun demonstrateBuffer() {
        log("━━━ 2. buffer() 并发缓冲 ━━━\n")

        val startTime = System.currentTimeMillis()
        var count = 0

        flow {
            for (i in 1..5) {
                log("  [${elapsed(startTime)}] 发送: $i")
                emit(i)
            }
        }
            .buffer() // 在生产者和消费者之间添加缓冲区
            .onEach { value ->
                log("  [${elapsed(startTime)}] 开始处理: $value")
                delay(300) // 模拟耗时处理
                count++
                log("  [${elapsed(startTime)}] 处理完成: $value")
            }
            .collect {}

        log("  总计处理 $count 项，耗时约 ${elapsed(startTime)}ms（更快!）\n")
    }

    /**
     * 3. conflate() — 跳过中间值
     * 当生产者快于消费者时，跳过未处理的中间值，只保留最新的
     * 适合：进度条、实时位置更新等场景
     */
    private suspend fun demonstrateConflate() {
        log("━━━ 3. conflate() 跳过中间值 ━━━\n")

        val startTime = System.currentTimeMillis()

        flow {
            for (i in 1..10) {
                log("  [${elapsed(startTime)}] 发送: $i")
                emit(i)
                delay(50) // 生产者快
            }
        }
            .conflate() // 跳过积压的值
            .collect { value ->
                log("  [${elapsed(startTime)}] 收集: $value")
                delay(200) // 消费者慢
                log("  [${elapsed(startTime)}] 处理完成: $value")
            }

        log("  注意: 部分值被跳过（conflate 只保留最新值）\n")
    }

    /**
     * 4. debounce() — 防抖
     * 在指定时间内没有新值发出后，才发送最后一个值
     * 适合：搜索框输入、窗口大小调整
     */
    @OptIn(FlowPreview::class)
    private suspend fun demonstrateDebounce() {
        log("━━━ 4. debounce() 防抖 ━━━\n")

        log("  模拟搜索输入（快速输入 + 停顿）:")
        log("  输入: 'K' → 'Ko' → 'Kot' → [停顿] → 'Kotlin'\n")

        flow {
            emit("K")
            delay(50)
            emit("Ko")
            delay(50)
            emit("Kot")
            delay(50)
            emit("Kotl")
            delay(50)
            emit("Kotlin")
            delay(300) // 停顿超过 debounce 时间
            emit("Kotlin C")
            delay(50)
            emit("Kotlin Co")
            delay(50)
            emit("Kotlin Cor")
            delay(50)
            emit("Kotlin Coroutine")
            delay(300) // 停顿
        }
            .debounce(200) // 200ms 内没有新值才发送
            .collect { value ->
                log("  debounce 输出: \"$value\"")
            }

        log("\n")
    }

    /**
     * 5. collectLatest — 取消前一个收集
     * 新值到来时，取消正在执行的前一个收集操作
     * 适合：图片加载（取消前一张，加载新一张）
     */
    private suspend fun demonstrateCollectLatest() {
        log("━━━ 5. collectLatest 取消前一个 ━━━\n")

        val startTime = System.currentTimeMillis()

        flow {
            for (i in 1..5) {
                emit(i)
                delay(100)
            }
        }
            .collectLatest { value ->
                log("  [${elapsed(startTime)}] 开始处理: $value")
                delay(250) // 模拟耗时操作
                log("  [${elapsed(startTime)}] 处理完成: $value") // 只有最后一个会到这里
            }

        log("  注意: 只有最后一个值被完整处理（前面的被取消了）\n")
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
