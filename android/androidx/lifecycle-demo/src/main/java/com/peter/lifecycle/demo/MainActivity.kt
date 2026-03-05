package com.peter.lifecycle.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.lifecycle.demo.databinding.ActivityMainBinding

/**
 * Lifecycle Demo 主入口
 * 
 * 本 Demo 包含以下内容：
 * 
 * 一、Lifecycle 基础
 * 1. LifecycleObserver - 生命周期观察者
 * 2. 自定义 LifecycleOwner - 实现生命周期所有者
 * 
 * 二、ViewModel 进阶
 * 1. SavedStateHandle - 状态保存与恢复
 * 2. ViewModelProvider.Factory - 自定义工厂
 * 3. 共享 ViewModel - Activity/Fragment 通信
 * 
 * 三、LiveData 转换
 * 1. Transformations - 数据转换
 * 2. MediatorLiveData - 合并多个数据源
 * 3. 自定义 LiveData - 网络状态监听
 * 
 * 四、Kotlin Flow
 * 1. StateFlow - 状态流
 * 2. SharedFlow - 共享流
 * 3. Flow 与 LiveData 互转
 * 
 * 五、协程进阶
 * 1. viewModelScope - ViewModel 协程作用域
 * 2. repeatOnLifecycle - 生命周期感知的流收集
 * 3. ProcessLifecycleOwner - 应用生命周期
 * 
 * 六、MVVM 实战
 * 1. 完整 MVVM 架构示例
 * 2. Repository 模式
 * 3. 网络请求与错误处理
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
                startActivity(item.intent)
            }
        }
    }

    private fun getMenuItems(): List<MenuItem> {
        return listOf(
            // Lifecycle 基础
            MenuItem(
                title = getString(R.string.lifecycle_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.lifecycle_observer),
                description = getString(R.string.lifecycle_observer_desc),
                intent = createLifecycleObserverIntent(this)
            ),
            MenuItem(
                title = getString(R.string.custom_lifecycle_owner),
                description = getString(R.string.custom_lifecycle_owner_desc),
                intent = createCustomLifecycleOwnerIntent(this)
            ),

            // ViewModel 进阶
            MenuItem(
                title = getString(R.string.viewmodel_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.viewmodel_savedstate),
                description = getString(R.string.viewmodel_savedstate_desc),
                intent = createViewModelSavedStateIntent(this)
            ),
            MenuItem(
                title = getString(R.string.viewmodel_factory),
                description = getString(R.string.viewmodel_factory_desc),
                intent = createViewModelFactoryIntent(this)
            ),
            MenuItem(
                title = getString(R.string.shared_viewmodel),
                description = getString(R.string.shared_viewmodel_desc),
                intent = createSharedViewModelIntent(this)
            ),

            // LiveData 转换
            MenuItem(
                title = getString(R.string.livedata_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.transformations),
                description = getString(R.string.transformations_desc),
                intent = createTransformationsIntent(this)
            ),
            MenuItem(
                title = getString(R.string.mediator_livedata),
                description = getString(R.string.mediator_livedata_desc),
                intent = createMediatorLiveDataIntent(this)
            ),
            MenuItem(
                title = getString(R.string.custom_livedata),
                description = getString(R.string.custom_livedata_desc),
                intent = createCustomLiveDataIntent(this)
            ),

            // Kotlin Flow
            MenuItem(
                title = getString(R.string.flow_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.state_flow),
                description = getString(R.string.state_flow_desc),
                intent = createStateFlowIntent(this)
            ),
            MenuItem(
                title = getString(R.string.shared_flow),
                description = getString(R.string.shared_flow_desc),
                intent = createSharedFlowIntent(this)
            ),
            MenuItem(
                title = getString(R.string.flow_livedata),
                description = getString(R.string.flow_livedata_desc),
                intent = createFlowLiveDataIntent(this)
            ),

            // 协程进阶
            MenuItem(
                title = getString(R.string.coroutine_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.viewmodel_scope),
                description = getString(R.string.viewmodel_scope_desc),
                intent = createViewModelScopeIntent(this)
            ),
            MenuItem(
                title = getString(R.string.repeat_on_lifecycle),
                description = getString(R.string.repeat_on_lifecycle_desc),
                intent = createRepeatOnLifecycleIntent(this)
            ),
            MenuItem(
                title = getString(R.string.process_lifecycle),
                description = getString(R.string.process_lifecycle_desc),
                intent = createProcessLifecycleIntent(this)
            ),

            // MVVM 实战
            MenuItem(
                title = getString(R.string.practice_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.mvvm_practice),
                description = getString(R.string.mvvm_practice_desc),
                intent = createMvvmIntent(this)
            )
        )
    }
}
