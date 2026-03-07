package com.peter.coroutine.demo.basics

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 协程调度器 (Dispatchers) 演示
 *
 * 本 Activity 展示 Kotlin 协程中的四种主要调度器及其使用场景。
 *
 * ## 什么是调度器？
 * 调度器决定了协程在哪个线程或线程池上执行。
 * 它是协程上下文 (CoroutineContext) 的重要组成部分。
 *
 * ## 四种主要调度器：
 *
 * ### 1. Dispatchers.Default
 * - 用于 CPU 密集型任务
 * - 使用共享的后台线程池
 * - 线程数默认等于 CPU 核心数（至少 2 个）
 * - 适用于: 排序、过滤、计算等
 *
 * ### 2. Dispatchers.IO
 * - 用于 I/O 密集型任务
 * - 按需创建线程，最多可创建 64 个（或更多）
 * - 适用于: 网络请求、文件读写、数据库操作等
 *
 * ### 3. Dispatchers.Main
 * - 用于 UI 操作
 * - 通常只包含一个线程（主线程）
 * - 适用于: 更新 UI、处理用户交互等
 *
 * ### 4. Dispatchers.Unconfined
 * - 不限制执行线程
 * - 在调用者线程启动，挂起后在恢复线程继续
 * - 一般不推荐使用，仅用于特殊场景
 *
 * ## withContext 切换调度器：
 * 使用 withContext 可以在不创建新协程的情况下切换调度器。
 *
 * @see kotlinx.coroutines.Dispatchers
 * @see kotlinx.coroutines.withContext
 */
class DispatchersActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dispatchers)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.dispatchers)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        findViewById<Button>(R.id.btnDefault).setOnClickListener {
            testDefaultDispatcher()
        }

        findViewById<Button>(R.id.btnIO).setOnClickListener {
            testIODispatcher()
        }

        findViewById<Button>(R.id.btnMain).setOnClickListener {
            testMainDispatcher()
        }

        findViewById<Button>(R.id.btnSwitch).setOnClickListener {
            demonstrateDispatcherSwitch()
        }

        // 初始说明
        log("点击按钮测试不同的调度器\n")
    }

    /**
     * 测试 Dispatchers.Default
     * 适用于 CPU 密集型任务
     */
    private fun testDefaultDispatcher() {
        log("\n=== Dispatchers.Default ===")

        lifecycleScope.launch(Dispatchers.Default) {
            log("执行线程: ${Thread.currentThread().name}")
            log("这是一个 CPU 密集型任务的理想选择")
            log("线程池大小通常等于 CPU 核心数")

            // 模拟 CPU 密集型计算
            val result = computeFibonacci(20)
            log("计算结果: Fibonacci(20) = $result")
        }
    }

    /**
     * 测试 Dispatchers.IO
     * 适用于 I/O 密集型任务
     */
    private fun testIODispatcher() {
        log("\n=== Dispatchers.IO ===")

        lifecycleScope.launch(Dispatchers.IO) {
            log("执行线程: ${Thread.currentThread().name}")
            log("这是 I/O 操作的理想选择")
            log("线程池可以弹性扩展")

            // 模拟 I/O 操作
            delay(100)
            log("模拟文件读取/网络请求完成")
        }
    }

    /**
     * 测试 Dispatchers.Main
     * 适用于 UI 操作
     */
    private fun testMainDispatcher() {
        log("\n=== Dispatchers.Main ===")

        lifecycleScope.launch(Dispatchers.Main) {
            log("执行线程: ${Thread.currentThread().name}")
            log("这是 UI 更新的理想选择")
            log("可以直接更新 UI 组件")

            // 在 Main 调度器中可以直接更新 UI
            tvLog.text = logBuilder.toString()
        }
    }

    /**
     * 演示调度器切换
     * 使用 withContext 在协程内切换调度器
     */
    private fun demonstrateDispatcherSwitch() {
        log("\n=== 调度器切换演示 ===")

        lifecycleScope.launch(Dispatchers.Main) {
            log("1. 起始于 Main: ${Thread.currentThread().name}")

            // 切换到 IO 执行网络请求
            val data = withContext(Dispatchers.IO) {
                log("2. 切换到 IO: ${Thread.currentThread().name}")
                delay(500) // 模拟网络请求
                "网络数据"
            }

            log("3. 回到 Main: ${Thread.currentThread().name}")

            // 切换到 Default 执行计算
            val result = withContext(Dispatchers.Default) {
                log("4. 切换到 Default: ${Thread.currentThread().name}")
                computeFibonacci(15)
            }

            log("5. 回到 Main: ${Thread.currentThread().name}")
            log("数据: $data, 计算结果: $result")
            log("withContext 保持了协程的连续性，只是切换了执行线程")
        }
    }

    /**
     * 计算 Fibonacci 数（模拟 CPU 密集型任务）
     */
    private fun computeFibonacci(n: Int): Long {
        if (n <= 1) return n.toLong()
        var a = 0L
        var b = 1L
        for (i in 2..n) {
            val temp = a + b
            a = b
            b = temp
        }
        return b
    }

    /**
     * 添加日志
     */
    private fun log(message: String) {
        logBuilder.append(message).append("\n")
        tvLog.text = logBuilder.toString()
    }
}
