package com.peter.dagger.demo.qualifier

/**
 * DataSource - 数据源接口
 *
 * 演示 Dagger2 限定符的使用场景：
 * 当同一接口有多个实现时，需要用限定符区分
 *
 * 在 Dagger2 中，会配合 @Qualifier 使用：
 * @Inject @LocalDataSource lateinit var localSource: DataSource
 * @Inject @RemoteDataSource lateinit var remoteSource: DataSource
 */
interface DataSource {

    /**
     * 获取数据
     * @return 数据内容
     */
    fun fetchData(): String

    /**
     * 保存数据
     * @param data 要保存的数据
     */
    fun saveData(data: String)

    /**
     * 获取数据源类型标识
     */
    fun getSourceType(): String

    /**
     * 获取实例唯一标识（用于验证单例/多例）
     */
    fun getInstanceId(): String
}
