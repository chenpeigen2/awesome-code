package com.peter.layoutinflater.demo.advanced

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.LayoutInflaterCompat
import com.peter.layoutinflater.demo.databinding.ActivityLayoutInflaterCompatBinding

/**
 * LayoutInflaterCompat 与 Factory2 示例
 * 
 * 【核心概念】
 * LayoutInflaterCompat 是 AndroidX 提供的兼容类，用于安全地设置 Factory2。
 * Factory2 相比 Factory 增加了 parent 参数，可以感知父容器。
 * 
 * 【使用场景】
 * 1. 根据父容器类型做不同的 View 处理
 * 2. 实现更复杂的皮肤切换
 * 3. 添加调试信息
 */
class LayoutInflaterCompatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLayoutInflaterCompatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // 打印 LayoutInflater 信息（设置 Factory2 前）
        printLayoutInflaterInfo("设置 Factory2 前")
        
        // 【关键】在 super.onCreate 之前设置 Factory2
        // 这样 setContentView 时 Factory2 就已生效
        LayoutInflaterCompat.setFactory2(layoutInflater, CustomFactory2())
        
        // 打印 LayoutInflater 信息（设置 Factory2 后）
        printLayoutInflaterInfo("设置 Factory2 后")

        super.onCreate(savedInstanceState)
        binding = ActivityLayoutInflaterCompatBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * 打印当前 Activity 的 LayoutInflater 信息
     */
    private fun printLayoutInflaterInfo(stage: String) {
        val inflater = layoutInflater
        val sb = StringBuilder()
        sb.appendLine("========== LayoutInflaterCompatActivity [$stage] ==========")
        sb.appendLine("LayoutInflater 实例: $inflater")
        sb.appendLine("hashCode: ${inflater.hashCode()}")
        sb.appendLine("Factory: ${inflater.factory}")
        sb.appendLine("Factory2: ${inflater.factory2}")
        sb.appendLine("==========================================================")
        android.util.Log.d("LayoutInflaterDemo", sb.toString())
    }

    /**
     * 自定义 Factory2 实现
     * 
     * 【功能】根据父容器类型为 TextView 应用不同样式
     */
    private inner class CustomFactory2 : LayoutInflater.Factory2 {

        override fun onCreateView(
            parent: View?,
            name: String,
            context: Context,
            attrs: AttributeSet
        ): View? {
            // 只处理 TextView
            if (name == "TextView" || name == "android.widget.TextView") {
                return createStyledTextView(parent, context, attrs)
            }

            // 其他 View 使用默认逻辑
            return null
        }

        override fun onCreateView(
            name: String,
            context: Context,
            attrs: AttributeSet
        ): View? {
            // 这个方法在 Factory2 中一般不需要实现
            // 因为 onCreateView(parent, ...) 会被优先调用
            return null
        }

        /**
         * 根据父容器创建不同样式的 TextView
         */
        private fun createStyledTextView(
            parent: View?,
            context: Context,
            attrs: AttributeSet
        ): TextView {
            // 创建 TextView
            val textView = TextView(context)

            // 解析原始属性
            val typedArray = context.obtainStyledAttributes(
                attrs,
                intArrayOf(android.R.attr.text)
            )
            val text = typedArray.getText(0)
            typedArray.recycle()

            textView.text = text

            // 【核心】根据父容器类型应用不同样式
            when (parent) {
                is LinearLayout -> {
                    val orientation = parent.orientation
                    if (orientation == LinearLayout.VERTICAL) {
                        // 垂直 LinearLayout：紫色粗体
                        textView.setTextColor(Color.parseColor("#6200EE"))
                        textView.setTypeface(null, Typeface.BOLD)
                        textView.textSize = 16f
                        textView.setPadding(8, 8, 8, 8)
                        Log.d("Factory2", "垂直 LinearLayout 中的 TextView")
                    } else {
                        // 水平 LinearLayout：橙色斜体
                        textView.setTextColor(Color.parseColor("#FF9800"))
                        textView.setTypeface(null, Typeface.ITALIC)
                        textView.textSize = 14f
                        Log.d("Factory2", "水平 LinearLayout 中的 TextView")
                    }
                }
                is FrameLayout -> {
                    // FrameLayout：绿色下划线
                    textView.setTextColor(Color.parseColor("#4CAF50"))
                    textView.paint.isUnderlineText = true
                    textView.textSize = 15f
                    Log.d("Factory2", "FrameLayout 中的 TextView")
                }
                else -> {
                    // 其他容器：默认样式
                    textView.setTextColor(Color.DKGRAY)
                    Log.d("Factory2", "其他容器中的 TextView: ${parent?.javaClass?.simpleName}")
                }
            }

            return textView
        }
    }
}

/**
 * 【深入理解】Factory vs Factory2
 * 
 * Factory:
 * ```kotlin
 * interface Factory {
 *     fun onCreateView(name: String, context: Context, attrs: AttributeSet): View?
 * }
 * ```
 * 
 * Factory2:
 * ```kotlin
 * interface Factory2 : Factory {
 *     fun onCreateView(
 *         parent: View?,
 *         name: String,
 *         context: Context,
 *         attrs: AttributeSet
 *     ): View?
 * }
 * ```
 * 
 * 【区别】
 * - Factory2 多了一个 parent 参数
 * - Factory2 可以感知父容器
 * - LayoutInflaterCompat 推荐使用 Factory2
 * 
 * 【注意事项】
 * 1. 一个 LayoutInflater 只能设置一个 Factory
 * 2. 设置后无法取消，除非 cloneInContext 创建新实例
 * 3. AppCompat 会自动设置 Factory 来支持 tint 等
 * 4. 如果在 AppCompat Activity 中，需要保留原有 Factory 的功能
 * 
 * 【正确做法】
 * ```kotlin
 * // 保留原有 Factory 的功能
 * class CustomFactory2(
 *     private val originalFactory: LayoutInflater.Factory2?
 * ) : LayoutInflater.Factory2 {
 *     
 *     override fun onCreateView(...): View? {
 *         // 先尝试自定义逻辑
 *         val customView = createCustomView(name, context, attrs)
 *         if (customView != null) return customView
 *         
 *         // 回退到原有 Factory
 *         return originalFactory?.onCreateView(parent, name, context, attrs)
 *     }
 * }
 * ```
 */
