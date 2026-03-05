package com.peter.recyclerview.demo.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.NestedScrollingChild3
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * 自定义 NestedScrollingChild 示例
 * 
 * ============================================
 * NestedScrolling 机制详解
 * ============================================
 * 
 * 背景：
 * 传统的 requestDisallowInterceptTouchEvent() 只能简单地请求父 View 不拦截事件，
 * 但无法实现精细的嵌套滑动控制。NestedScrolling 机制应运而生。
 * 
 * 核心接口：
 * - NestedScrollingChild：子 View 实现，负责发起嵌套滑动
 * - NestedScrollingParent：父 View 实现，负责响应嵌套滑动
 * 
 * 工作流程：
 * 1. 子 View 开始滑动前，询问父 View 是否需要参与
 * 2. 子 View 滑动时，先让父 View 消费滑动距离
 * 3. 父 View 消费后，子 View 再消费剩余距离
 * 4. 子 View 滑动结束后，通知父 View
 * 
 * ============================================
 * RecyclerView 的实现
 * ============================================
 * 
 * RecyclerView 实现了 NestedScrollingChild3 接口：
 * 
 * 1. startNestedScroll() - 开始嵌套滑动
 *    在 dispatchTouchEvent 的 ACTION_DOWN 中调用
 *    
 * 2. dispatchNestedPreScroll() - 滑动前分发
 *    在消费滑动距离前，先让父 View 消费
 *    
 * 3. dispatchNestedScroll() - 滑动后分发
 *    在消费滑动距离后，通知父 View（包括未消费的距离）
 *    
 * 4. stopNestedScroll() - 结束嵌套滑动
 *    在 dispatchTouchEvent 的 ACTION_UP/CANCEL 中调用
 * 
 * ============================================
 * 自定义 NestedScrollingChild 步骤
 * ============================================
 * 
 * 1. 实现 NestedScrollingChild3 接口
 * 2. 创建 NestedScrollingChildHelper 实例
 * 3. 在适当位置调用 helper 的方法
 * 4. 处理父 View 消费后的剩余距离
 */
class NestedScrollableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), NestedScrollingChild3 {

    private val scrollingChildHelper = NestedScrollingChildHelper(this)

    init {
        // 启用嵌套滑动
        isNestedScrollingEnabled = true
    }

    // ============ NestedScrollingChild3 接口实现 ============

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        scrollingChildHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return scrollingChildHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return scrollingChildHelper.startNestedScroll(axes, type)
    }

    override fun stopNestedScroll(type: Int) {
        scrollingChildHelper.stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return scrollingChildHelper.hasNestedScrollingParent(type)
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int,
        consumed: IntArray
    ) {
        scrollingChildHelper.dispatchNestedScroll(
            dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
            offsetInWindow, type, consumed
        )
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        return scrollingChildHelper.dispatchNestedScroll(
            dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
            offsetInWindow, type
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        return scrollingChildHelper.dispatchNestedPreScroll(
            dx, dy, consumed, offsetInWindow, type
        )
    }

    // ============ 使用示例 ============

    /**
     * 在触摸事件中使用 NestedScrolling
     */
    private fun handleScroll(dy: Int) {
        val consumed = IntArray(2)
        val offsetInWindow = IntArray(2)

        // 1. 滑动前，先让父 View 消费
        if (dispatchNestedPreScroll(0, dy, consumed, offsetInWindow, ViewCompat.TYPE_TOUCH)) {
            // 父 View 消费了部分距离
            val remainingDy = dy - consumed[1]
            
            // 2. 子 View 消费剩余距离
            // ... 自己处理滑动 ...
            
            val dyConsumed = dy - remainingDy
            val dyUnconsumed = remainingDy
            
            // 3. 通知父 View 滑动结果
            dispatchNestedScroll(
                0, dyConsumed,
                0, dyUnconsumed,
                null,
                ViewCompat.TYPE_TOUCH
            )
        } else {
            // 父 View 不参与，自己处理全部滑动
            // ... 自己处理滑动 ...
        }
    }
}

/**
 * ============================================
 * 常见的嵌套滑动场景和解决方案
 * ============================================
 * 
 * 场景一：CoordinatorLayout + AppBarLayout + RecyclerView
 * 解决方案：使用默认的 AppBarLayout.ScrollingViewBehavior
 * 
 * 场景二：NestedScrollView + RecyclerView
 * 解决方案：
 * - 方案A：禁用 RecyclerView 的嵌套滑动
 * - 方案B：使用单一 RecyclerView + 多类型 Item
 * 
 * 场景三：ViewPager2 + RecyclerView（同方向）
 * 解决方案：ViewPager2 内部已处理
 * 
 * 场景四：自定义嵌套滑动
 * 解决方案：
 * - 父 View 实现 NestedScrollingParent
 * - 子 View 实现 NestedScrollingChild
 * - 使用 NestedScrollingParentHelper 和 NestedScrollingChildHelper
 */
