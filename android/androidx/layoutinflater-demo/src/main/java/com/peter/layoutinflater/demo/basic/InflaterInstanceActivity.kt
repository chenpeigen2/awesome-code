package com.peter.layoutinflater.demo.basic

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.LayoutInflaterCompat
import com.peter.layoutinflater.demo.R
import com.peter.layoutinflater.demo.databinding.ActivityInflaterInstanceBinding

/**
 * Activity.layoutInflater 深入解析示例
 * 
 * 【核心问题】
 * Activity.layoutInflater 与 Context.getSystemService() 获取的 LayoutInflater 
 * 为什么可能是不同的实例？
 * 
 * 【原因】
 * 1. Context.getSystemService() 调用的是 ContextImpl 的方法，返回系统级 LayoutInflater
 * 2. Activity.layoutInflater 调用的是 getActivitySystemService()，Activity 可能返回自己的副本
 * 3. AppCompatActivity 会进一步包装 LayoutInflater 以支持兼容特性（如 tint）
 * 
 * 【实际影响】
 * - 如果使用 Activity.layoutInflater 设置 Factory，会影响整个 Activity 的布局加载
 * - 包括 setContentView、Fragment 加载等都会受影响
 */
class InflaterInstanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInflaterInstanceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 打印 LayoutInflater 信息
        printLayoutInflaterInfo()
        
        binding = ActivityInflaterInstanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
        runInstanceComparison()
    }

    /**
     * 打印当前 Activity 的 LayoutInflater 信息
     */
    private fun printLayoutInflaterInfo() {
        val inflater = layoutInflater
        val sb = StringBuilder()
        sb.appendLine("========== InflaterInstanceActivity ==========")
        sb.appendLine("LayoutInflater 实例: $inflater")
        sb.appendLine("hashCode: ${inflater.hashCode()}")
        sb.appendLine("Factory: ${inflater.factory}")
        sb.appendLine("Factory2: ${inflater.factory2}")
        sb.appendLine("==============================================")
        android.util.Log.d("LayoutInflaterDemo", sb.toString())
    }

    private fun setupButtons() {
        binding.btnTest.setOnClickListener {
            runInstanceComparison()
        }

        binding.btnFactoryTest.setOnClickListener {
            demonstrateFactoryScope()
        }
    }

    /**
     * 实例对比测试
     * 
     * 详细展示两种方式获取的 LayoutInflater 的区别
     */
    private fun runInstanceComparison() {
        val result = StringBuilder()

        // ========== 获取四种 LayoutInflater ==========
        
        // 方式 1: LayoutInflater.from(context)
        val inflaterFrom = LayoutInflater.from(this)
        
        // 方式 2: Context.getSystemService()
        @Suppress("UNCHECKED_CAST")
        val inflaterSystem = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        
        // 方式 3: Activity.getSystemService()（无参数版本）
        @Suppress("UNCHECKED_CAST")
        val inflaterActivitySystem = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        
        // 方式 4: Activity.layoutInflater 扩展属性
        val inflaterActivity = layoutInflater

        // ========== 显示实例信息 ==========
        result.appendLine("【四种方式获取的 LayoutInflater】")
        result.appendLine()
        result.appendLine("1. LayoutInflater.from(this)")
        result.appendLine("   hashCode: ${inflaterFrom.hashCode()}")
        result.appendLine()
        result.appendLine("2. getSystemService(LAYOUT_INFLATER_SERVICE)")
        result.appendLine("   hashCode: ${inflaterSystem.hashCode()}")
        result.appendLine()
        result.appendLine("3. Activity.getSystemService()")  
        result.appendLine("   hashCode: ${inflaterActivitySystem.hashCode()}")
        result.appendLine()
        result.appendLine("4. Activity.layoutInflater")
        result.appendLine("   hashCode: ${inflaterActivity.hashCode()}")
        result.appendLine()

        // ========== 实例对比 ==========
        result.appendLine("【实例对比（=== 严格相等）】")
        result.appendLine()
        result.appendLine("inflaterFrom === inflaterSystem: ${inflaterFrom === inflaterSystem}")
        result.appendLine("inflaterFrom === inflaterActivitySystem: ${inflaterFrom === inflaterActivitySystem}")
        result.appendLine("inflaterFrom === inflaterActivity: ${inflaterFrom === inflaterActivity}")
        result.appendLine("inflaterSystem === inflaterActivity: ${inflaterSystem === inflaterActivity}")
        result.appendLine()

        // ========== Factory 对比 ==========
        result.appendLine("【Factory 对比】")
        result.appendLine()
        result.appendLine("LayoutInflater.from().factory: ${inflaterFrom.factory}")
        result.appendLine("Activity.layoutInflater.factory: ${inflaterActivity.factory}")
        result.appendLine()

        // ========== 解释结果 ==========
        result.appendLine("【结果解释】")
        result.appendLine()
        
        if (inflaterFrom === inflaterActivity) {
            result.appendLine("• 在此环境中，四种方式获取的是同一个实例")
            result.appendLine("• 这是某些 Android 版本或配置下的行为")
        } else {
            result.appendLine("• Activity.layoutInflater 是独立实例")
            result.appendLine("• AppCompatActivity 会创建包装的 LayoutInflater")
            result.appendLine("• 用于支持 AppCompat 的兼容特性（如 tint）")
        }

        binding.tvInstanceResult.text = result.toString()

        // ========== 视觉对比：使用两种 inflater 加载布局 ==========
        binding.containerA.removeAllViews()
        binding.containerB.removeAllViews()

        // 使用 Context.getSystemService() 获取的 inflater 加载
        val viewA = inflaterSystem.inflate(R.layout.item_sample, binding.containerA, false)
        binding.containerA.addView(viewA)

        // 使用 Activity.layoutInflater 加载
        val viewB = inflaterActivity.inflate(R.layout.item_sample, binding.containerB, false)
        binding.containerB.addView(viewB)

        Log.d("InflaterInstance", "inflaterFrom: $inflaterFrom")
        Log.d("InflaterInstance", "inflaterActivity: $inflaterActivity")
    }

    /**
     * 演示 Factory 的影响范围
     * 
     * 【关键点】
     * 如果为 Activity.layoutInflater 设置 Factory，
     * 会影响整个 Activity 中所有使用该 inflater 的布局加载。
     */
    private fun demonstrateFactoryScope() {
        val result = StringBuilder()

        result.appendLine("【Factory 影响范围演示】")
        result.appendLine()
        result.appendLine("点击下方按钮为 Activity.layoutInflater")
        result.appendLine("设置自定义 Factory，然后观察效果：")
        result.appendLine()

        binding.tvFactoryResult.text = result.toString()

        // 创建一个克隆的 inflater 并设置 Factory
        val clonedInflater = layoutInflater.cloneInContext(this)
        clonedInflater.factory = DemoFactory()

        // 使用克隆的 inflater 加载到 containerA
        binding.containerA.removeAllViews()
        binding.containerB.removeAllViews()

        // 原始 inflater 加载（不受影响）
        val viewOriginal = layoutInflater.inflate(R.layout.item_sample, binding.containerA, false)
        binding.containerA.addView(viewOriginal)

        // 克隆 inflater 加载（应用 Factory）
        val viewCloned = clonedInflater.inflate(R.layout.item_sample, binding.containerB, false)
        binding.containerB.addView(viewCloned)

        result.appendLine("• 左侧：原始 inflater 加载（无 Factory）")
        result.appendLine("• 右侧：克隆 inflater 加载（有 Factory）")
        result.appendLine()
        result.appendLine("可以看到右侧的 TextView 变成了洋红色斜体")
        result.appendLine("这就是 Factory 的作用！")

        binding.tvFactoryResult.text = result.toString()

        Toast.makeText(this, "Factory 已设置，观察右侧效果", Toast.LENGTH_SHORT).show()
    }

    /**
     * 演示用 Factory：将 TextView 改为洋红色斜体
     */
    private class DemoFactory : LayoutInflater.Factory {
        override fun onCreateView(
            name: String,
            context: Context,
            attrs: AttributeSet
        ): View? {
            if (name == "TextView" || name == "android.widget.TextView") {
                val tv = TextView(context)
                val typedArray = context.obtainStyledAttributes(
                    attrs,
                    intArrayOf(android.R.attr.text)
                )
                tv.text = typedArray.getText(0)
                typedArray.recycle()

                // 应用自定义样式
                tv.setTextColor(Color.MAGENTA)
                tv.setTypeface(null, Typeface.ITALIC)
                
                return tv
            }
            return null
        }
    }
}

