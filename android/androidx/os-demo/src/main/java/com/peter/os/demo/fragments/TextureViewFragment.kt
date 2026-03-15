package com.peter.os.demo.fragments

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.peter.os.demo.R
import com.peter.os.demo.TextureViewAdapter
import com.peter.os.demo.TextureViewItem
import com.peter.os.demo.TextureViewType
import com.peter.os.demo.ViewCategory
import com.peter.os.demo.databinding.DialogTextureBasicBinding
import com.peter.os.demo.databinding.DialogTextureTransformBinding
import com.peter.os.demo.databinding.FragmentTextureViewBinding
import kotlin.random.Random

/**
 * TextureView 示例 Fragment
 * 
 * TextureView 特点:
 * - 与普通 View 一样，可以设置透明度、旋转等属性
 * - 必须在硬件加速窗口中使用
 * - 适合视频播放、相机预览等场景
 * - 内容更新通过 SurfaceTextureListener 回调
 */
class TextureViewFragment : Fragment() {

    private var _binding: FragmentTextureViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTextureViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = listOf(
            // 基础用法
            TextureViewItem(
                type = TextureViewType.BASIC_DRAW,
                title = "基础绘制",
                description = "使用 Canvas 在 TextureView 上绘制图形",
                category = ViewCategory.BASIC
            ),
            TextureViewItem(
                type = TextureViewType.SURFACE_TEXTURE,
                title = "SurfaceTexture",
                description = "理解 SurfaceTexture 的工作原理",
                category = ViewCategory.BASIC
            ),
            // 动画绘制
            TextureViewItem(
                type = TextureViewType.ANIMATION_DRAW,
                title = "动画绘制",
                description = "在 TextureView 上进行动画绘制",
                category = ViewCategory.ANIMATION
            ),
            // 高级应用
            TextureViewItem(
                type = TextureViewType.TRANSFORM,
                title = "变换效果",
                description = "旋转、缩放、透明度等变换",
                category = ViewCategory.ADVANCED
            ),
            TextureViewItem(
                type = TextureViewType.LAYER_BLEND,
                title = "图层混合",
                description = "TextureView 的透明度和图层混合",
                category = ViewCategory.ADVANCED
            )
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = TextureViewAdapter(items) { type ->
            onItemClick(type)
        }
    }

    private fun onItemClick(type: TextureViewType) {
        when (type) {
            TextureViewType.BASIC_DRAW -> showBasicDrawDialog()
            TextureViewType.ANIMATION_DRAW -> showAnimationDrawDialog()
            TextureViewType.TRANSFORM -> showTransformDialog()
            TextureViewType.LAYER_BLEND -> showLayerBlendDemo()
            TextureViewType.SURFACE_TEXTURE -> showSurfaceTextureDemo()
        }
    }

    // ==================== 基础绘制 ====================
    
