package com.peter.os.demo.fragments

import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.view.Choreographer
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.peter.os.demo.ChoreographerAdapter
import com.peter.os.demo.ChoreographerCategory
import com.peter.os.demo.ChoreographerItem
import com.peter.os.demo.ChoreographerType
import com.peter.os.demo.R
import com.peter.os.demo.databinding.DialogChoreographerFpsBinding
import com.peter.os.demo.databinding.DialogChoreographerSurfaceBinding
import com.peter.os.demo.databinding.FragmentChoreographerBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Choreographer 编舞者示例
 * 
 * Choreographer: 协调动画、输入和绘制时序
 * - postFrameCallback(): 在下一帧执行回调
 * - getFrameTime(): 获取帧时间
 * - getFrameIntervalNanos(): 获取帧间隔
 */
class ChoreographerFragment : Fragment() {

    private var _binding: FragmentChoreographerBinding? = null
    private val binding get() = _binding!!
    
    private val choreographerInstance = Choreographer.getInstance()
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    
    // FPS 监控对话框
    private var fpsDialog: android.app.Dialog? = null
    private var fpsCallback: Choreographer.FrameCallback? = null
    private var lastFrameTime = 0L
    private var frameCount = 0
    private var totalDroppedFrames = 0
    private var isMonitoringFps = false
    private var fpsBinding: DialogChoreographerFpsBinding? = null
    
    // Surface 绘制对话框
    private var surfaceDialog: android.app.Dialog? = null
    private var surfaceCallback: Choreographer.FrameCallback? = null
    private var surfaceBinding: DialogChoreographerSurfaceBinding? = null
    private var circleX = 0f
    private var circleY = 0f
    private var circleVelocityX = 5f
    private var circleVelocityY = 3f
    private var isSurfaceAnimating = false
    private val paint = Paint().apply {
        color = 0xFF6750A4.toInt()
        isAntiAlias = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChoreographerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = listOf(
            // 基础用法
            ChoreographerItem(
                type = ChoreographerType.BASIC_CALLBACK,
                title = "基础帧回调",
                description = "postFrameCallback 在每一帧执行回调，获取帧时间",
                category = ChoreographerCategory.BASIC
            ),
            ChoreographerItem(
                type = ChoreographerType.FRAME_TIME,
                title = "帧时间信息",
                description = "获取帧时间、帧间隔等时间信息",
                category = ChoreographerCategory.BASIC
            ),
            // 帧率监控
            ChoreographerItem(
                type = ChoreographerType.FPS_MONITOR,
                title = "FPS 监控",
                description = "实时监控帧率，检测丢帧情况",
                category = ChoreographerCategory.MONITOR
            ),
            ChoreographerItem(
                type = ChoreographerType.DROP_FRAME_DETECT,
                title = "丢帧检测",
                description = "检测并统计丢帧情况",
                category = ChoreographerCategory.MONITOR
            ),
            // 自定义动画
            ChoreographerItem(
                type = ChoreographerType.CUSTOM_ANIMATION,
                title = "自定义动画",
                description = "使用 Choreographer 驱动平滑动画",
                category = ChoreographerCategory.ANIMATION
            ),
            ChoreographerItem(
                type = ChoreographerType.SURFACE_DRAW,
                title = "SurfaceView 绘制",
                description = "高性能自定义绘制，与屏幕刷新同步",
                category = ChoreographerCategory.ADVANCED
            )
        )
        
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = ChoreographerAdapter(items) { type ->
            onItemClick(type)
        }
    }

    private fun onItemClick(type: ChoreographerType) {
        when (type) {
            ChoreographerType.BASIC_CALLBACK -> showBasicCallbackDemo()
            ChoreographerType.FRAME_TIME -> showFrameTimeDemo()
            ChoreographerType.FPS_MONITOR -> showFpsMonitorDialog()
            ChoreographerType.DROP_FRAME_DETECT -> showDropFrameDemo()
            ChoreographerType.CUSTOM_ANIMATION -> showCustomAnimationDemo()
            ChoreographerType.SURFACE_DRAW -> showSurfaceDrawDialog()
        }
    }

    // ==================== 基础回调 ====================
    
