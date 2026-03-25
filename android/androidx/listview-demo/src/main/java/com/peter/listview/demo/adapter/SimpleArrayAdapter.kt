package com.peter.listview.demo.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.peter.listview.demo.R

/**
 * 简单的 ArrayAdapter 扩展
 *
 * ArrayAdapter 是最简单的 Adapter，适用于：
 * - 简单的字符串列表
 * - 单一数据源的列表展示
 *
 * 使用方式：
 * ```kotlin
 * val adapter = SimpleArrayAdapter(context, android.R.layout.simple_list_item_1, items)
 * listView.adapter = adapter
 * ```
 */
class SimpleArrayAdapter(
    context: Context,
    resource: Int,
    objects: MutableList<String>
) : ArrayAdapter<String>(context, resource, objects) {

    /**
     * 自定义 getItemViewType 示例
     * 注意：ArrayAdapter 默认不支持多类型，这里仅作演示
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // ArrayAdapter 内部已经实现了 convertView 复用
        // 这里我们获取 View 并进行自定义处理
        val view = super.getView(position, convertView, parent)

        // 获取 TextView 并设置自定义样式
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView?.apply {
            textSize = 16f
            setPadding(32, 24, 32, 24)
        }

        return view
    }
}
