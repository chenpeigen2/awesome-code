package com.peter.recyclerview.demo.basic

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.recyclerview.demo.R
import com.peter.recyclerview.demo.adapter.SimpleListAdapter
import com.peter.recyclerview.demo.databinding.ActivitySimpleListBinding
import com.peter.recyclerview.demo.model.SimpleItem

/**
 * 简单列表示例
 * 
 * 功能演示：
 * 1. RecyclerView 基本使用
 * 2. LinearLayoutManager 线性布局
 * 3. 点击事件和长按事件
 * 4. 下拉刷新
 * 
 * 关键知识点：
 * - RecyclerView 的基本组成：Adapter、ViewHolder、LayoutManager
 * - ListAdapter + DiffUtil 高效更新数据
 * - ItemDecoration 添加分割线
 * - ItemAnimator 处理动画效果
 */
class SimpleListActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySimpleListBinding
    private lateinit var adapter: SimpleListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimpleListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
        loadData()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        // 1. 创建 Adapter，传入点击事件回调
        adapter = SimpleListAdapter(
            onItemClick = { item, position ->
                Toast.makeText(
                    this,
                    "点击: ${item.title}, 位置: $position",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onItemLongClick = { item, position ->
                Toast.makeText(
                    this,
                    "长按: ${item.title}, 位置: $position",
                    Toast.LENGTH_SHORT
                ).show()
                true // 返回 true 表示消费了事件
            }
        )

        // 2. 设置 RecyclerView
        binding.recyclerView.apply {
            // 设置 LayoutManager
            // LinearLayoutManager 提供了类似 ListView 的垂直/水平列表
            layoutManager = LinearLayoutManager(this@SimpleListActivity)

            // 设置 Adapter
            adapter = this@SimpleListActivity.adapter

            // 可选：设置固定大小，如果 Item 大小固定可以提高性能
            setHasFixedSize(true)

            // 可选：添加分割线
            // addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            // 模拟刷新
            binding.swipeRefresh.postDelayed({
                loadData()
                binding.swipeRefresh.isRefreshing = false
            }, 1000)
        }
    }

    private fun loadData() {
        // 创建测试数据
        val colors = listOf(
            R.color.card_blue,
            R.color.card_green,
            R.color.card_orange,
            R.color.card_purple,
            R.color.card_red
        )

        val items = (1..30).map { index ->
            SimpleItem(
                id = index,
                title = "Item $index",
                description = "这是第 $index 个列表项的详细描述信息，展示了列表项的内容。",
                colorRes = colors[index % colors.size]
            )
        }

        // 使用 ListAdapter 的 submitList 方法更新数据
        // DiffUtil 会自动计算差异并更新
        adapter.submitList(items)
    }
}
