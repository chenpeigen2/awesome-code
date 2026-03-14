package com.peter.dagger.demo.android

import androidx.lifecycle.ViewModel
import com.peter.dagger.demo.qualifier.DataSource
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named

/**
 * DemoViewModel - 演示 ViewModel 中的依赖注入
 */
class DemoViewModel @Inject constructor(
    @Named("local") private val localDataSource: DataSource,
    @Named("remote") private val remoteDataSource: DataSource
) : ViewModel() {

    private val instanceId = UUID.randomUUID().toString().substring(0, 8)

    fun getLocalData(): String = localDataSource.fetchData()

    fun getRemoteData(): String = remoteDataSource.fetchData()

    fun getInstanceId(): String = instanceId

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
