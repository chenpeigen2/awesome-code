package com.peter.recyclerview.demo.conflict

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.recyclerview.demo.R
import com.peter.recyclerview.demo.adapter.SimpleListAdapter
import com.peter.recyclerview.demo.databinding.ActivityCoordinatorBinding
import com.peter.recyclerview.demo.model.SimpleItem

/**
 * CoordinatorLayout + AppBarLayout + RecyclerView 联动示例
 * 
 * 核心概念：
 * - CoordinatorLayout：一个超级 FrameLayout，用于协调子 View 之间的交互
 * - AppBarLayout：继承自 LinearLayout，通过 scrollFlags 控制滚动行为
 * - Behavior：定义 View 如何响应其他 View 的变化
 * 
 * layout_scrollFlags 属性详解：
 * - scroll：该 View 会随滚动而滚动出屏幕
 * - enterAlways：向下滚动时，该 View 立即出现
 * - enterAlwaysCollapsed：配合 minHeight 使用
 * - exitUntilCollapsed：配合 minHeight 使用
 * - snap：滑动结束时自动吸附
 */
class CoordinatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoordinatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoordinatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        val listAdapter = SimpleListAdapter()
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CoordinatorActivity)
            adapter = listAdapter
            setHasFixedSize(true)
        }

        loadData(listAdapter)
    }

    private fun loadData(adapter: SimpleListAdapter) {
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
                description = "CoordinatorLayout + AppBarLayout 联动示例 - 第 $index 项",
                colorRes = colors[index % colors.size]
            )
        }

        adapter.submitList(items)
    }
}