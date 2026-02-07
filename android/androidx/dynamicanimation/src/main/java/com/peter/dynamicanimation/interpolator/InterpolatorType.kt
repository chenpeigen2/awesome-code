package com.peter.dynamicanimation.interpolator

import android.view.animation.Interpolator

/**
 * 预定义的插值器类型
 * 用于快速选择常用的动画缓动效果
 */
enum class InterpolatorType(val displayName: String) {
    LINEAR("线性"),
    ACCELERATE("加速"),
    DECELERATE("减速"),
    ACCELERATE_DECELERATE("加速减速"),
    ANTICIPATE("回弹"),
    OVERSHOOT("超调"),
    ANTICIPATE_OVERSHOOT("回弹超调"),
    BOUNCE("弹跳"),
    FAST_OUT_LINEAR_IN("快出线性入"),
    FAST_OUT_SLOW_IN("快出慢入"),
    LINEAR_OUT_SLOW_IN("线性出慢入"),
    EASE_IN_QUAD("二次缓入"),
    EASE_OUT_QUAD("二次缓出"),
    EASE_IN_OUT_QUAD("二次缓入缓出"),
    EASE_IN_CUBIC("三次缓入"),
    EASE_OUT_CUBIC("三次缓出"),
    EASE_IN_OUT_CUBIC("三次缓入缓出"),
    EASE_IN_ELASTIC("弹性缓入"),
    EASE_OUT_ELASTIC("弹性缓出"),
    EASE_IN_OUT_ELASTIC("弹性缓入缓出");

    companion object {
        fun fromDisplayName(name: String): InterpolatorType? =
            values().find { it.displayName == name }
    }
}