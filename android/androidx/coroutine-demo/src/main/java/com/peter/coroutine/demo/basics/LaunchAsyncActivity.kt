package com.peter.coroutine.demo.basics

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Launch vs Async 对比演示
 *
 * 本 Activity 展示协程构建器 launch 和 async 的区别和使用场景。
 *
 * ## launch - 启动协程（"发射后不管"）
 * - 返回 Job 对象
 * - 用于执行不需要返回结果的任务
 * - 类似于启动一个新线程但不等待结果
 * - 适用于: 日志记录、UI 更新、事件处理等
 *
 * ## async - 异步计算（"启动并等待结果"）
 * - 返回 Deferred<T> 对象（一个未来的结果）
 * - 用于需要返回值的异步计算
 * - 通过 await() 获取结果
 * - 适用于: 并行计算、网络请求、数据库查询等
 *
 * ## 并行执行
 * 使用 async 可以轻松实现多个任务的并行执行，
 * 然后通过 await() 按需获取结果。
 *
 * ## 选择原则：
 * - 如果需要结果：使用 async
 * - 如果只需要执行：使用 launch
 *
 * @see kotlinx.coroutines.launch
 * @see kotlinx.coroutines.async
 * @see kotlinx.coroutines.Job
 * @see kotlinx.coroutines.Deferred
 */
class LaunchAsyncActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.launch_async)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateLaunchVsAsync()
    }

    /**
     * 演示 launch 和 async 的区别
     */
    private fun demonstrateLaunchVsAsync() {
        log("========== Launch vs Async 演示 ==========\n")

        lifecycleScope.launch {
            // 演示1: launch 返回 Job
            demonstrateLaunch()

            delay(500)

            // 演示2: async 返回 Deferred
            demonstrateAsync()

            delay(500)

            // 演示3: 并行执行
            demonstrateParallelExecution()

            log("\n========== 演示完成 ==========")
        }
    }

    /**
     * 演示 launch 的使用
     *
     * launch 启动一个新协程，返回 Job 对象。
     * Job 可以用于控制协程的生命周期（取消、等待完成等）。
     */
    private suspend fun demonstrateLaunch() = coroutineScope {
        log("--- 演示1: launch 返回 Job ---")

        val job: Job = launch {
            log("launch: 协程开始执行")
            delay(500)
            log("launch: 协程执行完毕")
            // launch 无法直接返回结果
        }

        log("launch: 返回的 Job = $job")
        log("launch: Job 状态 - isActive=${job.isActive}, isCompleted=${job.isCompleted}")

        // 等待 job 完成
        job.join() // join() 会挂起直到协程完成
        log("launch: Job 状态 - isActive=${job.isActive}, isCompleted=${job.isCompleted}\n")
    }

    /**
     * 演示 async 的使用
     *
     * async 启动一个协程并返回 Deferred<T>，
     * Deferred 是一个带有结果的 Job。
     */
    private suspend fun demonstrateAsync() = coroutineScope {
        log("--- 演示2: async 返回 Deferred ---")

        val deferred: Deferred<String> = async {
            log("async: 开始计算...")
            delay(500)
            log("async: 计算完成")
            return@async "计算结果: 42" // 返回结果
        }

        log("async: 返回的 Deferred = $deferred")
        log("async: 等待结果...")

        // await() 会挂起直到结果可用
        val result = deferred.await()
        log("async: 获取到结果 = $result")
        log("async: Deferred 状态 - isCompleted=${deferred.isCompleted}\n")
    }

    /**
     * 演示并行执行
     *
     * 使用 async 可以同时启动多个任务，
     * 它们会并行执行，然后通过 await() 获取结果。
     */
    private suspend fun demonstrateParallelExecution() = coroutineScope {
        log("--- 演示3: 并行执行 ---")

        val startTime = System.currentTimeMillis()

        // 同时启动三个异步任务
        log("同时启动三个任务...")

        val deferred1 = async {
            log("任务1: 开始 (模拟耗时 800ms)")
            delay(800)
            log("任务1: 完成")
            "Result-1"
        }

        val deferred2 = async {
            log("任务2: 开始 (模拟耗时 600ms)")
            delay(600)
            log("任务2: 完成")
            "Result-2"
        }

        val deferred3 = async {
            log("任务3: 开始 (模拟耗时 1000ms)")
            delay(1000)
            log("任务3: 完成")
            "Result-3"
        }

        // 等待所有结果
        log("等待所有结果...")
        val result1 = deferred1.await()
        val result2 = deferred2.await()
        val result3 = deferred3.await()

        val totalTime = System.currentTimeMillis() - startTime

        log("所有任务完成!")
        log("结果: $result1, $result2, $result3")
        log("总耗时: ${totalTime}ms")
        log("(如果顺序执行需要 ~2400ms，并行只需 ~1000ms)")
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
