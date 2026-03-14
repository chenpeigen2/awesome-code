package com.peter.dagger.demo.di

import android.app.Application
import com.peter.dagger.demo.MainActivity
import com.peter.dagger.demo.android.DemoViewModelFactory
import com.peter.dagger.demo.assisted.TaskProcessor
import com.peter.dagger.demo.model.CoffeeMaker
import com.peter.dagger.demo.multibinding.Plugin
import com.peter.dagger.demo.qualifier.DataSource
import com.peter.dagger.demo.scope.DatabaseService
import com.peter.dagger.demo.scope.RequestService
import com.peter.dagger.demo.scope.UserService
import com.peter.dagger.demo.subcomponent.LoginComponent
import com.peter.dagger.demo.ui.fragment.AndroidFragment
import com.peter.dagger.demo.ui.fragment.BasicFragment
import com.peter.dagger.demo.ui.fragment.MultibindingFragment
import com.peter.dagger.demo.ui.fragment.QualifierFragment
import com.peter.dagger.demo.ui.fragment.ScopeFragment
import com.peter.dagger.demo.ui.fragment.SubcomponentFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named
import javax.inject.Singleton

/**
 * AppComponent - 应用级 Dagger 组件
 *
 * 依赖来源：
 * 1. @Binds - 接口到实现的绑定 (Heater, Pump)
 * 2. @Provides - 带限定符或额外逻辑的依赖 (DataSource, Context)
 * 3. @Inject constructor() - 自动注入 (CoffeeMaker, DatabaseService, RequestService)
 * 4. @Multibindings - 集合注入 (Set<Plugin>, Map<String, Plugin>)
 * 5. @AssistedInject - 工厂注入 (TaskProcessor.Factory)
 */
@Singleton
@Component(modules = [AppModule::class, MultibindingModule::class])
interface AppComponent {

    // 注入入口
    fun inject(activity: MainActivity)
    fun inject(fragment: BasicFragment)
    fun inject(fragment: ScopeFragment)
    fun inject(fragment: QualifierFragment)
    fun inject(fragment: SubcomponentFragment)
    fun inject(fragment: AndroidFragment)
    fun inject(fragment: MultibindingFragment)

    // 直接暴露依赖
    fun coffeeMaker(): CoffeeMaker
    fun databaseService(): DatabaseService

    @Named("local")
    fun localDataSource(): DataSource

    @Named("remote")
    fun remoteDataSource(): DataSource

    fun requestService(): RequestService
    fun userService(): UserService
    fun demoViewModelFactory(): DemoViewModelFactory

    // ============== Multibindings ==============

    /**
     * Set<Plugin> - 通过 @IntoSet 收集所有 Plugin
     */
    fun plugins(): Set<Plugin>

    /**
     * Map<String, Plugin> - 通过 @StringKey 收集
     */
    fun pluginMap(): Map<String, Plugin>

    // ============== AssistedInject ==============

    /**
     * TaskProcessor.Factory - AssistedInject 工厂
     */
    fun taskProcessorFactory(): TaskProcessor.Factory

    // 子组件工厂
    fun loginComponent(): LoginComponent.Factory

    // 组件工厂
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}