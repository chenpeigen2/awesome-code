package com.peter.coroutine.demo

import android.content.Context
import android.content.Intent

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
 * 暂时返回 null，后续任务会实现具体的 Activity
 */

// ==================== 协程基础 (01-Basics) ====================
fun createSuspendFunctionIntent(context: Context) = null // Intent(context, SuspendFunctionActivity::class.java)
fun createLaunchAsyncIntent(context: Context) = null // Intent(context, LaunchAsyncActivity::class.java)
fun createDispatchersIntent(context: Context) = null // Intent(context, DispatchersActivity::class.java)
fun createJobIntent(context: Context) = null // Intent(context, JobActivity::class.java)
fun createCoroutineScopeIntent(context: Context) = null // Intent(context, CoroutineScopeActivity::class.java)

// ==================== Flow (02-Flow) ====================
fun createFlowBasicsIntent(context: Context) = null // Intent(context, FlowBasicsActivity::class.java)
fun createFlowOperatorsIntent(context: Context) = null // Intent(context, FlowOperatorsActivity::class.java)
fun createStateFlowIntent(context: Context) = null // Intent(context, StateFlowActivity::class.java)
fun createColdHotFlowIntent(context: Context) = null // Intent(context, ColdHotFlowActivity::class.java)

// ==================== Channel (03-Channel) ====================
fun createChannelBasicsIntent(context: Context) = null // Intent(context, ChannelBasicsActivity::class.java)
fun createProduceConsumeIntent(context: Context) = null // Intent(context, ProduceConsumeActivity::class.java)
fun createSelectExpressionIntent(context: Context) = null // Intent(context, SelectExpressionActivity::class.java)

// ==================== 异常处理 (04-ErrorHandling) ====================
fun createTryCatchIntent(context: Context) = null // Intent(context, TryCatchActivity::class.java)
fun createExceptionHandlerIntent(context: Context) = null // Intent(context, ExceptionHandlerActivity::class.java)
fun createSupervisorJobIntent(context: Context) = null // Intent(context, SupervisorJobActivity::class.java)

// ==================== Android 集成 (05-Android) ====================
fun createLifecycleScopeIntent(context: Context) = null // Intent(context, LifecycleScopeActivity::class.java)
fun createViewModelScopeIntent(context: Context) = null // Intent(context, ViewModelScopeActivity::class.java)
fun createCollectFlowIntent(context: Context) = null // Intent(context, CollectFlowActivity::class.java)

// ==================== 进阶原理 (06-Advanced) ====================
fun createContinuationIntent(context: Context) = null // Intent(context, ContinuationActivity::class.java)
fun createStateMachineIntent(context: Context) = null // Intent(context, StateMachineActivity::class.java)
fun createCustomScopeIntent(context: Context) = null // Intent(context, CustomScopeActivity::class.java)
fun createRoomExampleIntent(context: Context) = null // Intent(context, RoomExampleActivity::class.java)

// ==================== 测试 (07-Testing) ====================
fun createTestDispatcherIntent(context: Context) = null // Intent(context, TestDispatcherActivity::class.java)
fun createTimeControlIntent(context: Context) = null // Intent(context, TimeControlActivity::class.java)
fun createFlowTestIntent(context: Context) = null // Intent(context, FlowTestActivity::class.java)
