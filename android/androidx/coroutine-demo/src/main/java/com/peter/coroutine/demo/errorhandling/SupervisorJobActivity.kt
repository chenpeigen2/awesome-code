package com.peter.coroutine.demo.errorhandling

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

/**
 * SupervisorJob 独立失败演示
 *
 * 本 Activity 展示 SupervisorJob 如何实现子协程的独立失败。
 *
 * ## Job vs SupervisorJob
 *
 * ### 普通 Job 的行为
 * - 子协程失败会取消父协程
 * - 父协程取消会取消所有子协程
 * - 一个子协程失败会影响其他子协程
 *
 * ### SupervisorJob 的行为
 * - 子协程失败不会取消父协程
 * - 子协程失败不会影响其他子协程
 * - 父协程取消仍会取消所有子协程
 * - 适用于需要独立执行的任务组
 *
 * ## supervisorScope
 * supervisorScope 创建一个使用 SupervisorJob 的作用域。
 * 适合在协程内部临时创建独立失败的子作用域。
 *
 * ## 使用场景
 * - 多个独立网络请求（一个失败不影响其他）
 * - 并行数据处理任务
 * - UI 组件的独立更新
 *
 * ## 注意事项
 * 1. SupervisorJob 中的子协程需要自己处理异常
 * 2. 使用 CoroutineExceptionHandler 捕获异常
 * 3. 每个子协程的失败是独立的
 *
 * @see kotlinx.coroutines.SupervisorJob
 * @see kotlinx.coroutines.supervisorScope
 * @see kotlinx.coroutines.Job
 */
class SupervisorJobActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private lateinit var tvStatus: TextView
    private lateinit var btnNormalJob: Button
    private lateinit var btnSupervisorJob: Button
    private lateinit var btnSupervisorScope: Button
    private lateinit var btnClearLog: Button

    private val logBuilder = StringBuilder()

    /**
     * 异常处理器 - 用于 SupervisorJob 中的子协程
     */
    private val exceptionHandler = CoroutineExceptionHandler { context, exception ->
        log("[ExceptionHandler] 捕获异常: ${exception.message}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.supervisor_job)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        setupButtons()
        showIntroduction()
    }

    /**
     * 动态添加按钮到布局
     */
    private fun setupButtons() {
        // 由于使用 activity_log 布局，我们需要在代码中创建按钮区域
        // 这里通过在日志中显示操作说明
        lifecycleScope.launch {
            delay(100)
            log("========== SupervisorJob 演示 ==========")
            log("\n本演示通过代码自动执行三种场景:")
            log("1. 普通 Job - 子协程失败影响其他")
            log("2. SupervisorJob - 子协程独立失败")
            log("3. supervisorScope - 临时独立作用域")
            log("\n演示即将开始...\n")

            delay(1000)
            demonstrateNormalJob()

            delay(2000)
            demonstrateSupervisorJob()

            delay(2000)
            demonstrateSupervisorScope()
        }
    }

    /**
     * 显示介绍信息
     */
    private fun showIntroduction() {
        // 初始日志
    }

    /**
     * 演示普通 Job 的行为
     *
     * 在普通 Job 中，一个子协程的失败会取消所有其他子协程。
     */
    private fun demonstrateNormalJob() {
        log("========== 场景1: 普通 Job ==========")
        log("启动三个子协程，其中一个会失败...\n")

        val job = lifecycleScope.launch(exceptionHandler) {
            // 父协程使用普通 Job（默认）

            launch {
                log("Job-子协程1: 开始执行")
                repeat(3) { i ->
                    delay(300)
                    log("Job-子协程1: 计数 ${i + 1}/3")
                }
                log("Job-子协程1: 完成!")
            }

            launch {
                log("Job-子协程2: 开始执行")
                delay(400)
                log("Job-子协程2: 即将抛出异常!")
                throw RuntimeException("子协程2 失败!")
            }

            launch {
                log("Job-子协程3: 开始执行")
                repeat(3) { i ->
                    delay(300)
                    log("Job-子协程3: 计数 ${i + 1}/3")
                }
                log("Job-子协程3: 完成!")
            }
        }

        // 观察结果
        lifecycleScope.launch {
            delay(1500)
            log("\n观察结果:")
            log("- 子协程2 失败后，子协程1 和 子协程3 被取消")
            log("- 这就是普通 Job 的\"级联取消\"行为\n")
        }
    }

    /**
     * 演示 SupervisorJob 的行为
     *
     * 在 SupervisorJob 中，子协程的失败不会影响其他子协程。
     */
    private fun demonstrateSupervisorJob() {
        log("========== 场景2: SupervisorJob ==========")
        log("启动三个子协程，其中一个会失败...\n")

        // 使用 SupervisorJob 创建根协程
        val supervisorJob = SupervisorJob()
        val scope = lifecycleScope.launch(exceptionHandler + supervisorJob) {

            launch {
                log("Supervisor-子协程1: 开始执行")
                repeat(3) { i ->
                    delay(300)
                    log("Supervisor-子协程1: 计数 ${i + 1}/3")
                }
                log("Supervisor-子协程1: 完成!")
            }

            launch {
                log("Supervisor-子协程2: 开始执行")
                delay(400)
                log("Supervisor-子协程2: 即将抛出异常!")
                throw RuntimeException("子协程2 失败!")
            }

            launch {
                log("Supervisor-子协程3: 开始执行")
                repeat(3) { i ->
                    delay(300)
                    log("Supervisor-子协程3: 计数 ${i + 1}/3")
                }
                log("Supervisor-子协程3: 完成!")
            }
        }

        // 观察结果
        lifecycleScope.launch {
            delay(1500)
            log("\n观察结果:")
            log("- 子协程2 失败后，子协程1 和 子协程3 继续执行")
            log("- 这就是 SupervisorJob 的\"独立失败\"行为\n")

            // 清理
            supervisorJob.cancel()
        }
    }

    /**
     * 演示 supervisorScope 的使用
     *
     * supervisorScope 创建一个临时的独立失败作用域。
     * 适合在协程内部需要隔离失败的场景。
     */
    private fun demonstrateSupervisorScope() {
        log("========== 场景3: supervisorScope ==========")
        log("使用 supervisorScope 创建临时独立作用域...\n")

        lifecycleScope.launch(exceptionHandler) {
            log("外层协程: 开始执行")

            // 使用 supervisorScope 创建独立失败的作用域
            supervisorScope {
                log("supervisorScope: 进入作用域")

                launch {
                    log("Scope-任务A: 开始")
                    repeat(3) { i ->
                        delay(300)
                        log("Scope-任务A: 步骤 ${i + 1}/3")
                    }
                    log("Scope-任务A: 完成!")
                }

                launch {
                    log("Scope-任务B: 开始")
                    delay(400)
                    log("Scope-任务B: 失败!")
                    throw RuntimeException("任务B 失败!")
                }

                launch {
                    log("Scope-任务C: 开始")
                    repeat(3) { i ->
                        delay(300)
                        log("Scope-任务C: 步骤 ${i + 1}/3")
                    }
                    log("Scope-任务C: 完成!")
                }
            }

            log("\nsupervisorScope: 所有任务结束（无论成功或失败）")
            log("外层协程: 继续执行")
        }

        // 观察结果
        lifecycleScope.launch {
            delay(1500)
            log("\n观察结果:")
            log("- 任务B 失败不影响任务A和C")
            log("- supervisorScope 等待所有子协程完成")
            log("- 外层协程在 supervisorScope 返回后继续执行\n")

            delay(500)
            showSummary()
        }
    }

    /**
     * 显示总结
     */
    private fun showSummary() {
        log("========== 总结 ==========")
        log("")
        log("Job vs SupervisorJob 对比:")
        log("")
        log("普通 Job:")
        log("  - 子失败 -> 取消父和兄弟")
        log("  - 适合相互依赖的任务")
        log("")
        log("SupervisorJob:")
        log("  - 子失败 -> 不影响其他")
        log("  - 适合独立执行的任务")
        log("")
        log("supervisorScope:")
        log("  - 临时创建独立作用域")
        log("  - 等待所有子协程完成")
        log("  - 适合协程内部的隔离场景")
        log("")
        log("========== 演示完成 ==========")
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
