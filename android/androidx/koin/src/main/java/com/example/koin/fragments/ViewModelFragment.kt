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
import com.example.koin.databinding.FragmentViewmodelBinding
import com.example.koin.viewmodel.DemoViewModel
import com.example.koin.viewmodel.FactoryViewModel
import com.example.koin.viewmodel.SavedStateViewModel
import com.example.koin.viewmodel.SharedViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ViewModelFragment : Fragment() {

    private var _binding: FragmentViewmodelBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModel()

    companion object {
        fun newInstance() = ViewModelFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewmodelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = listOf(
            KoinItem(
                title = "Basic ViewModel",
                description = "基础的ViewModel注入",
                codeSnippet = "viewModel { DemoViewModel() }\nby viewModel<DemoViewModel>()",
                category = KoinCategory.VIEWMODELS,
                action = {
                    val vm: DemoViewModel by viewModel()
                    Toast.makeText(
                        requireContext(),
                        "Basic ViewModel:\n${vm.javaClass.simpleName}\nid: ${vm.id}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "SavedState ViewModel",
                description = "带SavedStateHandle的ViewModel，支持状态保存",
                codeSnippet = "viewModel { SavedStateVM(get()) }",
                category = KoinCategory.VIEWMODELS,
                action = {
                    val vm: SavedStateViewModel by viewModel()
                    vm.saveValue("demo_key", "demo_value")
                    val saved = vm.getValue("demo_key")
                    Toast.makeText(
                        requireContext(),
                        "SavedState ViewModel:\n保存: demo_key=demo_value\n读取: $saved",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "Factory ViewModel",
                description = "带参数的ViewModel工厂",
                codeSnippet = "viewModel { params ->\n  FactoryVM(params.get())\n}\nviewModel { parametersOf(\"param\") }",
                category = KoinCategory.VIEWMODELS,
                action = {
                    val param = "CustomParam_${System.currentTimeMillis() % 1000}"
                    val vm: FactoryViewModel by viewModel { parametersOf(param) }
                    Toast.makeText(
                        requireContext(),
                        "Factory ViewModel:\n传入参数: $param\nViewModel收到: ${vm.injectedParam}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "Shared ViewModel",
                description = "多个Fragment共享同一个ViewModel实例",
                codeSnippet = "// Fragment A & B\nby activityViewModel<SharedVM>()",
                category = KoinCategory.VIEWMODELS,
                action = {
                    val currentTime = System.currentTimeMillis().toString()
                    sharedViewModel.data = "Updated at $currentTime"
                    Toast.makeText(
                        requireContext(),
                        "Shared ViewModel:\nid: ${sharedViewModel.id}\n数据: ${sharedViewModel.data}\n(与Activity共享同一实例)",
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
