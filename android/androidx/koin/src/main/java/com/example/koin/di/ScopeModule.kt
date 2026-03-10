package com.example.koin.di

import com.example.koin.MainActivity
import org.koin.dsl.module

// 作用域示例类
class ActivityScopedService {
    val id: Int = (1000..9999).random()
}

class LinkedService {
    val id: Int = (1000..9999).random()
}

val scopeModule = module {
    // Activity作用域
    scope<MainActivity> {
        scoped { ActivityScopedService() }
    }
}
