package com.peter.dagger.demo.scope

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DatabaseService - 数据库服务
 *
 * @Singleton 单例作用域
 */
@Singleton
class DatabaseService @Inject constructor() {

    val instanceId: String = UUID.randomUUID().toString()

    init {
        println("DatabaseService: 创建单例实例 [$instanceId]")
    }

    override fun toString(): String {
        return "DatabaseService(hashCode=${hashCode()}, instanceId=$instanceId)"
    }
}