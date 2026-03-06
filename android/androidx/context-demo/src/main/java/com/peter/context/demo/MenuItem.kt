package com.peter.context.demo

import android.content.Context
import android.content.Intent
import com.peter.context.demo.advanced.ContextDatabaseActivity
import com.peter.context.demo.advanced.ContextSystemServicesActivity
import com.peter.context.demo.advanced.ContextWindowActivity
import com.peter.context.demo.basic.ContextFileActivity
import com.peter.context.demo.basic.ContextResourcesActivity
import com.peter.context.demo.basic.ContextSharedPreferencesActivity
import com.peter.context.demo.basic.ContextTypeActivity
import com.peter.context.demo.deep.ContextBestPracticesActivity
import com.peter.context.demo.deep.ContextMemoryLeakActivity
import com.peter.context.demo.deep.ContextWrapperActivity

/**
 * 菜单项数据模型
 */
data class MenuItem(
    val title: String,
    val description: String = "",
    val isHeader: Boolean = false,
    val intent: Intent? = null
)

/**
 * 各 Activity 的 Intent 创建函数
 */

// 基础示例
fun createContextTypeIntent(context: Context) = Intent(context, ContextTypeActivity::class.java)
fun createContextResourcesIntent(context: Context) = Intent(context, ContextResourcesActivity::class.java)
fun createContextSharedPreferencesIntent(context: Context) = Intent(context, ContextSharedPreferencesActivity::class.java)
fun createContextFileIntent(context: Context) = Intent(context, ContextFileActivity::class.java)

// 进阶示例
fun createContextDatabaseIntent(context: Context) = Intent(context, ContextDatabaseActivity::class.java)
fun createContextSystemServicesIntent(context: Context) = Intent(context, ContextSystemServicesActivity::class.java)
fun createContextWindowIntent(context: Context) = Intent(context, ContextWindowActivity::class.java)

// 深入示例
fun createContextWrapperIntent(context: Context) = Intent(context, ContextWrapperActivity::class.java)
fun createContextMemoryLeakIntent(context: Context) = Intent(context, ContextMemoryLeakActivity::class.java)
fun createContextBestPracticesIntent(context: Context) = Intent(context, ContextBestPracticesActivity::class.java)
