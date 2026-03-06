package com.peter.window.demo.practice

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.peter.window.demo.databinding.ActivityCustomWindowBinding

/**
 * 实战篇：自定义 Window
 * 
 * 本Activity演示如何创建自定义 Window，包括：
 * 
 * 1. 基本悬浮窗
 * 2. 可拖动悬浮窗
 * 3. 带关闭按钮的悬浮窗
 * 4. 系统级提示窗
 * 5. 锁屏显示窗口
 * 
 * 自定义 Window 的核心步骤：
 * 
 * ```java
 * // 1. 创建 View
 * View view = createCustomView();
 * 
 * // 2. 创建 LayoutParams
 * WindowManager.LayoutParams params = new WindowManager.LayoutParams();
 * params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
 * params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
 * 
 * // 3. 添加到 WindowManager
 * WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
 * wm.addView(view, params);
 * ```
 */
class CustomWindowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomWindowBinding
    private val wm: WindowManager by lazy { 
        getSystemService(Context.WINDOW_SERVICE) as WindowManager 
    }
    private var customWindows = mutableListOf<View>()
    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomWindowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showCustomWindowInfo()
    }

    private fun setupListeners() {
        binding.btnBasicWindow.setOnClickListener { showBasicWindow() }
        binding.btnDraggableWindow.setOnClickListener { showDraggableWindow() }
        binding.btnClosableWindow.setOnClickListener { showClosableWindow() }
        binding.btnNotificationWindow.setOnClickListener { showNotificationStyleWindow() }
        binding.btnRemoveAllWindows.setOnClickListener { removeAllWindows() }
    }

    private fun showCustomWindowInfo() {
        sb.clear()
        sb.appendLine("=== 自定义 Window ===\n")
        sb.appendLine("自定义 Window 的核心组件：\n")
        sb.appendLine("1. View - 窗口的内容视图")
        sb.appendLine("2. WindowManager.LayoutParams - 窗口参数")
        sb.appendLine("3. WindowManager - 窗口管理器\n")
        sb.appendLine("常用窗口类型：")
        sb.appendLine("  TYPE_APPLICATION_OVERLAY - 应用覆盖层")
        sb.appendLine("  TYPE_APPLICATION_PANEL - 应用面板\n")
        sb.appendLine("常用窗口标志：")
        sb.appendLine("  FLAG_NOT_FOCUSABLE - 不获取焦点")
        sb.appendLine("  FLAG_NOT_TOUCH_MODAL - 触摸穿透")
        sb.appendLine("  FLAG_WATCH_OUTSIDE_TOUCH - 监听外部触摸\n")
        sb.appendLine("当前已添加窗口数: ${customWindows.size}")
        
        binding.tvInfo.text = sb.toString()
    }

    private fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else true
    }

    /**
     * 基本悬浮窗
     */
    private fun showBasicWindow() {
        if (!hasOverlayPermission()) {
            Toast.makeText(this, "请先获取悬浮窗权限", Toast.LENGTH_SHORT).show()
            return
        }

        sb.clear()
        sb.appendLine("=== 基本悬浮窗 ===\n")

        // 创建视图
        val view = TextView(this).apply {
            text = "基本悬浮窗\n点击关闭"
            setPadding(32, 32, 32, 32)
            setBackgroundColor(Color.parseColor("#E06200EE"))
            setTextColor(Color.WHITE)
            textSize = 16f
            setOnClickListener { removeWindow(this) }
        }

        // 创建参数
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            getWindowType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 200
        }

        // 添加窗口
        wm.addView(view, params)
        customWindows.add(view)

        sb.appendLine("窗口添加成功")
        sb.appendLine("类型: ${getWindowTypeName()}")
        sb.appendLine("位置: (100, 200)")
        sb.appendLine("点击窗口可关闭")
        
        updateInfo()
        binding.tvResult.text = sb.toString()
    }

    /**
     * 可拖动悬浮窗
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun showDraggableWindow() {
        if (!hasOverlayPermission()) {
            Toast.makeText(this, "请先获取悬浮窗权限", Toast.LENGTH_SHORT).show()
            return
        }

        sb.clear()
        sb.appendLine("=== 可拖动悬浮窗 ===\n")

        // 创建视图
        val view = TextView(this).apply {
            text = "拖动我"
            setPadding(32, 32, 32, 32)
            setBackgroundColor(Color.parseColor("#E003DAC5"))
            setTextColor(Color.WHITE)
            textSize = 18f
        }

        // 创建参数
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            getWindowType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 200
            y = 300
        }

        // 拖动实现
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f

        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    wm.updateViewLayout(view, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val dx = Math.abs(event.rawX - initialTouchX)
                    val dy = Math.abs(event.rawY - initialTouchY)
                    if (dx < 10 && dy < 10) {
                        // 点击事件
                        removeWindow(view)
                    }
                    true
                }
                else -> false
            }
        }

        wm.addView(view, params)
        customWindows.add(view)

        sb.appendLine("可拖动悬浮窗添加成功")
        sb.appendLine("拖动可移动位置")
        sb.appendLine("点击可关闭")
        
        updateInfo()
        binding.tvResult.text = sb.toString()
    }

    /**
     * 带关闭按钮的悬浮窗
     */
    private fun showClosableWindow() {
        if (!hasOverlayPermission()) {
            Toast.makeText(this, "请先获取悬浮窗权限", Toast.LENGTH_SHORT).show()
            return
        }

        sb.clear()
        sb.appendLine("=== 带关闭按钮的悬浮窗 ===\n")

        // 创建容器
        val container = FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#E0FFFFFF"))
            setPadding(16, 16, 16, 16)
        }

        // 内容视图
        val contentView = TextView(this).apply {
            text = "这是一个带关闭按钮的悬浮窗\n可以有更复杂的布局"
            setPadding(24, 24, 24, 24)
            textSize = 16f
            setTextColor(Color.BLACK)
        }

        // 关闭按钮
        val closeButton = TextView(this).apply {
            text = "×"
            textSize = 24f
            setTextColor(Color.RED)
            setPadding(8, 8, 8, 8)
            setOnClickListener { removeWindow(container) }
        }

        container.addView(contentView, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
        })

        container.addView(closeButton, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
        })

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            getWindowType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        wm.addView(container, params)
        customWindows.add(container)

        sb.appendLine("带关闭按钮的悬浮窗添加成功")
        sb.appendLine("点击右上角 × 关闭")
        
        updateInfo()
        binding.tvResult.text = sb.toString()
    }

    /**
     * 通知样式悬浮窗
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun showNotificationStyleWindow() {
        if (!hasOverlayPermission()) {
            Toast.makeText(this, "请先获取悬浮窗权限", Toast.LENGTH_SHORT).show()
            return
        }

        sb.clear()
        sb.appendLine("=== 通知样式悬浮窗 ===\n")

        // 创建通知样式的布局
        val view = layoutInflater.inflate(android.R.layout.simple_list_item_2, null)
        val text1 = view.findViewById<TextView>(android.R.id.text1)
        val text2 = view.findViewById<TextView>(android.R.id.text2)
        
        text1.text = "通知标题"
        text1.setTextColor(Color.BLACK)
        text1.textSize = 16f
        text2.text = "这是通知内容，可以显示更多信息"
        text2.setTextColor(Color.GRAY)
        text2.textSize = 14f
        
        view.setPadding(24, 16, 24, 16)
        view.setBackgroundColor(Color.parseColor("#F5F5F5"))

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            getWindowType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP
            y = 100
        }

        // 点击关闭
        view.setOnClickListener { removeWindow(view) }

        // 3秒后自动关闭
        view.postDelayed({ removeWindow(view) }, 3000)

        wm.addView(view, params)
        customWindows.add(view)

        sb.appendLine("通知样式悬浮窗添加成功")
        sb.appendLine("位置: 屏幕顶部")
        sb.appendLine("3秒后自动关闭")
        
        updateInfo()
        binding.tvResult.text = sb.toString()
    }

    private fun removeWindow(view: View) {
        if (customWindows.contains(view)) {
            wm.removeView(view)
            customWindows.remove(view)
            updateInfo()
        }
    }

    private fun removeAllWindows() {
        customWindows.forEach { view ->
            try {
                wm.removeView(view)
            } catch (e: Exception) {
                // 忽略
            }
        }
        customWindows.clear()
        updateInfo()
        binding.tvResult.text = "所有窗口已移除"
    }

    private fun getWindowType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
    }

    private fun getWindowTypeName(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            "TYPE_APPLICATION_OVERLAY"
        } else {
            "TYPE_PHONE"
        }
    }

    private fun updateInfo() {
        sb.clear()
        sb.appendLine("当前已添加窗口数: ${customWindows.size}")
        binding.tvStatus.text = sb.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeAllWindows()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
