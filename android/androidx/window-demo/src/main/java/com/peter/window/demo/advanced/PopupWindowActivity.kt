package com.peter.window.demo.advanced

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.peter.window.demo.R
import com.peter.window.demo.databinding.ActivityPopupWindowBinding

/**
 * 进阶篇：PopupWindow 详解
 * 
 * PopupWindow 是一种弹出窗口，与 Dialog 不同：
 * 
 * 1. PopupWindow 依附于某个 View
 * 2. PopupWindow 可以指定显示位置
 * 3. PopupWindow 更轻量级
 * 
 * PopupWindow 的核心实现：
 * 
 * ```java
 * public class PopupWindow {
 *     private int mWidth;           // 宽度
 *     private int mHeight;          // 高度
 *     private View mContentView;    // 内容视图
 *     private WindowManager mWindowManager;
 *     
 *     public void showAsDropDown(View anchor) {
 *         // 在anchor下方显示
 *     }
 *     
 *     public void showAtLocation(View parent, int gravity, int x, int y) {
 *         // 在指定位置显示
 *     }
 *     
 *     public void dismiss() {
 *         // 移除窗口
 *     }
 * }
 * ```
 * 
 * PopupWindow 与 Dialog 的区别：
 * 
 * | 特性          | PopupWindow    | Dialog        |
 * |---------------|----------------|---------------|
 * | 依附对象      | View           | Activity      |
 * | 位置          | 可任意         | 屏幕中央      |
 * | 焦点          | 可选           | 有焦点        |
 * | 模态          | 可选           | 模态          |
 * | 背景变暗      | 需手动实现     | 自动          |
 * | 适用场景      | 下拉菜单、提示 | 对话框        |
 */
class PopupWindowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPopupWindowBinding
    private var popupWindow: PopupWindow? = null
    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPopupWindowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showPopupWindowInfo()
    }

    private fun setupListeners() {
        binding.btnShowAsDropDown.setOnClickListener { showAsDropDown() }
        binding.btnShowAtLocation.setOnClickListener { showAtLocation() }
        binding.btnDismiss.setOnClickListener { dismissPopupWindow() }
        binding.btnListPopup.setOnClickListener { showListPopup() }
        binding.btnMenuPopup.setOnClickListener { showMenuPopup() }
        binding.btnCustomPopup.setOnClickListener { showCustomPopup() }
        binding.btnFocusablePopup.setOnClickListener { showFocusablePopup() }
    }

    private fun showPopupWindowInfo() {
        sb.clear()
        sb.appendLine("=== PopupWindow 概述 ===\n")
        sb.appendLine("PopupWindow 是轻量级弹出窗口\n")
        sb.appendLine("与 Dialog 的区别：")
        sb.appendLine("  • 依附于 View 而非 Activity")
        sb.appendLine("  • 可以指定任意位置")
        sb.appendLine("  • 默认没有焦点")
        sb.appendLine("  • 更轻量级\n")
        sb.appendLine("常用方法：")
        sb.appendLine("  showAsDropDown(View anchor)")
        sb.appendLine("  showAtLocation(View parent, gravity, x, y)")
        sb.appendLine("  dismiss()")
        sb.appendLine("  setFocusable(boolean)")
        sb.appendLine("  setOutsideTouchable(boolean)")
        
        binding.tvInfo.text = sb.toString()
    }

    /**
     * 基本 PopupWindow - showAsDropDown
     */
    private fun showAsDropDown() {
        sb.clear()
        sb.appendLine("=== showAsDropDown ===\n")
        
        // 创建内容视图
        val contentView = TextView(this).apply {
            text = "这是一个 PopupWindow\n点击外部关闭"
            setPadding(32, 32, 32, 32)
            setBackgroundColor(Color.parseColor("#FF6200EE"))
            setTextColor(Color.WHITE)
            textSize = 16f
        }
        
        // 创建 PopupWindow
        popupWindow = PopupWindow(
            contentView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            // 设置点击外部可关闭
            isOutsideTouchable = true
            // 必须设置背景才能响应外部点击
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // 在按钮下方显示
            showAsDropDown(binding.btnShowAsDropDown)
        }
        
        sb.appendLine("PopupWindow 显示在按钮下方")
        sb.appendLine("isOutsideTouchable = true")
        sb.appendLine("点击外部可关闭")
        
        binding.tvResult.text = sb.toString()
    }

    /**
     * showAtLocation - 在指定位置显示
     */
    private fun showAtLocation() {
        sb.clear()
        sb.appendLine("=== showAtLocation ===\n")
        
        val contentView = TextView(this).apply {
            text = "居中显示的 PopupWindow"
            setPadding(48, 48, 48, 48)
            setBackgroundColor(Color.parseColor("#FF03DAC5"))
            setTextColor(Color.WHITE)
            textSize = 18f
        }
        
        popupWindow = PopupWindow(
            contentView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // 在屏幕中央显示
            showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        }
        
        sb.appendLine("使用 showAtLocation 指定位置")
        sb.appendLine("参数：parent, Gravity.CENTER, x, y")
        
        binding.tvResult.text = sb.toString()
    }

    /**
     * 关闭 PopupWindow
     */
    private fun dismissPopupWindow() {
        popupWindow?.dismiss()
        popupWindow = null
        binding.tvResult.text = "PopupWindow 已关闭"
    }

    /**
     * 列表 PopupWindow
     */
    private fun showListPopup() {
        sb.clear()
        sb.appendLine("=== 列表 PopupWindow ===\n")
        
        // 创建 ListView
        val listView = ListView(this).apply {
            adapter = ArrayAdapter(
                this@PopupWindowActivity,
                android.R.layout.simple_list_item_1,
                arrayOf("选项 1", "选项 2", "选项 3", "选项 4", "选项 5")
            )
            setOnItemClickListener { _, _, position, _ ->
                sb.appendLine("选择了: 选项 ${position + 1}")
                binding.tvResult.text = sb.toString()
                dismissPopupWindow()
            }
        }
        
        popupWindow = PopupWindow(
            listView,
            300,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable(Color.WHITE))
            showAsDropDown(binding.btnListPopup)
        }
        
        sb.appendLine("使用 ListView 作为内容")
        sb.appendLine("宽度: 300px")
        
        binding.tvResult.text = sb.toString()
    }

    /**
     * 菜单样式 PopupWindow
     */
    private fun showMenuPopup() {
        sb.clear()
        sb.appendLine("=== 菜单样式 PopupWindow ===\n")
        
        val menuItems = listOf("编辑", "删除", "分享", "更多")
        
        // 创建菜单布局
        val contentView = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(0, 8, 0, 8)
            
            menuItems.forEach { text ->
                addView(TextView(context).apply {
                    this.text = text
                    setPadding(48, 24, 48, 24)
                    textSize = 16f
                    setOnClickListener {
                        sb.appendLine("点击了: $text")
                        binding.tvResult.text = sb.toString()
                        dismissPopupWindow()
                    }
                })
            }
        }
        
        popupWindow = PopupWindow(
            contentView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable(Color.WHITE))
            elevation = 8f
            showAsDropDown(binding.btnMenuPopup)
        }
        
        sb.appendLine("自定义菜单样式")
        sb.appendLine("elevation = 8f")
        
        binding.tvResult.text = sb.toString()
    }

    /**
     * 自定义布局 PopupWindow
     */
    private fun showCustomPopup() {
        sb.clear()
        sb.appendLine("=== 自定义布局 PopupWindow ===\n")
        
        val contentView = layoutInflater.inflate(R.layout.popup_custom, null)
        
        contentView.findViewById<Button>(R.id.btnAction1).setOnClickListener {
            sb.appendLine("执行操作 1")
            binding.tvResult.text = sb.toString()
        }
        
        contentView.findViewById<Button>(R.id.btnAction2).setOnClickListener {
            sb.appendLine("执行操作 2")
            binding.tvResult.text = sb.toString()
            dismissPopupWindow()
        }
        
        popupWindow = PopupWindow(
            contentView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            showAsDropDown(binding.btnCustomPopup)
        }
        
        sb.appendLine("使用自定义布局")
        
        binding.tvResult.text = sb.toString()
    }

    /**
     * 可获取焦点的 PopupWindow
     */
    private fun showFocusablePopup() {
        sb.clear()
        sb.appendLine("=== 可获取焦点的 PopupWindow ===\n")
        
        val contentView = layoutInflater.inflate(R.layout.popup_editable, null)
        val editText = contentView.findViewById<android.widget.EditText>(R.id.editText)
        
        popupWindow = PopupWindow(
            contentView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            // 设置可获取焦点，这样 EditText 可以输入
            isFocusable = true
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable(Color.WHITE))
            // 设置软键盘模式
            softInputMode = android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
            showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        }
        
        sb.appendLine("isFocusable = true")
        sb.appendLine("EditText 可以输入")
        sb.appendLine("softInputMode 设置软键盘显示")
        
        binding.tvResult.text = sb.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissPopupWindow()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
