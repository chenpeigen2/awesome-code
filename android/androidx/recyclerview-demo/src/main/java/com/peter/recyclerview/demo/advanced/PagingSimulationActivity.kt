package com.peter.recyclerview.demo.advanced

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.peter.recyclerview.demo.R
import com.peter.recyclerview.demo.databinding.ActivitySimpleListBinding
import com.peter.recyclerview.demo.model.PageItem

/**
 * 分页加载模拟示例
 * 
 * 功能演示：
 * 1. 滚动到底部时加载更多
 * 2. 显示加载状态（加载中、加载完成、加载失败、没有更多）
 * 3. 使用 ItemDecoration 或特殊 Item 显示加载状态
 * 
 * 关键知识点：
 * - RecyclerView.OnScrollListener 监听滚动事件
 * - LinearLayoutManager.findLastVisibleItemPosition() 获取最后可见位置
 * - 分页加载的触发时机和状态管理
 * 
 * 注意：实际项目中推荐使用 Jetpack Paging3 库
 */
class PagingSimulationActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySimpleListBinding
    private lateinit var adapter: PagingAdapter
    private val items = mutableListOf<PageItem>()
    private var currentPage = 0
    private var isLoading = false
    private var hasMore = true
    private val pageSize = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimpleListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        loadFirstPage()
    }

    private fun setupToolbar() {
        binding.toolbar.title = "分页加载模拟"
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = PagingAdapter()

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@PagingSimulationActivity)
            adapter = this@PagingSimulationActivity.adapter

            // 监听滚动事件，实现加载更多
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    // 只在向下滚动时检查
                    if (dy <= 0) return

                    val layoutManager = layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                    // 当滚动到最后 3 个 Item 时，开始加载更多
                    if (!isLoading && hasMore && 
                        lastVisibleItemPosition >= totalItemCount - 3) {
                        loadMore()
                    }
                }
            })
        }

        // 下拉刷新
        binding.swipeRefresh.setOnRefreshListener {
            refresh()
        }
    }

    private fun loadFirstPage() {
        currentPage = 0
        hasMore = true
        items.clear()
        loadMore()
    }

    private fun refresh() {
        currentPage = 0
        hasMore = true
        items.clear()
        adapter.setData(items, isLoading = false, hasMore = true)
        loadMore()
    }

    private fun loadMore() {
        if (isLoading || !hasMore) return

        isLoading = true
        adapter.setData(items, isLoading = true, hasMore = true)

        // 模拟网络请求延迟
        Handler(Looper.getMainLooper()).postDelayed({
            // 模拟加载完成
            currentPage++
            
            // 模拟数据
            val startId = (currentPage - 1) * pageSize + 1
            val newItems = (startId..startId + pageSize - 1).map { id ->
                PageItem(
                    id = id,
                    title = "Item $id",
                    content = "这是第 $id 个数据项的内容，来自第 $currentPage 页"
                )
            }

            items.addAll(newItems)

            // 模拟只有 5 页数据
            hasMore = currentPage < 5

            adapter.setData(items, isLoading = false, hasMore = hasMore)
            isLoading = false

            if (binding.swipeRefresh.isRefreshing) {
                binding.swipeRefresh.isRefreshing = false
            }
        }, 1000)
    }
}

/**
 * 分页 Adapter
 */
class PagingAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_LOADING = 1
    }

    private val items = mutableListOf<PageItem>()
    private var isLoading = false
    private var hasMore = true

    fun setData(items: List<PageItem>, isLoading: Boolean, hasMore: Boolean) {
        this.items.clear()
        this.items.addAll(items)
        this.isLoading = isLoading
        this.hasMore = hasMore
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        // 如果正在加载或还有更多数据，需要额外一个 loading item
        return if (items.isEmpty()) {
            0
        } else if (isLoading || hasMore) {
            items.size + 1
        } else {
            items.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == items.size) {
            TYPE_LOADING
        } else {
            TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ITEM -> ItemViewHolder(parent)
            else -> LoadingViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemViewHolder -> holder.bind(items[position])
            is LoadingViewHolder -> holder.bind(isLoading, hasMore)
        }
    }

    inner class ItemViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false)
    ) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvContent: TextView = itemView.findViewById(R.id.tvContent)

        fun bind(item: PageItem) {
            tvTitle.text = item.title
            tvContent.text = item.content
        }
    }

    inner class LoadingViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
    ) {
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(isLoading: Boolean, hasMore: Boolean) {
            tvStatus.text = when {
                isLoading -> "正在加载..."
                hasMore -> "上拉加载更多"
                else -> "没有更多数据了"
            }
        }
    }
}
