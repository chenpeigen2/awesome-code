package com.peter.autodensity.core

import android.app.Activity
import android.content.Context
import android.util.Log

/**
 * 密度调试工具
 *
 * 用于打印修改前后的对比日志
 */
object DensityDebugger {

    private const val TAG = "DensityDebugger"

    /**
     * 打印初始化配置
     */
    fun printInitInfo(
        configDesignWidthDp: Int,
        userDesignWidth: Int,
        userFontScale: Float?,
        configFontScale: Float
    ) {
        val actualDesignWidth = if (userDesignWidth > 0) userDesignWidth else configDesignWidthDp
        val actualFontScale = userFontScale ?: configFontScale

        Log.d(TAG, "")
        Log.d(TAG, "╔══════════════════════════════════════════════════════════╗")
        Log.d(TAG, "║               AutoDensity 初始化配置                      ║")
        Log.d(TAG, "╠══════════════════════════════════════════════════════════╣")
        Log.d(TAG, "║  config.designWidthDp = $configDesignWidthDp dp")
        Log.d(TAG, "║  userDesignWidth      = $userDesignWidth dp")
        Log.d(TAG, "║  config.fontScale     = $configFontScale")
        Log.d(TAG, "║  userFontScale        = $userFontScale")
        Log.d(TAG, "╠──────────────────────────────────────────────────────────╣")
        Log.d(TAG, "║  → 实际使用的 designWidth = $actualDesignWidth dp")
        Log.d(TAG, "║  → 实际使用的 fontScale   = $actualFontScale")
        Log.d(TAG, "╚══════════════════════════════════════════════════════════╝")
        Log.d(TAG, "")
    }

    /**
     * 打印 Activity 处理信息
     */
    fun printActivityHandle(activityName: String, shouldAdapt: Boolean) {
        Log.d(TAG, "╔══════════════════════════════════════════════════════════╗")
        Log.d(TAG, "║  Activity 创建: $activityName")
        Log.d(TAG, "║  shouldAdaptDensity = $shouldAdapt")
        Log.d(TAG, "╚══════════════════════════════════════════════════════════╝")
    }

    /**
     * 打印计算参数
     */
    fun printCalculateParams(
        activityName: String,
        configDesignWidthDp: Int,
        userDesignWidth: Int,
        actualDesignWidth: Int,
        fontScale: Float?,
        baseWidthDp: Int,
        forceDesignWidth: Boolean = false
    ) {
        Log.d(TAG, "")
        Log.d(TAG, "╔══════════════════════════════════════════════════════════╗")
        Log.d(TAG, "║  计算参数: $activityName")
        Log.d(TAG, "╠══════════════════════════════════════════════════════════╣")
        Log.d(TAG, "║  config.designWidthDp = $configDesignWidthDp dp")
        Log.d(TAG, "║  userDesignWidth      = $userDesignWidth dp")
        Log.d(TAG, "║  实际 designWidth     = $actualDesignWidth dp")
        Log.d(TAG, "║  fontScale            = $fontScale")
        Log.d(TAG, "║  baseWidthDp          = $baseWidthDp dp")
        Log.d(TAG, "║  forceDesignWidth     = $forceDesignWidth")
        Log.d(TAG, "╚══════════════════════════════════════════════════════════╝")
    }

    /**
     * 打印原始密度信息（修改前）
     */
    fun printOriginal(context: Context, label: String = "") {
        val info = DisplayInfo.from(context)
        val prefix = if (label.isNotEmpty()) "[$label] " else ""

        Log.d(TAG, "╔══════════════════════════════════════════════════════════╗")
        Log.d(TAG, "║  ${prefix}原始密度 (修改前)")
        Log.d(TAG, "╠══════════════════════════════════════════════════════════╣")
        Log.d(TAG, "║  density        = ${info.density}")
        Log.d(TAG, "║  densityDpi     = ${info.densityDpi}")
        Log.d(TAG, "║  scaledDensity  = ${info.scaledDensity}")
        Log.d(TAG, "║  fontScale      = ${info.fontScale}")
        Log.d(TAG, "╠──────────────────────────────────────────────────────────╣")
        Log.d(TAG, "║  屏幕尺寸       = ${info.widthPixels} x ${info.heightPixels} px")
        Log.d(TAG, "║  屏幕尺寸       = ${String.format("%.2f", info.widthDp)} x ${String.format("%.2f", info.heightDp)} dp")
        Log.d(TAG, "╚══════════════════════════════════════════════════════════╝")
    }

