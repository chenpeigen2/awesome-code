package com.peter.os.demo.fragments

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.peter.os.demo.R
import com.peter.os.demo.SurfaceViewAdapter
import com.peter.os.demo.SurfaceViewItem
import com.peter.os.demo.SurfaceViewType
import com.peter.os.demo.ViewCategory
import com.peter.os.demo.databinding.DialogSurfaceAnimationBinding
import com.peter.os.demo.databinding.DialogSurfaceBasicBinding
import com.peter.os.demo.databinding.DialogSurfaceTouchBinding
import com.peter.os.demo.databinding.FragmentSurfaceViewBinding
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * SurfaceView 示例 Fragment
 * 
 * SurfaceView 特点:
 * - 拥有独立的绘图表面
 * - 可以在子线程中绘制，不影响 UI 线程
 * - 适合游戏、视频播放等高性能场景
 * - 默认位于窗口下方，可设置 Z-Order 显示在上层
 */
class SurfaceViewFragment : Fragment() {

    private var _binding: FragmentSurfaceViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSurfaceViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = listOf(
            // 基础用法
            SurfaceViewItem(
                type = SurfaceViewType.BASIC_DRAW,
                title = "基础绘制",
                description = "使用 Canvas 在 SurfaceView 上绘制基本图形",
                category = ViewCategory.BASIC
            ),
            SurfaceViewItem(
                type = SurfaceViewType.THREAD_DRAW,
                title = "子线程绘制",
                description = "在子线程中进行绘制操作",
                category = ViewCategory.BASIC
            ),
            // 动画绘制
            SurfaceViewItem(
                type = SurfaceViewType.ANIMATION_DRAW,
                title = "动画绘制",
                description = "使用独立线程进行流畅的动画绘制",
                category = ViewCategory.ANIMATION
            ),
            SurfaceViewItem(
                type = SurfaceViewType.DOUBLE_BUFFER,
                title = "双缓冲绘制",
                description = "演示双缓冲技术避免闪烁",
                category = ViewCategory.ANIMATION
            ),
            // 高级应用
            SurfaceViewItem(
                type = SurfaceViewType.TOUCH_DRAW,
                title = "触摸绘制",
                description = "响应触摸事件进行实时绘制",
                category = ViewCategory.ADVANCED
            )
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = SurfaceViewAdapter(items) { type ->
            onItemClick(type)
        }
    }

    private fun onItemClick(type: SurfaceViewType) {
        when (type) {
            SurfaceViewType.BASIC_DRAW -> showBasicDrawDialog()
            SurfaceViewType.ANIMATION_DRAW -> showAnimationDialog()
            SurfaceViewType.DOUBLE_BUFFER -> showDoubleBufferDemo()
            SurfaceViewType.THREAD_DRAW -> showThreadDrawDialog()
            SurfaceViewType.TOUCH_DRAW -> showTouchDrawDialog()
        }
    }

    // ==================== 基础绘制 ====================
    
