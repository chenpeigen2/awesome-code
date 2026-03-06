package com.peter.window.demo.practice

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast

/**
 * 悬浮窗服务
 * 
 * 使用 Service 管理悬浮窗，可以独立于 Activity 生命周期存在。
 * 
 * 服务管理悬浮窗的优点：
 * 1. 生命周期独立，不受 Activity 影响
 * 2. 可以在后台持续运行
 * 3. 可以跨进程操作
 * 
 * 使用步骤：
 * 1. 在 Manifest 中注册服务
 * 2. 获取悬浮窗权限
 * 3. 启动服务
 * 4. 绑定服务获取控制接口
 * 5. 通过服务控制悬浮窗
 */
class FloatingWindowService : Service() {

    private lateinit var windowManager: WindowManager
    private val windowRecords = mutableListOf<WindowRecord>()
    private var windowId = 0
    
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): FloatingWindowService = this@FloatingWindowService
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    /**
     * 显示悬浮窗
     */
    @SuppressLint("ClickableViewAccessibility")
    fun showFloatingWindow(title: String): Int {
        val id = ++windowId
        
        // 创建视图
        val view = createWindowView(id, title)
        
        // 创建参数
        val params = createLayoutParams().apply {
            // 根据已有窗口数量调整位置
            x = 100 + (windowRecords.size * 50)
            y = 200 + (windowRecords.size * 100)
        }
        
        // 添加窗口
        windowManager.addView(view, params)
        
        // 记录窗口
        windowRecords.add(WindowRecord(id, view, params))
        
        return id
    }

    /**
     * 隐藏悬浮窗
     */
    fun hideFloatingWindow() {
        if (windowRecords.isNotEmpty()) {
            val record = windowRecords.removeAt(windowRecords.size - 1)
            windowManager.removeView(record.view)
        }
    }

    /**
     * 隐藏指定窗口
     */
    fun hideWindow(id: Int) {
        val index = windowRecords.indexOfFirst { it.id == id }
        if (index >= 0) {
            val record = windowRecords.removeAt(index)
            windowManager.removeView(record.view)
        }
    }

    /**
     * 隐藏所有窗口
     */
    fun hideAllWindows() {
        windowRecords.forEach { record ->
            windowManager.removeView(record.view)
        }
        windowRecords.clear()
    }

    /**
     * 窗口是否显示中
     */
    fun isWindowShowing(): Boolean {
        return windowRecords.isNotEmpty()
    }

    /**
     * 执行窗口动画
     */
    fun animateWindow() {
        windowRecords.forEach { record ->
            // 创建位移动画
            val animation = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f
            ).apply {
                duration = 500
                interpolator = AccelerateDecelerateInterpolator()
                repeatCount = 1
                repeatMode = Animation.REVERSE
            }
            record.view.startAnimation(animation)
        }
    }

    /**
     * 更新窗口位置
     */
    fun updateWindowPosition(id: Int, x: Int, y: Int) {
        val record = windowRecords.find { it.id == id }
        record?.let {
            it.params.x = x
            it.params.y = y
            windowManager.updateViewLayout(it.view, it.params)
        }
    }

    /**
     * 获取窗口数量
     */
    fun getWindowCount(): Int = windowRecords.size

    /**
     * 创建窗口视图
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun createWindowView(id: Int, title: String): View {
        val container = FrameLayout(this).apply {
            setBackgroundColor(0xE06200EE.toInt())
            setPadding(24, 24, 24, 24)
        }
        
        // 标题
        val titleView = TextView(this).apply {
            text = "$title\nID: $id\n拖动移动\n点击关闭"
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 14f
            gravity = Gravity.CENTER
        }
        
        container.addView(titleView)
        
        // 设置触摸监听（拖动）
        container.setOnTouchListener(createTouchListener(container))
        
        // 设置点击监听（关闭）
        container.setOnClickListener {
            hideWindow(id)
        }
        
        return container
    }

    /**
     * 创建触摸监听器（支持拖动）
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun createTouchListener(view: View): View.OnTouchListener {
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var params: WindowManager.LayoutParams? = null
        
        return View.OnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    params = (view.layoutParams as? WindowManager.LayoutParams)?.let {
                        initialX = it.x
                        initialY = it.y
                        it
                    }
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params?.let { p ->
                        p.x = initialX + (event.rawX - initialTouchX).toInt()
                        p.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(view, p)
                    }
                    true
                }
                else -> false
            }
        }
    }

    /**
     * 创建 LayoutParams
     */
    private fun createLayoutParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
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
    }

    private fun getWindowType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hideAllWindows()
    }

    /**
     * 窗口记录
     */
    data class WindowRecord(
        val id: Int,
        val view: View,
        val params: WindowManager.LayoutParams
    )
}
