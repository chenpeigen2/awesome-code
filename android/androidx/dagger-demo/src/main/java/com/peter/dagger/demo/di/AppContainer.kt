package com.peter.dagger.demo.di

import com.peter.dagger.demo.android.DemoViewModelFactory
import com.peter.dagger.demo.model.CoffeeMaker
import com.peter.dagger.demo.model.ElectricHeater
import com.peter.dagger.demo.model.Heater
import com.peter.dagger.demo.model.Pump
import com.peter.dagger.demo.model.Thermosiphon
import com.peter.dagger.demo.qualifier.DataSource
import com.peter.dagger.demo.qualifier.LocalDataSourceImpl
import com.peter.dagger.demo.qualifier.RemoteDataSourceImpl
import com.peter.dagger.demo.scope.DatabaseService
import com.peter.dagger.demo.scope.RequestService
import com.peter.dagger.demo.scope.UserService
import com.peter.dagger.demo.subcomponent.LoginComponent

/**
 * AppContainer - 应用级依赖容器
 *
 * 手动实现依赖注入容器
 * 模拟 Dagger2 的 @Singleton 作用域
 */
class AppContainer {

    // ============== 基础注入示例 ==============

    // 单例 Heater
    private val _heater: Heater by lazy { ElectricHeater() }

    // 单例 Pump
    private val _pump: Pump by lazy { Thermosiphon(_heater) }

    // 单例 CoffeeMaker
    private val _coffeeMaker: CoffeeMaker by lazy { CoffeeMaker(_heater, _pump) }

    // 提供公共访问
    val heater: Heater get() = _heater
    val pump: Pump get() = _pump
    val coffeeMaker: CoffeeMaker get() = _coffeeMaker

    // ============== 作用域示例 ==============

    // @Singleton - 应用级单例
    private val _databaseService: DatabaseService by lazy { DatabaseService() }
    val databaseService: DatabaseService get() = _databaseService

    // @ActivityScoped - 由 ActivityContainer 管理
    // 每次获取都创建新实例 (模拟无作用域)
    fun createRequestService(): RequestService = RequestService()

    // 创建 Activity 级别的容器
    fun createActivityContainer(): ActivityContainer = ActivityContainer(this)

    // ============== 限定符示例 ==============

    // @LocalDataSource - 本地数据源单例
    // 对应 Dagger2: @Provides @LocalDataSource fun provideLocalDataSource(): DataSource
    private val _localDataSource: DataSource by lazy { LocalDataSourceImpl() }
    val localDataSource: DataSource get() = _localDataSource

    // @RemoteDataSource - 远程数据源单例
    // 对应 Dagger2: @Provides @RemoteDataSource fun provideRemoteDataSource(): DataSource
    private val _remoteDataSource: DataSource by lazy { RemoteDataSourceImpl() }
    val remoteDataSource: DataSource get() = _remoteDataSource

    // ============== 子组件示例 ==============

    // 创建 LoginComponent 子组件
    // 对应 Dagger2: appComponent.loginComponent().create()
    // 子组件可以访问父组件的依赖 (databaseService)
    fun createLoginComponent(): LoginComponent {
        return LoginComponent(databaseService.instanceId)
    }

    // ============== Android 集成示例 ==============

    // ViewModel Factory - 单例
    // 对应 Dagger2: @Provides fun provideDemoViewModelFactory(...): DemoViewModelFactory
    private val _demoViewModelFactory: DemoViewModelFactory by lazy {
        DemoViewModelFactory(localDataSource, remoteDataSource)
    }
    val demoViewModelFactory: DemoViewModelFactory get() = _demoViewModelFactory
}

/**
 * ActivityContainer - Activity 级别依赖容器
 *
 * 模拟 Dagger2 的 @ActivityScoped 作用域
 * 在 Activity 生命周期内保持单例
 */
class ActivityContainer(private val appContainer: AppContainer) {

    // @ActivityScoped - Activity 级别单例
    private val _userService: UserService by lazy { UserService() }
    val userService: UserService get() = _userService

    // 可以访问应用级依赖
    val databaseService: DatabaseService get() = appContainer.databaseService
}
