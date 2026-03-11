package com.peter.dagger.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.dagger.demo.DemoApplication
import com.peter.dagger.demo.databinding.FragmentBasicBinding
import com.peter.dagger.demo.di.AppContainer
import com.peter.dagger.demo.model.CoffeeMaker

/**
 * BasicFragment - 基础注入演示
 *
 * 学习目标：
 * 1. @Inject 构造器注入
 * 2. @Module + @Provides 提供依赖
 * 3. @Component 连接注入
 */
class BasicFragment : Fragment() {

    private var _binding: FragmentBasicBinding? = null
    private val binding get() = _binding!!

    // 从 Application 获取依赖容器
    private val appContainer: AppContainer by lazy {
        (requireActivity().application as DemoApplication).appContainer
    }

    // 获取 CoffeeMaker 实例
    private val coffeeMaker: CoffeeMaker by lazy {
        appContainer.coffeeMaker
    }

    companion object {
        fun newInstance() = BasicFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBasicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitle.text = "Step 1: 基础注入"
        binding.tvDescription.text = """
            |学习 Dagger2 的三个核心注解：
            |1. @Inject - 标记需要注入的位置
            |2. @Module - 定义如何提供依赖
            |3. @Component - 连接注入点和依赖提供者
            |
            |点击下方按钮查看注入结果
        """.trimMargin()

        binding.btnInject.setOnClickListener {
            showInjectionResult()
        }
    }

    private fun showInjectionResult() {
        val sb = StringBuilder()

        sb.appendLine("===== CoffeeMaker 信息 =====")
        sb.appendLine(coffeeMaker.toString())
        sb.appendLine("===== 开始制作咖啡 =====")

        // 执行注入，制作咖啡
        val result = coffeeMaker.brew()
        sb.append(result)

        binding.tvResult.text = sb.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
