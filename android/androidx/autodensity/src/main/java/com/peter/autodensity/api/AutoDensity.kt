@file:Suppress("unused")

package com.peter.autodensity.api

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.peter.autodensity.core.DensityApplier
import com.peter.autodensity.core.DensityCalculator
import com.peter.autodensity.core.DensityDebugger
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
    private var config: DensityConfig = DensityConfig()

    // 用户设置
    private var userDesignWidth: Int = 0
    private var userFontScale: Float? = null
    private var userForceDesignWidth: Boolean? = null

    // 缓存最近一次计算结果
    private var lastResult: DensityCalculator.Result? = null

    /**
     * 初始化密度适配
     */
    fun init(application: Application, densityConfig: DensityConfig = DensityConfig()) {
        if (isInitialized) return

        this.app = application
        this.config = densityConfig
        this.isInitialized = true

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
        userDesignWidth = widthDp
    }

    /**
     * 设置字体缩放比例
     */
    fun setFontScale(scale: Float) {
        userFontScale = scale
    }

    /**
     * 设置是否强制使用 designWidthDp（忽略 baseWidthDp 限制）
     *
     * @param force true = 强制使用 designWidthDp，不做任何限制
     */
    fun setForceDesignWidth(force: Boolean) {
        userForceDesignWidth = force
    }

    /**
     * 手动刷新 Activity 的密度
     */
    fun refresh(activity: Activity) {
        val forceDesignWidth = userForceDesignWidth ?: config.forceDesignWidth
        applyToActivity(activity, 0, forceDesignWidth)
    }

    /**
     * 获取调试信息
     */
    fun getDebugInfo(): String {
        val original = DisplayInfo.from(app)
        return """
            |AutoDensity Debug Info:
            |Original: $original
            |Config: $config
            |UserDesignWidth: $userDesignWidth
            |UserFontScale: $userFontScale
        """.trimMargin()
    }

    /**
     * 获取最近一次计算结果
     * 包含原始值和目标值的对比
     */
    fun getLastResult(): DensityCalculator.Result? = lastResult

    private fun handleActivityCreate(activity: Activity) {
        // 检查是否需要适配
        val appInstance = app  // 捕获到局部变量
        val shouldAdapt = when {
            activity is ActivityDensityAware -> activity.shouldAdaptDensity()
            activity is DensityAware -> activity.shouldAdaptDensity()
            appInstance is DensityAware -> appInstance.shouldAdaptDensity()
            else -> true
        }

        DensityDebugger.printActivityHandle(activity.javaClass.simpleName, shouldAdapt)

        if (shouldAdapt) {
            // 获取 baseWidthDp
            val baseWidthDp = when {
                activity is ActivityDensityAware -> {
                    val activityBase = activity.getBaseWidthDp()
                    when {
                        activityBase == -1 -> {
                            // -1 表示使用屏幕实际 dp 宽度
                            val info = DisplayInfo.from(activity)
                            info.widthDp.toInt()
                        }
                        activityBase > 0 -> activityBase
                        else -> 0  // 0 表示不限制
                    }
                }
                else -> 0
            }

            // 获取 forceDesignWidth
            val forceDesignWidth = when {
                userForceDesignWidth != null -> userForceDesignWidth!!
                activity is ActivityDensityAware -> activity.forceDesignWidth()
                else -> config.forceDesignWidth
            }

            applyToActivity(activity, baseWidthDp, forceDesignWidth)
        }
    }

    internal fun applyToActivity(activity: Activity, baseWidthDp: Int, forceDesignWidth: Boolean) {
        val designWidth = if (userDesignWidth > 0) userDesignWidth else config.designWidthDp
        val fontScale = userFontScale ?: config.fontScale

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

        // 缓存结果
        lastResult = result

        DensityApplier.applyToActivity(activity, result, designWidth)

        if (config.updateSystemResources) {
            DensityApplier.applyToSystem(result)
        }
    }
}
