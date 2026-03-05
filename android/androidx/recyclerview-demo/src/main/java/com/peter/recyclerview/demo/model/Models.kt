package com.peter.recyclerview.demo.model

/**
 * 基础列表项数据模型
 */
data class SimpleItem(
    val id: Int,
    val title: String,
    val description: String,
    val colorRes: Int
)

/**
 * 多类型 Item 数据模型
 */
sealed class MultiTypeItem {
    abstract val id: Int
    
    // 文本消息
    data class TextMessage(
        override val id: Int,
        val content: String,
        val isMine: Boolean,
        val time: String
    ) : MultiTypeItem()
    
    // 图片消息
    data class ImageMessage(
        override val id: Int,
        val imageUrl: String,
        val isMine: Boolean,
        val time: String
    ) : MultiTypeItem()
    
    // 时间分割线
    data class TimeDivider(
        override val id: Int,
        val time: String
    ) : MultiTypeItem()
    
    // 系统通知
    data class SystemNotice(
        override val id: Int,
        val content: String
    ) : MultiTypeItem()
}

/**
 * 瀑布流图片数据模型
 */
data class StaggeredItem(
    val id: Int,
    val title: String,
    val imageRes: Int,
    val ratio: Float // 图片宽高比
)

/**
 * 分页数据模型
 */
data class PageItem(
    val id: Int,
    val title: String,
    val content: String
)

/**
 * 嵌套列表数据模型
 */
data class ParentItem(
    val id: Int,
    val title: String,
    val children: List<ChildItem>
)

data class ChildItem(
    val id: Int,
    val name: String,
    val description: String
)
