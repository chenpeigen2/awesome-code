package com.peter.listview.demo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.peter.listview.demo.R
import com.peter.listview.demo.model.MultiTypeItem

/**
 * 多类型 Adapter - 展示多种 Item 布局
 *
 * 核心方法：
 * - getViewTypeCount(): 返回 Item 类型数量
 * - getItemViewType(position): 返回指定位置的 Item 类型
 *
 * 使用场景：
 * - 列表中有多种不同布局的 Item（如聊天列表、时间轴等）
 * - 需要根据数据类型显示不同的 UI
 */
class MultiTypeAdapter(
    private val context: Context,
    private var items: List<MultiTypeItem>
) : BaseAdapter() {

    companion object {
        // 定义 Item 类型常量
        private const val TYPE_HEADER = 0
        private const val TYPE_CONTENT = 1
    }

    /**
     * 返回 Item 类型数量
     * 必须与 getItemViewType 返回的类型数量一致
     */
    override fun getViewTypeCount(): Int = 2

    /**
     * 返回指定位置的 Item 类型
     * ListView 会根据类型缓存不同类型的 convertView
     */
    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is MultiTypeItem.Header -> TYPE_HEADER
            is MultiTypeItem.Content -> TYPE_CONTENT
        }
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = items[position]

        return when (item) {
            is MultiTypeItem.Header -> {
                // Header 类型视图
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.item_group_header, parent, false)
                val textView = view.findViewById<TextView>(R.id.header_text)
                textView.text = item.title
                view
            }
            is MultiTypeItem.Content -> {
                // Content 类型视图
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.item_simple_text, parent, false)
                val textView = view.findViewById<TextView>(R.id.text_view)
                textView.text = "${item.title}\n${item.description}"
                view
            }
        }
    }

    /**
     * 启用稳定 ID
     * 注意：BaseAdapter 默认 hasStableIds = false
     * 如果启用，需要确保 getItemId 对同一数据项始终返回相同 ID
     */
    fun updateItems(newItems: List<MultiTypeItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
