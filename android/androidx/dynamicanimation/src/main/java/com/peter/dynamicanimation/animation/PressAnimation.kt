package com.peter.dynamicanimation.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import com.peter.dynamicanimation.interpolator.InterpolatorFactory

/**
 * 按压动画类
 * 提供简洁的按压动效实现
 */
class PressAnimation private constructor(
    private val view: View,
    private val config: AnimationConfig
) {

    private var currentAnimator: AnimatorSet? = null
    private var isPressed = false

    companion object {
        /**
         * 为 View 创建按压动画
         * @param view 目标 View
         * @param config 动画配置
         */
        fun create(view: View, config: AnimationConfig = AnimationConfig()): PressAnimation {
            return PressAnimation(view, config)
        }

        /**
         * 使用 Builder 模式创建按压动画
         * @param view 目标 View
         */
        fun builder(view: View): AnimationConfig.Builder {
            val config = AnimationConfig.Builder()
            return config
        }
    }

    /**
     * 执行按下动画
     */
    fun press() {
        if (isPressed || currentAnimator?.isRunning == true) return
        isPressed = true

        val scaleDown = ObjectAnimator.ofFloat(view, View.SCALE_X, config.scaleDown)
        val scaleDownY = ObjectAnimator.ofFloat(view, View.SCALE_Y, config.scaleDown)
        val alphaDown = ObjectAnimator.ofFloat(view, View.ALPHA, config.alphaPressed)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleDown, scaleDownY, alphaDown)
        animatorSet.duration = config.duration
        animatorSet.interpolator = InterpolatorFactory.create(config.interpolatorType)
        animatorSet.startDelay = config.startDelay

        currentAnimator = animatorSet
        animatorSet.start()
    }

    /**
     * 执行释放动画
     */
    fun release() {
        if (!isPressed) return
        isPressed = false

        val scaleUp = ObjectAnimator.ofFloat(view, View.SCALE_X, config.scaleUp)
        val scaleUpY = ObjectAnimator.ofFloat(view, View.SCALE_Y, config.scaleUp)
        val alphaUp = ObjectAnimator.ofFloat(view, View.ALPHA, config.alphaNormal)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleUp, scaleUpY, alphaUp)
        animatorSet.duration = config.duration
        animatorSet.interpolator = InterpolatorFactory.create(config.interpolatorType)
        animatorSet.startDelay = config.startDelay

        currentAnimator = animatorSet
        animatorSet.start()

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                currentAnimator = null
            }
        })
    }

    /**
     * 取消当前动画
     */
    fun cancel() {
        currentAnimator?.cancel()
        currentAnimator = null
        isPressed = false
    }

    /**
     * 为 View 设置触摸监听，自动处理按压动画
     */
    fun attachToView() {
        view.setOnTouchListener { _, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    press()
                    false
                }
                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                    release()
                    false
                }
                else -> false
            }
        }
    }

    /**
     * 为 View 设置点击监听，同时处理按压动画
     */
    fun setOnClickListener(listener: (View) -> Unit) {
        view.setOnClickListener {
            press()
            view.postDelayed({ release() }, config.duration / 2)
            listener(it)
        }
    }
}