    private fun showBasicDrawDialog() {
        val dialogBinding = DialogSurfaceBasicBinding.inflate(layoutInflater)
        
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("基础绘制")
            .setView(dialogBinding.root)
            .setNegativeButton("关闭", null)
            .create()

        var drawCount = 0
        
        // 设置 Z-Order 让 SurfaceView 显示在上层
        dialogBinding.surfaceView.setZOrderOnTop(true)
        
        dialogBinding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                // 初始绘制
                drawBasicShapes(holder)
            }
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {}
        })

        dialogBinding.btnDraw.setOnClickListener {
            drawCount++
            val canvas = dialogBinding.surfaceView.holder.lockCanvas()
            if (canvas != null) {
                try {
                    // 随机绘制
                    val paint = Paint().apply {
                        color = Color.rgb(
                            Random.nextInt(256),
                            Random.nextInt(256),
                            Random.nextInt(256)
                        )
                        isAntiAlias = true
                    }
                    
                    val x = Random.nextInt(canvas.width - 100) + 50f
                    val y = Random.nextInt(canvas.height - 100) + 50f
                    val radius = Random.nextInt(50) + 20f
                    
                    canvas.drawCircle(x, y, radius, paint)
                } finally {
                    dialogBinding.surfaceView.holder.unlockCanvasAndPost(canvas)
                }
            }
        }

        dialogBinding.btnClear.setOnClickListener {
            drawBasicShapes(dialogBinding.surfaceView.holder)
        }

        dialog.show()
    }

    private fun drawBasicShapes(holder: SurfaceHolder) {
        val canvas = holder.lockCanvas()
        if (canvas != null) {
            try {
                // 清除背景
                canvas.drawColor(0xFFFBFDF8.toInt())
                
                // 绘制各种图形
                val paint = Paint().apply {
                    isAntiAlias = true
                    strokeWidth = 4f
                }
                
                // 矩形
                paint.color = 0xFF006C4C.toInt()
                paint.style = Paint.Style.FILL
                canvas.drawRect(50f, 50f, 150f, 150f, paint)
                
                // 圆形
                paint.color = 0xFF0061A4.toInt()
                canvas.drawCircle(250f, 100f, 50f, paint)
                
                // 椭圆
                paint.color = 0xFF7D5800.toInt()
                canvas.drawOval(50f, 200f, 250f, 280f, paint)
                
                // 线条
                paint.color = 0xFF6750A4.toInt()
                paint.style = Paint.Style.STROKE
                canvas.drawLine(50f, 320f, 300f, 380f, paint)
                
                // 文字
                paint.color = 0xFF191C1A.toInt()
                paint.style = Paint.Style.FILL
                paint.textSize = 40f
                canvas.drawText("SurfaceView 绘制", 50f, 450f, paint)
            } finally {
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }

    // ==================== 动画绘制 ====================
    
    private var animationThread: Thread? = null
    private var isAnimating = false

    private fun showAnimationDialog() {
        val dialogBinding = DialogSurfaceAnimationBinding.inflate(layoutInflater)
        
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("动画绘制")
            .setView(dialogBinding.root)
            .setNegativeButton("关闭") { _, _ ->
                stopAnimation()
            }
            .setCancelable(false)
            .create()

        var balls = mutableListOf<Ball>()
        
        // 设置 Z-Order 让 SurfaceView 显示在上层
        dialogBinding.surfaceView.setZOrderOnTop(true)
        
        dialogBinding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                // 初始化球
                balls = mutableListOf()
                for (i in 0 until 10) {
                    balls.add(Ball(
                        x = Random.nextInt(300).toFloat(),
                        y = Random.nextInt(200).toFloat() + 50,
                        vx = Random.nextInt(10) - 5f,
                        vy = Random.nextInt(10) - 5f,
                        radius = Random.nextInt(20) + 15f,
                        color = Color.rgb(
                            Random.nextInt(256),
                            Random.nextInt(256),
                            Random.nextInt(256)
                        )
                    ))
                }
            }
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                stopAnimation()
            }
        })

        dialogBinding.btnStart.setOnClickListener {
            if (!isAnimating) {
                isAnimating = true
                dialogBinding.btnStart.isEnabled = false
                dialogBinding.btnStop.isEnabled = true
                
                animationThread = Thread {
                    var lastTime = System.currentTimeMillis()
                    var frameCount = 0
                    
                    while (isAnimating) {
                        val canvas = dialogBinding.surfaceView.holder.lockCanvas()
                        if (canvas != null) {
                            try {
                                canvas.drawColor(0xFFFBFDF8.toInt())
                                
                                // 更新和绘制球
                                synchronized(balls) {
                                    for (ball in balls) {
                                        ball.update(canvas.width, canvas.height)
                                        ball.draw(canvas)
                                    }
                                }
                                
                                // 计算 FPS
                                frameCount++
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastTime >= 1000) {
                                    val fps = frameCount * 1000f / (currentTime - lastTime)
                                    activity?.runOnUiThread {
                                        dialogBinding.tvFps.text = "FPS: ${String.format("%.1f", fps)}"
                                    }
                                    frameCount = 0
                                    lastTime = currentTime
                                }
                            } finally {
                                dialogBinding.surfaceView.holder.unlockCanvasAndPost(canvas)
                            }
                        }
                        
                        try {
                            Thread.sleep(16) // ~60fps
                        } catch (e: InterruptedException) {
                            break
                        }
                    }
                }.apply { start() }
            }
        }

        dialogBinding.btnStop.setOnClickListener {
            stopAnimation()
            dialogBinding.btnStart.isEnabled = true
            dialogBinding.btnStop.isEnabled = false
        }

        dialog.setOnDismissListener {
            stopAnimation()
        }
        
        dialog.show()
    }

    private fun stopAnimation() {
        isAnimating = false
        animationThread?.interrupt()
        animationThread = null
    }

    private data class Ball(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        val radius: Float,
        val color: Int
    ) {
        private val paint = Paint().apply {
            isAntiAlias = true
            this.color = this@Ball.color
        }

        fun update(width: Int, height: Int) {
            x += vx
            y += vy
            
            if (x - radius < 0 || x + radius > width) {
                vx = -vx
                x = x.coerceIn(radius, width - radius)
            }
            if (y - radius < 0 || y + radius > height) {
                vy = -vy
                y = y.coerceIn(radius, height - radius)
            }
        }

        fun draw(canvas: Canvas) {
            canvas.drawCircle(x, y, radius, paint)
        }
    }

    // ==================== 双缓冲演示 ====================
    
    private fun showDoubleBufferDemo() {
        val sb = StringBuilder()
        
        sb.appendLine("=== 双缓冲技术 ===")
        sb.appendLine()
        sb.appendLine("什么是双缓冲?")
        sb.appendLine("• 使用两个缓冲区交替绘制")
        sb.appendLine("• 前台缓冲显示，后台缓冲绘制")
        sb.appendLine("• 绘制完成后交换缓冲区")
        sb.appendLine()
        sb.appendLine("为什么需要双缓冲?")
        sb.appendLine("• 避免画面闪烁")
        sb.appendLine("• 避免画面撕裂")
        sb.appendLine("• 提供流畅的视觉体验")
        sb.appendLine()
        sb.appendLine("SurfaceView 中的双缓冲:")
        sb.appendLine("• lockCanvas() 锁定后台缓冲")
        sb.appendLine("• unlockCanvasAndPost() 提交并交换")
        sb.appendLine("• 系统自动管理缓冲区")
        sb.appendLine()
        sb.appendLine("vs 单缓冲:")
        sb.appendLine("单缓冲: 直接绘制到显示缓冲")
        sb.appendLine("        可能看到绘制过程 → 闪烁")
        sb.appendLine("双缓冲: 完整绘制后才显示")
        sb.appendLine("        用户只看到完整画面 → 流畅")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("双缓冲绘制")
            .setMessage(sb.toString())
            .setPositiveButton("确定", null)
            .show()
    }

    // ==================== 子线程绘制 ====================
    
    private var threadDrawThread: Thread? = null
    private var isThreadDrawing = false

    private fun showThreadDrawDialog() {
        val sb = StringBuilder()
        
        sb.appendLine("=== 子线程绘制 ===")
        sb.appendLine()
        sb.appendLine("SurfaceView 最大的优势:")
        sb.appendLine("• 可以在子线程中绘制")
        sb.appendLine("• 不会阻塞 UI 线程")
        sb.appendLine("• 适合复杂/耗时的绘制操作")
        sb.appendLine()
        sb.appendLine("代码示例:")
        sb.appendLine()
        sb.appendLine("Thread {")
        sb.appendLine("  val canvas = holder.lockCanvas()")
        sb.appendLine("  // 绘制操作...")
        sb.appendLine("  holder.unlockCanvasAndPost(canvas)")
        sb.appendLine("}.start()")
        sb.appendLine()
        sb.appendLine("注意事项:")
        sb.appendLine("• 确保线程安全")
        sb.appendLine("• 正确处理 Surface 生命周期")
        sb.appendLine("• 在 surfaceDestroyed 时停止线程")
        sb.appendLine()
        sb.appendLine("vs 普通 View:")
        sb.appendLine("普通 View 必须在主线程绘制")
        sb.appendLine("复杂绘制会导致界面卡顿")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("子线程绘制")
            .setMessage(sb.toString())
            .setPositiveButton("确定", null)
            .show()
    }

    // ==================== 触摸绘制 ====================
    
    private var currentPaintColor = Color.RED
    private val drawPath = Path()
    private val drawPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private var touchDrawThread: Thread? = null
    private var isTouchDrawing = false
    private var touchX = 0f
    private var touchY = 0f
    private var isTouching = false

    private fun showTouchDrawDialog() {
        val dialogBinding = DialogSurfaceTouchBinding.inflate(layoutInflater)
        
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("触摸绘制")
            .setView(dialogBinding.root)
            .setNegativeButton("关闭") { _, _ ->
                stopTouchDraw()
            }
            .setCancelable(false)
            .create()

        val paths = mutableListOf<Pair<Path, Int>>()
        
        // 设置 Z-Order 让 SurfaceView 显示在上层
        dialogBinding.surfaceView.setZOrderOnTop(true)
        
        dialogBinding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                val canvas = holder.lockCanvas()
                if (canvas != null) {
                    canvas.drawColor(0xFFFBFDF8.toInt())
                    holder.unlockCanvasAndPost(canvas)
                }
                startTouchDrawThread(holder, paths)
            }
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                stopTouchDraw()
            }
        })

        dialogBinding.surfaceView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchX = event.x
                    touchY = event.y
                    isTouching = true
                    drawPath.reset()
                    drawPath.moveTo(event.x, event.y)
                }
                MotionEvent.ACTION_MOVE -> {
                    touchX = event.x
                    touchY = event.y
                    drawPath.lineTo(event.x, event.y)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isTouching = false
                    if (!drawPath.isEmpty) {
                        synchronized(paths) {
                            paths.add(Pair(Path(drawPath), drawPaint.color))
                        }
                        drawPath.reset()
                    }
                }
            }
            true
        }

        dialogBinding.btnRed.setOnClickListener { drawPaint.color = Color.RED }
        dialogBinding.btnGreen.setOnClickListener { drawPaint.color = Color.GREEN }
        dialogBinding.btnBlue.setOnClickListener { drawPaint.color = Color.BLUE }

        dialogBinding.btnClear.setOnClickListener {
            synchronized(paths) {
                paths.clear()
            }
            val canvas = dialogBinding.surfaceView.holder.lockCanvas()
            if (canvas != null) {
                canvas.drawColor(0xFFFBFDF8.toInt())
                dialogBinding.surfaceView.holder.unlockCanvasAndPost(canvas)
            }
        }

        dialog.setOnDismissListener {
            stopTouchDraw()
        }
        
        dialog.show()
    }

    private fun startTouchDrawThread(holder: SurfaceHolder, paths: MutableList<Pair<Path, Int>>) {
        isTouchDrawing = true
        touchDrawThread = Thread {
            while (isTouchDrawing) {
                val canvas = holder.lockCanvas()
                if (canvas != null) {
                    try {
                        canvas.drawColor(0xFFFBFDF8.toInt())
                        
                        // 绘制已保存的路径
                        synchronized(paths) {
                            for ((path, color) in paths) {
                                val paint = Paint().apply {
                                    this.color = color
                                    style = Paint.Style.STROKE
                                    strokeWidth = 8f
                                    isAntiAlias = true
                                    strokeCap = Paint.Cap.ROUND
                                    strokeJoin = Paint.Join.ROUND
                                }
                                canvas.drawPath(path, paint)
                            }
                        }
                        
                        // 绘制当前路径
                        if (isTouching) {
                            canvas.drawPath(drawPath, drawPaint)
                        }
                    } finally {
                        holder.unlockCanvasAndPost(canvas)
                    }
                }
                
                try {
                    Thread.sleep(16)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }.apply { start() }
    }

    private fun stopTouchDraw() {
        isTouchDrawing = false
        touchDrawThread?.interrupt()
        touchDrawThread = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAnimation()
        stopTouchDraw()
        _binding = null
    }

    companion object {
        fun newInstance() = SurfaceViewFragment()
    }
}
