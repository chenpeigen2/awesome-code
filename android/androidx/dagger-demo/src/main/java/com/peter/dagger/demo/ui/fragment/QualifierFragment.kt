package com.peter.dagger.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.dagger.demo.databinding.FragmentQualifierBinding

/**
 * QualifierFragment - 限定符演示
 *
 * 学习目标：
 * 1. @Qualifier 自定义限定符
 * 2. @Named 命名注入
 * 3. 同一接口多个实现
 */
class QualifierFragment : Fragment() {

    private var _binding: FragmentQualifierBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = QualifierFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQualifierBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitle.text = "Step 3: 限定符"
        binding.tvDescription.text = """
            |学习如何处理同一接口的多个实现：
            |
            |1. @Qualifier - 自定义限定符注解
            |2. @Named - 字符串命名的限定符
            |3. 使用场景：本地/远程数据源
        """.trimMargin()

        binding.btnShowQualifiers.setOnClickListener {
            showQualifiers()
        }
    }

    private fun showQualifiers() {
        // TODO: 在 Phase 4 实现限定符演示
        binding.tvResult.text = """
            |Qualifier Demo Result:
            |
            |限定符示例将在 Phase 4 实现
            |
            |自定义限定符示例:
            |@Qualifier
            |annotation class LocalDataSource
            |
            |@Qualifier
            |annotation class RemoteDataSource
        """.trimMargin()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
