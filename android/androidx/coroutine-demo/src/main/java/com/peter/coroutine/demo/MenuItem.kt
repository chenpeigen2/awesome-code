package com.peter.coroutine.demo

import android.content.Context
import android.content.Intent

/**
 * 菜单项数据模型
 */
data class MenuItem(
    val title: String,
    val description: String = "",
    val isHeader: Boolean = false,
    val intent: Intent? = null
)
