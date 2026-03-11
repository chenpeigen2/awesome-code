package com.peter.dagger.demo

import android.app.Application
import com.peter.dagger.demo.di.AppContainer

/**
 * Dagger2 Demo Application
 *
 * 手动依赖注入示例
 * 在 Application 中创建依赖容器
 */
class DemoApplication : Application() {

    // 应用级依赖容器
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer()
    }
}
