package com.example.koin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.koin.KoinAdapter
import com.example.koin.KoinCategory
import com.example.koin.KoinItem
import com.example.koin.databinding.FragmentTestBinding

class TestFragment : Fragment() {

    private var _binding: FragmentTestBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = TestFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = listOf(
            KoinItem(
                title = "Module Verification",
                description = "验证模块定义的正确性，检查所有依赖是否可解析",
                codeSnippet = "val module = module { ... }\nmodule.verify()",
                category = KoinCategory.TESTING,
                action = {
                    Toast.makeText(
                        requireContext(),
                        "Module Verification:\nmodule.verify()\n检查模块中所有定义是否有效\n确保所有get()依赖都能解析",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "KoinTestRule",
                description = "JUnit测试规则，自动启动和停止Koin",
                codeSnippet = "@get:Rule\nval koinTestRule = KoinTestRule.create {\n  modules(testModule)\n}",
                category = KoinCategory.TESTING,
                action = {
                    Toast.makeText(
                        requireContext(),
                        "KoinTestRule:\n在每个测试前启动Koin\n测试后自动停止\n确保测试隔离",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "Mock Replacement",
                description = "用Mock对象替换真实依赖进行测试",
                codeSnippet = "loadKoinModules(module {\n  single<Repository> { mockRepo }\n})",
                category = KoinCategory.TESTING,
                action = {
                    Toast.makeText(
                        requireContext(),
                        "Mock Replacement:\n使用loadKoinModules()\n覆盖原有定义\n注入Mock对象进行测试",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "Check Modules",
                description = "检查所有模块配置是否完整",
                codeSnippet = "checkKoinModules()\n// 或\nkoin.checkModules()",
                category = KoinCategory.TESTING,
                action = {
                    Toast.makeText(
                        requireContext(),
                        "Check Modules:\nkoin.checkModules()\n验证所有模块定义\n确保没有循环依赖",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = KoinAdapter(items)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
