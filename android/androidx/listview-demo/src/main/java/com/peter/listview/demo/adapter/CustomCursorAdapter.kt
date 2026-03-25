package com.peter.listview.demo.adapter

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import com.peter.listview.demo.R
import com.peter.listview.demo.databinding.ItemContactBinding
import com.peter.listview.demo.helper.DatabaseHelper

/**
 * CursorAdapter 实现 - 直接使用 SQLite Cursor
 *
 * CursorAdapter 适用于：
 * - 数据来自 SQLite 数据库
 * - 需要高效地显示大量数据库记录
 * - 配合 SearchView 进行搜索过滤
 *
 * 重要注意事项：
 * 1. Cursor 必须包含 "_id" 列
 * 2. newView() 和 bindView() 分离，提高效率
 * 3. 需要手动管理 Cursor 的关闭
 *
 * 注意：CursorAdapter 已被废弃，现代开发建议使用 Room + RecyclerView
 * 这里仅作为学习 CursorAdapter 原理的示例
 */
@Suppress("DEPRECATION")
class CustomCursorAdapter(
    context: Context,
    cursor: Cursor?
) : CursorAdapter(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER) {

    companion object {
        private const val TAG = "CustomCursorAdapter"
    }

    /**
     * ViewHolder 用于缓存 View 引用
     * 虽然是 CursorAdapter，但 ViewHolder 模式仍然有效
     */
    private class ViewHolder {
        lateinit var nameTextView: TextView
        lateinit var phoneTextView: TextView
        lateinit var sectionIndexTextView: TextView
    }

    /**
     * 创建新视图
     * CursorAdapter 将视图创建和数据绑定分离，提高效率
     */
    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(context), parent, false)
        val holder = ViewHolder().apply {
            nameTextView = binding.name
            phoneTextView = binding.phone
            sectionIndexTextView = binding.sectionIndex
        }
        binding.root.tag = holder
        return binding.root
    }

    /**
     * 绑定数据到视图
     * 当 convertView 不为 null 时，直接复用并绑定新数据
     */
    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        cursor ?: return

        val holder = view?.tag as? ViewHolder ?: return

        // 从 Cursor 读取数据
        val nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)
        val phoneIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE)

        val name = cursor.getString(nameIndex)
        val phone = cursor.getString(phoneIndex)

        // 绑定到视图
        holder.nameTextView.text = name
        holder.phoneTextView.text = phone

        // 分组首字母显示
        val firstChar = name.firstOrNull()?.uppercaseChar()?.toString() ?: "#"
        holder.sectionIndexTextView.text = firstChar
        holder.sectionIndexTextView.visibility = View.VISIBLE

        // 如果是同组的第一个，显示分组标题
        val position = cursor.position
        if (position == 0) {
            holder.sectionIndexTextView.visibility = View.VISIBLE
        } else {
            cursor.moveToPrevious()
            val prevNameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)
            val prevFirstChar = cursor.getString(prevNameIndex).firstOrNull()?.uppercaseChar()?.toString() ?: "#"
            cursor.moveToNext()

            if (firstChar == prevFirstChar) {
                holder.sectionIndexTextView.visibility = View.INVISIBLE
            } else {
                holder.sectionIndexTextView.visibility = View.VISIBLE
            }
        }
    }

    /**
     * 更新 Cursor 并刷新列表
     */
    fun updateCursor(newCursor: Cursor?) {
        val oldCursor = swapCursor(newCursor)
        oldCursor?.close()
    }
}
