package com.peter.dynamicanimation.animation

import com.peter.dynamicanimation.interpolator.InterpolatorType

/**
 * 动画配置类
 * 使用 Builder 模式构建动画参数
 */
data class AnimationConfig(
    val duration: Long = 300L,
    val interpolatorType: InterpolatorType = InterpolatorType.FAST_OUT_SLOW_IN,
    val startDelay: Long = 0L,
    val scaleDown: Float = 0.9f,
    val scaleUp: Float = 1.0f,
    val alphaNormal: Float = 1.0f,
    val alphaPressed: Float = 0.8f
) {
    class Builder {
        private var duration: Long = 300L
        private var interpolatorType: InterpolatorType = InterpolatorType.FAST_OUT_SLOW_IN
        private var startDelay: Long = 0L
        private var scaleDown: Float = 0.9f
        private var scaleUp: Float = 1.0f
        private var alphaNormal: Float = 1.0f
        private var alphaPressed: Float = 0.8f

        fun duration(duration: Long) = apply { this.duration = duration }
        fun interpolator(interpolatorType: InterpolatorType) = apply { this.interpolatorType = interpolatorType }
        fun startDelay(startDelay: Long) = apply { this.startDelay = startDelay }
        fun scaleDown(scaleDown: Float) = apply { this.scaleDown = scaleDown }
        fun scaleUp(scaleUp: Float) = apply { this.scaleUp = scaleUp }
        fun alphaNormal(alphaNormal: Float) = apply { this.alphaNormal = alphaNormal }
        fun alphaPressed(alphaPressed: Float) = apply { this.alphaPressed = alphaPressed }

        fun build(): AnimationConfig = AnimationConfig(
            duration, interpolatorType, startDelay, scaleDown, scaleUp, alphaNormal, alphaPressed
        )
    }
}