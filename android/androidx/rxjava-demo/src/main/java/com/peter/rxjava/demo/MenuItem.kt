package com.peter.rxjava.demo

import android.content.Context
import android.content.Intent
import com.peter.rxjava.demo.android.RxAndroidActivity
import com.peter.rxjava.demo.basics.DisposableActivity
import com.peter.rxjava.demo.basics.ObservableActivity
import com.peter.rxjava.demo.basics.SingleMaybeCompletableActivity
import com.peter.rxjava.demo.errorhandling.ErrorHandlingActivity
import com.peter.rxjava.demo.operators.CombineOperatorsActivity
import com.peter.rxjava.demo.operators.FilterOperatorsActivity
import com.peter.rxjava.demo.operators.TransformOperatorsActivity
import com.peter.rxjava.demo.schedulers.SchedulersActivity
import com.peter.rxjava.demo.subjects.SubjectsActivity

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

// ==================== RxJava 基础 (01-Basics) ====================
fun createObservableIntent(context: Context) = Intent(context, ObservableActivity::class.java)
fun createSingleMaybeCompletableIntent(context: Context) = Intent(context, SingleMaybeCompletableActivity::class.java)
fun createDisposableIntent(context: Context) = Intent(context, DisposableActivity::class.java)

// ==================== 操作符 (02-Operators) ====================
fun createTransformOperatorsIntent(context: Context) = Intent(context, TransformOperatorsActivity::class.java)
fun createFilterOperatorsIntent(context: Context) = Intent(context, FilterOperatorsActivity::class.java)
fun createCombineOperatorsIntent(context: Context) = Intent(context, CombineOperatorsActivity::class.java)

// ==================== 调度器 (03-Schedulers) ====================
fun createSchedulersIntent(context: Context) = Intent(context, SchedulersActivity::class.java)

// ==================== Subject (04-Subjects) ====================
fun createSubjectsIntent(context: Context) = Intent(context, SubjectsActivity::class.java)

// ==================== 错误处理 (05-ErrorHandling) ====================
fun createErrorHandlingIntent(context: Context) = Intent(context, ErrorHandlingActivity::class.java)

// ==================== Android 集成 (06-Android) ====================
fun createRxAndroidIntent(context: Context) = Intent(context, RxAndroidActivity::class.java)
