package com.peter.dagger.demo.qualifier

import java.util.UUID

/**
 * LocalDataSourceImpl - 本地数据源实现
 */
class LocalDataSourceImpl : DataSource {

    private val instanceId = UUID.randomUUID().toString().substring(0, 8)
    private var cachedData: String = "初始本地数据"

    override fun fetchData(): String = "📁 [本地数据源] $cachedData"

    override fun saveData(data: String) {
        cachedData = data
    }

    override fun getSourceType(): String = "LOCAL"

    override fun getInstanceId(): String = instanceId

    override fun toString(): String {
        return "LocalDataSourceImpl(instanceId=$instanceId, type=LOCAL)"
    }
}