    /**
     * 打印修改后的密度信息
     */
    fun printModified(context: Context, result: DensityCalculator.Result, label: String = "") {
        val prefix = if (label.isNotEmpty()) "[$label] " else ""

        Log.d(TAG, "╔══════════════════════════════════════════════════════════╗")
        Log.d(TAG, "║  ${prefix}目标密度 (修改后)")
        Log.d(TAG, "╠══════════════════════════════════════════════════════════╣")
        Log.d(TAG, "║  targetDensity       = ${result.targetDensity}")
        Log.d(TAG, "║  targetDpi           = ${result.targetDpi}")
        Log.d(TAG, "║  targetScaledDensity = ${result.targetScaledDensity}")
        Log.d(TAG, "║  scale               = ${String.format("%.4f", result.scale)}")
        Log.d(TAG, "╚══════════════════════════════════════════════════════════╝")

        // 打印对比
        printComparison(result, label)
    }

    /**
     * 打印对比信息
     */
    private fun printComparison(result: DensityCalculator.Result, label: String) {
        val original = result.original
        val prefix = if (label.isNotEmpty()) "[$label] " else ""

        Log.d(TAG, "")
        Log.d(TAG, "╔══════════════════════════════════════════════════════════╗")
        Log.d(TAG, "║  ${prefix}对比 (原始 → 目标)")
        Log.d(TAG, "╠══════════════════════════════════════════════════════════╣")
        Log.d(TAG, "║  density:       ${original.density} → ${result.targetDensity}")
        Log.d(TAG, "║  densityDpi:    ${original.densityDpi} → ${result.targetDpi}")
        Log.d(TAG, "║  scaledDensity: ${original.scaledDensity} → ${result.targetScaledDensity}")
        Log.d(TAG, "╠──────────────────────────────────────────────────────────╣")
        Log.d(TAG, "║  屏幕宽度:      ${String.format("%.2f", original.widthDp)} dp → ${String.format("%.2f", original.widthPixels / result.targetDensity)} dp")
        Log.d(TAG, "╚══════════════════════════════════════════════════════════╝")
        Log.d(TAG, "")
    }

