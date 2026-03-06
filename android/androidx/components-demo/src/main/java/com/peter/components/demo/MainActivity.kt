package com.peter.components.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.components.demo.databinding.ActivityMainBinding

/**
 * Android 四大组件 Demo 主入口
 *
 * 本 Demo 全面覆盖 Android 四大组件，从基础到进阶：
 *
 * ═══════════════════════════════════════════════════════════════
 * 一、Activity 组件
 * ═══════════════════════════════════════════════════════════════
 *
 * 1. 基础篇
 *    - 显式 Intent 启动：直接指定目标组件
 *    - 隐式 Intent 启动：通过 Action/Category/Uri 匹配
 *    - Intent 数据传递：基本类型、Bundle、Parcelable、Serializable
 *
 * 2. 生命周期篇
 *    - 正常生命周期：onCreate -> onStart -> onResume -> onPause -> onStop -> onDestroy
 *    - 异常生命周期：系统配置改变、内存不足导致重建
 *    - 透明/对话框 Activity 的影响
 *
 * 3. 启动模式篇
 *    - standard：每次启动都创建新实例
 *    - singleTop：栈顶复用
 *    - singleTask：栈内复用
 *    - singleInstance：独立任务栈
 *
 * 4. 进阶篇
 *    - TaskAffinity：任务栈亲和性
 *    - Activity Result API：替代 startActivityForResult
 *    - SharedElement：共享元素转场动画
 *    - Fragment 通信：ViewModel、接口回调、Result API
 *
 * ═══════════════════════════════════════════════════════════════
 * 二、Service 组件
 * ═══════════════════════════════════════════════════════════════
 *
 * 1. 基础篇
 *    - startService：启动服务，独立于启动者生命周期
 *    - IntentService：后台任务队列（已废弃，使用 WorkManager）
 *
 * 2. 前台服务篇
 *    - 通知栏常驻：必须显示通知
 *    - 服务类型声明：dataSync、location、mediaPlayback 等
 *
 * 3. 绑定服务篇
 *    - 本地绑定：同进程内通信
 *    - 远程绑定：跨进程通信 (AIDL)
 *    - 连接管理：绑定/解绑生命周期
 *
 * ═══════════════════════════════════════════════════════════════
 * 三、BroadcastReceiver 组件
 * ═══════════════════════════════════════════════════════════════
 *
 * 1. 注册方式
 *    - 静态注册：Manifest 中声明，应用未启动也能接收
 *    - 动态注册：代码中注册，跟随组件生命周期
 *
 * 2. 广播类型
 *    - 普通广播：异步，所有接收者同时收到
 *    - 有序广播：同步，按优先级传递，可拦截/修改
 *    - 本地广播：应用内安全通信
 *
 * 3. 系统广播
 *    - 开机启动、网络变化、电量变化等
 *
 * ═══════════════════════════════════════════════════════════════
 * 四、ContentProvider 组件
 * ═══════════════════════════════════════════════════════════════
 *
 * 1. 基础篇
 *    - URI 设计：content://authority/path/id
 *    - CRUD 操作：insert、query、update、delete
 *    - UriMatcher：URI 路径匹配
 *
 * 2. 进阶篇
 *    - ContentObserver：数据变化监听
 *    - 批量操作：applyBatch、bulkInsert
 *    - MIME 类型：getType 返回数据类型
 *    - 权限控制：读/写权限分离
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = MainAdapter(getMenuItems()) { item ->
                startActivity(item.intent)
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
                title = getString(R.string.activity_lifecycle),
                description = getString(R.string.activity_lifecycle_desc),
                intent = createLifecycleNormalIntent(this)
            ),
            MenuItem(
                title = getString(R.string.activity_launchmode),
                description = getString(R.string.activity_launchmode_desc),
                intent = createStandardActivityIntent(this)
            ),
            MenuItem(
                title = getString(R.string.activity_advanced),
                description = getString(R.string.activity_advanced_desc),
                intent = createTaskAffinityActivityIntent(this)
            ),

            // ==================== Service 组件 ====================
            MenuItem(
                title = getString(R.string.service_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.service_basic),
                description = getString(R.string.service_basic_desc),
                intent = createServiceMainActivityIntent(this)
            ),

            // ==================== BroadcastReceiver 组件 ====================
            MenuItem(
                title = getString(R.string.receiver_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.receiver_basic),
                description = getString(R.string.receiver_basic_desc),
                intent = createSystemBroadcastActivityIntent(this)
            ),
            MenuItem(
                title = getString(R.string.receiver_ordered),
                description = getString(R.string.receiver_ordered_desc),
                intent = createOrderedBroadcastActivityIntent(this)
            ),
            MenuItem(
                title = getString(R.string.receiver_local),
                description = getString(R.string.receiver_local_desc),
                intent = createLocalBroadcastActivityIntent(this)
            ),

            // ==================== ContentProvider 组件 ====================
            MenuItem(
                title = getString(R.string.provider_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.provider_basic),
                description = getString(R.string.provider_basic_desc),
                intent = createProviderBasicActivityIntent(this)
            ),
            MenuItem(
                title = getString(R.string.provider_advanced),
                description = getString(R.string.provider_advanced_desc),
                intent = createProviderAdvancedActivityIntent(this)
            )
        )
    }
}
