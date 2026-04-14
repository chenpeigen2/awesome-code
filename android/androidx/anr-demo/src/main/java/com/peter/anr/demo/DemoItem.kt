package com.peter.anr.demo

import android.content.Context
import android.content.Intent

/**
 * Demo 菜单项数据模型
 */
data class DemoItem(
    val title: String,
    val description: String = "",
    val isHeader: Boolean = false,
    val targetClass: Class<*>? = null
) {
    fun createIntent(context: Context): Intent? {
        return targetClass?.let { Intent(context, it) }
    }
}
