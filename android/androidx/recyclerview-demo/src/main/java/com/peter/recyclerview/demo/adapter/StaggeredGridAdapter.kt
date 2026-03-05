package com.peter.recyclerview.demo.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.peter.recyclerview.demo.R
import com.peter.recyclerview.demo.model.StaggeredItem

/**
 * 瀑布流列表 Adapter
 */
class StaggeredGridAdapter : ListAdapter<StaggeredItem, StaggeredGridAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(parent: android.view.ViewGroup) : BaseViewHolder<StaggeredItem>(
        parent, R.layout.item_staggered
    ) {
        private val imageView: View = itemView.findViewById(R.id.imageView)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)

        override fun bind(item: StaggeredItem) {
            tvTitle.text = item.title
            
            // 动态设置图片高度，实现瀑布流效果
            val layoutParams = imageView.layoutParams
            if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                // 根据比例计算高度，模拟不同高度的图片
                val baseHeight = 150
                val height = (baseHeight * item.ratio).toInt()
                layoutParams.height = height.coerceIn(80, 250)
                imageView.layoutParams = layoutParams
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<StaggeredItem>() {
        override fun areItemsTheSame(oldItem: StaggeredItem, newItem: StaggeredItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StaggeredItem, newItem: StaggeredItem): Boolean {
            return oldItem == newItem
        }
    }
}