    private fun showBasicCallbackDemo() {
        val sb = StringBuilder()
        var frameCount = 0
        
        sb.appendLine("=== postFrameCallback 基础用法 ===")
        sb.appendLine()
        sb.appendLine("每帧回调一次，与屏幕刷新同步:")
        sb.appendLine()
        
        val callback = object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                frameCount++
                val frameTimeMs = TimeUnit.NANOSECONDS.toMillis(frameTimeNanos)
                
                if (frameCount <= 5) {
                    sb.appendLine("帧 #$frameCount:")
                    sb.appendLine("  时间: ${dateFormat.format(Date(frameTimeMs))}")
                    sb.appendLine("  纳秒: $frameTimeNanos")
                    sb.appendLine()
                }
                
                if (frameCount < 5) {
                    choreographerInstance.postFrameCallback(this)
                } else {
                    activity?.runOnUiThread {
                        showResultDialog("基础帧回调", sb.toString())
                    }
                }
            }
        }
        
        choreographerInstance.postFrameCallback(callback)
        Toast.makeText(requireContext(), "正在捕获5帧数据...", Toast.LENGTH_SHORT).show()
    }

    // ==================== 帧时间信息 ====================
    
    private fun showFrameTimeDemo() {
        var lastFrameTime = 0L
        var frameCount = 0
        
        val callback = object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                frameCount++
                
                if (frameCount == 1) {
                    lastFrameTime = frameTimeNanos
                    choreographerInstance.postFrameCallback(this)
                } else if (frameCount == 2) {
                    val frameInterval = frameTimeNanos - lastFrameTime
                    val frameIntervalMs = TimeUnit.NANOSECONDS.toMillis(frameInterval)
                    val fps = if (frameIntervalMs > 0) 1000f / frameIntervalMs else 0f
                    
                    activity?.runOnUiThread {
                        val sb = StringBuilder()
                        sb.appendLine("=== 帧时间信息 ===")
                        sb.appendLine()
                        sb.appendLine("帧间隔:")
                        sb.appendLine("  纳秒: $frameInterval ns")
                        sb.appendLine("  毫秒: $frameIntervalMs ms")
                        sb.appendLine("  等效 FPS: ${String.format("%.1f", fps)}")
                        sb.appendLine()
                        sb.appendLine("=== 计算方式 ===")
                        sb.appendLine("FPS = 1000 / 帧间隔(ms)")
                        sb.appendLine("60fps → 16.67ms/帧")
                        sb.appendLine("90fps → 11.11ms/帧")
                        sb.appendLine("120fps → 8.33ms/帧")
                        
                        showResultDialog("帧时间信息", sb.toString())
                    }
                }
            }
        }
        
        choreographerInstance.postFrameCallback(callback)
        Toast.makeText(requireContext(), "正在计算帧间隔...", Toast.LENGTH_SHORT).show()
    }

    // ==================== FPS 监控 ====================
    
    private fun showFpsMonitorDialog() {
        fpsBinding = DialogChoreographerFpsBinding.inflate(layoutInflater)
        
        fpsDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("FPS 监控")
            .setView(fpsBinding!!.root)
            .setNegativeButton("关闭") { _, _ ->
                stopFpsMonitor()
            }
            .setCancelable(false)
            .create()
        
        fpsBinding!!.btnStart.setOnClickListener { startFpsMonitor() }
        fpsBinding!!.btnStop.setOnClickListener { stopFpsMonitor() }
        
        fpsDialog!!.setOnDismissListener { stopFpsMonitor() }
        fpsDialog!!.show()
    }
    
    private fun startFpsMonitor() {
        lastFrameTime = System.nanoTime()
        frameCount = 0
        totalDroppedFrames = 0
        isMonitoringFps = true
        
        fpsCallback = object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                if (!isMonitoringFps) return
                
                frameCount++
                val frameInterval = frameTimeNanos - lastFrameTime
                val frameIntervalMs = TimeUnit.NANOSECONDS.toMillis(frameInterval)
                
                if (frameIntervalMs > 33) {
                    val dropped = (frameIntervalMs / 16.67).toInt()
                    totalDroppedFrames += dropped
                    activity?.runOnUiThread {
                        fpsBinding?.tvLog?.append("⚠ 丢帧: $dropped 帧 (间隔 ${frameIntervalMs}ms)\n")
                    }
                }
                
                lastFrameTime = frameTimeNanos
                
                if (frameCount >= 30) {
                    val fps = 1000f / frameIntervalMs.coerceAtLeast(1)
                    activity?.runOnUiThread {
                        fpsBinding?.tvFps?.text = String.format("%.1f fps", fps)
                        fpsBinding?.tvDropped?.text = "丢帧: $totalDroppedFrames"
                    }
                    frameCount = 0
                }
                
                choreographerInstance.postFrameCallback(this)
            }
        }
        
        choreographerInstance.postFrameCallback(fpsCallback!!)
        fpsBinding!!.btnStart.isEnabled = false
        fpsBinding!!.btnStop.isEnabled = true
        fpsBinding!!.tvLog.append("开始监控...\n")
    }
    
    private fun stopFpsMonitor() {
        isMonitoringFps = false
        if (fpsCallback != null) {
            choreographerInstance.removeFrameCallback(fpsCallback!!)
        }
        fpsCallback = null
        
        fpsBinding?.apply {
            tvFps.text = "-- fps"
            tvDropped.text = "丢帧: 0"
            btnStart.isEnabled = true
            btnStop.isEnabled = false
            tvLog.append("停止监控\n")
        }
    }

    // ==================== 丢帧检测 ====================
    
    private fun showDropFrameDemo() {
        val sb = StringBuilder()
        
        sb.appendLine("=== 丢帧检测原理 ===")
        sb.appendLine()
        sb.appendLine("通过帧间隔检测丢帧:")
        sb.appendLine("• 正常帧间隔: ~16.67ms (60fps)")
        sb.appendLine("• 超过 33ms 视为丢帧")
        sb.appendLine("• 丢帧数 = 间隔时间 / 16.67")
        sb.appendLine()
        sb.appendLine("=== 常见丢帧原因 ===")
        sb.appendLine("• 主线程执行耗时操作")
        sb.appendLine("• 复杂布局渲染")
        sb.appendLine("• 频繁 GC")
        sb.appendLine("• 过度绘制")
        sb.appendLine()
        sb.appendLine("=== 解决方案 ===")
        sb.appendLine("• 使用 Systrace/Perfetto 分析")
        sb.appendLine("• 开启 StrictMode 检测")
        sb.appendLine("• 优化布局层次")
        sb.appendLine("• 使用 Choreographer 同步")
        
        showResultDialog("丢帧检测", sb.toString())
    }

    // ==================== 自定义动画 ====================
    
    private fun showCustomAnimationDemo() {
        val sb = StringBuilder()
        
        sb.appendLine("=== 自定义动画原理 ===")
        sb.appendLine()
        sb.appendLine("Choreographer 动画优势:")
        sb.appendLine("• 与屏幕刷新同步")
        sb.appendLine("• 避免过度绘制")
        sb.appendLine("• 平滑流畅")
        sb.appendLine()
        sb.appendLine("=== 代码示例 ===")
        sb.appendLine()
        sb.appendLine("val callback = object : Choreographer.FrameCallback {")
        sb.appendLine("    override fun doFrame(frameTimeNanos: Long) {")
        sb.appendLine("        // 更新动画状态")
        sb.appendLine("        updateAnimation()")
        sb.appendLine("        // 请求下一帧")
        sb.appendLine("        choreographer.postFrameCallback(this)")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("=== vs ValueAnimator ===")
        sb.appendLine("ValueAnimator 内部已使用 Choreographer")
        sb.appendLine("推荐优先使用 ValueAnimator")
        sb.appendLine("仅在需要自定义渲染时直接使用")
        
        showResultDialog("自定义动画", sb.toString())
    }

    // ==================== SurfaceView 绘制 ====================
    
    private fun showSurfaceDrawDialog() {
        surfaceBinding = DialogChoreographerSurfaceBinding.inflate(layoutInflater)
        
        surfaceBinding!!.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                circleX = surfaceBinding!!.surfaceView.width / 2f
                circleY = surfaceBinding!!.surfaceView.height / 2f
            }
            override fun surfaceDestroyed(holder: SurfaceHolder) { stopSurfaceAnimation() }
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
        })
        
        surfaceDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("SurfaceView 绘制")
            .setView(surfaceBinding!!.root)
            .setNegativeButton("关闭") { _, _ ->
                stopSurfaceAnimation()
            }
            .setCancelable(false)
            .create()
        
        surfaceBinding!!.btnStart.setOnClickListener { startSurfaceAnimation() }
        surfaceBinding!!.btnStop.setOnClickListener { stopSurfaceAnimation() }
        
        surfaceDialog!!.setOnDismissListener { stopSurfaceAnimation() }
        surfaceDialog!!.show()
    }
    
    private fun startSurfaceAnimation() {
        isSurfaceAnimating = true
        
        surfaceCallback = object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                if (!isSurfaceAnimating) return
                
                val holder = surfaceBinding!!.surfaceView.holder
                val canvas = holder.lockCanvas() // core lock the canvas
                
                if (canvas != null) {
                    try {
                        canvas.drawColor(0xFFFBFDF8.toInt())
                        
                        circleX += circleVelocityX
                        circleY += circleVelocityY
                        
                        if (circleX <= 30 || circleX >= canvas.width - 30) circleVelocityX = -circleVelocityX
                        if (circleY <= 30 || circleY >= canvas.height - 30) circleVelocityY = -circleVelocityY
                        
                        canvas.drawCircle(circleX, circleY, 30f, paint)
                    } finally {
                        holder.unlockCanvasAndPost(canvas)
                    }
                }
                
                choreographerInstance.postFrameCallback(this)
            }
        }
        
        choreographerInstance.postFrameCallback(surfaceCallback!!)
        surfaceBinding!!.btnStart.isEnabled = false
        surfaceBinding!!.btnStop.isEnabled = true
    }
    
    private fun stopSurfaceAnimation() {
        isSurfaceAnimating = false
        if (surfaceCallback != null) {
            choreographerInstance.removeFrameCallback(surfaceCallback!!)
        }
        surfaceCallback = null
        
        surfaceBinding?.apply {
            btnStart.isEnabled = true
            btnStop.isEnabled = false
        }
    }

    // ==================== 工具方法 ====================
    
    private fun showResultDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("确定", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopFpsMonitor()
        stopSurfaceAnimation()
        fpsDialog?.dismiss()
        surfaceDialog?.dismiss()
        _binding = null
    }

    companion object {
        fun newInstance() = ChoreographerFragment()
    }
}
