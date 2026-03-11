package com.peter.dagger.demo.qualifier

import java.util.UUID

/**
 * RemoteDataSourceImpl - 远程数据源实现
 *
 * 对应 Dagger2 中的 @RemoteDataSource 限定符：
 *
 * // 定义限定符注解
 * @Qualifier
 * @Retention(AnnotationRetention.RUNTIME)
 * annotation class RemoteDataSource
 *
 * // 在 Module 中提供
 * @Provides @RemoteDataSource
 * fun provideRemoteDataSource(): DataSource = RemoteDataSourceImpl()
 */
class RemoteDataSourceImpl : DataSource {

    private val instanceId = UUID.randomUUID().toString().substring(0, 8)
    private var remoteData: String = "来自服务器的数据"

    override fun fetchData(): String {
        return "☁️ [远程数据源] $remoteData"
    }

    override fun saveData(data: String) {
        remoteData = data
        // 模拟网络请求
        println("RemoteDataSource: 上传数据到服务器 - $data")
    }

    override fun getSourceType(): String = "REMOTE"

    override fun getInstanceId(): String = instanceId

    override fun toString(): String {
        return "RemoteDataSourceImpl(instanceId=$instanceId, type=REMOTE)"
    }
}
