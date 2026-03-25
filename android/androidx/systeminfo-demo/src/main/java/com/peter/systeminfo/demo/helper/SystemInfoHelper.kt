package com.peter.systeminfo.demo.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.annotation.RequiresApi

/**
 * 系统信息工具类
 * 用于获取屏幕显示、电池状态、系统信息、字体配置等
 */
object SystemInfoHelper {

    // ===== 屏幕显示信息 =====

    data class DisplayInfo(
        val resolution: String,        // "1080 x 2400"
        val density: Float,            // 2.75
        val densityDpi: Int,           // 440
        val scaledDensity: Float,      // 字体缩放密度
        val xdpi: Float,               // 物理 X 轴 DPI
        val ydpi: Float,               // 物理 Y 轴 DPI
        val widthPixels: Int,          // 1080
        val heightPixels: Int,         // 2400
        val refreshRate: Float,        // 120Hz
        val screenWidthDp: Float,      // 360dp
        val screenHeightDp: Float,     // 800dp
        val isScreenRound: Boolean,    // 是否为圆形屏幕
        val isHdrSupported: Boolean    // 是否支持 HDR
    )

    fun getDisplayInfo(activity: Activity): DisplayInfo {
        val windowManager = activity.windowManager
        val displayMetrics = DisplayMetrics()

        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val refreshRate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.display?.refreshRate ?: 60f
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.refreshRate
        }

        val isScreenRound = activity.resources.configuration.isScreenRound

