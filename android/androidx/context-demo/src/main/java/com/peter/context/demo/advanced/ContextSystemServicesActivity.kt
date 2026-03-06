package com.peter.context.demo.advanced

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.KeyguardManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobScheduler
import android.content.ClipboardManager
import android.content.Context
import android.hardware.SensorManager
import android.location.LocationManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.Vibrator
import android.os.VibratorManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.peter.context.demo.databinding.ActivityContextSystemservicesBinding

/**
 * Context 系统服务示例
 * 
 * 通过 Context.getSystemService() 获取各种系统服务
 * 
 * 常用系统服务：
 * - WINDOW_SERVICE - WindowManager
 * - LAYOUT_INFLATER_SERVICE - LayoutInflater
 * - ACTIVITY_SERVICE - ActivityManager
 * - POWER_SERVICE - PowerManager
 * - ALARM_SERVICE - AlarmManager
 * - NOTIFICATION_SERVICE - NotificationManager
 * - KEYGUARD_SERVICE - KeyguardManager
 * - LOCATION_SERVICE - LocationManager
 * - SENSOR_SERVICE - SensorManager
 * - STORAGE_SERVICE - StorageManager
 * - WIFI_SERVICE - WifiManager
 * - CONNECTIVITY_SERVICE - ConnectivityManager
 * - AUDIO_SERVICE - AudioManager
 * - TELEPHONY_SERVICE - TelephonyManager
 * - CLIPBOARD_SERVICE - ClipboardManager
 * - INPUT_METHOD_SERVICE - InputMethodManager
 * - VIBRATOR_SERVICE - Vibrator
 * - JOB_SCHEDULER_SERVICE - JobScheduler
 */
class ContextSystemServicesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContextSystemservicesBinding
    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContextSystemservicesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showSystemServicesInfo()
    }

    private fun setupListeners() {
        binding.btnActivityManager.setOnClickListener { showActivityManager() }
        binding.btnPowerManager.setOnClickListener { showPowerManager() }
        binding.btnConnectivityManager.setOnClickListener { showConnectivityManager() }
        binding.btnWifiManager.setOnClickListener { showWifiManager() }
        binding.btnAudioManager.setOnClickListener { showAudioManager() }
        binding.btnTelephonyManager.setOnClickListener { showTelephonyManager() }
        binding.btnSensorManager.setOnClickListener { showSensorManager() }
        binding.btnClipboardManager.setOnClickListener { showClipboardManager() }
        binding.btnInputMethodManager.setOnClickListener { showInputMethodManager() }
        binding.btnVibrator.setOnClickListener { showVibrator() }
        binding.btnAlarmManager.setOnClickListener { showAlarmManager() }
        binding.btnNotificationManager.setOnClickListener { showNotificationManager() }
    }

    private fun showSystemServicesInfo() {
        sb.clear()
        
        sb.appendLine("=== Context 系统服务 ===\n")
        
        sb.appendLine("通过 Context.getSystemService() 获取系统服务")
        sb.appendLine("语法: getSystemService(Context.XXX_SERVICE)")
        sb.appendLine()
        
        sb.appendLine("=== 服务分类 ===\n")
        sb.appendLine("【窗口/UI 服务】")
        sb.appendLine("  - WINDOW_SERVICE (WindowManager)")
        sb.appendLine("  - LAYOUT_INFLATER_SERVICE (LayoutInflater)")
        sb.appendLine("  - INPUT_METHOD_SERVICE (InputMethodManager)")
        sb.appendLine("  - CLIPBOARD_SERVICE (ClipboardManager)")
        sb.appendLine()
        
        sb.appendLine("【电源/性能服务】")
        sb.appendLine("  - POWER_SERVICE (PowerManager)")
        sb.appendLine("  - ACTIVITY_SERVICE (ActivityManager)")
        sb.appendLine("  - JOB_SCHEDULER_SERVICE (JobScheduler)")
        sb.appendLine("  - ALARM_SERVICE (AlarmManager)")
        sb.appendLine()
        
        sb.appendLine("【网络通信服务】")
        sb.appendLine("  - CONNECTIVITY_SERVICE (ConnectivityManager)")
        sb.appendLine("  - WIFI_SERVICE (WifiManager)")
        sb.appendLine("  - TELEPHONY_SERVICE (TelephonyManager)")
        sb.appendLine()
        
        sb.appendLine("【硬件服务】")
        sb.appendLine("  - SENSOR_SERVICE (SensorManager)")
        sb.appendLine("  - LOCATION_SERVICE (LocationManager)")
        sb.appendLine("  - AUDIO_SERVICE (AudioManager)")
        sb.appendLine("  - VIBRATOR_SERVICE (Vibrator)")
        sb.appendLine()
        
        sb.appendLine("【通知服务】")
        sb.appendLine("  - NOTIFICATION_SERVICE (NotificationManager)")
        sb.appendLine("  - KEYGUARD_SERVICE (KeyguardManager)")
        sb.appendLine()
        
        binding.tvInfo.text = sb.toString()
    }

    private fun showActivityManager() {
        sb.clear()
        sb.appendLine("=== ActivityManager ===\n")
        
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        sb.appendLine("实例: $am")
        sb.appendLine()
        
        // 内存信息
        val memInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memInfo)
        sb.appendLine("内存信息:")
        sb.appendLine("  总内存: ${formatSize(memInfo.totalMem)}")
        sb.appendLine("  可用内存: ${formatSize(memInfo.availMem)}")
        sb.appendLine("  阈值: ${formatSize(memInfo.threshold)}")
        sb.appendLine("  低内存: ${memInfo.lowMemory}")
        sb.appendLine()
        
        // 运行中的应用
        sb.appendLine("运行中的应用:")
        val appProcesses = am.runningAppProcesses
        appProcesses?.take(5)?.forEach {
            sb.appendLine("  ${it.processName} (${it.pid})")
        }
        sb.appendLine()
        
        // 当前任务
        sb.appendLine("最近任务:")
        val tasks = am.getRecentTasks(5, 0)
        tasks.forEach {
            sb.appendLine("  ${it.baseIntent.component?.flattenToShortString()}")
        }
        
        binding.tvResult.text = sb.toString()
        Log.d("SystemService", sb.toString())
    }

    private fun showPowerManager() {
        sb.clear()
        sb.appendLine("=== PowerManager ===\n")
        
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        sb.appendLine("实例: $pm")
        sb.appendLine()
        
        sb.appendLine("电源状态:")
        sb.appendLine("  屏幕亮着: ${pm.isInteractive}")
        sb.appendLine("  设备空闲: ${pm.isDeviceIdleMode}")
        sb.appendLine("  省电模式: ${pm.isPowerSaveMode}")
        sb.appendLine()
        
        // WakeLock 说明
        sb.appendLine("WakeLock 使用:")
        sb.appendLine("  val wakeLock = pm.newWakeLock(flags, tag)")
        sb.appendLine("  wakeLock.acquire(timeout)")
        sb.appendLine("  wakeLock.release()")
        sb.appendLine()
        
        sb.appendLine("WakeLock Flags:")
        sb.appendLine("  PARTIAL_WAKE_LOCK - CPU运行,屏幕关闭")
        sb.appendLine("  SCREEN_DIM_WAKE_LOCK - 屏幕变暗")
        sb.appendLine("  SCREEN_BRIGHT_WAKE_LOCK - 屏幕亮起")
        sb.appendLine("  FULL_WAKE_LOCK - 屏幕+键盘亮起")
        sb.appendLine("  ACQUIRE_CAUSES_WAKEUP - 获取时唤醒")
        sb.appendLine("  ON_AFTER_RELEASE - 释放后保持一会")
        
        binding.tvResult.text = sb.toString()
        Log.d("SystemService", sb.toString())
    }

    private fun showConnectivityManager() {
        sb.clear()
        sb.appendLine("=== ConnectivityManager ===\n")
        
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        sb.appendLine("实例: $cm")
        sb.appendLine()
        
        // 当前网络
        val activeNetwork = cm.activeNetworkInfo
        sb.appendLine("当前网络:")
        if (activeNetwork != null && activeNetwork.isConnected) {
            sb.appendLine("  类型: ${activeNetwork.typeName}")
            sb.appendLine("  状态: ${activeNetwork.state}")
            sb.appendLine("  详细状态: ${activeNetwork.detailedState}")
        } else {
            sb.appendLine("  无网络连接")
        }
        sb.appendLine()
        
        // Android 10+ NetworkCapabilities
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val nc = cm.getNetworkCapabilities(cm.activeNetwork)
            sb.appendLine("NetworkCapabilities (Android 10+):")
            nc?.let {
                sb.appendLine("  上行带宽: ${it.linkUpstreamBandwidthKbps} kbps")
                sb.appendLine("  下行带宽: ${it.linkDownstreamBandwidthKbps} kbps")
                sb.appendLine("  有 WiFi: ${it.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)}")
                sb.appendLine("  有蜂窝: ${it.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)}")
                sb.appendLine("  有以太网: ${it.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)}")
            }
        }
        
        binding.tvResult.text = sb.toString()
        Log.d("SystemService", sb.toString())
    }

    private fun showWifiManager() {
        sb.clear()
        sb.appendLine("=== WifiManager ===\n")
        
        val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        sb.appendLine("实例: $wm")
        sb.appendLine()
        
        sb.appendLine("WiFi 状态:")
        sb.appendLine("  启用: ${wm.isWifiEnabled}")
        sb.appendLine("  状态: ${getWifiStateString(wm.wifiState)}")
        sb.appendLine()
        
        // 连接信息
        val info = wm.connectionInfo
        sb.appendLine("连接信息:")
        sb.appendLine("  SSID: ${info.ssid}")
        sb.appendLine("  BSSID: ${info.bssid}")
        sb.appendLine("  IP: ${intToIp(info.ipAddress)}")
        sb.appendLine("  信号强度: ${info.rssi} dBm")
        sb.appendLine("  链接速度: ${info.linkSpeed} Mbps")
        sb.appendLine()
        
        sb.appendLine("扫描结果:")
        wm.scanResults.take(3).forEach {
            sb.appendLine("  ${it.SSID} (${it.level} dBm)")
        }
        
        binding.tvResult.text = sb.toString()
        Log.d("SystemService", sb.toString())
    }
    
    private fun getWifiStateString(state: Int): String = when (state) {
        WifiManager.WIFI_STATE_DISABLING -> "正在关闭"
        WifiManager.WIFI_STATE_DISABLED -> "已关闭"
        WifiManager.WIFI_STATE_ENABLING -> "正在开启"
        WifiManager.WIFI_STATE_ENABLED -> "已开启"
        WifiManager.WIFI_STATE_UNKNOWN -> "未知"
        else -> "未知状态"
    }
    
    private fun intToIp(ip: Int): String {
        return "${ip and 0xFF}.${ip shr 8 and 0xFF}.${ip shr 16 and 0xFF}.${ip shr 24 and 0xFF}"
    }

    private fun showAudioManager() {
        sb.clear()
        sb.appendLine("=== AudioManager ===\n")
        
        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        sb.appendLine("实例: $am")
        sb.appendLine()
        
        sb.appendLine("音量信息:")
        sb.appendLine("  媒体音量: ${am.getStreamVolume(AudioManager.STREAM_MUSIC)}/${am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)}")
        sb.appendLine("  铃声音量: ${am.getStreamVolume(AudioManager.STREAM_RING)}/${am.getStreamMaxVolume(AudioManager.STREAM_RING)}")
        sb.appendLine("  通知音量: ${am.getStreamVolume(AudioManager.STREAM_NOTIFICATION)}/${am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)}")
        sb.appendLine("  闹钟音量: ${am.getStreamVolume(AudioManager.STREAM_ALARM)}/${am.getStreamMaxVolume(AudioManager.STREAM_ALARM)}")
        sb.appendLine()
        
        sb.appendLine("状态:")
        sb.appendLine("  静音模式: ${am.ringerMode == AudioManager.RINGER_MODE_SILENT}")
        sb.appendLine("  振动模式: ${am.ringerMode == AudioManager.RINGER_MODE_VIBRATE}")
        sb.appendLine("  蓝牙A2DP: ${am.isBluetoothA2dpOn}")
        sb.appendLine("  外放: ${am.isSpeakerphoneOn}")
        
        binding.tvResult.text = sb.toString()
        Log.d("SystemService", sb.toString())
    }

    private fun showTelephonyManager() {
        sb.clear()
        sb.appendLine("=== TelephonyManager ===\n")
        
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        sb.appendLine("实例: $tm")
        sb.appendLine()
        
        sb.appendLine("设备信息:")
        sb.appendLine("  运营商: ${tm.networkOperatorName}")
        sb.appendLine("  国家: ${tm.networkCountryIso}")
        sb.appendLine("  网络类型: ${getNetworkTypeString(tm.networkType)}")
        sb.appendLine("  数据状态: ${tm.dataState}")
        sb.appendLine("  SIM 状态: ${getSimStateString(tm.simState)}")
        sb.appendLine()
        
        sb.appendLine("注意: 需要 READ_PHONE_STATE 权限")
        sb.appendLine("IMEI 等敏感信息 Android 10+ 受限")
        
        binding.tvResult.text = sb.toString()
        Log.d("SystemService", sb.toString())
    }
    
    private fun getNetworkTypeString(type: Int): String = when (type) {
        TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE,
        TelephonyManager.NETWORK_TYPE_CDMA -> "2G"
        TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0,
        TelephonyManager.NETWORK_TYPE_HSDPA -> "3G"
        TelephonyManager.NETWORK_TYPE_LTE -> "4G"
        TelephonyManager.NETWORK_TYPE_NR -> "5G"
        else -> "未知"
    }
    
    private fun getSimStateString(state: Int): String = when (state) {
        TelephonyManager.SIM_STATE_ABSENT -> "无SIM卡"
        TelephonyManager.SIM_STATE_PIN_REQUIRED -> "需要PIN"
        TelephonyManager.SIM_STATE_PUK_REQUIRED -> "需要PUK"
        TelephonyManager.SIM_STATE_NETWORK_LOCKED -> "网络锁定"
        TelephonyManager.SIM_STATE_READY -> "就绪"
        else -> "未知"
    }

    private fun showSensorManager() {
        sb.clear()
        sb.appendLine("=== SensorManager ===\n")
        
        val sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sb.appendLine("实例: $sm")
        sb.appendLine()
        
        sb.appendLine("设备传感器列表:")
        sm.getSensorList(SensorManager.SENSOR_ALL).forEach { sensor ->
            sb.appendLine("  ${sensor.name}")
            sb.appendLine("    类型: ${sensor.stringType}")
            sb.appendLine("    厂商: ${sensor.vendor}")
            sb.appendLine("    版本: ${sensor.version}")
            sb.appendLine("    范围: ${sensor.maximumRange}")
            sb.appendLine("    分辨率: ${sensor.resolution}")
            sb.appendLine("    功耗: ${sensor.power} mA")
            sb.appendLine()
        }
        
        binding.tvResult.text = sb.toString()
        Log.d("SystemService", sb.toString())
    }

    private fun showClipboardManager() {
        sb.clear()
        sb.appendLine("=== ClipboardManager ===\n")
        
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        sb.appendLine("实例: $cm")
        sb.appendLine()
        
        // 当前剪贴板内容
        if (cm.hasPrimaryClip()) {
            val clip = cm.primaryClip
            sb.appendLine("剪贴板内容:")
            clip?.let {
                for (i in 0 until it.itemCount) {
                    sb.appendLine("  Item $i: ${it.getItemAt(i).text}")
                }
            }
        } else {
            sb.appendLine("剪贴板为空")
        }
        sb.appendLine()
        
        sb.appendLine("复制文本示例:")
        sb.appendLine("  val clip = ClipData.newPlainText(\"label\", \"text\")")
        sb.appendLine("  cm.setPrimaryClip(clip)")
        
        binding.tvResult.text = sb.toString()
        Log.d("SystemService", sb.toString())
    }

    private fun showInputMethodManager() {
        sb.clear()
        sb.appendLine("=== InputMethodManager ===\n")
        
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        sb.appendLine("实例: $imm")
        sb.appendLine()
        
        sb.appendLine("常用方法:")
        sb.appendLine("  showSoftInput(view, flags) - 显示软键盘")
        sb.appendLine("  hideSoftInputFromWindow(token, flags) - 隐藏软键盘")
        sb.appendLine("  toggleSoftInput(showFlags, hideFlags) - 切换")
        sb.appendLine("  isActive() - 是否激活")
        sb.appendLine()
        
        sb.appendLine("隐藏键盘示例:")
        sb.appendLine("  imm.hideSoftInputFromWindow(editText.windowToken, 0)")
        sb.appendLine()
        
        sb.appendLine("显示键盘示例:")
        sb.appendLine("  editText.requestFocus()")
        sb.appendLine("  imm.showSoftInput(editText, 0)")
        
        binding.tvResult.text = sb.toString()
        Log.d("SystemService", sb.toString())
    }

    private fun showVibrator() {
        sb.clear()
        sb.appendLine("=== Vibrator ===\n")
        
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        
        sb.appendLine("实例: $vibrator")
        sb.appendLine("有振动器: ${vibrator.hasVibrator()}")
        sb.appendLine()
        
        sb.appendLine("振动方法:")
        sb.appendLine("  vibrate(milliseconds) - 振动指定时长")
        sb.appendLine("  vibrate(pattern, repeat) - 模式振动")
        sb.appendLine("  cancel() - 取消振动")
        sb.appendLine()
        
        sb.appendLine("示例:")
        sb.appendLine("  vibrator.vibrate(100) // 振动100ms")
        sb.appendLine()
        sb.appendLine("Android 12+ 需要 VIBRATE 权限")
        
        binding.tvResult.text = sb.toString()
        Log.d("SystemService", sb.toString())
    }

    private fun showAlarmManager() {
        sb.clear()
        sb.appendLine("=== AlarmManager ===\n")
        
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        sb.appendLine("实例: $am")
        sb.appendLine()
        
        sb.appendLine("定时任务类型:")
        sb.appendLine("  RTC_WAKEUP - 唤醒设备执行")
        sb.appendLine("  RTC - 不唤醒设备")
        sb.appendLine("  ELAPSED_REALTIME_WAKEUP - 从启动开始计时")
        sb.appendLine("  ELAPSED_REALTIME - 从启动开始计时，不唤醒")
        sb.appendLine()
        
        sb.appendLine("设置闹钟:")
        sb.appendLine("  am.set(type, triggerAtMillis, operation)")
        sb.appendLine("  am.setExact(type, triggerAtMillis, operation) // 精确")
        sb.appendLine("  am.setRepeating(type, triggerAtMillis, interval, op) // 重复")
        sb.appendLine()
        
        sb.appendLine("注意:")
        sb.appendLine("  Android 12+ 需要 SCHEDULE_EXACT_ALARM 权限")
        sb.appendLine("  推荐使用 WorkManager 代替")
        
        binding.tvResult.text = sb.toString()
        Log.d("SystemService", sb.toString())
    }

    private fun showNotificationManager() {
        sb.clear()
        sb.appendLine("=== NotificationManager ===\n")
        
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        sb.appendLine("实例: $nm")
        sb.appendLine()
        
        sb.appendLine("通知渠道 (Android 8.0+):")
        val channels = nm.notificationChannels
        if (channels.isEmpty()) {
            sb.appendLine("  暂无渠道")
        } else {
            channels.forEach {
                sb.appendLine("  ${it.id}: ${it.name}")
            }
        }
        sb.appendLine()
        
        sb.appendLine("创建通知渠道:")
        sb.appendLine("  val channel = NotificationChannel(id, name, importance)")
        sb.appendLine("  nm.createNotificationChannel(channel)")
        sb.appendLine()
        
        sb.appendLine("发送通知:")
        sb.appendLine("  nm.notify(id, notification)")
        sb.appendLine()
        
        sb.appendLine("取消通知:")
        sb.appendLine("  nm.cancel(id)")
        sb.appendLine("  nm.cancelAll()")
        
        binding.tvResult.text = sb.toString()
        Log.d("SystemService", sb.toString())
    }

    private fun formatSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> String.format("%.2f KB", bytes / 1024.0)
            bytes < 1024 * 1024 * 1024 -> String.format("%.2f MB", bytes / (1024.0 * 1024))
            else -> String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024))
        }
    }
}
