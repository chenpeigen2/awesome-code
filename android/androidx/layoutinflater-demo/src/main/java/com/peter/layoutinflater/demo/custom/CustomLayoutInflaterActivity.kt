package com.peter.layoutinflater.demo.custom

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.peter.layoutinflater.demo.R
import com.peter.layoutinflater.demo.databinding.ActivityCustomLayoutInflaterBinding

/**
 * 自定义 LayoutInflater 示例
 * 
 * 【核心概念】
 * LayoutInflater 本身是 final 类，不能直接继承。
 * 但可以通过 cloneInContext() 创建一个副本，然后设置自定义 Factory。
 * 
 * 【cloneInContext 的作用】
 * - 创建一个新的 LayoutInflater 实例
 * - 复制原始 inflater 的配置（如 Factory）
 * - 使用新的 Context
 * - 对副本的修改不会影响原始 inflater
 * 
 * 【典型用法】
 * val customInflater = layoutInflater.cloneInContext(this)
 * customInflater.factory = MyCustomFactory()
 */
class CustomLayoutInflaterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomLayoutInflaterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomLayoutInflaterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        demonstrateCustomLayoutInflater()
    }

    /**
     * 演示自定义 LayoutInflater 的使用
     */
    private fun demonstrateCustomLayoutInflater() {
        // 【方式一】使用原始 LayoutInflater（无自定义 Factory）
        val originalView = layoutInflater.inflate(
            R.layout.item_sample,
            binding.containerOriginal,
            false
        )
        binding.containerOriginal.addView(originalView)

        // 【方式二】使用自定义 LayoutInflater（带自定义 Factory）
        // 1. 克隆原始 LayoutInflater
        val customInflater = layoutInflater.cloneInContext(this)

        // 2. 设置自定义 Factory
        customInflater.factory = ItalicTextViewFactory()

        // 3. 使用自定义 inflater 加载布局
        val customView = customInflater.inflate(
            R.layout.item_sample,
            binding.containerCustom,
            false
        )
        binding.containerCustom.addView(customView)
    }

    /**
     * 自定义 Factory：将所有 TextView 变成斜体洋红色
     */
    private class ItalicTextViewFactory : LayoutInflater.Factory {

        override fun onCreateView(
            name: String,
            context: Context,
            attrs: AttributeSet
        ): View? {
            // 只处理 TextView
            if (name.endsWith("TextView")) {
                val tv = TextView(context)

                // 解析原始属性
                val typedArray = context.obtainStyledAttributes(
                    attrs,
                    intArrayOf(
                        android.R.attr.text,
                        android.R.attr.textSize,
                        android.R.attr.gravity
                    )
                )

                tv.text = typedArray.getText(0)
                tv.textSize = typedArray.getDimension(1, 14f) / context.resources.displayMetrics.density
                tv.gravity = typedArray.getInt(2, android.view.Gravity.CENTER)

                typedArray.recycle()

                // 【自定义样式】斜体 + 洋红色
                tv.setTypeface(null, Typeface.ITALIC)
                tv.setTextColor(Color.MAGENTA)

                return tv
            }

            // 其他 View 使用默认创建逻辑
            return null
        }
    }
}

/**
 * 【进阶】LayoutInflater 的内部实现机制
 * 
 * LayoutInflater.inflate() 的执行流程：
 * 
 * 1. 解析 XML 资源
 *    - 通过 XmlResourceParser 解析 XML
 *    - 获取标签名（如 "TextView"）、属性等
 * 
 * 2. 创建 View
 *    - 首先检查是否有 Factory
 *    - 如果 Factory.onCreateView() 返回非 null，使用返回的 View
 *    - 否则使用默认的createView() 方法
 * 
 * 3. 默认 createView 流程
 *    - 对于系统 View（如 TextView），使用 "android.widget." 前缀
 *    - 对于自定义 View，使用完整类名
 *    - 通过反射创建 View 实例
 * 
 * 4. 解析属性
 *    - 通过 AttributeSet 解析 XML 属性
 *    - 调用 View 的构造函数传入属性
 * 
 * 5. 递归处理子 View
 *    - 如果是 ViewGroup，递归处理子元素
 *    - 调用 ViewGroup.addView() 添加子 View
 * 
 * 【核心源码简化】
 * ```java
 * public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot) {
 *     // 1. 获取 XML 解析器
 *     final AttributeSet attrs = Xml.asAttributeSet(parser);
 *     
 *     // 2. 读取根标签
 *     String name = parser.getName();
 *     
 *     // 3. 创建根 View
 *     View view = createViewFromTag(name, attrs);
 *     
 *     // 4. 如果是 ViewGroup，递归 inflate 子元素
 *     if (view instanceof ViewGroup) {
 *         rInflate(parser, (ViewGroup) view, attrs);
 *     }
 *     
 *     // 5. 处理 attachToRoot
 *     if (root != null && attachToRoot) {
 *         root.addView(view, params);
 *     }
 *     
 *     return root != null && attachToRoot ? root : view;
 * }
 * 
 * View createViewFromTag(String name, AttributeSet attrs) {
 *     // 先尝试 Factory
 *     if (mFactory != null) {
 *         View view = mFactory.onCreateView(name, mContext, attrs);
 *         if (view != null) return view;
 *     }
 *     
 *     // 默认创建逻辑
 *     return createView(name, null, attrs);
 * }
 * ```
 */
