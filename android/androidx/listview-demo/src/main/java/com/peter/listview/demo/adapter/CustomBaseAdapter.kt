package com.peter.listview.demo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.peter.listview.demo.R

/**
 * 图标类型枚举
 */
enum class IconType {
    STAR, PERSON, MESSAGE, SETTINGS, PHONE, EMAIL
}

/**
 * 带图标的数据项
 */
data class IconItem(
    val id: Long,
    val iconType: IconType,
    val title: String,
    val description: String
)

/**
 * BaseAdapter 实现示例 - 展示 ViewHolder 模式
 *
 * BaseAdapter 是最灵活的 Adapter，需要实现 4 个核心方法：
 * - getCount(): 返回数据总数
 * - getItem(position): 返回指定位置的数据
 * - getItemId(position): 返回指定位置的 Item ID
 * - getView(position, convertView, parent): 返回 Item 视图
 *
 * ViewHolder 模式的作用：
 * - 缓存 View 引用，避免重复 findViewById()
 * - 提高列表滚动流畅度
 */
class CustomBaseAdapter(
    private val context: Context,
    var items: List<IconItem>
) : BaseAdapter() {

    /**
     * ViewHolder 用于缓存 View 引用
     * 声明为静态内部类，避免持有外部类引用
     */
    private class ViewHolder {
        lateinit var icon: ImageView
        lateinit var title: TextView
        lateinit var description: TextView
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = items[position].id

    /**
     * 核心方法：获取 Item 视图
     *
     * @param position 当前 Item 位置
     * @param convertView 可复用的视图（可能为 null）
     * @param parent 父容器
     * @return Item 视图
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val view: View

        // convertView 复用机制
        if (convertView == null) {
            // 首次创建，需要 inflate 布局
            view = LayoutInflater.from(context)
                .inflate(R.layout.item_with_icon, parent, false)

            // 创建 ViewHolder 并缓存 View 引用
            holder = ViewHolder().apply {
                icon = view.findViewById(R.id.icon)
                title = view.findViewById(R.id.title)
                description = view.findViewById(R.id.subtitle)
            }

            // 将 ViewHolder 存储在 View 的 tag 中
            view.tag = holder
        } else {
            // 复用已有视图
            view = convertView
            // 从 tag 中获取缓存的 ViewHolder
            holder = view.tag as ViewHolder
        }

        // 绑定数据
        val item = items[position]
        holder.apply {
            setImageResource(icon, item.iconType)
            title.text = item.title
            description.text = item.description
        }

        return view
    }

    /**
     * 更新数据并刷新列表
     * 在主线程调用，ListView 会自动处理动画
     */
    fun updateItems(newItems: List<IconItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    /**
     * 设置图标资源
     */
    private fun setImageResource(imageView: ImageView, iconType: IconType) {
        val resId = when (iconType) {
            IconType.STAR -> R.drawable.ic_tab_advanced
            IconType.PERSON -> R.drawable.ic_tab_list
            IconType.MESSAGE -> R.drawable.ic_tab_code
            IconType.SETTINGS -> R.drawable.ic_tab_advanced
            IconType.PHONE -> R.drawable.ic_tab_database
            IconType.EMAIL -> R.drawable.ic_tab_code
        }
        imageView.setImageResource(resId)
    }
}
