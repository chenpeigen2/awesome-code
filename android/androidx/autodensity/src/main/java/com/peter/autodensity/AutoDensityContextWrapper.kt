package com.peter.autodensity

import android.content.Context
import android.content.res.Configuration
import android.view.ContextThemeWrapper

/**
 * 自动密度适配的Context包装器
 * 用于在Context层级处理密度适配
 */
class AutoDensityContextWrapper(base: Context, themeResId: Int = 0) : ContextThemeWrapper(base, themeResId) {

    private var originConfiguration: Configuration? = null

    /**
     * 设置原始Configuration
     */
    fun setOriginConfiguration(configuration: Configuration) {
        originConfiguration = Configuration(configuration)
    }

    /**
     * 获取原始Configuration
     */
    fun getOriginConfiguration(): Configuration? = originConfiguration

    /**
     * 恢复原始配置
     */
    fun restoreOriginConfig() {
        val origin = originConfiguration ?: return
        val currentConfig = resources.configuration
        currentConfig.setTo(origin)
        resources.displayMetrics.density = origin.densityDpi / 160f
        resources.displayMetrics.densityDpi = origin.densityDpi
        val fontScale = origin.fontScale
        resources.displayMetrics.scaledDensity =
            resources.displayMetrics.density * (if (fontScale != 0f) fontScale else 1.0f)
    }

    /**
     * 获取未覆盖的Configuration
     */
    fun getNoOverrideConfiguration(): Configuration {
        var contextWrapper: ContextThemeWrapper = this
        while (contextWrapper.baseContext is ContextThemeWrapper) {
            contextWrapper = contextWrapper.baseContext as ContextThemeWrapper
        }
        return contextWrapper.resources.configuration
    }
}
