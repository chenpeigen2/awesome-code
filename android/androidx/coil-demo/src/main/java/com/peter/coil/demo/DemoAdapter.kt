package com.peter.coil.demo

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DemoAdapter(
    private val items: List<DemoItem>,
    private val onItemClick: (DemoItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int) =
        if (items[position].isHeader) TYPE_HEADER else TYPE_ITEM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(parent)
            else -> ItemViewHolder(parent)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is HeaderViewHolder -> holder.bind(item)
            is ItemViewHolder -> holder.bind(item, onItemClick)
        }
    }

    override fun getItemCount() = items.size

    class HeaderViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_menu_header, parent, false)
    ) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        fun bind(item: DemoItem) { tvTitle.text = item.title }
    }

    class ItemViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
    ) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDesc: TextView = itemView.findViewById(R.id.tvDesc)
        fun bind(item: DemoItem, onClick: (DemoItem) -> Unit) {
            tvTitle.text = item.title
            tvDesc.text = item.description
            itemView.setOnClickListener { onClick(item) }
        }
    }
}
