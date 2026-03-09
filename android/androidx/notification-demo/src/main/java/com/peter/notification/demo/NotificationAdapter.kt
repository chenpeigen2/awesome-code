package com.peter.notification.demo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.peter.notification.demo.databinding.ItemNotificationTypeBinding

/**
 * 通知类型列表适配器
 */
class NotificationAdapter(
    private val items: List<NotificationItem>,
    private val onItemClick: (NotificationType) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(
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
                tvCategory.setBackgroundColor(categoryColor)

                btnSend.setOnClickListener {
                    onItemClick(item.type)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationTypeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}