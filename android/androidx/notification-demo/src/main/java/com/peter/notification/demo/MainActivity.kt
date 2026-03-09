package com.peter.notification.demo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.peter.notification.demo.channel.ChannelManager
import com.peter.notification.demo.databinding.ActivityMainBinding
import com.peter.notification.demo.fragments.ChannelFragment
import com.peter.notification.demo.fragments.GroupFragment
import com.peter.notification.demo.fragments.PermissionFragment
import com.peter.notification.demo.fragments.SoundFragment
import com.peter.notification.demo.fragments.TypeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var channelManager: ChannelManager

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

        // 初始化 Channel
        channelManager = ChannelManager(this)
        channelManager.initializeDefaultChannels()

        // 设置 ViewPager
        setupViewPager()

        // 检查权限状态
        checkPermissionStatus()
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
    }

    private fun checkPermissionStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            updatePermissionIndicator(isGranted)
        } else {
            // Android 13 以下不需要运行时权限
            updatePermissionIndicator(true)
        }
    }

    private fun updatePermissionIndicator(isGranted: Boolean) {
        if (isGranted) {
            binding.permissionIndicator.apply {
                setBackgroundColor(
                    ContextCompat.getColor(this@MainActivity, R.color.permission_granted)
                )
                text = "✓ 通知权限：已授权"
                setOnClickListener(null)
            }
        } else {
            binding.permissionIndicator.apply {
                setBackgroundColor(
                    ContextCompat.getColor(this@MainActivity, R.color.permission_denied)
                )
                text = "⚠ 通知权限：未授权 [点击开启]"
                setOnClickListener {
                    requestNotificationPermission()
                }
            }
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
                // 用户之前拒绝过，解释为什么需要权限
                Toast.makeText(
                    this,
                    "需要通知权限才能发送通知演示",
                    Toast.LENGTH_LONG
                ).show()
            }
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}

class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    private val titles = listOf("通知类型", "Channel", "分组", "铃声", "权限")

    override fun getItemCount(): Int = titles.size

    override fun createFragment(position: Int): androidx.fragment.app.Fragment {
        return when (position) {
            0 -> TypeFragment()
            1 -> ChannelFragment()
            2 -> GroupFragment()
            3 -> SoundFragment()
            4 -> PermissionFragment()
            else -> TypeFragment()
        }
    }

    fun getTitle(position: Int): String = titles[position]
}