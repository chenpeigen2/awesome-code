package com.peter.recyclerview.demo.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * 自定义滑动冲突处理的 RecyclerView
 * 
 * ============================================
 * 滑动冲突处理核心原理
 * ============================================
 * 
 * Android 事件分发机制：
 * 1. dispatchTouchEvent() - 分发事件
 * 2. onInterceptTouchEvent() - 拦截事件
 * 3. onTouchEvent() - 处理事件
 * 
 * 关键方法：
 * - requestDisallowInterceptTouchEvent(boolean)
 *   子 View 调用此方法请求父 View 不拦截事件
 * 
 * 滑动冲突判断依据：
 * 1. 滑动方向（水平 vs 垂直）
 * 2. 滑动距离
 * 3. 滑动速度
 * 4. View 是否可滑动（canScrollVertically/canScrollHorizontally）
 * 
 * ============================================
 * 使用场景
 * ============================================
 * 
 * 1. ViewPager 嵌套垂直 RecyclerView（已解决）
 * 2. 水平 RecyclerView 嵌套垂直 RecyclerView（需手动处理）
 * 3. 多层嵌套滑动（复杂场景）
 */
class CustomScrollRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "CustomScrollRV"
    }

    // 触摸点坐标
    private var initialX = 0f
    private var initialY = 0f
    
    // 最后一次触摸点坐标
    private var lastX = 0f
    private var lastY = 0f
    
    // 最小滑动距离
    private val touchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop
    
    // 是否正在拖拽
    private var isDragging = false
    
    // 滑动方向锁定
    private var isLockOrientation = false
    private var orientation = 0 // 0: 未确定, 1: 水平, 2: 垂直

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                // 记录初始位置
                initialX = ev.x
                initialY = ev.y
                lastX = ev.x
                lastY = ev.y
                
                // 重置状态
                isDragging = false
                isLockOrientation = false
                orientation = 0
                
                Log.d(TAG, "ACTION_DOWN: x=${ev.x}, y=${ev.y}")
            }
            
            MotionEvent.ACTION_MOVE -> {
                val dx = ev.x - lastX
                val dy = ev.y - lastY
                
                val absDx = abs(dx)
                val absDy = abs(dy)
                
                // 判断是否超过最小滑动距离
                if (!isDragging && (absDx > touchSlop || absDy > touchSlop)) {
                    isDragging = true
                    
                    // 确定滑动方向
                    if (!isLockOrientation) {
                        orientation = if (absDx > absDy) {
                            1 // 水平
                        } else {
                            2 // 垂直
                        }
                        isLockOrientation = true
                        
                        Log.d(TAG, "滑动方向: ${if (orientation == 1) "水平" else "垂直"}")
                    }
                }
                
                // 根据滑动方向决定是否请求父 View 不拦截
                if (isDragging) {
                    handleScrollConflict(orientation, dx, dy)
                }
                
                lastX = ev.x
                lastY = ev.y
                
                Log.d(TAG, "ACTION_MOVE: dx=$dx, dy=$dy, orientation=$orientation")
            }
            
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                Log.d(TAG, "ACTION_${if (ev.action == MotionEvent.ACTION_UP) "UP" else "CANCEL"}")
                
                isDragging = false
                isLockOrientation = false
                orientation = 0
            }
        }
        
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        // 根据需要决定是否拦截事件
        return super.onInterceptTouchEvent(e)
    }

    /**
     * 处理滑动冲突
     * 
     * @param orientation 滑动方向 (1: 水平, 2: 垂直)
     * @param dx X 方向移动距离
     * @param dy Y 方向移动距离
     */
    private fun handleScrollConflict(orientation: Int, dx: Float, dy: Float) {
        val layoutManager = layoutManager ?: return
        
        val isVertical = layoutManager.canScrollVertically()
        val isHorizontal = layoutManager.canScrollHorizontally()
        
        when (orientation) {
            1 -> { // 水平滑动
                if (isHorizontal) {
                    // 当前 RecyclerView 可以水平滑动
                    // 检查是否可以继续滑动
                    val canScroll = if (dx > 0) {
                        // 向右滑动，检查是否可以继续向右
                        canScrollHorizontally(-1)
                    } else {
                        // 向左滑动，检查是否可以继续向左
                        canScrollHorizontally(1)
                    }
                    
                    if (canScroll) {
                        // 请求父 View 不要拦截
                        parent?.requestDisallowInterceptTouchEvent(true)
                        Log.d(TAG, "水平滑动: 请求父View不拦截")
                    } else {
                        // 已到边界，允许父 View 拦截
                        parent?.requestDisallowInterceptTouchEvent(false)
                        Log.d(TAG, "水平滑动: 到达边界，允许父View拦截")
                    }
                } else {
                    // 当前 RecyclerView 不支持水平滑动，让父 View 处理
                    parent?.requestDisallowInterceptTouchEvent(false)
                    Log.d(TAG, "水平滑动: 当前RV不支持，让父View处理")
                }
            }
            
            2 -> { // 垂直滑动
                if (isVertical) {
                    // 当前 RecyclerView 可以垂直滑动
                    val canScroll = if (dy > 0) {
                        // 向下滑动，检查是否可以继续向下
                        canScrollVertically(-1)
                    } else {
                        // 向上滑动，检查是否可以继续向上
                        canScrollVertically(1)
                    }
                    
                    if (canScroll) {
                        // 请求父 View 不要拦截
                        parent?.requestDisallowInterceptTouchEvent(true)
                        Log.d(TAG, "垂直滑动: 请求父View不拦截")
                    } else {
                        // 已到边界，允许父 View 拦截
                        parent?.requestDisallowInterceptTouchEvent(false)
                        Log.d(TAG, "垂直滑动: 到达边界，允许父View拦截")
                    }
                } else {
                    // 当前 RecyclerView 不支持垂直滑动，让父 View 处理
                    parent?.requestDisallowInterceptTouchEvent(false)
                    Log.d(TAG, "垂直滑动: 当前RV不支持，让父View处理")
                }
            }
        }
    }
}
