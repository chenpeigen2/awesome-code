package com.peter.recyclerview.demo.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.peter.recyclerview.demo.R
import com.peter.recyclerview.demo.adapter.GridListAdapter
import com.peter.recyclerview.demo.databinding.ActivityGridListBinding
import com.peter.recyclerview.demo.model.SimpleItem

/**
 * 网格列表示例
 * 
 * 功能演示：
 * 1. GridLayoutManager 网格布局
 * 2. SpanSizeLookup 实现跨列
 * 3. 动态切换列数
 * 
 * 关键知识点：
 * - GridLayoutManager 设置列数
 * - SpanSizeLookup 控制每个 Item 占用的列数
 */
class GridListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGridListBinding
    private lateinit var adapter: GridListAdapter
    private var spanCount = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGridListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        loadData()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = GridListAdapter()

        // GridLayoutManager：网格布局管理器
        // spanCount: 列数
        // orientation: 滚动方向
        // reverseLayout: 是否反向布局
        val layoutManager = GridLayoutManager(this, spanCount)

        // SpanSizeLookup：控制每个 Item 占用的列数
        // 例如：某些 Item 占用全部列（类似 Header）
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // 每 5 个 Item 让第 1 个占用全部列（类似广告位或 Header）
                return if (position % 5 == 0) {
                    spanCount // 占用全部列
                } else {
                    1 // 占用 1 列
                }
            }
        }

        binding.recyclerView.apply {
            this.layoutManager = layoutManager
            adapter = this@GridListActivity.adapter
        }
    }

    private fun loadData() {
        val colors = listOf(
            R.color.card_blue,
            R.color.card_green,
            R.color.card_orange,
            R.color.card_purple,
            R.color.card_red
        )

        val items = (1..50).map { index ->
            SimpleItem(
                id = index,
                title = "Item $index",
                description = "",
                colorRes = colors[index % colors.size]
            )
        }

        adapter.submitList(items)
    }
}
