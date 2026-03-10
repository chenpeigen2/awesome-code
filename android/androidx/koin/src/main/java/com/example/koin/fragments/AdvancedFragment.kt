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
import com.example.koin.databinding.FragmentAdvancedBinding
import com.example.koin.di.NamedService
import com.example.koin.di.ParameterService
import com.example.koin.di.PropertyService
import com.example.koin.di.Repository
import com.example.koin.di.LazyService
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

class AdvancedFragment : Fragment() {

    private var _binding: FragmentAdvancedBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = AdvancedFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdvancedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = listOf(
            KoinItem(
                title = "Named - 命名限定符",
                description = "使用名称区分同一类型的不同实例",
                codeSnippet = "single(named(\"A\")) { Service(\"A\") }\nget<Service>(named(\"A\"))",
                category = KoinCategory.ADVANCED,
                action = {
                    val serviceA: NamedService = get(named("serviceA"))
                    val serviceB: NamedService = get(named("serviceB"))
                    Toast.makeText(
                        requireContext(),
                        "Named:\nserviceA: ${serviceA.name}\nserviceB: ${serviceB.name}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "Injected Parameters",
                description = "注入时传递参数",
                codeSnippet = "factory { params ->\n  Service(params.get())\n}\nget { parametersOf(\"value\") }",
                category = KoinCategory.ADVANCED,
                action = {
                    val service: ParameterService = get { parametersOf("Hello Koin!") }
                    Toast.makeText(
                        requireContext(),
                        "Injected Params:\n传入参数: \"Hello Koin!\"\n收到值: ${service.value}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "Property Injection",
                description = "从Koin属性中获取配置值",
                codeSnippet = "startKoin {\n  properties(mapOf(\"key\" to \"value\"))\n}\ngetProperty(\"key\")",
                category = KoinCategory.ADVANCED,
                action = {
                    val service: PropertyService = get()
                    Toast.makeText(
                        requireContext(),
                        "Property:\n从Koin获取配置\n值: ${service.configValue}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "Lazy Injection",
                description = "延迟获取依赖，使用时才初始化",
                codeSnippet = "by inject<Service>()\n// 或\nby lazy { get<Service>() }",
                category = KoinCategory.ADVANCED,
                action = {
                    val lazyService: LazyService by lazy { get() }
                    Toast.makeText(
                        requireContext(),
                        "Lazy Injection:\nby lazy { get() }\n首次访问时才创建\nid: ${lazyService.id}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "Interface Binding",
                description = "通过接口类型获取实现类",
                codeSnippet = "single<Repository> { RealRepository() }\nget<Repository>()",
                category = KoinCategory.ADVANCED,
                action = {
                    val repo: Repository = get()
                    Toast.makeText(
                        requireContext(),
                        "Interface Binding:\nget<Repository>()\n返回: ${repo.javaClass.simpleName}\n数据: ${repo.getData()}",
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
