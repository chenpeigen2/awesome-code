package com.peter.systeminfo.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.peter.systeminfo.demo.databinding.ActivityMainBinding
import com.peter.systeminfo.demo.fragments.BatteryFragment
import com.peter.systeminfo.demo.fragments.DisplayFragment
import com.peter.systeminfo.demo.fragments.FontFragment
import com.peter.systeminfo.demo.fragments.SystemFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 设置沉浸式状态栏
        setupImmersiveStatusBar()

        // 设置 ViewPager
        setupViewPager()
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
    }
}

class ViewPagerAdapter(
    private val activity: AppCompatActivity
) : androidx.viewpager2.adapter.FragmentStateAdapter(activity) {

    private val fragments = listOf(
        DisplayFragment.newInstance(),
        BatteryFragment.newInstance(),
        SystemFragment.newInstance(),
        FontFragment.newInstance()
    )

    private val titles = listOf(
        activity.getString(R.string.tab_display),
        activity.getString(R.string.tab_battery),
        activity.getString(R.string.tab_system),
        activity.getString(R.string.tab_font)
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int) = fragments[position]

    fun getTitle(position: Int): String = titles[position]
}
