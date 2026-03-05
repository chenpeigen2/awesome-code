package com.peter.constraintlayout.demo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.peter.constraintlayout.demo.advanced.AdvancedConstraintLayoutActivity
import com.peter.constraintlayout.demo.basic.BasicConstraintLayoutActivity
import com.peter.constraintlayout.demo.expert.ExpertConstraintLayoutActivity

/**
 * ConstraintLayout Demo 主入口
 * 
 * 本 Demo 从简单到深入演示 ConstraintLayout 的各种用法：
 * 
 * 1. 基础示例 (BasicConstraintLayoutActivity)
 *    - 相对定位 (Relative Positioning)
 *    - 边距 (Margins)
 *    - 居中定位 (Centering)
 *    - 偏移定位 (Bias)
 *    - 基线对齐 (Baseline Alignment)
 *    - 可选边距 (Gone Margins)
 * 
 * 2. 进阶示例 (AdvancedConstraintLayoutActivity)
 *    - 尺寸控制 (match_constraint)
 *    - 宽高比 (Ratio)
 *    - 链式布局 (Chains)
 *    - Guidelines
 *    - Barriers
 *    - Group
 * 
 * 3. 高级示例 (ExpertConstraintLayoutActivity)
 *    - ConstraintSet 动态约束
 *    - Transition 动画
 *    - Layer 层变换
 *    - 圆形定位 (Circular Positioning)
 *    - Flow 自动换行布局
 */
class MainActivity : AppCompatActivity() {

    data class DemoItem(
        val title: String,
        val description: String,
        val targetActivity: Class<*>,
        val level: String
    )

    private val demoItems = listOf(
        DemoItem(
            title = "基础示例：相对定位与边距",
            description = "学习 ConstraintLayout 最核心的功能：相对定位、边距、居中、偏移、基线对齐、Gone Margin",
            targetActivity = BasicConstraintLayoutActivity::class.java,
            level = "基础"
        ),
        DemoItem(
            title = "进阶示例：尺寸、比例与链",
            description = "学习尺寸控制、宽高比、链式布局、Guidelines、Barriers、Group 等进阶功能",
            targetActivity = AdvancedConstraintLayoutActivity::class.java,
            level = "进阶"
        ),
        DemoItem(
            title = "高级示例：ConstraintSet与动画",
            description = "学习 ConstraintSet 动态约束、Transition 动画、Layer 层变换、圆形定位、Flow 自动换行布局",
            targetActivity = ExpertConstraintLayoutActivity::class.java,
            level = "高级"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "ConstraintLayout Demo"

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DemoAdapter(demoItems) { item ->
            startActivity(Intent(this, item.targetActivity))
        }
    }

    private class DemoAdapter(
        private val items: List<DemoItem>,
        private val onItemClick: (DemoItem) -> Unit
    ) : RecyclerView.Adapter<DemoAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvTitle: TextView = view.findViewById(R.id.tv_title)
            val tvDescription: TextView = view.findViewById(R.id.tv_description)
            val tvLevel: TextView = view.findViewById(R.id.tv_level)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_demo, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.tvTitle.text = item.title
            holder.tvDescription.text = item.description
            holder.tvLevel.text = item.level

            // 根据级别设置不同的颜色
            val levelColor = when (item.level) {
                "基础" -> 0xFF4CAF50.toInt() // 绿色
                "进阶" -> 0xFFFF9800.toInt() // 橙色
                "高级" -> 0xFFF44336.toInt() // 红色
                else -> 0xFF9E9E9E.toInt()   // 灰色
            }
            holder.tvLevel.setBackgroundColor(levelColor)

            holder.itemView.setOnClickListener { onItemClick(item) }
        }

        override fun getItemCount() = items.size
    }
}
