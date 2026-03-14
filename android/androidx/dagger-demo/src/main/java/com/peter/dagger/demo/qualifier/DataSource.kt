package com.peter.dagger.demo.qualifier

/**
 * DataSource - 数据源接口
 *
 * 演示 Dagger2 限定符的使用场景
 */
interface DataSource {
    fun fetchData(): String
    fun saveData(data: String)
    fun getSourceType(): String
    fun getInstanceId(): String
}