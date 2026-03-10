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
import com.example.koin.databinding.FragmentDefinitionBinding
import com.example.koin.di.FactoryService
import com.example.koin.di.SingleRepository
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.qualifier.named

class DefinitionFragment : Fragment() {

    private var _binding: FragmentDefinitionBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = DefinitionFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDefinitionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = listOf(
            KoinItem(
                title = "single - 单例",
                description = "整个应用生命周期只创建一次实例",
                codeSnippet = "single { Repository() }",
                category = KoinCategory.DEFINITIONS,
                action = {
                    val repo1: SingleRepository = get()
                    val repo2: SingleRepository = get()
                    Toast.makeText(
                        requireContext(),
                        "single: 两次获取hashCode相同\n${repo1.id} == ${repo2.id}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "factory - 工厂",
                description = "每次获取都创建新实例",
                codeSnippet = "factory { Service() }",
                category = KoinCategory.DEFINITIONS,
                action = {
                    val service1: FactoryService = get()
                    val service2: FactoryService = get()
                    Toast.makeText(
                        requireContext(),
                        "factory: 两次获取hashCode不同\n${service1.id} != ${service2.id}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "scoped - 作用域单例",
                description = "在作用域内是单例，不同作用域是不同实例",
                codeSnippet = "scope<Activity> {\n  scoped { Helper() }\n}",
                category = KoinCategory.DEFINITIONS,
                action = {
                    // 创建两个不同的scope
                    val scope1 = getKoin().createScope("scope_1", named("demo"))
                    val scope2 = getKoin().createScope("scope_2", named("demo"))
                    
                    Toast.makeText(
                        requireContext(),
                        "Scoped Demo:\nscope1.id=${scope1.id}\nscope2.id=${scope2.id}\n不同scope有不同实例",
                        Toast.LENGTH_LONG
                    ).show()
                    
                    scope1.close()
                    scope2.close()
                }
            ),
            KoinItem(
                title = "viewModel - ViewModel集成",
                description = "Koin与Android ViewModel无缝集成",
                codeSnippet = "viewModel { MyViewModel() }",
                category = KoinCategory.DEFINITIONS,
                action = {
                    val vm = getViewModel<com.example.koin.viewmodel.DemoViewModel>()
                    Toast.makeText(
                        requireContext(),
                        "viewModel: ${vm.javaClass.simpleName}\nid: ${vm.id}",
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