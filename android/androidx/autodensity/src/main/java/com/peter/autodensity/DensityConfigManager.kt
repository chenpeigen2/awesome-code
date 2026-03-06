package com.peter.autodensity

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Process
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.WindowManager

/**
 * 密度配置管理器
 * 负责计算和管理设备的密度配置
 */
class DensityConfigManager private constructor() {

    companion object {
        // 标准DPI基准 (以常见手机为基准)
        private const val STANDARD_DPI = 440f
        private const val STANDARD_PPI = 386f
        private const val STANDARD_SCALE = STANDARD_DPI / STANDARD_PPI

        @Volatile
        private var sInstance: DensityConfigManager? = null

        fun getInstance(): DensityConfigManager {
            return sInstance ?: synchronized(this) {
                sInstance ?: DensityConfigManager().also { sInstance = it }
            }
        }
    }

    // 用户自定义配置
    private var mUserDeviceScale = 0f
    private var mUserPPI = 0

    // 计算后的配置
    private var mDeviceScale = 0f
    private var mPPI = 0

    // 原始和目标配置
    var originConfig: DisplayConfig? = null
        private set
    var targetConfig: DisplayConfig? = null
        private set

    // 屏幕尺寸
    private val mPhysicalScreenSize = Point()
    private val mScreenSize = Point()

    // 是否启用自动密度
    var isAutoDensityEnabled = true
        private set

    /**
     * 设置用户自定义设备缩放值
     * 需要在 init 之前调用
     */
    fun setUserDeviceScale(deviceScale: Float) {
        mUserDeviceScale = deviceScale
    }

    /**
     * 设置用户自定义PPI
     * 需要在 init 之前调用
     */
    fun setUserPPI(ppi: Int) {
        mUserPPI = ppi
    }

    /**
     * 获取当前设备缩放值
     */
    fun getCurrentDeviceScale(): Float = mDeviceScale

    /**
     * 获取当前PPI
     */
    fun getCurrentPPI(): Int = mPPI

    /**
     * 初始化配置
     */
    fun init(context: Context) {
        targetConfig = DisplayConfig(context.resources.configuration)
        DebugUtil.printDensityLog("DensityConfigManager init")
        updateConfig(context, context.resources.configuration)
    }

    /**
     * 尝试更新配置
     * @return true 表示配置已更新，false 表示配置未变化
     */
    fun tryUpdateConfig(context: Context, newConfig: Configuration): Boolean {
        DebugUtil.printDensityLog("tryUpdateConfig newConfig $newConfig")

        val origin = originConfig ?: run {
            updateConfig(context, newConfig)
            return true
        }

        // 检查配置是否变化
        if (newConfig.screenWidthDp != origin.windowWidthDp
            || newConfig.screenHeightDp != origin.windowHeightDp
            || newConfig.densityDpi != origin.densityDpi
            || newConfig.fontScale != origin.fontScale
        ) {
            updateConfig(context, newConfig)
            return true
        }

        DebugUtil.printDensityLog("tryUpdateConfig: config not changed")
        return false
    }

    /**
     * 更新配置
     */
    fun updateConfig(context: Context, newConfig: Configuration) {
        DebugUtil.printDensityLog("DensityConfigManager updateConfig $newConfig")

        originConfig = DisplayConfig(newConfig)
        updatePhysicalSize(context)

        val ppi = updatePPIOfDevice(context)
        val deviceScale = updateDeviceScale(context)
        val targetDpi = (ppi * STANDARD_SCALE * deviceScale).toInt()
        val scale = targetDpi * 1.0f / newConfig.densityDpi

        targetConfig?.apply {
            this.defaultBitmapDensity = targetDpi
            this.densityDpi = targetDpi
            this.density = originConfig!!.density * scale
            this.scaledDensity = originConfig!!.scaledDensity * scale
            this.fontScale = originConfig!!.fontScale * scale
            this.windowWidthDp = originConfig!!.windowWidthDp
            this.windowHeightDp = originConfig!!.windowHeightDp
        }

        DebugUtil.printDensityLog("Config changed. Raw($originConfig) Target($targetConfig)")
    }

