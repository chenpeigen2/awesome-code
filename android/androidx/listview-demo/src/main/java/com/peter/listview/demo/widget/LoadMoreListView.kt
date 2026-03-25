package com.peter.listview.demo.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AbsListView
import android.widget.ListView
import com.peter.listview.demo.databinding.LoadingFooterBinding

/**
 * 支持加载更多的 ListView
 *
 * 功能：
 * - 滚动到底部时自动触发加载更多
 * - 显示加载状态（加载中/加载完成/加载失败）
 * - 防止重复加载
 */
class LoadMoreListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ListView(context, attrs, defStyleAttr) {

    // 加载更多回调
    private var onLoadMoreListener: (() -> Unit)? = null

    // 加载更多 Footer
    private var loadingFooter: View? = null
    private var footerBinding: LoadingFooterBinding? = null

    // 状态
    private var isLoading = false
    private var hasMoreData = true
    private var isLoadMoreEnabled = true

    // 触发加载的阈值（距离底部还有多少项时开始加载）
    private var threshold = 3

    init {
        setupScrollListener()
    }

    private fun setupScrollListener() {
        setOnScrollListener(object : OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                // 滚动停止时检查是否需要加载更多
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    checkLoadMore()
                }
            }

            override fun onScroll(
                view: AbsListView?,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                // 滚动过程中也检查，提高响应速度
                checkLoadMore(firstVisibleItem, visibleItemCount, totalItemCount)
            }
        })
    }

    private fun checkLoadMore(
        firstVisibleItem: Int = firstVisiblePosition,
        visibleItemCount: Int = childCount,
        totalItemCount: Int = count
    ) {
        // 判断是否滚动到底部附近
        val lastVisibleItem = firstVisibleItem + visibleItemCount
        val shouldLoad = !isLoading &&
                hasMoreData &&
                isLoadMoreEnabled &&
                totalItemCount > 0 &&
                lastVisibleItem >= totalItemCount - threshold

        if (shouldLoad) {
            triggerLoadMore()
        }
    }

    private fun triggerLoadMore() {
        isLoading = true
        showLoading()
        onLoadMoreListener?.invoke()
    }

    // ========== 公共方法 ==========

    /**
     * 设置加载更多监听器
     */
    fun setOnLoadMoreListener(listener: () -> Unit) {
        this.onLoadMoreListener = listener
    }

    /**
     * 设置是否启用加载更多
     */
    fun setLoadMoreEnabled(enabled: Boolean) {
        this.isLoadMoreEnabled = enabled
    }

    /**
     * 设置加载阈值
     */
    fun setThreshold(threshold: Int) {
        this.threshold = threshold
    }

    /**
     * 显示加载中状态
     */
    fun showLoading() {
        if (loadingFooter == null) {
            footerBinding = LoadingFooterBinding.inflate(LayoutInflater.from(context), this, false)
            loadingFooter = footerBinding!!.root
            addFooterView(loadingFooter)
        }
        footerBinding?.apply {
            progressBar.visibility = View.VISIBLE
            loadingText.text = context.getString(com.peter.listview.demo.R.string.loading)
        }
    }

    /**
     * 加载完成
     */
    fun showLoadComplete() {
        isLoading = false
        footerBinding?.apply {
            progressBar.visibility = View.GONE
            loadingText.text = context.getString(com.peter.listview.demo.R.string.load_complete)
        }
        // 延迟隐藏完成提示
        postDelayed({
            if (loadingFooter != null) {
                removeFooterView(loadingFooter)
                loadingFooter = null
                footerBinding = null
            }
        }, 1000)
    }

    /**
     * 加载失败
     */
    fun showLoadFailed() {
        isLoading = false
        footerBinding?.apply {
            progressBar.visibility = View.GONE
            loadingText.text = context.getString(com.peter.listview.demo.R.string.load_failed)
        }
    }

    /**
     * 没有更多数据
     */
    fun setNoMoreData() {
        hasMoreData = false
        isLoading = false
        footerBinding?.apply {
            progressBar.visibility = View.GONE
            loadingText.text = context.getString(com.peter.listview.demo.R.string.no_more_data)
        }
    }

    /**
     * 重置状态（用于下拉刷新后）
     */
    fun reset() {
        hasMoreData = true
        isLoading = false
        loadingFooter?.let {
            removeFooterView(it)
            loadingFooter = null
            footerBinding = null
        }
    }
}
