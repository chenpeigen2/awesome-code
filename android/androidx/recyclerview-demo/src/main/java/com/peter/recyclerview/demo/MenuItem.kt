package com.peter.recyclerview.demo

import android.content.Context
import android.content.Intent
import com.peter.recyclerview.demo.advanced.DiffUtilActivity
import com.peter.recyclerview.demo.advanced.MultiTypeActivity
import com.peter.recyclerview.demo.advanced.PagingSimulationActivity
import com.peter.recyclerview.demo.basic.DragSwipeActivity
import com.peter.recyclerview.demo.basic.GridListActivity
import com.peter.recyclerview.demo.basic.HeaderFooterActivity
import com.peter.recyclerview.demo.basic.SimpleListActivity
import com.peter.recyclerview.demo.basic.StaggeredGridActivity
import com.peter.recyclerview.demo.conflict.CoordinatorActivity
import com.peter.recyclerview.demo.conflict.CustomConflictActivity
import com.peter.recyclerview.demo.conflict.NestedScrollActivity
import com.peter.recyclerview.demo.conflict.RecyclerViewNestedActivity
import com.peter.recyclerview.demo.conflict.ViewPager2NestedActivity

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
fun createSimpleListIntent(context: Context) = Intent(context, SimpleListActivity::class.java)
fun createGridListIntent(context: Context) = Intent(context, GridListActivity::class.java)
fun createStaggeredGridIntent(context: Context) = Intent(context, StaggeredGridActivity::class.java)
fun createHeaderFooterIntent(context: Context) = Intent(context, HeaderFooterActivity::class.java)
fun createDragSwipeIntent(context: Context) = Intent(context, DragSwipeActivity::class.java)

// 进阶示例
fun createMultiTypeIntent(context: Context) = Intent(context, MultiTypeActivity::class.java)
fun createDiffUtilIntent(context: Context) = Intent(context, DiffUtilActivity::class.java)
fun createPagingSimulationIntent(context: Context) = Intent(context, PagingSimulationActivity::class.java)

// 滑动冲突示例
fun createViewPager2NestedIntent(context: Context) = Intent(context, ViewPager2NestedActivity::class.java)
fun createRecyclerViewNestedIntent(context: Context) = Intent(context, RecyclerViewNestedActivity::class.java)
fun createNestedScrollIntent(context: Context) = Intent(context, NestedScrollActivity::class.java)
fun createCoordinatorIntent(context: Context) = Intent(context, CoordinatorActivity::class.java)
fun createCustomConflictIntent(context: Context) = Intent(context, CustomConflictActivity::class.java)