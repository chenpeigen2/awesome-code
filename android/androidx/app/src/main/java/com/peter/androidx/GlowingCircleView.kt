package com.peter.androidx

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

/**
 * 带光晕效果的圆形View
 * 实现红色球体周围的渐变光晕效果
 */
class GlowingCircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 主圆的画笔
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#E53935") // 红色
    }

    // 光晕的画笔（使用径向渐变）
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    // 圆心坐标
    private var centerX = 0f
    private var centerY = 0f

    // 主圆半径
    private var mainRadius = 0f

    // 光晕半径（主圆的1.8-2倍）
    private var glowRadius = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        centerX = w / 2f
        centerY = h / 2f

        // 计算半径，留出足够空间给光晕
        val availableRadius = min(w, h) / 2f
        mainRadius = availableRadius * 0.6f // 主圆占可用空间的50%
        glowRadius = availableRadius * 0.95f // 光晕占95%，留出边距

        // 创建径向渐变着色器
        val glowShader = RadialGradient(
            centerX,
            centerY,
            glowRadius,
            intArrayOf(
                Color.parseColor("#FFE53935"), // 中心红色，不透明
                Color.parseColor("#99E53935"), // 60%透明度
                Color.parseColor("#66E53935"), // 40%透明度
                Color.parseColor("#33E53935"), // 20%透明度
                Color.parseColor("#00E53935")  // 完全透明
            ),
            floatArrayOf(0f, 0.5f, 0.7f, 0.85f, 1f), // 渐变位置
            Shader.TileMode.CLAMP
        )
        glowPaint.shader = glowShader
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 先绘制光晕
        canvas.drawCircle(centerX, centerY, glowRadius, glowPaint)

        // 再绘制主圆，覆盖在光晕之上
        canvas.drawCircle(centerX, centerY, mainRadius, circlePaint)
    }

    /**
     * 设置主圆颜色
     */
    fun setCircleColor(color: Int) {
        circlePaint.color = color
        invalidate()
    }

    /**
     * 更新光晕效果（可以传入自定义颜色）
     */
    fun updateGlowColor(baseColor: Int) {
        val glowShader = RadialGradient(
            centerX,
            centerY,
            glowRadius,
            intArrayOf(
                setAlpha(baseColor, 255),
                setAlpha(baseColor, 153),
                setAlpha(baseColor, 102),
                setAlpha(baseColor, 51),
                setAlpha(baseColor, 0)
            ),
            floatArrayOf(0f, 0.5f, 0.7f, 0.85f, 1f),
            Shader.TileMode.CLAMP
        )
        glowPaint.shader = glowShader
        circlePaint.color = baseColor
        invalidate()
    }

    /**
     * 设置颜色的透明度
     */
    private fun setAlpha(color: Int, alpha: Int): Int {
        return Color.argb(
            alpha,
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }
}