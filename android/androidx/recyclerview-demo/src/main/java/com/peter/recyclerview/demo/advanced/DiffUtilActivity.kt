package com.peter.recyclerview.demo.advanced

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.recyclerview.demo.R
import com.peter.recyclerview.demo.adapter.SimpleListAdapter
import com.peter.recyclerview.demo.databinding.ActivitySimpleListBinding
import com.peter.recyclerview.demo.model.SimpleItem

/**
 * DiffUtil 数据更新示例
 * 
 * 功能演示：
 * 1. 手动使用 DiffUtil 计算数据差异
 * 2. 局部更新 vs 全量更新
 * 3. DiffUtil.Callback 的实现
 * 
 * 关键知识点：
 * - DiffUtil.ItemCallback（用于 ListAdapter）
 * - DiffUtil.Callback（用于手动计算）
 * - DiffUtil.DiffResult 用于分发更新
 * 
 * 性能优化：
 * - 对于大数据量，建议在后台线程计算 Diff
 * - 使用 areItemsTheSame() 判断是否是同一个 Item
 * - 使用 areContentsTheSame() 判断内容是否变化
 * - 使用 getChangePayload() 获取变化的具体内容
 */
class DiffUtilActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySimpleListBinding
    private val adapter = SimpleListAdapter(
        onItemClick = { item, _ ->
            Toast.makeText(this, "点击: ${item.title}", Toast.LENGTH_SHORT).show()
        }
    )
    private val items = mutableListOf<SimpleItem>()
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimpleListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        loadData()
    }

    private fun setupToolbar() {
        binding.toolbar.title = "DiffUtil 数据更新"
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DiffUtilActivity)
            adapter = this@DiffUtilActivity.adapter
        }

        // 下拉刷新时更新数据
        binding.swipeRefresh.setOnRefreshListener {
            updateDataWithDiffUtil()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun loadData() {
        val colors = listOf(
            R.color.card_blue,
            R.color.card_green,
            R.color.card_orange
        )

        items.clear()
        items.addAll((1..20).map { index ->
            SimpleItem(
                id = index,
                title = "Item $index",
                description = "描述信息 $index",
                colorRes = colors[index % colors.size]
            )
        })

        adapter.submitList(items.toList())
    }

    /**
     * 使用 DiffUtil 更新数据
     * 
     * 这个示例展示了如何手动使用 DiffUtil
     * 注意：ListAdapter 内部已经封装了 DiffUtil，不需要手动调用
     */
    private fun updateDataWithDiffUtil() {
        val oldList = items.toList()
        
        // 模拟数据变化
        counter++
        val newList = oldList.mapIndexed { index, item ->
            when {
                // 修改某个 Item
                index == 5 -> item.copy(
                    title = "Item ${index + 1} (已修改 #$counter)",
                    description = "更新时间: ${System.currentTimeMillis()}"
                )
                // 删除某个 Item
                index == 10 -> null
                // 新增 Item
                index == 15 -> SimpleItem(
                    id = 100 + counter,
                    title = "新增 Item #$counter",
                    description = "这是新增的数据",
                    colorRes = R.color.card_purple
                )
                else -> item
            }
        }.filterNotNull().toMutableList()

        // 在最后添加一个新 Item
        newList.add(SimpleItem(
            id = 200 + counter,
            title = "尾部新增 Item #$counter",
            description = "这是尾部新增的数据",
            colorRes = R.color.card_red
        ))

        // 使用 DiffUtil 计算差异
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldList.size

            override fun getNewListSize(): Int = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition].id == newList[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }

            // 可选：返回变化的具体内容，用于部分更新
            override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                val oldItem = oldList[oldItemPosition]
                val newItem = newList[newItemPosition]
                
                // 返回变化的字段
                return if (oldItem.title != newItem.title) {
                    "title_changed"
                } else {
                    null
                }
            }
        }

        // 计算差异（对于大数据量，应该在后台线程执行）
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        // 更新数据
        items.clear()
        items.addAll(newList)

        // 分发更新到 Adapter
        diffResult.dispatchUpdatesTo(adapter)
        
        // 或者使用 ListAdapter 的 submitList（内部已经实现了 DiffUtil）
        // adapter.submitList(newList)

        Toast.makeText(this, "数据已更新 #$counter", Toast.LENGTH_SHORT).show()
    }
}
