package com.peter.notification.demo

import android.view.LayoutInflater
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

                // 根据分类设置图标、背景和阴影
                val iconRes: Int
                val bgRes: Int
                val iconTint: Int
                val shadowRes: Int
                
                when (item.type) {
                    NotificationType.NORMAL -> {
                        iconRes = R.drawable.ic_notification_normal
                        bgRes = R.drawable.bg_icon_basic
                        iconTint = R.color.category_basic
                        shadowRes = R.drawable.shadow_basic
                    }
                    NotificationType.BIG_TEXT -> {
                        iconRes = R.drawable.ic_notification_big_text
                        bgRes = R.drawable.bg_icon_basic
                        iconTint = R.color.category_basic
                        shadowRes = R.drawable.shadow_basic
                    }
                    NotificationType.BIG_PICTURE -> {
                        iconRes = R.drawable.ic_notification_big_picture
                        bgRes = R.drawable.bg_icon_basic
                        iconTint = R.color.category_basic
                        shadowRes = R.drawable.shadow_basic
                    }
                    NotificationType.INBOX -> {
                        iconRes = R.drawable.ic_notification_inbox
                        bgRes = R.drawable.bg_icon_basic
                        iconTint = R.color.category_basic
                        shadowRes = R.drawable.shadow_basic
                    }
                    
                    NotificationType.MESSAGING -> {
                        iconRes = R.drawable.ic_notification_messaging
                        bgRes = R.drawable.bg_icon_message
                        iconTint = R.color.category_message
                        shadowRes = R.drawable.shadow_message
                    }
                    NotificationType.CONVERSATION -> {
                        iconRes = R.drawable.ic_notification_conversation
                        bgRes = R.drawable.bg_icon_message
                        iconTint = R.color.category_message
                        shadowRes = R.drawable.shadow_message
                    }
                    NotificationType.BUBBLE -> {
                        iconRes = R.drawable.ic_notification_bubble
                        bgRes = R.drawable.bg_icon_message
                        iconTint = R.color.category_message
                        shadowRes = R.drawable.shadow_message
                    }
                    
                    NotificationType.PROGRESS -> {
                        iconRes = R.drawable.ic_notification_progress
                        bgRes = R.drawable.bg_icon_progress
                        iconTint = R.color.category_progress
                        shadowRes = R.drawable.shadow_progress
                    }
                    NotificationType.ONGOING -> {
                        iconRes = R.drawable.ic_notification_ongoing
                        bgRes = R.drawable.bg_icon_progress
                        iconTint = R.color.category_progress
                        shadowRes = R.drawable.shadow_progress
                    }
                    NotificationType.FOREGROUND -> {
                        iconRes = R.drawable.ic_notification_foreground
                        bgRes = R.drawable.bg_icon_progress
                        iconTint = R.color.category_progress
                        shadowRes = R.drawable.shadow_progress
                    }
                    
                    NotificationType.MEDIA -> {
                        iconRes = R.drawable.ic_notification_media
                        bgRes = R.drawable.bg_icon_media
                        iconTint = R.color.category_media
                        shadowRes = R.drawable.shadow_media
                    }
                    
                    NotificationType.CUSTOM -> {
                        iconRes = R.drawable.ic_notification_custom
                        bgRes = R.drawable.bg_icon_advanced
                        iconTint = R.color.category_advanced
                        shadowRes = R.drawable.shadow_advanced
                    }
                    NotificationType.SCHEDULED -> {
                        iconRes = R.drawable.ic_notification_scheduled
                        bgRes = R.drawable.bg_icon_advanced
                        iconTint = R.color.category_advanced
                        shadowRes = R.drawable.shadow_advanced
                    }
                    NotificationType.ACTION -> {
                        iconRes = R.drawable.ic_notification_action
                        bgRes = R.drawable.bg_icon_advanced
                        iconTint = R.color.category_advanced
                        shadowRes = R.drawable.shadow_advanced
                    }
                }

                // 设置图标
                ivIcon.setImageResource(iconRes)
                ivIcon.setColorFilter(ContextCompat.getColor(context, iconTint))
                
                // 设置图标背景
                viewIconBg.setBackgroundResource(bgRes)
                
                // 设置底部阴影
                viewColorShadow.setBackgroundResource(shadowRes)

                // 点击整个卡片发送通知
                cardView.setOnClickListener {
                    onItemClick(item.type)
                }
            }
        }
    }
}