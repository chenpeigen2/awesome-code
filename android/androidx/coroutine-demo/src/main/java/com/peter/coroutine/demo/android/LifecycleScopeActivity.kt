package com.peter.coroutine.demo.android

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * lifecycleScope 演示
 *
 * 本 Activity 展示 lifecycleScope 的使用以及生命周期感知特性。
 *
 * ## 什么是 lifecycleScope？
 * lifecycleScope 是 LifecycleOwner 的扩展属性，它返回一个与 Lifecycle 绑定的
 * CoroutineScope。当 Lifecycle 被销毁时，这个 Scope 中的所有协程会自动取消。
 *
 * ## 使用场景：
 * - 在 Activity/Fragment 中执行异步操作
 * - 网络请求
 * - 数据库操作
 * - UI 动画
 *
 * ## 生命周期感知：
 * - CREATED: 对应 onCreate - onDestroy
 * - STARTED: 对应 onStart - onStop
 * - RESUMED: 对应 onResume - onPause
 * - DESTROYED: 协程会被取消
 *
 * ## repeatOnLifecycle：
 * 推荐使用 repeatOnLifecycle 来收集 Flow，它会在生命周期达到指定状态时启动协程，
 * 在生命周期低于指定状态时取消协程。
 *
 * ## 最佳实践：
 * ```kotlin
 * // 收集 Flow 的推荐方式
 * lifecycleScope.launch {
 *     repeatOnLifecycle(Lifecycle.State.STARTED) {
 *         viewModel.flow.collect { ... }
 *     }
 * }
 * ```
 *
 * @see androidx.lifecycle.lifecycleScope
 * @see androidx.lifecycle.repeatOnLifecycle
 */
class LifecycleScopeActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private val logBuilder = StringBuilder()

    private var countJob: Job? = null
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lifecycle_scope)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.lifecycle_scope)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)
        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)

        // 初始日志
        log("========== lifecycleScope 演示 ==========\n")
        log("lifecycleScope 与 Activity 生命周期绑定")
        log("Activity 销毁时协程自动取消\n")

        // 演示1: 基本使用
        demonstrateBasicUsage()

        // 演示2: repeatOnLifecycle
        demonstrateRepeatOnLifecycle()

        // 设置按钮监听
        btnStart.setOnClickListener {
            startCounting()
        }

        btnStop.setOnClickListener {
            stopCounting()
        }

        log("\n点击「开始计数」按钮启动协程")
        log("返回或关闭 Activity 观察协程自动取消")
    }

    /**
     * 演示 lifecycleScope 基本使用
     */
    private fun demonstrateBasicUsage() {
        log("--- 演示1: lifecycleScope 基本使用 ---")

        // 在 lifecycleScope 中启动协程
        lifecycleScope.launch {
            log("协程启动，当前生命周期: ${lifecycle.currentState}")

            // 模拟耗时操作
            withContext(Dispatchers.IO) {
                delay(500)
            }

            log("协程执行完成")
        }

        // 在指定调度器上启动
        lifecycleScope.launch(Dispatchers.IO) {
            log("IO 调度器上的协程: ${Thread.currentThread().name}")
        }
    }

    /**
     * 演示 repeatOnLifecycle
     */
    private fun demonstrateRepeatOnLifecycle() {
        log("\n--- 演示2: repeatOnLifecycle ---")

        // repeatOnLifecycle 会在生命周期达到 STARTED 时执行
        // 低于 STARTED 时取消，再次达到 STARTED 时重新执行
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                log("repeatOnLifecycle: 生命周期达到 STARTED")
                // 这里通常用于收集 Flow
            }
        }
    }

    /**
     * 开始计数
     */
    private fun startCounting() {
        if (countJob != null && countJob?.isActive == true) {
            log("计数已在进行中...")
            return
        }

        log("\n--- 启动计数协程 ---")
        counter = 0

        countJob = lifecycleScope.launch {
            log("计数协程启动")

            while (true) {
                delay(1000)
                counter++
                log("计数: $counter (线程: ${Thread.currentThread().name})")
            }
        }

        log("Job 状态: isActive=${countJob?.isActive}")
    }

    /**
     * 停止计数
     */
    private fun stopCounting() {
        if (countJob == null || countJob?.isActive == false) {
            log("计数未在运行")
            return
        }

        log("\n--- 手动取消计数协程 ---")
        countJob?.cancel()
        log("Job 状态: isCancelled=${countJob?.isCancelled}")
        countJob = null
    }

    override fun onDestroy() {
        super.onDestroy()
        // lifecycleScope 中的协程会自动取消
        log("\n--- Activity onDestroy ---")
        log("lifecycleScope 协程自动取消: isCancelled=${countJob?.isCancelled}")
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
