package com.peter.dagger.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.dagger.demo.DemoApplication
import com.peter.dagger.demo.databinding.FragmentSubcomponentBinding
import com.peter.dagger.demo.di.AppContainer
import com.peter.dagger.demo.subcomponent.LoginComponent

/**
 * SubcomponentFragment - 子组件演示
 *
 * 学习目标：
 * 1. @Subcomponent 子组件
 * 2. 组件继承关系
 * 3. 作用域传递
 */
class SubcomponentFragment : Fragment() {

    private var _binding: FragmentSubcomponentBinding? = null
    private val binding get() = _binding!!

    // 从 Application 获取依赖容器
    private val appContainer: AppContainer by lazy {
        (requireActivity().application as DemoApplication).appContainer
    }

    // 创建 LoginComponent 子组件
    // 对应 Dagger2: val loginComponent = appComponent.loginComponent().create()
    private var loginComponent: LoginComponent? = null

    companion object {
        fun newInstance() = SubcomponentFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubcomponentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitle.text = "Step 4: 子组件"
        binding.tvDescription.text = """
            |学习 Dagger2 的子组件机制：
            |
            |1. @Subcomponent - 子组件定义
            |2. 继承父组件的依赖
            |3. 封装特定功能的依赖图
            |
            |示例：登录流程子组件
        """.trimMargin()

        binding.btnShowSubcomponent.setOnClickListener {
            showSubcomponent()
        }
    }

    private fun showSubcomponent() {
        // 创建子组件（每次点击创建新的子组件实例）
        loginComponent = appContainer.createLoginComponent()

        val sb = StringBuilder()

        sb.appendLine("===== 子组件 (Subcomponent) 演示 =====")
        sb.appendLine()

        sb.appendLine("📦 父组件 (AppContainer)")
        sb.appendLine("  - DatabaseService ID: ${appContainer.databaseService.instanceId}")
        sb.appendLine()

        sb.appendLine("🔧 子组件 (LoginComponent)")
        sb.appendLine("  - 可访问父组件的 DatabaseService")

        // 获取子组件中的依赖
        val authService = loginComponent!!.authService
        val userRepository = loginComponent!!.userRepository

        sb.appendLine("  - AuthService ID: ${authService.getInstanceId()}")
        sb.appendLine("  - UserRepository ID: ${userRepository.getInstanceId()}")
        sb.appendLine()

        sb.appendLine("===== 模拟登录流程 =====")
        sb.appendLine()

        // 使用 AuthService 进行登录
        val loginResult = authService.login("test_user", "password123")
        sb.appendLine("登录结果: ${if (loginResult) "成功 ✓" else "失败 ✗"}")
        sb.appendLine("当前用户: ${authService.getCurrentUser()}")
        sb.appendLine("认证状态: ${if (authService.isAuthenticated()) "已认证" else "未认证"}")
        sb.appendLine()

        // 使用 UserRepository
        val userInfo = userRepository.getUserInfo("1001")
        sb.appendLine("用户信息: $userInfo")
        sb.appendLine()

        sb.appendLine("===== Dagger2 中的用法 =====")
        sb.appendLine()
        sb.appendLine("// 定义子组件")
        sb.appendLine("@Subcomponent @LoginScope")
        sb.appendLine("interface LoginComponent { ... }")
        sb.appendLine()
        sb.appendLine("// 在父组件中声明工厂")
        sb.appendLine("@Component interface AppComponent {")
        sb.appendLine("  fun loginComponent(): LoginComponent.Factory")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("// 创建子组件")
        sb.appendLine("val loginComponent = appComponent")
        sb.appendLine("    .loginComponent().create()")

        binding.tvResult.text = sb.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
