package com.peter.dagger.demo.subcomponent

import java.util.UUID

/**
 * AuthService - 认证服务
 *
 * 属于 LoginComponent 子组件的依赖
 * 在登录流程中管理认证状态
 */
class AuthService {

    private val instanceId = UUID.randomUUID().toString().substring(0, 8)
    private var isLoggedIn = false
    private var currentUser: String? = null

    /**
     * 登录
     */
    fun login(username: String, password: String): Boolean {
        // 模拟登录验证
        val success = password.isNotEmpty()
        if (success) {
            isLoggedIn = true
            currentUser = username
        }
        return success
    }

    /**
     * 登出
     */
    fun logout() {
        isLoggedIn = false
        currentUser = null
    }

    /**
     * 检查是否已登录
     */
    fun isAuthenticated(): Boolean = isLoggedIn

    /**
     * 获取当前用户
     */
    fun getCurrentUser(): String? = currentUser

    /**
     * 获取实例ID
     */
    fun getInstanceId(): String = instanceId

    override fun toString(): String {
        return "AuthService(instanceId=$instanceId, loggedIn=$isLoggedIn, user=$currentUser)"
    }
}
