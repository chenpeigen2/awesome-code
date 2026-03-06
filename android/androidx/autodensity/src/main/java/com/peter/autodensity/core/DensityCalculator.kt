package com.peter.autodensity.core

import android.content.Context
import kotlin.math.abs

/**
 * 密度计算器
 *
 * 核心算法：
 * ```
 * targetDensity = screenWidth / designWidthDp
 * ```
 *
 * 例如：
 * - 屏幕宽度 1080px，设计稿宽度 360dp
 * - targetDensity = 1080 / 360 = 3.0
 * - 这样 360dp 的宽度刚好填满屏幕
 */
object DensityCalculator {

    /**
     * 计算目标密度
     *
     * @param context Context
     * @param designWidthDp 设计稿宽度（dp）
     * @param fontScale 字体缩放比例，null 表示保持原始比例
     * @param baseWidthDp 基准宽度（用于限制界面过大），0 表示不启用
     * @param forceDesignWidth 是否强制使用 designWidthDp（忽略 baseWidthDp 限制）
     * @return 计算结果
     */
    fun calculate(
        context: Context,
        designWidthDp: Int,
        fontScale: Float? = null,
        baseWidthDp: Int = 0,
        forceDesignWidth: Boolean = false
    ): Result {
        // 获取原始显示信息
        val original = DisplayInfo.from(context)

        // 计算目标密度
        // targetDensity = screenWidth / designWidthDp
        val targetDensity = original.widthPixels.toFloat() / designWidthDp

        // 计算缩放比例
        val scale = targetDensity / original.density

        // 计算目标 scaledDensity
        // scaledDensity = density * fontScale
        val targetScaledDensity = when (fontScale) {
            null -> original.scaledDensity * scale
            else -> targetDensity * fontScale
        }

        // 计算目标 DPI
        // densityDpi = density * 160
        val targetDpi = (targetDensity * 160).toInt()

        // 处理限制（防止界面过大）
        val finalDensity: Float
        val finalScaledDensity: Float
        val finalDpi: Int

        // 如果强制使用 designWidthDp，跳过限制逻辑
        if (forceDesignWidth || baseWidthDp <= 0) {
            finalDensity = targetDensity
            finalScaledDensity = targetScaledDensity
            finalDpi = targetDpi
        } else {
            val currentWidthDp = original.widthPixels / targetDensity
            if (currentWidthDp < baseWidthDp) {
                val ratio = currentWidthDp / baseWidthDp
                finalDensity = targetDensity * ratio
                finalScaledDensity = targetScaledDensity * ratio
                finalDpi = (targetDpi * ratio).toInt()
            } else {
                finalDensity = targetDensity
                finalScaledDensity = targetScaledDensity
                finalDpi = targetDpi
            }
        }

        return Result(
            original = original,
            targetDensity = finalDensity,
            targetScaledDensity = finalScaledDensity,
            targetDpi = finalDpi,
            scale = finalDensity / original.density,
            baseWidthDp = baseWidthDp,
            forceDesignWidth = forceDesignWidth
        )
    }

    /**
     * 计算结果
     */
    data class Result(
        /** 原始显示信息 */
        val original: DisplayInfo,

        /** 目标 density */
        val targetDensity: Float,

        /** 目标 scaledDensity */
        val targetScaledDensity: Float,

        /** 目标 DPI */
        val targetDpi: Int,

        /** 缩放比例 */
        val scale: Float,

        /** 基准宽度 */
        val baseWidthDp: Int = 0,

        /** 是否强制使用 designWidthDp */
        val forceDesignWidth: Boolean = false
    ) {
        /**
         * 是否需要更新
         */
        fun needsUpdate(): Boolean {
            return abs(targetDensity - original.density) > 0.001f ||
                    abs(targetScaledDensity - original.scaledDensity) > 0.001f
        }

        /**
         * 转换为可读字符串
         */
        override fun toString(): String = buildString {
            appendLine("DensityCalculator.Result:")
            appendLine("  original:")
            appendLine("    density       = ${original.density}")
            appendLine("    scaledDensity = ${original.scaledDensity}")
            appendLine("    densityDpi    = ${original.densityDpi}")
            appendLine("  target:")
            appendLine("    density       = $targetDensity")
            appendLine("    scaledDensity = $targetScaledDensity")
            appendLine("    densityDpi    = $targetDpi")
            appendLine("  scale = $scale")
            appendLine("  baseWidthDp = $baseWidthDp")
            appendLine("  forceDesignWidth = $forceDesignWidth")
            appendLine("  needsUpdate = ${needsUpdate()}")
        }
    }
}
