package com.peter.anr.demo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.peter.anr.demo.databinding.ActivityBroadcastAnrBinding

/**
 * BroadcastReceiver ANR 演示
 *
 * 本 Activity 展示 BroadcastReceiver 的 ANR 机制和解决方案。
 *
 * ## 核心概念
 * BroadcastReceiver 的 onReceive() 方法默认在主线程执行，
 * 如果在 onReceive() 中做耗时操作，超过超时时间就会触发 ANR。
 *
 * ## 超时阈值
 * - 前台广播: 10 秒
 * - 后台广播: 60 秒
 *
 * ## 解决方案
 * 1. 使用 goAsync() 延长处理时间（最多 30 秒）
 * 2. 将耗时操作放到子线程
 * 3. 使用 WorkManager 处理真正耗时的任务
 */
class BroadcastAnrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBroadcastAnrBinding
    private val sb = StringBuilder()
    private var demoReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBroadcastAnrBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupInfo()
        setupListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        // 注销广播接收器，防止泄漏
        demoReceiver?.let {
            try {
                unregisterReceiver(it)
            } catch (_: Exception) {
            }
        }
    }

    /**
     * 显示基础信息
     */
    private fun setupInfo() {
        sb.clear()
        sb.appendLine("=== BroadcastReceiver ANR ===")
        sb.appendLine()
        sb.appendLine("=== 超时阈值 ===")
        sb.appendLine("前台广播: 10 秒")
        sb.appendLine("后台广播: 60 秒")
        sb.appendLine()
        sb.appendLine("=== 触发条件 ===")
        sb.appendLine("onReceive() 方法在主线程执行")
        sb.appendLine("如果执行超过超时时间 → ANR")
        sb.appendLine()
        sb.appendLine("=== 为什么 onReceive 在主线程？ ===")
        sb.appendLine("BroadcastReceiver 的 onReceive() 默认在主线程回调")
        sb.appendLine("这意味着不能在 onReceive() 中做耗时操作")
        binding.tvInfo.text = sb.toString()
    }

    private fun setupListeners() {
        binding.btnReceiveBlock.setOnClickListener { showReceiveBlock() }
        binding.btnGoAsync.setOnClickListener { showGoAsync() }
        binding.btnBestPractice.setOnClickListener { showBestPractice() }
    }

    /**
     * 演示 onReceive 阻塞导致 ANR
     *
     * 注册一个动态广播接收器，发送广播后 onReceive() 中 Thread.sleep(15_000)
     * 前台广播 10 秒超时，所以会触发 ANR
     */
    private fun showReceiveBlock() {
        sb.clear()
        sb.appendLine("=== onReceive 阻塞 → ANR ===")
        sb.appendLine()
        sb.appendLine("// ❌ 错误：onReceive 中做耗时操作")
        sb.appendLine("override fun onReceive(context: Context, intent: Intent) {")
        sb.appendLine("    Thread.sleep(15_000) // 前台广播 10 秒超时 → ANR")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("⚠️ 注意：")
        sb.appendLine("前台广播超时 = 10 秒")
        sb.appendLine("Thread.sleep(15_000) = 15 秒")
        sb.appendLine("→ 必定触发 ANR！")
        sb.appendLine()
        sb.appendLine("实际执行流程：")
        sb.appendLine("1. registerReceiver() 注册接收器")
        sb.appendLine("2. sendBroadcast() 发送前台广播")
        sb.appendLine("3. onReceive() 在主线程被回调")
        sb.appendLine("4. Thread.sleep(15_000) 阻塞主线程")
        sb.appendLine("5. 10 秒后 AMS 检测到超时")
        sb.appendLine("6. 弹出 ANR 对话框")
        binding.tvResult.text = sb.toString()

        // 注册动态广播
        demoReceiver?.let {
            try {
                unregisterReceiver(it)
            } catch (_: Exception) {
            }
        }

        demoReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                // ❌ 错误：在 onReceive 中做耗时操作
                // 这将导致 ANR（前台广播 10 秒超时）
                Thread.sleep(15_000)
            }
        }

        val filter = IntentFilter("com.peter.anr.demo.BROADCAST_ANR")
        registerReceiver(demoReceiver, filter)

        // 发送广播（注意：这会触发 ANR）
        // 实际演示时取消注释下面这行：
        // sendBroadcast(Intent("com.peter.anr.demo.BROADCAST_ANR"))
    }

    /**
     * 演示 goAsync() 方案
     *
     * goAsync() 可以将广播的处理时间从 10 秒延长到 30 秒，
     * 但仍然必须在 30 秒内调用 pendingResult.finish()
     */
    private fun showGoAsync() {
        sb.clear()
        sb.appendLine("=== goAsync() 方案 ===")
        sb.appendLine()
        sb.appendLine("// ✓ 使用 goAsync() 延长处理时间")
        sb.appendLine("override fun onReceive(context: Context, intent: Intent) {")
        sb.appendLine("    val pendingResult = goAsync()")
        sb.appendLine("    Thread {")
        sb.appendLine("        // 在后台线程处理")
        sb.appendLine("        Thread.sleep(15_000)")
        sb.appendLine("        pendingResult.finish() // 必须调用！")
        sb.appendLine("    }.start()")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("=== goAsync() 说明 ===")
        sb.appendLine()
        sb.appendLine("1. 调用 goAsync() 返回 PendingResult")
        sb.appendLine("   - 将广播处理转为异步模式")
        sb.appendLine("   - 超时从 10 秒延长到 30 秒")
        sb.appendLine()
        sb.appendLine("2. 必须在子线程处理耗时操作")
        sb.appendLine("   - onReceive() 本身仍在主线程")
        sb.appendLine("   - goAsync() 只是告诉系统等一下")
        sb.appendLine()
        sb.appendLine("3. 必须调用 pendingResult.finish()")
        sb.appendLine("   - 否则广播会被认为仍在处理")
        sb.appendLine("   - 超过 30 秒仍然会 ANR")
        sb.appendLine()
        sb.appendLine("4. 适用场景")
        sb.appendLine("   - 耗时 10~30 秒的轻量操作")
        sb.appendLine("   - 不适合超过 30 秒的操作")
        binding.tvResult.text = sb.toString()
    }

    /**
     * 显示广播 ANR 最佳实践
     */
    private fun showBestPractice() {
        sb.clear()
        sb.appendLine("=== 广播 ANR 最佳实践 ===")
        sb.appendLine()
        sb.appendLine("1. onReceive() 中不做耗时操作")
        sb.appendLine("   • 耗时操作放到子线程")
        sb.appendLine("   • 使用 goAsync() 延长时间")
        sb.appendLine()
        sb.appendLine("2. 推荐：使用 WorkManager 处理耗时任务")
        sb.appendLine("   onReceive() → 触发 WorkManager → 后台执行")
        sb.appendLine()
        sb.appendLine("3. 使用 LocalBroadcastManager（已废弃）")
        sb.appendLine("   替代：LiveData / Flow / EventBus")
        sb.appendLine()
        sb.appendLine("4. 静态广播 Android 8.0+ 限制")
        sb.appendLine("   • 大部分隐式广播无法静态注册")
        sb.appendLine("   • 建议使用动态注册")
        sb.appendLine()
        sb.appendLine("=== 推荐方案代码 ===")
        sb.appendLine()
        sb.appendLine("// 方案1: goAsync（适合 10~30 秒）")
        sb.appendLine("override fun onReceive(context: Context, intent: Intent) {")
        sb.appendLine("    val pendingResult = goAsync()")
        sb.appendLine("    CoroutineScope(Dispatchers.IO).launch {")
        sb.appendLine("        // 耗时操作")
        sb.appendLine("        pendingResult.finish()")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("// 方案2: WorkManager（适合超过 30 秒）")
        sb.appendLine("override fun onReceive(context: Context, intent: Intent) {")
        sb.appendLine("    val request = OneTimeWorkRequestBuilder<MyWorker>()")
        sb.appendLine("        .build()")
        sb.appendLine("    WorkManager.getInstance(context).enqueue(request)")
        sb.appendLine("}")
        binding.tvResult.text = sb.toString()
    }
}
