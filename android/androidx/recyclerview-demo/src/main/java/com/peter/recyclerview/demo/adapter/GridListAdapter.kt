package com.peter.recyclerview.demo.adapter

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.peter.recyclerview.demo.R
import com.peter.recyclerview.demo.model.SimpleItem

/**
 * 网格列表 Adapter
 * 与 SimpleListAdapter 类似，但使用不同的布局
 */
class GridListAdapter : ListAdapter<SimpleItem, GridListAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(parent: android.view.ViewGroup) : BaseViewHolder<SimpleItem>(
        parent, R.layout.item_grid
    ) {
        private val colorView: View = itemView.findViewById(R.id.colorView)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)

        override fun bind(item: SimpleItem) {
            tvTitle.text = item.title
            colorView.setBackgroundColor(
                ContextCompat.getColor(itemView.context, item.colorRes)
            )
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
