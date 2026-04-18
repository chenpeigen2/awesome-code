package com.peter.classloader.demo.fragments

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.classloader.demo.*
import com.peter.classloader.demo.databinding.FragmentCustomBinding
import com.peter.plugin.CalculatorPlugin
import com.peter.plugin.HelloPlugin
import com.peter.plugin.IPlugin
import com.peter.plugin.PluginContext
import com.peter.plugin.PluginState
import com.peter.plugin.TextProcessorPlugin
import dalvik.system.DexClassLoader
import java.io.File
import java.io.FileOutputStream

class CustomFragment : Fragment() {

    private var _binding: FragmentCustomBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = CustomFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCustomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val items = listOf(
            FeatureItem(ClassLoaderFeature.CUSTOM_SIMPLE, "简单自定义 ClassLoader", "继承 ClassLoader 实现简单的类加载", FeatureCategory.CUSTOM),
            FeatureItem(ClassLoaderFeature.CUSTOM_DEX, "DexClassLoader 动态加载", "加载 plugin.dex，演示完整生命周期和参数化执行", FeatureCategory.CUSTOM),
            FeatureItem(ClassLoaderFeature.CUSTOM_IN_MEMORY, "内存加载类", "从内存中定义和加载类", FeatureCategory.CUSTOM)
        )
        binding.recyclerView.apply {
            adapter = FeatureAdapter(items) { handleFeatureClick(it) }
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun handleFeatureClick(feature: ClassLoaderFeature) {
        when (feature) {
            ClassLoaderFeature.CUSTOM_SIMPLE -> showSimpleClassLoaderDemo()
            ClassLoaderFeature.CUSTOM_DEX -> showDexClassLoaderDemo()
            ClassLoaderFeature.CUSTOM_IN_MEMORY -> showInMemoryDemo()
            else -> {}
        }
    }

    private fun showSimpleClassLoaderDemo() {
        val message = """
=== SimpleClassLoader 实现 ===

【代码示例】
class SimpleClassLoader : ClassLoader() {
    private val classBytes = mutableMapOf<String, ByteArray>()

    fun registerClass(name: String, bytes: ByteArray) {
        classBytes[name] = bytes
    }

    override fun findClass(name: String): Class<*> {
        val bytes = classBytes[name]
            ?: throw ClassNotFoundException()
        return defineClass(name, bytes, 0, bytes.size)
    }
}

【关键点】
1. 继承 ClassLoader
2. 重写 findClass() 方法
3. 使用 defineClass() 定义类
4. Android 中建议用 DexClassLoader 替代
        """.trimIndent()

        val loaderInfo = javaClass.classLoader?.javaClass?.name ?: "Bootstrap"
        showResultDialog("简单自定义 ClassLoader", message + "\n\n【当前类信息】\n类名: ${javaClass.name}\n加载器: $loaderInfo")
    }

    private fun showDexClassLoaderDemo() {
        try {
            val result = loadAndExecutePlugins()
            showResultDialog("DexClassLoader 动态加载", result)
        } catch (e: Exception) {
            showResultDialog("DexClassLoader 动态加载", "加载失败: ${e.message}\n\n请确保 plugin.dex 已正确生成。")
        }
    }

    private fun loadAndExecutePlugins(): String {
        val context = requireContext()
        val sb = StringBuilder()
        sb.appendLine("=== DexClassLoader 动态加载演示 ===")
        sb.appendLine()

        // 1. 从 assets 复制 DEX
        val dexFile = File(context.codeCacheDir, "plugin.dex")
        sb.appendLine("【步骤1】准备 DEX 文件")
        try {
            context.assets.open("plugin.dex").use { input ->
                FileOutputStream(dexFile).use { output -> input.copyTo(output) }
            }
            dexFile.setReadOnly()
            sb.appendLine("路径: ${dexFile.absolutePath}")
            sb.appendLine("大小: ${dexFile.length()} bytes")
        } catch (e: Exception) {
            sb.appendLine("错误: ${e.message}")
            return sb.toString()
        }
        sb.appendLine()

        // 2. 创建 DexClassLoader
        sb.appendLine("【步骤2】创建 DexClassLoader")
        val optDir = File(context.codeCacheDir, "plugin_opt")
        if (!optDir.exists()) optDir.mkdirs()
        val dexClassLoader = DexClassLoader(
            dexFile.absolutePath, optDir.absolutePath, null, context.classLoader
        )
        sb.appendLine("ClassLoader: ${dexClassLoader.javaClass.name}")
        sb.appendLine()

        // 3. 加载并执行所有插件
        sb.appendLine("【步骤3】加载插件并演示生命周期")
        val pluginClasses = listOf(
            "com.peter.plugin.HelloPlugin",
            "com.peter.plugin.CalculatorPlugin",
            "com.peter.plugin.TextProcessorPlugin"
        )

        for (className in pluginClasses) {
            try {
                val pluginClass = dexClassLoader.loadClass(className)
                val plugin = pluginClass.getDeclaredConstructor().newInstance()

                // 反射调用 getMeta
                val meta = pluginClass.getMethod("getMeta").invoke(plugin)
                val metaClass = meta!!.javaClass
                val name = metaClass.getMethod("getName").invoke(meta) as String
                val version = metaClass.getMethod("getVersion").invoke(meta) as String
                val desc = metaClass.getMethod("getDescription").invoke(meta) as String

                sb.appendLine("--- $name v$version ---")
                sb.appendLine("描述: $desc")

                // 演示生命周期: onCreate → onStart → execute → onStop → onDestroy
                val pluginContextClass = dexClassLoader.loadClass("com.peter.plugin.PluginContext")
                val pluginContext = pluginContextClass.getConstructor(
                    ClassLoader::class.java, Map::class.java
                ).newInstance(dexClassLoader, emptyMap<String, String>())
                pluginClass.getMethod("onCreate", pluginContextClass).invoke(plugin, pluginContext)
                sb.appendLine("状态: ${pluginClass.getMethod("getState").invoke(plugin)}")

                pluginClass.getMethod("onStart").invoke(plugin)
                sb.appendLine("状态: ${pluginClass.getMethod("getState").invoke(plugin)}")

                // 参数化执行
                @Suppress("UNCHECKED_CAST")
                val params: Map<String, String> = when {
                    className.contains("Hello") -> mapOf("name" to "Android", "lang" to "zh")
                    className.contains("Calculator") -> mapOf("a" to "42", "b" to "13", "op" to "*")
                    className.contains("Text") -> mapOf("text" to "Hello Plugin Framework", "mode" to "uppercase")
                    else -> emptyMap()
                }
                val executeResult = pluginClass.getMethod("execute", Map::class.java).invoke(plugin, params) as String
                sb.appendLine("执行结果: $executeResult")

                pluginClass.getMethod("onStop").invoke(plugin)
                pluginClass.getMethod("onDestroy").invoke(plugin)
                sb.appendLine("最终状态: ${pluginClass.getMethod("getState").invoke(plugin)}")
                sb.appendLine()
            } catch (e: Exception) {
                sb.appendLine("加载 $className 失败: ${e.message}")
                sb.appendLine()
            }
        }

        // 4. 类隔离验证
        sb.appendLine("【步骤4】类隔离验证")
        val helloClass = dexClassLoader.loadClass("com.peter.plugin.HelloPlugin")
        sb.appendLine("插件 ClassLoader: ${helloClass.classLoader?.javaClass?.simpleName}")
        sb.appendLine("宿主 ClassLoader: ${javaClass.classLoader?.javaClass?.simpleName}")
        sb.appendLine("是否相同: ${helloClass.classLoader == javaClass.classLoader}")
        sb.appendLine()
        sb.appendLine("✅ 动态加载成功！插件经历了完整的生命周期: CREATED → STARTED → STOPPED → DESTROYED")

        return sb.toString()
    }

    private fun showInMemoryDemo() {
        val message = """
=== 内存加载类 ===

【Android 8.0+ InMemoryDexClassLoader】
val dexBytes: ByteArray = loadDexFromNetwork()
val loader = InMemoryDexClassLoader(
    ByteBuffer.wrap(dexBytes),
    parentClassLoader
)

【应用场景】
• 动态下载代码执行
• 加密 DEX 解密后加载
• 热更新下发补丁

【安全考虑】
• 验证 DEX 完整性
• 检查签名
• 限制可加载的类
        """.trimIndent()
        showResultDialog("内存加载类", message)
    }

    private fun showResultDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("确定", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
