package com.peter.dagger.demo

import android.app.Application
import com.peter.dagger.demo.di.AppComponent
import com.peter.dagger.demo.di.DaggerAppComponent

/**
 * Dagger2 Demo Application
 *
 * 使用真正的 Dagger2 依赖注入
 */
class DemoApplication : Application() {

    // 应用级 Dagger 组件
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        // 构建 Dagger 组件
        appComponent = DaggerAppComponent.factory()
            .create(this)
    }
}
