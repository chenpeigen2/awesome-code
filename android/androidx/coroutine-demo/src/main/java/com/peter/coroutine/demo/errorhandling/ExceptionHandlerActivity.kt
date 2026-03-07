package com.peter.coroutine.demo.errorhandling

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

/**
 * CoroutineExceptionHandler 全局异常处理器演示
 *
 * 本 Activity 展示如何使用 CoroutineExceptionHandler 处理协程中未捕获的异常。
 *
 * ## 什么是 CoroutineExceptionHandler？
 * CoroutineExceptionHandler 是协程上下文的一个元素，用于处理协程中未捕获的异常。
 * 它类似于线程的 UncaughtExceptionHandler。
 *
 * ## 使用场景
 * - 记录日志
 * - 向用户显示错误信息
 * - 错误监控和上报
 *
 * ## 异常传播机制
 *
 * ### 自动传播的异常
 * - launch 中未捕获的异常会自动传播
 * - 会取消父协程和兄弟协程
 * - 最终由 CoroutineExceptionHandler 处理
 *
 * ### 暴露给用户的异常
 * - async 的异常通过 await() 暴露
 * - 需要显式处理，不会触发 CoroutineExceptionHandler
 *
 * ## 重要限制
 * CoroutineExceptionHandler 只能处理从协程内部自动传播出来的异常：
 * - 只对 launch 有效
 * - async 的异常需要通过 await() 处理
 * - 必须作为根协程或 SupervisorJob 子协程的上下文
 *
 * @see kotlinx.coroutines.CoroutineExceptionHandler
 * @see kotlinx.coroutines.CoroutineContext
 */
class ExceptionHandlerActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private lateinit var tvDescription: TextView
    private lateinit var btnLaunchException: Button
    private lateinit var btnAsyncException: Button
    private lateinit var btnChildException: Button
    private lateinit var btnClearLog: Button

    private val logBuilder = StringBuilder()

    /**
     * 全局异常处理器
     *
     * 当协程中发生未捕获的异常时，会回调此处理器。
     * context: 发生异常的协程上下文
     * exception: 未捕获的异常
     */
    private val exceptionHandler = CoroutineExceptionHandler { context, exception ->
        log("========== CoroutineExceptionHandler 被触发 ==========")
        log("协程上下文: $context")
        log("异常类型: ${exception.javaClass.simpleName}")
        log("异常消息: ${exception.message}")
        log("======================================================\n")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exception_handler)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.exception_handler)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)
        tvDescription = findViewById(R.id.tvDescription)
        btnLaunchException = findViewById(R.id.btnLaunchException)
        btnAsyncException = findViewById(R.id.btnAsyncException)
        btnChildException = findViewById(R.id.btnChildException)
        btnClearLog = findViewById(R.id.btnClearLog)

        setupClickListeners()
        showIntroduction()
    }

    /**
     * 设置按钮点击监听
     */
    private fun setupClickListeners() {
        btnLaunchException.setOnClickListener { triggerLaunchException() }
        btnAsyncException.setOnClickListener { triggerAsyncException() }
        btnChildException.setOnClickListener { triggerChildException() }
        btnClearLog.setOnClickListener { clearLog() }
    }

    /**
     * 显示介绍信息
     */
    private fun showIntroduction() {
        log("========== CoroutineExceptionHandler 演示 ==========")
        log("点击按钮触发不同类型的异常，观察异常处理器的行为\n")
    }

    /**
     * 触发 launch 异常
     *
     * launch 中的未捕获异常会自动传播到 CoroutineExceptionHandler
     */
    private fun triggerLaunchException() {
        log("--- 触发 launch 异常 ---")
        log("启动一个 launch 协程，内部抛出异常...")

        lifecycleScope.launch(exceptionHandler) {
            log("launch: 协程开始执行")
            delay(100)
            throw RuntimeException("launch 抛出的异常!")
        }

        log("launch: 协程已启动，等待异常传播...\n")
    }

    /**
     * 触发 async 异常
     *
     * async 的异常不会触发 CoroutineExceptionHandler，
     * 必须通过 await() 捕获
     */
    private fun triggerAsyncException() {
        log("--- 触发 async 异常 ---")
        log("启动一个 async 协程，内部抛出异常...")

        lifecycleScope.launch(exceptionHandler) {
            val deferred: Deferred<String> = async {
                log("async: 协程开始执行")
                delay(100)
                throw RuntimeException("async 抛出的异常!")
            }

            log("async: 协程已启动")

            try {
                val result = deferred.await()
                log("async: 获取结果 = $result")
            } catch (e: Exception) {
                log("async: await() 捕获到异常 - ${e.message}")
                log("注意: async 的异常不会触发 CoroutineExceptionHandler")
            }
        }

        log("")
    }

    /**
     * 触发子协程异常
     *
     * 演示异常如何影响父协程和兄弟协程
     */
    private fun triggerChildException() {
        log("--- 触发子协程异常 ---")
        log("启动父协程，包含多个子协程...")

        lifecycleScope.launch(exceptionHandler) {
            log("父协程: 开始执行")

            // 启动多个子协程
            launch {
                log("子协程1: 开始执行")
                repeat(5) { i ->
                    delay(200)
                    log("子协程1: 计数 $i")
                }
                log("子协程1: 完成")
            }

            launch {
                log("子协程2: 开始执行，即将抛出异常")
                delay(300)
                throw RuntimeException("子协程2 抛出异常!")
            }

            launch {
                log("子协程3: 开始执行")
                repeat(5) { i ->
                    delay(200)
                    log("子协程3: 计数 $i")
                }
                log("子协程3: 完成")
            }

            log("父协程: 所有子协程已启动")
        }

        log("观察: 异常发生后，父协程和所有子协程都会被取消\n")
    }

    /**
     * 清空日志
     */
    private fun clearLog() {
        logBuilder.clear()
        tvLog.text = ""
        showIntroduction()
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
