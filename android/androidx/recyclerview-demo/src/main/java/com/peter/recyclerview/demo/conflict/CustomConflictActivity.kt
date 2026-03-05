package com.peter.recyclerview.demo.conflict

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peter.recyclerview.demo.R
import com.peter.recyclerview.demo.adapter.BaseViewHolder
import com.peter.recyclerview.demo.databinding.ActivityCustomConflictBinding
import com.peter.recyclerview.demo.model.SimpleItem
import kotlin.math.abs

/**
 * 自定义滑动冲突处理示例
 * 
 * ============================================
 * 本示例演示的场景
 * ============================================
 * 
 * 场景：一个包含多个水平滑动列表的垂直滑动列表
 * 类似于电商首页：多个分类，每个分类下有水平滑动的商品列表
 * 
 * 滑动冲突分析：
 * 1. 垂直滑动：由外层 RecyclerView 处理
 * 2. 水平滑动：由内层 RecyclerView 处理
 * 3. 斜向滑动：需要判断滑动角度决定谁来处理
 * 
 * ============================================
 * 解决方案对比
 * ============================================
 * 
 * 方案一：使用默认的 NestedScrolling 机制
 * - RecyclerView 默认支持 NestedScrolling
 * - 系统会自动处理方向判断
 * - 适用于大部分场景
 * 
 * 方案二：自定义处理（本示例）
 * - 重写 onInterceptTouchEvent 和 onTouchEvent
 * - 根据滑动角度和方向决定事件分发
 * - 适用于需要精细控制的场景
 * 
 * 方案三：使用 NestedScrollingParent/Child 接口
 * - 完全控制嵌套滑动行为
 * - 适用于复杂自定义控件
 */
class CustomConflictActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomConflictBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomConflictBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupInfo()
        setupRecyclerView()
        loadData()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupInfo() {
        binding.tvInfo.text = """
            |本示例展示如何自定义处理滑动冲突。
            |
            |每个分类下有一个水平滑动列表。
            |
            |测试方法：
            |1. 水平滑动内层列表 - 内层滑动
            |2. 垂直滑动任意位置 - 外层滑动
            |3. 斜向滑动 - 根据角度判断
            |
            |查看 Logcat 输出，标签: ScrollConflict
        """.trimMargin()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CustomConflictActivity)
            adapter = OuterAdapter()
        }
    }

    private fun loadData() {
        val colors = listOf(
            R.color.card_blue,
            R.color.card_green,
            R.color.card_orange,
            R.color.card_purple,
            R.color.card_red
        )

        val categories = listOf("推荐", "热门", "最新", "精选", "限时", "特惠")
        
        val items = categories.mapIndexed { index, category ->
            CategoryItem(
                id = index,
                title = category,
                items = (1..15).map { itemIndex ->
                    SimpleItem(
                        id = index * 100 + itemIndex,
                        title = "$category-$itemIndex",
                        description = "商品描述",
                        colorRes = colors[(index + itemIndex) % colors.size]
                    )
                }
            )
        }

        (binding.recyclerView.adapter as OuterAdapter).submitList(items)
    }
}

// 数据模型
data class CategoryItem(
    val id: Int,
    val title: String,
    val items: List<SimpleItem>
)

// 外层 Adapter
class OuterAdapter : ListAdapter<CategoryItem, OuterAdapter.CategoryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(parent)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(parent: ViewGroup) : BaseViewHolder<CategoryItem>(
        parent, R.layout.item_category
    ) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val innerRecyclerView: ConflictResolvingHorizontalRecyclerView = 
            itemView.findViewById(R.id.innerRecyclerView)

        override fun bind(item: CategoryItem) {
            tvTitle.text = item.title

            innerRecyclerView.layoutManager = LinearLayoutManager(
                itemView.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )

            innerRecyclerView.adapter = InnerAdapter().apply {
                submitList(item.items)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CategoryItem>() {
        override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem == newItem
        }
    }
}

// 内层 Adapter
class InnerAdapter : ListAdapter<SimpleItem, InnerAdapter.InnerViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
        return InnerViewHolder(parent)
    }

    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class InnerViewHolder(parent: ViewGroup) : BaseViewHolder<SimpleItem>(
        parent, R.layout.item_inner_product
    ) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)

        override fun bind(item: SimpleItem) {
            tvTitle.text = item.title
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SimpleItem>() {
        override fun areItemsTheSame(oldItem: SimpleItem, newItem: SimpleItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SimpleItem, newItem: SimpleItem): Boolean {
            return oldItem == newItem
        }
    }
}

/**
 * 自定义处理滑动冲突的水平 RecyclerView
 * 
 * 关键点：
 * 1. 记录触摸起始位置
 * 2. 根据滑动方向判断应该由谁处理
 * 3. 使用 requestDisallowInterceptTouchEvent 控制事件分发
 */
class ConflictResolvingHorizontalRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "ScrollConflict"
    }

    private var initialX = 0f
    private var initialY = 0f
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = ev.x
                initialY = ev.y
                
                // 请求父 View 不拦截，让内层有机会处理
                parent?.requestDisallowInterceptTouchEvent(true)
                
                Log.d(TAG, "ACTION_DOWN: 请求父View不拦截")
            }
            
            MotionEvent.ACTION_MOVE -> {
                val dx = ev.x - initialX
                val dy = ev.y - initialY
                val absDx = abs(dx)
                val absDy = abs(dy)
                
                // 超过最小滑动距离
                if (absDx > touchSlop || absDy > touchSlop) {
                    // 计算滑动角度
                    val angle = Math.toDegrees(kotlin.math.atan2(absDy.toDouble(), absDx.toDouble()))
                    
                    Log.d(TAG, "滑动: dx=$absDx, dy=$absDy, angle=$angle")
                    
                    when {
                        // 水平滑动（角度 < 30°）
                        angle < 30 -> {
                            // 检查是否滑动到边界
                            val canScrollHorizontally = if (dx > 0) {
                                canScrollHorizontally(-1)
                            } else {
                                canScrollHorizontally(1)
                            }
                            
                            if (canScrollHorizontally) {
                                // 内层处理
                                parent?.requestDisallowInterceptTouchEvent(true)
                                Log.d(TAG, "水平滑动: 内层处理")
                            } else {
                                // 到达边界，让外层处理
                                parent?.requestDisallowInterceptTouchEvent(false)
                                Log.d(TAG, "水平滑动: 到达边界，外层处理")
                            }
                        }
                        
                        // 垂直滑动（角度 > 60°）
                        angle > 60 -> {
                            // 外层处理
                            parent?.requestDisallowInterceptTouchEvent(false)
                            Log.d(TAG, "垂直滑动: 外层处理")
                        }
                        
                        // 斜向滑动
                        else -> {
                            // 根据滑动距离决定
                            if (absDy > absDx) {
                                parent?.requestDisallowInterceptTouchEvent(false)
                                Log.d(TAG, "斜向滑动(偏垂直): 外层处理")
                            } else {
                                parent?.requestDisallowInterceptTouchEvent(true)
                                Log.d(TAG, "斜向滑动(偏水平): 内层处理")
                            }
                        }
                    }
                }
            }
            
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                Log.d(TAG, "ACTION_${if (ev.action == MotionEvent.ACTION_UP) "UP" else "CANCEL"}")
            }
        }
        
        return super.dispatchTouchEvent(ev)
    }
}
