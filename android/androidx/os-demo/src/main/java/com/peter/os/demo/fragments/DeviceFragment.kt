package com.peter.os.demo.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.peter.os.demo.R
import com.peter.os.demo.databinding.FragmentDeviceBinding

/**
 * Vibrator/PowerManager 设备硬件示例
 * 
 * Vibrator: 震动器
 * - vibrate(): 触发震动
 * - cancel(): 取消震动
 * - hasVibrator(): 是否支持震动
 * - hasAmplitudeControl(): 是否支持振幅控制
 * 
 * PowerManager: 电源管理
 * - newWakeLock(): 创建 WakeLock
 * - isInteractive(): 屏幕是否亮起
 * - isPowerSaveMode(): 是否省电模式
 * - isDeviceIdleMode(): 是否空闲模式
 */
class DeviceFragment : Fragment() {

    private var _binding: FragmentDeviceBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var vibrator: Vibrator
    private lateinit var powerManager: PowerManager
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NewApi", "MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initServices()
        setupViews()
        updateDeviceInfo()
    }

    @SuppressLint("NewApi")
    private fun initServices() {
        // 获取 Vibrator 服务
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        
        // 获取 PowerManager 服务
        powerManager = requireContext().getSystemService(Context.POWER_SERVICE) as PowerManager
    }

    @SuppressLint("NewApi", "MissingPermission")
    private fun setupViews() {
        // 震动按钮
        binding.btnVibrateShort.setOnClickListener {
            vibrateShort()
        }

        binding.btnVibrateLong.setOnClickListener {
            vibrateLong()
        }

        binding.btnVibratePattern.setOnClickListener {
            vibratePattern()
        }

        binding.btnVibrateCancel.setOnClickListener {
            vibrator.cancel()
            Toast.makeText(requireContext(), "震动已取消", Toast.LENGTH_SHORT).show()
        }

        // WakeLock 按钮
        binding.btnWakeLockAcquire.setOnClickListener {
            acquireWakeLock()
        }

        binding.btnWakeLockRelease.setOnClickListener {
            releaseWakeLock()
        }

        // 刷新设备信息
        binding.btnRefresh.setOnClickListener {
            updateDeviceInfo()
        }
    }

    @SuppressLint("MissingPermission")
    private fun vibrateShort() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(100)
        }
        appendLog("短震动 (100ms)")
    }

    @SuppressLint("MissingPermission")
    private fun vibrateLong() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
        appendLog("长震动 (500ms)")
    }

    @SuppressLint("MissingPermission", "NewApi")
    private fun vibratePattern() {
        // 模式: 等待, 震动, 等待, 震动, ...
        val pattern = longArrayOf(0, 100, 100, 200, 100, 300)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 创建带振幅的模式
            val amplitudes = intArrayOf(0, 128, 0, 200, 0, 255)
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
        appendLog("震动模式: 等待→100ms→等待→200ms→等待→300ms")
    }

    @SuppressLint("NewApi")
    private fun acquireWakeLock() {
        if (wakeLock?.isHeld == true) {
            Toast.makeText(requireContext(), "WakeLock 已持有", Toast.LENGTH_SHORT).show()
            return
        }
        
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "OsDemo::WakeLock"
        )
        wakeLock?.acquire(10 * 60 * 1000L) // 最多持有10分钟
        binding.tvWakeLockStatus.text = getString(R.string.device_wake_lock_held)
        appendLog("WakeLock 已获取 (10分钟超时)")
        Toast.makeText(requireContext(), "WakeLock 已获取", Toast.LENGTH_SHORT).show()
    }

    private fun releaseWakeLock() {
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
            binding.tvWakeLockStatus.text = getString(R.string.device_wake_lock_released)
            appendLog("WakeLock 已释放")
            Toast.makeText(requireContext(), "WakeLock 已释放", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "WakeLock 未持有", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("NewApi")
    private fun updateDeviceInfo() {
        val sb = StringBuilder()
        
        // 震动器信息
        sb.appendLine("=== 震动器信息 ===")
        sb.appendLine("支持震动: ${if (vibrator.hasVibrator()) "是" else "否"}")
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sb.appendLine("支持振幅控制: ${if (vibrator.hasAmplitudeControl()) "是" else "否"}")
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            sb.appendLine("震动器 ID: ${vibrator.id}")
        }
        sb.appendLine()
        
        // 电源管理信息
        sb.appendLine("=== 电源管理 ===")
        sb.appendLine("屏幕亮起: ${if (powerManager.isInteractive) "是" else "否"}")
        sb.appendLine("省电模式: ${if (powerManager.isPowerSaveMode) "开启" else "关闭"}")
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sb.appendLine("空闲模式: ${if (powerManager.isDeviceIdleMode) "是" else "否"}")
            sb.appendLine("忽略电池优化: ${if (powerManager.isIgnoringBatteryOptimizations(requireContext().packageName)) "是" else "否"}")
        }
        sb.appendLine()
        
        // WakeLock 状态
        sb.appendLine("=== WakeLock 状态 ===")
        sb.appendLine("持有中: ${if (wakeLock?.isHeld == true) "是" else "否"}")
        
        binding.tvDeviceInfo.text = sb.toString()
    }

    private fun appendLog(log: String) {
        val currentText = binding.tvLog.text.toString()
        val newText = if (currentText.isEmpty()) log else "$currentText\n$log"
        binding.tvLog.text = newText
        
        binding.scrollView.post {
            binding.scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vibrator.cancel()
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
        _binding = null
    }

    companion object {
        fun newInstance() = DeviceFragment()
    }
}
