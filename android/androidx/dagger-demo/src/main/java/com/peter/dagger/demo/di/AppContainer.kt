package com.peter.dagger.demo.di

import com.peter.dagger.demo.model.CoffeeMaker
import com.peter.dagger.demo.model.ElectricHeater
import com.peter.dagger.demo.model.Heater
import com.peter.dagger.demo.model.Pump
import com.peter.dagger.demo.model.Thermosiphon
import com.peter.dagger.demo.scope.DatabaseService
import com.peter.dagger.demo.scope.RequestService
import com.peter.dagger.demo.scope.UserService

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
