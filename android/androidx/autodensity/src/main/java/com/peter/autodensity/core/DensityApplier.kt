package com.peter.autodensity.core

import android.app.Activity
import android.content.Context
import android.content.res.Resources

/**
 * 密度应用器
 *
 * 负责将计算好的密度值应用到 Resources
 */
internal object DensityApplier {

    /**
     * 应用到 Activity
     */
    fun applyToActivity(activity: Activity, result: DensityCalculator.Result, designWidthDp: Int = 0) {
        if (!result.needsUpdate()) {
            // 打印详细的无更新信息
            DensityDebugger.printNoUpdateNeeded(activity, result, designWidthDp)
            return
        }

        // 打印修改前的原始信息
        DensityDebugger.printOriginal(activity, activity.javaClass.simpleName)

        // 应用到 Activity 的 resources
        applyToResources(activity.resources, result)

        // 打印修改后的信息和完整对比
        DensityDebugger.printModified(activity, result, activity.javaClass.simpleName)

        if (designWidthDp > 0) {
            DensityDebugger.printFullComparison(activity, result, designWidthDp)
        }
    }

    /**
     * 应用到系统 Resources
     */
    fun applyToSystem(result: DensityCalculator.Result) {
        applyToResources(Resources.getSystem(), result)
        DensityDebugger.printSystemResourcesUpdated()
    }

    /**
     * 应用到任意 Context（Service 等）
     */
    fun applyToContext(context: Context, result: DensityCalculator.Result) {
        applyToResources(context.resources, result)
        DensityDebugger.printContextUpdated(context.javaClass.simpleName)
    }

    /**
     * 应用到指定 Resources
     */
    private fun applyToResources(resources: Resources, result: DensityCalculator.Result) {
        val metrics = resources.displayMetrics
        val config = resources.configuration

        // 更新 DisplayMetrics
        metrics.density = result.targetDensity
        metrics.scaledDensity = result.targetScaledDensity
        metrics.densityDpi = result.targetDpi

        // 更新 Configuration
        config.densityDpi = result.targetDpi
    }

    /**
     * 恢复到原始密度
     */
    fun restore(context: Context, original: DisplayInfo) {
        val metrics = context.resources.displayMetrics
        val config = context.resources.configuration

        metrics.density = original.density
        metrics.scaledDensity = original.scaledDensity
        metrics.densityDpi = original.densityDpi
        config.densityDpi = original.densityDpi

        DensityDebugger.printRestored()
    }
}
