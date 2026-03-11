package com.peter.dagger.demo.scope

import java.util.UUID

/**
 * UserService - 用户服务
 *
 * 模拟 Dagger2 的 @ActivityScoped 作用域
 * 在 Activity 生命周期内只有一个实例
 */
class UserService {

    val instanceId: String = UUID.randomUUID().toString()
    private var userId: String? = null

    init {
        println("UserService: 创建新实例 [$instanceId]")
    }

    fun setUserId(id: String) {
        this.userId = id
    }

    fun getUserId(): String {
        return userId ?: "unknown"
    }

    override fun toString(): String {
        return "UserService(hashCode=${hashCode()}, instanceId=$instanceId)"
    }
}
