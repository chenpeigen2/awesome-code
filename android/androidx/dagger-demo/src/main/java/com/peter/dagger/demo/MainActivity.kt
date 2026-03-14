package com.peter.dagger.demo

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.peter.dagger.demo.databinding.ActivityMainBinding
import com.peter.dagger.demo.ui.fragment.AndroidFragment
import com.peter.dagger.demo.ui.fragment.BasicFragment
import com.peter.dagger.demo.ui.fragment.MultibindingFragment
import com.peter.dagger.demo.ui.fragment.QualifierFragment
import com.peter.dagger.demo.ui.fragment.ScopeFragment
import com.peter.dagger.demo.ui.fragment.SubcomponentFragment

/**
 * Dagger2 Demo 主界面
 *
 * 使用 ViewPager2 + TabLayout 切换不同的学习页面
 * 每个页面展示 Dagger2 的一个核心概念
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Tab 主题色
    private val tabColors = listOf(
        Pair(R.color.tab_basic, R.color.tab_basic_container),
        Pair(R.color.tab_scope, R.color.tab_scope_container),
        Pair(R.color.tab_qualifier, R.color.tab_qualifier_container),
        Pair(R.color.tab_subcomponent, R.color.tab_subcomponent_container),
        Pair(R.color.tab_multibinding, R.color.tab_multibinding_container),
        Pair(R.color.tab_android, R.color.tab_android_container)
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
        WindowCompat.setDecorFitsSystemWindows(window, false)
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
    }
}

class ViewPagerAdapter(
    private val activity: AppCompatActivity
) : androidx.viewpager2.adapter.FragmentStateAdapter(activity) {

    private val fragments = listOf(
        BasicFragment.newInstance(),
        ScopeFragment.newInstance(),
        QualifierFragment.newInstance(),
        SubcomponentFragment.newInstance(),
        MultibindingFragment.newInstance(),
        AndroidFragment.newInstance()
    )

    private val titles = listOf(
        "基础", "作用域", "限定符", "子组件", "多绑定", "Android"
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

    fun getTitle(position: Int): String = titles[position]
}
