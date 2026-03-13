package com.peter.room.demo

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.peter.room.demo.databinding.ActivityMainBinding
import com.peter.room.demo.fragments.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Tab 主题色
    private val tabColors = listOf(
        Pair(R.color.tab_basic, R.color.tab_basic_container),
        Pair(R.color.tab_relation, R.color.tab_relation_container),
        Pair(R.color.tab_migration, R.color.tab_migration_container),
        Pair(R.color.tab_converter, R.color.tab_converter_container),
        Pair(R.color.tab_advanced, R.color.tab_advanced_container)
    )

    private var currentTabPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        applyTabTheme(0)
    }

    private fun setupViewPager() {
        val adapter = MainPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentTabPosition = position
                applyTabTheme(position)
            }
        })
    }

    private fun applyTabTheme(position: Int) {
        val (primaryColorRes, containerColorRes) = tabColors[position]
        val primaryColor = ContextCompat.getColor(this, primaryColorRes)
        val containerColor = ContextCompat.getColor(this, containerColorRes)
        val currentContainerColor = ContextCompat.getColor(this, tabColors[currentTabPosition].second)

        val colorAnimator = ValueAnimator.ofObject(
            ArgbEvaluator(),
            currentContainerColor,
            containerColor
        )
        colorAnimator.duration = 300
        colorAnimator.addUpdateListener { animator ->
            val color = animator.animatedValue as Int
            binding.appBarLayout.setBackgroundColor(color)
        }
        colorAnimator.start()

        binding.tabLayout.setSelectedTabIndicatorColor(primaryColor)
        binding.tabLayout.setTabTextColors(
            ContextCompat.getColor(this, R.color.on_surface_variant),
            primaryColor
        )
        binding.tvTitle.setTextColor(primaryColor)
    }
}

class MainPagerAdapter(
    activity: AppCompatActivity
) : FragmentStateAdapter(activity) {

    private val fragments = listOf(
        BasicFragment.newInstance(),
        RelationFragment.newInstance(),
        MigrationFragment.newInstance(),
        ConverterFragment.newInstance(),
        AdvancedFragment.newInstance()
    )

    private val titles = listOf(
        "基础用法",
        "关系映射",
        "迁移",
        "类型转换",
        "高级查询"
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int) = fragments[position]

    fun getTitle(position: Int): String = titles[position]
}
