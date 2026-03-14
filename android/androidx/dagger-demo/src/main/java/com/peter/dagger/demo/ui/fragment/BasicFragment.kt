package com.peter.dagger.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.dagger.demo.DemoApplication
import com.peter.dagger.demo.databinding.FragmentBasicBinding
import com.peter.dagger.demo.model.CoffeeMaker
import javax.inject.Inject

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

    // 通过 Dagger2 注入 CoffeeMaker
    @Inject
    lateinit var coffeeMaker: CoffeeMaker

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

        // Dagger2 注入
        (requireActivity().application as DemoApplication)
            .appComponent.inject(this)

        binding.btnBrew.setOnClickListener {
            brewCoffee()
        }

        binding.btnCheckInstance.setOnClickListener {
            checkInstance()
        }
    }

    private fun brewCoffee() {
        val sb = StringBuilder()

        sb.appendLine("===== CoffeeMaker 信息 =====")
        sb.appendLine(coffeeMaker.toString())
        sb.appendLine()

        // 制作咖啡
        val result = coffeeMaker.brew()
        sb.append(result)

        binding.tvResult.text = sb.toString()
    }

    private fun checkInstance() {
        val sb = StringBuilder()

        sb.appendLine("===== 实例检查 =====")
        sb.appendLine("CoffeeMaker 实例:")
        sb.appendLine("  hashCode: ${coffeeMaker.hashCode()}")
        sb.appendLine()
        sb.appendLine("多次获取同一实例验证单例模式")
        sb.appendLine("尝试再次点击\"制作咖啡\"查看 hashCode 是否相同")

        binding.tvResult.text = sb.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}