package com.peter.recyclerview.demo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peter.recyclerview.demo.R
import com.peter.recyclerview.demo.model.SimpleItem

/**
 * 带头部和尾部的 Adapter
 * 
 * 实现方式：通过 viewType 区分不同类型的 Item
 * 
 * 关键知识点：
 * - getItemViewType() 返回不同的 viewType
 * - onCreateViewHolder() 根据 viewType 创建不同的 ViewHolder
 * - onBindViewHolder() 根据 viewHolder 类型绑定数据
 * - 注意：由于添加了 header 和 footer，数据 position 需要调整
 */
class HeaderFooterAdapter : ListAdapter<SimpleItem, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
        private const val TYPE_FOOTER = 2
    }

    // 是否显示 header 和 footer
    var showHeader: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    var showFooter: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    // 计算实际的 header 数量
    private val headerCount: Int get() = if (showHeader) 1 else 0
    private val footerCount: Int get() = if (showFooter) 1 else 0

    // 重写 getItemCount，加上 header 和 footer
    override fun getItemCount(): Int {
        return super.getItemCount() + headerCount + footerCount
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position < headerCount -> TYPE_HEADER
            position >= itemCount - footerCount -> TYPE_FOOTER
            else -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(parent)
            TYPE_FOOTER -> FooterViewHolder(parent)
            else -> ItemViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemViewHolder -> {
                // 获取数据时需要减去 header 数量
                val dataIndex = position - headerCount
                holder.bind(getItem(dataIndex))
            }
            is HeaderViewHolder -> holder.bind()
            is FooterViewHolder -> holder.bind()
        }
    }

    // ============ ViewHolders ============

    inner class HeaderViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false)
    ) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        
        fun bind() {
            tvTitle.text = "这是列表头部"
        }
    }

    inner class FooterViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_footer, parent, false)
    ) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        
        fun bind() {
            tvTitle.text = "这是列表尾部 · 已加载全部"
        }
    }

    inner class ItemViewHolder(parent: ViewGroup) : BaseViewHolder<SimpleItem>(
        parent, R.layout.item_simple
    ) {
        private val colorView: View = itemView.findViewById(R.id.colorView)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDesc: TextView = itemView.findViewById(R.id.tvDesc)

        override fun bind(item: SimpleItem) {
            tvTitle.text = item.title
            tvDesc.text = item.description
            itemView.setBackgroundResource(item.colorRes)
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
