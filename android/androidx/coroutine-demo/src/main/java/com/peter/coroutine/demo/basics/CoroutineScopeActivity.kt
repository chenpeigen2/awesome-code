package com.peter.coroutine.demo.basics

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * 协程作用域 (CoroutineScope) 演示
 *
 * 本 Activity 展示结构化并发的核心概念：CoroutineScope。
 *
 * ## 什么是 CoroutineScope？
 * CoroutineScope 定义了协程的生命周期范围。
 * 它包含一个 CoroutineContext，决定了协程的执行行为。
 *
 * 每个协程必须在一个 Scope 中启动，当 Scope 被取消时，
 * 其中的所有协程也会被取消——这就是结构化并发。
 *
 * ## 结构化并发原则：
 * 1. 每个协程有一个父级（除了根协程）
 * 2. 父协程会等待所有子协程完成
 * 3. 取消父协程会取消所有子协程
 * 4. 子协程的失败会取消父协程（默认行为）
 *
 * ## 常用的 Scope：
 * - lifecycleScope: Android Lifecycle 相关的 Scope
 * - viewModelScope: ViewModel 相关的 Scope
 * - GlobalScope: 全局 Scope（不推荐使用）
 * - 自定义 Scope: 使用 CoroutineScope() 创建
 *
 * ## 自定义 Scope：
 * ```kotlin
 * val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
 * ```
 *
 * ## SupervisorJob：
 * 使用 SupervisorJob 时，子协程的失败不会影响其他子协程。
 * 适用于需要独立处理错误的场景。
 *
 * @see kotlinx.coroutines.CoroutineScope
 * @see kotlinx.coroutines.Job
 * @see kotlinx.coroutines.SupervisorJob
 */
class CoroutineScopeActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    // 自定义 CoroutineScope
    private lateinit var customScope: CoroutineScope
    private lateinit var supervisorScope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.coroutine_scope)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        // 创建自定义 Scope
        customScope = CoroutineScope(Dispatchers.Default + Job())
        supervisorScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

        demonstrateStructuredConcurrency()
    }

    /**
     * 演示结构化并发
     */
    private fun demonstrateStructuredConcurrency() {
        log("========== 结构化并发演示 ==========\n")

        lifecycleScope.launch {
            // 演示1: 父子协程关系
            demonstrateParentChildRelation()

            delay(500)

            // 演示2: 取消传播
            demonstrateCancellationPropagation()

            delay(500)

            // 演示3: 自定义 Scope
            demonstrateCustomScope()

            delay(500)

            // 演示4: SupervisorJob
            demonstrateSupervisorJob()

            log("\n========== 演示完成 ==========")
        }
    }

    /**
     * 演示父子协程关系
     */
    private suspend fun demonstrateParentChildRelation() {
        log("--- 演示1: 父子协程关系 ---")

        val parentJob = lifecycleScope.launch {
            log("父协程开始: ${Thread.currentThread().name}")

            // 启动子协程
            launch {
                log("  子协程1 开始")
                delay(300)
                log("  子协程1 完成")
            }

            launch {
                log("  子协程2 开始")
                delay(200)
                log("  子协程2 完成")
            }

            // 父协程会等待所有子协程完成
            log("父协程代码执行完毕，等待子协程...")
        }

        log("父协程 Job: $parentJob")
        log("父协程子 Job 数量: ${parentJob.children.toList().size}")

        parentJob.join()
        log("父协程及其所有子协程已完成\n")
    }

    /**
     * 演示取消传播
     */
    private suspend fun demonstrateCancellationPropagation() {
        log("--- 演示2: 取消传播 ---")

        var childJob1: Job? = null
        var childJob2: Job? = null

        val parentJob = lifecycleScope.launch {
            log("父协程开始")

            childJob1 = launch {
                log("  子协程1 开始")
                repeat(10) {
                    delay(100)
                    log("  子协程1 计数: $it")
                }
                log("  子协程1 完成")
            }

            childJob2 = launch {
                log("  子协程2 开始")
                repeat(10) {
                    delay(100)
                    log("  子协程2 计数: $it")
                }
                log("  子协程2 完成")
            }
        }

        delay(350) // 让子协程执行一会儿

        log("\n取消父协程...")
        parentJob.cancel()

        delay(100)

        log("父协程状态 - isCancelled: ${parentJob.isCancelled}")
        log("子协程1 状态 - isCancelled: ${childJob1?.isCancelled}")
        log("子协程2 状态 - isCancelled: ${childJob2?.isCancelled}")
        log("取消会从父协程传播到所有子协程\n")
    }

    /**
     * 演示自定义 Scope
     */
    private suspend fun demonstrateCustomScope() {
        log("--- 演示3: 自定义 Scope ---")

        log("创建自定义 CoroutineScope")
        log("Scope = CoroutineScope(Dispatchers.Default + Job())")

        var job1: Job? = null
        var job2: Job? = null

        // 在自定义 scope 中启动协程
        job1 = customScope.launch {
            log("自定义 Scope 协程1 开始")
            delay(500)
            log("自定义 Scope 协程1 完成")
        }

        job2 = customScope.launch {
            log("自定义 Scope 协程2 开始")
            delay(300)
            log("自定义 Scope 协程2 完成")
        }

        log("协程已启动，等待完成...")

        // 等待协程完成
        job1.join()
        job2.join()

        log("自定义 Scope 中的协程全部完成\n")

        // 取消自定义 Scope 的所有协程
        log("取消自定义 Scope 的所有子协程...")
        customScope.coroutineContext[Job]?.cancelChildren()
    }

    /**
     * 演示 SupervisorJob
     */
    private suspend fun demonstrateSupervisorJob() {
        log("--- 演示4: SupervisorJob ---")

        log("SupervisorJob 特点:")
        log("子协程的失败不会取消其他子协程")

        val job1 = supervisorScope.launch {
            log("  协程1 开始")
            delay(200)
            throw RuntimeException("协程1 故意抛出异常")
        }

        val job2 = supervisorScope.launch {
            log("  协程2 开始")
            delay(400)
            log("  协程2 完成 (不受协程1失败影响)")
        }

        // 等待协程完成（即使有异常）
        try {
            job1.join()
        } catch (e: Exception) {
            log("协程1 异常: ${e.message}")
        }

        job2.join()

        log("\n对比: 普通 Job 情况下，一个子协程失败会取消所有兄弟协程")
        log("SupervisorJob 使子协程相互独立\n")
    }

    /**
     * 清理资源
     */
    override fun onDestroy() {
        super.onDestroy()
        // 取消自定义 Scope
        customScope.cancel()
        supervisorScope.cancel()
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
