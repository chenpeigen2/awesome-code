package com.peter.dagger.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.peter.dagger.demo.DemoApplication
import com.peter.dagger.demo.android.DemoViewModel
import com.peter.dagger.demo.databinding.FragmentAndroidBinding
import com.peter.dagger.demo.di.AppContainer

/**
 * AndroidFragment - Android 集成演示
 *
 * 学习目标：
 * 1. Activity/Fragment 注入
 * 2. ViewModel 手动注入
 * 3. Dagger2 在 Android 中的最佳实践
 */
class AndroidFragment : Fragment() {

    private var _binding: FragmentAndroidBinding? = null
    private val binding get() = _binding!!

    // 从 Application 获取依赖容器
    private val appContainer: AppContainer by lazy {
        (requireActivity().application as DemoApplication).appContainer
    }

    // 使用 ViewModel Factory 创建 ViewModel
    // 对应 Dagger2: private val viewModel: DemoViewModel by viewModels { viewModelFactory }
    // 其中 viewModelFactory 通过 @Inject 注入
    private val viewModel: DemoViewModel by viewModels {
        appContainer.demoViewModelFactory
    }

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

        binding.tvTitle.text = "Step 5: Android 集成"
        binding.tvDescription.text = """
            |学习 Dagger2 在 Android 中的应用：
            |
            |1. 手动调用 component.inject(this)
            |2. ViewModel Factory 注入
            |3. 与 Hilt 的对比
        """.trimMargin()

        binding.btnShowAndroid.setOnClickListener {
            showAndroidIntegration()
        }
    }

    private fun showAndroidIntegration() {
        val sb = StringBuilder()

        sb.appendLine("===== Android 集成演示 =====")
        sb.appendLine()

        sb.appendLine("📦 ViewModel 信息")
        sb.appendLine(viewModel.getDataSourceInfo())
        sb.appendLine()

        sb.appendLine("📂 数据获取演示")
        sb.appendLine("- 本地数据: ${viewModel.getLocalData()}")
        sb.appendLine("- 远程数据: ${viewModel.getRemoteData()}")
        sb.appendLine()

        sb.appendLine("===== Dagger2 vs Hilt 对比 =====")
        sb.appendLine()
        sb.appendLine("Dagger2 (手动方式):")
        sb.appendLine("1. 创建 ViewModelFactory")
        sb.appendLine("2. 在 Component 中提供 Factory")
        sb.appendLine("3. 在 Fragment 中获取 Factory")
        sb.appendLine("4. 使用 by viewModels { factory }")
        sb.appendLine()
        sb.appendLine("Hilt (自动方式):")
        sb.appendLine("@HiltViewModel")
        sb.appendLine("class DemoViewModel @Inject constructor(")
        sb.appendLine("  private val dataSource: DataSource")
        sb.appendLine(") : ViewModel()")
        sb.appendLine()
        sb.appendLine("// 直接使用，无需 Factory")
        sb.appendLine("private val viewModel: DemoViewModel by viewModels()")
        sb.appendLine()

        sb.appendLine("===== Fragment 注入方式 =====")
        sb.appendLine()
        sb.appendLine("Dagger2:")
        sb.appendLine("override fun onAttach(context: Context) {")
        sb.appendLine("  (context.application as DemoApp)")
        sb.appendLine("    .appComponent.inject(this)")
        sb.appendLine("  super.onAttach(context)")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("Hilt:")
        sb.appendLine("@AndroidEntryPoint")
        sb.appendLine("class MyFragment : Fragment() {")
        sb.appendLine("  @Inject lateinit var service: MyService")
        sb.appendLine("}")

        binding.tvResult.text = sb.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
