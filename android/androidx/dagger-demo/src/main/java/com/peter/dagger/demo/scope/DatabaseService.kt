package com.peter.dagger.demo.scope

import java.util.UUID

/**
 * DatabaseService - 数据库服务
 *
 * 模拟 Dagger2 的 @Singleton 作用域
 * 在应用生命周期内只有一个实例
 */
class DatabaseService {

    val instanceId: String = UUID.randomUUID().toString()

    init {
        println("DatabaseService: 创建单例实例 [$instanceId]")
    }

    override fun toString(): String {
        return "DatabaseService(hashCode=${hashCode()}, instanceId=$instanceId)"
    }
}
