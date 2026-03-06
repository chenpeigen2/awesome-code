package com.peter.window.demo.basic

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.peter.window.demo.databinding.ActivityWindowBasicBinding

/**
 * 基础篇：Window 基础概念
 * 
 * 本Activity讲解Window的核心概念：
 * 
 * 1. Window是什么
 * 2. Window的类型
 * 3. Window的内部结构
 * 4. PhoneWindow的实现
 * 
 * Window是一个抽象类，定义在android.view包中：
 * 
 * ```java
 * public abstract class Window {
 *     // Callback接口，用于处理输入事件
 *     public interface Callback {
 *         boolean dispatchKeyEvent(KeyEvent event);
 *         boolean dispatchTouchEvent(MotionEvent event);
 *         // ...
 *     }
 *     
 *     // 获取DecorView
 *     public abstract View getDecorView();
 *     
 *     // 设置内容视图
 *     public abstract void setContentView(int layoutResID);
 *     public abstract void setContentView(View view);
 *     
 *     // 获取WindowManager
 *     public WindowManager getWindowManager();
 * }
 * ```
 * 
 * PhoneWindow是Window的唯一实现类：
 * 
 * ```java
 * public class PhoneWindow extends Window {
 *     // DecorView是Window的根视图
 *     private DecorView mDecor;
 *     
 *     @Override
 *     public void setContentView(int layoutResID) {
 *         // 安装DecorView
 *         if (mContentParent == null) {
 *             installDecor();
 *         }
 *         // 将布局添加到mContentParent
 *         mLayoutInflater.inflate(layoutResID, mContentParent);
 *     }
 * }
 * ```
 */
class WindowBasicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWindowBasicBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWindowBasicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        showWindowConcept()
    }

    private fun showWindowConcept() {
        val sb = StringBuilder()
        
        sb.appendLine("=== 1. Window 是什么 ===\n")
        sb.appendLine("Window 是 Android 中的一个抽象概念，")
        sb.appendLine("代表一个用于显示内容的窗口。\n")
        sb.appendLine("Window 的特点：")
        sb.appendLine("  • 它是一个抽象类，定义在 android.view 包")
        sb.appendLine("  • 唯一实现类是 PhoneWindow")
        sb.appendLine("  • 每个 Activity 都有一个 Window")
        sb.appendLine("  • Dialog、PopupWindow 等也使用 Window\n")
        
        sb.appendLine("=== 2. Window 类定义 ===\n")
        sb.appendLine("public abstract class Window {")
        sb.appendLine("    // 获取DecorView")
        sb.appendLine("    public abstract View getDecorView();")
        sb.appendLine("    ")
        sb.appendLine("    // 设置内容视图")
        sb.appendLine("    public abstract void setContentView(View);")
        sb.appendLine("    ")
        sb.appendLine("    // 获取WindowManager")
        sb.appendLine("    public WindowManager getWindowManager();")
        sb.appendLine("    ")
        sb.appendLine("    // Callback接口")
        sb.appendLine("    public interface Callback {")
        sb.appendLine("        boolean dispatchKeyEvent(KeyEvent);")
        sb.appendLine("        boolean dispatchTouchEvent(MotionEvent);")
        sb.appendLine("    }")
        sb.appendLine("}\n")
        
        binding.tvInfo.text = sb.toString()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
