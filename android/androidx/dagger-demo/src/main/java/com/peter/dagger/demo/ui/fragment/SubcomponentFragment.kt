package com.peter.dagger.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.dagger.demo.DemoApplication
import com.peter.dagger.demo.databinding.FragmentSubcomponentBinding
import com.peter.dagger.demo.scope.DatabaseService
import com.peter.dagger.demo.subcomponent.LoginComponent
import javax.inject.Inject

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

    // 通过 Dagger2 注入父组件的依赖
    @Inject
    lateinit var databaseService: DatabaseService

    // 子组件实例
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

        // Dagger2 注入
        (requireActivity().application as DemoApplication)
            .appComponent.inject(this)

        binding.btnCreateComponent.setOnClickListener {
            createSubcomponent()
        }

        binding.btnGetAuth.setOnClickListener {
            showAuthService()
        }

        binding.btnGetRepo.setOnClickListener {
            showUserRepository()
        }
    }

    private fun createSubcomponent() {
        // 通过 Dagger2 创建子组件
        val appComponent = (requireActivity().application as DemoApplication).appComponent
        loginComponent = appComponent.loginComponent().create()

        val sb = StringBuilder()

        sb.appendLine("===== 子组件创建成功 =====")
        sb.appendLine()
        sb.appendLine("父组件依赖:")
        sb.appendLine("  DatabaseService ID: ${databaseService.instanceId}")
        sb.appendLine()
        sb.appendLine("子组件可以访问父组件的所有依赖")
        sb.appendLine()
        sb.appendLine("点击下方按钮获取子组件中的服务")

        binding.tvResult.text = sb.toString()
    }

    private fun showAuthService() {
        if (loginComponent == null) {
            binding.tvResult.text = "请先创建 LoginComponent"
            return
        }

        val authService = loginComponent!!.authService

        val sb = StringBuilder()

        sb.appendLine("===== AuthService =====")
        sb.appendLine()
        sb.appendLine("实例ID: ${authService.getInstanceId()}")
        sb.appendLine("hashCode: ${authService.hashCode()}")
        sb.appendLine()

        // 模拟登录
        val result = authService.login("user", "password")
        sb.appendLine("模拟登录: ${if (result) "成功" else "失败"}")
        sb.appendLine("当前用户: ${authService.getCurrentUser()}")
        sb.appendLine("认证状态: ${authService.isAuthenticated()}")

        binding.tvResult.text = sb.toString()
    }

    private fun showUserRepository() {
        if (loginComponent == null) {
            binding.tvResult.text = "请先创建 LoginComponent"
            return
        }

        val userRepository = loginComponent!!.userRepository

        val sb = StringBuilder()

        sb.appendLine("===== UserRepository =====")
        sb.appendLine()
        sb.appendLine("实例ID: ${userRepository.getInstanceId()}")
        sb.appendLine("hashCode: ${userRepository.hashCode()}")
        sb.appendLine()
        sb.appendLine("用户信息: ${userRepository.getUserInfo("1001")}")
        sb.appendLine()
        sb.appendLine("子组件可以访问父组件的 DatabaseService")

        binding.tvResult.text = sb.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}