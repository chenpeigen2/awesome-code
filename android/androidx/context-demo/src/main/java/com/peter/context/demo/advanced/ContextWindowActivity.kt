package com.peter.context.demo.advanced

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.peter.context.demo.R
import com.peter.context.demo.databinding.ActivityContextWindowBinding

private const val REQUEST_CODE_OVERLAY_PERMISSION = 1001

/**
 * Context Window 和 Dialog 示例
 * 
 * 重要概念：
 * 1. Dialog 必须使用 Activity Context 创建
 * 2. WindowManager 可以添加悬浮窗
 * 3. 不同 Context 对 Window 操作的影响
 * 
 * 使用 Application Context 创建 Dialog 会抛出异常：
 * android.view.WindowManager$BadTokenException: Unable to add window
 * 
 * 原因：Dialog 需要 Window 来显示，而只有 Activity 拥有 Window
 */
class ContextWindowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContextWindowBinding
    private val sb = StringBuilder()
    private var floatingView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContextWindowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showWindowInfo()
    }

    private fun setupListeners() {
        // Dialog 示例
        binding.btnAlertDialog.setOnClickListener { showAlertDialog() }
        binding.btnProgressDialog.setOnClickListener { showProgressDialog() }
        binding.btnCustomDialog.setOnClickListener { showCustomDialog() }
        binding.btnDatePickerDialog.setOnClickListener { showDatePickerDialog() }
        
        // Window 示例
        binding.btnAddFloatingWindow.setOnClickListener { addFloatingWindow() }
        binding.btnRemoveFloatingWindow.setOnClickListener { removeFloatingWindow() }
        
        // 错误示例
        binding.btnDialogWithAppContext.setOnClickListener { showDialogWithApplicationContext() }
    }

    private fun showWindowInfo() {
        sb.clear()
        
        sb.appendLine("=== Context 与 Window/Dialog ===\n")
        
        // 1. Window 基础概念
        sb.appendLine("=== 1. Window 基础概念 ===")
        sb.appendLine("Window 是 Android 的窗口概念:")
        sb.appendLine("  - 每个 Activity 都有一个 Window")
        sb.appendLine("  - Dialog 需要依附于 Window")
        sb.appendLine("  - PopupMenu/PopupWindow 也需要 Window")
        sb.appendLine("  - Toast 使用系统 Window")
        sb.appendLine()
        
        // 2. Context 对 Dialog 的影响
        sb.appendLine("=== 2. Context 对 Dialog 的影响 ===")
        sb.appendLine("Activity Context:")
        sb.appendLine("  ✓ 可以创建 Dialog")
        sb.appendLine("  ✓ Dialog 使用 Activity 的主题")
        sb.appendLine("  ✓ Dialog 随 Activity 销毁而销毁")
        sb.appendLine()
        sb.appendLine("Application Context:")
        sb.appendLine("  ✗ 不能创建普通 Dialog")
        sb.appendLine("  ✗ 会抛出 BadTokenException")
        sb.appendLine("  ✓ 可以创建系统级 Dialog (需要权限)")
        sb.appendLine()
        
        // 3. WindowManager
        sb.appendLine("=== 3. WindowManager ===")
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        sb.appendLine("WindowManager: $wm")
        sb.appendLine("默认显示: ${wm.defaultDisplay}")
        sb.appendLine()
        
        sb.appendLine("WindowManager 功能:")
        sb.appendLine("  addView(view, params) - 添加视图")
        sb.appendLine("  updateViewLayout(view, params) - 更新布局")
        sb.appendLine("  removeView(view) - 移除视图")
        sb.appendLine()
        
        // 4. 悬浮窗权限
        sb.appendLine("=== 4. 悬浮窗权限 ===")
        sb.appendLine("Android 6.0+ 需要 SYSTEM_ALERT_WINDOW 权限")
        sb.appendLine("Settings.canDrawOverlays(context) 检查权限")
        sb.appendLine()
        
        // 5. Window 类型
        sb.appendLine("=== 5. Window 类型 ===")
        sb.appendLine("TYPE_APPLICATION (普通应用窗口)")
        sb.appendLine("TYPE_APPLICATION_PANEL (面板窗口)")
        sb.appendLine("TYPE_APPLICATION_ATTACHED_DIALOG")
        sb.appendLine("TYPE_PHONE (电话窗口, 需权限)")
        sb.appendLine("TYPE_SYSTEM_ALERT (系统警告, 需权限)")
        sb.appendLine("TYPE_TOAST (Toast窗口)")
        sb.appendLine()
        
        binding.tvInfo.text = sb.toString()
    }

    private fun showAlertDialog() {
        sb.clear()
        sb.appendLine("=== AlertDialog ===\n")
        
        // 使用 Activity Context 创建
        AlertDialog.Builder(this)
            .setTitle("AlertDialog 标题")
            .setMessage("这是一个 AlertDialog 示例\n使用 Activity Context 创建")
            .setIcon(R.mipmap.ic_launcher)
            .setPositiveButton("确定") { dialog, which ->
                Toast.makeText(this, "点击了确定", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消") { dialog, which ->
                Toast.makeText(this, "点击了取消", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("忽略") { dialog, which ->
                Toast.makeText(this, "点击了忽略", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(true)
            .show()
        
        sb.appendLine("AlertDialog 创建成功")
        sb.appendLine("使用 Activity Context (this)")
        sb.appendLine()
        
        sb.appendLine("AlertDialog.Builder 方法:")
        sb.appendLine("  setTitle() - 设置标题")
        sb.appendLine("  setMessage() - 设置消息")
        sb.appendLine("  setIcon() - 设置图标")
        sb.appendLine("  setPositiveButton() - 确定按钮")
        sb.appendLine("  setNegativeButton() - 取消按钮")
        sb.appendLine("  setNeutralButton() - 中性按钮")
        sb.appendLine("  setItems() - 列表选项")
        sb.appendLine("  setMultiChoiceItems() - 多选列表")
        sb.appendLine("  setSingleChoiceItems() - 单选列表")
        sb.appendLine("  setCancelable() - 是否可取消")
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextWindow", sb.toString())
    }

    private fun showProgressDialog() {
        sb.clear()
        sb.appendLine("=== ProgressDialog ===\n")
        
        // ProgressDialog 已被废弃，但仍可使用
        @Suppress("DEPRECATION")
        val progressDialog = ProgressDialog(this).apply {
            setMessage("加载中...")
            setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            setMax(100)
            setProgress(30)
            setCancelable(true)
        }
        progressDialog.show()
        
        // 模拟进度更新
        Thread {
            for (i in 30..100 step 10) {
                Thread.sleep(300)
                runOnUiThread {
                    progressDialog.progress = i
                }
            }
            runOnUiThread {
                progressDialog.dismiss()
                Toast.makeText(this, "加载完成", Toast.LENGTH_SHORT).show()
            }
        }.start()
        
        sb.appendLine("ProgressDialog 已废弃")
        sb.appendLine("推荐替代方案:")
        sb.appendLine("  - 使用 ProgressBar + AlertDialog")
        sb.appendLine("  - 使用 Material Design 组件")
        sb.appendLine("  - 使用自定义 Dialog")
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextWindow", sb.toString())
    }

    private fun showCustomDialog() {
        sb.clear()
        sb.appendLine("=== 自定义 Dialog ===\n")
        
        // 自定义布局
        val view = layoutInflater.inflate(R.layout.dialog_custom, null)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
        val btnPositive = view.findViewById<Button>(R.id.btnPositive)
        
        tvTitle.text = "自定义 Dialog"
        tvMessage.text = "这是自定义布局的 Dialog"
        
        val dialog = Dialog(this).apply {
            setContentView(view)
            // 设置宽高
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
        
        btnPositive.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
        
        sb.appendLine("自定义 Dialog 步骤:")
        sb.appendLine("  1. 创建自定义布局")
        sb.appendLine("  2. 使用 Dialog(this) 创建")
        sb.appendLine("  3. setContentView(view)")
        sb.appendLine("  4. 设置 Window 参数")
        sb.appendLine("  5. show() 显示")
        sb.appendLine()
        sb.appendLine("也可以继承 Dialog 或 DialogFragment")
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextWindow", sb.toString())
    }
    
    private fun showDatePickerDialog() {
        sb.clear()
        sb.appendLine("=== DatePickerDialog ===\n")
        
        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH)
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        
        android.app.DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                sb.appendLine("选择的日期: ${selectedYear}年${selectedMonth + 1}月${selectedDay}日")
                binding.tvResult.text = sb.toString()
            },
            year,
            month,
            day
        ).show()
        
        sb.appendLine("DatePickerDialog 使用系统日历选择器")
        sb.appendLine("同样有 TimePickerDialog")
        
        binding.tvResult.text = sb.toString()
    }

    private fun addFloatingWindow() {
        sb.clear()
        sb.appendLine("=== 添加悬浮窗 ===\n")
        
        // 检查权限
        if (!hasOverlayPermission()) {
            sb.appendLine("没有悬浮窗权限")
            sb.appendLine("正在请求权限...")
            requestOverlayPermission()
            binding.tvResult.text = sb.toString()
            return
        }
        
        // 创建悬浮视图
        if (floatingView != null) {
            sb.appendLine("悬浮窗已存在")
            binding.tvResult.text = sb.toString()
            return
        }
        
        floatingView = layoutInflater.inflate(R.layout.floating_window, null)
        val tvFloating = floatingView?.findViewById<TextView>(R.id.tvFloating)
        tvFloating?.text = "悬浮窗\n点击关闭"
        
        // 设置 WindowManager 参数
        val params = WindowManager.LayoutParams(
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
        
        // 获取 WindowManager
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(floatingView, params)
        
        // 点击关闭
        floatingView?.setOnClickListener {
            removeFloatingWindow()
        }
        
        sb.appendLine("悬浮窗添加成功")
        sb.appendLine("位置: (${params.x}, ${params.y})")
        sb.appendLine("类型: TYPE_APPLICATION_OVERLAY")
        sb.appendLine("点击悬浮窗可关闭")
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextWindow", sb.toString())
    }

    /**
     * 检查是否有悬浮窗权限
     */
    private fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true // Android 6.0 以下默认有权限
        }
    }

    /**
     * 请求悬浮窗权限
     * 注意：悬浮窗权限无法通过 requestPermissions 申请
     * 必须引导用户到系统设置页面手动开启
     */
    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 显示说明对话框
            AlertDialog.Builder(this)
                .setTitle("需要悬浮窗权限")
                .setMessage("显示悬浮窗需要您手动开启权限。\n\n请在设置页面中找到本应用，开启\"显示在其他应用上层\"权限。")
                .setPositiveButton("去设置") { _, _ ->
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    @Suppress("DEPRECATION")
                    startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION)
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
            sb.clear()
            sb.appendLine("=== 权限申请结果 ===\n")
            
            if (hasOverlayPermission()) {
                sb.appendLine("悬浮窗权限已获取!")
                sb.appendLine("可以点击\"添加悬浮窗\"按钮")
            } else {
                sb.appendLine("悬浮窗权限被拒绝")
                sb.appendLine("如需使用悬浮窗功能，请手动到设置中开启")
            }
            
            binding.tvResult.text = sb.toString()
            Log.d("ContextWindow", sb.toString())
        }
    }

    private fun removeFloatingWindow() {
        sb.clear()
        sb.appendLine("=== 移除悬浮窗 ===\n")
        
        if (floatingView == null) {
            sb.appendLine("悬浮窗不存在")
            binding.tvResult.text = sb.toString()
            return
        }
        
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.removeView(floatingView)
        floatingView = null
        
        sb.appendLine("悬浮窗已移除")
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextWindow", sb.toString())
    }

    private fun showDialogWithApplicationContext() {
        sb.clear()
        sb.appendLine("=== 错误示例: Application Context 创建 Dialog ===\n")
        
        try {
            // 使用 Application Context 创建 Dialog - 会抛出异常
            AlertDialog.Builder(applicationContext)
                .setTitle("标题")
                .setMessage("使用 Application Context 创建")
                .show()
            
            sb.appendLine("创建成功 (不应该执行到这里)")
        } catch (e: Exception) {
            sb.appendLine("异常捕获!")
            sb.appendLine("类型: ${e.javaClass.simpleName}")
            sb.appendLine("消息: ${e.message}")
            sb.appendLine()
            sb.appendLine("原因分析:")
            sb.appendLine("  Application Context 没有 Window")
            sb.appendLine("  Dialog 需要依附于 Window")
            sb.appendLine("  只有 Activity 拥有 Window")
            sb.appendLine()
            sb.appendLine("解决方案:")
            sb.appendLine("  1. 使用 Activity Context")
            sb.appendLine("  2. 使用 TYPE_SYSTEM_ALERT (需要权限)")
        }
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextWindow", sb.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        // 确保移除悬浮窗
        if (floatingView != null) {
            val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.removeView(floatingView)
            floatingView = null
        }
    }
}
