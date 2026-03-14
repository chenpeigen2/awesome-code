package com.peter.dagger.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.peter.dagger.demo.DemoApplication
import com.peter.dagger.demo.android.DemoViewModel
import com.peter.dagger.demo.android.DemoViewModelFactory
import com.peter.dagger.demo.databinding.FragmentAndroidBinding
import javax.inject.Inject

/**
 * AndroidFragment - Android 集成演示
 *
 * 学习目标：
 * 1. Activity/Fragment 注入
 * 2. ViewModel Factory 注入
 * 3. Dagger2 在 Android 中的最佳实践
 */
class AndroidFragment : Fragment() {

    private var _binding: FragmentAndroidBinding? = null
    private val binding get() = _binding!!

    // 通过 Dagger2 注入 ViewModelFactory
    @Inject
    lateinit var viewModelFactory: DemoViewModelFactory

    // 使用 Factory 创建 ViewModel
    private val viewModel: DemoViewModel by viewModels { viewModelFactory }

    companion object {
        fun newInstance() = AndroidFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAndroidBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Dagger2 注入
        (requireActivity().application as DemoApplication)
            .appComponent.inject(this)

        binding.btnGetViewModel.setOnClickListener {
            showViewModelInfo()
        }
    }

    private fun showViewModelInfo() {
        val sb = StringBuilder()

        sb.appendLine("===== ViewModel 注入 =====")
        sb.appendLine()
        sb.appendLine("ViewModel 实例信息:")
        sb.appendLine("  instanceId: ${viewModel.getInstanceId()}")
        sb.appendLine()
        sb.appendLine(viewModel.getDataSourceInfo())
        sb.appendLine()
        sb.appendLine("数据获取:")
        sb.appendLine("  本地数据: ${viewModel.getLocalData()}")
        sb.appendLine("  远程数据: ${viewModel.getRemoteData()}")
        sb.appendLine()
        sb.appendLine("===== Dagger2 用法 =====")
        sb.appendLine()
        sb.appendLine("// 1. 定义 ViewModel")
        sb.appendLine("class DemoViewModel @Inject constructor(")
        sb.appendLine("  @LocalDataSource val local: DataSource,")
        sb.appendLine("  @RemoteDataSource val remote: DataSource")
        sb.appendLine(") : ViewModel()")
        sb.appendLine()
        sb.appendLine("// 2. 定义 Factory")
        sb.appendLine("class Factory @Inject constructor(")
        sb.appendLine("  @LocalDataSource val local: DataSource,")
        sb.appendLine("  @RemoteDataSource val remote: DataSource")
        sb.appendLine(") : ViewModelProvider.Factory")
        sb.appendLine()
        sb.appendLine("// 3. 注入 Factory 并使用")
        sb.appendLine("@Inject lateinit var factory: Factory")
        sb.appendLine("val vm: DemoViewModel by viewModels { factory }")

        binding.tvResult.text = sb.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}