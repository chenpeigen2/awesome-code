package com.peter.coroutine.demo

import android.content.Context
import android.content.Intent
import com.peter.coroutine.demo.basics.CoroutineScopeActivity
import com.peter.coroutine.demo.basics.DispatchersActivity
import com.peter.coroutine.demo.basics.JobActivity
import com.peter.coroutine.demo.basics.LaunchAsyncActivity
import com.peter.coroutine.demo.basics.SuspendFunctionActivity
import com.peter.coroutine.demo.channel.ChannelBasicsActivity
import com.peter.coroutine.demo.channel.ProduceConsumeActivity
import com.peter.coroutine.demo.channel.SelectExpressionActivity
import com.peter.coroutine.demo.flow.ColdHotFlowActivity
import com.peter.coroutine.demo.flow.FlowBasicsActivity
import com.peter.coroutine.demo.flow.FlowOperatorsActivity
import com.peter.coroutine.demo.flow.StateFlowActivity
import com.peter.coroutine.demo.errorhandling.ExceptionHandlerActivity
import com.peter.coroutine.demo.errorhandling.SupervisorJobActivity
import com.peter.coroutine.demo.errorhandling.TryCatchActivity
import com.peter.coroutine.demo.android.CollectFlowActivity
import com.peter.coroutine.demo.android.LifecycleScopeActivity
import com.peter.coroutine.demo.android.ViewModelScopeActivity
import com.peter.coroutine.demo.advanced.ContinuationActivity
import com.peter.coroutine.demo.advanced.CustomScopeActivity
import com.peter.coroutine.demo.advanced.RoomExampleActivity
import com.peter.coroutine.demo.advanced.StateMachineActivity

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

// ==================== 协程基础 (01-Basics) ====================
fun createSuspendFunctionIntent(context: Context) = Intent(context, SuspendFunctionActivity::class.java)
fun createLaunchAsyncIntent(context: Context) = Intent(context, LaunchAsyncActivity::class.java)
fun createDispatchersIntent(context: Context) = Intent(context, DispatchersActivity::class.java)
fun createJobIntent(context: Context) = Intent(context, JobActivity::class.java)
fun createCoroutineScopeIntent(context: Context) = Intent(context, CoroutineScopeActivity::class.java)

// ==================== Flow (02-Flow) ====================
fun createFlowBasicsIntent(context: Context) = Intent(context, FlowBasicsActivity::class.java)
fun createFlowOperatorsIntent(context: Context) = Intent(context, FlowOperatorsActivity::class.java)
fun createStateFlowIntent(context: Context) = Intent(context, StateFlowActivity::class.java)
fun createColdHotFlowIntent(context: Context) = Intent(context, ColdHotFlowActivity::class.java)

// ==================== Channel (03-Channel) ====================
fun createChannelBasicsIntent(context: Context) = Intent(context, ChannelBasicsActivity::class.java)
fun createProduceConsumeIntent(context: Context) = Intent(context, ProduceConsumeActivity::class.java)
fun createSelectExpressionIntent(context: Context) = Intent(context, SelectExpressionActivity::class.java)

// ==================== 异常处理 (04-ErrorHandling) ====================
fun createTryCatchIntent(context: Context) = Intent(context, TryCatchActivity::class.java)
fun createExceptionHandlerIntent(context: Context) = Intent(context, ExceptionHandlerActivity::class.java)
fun createSupervisorJobIntent(context: Context) = Intent(context, SupervisorJobActivity::class.java)

// ==================== Android 集成 (05-Android) ====================
fun createLifecycleScopeIntent(context: Context) = Intent(context, LifecycleScopeActivity::class.java)
fun createViewModelScopeIntent(context: Context) = Intent(context, ViewModelScopeActivity::class.java)
fun createCollectFlowIntent(context: Context) = Intent(context, CollectFlowActivity::class.java)

// ==================== 进阶原理 (06-Advanced) ====================
fun createContinuationIntent(context: Context) = Intent(context, ContinuationActivity::class.java)
fun createStateMachineIntent(context: Context) = Intent(context, StateMachineActivity::class.java)
fun createCustomScopeIntent(context: Context) = Intent(context, CustomScopeActivity::class.java)
fun createRoomExampleIntent(context: Context) = Intent(context, RoomExampleActivity::class.java)

// ==================== 测试 (07-Testing) ====================
fun createTestDispatcherIntent(context: Context) = null // Intent(context, TestDispatcherActivity::class.java)
fun createTimeControlIntent(context: Context) = null // Intent(context, TimeControlActivity::class.java)
fun createFlowTestIntent(context: Context) = null // Intent(context, FlowTestActivity::class.java)
