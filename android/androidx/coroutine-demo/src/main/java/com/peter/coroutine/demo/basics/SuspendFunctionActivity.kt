package com.peter.coroutine.demo.basics

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Thread.sleep

/**
 * Suspend 函数原理演示
 *
 * 本 Activity 展示 Kotlin 协程中 suspend 关键字的核心概念：
 *
 * ## 什么是 Suspend 函数？
 * suspend 函数是一种可以在不阻塞线程的情况下挂起执行的函数。
 * 当协程执行到 suspend 函数时，它会保存当前状态并释放线程，
 * 等待操作完成后恢复执行。
 *
 * ## 核心概念：
 * 1. **挂起 (Suspend)**: 协程暂停执行，释放线程资源
 * 2. **恢复 (Resume)**: 挂起操作完成后，协程从挂起点继续执行
 * 3. **非阻塞**: 挂起期间线程可以做其他工作，不会被阻塞
 *
 * ## 挂起函数 vs 阻塞函数：
 * - `Thread.sleep()`: 阻塞当前线程，浪费资源
 * - `delay()`: 挂起协程，释放线程，效率更高
 *
 * ## Continuation 机制：
 * 编译器会将 suspend 函数转换为状态机，使用 Continuation
 * 来保存和恢复协程的执行状态。
 *
 * @see kotlinx.coroutines.delay
 * @see kotlin.coroutines.Continuation
 */
class SuspendFunctionActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.suspend_function)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateSuspendFunction()
    }

    /**
     * 演示 suspend 函数的挂起和恢复
     */
    private fun demonstrateSuspendFunction() {
        log("========== Suspend 函数演示 ==========\n")

        // 使用 lifecycleScope 启动协程
        lifecycleScope.launch {
            log("协程开始执行")
            log("当前线程: ${Thread.currentThread().name}\n")

            // 演示1: 使用 delay 挂起
            log("--- 演示1: delay() 挂起 ---")
            log("调用 delay(1000) 前")
            delay(1000) // 挂起 1 秒，但不阻塞线程
            log("delay(1000) 完成后\n")

            // 演示2: 对比阻塞与非阻塞
            log("--- 演示2: 阻塞 vs 非阻塞 ---")

            // 启动另一个协程来观察
            launch {
                log("子协程: 在主协程 delay 期间，我可以执行！")
            }

            delay(500)
            log("主协程继续\n")

            // 演示3: 调用自定义 suspend 函数
            log("--- 演示3: 自定义 suspend 函数 ---")
            val result = fetchDataFromNetwork()
            log("获取到数据: $result\n")

            // 演示4: 模拟线程阻塞的影响
            log("--- 演示4: Thread.sleep() 的危害 ---")
            log("开始阻塞操作...")

            // 在 IO 线程执行阻塞操作
            withContext(Dispatchers.IO) {
                log("在 IO 线程执行 Thread.sleep(1000)")
                log("阻塞期间，整个线程被占用，无法执行其他协程！")
                sleep(1000) // 这会阻塞线程！
                log("阻塞结束")
            }

            log("\n========== 演示完成 ==========")
        }
    }

    /**
     * 模拟网络请求的 suspend 函数
     *
     * 这是一个自定义的 suspend 函数，它会在执行时挂起协程，
     * 模拟网络延迟后返回结果。
     *
     * @return 模拟的网络响应数据
     */
    private suspend fun fetchDataFromNetwork(): String {
        log("  开始网络请求...")
        log("  当前线程: ${Thread.currentThread().name}")

        // 切换到 IO 调度器执行耗时操作
        return withContext(Dispatchers.IO) {
            log("  网络请求中 (IO 线程)...")
            delay(800) // 模拟网络延迟
            log("  网络请求完成")
            "{'data': 'Hello from server!'}"
        }
    }

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
