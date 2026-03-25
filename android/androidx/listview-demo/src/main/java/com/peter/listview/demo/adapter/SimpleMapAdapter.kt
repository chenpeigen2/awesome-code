package com.peter.listview.demo.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SimpleAdapter
import android.widget.TextView
import com.peter.listview.demo.R
import com.peter.listview.demo.model.IconItem

/**
 * SimpleAdapter 扩展 - 使用 Map 数据源
 *
 * SimpleAdapter 适用于：
 * - 数据来自 Map 结构（如 JSON 解析结果）
 * - 需要将多个字段绑定到多个 View
 * - 相对静态的数据展示
 *
 * 使用方式：
 * ```kotlin
 * val data = listOf(
 *     mapOf("title" to "标题1", "icon" to R.drawable.ic_star),
 *     mapOf("title" to "标题2", "icon" to R.drawable.ic_person)
 * )
 * val adapter = SimpleMapAdapter(
 *     context,
 *     data,
 *     R.layout.item_with_icon,
 *     arrayOf("title", "icon"),
 *     intArrayOf(R.id.title, R.id.icon)
 * )
 * ```
 */
class SimpleMapAdapter(
    context: Context,
    data: List<Map<String, Any>>,
    resource: Int,
    from: Array<String>,
    to: IntArray
) : SimpleAdapter(context, data, resource, from, to) {

    /**
     * ViewBinder 允许自定义数据绑定逻辑
     *
     * 当 SimpleAdapter 的默认绑定逻辑不满足需求时，
     * 可以通过 setViewBinder() 设置自定义绑定器
     */
    private var customViewBinder: ViewBinder? = null

    override fun setViewBinder(viewBinder: ViewBinder?) {
        customViewBinder = viewBinder
        super.setViewBinder(viewBinder)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // SimpleAdapter 内部已实现 convertView 复用
        return super.getView(position, convertView, parent)
    }
}

/**
 * 将 IconItem 列表转换为 SimpleAdapter 需要的 Map 列表
 */
fun IconItem.toMap(): Map<String, Any> {
    val iconRes = when (iconType) {
        IconItem.IconType.STAR -> R.drawable.ic_tab_advanced
        IconItem.IconType.PERSON -> R.drawable.ic_tab_list
        IconItem.IconType.MESSAGE -> R.drawable.ic_tab_code
        IconItem.IconType.SETTINGS -> R.drawable.ic_tab_advanced
        IconItem.IconType.PHONE -> R.drawable.ic_tab_database
        IconItem.IconType.EMAIL -> R.drawable.ic_tab_code
    }
    return mapOf(
        "id" to id,
        "icon" to iconRes,
        "title" to title,
        "description" to description
    )
}

fun List<IconItem>.toSimpleAdapterData(): List<Map<String, Any>> {
    return this.map { it.toMap() }
}
