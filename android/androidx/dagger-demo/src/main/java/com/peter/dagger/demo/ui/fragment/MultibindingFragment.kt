package com.peter.dagger.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.dagger.demo.DemoApplication
import com.peter.dagger.demo.assisted.TaskProcessor
import com.peter.dagger.demo.databinding.FragmentMultibindingBinding
import com.peter.dagger.demo.multibinding.Plugin
import javax.inject.Inject

/**
 * MultibindingFragment - 多绑定与 AssistedInject 演示
 *
 * 学习目标：
 * 1. @IntoSet / @StringKey - 集合注入
 * 2. @AssistedInject / @AssistedFactory - 工厂注入
 */
class MultibindingFragment : Fragment() {

    private var _binding: FragmentMultibindingBinding? = null
    private val binding get() = _binding!!

    // Multibindings: Set<Plugin> 自动收集所有插件
    // Kotlin 泛型需要使用 @JvmSuppressWildcards
    @Inject
    lateinit var plugins: @JvmSuppressWildcards Set<Plugin>

    // Multibindings: Map<String, Plugin>
    @Inject
    lateinit var pluginMap: @JvmSuppressWildcards Map<String, Plugin>

    // AssistedInject: 工厂注入
    @Inject
    lateinit var taskProcessorFactory: TaskProcessor.Factory

    companion object {
        fun newInstance() = MultibindingFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMultibindingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Dagger2 注入
        (requireActivity().application as DemoApplication)
            .appComponent.inject(this)

        binding.btnShowPlugins.setOnClickListener {
            showPlugins()
        }

        binding.btnShowMap.setOnClickListener {
            showPluginMap()
        }

        binding.btnAssistedInject.setOnClickListener {
            showAssistedInject()
        }
    }

    private fun showPlugins() {
        val sb = StringBuilder()

        sb.appendLine("===== @IntoSet Set 注入 =====")
        sb.appendLine()
        sb.appendLine("自动收集所有 Plugin 实现：")
        sb.appendLine()

        plugins.forEachIndexed { index, plugin ->
            sb.appendLine("${index + 1}. ${plugin.name()}")
            sb.appendLine("   ${plugin.execute()}")
        }

        sb.appendLine()
        sb.appendLine("===== 代码示例 =====")
        sb.appendLine()
        sb.appendLine("// Module 中")
        sb.appendLine("@Binds @IntoSet")
        sb.appendLine("fun bindPlugin(p: AnalyticsPlugin): Plugin")
        sb.appendLine()
        sb.appendLine("// 注入点 (Kotlin 需要 @JvmSuppressWildcards)")
        sb.appendLine("@Inject lateinit var plugins:")
        sb.appendLine("  @JvmSuppressWildcards Set<Plugin>")

        binding.tvResult.text = sb.toString()
    }

    private fun showPluginMap() {
        val sb = StringBuilder()

        sb.appendLine("===== @StringKey Map 注入 =====")
        sb.appendLine()

        pluginMap.forEach { (key, plugin) ->
            sb.appendLine("Key: \"$key\" → ${plugin.name()}")
            sb.appendLine("   ${plugin.execute()}")
        }

        sb.appendLine()
        sb.appendLine("===== 代码示例 =====")
        sb.appendLine()
        sb.appendLine("// Module 中")
        sb.appendLine("@Binds @IntoMap @StringKey(\"analytics\")")
        sb.appendLine("fun bindPlugin(p: AnalyticsPlugin): Plugin")
        sb.appendLine()
        sb.appendLine("// 注入点")
        sb.appendLine("@Inject lateinit var pluginMap:")
        sb.appendLine("  @JvmSuppressWildcards Map<String, Plugin>")

        binding.tvResult.text = sb.toString()
    }

    private fun showAssistedInject() {
        val sb = StringBuilder()

        sb.appendLine("===== @AssistedInject 工厂注入 =====")
        sb.appendLine()

        // 使用工厂创建 TaskProcessor，传入运行时参数
        val processor1 = taskProcessorFactory.create(
            taskId = "TASK-001",
            taskName = "数据同步"
        )

        val processor2 = taskProcessorFactory.create(
            taskId = "TASK-002",
            taskName = "文件上传"
        )

        sb.appendLine("处理器 1:")
        sb.appendLine(processor1.process())
        sb.appendLine()
        sb.appendLine("处理器 2:")
        sb.appendLine(processor2.process())

        sb.appendLine()
        sb.appendLine("===== 代码示例 =====")
        sb.appendLine()
        sb.appendLine("class TaskProcessor @AssistedInject constructor(")
        sb.appendLine("  private val scheduler: TaskScheduler,  // Dagger 提供")
        sb.appendLine("  @Assisted(\"taskId\") val taskId: String,   // 运行时")
        sb.appendLine("  @Assisted(\"taskName\") val name: String    // 运行时")
        sb.appendLine(") {")
        sb.appendLine("  @AssistedFactory")
        sb.appendLine("  interface Factory {")
        sb.appendLine("    fun create(")
        sb.appendLine("      @Assisted(\"taskId\") id: String,")
        sb.appendLine("      @Assisted(\"taskName\") name: String")
        sb.appendLine("    ): TaskProcessor")
        sb.appendLine("  }")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("// 使用")
        sb.appendLine("@Inject lateinit var factory: TaskProcessor.Factory")
        sb.appendLine("val processor = factory.create(\"TASK-001\", \"任务名\")")

        binding.tvResult.text = sb.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}