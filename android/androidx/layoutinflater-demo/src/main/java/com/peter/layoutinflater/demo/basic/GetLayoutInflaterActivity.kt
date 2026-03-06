package com.peter.layoutinflater.demo.basic

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.peter.layoutinflater.demo.databinding.ActivityGetLayoutInflaterBinding

/**
 * 获取 LayoutInflater 的四种方式对比
 * 
 * 演示不同方式获取 LayoutInflater 的异同
 */
class GetLayoutInflaterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGetLayoutInflaterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 打印 LayoutInflater 信息
        printLayoutInflaterInfo()
        
        binding = ActivityGetLayoutInflaterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        demonstrateLayoutInflaterSources()
    }

    /**
     * 打印当前 Activity 的 LayoutInflater 信息
     */
    private fun printLayoutInflaterInfo() {
        val inflater = layoutInflater
        val sb = StringBuilder()
        sb.appendLine("========== GetLayoutInflaterActivity ==========")
        sb.appendLine("LayoutInflater 实例: $inflater")
        sb.appendLine("hashCode: ${inflater.hashCode()}")
        sb.appendLine("Factory: ${inflater.factory}")
        sb.appendLine("Factory2: ${inflater.factory2}")
        sb.appendLine("===============================================")
        android.util.Log.d("LayoutInflaterDemo", sb.toString())
    }

    /**
     * 演示四种获取 LayoutInflater 的方式
     * 
     * 【关键点】
     * 方式一、二、三 获取的是同一个实例（都是通过 Context.getSystemService）
     * 方式四 可能获取的是不同的实例（Activity 有自己的 LayoutInflater 副本）
     */
    private fun demonstrateLayoutInflaterSources() {
        // 方式一：LayoutInflater.from(context) - 最推荐
        val inflater1 = LayoutInflater.from(this)
        
        // 方式二：context.getSystemService() - 底层实现
        @Suppress("UNCHECKED_CAST")
        val inflater2 = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        
        // 方式三：Activity.getSystemService() - 简化写法
        @Suppress("UNCHECKED_CAST")
        val inflater3 = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        
        // 方式四：Activity.layoutInflater 扩展属性
        val inflater4 = layoutInflater

        // 显示各个实例信息
        binding.tvInflater1.text = "实例: $inflater1\nhashCode: ${inflater1.hashCode()}"
        binding.tvInflater2.text = "实例: $inflater2\nhashCode: ${inflater2.hashCode()}"
        binding.tvInflater3.text = "实例: $inflater3\nhashCode: ${inflater3.hashCode()}"
        binding.tvInflater4.text = "实例: $inflater4\nhashCode: ${inflater4.hashCode()}"

        // 对比结果
        val comparison = buildString {
            appendLine("【实例对比】")
            appendLine()
            appendLine("inflater1 === inflater2: ${inflater1 === inflater2}")
            appendLine("inflater1 === inflater3: ${inflater1 === inflater3}")
            appendLine("inflater1 === inflater4: ${inflater1 === inflater4}")
            appendLine("inflater2 === inflater3: ${inflater2 === inflater3}")
            appendLine("inflater2 === inflater4: ${inflater2 === inflater4}")
            appendLine("inflater3 === inflater4: ${inflater3 === inflater4}")
            appendLine()
            appendLine("【结论】")
            appendLine()
            appendLine("• 方式一、二、三 获取的是同一个实例")
            appendLine("  都是通过 Context.getSystemService() 获取")
            appendLine()
            appendLine("• 方式四 可能是不同的实例")
            appendLine("  Activity 有自己的 LayoutInflater 副本")
            appendLine("  可以设置 Factory 来影响所有 inflate 调用")
            appendLine()
            appendLine("【推荐使用】")
            appendLine("• LayoutInflater.from(context) 最简洁")
            appendLine("• 在 Activity 中可用 layoutInflater 属性")
            appendLine("• ViewBinding 会自动使用正确的 LayoutInflater")
        }

        binding.tvComparison.text = comparison
    }
}
