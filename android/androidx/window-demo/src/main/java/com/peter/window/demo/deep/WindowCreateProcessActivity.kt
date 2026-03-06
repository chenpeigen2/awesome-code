package com.peter.window.demo.deep

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.peter.window.demo.databinding.ActivityWindowCreateProcessBinding

/**
 * 深入篇：Window 创建过程
 * 
 * 本Activity详细讲解从 Activity 启动到 Window 显示的完整过程。
 * 
 * 完整流程图：
 * 
 * ```
 * ActivityThread.main()
 *     ↓
 * ActivityThread.attach()
 *     ↓
 * ActivityManagerService.attachApplication()
 *     ↓
 * ActivityStackSupervisor.attachApplicationLocked()
 *     ↓
 * ActivityThread.handleLaunchActivity()
 *     ↓
 * ActivityThread.performLaunchActivity()
 *     ├── 创建 Activity
 *     ├── Activity.attach() → 创建 PhoneWindow
 *     └── Activity.setContentView() → 创建 DecorView
 *     ↓
 * ActivityThread.handleResumeActivity()
 *     ├── Activity.performResume()
 *     └── WindowManager.addView(DecorView)
 *     ↓
 * ViewRootImpl.setView()
 *     ├── requestLayout() → 第一次布局
 *     └── WindowSession.addToDisplay()
 *     ↓
 * WindowManagerService.addWindow()
 *     ├── 创建 WindowState
 *     └── 分配 Surface
 *     ↓
 * ViewRootImpl.performTraversals()
 *     ├── performMeasure()
 *     ├── performLayout()
 *     └── performDraw()
 * ```
 */
class WindowCreateProcessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWindowCreateProcessBinding
    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWindowCreateProcessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showFullProcess()
    }

    private fun setupListeners() {
        binding.btnFullProcess.setOnClickListener { showFullProcess() }
        binding.btnActivityAttach.setOnClickListener { showActivityAttach() }
        binding.btnSetContentView.setOnClickListener { showSetContentView() }
        binding.btnResumeActivity.setOnClickListener { showResumeActivity() }
        binding.btnViewRootImpl.setOnClickListener { showViewRootImplSetView() }
        binding.btnWmsAddWindow.setOnClickListener { showWmsAddWindow() }
        binding.btnFirstTraversals.setOnClickListener { showFirstTraversals() }
    }

    private fun showFullProcess() {
        sb.clear()
        sb.appendLine("=== Window 创建完整流程 ===\n")
        
        sb.appendLine("```\n")
        sb.appendLine("【第一阶段：Activity 创建】")
        sb.appendLine("ActivityThread.main()")
        sb.appendLine("    ↓")
        sb.appendLine("ActivityThread.attach()")
        sb.appendLine("    ↓ (Binder 调用)")
        sb.appendLine("ActivityManagerService.attachApplication()")
        sb.appendLine("    ↓")
        sb.appendLine("ActivityStackSupervisor.realStartActivityLocked()")
        sb.appendLine("    ↓")
        sb.appendLine("ActivityThread.handleLaunchActivity()\n")
        
        sb.appendLine("【第二阶段：Window 创建】")
        sb.appendLine("ActivityThread.performLaunchActivity()")
        sb.appendLine("    ├── 创建 Activity 实例")
        sb.appendLine("    ├── Activity.attach()")
        sb.appendLine("    │       └── 创建 PhoneWindow")
        sb.appendLine("    └── Activity.onCreate()")
        sb.appendLine("            └── setContentView()")
        sb.appendLine("                    └── 创建 DecorView\n")
        
        sb.appendLine("【第三阶段：Window 显示】")
        sb.appendLine("ActivityThread.handleResumeActivity()")
        sb.appendLine("    ├── Activity.performResume()")
        sb.appendLine("    └── WindowManager.addView(DecorView)")
        sb.appendLine("            ↓")
        sb.appendLine("        ViewRootImpl.setView()")
        sb.appendLine("            ↓")
        sb.appendLine("        WindowManagerService.addWindow()\n")
        
        sb.appendLine("【第四阶段：首次布局】")
        sb.appendLine("ViewRootImpl.performTraversals()")
        sb.appendLine("    ├── performMeasure()")
        sb.appendLine("    ├── performLayout()")
        sb.appendLine("    └── performDraw()")
        sb.appendLine("```\n")
        
        sb.appendLine("点击下方按钮查看各阶段详细过程")
        
        binding.tvContent.text = sb.toString()
    }

    private fun showActivityAttach() {
        sb.clear()
        sb.appendLine("=== Activity.attach() ===\n")
        
        sb.appendLine("Activity 创建后，调用 attach() 初始化\n")
        
        sb.appendLine("【Activity.attach()】")
        sb.appendLine("```java")
        sb.appendLine("final void attach(Context context, ActivityThread aThread,")
        sb.appendLine("        Instrumentation instr, IBinder token, ...) {")
        sb.appendLine("    // 保存上下文")
        sb.appendLine("    mMainThread = aThread;")
        sb.appendLine("    mToken = token;  // Activity Token")
        sb.appendLine("    ")
        sb.appendLine("    // 创建 PhoneWindow")
        sb.appendLine("    mWindow = new PhoneWindow(this, window);")
        sb.appendLine("    mWindow.setCallback(this);  // 设置回调")
        sb.appendLine("    ")
        sb.appendLine("    // 设置 WindowManager")
        sb.appendLine("    mWindow.setWindowManager(")
        sb.appendLine("        (WindowManager)context.getSystemService(Context.WINDOW_SERVICE),")
        sb.appendLine("        mToken, mComponent.flattenToString(),")
        sb.appendLine("        0 // info.flags")
        sb.appendLine("    );")
        sb.appendLine("    mWindowManager = mWindow.getWindowManager();")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【PhoneWindow 构造函数】")
        sb.appendLine("```java")
        sb.appendLine("public PhoneWindow(Context context) {")
        sb.appendLine("    super(context);")
        sb.appendLine("    mLayoutInflater = LayoutInflater.from(context);")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【关键点】")
        sb.appendLine("1. 创建 PhoneWindow 作为 Window 实现")
        sb.appendLine("2. 设置 Activity 为 Window 的 Callback")
        sb.appendLine("3. 使用 Activity Token 初始化 WindowManager")
        sb.appendLine("4. Token 用于窗口验证和归属")
        
        binding.tvContent.text = sb.toString()
    }

    private fun showSetContentView() {
        sb.clear()
        sb.appendLine("=== setContentView() ===\n")
        
        sb.appendLine("setContentView() 创建并设置 DecorView\n")
        
        sb.appendLine("【Activity.setContentView()】")
        sb.appendLine("```java")
        sb.appendLine("public void setContentView(@LayoutRes int layoutResID) {")
        sb.appendLine("    getWindow().setContentView(layoutResID);")
        sb.appendLine("    initWindowDecorActionBar();")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【PhoneWindow.setContentView()】")
        sb.appendLine("```java")
        sb.appendLine("public void setContentView(int layoutResID) {")
        sb.appendLine("    // 安装 DecorView")
        sb.appendLine("    if (mContentParent == null) {")
        sb.appendLine("        installDecor();")
        sb.appendLine("    } else if (!hasFeature(FEATURE_CONTENT_TRANSITIONS)) {")
        sb.appendLine("        mContentParent.removeAllViews();")
        sb.appendLine("    }")
        sb.appendLine("    ")
        sb.appendLine("    // 填充布局到 mContentParent")
        sb.appendLine("    mLayoutInflater.inflate(layoutResID, mContentParent);")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【installDecor()】")
        sb.appendLine("```java")
        sb.appendLine("private void installDecor() {")
        sb.appendLine("    if (mDecor == null) {")
        sb.appendLine("        mDecor = new DecorView(this);  // 创建 DecorView")
        sb.appendLine("    }")
        sb.appendLine("    if (mContentParent == null) {")
        sb.appendLine("        // 根据主题生成布局")
        sb.appendLine("        mContentParent = generateLayout(mDecor);")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【generateLayout()】")
        sb.appendLine("```java")
        sb.appendLine("protected ViewGroup generateLayout(DecorView decor) {")
        sb.appendLine("    // 根据主题选择布局")
        sb.appendLine("    int layoutResource = selectLayoutResource();")
        sb.appendLine("    // 填充布局")
        sb.appendLine("    mDecor.onResourcesLoaded(mLayoutInflater, layoutResource);")
        sb.appendLine("    // 找到 content 区域")
        sb.appendLine("    ViewGroup contentParent = (ViewGroup)findViewById(ID_ANDROID_CONTENT);")
        sb.appendLine("    return contentParent;")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【DecorView 结构】")
        sb.appendLine("```\n")
        sb.appendLine("DecorView (根视图)")
        sb.appendLine("    │")
        sb.appendLine("    ├── StatusBar 背景")
        sb.appendLine("    │")
        sb.appendLine("    └── LinearLayout")
        sb.appendLine("            ├── ActionBar")
        sb.appendLine("            │")
        sb.appendLine("            └── FrameLayout (id=content)")
        sb.appendLine("                    │")
        sb.appendLine("                    └── 我们的布局")
        sb.appendLine("```")
        
        binding.tvContent.text = sb.toString()
    }

    private fun showResumeActivity() {
        sb.clear()
        sb.appendLine("=== handleResumeActivity() ===\n")
        
        sb.appendLine("Activity Resume 时将 DecorView 添加到 WindowManager\n")
        
        sb.appendLine("【ActivityThread.handleResumeActivity()】")
        sb.appendLine("```java")
        sb.appendLine("public void handleResumeActivity(IBinder token, ...) {")
        sb.appendLine("    // 调用 Activity.onResume()")
        sb.appendLine("    final ActivityClientRecord r = performResumeActivity(token, ...);")
        sb.appendLine("    ")
        sb.appendLine("    if (r.window == null && !r.activity.mFinished) {")
        sb.appendLine("        // 获取 Window 和 DecorView")
        sb.appendLine("        r.window = r.activity.getWindow();")
        sb.appendLine("        View decor = r.window.getDecorView();")
        sb.appendLine("        decor.setVisibility(View.INVISIBLE);")
        sb.appendLine("        ")
        sb.appendLine("        // 获取 WindowManager")
        sb.appendLine("        ViewManager wm = a.getWindowManager();")
        sb.appendLine("        WindowManager.LayoutParams l = r.window.getAttributes();")
        sb.appendLine("        ")
        sb.appendLine("        // 添加 DecorView 到 WindowManager")
        sb.appendLine("        wm.addView(decor, l);")
        sb.appendLine("    }")
        sb.appendLine("    ")
        sb.appendLine("    r.activity.mVisibleFromServer = true;")
        sb.appendLine("    r.activity.makeVisible();")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【Activity.makeVisible()】")
        sb.appendLine("```java")
        sb.appendLine("void makeVisible() {")
        sb.appendLine("    if (!mWindowAdded) {")
        sb.appendLine("        ViewManager wm = getWindowManager();")
        sb.appendLine("        wm.addView(mDecor, getWindow().getAttributes());")
        sb.appendLine("        mWindowAdded = true;")
        sb.appendLine("    }")
        sb.appendLine("    mDecor.setVisibility(View.VISIBLE);")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【关键点】")
        sb.appendLine("1. performResumeActivity() 调用 Activity.onResume()")
        sb.appendLine("2. 获取 PhoneWindow 和 DecorView")
        sb.appendLine("3. 创建 WindowManager.LayoutParams")
        sb.appendLine("4. 调用 wm.addView() 添加视图")
        
        binding.tvContent.text = sb.toString()
    }

    private fun showViewRootImplSetView() {
        sb.clear()
        sb.appendLine("=== ViewRootImpl.setView() ===\n")
        
        sb.appendLine("ViewRootImpl 是连接 View 和 WMS 的桥梁\n")
        
        sb.appendLine("【WindowManagerGlobal.addView()】")
        sb.appendLine("```java")
        sb.appendLine("public void addView(View view, ViewGroup.LayoutParams params,")
        sb.appendLine("        Display display, Window parentWindow) {")
        sb.appendLine("    // 调整参数")
        sb.appendLine("    final WindowManager.LayoutParams wparams = (WindowManager.LayoutParams) params;")
        sb.appendLine("    ")
        sb.appendLine("    ViewRootImpl root;")
        sb.appendLine("    View panelParentView = null;")
        sb.appendLine("    ")
        sb.appendLine("    synchronized (mLock) {")
        sb.appendLine("        // 创建 ViewRootImpl")
        sb.appendLine("        root = new ViewRootImpl(view.getContext(), display);")
        sb.appendLine("        ")
        sb.appendLine("        // 保存到列表")
        sb.appendLine("        mViews.add(view);")
        sb.appendLine("        mRoots.add(root);")
        sb.appendLine("        mParams.add(wparams);")
        sb.appendLine("        ")
        sb.appendLine("        try {")
        sb.appendLine("            // 设置 View")
        sb.appendLine("            root.setView(view, wparams, panelParentView);")
        sb.appendLine("        } catch (RuntimeException e) {")
        sb.appendLine("            // 错误处理")
        sb.appendLine("        }")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【ViewRootImpl.setView()】")
        sb.appendLine("```java")
        sb.appendLine("public void setView(View view, WindowManager.LayoutParams attrs,")
        sb.appendLine("        View panelParentView) {")
        sb.appendLine("    synchronized (this) {")
        sb.appendLine("        if (mView == null) {")
        sb.appendLine("            mView = view;")
        sb.appendLine("            mWindowAttributes.copyFrom(attrs);")
        sb.appendLine("            ")
        sb.appendLine("            // 请求第一次布局")
        sb.appendLine("            requestLayout();")
        sb.appendLine("            ")
        sb.appendLine("            try {")
        sb.appendLine("                // 添加到 WMS")
        sb.appendLine("                mOrigWindowType = mWindowAttributes.type;")
        sb.appendLine("                mAttachInfo.mRecomputeGlobalAttributes = true;")
        sb.appendLine("                collectViewAttributes();")
        sb.appendLine("                res = mWindowSession.addToDisplay(...);")
        sb.appendLine("            } catch (RemoteException e) {")
        sb.appendLine("                // 处理异常")
        sb.appendLine("            }")
        sb.appendLine("        }")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【IWindowSession.addToDisplay()】")
        sb.appendLine("这是一个 Binder 调用，从应用进程调用到系统进程")
        sb.appendLine("最终调用 WindowManagerService.addWindow()")
        
        binding.tvContent.text = sb.toString()
    }

    private fun showWmsAddWindow() {
        sb.clear()
        sb.appendLine("=== WMS.addWindow() ===\n")
        
        sb.appendLine("WindowManagerService 添加窗口\n")
        
        sb.appendLine("【WindowManagerService.addWindow()】")
        sb.appendLine("```java")
        sb.appendLine("public int addWindow(Session session, IWindow client,")
        sb.appendLine("        WindowManager.LayoutParams attrs, ...) {")
        sb.appendLine("    int[] appOp = new int[1];")
        sb.appendLine("    int res = mPolicy.checkAddPermission(attrs, appOp);")
        sb.appendLine("    ")
        sb.appendLine("    synchronized (mWindowMap) {")
        sb.appendLine("        // 检查 DisplayContent")
        sb.appendLine("        final DisplayContent displayContent = getDisplayContentOrCreate(displayId);")
        sb.appendLine("        ")
        sb.appendLine("        // 查找或创建 WindowToken")
        sb.appendLine("        WindowToken token = displayContent.getWindowToken(token);")
        sb.appendLine("        if (token == null) {")
        sb.appendLine("            token = new WindowToken(this, binder, type, false);")
        sb.appendLine("        }")
        sb.appendLine("        ")
        sb.appendLine("        // 创建 WindowState")
        sb.appendLine("        WindowState win = new WindowState(this, session, client,")
        sb.appendLine("                token, parentWindow, appOp[0], seq, attrs, viewVisibility);")
        sb.appendLine("        ")
        sb.appendLine("        // 添加到列表")
        sb.appendLine("        mWindowMap.put(client.asBinder(), win);")
        sb.appendLine("        ")
        sb.appendLine("        // 添加到 Token")
        sb.appendLine("        win.mToken.addWindow(win);")
        sb.appendLine("        ")
        sb.appendLine("        // 分配 Surface")
        sb.appendLine("        win.attach();")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【WindowState.attach()】")
        sb.appendLine("```java")
        sb.appendLine("void attach() {")
        sb.appendLine("    mSession.windowAddedLocked();")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【Session.windowAddedLocked()】")
        sb.appendLine("```java")
        sb.appendLine("void windowAddedLocked() {")
        sb.appendLine("    if (mSurfaceSession == null) {")
        sb.appendLine("        mSurfaceSession = new SurfaceSession();")
        sb.appendLine("        mService.mSessions.add(this);")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【关键步骤】")
        sb.appendLine("1. 权限检查")
        sb.appendLine("2. 创建/查找 WindowToken")
        sb.appendLine("3. 创建 WindowState")
        sb.appendLine("4. 添加到 WindowMap")
        sb.appendLine("5. 创建 SurfaceSession")
        
        binding.tvContent.text = sb.toString()
    }

    private fun showFirstTraversals() {
        sb.clear()
        sb.appendLine("=== 首次布局 performTraversals() ===\n")
        
        sb.appendLine("ViewRootImpl 触发首次测量、布局、绘制\n")
        
        sb.appendLine("【requestLayout()】")
        sb.appendLine("```java")
        sb.appendLine("public void requestLayout() {")
        sb.appendLine("    if (!mHandlingLayoutInLayoutRequest) {")
        sb.appendLine("        checkThread();  // 检查线程")
        sb.appendLine("        mLayoutRequested = true;")
        sb.appendLine("        scheduleTraversals();")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【scheduleTraversals()】")
        sb.appendLine("```java")
        sb.appendLine("void scheduleTraversals() {")
        sb.appendLine("    if (!mTraversalScheduled) {")
        sb.appendLine("        mTraversalScheduled = true;")
        sb.appendLine("        // 发送同步屏障")
        sb.appendLine("        mTraversalBarrier = mHandler.getLooper().getQueue().postSyncBarrier();")
        sb.appendLine("        // 安排遍历")
        sb.appendLine("        mChoreographer.postCallback(Choreographer.CALLBACK_TRAVERSAL,")
        sb.appendLine("                mTraversalRunnable, null);")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【performTraversals()】")
        sb.appendLine("```java")
        sb.appendLine("void performTraversals() {")
        sb.appendLine("    // 获取各种尺寸")
        sb.appendLine("    int desiredWindowWidth = mWinFrame.width();")
        sb.appendLine("    int desiredWindowHeight = mWinFrame.height();")
        sb.appendLine("    ")
        sb.appendLine("    // 测量")
        sb.appendLine("    performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);")
        sb.appendLine("    ")
        sb.appendLine("    // 布局")
        sb.appendLine("    performLayout(lp, desiredWindowWidth, desiredWindowHeight);")
        sb.appendLine("    ")
        sb.appendLine("    // 绘制")
        sb.appendLine("    performDraw();")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【三个核心方法】")
        sb.appendLine("```java")
        sb.appendLine("// 测量")
        sb.appendLine("private void performMeasure(int widthMeasureSpec, int heightMeasureSpec) {")
        sb.appendLine("    mView.measure(widthMeasureSpec, heightMeasureSpec);")
        sb.appendLine("}")
        sb.appendLine("")
        sb.appendLine("// 布局")
        sb.appendLine("private void performLayout(WindowManager.LayoutParams lp, int width, int height) {")
        sb.appendLine("    mView.layout(0, 0, width, height);")
        sb.appendLine("}")
        sb.appendLine("")
        sb.appendLine("// 绘制")
        sb.appendLine("private void performDraw() {")
        sb.appendLine("    draw(fullRedrawNeeded);")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【Choreographer】")
        sb.appendLine("- 接收 VSync 信号")
        sb.appendLine("- 协调 UI 渲染")
        sb.appendLine("- 保证流畅的 60fps")
        
        binding.tvContent.text = sb.toString()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
