package com.peter.window.demo.deep

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.peter.window.demo.databinding.ActivityWmsBinding

/**
 * 深入篇：WindowManagerService (WMS)
 * 
 * WindowManagerService 是 Android 系统核心服务之一，负责管理所有窗口。
 * 
 * WMS 的主要职责：
 * 1. 窗口管理 - 添加、删除、更新窗口
 * 2. 窗口层级 - Z-Order 管理
 * 3. 窗口动画 - 切换动画、打开关闭动画
 * 4. 输入事件分发 - 触摸事件、按键事件
 * 5. Surface 管理 - 分配和管理 Surface
 * 
 * WMS 的架构：
 * 
 * ```
 * 应用进程                    系统进程
 *     │                         │
 * WindowManagerImpl             │
 *     │                         │
 * WindowManagerGlobal           │
 *     │                         │
 * ViewRootImpl                  │
 *     │                         │
 * IWindowSession ────────────→ IWindowSession (Binder)
 *     │                         │
 *     │                    WindowManagerService
 *     │                         │
 *     │                    ├── WindowState
 *     │                    ├── WindowToken
 *     │                    └── DisplayContent
 *     │                         │
 *     │                    SurfaceFlinger
 *     │                         │
 * IWindow ←──────────────── IWindow (Binder)
 *     │                         │
 * Surface                   Surface
 * ```
 */
class WMSActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWmsBinding
    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWmsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showWMSOverview()
    }

    private fun setupListeners() {
        binding.btnOverview.setOnClickListener { showWMSOverview() }
        binding.btnWindowState.setOnClickListener { showWindowState() }
        binding.btnWindowToken.setOnClickListener { showWindowToken() }
        binding.btnZOrder.setOnClickListener { showZOrder() }
        binding.btnInputDispatch.setOnClickListener { showInputDispatch() }
        binding.btnSurfaceManagement.setOnClickListener { showSurfaceManagement() }
    }

    private fun showWMSOverview() {
        sb.clear()
        sb.appendLine("=== WMS 概述 ===\n")
        
        sb.appendLine("WindowManagerService 是系统级服务\n")
        
        sb.appendLine("【启动过程】")
        sb.appendLine("```java")
        sb.appendLine("// SystemServer.startOtherServices()")
        sb.appendLine("WindowManagerService wm = WindowManagerService.main(...);")
        sb.appendLine("ServiceManager.addService(Context.WINDOW_SERVICE, wm);")
        sb.appendLine("```\n")
        
        sb.appendLine("【主要职责】")
        sb.appendLine("1. 窗口管理")
        sb.appendLine("   - addWindow() 添加窗口")
        sb.appendLine("   - removeWindow() 移除窗口")
        sb.appendLine("   - relayoutWindow() 更新窗口布局\n")
        sb.appendLine("2. 窗口层级管理")
        sb.appendLine("   - 维护窗口 Z-Order")
        sb.appendLine("   - 管理窗口显示顺序\n")
        sb.appendLine("3. 输入事件管理")
        sb.appendLine("   - 接收 InputManager 的事件")
        sb.appendLine("   - 分发到正确的窗口\n")
        sb.appendLine("4. Surface 管理")
        sb.appendLine("   - 与 SurfaceFlinger 交互")
        sb.appendLine("   - 分配 Surface 给窗口\n")
        
        sb.appendLine("【架构图】")
        sb.appendLine("```\n")
        sb.appendLine("┌─────────────────────────────────────┐")
        sb.appendLine("│           应用进程                   │")
        sb.appendLine("│  ┌─────────────────────────────┐    │")
        sb.appendLine("│  │  ViewRootImpl               │    │")
        sb.appendLine("│  │    ↓                        │    │")
        sb.appendLine("│  │  IWindowSession (Binder) ───┼────┼──┐")
        sb.appendLine("│  └─────────────────────────────┘    │  │")
        sb.appendLine("└─────────────────────────────────────┘  │")
        sb.appendLine("                                         │")
        sb.appendLine("┌─────────────────────────────────────┐  │")
        sb.appendLine("│           系统进程                   │  │")
        sb.appendLine("│  ┌─────────────────────────────┐    │  │")
        sb.appendLine("│  │  WindowManagerService  ←────┼────┼──┘")
        sb.appendLine("│  │    │                        │    │")
        sb.appendLine("│  │    ├─ WindowState           │    │")
        sb.appendLine("│  │    ├─ WindowToken           │    │")
        sb.appendLine("│  │    └─ DisplayContent        │    │")
        sb.appendLine("│  └─────────────────────────────┘    │")
        sb.appendLine("└─────────────────────────────────────┘")
        sb.appendLine("```")
        
        binding.tvContent.text = sb.toString()
    }

    private fun showWindowState() {
        sb.clear()
        sb.appendLine("=== WindowState ===\n")
        
        sb.appendLine("WindowState 表示一个窗口的状态信息\n")
        
        sb.appendLine("【核心属性】")
        sb.appendLine("```java")
        sb.appendLine("class WindowState extends WindowContainer<WindowState> {")
        sb.appendLine("    final Session mSession;         // 窗口会话")
        sb.appendLine("    final IWindow mClient;          // 客户端窗口 Binder")
        sb.appendLine("    WindowToken mToken;             // 窗口令牌")
        sb.appendLine("    ")
        sb.appendLine("    int mBaseLayer;                 // 基础层级")
        sb.appendLine("    int mSubLayer;                  // 子层级")
        sb.appendLine("    ")
        sb.appendLine("    int mAttrs;                     // LayoutParams")
        sb.appendLine("    Rect mFrame;                    // 窗口位置")
        sb.appendLine("    Rect mVisibleFrame;             // 可见区域")
        sb.appendLine("    ")
        sb.appendLine("    SurfaceControl mSurfaceControl; // Surface 控制")
        sb.appendLine("    ")
        sb.appendLine("    boolean mHasSurface;            // 是否有 Surface")
        sb.appendLine("    boolean mIsVisible;             // 是否可见")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【窗口状态变化】")
        sb.appendLine("```\n")
        sb.appendLine("WindowState.Agent.onStateChanged(states)")
        sb.appendLine("    │")
        sb.appendLine("    ├── NO_SURFACE         // 无 Surface")
        sb.appendLine("    ├── DRAW_PENDING       // 等待绘制")
        sb.appendLine("    ├── COMMIT_DRAW_PENDING // 等待提交")
        sb.appendLine("    ├── READY_TO_SHOW      // 准备显示")
        sb.appendLine("    ├── HAS_DRAWN          // 已绘制")
        sb.appendLine("    └── READY_TO_HIDE      // 准备隐藏")
        sb.appendLine("```\n")
        
        sb.appendLine("【窗口可见性】")
        sb.appendLine("- mViewVisibility: View 的可见性")
        sb.appendLine("- mAppOpVisibility: AppOps 限制")
        sb.appendLine("- mPermanentlyHidden: 永久隐藏")
        sb.appendLine("- mAnimating: 是否在动画中")
        
        binding.tvContent.text = sb.toString()
    }

    private fun showWindowToken() {
        sb.clear()
        sb.appendLine("=== WindowToken ===\n")
        
        sb.appendLine("WindowToken 是窗口的令牌，用于标识窗口的归属\n")
        
        sb.appendLine("【核心概念】")
        sb.appendLine("```java")
        sb.appendLine("class WindowToken extends WindowContainer<WindowState> {")
        sb.appendLine("    final IBinder token;            // Binder 令牌")
        sb.appendLine("    final int windowType;           // 窗口类型")
        sb.appendLine("    final boolean ownerCanManageAppTokens; // 是否可管理")
        sb.appendLine("    ")
        sb.appendLine("    // 关联的 Activity")
        sb.appendLine("    ActivityRecord activity;        // 如果是 Activity 窗口")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【Token 的作用】")
        sb.appendLine("1. 安全验证")
        sb.appendLine("   - 防止恶意应用伪造窗口")
        sb.appendLine("   - 确保窗口的合法性\n")
        sb.appendLine("2. 窗口分组")
        sb.appendLine("   - 同一 Token 的窗口属于同一应用")
        sb.appendLine("   - 便于统一管理\n")
        sb.appendLine("3. 窗口层级")
        sb.appendLine("   - 同一 Token 的窗口在同一层级\n")
        
        sb.appendLine("【Token 类型】")
        sb.appendLine("```java")
        sb.appendLine("// Activity Token")
        sb.appendLine("ActivityRecord.appToken")
        sb.appendLine("    └── 用于 Activity 窗口\n")
        sb.appendLine("// Application Token")
        sb.appendLine("IApplicationToken")
        sb.appendLine("    └── 用于 Application 窗口\n")
        sb.appendLine("// 系统窗口 Token")
        sb.appendLine("// 系统窗口使用特定的 Token")
        sb.appendLine("```\n")
        
        sb.appendLine("【Activity 窗口的 Token】")
        sb.appendLine("```java")
        sb.appendLine("// Activity.attach()")
        sb.appendLine("mToken = new Binder();")
        sb.appendLine("// 传递给 PhoneWindow")
        sb.appendLine("mWindow.setWindowManager(..., mToken, ...);")
        sb.appendLine("```")
        
        binding.tvContent.text = sb.toString()
    }

    private fun showZOrder() {
        sb.clear()
        sb.appendLine("=== Z-Order 层级管理 ===\n")
        
        sb.appendLine("Z-Order 决定窗口的前后显示顺序\n")
        
        sb.appendLine("【层级计算】")
        sb.appendLine("```java")
        sb.appendLine("int baseLayer = typeToLayer(windowType);")
        sb.appendLine("int subLayer = getSubLayer(windowType);")
        sb.appendLine("int finalLayer = baseLayer * TYPE_LAYER_MULTIPLIER + subLayer;")
        sb.appendLine("```\n")
        
        sb.appendLine("【窗口层级范围】")
        sb.appendLine("```\n")
        sb.appendLine("层级范围          窗口类型")
        sb.appendLine("────────────────────────────")
        sb.appendLine("1 - 2            TYPE_WALLPAPER")
        sb.appendLine("2 - 999          应用窗口")
        sb.appendLine("1000 - 1999      子窗口")
        sb.appendLine("2000 - 2999      系统窗口")
        sb.appendLine("    2000 - 2010  TYPE_PHONE 等")
        sb.appendLine("    2010 - 2020  TYPE_SYSTEM_ALERT")
        sb.appendLine("    2030 - 2040  TYPE_INPUT_METHOD")
        sb.appendLine("    2040 - 2050  TYPE_WALLPAPER")
        sb.appendLine("    2100 - 2200  TYPE_STATUS_BAR")
        sb.appendLine("    2200 - 2300  TYPE_KEYGUARD")
        sb.appendLine("    2300 - 2400  TYPE_TOAST")
        sb.appendLine("```\n")
        
        sb.appendLine("【层级示意图】")
        sb.appendLine("```\n")
        sb.appendLine("  ↑ 高层级 (前台)")
        sb.appendLine("  │")
        sb.appendLine("  ├── 键盘")
        sb.appendLine("  │")
        sb.appendLine("  ├── 状态栏")
        sb.appendLine("  │")
        sb.appendLine("  ├── Toast")
        sb.appendLine("  │")
        sb.appendLine("  ├── 悬浮窗")
        sb.appendLine("  │")
        sb.appendLine("  ├── Dialog")
        sb.appendLine("  │")
        sb.appendLine("  ├── Activity 窗口")
        sb.appendLine("  │")
        sb.appendLine("  ├── 壁纸")
        sb.appendLine("  │")
        sb.appendLine("  ↓ 低层级 (后台)")
        sb.appendLine("```\n")
        
        sb.appendLine("【同一层级内的排序】")
        sb.appendLine("- 使用 subLayer 进一步区分")
        sb.appendLine("- 动态调整：bringToFront(), sendToBack()")
        
        binding.tvContent.text = sb.toString()
    }

    private fun showInputDispatch() {
        sb.clear()
        sb.appendLine("=== 输入事件分发 ===\n")
        
        sb.appendLine("输入事件通过 WMS 分发到正确的窗口\n")
        
        sb.appendLine("【输入事件流程】")
        sb.appendLine("```\n")
        sb.appendLine("硬件设备 (触摸屏/键盘)")
        sb.appendLine("    ↓")
        sb.appendLine("EventHub (读取原始事件)")
        sb.appendLine("    ↓")
        sb.appendLine("InputReader (解析事件)")
        sb.appendLine("    ↓")
        sb.appendLine("InputDispatcher (分发事件)")
        sb.appendLine("    ↓")
        sb.appendLine("WindowManagerService")
        sb.appendLine("    ↓")
        sb.appendLine("WindowState (找到目标窗口)")
        sb.appendLine("    ↓")
        sb.appendLine("IWindow (Binder 调用)")
        sb.appendLine("    ↓")
        sb.appendLine("ViewRootImpl")
        sb.appendLine("    ↓")
        sb.appendLine("View.dispatchTouchEvent()")
        sb.appendLine("```\n")
        
        sb.appendLine("【窗口选择】")
        sb.appendLine("```java")
        sb.appendLine("// InputDispatcher.findFocusedWindowTargets()")
        sb.appendLine("WindowState window = mWindowManager.getFocusedWindow();")
        sb.appendLine("// 检查窗口是否可见、可接收输入")
        sb.appendLine("if (window.isVisible() && window.isInputMethodTarget()) {")
        sb.appendLine("    // 分发到该窗口")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【触摸事件分发】")
        sb.appendLine("1. 找到触摸点所在的窗口")
        sb.appendLine("2. 检查窗口可见性")
        sb.appendLine("3. 检查窗口是否可接收触摸")
        sb.appendLine("4. 通过 Binder 发送到应用进程\n")
        
        sb.appendLine("【输入法窗口处理】")
        sb.appendLine("- 输入法窗口有特殊层级")
        sb.appendLine("- 可以拦截部分输入事件")
        sb.appendLine("- 与编辑框交互")
        
        binding.tvContent.text = sb.toString()
    }

    private fun showSurfaceManagement() {
        sb.clear()
        sb.appendLine("=== Surface 管理 ===\n")
        
        sb.appendLine("Surface 是窗口的绘制表面\n")
        
        sb.appendLine("【Surface 的创建】")
        sb.appendLine("```\n")
        sb.appendLine("ViewRootImpl.relayoutWindow()")
        sb.appendLine("    ↓")
        sb.appendLine("WindowSession.relayout()")
        sb.appendLine("    ↓")
        sb.appendLine("WindowManagerService.relayoutWindow()")
        sb.appendLine("    ↓")
        sb.appendLine("WindowStateAnimator.createSurfaceLocked()")
        sb.appendLine("    ↓")
        sb.appendLine("SurfaceControl.Builder.build()")
        sb.appendLine("    ↓")
        sb.appendLine("SurfaceFlinger.createLayer()")
        sb.appendLine("```\n")
        
        sb.appendLine("【SurfaceControl】")
        sb.appendLine("```java")
        sb.appendLine("SurfaceControl surfaceControl = new SurfaceControl.Builder()")
        sb.appendLine("    .setName(\"Window Name\")")
        sb.appendLine("    .setBufferSize(width, height)")
        sb.appendLine("    .setFormat(PixelFormat.TRANSLUCENT)")
        sb.appendLine("    .build();")
        sb.appendLine("```\n")
        
        sb.appendLine("【Surface 绘制流程】")
        sb.appendLine("```\n")
        sb.appendLine("ViewRootImpl.performDraw()")
        sb.appendLine("    ↓")
        sb.appendLine("Canvas canvas = mSurface.lockCanvas()")
        sb.appendLine("    ↓")
        sb.appendLine("View.draw(canvas)")
        sb.appendLine("    ↓")
        sb.appendLine("mSurface.unlockCanvasAndPost(canvas)")
        sb.appendLine("    ↓")
        sb.appendLine("SurfaceFlinger 合成显示")
        sb.appendLine("```\n")
        
        sb.appendLine("【SurfaceFlinger】")
        sb.appendLine("- 独立进程，负责合成所有 Surface")
        sb.appendLine("- 使用 OpenGL/Vulkan 进行硬件合成")
        sb.appendLine("- 将合成结果发送到显示屏")
        sb.appendLine("- 处理 VSync 信号\n")
        
        sb.appendLine("【BufferQueue】")
        sb.appendLine("```\n")
        sb.appendLine("┌──────────────────────────────────┐")
        sb.appendLine("│          BufferQueue             │")
        sb.appendLine("│  ┌─────┐ ┌─────┐ ┌─────┐        │")
        sb.appendLine("│  │ B1  │ │ B2  │ │ B3  │ (缓冲区)│")
        sb.appendLine("│  └─────┘ └─────┘ └─────┘        │")
        sb.appendLine("│       ↑                   ↓      │")
        sb.appendLine("│  Producer            Consumer   │")
        sb.appendLine("│  (应用绘制)         (合成显示)   │")
        sb.appendLine("└──────────────────────────────────┘")
        sb.appendLine("```")
        
        binding.tvContent.text = sb.toString()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
