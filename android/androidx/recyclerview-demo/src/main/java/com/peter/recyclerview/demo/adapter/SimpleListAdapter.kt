package com.peter.recyclerview.demo.adapter

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.peter.recyclerview.demo.R
import com.peter.recyclerview.demo.model.SimpleItem

/**
 * 简单列表 Adapter
 * 使用 ListAdapter + DiffUtil 实现高效的数据更新
 */
class SimpleListAdapter @JvmOverloads constructor(
    private val onItemClick: ((SimpleItem, Int) -> Unit)? = null,
    private val onItemLongClick: ((SimpleItem, Int) -> Boolean)? = null
) : ListAdapter<SimpleItem, SimpleListAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(parent: android.view.ViewGroup) : BaseViewHolder<SimpleItem>(
        parent, R.layout.item_simple
    ) {
        private val colorView: View = itemView.findViewById(R.id.colorView)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDesc: TextView = itemView.findViewById(R.id.tvDesc)

        init {
            // 点击事件
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(getItem(position), position)
                }
            }
            // 长按事件
            itemView.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                    onItemLongClick?.invoke(getItem(position), position) ?: false
                } else {
                    false
                }
            }
        }

        override fun bind(item: SimpleItem) {
            tvTitle.text = item.title
            tvDesc.text = item.description
            colorView.setBackgroundColor(
                ContextCompat.getColor(itemView.context, item.colorRes)
            )
        }
    }

    /**
     * DiffUtil.ItemCallback 用于比较两个 Item 是否相同
     * 这样可以在更新数据时只刷新变化的 Item，提高性能
     */
    class DiffCallback : DiffUtil.ItemCallback<SimpleItem>() {
        // 判断是否是同一个 Item（通常比较 id）
        override fun areItemsTheSame(oldItem: SimpleItem, newItem: SimpleItem): Boolean {
            return oldItem.id == newItem.id
        }

        // 判断内容是否相同（比较所有需要显示的字段）
        override fun areContentsTheSame(oldItem: SimpleItem, newItem: SimpleItem): Boolean {
            return oldItem == newItem
        }
    }
}
