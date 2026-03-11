package com.peter.dagger.demo.qualifier

import java.util.UUID

/**
 * LocalDataSourceImpl - 本地数据源实现
 *
 * 对应 Dagger2 中的 @LocalDataSource 限定符：
 *
 * // 定义限定符注解
 * @Qualifier
 * @Retention(AnnotationRetention.RUNTIME)
 * annotation class LocalDataSource
 *
 * // 在 Module 中提供
 * @Provides @LocalDataSource
 * fun provideLocalDataSource(): DataSource = LocalDataSourceImpl()
 */
class LocalDataSourceImpl : DataSource {

    private val instanceId = UUID.randomUUID().toString().substring(0, 8)
    private var cachedData: String = "初始本地数据"

    override fun fetchData(): String {
        return "📁 [本地数据源] $cachedData"
    }

    override fun saveData(data: String) {
        cachedData = data
    }

    override fun getSourceType(): String = "LOCAL"

    override fun getInstanceId(): String = instanceId

    override fun toString(): String {
        return "LocalDataSourceImpl(instanceId=$instanceId, type=LOCAL)"
    }
}
