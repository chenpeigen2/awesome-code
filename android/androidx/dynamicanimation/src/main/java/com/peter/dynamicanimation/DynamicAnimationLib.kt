package com.peter.dynamicanimation

import android.view.View
import com.peter.dynamicanimation.animation.AnimationBuilder
import com.peter.dynamicanimation.animation.AnimationConfig
import com.peter.dynamicanimation.animation.PressAnimation

/**
 * DynamicAnimationLib
 * 简洁高效的 Android 动画库
 *
 * 提供以下功能：
 * - 简单的按压动效
 * - 通用动画构建器
 * - 多种预设插值器
 *
 * 使用示例：
 * ```kotlin
 * // 按压动画
 * PressAnimation.create(button).attachToView()
 *
 * // 通用动画
 * AnimationBuilder.on(view)
 *     .scale(1.2f)
 *     .alpha(0.8f)
 *     .duration(300)
 *     .start()
 * ```
 */
object DynamicAnimationLib {

    /**
     * 为 View 创建按压动画
     * @param view 目标 View
     * @param config 动画配置（可选）
     */
    fun pressAnimation(view: View, config: AnimationConfig = AnimationConfig()): PressAnimation {
        return PressAnimation.create(view, config)
    }

    /**
     * 为 View 创建动画构建器
     * @param view 目标 View
     */
    fun animate(view: View): AnimationBuilder {
        return AnimationBuilder.on(view)
    }

    /**
     * 快速创建按压动画并自动附加到 View
     * @param view 目标 View
     */
    fun enablePressAnimation(view: View) {
        PressAnimation.create(view).attachToView()
    }

    /**
     * 创建自定义按压动画并自动附加到 View
     * @param view 目标 View
     * @param block 配置块
     */
    fun enablePressAnimation(view: View, block: AnimationConfig.Builder.() -> Unit) {
        val config = AnimationConfig.Builder().apply(block).build()
        PressAnimation.create(view, config).attachToView()
    }
}