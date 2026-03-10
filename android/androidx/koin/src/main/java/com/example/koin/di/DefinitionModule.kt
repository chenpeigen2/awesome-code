package com.example.koin.di

import org.koin.dsl.module

// 定义示例类
class SingleRepository {
    val id: Int = (1000..9999).random()
}

class FactoryService {
    val id: Int = (1000..9999).random()
}

class ScopedHelper {
    val id: Int = (1000..9999).random()
}

val definitionModule = module {
    // single - 整个应用生命周期单例
    single { SingleRepository() }

    // factory - 每次获取都创建新实例
    factory { FactoryService() }

    // scoped - 作用域内单例 (需要在scope中声明)
}
