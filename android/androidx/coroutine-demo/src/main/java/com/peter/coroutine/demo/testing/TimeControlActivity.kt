package com.peter.coroutine.demo.testing

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 时间控制演示 Activity
 *
 * 本 Activity 展示协程测试中的时间控制概念。
 *
 * ## 虚拟时间
 * 在测试中，我们可以使用虚拟时间来控制协程的执行。
 * 这样可以：
 * - 跳过长时间等待
 * - 测试定时任务
 * - 验证超时逻辑
 *
 * ## 时间控制 API
 * 1. **advanceTimeBy(duration)**
 *    - 将虚拟时间推进指定的时长
 *    - 会执行在这段时间内应该运行的协程
 *
 * 2. **advanceUntilIdle()**
 *    - 推进时间直到所有协程完成
 *    - 等待所有待处理的任务
 *
 * 3. **runCurrent()**
 *    - 执行所有已准备好的协程
 *    - 不推进时间
 *
 * ## 使用示例
 * ```kotlin
 * @Test
 * fun testVirtualTime() = runTest {
 *     val results = mutableListOf<String>()
 *
 *     launch {
 *         results.add("Start")
 *         delay(1000)
 *         results.add("After 1s")
 *     }
 *
 *     testScheduler.advanceTimeBy(500)
 *     assertEquals(listOf("Start"), results)
 *
 *     testScheduler.advanceTimeBy(500)
 *     assertEquals(listOf("Start", "After 1s"), results)
 * }
 * ```
 *
 * @see kotlinx.coroutines.test.TestScheduler
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TimeControlActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.time_control)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateTimeControl()
    }

    /**
     * 演示时间控制
     */
    private fun demonstrateTimeControl() {
        log("========== 时间控制演示 ==========\n")

        demonstrateRealTimeDelay()
    }

    /**
     * 演示真实时间延迟
     *
     * 在生产代码中，delay 会真正等待指定的时间。
     * 这在测试中可能导致测试运行缓慢。
     */
    private fun demonstrateRealTimeDelay() {
        log("--- 1. 真实时间延迟 ---")

        val startTime = System.currentTimeMillis()

        lifecycleScope.launch {
            log("开始延迟...")
            delay(100) // 真实等待 100ms
            val elapsed = System.currentTimeMillis() - startTime
            log("延迟结束，实际耗时: ${elapsed}ms")
            log("")

            // 继续后续演示
            demonstrateVirtualTime()
        }
    }

    /**
     * 演示虚拟时间的概念
     */
    private fun demonstrateVirtualTime() {
        log("--- 2. 虚拟时间概念 ---")

        log("在测试中，使用虚拟时间可以:")
        log("1. 跳过长时间等待")
        log("   delay(60000) // 1 分钟")
        log("   在测试中瞬间完成")
        log("")
        log("2. 精确控制时间推进")
        log("   testScheduler.advanceTimeBy(1000)")
        log("")
        log("3. 测试定时任务")
        log("   可以验证定时器是否在正确的时间触发")
        log("")

        demonstrateTimeControlAPIs()
    }

    /**
     * 演示时间控制 API
     */
    private fun demonstrateTimeControlAPIs() {
        log("--- 3. 时间控制 API ---")

        log("runTest {")
        log("    // testScheduler 提供时间控制")
        log("")
        log("    // 1. advanceTimeBy(duration)")
        log("    // 推进虚拟时间")
        log("    testScheduler.advanceTimeBy(1000)")
        log("")
        log("    // 2. advanceUntilIdle()")
        log("    // 等待所有协程完成")
        log("    testScheduler.advanceUntilIdle()")
        log("")
        log("    // 3. runCurrent()")
        log("    // 执行当前待执行的任务")
        log("    testScheduler.runCurrent()")
        log("")
        log("    // 4. currentTime")
        log("    // 获取当前虚拟时间")
        log("    val time = testScheduler.currentTime")
        log("}")

        // 等待前面的协程完成
        lifecycleScope.launch {
            delay(300)
            log("\n========== 演示完成 ==========")
            log("\n示例代码 - 测试延迟逻辑:")
            log("```kotlin")
            log("@Test")
            log("fun testDelay() = runTest {")
            log("    var result = 0")
            log("    launch {")
            log("        delay(1000)")
            log("        result = 42")
            log("    }")
            log("    // runTest 会自动推进时间")
            log("    assertEquals(42, result)")
            log("}")
            log("```")
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
