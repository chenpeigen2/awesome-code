package com.peter.anr.demo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.peter.anr.demo.databinding.ActivityInputAnrBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 输入事件 ANR 场景演示
 *
 * 本页面演示四种与输入事件相关的 ANR 场景：
 *
 * 【错误示范】
 * 1. Thread.sleep 阻塞
 *    - 在主线程调用 Thread.sleep(10_000)
 *    - 直接阻塞主线程 10 秒，超过 5 秒阈值触发 ANR
 *
 * 2. 死循环
 *    - 主线程进入 while(true) 无限循环
 *    - 主线程永远无法处理其他消息，必然 ANR
 *
 * 3. 死锁
 *    - 两个线程互相持有对方需要的锁
 *    - 如果其中一个线程涉及主线程等待，会触发 ANR
 *
 * 【正确方式】
 * 4. 使用协程
 *    - 将耗时操作放到 IO 线程执行
 *    - 通过 withContext 切换回主线程更新 UI
 *    - 不会阻塞主线程，不会触发 ANR
 *
 * 注意：前三种操作会真实触发 ANR，请在调试设备上使用！
 */
class InputAnrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputAnrBinding
    private val infoSb = StringBuilder()
    private val resultSb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputAnrBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupChips()
        showOverview()
    }

    /**
     * 设置 Chip 按钮点击事件
     *
     * 四个 Chip 分别对应：
     * - btnSleep: Thread.sleep 阻塞主线程
     * - btnLoop: 死循环阻塞主线程
     * - btnDeadlock: synchronized 死锁演示
     * - btnCorrect: 使用协程的正确方式
     */
    private fun setupChips() {
        // Thread.sleep 阻塞
        binding.btnSleep.setOnClickListener {
            demonstrateSleep()
        }

        // 死循环
        binding.btnLoop.setOnClickListener {
            demonstrateLoop()
        }

        // 死锁
        binding.btnDeadlock.setOnClickListener {
            demonstrateDeadlock()
        }

        // 正确方式（协程）
        binding.btnCorrect.setOnClickListener {
            demonstrateCorrect()
        }
    }

    /**
     * 显示概览信息
     *
     * 页面打开时展示 Input Dispatching Timeout 的基本说明，
     * 包括超时阈值、触发条件和常见原因。
     */
    private fun showOverview() {
        infoSb.clear()
        infoSb.appendLine("=== 输入事件 ANR ===")
        infoSb.appendLine()
        infoSb.appendLine("Input Dispatching Timeout")
        infoSb.appendLine("超时阈值: 5 秒")
        infoSb.appendLine("触发条件: 主线程无法在 5 秒内响应输入事件")
        infoSb.appendLine()
        infoSb.appendLine("常见原因:")
        infoSb.appendLine("  • 主线程 Thread.sleep()")
        infoSb.appendLine("  • 主线程死循环")
        infoSb.appendLine("  • 主线程死锁等待")
        infoSb.appendLine("  • 主线程执行耗时 I/O 操作")
        infoSb.appendLine("  • 主线程进行大量计算")
        infoSb.appendLine()
        infoSb.appendLine("点击上方按钮查看具体演示")

        binding.tvInfo.text = infoSb.toString()
    }

    /**
     * 演示 Thread.sleep 导致的 ANR
     *
     * 在主线程直接调用 Thread.sleep(10_000) 会阻塞主线程 10 秒，
     * 远超 Input Dispatching Timeout 的 5 秒阈值，
     * 期间任何触摸屏幕的操作都会触发 ANR 对话框。
     *
     * 这是最简单也最常见的 ANR 原因之一。
     */
    private fun demonstrateSleep() {
        infoSb.clear()
        resultSb.clear()

        // 显示代码示例和说明
        infoSb.appendLine("=== Thread.sleep 阻塞 ===")
        infoSb.appendLine()
        infoSb.appendLine("// 错误：主线程 sleep")
        infoSb.appendLine("Thread.sleep(10_000) // 超过 5 秒 → ANR")
        infoSb.appendLine()
        infoSb.appendLine("原理:")
        infoSb.appendLine("  Thread.sleep() 会直接阻塞当前线程")
        infoSb.appendLine("  在主线程调用会导致消息队列停止分发")
        infoSb.appendLine("  所有输入事件（触摸、按键）无法被处理")
        infoSb.appendLine("  超过 5 秒后系统弹出 ANR 对话框")
        binding.tvInfo.text = infoSb.toString()

        // 显示结果（在 sleep 之前显示，因为 sleep 后无法更新 UI）
        resultSb.appendLine("⚠️ 即将在主线程执行 Thread.sleep(10_000)")
        resultSb.appendLine("⚠️ 主线程将被阻塞 10 秒")
        resultSb.appendLine("⚠️ 5 秒后触摸屏幕将触发 ANR")
        resultSb.appendLine()
        resultSb.appendLine("执行中...")
        binding.tvResult.text = resultSb.toString()

        // 在主线程执行 sleep，这将阻塞主线程 10 秒
        // 期间如果用户触摸屏幕，将在 5 秒后触发 ANR
        Thread.sleep(10_000)

        // 如果没有触发 ANR（例如没有触摸屏幕），sleep 结束后显示结果
        resultSb.clear()
        resultSb.appendLine("sleep 结束")
        resultSb.appendLine("如果期间没有触摸屏幕，不会触发 ANR")
        resultSb.appendLine("因为 ANR 是在输入事件无法分发时才触发的")
        resultSb.appendLine()
        resultSb.appendLine("注意: 即使没有 ANR 弹窗，")
        resultSb.appendLine("用户也会感受到明显的卡顿！")
        binding.tvResult.text = resultSb.toString()
    }

    /**
     * 演示死循环导致的 ANR
     *
     * while(true) 会让主线程永远无法退出循环，
     * 后续所有消息和输入事件都无法被处理，
     * 必然会触发 ANR。
     *
     * 这是比 Thread.sleep 更严重的情况：
     * sleep 至少会在指定时间后恢复，而死循环永远不会。
     */
    private fun demonstrateLoop() {
        infoSb.clear()
        resultSb.clear()

        // 显示代码示例和说明
        infoSb.appendLine("=== 死循环 ===")
        infoSb.appendLine()
        infoSb.appendLine("// 错误：死循环")
        infoSb.appendLine("while (true) {")
        infoSb.appendLine("    // 主线程永远无法处理其他消息")
        infoSb.appendLine("}")
        infoSb.appendLine()
        infoSb.appendLine("原理:")
        infoSb.appendLine("  while(true) 是一个无限循环")
        infoSb.appendLine("  主线程被死循环占满，无法处理任何消息")
        infoSb.appendLine("  所有事件（输入、生命周期、UI 绘制）全部阻塞")
        infoSb.appendLine("  必然触发 ANR，且永远无法自动恢复")
        binding.tvInfo.text = infoSb.toString()

        // 显示结果（在进入死循环之前显示，之后无法更新 UI）
        resultSb.appendLine("⚠️ 即将进入 while(true) 死循环")
        resultSb.appendLine("⚠️ 主线程将永远被阻塞")
        resultSb.appendLine("⚠️ 5 秒后触摸屏幕将触发 ANR")
        resultSb.appendLine("⚠️ 且无法自动恢复，只能强制关闭应用")
        binding.tvResult.text = resultSb.toString()

        // 进入死循环 —— 主线程将永远无法退出
        // 注意：这个循环永远不会结束！
        while (true) {
            // 主线程永远无法处理其他消息
        }
    }

    /**
     * 演示死锁场景
     *
     * 死锁条件（必须同时满足）：
     * 1. 互斥：资源只能被一个线程持有
     * 2. 持有并等待：线程持有资源，同时等待其他资源
     * 3. 不可剥夺：已持有的资源不能被强制释放
     * 4. 循环等待：线程间形成环形等待链
     *
     * 本示例中：
     * - 线程 A 持有 lockA，等待 lockB
     * - 线程 B 持有 lockB，等待 lockA
     * - 主线程等待 lockA（通过 join 等待线程 A 结束）
     * - 形成死锁，导致 ANR
     */
    private fun demonstrateDeadlock() {
        infoSb.clear()
        resultSb.clear()

        // 显示代码示例和说明
        infoSb.appendLine("=== 死锁 ===")
        infoSb.appendLine()
        infoSb.appendLine("// 死锁场景示例")
        infoSb.appendLine("val lockA = Object()  // 锁 A")
        infoSb.appendLine("val lockB = Object()  // 锁 B")
        infoSb.appendLine()
        infoSb.appendLine("// 线程 1: 持有 lockA，等待 lockB")
        infoSb.appendLine("synchronized(lockA) {")
        infoSb.appendLine("    Thread.sleep(100)  // 确保线程 2 先获取 lockB")
        infoSb.appendLine("    synchronized(lockB) {  // 等待 lockB → 死锁!")
        infoSb.appendLine("        // 永远无法执行")
        infoSb.appendLine("    }")
        infoSb.appendLine("}")
        infoSb.appendLine()
        infoSb.appendLine("// 线程 2: 持有 lockB，等待 lockA")
        infoSb.appendLine("synchronized(lockB) {")
        infoSb.appendLine("    Thread.sleep(100)  // 确保线程 1 先获取 lockA")
        infoSb.appendLine("    synchronized(lockA) {  // 等待 lockA → 死锁!")
        infoSb.appendLine("        // 永远无法执行")
        infoSb.appendLine("    }")
        infoSb.appendLine("}")
        infoSb.appendLine()
        infoSb.appendLine("死锁四个必要条件:")
        infoSb.appendLine("  1. 互斥: 资源只能被一个线程持有")
        infoSb.appendLine("  2. 持有并等待: 持有资源同时等待其他资源")
        infoSb.appendLine("  3. 不可剥夺: 已持有的资源不能被强制释放")
        infoSb.appendLine("  4. 循环等待: 线程间形成环形等待链")
        binding.tvInfo.text = infoSb.toString()

        // 显示结果
        resultSb.appendLine("⚠️ 即将创建死锁场景")
        resultSb.appendLine("⚠️ 两个线程互相等待对方持有的锁")
        resultSb.appendLine("⚠️ 主线程将被阻塞，导致 ANR")
        binding.tvResult.text = resultSb.toString()

        // 创建两个锁对象
        val lockA = Object()
        val lockB = Object()

        // 线程 A: 先获取 lockA，再尝试获取 lockB
        val threadA = Thread {
            synchronized(lockA) {
                try {
                    // 短暂等待，确保线程 B 有机会先获取 lockB
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                // 此时线程 B 已经持有 lockB
                // 线程 A 等待 lockB，而线程 B 等待 lockA → 死锁！
                synchronized(lockB) {
                    // 永远无法到达这里
                }
            }
        }

        // 线程 B: 先获取 lockB，再尝试获取 lockA
        val threadB = Thread {
            synchronized(lockB) {
                try {
                    // 短暂等待，确保线程 A 有机会先获取 lockA
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                // 此时线程 A 已经持有 lockA
                // 线程 B 等待 lockA，而线程 A 等待 lockB → 死锁！
                synchronized(lockA) {
                    // 永远无法到达这里
                }
            }
        }

        // 启动两个线程
        threadA.start()
        threadB.start()

        // 主线程等待线程 A 结束 —— 但线程 A 永远不会结束（死锁）
        // 这将阻塞主线程，导致 ANR
        try {
            threadA.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    /**
     * 演示正确的方式 —— 使用协程
     *
     * 通过 lifecycleScope.launch 在 IO 调度器上执行耗时操作，
     * 不会阻塞主线程。操作完成后通过 withContext 切换回主线程更新 UI。
     *
     * 这是处理耗时操作的标准方式：
     * 1. 在 Dispatchers.IO 上执行阻塞/耗时操作
     * 2. 使用 withContext(Dispatchers.Main) 切换回主线程
     * 3. 在主线程安全地更新 UI
     */
    private fun demonstrateCorrect() {
        infoSb.clear()
        resultSb.clear()

        // 显示代码示例和说明
        infoSb.appendLine("=== 正确方式：使用协程 ===")
        infoSb.appendLine()
        infoSb.appendLine("// 正确：使用协程")
        infoSb.appendLine("lifecycleScope.launch(Dispatchers.IO) {")
        infoSb.appendLine("    Thread.sleep(10_000) // 在 IO 线程执行")
        infoSb.appendLine("    withContext(Dispatchers.Main) {")
        infoSb.appendLine("        // 回到主线程更新 UI")
        infoSb.appendLine("    }")
        infoSb.appendLine("}")
        infoSb.appendLine()
        infoSb.appendLine("原理:")
        infoSb.appendLine("  • lifecycleScope 绑定 Activity 生命周期")
        infoSb.appendLine("  • Dispatchers.IO 在 IO 线程池执行耗时操作")
        infoSb.appendLine("  • 主线程保持空闲，可以正常处理输入事件")
        infoSb.appendLine("  • withContext(Dispatchers.Main) 安全更新 UI")
        infoSb.appendLine()
        infoSb.appendLine("优点:")
        infoSb.appendLine("  • 不阻塞主线程，用户体验流畅")
        infoSb.appendLine("  • 自动随生命周期取消，避免内存泄漏")
        infoSb.appendLine("  • 结构化并发，代码清晰易读")
        binding.tvInfo.text = infoSb.toString()

        // 显示开始执行
        resultSb.appendLine("开始执行协程...")
        resultSb.appendLine("耗时操作在 IO 线程池执行")
        resultSb.appendLine("主线程保持空闲，可以正常交互")
        resultSb.appendLine()
        resultSb.appendLine("请尝试触摸屏幕 — 不会 ANR！")
        binding.tvResult.text = resultSb.toString()

        // 使用协程在 IO 线程执行耗时操作
        lifecycleScope.launch(Dispatchers.IO) {
            // 在 IO 线程池执行 10 秒耗时操作
            // 主线程完全不受影响，可以正常处理输入事件
            Thread.sleep(10_000)

            // 耗时操作完成，切换回主线程更新 UI
            withContext(Dispatchers.Main) {
                resultSb.clear()
                resultSb.appendLine("✓ 协程执行完成！")
                resultSb.appendLine()
                resultSb.appendLine("执行详情:")
                resultSb.appendLine("  • 耗时操作在 IO 线程池完成")
                resultSb.appendLine("  • 耗时: 10 秒")
                resultSb.appendLine("  • 主线程全程空闲，未发生 ANR")
                resultSb.appendLine("  • UI 更新安全地在主线程执行")
                resultSb.appendLine()
                resultSb.appendLine("对比 Thread.sleep 方式:")
                resultSb.appendLine("  ✗ Thread.sleep(10_000) → 主线程阻塞 → ANR")
                resultSb.appendLine("  ✓ lifecycleScope + IO  → 后台执行 → 无 ANR")
                binding.tvResult.text = resultSb.toString()

                // 显示 Toast 提示
                Toast.makeText(
                    this@InputAnrActivity,
                    "协程执行完成，未发生 ANR！",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