    /**
     * 打印完整的修改过程（原始 → 目标 → 实际）
     */
    fun printFullComparison(activity: Activity, result: DensityCalculator.Result, designWidthDp: Int) {
        val original = result.original
        val currentDm = activity.resources.displayMetrics
        val currentWidthDp = currentDm.widthPixels / currentDm.density

        Log.d(TAG, "")
        Log.d(TAG, "╔════════════════════════════════════════════════════════════════════╗")
        Log.d(TAG, "║                    AutoDensity 调试信息                            ║")
        Log.d(TAG, "╠════════════════════════════════════════════════════════════════════╣")
        Log.d(TAG, "║  配置: designWidthDp = $designWidthDp dp")
        Log.d(TAG, "║  公式: targetDensity = screenWidth(px) / designWidthDp")
        Log.d(TAG, "║       = ${original.widthPixels} / $designWidthDp = ${String.format("%.4f", original.widthPixels.toFloat() / designWidthDp)}")
        Log.d(TAG, "╠════════════════════════════════════════════════════════════════════╣")
        Log.d(TAG, "║")
        Log.d(TAG, "║  ┌─────────────────────────────────────────────────────────────┐")
        Log.d(TAG, "║  │  原始值 (修改前)                                            │")
        Log.d(TAG, "║  ├─────────────────────────────────────────────────────────────┤")
        Log.d(TAG, "║  │  density        = ${String.format("%-12s", original.density)}                      │")
        Log.d(TAG, "║  │  densityDpi     = ${String.format("%-12s", original.densityDpi)}                      │")
        Log.d(TAG, "║  │  scaledDensity  = ${String.format("%-12s", original.scaledDensity)}                      │")
        Log.d(TAG, "║  │  屏幕宽度       = ${String.format("%-12s", "${original.widthPixels}px")}                      │")
        Log.d(TAG, "║  │  屏幕宽度       = ${String.format("%-12s", "${String.format("%.2f", original.widthDp)}dp")}                      │")
        Log.d(TAG, "║  └─────────────────────────────────────────────────────────────┘")
        Log.d(TAG, "║")
        Log.d(TAG, "║  ┌─────────────────────────────────────────────────────────────┐")
        Log.d(TAG, "║  │  目标值 (计算结果)                                          │")
        Log.d(TAG, "║  ├─────────────────────────────────────────────────────────────┤")
        Log.d(TAG, "║  │  targetDensity       = ${String.format("%-12s", result.targetDensity)}                  │")
        Log.d(TAG, "║  │  targetDpi           = ${String.format("%-12s", result.targetDpi)}                  │")
        Log.d(TAG, "║  │  targetScaledDensity = ${String.format("%-12s", result.targetScaledDensity)}                  │")
        Log.d(TAG, "║  │  缩放比例            = ${String.format("%-12s", String.format("%.4f", result.scale))}                  │")
        Log.d(TAG, "║  └─────────────────────────────────────────────────────────────┘")
        Log.d(TAG, "║")
        Log.d(TAG, "║  ┌─────────────────────────────────────────────────────────────┐")
        Log.d(TAG, "║  │  当前实际值 (修改后读取)                                    │")
        Log.d(TAG, "║  ├─────────────────────────────────────────────────────────────┤")
        Log.d(TAG, "║  │  density        = ${String.format("%-12s", currentDm.density)}                      │")
        Log.d(TAG, "║  │  densityDpi     = ${String.format("%-12s", currentDm.densityDpi)}                      │")
        Log.d(TAG, "║  │  scaledDensity  = ${String.format("%-12s", currentDm.scaledDensity)}                      │")
        Log.d(TAG, "║  │  屏幕宽度       = ${String.format("%-12s", "${String.format("%.2f", currentWidthDp)}dp")}                      │")
        Log.d(TAG, "║  └─────────────────────────────────────────────────────────────┘")
        Log.d(TAG, "║")
        Log.d(TAG, "╠════════════════════════════════════════════════════════════════════╣")
        Log.d(TAG, "║  变化: ${original.density} → ${currentDm.density} (density)")
        Log.d(TAG, "║       ${original.densityDpi} → ${currentDm.densityDpi} (densityDpi)")
        Log.d(TAG, "║       ${String.format("%.2f", original.widthDp)}dp → ${String.format("%.2f", currentWidthDp)}dp (屏幕宽度)")
        Log.d(TAG, "╚════════════════════════════════════════════════════════════════════╝")
        Log.d(TAG, "")
    }

