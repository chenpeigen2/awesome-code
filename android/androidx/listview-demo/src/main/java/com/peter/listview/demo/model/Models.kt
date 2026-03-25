package com.peter.listview.demo.model

/**
 * 简单文本项 - 用于 ArrayAdapter 基础演示
 */
data class SimpleItem(
    val id: Long,
    val title: String,
    val subtitle: String? = null
) {
    override fun toString(): String = title
}

/**
 * 带图标的项 - 用于自定义 ArrayAdapter 和 BaseAdapter
 */
data class IconItem(
    val id: Long,
    val iconType: IconType,
    val title: String,
    val description: String
) {
    enum class IconType {
        STAR, PERSON, MESSAGE, SETTINGS, PHONE, EMAIL
    }
}

/**
 * 用户数据 - 用于 CursorAdapter 演示
 * 对应 SQLite 数据库中的 users 表
 */
data class User(
    val id: Long,
    val name: String,
    val phone: String
)

/**
 * 多类型 Item - 用于多类型 Adapter 演示
 */
sealed class MultiTypeItem {
    data class Header(val title: String) : MultiTypeItem()
    data class Content(val id: Long, val title: String, val description: String) : MultiTypeItem()
}

/**
 * 分组项 - 用于分组列表演示
 */
data class GroupedItem<T>(
    val section: String,  // 分组标识（如首字母）
    val data: T
)
