package com.peter.dagger.demo.di

import android.app.Application
import com.peter.dagger.demo.MainActivity
import com.peter.dagger.demo.android.DemoViewModelFactory
import com.peter.dagger.demo.model.CoffeeMaker
import com.peter.dagger.demo.qualifier.DataSource
import com.peter.dagger.demo.scope.DatabaseService
import com.peter.dagger.demo.scope.RequestService
import com.peter.dagger.demo.scope.UserService
import com.peter.dagger.demo.subcomponent.LoginComponent
import com.peter.dagger.demo.ui.fragment.AndroidFragment
import com.peter.dagger.demo.ui.fragment.BasicFragment
import com.peter.dagger.demo.ui.fragment.QualifierFragment
import com.peter.dagger.demo.ui.fragment.ScopeFragment
import com.peter.dagger.demo.ui.fragment.SubcomponentFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named
import javax.inject.Singleton

/**
 * AppComponent - 应用级 Dagger 组件
 */
@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    // 注入入口
    fun inject(activity: MainActivity)
    fun inject(fragment: BasicFragment)
    fun inject(fragment: ScopeFragment)
    fun inject(fragment: QualifierFragment)
    fun inject(fragment: SubcomponentFragment)
    fun inject(fragment: AndroidFragment)

    // 直接暴露依赖（使用方法）
    fun coffeeMaker(): CoffeeMaker
    fun databaseService(): DatabaseService

    @Named("local")
    fun localDataSource(): DataSource

    @Named("remote")
    fun remoteDataSource(): DataSource

    fun requestService(): RequestService
    fun userService(): UserService
    fun demoViewModelFactory(): DemoViewModelFactory

    // 子组件工厂
    fun loginComponent(): LoginComponent.Factory

    // 组件工厂
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}