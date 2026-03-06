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

// ==================== Activity Intent 工厂方法 ====================

// Activity 基础
fun createBasicActivityIntent(context: Context) = Intent(context, BasicActivity::class.java)
fun createExplicitIntentActivityIntent(context: Context) = Intent(context, ExplicitIntentActivity::class.java)
fun createImplicitIntentActivityIntent(context: Context) = Intent(context, ImplicitIntentActivity::class.java)

// Activity 生命周期
fun createLifecycleNormalIntent(context: Context) = Intent(context, LifecycleNormalActivity::class.java)
fun createLifecycleDialogIntent(context: Context) = Intent(context, LifecycleDialogActivity::class.java)
fun createLifecycleTranslucentIntent(context: Context) = Intent(context, LifecycleTranslucentActivity::class.java)

// Activity 启动模式
fun createStandardActivityIntent(context: Context) = Intent(context, StandardActivity::class.java)
fun createSingleTopActivityIntent(context: Context) = Intent(context, SingleTopActivity::class.java)
fun createSingleTaskActivityIntent(context: Context) = Intent(context, SingleTaskActivity::class.java)
fun createSingleInstanceActivityIntent(context: Context) = Intent(context, SingleInstanceActivity::class.java)

// Activity 进阶
fun createTaskAffinityActivityIntent(context: Context) = Intent(context, TaskAffinityActivity::class.java)
fun createActivityResultActivityIntent(context: Context) = Intent(context, ActivityResultActivity::class.java)
fun createSharedElementActivityIntent(context: Context) = Intent(context, SharedElementActivity::class.java)
fun createFragmentCommunicationActivityIntent(context: Context) = Intent(context, FragmentCommunicationActivity::class.java)

// ==================== Service Intent 工厂方法 ====================

fun createServiceMainActivityIntent(context: Context) = Intent(context, ServiceMainActivity::class.java)

// ==================== BroadcastReceiver Intent 工厂方法 ====================

fun createSystemBroadcastActivityIntent(context: Context) = Intent(context, SystemBroadcastActivity::class.java)
fun createOrderedBroadcastActivityIntent(context: Context) = Intent(context, OrderedBroadcastActivity::class.java)
fun createLocalBroadcastActivityIntent(context: Context) = Intent(context, LocalBroadcastActivity::class.java)

// ==================== ContentProvider Intent 工厂方法 ====================

fun createProviderBasicActivityIntent(context: Context) = Intent(context, ProviderBasicActivity::class.java)
fun createProviderAdvancedActivityIntent(context: Context) = Intent(context, ProviderAdvancedActivity::class.java)
