package com.peter.touch.demo

/**
 * 列表项数据模型
 */
data class TouchItem(
    val id: String,
    val title: String,
    val description: String,
    val category: Category,
    val activityClass: Class<*>
) {
    /**
     * 分类枚举
     */
    enum class Category {
        BASIC,        // 基础
        INTERMEDIATE, // 进阶
        ADVANCED;     // 高级

        fun getDisplayName(): String = when (this) {
            BASIC -> "基础"
            INTERMEDIATE -> "进阶"
            ADVANCED -> "高级"
        }

        fun getColor(): Int = when (this) {
            BASIC -> 0xFF4CAF50.toInt()      // 绿色
            INTERMEDIATE -> 0xFF2196F3.toInt() // 蓝色
            ADVANCED -> 0xFFFF9800.toInt()     // 橙色
        }
    }
}
