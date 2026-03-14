package com.peter.dagger.demo.di

import android.app.Application
import android.content.Context
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
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

/**
 * AppModule - 应用级 Dagger 模块
 *
 * 演示 @Binds 和 @Provides 两种提供依赖的方式：
 *
 * @Binds:
 * - 更简洁，适用于接口到实现的绑定
 * - 只能用于 abstract 方法
 * - 参数是实现类，返回值是接口
 * - 比 @Provides 更高效（生成更少代码）
 *
 * @Provides:
 * - 更灵活，适用于需要额外逻辑的场景
 * - 可以用于 object 中的方法
 * - 可以使用限定符 (@Named, @Qualifier)
 */
@Module
interface AppModule {

    // ============== @Binds 示例 ==============

    /**
     * @Binds 绑定 Heater 接口到 ElectricHeater 实现
     * 等价于 @Provides fun provideHeater(): Heater = ElectricHeater()
     */
    @Binds
    @Singleton
    fun bindHeater(heater: ElectricHeater): Heater

    /**
     * @Binds 绑定 Pump 接口到 Thermosiphon 实现
     * Thermosiphon 的依赖 (Heater) 会自动注入
     */
    @Binds
    @Singleton
    fun bindPump(pump: Thermosiphon): Pump

    // ============== @Provides 示例 (companion object) ==============

    companion object {

        /**
         * @Provides 提供带限定符的依赖
         * 对于同一接口的多个实现，需要使用 @Named 或自定义 @Qualifier
         */
        @Provides
        @Singleton
        @Named("local")
        fun provideLocalDataSource(impl: LocalDataSourceImpl): DataSource = impl

        @Provides
        @Singleton
        @Named("remote")
        fun provideRemoteDataSource(impl: RemoteDataSourceImpl): DataSource = impl

        /**
         * @Provides 提供需要额外逻辑的依赖
         */
        @Provides
        @Singleton
        fun provideContext(application: Application): Context {
            return application.applicationContext
        }

        /**
         * @Provides 提供无作用域依赖
         * 每次注入都会创建新实例
         */
        @Provides
        fun provideRequestService(): RequestService {
            return RequestService()
        }

        @Provides
        fun provideUserService(): UserService {
            return UserService()
        }
    }
}