    private fun showBasicDrawDialog() {
        val dialogBinding = DialogTextureBasicBinding.inflate(layoutInflater)
        
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("基础绘制")
            .setView(dialogBinding.root)
            .setNegativeButton("关闭", null)
            .create()

        var drawCount = 0
        var surface: Surface? = null
        
        dialogBinding.textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
                surface = Surface(surfaceTexture)
                drawBasicShapes(surface!!)
            }
            override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {}
            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                surface?.release()
                surface = null
                return true
            }
            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
        }

        dialogBinding.btnDraw.setOnClickListener {
            surface?.let { s ->
                val canvas = s.lockCanvas(null)
                if (canvas != null) {
                    try {
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
                        s.unlockCanvasAndPost(canvas)
                    }
                }
            }
        }

        dialogBinding.btnClear.setOnClickListener {
            surface?.let { drawBasicShapes(it) }
        }

        dialog.show()
    }

    private fun drawBasicShapes(surface: Surface) {
        val canvas = surface.lockCanvas(null)
        if (canvas != null) {
            try {
                canvas.drawColor(0xFFFBFDF8.toInt())
                
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
                
                // 文字
                paint.color = 0xFF191C1A.toInt()
                paint.style = Paint.Style.FILL
                paint.textSize = 40f
                canvas.drawText("TextureView 绘制", 50f, 400f, paint)
            } finally {
                surface.unlockCanvasAndPost(canvas)
            }
        }
    }

    // ==================== 动画绘制 ====================
    
    private var textureAnimThread: Thread? = null
    private var isTextureAnimating = false

    private fun showAnimationDrawDialog() {
        val dialogBinding = DialogTextureBasicBinding.inflate(layoutInflater)
        
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("动画绘制")
            .setView(dialogBinding.root)
            .setNegativeButton("关闭") { _, _ ->
                stopTextureAnimation()
            }
            .setCancelable(false)
            .create()

        var surface: Surface? = null
        var balls = mutableListOf<TextureBall>()
        
        dialogBinding.textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
                surface = Surface(surfaceTexture)
                // 初始化球
                balls = mutableListOf()
                for (i in 0 until 8) {
                    balls.add(TextureBall(
                        x = Random.nextInt(250).toFloat() + 25f,
                        y = Random.nextInt(200).toFloat() + 50f,
                        vx = Random.nextInt(8) - 4f,
                        vy = Random.nextInt(8) - 4f,
                        radius = Random.nextInt(25) + 15f,
                        color = Color.rgb(
                            Random.nextInt(256),
                            Random.nextInt(256),
                            Random.nextInt(256)
                        )
                    ))
                }
            }
            override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {}
            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                stopTextureAnimation()
                surface?.release()
                surface = null
                return true
            }
            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
        }

        dialogBinding.btnDraw.setOnClickListener {
            if (!isTextureAnimating && surface != null) {
                isTextureAnimating = true
                textureAnimThread = Thread {
                    while (isTextureAnimating && surface != null) {
                        val canvas = surface!!.lockCanvas(null)
                        if (canvas != null) {
                            try {
                                canvas.drawColor(0xFFFBFDF8.toInt())
                                
                                for (ball in balls) {
                                    ball.update(canvas.width, canvas.height)
                                    ball.draw(canvas)
                                }
                            } finally {
                                surface!!.unlockCanvasAndPost(canvas)
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
        }

        dialogBinding.btnClear.setOnClickListener {
            stopTextureAnimation()
            surface?.let { drawBasicShapes(it) }
        }

        dialog.setOnDismissListener {
            stopTextureAnimation()
        }
        
        dialog.show()
    }

    private fun stopTextureAnimation() {
        isTextureAnimating = false
        textureAnimThread?.interrupt()
        textureAnimThread = null
    }

    private data class TextureBall(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        val radius: Float,
        val color: Int
    ) {
        private val paint = Paint().apply {
            isAntiAlias = true
            this.color = this@TextureBall.color
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

    // ==================== 变换效果 ====================
    
    private var transformAnimThread: Thread? = null
    private var isTransformAnimating = false

    private fun showTransformDialog() {
        val dialogBinding = DialogTextureTransformBinding.inflate(layoutInflater)
        
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("变换效果")
            .setView(dialogBinding.root)
            .setNegativeButton("关闭") { _, _ ->
                stopTransformAnimation()
            }
            .setCancelable(false)
            .create()

        var surface: Surface? = null
        
        dialogBinding.textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
                surface = Surface(surfaceTexture)
                drawTransformDemo(surface!!)
            }
            override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {}
            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                stopTransformAnimation()
                surface?.release()
                surface = null
                return true
            }
            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
        }

        dialogBinding.seekRotation.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                dialogBinding.textureView.rotation = progress.toFloat()
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        dialogBinding.seekScale.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                val scale = progress / 100f
                dialogBinding.textureView.scaleX = scale
                dialogBinding.textureView.scaleY = scale
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        dialogBinding.seekAlpha.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                dialogBinding.textureView.alpha = progress / 100f
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        dialog.setOnDismissListener {
            stopTransformAnimation()
        }
        
        dialog.show()
    }

    private fun drawTransformDemo(surface: Surface) {
        val canvas = surface.lockCanvas(null)
        if (canvas != null) {
            try {
                // 绘制渐变背景
                val paint = Paint().apply {
                    isAntiAlias = true
                }
                
                // 绘制多个圆形
                for (i in 0 until 10) {
                    paint.color = Color.rgb(
                        (i * 25) % 256,
                        (255 - i * 20) % 256,
                        (i * 30 + 100) % 256
                    )
                    canvas.drawCircle(
                        canvas.width / 2f,
                        canvas.height / 2f,
                        80f + i * 8,
                        paint.apply { style = Paint.Style.STROKE; strokeWidth = 3f }
                    )
                }
                
                // 中心文字
                paint.color = Color.WHITE
                paint.style = Paint.Style.FILL
                paint.textSize = 24f
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText("TextureView", canvas.width / 2f, canvas.height / 2f - 10, paint)
                canvas.drawText("可变换", canvas.width / 2f, canvas.height / 2f + 20, paint)
            } finally {
                surface.unlockCanvasAndPost(canvas)
            }
        }
    }

    private fun stopTransformAnimation() {
        isTransformAnimating = false
        transformAnimThread?.interrupt()
        transformAnimThread = null
    }

    // ==================== 图层混合 ====================
    
    private fun showLayerBlendDemo() {
        val sb = StringBuilder()
        
        sb.appendLine("=== 图层混合 ===")
        sb.appendLine()
        sb.appendLine("TextureView 透明度特性:")
        sb.appendLine("• 支持 setAlpha() 设置透明度")
        sb.appendLine("• 可以看到下层 View 内容")
        sb.appendLine("• 适合实现视频叠加效果")
        sb.appendLine()
        sb.appendLine("vs SurfaceView:")
        sb.appendLine("SurfaceView 默认不透明")
        sb.appendLine("需要设置 setZOrderOnTop(true)")
        sb.appendLine("但会覆盖其他 View")
        sb.appendLine()
        sb.appendLine("应用场景:")
        sb.appendLine("• 视频播放器 (半透明控制层)")
        sb.appendLine("• 相机预览 (实时滤镜叠加)")
        sb.appendLine("• AR 应用 (虚拟物体叠加)")
        sb.appendLine("• 视频聊天 (悬浮窗口)")
        sb.appendLine()
        sb.appendLine("代码示例:")
        sb.appendLine("textureView.alpha = 0.5f")
        sb.appendLine("textureView.setOpaque(false)")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("图层混合")
            .setMessage(sb.toString())
            .setPositiveButton("确定", null)
            .show()
    }

    // ==================== SurfaceTexture 原理 ====================
    
    private fun showSurfaceTextureDemo() {
        val sb = StringBuilder()
        
        sb.appendLine("=== SurfaceTexture 原理 ===")
        sb.appendLine()
        sb.appendLine("什么是 SurfaceTexture?")
        sb.appendLine("• 捕获图像流为 OpenGL ES 纹理")
        sb.appendLine("• 可以获取每一帧的内容")
        sb.appendLine("• 支持图像处理/滤镜效果")
        sb.appendLine()
        sb.appendLine("工作流程:")
        sb.appendLine("1. onSurfaceTextureAvailable()")
        sb.appendLine("   SurfaceTexture 创建完成")
        sb.appendLine()
        sb.appendLine("2. onSurfaceTextureUpdated()")
        sb.appendLine("   每帧更新时调用")
        sb.appendLine("   可获取最新帧数据")
        sb.appendLine()
        sb.appendLine("3. onSurfaceTextureDestroyed()")
        sb.appendLine("   SurfaceTexture 销毁")
        sb.appendLine("   返回 true 表示释放资源")
        sb.appendLine()
        sb.appendLine("获取帧数据:")
        sb.appendLine("surfaceTexture.updateTexImage()")
        sb.appendLine("surfaceTexture.getTransformMatrix()")
        sb.appendLine()
        sb.appendLine("应用场景:")
        sb.appendLine("• 视频帧处理")
        sb.appendLine("• 相机滤镜")
        sb.appendLine("• 视频截图")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("SurfaceTexture 原理")
            .setMessage(sb.toString())
            .setPositiveButton("确定", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopTextureAnimation()
        stopTransformAnimation()
        _binding = null
    }

    companion object {
        fun newInstance() = TextureViewFragment()
    }
}
