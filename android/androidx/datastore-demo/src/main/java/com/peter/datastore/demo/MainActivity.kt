package com.peter.datastore.demo

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.peter.datastore.demo.databinding.ActivityMainBinding
import com.peter.datastore.demo.fragments.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Tab 主题色
    private val tabColors = listOf(
        Pair(R.color.tab_basic, R.color.tab_basic_container),           // 基础存储
        Pair(R.color.tab_json, R.color.tab_json_container),             // JSON
        Pair(R.color.tab_reactive, R.color.tab_reactive_container),     // 响应式
        Pair(R.color.tab_multitype, R.color.tab_multitype_container),   // 多类型
        Pair(R.color.tab_transaction, R.color.tab_transaction_container) // 事务
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

        // 更新状态徽章颜色
        updateStatusBadgeColor(primaryColor, containerColor)
    }

    private fun updateStatusBadgeColor(primaryColor: Int, containerColor: Int) {
        binding.statusBadge.strokeColor = primaryColor
        binding.statusBadge.setCardBackgroundColor(containerColor)
        binding.tvStatusText.setTextColor(primaryColor)
    }
}

class ViewPagerAdapter(
    private val activity: AppCompatActivity
) : androidx.viewpager2.adapter.FragmentStateAdapter(activity) {

    private val fragments = listOf(
        BasicFragment.newInstance(),
        JsonFragment.newInstance(),
        ReactiveFragment.newInstance(),
        MultiTypeFragment.newInstance(),
        TransactionFragment.newInstance()
    )

    private val titles = listOf(
        "基础", "JSON", "响应式", "多类型", "事务"
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int) = fragments[position]

    fun getTitle(position: Int): String = titles[position]
}
