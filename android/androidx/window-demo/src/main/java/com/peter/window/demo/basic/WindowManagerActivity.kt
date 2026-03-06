package com.peter.window.demo.basic

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.peter.window.demo.databinding.ActivityWindowManagerBinding

/**
 * 基础篇：WindowManager 详解
 * 
 * WindowManager 是管理 Window 的接口，继承自 ViewManager：
 * 
 * ```java
 * public interface ViewManager {
 *     void addView(View view, ViewGroup.LayoutParams params);
 *     void updateViewLayout(View view, ViewGroup.LayoutParams params);
 *     void removeView(View view);
 * }
 * 
 * public interface WindowManager extends ViewManager {
 *     Display getDefaultDisplay();
 *     void removeViewImmediate(View view);
 * }
 * ```
 * 
 * WindowManager 的实现类是 WindowManagerImpl：
 * 
 * ```java
 * public final class WindowManagerImpl implements WindowManager {
 *     private final WindowManagerGlobal mGlobal = WindowManagerGlobal.getInstance();
 *     
 *     @Override
 *     public void addView(View view, ViewGroup.LayoutParams params) {
 *         mGlobal.addView(view, params, mContext.getDisplay(), mParentWindow);
 *     }
 * }
 * ```
 * 
 * WindowManager.LayoutParams 是窗口的布局参数：
 * 
 * ```java
 * public static class LayoutParams extends ViewGroup.LayoutParams {
 *     public int type;          // 窗口类型
 *     public int flags;         // 窗口标志
 *     public int softInputMode; // 软键盘模式
 *     public int gravity;       // 重力
 *     public float dimAmount;   // 暗淡程度
 *     public float alpha;       // 透明度
 * }
 * ```
 */
class WindowManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWindowManagerBinding
    private var addedView: View? = null
    private var addedCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWindowManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showWindowManagerInfo()
    }

    private fun setupListeners() {
        binding.btnAddView.setOnClickListener { addViewToWindow() }
        binding.btnUpdateView.setOnClickListener { updateViewLayout() }
        binding.btnRemoveView.setOnClickListener { removeViewFromWindow() }
        binding.btnShowParams.setOnClickListener { showLayoutParamsDemo() }
    }

    private fun showWindowManagerInfo() {
        val sb = StringBuilder()
        
        sb.appendLine("=== WindowManager 概述 ===\n")
        
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        sb.appendLine("WindowManager: $windowManager\n")
        
        sb.appendLine("=== WindowManager 接口定义 ===\n")
        sb.appendLine("public interface WindowManager extends ViewManager {")
        sb.appendLine("    // 继承自ViewManager的方法:")
        sb.appendLine("    void addView(View, LayoutParams);")
        sb.appendLine("    void updateViewLayout(View, LayoutParams);")
        sb.appendLine("    void removeView(View);")
        sb.appendLine("    ")
        sb.appendLine("    // WindowManager自己的方法:")
        sb.appendLine("    Display getDefaultDisplay();")
        sb.appendLine("    void removeViewImmediate(View);")
        sb.appendLine("}\n")
        
        sb.appendLine("=== WindowManagerImpl 实现 ===\n")
        sb.appendLine("public final class WindowManagerImpl {")
        sb.appendLine("    // 委托给 WindowManagerGlobal")
        sb.appendLine("    private final WindowManagerGlobal mGlobal;")
        sb.appendLine("    ")
        sb.appendLine("    void addView(View view, LayoutParams params) {")
        sb.appendLine("        mGlobal.addView(view, params, ...);")
        sb.appendLine("    }")
        sb.appendLine("}\n")
        
        binding.tvInfo.text = sb.toString()
    }

    /**
     * 演示：使用 WindowManager 添加视图
     */
    private fun addViewToWindow() {
        if (addedView != null) {
            Toast.makeText(this, "视图已存在，请先移除", Toast.LENGTH_SHORT).show()
            return
        }

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        // 创建要添加的视图
        val textView = TextView(this).apply {
            text = "WindowManager添加的视图\n点击移除"
            setPadding(24, 24, 24, 24)
            setBackgroundColor(0xE06200EE.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 16f
            setOnClickListener { removeViewFromWindow() }
        }
        
        // 创建 LayoutParams
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_PANEL, // 应用内面板类型
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }
        
        // 添加视图
        windowManager.addView(textView, params)
        addedView = textView
        addedCount++
        
        val sb = StringBuilder()
        sb.appendLine("=== 添加视图成功 ===\n")
        sb.appendLine("视图类型: TYPE_APPLICATION_PANEL")
        sb.appendLine("位置: 屏幕中央")
        sb.appendLine("标志: FLAG_NOT_FOCUSABLE\n")
        sb.appendLine("点击视图可移除")
        
        binding.tvResult.text = sb.toString()
    }

    /**
     * 演示：更新视图布局
     */
    private fun updateViewLayout() {
        if (addedView == null) {
            Toast.makeText(this, "请先添加视图", Toast.LENGTH_SHORT).show()
            return
        }

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        // 创建新的 LayoutParams（随机位置）
        val random = java.util.Random()
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = random.nextInt(metrics.widthPixels - 400)
            y = random.nextInt(metrics.heightPixels - 400)
        }
        
        // 更新布局
        windowManager.updateViewLayout(addedView, params)
        
        val sb = StringBuilder()
        sb.appendLine("=== 更新布局成功 ===\n")
        sb.appendLine("新位置: (${params.x}, ${params.y})")
        
        binding.tvResult.text = sb.toString()
    }

    /**
     * 演示：移除视图
     */
    private fun removeViewFromWindow() {
        if (addedView == null) {
            return
        }

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.removeView(addedView)
        addedView = null
        
        binding.tvResult.text = "视图已移除"
    }

    /**
     * 演示：WindowManager.LayoutParams 常用属性
     */
    private fun showLayoutParamsDemo() {
        val sb = StringBuilder()
        
        sb.appendLine("=== LayoutParams 常用属性 ===\n")
        
        sb.appendLine("【type - 窗口类型】")
        sb.appendLine("TYPE_APPLICATION = 2")
        sb.appendLine("  普通应用窗口\n")
        sb.appendLine("TYPE_APPLICATION_PANEL = 1000")
        sb.appendLine("  应用面板窗口（依附于Activity）\n")
        sb.appendLine("TYPE_APPLICATION_ATTACHED_DIALOG = 1003")
        sb.appendLine("  依附于Activity的对话框\n")
        sb.appendLine("TYPE_PHONE = 2002 (已废弃)")
        sb.appendLine("  电话窗口（需权限）\n")
        sb.appendLine("TYPE_SYSTEM_ALERT = 2003 (已废弃)")
        sb.appendLine("  系统警告窗口（需权限）\n")
        sb.appendLine("TYPE_APPLICATION_OVERLAY = 2038")
        sb.appendLine("  系统覆盖窗口（Android 8.0+）\n")
        
        sb.appendLine("【flags - 窗口标志】")
        sb.appendLine("FLAG_NOT_FOCUSABLE = 0x00000008")
        sb.appendLine("  窗口不获取焦点\n")
        sb.appendLine("FLAG_NOT_TOUCHABLE = 0x00000010")
        sb.appendLine("  窗口不接收触摸事件\n")
        sb.appendLine("FLAG_NOT_TOUCH_MODAL = 0x00000020")
        sb.appendLine("  窗口外的触摸事件可以穿透\n")
        sb.appendLine("FLAG_LAYOUT_IN_SCREEN = 0x00000100")
        sb.appendLine("  允许窗口延伸到屏幕边缘\n")
        sb.appendLine("FLAG_LAYOUT_NO_LIMITS = 0x00000200")
        sb.appendLine("  允许窗口超出屏幕边界\n")
        sb.appendLine("FLAG_FULLSCREEN = 0x00000400")
        sb.appendLine("  全屏模式\n")
        sb.appendLine("FLAG_KEEP_SCREEN_ON = 0x00000080")
        sb.appendLine("  保持屏幕常亮\n")
        sb.appendLine("FLAG_WATCH_OUTSIDE_TOUCH = 0x00040000")
        sb.appendLine("  监听窗口外的触摸事件\n")
        
        sb.appendLine("【softInputMode - 软键盘模式】")
        sb.appendLine("SOFT_INPUT_STATE_UNSPECIFIED")
        sb.appendLine("SOFT_INPUT_STATE_UNCHANGED")
        sb.appendLine("SOFT_INPUT_STATE_HIDDEN")
        sb.appendLine("SOFT_INPUT_STATE_ALWAYS_HIDDEN")
        sb.appendLine("SOFT_INPUT_ADJUST_RESIZE")
        sb.appendLine("SOFT_INPUT_ADJUST_PAN\n")
        
        sb.appendLine("【其他属性】")
        sb.appendLine("gravity - 重力")
        sb.appendLine("x, y - 位置坐标")
        sb.appendLine("width, height - 宽高")
        sb.appendLine("alpha - 透明度 (0-1)")
        sb.appendLine("dimAmount - 暗淡程度 (0-1)")
        sb.appendLine("format - 像素格式")
        sb.appendLine("token - 窗口令牌")
        
        binding.tvResult.text = sb.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 确保移除添加的视图
        removeViewFromWindow()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
