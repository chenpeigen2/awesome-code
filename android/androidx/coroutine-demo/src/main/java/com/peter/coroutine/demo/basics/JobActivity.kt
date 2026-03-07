package com.peter.coroutine.demo.basics

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Job 控制演示
 *
 * 本 Activity 展示协程中 Job 的生命周期和控制方法。
 *
 * ## 什么是 Job？
 * Job 代表一个协程的句柄，用于控制协程的生命周期。
 * 每个 launch 返回一个 Job，async 返回 Deferred（继承自 Job）。
 *
 * ## Job 的状态：
 * Job 有以下几种状态：
 * 1. **New**: 新创建，尚未启动
 * 2. **Active**: 活跃状态，正在执行
 * 3. **Completing**: 正在完成（等待子协程）
 * 4. **Completed**: 已完成
 * 5. **Cancelling**: 正在取消
 * 6. **Cancelled**: 已取消
 *
 * 状态查询属性：
 * - isActive: 是否处于活跃状态
 * - isCancelled: 是否已取消
 * - isCompleted: 是否已完成
 *
 * ## Job 的控制方法：
 *
 * ### cancel()
 * - 取消协程的执行
 * - 协程会在下一个挂起点检查取消状态
 * - 需要协程配合（检查 isActive 或使用可取消的挂起函数）
 *
 * ### join()
 * - 挂起当前协程，等待目标 Job 完成
 * - 是一个 suspend 函数
 *
 * ### cancelAndJoin()
 * - 组合操作：取消后等待完成
 *
 * ## 取消的协作性：
 * 协程取消是协作式的，代码需要主动检查取消状态：
 * - 使用 ensureActive() 或 isActive 检查
 * - delay() 等挂起函数会自动检查
 *
 * @see kotlinx.coroutines.Job
 * @see kotlinx.coroutines.CoroutineScope.launch
 */
class JobActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var tvCount: TextView
    private lateinit var tvLog: TextView
    private lateinit var btnStart: Button
    private lateinit var btnCancel: Button
    private lateinit var btnJoin: Button

    private var countingJob: Job? = null
    private var count = 0
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.job)
        toolbar.setNavigationOnClickListener { finish() }

        tvStatus = findViewById(R.id.tvStatus)
        tvCount = findViewById(R.id.tvCount)
        tvLog = findViewById(R.id.tvLog)

        btnStart = findViewById(R.id.btnStart)
        btnCancel = findViewById(R.id.btnCancel)
        btnJoin = findViewById(R.id.btnJoin)

        btnStart.setOnClickListener { startCountingJob() }
        btnCancel.setOnClickListener { cancelJob() }
        btnJoin.setOnClickListener { joinJob() }

        updateJobStatus()
        log("Job 控制演示\n点击\"启动\"开始一个计数协程")
    }

    /**
     * 启动一个计数协程
     */
    private fun startCountingJob() {
        // 如果已有运行中的协程，先取消
        countingJob?.cancel()

        count = 0
        tvCount.text = "0"

        log("\n=== 启动新 Job ===")

        countingJob = lifecycleScope.launch(Dispatchers.Default) {
            log("协程开始执行")
            log("线程: ${Thread.currentThread().name}")

            while (isActive) { // 检查协程是否仍然活跃
                delay(500) // delay 会自动检查取消状态
                count++

                withContext(Dispatchers.Main) {
                    tvCount.text = count.toString()
                }

                // 也可以手动检查
                if (!isActive) {
                    log("检测到取消请求，退出循环")
                    break
                }
            }

            log("协程结束")
        }

        log("Job 已创建: $countingJob")
        updateJobStatus()
    }

    /**
     * 取消协程
     */
    private fun cancelJob() {
        val job = countingJob
        if (job == null) {
            log("\n没有可取消的 Job")
            return
        }

        log("\n=== 取消 Job ===")
        log("调用 cancel()...")

        job.cancel()

        log("cancel() 调用完成")
        log("注意: cancel() 不会立即停止协程")
        log("协程会在下一个挂起点检查并退出")

        updateJobStatus()

        // 延迟更新状态，观察状态变化
        lifecycleScope.launch {
            delay(100)
            updateJobStatus()
        }
    }

    /**
     * 等待协程完成
     */
    private fun joinJob() {
        val job = countingJob
        if (job == null) {
            log("\n没有可 Join 的 Job")
            return
        }

        log("\n=== Join Job ===")
        log("调用 join()，等待协程完成...")

        lifecycleScope.launch {
            job.join() // 这是一个 suspend 函数

            log("join() 返回，协程已完成")
            updateJobStatus()
        }

        updateJobStatus()
    }

    /**
     * 更新 Job 状态显示
     */
    private fun updateJobStatus() {
        val job = countingJob

        val statusText = if (job != null) {
            """
Job: $job

状态:
  - isActive:   ${job.isActive}
  - isCancelled: ${job.isCancelled}
  - isCompleted: ${job.isCompleted}

状态流转:
  New → Active → Completing → Completed
                ↘ Cancelling → Cancelled
            """.trimIndent()
        } else {
            "没有活动的 Job"
        }

        tvStatus.text = statusText
    }

    /**
     * 添加日志
     */
    private fun log(message: String) {
        logBuilder.append(message).append("\n")
        tvLog.text = logBuilder.toString()
    }
}