/**
 * 【深入原理】为什么 Activity.layoutInflater 可能是不同实例？
 * 
 * 1. ContextImpl.getSystemService()
 * ```java
 * // ContextImpl.java
 * public Object getSystemService(String name) {
 *     if (LAYOUT_INFLATER_SERVICE.equals(name)) {
 *         // 返回系统级 LayoutInflater
 *         return mLayoutInflater;
 *     }
 *     // ...
 * }
 * ```
 * 
 * 2. Activity.getActivitySystemService()
 * ```java
 * // Activity.java
 * public Object getActivitySystemService(String name) {
 *     if (LAYOUT_INFLATER_SERVICE.equals(name)) {
 *         // 可能返回 Activity 专用的 LayoutInflater
 *         return mLayoutInflater;
 *     }
 *     return super.getSystemService(name);
 * }
 * ```
 * 
 * 3. AppCompatActivity 进一步包装
 * ```java
 * // AppCompatActivity.java
 * @Override
 * public LayoutInflater getLayoutInflater() {
 *     // 返回包装后的 LayoutInflater
 * // 支持 AppCompat 的兼容特性
 * }
 * ```
 * 
 * 【结论】
 * - 在普通 Activity 中，可能返回相同实例
 * - 在 AppCompatActivity 中，通常返回包装后的不同实例
 * - 包装的目的是支持 tint、主题等兼容特性
 */
