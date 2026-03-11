package com.peter.dagger.demo.subcomponent

import java.util.UUID

/**
 * UserRepository - 用户仓库
 *
 * 属于 LoginComponent 子组件的依赖
 * 可以访问父组件 (AppComponent) 的依赖
 */
class UserRepository(
    // 可以注入父组件的依赖，如 DatabaseService
    private val databaseName: String
) {

    private val instanceId = UUID.randomUUID().toString().substring(0, 8)

    /**
     * 获取用户信息
     */
    fun getUserInfo(userId: String): String {
        return "用户信息: ID=$userId, 来自数据库=$databaseName"
    }

    /**
     * 保存用户信息
     */
    fun saveUserInfo(userId: String, info: String) {
        println("UserRepository: 保存用户 $userId 的信息: $info")
    }

    /**
     * 获取实例ID
     */
    fun getInstanceId(): String = instanceId

    override fun toString(): String {
        return "UserRepository(instanceId=$instanceId, db=$databaseName)"
    }
}
