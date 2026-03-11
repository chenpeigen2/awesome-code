package com.peter.dagger.demo.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.peter.dagger.demo.qualifier.DataSource

/**
 * DemoViewModelFactory - ViewModel 工厂
 *
 * 手动实现 ViewModelProvider.Factory
 * 用于创建带有依赖注入的 ViewModel
 *
 * 在 Dagger2 中，这个 Factory 通常也由 Dagger 提供：
 *
 * @Module
 * object ViewModelModule {
 *     @Provides
 *     fun provideDemoViewModelFactory(
 *         localDataSource: DataSource,
 *         remoteDataSource: DataSource
 *     ): DemoViewModelFactory {
 *         return DemoViewModelFactory(localDataSource, remoteDataSource)
 *     }
 * }
 *
 * Hilt 通过 @HiltViewModel 注解自动生成 Factory
 */
class DemoViewModelFactory(
    private val localDataSource: DataSource,
    private val remoteDataSource: DataSource
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DemoViewModel::class.java)) {
            return DemoViewModel(localDataSource, remoteDataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
