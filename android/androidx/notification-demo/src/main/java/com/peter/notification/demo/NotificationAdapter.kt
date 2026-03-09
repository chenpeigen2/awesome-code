package com.peter.notification.demo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.peter.notification.demo.databinding.ItemNotificationTypeBinding

/**
 * 通知类型列表适配器
 */
class NotificationAdapter(
    private val items: List<NotificationItem>,
    private val onItemClick: (NotificationType) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    // 构建带分类标题的列表项
    private val itemsWithHeaders: List<Any> = buildList {
        var lastCategory: NotificationCategory? = null
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
            is NotificationCategory -> TYPE_HEADER
            else -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_category_header, parent, false)
            )
            else -> ItemViewHolder(
                ItemNotificationTypeBinding.inflate(
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
                val category = itemsWithHeaders[position] as NotificationCategory
                holder.bind(category)
            }
            is ItemViewHolder -> {
                val item = itemsWithHeaders[position] as NotificationItem
                holder.bind(item)
            }
        }
    }

    override fun getItemCount(): Int = itemsWithHeaders.size

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(category: NotificationCategory) {
            // header view is simple, just displays category name
        }
    }

    class ItemViewHolder(
        private val binding: ItemNotificationTypeBinding,
        private val onItemClick: (NotificationType) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NotificationItem) {
            binding.apply {
                tvTitle.text = item.title
                tvDescription.text = item.description
                tvCategory.text = item.category.displayName

                val categoryColor = when (item.category) {
                    NotificationCategory.BASIC -> Color.parseColor("#2196F3")
                    NotificationCategory.MESSAGE -> Color.parseColor("#4CAF50")
                    NotificationCategory.PROGRESS -> Color.parseColor("#FF9800")
                    NotificationCategory.MEDIA -> Color.parseColor("#9C27B0")
                    NotificationCategory.ADVANCED -> Color.parseColor("#F44336")
                }
                viewCategoryColor.setBackgroundColor(categoryColor)
                tvCategory.setBackgroundColor(categoryColor)

                btnSend.setOnClickListener {
                    onItemClick(item.type)
                }

                cardView.setOnClickListener {
                    onItemClick(item.type)
                }
            }
        }
    }
}