package com.peter.layoutinflater.demo

import android.content.Context
import android.content.Intent
import com.peter.layoutinflater.demo.advanced.AttachToRootActivity
import com.peter.layoutinflater.demo.advanced.LayoutInflaterCompatActivity
import com.peter.layoutinflater.demo.async.AsyncInflateActivity
import com.peter.layoutinflater.demo.basic.BasicInflateActivity
import com.peter.layoutinflater.demo.basic.GetLayoutInflaterActivity
import com.peter.layoutinflater.demo.basic.InflaterInstanceActivity
import com.peter.layoutinflater.demo.clone.CloneInflaterActivity
import com.peter.layoutinflater.demo.custom.CustomLayoutInflaterActivity
import com.peter.layoutinflater.demo.dynamic.DynamicViewActivity
import com.peter.layoutinflater.demo.factory.FactoryActivity
import com.peter.layoutinflater.demo.recyclerview.RecyclerViewActivity

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
fun createBasicInflateIntent(context: Context) = Intent(context, BasicInflateActivity::class.java)
fun createGetLayoutInflaterIntent(context: Context) = Intent(context, GetLayoutInflaterActivity::class.java)
fun createInflaterInstanceIntent(context: Context) = Intent(context, InflaterInstanceActivity::class.java)

// 进阶示例
fun createAttachToRootIntent(context: Context) = Intent(context, AttachToRootActivity::class.java)
fun createLayoutInflaterCompatIntent(context: Context) = Intent(context, LayoutInflaterCompatActivity::class.java)
fun createAsyncInflateIntent(context: Context) = Intent(context, AsyncInflateActivity::class.java)

// 工厂机制
fun createFactoryIntent(context: Context) = Intent(context, FactoryActivity::class.java)

// 自定义 LayoutInflater
fun createCustomLayoutInflaterIntent(context: Context) = Intent(context, CustomLayoutInflaterActivity::class.java)
fun createCloneInflaterIntent(context: Context) = Intent(context, CloneInflaterActivity::class.java)

// 实战示例
fun createRecyclerViewIntent(context: Context) = Intent(context, RecyclerViewActivity::class.java)
fun createDynamicViewIntent(context: Context) = Intent(context, DynamicViewActivity::class.java)
