package com.peter.coroutine.demo.advanced

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Continuation 原理演示
 *
 * 本 Activity 深入讲解 Kotlin 协程的核心机制：Continuation（续体）
 *
 * ## 什么是 Continuation？
 * Continuation 是协程挂起和恢复的核心接口，它封装了协程在挂起点的状态，
 * 以及挂起后恢复执行所需的上下文信息。
 *
 * ## Continuation 接口定义：
 * ```kotlin
 * public interface Continuation<in T> {
 *     // 协程上下文，包含 Job、Dispatcher 等信息
 *     public val context: CoroutineContext
 *
 *     // 恢复协程执行，传入挂起点的返回值或结果
 *     public fun resumeWith(result: Result<T>)
 * }
 * ```
 *
 * ## 挂起函数的编译器转换：
 * 当编译器遇到 suspend 函数时，会自动添加一个 Continuation 参数：
 * - 源码: `suspend fun foo(): String`
 * - 编译后: `fun foo(continuation: Continuation<String>): Any?`
 *
 * 返回类型 Any? 表示三种可能：
 * 1. `COROUTINE_SUSPENDED` - 协程已挂起
 * 2. 实际返回值 - 协程正常完成
 * 3. 异常 - 协程失败
 *
 * ## 挂起和恢复过程：
 * 1. **挂起**: 当协程遇到挂起点（如 delay），它会保存当前状态到 Continuation
 * 2. **等待**: 协程释放线程，其他代码可以执行
 * 3. **恢复**: 当条件满足时，调用 Continuation.resumeWith() 恢复执行
 *
 * @see kotlin.coroutines.Continuation
 * @see kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
 */
class ContinuationActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.continuation)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateContinuation()
    }

    /**
     * 演示 Continuation 的挂起和恢复机制
     */
    private fun demonstrateContinuation() {
        log("========== Continuation 原理演示 ==========\n")

        // 演示1: 理解挂起点的概念
        log("--- 演示1: 挂起点与恢复 ---")
        lifecycleScope.launch {
            log("协程开始执行")
            log("即将到达第一个挂起点: delay(500)")
            log("此时 Continuation 会保存当前状态\n")

            // 第一个挂起点
            delay(500)

            log("从第一个挂起点恢复")
            log("Continuation 恢复了之前的执行状态\n")

            // 第二个挂起点
            log("即将到达第二个挂起点: fetchData()")
            delay(300)

            log("从第二个挂起点恢复\n")
            log("========== 核心概念总结 ==========\n")

            printContinuationSummary()
        }
    }

    /**
     * 打印 Continuation 核心概念总结
     */
    private fun printContinuationSummary() {
        log("Continuation 接口核心成员:\n")

        log("1. context: CoroutineContext")
        log("   - 包含 Job、Dispatcher、CoroutineName 等")
        log("   - 协程运行所需的所有上下文信息\n")

        log("2. resumeWith(result: Result<T>)")
        log("   - 恢复协程执行")
        log("   - result 可以是成功值或异常\n")

        log("3. 扩展函数:")
        log("   - resume(value: T) - 正常恢复")
        log("   - resumeWithException(exception: Throwable) - 异常恢复\n")

        log("编译器如何处理 suspend 函数:\n")

        log("源码:")
        log("  suspend fun getData(): String {")
        log("      delay(1000)")
        log("      return \"data\"")
        log("  }\n")

        log("编译后（伪代码）:")
        log("  fun getData(cont: Continuation<String>): Any? {")
        log("      // 状态机逻辑")
        log("      // 返回 COROUTINE_SUSPENDED 或实际值")
        log("  }\n")

        log("==========================================\n")
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
