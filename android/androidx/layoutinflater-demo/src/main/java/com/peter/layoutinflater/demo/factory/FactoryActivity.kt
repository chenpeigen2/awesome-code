package com.peter.layoutinflater.demo.factory

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.peter.layoutinflater.demo.databinding.ActivityFactoryBinding

/**
 * LayoutInflater.Factory 机制示例
 * 
 * 【核心概念】
 * LayoutInflater.Factory 是一个回调接口，在 LayoutInflater 创建 View 时被调用。
 * 你可以通过实现这个接口来拦截 View 的创建过程。
 * 
 * 【Factory 的层次】
 * 1. LayoutInflater.Factory - 基础接口
 * 2. LayoutInflater.Factory2 - 扩展接口，支持父 View 参数
 * 
 * 【设置方式】
 * - LayoutInflater.setFactory()
 * - LayoutInflaterCompat.setFactory() (AndroidX 兼容)
 */
class FactoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFactoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // 【关键步骤】在 super.onCreate 之前设置 Factory
        // 因为 setContentView 会触发 inflate，此时 Factory 已生效
        val inflater = layoutInflater
        inflater.factory = CustomFactory()

        super.onCreate(savedInstanceState)
        binding = ActivityFactoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 动态加载演示
        demonstrateFactory()
    }

    /**
     * 动态演示 Factory 的效果
     */
    private fun demonstrateFactory() {
        // 创建一个使用自定义 Factory 的 LayoutInflater
        val customInflater = LayoutInflater.from(this).cloneInContext(this)
        customInflater.factory = CustomFactory()

        // 动态加载一个包含 TextView 的布局
        val dynamicView = customInflater.inflate(
            android.R.layout.simple_list_item_1,
            binding.container,
            false
        ) as TextView

        dynamicView.text = "这是动态加载的 TextView\n也应用了 Factory 的样式"

        binding.container.addView(dynamicView)
    }

    /**
     * 自定义 Factory 实现
     * 
     * 【功能】
     * - 拦截所有 TextView 的创建
     * - 自动应用统一样式
     * - 记录 View 创建信息
     */
    private inner class CustomFactory : LayoutInflater.Factory {

        override fun onCreateView(
            name: String,
            context: Context,
            attrs: AttributeSet
        ): View? {
            // 【拦截逻辑】只处理 TextView
            // name 是 XML 中的标签名，如 "TextView", "LinearLayout" 等
            if (name == "TextView" || name == "android.widget.TextView") {
                return createEnhancedTextView(context, attrs)
            }

            // 返回 null 表示使用 LayoutInflater 默认的创建逻辑
            return null
        }

        /**
         * 创建增强版 TextView
         */
        private fun createEnhancedTextView(
            context: Context,
            attrs: AttributeSet
        ): TextView {
            // 创建标准的 TextView
            val textView = TextView(context)

            // 应用 XML 中的属性
            // 这里需要手动解析 attrs，或者使用系统方法
            val typedArray = context.obtainStyledAttributes(
                attrs,
                intArrayOf(android.R.attr.text, android.R.attr.textSize)
            )

            val text = typedArray.getText(0)
            val textSize = typedArray.getDimension(1, 14f)

            typedArray.recycle()

            // 设置原始属性
            textView.text = text
            textView.textSize = textSize / resources.displayMetrics.density

            // 【自动增强】添加统一的样式
            textView.setTypeface(null, Typeface.BOLD)
            textView.setTextColor(Color.parseColor("#6200EE"))
            textView.setPadding(16, 16, 16, 16)

            // 【添加功能】例如点击提示
            textView.setOnClickListener {
                Toast.makeText(
                    context,
                    "点击了增强 TextView: ${textView.text}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            return textView
        }
    }
}

/**
 * Factory2 示例（扩展版本）
 * 
 * LayoutInflater.Factory2 是 Factory 的扩展接口，增加了 parent 参数。
 * 这在某些需要根据父 View 做不同处理的场景下很有用。
 * 
 * 【接口定义】
 * interface Factory2 : Factory {
 *     fun onCreateView(
 *         parent: View?,
 *         name: String,
 *         context: Context,
 *         attrs: AttributeSet
 *     ): View?
 * }
 * 
 * 【使用方式】
 * layoutInflater.factory2 = object : LayoutInflater.Factory2 {
 *     override fun onCreateView(
 *         parent: View?,
 *         name: String,
 *         context: Context,
 *         attrs: AttributeSet
 *     ): View? {
 *         // 可以根据 parent 做不同的处理
 *         if (parent is LinearLayout) {
 *             // 特殊处理
 *         }
 *         return null
 *     }
 *     
 *     override fun onCreateView(
 *         name: String,
 *         context: Context,
 *         attrs: AttributeSet
 *     ): View? {
 *         return null
 *     }
 * }
 */
