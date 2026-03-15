package com.peter.os.demo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.peter.os.demo.databinding.ItemCategoryHeaderBinding
import com.peter.os.demo.databinding.ItemChoreographerBinding

/**
 * Choreographer 示例列表适配器
 */
class ChoreographerAdapter(
    private val items: List<ChoreographerItem>,
    private val onItemClick: (ChoreographerType) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    private val itemsWithHeaders: List<Any> = buildList {
        var lastCategory: ChoreographerCategory? = null
        items.forEach { item ->
            if (item.category != lastCategory) {
                add(item.category)
                lastCategory = item.category
            }
            add(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (itemsWithHeaders[position]) {
            is ChoreographerCategory -> TYPE_HEADER
            else -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(
                ItemCategoryHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> ItemViewHolder(
                ItemChoreographerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onItemClick
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                val category = itemsWithHeaders[position] as ChoreographerCategory
                holder.bind(category)
            }
            is ItemViewHolder -> {
                val item = itemsWithHeaders[position] as ChoreographerItem
                holder.bind(item)
            }
        }
    }

    override fun getItemCount(): Int = itemsWithHeaders.size

    class HeaderViewHolder(
        private val binding: ItemCategoryHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: ChoreographerCategory) {
            binding.tvCategoryHeader.text = category.displayName
            val colorRes = when (category) {
                ChoreographerCategory.BASIC -> R.color.tab_choreographer
                ChoreographerCategory.MONITOR -> R.color.tab_timer
                ChoreographerCategory.ANIMATION -> R.color.tab_handler
                ChoreographerCategory.ADVANCED -> R.color.tab_debug
            }
            binding.tvCategoryHeader.setTextColor(
                ContextCompat.getColor(binding.root.context, colorRes)
            )
        }
    }

    class ItemViewHolder(
        private val binding: ItemChoreographerBinding,
        private val onItemClick: (ChoreographerType) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChoreographerItem) {
            binding.apply {
                tvTitle.text = item.title
                tvDescription.text = item.description

                val colorRes = when (item.category) {
                    ChoreographerCategory.BASIC -> R.color.tab_choreographer
                    ChoreographerCategory.MONITOR -> R.color.tab_timer
                    ChoreographerCategory.ANIMATION -> R.color.tab_handler
                    ChoreographerCategory.ADVANCED -> R.color.tab_debug
                }
                val color = ContextCompat.getColor(binding.root.context, colorRes)
                viewColorDot.setBackgroundColor(color)
                cardView.strokeColor = color
                
                cardView.setOnClickListener {
                    onItemClick(item.type)
                }
            }
        }
    }
}
