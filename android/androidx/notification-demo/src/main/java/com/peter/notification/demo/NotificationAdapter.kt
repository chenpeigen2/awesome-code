package com.peter.notification.demo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.peter.notification.demo.databinding.ItemCategoryHeaderBinding
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

                // 根据分类设置图标和背景
                val (iconRes, bgRes, iconTint) = when (item.type) {
                    NotificationType.NORMAL -> Triple(R.drawable.ic_notification_normal, R.drawable.bg_icon_basic, R.color.category_basic)
                    NotificationType.BIG_TEXT -> Triple(R.drawable.ic_notification_big_text, R.drawable.bg_icon_basic, R.color.category_basic)
                    NotificationType.BIG_PICTURE -> Triple(R.drawable.ic_notification_big_picture, R.drawable.bg_icon_basic, R.color.category_basic)
                    NotificationType.INBOX -> Triple(R.drawable.ic_notification_inbox, R.drawable.bg_icon_basic, R.color.category_basic)
                    
                    NotificationType.MESSAGING -> Triple(R.drawable.ic_notification_messaging, R.drawable.bg_icon_message, R.color.category_message)
                    NotificationType.CONVERSATION -> Triple(R.drawable.ic_notification_conversation, R.drawable.bg_icon_message, R.color.category_message)
                    NotificationType.BUBBLE -> Triple(R.drawable.ic_notification_bubble, R.drawable.bg_icon_message, R.color.category_message)
                    
                    NotificationType.PROGRESS -> Triple(R.drawable.ic_notification_progress, R.drawable.bg_icon_progress, R.color.category_progress)
                    NotificationType.ONGOING -> Triple(R.drawable.ic_notification_ongoing, R.drawable.bg_icon_progress, R.color.category_progress)
                    NotificationType.FOREGROUND -> Triple(R.drawable.ic_notification_foreground, R.drawable.bg_icon_progress, R.color.category_progress)
                    
                    NotificationType.MEDIA -> Triple(R.drawable.ic_notification_media, R.drawable.bg_icon_media, R.color.category_media)
                    
                    NotificationType.CUSTOM -> Triple(R.drawable.ic_notification_custom, R.drawable.bg_icon_advanced, R.color.category_advanced)
                    NotificationType.SCHEDULED -> Triple(R.drawable.ic_notification_scheduled, R.drawable.bg_icon_advanced, R.color.category_advanced)
                    NotificationType.ACTION -> Triple(R.drawable.ic_notification_action, R.drawable.bg_icon_advanced, R.color.category_advanced)
                }

                // 设置图标
                ivIcon.setImageResource(iconRes)
                ivIcon.setColorFilter(ContextCompat.getColor(context, iconTint))
                
                // 设置图标背景
                viewIconBg.setBackgroundResource(bgRes)

                // 点击整个卡片发送通知
                cardView.setOnClickListener {
                    onItemClick(item.type)
                }
            }
        }
    }
}