        val isHdrSupported = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.display?.isHdr ?: false
        } else {
            false
        }

        // 计算 dp 值
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val screenHeightDp = displayMetrics.heightPixels / displayMetrics.density

        return DisplayInfo(
            resolution = "${displayMetrics.widthPixels} × ${displayMetrics.heightPixels}",
            density = displayMetrics.density,
            densityDpi = displayMetrics.densityDpi,
            scaledDensity = displayMetrics.scaledDensity,
            xdpi = displayMetrics.xdpi,
            ydpi = displayMetrics.ydpi,
            widthPixels = displayMetrics.widthPixels,
            heightPixels = displayMetrics.heightPixels,
            refreshRate = refreshRate,
            screenWidthDp = screenWidthDp,
            screenHeightDp = screenHeightDp,
            isScreenRound = isScreenRound,
            isHdrSupported = isHdrSupported
        )
    }

    // ===== 电池状态信息 =====

    data class BatteryInfo(
        val level: Int,                // 85 (%)
        val status: String,            // 充电中/放电中/已充满/未知
        val health: String,            // 良好/过热/损坏/未知
        val temperature: Float,        // 25.5 (°C)
        val voltage: Int,              // 4200 (mV)
        val technology: String,        // Li-ion
        val plugged: String,           // 未连接/USB/交流电/无线
        val capacity: Int              // 电池容量 (mAh)，部分设备可用
    )

    fun getBatteryInfo(context: Context): BatteryInfo {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        // 获取电量
        val level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        // 通过 Intent 获取详细电池信息
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, intentFilter)

        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val health = batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1
        val temperature = batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
        val voltage = batteryStatus?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0
        val technology = batteryStatus?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Unknown"
        val plugged = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0

        // 电池容量（需要 API 21+，且部分设备不可用）
        val capacity = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER).toInt() / 1000
        } else {
            0
        }

        return BatteryInfo(
            level = level,
            status = getStatusString(status),
            health = getHealthString(health),
            temperature = temperature / 10f,  // 转换为摄氏度
            voltage = voltage,
            technology = technology,
            plugged = getPluggedString(plugged),
            capacity = if (capacity > 0) capacity else getEstimatedCapacity(context)
        )
    }

    private fun getStatusString(status: Int): String {
        return when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "充电中"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "放电中"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "未充电"
            BatteryManager.BATTERY_STATUS_FULL -> "已充满"
            BatteryManager.BATTERY_STATUS_UNKNOWN -> "未知"
            else -> "未知"
        }
    }

    private fun getHealthString(health: Int): String {
        return when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "良好"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "过热"
            BatteryManager.BATTERY_HEALTH_DEAD -> "损坏"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "过压"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "未知故障"
            BatteryManager.BATTERY_HEALTH_COLD -> "过冷"
            else -> "未知"
        }
    }

    private fun getPluggedString(plugged: Int): String {
        return when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> "交流电"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "无线"
            0 -> "未连接"
            else -> "未知"
        }
    }

    private fun getEstimatedCapacity(context: Context): Int {
        // 尝试从系统文件读取电池容量
        return try {
            val powerProfile = Class.forName("com.android.internal.os.PowerProfile")
                .getConstructor(Context::class.java)
                .newInstance(context)
            val batteryCapacity = powerProfile.javaClass
                .getMethod("getBatteryCapacity")
                .invoke(powerProfile) as Double
            batteryCapacity.toInt()
        } catch (e: Exception) {
            0
        }
    }

    // ===== 系统信息 =====

    data class SystemInfo(
        val androidVersion: String,    // "Android 14"
        val apiLevel: Int,             // 34
        val securityPatch: String,     // "2024-01-05"
        val device: String,            // 设备代号
        val model: String,             // Pixel 7
        val brand: String,             // Google
        val manufacturer: String,      // Google
        val product: String,           // 产品名称
        val board: String,             // 主板
        val hardware: String,          // 硬件名称
        val buildId: String,           // Build ID
        val buildFingerprint: String,  // 指纹
        val bootloader: String,        // 引导程序版本
        val radioVersion: String,      // 基带版本
        val kernelVersion: String,     // 内核版本
        val isEmulator: Boolean        // 是否为模拟器
    )

    fun getSystemInfo(): SystemInfo {
        return SystemInfo(
            androidVersion = "Android ${Build.VERSION.RELEASE}",
            apiLevel = Build.VERSION.SDK_INT,
            securityPatch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Build.VERSION.SECURITY_PATCH
            } else {
                "N/A"
            },
            device = Build.DEVICE,
            model = Build.MODEL,
            brand = Build.BRAND,
            manufacturer = Build.MANUFACTURER,
            product = Build.PRODUCT,
            board = Build.BOARD,
            hardware = Build.HARDWARE,
            buildId = Build.ID,
            buildFingerprint = Build.FINGERPRINT,
            bootloader = Build.BOOTLOADER,
            radioVersion = Build.getRadioVersion() ?: "N/A",
            kernelVersion = System.getProperty("os.version") ?: "N/A",
            isEmulator = isEmulator()
        )
    }

    private fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || "google_sdk" == Build.PRODUCT)
    }

    // ===== 字体配置信息 =====

    data class FontInfo(
        val fontScale: Float,          // 1.0 (系统字体缩放)
        val density: Float,            // 屏幕密度
        val scaledDensity: Float,      // 缩放后的密度
        val smallFontSize: Float,      // 小字体大小 (sp)
        val mediumFontSize: Float,     // 中等字体大小 (sp)
        val largeFontSize: Float       // 大字体大小 (sp)
    )

    fun getFontInfo(context: Context): FontInfo {
        val configuration = context.resources.configuration
        val displayMetrics = context.resources.displayMetrics

        // 获取系统字体大小配置
        val fontScale = configuration.fontScale

        return FontInfo(
            fontScale = fontScale,
            density = displayMetrics.density,
            scaledDensity = displayMetrics.scaledDensity,
            smallFontSize = 12f,
            mediumFontSize = 14f,
            largeFontSize = 18f
        )
    }

    // ===== 存储信息 =====

    data class StorageInfo(
        val totalInternalStorage: String,    // 内部存储总容量
        val availableInternalStorage: String, // 内部存储可用空间
        val totalExternalStorage: String,     // 外部存储总容量
        val availableExternalStorage: String, // 外部存储可用空间
        val isExternalStorageAvailable: Boolean,
        val isExternalStorageRemovable: Boolean
    )

    fun getStorageInfo(context: Context): StorageInfo {
        val internalPath = Environment.getDataDirectory()
        val externalPath = Environment.getExternalStorageDirectory()

        val internalStat = android.os.StatFs(internalPath.path)
        val externalStat = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            android.os.StatFs(externalPath.path)
        } else {
            null
        }

        val totalInternal = internalStat.totalBytes
        val availableInternal = internalStat.availableBytes

        val totalExternal = externalStat?.totalBytes ?: 0L
        val availableExternal = externalStat?.availableBytes ?: 0L

        return StorageInfo(
            totalInternalStorage = formatSize(totalInternal),
            availableInternalStorage = formatSize(availableInternal),
            totalExternalStorage = if (totalExternal > 0) formatSize(totalExternal) else "N/A",
            availableExternalStorage = if (availableExternal > 0) formatSize(availableExternal) else "N/A",
            isExternalStorageAvailable = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED,
            isExternalStorageRemovable = Environment.isExternalStorageRemovable()
        )
    }

    private fun formatSize(bytes: Long): String {
        val kb = 1024L
        val mb = kb * 1024
        val gb = mb * 1024

        return when {
            bytes >= gb -> String.format("%.1f GB", bytes.toFloat() / gb)
            bytes >= mb -> String.format("%.1f MB", bytes.toFloat() / mb)
            bytes >= kb -> String.format("%.1f KB", bytes.toFloat() / kb)
            else -> "$bytes B"
        }
    }
}
