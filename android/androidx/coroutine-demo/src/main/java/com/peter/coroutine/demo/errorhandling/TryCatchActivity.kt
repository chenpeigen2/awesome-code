package com.peter.coroutine.demo.errorhandling

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 协程异常捕获演示 - try/catch
 *
 * 本 Activity 展示协程中 try/catch 的使用方法和注意事项。
 *
 * ## 协程异常处理的核心概念
 *
 * ### launch 的异常处理
 * - launch 中未捕获的异常会立即传播到父协程
 * - 可以直接在协程体内使用 try/catch
 * - 异常如果不处理会导致整个协程树取消
 *
 * ### async 的异常处理
 * - async 的异常不会立即抛出，而是存储在 Deferred 中
 * - 只有在调用 await() 时才会抛出异常
 * - 可以在 await() 处使用 try/catch 捕获
 *
 * ## try/catch 的限制
 * try/catch 只能捕获协程内部代码的异常，不能捕获协程启动失败等外部异常。
 * 这是因为协程是异步执行的，try 块可能在协程开始执行前就已经结束。
 *
 * ## 最佳实践
 * 1. 对于 launch：在协程内部使用 try/catch 或使用 CoroutineExceptionHandler
 * 2. 对于 async：在 await() 处使用 try/catch
 * 3. 避免在外部包裹整个 launch/async 调用
 *
 * @see kotlinx.coroutines.launch
 * @see kotlinx.coroutines.async
 * @see kotlinx.coroutines.Deferred.await
 */
class TryCatchActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.try_catch)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateTryCatch()
    }

    /**
     * 演示 try/catch 在协程中的使用
     */
    private fun demonstrateTryCatch() {
        log("========== try/catch 异常捕获演示 ==========\n")

        lifecycleScope.launch {
            // 演示1: launch 中直接捕获异常
            demonstrateLaunchWithTryCatch()

            delay(500)

            // 演示2: async 在 await 时捕获异常
            demonstrateAsyncWithTryCatch()

            delay(500)

            // 演示3: try/catch 无法捕获外部异常
            demonstrateExternalTryCatch()

            delay(500)

            // 演示4: 协程内部 try/catch vs 外部 try/catch
            demonstrateInternalVsExternal()

            log("\n========== 演示完成 ==========")
        }
    }

    /**
     * 演示 launch 中使用 try/catch
     *
     * 在 launch 协程内部使用 try/catch 可以有效捕获异常，
     * 防止异常传播到父协程导致整个协程树取消。
     */
    private suspend fun demonstrateLaunchWithTryCatch() = coroutineScope {
        log("--- 演示1: launch 内部 try/catch ---")

        launch {
            try {
                log("launch: 开始执行可能抛出异常的代码")
                delay(100)
                throw RuntimeException("launch 内部异常!")
            } catch (e: Exception) {
                log("launch: 捕获到异常 - ${e.message}")
            }
            log("launch: 异常处理后继续执行")
        }.join()

        log("launch: 协程正常完成，异常被内部捕获\n")
    }

    /**
     * 演示 async 在 await 时捕获异常
     *
     * async 的异常不会立即抛出，而是保存在 Deferred 中。
     * 调用 await() 时才会抛出异常，因此需要在这里捕获。
     */
    private suspend fun demonstrateAsyncWithTryCatch() = coroutineScope {
        log("--- 演示2: async 在 await 时捕获异常 ---")

        val deferred: Deferred<String> = async {
            log("async: 开始执行")
            delay(100)
            throw RuntimeException("async 内部异常!")
            // 不会执行到这里
            "Result"
        }

        log("async: 协程已启动，准备 await...")

        try {
            val result = deferred.await()
            log("async: 获取结果 = $result")
        } catch (e: Exception) {
            log("async: await 时捕获到异常 - ${e.message}")
        }

        log("async: 异常处理后继续执行\n")
    }

    /**
     * 演示外部 try/catch 的限制
     *
     * 重要：在 launch/async 外部包裹 try/catch 是无效的！
     * 因为协程是异步执行的，try 块在协程开始前就结束了。
     */
    private suspend fun demonstrateExternalTryCatch() = coroutineScope {
        log("--- 演示3: 外部 try/catch 的限制 ---")

        log("错误示例: 在 launch 外部使用 try/catch")

        try {
            launch {
                delay(100)
                throw RuntimeException("外部 try/catch 无法捕获这个异常!")
            }
            log("launch 已启动（但 try 块立即结束）")
        } catch (e: Exception) {
            // 这里永远不会被调用！
            log("外部 catch: 捕获到异常 - ${e.message}")
        }

        log("注意: 上面的 try/catch 无法捕获协程内部的异常")
        log("因为协程是异步执行的，try 块已经结束了")

        // 等待上面的协程异常
        delay(200)

        // 正确做法：使用 CoroutineExceptionHandler 或内部 try/catch
        log("\n正确做法: 在协程内部使用 try/catch")
        launch {
            try {
                delay(50)
                throw RuntimeException("内部异常被正确捕获!")
            } catch (e: Exception) {
                log("内部 catch: ${e.message}")
            }
        }.join()

        log("")
    }

    /**
     * 演示内部 vs 外部 try/catch 的对比
     *
     * 展示为什么内部 try/catch 才是正确的方式。
     */
    private suspend fun demonstrateInternalVsExternal() = coroutineScope {
        log("--- 演示4: 内部 vs 外部 try/catch ---")

        // 场景1: 外部 try/catch (无效)
        log("场景1: 外部包裹 try/catch")
        var caught = false
        try {
            async {
                delay(50)
                throw RuntimeException("async 异常")
            }
        } catch (e: Exception) {
            caught = true
            log("外部捕获: ${e.message}")
        }
        log("外部 catch 是否执行: $caught (应该为 false)")

        delay(100)

        // 场景2: 在 await 处 try/catch (有效)
        log("\n场景2: 在 await 处 try/catch")
        val deferred = async {
            delay(50)
            throw RuntimeException("await 异常")
        }
        try {
            deferred.await()
        } catch (e: Exception) {
            log("await 处捕获: ${e.message}")
        }

        log("\n结论:")
        log("- launch: 在协程内部使用 try/catch")
        log("- async: 在 await() 处使用 try/catch")
        log("- 或使用 CoroutineExceptionHandler 处理未捕获异常")
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
