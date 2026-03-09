package com.peter.animation.demo

/**
 * 动画列表项数据模型
 */
data class AnimationItem(
    val title: String,
    val description: String,
    val category: AnimationCategory,
    val targetClass: Class<*>
)

/**
 * 动画分类
 */
enum class AnimationCategory(
    val title: String,
    val colorRes: Int
) {
    VIEW_ANIMATION("View动画", android.graphics.Color.parseColor("#4CAF50")),
    PROPERTY_ANIMATION("属性动画", android.graphics.Color.parseColor("#2196F3")),
    TRANSITION_ANIMATION("转场动画", android.graphics.Color.parseColor("#FF9800")),
    PHYSICS_ANIMATION("Physics动画", android.graphics.Color.parseColor("#9C27B0")),
    MATERIAL_SHAPE("Material形状", android.graphics.Color.parseColor("#E91E63"))
}
