package com.peter.window.demo.advanced

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.peter.window.demo.databinding.ActivityFloatingWindowBinding

/**
 * 进阶篇：悬浮窗实现
 * 
 * 悬浮窗是一种特殊的窗口，可以在其他应用之上显示。
 * 
 * 悬浮窗的核心实现：
 * 
 * ```java
 * // 1. 获取 WindowManager
 * WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
 * 
 * // 2. 创建 LayoutParams
 * WindowManager.LayoutParams params = new WindowManager.LayoutParams(
 *     width, height,
 *     type,    // 窗口类型
 *     flags,   // 窗口标志
 *     format   // 像素格式
 * );
 * 
 * // 3. 添加视图
 * wm.addView(view, params);
 * 
 * // 4. 移除视图
 * wm.removeView(view);
 * ```
 * 
 * 重要知识点：
 * 
 * 1. 权限
 *    - Android 6.0+ 需要 SYSTEM_ALERT_WINDOW 权限
 *    - 用户需要手动授权（Settings.ACTION_MANAGE_OVERLAY_PERMISSION）
 * 
 * 2. 窗口类型
 *    - TYPE_PHONE (已废弃): 电话窗口
 *    - TYPE_SYSTEM_ALERT (已废弃): 系统警告
 *    - TYPE_APPLICATION_OVERLAY (Android 8.0+): 应用覆盖窗口
 * 
 * 3. 常用标志
 *    - FLAG_NOT_FOCUSABLE: 不获取焦点
 *    - FLAG_NOT_TOUCH_MODAL: 允许外部触摸穿透
 *    - FLAG_LAYOUT_IN_SCREEN: 允许延伸到屏幕边缘
 * 
 * 4. 生命周期管理
 *    - 确保在不需要时移除悬浮窗
 *    - 处理配置变更（屏幕旋转）
 */
class FloatingWindowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFloatingWindowBinding
    private var floatingView: View? = null
    private var isFloatingWindowShowing = false
    private val sb = StringBuilder()
    
    companion object {
        private const val REQUEST_CODE_OVERLAY_PERMISSION = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFloatingWindowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showFloatingWindowInfo()
    }

    private fun setupListeners() {
        binding.btnCheckPermission.setOnClickListener { checkOverlayPermission() }
        binding.btnRequestPermission.setOnClickListener { requestOverlayPermission() }
        binding.btnShowFloating.setOnClickListener { showFloatingWindow() }
        binding.btnHideFloating.setOnClickListener { hideFloatingWindow() }
        binding.btnDraggableFloating.setOnClickListener { showDraggableFloatingWindow() }
    }

    private fun showFloatingWindowInfo() {
        sb.clear()
        sb.appendLine("=== 悬浮窗概述 ===\n")
        sb.appendLine("悬浮窗可以在其他应用之上显示\n")
        sb.appendLine("权限要求：")
        sb.appendLine("  Android 6.0+ 需要 SYSTEM_ALERT_WINDOW")
        sb.appendLine("  用户需要手动授权\n")
        sb.appendLine("窗口类型：")
        sb.appendLine("  TYPE_APPLICATION_OVERLAY (Android 8.0+)")
        sb.appendLine("  TYPE_PHONE (已废弃)")
        sb.appendLine("  TYPE_SYSTEM_ALERT (已废弃)\n")
        sb.appendLine("当前悬浮窗状态: ${if (isFloatingWindowShowing) "显示中" else "未显示"}\n")
        sb.appendLine("权限状态: ${if (hasOverlayPermission()) "已授权" else "未授权"}")
        
        binding.tvInfo.text = sb.toString()
    }

    /**
     * 检查悬浮窗权限
     */
    private fun checkOverlayPermission() {
        sb.clear()
        sb.appendLine("=== 检查权限 ===\n")
        
        val hasPermission = hasOverlayPermission()
        sb.appendLine("悬浮窗权限: ${if (hasPermission) "已授权 ✓" else "未授权 ✗"}\n")
        
        if (!hasPermission) {
            sb.appendLine("需要请求权限才能显示悬浮窗")
            sb.appendLine("点击「请求权限」按钮")
        } else {
            sb.appendLine("可以显示悬浮窗")
        }
        
        binding.tvResult.text = sb.toString()
        updateStatusText()
    }

    /**
     * 请求悬浮窗权限
     */
    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasOverlayPermission()) {
                // 引导用户到设置页面
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION)
            } else {
                Toast.makeText(this, "已有悬浮窗权限", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Android 6.0 以下默认有权限", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
            sb.clear()
            sb.appendLine("=== 权限申请结果 ===\n")
            
            if (hasOverlayPermission()) {
                sb.appendLine("权限授权成功 ✓")
            } else {
                sb.appendLine("权限被拒绝 ✗")
                sb.appendLine("请在设置中手动开启")
            }
            
            binding.tvResult.text = sb.toString()
            updateStatusText()
        }
    }

    /**
     * 显示基本悬浮窗
     */
    private fun showFloatingWindow() {
        if (!hasOverlayPermission()) {
            Toast.makeText(this, "请先获取悬浮窗权限", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (floatingView != null) {
            Toast.makeText(this, "悬浮窗已显示", Toast.LENGTH_SHORT).show()
            return
        }
        
        sb.clear()
        sb.appendLine("=== 显示悬浮窗 ===\n")
        
        // 创建悬浮视图
        val view = TextView(this).apply {
            text = "悬浮窗\n点击关闭"
            setPadding(32, 32, 32, 32)
            setBackgroundColor(0xE06200EE.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 16f
            setOnClickListener {
                hideFloatingWindow()
            }
        }
        
        // 创建 LayoutParams
        val params = createLayoutParams()
        
        // 获取 WindowManager 并添加视图
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(view, params)
        
        floatingView = view
        isFloatingWindowShowing = true
        
        sb.appendLine("悬浮窗显示成功")
        sb.appendLine("类型: TYPE_APPLICATION_OVERLAY")
        sb.appendLine("位置: (100, 200)")
        sb.appendLine("点击悬浮窗可关闭")
        
        binding.tvResult.text = sb.toString()
        updateStatusText()
    }

    /**
     * 隐藏悬浮窗
     */
    private fun hideFloatingWindow() {
        if (floatingView == null) {
            binding.tvResult.text = "悬浮窗未显示"
            return
        }
        
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.removeView(floatingView)
        floatingView = null
        isFloatingWindowShowing = false
        
        binding.tvResult.text = "悬浮窗已关闭"
        updateStatusText()
    }

    /**
     * 显示可拖动的悬浮窗
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun showDraggableFloatingWindow() {
        if (!hasOverlayPermission()) {
            Toast.makeText(this, "请先获取悬浮窗权限", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 先移除已有的悬浮窗
        hideFloatingWindow()
        
        sb.clear()
        sb.appendLine("=== 可拖动悬浮窗 ===\n")
        
        // 创建悬浮视图
        val view = TextView(this).apply {
            text = "可拖动\n悬浮窗"
            setPadding(32, 32, 32, 32)
            setBackgroundColor(0xE003DAC5.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 16f
        }
        
        // 创建 LayoutParams
        val params = createLayoutParams()
        
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        // 触摸监听实现拖动
        val initialX = IntArray(1)
        val initialY = IntArray(1)
        val initialTouchX = FloatArray(1)
        val initialTouchY = FloatArray(1)
        
        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX[0] = params.x
                    initialY[0] = params.y
                    initialTouchX[0] = event.rawX
                    initialTouchY[0] = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX[0] + (event.rawX - initialTouchX[0]).toInt()
                    params.y = initialY[0] + (event.rawY - initialTouchY[0]).toInt()
                    windowManager.updateViewLayout(view, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    // 检测点击
                    val dx = Math.abs(event.rawX - initialTouchX[0])
                    val dy = Math.abs(event.rawY - initialTouchY[0])
                    if (dx < 10 && dy < 10) {
                        hideFloatingWindow()
                    }
                    true
                }
                else -> false
            }
        }
        
        windowManager.addView(view, params)
        floatingView = view
        isFloatingWindowShowing = true
        
        sb.appendLine("可拖动悬浮窗显示成功")
        sb.appendLine("拖动可移动位置")
        sb.appendLine("点击可关闭")
        
        binding.tvResult.text = sb.toString()
        updateStatusText()
    }

    /**
     * 创建 LayoutParams
     */
    private fun createLayoutParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 200
        }
    }

    /**
     * 检查是否有悬浮窗权限
     */
    private fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }

    private fun updateStatusText() {
        sb.clear()
        sb.appendLine("权限: ${if (hasOverlayPermission()) "已授权" else "未授权"}")
        sb.appendLine("悬浮窗: ${if (isFloatingWindowShowing) "显示中" else "未显示"}")
        binding.tvStatus.text = sb.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 确保移除悬浮窗
        hideFloatingWindow()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
