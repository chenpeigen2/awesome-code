package com.peter.os.demo

/**
 * SurfaceView 示例类型
 */
enum class SurfaceViewType {
    BASIC_DRAW,
    ANIMATION_DRAW,
    DOUBLE_BUFFER,
    THREAD_DRAW,
    TOUCH_DRAW
}

/**
 * TextureView 示例类型
 */
enum class TextureViewType {
    BASIC_DRAW,
    ANIMATION_DRAW,
    TRANSFORM,
    LAYER_BLEND,
    SURFACE_TEXTURE
}

/**
 * View 示例分类
 */
enum class ViewCategory(val displayName: String) {
    BASIC("基础用法"),
    ANIMATION("动画绘制"),
    ADVANCED("高级应用")
}

/**
 * SurfaceView 示例数据模型
 */
data class SurfaceViewItem(
    val type: SurfaceViewType,
    val title: String,
    val description: String,
    val category: ViewCategory,
    val action: () -> Unit = {}
)

/**
 * TextureView 示例数据模型
 */
data class TextureViewItem(
    val type: TextureViewType,
    val title: String,
    val description: String,
    val category: ViewCategory,
    val action: () -> Unit = {}
)
