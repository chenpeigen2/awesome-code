package com.peter.window.demo.viewroot.basic

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import com.peter.window.demo.databinding.ActivityViewrootBasicBinding

/**
 * 基础篇：ViewRootImpl 基础概念
 * 
 * ViewRootImpl 是 Android GUI 系统中最核心的类之一，
 * 它是连接 View 树和 WindowManagerService 的桥梁。
 * 
 * 主要职责：
 * 1. 管理 View 树的测量、布局、绘制
 * 2. 处理输入事件（触摸、键盘等）
 * 3. 与 WMS 通信（窗口添加、更新、删除）
 * 4. 管理 Surface 和 Canvas
 * 5. 协调 VSync 信号
 */
class ViewRootBasicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewrootBasicBinding
    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewrootBasicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showOverview()
    }

    private fun setupListeners() {
        binding.btnOverview.setOnClickListener { showOverview() }
        binding.btnGetViewRoot.setOnClickListener { showGetViewRoot() }
        binding.btnCoreFields.setOnClickListener { showCoreFields() }
        binding.btnCoreMethods.setOnClickListener { showCoreMethods() }
        binding.btnLifecycle.setOnClickListener { showLifecycle() }
        binding.btnRelation.setOnClickListener { showRelation() }
        binding.btnViewTreeObserver.setOnClickListener { demonstrateViewTreeObserver() }
    }

    /**
     * ViewRootImpl 概述
     */
    private fun showOverview() {
        sb.clear()
        sb.appendLine("=== ViewRootImpl 概述 ===\n")
        
        sb.appendLine("【什么是 ViewRootImpl】")
        sb.appendLine("ViewRootImpl 是 View 树的根，它不是 View，")
        sb.appendLine("而是管理整个 View 树的控制器。\n")
        
        sb.appendLine("【核心定位】")
        sb.appendLine("┌─────────────────────────────────────┐\n")
        sb.appendLine("│           Activity                  │\n")
        sb.appendLine("│              ↓                      │\n")
        sb.appendLine("│         PhoneWindow                 │\n")
        sb.appendLine("│              ↓                      │\n")
        sb.appendLine("│          DecorView                  │\n")
        sb.appendLine("│              ↓                      │\n")
        sb.appendLine("│       ┌──────────────┐              │\n")
        sb.appendLine("│       │ ViewRootImpl │ ← 核心！     │\n")
        sb.appendLine("│       └──────────────┘              │\n")
        sb.appendLine("│         ↓         ↓                 │\n")
        sb.appendLine("│    View 树      WMS                 │\n")
        sb.appendLine("└─────────────────────────────────────┘\n")
        
        sb.appendLine("【主要职责】")
        sb.appendLine("1. 【View 树管理】")
        sb.appendLine("   - 管理 DecorView 及其子 View")
        sb.appendLine("   - 触发测量、布局、绘制流程")
        sb.appendLine("   - 维护 View 的 AttachInfo\n")
        
        sb.appendLine("2. 【窗口管理】")
        sb.appendLine("   - 与 WMS 通信")
        sb.appendLine("   - 添加/更新/删除窗口")
        sb.appendLine("   - 处理窗口属性变化\n")
        
        sb.appendLine("3. 【输入事件处理】")
        sb.appendLine("   - 接收来自 InputManager 的事件")
        sb.appendLine("   - 分发触摸事件到 View 树")
        sb.appendLine("   - 处理键盘事件\n")
        
        sb.appendLine("4. 【渲染协调】")
        sb.appendLine("   - 与 Choreographer 协调")
        sb.appendLine("   - 接收 VSync 信号")
        sb.appendLine("   - 管理硬件加速渲染\n")
        
        sb.appendLine("【创建时机】")
        sb.appendLine("在 WindowManager.addView() 时创建：")
        sb.appendLine("```java")
        sb.appendLine("// WindowManagerGlobal.addView()")
        sb.appendLine("root = new ViewRootImpl(view.getContext(), display);")
        sb.appendLine("root.setView(view, wparams, panelParentView);")
        sb.appendLine("```\n")
        
        sb.appendLine("【重要特性】")
        sb.appendLine("- 每个 Window 对应一个 ViewRootImpl")
        sb.appendLine("- 只能在主线程操作 View")
        sb.appendLine("- 使用 Handler 进行线程通信")
        
        binding.tvContent.text = sb.toString()
    }

    /**
     * 获取 ViewRootImpl 的方式
     */
    private fun showGetViewRoot() {
        sb.clear()
        sb.appendLine("=== 获取 ViewRootImpl ===\n")
        
        sb.appendLine("【方法一：通过 View 获取】")
        sb.appendLine("```java")
        sb.appendLine("// 方式 1：通过 getViewRootImpl()")
        sb.appendLine("ViewRootImpl viewRoot = view.getViewRootImpl();")
        sb.appendLine("")
        sb.appendLine("// 方式 2：通过 AttachInfo")
        sb.appendLine("View.AttachInfo info = view.mAttachInfo;")
        sb.appendLine("if (info != null) {")
        sb.appendLine("    ViewRootImpl viewRoot = info.mViewRootImpl;")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【方法二：反射获取】")
        sb.appendLine("```java")
        sb.appendLine("public static ViewRootImpl getViewRootImpl(View view) {")
        sb.appendLine("    try {")
        sb.appendLine("        // 获取 AttachInfo")
        sb.appendLine("        Field attachInfoField = View.class.getDeclaredField(\"mAttachInfo\");")
        sb.appendLine("        attachInfoField.setAccessible(true);")
        sb.appendLine("        Object attachInfo = attachInfoField.get(view);")
        sb.appendLine("        ")
        sb.appendLine("        if (attachInfo != null) {")
        sb.appendLine("            // 获取 ViewRootImpl")
        sb.appendLine("            Field viewRootField = attachInfo.getClass()")
        sb.appendLine("                .getDeclaredField(\"mViewRootImpl\");")
        sb.appendLine("            viewRootField.setAccessible(true);")
        sb.appendLine("            return (ViewRootImpl) viewRootField.get(attachInfo);")
        sb.appendLine("        }")
        sb.appendLine("    } catch (Exception e) {")
        sb.appendLine("        e.printStackTrace();")
        sb.appendLine("    }")
        sb.appendLine("    return null;")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【方法三：通过 WindowManagerGlobal】")
        sb.appendLine("```java")
        sb.appendLine("public static ViewRootImpl getViewRootImpl() {")
        sb.appendLine("    try {")
        sb.appendLine("        WindowManagerGlobal wmg = WindowManagerGlobal.getInstance();")
        sb.appendLine("        ")
        sb.appendLine("        // 获取 ViewRootImpl 列表")
        sb.appendLine("        Field rootsField = WindowManagerGlobal.class")
        sb.appendLine("            .getDeclaredField(\"mRoots\");")
        sb.appendLine("        rootsField.setAccessible(true);")
        sb.appendLine("        List<ViewRootImpl> roots = (List<ViewRootImpl>) rootsField.get(wmg);")
        sb.appendLine("        ")
        sb.appendLine("        if (!roots.isEmpty()) {")
        sb.appendLine("            return roots.get(0);  // 返回第一个")
        sb.appendLine("        }")
        sb.appendLine("    } catch (Exception e) {")
        sb.appendLine("        e.printStackTrace();")
        sb.appendLine("    }")
        sb.appendLine("    return null;")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【注意事项】")
        sb.appendLine("1. View 必须已经 attach 到 Window")
        sb.appendLine("2. 反射方法可能在部分 ROM 上失效")
        sb.appendLine("3. ViewRootImpl 是 @hide API")
        
        binding.tvContent.text = sb.toString()
    }

    /**
     * ViewRootImpl 核心字段
     */
    private fun showCoreFields() {
        sb.clear()
        sb.appendLine("=== ViewRootImpl 核心字段 ===\n")
        
        sb.appendLine("【View 相关】")
        sb.appendLine("```java")
        sb.appendLine("// View 树的根视图")
        sb.appendLine("View mView;  // 通常是 DecorView")
        sb.appendLine("")
        sb.appendLine("// AttachInfo，包含 View 附加信息")
        sb.appendLine("final View.AttachInfo mAttachInfo;")
        sb.appendLine("")
        sb.appendLine("// View 的可见性")
        sb.appendLine("int mViewVisibility;")
        sb.appendLine("```\n")
        
        sb.appendLine("【窗口相关】")
        sb.appendLine("```java")
        sb.appendLine("// 窗口 Session，用于与 WMS 通信")
        sb.appendLine("final IWindowSession mWindowSession;")
        sb.appendLine("")
        sb.appendLine("// 窗口对象")
        sb.appendLine("final W mWindow;  // IWindow.Stub 实现")
        sb.appendLine("")
        sb.appendLine("// 窗口属性")
        sb.appendLine("WindowManager.LayoutParams mWindowAttributes;")
        sb.appendLine("")
        sb.appendLine("// 窗口尺寸")
        sb.appendLine("final Rect mWinFrame;  // 窗口实际尺寸")
        sb.appendLine("final Rect mVisibleInsets;  // 可见区域")
        sb.appendLine("final Rect mContentInsets;  // 内容区域")
        sb.appendLine("```\n")
        
        sb.appendLine("【渲染相关】")
        sb.appendLine("```java")
        sb.appendLine("// Choreographer，协调 VSync")
        sb.appendLine("final Choreographer mChoreographer;")
        sb.appendLine("")
        sb.appendLine("// Surface，绘制的画布")
        sb.appendLine("final Surface mSurface = new Surface();")
        sb.appendLine("")
        sb.appendLine("// 硬件渲染器")
        sb.appendLine("ThreadedRenderer mThreadedRenderer;")
        sb.appendLine("")
        sb.appendLine("// 是否使用硬件加速")
        sb.appendLine("boolean mHardwareAccelerated;")
        sb.appendLine("```\n")
        
        sb.appendLine("【输入相关】")
        sb.appendLine("```java")
        sb.appendLine("// 输入事件接收器")
        sb.appendLine("InputEventReceiver mInputEventReceiver;")
        sb.appendLine("")
        sb.appendLine("// 输入事件队列")
        sb.appendLine("QueuedInputEvent mPendingInputEventQueue;")
        sb.appendLine("")
        sb.appendLine("// 输入法管理器")
        sb.appendLine("InputMethodManager mInputMethodManager;")
        sb.appendLine("```\n")
        
        sb.appendLine("【布局相关】")
        sb.appendLine("```java")
        sb.appendLine("// 是否需要布局")
        sb.appendLine("boolean mLayoutRequested;")
        sb.appendLine("")
        sb.appendLine("// 是否在布局中")
        sb.appendLine("boolean mInLayout;")
        sb.appendLine("")
        sb.appendLine("// 布局同步屏障")
        sb.appendLine("int mTraversalBarrier;")
        sb.appendLine("")
        sb.appendLine("// 遍历是否已调度")
        sb.appendLine("boolean mTraversalScheduled;")
        sb.appendLine("```\n")
        
        sb.appendLine("【线程相关】")
        sb.appendLine("```java")
        sb.appendLine("// 主线程")
        sb.appendLine("final Thread mThread;")
        sb.appendLine("")
        sb.appendLine("// Handler")
        sb.appendLine("final ViewRootHandler mHandler;")
        sb.appendLine("```\n")
        
        sb.appendLine("【重要说明】")
        sb.appendLine("mThread 记录了 ViewRootImpl 创建时的线程，")
        sb.appendLine("后续所有 View 操作都会检查是否在同一线程。")
        
        binding.tvContent.text = sb.toString()
    }

    /**
     * ViewRootImpl 核心方法
     */
    private fun showCoreMethods() {
        sb.clear()
        sb.appendLine("=== ViewRootImpl 核心方法 ===\n")
        
        sb.appendLine("【初始化相关】")
        sb.appendLine("```java")
        sb.appendLine("// 设置 View 树")
        sb.appendLine("public void setView(View view, WindowManager.LayoutParams attrs,")
        sb.appendLine("        View panelParentView)")
        sb.appendLine("")
        sb.appendLine("// 初始化硬件渲染")
        sb.appendLine("private void enableHardwareAcceleration(WindowManager.LayoutParams attrs)")
        sb.appendLine("```\n")
        
        sb.appendLine("【布局相关】")
        sb.appendLine("```java")
        sb.appendLine("// 请求布局")
        sb.appendLine("public void requestLayout()")
        sb.appendLine("")
        sb.appendLine("// 调度遍历")
        sb.appendLine("void scheduleTraversals()")
        sb.appendLine("")
        sb.appendLine("// 执行遍历（核心！）")
        sb.appendLine("void performTraversals()")
        sb.appendLine("")
        sb.appendLine("// 测量")
        sb.appendLine("private void performMeasure(int widthMeasureSpec, int heightMeasureSpec)")
        sb.appendLine("")
        sb.appendLine("// 布局")
        sb.appendLine("private void performLayout(WindowManager.LayoutParams lp, int width, int height)")
        sb.appendLine("")
        sb.appendLine("// 绘制")
        sb.appendLine("private void performDraw()")
        sb.appendLine("private boolean draw(boolean fullRedrawNeeded)")
        sb.appendLine("```\n")
        
        sb.appendLine("【窗口相关】")
        sb.appendLine("```java")
        sb.appendLine("// 重新分配窗口尺寸")
        sb.appendLine("public void relayoutWindow(WindowManager.LayoutParams params,")
        sb.appendLine("        int viewVisibility, boolean insetsPending)")
        sb.appendLine("")
        sb.appendLine("// 分配窗口")
        sb.appendLine("private int relayoutWindow(WindowManager.LayoutParams params, int viewVisibility,")
        sb.appendLine("        boolean insetsPending, int relayoutResult)")
        sb.appendLine("")
        sb.appendLine("// 检查窗口变化")
        sb.appendLine("boolean checkCallingPermission(String permission)")
        sb.appendLine("```\n")
        
        sb.appendLine("【输入事件相关】")
        sb.appendLine("```java")
        sb.appendLine("// 分发输入事件")
        sb.appendLine("void deliverInputEvent(QueuedInputEvent q)")
        sb.appendLine("")
        sb.appendLine("// 处理触摸事件")
        sb.appendLine("private void processPointerEvent(QueuedInputEvent q)")
        sb.appendLine("")
        sb.appendLine("// 处理键盘事件")
        sb.appendLine("private void processKeyEvent(QueuedInputEvent q)")
        sb.appendLine("")
        sb.appendLine("// 处理轨迹球事件")
        sb.appendLine("private void processTrackballEvent(QueuedInputEvent q)")
        sb.appendLine("```\n")
        
        sb.appendLine("【Surface 相关】")
        sb.appendLine("```java")
        sb.appendLine("// 创建 Surface")
        sb.appendLine("private void performTraversals() {")
        sb.appendLine("    // ...")
        sb.appendLine("    if (mSurface.isValid()) {")
        sb.appendLine("        // Surface 已就绪")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("")
        sb.appendLine("// 销毁 Surface")
        sb.appendLine("void destroySurface()")
        sb.appendLine("```\n")
        
        sb.appendLine("【线程检查】")
        sb.appendLine("```java")
        sb.appendLine("// 检查是否在主线程")
        sb.appendLine("void checkThread() {")
        sb.appendLine("    if (mThread != Thread.currentThread()) {")
        sb.appendLine("        throw new CalledFromWrongThreadException(")
        sb.appendLine("            \"Only the original thread that created a view hierarchy can touch its views.\");")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【关键点】")
        sb.appendLine("performTraversals() 是最核心的方法，")
        sb.appendLine("它包含了完整的测量、布局、绘制流程。")
        
        binding.tvContent.text = sb.toString()
    }

    /**
     * ViewRootImpl 生命周期
     */
    private fun showLifecycle() {
        sb.clear()
        sb.appendLine("=== ViewRootImpl 生命周期 ===\n")
        
        sb.appendLine("【完整生命周期】")
        sb.appendLine("```\n")
        sb.appendLine("        创建")
        sb.appendLine("          │")
        sb.appendLine("          ↓")
        sb.appendLine("┌─────────────────────┐")
        sb.appendLine("│   ViewRootImpl      │")
        sb.appendLine("│                     │")
        sb.appendLine("│  1. new()           │")
        sb.appendLine("│  2. setView()       │")
        sb.appendLine("│     - requestLayout │")
        sb.appendLine("│     - addToDisplay  │")
        sb.appendLine("│  3. performTraversals│")
        sb.appendLine("│     - measure       │")
        sb.appendLine("│     - layout        │")
        sb.appendLine("│     - draw          │")
        sb.appendLine("│  4. 事件循环        │")
        sb.appendLine("│     - 接收输入      │")
        sb.appendLine("│     - 处理 VSync    │")
        sb.appendLine("│     - 更新视图      │")
        sb.appendLine("│  5. die()           │")
        sb.appendLine("│     - doDie()       │")
        sb.appendLine("│     - dispatchDetachedFromWindow │")
        sb.appendLine("└─────────────────────┘")
        sb.appendLine("          │")
        sb.appendLine("          ↓")
        sb.appendLine("        销毁")
        sb.appendLine("```\n")
        
        sb.appendLine("【创建阶段】")
        sb.appendLine("```java")
        sb.appendLine("// 1. 构造函数")
        sb.appendLine("public ViewRootImpl(Context context, Display display) {")
        sb.appendLine("    mContext = context;")
        sb.appendLine("    mThread = Thread.currentThread();  // 记录主线程")
        sb.appendLine("    mWindow = new W(this);  // 创建窗口 Binder")
        sb.appendLine("    mChoreographer = Choreographer.getInstance();")
        sb.appendLine("    mAttachInfo = new View.AttachInfo(...);")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【setView 阶段】")
        sb.appendLine("```java")
        sb.appendLine("public void setView(View view, WindowManager.LayoutParams attrs,")
        sb.appendLine("        View panelParentView) {")
        sb.appendLine("    mView = view;")
        sb.appendLine("    ")
        sb.appendLine("    // 请求第一次布局")
        sb.appendLine("    requestLayout();")
        sb.appendLine("    ")
        sb.appendLine("    // 添加到 WMS")
        sb.appendLine("    res = mWindowSession.addToDisplay(mWindow, mSeq,")
        sb.appendLine("            mWindowAttributes, getHostVisibility(),")
        sb.appendLine("            mDisplay.getDisplayId(), mWinFrame, ...);")
        sb.appendLine("    ")
        sb.appendLine("    // View 派发 attached")
        sb.appendLine("    view.assignParent(this);")
        sb.appendLine("    mAttachInfo.mTreeObserver.dispatchOnWindowAttachedChange(true);")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【运行阶段】")
        sb.appendLine("```java")
        sb.appendLine("// 持续响应 VSync 信号")
        sb.appendLine("void doTraversal() {")
        sb.appendLine("    performTraversals();")
        sb.appendLine("}")
        sb.appendLine("")
        sb.appendLine("// 接收输入事件")
        sb.appendLine("final class WindowInputEventReceiver extends InputEventReceiver {")
        sb.appendLine("    public void onInputEvent(InputEvent event) {")
        sb.appendLine("        enqueueInputEvent(event, this, ...);")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【销毁阶段】")
        sb.appendLine("```java")
        sb.appendLine("void die(boolean immediate) {")
        sb.appendLine("    if (immediate) {")
        sb.appendLine("        doDie();")
        sb.appendLine("    } else {")
        sb.appendLine("        sendEmptyMessage(MSG_DIE);")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("")
        sb.appendLine("void doDie() {")
        sb.appendLine("    dispatchDetachedFromWindow();")
        sb.appendLine("}")
        sb.appendLine("")
        sb.appendLine("void dispatchDetachedFromWindow() {")
        sb.appendLine("    // 1. 派发 detached 事件")
        sb.appendLine("    mAttachInfo.mTreeObserver.dispatchOnWindowAttachedChange(false);")
        sb.appendLine("    mView.dispatchDetachedFromWindow();")
        sb.appendLine("    ")
        sb.appendLine("    // 2. 移除窗口")
        sb.appendLine("    mWindowSession.remove(mWindow);")
        sb.appendLine("    ")
        sb.appendLine("    // 3. 销毁 Surface")
        sb.appendLine("    destroySurface();")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【Activity 生命周期对应】")
        sb.appendLine("onCreate()  → setContentView() → 创建 DecorView")
        sb.appendLine("onResume()  → wm.addView()    → 创建 ViewRootImpl")
        sb.appendLine("onPause()   → wm.removeView() → 销毁 ViewRootImpl")
        
        binding.tvContent.text = sb.toString()
    }

    /**
     * ViewRootImpl 与其他组件的关系
     */
    private fun showRelation() {
        sb.clear()
        sb.appendLine("=== ViewRootImpl 与其他组件的关系 ===\n")
        
        sb.appendLine("【整体架构图】")
        sb.appendLine("```\n")
        sb.appendLine("┌────────────────────────────────────────────────────────┐\n")
        sb.appendLine("│                    应用进程                            │\n")
        sb.appendLine("│                                                        │\n")
        sb.appendLine("│  Activity ─→ PhoneWindow ─→ DecorView                 │\n")
        sb.appendLine("│                                  │                     │\n")
        sb.appendLine("│                                  ↓                     │\n")
        sb.appendLine("│                            ViewRootImpl               │\n")
        sb.appendLine("│                            │    │    │                 │\n")
        sb.appendLine("│           ┌────────────────┘    │    └─────────────┐  │\n")
        sb.appendLine("│           ↓                     ↓                    ↓  │\n")
        sb.appendLine("│    ┌──────────────┐    ┌──────────────┐    ┌──────────┐│\n")
        sb.appendLine("│    │ Choreographer│    │    Surface   │    │ Handler  ││\n")
        sb.appendLine("│    └──────────────┘    └──────────────┘    └──────────┘│\n")
        sb.appendLine("│           │                     │                     │\n")
        sb.appendLine("└───────────│─────────────────────│─────────────────────┘\n")
        sb.appendLine("            │ (VSync)             │ (SurfaceFlinger)\n")
        sb.appendLine("            ↓                     ↓\n")
        sb.appendLine("┌────────────────────────────────────────────────────────┐\n")
        sb.appendLine("│                    系统进程                            │\n")
        sb.appendLine("│                                                        │\n")
        sb.appendLine("│    ┌──────────────┐           ┌──────────────┐        │\n")
        sb.appendLine("│    │SurfaceFlinger│←─────────│WMS           │        │\n")
        sb.appendLine("│    └──────────────┘           └──────────────┘        │\n")
        sb.appendLine("│                                        │              │\n")
        sb.appendLine("│                                        ↓              │\n")
        sb.appendLine("│                              ┌──────────────┐         │\n")
        sb.appendLine("│                              │InputManager  │         │\n")
        sb.appendLine("│                              └──────────────┘         │\n")
        sb.appendLine("└────────────────────────────────────────────────────────┘\n")
        sb.appendLine("```\n")
        
        sb.appendLine("【与 WindowManagerGlobal 的关系】")
        sb.appendLine("```java")
        sb.appendLine("// WindowManagerGlobal 管理所有 ViewRootImpl")
        sb.appendLine("public final class WindowManagerGlobal {")
        sb.appendLine("    // 所有 View 树")
        sb.appendLine("    private final ArrayList<View> mViews = new ArrayList<>();")
        sb.appendLine("    // 所有 ViewRootImpl")
        sb.appendLine("    private final ArrayList<ViewRootImpl> mRoots = new ArrayList<>();")
        sb.appendLine("    // 所有窗口参数")
        sb.appendLine("    private final ArrayList<WindowManager.LayoutParams> mParams = ...;")
        sb.appendLine("    ")
        sb.appendLine("    public void addView(View view, ...) {")
        sb.appendLine("        ViewRootImpl root = new ViewRootImpl(...);")
        sb.appendLine("        mViews.add(view);")
        sb.appendLine("        mRoots.add(root);")
        sb.appendLine("        mParams.add(params);")
        sb.appendLine("        root.setView(view, params, ...);")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【与 Choreographer 的关系】")
        sb.appendLine("```java")
        sb.appendLine("// ViewRootImpl 通过 Choreographer 接收 VSync")
        sb.appendLine("void scheduleTraversals() {")
        sb.appendLine("    mTraversalBarrier = mHandler.getLooper().getQueue()")
        sb.appendLine("        .postSyncBarrier();")
        sb.appendLine("    mChoreographer.postCallback(")
        sb.appendLine("        Choreographer.CALLBACK_TRAVERSAL,")
        sb.appendLine("        mTraversalRunnable, null);")
        sb.appendLine("}")
        sb.appendLine("")
        sb.appendLine("// VSync 信号到来时执行")
        sb.appendLine("final TraversalRunnable mTraversalRunnable = new TraversalRunnable();")
        sb.appendLine("final class TraversalRunnable implements Runnable {")
        sb.appendLine("    public void run() {")
        sb.appendLine("        doTraversal();")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【与 Surface 的关系】")
        sb.appendLine("```java")
        sb.appendLine("// ViewRootImpl 持有 Surface")
        sb.appendLine("final Surface mSurface = new Surface();")
        sb.appendLine("")
        sb.appendLine("// 通过 relayoutWindow 获取有效的 Surface")
        sb.appendLine("private int relayoutWindow(...) {")
        sb.appendLine("    int relayoutResult = mWindowSession.relayout(..., mSurface);")
        sb.appendLine("    // mSurface 现在可以用于绘制")
        sb.appendLine("}")
        sb.appendLine("")
        sb.appendLine("// 绘制时使用")
        sb.appendLine("private boolean draw(boolean fullRedrawNeeded) {")
        sb.appendLine("    Canvas canvas = mSurface.lockCanvas(dirty);")
        sb.appendLine("    mView.draw(canvas);")
        sb.appendLine("    mSurface.unlockCanvasAndPost(canvas);")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【与 WMS 的关系】")
        sb.appendLine("```java")
        sb.appendLine("// 通过 IWindowSession 与 WMS 通信")
        sb.appendLine("final IWindowSession mWindowSession;")
        sb.appendLine("")
        sb.appendLine("// 添加窗口")
        sb.appendLine("mWindowSession.addToDisplay(mWindow, ...);")
        sb.appendLine("")
        sb.appendLine("// 更新窗口")
        sb.appendLine("mWindowSession.relayout(mWindow, ...);")
        sb.appendLine("")
        sb.appendLine("// 移除窗口")
        sb.appendLine("mWindowSession.remove(mWindow);")
        sb.appendLine("```\n")
        
        sb.appendLine("【总结】")
        sb.appendLine("ViewRootImpl 是整个 GUI 系统的中枢：")
        sb.appendLine("- 对上：管理 View 树")
        sb.appendLine("- 对下：与系统服务通信")
        sb.appendLine("- 左：接收输入事件")
        sb.appendLine("- 右：协调渲染时机")
        
        binding.tvContent.text = sb.toString()
    }

    /**
     * ViewTreeObserver 使用示例
     */
    private fun demonstrateViewTreeObserver() {
        sb.clear()
        sb.appendLine("=== ViewTreeObserver 实践 ===\n")
        
        sb.appendLine("ViewTreeObserver 用于监听 View 树的变化\n")
        
        sb.appendLine("【常用监听器】")
        sb.appendLine("```java")
        sb.appendLine("// 全局布局监听")
        sb.appendLine("view.getViewTreeObserver().addOnGlobalLayoutListener(")
        sb.appendLine("    new ViewTreeObserver.OnGlobalLayoutListener() {")
        sb.appendLine("        @Override")
        sb.appendLine("        public void onGlobalLayout() {")
        sb.appendLine("            // 布局完成后的回调")
        sb.appendLine("            int width = view.getWidth();")
        sb.appendLine("            int height = view.getHeight();")
        sb.appendLine("        }")
        sb.appendLine("    });")
        sb.appendLine("")
        sb.appendLine("// 绘制前监听")
        sb.appendLine("view.getViewTreeObserver().addOnPreDrawListener(")
        sb.appendLine("    new ViewTreeObserver.OnPreDrawListener() {")
        sb.appendLine("        @Override")
        sb.appendLine("        public boolean onPreDraw() {")
        sb.appendLine("            // 返回 false 可以阻止本次绘制")
        sb.appendLine("            return true;")
        sb.appendLine("        }")
        sb.appendLine("    });")
        sb.appendLine("")
        sb.appendLine("// 窗口附加监听")
        sb.appendLine("view.getViewTreeObserver().addOnWindowAttachListener(")
        sb.appendLine("    new ViewTreeObserver.OnWindowAttachListener() {")
        sb.appendLine("        @Override")
        sb.appendLine("        public void onWindowAttached() {")
        sb.appendLine("            // 窗口已附加")
        sb.appendLine("        }")
        sb.appendLine("        @Override")
        sb.appendLine("        public void onWindowDetached() {")
        sb.appendLine("            // 窗口已分离")
        sb.appendLine("        }")
        sb.appendLine("    });")
        sb.appendLine("")
        sb.appendLine("// 窗口焦点变化监听")
        sb.appendLine("view.getViewTreeObserver().addOnWindowFocusChangeListener(")
        sb.appendLine("    new ViewTreeObserver.OnWindowFocusChangeListener() {")
        sb.appendLine("        @Override")
        sb.appendLine("        public void onWindowFocusChanged(boolean hasFocus) {")
        sb.appendLine("            // 窗口焦点变化")
        sb.appendLine("        }")
        sb.appendLine("    });")
        sb.appendLine("```\n")
        
        sb.appendLine("【实际示例】")
        // 添加实际监听
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    sb.appendLine("✓ onGlobalLayout 被调用！")
                    sb.appendLine("  root width = ${binding.root.width}")
                    sb.appendLine("  root height = ${binding.root.height}")
                    binding.tvContent.text = sb.toString()
                }
            }
        )
        
        sb.appendLine("正在等待布局完成...")
        
        binding.tvContent.text = sb.toString()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
