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

    fun calculate(
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
