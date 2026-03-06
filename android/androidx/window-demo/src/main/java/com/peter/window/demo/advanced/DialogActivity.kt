package com.peter.window.demo.advanced

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.peter.window.demo.R
import com.peter.window.demo.databinding.ActivityDialogBinding
import java.util.Calendar

/**
 * 进阶篇：Dialog 详解
 * 
 * Dialog 是Android中常用的窗口组件，它依附于Activity的Window。
 * 
 * Dialog的核心实现：
 * 
 * ```java
 * public class Dialog implements DialogInterface, Window.Callback, KeyEvent.Callback {
 *     private Window mWindow;           // Dialog拥有的Window
 *     private WindowManager mWindowManager;
 *     
 *     Dialog(Context context) {
 *         // 创建Window
 *         mWindow = new PhoneWindow(context);
 *         mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
 *     }
 *     
 *     public void show() {
 *         // 将DecorView添加到WindowManager
 *         mWindowManager.addView(mDecor, mWindow.getAttributes());
 *     }
 *     
 *     public void dismiss() {
 *         // 从WindowManager移除DecorView
 *         mWindowManager.removeView(mDecor);
 *     }
 * }
 * ```
 * 
 * 重要知识点：
 * 1. Dialog必须使用Activity Context创建（不能用Application Context）
 * 2. Dialog有自己的Window和DecorView
 * 3. Dialog显示时，将DecorView添加到WindowManager
 * 4. Dialog销毁时，从WindowManager移除DecorView
 */
class DialogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDialogBinding
    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showDialogInfo()
    }

    private fun setupListeners() {
        binding.btnAlertDialog.setOnClickListener { showAlertDialog() }
        binding.btnListDialog.setOnClickListener { showListDialog() }
        binding.btnMultiChoiceDialog.setOnClickListener { showMultiChoiceDialog() }
        binding.btnSingleChoiceDialog.setOnClickListener { showSingleChoiceDialog() }
        binding.btnCustomDialog.setOnClickListener { showCustomDialog() }
        binding.btnDatePickerDialog.setOnClickListener { showDatePickerDialog() }
        binding.btnTimePickerDialog.setOnClickListener { showTimePickerDialog() }
        binding.btnProgressDialog.setOnClickListener { showProgressDialog() }
        binding.btnDialogFragment.setOnClickListener { showDialogFragment() }
        binding.btnErrorAppContext.setOnClickListener { showDialogWithAppContext() }
    }

    private fun showDialogInfo() {
        sb.clear()
        sb.appendLine("=== Dialog 概述 ===\n")
        sb.appendLine("Dialog 是一个窗口组件，依附于Activity的Window。\n")
        sb.appendLine("Dialog 的特点：")
        sb.appendLine("  • 有自己的 Window 和 DecorView")
        sb.appendLine("  • 必须使用 Activity Context 创建")
        sb.appendLine("  • 显示时添加到 WindowManager")
        sb.appendLine("  • 销毁时从 WindowManager 移除\n")
        sb.appendLine("Dialog 的实现类：")
        sb.appendLine("  • AlertDialog - 警告对话框")
        sb.appendLine("  • ProgressDialog - 进度对话框")
        sb.appendLine("  • DatePickerDialog - 日期选择")
        sb.appendLine("  • TimePickerDialog - 时间选择")
        sb.appendLine("  • DialogFragment - 推荐使用方式\n")
        sb.appendLine("点击下方按钮查看各种Dialog示例")
        
        binding.tvInfo.text = sb.toString()
    }

    /**
     * 基本 AlertDialog
     */
    private fun showAlertDialog() {
        sb.clear()
        sb.appendLine("=== AlertDialog ===\n")
        
        AlertDialog.Builder(this)
            .setTitle("AlertDialog 标题")
            .setMessage("这是 AlertDialog 的消息内容。\n\nAlertDialog 是最常用的对话框类型。")
            .setIcon(R.mipmap.ic_launcher)
            .setPositiveButton("确定") { dialog, _ ->
                Toast.makeText(this, "点击了确定", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("取消") { dialog, _ ->
                Toast.makeText(this, "点击了取消", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNeutralButton("忽略") { _, _ ->
                Toast.makeText(this, "点击了忽略", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(true)
            .show()
        
        sb.appendLine("AlertDialog.Builder 方法：")
        sb.appendLine("  setTitle() - 设置标题")
        sb.appendLine("  setMessage() - 设置消息")
        sb.appendLine("  setIcon() - 设置图标")
        sb.appendLine("  setPositiveButton() - 确定按钮")
        sb.appendLine("  setNegativeButton() - 取消按钮")
        sb.appendLine("  setNeutralButton() - 中性按钮")
        sb.appendLine("  setCancelable() - 是否可取消")
        
        binding.tvResult.text = sb.toString()
    }

    /**
     * 列表选择 Dialog
     */
    private fun showListDialog() {
        sb.clear()
        sb.appendLine("=== 列表选择 Dialog ===\n")
        
        val items = arrayOf("选项 1", "选项 2", "选项 3", "选项 4", "选项 5")
        
        AlertDialog.Builder(this)
            .setTitle("请选择一项")
            .setItems(items) { dialog, which ->
                sb.appendLine("选择了: ${items[which]}")
                binding.tvResult.text = sb.toString()
                dialog.dismiss()
            }
            .show()
        
        sb.appendLine("setItems() 用于显示列表选项")
    }

    /**
     * 多选 Dialog
     */
    private fun showMultiChoiceDialog() {
        sb.clear()
        sb.appendLine("=== 多选 Dialog ===\n")
        
        val items = arrayOf("苹果", "香蕉", "橙子", "葡萄", "西瓜")
        val checkedItems = booleanArrayOf(false, false, false, false, false)
        
        AlertDialog.Builder(this)
            .setTitle("选择你喜欢的水果（可多选）")
            .setMultiChoiceItems(items, checkedItems) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }
            .setPositiveButton("确定") { dialog, _ ->
                val selected = items.filterIndexed { index, _ -> checkedItems[index] }
                sb.appendLine("选择了: ${selected.joinToString(", ")}")
                binding.tvResult.text = sb.toString()
                dialog.dismiss()
            }
            .setNegativeButton("取消", null)
            .show()
        
        sb.appendLine("setMultiChoiceItems() 用于多选列表")
    }

    /**
     * 单选 Dialog
     */
    private fun showSingleChoiceDialog() {
        sb.clear()
        sb.appendLine("=== 单选 Dialog ===\n")
        
        val items = arrayOf("红色", "绿色", "蓝色", "黄色", "紫色")
        var selectedIndex = -1
        
        AlertDialog.Builder(this)
            .setTitle("选择你喜欢的颜色")
            .setSingleChoiceItems(items, -1) { _, which ->
                selectedIndex = which
            }
            .setPositiveButton("确定") { dialog, _ ->
                if (selectedIndex >= 0) {
                    sb.appendLine("选择了: ${items[selectedIndex]}")
                    binding.tvResult.text = sb.toString()
                }
                dialog.dismiss()
            }
            .setNegativeButton("取消", null)
            .show()
        
        sb.appendLine("setSingleChoiceItems() 用于单选列表")
    }

    /**
     * 自定义布局 Dialog
     */
    private fun showCustomDialog() {
        sb.clear()
        sb.appendLine("=== 自定义布局 Dialog ===\n")
        
        // 创建自定义视图
        val view = layoutInflater.inflate(R.layout.dialog_custom_view, null)
        val editText = view.findViewById<EditText>(R.id.editText)
        val btnConfirm = view.findViewById<Button>(R.id.btnConfirm)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        
        val dialog = Dialog(this).apply {
            setContentView(view)
            // 设置宽高
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
        
        btnConfirm.setOnClickListener {
            val input = editText.text.toString()
            sb.appendLine("输入内容: $input")
            binding.tvResult.text = sb.toString()
            dialog.dismiss()
        }
        
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
        
        sb.appendLine("自定义 Dialog 步骤：")
        sb.appendLine("  1. 创建自定义布局")
        sb.appendLine("  2. Dialog(context).setContentView(view)")
        sb.appendLine("  3. 设置 Window 参数（宽高）")
        sb.appendLine("  4. show() 显示")
    }

    /**
     * 日期选择 Dialog
     */
    private fun showDatePickerDialog() {
        sb.clear()
        sb.appendLine("=== DatePickerDialog ===\n")
        
        val calendar = Calendar.getInstance()
        
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                sb.appendLine("选择的日期: $year 年 ${month + 1} 月 $dayOfMonth 日")
                binding.tvResult.text = sb.toString()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
        
        sb.appendLine("DatePickerDialog 用于选择日期")
    }

    /**
     * 时间选择 Dialog
     */
    private fun showTimePickerDialog() {
        sb.clear()
        sb.appendLine("=== TimePickerDialog ===\n")
        
        val calendar = Calendar.getInstance()
        
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                sb.appendLine("选择的时间: $hourOfDay 时 $minute 分")
                binding.tvResult.text = sb.toString()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
        
        sb.appendLine("TimePickerDialog 用于选择时间")
    }

    /**
     * 进度 Dialog（已废弃，演示使用替代方案）
     */
    @Suppress("DEPRECATION")
    private fun showProgressDialog() {
        sb.clear()
        sb.appendLine("=== ProgressDialog ===\n")
        sb.appendLine("注意：ProgressDialog 已被废弃！")
        sb.appendLine("推荐使用 ProgressBar 或自定义 Dialog\n")
        
        // 仍然可以使用的示例
        val progressDialog = ProgressDialog(this).apply {
            setMessage("加载中...")
            setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            setMax(100)
            setProgress(0)
            setCancelable(false)
        }
        progressDialog.show()
        
        // 模拟进度
        Thread {
            for (i in 0..100 step 5) {
                Thread.sleep(100)
                runOnUiThread {
                    progressDialog.progress = i
                }
            }
            runOnUiThread {
                progressDialog.dismiss()
                Toast.makeText(this, "加载完成", Toast.LENGTH_SHORT).show()
            }
        }.start()
        
        binding.tvResult.text = sb.toString()
    }

    /**
     * DialogFragment 示例
     */
    private fun showDialogFragment() {
        sb.clear()
        sb.appendLine("=== DialogFragment ===\n")
        
        // 显示 DialogFragment
        MyDialogFragment().show(supportFragmentManager, "MyDialogFragment")
        
        sb.appendLine("DialogFragment 是推荐的使用方式：")
        sb.appendLine("  • 处理配置变更（屏幕旋转）")
        sb.appendLine("  • 生命周期管理")
        sb.appendLine("  • 可以作为普通 Fragment 使用")
        sb.appendLine("  • 返回键处理")
        
        binding.tvResult.text = sb.toString()
    }

    /**
     * 错误示例：使用 Application Context 创建 Dialog
     */
    private fun showDialogWithAppContext() {
        sb.clear()
        sb.appendLine("=== 错误示例 ===\n")
        
        try {
            AlertDialog.Builder(applicationContext)
                .setTitle("错误示例")
                .setMessage("使用 Application Context 创建 Dialog")
                .show()
        } catch (e: Exception) {
            sb.appendLine("异常捕获！")
            sb.appendLine("类型: ${e.javaClass.simpleName}")
            sb.appendLine("消息: ${e.message}\n")
            sb.appendLine("原因分析：")
            sb.appendLine("  Application Context 没有 Window")
            sb.appendLine("  Dialog 需要 Activity 的 Window")
            sb.appendLine("  只有 Activity 拥有可用的 Window Token\n")
            sb.appendLine("解决方案：")
            sb.appendLine("  使用 Activity Context (this)")
        }
        
        binding.tvResult.text = sb.toString()
    }

    /**
     * 自定义 DialogFragment
     */
    class MyDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return AlertDialog.Builder(requireActivity())
                .setTitle("DialogFragment")
                .setMessage("这是使用 DialogFragment 创建的对话框。\n\nDialogFragment 是推荐的使用方式，因为它能正确处理配置变更。")
                .setPositiveButton("确定") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
