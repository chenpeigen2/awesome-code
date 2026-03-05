package com.peter.recyclerview.demo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peter.recyclerview.demo.R
import com.peter.recyclerview.demo.model.MultiTypeItem

/**
 * 多类型 Item Adapter
 * 
 * 实现方式：
 * 1. 使用 sealed class 定义不同类型的数据模型
 * 2. 通过 getItemViewType() 返回不同类型
 * 3. 创建不同的 ViewHolder 处理不同类型的数据
 * 
 * 适用场景：
 * - 聊天列表（文字、图片、语音等）
 * - 新闻列表（标题、广告、视频等）
 * - 订单列表（订单项、分割线、Header等）
 */
class MultiTypeAdapter : ListAdapter<MultiTypeItem, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_TEXT_MINE = 0      // 我的文字消息
        private const val TYPE_TEXT_OTHER = 1     // 他人的文字消息
        private const val TYPE_IMAGE_MINE = 2     // 我的图片消息
        private const val TYPE_IMAGE_OTHER = 3    // 他人的图片消息
        private const val TYPE_TIME_DIVIDER = 4   // 时间分割线
        private const val TYPE_SYSTEM_NOTICE = 5  // 系统通知
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is MultiTypeItem.TextMessage -> if (item.isMine) TYPE_TEXT_MINE else TYPE_TEXT_OTHER
            is MultiTypeItem.ImageMessage -> if (item.isMine) TYPE_IMAGE_MINE else TYPE_IMAGE_OTHER
            is MultiTypeItem.TimeDivider -> TYPE_TIME_DIVIDER
            is MultiTypeItem.SystemNotice -> TYPE_SYSTEM_NOTICE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_TEXT_MINE -> TextMessageViewHolder(parent, true)
            TYPE_TEXT_OTHER -> TextMessageViewHolder(parent, false)
            TYPE_IMAGE_MINE -> ImageMessageViewHolder(parent, true)
            TYPE_IMAGE_OTHER -> ImageMessageViewHolder(parent, false)
            TYPE_TIME_DIVIDER -> TimeDividerViewHolder(parent)
            TYPE_SYSTEM_NOTICE -> SystemNoticeViewHolder(parent)
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is MultiTypeItem.TextMessage -> (holder as TextMessageViewHolder).bind(item)
            is MultiTypeItem.ImageMessage -> (holder as ImageMessageViewHolder).bind(item)
            is MultiTypeItem.TimeDivider -> (holder as TimeDividerViewHolder).bind(item)
            is MultiTypeItem.SystemNotice -> (holder as SystemNoticeViewHolder).bind(item)
        }
    }

    // ============ ViewHolders ============

    /**
     * 文字消息 ViewHolder
     */
    inner class TextMessageViewHolder(
        parent: ViewGroup,
        private val isMine: Boolean
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            if (isMine) R.layout.item_text_message_right else R.layout.item_text_message_left,
            parent,
            false
        )
    ) {
        private val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(item: MultiTypeItem.TextMessage) {
            tvContent.text = item.content
            tvTime.text = item.time
        }
    }

    /**
     * 图片消息 ViewHolder
     */
    inner class ImageMessageViewHolder(
        parent: ViewGroup,
        private val isMine: Boolean
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            if (isMine) R.layout.item_image_message_right else R.layout.item_image_message_left,
            parent,
            false
        )
    ) {
        private val imageView: View = itemView.findViewById(R.id.imageView)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(item: MultiTypeItem.ImageMessage) {
            // 实际项目中应该使用图片加载库加载图片
            tvTime.text = item.time
        }
    }

    /**
     * 时间分割线 ViewHolder
     */
    inner class TimeDividerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_time_divider, parent, false)
    ) {
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(item: MultiTypeItem.TimeDivider) {
            tvTime.text = item.time
        }
    }

    /**
     * 系统通知 ViewHolder
     */
    inner class SystemNoticeViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_system_notice, parent, false)
    ) {
        private val tvContent: TextView = itemView.findViewById(R.id.tvContent)

        fun bind(item: MultiTypeItem.SystemNotice) {
            tvContent.text = item.content
        }
    }

    // ============ DiffCallback ============

    class DiffCallback : DiffUtil.ItemCallback<MultiTypeItem>() {
        override fun areItemsTheSame(oldItem: MultiTypeItem, newItem: MultiTypeItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MultiTypeItem, newItem: MultiTypeItem): Boolean {
            return oldItem == newItem
        }
    }
}
