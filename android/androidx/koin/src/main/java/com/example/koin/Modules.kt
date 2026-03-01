package com.example.koin

import com.example.koin.model.MainPresenter
import com.example.koin.model.Peter
import com.example.koin.model.Tool
import org.koin.dsl.module


val sampleModule = module {
    single { Tool("tool", "tool description") }
    single { Peter("chen", 18, get<Tool>()) }

    // 为 MainActivity 声明一个作用域
    scope<MainActivity> {
        // scoped 定义的 MainPresenter，在 MainActivity 的作用域内是单例
        // 注意：这里依然可以使用 get() 获取外部依赖
        scoped { MainPresenter(get(), get()) }
    }
}