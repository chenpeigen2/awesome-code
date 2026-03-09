package com.peter.notification.demo

import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.peter.notification.demo.channel.ChannelManager
import com.peter.notification.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var channelManager: ChannelManager

    // Tab 主题色
    private val tabColors = listOf(
        Pair(R.color.tab_type, R.color.tab_type_container),           // 类型
        Pair(R.color.tab_channel, R.color.tab_channel_container),     // Channel
        Pair(R.color.tab_group, R.color.tab_group_container),         // 分组
        Pair(R.color.tab_sound, R.color.tab_sound_container),         // 铃声
        Pair(R.color.tab_permission, R.color.tab_permission_container) // 权限
    )

    private var currentTabPosition = 0

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        updatePermissionIndicator(isGranted)
        if (isGranted) {
            showSnackbar("通知权限已授予")
        } else {
            showSnackbar("通知权限被拒绝")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 设置沉浸式状态栏（需要在 setContentView 之后）
        setupImmersiveStatusBar()

        // 初始化 Channel
        channelManager = ChannelManager(this)

        // 设置 ViewPager
        setupViewPager()

        // 设置初始主题色
        applyTabTheme(0)

        // 检查权限状态
        checkPermissionStatus()
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

    override fun onResume() {
        super.onResume()
        checkPermissionStatus()
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

        // 动画过渡背景色（AppBarLayout 背景会自动延伸到状态栏）
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

        // 更新权限徽章
        updatePermissionBadgeColor(primaryColor, containerColor)
    }

    private fun updatePermissionBadgeColor(primaryColor: Int, containerColor: Int) {
        // 更新权限徽章边框颜色
        binding.permissionBadge.strokeColor = primaryColor
        binding.permissionBadge.setCardBackgroundColor(containerColor)
        binding.tvPermissionStatus.setTextColor(primaryColor)
    }

    private fun checkPermissionStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            updatePermissionIndicator(isGranted)
        } else {
            updatePermissionIndicator(true)
        }
    }

    private fun updatePermissionIndicator(isGranted: Boolean) {
        if (isGranted) {
            binding.viewPermissionDot.setBackgroundResource(R.drawable.bg_permission_granted)
            binding.tvPermissionStatus.text = "已授权"
        } else {
            binding.viewPermissionDot.setBackgroundResource(R.drawable.bg_permission_denied)
            binding.tvPermissionStatus.text = "未授权"
        }
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                showSnackbar("需要通知权限才能发送通知演示")
            }
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun showSnackbar(message: String) {
        Toast.makeText(binding.root.context, message, Toast.LENGTH_SHORT).show()
    }
}

class ViewPagerAdapter(
    private val activity: AppCompatActivity
) : androidx.viewpager2.adapter.FragmentStateAdapter(activity) {

    private val fragments = listOf(
        com.peter.notification.demo.fragments.TypeFragment.newInstance(),
        com.peter.notification.demo.fragments.ChannelFragment.newInstance(),
        com.peter.notification.demo.fragments.GroupFragment.newInstance(),
        com.peter.notification.demo.fragments.SoundFragment.newInstance(),
        com.peter.notification.demo.fragments.PermissionFragment.newInstance()
    )

    private val titles = listOf(
        "类型", "Channel", "分组", "铃声", "权限"
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int) = fragments[position]

    fun getTitle(position: Int): String = titles[position]
}
