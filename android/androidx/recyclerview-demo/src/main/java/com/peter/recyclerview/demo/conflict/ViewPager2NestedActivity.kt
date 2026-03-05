package com.peter.recyclerview.demo.conflict

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.peter.recyclerview.demo.R
import com.peter.recyclerview.demo.adapter.SimpleListAdapter
import com.peter.recyclerview.demo.databinding.ActivityViewpager2NestedBinding
import com.peter.recyclerview.demo.model.SimpleItem

/**
 * ViewPager2 嵌套 RecyclerView 示例
 * 
 * ============================================
 * 滑动冲突场景分析
 * ============================================
 * 
 * 场景：ViewPager2 内部包含垂直滑动的 RecyclerView
 * 
 * 冲突类型：同方向滑动冲突（都是垂直方向）
 * 
 * 问题描述：
 * - ViewPager2 内部也是 RecyclerView，当内部 RecyclerView 滑动时
 * - 系统需要判断是由内部 RecyclerView 消费滑动事件，还是由外部 ViewPager2 消费
 * 
 * 解决方案：
 * 1. ViewPager2 已经内置处理了与内部 RecyclerView 的滑动冲突
 * 2. 当内部 RecyclerView 滑动到边界时，事件会自动传递给外部 ViewPager2
 * 3. 这种机制是通过 NestedScrolling 机制实现的
 * 
 * 关键知识点：
 * - ViewPager2 内部使用 RecyclerView 实现
 * - NestedScrolling 机制处理嵌套滑动
 * - RecyclerView 默认实现了 NestedScrollingChild3 接口
 * 
 * 特殊情况：
 * - 如果内部 RecyclerView 是水平滑动，与 ViewPager2 滑动方向垂直
 * - 这种情况不会有滑动冲突，因为滑动方向不同
 */
class ViewPager2NestedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewpager2NestedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewpager2NestedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViewPager()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupViewPager() {
        // 创建 ViewPager2 的 Adapter
        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // 关联 TabLayout 和 ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "垂直列表"
                1 -> "水平列表"
                else -> "Tab ${position + 1}"
            }
        }.attach()
    }

    /**
     * ViewPager2 的 FragmentStateAdapter
     */
    private inner class ViewPagerAdapter(activity: FragmentActivity) : 
        FragmentStateAdapter(activity) {
        
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> VerticalListFragment.newInstance("垂直滑动列表")
                1 -> HorizontalListFragment.newInstance("水平滑动列表")
                else -> VerticalListFragment.newInstance("Tab ${position + 1}")
            }
        }
    }

    /**
     * 垂直滑动列表 Fragment
     * 
     * 与 ViewPager2（垂直方向）存在同方向滑动冲突
     * ViewPager2 内部已经处理好这种冲突
     */
    class VerticalListFragment : Fragment(R.layout.fragment_recycler_view) {
        companion object {
            fun newInstance(title: String) = VerticalListFragment().apply {
                arguments = Bundle().apply { putString("title", title) }
            }
        }

        override fun onResume() {
            super.onResume()
            val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView) ?: return
            
            // 设置垂直滑动
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            
            val adapter = SimpleListAdapter()
            recyclerView.adapter = adapter

            // 加载数据
            val colors = listOf(
                R.color.card_blue, R.color.card_green, R.color.card_orange,
                R.color.card_purple, R.color.card_red
            )
            val items = (1..50).map { index ->
                SimpleItem(
                    id = index,
                    title = "垂直 Item $index",
                    description = "这是垂直滑动列表中的第 $index 个 Item",
                    colorRes = colors[index % colors.size]
                )
            }
            adapter.submitList(items)
        }
    }

    /**
     * 水平滑动列表 Fragment
     * 
     * 与 ViewPager2（垂直方向）滑动方向垂直，不存在冲突
     */
    class HorizontalListFragment : Fragment(R.layout.fragment_recycler_view_horizontal) {
        companion object {
            fun newInstance(title: String) = HorizontalListFragment().apply {
                arguments = Bundle().apply { putString("title", title) }
            }
        }

        override fun onResume() {
            super.onResume()
            val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView) ?: return
            
            // 设置水平滑动
            recyclerView.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            
            val adapter = SimpleListAdapter()
            recyclerView.adapter = adapter

            // 加载数据
            val colors = listOf(
                R.color.card_blue, R.color.card_green, R.color.card_orange,
                R.color.card_purple, R.color.card_red
            )
            val items = (1..20).map { index ->
                SimpleItem(
                    id = index,
                    title = "水平 $index",
                    description = "水平滑动 Item",
                    colorRes = colors[index % colors.size]
                )
            }
            adapter.submitList(items)
        }
    }
}
