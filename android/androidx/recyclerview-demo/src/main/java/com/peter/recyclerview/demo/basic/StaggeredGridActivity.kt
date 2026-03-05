package com.peter.recyclerview.demo.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.peter.recyclerview.demo.adapter.StaggeredGridAdapter
import com.peter.recyclerview.demo.databinding.ActivityStaggeredGridBinding
import com.peter.recyclerview.demo.model.StaggeredItem
import kotlin.random.Random

/**
 * 瀑布流布局示例
 * 
 * 功能演示：
 * 1. StaggeredGridLayoutManager 瀑布流布局
 * 2. 动态高度的 Item
 * 
 * 关键知识点：
 * - StaggeredGridLayoutManager 的使用
 * - 在 ViewHolder 中动态设置 Item 高度
 * - 瀑布流布局的特点：Item 高度可以不同，自动排列
 */
class StaggeredGridActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStaggeredGridBinding
    private lateinit var adapter: StaggeredGridAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaggeredGridBinding.inflate(layoutInflater)
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
        adapter = StaggeredGridAdapter()

        // StaggeredGridLayoutManager：瀑布流布局管理器
        // spanCount: 列数
        // orientation: 滚动方向（VERTICAL 或 HORIZONTAL）
        val layoutManager = StaggeredGridLayoutManager(
            2, // 2 列
            StaggeredGridLayoutManager.VERTICAL // 垂直滚动
        )

        binding.recyclerView.apply {
            this.layoutManager = layoutManager
            adapter = this@StaggeredGridActivity.adapter
        }
    }

    private fun loadData() {
        val items = (1..30).map { index ->
            StaggeredItem(
                id = index,
                title = "图片 $index",
                imageRes = 0, // 占位，实际项目中使用真实图片资源
                ratio = Random.nextDouble(0.5, 2.0).toFloat() // 随机宽高比
            )
        }

        adapter.submitList(items)
    }
}
