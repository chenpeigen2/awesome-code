package com.peter.notification.demo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.peter.notification.demo.channel.ChannelManager
import com.peter.notification.demo.databinding.ActivityMainBinding

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
            updatePermissionIndicator(true)
        }
    }

    private fun updatePermissionIndicator(isGranted: Boolean) {
        val context = this
        if (isGranted) {
            binding.viewPermissionDot.setBackgroundResource(R.drawable.bg_permission_granted)
            binding.tvPermissionStatus.text = "通知权限：已授权"
        } else {
            binding.viewPermissionDot.setBackgroundResource(R.drawable.bg_permission_denied)
            binding.tvPermissionStatus.text = "通知权限：未授权"
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
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
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