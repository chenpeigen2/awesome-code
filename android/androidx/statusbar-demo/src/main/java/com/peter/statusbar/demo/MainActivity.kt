package com.peter.statusbar.demo

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.peter.statusbar.demo.StatusBarHelper.getStatusBarHeight
import com.peter.statusbar.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Tab 主题色
    private val tabColors = listOf(
        Pair(R.color.tab_color, R.color.tab_color_container),           // 颜色
        Pair(R.color.tab_immersive, R.color.tab_immersive_container),   // 沉浸式
        Pair(R.color.tab_icon, R.color.tab_icon_container),             // 图标
        Pair(R.color.tab_hide, R.color.tab_hide_container),             // 隐藏
        Pair(R.color.tab_notch, R.color.tab_notch_container)            // 刘海屏
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

        // 显示状态栏高度
        displayStatusBarHeight()
    }

    private fun setupImmersiveStatusBar() {
        // 让内容延伸到状态栏和导航栏后面
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 设置状态栏和导航栏为浅色按钮（因为背景是浅色的）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )
        }
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this)
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

        // 更新徽章颜色
        binding.statusBarBadge.strokeColor = primaryColor
        binding.statusBarBadge.setCardBackgroundColor(containerColor)
        binding.tvStatusBarHeight.setTextColor(primaryColor)
    }

    private fun displayStatusBarHeight() {
        val statusBarHeight = getStatusBarHeight(this)
        binding.tvStatusBarHeight.text = "${statusBarHeight}px"
    }

    fun updateStatusBarBadgeColor(primaryColor: Int, containerColor: Int) {
        binding.statusBarBadge.strokeColor = primaryColor
        binding.statusBarBadge.setCardBackgroundColor(containerColor)
        binding.tvStatusBarHeight.setTextColor(primaryColor)
    }

    fun getAppBarLayout() = binding.appBarLayout
}

class ViewPagerAdapter(
    private val activity: AppCompatActivity
) : androidx.viewpager2.adapter.FragmentStateAdapter(activity) {

    private val fragments = listOf(
        com.peter.statusbar.demo.fragments.ColorFragment.newInstance(),
        com.peter.statusbar.demo.fragments.ImmersiveFragment.newInstance(),
        com.peter.statusbar.demo.fragments.IconFragment.newInstance(),
        com.peter.statusbar.demo.fragments.HideFragment.newInstance(),
        com.peter.statusbar.demo.fragments.NotchFragment.newInstance()
    )

    private val titles = listOf(
        activity.getString(R.string.tab_color),
        activity.getString(R.string.tab_immersive),
        activity.getString(R.string.tab_icon),
        activity.getString(R.string.tab_hide),
        activity.getString(R.string.tab_notch)
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int) = fragments[position]

    fun getTitle(position: Int): String = titles[position]
}