    /**
     * 打印"无需更新"的详细信息
     */
    fun printNoUpdateNeeded(activity: Activity, result: DensityCalculator.Result, designWidthDp: Int) {
        val original = result.original

        Log.d(TAG, "")
        Log.d(TAG, "╔════════════════════════════════════════════════════════════════════╗")
        Log.d(TAG, "║              ⚠️  AutoDensity - 无需更新                            ║")
        Log.d(TAG, "╠════════════════════════════════════════════════════════════════════╣")
        Log.d(TAG, "║  Activity: ${activity.javaClass.simpleName}")
        Log.d(TAG, "║  designWidthDp = $designWidthDp")
        Log.d(TAG, "╠════════════════════════════════════════════════════════════════════╣")
        Log.d(TAG, "║")
        Log.d(TAG, "║  计算过程:")
        Log.d(TAG, "║  targetDensity = screenWidth / designWidthDp")
        Log.d(TAG, "║               = ${original.widthPixels} / $designWidthDp")
        Log.d(TAG, "║               = ${String.format("%.4f", result.targetDensity)}")
        Log.d(TAG, "║")
        Log.d(TAG, "║  ┌─────────────────────────────────────────────────────────────┐")
        Log.d(TAG, "║  │  原始值 vs 目标值                                          │")
        Log.d(TAG, "║  ├─────────────────────────────────────────────────────────────┤")
        Log.d(TAG, "║  │  density:        ${original.density} vs ${result.targetDensity}")
        Log.d(TAG, "║  │  scaledDensity:  ${original.scaledDensity} vs ${result.targetScaledDensity}")
        Log.d(TAG, "║  │  差值(density):  ${String.format("%.6f", Math.abs(original.density - result.targetDensity))}")
        Log.d(TAG, "║  │  差值(scaled):   ${String.format("%.6f", Math.abs(original.scaledDensity - result.targetScaledDensity))}")
        Log.d(TAG, "║  └─────────────────────────────────────────────────────────────┘")
        Log.d(TAG, "║")
        Log.d(TAG, "║  原因: 差值 < 0.001，认为无需更新")
        Log.d(TAG, "║")
        Log.d(TAG, "╚════════════════════════════════════════════════════════════════════╝")
        Log.d(TAG, "")
    }

    /**
     * 打印系统资源更新
     */
    fun printSystemResourcesUpdated() {
        Log.d(TAG, "╔══════════════════════════════════════════════════════════╗")
        Log.d(TAG, "║  System Resources 已更新                                 ║")
        Log.d(TAG, "╚══════════════════════════════════════════════════════════╝")
    }

    /**
     * 打印恢复原始密度
     */
    fun printRestored() {
        Log.d(TAG, "╔══════════════════════════════════════════════════════════╗")
        Log.d(TAG, "║  密度已恢复到原始值                                       ║")
        Log.d(TAG, "╚══════════════════════════════════════════════════════════╝")
    }

    /**
     * 打印配置改变
     */
    fun printConfigChange(activityName: String) {
        Log.d(TAG, "╔══════════════════════════════════════════════════════════╗")
        Log.d(TAG, "║  Configuration 改变: $activityName")
        Log.d(TAG, "║  重新计算并应用密度...                                   ║")
        Log.d(TAG, "╚══════════════════════════════════════════════════════════╝")
    }

    /**
     * 打印 Context 更新
     */
    fun printContextUpdated(contextName: String) {
        Log.d(TAG, "╔══════════════════════════════════════════════════════════╗")
        Log.d(TAG, "║  Context 密度已应用: $contextName")
        Log.d(TAG, "╚══════════════════════════════════════════════════════════╝")
    }

    /**
     * 打印 Service 密度应用
     */
    fun printServiceApply(serviceName: String, designWidth: Int, fontScale: Float?, forceDesignWidth: Boolean) {
        Log.d(TAG, "")
        Log.d(TAG, "╔══════════════════════════════════════════════════════════╗")
        Log.d(TAG, "║  Service 密度应用: $serviceName")
        Log.d(TAG, "╠══════════════════════════════════════════════════════════╣")
        Log.d(TAG, "║  designWidth     = $designWidth dp")
        Log.d(TAG, "║  fontScale       = $fontScale")
        Log.d(TAG, "║  forceDesignWidth= $forceDesignWidth")
        Log.d(TAG, "║  baseWidthDp     = 0 (Service 无限制)")
        Log.d(TAG, "╚══════════════════════════════════════════════════════════╝")
        Log.d(TAG, "")
    }
}
