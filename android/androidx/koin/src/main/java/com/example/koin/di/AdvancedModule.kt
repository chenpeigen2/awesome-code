package com.example.koin.di

import org.koin.core.qualifier.named
import org.koin.dsl.module

// 命名限定符示例
class NamedService(val name: String)

// 注入参数示例
class ParameterService(val value: String)

// 属性注入示例
class PropertyService(val configValue: String)

// 接口绑定示例
interface Repository {
    fun getData(): String
}

class RealRepository : Repository {
    override fun getData(): String = "Real Data"
}

// 懒加载示例
class LazyService {
    val id: Int = (1000..9999).random()
}

val advancedModule = module {
    // Named - 命名限定符
    single(named("serviceA")) { NamedService("Service A") }
    single(named("serviceB")) { NamedService("Service B") }

    // Factory with parameters - 注入参数
    factory { params -> ParameterService(params.get()) }

    // Property - 从Koin properties获取
    single { PropertyService(getProperty("app.config", "default_value")) }

    // Interface Binding - 接口绑定
    single<Repository> { RealRepository() }

    // Single for lazy demo
    single { LazyService() }
}
