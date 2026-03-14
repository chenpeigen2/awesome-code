package com.peter.dagger.demo.subcomponent

import java.util.UUID
import javax.inject.Inject

/**
 * AuthService - 认证服务
 *
 * 属于 LoginComponent 子组件的依赖
 */
class AuthService @Inject constructor() {

    private val instanceId = UUID.randomUUID().toString().substring(0, 8)
    private var isLoggedIn = false
    private var currentUser: String? = null

    fun login(username: String, password: String): Boolean {
        val success = password.isNotEmpty()
        if (success) {
            isLoggedIn = true
            currentUser = username
        }
        return success
    }

    fun logout() {
        isLoggedIn = false
        currentUser = null
    }

    fun isAuthenticated(): Boolean = isLoggedIn

    fun getCurrentUser(): String? = currentUser

    fun getInstanceId(): String = instanceId

    override fun toString(): String {
        return "AuthService(instanceId=$instanceId, loggedIn=$isLoggedIn, user=$currentUser)"
    }
}