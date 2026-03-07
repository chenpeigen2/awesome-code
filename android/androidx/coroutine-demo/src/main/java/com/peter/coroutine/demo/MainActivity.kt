package com.peter.coroutine.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.coroutine.demo.databinding.ActivityMainBinding

/**
 * Coroutine Demo 主入口
 *
 * 本 Demo 包含以下内容：
 *
 * 一、协程基础
 * 1. suspend 函数 - 挂起函数的原理与使用
 * 2. launch 与 async - 协程启动方式对比
 * 3. Dispatchers 调度器 - 四种调度器的使用场景
 * 4. Job 与协程控制 - Job 状态、取消与等待
 * 5. CoroutineScope 作用域 - 结构化并发与作用域管理
 *
 * 二、Kotlin Flow
 * 1. Flow 基础 - Flow 构建器与 collect
 * 2. Flow 操作符 - map/filter/transform/combine
 * 3. StateFlow - 状态流与 UI 状态管理
 * 4. 冷流与热流 - Flow、SharedFlow、StateFlow 对比
 *
 * 三、Channel 通道
 * 1. Channel 基础 - Channel 的创建与使用
 * 2. produce 与 consume - 生产者消费者模式
 * 3. select 表达式 - 多路复用与选择
 *
 * 四、异常处理
 * 1. try/catch 异常捕获 - 协程中的异常捕获
 * 2. CoroutineExceptionHandler - 全局异常处理器
 * 3. SupervisorJob - 子协程独立失败
 *
 * 五、Android 集成
 * 1. lifecycleScope - 生命周期感知的协程
 * 2. viewModelScope - ViewModel 协程作用域
 * 3. Flow 收集最佳实践 - repeatOnLifecycle 与 flowWithLifecycle
 *
 * 六、进阶原理
 * 1. Continuation 原理 - 挂起与恢复机制
 * 2. 状态机原理 - 协程的编译器转换
 * 3. 自定义 CoroutineScope - 创建自定义作用域
 * 4. Room + 协程 - 数据库协程操作
 *
 * 七、协程测试
 * 1. TestDispatcher - 测试调度器
 * 2. 时间控制 - 虚拟时间与 delay 控制
 * 3. Flow 测试 - 测试 Flow 发射与收集
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = MainAdapter(getMenuItems()) { item ->
                item.intent?.let { startActivity(it) }
            }
        }
    }

    private fun getMenuItems(): List<MenuItem> {
        return listOf(
            // 一、协程基础
            MenuItem(
                title = getString(R.string.basics_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.suspend_function),
                description = getString(R.string.suspend_function_desc),
                intent = createSuspendFunctionIntent(this)
            ),
            MenuItem(
                title = getString(R.string.launch_async),
                description = getString(R.string.launch_async_desc),
                intent = createLaunchAsyncIntent(this)
            ),
            MenuItem(
                title = getString(R.string.dispatchers),
                description = getString(R.string.dispatchers_desc),
                intent = createDispatchersIntent(this)
            ),
            MenuItem(
                title = getString(R.string.job),
                description = getString(R.string.job_desc),
                intent = createJobIntent(this)
            ),
            MenuItem(
                title = getString(R.string.coroutine_scope),
                description = getString(R.string.coroutine_scope_desc),
                intent = createCoroutineScopeIntent(this)
            ),

            // 二、Kotlin Flow
            MenuItem(
                title = getString(R.string.flow_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.flow_basics),
                description = getString(R.string.flow_basics_desc),
                intent = createFlowBasicsIntent(this)
            ),
            MenuItem(
                title = getString(R.string.flow_operators),
                description = getString(R.string.flow_operators_desc),
                intent = createFlowOperatorsIntent(this)
            ),
            MenuItem(
                title = getString(R.string.state_flow),
                description = getString(R.string.state_flow_desc),
                intent = createStateFlowIntent(this)
            ),
            MenuItem(
                title = getString(R.string.cold_hot_flow),
                description = getString(R.string.cold_hot_flow_desc),
                intent = createColdHotFlowIntent(this)
            ),

            // 三、Channel 通道
            MenuItem(
                title = getString(R.string.channel_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.channel_basics),
                description = getString(R.string.channel_basics_desc),
                intent = createChannelBasicsIntent(this)
            ),
            MenuItem(
                title = getString(R.string.produce_consume),
                description = getString(R.string.produce_consume_desc),
                intent = createProduceConsumeIntent(this)
            ),
            MenuItem(
                title = getString(R.string.select_expression),
                description = getString(R.string.select_expression_desc),
                intent = createSelectExpressionIntent(this)
            ),

            // 四、异常处理
            MenuItem(
                title = getString(R.string.error_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.try_catch),
                description = getString(R.string.try_catch_desc),
                intent = createTryCatchIntent(this)
            ),
            MenuItem(
                title = getString(R.string.exception_handler),
                description = getString(R.string.exception_handler_desc),
                intent = createExceptionHandlerIntent(this)
            ),
            MenuItem(
                title = getString(R.string.supervisor_job),
                description = getString(R.string.supervisor_job_desc),
                intent = createSupervisorJobIntent(this)
            ),

            // 五、Android 集成
            MenuItem(
                title = getString(R.string.android_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.lifecycle_scope),
                description = getString(R.string.lifecycle_scope_desc),
                intent = createLifecycleScopeIntent(this)
            ),
            MenuItem(
                title = getString(R.string.viewmodel_scope),
                description = getString(R.string.viewmodel_scope_desc),
                intent = createViewModelScopeIntent(this)
            ),
            MenuItem(
                title = getString(R.string.collect_flow),
                description = getString(R.string.collect_flow_desc),
                intent = createCollectFlowIntent(this)
            ),

            // 六、进阶原理
            MenuItem(
                title = getString(R.string.advanced_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.continuation),
                description = getString(R.string.continuation_desc),
                intent = createContinuationIntent(this)
            ),
            MenuItem(
                title = getString(R.string.state_machine),
                description = getString(R.string.state_machine_desc),
                intent = createStateMachineIntent(this)
            ),
            MenuItem(
                title = getString(R.string.custom_scope),
                description = getString(R.string.custom_scope_desc),
                intent = createCustomScopeIntent(this)
            ),
            MenuItem(
                title = getString(R.string.room_example),
                description = getString(R.string.room_example_desc),
                intent = createRoomExampleIntent(this)
            ),

            // 七、协程测试
            MenuItem(
                title = getString(R.string.testing_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.test_dispatcher),
                description = getString(R.string.test_dispatcher_desc),
                intent = createTestDispatcherIntent(this)
            ),
            MenuItem(
                title = getString(R.string.time_control),
                description = getString(R.string.time_control_desc),
                intent = createTimeControlIntent(this)
            ),
            MenuItem(
                title = getString(R.string.flow_test),
                description = getString(R.string.flow_test_desc),
                intent = createFlowTestIntent(this)
            )
        )
    }
}
