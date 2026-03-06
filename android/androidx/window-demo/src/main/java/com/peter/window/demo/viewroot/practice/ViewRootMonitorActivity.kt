package com.peter.window.demo.viewroot.practice

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.os.SystemClock
import android.view.Choreographer
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.peter.window.demo.databinding.ActivityViewrootMonitorBinding

/**
 * 实践篇：ViewRootImpl 监控实战
 * 
 * 本 Activity 展示如何监控 ViewRootImpl 的各种状态，
 * 包括帧率、布局时间、绘制时间等。
 */
class ViewRootMonitorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewrootMonitorBinding
    private val sb = StringBuilder()
    
    private val frameMonitor = FrameMonitor()
    private val layoutMonitor = LayoutMonitor()
    private val drawMonitor = DrawMonitor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewrootMonitorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showMonitorOverview()
    }

    private fun setupListeners() {
        binding.btnOverview.setOnClickListener { showMonitorOverview() }
        binding.btnFrameMonitor.setOnClickListener { startFrameMonitor() }
        binding.btnLayoutMonitor.setOnClickListener { showLayoutMonitor() }
        binding.btnDrawMonitor.setOnClickListener { showDrawMonitor() }
        binding.btnViewHierarchy.setOnClickListener { showViewHierarchy() }
        binding.btnPerformance.setOnClickListener { showPerformanceTips() }
    }

    /**
     * 监控概述
     */
    private fun showMonitorOverview() {
        sb.clear()
        sb.appendLine("=== ViewRootImpl 监控概述 ===\n")
        
        sb.appendLine("【监控维度】")
        sb.appendLine("```\n")
        sb.appendLine("1. 帧率监控")
        sb.appendLine("   - FPS 计算")
        sb.appendLine("   - 掉帧检测")
        sb.appendLine("   - 帧时间分布")
        sb.appendLine("")
        sb.appendLine("2. 布局监控")
        sb.appendLine("   - measure 耗时")
        sb.appendLine("   - layout 耗时")
        sb.appendLine("   - requestLayout 次数")
        sb.appendLine("")
        sb.appendLine("3. 绘制监控")
        sb.appendLine("   - draw 耗时")
        sb.appendLine("   - invalidate 次数")
        sb.appendLine("   - 过度绘制检测")
        sb.appendLine("")
        sb.appendLine("4. View 层级监控")
        sb.appendLine("   - View 数量")
        sb.appendLine("   - 层级深度")
        sb.appendLine("   - 内存占用")
        sb.appendLine("```\n")
        
        sb.appendLine("【监控方法】")
        sb.appendLine("```java")
        sb.appendLine("// 1. Choreographer.FrameCallback")
        sb.appendLine("Choreographer.getInstance().postFrameCallback(callback);")
        sb.appendLine("")
        sb.appendLine("// 2. ViewTreeObserver")
        sb.appendLine("view.getViewTreeObserver().addOnGlobalLayoutListener(listener);")
        sb.appendLine("")
        sb.appendLine("// 3. Window.OnFrameMetricsAvailableListener (API 24+)")
        sb.appendLine("window.addOnFrameMetricsAvailableListener(listener, handler);")
        sb.appendLine("")
        sb.appendLine("// 4. 自定义 View onMeasure/onLayout/onDraw")
        sb.appendLine("@Override")
        sb.appendLine("protected void onDraw(Canvas canvas) {")
        sb.appendLine("    long start = System.nanoTime();")
        sb.appendLine("    super.onDraw(canvas);")
        sb.appendLine("    long duration = System.nanoTime() - start;")
        sb.appendLine("    // 记录绘制耗时")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【监控工具】")
        sb.appendLine("```\n")
        sb.appendLine("1. Systrace / Perfetto")
        sb.appendLine("   - 系统级性能分析")
        sb.appendLine("   - 可视化时间线")
        sb.appendLine("")
        sb.appendLine("2. Layout Inspector")
        sb.appendLine("   - View 层级查看")
        sb.appendLine("   - 属性检查")
        sb.appendLine("")
        sb.appendLine("3. GPU Overdraw")
        sb.appendLine("   - 开发者选项 → 调试 GPU 过度绘制")
        sb.appendLine("   - 可视化过度绘制")
        sb.appendLine("")
        sb.appendLine("4. Profile GPU Rendering")
        sb.appendLine("   - 开发者选项 → GPU 呈现模式分析")
        sb.appendLine("   - 实时渲染时间图表")
        sb.appendLine("```")
        
        binding.tvContent.text = sb.toString()
    }

    /**
     * 帧率监控
     */
    private fun startFrameMonitor() {
        sb.clear()
        sb.appendLine("=== 帧率监控 ===\n")
        sb.appendLine("点击下方按钮开始监控...\n")
        
        binding.btnStartMonitor.setOnClickListener {
            frameMonitor.start()
            sb.appendLine("✓ 监控已启动\n")
            sb.appendLine("等待 VSync 信号...\n")
            binding.tvContent.text = sb.toString()
        }
        
        binding.btnStopMonitor.setOnClickListener {
            frameMonitor.stop()
            sb.appendLine("\n✓ 监控已停止")
            sb.appendLine("\n=== 统计信息 ===")
            sb.appendLine("总帧数: ${frameMonitor.totalFrames}")
            sb.appendLine("平均 FPS: ${"%.1f".format(frameMonitor.averageFps)}")
            sb.appendLine("最大帧间隔: ${frameMonitor.maxFrameInterval}ms")
            sb.appendLine("掉帧次数: ${frameMonitor.droppedFrameCount}")
            binding.tvContent.text = sb.toString()
        }
        
        sb.appendLine("【FrameMonitor 实现】")
        sb.appendLine("```java")
        sb.appendLine("class FrameMonitor implements Choreographer.FrameCallback {")
        sb.appendLine("    private long mLastFrameTimeNanos = 0;")
        sb.appendLine("    private int mFrameCount = 0;")
        sb.appendLine("    private long mStartTimeNanos = 0;")
        sb.appendLine("    ")
        sb.appendLine("    void start() {")
        sb.appendLine("        Choreographer.getInstance().postFrameCallback(this);")
        sb.appendLine("    }")
        sb.appendLine("    ")
        sb.appendLine("    @Override")
        sb.appendLine("    public void doFrame(long frameTimeNanos) {")
        sb.appendLine("        mFrameCount++;")
        sb.appendLine("        ")
        sb.appendLine("        // 计算帧间隔")
        sb.appendLine("        if (mLastFrameTimeNanos != 0) {")
        sb.appendLine("            long interval = (frameTimeNanos - mLastFrameTimeNanos) / 1000000;")
        sb.appendLine("            if (interval > 33) {")
        sb.appendLine("                // 掉帧检测")
        sb.appendLine("            }")
        sb.appendLine("        }")
        sb.appendLine("        ")
        sb.appendLine("        mLastFrameTimeNanos = frameTimeNanos;")
        sb.appendLine("        Choreographer.getInstance().postFrameCallback(this);")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        binding.tvContent.text = sb.toString()
    }

    /**
     * 布局监控
     */
    private fun showLayoutMonitor() {
        sb.clear()
        sb.appendLine("=== 布局监控 ===\n")
        
        sb.appendLine("【使用 ViewTreeObserver 监控布局】")
        sb.appendLine("```java")
        sb.appendLine("view.getViewTreeObserver().addOnGlobalLayoutListener(")
        sb.appendLine("    new ViewTreeObserver.OnGlobalLayoutListener() {")
        sb.appendLine("        @Override")
        sb.appendLine("        public void onGlobalLayout() {")
        sb.appendLine("            // 布局完成回调")
        sb.appendLine("            long layoutTime = measureLayoutTime();")
        sb.appendLine("            Log.d(\"Layout\", \"布局耗时: \" + layoutTime + \"ms\");")
        sb.appendLine("        }")
        sb.appendLine("    });")
        sb.appendLine("```\n")
        
        sb.appendLine("【自定义监控 ViewGroup】")
        sb.appendLine("```java")
        sb.appendLine("public class MonitorFrameLayout extends FrameLayout {")
        sb.appendLine("    private long mMeasureStart;")
        sb.appendLine("    private long mLayoutStart;")
        sb.appendLine("    ")
        sb.appendLine("    @Override")
        sb.appendLine("    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {")
        sb.appendLine("        mMeasureStart = System.nanoTime();")
        sb.appendLine("        super.onMeasure(widthMeasureSpec, heightMeasureSpec);")
        sb.appendLine("        long duration = (System.nanoTime() - mMeasureStart) / 1000000;")
        sb.appendLine("        Log.d(\"Monitor\", \"measure: \" + duration + \"ms\");")
        sb.appendLine("    }")
        sb.appendLine("    ")
        sb.appendLine("    @Override")
        sb.appendLine("    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {")
        sb.appendLine("        mLayoutStart = System.nanoTime();")
        sb.appendLine("        super.onLayout(changed, left, top, right, bottom);")
        sb.appendLine("        long duration = (System.nanoTime() - mLayoutStart) / 1000000;")
        sb.appendLine("        Log.d(\"Monitor\", \"layout: \" + duration + \"ms\");")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【布局时间统计】")
        layoutMonitor.attach(binding.monitorContainer)
        sb.appendLine("正在监控容器布局...\n")
        
        sb.appendLine("【优化建议】")
        sb.appendLine("```\n")
        sb.appendLine("1. 减少布局层级")
        sb.appendLine("   - 使用 ConstraintLayout")
        sb.appendLine("   - 使用 merge 标签")
        sb.appendLine("   - 使用 ViewStub")
        sb.appendLine("")
        sb.appendLine("2. 避免重复测量")
        sb.appendLine("   - 使用固定尺寸")
        sb.appendLine("   - 避免使用权重")
        sb.appendLine("")
        sb.appendLine("3. 异步布局")
        sb.appendLine("   - AsyncLayoutInflater")
        sb.appendLine("   - 提前创建 View")
        sb.appendLine("```")
        
        binding.tvContent.text = sb.toString()
    }

    /**
     * 绘制监控
     */
    private fun showDrawMonitor() {
        sb.clear()
        sb.appendLine("=== 绘制监控 ===\n")
        
        sb.appendLine("【自定义绘制监控 View】")
        sb.appendLine("```java")
        sb.appendLine("public class MonitorView extends View {")
        sb.appendLine("    private long mDrawStart;")
        sb.appendLine("    private int mInvalidateCount = 0;")
        sb.appendLine("    ")
        sb.appendLine("    @Override")
        sb.appendLine("    protected void onDraw(Canvas canvas) {")
        sb.appendLine("        mDrawStart = System.nanoTime();")
        sb.appendLine("        ")
        sb.appendLine("        // 实际绘制")
        sb.appendLine("        super.onDraw(canvas);")
        sb.appendLine("        drawContent(canvas);")
        sb.appendLine("        ")
        sb.appendLine("        long duration = (System.nanoTime() - mDrawStart) / 1000000;")
        sb.appendLine("        Log.d(\"Draw\", \"draw time: \" + duration + \"ms\");")
        sb.appendLine("    }")
        sb.appendLine("    ")
        sb.appendLine("    @Override")
        sb.appendLine("    public void invalidate() {")
        sb.appendLine("        mInvalidateCount++;")
        sb.appendLine("        super.invalidate();")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【过度绘制检测】")
        sb.appendLine("```java")
        sb.appendLine("// 检测过度绘制")
        sb.appendLine("public class OverdrawDetector {")
        sb.appendLine("    private int mOverdrawCount = 0;")
        sb.appendLine("    ")
        sb.appendLine("    public void onDraw(Canvas canvas) {")
        sb.appendLine("        // 检查是否有内容被覆盖")
        sb.appendLine("        // 如果有，增加过度绘制计数")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【绘制演示】")
        drawMonitor.attach(binding.monitorContainer)
        sb.appendLine("正在演示自定义绘制监控...\n")
        
        sb.appendLine("【优化建议】")
        sb.appendLine("```\n")
        sb.appendLine("1. 减少绘制操作")
        sb.appendLine("   - 使用 clipRect 裁剪")
        sb.appendLine("   - 避免重复绘制")
        sb.appendLine("")
        sb.appendLine("2. 使用硬件层")
        sb.appendLine("   - view.setLayerType(LAYER_TYPE_HARDWARE, null);")
        sb.appendLine("   - 适用于动画")
        sb.appendLine("")
        sb.appendLine("3. 避免在绘制中创建对象")
        sb.appendLine("   - Paint, Path 等提前创建")
        sb.appendLine("   - 避免在 onDraw 中 new")
        sb.appendLine("```")
        
        binding.tvContent.text = sb.toString()
    }

    /**
     * View 层级分析
     */
    private fun showViewHierarchy() {
        sb.clear()
        sb.appendLine("=== View 层级分析 ===\n")
        
        sb.appendLine("【当前 View 树分析】\n")
        
        // 分析当前 View 树
        val analysis = analyzeViewHierarchy(binding.root)
        
        sb.appendLine("总 View 数量: ${analysis.totalViews}")
        sb.appendLine("最大深度: ${analysis.maxDepth}")
        sb.appendLine("ViewGroup 数量: ${analysis.viewGroupCount}")
        sb.appendLine("普通 View 数量: ${analysis.viewCount}\n")
        
        sb.appendLine("【View 树结构】")
        printViewTree(binding.root, 0)
        
        sb.appendLine("\n【层级分析代码】")
        sb.appendLine("```java")
        sb.appendLine("public class ViewHierarchyAnalyzer {")
        sb.appendLine("    public static AnalysisResult analyze(View root) {")
        sb.appendLine("        AnalysisResult result = new AnalysisResult();")
        sb.appendLine("        traverseView(root, 0, result);")
        sb.appendLine("        return result;")
        sb.appendLine("    }")
        sb.appendLine("    ")
        sb.appendLine("    private static void traverseView(View view, int depth, AnalysisResult result) {")
        sb.appendLine("        result.totalViews++;")
        sb.appendLine("        result.maxDepth = Math.max(result.maxDepth, depth);")
        sb.appendLine("        ")
        sb.appendLine("        if (view instanceof ViewGroup) {")
        sb.appendLine("            result.viewGroupCount++;")
        sb.appendLine("            ViewGroup group = (ViewGroup) view;")
        sb.appendLine("            for (int i = 0; i < group.getChildCount(); i++) {")
        sb.appendLine("                traverseView(group.getChildAt(i), depth + 1, result);")
        sb.appendLine("            }")
        sb.appendLine("        } else {")
        sb.appendLine("            result.viewCount++;")
        sb.appendLine("        }")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【优化建议】")
        sb.appendLine("```\n")
        sb.appendLine("1. 减少层级深度")
        sb.appendLine("   - 理想深度: < 5 层")
        sb.appendLine("   - 使用 ConstraintLayout 扁平化")
        sb.appendLine("")
        sb.appendLine("2. 减少 View 数量")
        sb.appendLine("   - 使用 merge 标签")
        sb.appendLine("   - 复用 View")
        sb.appendLine("")
        sb.appendLine("3. 延迟加载")
        sb.appendLine("   - ViewStub 延迟加载")
        sb.appendLine("   - 按需创建 View")
        sb.appendLine("```")
        
        binding.tvContent.text = sb.toString()
    }

    /**
     * 性能优化建议
     */
    private fun showPerformanceTips() {
        sb.clear()
        sb.appendLine("=== 性能优化建议 ===\n")
        
        sb.appendLine("【UI 渲染优化】")
        sb.appendLine("```\n")
        sb.appendLine("1. 布局优化")
        sb.appendLine("   - 使用 ConstraintLayout 减少层级")
        sb.appendLine("   - 使用 merge 标签减少节点")
        sb.appendLine("   - 使用 ViewStub 延迟加载")
        sb.appendLine("   - 避免嵌套过多 LinearLayout")
        sb.appendLine("")
        sb.appendLine("2. 绘制优化")
        sb.appendLine("   - 避免过度绘制（移除不必要的背景）")
        sb.appendLine("   - 使用 clipRect 裁剪绘制区域")
        sb.appendLine("   - 避免在 onDraw 中创建对象")
        sb.appendLine("   - 使用硬件加速")
        sb.appendLine("")
        sb.appendLine("3. 内存优化")
        sb.appendLine("   - 使用 RecyclerView 复用 View")
        sb.appendLine("   - 避免内存泄漏")
        sb.appendLine("   - 及时释放资源")
        sb.appendLine("```\n")
        
        sb.appendLine("【主线程优化】")
        sb.appendLine("```\n")
        sb.appendLine("1. 避免主线程阻塞")
        sb.appendLine("   - 耗时操作放到子线程")
        sb.appendLine("   - 使用 AsyncTask/Coroutine")
        sb.appendLine("   - 使用 Handler 延迟执行")
        sb.appendLine("")
        sb.appendLine("2. 减少主线程工作量")
        sb.appendLine("   - 预加载数据")
        sb.appendLine("   - 使用缓存")
        sb.appendLine("   - 批量处理")
        sb.appendLine("")
        sb.appendLine("3. 避免频繁 GC")
        sb.appendLine("   - 避免频繁创建临时对象")
        sb.appendLine("   - 使用对象池")
        sb.appendLine("```\n")
        
        sb.appendLine("【ViewRootImpl 相关优化】")
        sb.appendLine("```\n")
        sb.appendLine("1. 减少无效布局")
        sb.appendLine("   - invalidate 代替 requestLayout（尺寸不变时）")
        sb.appendLine("   - 批量更新，减少 requestLayout 次数")
        sb.appendLine("")
        sb.appendLine("2. 合理使用 VSync")
        sb.appendLine("   - 使用 Choreographer.FrameCallback 做动画")
        sb.appendLine("   - 避免错过 VSync")
        sb.appendLine("")
        sb.appendLine("3. 输入事件优化")
        sb.appendLine("   - 快速响应触摸事件")
        sb.appendLine("   - 使用事件分发拦截")
        sb.appendLine("```\n")
        
        sb.appendLine("【调试工具】")
        sb.appendLine("```\n")
        sb.appendLine("1. 开发者选项")
        sb.appendLine("   - GPU 过度绘制调试")
        sb.appendLine("   - GPU 呈现模式分析")
        sb.appendLine("   - 启用严格模式")
        sb.appendLine("")
        sb.appendLine("2. Android Studio")
        sb.appendLine("   - Layout Inspector")
        sb.appendLine("   - Profiler")
        sb.appendLine("   - CPU Profiler")
        sb.appendLine("")
        sb.appendLine("3. 命令行工具")
        sb.appendLine("   - systrace / perfetto")
        sb.appendLine("   - dumpsys gfxinfo")
        sb.appendLine("   - adb shell dumpsys activity top")
        sb.appendLine("```")
        
        binding.tvContent.text = sb.toString()
    }

    // ========== 辅助类和方法 ==========

    private data class ViewAnalysis(
        var totalViews: Int = 0,
        var maxDepth: Int = 0,
        var viewGroupCount: Int = 0,
        var viewCount: Int = 0
    )

    private fun analyzeViewHierarchy(view: View): ViewAnalysis {
        val result = ViewAnalysis()
        traverseView(view, 0, result)
        return result
    }

    private fun traverseView(view: View, depth: Int, result: ViewAnalysis) {
        result.totalViews++
        result.maxDepth = maxOf(result.maxDepth, depth)

        if (view is ViewGroup) {
            result.viewGroupCount++
            for (i in 0 until view.childCount) {
                traverseView(view.getChildAt(i), depth + 1, result)
            }
        } else {
            result.viewCount++
        }
    }

    private fun printViewTree(view: View, depth: Int) {
        val indent = "  ".repeat(depth)
        val viewName = view.javaClass.simpleName
        val size = "${view.width}x${view.height}"
        
        if (sb.length < 3000) {
            sb.appendLine("${indent}├─ $viewName ($size)")
            
            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    printViewTree(view.getChildAt(i), depth + 1)
                }
            }
        }
    }

    /**
     * 帧率监控器
     */
    inner class FrameMonitor : Choreographer.FrameCallback {
        private var lastFrameTimeNanos = 0L
        private var startTimeNanos = 0L
        private var isRunning = false
        
        var totalFrames = 0
        var averageFps = 0f
        var maxFrameInterval = 0L
        var droppedFrameCount = 0
        
        fun start() {
            if (!isRunning) {
                isRunning = true
                totalFrames = 0
                droppedFrameCount = 0
                maxFrameInterval = 0
                startTimeNanos = System.nanoTime()
                lastFrameTimeNanos = 0
                Choreographer.getInstance().postFrameCallback(this)
            }
        }
        
        fun stop() {
            isRunning = false
            Choreographer.getInstance().removeFrameCallback(this)
            
            val elapsed = System.nanoTime() - startTimeNanos
            if (elapsed > 0) {
                averageFps = totalFrames * 1_000_000_000f / elapsed
            }
        }
        
        override fun doFrame(frameTimeNanos: Long) {
            if (!isRunning) return
            
            totalFrames++
            
            if (lastFrameTimeNanos != 0L) {
                val intervalMs = (frameTimeNanos - lastFrameTimeNanos) / 1_000_000
                maxFrameInterval = maxOf(maxFrameInterval, intervalMs)
                
                if (intervalMs > 33) {
                    droppedFrameCount++
                }
            }
            
            lastFrameTimeNanos = frameTimeNanos
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    /**
     * 布局监控器
     */
    inner class LayoutMonitor {
        private var measureCount = 0
        private var layoutCount = 0
        private var lastMeasureTime = 0L
        private var lastLayoutTime = 0L

        fun attach(container: ViewGroup) {
            // 添加监控 View
            container.viewTreeObserver.addOnGlobalLayoutListener {
                layoutCount++
            }
        }
    }

    /**
     * 绘制监控器
     */
    inner class DrawMonitor {
        private var drawCount = 0
        private var invalidateCount = 0

        fun attach(container: ViewGroup) {
            // 添加监控 View
            val monitorView = MonitorDrawView(this@ViewRootMonitorActivity)
            container.addView(monitorView, ViewGroup.LayoutParams(200, 200))
        }
    }

    /**
     * 监控绘制的 View
     */
    inner class MonitorDrawView(context: android.content.Context) : View(context) {
        private val paint = Paint().apply {
            color = Color.BLUE
            style = Paint.Style.FILL
        }
        private var drawTime = 0L
        private var frameCount = 0

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            
            val start = System.nanoTime()
            
            // 绘制一个简单的图形
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            
            // 绘制帧计数
            paint.color = Color.WHITE
            paint.textSize = 30f
            canvas.drawText("Frame: $frameCount", 10f, 40f, paint)
            paint.color = Color.BLUE
            
            frameCount++
            drawTime = (System.nanoTime() - start) / 1_000_000
            
            // 每 30 帧刷新一次
            if (frameCount % 30 == 0) {
                invalidate()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
