package com.peter.androidx


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class RedGlowCircleView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.FILL
        // 使用 setShadowLayer 替代 BlurMaskFilter
        setShadowLayer(30f, 0f, 0f, Color.argb(80, 255, 0, 0))
    }

    override fun onDraw(canvas: Canvas) {
        val cx = width / 2f
        val cy = height / 2f
        val radius = min(cx, cy) * 0.6f

        // 直接画实心圆，阴影会自动绘制
        canvas.drawCircle(cx, cy, radius * 0.75f, innerPaint)
    }

    // 重要：启用硬件加速以支持阴影效果
    init {
        // 在 XML 中设置 android:layerType="software" 或代码中设置
        // setLayerType(LAYER_TYPE_SOFTWARE, null)  // 如果阴影不显示，取消注释这行
    }
}
