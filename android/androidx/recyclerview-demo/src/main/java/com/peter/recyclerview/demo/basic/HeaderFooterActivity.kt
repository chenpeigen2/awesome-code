package com.peter.recyclerview.demo.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.recyclerview.demo.R
import com.peter.recyclerview.demo.adapter.HeaderFooterAdapter
import com.peter.recyclerview.demo.databinding.ActivityHeaderFooterBinding
import com.peter.recyclerview.demo.model.SimpleItem

/**
 * 头部与尾部示例
 * 
 * 功能演示：
 * 1. 为 RecyclerView 添加 Header 和 Footer
 * 2. 动态显示/隐藏 Header 和 Footer
 * 
 * 关键知识点：
 * - 通过 viewType 区分 Header、Footer 和普通 Item
 * - onCreateViewHolder 根据 viewType 创建不同的 ViewHolder
 * - 数据 position 需要根据 header 数量进行调整
 */
class HeaderFooterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHeaderFooterBinding
    private lateinit var adapter: HeaderFooterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHeaderFooterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupButtons()
        loadData()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = HeaderFooterAdapter()

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HeaderFooterActivity)
            adapter = this@HeaderFooterActivity.adapter
        }
    }

    private fun setupButtons() {
        binding.btnToggleHeader.setOnClickListener {
            adapter.showHeader = !adapter.showHeader
            binding.btnToggleHeader.text = if (adapter.showHeader) "隐藏头部" else "显示头部"
        }

        binding.btnToggleFooter.setOnClickListener {
            adapter.showFooter = !adapter.showFooter
            binding.btnToggleFooter.text = if (adapter.showFooter) "隐藏尾部" else "显示尾部"
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

        val items = (1..20).map { index ->
            SimpleItem(
                id = index,
                title = "Item $index",
                description = "这是第 $index 个列表项",
                colorRes = colors[index % colors.size]
            )
        }

        adapter.submitList(items)
    }
}
