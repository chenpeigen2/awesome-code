package com.peter.dagger.demo.qualifier

/**
 * Dagger2 限定符注解示例
 *
 * 这些注解用于区分同一接口的多个实现
 * 在手动 DI 中作为文档参考，在真实 Dagger2 项目中会实际使用
 *
 * ========================
 * 使用示例 (Dagger2 语法)
 * ========================
 *
 * // 1. 定义限定符注解
 * @Qualifier
 * @Retention(AnnotationRetention.RUNTIME)
 * annotation class LocalDataSource
 *
 * @Qualifier
 * @Retention(AnnotationRetention.RUNTIME)
 * annotation class RemoteDataSource
 *
 * // 2. 在 Module 中使用限定符
 * @Module
 * object DataSourceModule {
 *
 *     @Provides
 *     @LocalDataSource
 *     fun provideLocalDataSource(): DataSource = LocalDataSourceImpl()
 *
 *     @Provides
 *     @RemoteDataSource
 *     fun provideRemoteDataSource(): DataSource = RemoteDataSourceImpl()
 * }
 *
 * // 3. 在注入点使用限定符
 * class Repository @Inject constructor(
 *     @LocalDataSource private val localSource: DataSource,
 *     @RemoteDataSource private val remoteSource: DataSource
 * ) {
 *     fun getLocalData() = localSource.fetchData()
 *     fun getRemoteData() = remoteSource.fetchData()
 * }
 *
 * ========================
 * @Named 替代方案
 * ========================
 *
 * 如果不想自定义限定符，可以使用 @Named：
 *
 * @Provides @Named("local")
 * fun provideLocalDataSource(): DataSource = LocalDataSourceImpl()
 *
 * @Provides @Named("remote")
 * fun provideRemoteDataSource(): DataSource = RemoteDataSourceImpl()
 *
 * // 注入时
 * @Inject @Named("local") lateinit var localSource: DataSource
 * @Inject @Named("remote") lateinit var remoteSource: DataSource
 *
 * 注意：@Named 是字符串类型的，容易出错且没有编译时检查
 * 推荐使用自定义 @Qualifier 注解
 */

// 在真实 Dagger2 项目中，这些注解定义如下：
/*
@javax.inject.Qualifier
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class LocalDataSource

@javax.inject.Qualifier
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class RemoteDataSource
*/
