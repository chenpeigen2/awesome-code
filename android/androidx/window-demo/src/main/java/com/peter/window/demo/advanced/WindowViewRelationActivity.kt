package com.peter.window.demo.advanced

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.peter.window.demo.databinding.ActivityWindowViewRelationBinding

/**
 * 高级篇：Window 与 View 的关系
 * 
 * 本Activity讲解Window和View的关系：
 * 
 * 1. DecorView 的结构
 * 2. View 树的构建过程
 * 3. Window 和 View 的交互
 * 
 * Window 与 View 的关系图：
 * 
 * ```
 * Window (PhoneWindow)
 *     │
 *     ├── DecorView (根视图)
 *     │       │
 *     │       ├── StatusBarBackground
 *     │       │
 *     │       └── LinearLayout (垂直)
 *     │               │
 *     │               ├── TitleBar (可选)
 *     │               │
 *     │               └── FrameLayout (id = content)
 *     │                       │
 *     │                       └── Activity 的布局
 *     │
 *     └── WindowManager
 * ```
 * 
 * View 树的测量、布局、绘制：
 * 
 * ```
 * ViewRootImpl.performTraversals()
 *     │
 *     ├── performMeasure() → View.measure()
 *     │       └── View.onMeasure()
 *     │
 *     ├── performLayout() → View.layout()
 *     │       └── View.onLayout()
 *     │
 *     └── performDraw() → View.draw()
 *             └── View.onDraw()
 * ```
 */
class WindowViewRelationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWindowViewRelationBinding
    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWindowViewRelationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showDecorViewStructure()
    }

    private fun setupListeners() {
        binding.btnDecorView.setOnClickListener { showDecorViewStructure() }
        binding.btnViewTree.setOnClickListener { showViewTreeBuild() }
        binding.btnMeasure.setOnClickListener { showMeasureProcess() }
        binding.btnLayout.setOnClickListener { showLayoutProcess() }
        binding.btnDraw.setOnClickListener { showDrawProcess() }
        binding.btnTouchEvent.setOnClickListener { showTouchEventDispatch() }
    }

    /**
     * DecorView 的结构
     */
    private fun showDecorViewStructure() {
        sb.clear()
        sb.appendLine("=== DecorView 结构 ===\n")
        
        sb.appendLine("DecorView 是 Window 的根视图\n")
        
        sb.appendLine("结构图：")
        sb.appendLine("```\n")
        sb.appendLine("DecorView (FrameLayout)")
        sb.appendLine("    │")
        sb.appendLine("    ├── View (StatusBar 背景)")
        sb.appendLine("    │")
        sb.appendLine("    └── LinearLayout (mContentRoot)")
        sb.appendLine("            │")
        sb.appendLine("            ├── ViewStub (ActionBar)")
        sb.appendLine("            │")
        sb.appendLine("            └── FrameLayout")
        sb.appendLine("                    │")
        sb.appendLine("                    └── id = content")
        sb.appendLine("                          │")
        sb.appendLine("                          └── Activity.setContentView() 的布局")
        sb.appendLine("```\n")
        
        sb.appendLine("【DecorView 的创建】")
        sb.appendLine("```java")
        sb.appendLine("// PhoneWindow.installDecor()")
        sb.appendLine("private void installDecor() {")
        sb.appendLine("    if (mDecor == null) {")
        sb.appendLine("        mDecor = new DecorView(this);")
        sb.appendLine("    }")
        sb.appendLine("    if (mContentParent == null) {")
        sb.appendLine("        // 根据主题选择布局")
        sb.appendLine("        mContentParent = generateLayout(mDecor);")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【generateLayout() 根据主题选择布局】")
        sb.appendLine("- R.layout.screen_simple")
        sb.appendLine("- R.layout.screen_title")
        sb.appendLine("- R.layout.screen_action_bar")
        sb.appendLine("等\n")
        
        sb.appendLine("【获取 DecorView】")
        sb.appendLine("```java")
        sb.appendLine("// Activity 中")
        sb.appendLine("View decorView = getWindow().getDecorView();")
        sb.appendLine("// 或者")
        sb.appendLine("View decorView = findViewById(android.R.id.content).getRootView();")
        sb.appendLine("```")
        
        binding.tvContent.text = sb.toString()
    }

    /**
     * View 树的构建过程
     */
    private fun showViewTreeBuild() {
        sb.clear()
        sb.appendLine("=== View 树构建过程 ===\n")
        
        sb.appendLine("1. Activity.setContentView()")
        sb.appendLine("```java")
        sb.appendLine("public void setContentView(int layoutResID) {")
        sb.appendLine("    getWindow().setContentView(layoutResID);")
        sb.appendLine("    initWindowDecorActionBar();")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("2. PhoneWindow.setContentView()")
        sb.appendLine("```java")
        sb.appendLine("public void setContentView(int layoutResID) {")
        sb.appendLine("    if (mContentParent == null) {")
        sb.appendLine("        installDecor();")
        sb.appendLine("    }")
        sb.appendLine("    // 清空 mContentParent")
        sb.appendLine("    mContentParent.removeAllViews();")
        sb.appendLine("    // 填充布局")
        sb.appendLine("    mLayoutInflater.inflate(layoutResID, mContentParent);")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("3. LayoutInflater.inflate()")
        sb.appendLine("```java")
        sb.appendLine("public View inflate(int resource, ViewGroup root) {")
        sb.appendLine("    XmlResourceParser parser = getResources().getLayout(resource);")
        sb.appendLine("    return inflate(parser, root, root != null);")
        sb.appendLine("}")
        sb.appendLine("```")
        sb.appendLine("   - 解析 XML")
        sb.appendLine("   - 创建 View 对象")
        sb.appendLine("   - 设置 LayoutParams")
        sb.appendLine("   - 添加到父 View\n")
        
        sb.appendLine("4. ActivityThread.handleResumeActivity()")
        sb.appendLine("```java")
        sb.appendLine("public void handleResumeActivity(IBinder token, ...) {")
        sb.appendLine("    ActivityClientRecord r = performResumeActivity(token, ...);")
        sb.appendLine("    ")
        sb.appendLine("    if (r.window == null && !a.mFinished) {")
        sb.appendLine("        r.window = r.activity.getWindow();")
        sb.appendLine("        View decor = r.window.getDecorView();")
        sb.appendLine("        ViewManager wm = a.getWindowManager();")
        sb.appendLine("        wm.addView(decor, l);")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("5. ViewRootImpl.setView()")
        sb.appendLine("   - 触发第一次布局")
        sb.appendLine("   - 添加到 WindowManagerService")
        
        binding.tvContent.text = sb.toString()
    }

    /**
     * 测量过程
     */
    private fun showMeasureProcess() {
        sb.clear()
        sb.appendLine("=== 测量过程 ===\n")
        
        sb.appendLine("测量决定 View 的大小\n")
        
        sb.appendLine("【MeasureSpec】")
        sb.appendLine("测量规格，包含测量模式和大小")
        sb.appendLine("```java")
        sb.appendLine("int measureSpec = MeasureSpec.makeMeasureSpec(size, mode);")
        sb.appendLine("int size = MeasureSpec.getSize(measureSpec);")
        sb.appendLine("int mode = MeasureSpec.getMode(measureSpec);")
        sb.appendLine("```")
        sb.appendLine("三种模式：")
        sb.appendLine("- EXACTLY: 精确大小 (match_parent 或具体值)")
        sb.appendLine("- AT_MOST: 最大限制 (wrap_content)")
        sb.appendLine("- UNSPECIFIED: 无限制\n")
        
        sb.appendLine("【测量流程】")
        sb.appendLine("```\n")
        sb.appendLine("ViewRootImpl.performMeasure()")
        sb.appendLine("    ↓")
        sb.appendLine("DecorView.measure()")
        sb.appendLine("    ↓")
        sb.appendLine("ViewGroup.onMeasure()")
        sb.appendLine("    ↓")
        sb.appendLine("遍历子 View → child.measure()")
        sb.appendLine("    ↓")
        sb.appendLine("View.onMeasure()")
        sb.appendLine("    ↓")
        sb.appendLine("setMeasuredDimension()")
        sb.appendLine("```\n")
        
        sb.appendLine("【自定义 View 重写 onMeasure】")
        sb.appendLine("```java")
        sb.appendLine("@Override")
        sb.appendLine("protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {")
        sb.appendLine("    int width = measureWidth(widthMeasureSpec);")
        sb.appendLine("    int height = measureHeight(heightMeasureSpec);")
        sb.appendLine("    setMeasuredDimension(width, height);")
        sb.appendLine("}")
        sb.appendLine("```")
        
        binding.tvContent.text = sb.toString()
    }

    /**
     * 布局过程
     */
    private fun showLayoutProcess() {
        sb.clear()
        sb.appendLine("=== 布局过程 ===\n")
        
        sb.appendLine("布局决定 View 的位置\n")
        
        sb.appendLine("【布局流程】")
        sb.appendLine("```\n")
        sb.appendLine("ViewRootImpl.performLayout()")
        sb.appendLine("    ↓")
        sb.appendLine("DecorView.layout()")
        sb.appendLine("    ↓")
        sb.appendLine("ViewGroup.onLayout()")
        sb.appendLine("    ↓")
        sb.appendLine("遍历子 View → child.layout()")
        sb.appendLine("```\n")
        
        sb.appendLine("【View.layout()】")
        sb.appendLine("```java")
        sb.appendLine("public void layout(int l, int t, int r, int b) {")
        sb.appendLine("    // 保存旧位置")
        sb.appendLine("    int oldL = mLeft;")
        sb.appendLine("    // 设置新位置")
        sb.appendLine("    mLeft = l; mTop = t; mRight = r; mBottom = b;")
        sb.appendLine("    // 调用 onLayout")
        sb.appendLine("    onLayout(changed, l, t, r, b);")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【ViewGroup.onLayout()】")
        sb.appendLine("```java")
        sb.appendLine("// 必须重写，放置子 View")
        sb.appendLine("@Override")
        sb.appendLine("protected void onLayout(boolean changed, int l, int t, int r, int b) {")
        sb.appendLine("    for (int i = 0; i < getChildCount(); i++) {")
        sb.appendLine("        View child = getChildAt(i);")
        sb.appendLine("        child.layout(childLeft, childTop, childRight, childBottom);")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【位置相关方法】")
        sb.appendLine("- getLeft(), getTop(), getRight(), getBottom()")
        sb.appendLine("- getWidth() = getRight() - getLeft()")
        sb.appendLine("- getHeight() = getBottom() - getTop()")
        sb.appendLine("- getX(), getY() (带动画偏移)")
        
        binding.tvContent.text = sb.toString()
    }

    /**
     * 绘制过程
     */
    private fun showDrawProcess() {
        sb.clear()
        sb.appendLine("=== 绘制过程 ===\n")
        
        sb.appendLine("绘制决定 View 的外观\n")
        
        sb.appendLine("【绘制流程】")
        sb.appendLine("```\n")
        sb.appendLine("ViewRootImpl.performDraw()")
        sb.appendLine("    ↓")
        sb.appendLine("DecorView.draw()")
        sb.appendLine("```\n")
        
        sb.appendLine("【View.draw() 的 6 个步骤】")
        sb.appendLine("```java")
        sb.appendLine("public void draw(Canvas canvas) {")
        sb.appendLine("    // 1. 绘制背景")
        sb.appendLine("    drawBackground(canvas);")
        sb.appendLine("    ")
        sb.appendLine("    // 2. 如果需要，保存图层")
        sb.appendLine("    saveCount = canvas.save();")
        sb.appendLine("    ")
        sb.appendLine("    // 3. 绘制自身内容")
        sb.appendLine("    onDraw(canvas);")
        sb.appendLine("    ")
        sb.appendLine("    // 4. 绘制子 View")
        sb.appendLine("    dispatchDraw(canvas);")
        sb.appendLine("    ")
        sb.appendLine("    // 5. 绘制前景（滚动条等）")
        sb.appendLine("    onDrawForeground(canvas);")
        sb.appendLine("    ")
        sb.appendLine("    // 6. 绘制默认焦点高亮")
        sb.appendLine("    drawDefaultFocusHighlight(canvas);")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【Canvas 常用方法】")
        sb.appendLine("- drawRect(), drawCircle(), drawPath()")
        sb.appendLine("- drawText(), drawBitmap()")
        sb.appendLine("- save(), restore()")
        sb.appendLine("- translate(), rotate(), scale()")
        sb.appendLine("- clipRect(), clipPath()\n")
        
        sb.appendLine("【硬件加速】")
        sb.appendLine("- 默认开启（API 14+）")
        sb.appendLine("- 使用 GPU 绘制")
        sb.appendLine("- 可以在 Manifest 中关闭")
        sb.appendLine("- 某些绘制操作不支持硬件加速")
        
        binding.tvContent.text = sb.toString()
    }

    /**
     * 触摸事件分发
     */
    private fun showTouchEventDispatch() {
        sb.clear()
        sb.appendLine("=== 触摸事件分发 ===\n")
        
        sb.appendLine("触摸事件从 Window 开始分发\n")
        
        sb.appendLine("【事件分发流程】")
        sb.appendLine("```\n")
        sb.appendLine("Window (PhoneWindow)")
        sb.appendLine("    ↓")
        sb.appendLine("DecorView.dispatchTouchEvent()")
        sb.appendLine("    ↓")
        sb.appendLine("Activity.dispatchTouchEvent()")
        sb.appendLine("    ↓")
        sb.appendLine("ViewGroup.dispatchTouchEvent()")
        sb.appendLine("    ↓")
        sb.appendLine("onInterceptTouchEvent() → 是否拦截")
        sb.appendLine("    ↓ (不拦截)")
        sb.appendLine("child.dispatchTouchEvent()")
        sb.appendLine("    ↓")
        sb.appendLine("View.onTouchEvent()")
        sb.appendLine("```\n")
        
        sb.appendLine("【核心方法】")
        sb.appendLine("```java")
        sb.appendLine("// Activity")
        sb.appendLine("public boolean dispatchTouchEvent(MotionEvent ev) {")
        sb.appendLine("    if (getWindow().superDispatchTouchEvent(ev)) {")
        sb.appendLine("        return true;")
        sb.appendLine("    }")
        sb.appendLine("    return onTouchEvent(ev);")
        sb.appendLine("}")
        sb.appendLine("")
        sb.appendLine("// ViewGroup")
        sb.appendLine("public boolean dispatchTouchEvent(MotionEvent ev) {")
        sb.appendLine("    if (onInterceptTouchEvent(ev)) {")
        sb.appendLine("        return onTouchEvent(ev);")
        sb.appendLine("    }")
        sb.appendLine("    return child.dispatchTouchEvent(ev);")
        sb.appendLine("}")
        sb.appendLine("")
        sb.appendLine("// View")
        sb.appendLine("public boolean dispatchTouchEvent(MotionEvent ev) {")
        sb.appendLine("    if (mOnTouchListener != null && onTouch(ev)) {")
        sb.appendLine("        return true;")
        sb.appendLine("    }")
        sb.appendLine("    return onTouchEvent(ev);")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【事件处理优先级】")
        sb.appendLine("1. OnTouchListener.onTouch()")
        sb.appendLine("2. onTouchEvent()")
        sb.appendLine("3. OnClickListener.onClick()")
        
        binding.tvContent.text = sb.toString()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
