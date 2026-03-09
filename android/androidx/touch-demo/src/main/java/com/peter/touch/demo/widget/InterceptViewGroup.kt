package com.peter.touch.demo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.graphics.toColorInt

/**
 * 可配置拦截策略的自定义ViewGroup
 * 用于演示 onInterceptTouchEvent 的工作机制
 */
class InterceptViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // 拦截配置
    var interceptDown: Boolean = false
    var interceptMove: Boolean = false
    var interceptUp: Boolean = false
    
    // 是否消费事件
    var consumeEvent: Boolean = false
    
    // 日志回调
    var logCallback: ((tag: String, method: String, action: String, result: String) -> Unit)? = null
    
    // 标签名称
    var tagName: String = "ViewGroup"
    
    // 背景色
    var bgColor: Int = "#E3F2FD".toColorInt()
        set(value) {
            field = value
            invalidate()
        }

    // 绘制相关
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = bgColor
    }
    
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = "#2196F3".toColorInt()
    }
    
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 36f
        color = Color.DKGRAY
        textAlign = Paint.Align.CENTER
    }

    // 触摸状态
    private var isPressed = false
    private val touchRect = Rect()

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val action = getActionString(ev.actionMasked)
        val result = super.dispatchTouchEvent(ev)
        
        logCallback?.invoke(tagName, "dispatchTouchEvent", action, if (result) "true" else "false")
        
        return result
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = getActionString(ev.actionMasked)
        val shouldIntercept = when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> interceptDown
            MotionEvent.ACTION_MOVE -> interceptMove
            MotionEvent.ACTION_UP -> interceptUp
            else -> false
        }
        
        logCallback?.invoke(tagName, "onInterceptTouchEvent", action, if (shouldIntercept) "true" else "false")
        
        return shouldIntercept
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = getActionString(event.actionMasked)
        
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isPressed = true
                invalidate()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isPressed = false
                invalidate()
            }
        }
        
        val result = consumeEvent
        logCallback?.invoke(tagName, "onTouchEvent", action, if (result) "true" else "false")
        
        return result
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // 绘制背景
        bgPaint.color = if (isPressed) {
            "#BBDEFB".toColorInt()
        } else {
            bgColor
        }
        
        val rect = Rect(0, 0, width, height)
        canvas.drawRect(rect, bgPaint)
        canvas.drawRect(rect, borderPaint)
        
        // 绘制标签
        canvas.drawText(tagName, width / 2f, 60f, textPaint)
    }

    private fun getActionString(action: Int): String {
        return when (action) {
            MotionEvent.ACTION_DOWN -> "DOWN"
            MotionEvent.ACTION_MOVE -> "MOVE"
            MotionEvent.ACTION_UP -> "UP"
            MotionEvent.ACTION_CANCEL -> "CANCEL"
            MotionEvent.ACTION_POINTER_DOWN -> "POINTER_DOWN"
            MotionEvent.ACTION_POINTER_UP -> "POINTER_UP"
            else -> "UNKNOWN($action)"
        }
    }
}

/**
 * 可配置消费策略的自定义View
 * 用于演示 onTouchEvent 的工作机制
 */
class TouchTestView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 是否消费事件
    var consumeEvent: Boolean = true
    
    // 日志回调
    var logCallback: ((tag: String, method: String, action: String, result: String) -> Unit)? = null
    
    // 标签名称
    var tagName: String = "View"
    
    // 背景色
    var bgColor: Int = "#FFF3E0".toColorInt()
        set(value) {
            field = value
            invalidate()
        }

    // 绘制相关
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = bgColor
    }
    
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = "#FF9800".toColorInt()
    }
    
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 32f
        color = Color.DKGRAY
        textAlign = Paint.Align.CENTER
    }

    // 触摸状态
    private var isPressed = false

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val action = getActionString(event.actionMasked)
        val result = super.dispatchTouchEvent(event)
        
        logCallback?.invoke(tagName, "dispatchTouchEvent", action, if (result) "true" else "false")
        
        return result
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = getActionString(event.actionMasked)
        
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isPressed = true
                invalidate()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isPressed = false
                invalidate()
            }
        }
        
        val result = consumeEvent
        logCallback?.invoke(tagName, "onTouchEvent", action, if (result) "true" else "false")
        
        return result
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // 绘制背景
        bgPaint.color = if (isPressed) {
            "#FFE0B2".toColorInt()
        } else {
            bgColor
        }
        
        val rect = Rect(0, 0, width, height)
        canvas.drawRect(rect, bgPaint)
        canvas.drawRect(rect, borderPaint)
        
        // 绘制标签
        canvas.drawText(tagName, width / 2f, height / 2f, textPaint)
        canvas.drawText(if (consumeEvent) "消费: 开" else "消费: 关", width / 2f, height / 2f + 40f, textPaint)
    }

    private fun getActionString(action: Int): String {
        return when (action) {
            MotionEvent.ACTION_DOWN -> "DOWN"
            MotionEvent.ACTION_MOVE -> "MOVE"
            MotionEvent.ACTION_UP -> "UP"
            MotionEvent.ACTION_CANCEL -> "CANCEL"
            MotionEvent.ACTION_POINTER_DOWN -> "POINTER_DOWN"
            MotionEvent.ACTION_POINTER_UP -> "POINTER_UP"
            else -> "UNKNOWN($action)"
        }
    }
}
