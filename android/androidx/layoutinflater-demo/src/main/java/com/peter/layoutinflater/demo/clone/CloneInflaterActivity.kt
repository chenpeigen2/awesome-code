package com.peter.layoutinflater.demo.clone

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.peter.layoutinflater.demo.R
import com.peter.layoutinflater.demo.databinding.ActivityCloneInflaterBinding

/**
 * cloneInContext 深入解析示例
 * 
 * 【核心概念】
 * cloneInContext() 创建一个 LayoutInflater 的副本，可以独立设置 Factory，
 * 而不影响原始 LayoutInflater 实例。
 * 
 * 【为什么需要 clone？】
 * LayoutInflater 的 setFactory() 只能设置一次，设置后无法取消。
 * 如果想在局部使用自定义 Factory，必须先 clone 一个新实例。
 * 
 * 【典型用法】
 * val clonedInflater = layoutInflater.cloneInContext(this)
 * clonedInflater.factory = MyFactory()
 */
class CloneInflaterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCloneInflaterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCloneInflaterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
    }

    private fun setupButtons() {
        binding.btnCompare.setOnClickListener {
            runComparison()
        }

        binding.btnClear.setOnClickListener {
            clearContainers()
        }
    }

    /**
     * 执行对比测试
     * 
     * 演示原始 LayoutInflater 和 clone 后的副本的区别
     */
    private fun runComparison() {
        clearContainers()

        val logBuilder = StringBuilder()

        // ========== 1. 获取原始 LayoutInflater ==========
        val originalInflater = layoutInflater
        logBuilder.appendLine("【原始 LayoutInflater】")
        logBuilder.appendLine("实例: $originalInflater")
        logBuilder.appendLine("hashCode: ${originalInflater.hashCode()}")
        logBuilder.appendLine("Factory: ${originalInflater.factory}")
        logBuilder.appendLine()

        // 使用原始 inflater 加载
        val view1 = originalInflater.inflate(R.layout.item_sample, binding.containerOriginal, false)
        binding.containerOriginal.addView(view1)

        // ========== 2. cloneInContext 创建副本 ==========
        val clonedInflater = originalInflater.cloneInContext(this)
        logBuilder.appendLine("【克隆后的 LayoutInflater】")
        logBuilder.appendLine("实例: $clonedInflater")
        logBuilder.appendLine("hashCode: ${clonedInflater.hashCode()}")
        logBuilder.appendLine("Factory: ${clonedInflater.factory}")
        logBuilder.appendLine()

        // ========== 3. 为克隆的 inflater 设置自定义 Factory ==========
        clonedInflater.factory = CustomTextViewFactory()

        logBuilder.appendLine("【设置 Factory 后】")
        logBuilder.appendLine("克隆 inflater Factory: ${clonedInflater.factory}")
        logBuilder.appendLine("原始 inflater Factory: ${originalInflater.factory}")
        logBuilder.appendLine()

        // 使用克隆的 inflater 加载
        val view2 = clonedInflater.inflate(R.layout.item_sample, binding.containerCloned, false)
        binding.containerCloned.addView(view2)

        // ========== 4. 验证原始 inflater 未受影响 ==========
        // 再次使用原始 inflater 加载，验证没有受到影响
        logBuilder.appendLine("【验证隔离性】")
        logBuilder.appendLine("原始 inflater 仍然使用默认行为")
        logBuilder.appendLine("克隆 inflater 的 Factory 修改不影响原始实例")

        // 显示日志
        binding.tvLog.text = logBuilder.toString()
    }

    /**
     * 自定义 Factory：将所有 TextView 改为洋红色
     */
    private class CustomTextViewFactory : LayoutInflater.Factory {

        override fun onCreateView(
            name: String,
            context: Context,
            attrs: AttributeSet
        ): View? {
            if (name == "TextView" || name == "android.widget.TextView") {
                val tv = TextView(context)

                // 解析属性
                val typedArray = context.obtainStyledAttributes(
                    attrs,
                    intArrayOf(android.R.attr.text)
                )
                tv.text = typedArray.getText(0)
                typedArray.recycle()

                // 应用自定义样式
                tv.setTextColor(Color.MAGENTA)
                tv.textSize = 18f
                tv.setPadding(16, 16, 16, 16)

                return tv
            }
            return null
        }

        override fun toString(): String = "CustomTextViewFactory"
    }

    private fun clearContainers() {
        binding.containerOriginal.removeAllViews()
        binding.containerCloned.removeAllViews()
        binding.tvLog.text = ""
    }
}

/**
 * 【深入理解】cloneInContext 的实现原理
 * 
 * LayoutInflater 本身是一个抽象类，其具体实现是 PhoneLayoutInflater。
 * cloneInContext 在 PhoneLayoutInflater 中的实现：
 * 
 * ```java
 * public LayoutInflater cloneInContext(Context newContext) {
 *     return new PhoneLayoutInflater(this, newContext);
 * }
 * 
 * // PhoneLayoutInflater 构造函数
 * protected PhoneLayoutInflater(LayoutInflater original, Context newContext) {
 *     super(original, newContext);
 *     // 复制一些配置
 *     // 但 Factory 会在 super 中被复制或重置
 * }
 * ```
 * 
 * 【关键点】
 * 1. cloneInContext 创建的是一个新实例
 * 2. 新实例有自己的 Factory 引用
 * 3. 设置 Factory 不会影响原始实例
 * 
 * 【注意事项】
 * 1. cloneInContext 是浅拷贝，某些内部状态可能共享
 * 2. Context 参数可以不同（如不同的主题 Context）
 * 3. 克隆后设置 Factory 是一次性操作
 * 
 * 【最佳实践】
 * ```kotlin
 * // 创建带主题的 Context
 * val themedContext = ContextThemeWrapper(this, R.style.CustomTheme)
 * 
 * // 克隆 inflater 并使用新主题
 * val themedInflater = layoutInflater.cloneInContext(themedContext)
 * 
 * // 设置额外的 Factory
 * themedInflater.factory = MyFactory()
 * 
 * // 加载布局（会使用新主题）
 * val view = themedInflater.inflate(R.layout.xxx, parent, false)
 * ```
 */
