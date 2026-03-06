package com.peter.window.demo.advanced

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.peter.window.demo.databinding.ActivityWindowMechanismBinding

/**
 * 高级篇：Window 内部机制
 * 
 * 本Activity深入讲解Window的内部实现机制：
 * 
 * 1. Window 的创建过程
 * 2. Window 的删除过程
 * 3. Window 的更新过程
 * 
 * 核心类：
 * - Window: 抽象窗口
 * - PhoneWindow: Window的实现类
 * - WindowManager: 窗口管理接口
 * - WindowManagerImpl: WindowManager的实现
 * - WindowManagerGlobal: 全局窗口管理器
 * - ViewRootImpl: View的根实现
 * - WindowSession: 窗口会话
 * - WindowManagerService: 系统窗口服务
 * 
 * Window 操作的调用链：
 * 
 * ```
 * Activity.setContentView()
 *     ↓
 * PhoneWindow.setContentView()
 *     ↓
 * installDecor() → 创建 DecorView
 *     ↓
 * ActivityThread.handleResumeActivity()
 *     ↓
 * WindowManagerImpl.addView(decorView, params)
 *     ↓
 * WindowManagerGlobal.addView()
 *     ↓
 * 创建 ViewRootImpl
 *     ↓
 * ViewRootImpl.setView()
 *     ↓
 * WindowSession.addToDisplay()
 *     ↓
 * WindowManagerService.addWindow()
 * ```
 */
class WindowMechanismActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWindowMechanismBinding
    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWindowMechanismBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showWindowCreation()
    }

    private fun setupListeners() {
        binding.btnCreation.setOnClickListener { showWindowCreation() }
        binding.btnDeletion.setOnClickListener { showWindowDeletion() }
        binding.btnUpdate.setOnClickListener { showWindowUpdate() }
        binding.btnCoreClasses.setOnClickListener { showCoreClasses() }
        binding.btnViewRootImpl.setOnClickListener { showViewRootImpl() }
    }

    /**
     * Window 的创建过程
     */
    private fun showWindowCreation() {
        sb.clear()
        sb.appendLine("=== Window 创建过程 ===\n")
        
        sb.appendLine("1. Activity.setContentView()")
        sb.appendLine("   ↓")
        sb.appendLine("2. PhoneWindow.setContentView()")
        sb.appendLine("   - installDecor() 创建 DecorView")
        sb.appendLine("   - 生成 mContentParent (id = content)")
        sb.appendLine("   ↓")
        sb.appendLine("3. ActivityThread.handleResumeActivity()")
        sb.appendLine("   - 调用 Activity.onResume()")
        sb.appendLine("   - 将 DecorView 添加到 WindowManager")
        sb.appendLine("   ↓")
        sb.appendLine("4. WindowManagerImpl.addView()")
        sb.appendLine("   - 委托给 WindowManagerGlobal")
        sb.appendLine("   ↓")
        sb.appendLine("5. WindowManagerGlobal.addView()")
        sb.appendLine("   ```java")
        sb.appendLine("   public void addView(View view, LayoutParams params) {")
        sb.appendLine("       // 创建 ViewRootImpl")
        sb.appendLine("       ViewRootImpl root = new ViewRootImpl(view.getContext(), display);")
        sb.appendLine("       // 保存到列表")
        sb.appendLine("       mViews.add(view);")
        sb.appendLine("       mRoots.add(root);")
        sb.appendLine("       mParams.add(params);")
        sb.appendLine("       // 设置 View")
        sb.appendLine("       root.setView(view, params, panelParentView);")
        sb.appendLine("   }")
        sb.appendLine("   ```")
        sb.appendLine("   ↓")
        sb.appendLine("6. ViewRootImpl.setView()")
        sb.appendLine("   - 触发第一次 layout")
        sb.appendLine("   - 通过 WindowSession 添加到 WMS")
        sb.appendLine("   ↓")
        sb.appendLine("7. WindowSession.addToDisplay()")
        sb.appendLine("   - 跨进程调用 WindowManagerService")
        sb.appendLine("   ↓")
        sb.appendLine("8. WindowManagerService.addWindow()")
        sb.appendLine("   - 分配 WindowToken")
        sb.appendLine("   - 创建 WindowState")
        sb.appendLine("   - 分配 Surface")
        
        binding.tvContent.text = sb.toString()
    }

    /**
     * Window 的删除过程
     */
    private fun showWindowDeletion() {
        sb.clear()
        sb.appendLine("=== Window 删除过程 ===\n")
        
        sb.appendLine("1. Activity.finish()")
        sb.appendLine("   ↓")
        sb.appendLine("2. ActivityThread.handleDestroyActivity()")
        sb.appendLine("   - 调用 Activity.onDestroy()")
        sb.appendLine("   ↓")
        sb.appendLine("3. WindowManagerImpl.removeViewImmediate()")
        sb.appendLine("   - 委托给 WindowManagerGlobal")
        sb.appendLine("   ↓")
        sb.appendLine("4. WindowManagerGlobal.removeViewLocked()")
        sb.appendLine("   ```java")
        sb.appendLine("   public void removeView(View view, boolean immediate) {")
        sb.appendLine("       // 查找 View 的索引")
        sb.appendLine("       int index = findViewLocked(view, true);")
        sb.appendLine("       // 从列表中移除")
        sb.appendLine("       ViewRootImpl root = mRoots.get(index);")
        sb.appendLine("       // 调用 ViewRootImpl.die()")
        sb.appendLine("       root.die(immediate);")
        sb.appendLine("   }")
        sb.appendLine("   ```")
        sb.appendLine("   ↓")
        sb.appendLine("5. ViewRootImpl.die()")
        sb.appendLine("   - 立即模式: doDie()")
        sb.appendLine("   - 非立即模式: 发送 MSG_DIE 消息")
        sb.appendLine("   ↓")
        sb.appendLine("6. ViewRootImpl.doDie()")
        sb.appendLine("   - 调用 dispatchDetachedFromWindow()")
        sb.appendLine("   ↓")
        sb.appendLine("7. dispatchDetachedFromWindow()")
        sb.appendLine("   - view.dispatchDetachedFromWindow()")
        sb.appendLine("   - WindowSession.remove()")
        sb.appendLine("   ↓")
        sb.appendLine("8. WindowManagerService.removeWindow()")
        sb.appendLine("   - 移除 WindowState")
        sb.appendLine("   - 释放 Surface")
        
        binding.tvContent.text = sb.toString()
    }

    /**
     * Window 的更新过程
     */
    private fun showWindowUpdate() {
        sb.clear()
        sb.appendLine("=== Window 更新过程 ===\n")
        
        sb.appendLine("当调用 requestLayout() 或更新 LayoutParams 时：\n")
        
        sb.appendLine("1. View.requestLayout()")
        sb.appendLine("   - 向上传递到 ViewRootImpl")
        sb.appendLine("   ↓")
        sb.appendLine("2. ViewRootImpl.requestLayout()")
        sb.appendLine("   ```java")
        sb.appendLine("   public void requestLayout() {")
        sb.appendLine("       if (!mHandlingLayoutInLayoutRequest) {")
        sb.appendLine("           checkThread(); // 检查线程")
        sb.appendLine("           mLayoutRequested = true;")
        sb.appendLine("           scheduleTraversals();")
        sb.appendLine("       }")
        sb.appendLine("   }")
        sb.appendLine("   ```")
        sb.appendLine("   ↓")
        sb.appendLine("3. scheduleTraversals()")
        sb.appendLine("   - 发送同步屏障消息")
        sb.appendLine("   - 发送 Choreographer 回调")
        sb.appendLine("   ↓")
        sb.appendLine("4. performTraversals()")
        sb.appendLine("   - performMeasure() - 测量")
        sb.appendLine("   - performLayout() - 布局")
        sb.appendLine("   - performDraw() - 绘制")
        sb.appendLine("   ↓")
        sb.appendLine("5. 更新 Window 参数")
        sb.appendLine("   - WindowSession.relayout()")
        sb.appendLine("   ↓")
        sb.appendLine("6. WindowManagerService.relayoutWindow()")
        sb.appendLine("   - 更新 WindowState")
        sb.appendLine("   - 重新分配 Surface（如果需要）\n")
        
        sb.appendLine("=== 更新 LayoutParams ===\n")
        sb.appendLine("WindowManager.updateViewLayout()")
        sb.appendLine("   ↓")
        sb.appendLine("WindowManagerGlobal.updateViewLayout()")
        sb.appendLine("   - 更新 mParams 列表")
        sb.appendLine("   - 调用 ViewRootImpl.setLayoutParams()")
        sb.appendLine("   ↓")
        sb.appendLine("ViewRootImpl.setLayoutParams()")
        sb.appendLine("   - scheduleTraversals()")
        sb.appendLine("   ↓")
        sb.appendLine("performTraversals() → WindowSession.relayout()")
        
        binding.tvContent.text = sb.toString()
    }

    /**
     * 核心类详解
     */
    private fun showCoreClasses() {
        sb.clear()
        sb.appendLine("=== 核心类详解 ===\n")
        
        sb.appendLine("【Window】")
        sb.appendLine("抽象类，定义窗口的基本操作")
        sb.appendLine("```java")
        sb.appendLine("public abstract class Window {")
        sb.appendLine("    private WindowManager mWindowManager;")
        sb.appendLine("    private Callback mCallback;")
        sb.appendLine("    ")
        sb.appendLine("    public abstract View getDecorView();")
        sb.appendLine("    public abstract void setContentView(View view);")
        sb.appendLine("    public WindowManager getWindowManager() { ... }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【PhoneWindow】")
        sb.appendLine("Window 的唯一实现类")
        sb.appendLine("```java")
        sb.appendLine("public class PhoneWindow extends Window {")
        sb.appendLine("    private DecorView mDecor;")
        sb.appendLine("    private ViewGroup mContentParent;")
        sb.appendLine("    ")
        sb.appendLine("    private void installDecor() {")
        sb.appendLine("        if (mDecor == null) {")
        sb.appendLine("            mDecor = new DecorView(this);")
        sb.appendLine("        }")
        sb.appendLine("        if (mContentParent == null) {")
        sb.appendLine("            mContentParent = generateLayout(mDecor);")
        sb.appendLine("        }")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【WindowManagerImpl】")
        sb.appendLine("WindowManager 的实现，委托给 WindowManagerGlobal")
        sb.appendLine("```java")
        sb.appendLine("public final class WindowManagerImpl implements WindowManager {")
        sb.appendLine("    private final WindowManagerGlobal mGlobal;")
        sb.appendLine("    ")
        sb.appendLine("    public void addView(View view, LayoutParams params) {")
        sb.appendLine("        mGlobal.addView(view, params, ...);")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【WindowManagerGlobal】")
        sb.appendLine("全局窗口管理器，管理所有窗口")
        sb.appendLine("```java")
        sb.appendLine("public final class WindowManagerGlobal {")
        sb.appendLine("    private final ArrayList<View> mViews = new ArrayList<>();")
        sb.appendLine("    private final ArrayList<ViewRootImpl> mRoots = new ArrayList<>();")
        sb.appendLine("    private final ArrayList<LayoutParams> mParams = new ArrayList<>();")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        binding.tvContent.text = sb.toString()
    }

    /**
     * ViewRootImpl 详解
     */
    private fun showViewRootImpl() {
        sb.clear()
        sb.appendLine("=== ViewRootImpl 详解 ===\n")
        
        sb.appendLine("ViewRootImpl 是连接 View 和 WindowManagerService 的桥梁\n")
        
        sb.appendLine("【主要职责】")
        sb.appendLine("1. 管理 View 树的测量、布局、绘制")
        sb.appendLine("2. 处理输入事件（触摸、按键）")
        sb.appendLine("3. 与 WMS 通信")
        sb.appendLine("4. 管理 Surface\n")
        
        sb.appendLine("【核心方法】")
        sb.appendLine("```java")
        sb.appendLine("public final class ViewRootImpl {")
        sb.appendLine("    View mView;           // 根视图")
        sb.appendLine("    IWindowSession mSession;  // 窗口会话")
        sb.appendLine("    Surface mSurface;     // 绘制表面")
        sb.appendLine("    ")
        sb.appendLine("    // 设置根视图")
        sb.appendLine("    public void setView(View view, LayoutParams attrs) {")
        sb.appendLine("        // 请求布局")
        sb.appendLine("        requestLayout();")
        sb.appendLine("        // 添加到 WMS")
        sb.appendLine("        mSession.addToDisplay(...);")
        sb.appendLine("    }")
        sb.appendLine("    ")
        sb.appendLine("    // 请求布局")
        sb.appendLine("    public void requestLayout() {")
        sb.appendLine("        checkThread();  // 检查是否在主线程")
        sb.appendLine("        scheduleTraversals();")
        sb.appendLine("    }")
        sb.appendLine("    ")
        sb.appendLine("    // 执行遍历")
        sb.appendLine("    void performTraversals() {")
        sb.appendLine("        performMeasure();")
        sb.appendLine("        performLayout();")
        sb.appendLine("        performDraw();")
        sb.appendLine("    }")
        sb.appendLine("    ")
        sb.appendLine("    // 检查线程")
        sb.appendLine("    void checkThread() {")
        sb.appendLine("        if (mThread != Thread.currentThread()) {")
        sb.appendLine("            throw new CalledFromWrongThreadException(...);")
        sb.appendLine("        }")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【线程检查】")
        sb.appendLine("ViewRootImpl 会在创建时记录当前线程")
        sb.appendLine("后续所有 UI 操作必须在该线程执行")
        sb.appendLine("这就是为什么不能在子线程更新 UI")
        
        binding.tvContent.text = sb.toString()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
