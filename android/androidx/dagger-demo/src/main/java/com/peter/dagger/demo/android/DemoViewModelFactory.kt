package com.peter.dagger.demo.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.peter.dagger.demo.qualifier.DataSource
import javax.inject.Inject
import javax.inject.Named

/**
 * DemoViewModelFactory - ViewModel 工厂
 */
class DemoViewModelFactory @Inject constructor(
    @Named("local") private val localDataSource: DataSource,
    @Named("remote") private val remoteDataSource: DataSource
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DemoViewModel::class.java)) {
            return DemoViewModel(localDataSource, remoteDataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
