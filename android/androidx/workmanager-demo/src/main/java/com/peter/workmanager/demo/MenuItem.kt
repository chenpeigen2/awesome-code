package com.peter.workmanager.demo

import android.content.Context
import android.content.Intent
import com.peter.workmanager.demo.advanced.UniqueWorkActivity
import com.peter.workmanager.demo.advanced.WorkChainActivity
import com.peter.workmanager.demo.advanced.WorkConstraintsActivity
import com.peter.workmanager.demo.advanced.WorkTagActivity
import com.peter.workmanager.demo.basic.BasicWorkerActivity
import com.peter.workmanager.demo.basic.OneTimeWorkActivity
import com.peter.workmanager.demo.basic.PeriodicWorkActivity
import com.peter.workmanager.demo.basic.WorkDataActivity
import com.peter.workmanager.demo.basic.WorkStatusActivity
import com.peter.workmanager.demo.expert.CoroutineWorkerActivity
import com.peter.workmanager.demo.expert.WorkExceptionActivity
import com.peter.workmanager.demo.practice.DataSyncActivity
import com.peter.workmanager.demo.practice.DownloadFileActivity
import com.peter.workmanager.demo.practice.ImageCompressActivity

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

// WorkManager 基础示例
fun createBasicWorkerIntent(context: Context) = Intent(context, BasicWorkerActivity::class.java)
fun createOneTimeWorkIntent(context: Context) = Intent(context, OneTimeWorkActivity::class.java)
fun createPeriodicWorkIntent(context: Context) = Intent(context, PeriodicWorkActivity::class.java)
fun createWorkDataIntent(context: Context) = Intent(context, WorkDataActivity::class.java)
fun createWorkStatusIntent(context: Context) = Intent(context, WorkStatusActivity::class.java)

// WorkManager 进阶示例
fun createWorkConstraintsIntent(context: Context) = Intent(context, WorkConstraintsActivity::class.java)
fun createWorkChainIntent(context: Context) = Intent(context, WorkChainActivity::class.java)
fun createUniqueWorkIntent(context: Context) = Intent(context, UniqueWorkActivity::class.java)
fun createWorkTagIntent(context: Context) = Intent(context, WorkTagActivity::class.java)

// WorkManager 高级示例
fun createCoroutineWorkerIntent(context: Context) = Intent(context, CoroutineWorkerActivity::class.java)
fun createWorkExceptionIntent(context: Context) = Intent(context, WorkExceptionActivity::class.java)

// WorkManager 实战示例
fun createDownloadFileIntent(context: Context) = Intent(context, DownloadFileActivity::class.java)
fun createImageCompressIntent(context: Context) = Intent(context, ImageCompressActivity::class.java)
fun createDataSyncIntent(context: Context) = Intent(context, DataSyncActivity::class.java)
