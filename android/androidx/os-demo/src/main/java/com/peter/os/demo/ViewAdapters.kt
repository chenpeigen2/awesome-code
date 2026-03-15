package com.peter.os.demo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.peter.os.demo.databinding.ItemCategoryHeaderBinding
import com.peter.os.demo.databinding.ItemViewDemoBinding

/**
 * SurfaceView 示例列表适配器
 */
class SurfaceViewAdapter(
    private val items: List<SurfaceViewItem>,
    private val onItemClick: (SurfaceViewType) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    private val itemsWithHeaders: List<Any> = buildList {
        var lastCategory: ViewCategory? = null
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
            is ViewCategory -> TYPE_HEADER
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
                ItemViewDemoBinding.inflate(
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
                val category = itemsWithHeaders[position] as ViewCategory
                holder.bind(category, R.color.tab_surface_view)
            }
            is ItemViewHolder -> {
                val item = itemsWithHeaders[position] as SurfaceViewItem
                holder.bind(item, R.color.tab_surface_view)
            }
        }
    }

    override fun getItemCount(): Int = itemsWithHeaders.size

    class HeaderViewHolder(
        private val binding: ItemCategoryHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: ViewCategory, colorRes: Int) {
            binding.tvCategoryHeader.text = category.displayName
            binding.tvCategoryHeader.setTextColor(
                ContextCompat.getColor(binding.root.context, colorRes)
            )
        }
    }

    class ItemViewHolder(
        private val binding: ItemViewDemoBinding,
        private val onItemClick: (SurfaceViewType) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SurfaceViewItem, colorRes: Int) {
            binding.apply {
                tvTitle.text = item.title
                tvDescription.text = item.description

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

/**
 * TextureView 示例列表适配器
 */
class TextureViewAdapter(
    private val items: List<TextureViewItem>,
    private val onItemClick: (TextureViewType) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    private val itemsWithHeaders: List<Any> = buildList {
        var lastCategory: ViewCategory? = null
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
            is ViewCategory -> TYPE_HEADER
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
                ItemViewDemoBinding.inflate(
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
                val category = itemsWithHeaders[position] as ViewCategory
                holder.bind(category, R.color.tab_texture_view)
            }
            is ItemViewHolder -> {
                val item = itemsWithHeaders[position] as TextureViewItem
                holder.bind(item, R.color.tab_texture_view)
            }
        }
    }

    override fun getItemCount(): Int = itemsWithHeaders.size

    class HeaderViewHolder(
        private val binding: ItemCategoryHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: ViewCategory, colorRes: Int) {
            binding.tvCategoryHeader.text = category.displayName
            binding.tvCategoryHeader.setTextColor(
                ContextCompat.getColor(binding.root.context, colorRes)
            )
        }
    }

    class ItemViewHolder(
        private val binding: ItemViewDemoBinding,
        private val onItemClick: (TextureViewType) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TextureViewItem, colorRes: Int) {
            binding.apply {
                tvTitle.text = item.title
                tvDescription.text = item.description

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
