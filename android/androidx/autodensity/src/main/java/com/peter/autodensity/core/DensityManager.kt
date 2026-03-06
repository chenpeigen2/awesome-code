package com.peter.autodensity.core

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import com.peter.autodensity.api.DensityConfig

/**
 * 密度管理器
 *
 * 负责管理全局密度配置和应用
 */
internal object DensityManager {

    private const val TAG = "DensityManager"

    private var isInitialized = false
    private lateinit var app: Application
    private var config: DensityConfig = DensityConfig()

    // 用户设置
    private var userDesignWidth: Int = 0
    private var userFontScale: Float? = null

    // 缓存
    private var cachedResult: DensityCalculator.Result? = null
    private var cachedOriginal: DisplayInfo? = null

    /**
     * 初始化
     */
    fun initialize(application: Application, densityConfig: DensityConfig) {
        if (isInitialized) return

        app = application
        config = densityConfig
        isInitialized = true

        log("Initialized with config: $config")
    }

    /**
     * 设置设计稿宽度
     */
    fun setDesignWidth(widthDp: Int) {
        userDesignWidth = widthDp
        cachedResult = null // 清除缓存
        log("Design width set to: $widthDp dp")
    }

    /**
     * 设置字体缩放
     */
    fun setFontScale(scale: Float) {
        userFontScale = scale
        cachedResult = null // 清除缓存
        log("Font scale set to: $scale")
    }

    /**
     * 应用到 Activity
     */
    fun applyToActivity(activity: Activity, baseWidthDp: Int = 0) {
        val result = calculate(activity, baseWidthDp)

        DensityApplier.applyToActivity(activity, result)

        // 如果配置了更新系统资源
        if (config.updateSystemResources) {
            DensityApplier.applyToSystem(result)
        }

        // 缓存原始值用于恢复
        cachedOriginal = DisplayInfo.from(activity)
    }

    /**
     * 计算密度
     */
    private fun calculate(context: Context, baseWidthDp: Int): DensityCalculator.Result {
        // 检查缓存（相同的 context 可能会多次调用）
        cachedResult?.let {
            if (it.original == DisplayInfo.from(context)) {
                return it
            }
        }

        // 获取设计稿宽度
        val designWidth = if (userDesignWidth > 0) userDesignWidth else config.designWidthDp

        // 获取字体缩放
        val fontScale = userFontScale ?: config.fontScale

        // 计算
        val result = DensityCalculator.calculate(
            context = context,
            designWidthDp = designWidth,
            fontScale = fontScale,
            baseWidthDp = baseWidthDp
        )

        // 缓存
        cachedResult = result

        log("Calculated: $result")

        return result
    }

    /**
     * 获取调试信息
     */
    fun getDebugInfo(): String = buildString {
        appendLine("=== AutoDensity Debug Info ===")
        appendLine()
        appendLine("Config:")
        appendLine("  designWidthDp  = ${config.designWidthDp}")
        appendLine("  fontScale      = ${config.fontScale}")
        appendLine("  debug          = ${config.debug}")
        appendLine()
        appendLine("User Settings:")
        appendLine("  userDesignWidth = $userDesignWidth")
        appendLine("  userFontScale   = $userFontScale")
        appendLine()
        appendLine("Cached Result:")
        cachedResult?.let {
            append(it.toString())
        } ?: appendLine("  (not calculated yet)")
    }

    private fun log(message: String) {
        if (config.debug) {
            Log.d(TAG, message)
        }
    }
}
