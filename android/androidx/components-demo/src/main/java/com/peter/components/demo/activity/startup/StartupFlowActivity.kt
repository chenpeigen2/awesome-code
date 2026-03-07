package com.peter.components.demo.activity.startup

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.R
import com.peter.components.demo.databinding.ActivityStartupFlowBinding

/**
 * Activity 启动流程解析与追踪 Demo
 *
 * ═══════════════════════════════════════════════════════════════
 *                    Activity 启动流程详解
 * ═══════════════════════════════════════════════════════════════
 *
 * 【一、核心角色】
 *
 * ┌─────────────────────────────────────────────────────────────┐
 * │  App 进程 (调用方)          │  System Server 进程           │
 * ├─────────────────────────────┼───────────────────────────────┤
 * │  Activity                   │  ATMS (ActivityTaskManager)   │
 * │  Instrumentation            │  AMS (ActivityManager)        │
 * │  ActivityThread             │  ActivityStack                │
 * │  ApplicationThread (Binder) │  ActivityStarter              │
 * └─────────────────────────────┴───────────────────────────────┘
 *
 * 【二、启动流程源码调用链】
 *
 * 1. 应用层发起启动请求
 *    Activity.startActivity(Intent)
 *        ↓
 *    Activity.startActivityForResult(Intent, int, Bundle)
 *        ↓
 *    Instrumentation.execStartActivity(...)
 *        │ [关键] Instrumentation 是应用层监控 Activity 生命周期的入口
 *        │ 可以通过自定义 Instrumentation 来 Hook Activity 创建过程
 *        ↓
 *    ATMS.getService().startActivity(...)  ← Binder IPC 跨进程调用
 *
 * 2. 系统服务处理（System Server 进程）
 *    ActivityTaskManagerService.startActivity(...)
 *        ↓
 *    ActivityStarter.execute()
 *        │ [关键] ActivityStarter 负责：
 *        │ - 解析 Intent
 *        │ - 检查权限
 *        │ - 计算 Launch Mode
 *        │ - 处理 Task Affinity
 *        ↓
 *    ActivityStack.startActivityLocked()
 *        │ [关键] ActivityStack 管理 Activity 状态：
 *        │ - PAUSING: 正在暂停前一个 Activity
 *        │ - RESUMED: 已恢复
 *        │ - STOPPING: 正在停止
 *        ↓
 *    ClientLifecycleManager.scheduleTransaction(...)
 *        │ [关键] 通过 Binder 回调到应用进程
 *
 * 3. 应用进程处理回调（目标 Activity 进程）
 *    ApplicationThread.scheduleTransaction(ClientTransaction)
 *        │ [关键] ApplicationThread 是 ActivityThread 的内部类
 *        │ 它是 Binder 服务端，接收 System Server 的调用
 *        ↓
 *    ActivityThread.handleLaunchActivity()
 *        │
 *        ├→ Activity.attach()      // 绑定 Window、Context
 *        │     ↓
 *        │   Instrumentation.callActivityOnCreate()
 *        │     ↓
 *        │   Activity.onCreate()
 *        │
 *        └→ ActivityThread.handleResumeActivity()
 *              ↓
 *            Activity.onResume()
 *              ↓
 *            WindowManager.addView()  // View 挂载到 Window
 *
 * 【三、关键类职责】
 *
 * ┌──────────────────────────┬────────────────────────────────────┐
 * │ 类名                      │ 职责                               │
 * ├──────────────────────────┼────────────────────────────────────┤
 * │ Instrumentation          │ 监控 Activity 生命周期，可 Hook     │
 * │ ActivityTaskManagerService│ 管理 Activity 任务栈（Android 10+）│
 * │ ActivityStack            │ 维护 Activity 状态和切换            │
 * │ ActivityThread           │ 应用主线程，处理消息循环            │
 * │ ApplicationThread        │ Binder 服务端，接收 AMS 调用        │
 * │ ClientTransaction        │ 封装生命周期指令                    │
 * │ PhoneWindow              │ Activity 的 Window 实现            │
 * └──────────────────────────┴────────────────────────────────────┘
 *
 * 【四、生命周期回调时序】
 *
 *    Activity.attach()
 *         │  绑定 Window、Application
 *         ↓
 *    onCreate()
 *         │  setContentView() 加载布局
 *         ↓
 *    onStart()
 *         │  Activity 即将可见
 *         ↓
 *    onResume()
 *         │  Activity 可见可交互
 *         ↓
 *    onWindowFocusChanged(true)
 *         │  首帧绘制完成
 *         ↓
 *    [用户可见完整界面]
 *
 * ═══════════════════════════════════════════════════════════════
 */
class StartupFlowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartupFlowBinding
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartupFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupClickListeners()
    }

    private fun setupViews() {
        // 设置日志区域可滚动
        binding.tvTimelineLog.movementMethod = ScrollingMovementMethod()

        // 显示源码调用链
        binding.tvSourceChain.text = getCallChainText()
    }

    private fun setupClickListeners() {
        // 启动 TargetActivity
        binding.btnStartActivity.setOnClickListener {
            Log.d("StartupFlow", "btnStartActivity clicked")
            startTargetActivity()
        }

        // 启动并等待结果
        binding.btnStartForResult.setOnClickListener {
            Log.d("StartupFlow", "btnStartForResult clicked")
            // 这里可以添加 startActivityForResult 的示例
            startTargetActivity()
        }

        // 重置追踪
        binding.btnReset.setOnClickListener {
            StartupTracker.reset()
            updateLogDisplay("追踪器已重置，点击按钮开始新的追踪")
            Log.d("StartupFlow", "Tracker reset")
        }
    }

    private fun startTargetActivity() {
        // 重置追踪器，开始新的追踪
        StartupTracker.reset()

        // 记录启动请求发起时间
        StartupTracker.logStage("startActivity() 调用", "发起启动请求")

        // 调用 startActivity
        val intent = Intent(this, TargetActivity::class.java)
        startActivity(intent)

        // 注意：startActivity() 返回后，目标 Activity 可能还未创建
        // 因为需要经过 Binder IPC 和系统服务调度
        StartupTracker.logStage("startActivity() 返回", "请求已发送到系统")
    }

    override fun onResume() {
        super.onResume()
        // 从 TargetActivity 返回后更新日志
        if (StartupTracker.getRecordCount() > 0) {
            updateLogDisplay(StartupTracker.getSimpleLog())
        }
    }

    private fun updateLogDisplay(log: String) {
        binding.tvTimelineLog.text = log
    }

    /**
     * 获取源码调用链说明文本
     */
    private fun getCallChainText(): String {
        return """
Activity.startActivity()
    │
    ↓
Instrumentation.execStartActivity()
    │  [应用层入口]
    │  可通过自定义 Instrumentation Hook
    │
    ↓
ATMS.startActivity() [Binder IPC]
    │  [跨进程调用]
    │  从 App 进程 → System Server 进程
    │
    ↓
ActivityStarter.execute()
    │  [解析 Intent、检查权限]
    │  计算 Launch Mode、Task Affinity
    │
    ↓
ActivityStack.startActivityLocked()
    │  [管理 Activity 状态]
    │  处理 Task 切换
    │
    ↓
ClientTransaction.schedule()
    │  [Binder IPC]
    │  System Server → App 进程
    │
    ↓
ApplicationThread.scheduleTransaction()
    │  [Binder 回调]
    │  在 App 进程中执行
    │
    ↓
ActivityThread.handleLaunchActivity()
    │
    ├→ Activity.attach()
    │     [绑定 Window、Context]
    │
    └→ Activity.onCreate()
         [创建界面]
         ↓
       Activity.onStart()
         ↓
       Activity.onResume()
         ↓
       首帧绘制完成
        """.trimIndent()
    }
}
