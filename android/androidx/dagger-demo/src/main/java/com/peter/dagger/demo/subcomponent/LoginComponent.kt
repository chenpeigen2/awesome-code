package com.peter.dagger.demo.subcomponent

/**
 * LoginComponent - 登录子组件
 *
 * 手动 DI 实现的子组件模式
 * 模拟 Dagger2 的 @Subcomponent 功能
 *
 * ========================
 * Dagger2 中的写法
 * ========================
 *
 * // 定义子组件
 * @Subcomponent(modules = [LoginModule::class])
 * @LoginScope
 * interface LoginComponent {
 *
 *     // 子组件可以提供的依赖
 *     val authService: AuthService
 *     val userRepository: UserRepository
 *
 *     // 注入方法
 *     fun inject(activity: LoginActivity)
 *
 *     // 子组件工厂
 *     @Subcomponent.Factory
 *     interface Factory {
 *         fun create(): LoginComponent
 *     }
 * }
 *
 * // 在父组件中声明子组件工厂
 * @Component(modules = [AppModule::class])
 * @Singleton
 * interface AppComponent {
 *     // 提供创建 LoginComponent 的工厂
 *     fun loginComponent(): LoginComponent.Factory
 * }
 *
 * ========================
 * 子组件特点
 * ========================
 *
 * 1. 继承父组件的依赖图
 *    - 可以访问父组件提供的所有依赖
 *    - 例如 UserRepository 可以注入 DatabaseService
 *
 * 2. 封装特定作用域
 *    - LoginComponent 有自己的 @LoginScope
 *    - 子组件内的单例只在子组件生命周期内有效
 *
 * 3. 生命周期管理
 *    - 子组件通常与特定流程绑定（如登录流程）
 *    - 流程结束后销毁子组件，释放资源
 */
class LoginComponent(
    // 从父组件继承的依赖
    private val databaseName: String
) {

    // 子组件作用域内的单例
    private val _authService: AuthService by lazy { AuthService() }
    val authService: AuthService get() = _authService

    private val _userRepository: UserRepository by lazy { UserRepository(databaseName) }
    val userRepository: UserRepository get() = _userRepository

    /**
     * 获取子组件信息
     */
    fun getComponentInfo(): String {
        return """
            |LoginComponent 信息:
            |- 父组件依赖: databaseName=$databaseName
            |- AuthService: ${authService.getInstanceId()}
            |- UserRepository: ${userRepository.getInstanceId()}
        """.trimMargin()
    }
}
