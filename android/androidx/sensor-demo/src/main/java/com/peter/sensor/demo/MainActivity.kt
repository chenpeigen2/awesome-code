package com.peter.sensor.demo

import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.peter.sensor.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var sensorHelper: SensorHelper

    // Tab 主题色
    private val tabColors = listOf(
        Pair(R.color.tab_motion, R.color.tab_motion_container),           // Motion
        Pair(R.color.tab_position, R.color.tab_position_container),       // Position
        Pair(R.color.tab_environment, R.color.tab_environment_container), // Environment
        Pair(R.color.tab_proximity, R.color.tab_proximity_container),     // Proximity
        Pair(R.color.tab_device, R.color.tab_device_container)            // Device
    )

    private var currentTabPosition = 0

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        updatePermissionIndicator(allGranted)
        if (allGranted) {
            showSnackbar("Sensor permissions granted")
        } else {
            showSnackbar("Some sensor permissions denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 设置沉浸式状态栏
        setupImmersiveStatusBar()

        // 初始化 SensorHelper
        sensorHelper = SensorHelper(this)

        // 设置 ViewPager
        setupViewPager()

        // 设置初始主题色
        applyTabTheme(0)

        // 检查权限状态
        checkPermissionStatus()

        // 设置权限徽章点击事件
        binding.permissionBadge.setOnClickListener {
            if (!hasSensorPermission()) {
                requestSensorPermission()
            } else {
                showSnackbar("Sensor permissions already granted")
            }
        }
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

        // 更新权限徽章
        updatePermissionBadgeColor(primaryColor, containerColor)
    }

    private fun updatePermissionBadgeColor(primaryColor: Int, containerColor: Int) {
        binding.permissionBadge.strokeColor = primaryColor
        binding.permissionBadge.setCardBackgroundColor(containerColor)
        binding.tvPermissionStatus.setTextColor(primaryColor)
    }

    private fun checkPermissionStatus() {
        val isGranted = hasSensorPermission()
        updatePermissionIndicator(isGranted)
    }

    private fun updatePermissionIndicator(isGranted: Boolean) {
        if (isGranted) {
            binding.viewPermissionDot.setBackgroundResource(R.drawable.bg_permission_granted)
            binding.tvPermissionStatus.text = getString(R.string.permission_granted)
        } else {
            binding.viewPermissionDot.setBackgroundResource(R.drawable.bg_permission_denied)
            binding.tvPermissionStatus.text = getString(R.string.permission_denied)
        }
    }

    fun hasSensorPermission(): Boolean {
        val hasBodySensors = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BODY_SENSORS
        ) == PackageManager.PERMISSION_GRANTED

        val hasActivityRecognition = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        return hasBodySensors && hasActivityRecognition
    }

    fun requestSensorPermission() {
        val permissions = mutableListOf<String>()

        // Body sensors permission
        permissions.add(Manifest.permission.BODY_SENSORS)

        // Activity recognition (Android 10+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }

        requestPermissionLauncher.launch(permissions.toTypedArray())
    }

    fun showSnackbar(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun getTabColor(position: Int): Int {
        return tabColors[position].first
    }

    fun getTabDotDrawable(position: Int): Int {
        return when (position) {
            0 -> R.drawable.bg_color_dot// Motion
            1 -> R.drawable.bg_color_dot // Position
            2 -> R.drawable.bg_color_dot// Environment
            3 -> R.drawable.bg_color_dot // Proximity
            else -> R.drawable.bg_color_dot// Device
        }
    }
}
