package com.peter.classloader.demo.fragments

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.classloader.demo.*
import com.peter.classloader.demo.databinding.FragmentCustomBinding
import dalvik.system.DexClassLoader
import java.io.File
import java.io.FileOutputStream

/**
 * 自定义 ClassLoader Fragment
 * 演示各种自定义 ClassLoader 的实现
 */
class CustomFragment : Fragment() {

    private var _binding: FragmentCustomBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = CustomFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = createFeatureItems()
        val adapter = FeatureAdapter(items) { feature ->
            handleFeatureClick(feature)
        }
        binding.recyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun createFeatureItems(): List<FeatureItem> {
        return listOf(
            FeatureItem(
                feature = ClassLoaderFeature.CUSTOM_SIMPLE,
                title = "简单自定义 ClassLoader",
                description = "继承 ClassLoader 实现简单的类加载",
                category = FeatureCategory.CUSTOM
            ),
            FeatureItem(
                feature = ClassLoaderFeature.CUSTOM_DEX,
                title = "DexClassLoader 动态加载",
                description = "加载 assets 中的 plugin.dex 并执行插件",
                category = FeatureCategory.CUSTOM
            ),
            FeatureItem(
                feature = ClassLoaderFeature.CUSTOM_IN_MEMORY,
                title = "内存加载类",
                description = "从内存中定义和加载类",
                category = FeatureCategory.CUSTOM
            )
        )
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

【使用方法】
val loader = SimpleClassLoader()
loader.registerClass("com.example.MyClass", classBytes)
val clazz = loader.loadClass("com.example.MyClass")

【关键点】
1. 继承 ClassLoader
2. 重写 findClass() 方法
3. 使用 defineClass() 定义类
4. 字节码需要是有效的 class 格式

【注意事项】
• Android 中 defineClass 需要特殊处理
• 建议使用 DexClassLoader 或 BaseDexClassLoader
• 类名必须与字节码中的类名一致
        """.trimIndent()
        
        val currentClass = javaClass
        val loaderInfo = currentClass.classLoader?.javaClass?.name ?: "Bootstrap"
        
        showResultDialog(
            "简单自定义 ClassLoader",
            message + "\n\n【当前类信息】\n类名: ${currentClass.name}\n加载器: $loaderInfo"
        )
    }

    private fun showDexClassLoaderDemo() {
        // 实际执行动态加载
        try {
            val result = loadAndExecutePlugin()
            showResultDialog("DexClassLoader 动态加载", result)
        } catch (e: Exception) {
            showResultDialog("DexClassLoader 动态加载", "加载失败: ${e.message}\n\n请确保 plugin.dex 已正确生成。")
        }
    }

    private fun loadAndExecutePlugin(): String {
        val context = requireContext()
        val sb = StringBuilder()
        
        sb.appendLine("=== DexClassLoader 动态加载演示 ===")
        sb.appendLine()
        
        // 1. 从 assets 复制 dex 到内部存储
        val dexFile = File(context.codeCacheDir, "plugin.dex")
        sb.appendLine("【步骤1】准备 DEX 文件")
        sb.appendLine("目标路径: ${dexFile.absolutePath}")
        
        try {
            context.assets.open("plugin.dex").use { input ->
                FileOutputStream(dexFile).use { output ->
                    input.copyTo(output)
                }
            }
            // Android 8.0+ 要求 DEX 文件必须是只读的
            dexFile.setReadOnly()
            sb.appendLine("文件大小: ${dexFile.length()} bytes")
            sb.appendLine("文件权限: 只读 (符合 Android 8.0+ 要求)")
            sb.appendLine()
        } catch (e: Exception) {
            sb.appendLine("错误: assets 中没有找到 plugin.dex")
            sb.appendLine()
            sb.appendLine("=== 备用演示：模拟加载过程 ===")
            sb.appendLine()
            sb.appendLine(getDexClassLoaderCodeExample())
            return sb.toString()
        }
        
        // 2. 创建 DexClassLoader
        sb.appendLine("【步骤2】创建 DexClassLoader")
        val optDir = File(context.codeCacheDir, "plugin_opt")
        if (!optDir.exists()) optDir.mkdirs()
        
        val dexClassLoader = DexClassLoader(
            dexFile.absolutePath,
            optDir.absolutePath,
            null,
            context.classLoader
        )
        sb.appendLine("ClassLoader: ${dexClassLoader.javaClass.name}")
        sb.appendLine("优化目录: ${optDir.absolutePath}")
        sb.appendLine()
        
        // 3. 加载插件类
        sb.appendLine("【步骤3】加载插件类")
        val helloPluginClass = dexClassLoader.loadClass("com.peter.plugin.HelloPlugin")
        sb.appendLine("HelloPlugin 类加载成功!")
        sb.appendLine("加载器: ${helloPluginClass.classLoader?.javaClass?.name}")
        sb.appendLine()
        
        val calcPluginClass = dexClassLoader.loadClass("com.peter.plugin.CalculatorPlugin")
        sb.appendLine("CalculatorPlugin 类加载成功!")
        sb.appendLine()
        
        // 4. 创建实例并执行
        sb.appendLine("【步骤4】创建实例并执行")
        
        // HelloPlugin
        val helloInstance = helloPluginClass.getDeclaredConstructor().newInstance()
        val getNameMethod = helloPluginClass.getMethod("getName")
        val getVersionMethod = helloPluginClass.getMethod("getVersion")
        val executeMethod = helloPluginClass.getMethod("execute")
        
        val name = getNameMethod.invoke(helloInstance) as String
        val version = getVersionMethod.invoke(helloInstance) as String
        val result = executeMethod.invoke(helloInstance) as String
        
        sb.appendLine("--- HelloPlugin ---")
        sb.appendLine("名称: $name")
        sb.appendLine("版本: $version")
        sb.appendLine("执行结果: $result")
        sb.appendLine()
        
        // CalculatorPlugin
        val calcInstance = calcPluginClass.getDeclaredConstructor().newInstance()
        val calcExecuteMethod = calcPluginClass.getMethod("execute")
        val calcResult = calcExecuteMethod.invoke(calcInstance) as String
        
        sb.appendLine("--- CalculatorPlugin ---")
        sb.appendLine("执行结果:")
        sb.appendLine(calcResult)
        sb.appendLine()
        
        // 5. 类隔离验证
        sb.appendLine("【步骤5】类隔离验证")
        sb.appendLine("插件类加载器: ${helloPluginClass.classLoader?.javaClass?.simpleName}")
        sb.appendLine("当前类加载器: ${javaClass.classLoader?.javaClass?.simpleName}")
        sb.appendLine("是否相同: ${helloPluginClass.classLoader == javaClass.classLoader}")
        sb.appendLine()
        sb.appendLine("✅ 动态加载成功！插件类来自独立的 DexClassLoader")
        
        return sb.toString()
    }

    private fun getDexClassLoaderCodeExample(): String {
        return """
【代码示例】
val dexPath = "/sdcard/plugin.dex"
val optDir = context.codeCacheDir.absolutePath

val dexClassLoader = DexClassLoader(
    dexPath,           // DEX 文件路径
    optDir,            // 优化目录
    null,              // native 库路径
    parentClassLoader  // 父加载器
)

// 加载类
val pluginClass = dexClassLoader.loadClass("com.plugin.PluginImpl")

// 创建实例
val instance = pluginClass.newInstance()

// 调用方法
val method = pluginClass.getMethod("execute")
method.invoke(instance)

【参数说明】
• dexPath: DEX/APK/JAR 文件路径
• optimizedDirectory: DEX 优化目录
• librarySearchPath: Native 库搜索路径
• parent: 父 ClassLoader

【Android 8.1+ 变化】
optimizedDirectory 已废弃，系统会自动管理

【安全注意事项】
• 需要文件读取权限
• 验证 DEX 来源
• 防止代码注入攻击

【插件化示例】
1. 定义插件接口
2. 插件实现接口
3. 主程序通过接口调用

【生成 plugin.dex】
修改 plugin-module 后重新编译：
./gradlew :classloader-demo:assembleDebug
        """.trimIndent()
    }

    private fun showInMemoryDemo() {
        val message = """
=== 内存加载类 ===

【Android 8.0+ InMemoryDexClassLoader】

val dexBytes: ByteArray = loadDexFromNetwork()
val dexClassLoader = InMemoryDexClassLoader(
    ByteBuffer.wrap(dexBytes),
    parentClassLoader
)

【自定义实现】

class InMemoryClassLoader(
    private val dexBuffer: ByteBuffer,
    parent: ClassLoader
) : BaseDexClassLoader(parent) {
    init {
        // 使用反射设置 dexPathList
        val pathListField = BaseDexClassLoader::class.java
            .getDeclaredField("pathList")
        pathListField.isAccessible = true
        
        val dexPathList = pathListField.get(this)
        val dexElements = createDexElements(dexBuffer)
        
        // 设置 dexElements...
    }
}

【应用场景】
• 动态下载代码执行
• 加密 DEX 解密后加载
• 热更新下发补丁

【安全考虑】
• 验证 DEX 完整性
• 检查签名
• 限制可加载的类

【示例：动态代码执行】
// 从服务器下载 DEX
val dexBytes = downloadDex()
// 解密（如果加密）
val decryptedDex = decrypt(dexBytes)
// 加载执行
val loader = InMemoryDexClassLoader(
    ByteBuffer.wrap(decryptedDex),
    classLoader
)
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