package com.peter.layoutinflater.demo

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * 主页菜单 Adapter
 * 
 * 这个 Adapter 本身也是 LayoutInflater 的一个典型使用场景示例
 */
class MainAdapter(
    private val items: List<MenuItem>,
    private val onItemClick: (MenuItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].isHeader) TYPE_HEADER else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // 【LayoutInflater 典型用法】
        // 在 RecyclerView 中使用 LayoutInflater 加载 item 布局
        // inflate(resource, root, attachToRoot)
        // - resource: 布局资源 ID
        // - root: 父容器，用于提供 LayoutParams
        // - attachToRoot: false，因为 RecyclerView 会负责添加
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(parent)
            else -> ItemViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is HeaderViewHolder -> holder.bind(item)
            is ItemViewHolder -> holder.bind(item, onItemClick)
        }
    }

    override fun getItemCount(): Int = items.size

    /**
     * Header ViewHolder
     */
    class HeaderViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        // 【LayoutInflater 标准用法】
        // from(parent.context) 获取 LayoutInflater 实例
        // parent 用于生成正确的 LayoutParams
        // false 表示不立即添加到 parent
        LayoutInflater.from(parent.context).inflate(R.layout.item_menu_header, parent, false)
    ) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)

        fun bind(item: MenuItem) {
            tvTitle.text = item.title
        }
    }

    /**
     * Item ViewHolder
     */
    class ItemViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
    ) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDesc: TextView = itemView.findViewById(R.id.tvDesc)

        fun bind(item: MenuItem, onClick: (MenuItem) -> Unit) {
            tvTitle.text = item.title
            tvDesc.text = item.description
            itemView.setOnClickListener { onClick(item) }
        }
    }
}
