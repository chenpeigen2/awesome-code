package com.peter.dynamicanimation.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.view.View
import android.view.animation.Interpolator
import com.peter.dynamicanimation.interpolator.InterpolatorFactory
import com.peter.dynamicanimation.interpolator.InterpolatorType

/**
 * 通用动画构建器
 * 支持多种动画属性的链式配置
 */
class AnimationBuilder private constructor(private val view: View) {

    private val animators = mutableListOf<Animator>()
    private var duration: Long = 300L
    private var interpolator: TimeInterpolator = InterpolatorFactory.create(InterpolatorType.FAST_OUT_SLOW_IN)
    private var startDelay: Long = 0L
    private var onAnimationEnd: (() -> Unit)? = null
    private var onAnimationStart: (() -> Unit)? = null
    private var onAnimationCancel: (() -> Unit)? = null
    private var onAnimationRepeat: (() -> Unit)? = null

    companion object {
        /**
         * 为 View 创建动画构建器
         */
        fun on(view: View): AnimationBuilder {
            return AnimationBuilder(view)
        }
    }

    /**
     * 设置动画持续时间
     */
    fun duration(duration: Long): AnimationBuilder = apply { this.duration = duration }

    /**
     * 设置插值器类型
     */
    fun interpolator(type: InterpolatorType): AnimationBuilder = apply {
        this.interpolator = InterpolatorFactory.create(type)
    }

    /**
     * 设置自定义插值器
     */
    fun interpolator(interpolator: Interpolator): AnimationBuilder = apply {
        this.interpolator = interpolator
    }

    /**
     * 设置启动延迟
     */
    fun startDelay(delay: Long): AnimationBuilder = apply { this.startDelay = delay }

    /**
     * X 轴平移动画
     */
    fun translationX(by: Float): AnimationBuilder = apply {
        animators.add(ObjectAnimator.ofFloat(view, View.TRANSLATION_X, by))
    }

    /**
     * Y 轴平移动画
     */
    fun translationY(by: Float): AnimationBuilder = apply {
        animators.add(ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, by))
    }

    /**
     * X 轴缩放动画
     */
    fun scaleX(to: Float): AnimationBuilder = apply {
        animators.add(ObjectAnimator.ofFloat(view, View.SCALE_X, to))
    }

    /**
     * Y 轴缩放动画
     */
    fun scaleY(to: Float): AnimationBuilder = apply {
        animators.add(ObjectAnimator.ofFloat(view, View.SCALE_Y, to))
    }

    /**
     * 同时缩放 X 和 Y 轴
     */
    fun scale(to: Float): AnimationBuilder = apply {
        animators.add(ObjectAnimator.ofFloat(view, View.SCALE_X, to))
        animators.add(ObjectAnimator.ofFloat(view, View.SCALE_Y, to))
    }

    /**
     * 旋转动画
     */
    fun rotation(to: Float): AnimationBuilder = apply {
        animators.add(ObjectAnimator.ofFloat(view, View.ROTATION, to))
    }

    /**
     * X 轴旋转动画
     */
    fun rotationX(to: Float): AnimationBuilder = apply {
        animators.add(ObjectAnimator.ofFloat(view, View.ROTATION_X, to))
    }

    /**
     * Y 轴旋转动画
     */
    fun rotationY(to: Float): AnimationBuilder = apply {
        animators.add(ObjectAnimator.ofFloat(view, View.ROTATION_Y, to))
    }

    /**
     * 透明度动画
     */
    fun alpha(to: Float): AnimationBuilder = apply {
        animators.add(ObjectAnimator.ofFloat(view, View.ALPHA, to))
    }

    /**
     * 动画开始回调
     */
    fun onAnimationStart(callback: () -> Unit): AnimationBuilder = apply {
        this.onAnimationStart = callback
    }

    /**
     * 动画结束回调
     */
    fun onAnimationEnd(callback: () -> Unit): AnimationBuilder = apply {
        this.onAnimationEnd = callback
    }

    /**
     * 动画取消回调
     */
    fun onAnimationCancel(callback: () -> Unit): AnimationBuilder = apply {
        this.onAnimationCancel = callback
    }

    /**
     * 动画重复回调
     */
    fun onAnimationRepeat(callback: () -> Unit): AnimationBuilder = apply {
        this.onAnimationRepeat = callback
    }

    /**
     * 启动动画
     */
    fun start(): AnimatorSet {
        if (animators.isEmpty()) {
            throw IllegalStateException("No animation properties specified")
        }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animators)
        animatorSet.duration = duration
        animatorSet.interpolator = interpolator
        animatorSet.startDelay = startDelay

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                onAnimationStart?.invoke()
            }

            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd?.invoke()
            }

            override fun onAnimationCancel(animation: Animator) {
                onAnimationCancel?.invoke()
            }

            override fun onAnimationRepeat(animation: Animator) {
                onAnimationRepeat?.invoke()
            }
        })

        animatorSet.start()
        return animatorSet
    }

    /**
     * 依次播放多个动画
     */
    fun playSequentially(): AnimatorSet {
        if (animators.isEmpty()) {
            throw IllegalStateException("No animation properties specified")
        }

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(animators)
        animatorSet.duration = duration
        animatorSet.interpolator = interpolator
        animatorSet.startDelay = startDelay

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                onAnimationStart?.invoke()
            }

            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd?.invoke()
            }

            override fun onAnimationCancel(animation: Animator) {
                onAnimationCancel?.invoke()
            }

            override fun onAnimationRepeat(animation: Animator) {
                onAnimationRepeat?.invoke()
            }
        })

        animatorSet.start()
        return animatorSet
    }
}