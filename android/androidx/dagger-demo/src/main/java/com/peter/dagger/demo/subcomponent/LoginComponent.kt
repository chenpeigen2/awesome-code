package com.peter.dagger.demo.subcomponent

import dagger.Subcomponent
import javax.inject.Scope

/**
 * LoginScope - 登录作用域
 *
 * 子组件的自定义作用域
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class LoginScope

/**
 * LoginComponent - 登录子组件
 *
 * 演示 Dagger2 的 @Subcomponent 功能
 * 子组件可以访问父组件的依赖
 */
@LoginScope
@Subcomponent
interface LoginComponent {

    // 子组件可以提供的依赖
    val authService: AuthService
    val userRepository: UserRepository

    // 子组件工厂 - 由父组件提供
    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginComponent
    }
}

/**
 * LoginModule - 登录模块
 *
 * 子组件的模块（可选）
 */
@dagger.Module
object LoginModule