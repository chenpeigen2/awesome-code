package com.peter.os.demo

/**
 * Choreographer 示例类型
 */
enum class ChoreographerType {
    BASIC_CALLBACK,
    FPS_MONITOR,
    FRAME_TIME,
    CUSTOM_ANIMATION,
    SURFACE_DRAW,
    DROP_FRAME_DETECT
}

/**
 * Choreographer 示例分类
 */
enum class ChoreographerCategory(val displayName: String) {
    BASIC("基础用法"),
    MONITOR("帧率监控"),
    ANIMATION("自定义动画"),
    ADVANCED("高级应用")
}

/**
 * Choreographer 示例数据模型
 */
data class ChoreographerItem(
    val type: ChoreographerType,
    val title: String,
    val description: String,
    val category: ChoreographerCategory,
    val action: () -> Unit = {}
)
