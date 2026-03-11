package com.peter.dagger.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.dagger.demo.DemoApplication
import com.peter.dagger.demo.databinding.FragmentScopeBinding
import com.peter.dagger.demo.di.ActivityContainer

/**
 * ScopeFragment - 作用域管理演示
 *
 * 学习目标：
 * 1. @Singleton - 应用级单例
 * 2. @ActivityScoped - Activity 级别单例
 * 3. 无作用域 - 每次创建新实例
 */
class ScopeFragment : Fragment() {

    private var _binding: FragmentScopeBinding? = null
    private val binding get() = _binding!!

    // 从 Application 获取应用容器
    private val appContainer by lazy {
        (requireActivity().application as DemoApplication).appContainer
    }

    // Activity 级容器
    private var activityContainer: ActivityContainer? = null

    companion object {
        fun newInstance() = ScopeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScopeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitle.text = "Step 2: 作用域管理"
        binding.tvDescription.text = """
            |学习 Dagger2 的作用域机制：
            |
            |1. @Singleton - 应用级单例
            |2. @ActivityScoped - Activity 级别
            |3. 无作用域 - 每次新建
            |
            |通过实例 ID 验证单例效果
        """.trimMargin()

        binding.btnCheckScope.setOnClickListener {
            checkScope()
        }
    }

    private fun checkScope() {
        // 确保 activityContainer 已初始化
        if (activityContainer == null) {
            activityContainer = appContainer.createActivityContainer()
        }

        val sb = StringBuilder()

        sb.appendLine()
        sb.appendLine("===== 作用域验证 =====")

        // @Singleton - 同一个实例
        val db = appContainer.databaseService
        sb.appendLine("DatabaseService (@Singleton)")
        sb.appendLine("  - 实例ID: ${db.instanceId}")

        // 多次获取验证单例
        val db1 = appContainer.databaseService
        val db2 = appContainer.databaseService
        sb.appendLine("  - 再次获取: ${db1.instanceId}")
        sb.appendLine("  - 相同实例: ${db1 === db2}")

        sb.appendLine()

        // @ActivityScoped - Activity 内单例
        val us = activityContainer!!.userService
        sb.appendLine("UserService (@ActivityScoped)")
        sb.appendLine("  - 实例ID: ${us.instanceId}")

        sb.appendLine()

        // 无作用域 - 每次都是新实例
        val req1 = appContainer.createRequestService()
        val req2 = appContainer.createRequestService()
        sb.appendLine("RequestService (无作用域)")
        sb.appendLine("  - 实例1: ${req1.instanceId}")
        sb.appendLine("  - 实例2: ${req2.instanceId}")
        sb.appendLine("  - 不同实例: ${req1 !== req2}")

        sb.appendLine()
        sb.appendLine("===== 作用域对比表 =====")
        sb.appendLine("| 服务 | 作用域 | 生命周期 |")
        sb.appendLine("|------|--------|----------|")
        sb.appendLine("| DatabaseService | @Singleton | 应用级单例 |")
        sb.appendLine("| UserService | @ActivityScoped | Activity 内单例 |")
        sb.appendLine("| RequestService | 无作用域 | 每次新建 |")

        binding.tvResult.text = sb.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
