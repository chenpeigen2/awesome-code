package com.peter.components.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.components.demo.databinding.ActivityMainBinding

/**
 * Android 四大组件 Demo 主入口
 * 
 * 本 Demo 涵盖 Android 四大核心组件的完整知识点：
 * 
 * 一、Activity 组件
 * 1. 基础用法 - 创建、配置、生命周期
 * 2. Intent 机制 - 显式/隐式 Intent、数据传递
 * 3. 启动模式 - Standard、SingleTop、SingleTask、SingleInstance
 * 4. 高级特性 - ActivityResult API、Fragment 通信、转场动画
 * 
 * 二、Service 组件
 * 1. 基础服务 - startService/stopService
 * 2. 绑定服务 - Binder/AIDL 跨进程通信
 * 3. 前台服务 - 通知与保活
 * 
 * 三、BroadcastReceiver 组件
 * 1. 动态注册 - 代码中注册与注销
 * 2. 有序广播 - 优先级与中断传播
 * 3. 本地广播 - LocalBroadcastManager
 * 
 * 四、ContentProvider 组件
 * 1. 基础查询 - ContentResolver 操作系统数据
 * 2. 自定义 Provider - CRUD 实现与 MIME 类型
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
            // ==================== Activity 组件 ====================
            MenuItem(
                title = getString(R.string.activity_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.activity_basic),
                description = getString(R.string.activity_basic_desc),
                intent = createBasicActivityIntent(this)
            ),
            MenuItem(
                title = getString(R.string.activity_explicit_intent),
                description = getString(R.string.activity_explicit_intent_desc),
                intent = createExplicitIntentActivityIntent(this)
            ),
            MenuItem(
                title = getString(R.string.activity_implicit_intent),
                description = getString(R.string.activity_implicit_intent_desc),
                intent = createImplicitIntentActivityIntent(this)
            ),
            MenuItem(
                title = getString(R.string.activity_lifecycle),
                description = getString(R.string.activity_lifecycle_desc),
                intent = createLifecycleNormalActivityIntent(this)
            ),
            MenuItem(
                title = getString(R.string.activity_launch_mode),
                description = getString(R.string.activity_launch_mode_desc),
                intent = createStandardActivityIntent(this)
            ),
            MenuItem(
                title = getString(R.string.activity_result),
                description = getString(R.string.activity_result_desc),
                intent = createActivityResultIntent(this)
            ),
            MenuItem(
                title = getString(R.string.activity_fragment_communication),
                description = getString(R.string.activity_fragment_communication_desc),
                intent = createFragmentCommunicationIntent(this)
            ),
            MenuItem(
                title = getString(R.string.activity_shared_element),
                description = getString(R.string.activity_shared_element_desc),
                intent = createSharedElementIntent(this)
            ),
            MenuItem(
                title = getString(R.string.activity_task_affinity),
                description = getString(R.string.activity_task_affinity_desc),
                intent = createTaskAffinityIntent(this)
            ),
            MenuItem(
                title = getString(R.string.activity_startup_flow),
                description = getString(R.string.activity_startup_flow_desc),
                intent = createStartupFlowIntent(this)
            ),

            // ==================== Service 组件 ====================
            MenuItem(
                title = getString(R.string.service_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.service_basic),
                description = getString(R.string.service_basic_desc),
                intent = createServiceMainIntent(this)
            ),
            MenuItem(
                title = getString(R.string.service_foreground),
                description = getString(R.string.service_foreground_desc),
                intent = createServiceMainIntent(this)
            ),
            MenuItem(
                title = getString(R.string.service_bind_local),
                description = getString(R.string.service_bind_local_desc),
                intent = createServiceMainIntent(this)
            ),
            MenuItem(
                title = getString(R.string.service_bind_remote),
                description = getString(R.string.service_bind_remote_desc),
                intent = createServiceMainIntent(this)
            ),

            // ==================== BroadcastReceiver 组件 ====================
            MenuItem(
                title = getString(R.string.receiver_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.receiver_dynamic),
                description = getString(R.string.receiver_dynamic_desc),
                intent = createSystemBroadcastIntent(this)
            ),
            MenuItem(
                title = getString(R.string.receiver_ordered),
                description = getString(R.string.receiver_ordered_desc),
                intent = createOrderedBroadcastIntent(this)
            ),
            MenuItem(
                title = getString(R.string.receiver_local),
                description = getString(R.string.receiver_local_desc),
                intent = createLocalBroadcastIntent(this)
            ),

            // ==================== ContentProvider 组件 ====================
            MenuItem(
                title = getString(R.string.provider_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.provider_basic),
                description = getString(R.string.provider_basic_desc),
                intent = createProviderBasicIntent(this)
            ),
            MenuItem(
                title = getString(R.string.provider_custom),
                description = getString(R.string.provider_custom_desc),
                intent = createProviderAdvancedIntent(this)
            )
        )
    }
}