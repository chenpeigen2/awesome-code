package com.peter.recyclerview.demo.basic

import android.graphics.Canvas
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.peter.recyclerview.demo.R
import com.peter.recyclerview.demo.adapter.SimpleListAdapter
import com.peter.recyclerview.demo.databinding.ActivitySimpleListBinding
import com.peter.recyclerview.demo.model.SimpleItem

/**
 * 拖拽排序和滑动删除示例
 * 
 * 功能演示：
 * 1. 长按拖拽排序
 * 2. 左右滑动删除
 * 3. 自定义滑动删除动画
 * 
 * 关键知识点：
 * - ItemTouchHelper：用于处理拖拽和滑动操作
 * - ItemTouchHelper.Callback：自定义拖拽和滑动行为
 * - 通过 onMove 和 onSwiped 回调处理数据变化
 */
class DragSwipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySimpleListBinding
    private lateinit var adapter: SimpleListAdapter
    private val items = mutableListOf<SimpleItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimpleListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupItemTouchHelper()
        loadData()
    }

    private fun setupToolbar() {
        binding.toolbar.title = "拖拽与滑动删除"
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = SimpleListAdapter()

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DragSwipeActivity)
            adapter = this@DragSwipeActivity.adapter
        }
    }

    /**
     * 设置 ItemTouchHelper
     * 
     * ItemTouchHelper 是 RecyclerView 的辅助类，用于处理拖拽和滑动操作
     */
    private fun setupItemTouchHelper() {
        val callback = object : ItemTouchHelper.SimpleCallback(
            // dragFlags: 拖拽方向
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            // swipeFlags: 滑动方向
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            /**
             * 当 Item 被拖拽移动时调用
             * 需要在这里更新数据列表
             */
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.bindingAdapterPosition
                val toPosition = target.bindingAdapterPosition

                if (fromPosition == RecyclerView.NO_POSITION || 
                    toPosition == RecyclerView.NO_POSITION) {
                    return false
                }

                // 交换数据列表中的位置
                // 注意：直接修改 items 列表
                val item = items.removeAt(fromPosition)
                items.add(toPosition, item)

                // 通知 Adapter 更新
                adapter.notifyItemMoved(fromPosition, toPosition)
                return true
            }

            /**
             * 当 Item 被滑动删除时调用
             */
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    items.removeAt(position)
                    adapter.notifyItemRemoved(position)
                }
            }

            /**
             * 自定义滑动时的绘制效果
             * 可以在这里绘制背景、删除图标等
             */
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                // 可以在这里添加自定义绘制效果
                // 例如：滑动时显示红色背景和删除图标
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            /**
             * 当拖拽或滑动开始时调用
             * 可以在这里改变 Item 的外观，例如放大、改变背景色
             */
            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    // 拖拽时改变外观
                    viewHolder?.itemView?.alpha = 0.8f
                }
            }

            /**
             * 当拖拽或滑动结束时调用
             * 需要恢复 Item 的外观
             */
            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                // 恢复外观
                viewHolder.itemView.alpha = 1f
            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun loadData() {
        val colors = listOf(
            R.color.card_blue,
            R.color.card_green,
            R.color.card_orange,
            R.color.card_purple,
            R.color.card_red
        )

        items.clear()
        items.addAll((1..20).map { index ->
            SimpleItem(
                id = index,
                title = "Item $index",
                description = "长按拖拽排序，左右滑动删除",
                colorRes = colors[index % colors.size]
            )
        })

        adapter.submitList(items.toList())
    }
}
