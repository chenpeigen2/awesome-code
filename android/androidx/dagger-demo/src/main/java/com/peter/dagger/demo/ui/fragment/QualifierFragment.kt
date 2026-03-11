package com.peter.dagger.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.dagger.demo.DemoApplication
import com.peter.dagger.demo.databinding.FragmentQualifierBinding
import com.peter.dagger.demo.di.AppContainer
import com.peter.dagger.demo.qualifier.DataSource

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

    // 从 Application 获取依赖容器
    private val appContainer: AppContainer by lazy {
        (requireActivity().application as DemoApplication).appContainer
    }

    // 使用限定符获取不同的数据源实现
    // 对应 Dagger2: @Inject @LocalDataSource lateinit var localSource: DataSource
    private val localDataSource: DataSource by lazy {
        appContainer.localDataSource
    }

    // 对应 Dagger2: @Inject @RemoteDataSource lateinit var remoteSource: DataSource
    private val remoteDataSource: DataSource by lazy {
        appContainer.remoteDataSource
    }

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
        val sb = StringBuilder()

        sb.appendLine("===== 限定符 (Qualifier) 演示 =====")
        sb.appendLine()
        sb.appendLine("同一接口 DataSource 有两个实现：")
        sb.appendLine()

        sb.appendLine("📁 本地数据源 (@LocalDataSource)")
        sb.appendLine("  - 类型: ${localDataSource.getSourceType()}")
        sb.appendLine("  - 实例ID: ${localDataSource.getInstanceId()}")
        sb.appendLine("  - 数据: ${localDataSource.fetchData()}")
        sb.appendLine()

        sb.appendLine("☁️ 远程数据源 (@RemoteDataSource)")
        sb.appendLine("  - 类型: ${remoteDataSource.getSourceType()}")
        sb.appendLine("  - 实例ID: ${remoteDataSource.getInstanceId()}")
        sb.appendLine("  - 数据: ${remoteDataSource.fetchData()}")
        sb.appendLine()

        sb.appendLine("===== Dagger2 中的用法 =====")
        sb.appendLine()
        sb.appendLine("// 1. 定义限定符")
        sb.appendLine("@Qualifier")
        sb.appendLine("annotation class LocalDataSource")
        sb.appendLine()
        sb.appendLine("// 2. 在 Module 中提供")
        sb.appendLine("@Provides @LocalDataSource")
        sb.appendLine("fun provideLocal(): DataSource = LocalDataSourceImpl()")
        sb.appendLine()
        sb.appendLine("// 3. 注入时使用限定符")
        sb.appendLine("@Inject @LocalDataSource")
        sb.appendLine("lateinit var localSource: DataSource")

        binding.tvResult.text = sb.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
