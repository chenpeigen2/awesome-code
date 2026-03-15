package com.peter.os.demo

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.peter.os.demo.databinding.ActivityMainBinding
import com.peter.os.demo.fragments.AsyncFragment
import com.peter.os.demo.fragments.BatteryFragment
import com.peter.os.demo.fragments.DebugFragment
import com.peter.os.demo.fragments.DeviceFragment
import com.peter.os.demo.fragments.FileObserverFragment
import com.peter.os.demo.fragments.HandlerFragment
import com.peter.os.demo.fragments.HandlerThreadFragment
import com.peter.os.demo.fragments.ParcelableFragment
import com.peter.os.demo.fragments.StrictModeFragment
import com.peter.os.demo.fragments.StorageFragment
import com.peter.os.demo.fragments.SystemInfoFragment
import com.peter.os.demo.fragments.TimerFragment
import com.peter.os.demo.fragments.UserFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Tab 主题色
    private val tabColors = listOf(
        Pair(R.color.tab_handler, R.color.tab_handler_container),           // Handler
        Pair(R.color.tab_file_observer, R.color.tab_file_observer_container), // FileObserver
        Pair(R.color.tab_storage, R.color.tab_storage_container),           // Storage
        Pair(R.color.tab_timer, R.color.tab_timer_container),               // Timer
        Pair(R.color.tab_device, R.color.tab_device_container),             // Device
        Pair(R.color.tab_system_info, R.color.tab_system_info_container),   // SystemInfo
        Pair(R.color.tab_handler_thread, R.color.tab_handler_thread_container), // HandlerThread
        Pair(R.color.tab_parcelable, R.color.tab_parcelable_container),     // Parcelable
        Pair(R.color.tab_battery, R.color.tab_battery_container),           // Battery
        Pair(R.color.tab_user, R.color.tab_user_container),                 // User
        Pair(R.color.tab_strict_mode, R.color.tab_strict_mode_container),   // StrictMode
        Pair(R.color.tab_debug, R.color.tab_debug_container),               // Debug
        Pair(R.color.tab_async, R.color.tab_async_container)                // Async
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

        // 显示 API Level
        binding.tvApiLevel.text = "API ${Build.VERSION.SDK_INT}"
    }

    private fun setupImmersiveStatusBar() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
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

        // 更新 API 徽章颜色
        binding.apiBadge.strokeColor = primaryColor
        binding.apiBadge.setCardBackgroundColor(containerColor)
        binding.tvApiLevel.setTextColor(primaryColor)
    }
}

class ViewPagerAdapter(
    private val activity: AppCompatActivity
) : androidx.viewpager2.adapter.FragmentStateAdapter(activity) {

    private val fragments = listOf(
        HandlerFragment.newInstance(),
        FileObserverFragment.newInstance(),
        StorageFragment.newInstance(),
        TimerFragment.newInstance(),
        DeviceFragment.newInstance(),
        SystemInfoFragment.newInstance(),
        HandlerThreadFragment.newInstance(),
        ParcelableFragment.newInstance(),
        BatteryFragment.newInstance(),
        UserFragment.newInstance(),
        StrictModeFragment.newInstance(),
        DebugFragment.newInstance(),
        AsyncFragment.newInstance()
    )

    private val titles = listOf(
        activity.getString(R.string.tab_handler),
        activity.getString(R.string.tab_file_observer),
        activity.getString(R.string.tab_storage),
        activity.getString(R.string.tab_timer),
        activity.getString(R.string.tab_device),
        activity.getString(R.string.tab_system_info),
        activity.getString(R.string.tab_handler_thread),
        activity.getString(R.string.tab_parcelable),
        activity.getString(R.string.tab_battery),
        activity.getString(R.string.tab_user),
        activity.getString(R.string.tab_strict_mode),
        activity.getString(R.string.tab_debug),
        activity.getString(R.string.tab_async)
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int) = fragments[position]

    fun getTitle(position: Int): String = titles[position]
}
