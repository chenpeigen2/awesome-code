package com.peter.coroutine.demo.testing

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * TestDispatcher 演示 Activity
 *
 * 本 Activity 展示协程测试中 TestDispatcher 的概念和使用场景。
 *
 * ## 为什么需要 TestDispatcher？
 * 在测试协程代码时，我们需要：
 * 1. 控制协程的执行时机
 * 2. 控制虚拟时间，跳过 delay
 * 3. 使测试可重复和可预测
 *
 * ## TestDispatcher 类型
 * 1. **StandardTestDispatcher**
 *    - 协程不会立即执行
 *    - 需要手动推进时间或等待
 *    - 适合测试协程的时序行为
 *
 * 2. **UnconfinedTestDispatcher**
 *    - 协程会立即执行（类似 Dispatchers.Unconfined）
 *    - 适合简单的测试场景
 *
 * ## 在实际测试中的使用
 * ```kotlin
 * @Test
 * fun testWithStandardDispatcher() = runTest {
 *     val dispatcher = StandardTestDispatcher(testScheduler)
 *     // 使用 dispatcher...
 * }
 * ```
 *
 * @see kotlinx.coroutines.test.StandardTestDispatcher
 * @see kotlinx.coroutines.test.UnconfinedTestDispatcher
 * @see kotlinx.coroutines.test.runTest
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatcherActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.test_dispatcher)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateTestDispatcherConcepts()
    }

    /**
     * 演示 TestDispatcher 的核心概念
     */
    private fun demonstrateTestDispatcherConcepts() {
        log("========== TestDispatcher 演示 ==========\n")

        // 在实际应用中，我们使用真实的 Dispatcher
        // 这里演示 TestDispatcher 解决的问题

        demonstrateRealDispatcherBehavior()
        demonstrateDispatcherInjection()
        demonstrateTestDispatcherBenefits()
    }

    /**
     * 演示真实 Dispatcher 的行为
     *
     * 在生产代码中，我们使用 Dispatchers.IO、Dispatchers.Main 等。
     * 这些 Dispatcher 的行为是异步的，难以在测试中控制。
     */
    private fun demonstrateRealDispatcherBehavior() {
        log("--- 1. 真实 Dispatcher 的行为 ---")

        lifecycleScope.launch {
            log("协程启动")

            // 切换到 IO 线程
            withContext(Dispatchers.IO) {
                log("在 IO 线程执行: ${Thread.currentThread().name}")
                delay(100)
                "IO 结果"
            }.also {
                log("IO 操作完成: $it")
            }

            log("协程结束\n")
        }
    }

    /**
     * 演示 Dispatcher 注入
     *
     * 为了使代码可测试，我们通常将 Dispatcher 作为依赖注入。
     * 这样在测试时可以替换为 TestDispatcher。
     */
    private fun demonstrateDispatcherInjection() {
        log("--- 2. Dispatcher 注入模式 ---")

        // 示例：一个接受 Dispatcher 的类
        class DataFetcher(
            private val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.IO
        ) {
            suspend fun fetchData(): String {
                return withContext(ioDispatcher) {
                    delay(100)
                    "Data from network"
                }
            }
        }

        lifecycleScope.launch {
            // 生产环境使用真实的 Dispatcher
            val fetcher = DataFetcher()
            val result = fetcher.fetchData()
            log("获取数据: $result")

            // 测试时可以注入 TestDispatcher
            log("在测试中，可以注入 TestDispatcher:")
            log("  val testDispatcher = StandardTestDispatcher()")
            log("  val fetcher = DataFetcher(testDispatcher)")
            log("  这样 delay 不会真正等待\n")
        }
    }

    /**
     * 演示 TestDispatcher 的好处
     */
    private fun demonstrateTestDispatcherBenefits() {
        log("--- 3. TestDispatcher 的好处 ---")

        log("使用 TestDispatcher 的优势:")
        log("1. 虚拟时间控制")
        log("   - delay(1000) 不会真正等待 1 秒")
        log("   - 测试可以瞬间完成")
        log("")
        log("2. 可重复性")
        log("   - 协程执行顺序可预测")
        log("   - 避免竞态条件")
        log("")
        log("3. 时间操作")
        log("   - 可以手动推进时间: advanceTimeBy(100)")
        log("   - 可以运行当前任务: runCurrent()")
        log("   - 可以等待所有任务: advanceUntilIdle()")
        log("")

        // 等待前面的协程完成
        lifecycleScope.launch {
            delay(200)
            log("\n========== 演示完成 ==========")
            log("\n提示: 查看测试文件了解 TestDispatcher 的实际使用:")
            log("  coroutine-demo/src/test/java/com/peter/coroutine/demo/testing/")
        }
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
