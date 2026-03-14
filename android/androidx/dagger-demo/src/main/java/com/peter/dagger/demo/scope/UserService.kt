package com.peter.dagger.demo.scope

import java.util.UUID
import javax.inject.Inject

/**
 * UserService - 用户服务
 *
 * 无作用域 - 每次创建新实例
 */
class UserService @Inject constructor() {

    val instanceId: String = UUID.randomUUID().toString()
    private var userId: String? = null

    init {
        println("UserService: 创建新实例 [$instanceId]")
    }

    fun setUserId(id: String) {
        this.userId = id
    }

    fun getUserId(): String = userId ?: "unknown"

    override fun toString(): String {
        return "UserService(hashCode=${hashCode()}, instanceId=$instanceId)"
    }
}
