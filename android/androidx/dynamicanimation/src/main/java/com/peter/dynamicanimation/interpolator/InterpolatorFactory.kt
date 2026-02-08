package com.peter.dynamicanimation.interpolator

import android.animation.TimeInterpolator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnticipateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator

/**
 * 插值器工厂类
 * 提供预定义插值器的创建方法
 */
object InterpolatorFactory {

    /**
     * 根据类型创建插值器
     */
    fun create(type: InterpolatorType): TimeInterpolator = when (type) {
        InterpolatorType.LINEAR -> LinearInterpolator()
        InterpolatorType.ACCELERATE -> AccelerateInterpolator()
        InterpolatorType.DECELERATE -> DecelerateInterpolator()
        InterpolatorType.ACCELERATE_DECELERATE -> AccelerateDecelerateInterpolator()
        InterpolatorType.ANTICIPATE -> AnticipateInterpolator()
        InterpolatorType.OVERSHOOT -> OvershootInterpolator()
        InterpolatorType.ANTICIPATE_OVERSHOOT -> AnticipateOvershootInterpolator()
        InterpolatorType.BOUNCE -> BounceInterpolator()
        InterpolatorType.FAST_OUT_LINEAR_IN -> com.google.android.material.animation.AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR
        InterpolatorType.FAST_OUT_SLOW_IN -> com.google.android.material.animation.AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR
        InterpolatorType.LINEAR_OUT_SLOW_IN -> com.google.android.material.animation.AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR
        InterpolatorType.EASE_IN_QUAD -> EaseInQuadInterpolator()
        InterpolatorType.EASE_OUT_QUAD -> EaseOutQuadInterpolator()
        InterpolatorType.EASE_IN_OUT_QUAD -> EaseInOutQuadInterpolator()
        InterpolatorType.EASE_IN_CUBIC -> EaseInCubicInterpolator()
        InterpolatorType.EASE_OUT_CUBIC -> EaseOutCubicInterpolator()
        InterpolatorType.EASE_IN_OUT_CUBIC -> EaseInOutCubicInterpolator()
        InterpolatorType.EASE_IN_ELASTIC -> EaseInElasticInterpolator()
        InterpolatorType.EASE_OUT_ELASTIC -> EaseOutElasticInterpolator()
        InterpolatorType.EASE_IN_OUT_ELASTIC -> EaseInOutElasticInterpolator()
    }

    /**
     * 自定义插值器
     */
    fun custom(interpolator: Interpolator): Interpolator = interpolator
}

/**
 * 二次缓入插值器
 */
private class EaseInQuadInterpolator : Interpolator {
    override fun getInterpolation(input: Float): Float = input * input
}

/**
 * 二次缓出插值器
 */
private class EaseOutQuadInterpolator : Interpolator {
    override fun getInterpolation(input: Float): Float = input * (2 - input)
}

/**
 * 二次缓入缓出插值器
 */
private class EaseInOutQuadInterpolator : Interpolator {
    override fun getInterpolation(input: Float): Float {
        return if (input < 0.5f) {
            2f * input * input
        } else {
            -1f + (4f - 2f * input) * input
        }
    }
}

/**
 * 三次缓入插值器
 */
private class EaseInCubicInterpolator : Interpolator {
    override fun getInterpolation(input: Float): Float = input * input * input
}

/**
 * 三次缓出插值器
 */
private class EaseOutCubicInterpolator : Interpolator {
    override fun getInterpolation(input: Float): Float {
        val f = input - 1f
        return f * f * f + 1f
    }
}

/**
 * 三次缓入缓出插值器
 */
private class EaseInOutCubicInterpolator : Interpolator {
    override fun getInterpolation(input: Float): Float {
        return if (input < 0.5f) {
            4f * input * input * input
        } else {
            val f = input - 1f
            1f + 4f * f * f * f
        }
    }
}

/**
 * 弹性缓入插值器
 */
private class EaseInElasticInterpolator : Interpolator {
    override fun getInterpolation(input: Float): Float {
        if (input == 0f || input == 1f) return input
        return -Math.pow(2.0, (10 * input - 10).toDouble()).toFloat() *
                Math.sin((input * 10 - 10.75) * (2 * Math.PI / 3)).toFloat()
    }
}

/**
 * 弹性缓出插值器
 */
private class EaseOutElasticInterpolator : Interpolator {
    override fun getInterpolation(input: Float): Float {
        if (input == 0f || input == 1f) return input
        return Math.pow(2.0, (-10 * input).toDouble()).toFloat() *
                Math.sin((input * 10 - 0.75) * (2 * Math.PI / 3)).toFloat() + 1f
    }
}

/**
 * 弹性缓入缓出插值器
 */
private class EaseInOutElasticInterpolator : Interpolator {
    override fun getInterpolation(input: Float): Float {
        if (input == 0f || input == 1f) return input
        return if (input < 0.5f) {
            -(Math.pow(2.0, (20 * input - 10).toDouble()).toFloat() *
                    Math.sin((input * 20 - 11.125) * (2 * Math.PI / 4.5)).toFloat()) * 0.5f
        } else {
            Math.pow(2.0, (-20 * input + 10).toDouble()).toFloat() *
                    Math.sin((input * 20 - 11.125) * (2 * Math.PI / 4.5)).toFloat() * 0.5f + 1f
        }
    }
}