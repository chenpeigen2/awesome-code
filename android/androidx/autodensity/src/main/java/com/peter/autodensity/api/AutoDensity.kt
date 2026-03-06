@file:Suppress("unused")

package com.peter.autodensity.api

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.peter.autodensity.core.DensityDebugger
import com.peter.autodensity.core.DensityManager
import com.peter.autodensity.core.DisplayInfo

/**
 * AutoDensity - Android 屏幕密度适配库
 *
 * 核心原理：
 * 1. 修改 DisplayMetrics 的 density/scaledDensity/densityDpi
 * 2. 让所有使用 dp/sp 的布局和文字按新的密度计算
 * 3. 实现不同屏幕尺寸的统一显示效果
 *
 * 使用示例：
 * ```kotlin
 * class MyApp : Application(), DensityAware {
 *     override fun onCreate() {
 *         super.onCreate()
 *         AutoDensity.init(this)
 *     }
 *     override fun shouldAdaptDensity() = true
 * }
 * ```
 */
object AutoDensity {

    private var isInitialized = false
    private lateinit var app: Application

    /**
     * 初始化密度适配
     */
    fun init(application: Application, densityConfig: DensityConfig = DensityConfig()) {
        if (isInitialized) return

        this.app = application
        DensityManager.initialize(application, densityConfig)
        isInitialized = true

        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                handleActivityCreate(activity)
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    /**
     * 设置设计稿宽度（dp）
     */
    fun setDesignWidth(widthDp: Int) {
        DensityManager.setDesignWidth(widthDp)
    }

    /**
     * 设置字体缩放比例
     */
    fun setFontScale(scale: Float) {
        DensityManager.setFontScale(scale)
    }

    /**
     * 设置是否强制使用 designWidthDp（忽略 baseWidthDp 限制）
     */
    fun setForceDesignWidth(force: Boolean) {
        DensityManager.setForceDesignWidth(force)
    }

    /**
     * 手动刷新 Activity 的密度
     */
    fun refresh(activity: Activity) {
        val forceDesignWidth = DensityManager.getConfig().forceDesignWidth
        DensityManager.calculate(activity, 0, forceDesignWidth)
    }

    /**
     * 获取调试信息
     */
    fun getDebugInfo(): String = DensityManager.getDebugInfo()

    /**
     * 获取最近一次计算结果
     */
    fun getLastResult() = DensityManager.getLastResult()

    private fun handleActivityCreate(activity: Activity) {
        val appInstance = app
        val shouldAdapt = when {
            activity is ActivityDensityAware -> activity.shouldAdaptDensity()
            activity is DensityAware -> activity.shouldAdaptDensity()
            appInstance is DensityAware -> appInstance.shouldAdaptDensity()
            else -> true
        }

        DensityDebugger.printActivityHandle(activity.javaClass.simpleName, shouldAdapt)

        if (shouldAdapt) {
            val baseWidthDp = resolveBaseWidthDp(activity)
            val forceDesignWidth = resolveForceDesignWidth(activity)
            DensityManager.calculate(activity, baseWidthDp, forceDesignWidth)
        }
    }

    private fun resolveBaseWidthDp(activity: Activity): Int {
        return when {
            activity is ActivityDensityAware -> {
                val activityBase = activity.getBaseWidthDp()
                when {
                    activityBase == -1 -> DisplayInfo.from(activity).widthDp.toInt()
                    activityBase > 0 -> activityBase
                    else -> 0
                }
            }
            else -> 0
        }
    }

    private fun resolveForceDesignWidth(activity: Activity): Boolean {
        val config = DensityManager.getConfig()
        return when {
            activity is ActivityDensityAware -> activity.forceDesignWidth()
            else -> config.forceDesignWidth
        }
    }
}
