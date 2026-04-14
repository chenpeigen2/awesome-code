package com.peter.anr.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.peter.anr.demo.databinding.ActivityServiceAnrBinding

/**
 * Service ANR 演示
 *
 * 本 Activity 展示 Service 的 ANR 机制和解决方案。
 *
 * ## 核心概念
 * Service 不是运行在独立线程的！Service 和 Activity 一样在主线程运行。
 * Service 的生命周期方法（onCreate, onStartCommand, onBind, onDestroy）
 * 都在主线程执行，如果这些方法执行超时就会触发 ANR。
 *
 * ## 超时阈值
 * - 前台 Service: 20 秒
 * - 后台 Service: 200 秒
 *
 * ## 常见误解
 * 很多开发者以为 Service 运行在独立线程，这是错误的。
 * Service 默认运行在主线程，需要手动创建子线程处理耗时操作。
 */
class ServiceAnrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServiceAnrBinding
    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceAnrBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupInfo()
        setupListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    /**
     * 显示基础信息
     */
    private fun setupInfo() {
        sb.clear()
        sb.appendLine("=== Service ANR ===")
        sb.appendLine()
        sb.appendLine("=== 超时阈值 ===")
        sb.appendLine("前台 Service: 20 秒")
        sb.appendLine("后台 Service: 200 秒")
        sb.appendLine()
        sb.appendLine("=== 触发条件 ===")
        sb.appendLine("Service 的以下方法在主线程执行：")
        sb.appendLine("  • onCreate()")
        sb.appendLine("  • onStartCommand()")
        sb.appendLine("  • onBind()")
        sb.appendLine("  • onDestroy()")
        sb.appendLine("如果这些方法执行超时 → ANR")
        sb.appendLine()
        sb.appendLine("=== 常见误解 ===")
        sb.appendLine("Service 不是运行在独立线程的！")
        sb.appendLine("Service 和 Activity 一样在主线程运行")
        binding.tvInfo.text = sb.toString()
    }

    private fun setupListeners() {
        binding.btnServiceBlock.setOnClickListener { showServiceBlock() }
        binding.btnCorrectWay.setOnClickListener { showCorrectWay() }
        binding.btnForegroundService.setOnClickListener { showForegroundService() }
    }

    /**
     * 演示 Service onCreate 阻塞导致 ANR
     *
     * 启动一个 Service (AnrDemoService)，在 onCreate() 中 Thread.sleep(30_000)
     * 前台 Service 20 秒超时，所以会触发 ANR
     */
    private fun showServiceBlock() {
        sb.clear()
        sb.appendLine("=== Service onCreate 阻塞 → ANR ===")
        sb.appendLine()
        sb.appendLine("// ❌ 错误：Service 的生命周期方法在主线程")
        sb.appendLine("class AnrDemoService : Service() {")
        sb.appendLine("    override fun onCreate() {")
        sb.appendLine("        super.onCreate()")
        sb.appendLine("        Thread.sleep(30_000) // 前台 Service 20 秒超时 → ANR")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("⚠️ 注意：")
        sb.appendLine("前台 Service 超时 = 20 秒")
        sb.appendLine("Thread.sleep(30_000) = 30 秒")
        sb.appendLine("→ 必定触发 ANR！")
        sb.appendLine()
        sb.appendLine("实际执行流程：")
        sb.appendLine("1. startService() 发送启动请求")
        sb.appendLine("2. ActivityThread 调用 Service.onCreate()")
        sb.appendLine("3. onCreate() 在主线程执行")
        sb.appendLine("4. Thread.sleep(30_000) 阻塞主线程")
        sb.appendLine("5. 20 秒后 ActiveServices 检测到超时")
        sb.appendLine("6. 弹出 ANR 对话框")
        binding.tvResult.text = sb.toString()

        // 启动 Service（注意：这会触发 ANR）
        // 实际演示时取消注释下面这行：
        // startService(Intent(this, AnrDemoService::class.java).apply {
        //     putExtra("block", true)
        // })
    }

    /**
     * 显示正确的 Service 使用方式
     */
    private fun showCorrectWay() {
        sb.clear()
        sb.appendLine("=== Service 正确使用方式 ===")
        sb.appendLine()
        sb.appendLine("// 方式1: 使用协程")
        sb.appendLine("class MyService : Service() {")
        sb.appendLine("    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {")
        sb.appendLine("        lifecycleScope.launch(Dispatchers.IO) {")
        sb.appendLine("            // 耗时操作在 IO 线程")
        sb.appendLine("        }")
        sb.appendLine("        return START_NOT_STICKY")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("// 方式2: 使用 IntentService（已废弃，推荐 WorkManager）")
        sb.appendLine("// IntentService 内部使用 HandlerThread")
        sb.appendLine()
        sb.appendLine("// 方式3: 手动创建线程")
        sb.appendLine("override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {")
        sb.appendLine("    Thread {")
        sb.appendLine("        // 耗时操作")
        sb.appendLine("        stopSelf(startId)")
        sb.appendLine("    }.start()")
        sb.appendLine("    return START_NOT_STICKY")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("// 方式4: 使用 WorkManager（推荐）")
        sb.appendLine("val request = OneTimeWorkRequestBuilder<MyWorker>()")
        sb.appendLine("    .build()")
        sb.appendLine("WorkManager.getInstance(context).enqueue(request)")
        binding.tvResult.text = sb.toString()
    }

    /**
     * 显示前台 Service 说明
     */
    private fun showForegroundService() {
        sb.clear()
        sb.appendLine("=== 前台 Service 说明 ===")
        sb.appendLine()
        sb.appendLine("Android 8.0+: 后台启动 Service 限制")
        sb.appendLine("  • 应用在前台时可自由启动")
        sb.appendLine("  • 应用在后台时不能启动后台 Service")
        sb.appendLine("  • 需要 startForegroundService()")
        sb.appendLine()
        sb.appendLine("Android 12+: 前台 Service 限制")
        sb.appendLine("  • 需要在 AndroidManifest 声明 foregroundServiceType")
        sb.appendLine("  • 调用 startForeground() 有时间限制")
        sb.appendLine("  • 超过时间会抛出 ForegroundServiceStartNotAllowedException")
        sb.appendLine()
        sb.appendLine("Android 14+: 更严格的前台 Service 类型")
        sb.appendLine("  • 必须指定具体的 foregroundServiceType")
        sb.appendLine("  • 如: location, camera, microphone, connectedDevice 等")
        binding.tvResult.text = sb.toString()
    }
}
