package com.peter.context.demo.advanced

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.peter.context.demo.databinding.ActivityContextBroadcastBinding

/**
 * Context 与 BroadcastReceiver
 *
 * Context 在广播中的核心作用：
 * 1. registerReceiver() - 动态注册广播接收器
 * 2. unregisterReceiver() - 注销广播接收器
 * 3. sendBroadcast() - 发送自定义广播
 * 4. sendOrderedBroadcast() - 发送有序广播
 *
 * 广播类型：
 * - 标准广播 (Normal Broadcast) - 完全异步，所有接收器几乎同时收到
 * - 有序广播 (Ordered Broadcast) - 按优先级逐级传递，可拦截
 * - 本地广播 (Local Broadcast) - 只在应用内传播，安全高效
 * - 粘性广播 (Sticky Broadcast) - 已废弃
 *
 * Context 选择：
 * - 动态注册：使用 Activity Context（生命周期绑定，避免泄漏）
 * - 发送广播：任意 Context 均可
 * - 静态注册：在 AndroidManifest.xml 声明，不需要 Context
 */
class ContextBroadcastActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContextBroadcastBinding
    private val sb = StringBuilder()

    // 动态注册的广播接收器
    private var standardReceiver: StandardReceiver? = null
    private var orderedReceiver1: OrderedReceiver1? = null
    private var orderedReceiver2: OrderedReceiver2? = null
    private var systemReceiver: SystemReceiver? = null

    // 自定义广播 Action
    companion object {
        const val ACTION_CUSTOM_STANDARD = "com.peter.context.demo.CUSTOM_STANDARD"
        const val ACTION_CUSTOM_ORDERED = "com.peter.context.demo.CUSTOM_ORDERED"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContextBroadcastBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showBroadcastInfo()
    }

    private fun setupListeners() {
        binding.btnRegisterStandard.setOnClickListener { registerStandardReceiver() }
        binding.btnUnregisterStandard.setOnClickListener { unregisterStandardReceiver() }
        binding.btnSendStandard.setOnClickListener { sendStandardBroadcast() }

        binding.btnRegisterOrdered.setOnClickListener { registerOrderedReceivers() }
        binding.btnUnregisterOrdered.setOnClickListener { unregisterOrderedReceivers() }
        binding.btnSendOrdered.setOnClickListener { sendOrderedBroadcast() }

        binding.btnRegisterSystem.setOnClickListener { registerSystemReceiver() }
        binding.btnUnregisterSystem.setOnClickListener { unregisterSystemReceiver() }

        binding.btnContextCompare.setOnClickListener { showContextCompare() }
    }

    private fun showBroadcastInfo() {
        sb.clear()

        sb.appendLine("=== Context 与 BroadcastReceiver ===\n")

        sb.appendLine("=== 1. 广播的作用 ===")
        sb.appendLine("广播是 Android 四大组件之一")
        sb.appendLine("用于应用内/应用间的消息传递")
        sb.appendLine("基于观察者模式（发布-订阅）")
        sb.appendLine()

        sb.appendLine("=== 2. Context 在广播中的角色 ===")
        sb.appendLine("registerReceiver(receiver, filter)")
        sb.appendLine("  → 动态注册广播接收器")
        sb.appendLine("unregisterReceiver(receiver)")
        sb.appendLine("  → 注销广播接收器")
        sb.appendLine("sendBroadcast(intent)")
        sb.appendLine("  → 发送标准广播")
        sb.appendLine("sendOrderedBroadcast(intent, perm)")
        sb.appendLine("  → 发送有序广播")
        sb.appendLine()

        sb.appendLine("=== 3. 静态 vs 动态注册 ===")
        sb.appendLine("静态注册（AndroidManifest.xml）:")
        sb.appendLine("  • 应用未启动也能接收")
        sb.appendLine("  • Android 8.0+ 大幅限制")
        sb.appendLine("  • 不需要 Context")
        sb.appendLine()
        sb.appendLine("动态注册（代码中注册）:")
        sb.appendLine("  • 需要调用 Context.registerReceiver()")
        sb.appendLine("  • 生命周期由开发者管理")
        sb.appendLine("  • 灵活控制注册和注销时机")
        sb.appendLine()

        sb.appendLine("=== 4. 注册方式对比 ===")
        sb.appendLine("Context.registerReceiver(receiver, filter)")
        sb.appendLine("  → Activity 或 Service Context")
        sb.appendLine()
        sb.appendLine("LocalBroadcastManager.getInstance(ctx)")
        sb.appendLine("  .registerReceiver(receiver, filter)")
        sb.appendLine("  → 已废弃，使用 LiveData/Flow 替代")

        binding.tvInfo.text = sb.toString()
    }

    // ==================== 标准广播 ====================

    /**
     * 标准广播接收器
     *
     * 演示最基础的动态注册方式
     */
    private class StandardReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // onReceive 中的 Context 说明：
            // 如果是动态注册 → 传入注册时使用的 Context
            // 如果是静态注册 → 传入 Application Context
            Log.d("Broadcast", "StandardReceiver onReceive")
            Log.d("Broadcast", "Context type: ${context.javaClass.simpleName}")
            Log.d("Broadcast", "Action: ${intent.action}")
            Log.d("Broadcast", "Extra message: ${intent.getStringExtra("message")}")
        }
    }

    private fun registerStandardReceiver() {
        if (standardReceiver != null) {
            appendResult("标准广播接收器已注册，请先注销")
            return
        }

        sb.clear()
        sb.appendLine("=== 注册标准广播接收器 ===\n")

        standardReceiver = StandardReceiver()
        val filter = IntentFilter(ACTION_CUSTOM_STANDARD)

        // 使用 Activity Context 注册
        // 生命周期与 Activity 绑定
        registerReceiver(standardReceiver, filter)

        sb.appendLine("✓ 注册成功")
        sb.appendLine("Action: $ACTION_CUSTOM_STANDARD")
        sb.appendLine("Context: ${this.javaClass.simpleName}")
        sb.appendLine()
        sb.appendLine("代码:")
        sb.appendLine("  val receiver = StandardReceiver()")
        sb.appendLine("  val filter = IntentFilter(ACTION_CUSTOM_STANDARD)")
        sb.appendLine("  registerReceiver(receiver, filter)")
        sb.appendLine()
        sb.appendLine("注意: 使用 Activity Context 注册")
        sb.appendLine("如果 Activity 销毁时未注销 → 内存泄漏！")

        binding.tvResult.text = sb.toString()
    }

    private fun unregisterStandardReceiver() {
        if (standardReceiver == null) {
            appendResult("标准广播接收器未注册")
            return
        }

        // 使用相同的 Context 注销
        unregisterReceiver(standardReceiver)
        standardReceiver = null

        sb.clear()
        sb.appendLine("=== 注销标准广播接收器 ===\n")
        sb.appendLine("✓ 注销成功")
        sb.appendLine()
        sb.appendLine("代码:")
        sb.appendLine("  unregisterReceiver(standardReceiver)")
        sb.appendLine()
        sb.appendLine("规则：")
        sb.appendLine("  • 注册和注销必须配对")
        sb.appendLine("  • 推荐在 onStart/onStop 或 onResume/onPause 中注册/注销")
        sb.appendLine("  • 一定要在 onDestroy 中兜底注销")

        binding.tvResult.text = sb.toString()
    }

    private fun sendStandardBroadcast() {
        sb.clear()
        sb.appendLine("=== 发送标准广播 ===\n")

        val intent = Intent(ACTION_CUSTOM_STANDARD)
        intent.putExtra("message", "来自 ContextBroadcastActivity 的消息")
        intent.setPackage(packageName) // Android 8.0+ 需要指定包名

        // 发送广播 — 任意 Context 均可调用
        sendBroadcast(intent)

        sb.appendLine("✓ 广播已发送")
        sb.appendLine("Action: $ACTION_CUSTOM_STANDARD")
        sb.appendLine("Message: 来自 ContextBroadcastActivity 的消息")
        sb.appendLine()
        sb.appendLine("代码:")
        sb.appendLine("  val intent = Intent(ACTION_CUSTOM_STANDARD)")
        sb.appendLine("  intent.putExtra(\"message\", \"...\")")
        sb.appendLine("  intent.setPackage(packageName)")
        sb.appendLine("  sendBroadcast(intent)")
        sb.appendLine()
        sb.appendLine("说明:")
        sb.appendLine("  • sendBroadcast() 是 Context 的方法")
        sb.appendLine("  • Activity/Service/Application Context 都可以发")
        sb.appendLine("  • setPackage() 限制为应用内广播（Android 8.0+）")

        binding.tvResult.text = sb.toString()
    }

    // ==================== 有序广播 ====================

    private class OrderedReceiver1 : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("Broadcast", "OrderedReceiver1 接收到广播")
            Log.d("Broadcast", "优先级最高，第一个收到")

            // 可以修改结果数据传递给下一个接收器
            val data = getResultData()
            setResultData("${data ?: "初始数据"} → Receiver1 处理")

            // 可以调用 abortBroadcast() 阻止广播继续传递
        }
    }

    private class OrderedReceiver2 : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("Broadcast", "OrderedReceiver2 接收到广播")
            Log.d("Broadcast", "优先级较低，第二个收到")

            val data = getResultData()
            Log.d("Broadcast", "收到数据: $data")
        }
    }

    private fun registerOrderedReceivers() {
        if (orderedReceiver1 != null) {
            appendResult("有序广播接收器已注册，请先注销")
            return
        }

        sb.clear()
        sb.appendLine("=== 注册有序广播接收器 ===\n")

        orderedReceiver1 = OrderedReceiver1()
        orderedReceiver2 = OrderedReceiver2()

        // Receiver1 优先级高
        val filter1 = IntentFilter(ACTION_CUSTOM_ORDERED)
        filter1.priority = 100  // 优先级 -1000 ~ 1000，值越大越先收到

        // Receiver2 优先级低
        val filter2 = IntentFilter(ACTION_CUSTOM_ORDERED)
        filter2.priority = 50

        registerReceiver(orderedReceiver1, filter1)
        registerReceiver(orderedReceiver2, filter2)

        sb.appendLine("✓ 注册成功（2个接收器）")
        sb.appendLine()
        sb.appendLine("OrderedReceiver1 优先级: 100")
        sb.appendLine("OrderedReceiver2 优先级: 50")
        sb.appendLine()
        sb.appendLine("代码:")
        sb.appendLine("  val filter1 = IntentFilter(ACTION_CUSTOM_ORDERED)")
        sb.appendLine("  filter1.priority = 100  // 高优先级")
        sb.appendLine("  registerReceiver(receiver1, filter1)")
        sb.appendLine()
        sb.appendLine("  val filter2 = IntentFilter(ACTION_CUSTOM_ORDERED)")
        sb.appendLine("  filter2.priority = 50   // 低优先级")
        sb.appendLine("  registerReceiver(receiver2, filter2)")
        sb.appendLine()
        sb.appendLine("特点:")
        sb.appendLine("  • 按优先级顺序接收")
        sb.appendLine("  • 可以通过 getResultData()/setResultData() 传递数据")
        sb.appendLine("  • 可以通过 abortBroadcast() 终止传播")

        binding.tvResult.text = sb.toString()
    }

    private fun unregisterOrderedReceivers() {
        if (orderedReceiver1 == null) {
            appendResult("有序广播接收器未注册")
            return
        }

        unregisterReceiver(orderedReceiver1)
        unregisterReceiver(orderedReceiver2)
        orderedReceiver1 = null
        orderedReceiver2 = null

        sb.clear()
        sb.appendLine("✓ 有序广播接收器已全部注销")
        binding.tvResult.text = sb.toString()
    }

    private fun sendOrderedBroadcast() {
        sb.clear()
        sb.appendLine("=== 发送有序广播 ===\n")

        val intent = Intent(ACTION_CUSTOM_ORDERED)
        intent.setPackage(packageName)

        // sendOrderedBroadcast 也是 Context 的方法
        sendOrderedBroadcast(intent, null)

        sb.appendLine("✓ 有序广播已发送")
        sb.appendLine()
        sb.appendLine("代码:")
        sb.appendLine("  val intent = Intent(ACTION_CUSTOM_ORDERED)")
        sb.appendLine("  sendOrderedBroadcast(intent, null)")
        sb.appendLine()
        sb.appendLine("参数说明:")
        sb.appendLine("  第1个参数: Intent（包含 Action）")
        sb.appendLine("  第2个参数: 权限字符串（null 表示不需要权限）")
        sb.appendLine()
        sb.appendLine("传递链:")
        sb.appendLine("  初始数据 → Receiver1(优先级100) → Receiver2(优先级50)")
        sb.appendLine()
        sb.appendLine("请查看 Logcat 输出（tag: Broadcast）")

        binding.tvResult.text = sb.toString()
    }

    // ==================== 系统广播 ====================

    private class SystemReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_ON -> {
                    Log.d("Broadcast", "屏幕亮起")
                }
                Intent.ACTION_SCREEN_OFF -> {
                    Log.d("Broadcast", "屏幕关闭")
                }
                Intent.ACTION_BATTERY_CHANGED -> {
                    val level = intent.getIntExtra("level", -1)
                    val scale = intent.getIntExtra("scale", -1)
                    Log.d("Broadcast", "电量变化: ${level * 100 / scale}%")
                }
                Intent.ACTION_CONFIGURATION_CHANGED -> {
                    Log.d("Broadcast", "配置变化")
                }
            }
        }
    }

    private fun registerSystemReceiver() {
        if (systemReceiver != null) {
            appendResult("系统广播接收器已注册")
            return
        }

        sb.clear()
        sb.appendLine("=== 注册系统广播接收器 ===\n")

        systemReceiver = SystemReceiver()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }

        registerReceiver(systemReceiver, filter)

        sb.appendLine("✓ 注册成功")
        sb.appendLine()
        sb.appendLine("监听的系统广播:")
        sb.appendLine("  • ACTION_SCREEN_ON - 屏幕亮起")
        sb.appendLine("  • ACTION_SCREEN_OFF - 屏幕关闭")
        sb.appendLine("  • ACTION_BATTERY_CHANGED - 电量变化")
        sb.appendLine()
        sb.appendLine("常见系统广播:")
        sb.appendLine("  • BOOT_COMPLETED - 开机完成（需要权限）")
        sb.appendLine("  • BATTERY_CHANGED - 电量变化")
        sb.appendLine("  • SCREEN_ON/OFF - 屏幕开关")
        sb.appendLine("  • CONNECTIVITY_CHANGE - 网络变化")
        sb.appendLine("  • PACKAGE_ADDED/REMOVED - 应用安装卸载")
        sb.appendLine()
        sb.appendLine("锁屏/解锁后请查看 Logcat（tag: Broadcast）")

        binding.tvResult.text = sb.toString()
    }

    private fun unregisterSystemReceiver() {
        if (systemReceiver == null) {
            appendResult("系统广播接收器未注册")
            return
        }

        unregisterReceiver(systemReceiver)
        systemReceiver = null

        sb.clear()
        sb.appendLine("✓ 系统广播接收器已注销")
        binding.tvResult.text = sb.toString()
    }

    // ==================== Context 对比 ====================

    private fun showContextCompare() {
        sb.clear()
        sb.appendLine("=== 广播中 Context 的选择 ===\n")

        sb.appendLine("=== 1. 注册时 Context 选择 ===")
        sb.appendLine()
        sb.appendLine("Activity Context（推荐）:")
        sb.appendLine("  registerReceiver(receiver, filter)")
        sb.appendLine("  ✗ Activity 销毁时未注销 → 内存泄漏")
        sb.appendLine("  ✓ 在 onDestroy 中注销即可避免")
        sb.appendLine()
        sb.appendLine("Application Context:")
        sb.appendLine("  applicationContext.registerReceiver(receiver, filter)")
        sb.appendLine("  ✗ 注册的接收器与应用进程同生命周期")
        sb.appendLine("  ✗ 长时间持有，容易忘记注销")
        sb.appendLine("  ✓ 适合需要在整个应用生命周期监听的场景")
        sb.appendLine()

        sb.appendLine("=== 2. 发送时 Context 选择 ===")
        sb.appendLine()
        sb.appendLine("任意 Context 都可以发送广播:")
        sb.appendLine("  activity.sendBroadcast(intent)")
        sb.appendLine("  service.sendBroadcast(intent)")
        sb.appendLine("  context.sendBroadcast(intent)")
        sb.appendLine("  applicationContext.sendBroadcast(intent)")
        sb.appendLine()

        sb.appendLine("=== 3. onReceive 中的 Context ===")
        sb.appendLine()
        sb.appendLine("动态注册:")
        sb.appendLine("  → onReceive 的 context 就是注册时用的 Context")
        sb.appendLine("  → 如果用 Activity 注册，context 就是该 Activity")
        sb.appendLine()
        sb.appendLine("静态注册:")
        sb.appendLine("  → onReceive 的 context 是 Application Context")
        sb.appendLine("  → 因为静态注册不依赖特定组件")
        sb.appendLine()

        sb.appendLine("=== 4. 安全最佳实践 ===")
        sb.appendLine()
        sb.appendLine("发送广播时指定包名:")
        sb.appendLine("  intent.setPackage(packageName)")
        sb.appendLine("  → 限制为应用内广播，防止信息泄露")
        sb.appendLine()
        sb.appendLine("使用权限限制:")
        sb.appendLine("  sendBroadcast(intent, \"com.example.MY_PERMISSION\")")
        sb.appendLine("  → 只有声明了该权限的接收器才能收到")
        sb.appendLine()
        sb.appendLine("推荐替代方案:")
        sb.appendLine("  • 应用内通信 → LiveData / Flow / EventBus")
        sb.appendLine("  • 跨应用通信 → BroadcastReceiver + 权限")
        sb.appendLine("  • 本地广播 → 已废弃，使用其他通信方式")

        binding.tvResult.text = sb.toString()
    }

    private fun appendResult(msg: String) {
        sb.clear()
        sb.appendLine(msg)
        binding.tvResult.text = sb.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 重要：在 onDestroy 中注销所有接收器，防止内存泄漏
        standardReceiver?.let { unregisterReceiver(it) }
        orderedReceiver1?.let { unregisterReceiver(it) }
        orderedReceiver2?.let { unregisterReceiver(it) }
        systemReceiver?.let { unregisterReceiver(it) }
    }
}

private object Log {
    fun d(tag: String, msg: String) {
        android.util.Log.d(tag, msg)
    }
}
