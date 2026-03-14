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
import com.peter.classloader.demo.databinding.FragmentAdvancedBinding
import dalvik.system.DexClassLoader
import java.io.File
import java.io.FileOutputStream

/**
 * 高级应用 Fragment
 * 演示 ClassLoader 的高级应用场景
 */
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
                feature = ClassLoaderFeature.ADVANCED_ISOLATION,
                title = "类隔离演示",
                description = "不同 ClassLoader 加载同名类的隔离效果",
                category = FeatureCategory.ADVANCED
            ),
            FeatureItem(
                feature = ClassLoaderFeature.ADVANCED_HOT_FIX,
                title = "热修复原理",
                description = "理解热修复技术的 ClassLoader 实现",
                category = FeatureCategory.ADVANCED
            ),
            FeatureItem(
                feature = ClassLoaderFeature.ADVANCED_PLUGIN,
                title = "插件化原理",
                description = "插件化框架的 ClassLoader 机制",
                category = FeatureCategory.ADVANCED
            ),
            FeatureItem(
                feature = ClassLoaderFeature.ADVANCED_COMPARE,
                title = "类比较与判断",
                description = "不同加载器加载的类的比较",
                category = FeatureCategory.ADVANCED
            )
        )
    }

    private fun handleFeatureClick(feature: ClassLoaderFeature) {
        when (feature) {
            ClassLoaderFeature.ADVANCED_ISOLATION -> showIsolationDemo()
            ClassLoaderFeature.ADVANCED_HOT_FIX -> showHotFixDemo()
            ClassLoaderFeature.ADVANCED_PLUGIN -> showPluginDemo()
            ClassLoaderFeature.ADVANCED_COMPARE -> showCompareDemo()
            else -> {}
        }
    }

    private fun showIsolationDemo() {
        val sb = StringBuilder()
        sb.appendLine("=== 类隔离演示 ===")
        sb.appendLine()
        
        // 尝试实际演示类隔离
        try {
            val context = requireContext()
            
            // 从 assets 复制 dex
            val dexFile = File(context.codeCacheDir, "plugin_isolation.dex")
            context.assets.open("plugin.dex").use { input ->
                FileOutputStream(dexFile).use { output ->
                    input.copyTo(output)
                }
            }
            dexFile.setReadOnly() // Android 8.0+ 要求 DEX 文件只读
            
            val optDir = File(context.codeCacheDir, "plugin_isolation_opt")
            if (!optDir.exists()) optDir.mkdirs()
            
            // 创建两个独立的 DexClassLoader
            val loader1 = DexClassLoader(
                dexFile.absolutePath,
                optDir.absolutePath + "/1",
                null,
                context.classLoader
            )
            
            val loader2 = DexClassLoader(
                dexFile.absolutePath,
                optDir.absolutePath + "/2",
                null,
                context.classLoader
            )
            
            sb.appendLine("【实验设置】")
            sb.appendLine("创建两个独立的 DexClassLoader")
            sb.appendLine("加载同一个 DEX 文件中的同一个类")
            sb.appendLine()
            
            // 使用两个不同的 ClassLoader 加载同一个类
            val pluginClass1 = loader1.loadClass("com.peter.plugin.HelloPlugin")
            val pluginClass2 = loader2.loadClass("com.peter.plugin.HelloPlugin")
            
            sb.appendLine("【加载结果】")
            sb.appendLine("HelloPlugin (Loader1):")
            sb.appendLine("  ClassLoader: ${pluginClass1.classLoader?.javaClass?.simpleName}")
            sb.appendLine("  hashCode: ${pluginClass1.hashCode()}")
            sb.appendLine()
            sb.appendLine("HelloPlugin (Loader2):")
            sb.appendLine("  ClassLoader: ${pluginClass2.classLoader?.javaClass?.simpleName}")
            sb.appendLine("  hashCode: ${pluginClass2.hashCode()}")
            sb.appendLine()
            
            // 关键：比较两个类
            val sameClass = pluginClass1 == pluginClass2
            val sameLoader = pluginClass1.classLoader == pluginClass2.classLoader
            
            sb.appendLine("【关键发现】")
            sb.appendLine("Class 对象相等: $sameClass")
            sb.appendLine("ClassLoader 相同: $sameLoader")
            sb.appendLine()
            
            if (!sameClass) {
                sb.appendLine("✅ 类隔离验证成功!")
                sb.appendLine("虽然是同一个 DEX 中的同一个类，")
                sb.appendLine("但被不同的 ClassLoader 加载后，")
                sb.appendLine("在 JVM 中是两个完全不同的 Class 对象!")
            }
            sb.appendLine()
            
            // 创建实例测试
            sb.appendLine("【实例测试】")
            val instance1 = pluginClass1.getDeclaredConstructor().newInstance()
            val instance2 = pluginClass2.getDeclaredConstructor().newInstance()
            
            val getNameMethod = pluginClass1.getMethod("getName")
            val name1 = getNameMethod.invoke(instance1) as String
            val name2 = getNameMethod.invoke(instance2) as String
            
            sb.appendLine("Instance1.getName(): $name1")
            sb.appendLine("Instance2.getName(): $name2")
            sb.appendLine()
            
            sb.appendLine("【应用场景】")
            sb.appendLine("• 插件隔离 - 不同插件使用独立 ClassLoader")
            sb.appendLine("• 版本共存 - 同一库的不同版本共存")
            sb.appendLine("• 热修复 - 加载修复后的类替代原类")
            
        } catch (e: Exception) {
            sb.appendLine("演示失败: ${e.message}")
            sb.appendLine()
            sb.appendLine(getIsolationCodeExample())
        }
        
        showResultDialog("类隔离演示", sb.toString())
    }
    
    private fun getIsolationCodeExample(): String {
        return """
【代码示例】
// 使用默认加载器加载
val class1 = Class.forName("com.example.Plugin")

// 使用自定义加载器加载
val customLoader = DexClassLoader(...)
val class2 = customLoader.loadClass("com.example.Plugin")

// 比较
println(class1 == class2)  // false!

【IsolatedClassLoader 实现】
class IsolatedClassLoader(
    private val delegate: ClassLoader
) : ClassLoader(null) {  // parent 为 null
    
    override fun loadClass(name: String): Class<*> {
        return delegate.loadClass(name)
    }
}
        """.trimIndent()
    }

    private fun showHotFixDemo() {
        val sb = StringBuilder()
        sb.appendLine("=== 热修复原理 ===")
        sb.appendLine()
        
        // 实际查看 PathClassLoader 的内部结构
        try {
            val classLoader = javaClass.classLoader
            sb.appendLine("【当前 ClassLoader 结构】")
            sb.appendLine("类型: ${classLoader?.javaClass?.name}")
            sb.appendLine()
            
            // 尝试获取 dexElements (仅作演示)
            if (classLoader != null) {
                sb.appendLine("PathClassLoader 内部结构:")
                sb.appendLine("├── DexPathList pathList")
                sb.appendLine("│   ├── Element[] dexElements")
                sb.appendLine("│   │   ├── [0] base.apk (主 DEX)")
                sb.appendLine("│   │   └── ...")
                sb.appendLine("│   └── NativeLibraryElement[] nativeLibraryElements")
                sb.appendLine()
            }
            
            // 模拟热修复流程
            sb.appendLine("【热修复核心流程】")
            sb.appendLine()
            sb.appendLine("1️⃣ 创建补丁 DEX")
            sb.appendLine("   javac FixClass.java")
            sb.appendLine("   d8 --output fix.dex FixClass.class")
            sb.appendLine()
            
            sb.appendLine("2️⃣ 加载补丁 DEX")
            sb.appendLine("   val patchLoader = DexClassLoader(patchPath, ...)")
            sb.appendLine()
            
            sb.appendLine("3️⃣ 获取 dexElements")
            sb.appendLine("""
   val pathListField = BaseDexClassLoader::class.java
       .getDeclaredField("pathList")
   pathListField.isAccessible = true
   val pathList = pathListField.get(classLoader)
   
   val dexElementsField = pathList.javaClass
       .getDeclaredField("dexElements")
   dexElementsField.isAccessible = true
   val oldElements = dexElementsField.get(pathList) as Array<*>
            """.trimIndent())
            sb.appendLine()
            
            sb.appendLine("4️⃣ 插入补丁到数组前面")
            sb.appendLine("   val newElements = mergeArrays(patchElements, oldElements)")
            sb.appendLine("   dexElementsField.set(pathList, newElements)")
            sb.appendLine()
            
            sb.appendLine("5️⃣ 重新加载类")
            sb.appendLine("   补丁类会被优先加载，原类被\"覆盖\"")
            sb.appendLine()
            
            // 实际演示：模拟
            sb.appendLine("【模拟效果】")
            sb.appendLine("原始数组: [base.dex]")
            sb.appendLine("修复后:   [patch.dex, base.dex]")
            sb.appendLine("加载类时: 从 patch.dex 先找到修复后的类")
            sb.appendLine()
            
            sb.appendLine("【主流热修复框架】")
            sb.appendLine("• Tinker (腾讯) - 全量 DEX 替换")
            sb.appendLine("• Sophix (阿里) - 方法级别修复")
            sb.appendLine("• Robust (美团) - Instant Run 方案")
            sb.appendLine()
            
            sb.appendLine("【局限性】")
            sb.appendLine("• 需要重启应用生效")
            sb.appendLine("• 资源修复需要额外处理")
            sb.appendLine("• 新增方法有限制")
            
        } catch (e: Exception) {
            sb.appendLine("演示失败: ${e.message}")
        }
        
        showResultDialog("热修复原理", sb.toString())
    }

    private fun showPluginDemo() {
        val sb = StringBuilder()
        sb.appendLine("=== 插件化原理 ===")
        sb.appendLine()
        
        // 实际演示插件加载
        try {
            val context = requireContext()
            val dexFile = File(context.codeCacheDir, "plugin_demo.dex")
            context.assets.open("plugin.dex").use { input ->
                FileOutputStream(dexFile).use { output ->
                    input.copyTo(output)
                }
            }
            dexFile.setReadOnly() // Android 8.0+ 要求 DEX 文件只读
            
            val optDir = File(context.codeCacheDir, "plugin_demo_opt")
            if (!optDir.exists()) optDir.mkdirs()
            
            // 创建模拟的插件 ClassLoader
            val pluginClassLoader = DexClassLoader(
                dexFile.absolutePath,
                optDir.absolutePath,
                null,
                context.classLoader
            )
            
            sb.appendLine("【插件加载演示】")
            sb.appendLine()
            sb.appendLine("1️⃣ 插件 APK/DEX 路径:")
            sb.appendLine("   ${dexFile.absolutePath}")
            sb.appendLine()
            
            sb.appendLine("2️⃣ 创建 PluginClassLoader:")
            sb.appendLine("   宿主 ClassLoader: ${context.classLoader?.javaClass?.simpleName}")
            sb.appendLine("   插件 ClassLoader: ${pluginClassLoader.javaClass.simpleName}")
            sb.appendLine("   父加载器: ${pluginClassLoader.parent?.javaClass?.simpleName}")
            sb.appendLine()
            
            sb.appendLine("3️⃣ 加载插件类:")
            val helloPlugin = pluginClassLoader.loadClass("com.peter.plugin.HelloPlugin")
            val calcPlugin = pluginClassLoader.loadClass("com.peter.plugin.CalculatorPlugin")
            sb.appendLine("   ✅ HelloPlugin 加载成功")
            sb.appendLine("   ✅ CalculatorPlugin 加载成功")
            sb.appendLine()
            
            sb.appendLine("4️⃣ 执行插件方法:")
            val helloInstance = helloPlugin.getDeclaredConstructor().newInstance()
            val executeMethod = helloPlugin.getMethod("execute")
            val result = executeMethod.invoke(helloInstance) as String
            sb.appendLine("   HelloPlugin.execute(): $result")
            sb.appendLine()
            
            val calcInstance = calcPlugin.getDeclaredConstructor().newInstance()
            val calcExecuteMethod = calcPlugin.getMethod("execute")
            val calcResult = calcExecuteMethod.invoke(calcInstance) as String
            sb.appendLine("   CalculatorPlugin.execute():")
            calcResult.lines().forEach { sb.appendLine("   $it") }
            sb.appendLine()
            
            sb.appendLine("【核心组件说明】")
            sb.appendLine()
            sb.appendLine("插件 ClassLoader:")
            sb.appendLine("• 加载插件中的类")
            sb.appendLine("• 可访问宿主类 (parent)")
            sb.appendLine("• 与其他插件隔离")
            sb.appendLine()
            
            sb.appendLine("资源加载:")
            sb.appendLine("""
val assetManager = AssetManager::class.java.newInstance()
val addAssetPath = AssetManager::class.java
    .getMethod("addAssetPath", String::class.java)
addAssetPath.invoke(assetManager, pluginPath)
val resources = Resources(assetManager, metrics, config)
            """.trimIndent())
            sb.appendLine()
            
            sb.appendLine("【四大组件代理】")
            sb.appendLine("• Activity: 代理 Activity + 欺骗 AMS")
            sb.appendLine("• Service: 动态注册或代理")
            sb.appendLine("• Receiver: 静态转动态注册")
            sb.appendLine("• Provider: ContentProviderClient")
            sb.appendLine()
            
            sb.appendLine("【主流框架】")
            sb.appendLine("• RePlugin (360)")
            sb.appendLine("• Shadow (腾讯)")
            sb.appendLine("• VirtualApp")
            
        } catch (e: Exception) {
            sb.appendLine("演示失败: ${e.message}")
        }
        
        showResultDialog("插件化原理", sb.toString())
    }

    private fun showCompareDemo() {
        val sb = StringBuilder()
        sb.appendLine("=== 类比较与判断 ===")
        sb.appendLine()
        
        // 获取一些类进行比较
        val stringClass = String::class.java
        val activityClass = android.app.Activity::class.java
        val myClass = javaClass
        
        sb.appendLine("【系统类加载器分析】")
        sb.appendLine()
        
        sb.appendLine("String 类:")
        sb.appendLine("  加载器: ${stringClass.classLoader?.javaClass?.simpleName ?: "Bootstrap"}")
        sb.appendLine("  来源: Java 核心类库")
        sb.appendLine()
        
        sb.appendLine("Activity 类:")
        sb.appendLine("  加载器: ${activityClass.classLoader?.javaClass?.simpleName ?: "Bootstrap"}")
        sb.appendLine("  来源: Android Framework")
        sb.appendLine()
        
        sb.appendLine("当前类 (${myClass.simpleName}):")
        sb.appendLine("  加载器: ${myClass.classLoader?.javaClass?.simpleName ?: "Bootstrap"}")
        sb.appendLine("  来源: 应用 DEX")
        sb.appendLine()
        
        // 实际加载插件类进行比较
        try {
            val context = requireContext()
            val dexFile = File(context.codeCacheDir, "plugin_compare.dex")
            context.assets.open("plugin.dex").use { input ->
                FileOutputStream(dexFile).use { output ->
                    input.copyTo(output)
                }
            }
            dexFile.setReadOnly() // Android 8.0+ 要求 DEX 文件只读
            
            val optDir = File(context.codeCacheDir, "plugin_compare_opt")
            if (!optDir.exists()) optDir.mkdirs()
            
            val pluginLoader = DexClassLoader(
                dexFile.absolutePath,
                optDir.absolutePath,
                null,
                context.classLoader
            )
            
            val pluginClass = pluginLoader.loadClass("com.peter.plugin.HelloPlugin")
            
            sb.appendLine("【插件类 vs 应用类比较】")
            sb.appendLine()
            
            sb.appendLine("HelloPlugin (DexClassLoader):")
            sb.appendLine("  加载器: ${pluginClass.classLoader?.javaClass?.simpleName}")
            sb.appendLine("  hashCode: ${pluginClass.hashCode()}")
            sb.appendLine()
            
            sb.appendLine("AdvancedFragment (PathClassLoader):")
            sb.appendLine("  加载器: ${myClass.classLoader?.javaClass?.simpleName}")
            sb.appendLine("  hashCode: ${myClass.hashCode()}")
            sb.appendLine()
            
            // 关键比较
            sb.appendLine("【关键比较结果】")
            sb.appendLine("插件类加载器 == 应用类加载器: ${pluginClass.classLoader == myClass.classLoader}")
            sb.appendLine("插件类加载器.parent == 应用类加载器: ${pluginClass.classLoader?.parent == myClass.classLoader}")
            sb.appendLine()
            
            // 实例化并调用方法
            sb.appendLine("【实际执行插件方法】")
            val instance = pluginClass.getDeclaredConstructor().newInstance()
            val getNameMethod = pluginClass.getMethod("getName")
            val getVersionMethod = pluginClass.getMethod("getVersion")
            val executeMethod = pluginClass.getMethod("execute")
            
            sb.appendLine("getName(): ${getNameMethod.invoke(instance)}")
            sb.appendLine("getVersion(): ${getVersionMethod.invoke(instance)}")
            sb.appendLine("execute(): ${executeMethod.invoke(instance)}")
            sb.appendLine()
            
            sb.appendLine("✅ 成功加载并执行插件类!")
            
        } catch (e: Exception) {
            sb.appendLine("插件类加载失败: ${e.message}")
        }
        
        sb.appendLine()
        sb.appendLine("【常见判断方法】")
        sb.appendLine("""
// 判断是否是系统类
fun isSystemClass(clazz: Class<*>): Boolean {
    val loader = clazz.classLoader
    return loader == null || loader is BootClassLoader
}

// 判断是否是应用类
fun isApplicationClass(clazz: Class<*>): Boolean {
    return clazz.classLoader is PathClassLoader
}

// 判断类是否可以被转换
fun canCast(from: Class<*>, to: Class<*>): Boolean {
    return to.isAssignableFrom(from)
}
        """.trimIndent())
        
        showResultDialog("类比较与判断", sb.toString())
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
