@file:Suppress("unused")

package com.peter.autodensity.api

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
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

    // 当前活跃的 Activity 列表（用于配置改变时重新适配）
    private val activeActivities = mutableSetOf<Activity>()

    /**
     * 初始化密度适配
     */
    fun init(application: Application, densityConfig: DensityConfig = DensityConfig()) {
        if (isInitialized) return

        this.app = application
        DensityManager.initialize(application, densityConfig)
        isInitialized = true

        // 注册 Activity 生命周期回调
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                handleActivityCreate(activity)
            }

            override fun onActivityStarted(activity: Activity) {
                activeActivities.add(activity)
            }

            override fun onActivityResumed(activity: Activity) {
                // 每次 Resume 时重新检查并应用（处理分辨率切换等不触发 onConfigurationChanged 的场景）
                handleActivityResume(activity)
            }

            override fun onActivityStopped(activity: Activity) {
                activeActivities.remove(activity)
            }

            override fun onActivityDestroyed(activity: Activity) {
                activeActivities.remove(activity)
            }
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        })

        // 注册全局配置改变回调（处理字体缩放等系统设置变化）
        application.registerComponentCallbacks(object : ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: Configuration) {
                handleGlobalConfigChange()
            }

            override fun onLowMemory() {}
            override fun onTrimMemory(level: Int) {}
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
        applyToActivity(activity, forceDesignWidth)
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
        val shouldAdapt = resolveShouldAdapt(activity)

        DensityDebugger.printActivityHandle(activity.javaClass.simpleName, shouldAdapt)

        if (shouldAdapt) {
            val forceDesignWidth = resolveForceDesignWidth(activity)
            applyToActivity(activity, forceDesignWidth)
        }
    }

    private fun handleActivityResume(activity: Activity) {
        // 每次 Resume 时重新检查屏幕参数，处理分辨率切换等场景
        val shouldAdapt = resolveShouldAdapt(activity)
        if (shouldAdapt) {
            // 检查屏幕参数是否变化
            val currentInfo = DisplayInfo.from(activity)
            val lastResult = DensityManager.getLastResult()

            // 如果屏幕宽度变化（分辨率切换、折叠屏等），重新计算
            if (lastResult == null ||
                lastResult.original.widthPixels != currentInfo.widthPixels ||
                lastResult.original.heightPixels != currentInfo.heightPixels) {

                DensityDebugger.printConfigChange("${activity.javaClass.simpleName} Resume - 检测到屏幕参数变化")

                // 清除缓存
                DensityManager.clearCache()

                // 重新应用
                val forceDesignWidth = resolveForceDesignWidth(activity)
                applyToActivity(activity, forceDesignWidth)
            }
        }
    }

    private fun handleGlobalConfigChange() {
        DensityDebugger.printConfigChange("全局配置改变")

        // 清除缓存，强制重新计算
        DensityManager.clearCache()

        // 重新适配所有活跃的 Activity
        activeActivities.forEach { activity ->
            val shouldAdapt = resolveShouldAdapt(activity)
            if (shouldAdapt) {
                val forceDesignWidth = resolveForceDesignWidth(activity)
                applyToActivity(activity, forceDesignWidth)
            }
        }
    }

    private fun applyToActivity(activity: Activity, forceDesignWidth: Boolean) {
        val baseWidthDp = resolveBaseWidthDp(activity)
        DensityManager.calculate(activity, baseWidthDp, forceDesignWidth)
    }

    private fun resolveShouldAdapt(activity: Activity): Boolean {
        val appInstance = app
        return when {
            activity is ActivityDensityAware -> activity.shouldAdaptDensity()
            activity is DensityAware -> activity.shouldAdaptDensity()
            appInstance is DensityAware -> appInstance.shouldAdaptDensity()
            else -> true
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
