package com.peter.touch.demo.widget

import android.content.Context
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.Scroller
import kotlin.math.abs


/**
 * 使用 VelocityTracker 实现类似 RecyclerView 的 fling 效果
 */
class FlingView(context: Context?) : View(context) {
    private val mVelocityTracker: VelocityTracker = VelocityTracker.obtain()
    private val mScroller: Scroller = Scroller(context)

    private var mLastX = 0
    private var mLastY = 0
    private var mScrollX = 0
    private var mScrollY = 0

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // ★★★ 速度追踪 ★★★
        mVelocityTracker.addMovement(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // 停止正在进行的 fling
                if (!mScroller.isFinished) {
                    mScroller.abortAnimation()
                }

                mLastX = event.x.toInt()
                mLastY = event.y.toInt()
            }

            MotionEvent.ACTION_MOVE -> {
                val x = event.x.toInt()
                val y = event.y.toInt()

                val deltaX = x - mLastX
                val deltaY = y - mLastY


                // 滚动内容
                mScrollX -= deltaX
                mScrollY -= deltaY


                // 重绘
                invalidate()

                mLastX = x
                mLastY = y
            }

            MotionEvent.ACTION_UP -> {
                // ★★★ 计算 fling 速度 ★★★
                mVelocityTracker.computeCurrentVelocity(1000, MAX_FLING_VELOCITY.toFloat())

                val velocityX = mVelocityTracker.xVelocity
                val velocityY = mVelocityTracker.yVelocity


                // 如果速度足够快，启动 fling
                if (abs(velocityX) > ViewConfiguration.getMinimumFlingVelocity()
                    || abs(velocityY) > ViewConfiguration.getMinimumFlingVelocity()
                ) {
                    startFling(-velocityX.toInt(), -velocityY.toInt())
                }
            }

            MotionEvent.ACTION_CANCEL -> mVelocityTracker.clear()
        }

        return true
    }

    private fun startFling(velocityX: Int, velocityY: Int) {
        // ★★★ 使用 Scroller 实现惯性滑动 ★★★
        mScroller.fling(
            mScrollX, mScrollY,  // 起始位置
            velocityX, velocityY,  // 初始速度
            0, 10000,  // 最小/最大 X 滚动范围
            0, 10000 // 最小/最大 Y 滚动范围
        )


        // 触发重绘，启动动画
        postInvalidateOnAnimation()
    }

    override fun computeScroll() {
        // ★★★ Scroller 滚动计算 ★★★
        if (mScroller.computeScrollOffset()) {
            // 更新滚动位置
            mScrollX = mScroller.currX
            mScrollY = mScroller.currY


            // 重绘
            postInvalidateOnAnimation()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mVelocityTracker.recycle()
    }

    companion object {
        // 最大 fling 速度（像素/秒）
        private const val MAX_FLING_VELOCITY = 5000
    }
}
