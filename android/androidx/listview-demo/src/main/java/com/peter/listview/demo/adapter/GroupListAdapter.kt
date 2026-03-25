package com.peter.listview.demo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SectionIndexer
import android.widget.TextView
import com.peter.listview.demo.R
import com.peter.listview.demo.model.GroupedItem
import com.peter.listview.demo.model.SimpleItem

/**
 * 分组列表 Adapter - 实现 SectionIndexer 接口
 *
 * 分组列表适用于：
 * - 通讯录式样的字母分组
 * - 分类数据展示
 * - 需要快速定位的场景
 *
 * SectionIndexer 接口提供：
 * - 右侧字母快速定位索引
 * - 快速滚动到指定分组
 */
class GroupListAdapter(
    private val context: Context,
    private var items: List<GroupedItem<SimpleItem>>
) : BaseAdapter(), SectionIndexer {

    // 分组标题列表（用于快速定位）
    private var sections: Array<String> = emptyArray()

    // 每个分组起始位置的映射
    private var sectionPositions: IntArray = intArrayOf()

    init {
        updateSections()
    }

    /**
     * 更新分组信息
     */
    private fun updateSections() {
        val sectionSet = mutableSetOf<String>()
        val positions = mutableListOf<Int>()

        items.forEachIndexed { index, item ->
            if (item.section !in sectionSet) {
                sectionSet.add(item.section)
                positions.add(index)
            }
        }

        sections = sectionSet.toTypedArray()
        sectionPositions = positions.toIntArray()
    }

    // ========== BaseAdapter 实现 ==========

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = items[position].data.id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = items[position]

        // 判断是否需要显示分组标题
        val showHeader = position == 0 || items[position - 1].section != item.section

        return if (showHeader) {
            // 带分组标题的视图
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.item_with_header, parent, false)

            val headerText = view.findViewById<TextView>(R.id.header_text)
            val contentText = view.findViewById<TextView>(R.id.content_text)

            headerText.text = item.section
            contentText.text = item.data.title

            view
        } else {
            // 普通内容视图
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.item_simple_text, parent, false)

            val textView = view.findViewById<TextView>(R.id.text_view)
            textView.text = item.data.title

            view
        }
    }

    // ========== SectionIndexer 实现 ==========

    /**
     * 获取所有分组标题
     * 用于显示右侧快速定位索引
     */
    override fun getSections(): Array<Any> = sections as Array<Any>

    /**
     * 根据分组索引获取该分组在列表中的起始位置
     * @param sectionIndex 分组在 sections 数组中的索引
     * @return 该分组在列表中的起始位置
     */
    override fun getPositionForSection(sectionIndex: Int): Int {
        if (sectionIndex < 0 || sectionIndex >= sectionPositions.size) {
            return 0
        }
        return sectionPositions[sectionIndex]
    }

    /**
     * 根据列表位置获取对应的分组索引
     * @param position 列表中的位置
     * @return 该位置对应分组在 sections 数组中的索引
     */
    override fun getSectionForPosition(position: Int): Int {
        if (position < 0 || position >= items.size) {
            return 0
        }

        val section = items[position].section
        return sections.indexOf(section)
    }

    // ========== 数据更新方法 ==========

    fun updateItems(newItems: List<GroupedItem<SimpleItem>>) {
        items = newItems
        updateSections()
        notifyDataSetChanged()
    }
}
