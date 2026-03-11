package com.peter.dagger.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.dagger.demo.databinding.FragmentSubcomponentBinding

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
        // TODO: 在 Phase 5 实现子组件演示
        binding.tvResult.text = """
            |Subcomponent Demo Result:
            |
            |子组件示例将在 Phase 5 实现
            |
            |组件继承关系:
            |AppComponent (应用级)
            |    └── LoginComponent (登录流程)
            |            └── UserRepository
            |            └── AuthService
        """.trimMargin()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
