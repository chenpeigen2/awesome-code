package com.peter.touch.demo.level4

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.peter.touch.demo.BaseTouchActivity
import com.peter.touch.demo.R
import com.peter.touch.demo.databinding.ActivityNestedScrollingBinding

/**
 * Level 4.3: 嵌套滚动
 * 
 * 演示 NestedScrolling 机制：
 * - 协调滚动的现代方案
 * - 核心接口：NestedScrollingChild / NestedScrollingParent
 * - AppBarLayout + RecyclerView 联动效果
 * - 对比传统拦截 vs NestedScrolling 的区别
 */
class NestedScrollingActivity : BaseTouchActivity() {

    private lateinit var binding: ActivityNestedScrollingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNestedScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupViewPager()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.level4_nested_title)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupViewPager() {
        val adapter = NestedScrollPagerAdapter(this)
        binding.viewPager.adapter = adapter
        
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "CoordinatorLayout"
                1 -> "对比演示"
                else -> "Unknown"
            }
        }.attach()
    }

    private class NestedScrollPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 2
        
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> CoordinatorLayoutDemoFragment.newInstance()
                1 -> NestedScrollCompareFragment.newInstance()
                else -> CoordinatorLayoutDemoFragment.newInstance()
            }
        }
    }
}

/**
 * CoordinatorLayout 演示 Fragment
 */
class CoordinatorLayoutDemoFragment : Fragment(
    R.layout.fragment_coordinator_layout_demo
) {
    companion object {
        fun newInstance() = CoordinatorLayoutDemoFragment()
    }
}

/**
 * 嵌套滚动对比演示 Fragment
 */
class NestedScrollCompareFragment : Fragment(
    R.layout.fragment_nested_scroll_compare
) {
    companion object {
        fun newInstance() = NestedScrollCompareFragment()
    }
}