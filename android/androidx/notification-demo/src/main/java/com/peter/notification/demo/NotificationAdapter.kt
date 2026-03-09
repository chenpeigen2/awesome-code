package com.peter.notification.demo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.peter.notification.demo.databinding.ItemCategoryHeaderBinding
import com.peter.notification.demo.databinding.ItemNotificationTypeBinding
import com.peter.notification.demo.fragments.ChannelFragment.Companion.dpToPx

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
                ItemCategoryHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
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

    class HeaderViewHolder(
        private val binding: ItemCategoryHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: NotificationCategory) {
            binding.tvCategoryHeader.text = category.displayName
            
            // 设置分类标题颜色
            val colorRes = when (category) {
                NotificationCategory.BASIC -> R.color.category_basic
                NotificationCategory.MESSAGE -> R.color.category_message
                NotificationCategory.PROGRESS -> R.color.category_progress
                NotificationCategory.MEDIA -> R.color.category_media
                NotificationCategory.ADVANCED -> R.color.category_advanced
            }
            binding.tvCategoryHeader.setTextColor(
                ContextCompat.getColor(binding.root.context, colorRes)
            )
        }
    }

    class ItemViewHolder(
        private val binding: ItemNotificationTypeBinding,
        private val onItemClick: (NotificationType) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NotificationItem) {
            val context = binding.root.context
            
            binding.apply {
                tvTitle.text = item.title
                tvDescription.text = item.description

                // 根据分类设置颜色圆点和阴影颜色
                val (dotRes, shadowColor) = when (item.category) {
                    NotificationCategory.BASIC -> 
                        R.drawable.bg_dot_basic to ContextCompat.getColor(context, R.color.category_basic)
                    NotificationCategory.MESSAGE -> 
                        R.drawable.bg_dot_message to ContextCompat.getColor(context, R.color.category_message)
                    NotificationCategory.PROGRESS -> 
                        R.drawable.bg_dot_progress to ContextCompat.getColor(context, R.color.category_progress)
                    NotificationCategory.MEDIA -> 
                        R.drawable.bg_dot_media to ContextCompat.getColor(context, R.color.category_media)
                    NotificationCategory.ADVANCED -> 
                        R.drawable.bg_dot_advanced to ContextCompat.getColor(context, R.color.category_advanced)
                }
                
                viewColorDot.setBackgroundResource(dotRes)
                
                // 设置 elevation 和带颜色的阴影 (API 28+)
                cardView.elevation = dpToPx(4, context).toFloat()
                cardView.outlineAmbientShadowColor = shadowColor
                cardView.outlineSpotShadowColor = shadowColor

                // 点击整个卡片发送通知
                cardView.setOnClickListener {
                    onItemClick(item.type)
                }
            }
        }
    }
}
