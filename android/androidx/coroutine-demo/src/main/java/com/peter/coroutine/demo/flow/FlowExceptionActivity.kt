package com.peter.coroutine.demo.flow

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch

/**
 * FlowExceptionActivity - Flow 异常处理
 *
 * 学习目标：
 * 1. catch 操作符捕获上游异常
 * 2. onCompletion 完成回调
 * 3. retry / retryWhen 重试策略
 * 4. 对比 try-catch vs catch 操作符
 */
class FlowExceptionActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.flow_exception)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)
        demonstrateFlowExceptions()
    }

    private fun demonstrateFlowExceptions() {
        log("========== Flow 异常处理演示 ==========\n")

        lifecycleScope.launch {
            demonstrateTryCatchVsCatch()
            demonstrateCatchOperator()
            demonstrateOnCompletion()
            demonstrateRetry()
            demonstrateRetryWhen()

            log("\n========== 演示完成 ==========")
        }
    }

    /**
     * 1. try-catch vs catch 操作符
     * try-catch 只能捕获终端操作符（collect）的异常
     * catch 操作符可以捕获上游（emit 之前）的异常
     */
    private suspend fun demonstrateTryCatchVsCatch() {
        log("━━━ 1. try-catch vs catch 操作符 ━━━\n")

        // try-catch 方式 — 能捕获，但不优雅
        log("【try-catch 方式】")
        try {
            val errorFlow = flow {
                emit(1)
                emit(2)
                throw RuntimeException("emit 时出错!")
            }
            errorFlow.collect { value ->
                log("  收集到: $value")
            }
        } catch (e: Exception) {
            log("  try-catch 捕获: ${e.message}")
        }

        log("")

        // catch 操作符方式 — 推荐
        log("【catch 操作符方式】")
        val errorFlow = flow {
            emit(1)
            emit(2)
            throw RuntimeException("emit 时出错!")
        }
        errorFlow
            .catch { e -> log("  catch 捕获: ${e.message}") }
            .collect { value ->
                log("  收集到: $value")
            }

        log("\n")
    }

    /**
     * 2. catch 操作符详解
     * catch 只能捕获上游的异常，不能捕获下游 collect 中的异常
     * catch 中可以 emit 新的值来替代异常
     */
    private suspend fun demonstrateCatchOperator() {
        log("━━━ 2. catch 操作符详解 ━━━\n")

        // catch 中 emit 替代值
        log("【catch 中 emit 替代值】")
        val riskyFlow = flow {
            emit("数据1")
            emit("数据2")
            throw IllegalStateException("服务器错误!")
        }
        riskyFlow
            .catch { e ->
                log("  捕获异常: ${e.message}")
                emit("默认数据（降级）")
            }
            .collect { value ->
                log("  收集到: $value")
            }

        log("\n")
    }

    /**
     * 3. onCompletion 完成回调
     * 类似 try-catch-finally 中的 finally
     * Flow 正常完成或异常退出都会调用
     * 可以通过参数判断是否因异常结束
     */
    private suspend fun demonstrateOnCompletion() {
        log("━━━ 3. onCompletion 完成回调 ━━━\n")

        // 正常完成
        log("【正常完成】")
        flow {
            emit("A")
            emit("B")
            emit("C")
        }
            .onCompletion { cause ->
                if (cause == null) {
                    log("  onCompletion: 正常完成 ✓")
                } else {
                    log("  onCompletion: 异常完成 ✗ (${cause.message})")
                }
            }
            .collect { log("  收集到: $it") }

        log("")

        // 异常完成
        log("【异常完成】")
        flow {
            emit(1)
            emit(2)
            throw RuntimeException("出错了!")
        }
            .onCompletion { cause ->
                if (cause == null) {
                    log("  onCompletion: 正常完成 ✓")
                } else {
                    log("  onCompletion: 异常完成 ✗ (${cause.message})")
                }
            }
            .catch { e -> log("  catch: ${e.message}") }
            .collect { log("  收集到: $it") }

        // 典型用法：加载状态管理
        log("\n【典型用法：加载状态管理】")
        log("  // 网络请求时：")
        log("  // flow { emit(api.fetchData()) }")
        log("  //     .onStart { showLoading() }")
        log("  //     .onCompletion { hideLoading() }")
        log("  //     .catch { showError(it) }")
        log("  //     .collect { showData(it) }")

        log("\n")
    }

    /**
     * 4. retry 重试
     * 发生异常时自动重新订阅上游 Flow
     */
    private suspend fun demonstrateRetry() {
        log("━━━ 4. retry 重试 ━━━\n")

        var attempt = 0

        log("【retry(3) — 最多重试 3 次】")
        flow {
            attempt++
            log("  尝试第 $attempt 次...")
            if (attempt < 3) {
                throw RuntimeException("第 $attempt 次失败")
            }
            emit("成功! (第 $attempt 次)")
        }
            .retry(3) { cause ->
                log("  重试原因: ${cause.message}")
                true // 返回 true 继续重试，false 停止
            }
            .catch { e -> log("  最终失败: ${e.message}") }
            .collect { log("  结果: $it") }

        log("\n")
    }

    /**
     * 5. retryWhen — 条件重试
     * 可以根据异常类型和重试次数决定是否重试
     */
    private suspend fun demonstrateRetryWhen() {
        log("━━━ 5. retryWhen 条件重试 ━━━\n")

        var attempt = 0

        log("【retryWhen — 根据异常类型决定是否重试】")
        flow {
            attempt++
            log("  尝试第 $attempt 次...")
            when (attempt) {
                1 -> throw java.io.IOException("网络超时") // 可重试
                2 -> throw SecurityException("权限不足") // 不可重试
                else -> emit("成功!")
            }
        }
            .retryWhen { cause, attempt ->
                when {
                    cause is java.io.IOException && attempt < 3 -> {
                        log("  网络异常，重试 (第 ${attempt + 1} 次)")
                        delay(500)
                        true
                    }
                    else -> {
                        log("  不可重试异常: ${cause::class.simpleName}")
                        false
                    }
                }
            }
            .catch { e -> log("  最终失败: ${e.message}") }
            .collect { log("  结果: $it") }

        log("\n")
    }

    private fun log(message: String) {
        runOnUiThread {
            logBuilder.append(message).append("\n")
            tvLog.text = logBuilder.toString()
        }
    }
}
