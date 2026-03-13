package com.peter.viewpager2.demo.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2

/**
 * ViewPager2 容器，用于处理嵌套滑动冲突
 * 将 ViewPager2 放入此容器中，自动处理与父 ViewPager2 的滑动冲突
 */
class NestedViewPagerContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var startX = 0f
    private var startY = 0f

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x
                startY = ev.y
                // 当手指按下时，请求父 View 不要拦截触摸事件
                parent?.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = ev.x - startX
                val dy = ev.y - startY

                // 找到内部的 ViewPager2
                val viewPager = findViewPager2()
                
                if (Math.abs(dx) > Math.abs(dy)) {
                    // 水平滑动
                    viewPager?.let { vp ->
                        val canScrollLeft = vp.canScrollHorizontally(-1)
                        val canScrollRight = vp.canScrollHorizontally(1)

                        if (dx > 0 && !canScrollLeft) {
                            // 向右滑，但已经到最左边，让父 View 处理
                            parent?.requestDisallowInterceptTouchEvent(false)
                        } else if (dx < 0 && !canScrollRight) {
                            // 向左滑，但已经到最右边，让父 View 处理
                            parent?.requestDisallowInterceptTouchEvent(false)
                        } else {
                            // 在中间位置，请求父 View 不要拦截
                            parent?.requestDisallowInterceptTouchEvent(true)
                        }
                    }
                } else {
                    // 垂直滑动，让父 View 处理
                    parent?.requestDisallowInterceptTouchEvent(false)
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    private fun findViewPager2(): ViewPager2? {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is ViewPager2) {
                return child
            }
        }
        return null
    }
}
