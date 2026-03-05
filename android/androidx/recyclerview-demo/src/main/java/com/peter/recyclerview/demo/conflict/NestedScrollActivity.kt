package com.peter.recyclerview.demo.conflict

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.recyclerview.demo.R
import com.peter.recyclerview.demo.adapter.SimpleListAdapter
import com.peter.recyclerview.demo.databinding.ActivityNestedScrollBinding
import com.peter.recyclerview.demo.model.SimpleItem

/**
 * NestedScrollView 嵌套 RecyclerView 示例
 * 
 * ============================================
 * 滑动冲突场景分析
 * ============================================
 * 
 * 场景：NestedScrollView 内部包含 RecyclerView
 * 
 * 冲突类型：同方向滑动冲突
 * 
 * 问题描述：
 * 1. NestedScrollView 和 RecyclerView 都可以垂直滑动
 * 2. 当用户滑动时，无法判断是应该滑动外层还是内层
 * 3. 可能导致滑动卡顿、滑动不流畅
 * 
 * ============================================
 * 常见问题及解决方案
 * ============================================
 * 
 * 问题一：RecyclerView 显示不完整或只显示一个 Item
 * 原因：NestedScrollView 测量模式下，RecyclerView 的高度不确定
 * 解决：
 * - 方案A：设置 RecyclerView 高度为固定值
 * - 方案B：禁用 RecyclerView 的滑动，让 NestedScrollView 处理滑动
 *   recyclerView.isNestedScrollingEnabled = false
 * 
 * 问题二：滑动卡顿
 * 原因：两个滑动控件互相争夺滑动事件
 * 解决：
 * - 设置 RecyclerView.setHasFixedSize(true)
 * - 使用 shared element transition 优化
 * - 使用 NestedScrollView 的 smoothScrollTo
 * 
 * 问题三：RecyclerView 的复用机制失效
 * 原因：RecyclerView 被当作普通 View 处理，所有 Item 都被创建
 * 解决：
 * - 避免在 NestedScrollView 中使用 RecyclerView
 * - 考虑使用单一 RecyclerView + 多类型 Item 替代
 * 
 * ============================================
 * 最佳实践
 * ============================================
 * 
 * 1. 如果需要嵌套滑动，考虑以下方案：
 *    - 使用 CoordinatorLayout + AppBarLayout
 *    - 使用单一 RecyclerView + 多类型 Item
 *    - 使用 ConcatAdapter 合并多个列表
 * 
 * 2. 如果必须在 NestedScrollView 中使用 RecyclerView：
 *    - 禁用 RecyclerView 的嵌套滑动
 *    - 设置固定高度或 wrap_content
 *    - 注意性能问题（Item 不会被复用）
 */
class NestedScrollActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNestedScrollBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNestedScrollBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupContent()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupContent() {
        // 设置顶部内容
        binding.tvHeader.text = """
            |这是 NestedScrollView 的顶部内容区域。
            |
            |下面是一个 RecyclerView 列表。
            |
            |注意事项：
            |1. RecyclerView 被禁用了嵌套滑动
            |2. 所有滑动由 NestedScrollView 处理
            |3. RecyclerView 的 Item 不会被复用（性能问题）
            |4. 这种方案适合数据量较小的情况
            |
            |对于大数据量，建议使用：
            |- 单一 RecyclerView + 多类型 Item
            |- ConcatAdapter 合并多个列表
            |- CoordinatorLayout + AppBarLayout
        """.trimMargin()
    }

    private fun setupRecyclerView() {
        val adapter = SimpleListAdapter()
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@NestedScrollActivity)
            this.adapter = adapter

            // ============ 关键配置 ============
            
            // 1. 设置固定大小，提高测量效率
            setHasFixedSize(true)
            
            // 2. 禁用嵌套滑动，让 NestedScrollView 处理滑动
            // 这样可以避免滑动冲突，但 RecyclerView 的滑动事件将不会被触发
            isNestedScrollingEnabled = false
            
            // 3. 设置嵌套滚动类型
            // ViewCompat.NESTED_SCROLLING_AXIS_NONE = 不参与嵌套滚动
            // 这样 RecyclerView 就完全交给 NestedScrollView 处理
            ViewCompat.setNestedScrollingEnabled(this, false)
        }

        loadData(adapter)
    }

    private fun loadData(adapter: SimpleListAdapter) {
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
                description = "这是 NestedScrollView 内部 RecyclerView 的第 $index 个 Item",
                colorRes = colors[index % colors.size]
            )
        }

        adapter.submitList(items)
    }
}

/**
 * ============================================
 * 替代方案：使用 ConcatAdapter
 * ============================================
 * 
 * 如果需要在列表中显示多种类型的内容（如 Header + 列表 + Footer），
 * 推荐使用 ConcatAdapter 而不是 NestedScrollView + RecyclerView
 * 
 * 示例代码：
 * 
 * val headerAdapter = HeaderAdapter()
 * val listAdapter = SimpleListAdapter()
 * val footerAdapter = FooterAdapter()
 * 
 * val concatAdapter = ConcatAdapter(headerAdapter, listAdapter, footerAdapter)
 * recyclerView.adapter = concatAdapter
 * 
 * 优点：
 * 1. 所有 Item 共享同一个 RecyclerView，复用机制正常工作
 * 2. 滑动流畅，没有冲突
 * 3. 可以单独更新每个 Adapter
 */
