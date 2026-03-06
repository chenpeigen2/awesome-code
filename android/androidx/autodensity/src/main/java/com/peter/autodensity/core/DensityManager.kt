package com.peter.autodensity.core

import android.app.Activity
import android.app.Application
import android.content.Context
import com.peter.autodensity.api.DensityConfig

/**
 * 密度管理器
 * 负责管理全局密度配置和计算
 */
internal object DensityManager {

    private var isInitialized = false
    private lateinit var app: Application
    private lateinit var config: DensityConfig

    // 用户设置
    private var userDesignWidth: Int = 0
    private var userFontScale: Float? = null
    private var userForceDesignWidth: Boolean? = null

    // 缓存
    private var lastResult: DensityCalculator.Result? = null

    fun initialize(application: Application, densityConfig: DensityConfig) {
        if (isInitialized) return
        app = application
        config = densityConfig
        isInitialized = true
    }

    fun setDesignWidth(widthDp: Int) {
        userDesignWidth = widthDp
    }

    fun setFontScale(scale: Float) {
        userFontScale = scale
    }

    fun setForceDesignWidth(force: Boolean) {
        userForceDesignWidth = force
    }

    fun getActualDesignWidth(): Int {
        return if (userDesignWidth > 0) userDesignWidth else config.designWidthDp
    }

    fun getActualFontScale(): Float? {
        return userFontScale ?: config.fontScale
    }

    fun getLastResult(): DensityCalculator.Result? = lastResult

    fun getConfig(): DensityConfig = config

    /**
     * 清除缓存（配置改变时调用）
     */
    fun clearCache() {
        lastResult = null
    }

    fun applyToActivity(
        activity: Activity,
        baseWidthDp: Int,
        forceDesignWidth: Boolean
    ): DensityCalculator.Result {
        val designWidth = getActualDesignWidth()
        val fontScale = getActualFontScale()

        DensityDebugger.printCalculateParams(
            activityName = activity.javaClass.simpleName,
            configDesignWidthDp = config.designWidthDp,
            userDesignWidth = userDesignWidth,
            actualDesignWidth = designWidth,
            fontScale = fontScale,
            baseWidthDp = baseWidthDp,
            forceDesignWidth = forceDesignWidth
        )

        val result = DensityCalculator.calculate(
            context = activity,
            designWidthDp = designWidth,
            fontScale = fontScale,
            baseWidthDp = baseWidthDp,
            forceDesignWidth = forceDesignWidth
        )

        lastResult = result

        DensityApplier.applyToActivity(activity, result, designWidth)

        if (config.updateSystemResources) {
            DensityApplier.applyToSystem(result)
        }

        return result
    }

    /**
     * 应用到任意 Context（Service 等）
     *
     * 注意：Service 不使用 baseWidthDp 限制
     * - Activity 有 baseWidthDp 来限制界面过大
     * - Service 没有这个限制，直接使用 designWidthDp 计算
     */
    fun applyToContext(context: Context, forceDesignWidth: Boolean) {
        val designWidth = getActualDesignWidth()
        val fontScale = getActualFontScale()
        // Service 不需要 baseWidthDp 限制，直接使用 0
        val baseWidthDp = 0

        DensityDebugger.printServiceApply(context.javaClass.simpleName, designWidth, fontScale, forceDesignWidth)

        val result = DensityCalculator.calculate(
            context = context,
            designWidthDp = designWidth,
            fontScale = fontScale,
            baseWidthDp = baseWidthDp,
            forceDesignWidth = forceDesignWidth
        )

        DensityApplier.applyToContext(context, result)
    }

    /**
     * 获取适配后的 DisplayMetrics
     */
    fun getAdaptedMetrics(context: Context, forceDesignWidth: Boolean): android.util.DisplayMetrics {
        val designWidth = getActualDesignWidth()
        val fontScale = getActualFontScale()

        val result = DensityCalculator.calculate(
            context = context,
            designWidthDp = designWidth,
            fontScale = fontScale,
            baseWidthDp = 0,
            forceDesignWidth = forceDesignWidth
        )

        return android.util.DisplayMetrics().apply {
            density = result.targetDensity
            scaledDensity = result.targetScaledDensity
            densityDpi = result.targetDpi
        }
    }

    fun getDebugInfo(): String {
        val original = DisplayInfo.from(app)
        return """
            |AutoDensity Debug Info:
            |Original: $original
            |Config: $config
            |UserDesignWidth: $userDesignWidth
            |UserFontScale: $userFontScale
            |UserForceDesignWidth: $userForceDesignWidth
        """.trimMargin()
    }
}
