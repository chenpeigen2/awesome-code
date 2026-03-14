package com.peter.dagger.demo.subcomponent

import java.util.UUID
import javax.inject.Inject

/**
 * UserRepository - 用户仓库
 *
 * 属于 LoginComponent 子组件的依赖
 */
class UserRepository @Inject constructor() {

    private val instanceId = UUID.randomUUID().toString().substring(0, 8)

    fun getUserInfo(userId: String): String {
        return "用户信息: ID=$userId"
    }

    fun saveUserInfo(userId: String, info: String) {
        println("UserRepository: 保存用户 $userId 的信息: $info")
    }

    fun getInstanceId(): String = instanceId

    override fun toString(): String {
        return "UserRepository(instanceId=$instanceId)"
    }
}