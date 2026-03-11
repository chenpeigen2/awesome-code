package com.peter.dagger.demo.android

import androidx.lifecycle.ViewModel
import com.peter.dagger.demo.qualifier.DataSource
import java.util.UUID

/**
 * DemoViewModel - 演示 ViewModel 中的依赖注入
 *
 * 在 Dagger2 中，ViewModel 需要通过 Factory 进行注入：
 *
 * // 定义 ViewModel
 * class DemoViewModel @Inject constructor(
 *     private val localDataSource: DataSource,
 *     private val remoteDataSource: DataSource
 * ) : ViewModel() { ... }
 *
 * // 定义 Factory
 * class DemoViewModelFactory @Inject constructor(
 *     private val localDataSource: DataSource,
 *     private val remoteDataSource: DataSource
 * ) : ViewModelProvider.Factory {
 *     override fun <T : ViewModel> create(modelClass: Class<T>): T {
 *         return DemoViewModel(localDataSource, remoteDataSource) as T
 *     }
 * }
 *
 * // 在 Activity/Fragment 中使用
 * class MyFragment : Fragment() {
 *     @Inject lateinit var viewModelFactory: DemoViewModelFactory
 *     private val viewModel: DemoViewModel by viewModels { viewModelFactory }
 * }
 *
 * Hilt 简化了这一过程：
 * @HiltViewModel
 * class DemoViewModel @Inject constructor(
 *     private val localDataSource: DataSource
 * ) : ViewModel() { ... }
 *
 * // 直接使用
 * private val viewModel: DemoViewModel by viewModels()
 */
class DemoViewModel(
    private val localDataSource: DataSource,
    private val remoteDataSource: DataSource
) : ViewModel() {

    private val instanceId = UUID.randomUUID().toString().substring(0, 8)

    /**
     * 获取本地数据
     */
    fun getLocalData(): String {
        return localDataSource.fetchData()
    }

    /**
     * 获取远程数据
     */
    fun getRemoteData(): String {
        return remoteDataSource.fetchData()
    }

    /**
     * 获取 ViewModel 实例 ID
     */
    fun getInstanceId(): String = instanceId

    /**
     * 获取数据源信息
     */
    fun getDataSourceInfo(): String {
        return """
            |DemoViewModel [$instanceId]
            |- LocalDataSource: ${localDataSource.getInstanceId()}
            |- RemoteDataSource: ${remoteDataSource.getInstanceId()}
        """.trimMargin()
    }

    override fun onCleared() {
        super.onCleared()
        println("DemoViewModel [$instanceId] - onCleared()")
    }
}
