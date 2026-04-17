package com.peter.coil.demo

import android.content.Context
import android.content.Intent

data class DemoItem(
    val title: String,
    val description: String = "",
    val isHeader: Boolean = false,
    val targetClass: Class<*>? = null
) {
    fun createIntent(context: Context): Intent? = targetClass?.let { Intent(context, it) }
}
