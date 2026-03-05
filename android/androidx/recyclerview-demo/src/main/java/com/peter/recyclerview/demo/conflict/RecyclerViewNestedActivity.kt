package com.peter.recyclerview.demo.conflict

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peter.recyclerview.demo.R
import com.peter.recyclerview.demo.adapter.BaseViewHolder
import com.peter.recyclerview.demo.databinding.ActivityRvNestedBinding
import com.peter.recyclerview.demo.model.ChildItem
import com.peter.recyclerview.demo.model.ParentItem

/**
 * RecyclerView 嵌套 RecyclerView 示例
 * 
 * ============================================
 * 滑动冲突场景分析
 * ============================================
 * 
 * 场景一：外层垂直 RecyclerView + 内层水平 RecyclerView
 * -------------------------------------------------
 * - 冲突类型：不同方向滑动（无实际冲突）
 * - 处理方式：系统自动根据滑动方向分发事件
 * - 水平滑动由内层 RecyclerView 消费
 * - 垂直滑动由外层 RecyclerView 消费
 * 
 * 场景二：外层垂直 RecyclerView + 内层垂直 RecyclerView
 * ---------------------------------------------------
 * - 冲突类型：同方向滑动冲突
 * - 问题描述：无法判断是内层滑动还是外层滑动
 * - 解决方案：
 *   1. 固定内层 RecyclerView 高度（不推荐，用户体验差）
 *   2. 禁止内层 RecyclerView 滑动，只让外层滑动
 *   3. 使用 NestedScrolling 机制处理（推荐）
 *   4. 外层使用 NestedScrollView 替代
 * 
 * ============================================
 * 解决方案详解
 * ============================================
 * 
 * 方案一：禁止内层滑动
 * - 设置内层 RecyclerView.isNestedScrollingEnabled = false
 * - 或者重写 canScrollVertically() 返回 false
 * 
 * 方案二：自定义 ViewPager 嵌套 ViewPager 处理
 * - 重写 onInterceptTouchEvent() 和 onTouchEvent()
 * - 根据滑动角度和速度判断应该由谁消费事件
 * 
 * 方案三：使用 NestedScrolling 机制
 * - RecyclerView 默认实现了 NestedScrollingChild
 * - 外层容器需要实现 NestedScrollingParent
 * - 通过 dispatchNestedScroll 等方法协调滑动
 */
class RecyclerViewNestedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRvNestedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRvNestedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        loadData()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@RecyclerViewNestedActivity)
            adapter = ParentAdapter()
        }
    }

    private fun loadData() {
        val items = (1..10).map { parentId ->
            ParentItem(
                id = parentId,
                title = "分类 $parentId",
                children = (1..8).map { childId ->
                    ChildItem(
                        id = parentId * 100 + childId,
                        name = "Item ${parentId}-${childId}",
                        description = "这是内层 Item 的描述"
                    )
                }
            )
        }

        (binding.recyclerView.adapter as ParentAdapter).submitList(items)
    }
}

/**
 * 外层 Adapter
 */
class ParentAdapter : ListAdapter<ParentItem, ParentAdapter.ParentViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        return ParentViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ParentViewHolder(parent: ViewGroup) : BaseViewHolder<ParentItem>(
        parent, R.layout.item_parent
    ) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val innerRecyclerView: RecyclerView = itemView.findViewById(R.id.innerRecyclerView)

        override fun bind(item: ParentItem) {
            tvTitle.text = item.title

            // 设置内层 RecyclerView
            // 使用水平方向，避免与外层垂直滑动冲突
            innerRecyclerView.apply {
                layoutManager = LinearLayoutManager(
                    itemView.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                
                // 方案一：禁止内层滑动（如果需要垂直嵌套垂直）
                // 这会让内层列表无法滚动，适合固定高度的列表
                // isNestedScrollingEnabled = false
                
                adapter = ChildAdapter().apply {
                    submitList(item.children)
                }
                
                // 重要：设置 RecycledViewPool 复用 ViewHolder，提高性能
                // 避免每个内层 RecyclerView 都创建新的 ViewHolder
                setRecycledViewPool(RecyclerView.RecycledViewPool())
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ParentItem>() {
        override fun areItemsTheSame(oldItem: ParentItem, newItem: ParentItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ParentItem, newItem: ParentItem): Boolean {
            return oldItem == newItem
        }
    }
}

/**
 * 内层 Adapter
 */
class ChildAdapter : ListAdapter<ChildItem, ChildAdapter.ChildViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        return ChildViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChildViewHolder(parent: ViewGroup) : BaseViewHolder<ChildItem>(
        parent, R.layout.item_child
    ) {
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvDesc: TextView = itemView.findViewById(R.id.tvDesc)

        override fun bind(item: ChildItem) {
            tvName.text = item.name
            tvDesc.text = item.description
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ChildItem>() {
        override fun areItemsTheSame(oldItem: ChildItem, newItem: ChildItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChildItem, newItem: ChildItem): Boolean {
            return oldItem == newItem
        }
    }
}

/**
 * ============================================
 * 自定义处理滑动冲突的 RecyclerView
 * ============================================
 * 
 * 这个类展示了如何手动处理滑动冲突
 * 主要原理：
 * 1. 在 onInterceptTouchEvent 中判断是否需要拦截
 * 2. 根据滑动方向和速度决定由谁消费事件
 * 3. 使用 requestDisallowInterceptTouchEvent 控制事件分发
 */
class ConflictResolvingRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var initialX = 0f
    private var initialY = 0f
    private var isDragging = false

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = e.x
                initialY = e.y
                isDragging = false
                // 当开始触摸时，请求父 View 不要拦截事件
                // 这样可以让内层 RecyclerView 有机会处理滑动
                parent?.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = Math.abs(e.x - initialX)
                val dy = Math.abs(e.y - initialY)

                // 判断滑动方向
                if (dx > dy) {
                    // 水平滑动：如果当前 RecyclerView 是水平方向，请求父 View 不要拦截
                    // 如果当前是垂直方向，让父 View 拦截
                    val layoutManager = layoutManager
                    if (layoutManager is LinearLayoutManager) {
                        if (layoutManager.orientation == LinearLayoutManager.HORIZONTAL) {
                            // 水平滑动且当前是水平 RecyclerView，由当前 View 处理
                            parent?.requestDisallowInterceptTouchEvent(true)
                        } else {
                            // 水平滑动但当前是垂直 RecyclerView，让父 View 处理
                            parent?.requestDisallowInterceptTouchEvent(false)
                        }
                    }
                } else {
                    // 垂直滑动：类似处理
                    val layoutManager = layoutManager
                    if (layoutManager is LinearLayoutManager) {
                        if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                            // 检查是否滑动到边界
                            if (canScrollVertically(-1) || canScrollVertically(1)) {
                                // 还可以滑动，由当前 View 处理
                                parent?.requestDisallowInterceptTouchEvent(true)
                            } else {
                                // 已到边界，让父 View 处理
                                parent?.requestDisallowInterceptTouchEvent(false)
                            }
                        } else {
                            // 垂直滑动但当前是水平 RecyclerView，让父 View 处理
                            parent?.requestDisallowInterceptTouchEvent(false)
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
            }
        }
        return super.onInterceptTouchEvent(e)
    }
}
