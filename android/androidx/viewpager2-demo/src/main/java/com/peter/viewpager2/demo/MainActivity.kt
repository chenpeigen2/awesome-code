package com.peter.viewpager2.demo

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.peter.viewpager2.demo.databinding.ActivityMainBinding
import com.peter.viewpager2.demo.fragments.*

/**
 * ViewPager2 Demo 主页面
 * 展示 ViewPager2 和 Fragment 的各种用法
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Tab 主题色
    private val tabColors = listOf(
        Pair(R.color.tab_simple, R.color.tab_simple_container),              // 基础用法
        Pair(R.color.tab_tab, R.color.tab_tab_container),                    // Tab + ViewPager2
        Pair(R.color.tab_lifecycle, R.color.tab_lifecycle_container),        // 生命周期
        Pair(R.color.tab_communication, R.color.tab_communication_container), // Fragment通信
        Pair(R.color.tab_nested, R.color.tab_nested_container),              // 嵌套ViewPager
        Pair(R.color.tab_dynamic, R.color.tab_dynamic_container)              // 动态页面
    )

    private var currentTabPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 设置沉浸式状态栏
        setupImmersiveStatusBar()

        // 设置 ViewPager
        setupViewPager()

        // 设置初始主题色
        applyTabTheme(0)
    }

    private fun setupImmersiveStatusBar() {
        // 让内容延伸到状态栏和导航栏后面
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 设置状态栏和导航栏为浅色按钮
        window.insetsController?.setSystemBarsAppearance(
            android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
            android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
            android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
            android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
        )
    }

    private fun setupViewPager() {
        val adapter = MainViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()

        // 监听页面切换
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

        // 动画过渡背景色
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

        // 更新 TabLayout 指示器颜色
        binding.tabLayout.setSelectedTabIndicatorColor(primaryColor)
        binding.tabLayout.setTabTextColors(
            ContextCompat.getColor(this, R.color.on_surface_variant),
            primaryColor
        )

        // 更新标题颜色
        binding.tvTitle.setTextColor(primaryColor)
    }

    fun showSnackbar(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}

/**
 * 主页面 ViewPager Adapter
 */
class MainViewPagerAdapter(
    private val activity: AppCompatActivity
) : androidx.viewpager2.adapter.FragmentStateAdapter(activity) {

    private val fragments = listOf(
        SimpleFragment.newInstance(),
        TabDemoFragment.newInstance(),
        LifecycleFragment.newInstance(),
        CommunicationFragment.newInstance(),
        NestedFragment.newInstance(),
        DynamicFragment.newInstance()
    )

    private val titles = listOf(
        activity.getString(R.string.demo_simple),
        activity.getString(R.string.demo_tab),
        activity.getString(R.string.demo_lifecycle),
        activity.getString(R.string.demo_communication),
        activity.getString(R.string.demo_nested),
        activity.getString(R.string.demo_dynamic)
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int) = fragments[position]

    fun getTitle(position: Int): String = titles[position]
}
