package com.peter.touch.demo.level3

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.peter.touch.demo.BaseTouchActivity
import com.peter.touch.demo.R
import com.peter.touch.demo.databinding.ActivityScrollConflictBinding

/**
 * Level 3: 滑动冲突
 * 
 * 演示常见滑动冲突的解决方案：
 * - 同向滑动冲突（垂直ScrollView嵌套垂直RecyclerView）
 * - 异向滑动冲突（水平ViewPager嵌套垂直RecyclerView）
 */
class ScrollConflictActivity : BaseTouchActivity() {

    private lateinit var binding: ActivityScrollConflictBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScrollConflictBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupViewPager()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.level3_title)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupViewPager() {
        val adapter = ConflictPagerAdapter(this)
        binding.viewPager.adapter = adapter
        
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "同向滑动冲突"
                1 -> "异向滑动冲突"
                2 -> "冲突解决方案"
                else -> "Unknown"
            }
        }.attach()
    }

    private class ConflictPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 3
        
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> SameDirectionConflictFragment.newInstance()
                1 -> DiffDirectionConflictFragment.newInstance()
                2 -> ConflictSolutionFragment.newInstance()
                else -> SameDirectionConflictFragment.newInstance()
            }
        }
    }
}

/**
 * 同向滑动冲突示例
 */
class SameDirectionConflictFragment : Fragment(
    R.layout.fragment_same_direction_conflict
) {
    companion object {
        fun newInstance() = SameDirectionConflictFragment()
    }
}

/**
 * 异向滑动冲突示例
 */
class DiffDirectionConflictFragment : Fragment(
    R.layout.fragment_diff_direction_conflict
) {
    companion object {
        fun newInstance() = DiffDirectionConflictFragment()
    }
}

/**
 * 冲突解决方案对比
 */
class ConflictSolutionFragment : Fragment(
    R.layout.fragment_conflict_solution
) {
    companion object {
        fun newInstance() = ConflictSolutionFragment()
    }
}