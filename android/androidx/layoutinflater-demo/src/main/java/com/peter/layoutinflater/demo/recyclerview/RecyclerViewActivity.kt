package com.peter.layoutinflater.demo.recyclerview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.peter.layoutinflater.demo.R
import com.peter.layoutinflater.demo.databinding.ActivityRecyclerViewBinding

/**
 * RecyclerView 中使用 LayoutInflater 示例
 * 
 * 【核心知识点】
 * 在 RecyclerView.Adapter 的 onCreateViewHolder 中使用 LayoutInflater
 * 
 * 【最佳实践】
 * 1. 使用 LayoutInflater.from(parent.context)
 * 2. 传入 parent 参数以生成正确的 LayoutParams
 * 3. attachToRoot = false（RecyclerView 会负责添加）
 */
class RecyclerViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecyclerViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@RecyclerViewActivity)
            adapter = DemoAdapter(createDemoItems())
        }
    }

    private fun createDemoItems(): List<DemoItem> {
        return listOf(
            DemoItem(
                title = "方式一：LayoutInflater.from()",
                description = "最推荐的方式，简洁明了",
                type = DemoItemType.RECOMMENDED
            ),
            DemoItem(
                title = "方式二：context.getSystemService()",
                description = "底层实现，代码较长",
                type = DemoItemType.NORMAL
            ),
            DemoItem(
                title = "方式三：parent.context.getSystemService()",
                description = "在 Adapter 中获取的方式",
                type = DemoItemType.NORMAL
            ),
            DemoItem(
                title = "错误方式：root = null",
                description = "layout 属性会失效",
                type = DemoItemType.WARNING
            ),
            DemoItem(
                title = "错误方式：attachToRoot = true",
                description = "会导致异常，RecyclerView 不允许",
                type = DemoItemType.ERROR
            ),
            DemoItem(
                title = "正确示例汇总",
                description = "记住这个模式就够了",
                type = DemoItemType.SUCCESS
            )
        )
    }

    /**
     * 数据模型
     */
    data class DemoItem(
        val title: String,
        val description: String,
        val type: DemoItemType
    )

    enum class DemoItemType {
        RECOMMENDED,  // 推荐
        NORMAL,       // 正常
        WARNING,      // 警告
        ERROR,        // 错误
        SUCCESS       // 成功
    }

    /**
     * RecyclerView Adapter
     * 
     * 演示 LayoutInflater 在 Adapter 中的正确使用方式
     */
    class DemoAdapter(
        private val items: List<DemoItem>
    ) : RecyclerView.Adapter<DemoAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            // 【LayoutInflater 正确用法】
            // 
            // 1. LayoutInflater.from(parent.context) - 获取 LayoutInflater
            // 2. parent - 用于生成 LayoutParams
            // 3. false - attachToRoot 必须为 false
            //
            // 为什么 attachToRoot 必须为 false？
            // - RecyclerView 会负责添加和移除 ViewHolder
            // - 如果 attachToRoot = true，View 会被添加两次，导致异常
            
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.item_recycler_demo, parent, false)
            
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
            private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
            private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)

            fun bind(item: DemoItem) {
                tvTitle.text = item.title
                tvDescription.text = item.description

                // 根据 type 设置图标颜色
                val colorRes = when (item.type) {
                    DemoItemType.RECOMMENDED -> android.R.color.holo_green_dark
                    DemoItemType.NORMAL -> android.R.color.darker_gray
                    DemoItemType.WARNING -> android.R.color.holo_orange_dark
                    DemoItemType.ERROR -> android.R.color.holo_red_dark
                    DemoItemType.SUCCESS -> android.R.color.holo_blue_dark
                }

                ivIcon.setColorFilter(
                    itemView.context.getColor(colorRes)
                )
            }
        }
    }
}

/**
 * 【完整代码示例】RecyclerView Adapter 中 LayoutInflater 的各种用法
 * 
 * ```kotlin
 * class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
 * 
 *     // 【推荐方式】
 *     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
 *         val inflater = LayoutInflater.from(parent.context)
 *         val view = inflater.inflate(R.layout.item_layout, parent, false)
 *         return ViewHolder(view)
 *     }
 *     
 *     // 【等价方式】context.getSystemService
 *     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
 *         val inflater = parent.context
 *             .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
 *         val view = inflater.inflate(R.layout.item_layout, parent, false)
 *         return ViewHolder(view)
 *     }
 *     
 *     // 【错误方式】root = null
 *     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
 *         val inflater = LayoutInflater.from(parent.context)
 *         val view = inflater.inflate(R.layout.item_layout, null) // ❌ LayoutParams 失效
 *         return ViewHolder(view)
 *     }
 *     
 *     // 【错误方式】attachToRoot = true
 *     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
 *         val inflater = LayoutInflater.from(parent.context)
 *         // ❌ 会抛出异常：The specified child already has a parent
 *         val view = inflater.inflate(R.layout.item_layout, parent, true)
 *         return ViewHolder(view)
 *     }
 * }
 * ```
 */
