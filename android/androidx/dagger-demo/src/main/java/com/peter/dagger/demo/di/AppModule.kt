package com.peter.dagger.demo.di

import android.content.Context
import com.peter.dagger.demo.DemoApplication

/**
 * AppModule - 应用级 Dagger 模块
 *
 * 手动依赖注入示例
 * 不使用 Dagger 注解处理器
 * 直接手动创建依赖
 */
object AppModule {

    /**
     * 提供 Application Context
     */
    fun provideContext(application: DemoApplication): Context {
        return application.applicationContext
    }
}