    /**
     * 更新物理屏幕尺寸
     */
    private fun updatePhysicalSize(context: Context) {
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            displayManager.getDisplay(Display.DEFAULT_DISPLAY)
        } else {
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        }

        updatePhysicalSizeFromDisplay(display)

        // 获取当前屏幕尺寸
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val outPoint = Point()
            display.getRealSize(outPoint)
            mScreenSize.set(outPoint.x, outPoint.y)
        } else {
            display.getSize(mScreenSize)
        }

        DebugUtil.printDensityLog("Physical size: $mPhysicalScreenSize, Screen size: $mScreenSize")
    }

    /**
     * 从Display获取物理尺寸
     */
    private fun updatePhysicalSizeFromDisplay(display: Display) {
        mPhysicalScreenSize.set(0, 0)
        val modes = display.supportedModes
        // 遍历所有模式取最大值作为真实屏幕分辨率
        for (mode in modes) {
            DebugUtil.printDensityLog("Display mode: $mode")
            mPhysicalScreenSize.x = maxOf(mode.physicalWidth, mPhysicalScreenSize.x)
            mPhysicalScreenSize.y = maxOf(mode.physicalHeight, mPhysicalScreenSize.y)
        }
    }

    /**
     * 更新设备PPI
     */
    private fun updatePPIOfDevice(context: Context): Int {
        // 使用用户自定义PPI
        if (mUserPPI > 0) {
            mPPI = mUserPPI
            return mUserPPI
        }

        val dm = context.resources.displayMetrics
        DebugUtil.printDensityLog("Physical size: $mPhysicalScreenSize, Screen size: $mScreenSize, xdpi: ${dm.xdpi}, ydpi: ${dm.ydpi}")

        val longDpi = maxOf(dm.xdpi, dm.ydpi)
        val shortDpi = minOf(dm.xdpi, dm.ydpi)
        val phyLongPoint = maxOf(mPhysicalScreenSize.x, mPhysicalScreenSize.y).toFloat()
        val phyShortPoint = minOf(mPhysicalScreenSize.x, mPhysicalScreenSize.y).toFloat()
        val longPoint = maxOf(mScreenSize.x, mScreenSize.y).toFloat()
        val shortPoint = minOf(mScreenSize.x, mScreenSize.y).toFloat()

        // 计算物理屏幕尺寸(英寸)
        val physicalX = phyLongPoint / longDpi
        val physicalY = phyShortPoint / shortDpi
        val screenInches = kotlin.math.sqrt((physicalX * physicalX + physicalY * physicalY).toDouble())

        // 计算PPI
        mPPI = (kotlin.math.sqrt((longPoint * longPoint + shortPoint * shortPoint).toDouble()) / screenInches).toInt()

        DebugUtil.printDensityLog("Screen inches: $screenInches, PPI: $mPPI, physicalX: $physicalX, physicalY: $physicalY")
        return mPPI
    }

    /**
     * 更新设备缩放值
     */
    private fun updateDeviceScale(context: Context): Float {
        // 检查调试模式
        val debugScale = getDebugScale()
        if (debugScale < 0) {
            isAutoDensityEnabled = false
            Log.d(DebugUtil.TAG, "Auto density disabled in debug mode")
        } else {
            isAutoDensityEnabled = true
        }

        val deviceScale = if (debugScale > 0) {
            debugScale
        } else {
            getDeviceScale(context)
        }

        return deviceScale * getAccessibilityDelta(context)
    }

    /**
     * 获取调试缩放值
     */
    private fun getDebugScale(): Float {
        return if (RootUtil.isDeviceRooted()) {
            DebugUtil.getDebugScale()
        } else {
            0f
        }
    }

    /**
     * 获取设备缩放值
     */
    private fun getDeviceScale(context: Context): Float {
        // 使用用户自定义缩放值
        if (mUserDeviceScale > 0) {
            mDeviceScale = mUserDeviceScale
            return mUserDeviceScale
        }

        // 根据设备类型计算缩放值
        val scale = calcDeviceScale(context)
        DebugUtil.printDensityLog("Device scale: $scale")
        mDeviceScale = scale
        return scale
    }

    /**
     * 计算设备缩放值
     * 可根据不同设备类型(手机/平板/折叠屏)进行定制
     */
    private fun calcDeviceScale(context: Context): Float {
        val minSizeInch = getMinSizeInch(context)
        val maxSizeInch = getMaxSizeInch(context)

        return when {
            // 平板设备
            isTablet(context) -> {
                val scale = maxSizeInch / 9.3f
                maxOf(1f, minOf(scale * 1.06f, 1.15f))
            }
            // 小屏手机
            minSizeInch < 2.7f -> {
                minSizeInch / 2.8f
            }
            // 普通手机
            else -> 1.0f
        }
    }

    /**
     * 获取无障碍设置带来的缩放增量
     */
    private fun getAccessibilityDelta(context: Context): Float {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && Process.isIsolated()) {
            Log.d(DebugUtil.TAG, "getAccessibilityDelta: isolated process")
            return 1f
        }

        val defaultDpi = getDeviceDefaultDpi()
        DebugUtil.printDensityLog("Default DPI: $defaultDpi")

        if (defaultDpi == -1) return 1f

        return try {
            // 尝试读取无障碍设置中的显示大小
            val accessibilityDpi = try {
                android.provider.Settings.Secure.getInt(
                    context.contentResolver,
                    "display_density_forced"
                )
            } catch (e: Exception) {
                defaultDpi
            }

            val delta = accessibilityDpi * 1f / defaultDpi
            DebugUtil.printDensityLog("Accessibility DPI: $accessibilityDpi, Delta: $delta")
            delta
        } catch (e: Exception) {
            Log.d(DebugUtil.TAG, "getAccessibilityDelta exception: $e")
            1f
        }
    }

    /**
     * 获取最小屏幕尺寸(英寸)
     */
    private fun getMinSizeInch(context: Context): Float {
        val dm = context.resources.displayMetrics
        val longDpi = maxOf(dm.xdpi, dm.ydpi)
        val shortDpi = minOf(dm.xdpi, dm.ydpi)
        val phyLongPoint = maxOf(mPhysicalScreenSize.x, mPhysicalScreenSize.y).toFloat()
        val phyShortPoint = minOf(mPhysicalScreenSize.x, mPhysicalScreenSize.y).toFloat()

        val physicalX = phyLongPoint / longDpi
        val physicalY = phyShortPoint / shortDpi
        return minOf(physicalY, physicalX)
    }

    /**
     * 获取最大屏幕尺寸(英寸)
     */
    private fun getMaxSizeInch(context: Context): Float {
        val dm = context.resources.displayMetrics
        val longDpi = maxOf(dm.xdpi, dm.ydpi)
        val shortDpi = minOf(dm.xdpi, dm.ydpi)
        val phyLongPoint = maxOf(mPhysicalScreenSize.x, mPhysicalScreenSize.y).toFloat()
        val phyShortPoint = minOf(mPhysicalScreenSize.x, mPhysicalScreenSize.y).toFloat()

        val physicalX = phyLongPoint / longDpi
        val physicalY = phyShortPoint / shortDpi
        return maxOf(physicalY, physicalX)
    }

    /**
     * 判断是否为平板设备
     */
    private fun isTablet(context: Context): Boolean {
        val configuration = context.resources.configuration
        return (configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    /**
     * 获取设备默认DPI
     */
    fun getDeviceDefaultDpi(): Int {
        return SystemProperties.getInt("ro.sf.lcd_density", originConfig?.densityDpi ?: -1)
    }

    /**
     * 获取设备当前默认DPI (考虑分辨率切换)
     */
    fun getDeviceCurrentDefaultDpi(): Int {
        val initialDensity = getDeviceDefaultDpi()

        val longPoint = maxOf(mScreenSize.x, mScreenSize.y).toFloat()
        val phyLongPoint = maxOf(mPhysicalScreenSize.x, mPhysicalScreenSize.y).toFloat()

        if (longPoint == phyLongPoint) {
            return initialDensity
        }

        // 可变分辨率设备需要按比例计算
        return Math.round(
            initialDensity *
                    minOf(mScreenSize.x, mScreenSize.y) * 1.0f /
                    minOf(mPhysicalScreenSize.x, mPhysicalScreenSize.y)
        )
    }
}
