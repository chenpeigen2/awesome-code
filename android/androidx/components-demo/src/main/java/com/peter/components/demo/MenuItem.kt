package com.peter.components.demo

import android.content.Context
import android.content.Intent
import com.peter.components.demo.activity.advanced.ActivityResultActivity
import com.peter.components.demo.activity.advanced.FragmentCommunicationActivity
import com.peter.components.demo.activity.advanced.SharedElementActivity
import com.peter.components.demo.activity.advanced.TaskAffinityActivity
import com.peter.components.demo.activity.basic.BasicActivity
import com.peter.components.demo.activity.basic.ExplicitIntentActivity
import com.peter.components.demo.activity.basic.ImplicitIntentActivity
import com.peter.components.demo.activity.launchmode.SingleInstanceActivity
import com.peter.components.demo.activity.launchmode.SingleTaskActivity
import com.peter.components.demo.activity.launchmode.SingleTopActivity
import com.peter.components.demo.activity.launchmode.StandardActivity
import com.peter.components.demo.activity.lifecycle.LifecycleDialogActivity
import com.peter.components.demo.activity.lifecycle.LifecycleNormalActivity
import com.peter.components.demo.activity.lifecycle.LifecycleTranslucentActivity
import com.peter.components.demo.activity.startup.StartupFlowActivity
import com.peter.components.demo.provider.ProviderAdvancedActivity
import com.peter.components.demo.provider.ProviderBasicActivity
import com.peter.components.demo.receiver.LocalBroadcastActivity
import com.peter.components.demo.receiver.OrderedBroadcastActivity
import com.peter.components.demo.receiver.SystemBroadcastActivity
import com.peter.components.demo.service.ServiceMainActivity

/**
 * 菜单项数据模型
 */
data class MenuItem(
    val title: String,
    val description: String = "",
    val isHeader: Boolean = false,
    val intent: Intent? = null
)

// ==================== Activity Intent 创建函数 ====================

fun createBasicActivityIntent(context: Context) = Intent(context, BasicActivity::class.java)
fun createExplicitIntentActivityIntent(context: Context) = Intent(context, ExplicitIntentActivity::class.java)
fun createImplicitIntentActivityIntent(context: Context) = Intent(context, ImplicitIntentActivity::class.java)
fun createLifecycleNormalActivityIntent(context: Context) = Intent(context, LifecycleNormalActivity::class.java)
fun createLifecycleDialogActivityIntent(context: Context) = Intent(context, LifecycleDialogActivity::class.java)
fun createLifecycleTranslucentActivityIntent(context: Context) = Intent(context, LifecycleTranslucentActivity::class.java)
fun createStandardActivityIntent(context: Context) = Intent(context, StandardActivity::class.java)
fun createSingleTopActivityIntent(context: Context) = Intent(context, SingleTopActivity::class.java)
fun createSingleTaskActivityIntent(context: Context) = Intent(context, SingleTaskActivity::class.java)
fun createSingleInstanceActivityIntent(context: Context) = Intent(context, SingleInstanceActivity::class.java)
fun createActivityResultIntent(context: Context) = Intent(context, ActivityResultActivity::class.java)
fun createFragmentCommunicationIntent(context: Context) = Intent(context, FragmentCommunicationActivity::class.java)
fun createSharedElementIntent(context: Context) = Intent(context, SharedElementActivity::class.java)
fun createTaskAffinityIntent(context: Context) = Intent(context, TaskAffinityActivity::class.java)
fun createStartupFlowIntent(context: Context) = Intent(context, StartupFlowActivity::class.java)

// ==================== Service Intent 创建函数 ====================

fun createServiceMainIntent(context: Context) = Intent(context, ServiceMainActivity::class.java)

// ==================== BroadcastReceiver Intent 创建函数 ====================

fun createSystemBroadcastIntent(context: Context) = Intent(context, SystemBroadcastActivity::class.java)
fun createOrderedBroadcastIntent(context: Context) = Intent(context, OrderedBroadcastActivity::class.java)
fun createLocalBroadcastIntent(context: Context) = Intent(context, LocalBroadcastActivity::class.java)

// ==================== ContentProvider Intent 创建函数 ====================

fun createProviderBasicIntent(context: Context) = Intent(context, ProviderBasicActivity::class.java)
fun createProviderAdvancedIntent(context: Context) = Intent(context, ProviderAdvancedActivity::class.java)