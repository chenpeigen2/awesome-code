package com.peter.touch.demo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.sqrt
import androidx.core.graphics.toColorInt

/**
 * 触摸可视化视图
 * 绘制触摸点、轨迹、事件流向箭头
 */
class TouchVisualizerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 触摸点数据
    private data class TouchPoint(
        val id: Int,
        val x: Float,
        val y: Float,
        val isCurrent: Boolean = true
    )

    // 触摸轨迹
    private data class TouchTrail(
        val id: Int,
        val points: MutableList<PointF> = mutableListOf()
    )

    private val touchPoints = mutableMapOf<Int, TouchPoint>()
    private val touchTrails = mutableMapOf<Int, TouchTrail>()
    private val trailPath = Path()

    // 画笔
    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    
    private val trailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 32f
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
    }

    // 颜色数组
    private val colors = intArrayOf(
        "#FF6B6B".toColorInt(),  // 红
        "#4ECDC4".toColorInt(),  // 青
        "#45B7D1".toColorInt(),  // 蓝
        "#96CEB4".toColorInt(),  // 绿
        "#FFEAA7".toColorInt(),  // 黄
        "#DDA0DD".toColorInt(),  // 紫
        "#FF8C00".toColorInt(),  // 橙
        "#00CED1".toColorInt(),  // 深青
        "#FF69B4".toColorInt(),  // 粉
        "#32CD32".toColorInt()   // 酸橙
    )

    // 是否显示轨迹
    var showTrails: Boolean = true
        set(value) {
            field = value
            invalidate()
        }

    // 是否显示ID标签
    var showLabels: Boolean = true
        set(value) {
            field = value
            invalidate()
        }

    // 轨迹最大长度
    var maxTrailLength: Int = 100
        set(value) {
            field = value
            invalidate()
        }

    // 清除所有触摸数据
    fun clearAll() {
        touchPoints.clear()
        touchTrails.clear()
        trailPath.reset()
        invalidate()
    }

    // 从外部添加触摸点（用于显示事件流向）
    fun addTouchPoint(id: Int, x: Float, y: Float) {
        touchPoints[id] = TouchPoint(id, x, y, true)
        
        // 添加到轨迹
        val trail = touchTrails.getOrPut(id) { TouchTrail(id) }
        trail.points.add(PointF(x, y))
        
        // 限制轨迹长度
        if (trail.points.size > maxTrailLength) {
            trail.points.removeAt(0)
        }
        
        invalidate()
    }

    // 移除触摸点
    fun removeTouchPoint(id: Int) {
        touchPoints.remove(id)
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                handleActionDown(event)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                handleActionPointerDown(event)
            }
            MotionEvent.ACTION_MOVE -> {
                handleActionMove(event)
            }
            MotionEvent.ACTION_POINTER_UP -> {
                handleActionPointerUp(event)
            }
            MotionEvent.ACTION_UP -> {
                handleActionUp(event)
            }
            MotionEvent.ACTION_CANCEL -> {
                handleActionCancel()
            }
        }
        return true
    }

    private fun handleActionDown(event: MotionEvent) {
        clearAll()
        val id = event.getPointerId(0)
        addTouchPoint(id, event.x, event.y)
    }

    private fun handleActionPointerDown(event: MotionEvent) {
        val index = event.actionIndex
        val id = event.getPointerId(index)
        addTouchPoint(id, event.getX(index), event.getY(index))
    }

    private fun handleActionMove(event: MotionEvent) {
        for (i in 0 until event.pointerCount) {
            val id = event.getPointerId(i)
            addTouchPoint(id, event.getX(i), event.getY(i))
        }
    }

    private fun handleActionPointerUp(event: MotionEvent) {
        val index = event.actionIndex
        val id = event.getPointerId(index)
        removeTouchPoint(id)
    }

    private fun handleActionUp(event: MotionEvent) {
        val id = event.getPointerId(0)
        removeTouchPoint(id)
    }

    private fun handleActionCancel() {
        clearAll()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // 绘制轨迹
        if (showTrails) {
            touchTrails.forEach { (id, trail) ->
                if (trail.points.size > 1) {
                    trailPaint.color = getColorForId(id)
                    trailPath.reset()
                    trailPath.moveTo(trail.points[0].x, trail.points[0].y)
                    for (i in 1 until trail.points.size) {
                        trailPath.lineTo(trail.points[i].x, trail.points[i].y)
                    }
                    canvas.drawPath(trailPath, trailPaint)
                }
            }
        }
        
        // 绘制触摸点
        touchPoints.forEach { (id, point) ->
            val color = getColorForId(id)
            pointPaint.color = color
            
            // 绘制外圈（半透明）
            pointPaint.alpha = 80
            canvas.drawCircle(point.x, point.y, 60f, pointPaint)
            
            // 绘制内圈（实心）
            pointPaint.alpha = 255
            canvas.drawCircle(point.x, point.y, 30f, pointPaint)
            
            // 绘制ID标签
            if (showLabels) {
                canvas.drawText(
                    "P$id",
                    point.x,
                    point.y - 70f,
                    textPaint
                )
            }
        }
        
        // 显示触摸点数量
        if (touchPoints.isNotEmpty()) {
            canvas.drawText(
                "触摸点: ${touchPoints.size}",
                width / 2f,
                60f,
                textPaint.apply { color = Color.DKGRAY }
            )
        }
    }

    private fun getColorForId(id: Int): Int {
        return colors[id % colors.size]
    }

    /**
     * 绘制事件流向箭头
     * @param fromX 起点X
     * @param fromY 起点Y
     * @param toX 终点X
     * @param toY 终点Y
     * @param label 标签文字
     */
    fun drawEventFlowArrow(
        canvas: Canvas,
        fromX: Float,
        fromY: Float,
        toX: Float,
        toY: Float,
        label: String,
        color: Int = Color.parseColor("#FF6B6B")
    ) {
        val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            strokeWidth = 3f
            style = Paint.Style.STROKE
        }
        
        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 24f
            this.color = color
            textAlign = Paint.Align.CENTER
        }
        
        // 绘制线条
        canvas.drawLine(fromX, fromY, toX, toY, arrowPaint)
        
        // 计算箭头
        val arrowSize = 20f
        val angle = Math.atan2((toY - fromY).toDouble(), (toX - fromX).toDouble())
        
        val arrowX1 = toX - arrowSize * Math.cos(angle - Math.PI / 6).toFloat()
        val arrowY1 = toY - arrowSize * Math.sin(angle - Math.PI / 6).toFloat()
        val arrowX2 = toX - arrowSize * Math.cos(angle + Math.PI / 6).toFloat()
        val arrowY2 = toY - arrowSize * Math.sin(angle + Math.PI / 6).toFloat()
        
        // 绘制箭头
        canvas.drawLine(toX, toY, arrowX1, arrowY1, arrowPaint)
        canvas.drawLine(toX, toY, arrowX2, arrowY2, arrowPaint)
        
        // 绘制标签
        val midX = (fromX + toX) / 2
        val midY = (fromY + toY) / 2
        canvas.drawText(label, midX, midY - 10, labelPaint)
    }
}
