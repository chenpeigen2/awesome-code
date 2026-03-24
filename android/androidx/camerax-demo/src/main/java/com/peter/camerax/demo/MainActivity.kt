package com.peter.camerax.demo

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
import com.peter.camerax.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var cameraHelper: CameraHelper

    // Tab theme colors
    private val tabColors = listOf(
        Pair(R.color.tab_basic, R.color.tab_basic_container),           // Basic
        Pair(R.color.tab_capture, R.color.tab_capture_container),       // Capture
        Pair(R.color.tab_video, R.color.tab_video_container),           // Video
        Pair(R.color.tab_analysis, R.color.tab_analysis_container)      // Analysis
    )

    private var currentTabPosition = 0

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        updatePermissionIndicator(allGranted)
        if (allGranted) {
            showSnackbar(getString(R.string.permission_granted_msg))
        } else {
            showSnackbar(getString(R.string.permission_denied_msg))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup immersive status bar
        setupImmersiveStatusBar()

        // Initialize CameraHelper
        cameraHelper = CameraHelper(this)
        cameraHelper.initCameraProvider(
            onSuccess = { },
            onError = { e -> showSnackbar("Camera init failed: ${e.message}") }
        )

        // Setup ViewPager
        setupViewPager()

        // Set initial theme
        applyTabTheme(0)

        // Check permission status
        checkPermissionStatus()

        // Setup permission badge click
        binding.permissionBadge.setOnClickListener {
            if (!hasCameraPermission()) {
                requestCameraPermission()
            } else {
                showSnackbar(getString(R.string.permission_already_granted))
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

    override fun onDestroy() {
        super.onDestroy()
        cameraHelper.release()
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()

        // Listen for page changes
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

        // Animate background color transition
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

        // Update TabLayout indicator color
        binding.tabLayout.setSelectedTabIndicatorColor(primaryColor)
        binding.tabLayout.setTabTextColors(
            ContextCompat.getColor(this, R.color.on_surface_variant),
            primaryColor
        )

        // Update title color
        binding.tvTitle.setTextColor(primaryColor)

        // Update permission badge
        updatePermissionBadgeColor(primaryColor, containerColor)
    }

    private fun updatePermissionBadgeColor(primaryColor: Int, containerColor: Int) {
        binding.permissionBadge.strokeColor = primaryColor
        binding.permissionBadge.setCardBackgroundColor(containerColor)
        binding.tvPermissionStatus.setTextColor(primaryColor)
    }

    private fun checkPermissionStatus() {
        val isGranted = hasCameraPermission()
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

    fun hasCameraPermission(): Boolean {
        val hasCamera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val hasAudio = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            true // Audio is optional, not required for basic camera
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        }

        return hasCamera
    }

    fun hasRecordAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestCameraPermission() {
        val permissions = mutableListOf<String>()

        // Camera permission
        permissions.add(Manifest.permission.CAMERA)

        requestPermissionLauncher.launch(permissions.toTypedArray())
    }

    fun requestAllPermissions() {
        val permissions = mutableListOf<String>()

        // Camera permission
        permissions.add(Manifest.permission.CAMERA)

        // Audio permission
        permissions.add(Manifest.permission.RECORD_AUDIO)

        requestPermissionLauncher.launch(permissions.toTypedArray())
    }

    fun showSnackbar(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun getTabColor(position: Int): Int {
        return tabColors[position].first
    }

    fun getTabDotDrawable(position: Int): Int {
        return R.drawable.bg_color_dot
    }
}
