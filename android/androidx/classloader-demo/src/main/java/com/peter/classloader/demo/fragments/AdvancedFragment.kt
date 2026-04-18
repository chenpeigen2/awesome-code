package com.peter.classloader.demo.fragments

import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

class AdvancedFragment : Fragment() {

    private var _binding: FragmentAdvancedBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = AdvancedFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdvancedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val items = listOf(
            FeatureItem(ClassLoaderFeature.ADVANCED_ISOLATION, "类隔离演示", "不同 ClassLoader 加载同名类的隔离效果", FeatureCategory.ADVANCED),
            FeatureItem(ClassLoaderFeature.ADVANCED_HOT_FIX, "热修复原理", "理解热修复技术的 ClassLoader 实现", FeatureCategory.ADVANCED),
            FeatureItem(ClassLoaderFeature.ADVANCED_PLUGIN, "插件化原理", "插件化框架的 ClassLoader 机制", FeatureCategory.ADVANCED),
            FeatureItem(ClassLoaderFeature.ADVANCED_COMPARE, "类比较与判断", "不同加载器加载的类的比较", FeatureCategory.ADVANCED),
            FeatureItem(ClassLoaderFeature.ADVANCED_LIFECYCLE, "插件生命周期", "完整的插件生命周期管理：create/start/stop/destroy", FeatureCategory.ADVANCED),
            FeatureItem(ClassLoaderFeature.ADVANCED_INTER_PLUGIN, "插件间通信", "通过 MessageBus 实现跨 ClassLoader 插件通信", FeatureCategory.ADVANCED)
        )
        binding.recyclerView.apply {
            adapter = FeatureAdapter(items) { handleFeatureClick(it) }
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun handleFeatureClick(feature: ClassLoaderFeature) {
        when (feature) {
            ClassLoaderFeature.ADVANCED_ISOLATION -> showIsolationDemo()
            ClassLoaderFeature.ADVANCED_HOT_FIX -> showHotFixDemo()
            ClassLoaderFeature.ADVANCED_PLUGIN -> showPluginDemo()
            ClassLoaderFeature.ADVANCED_COMPARE -> showCompareDemo()
            ClassLoaderFeature.ADVANCED_LIFECYCLE -> showLifecycleDemo()
            ClassLoaderFeature.ADVANCED_INTER_PLUGIN -> showInterPluginDemo()
            else -> {}
        }
    }

    private fun prepareDex(): File? {
        val context = requireContext()
        val dexFile = File(context.codeCacheDir, "plugin_shared.dex")
        return try {
            context.assets.open("plugin.dex").use { input ->
                FileOutputStream(dexFile).use { output -> input.copyTo(output) }
            }
            dexFile.setReadOnly()
            dexFile
        } catch (e: Exception) {
            null
        }
    }

    private fun createDexClassLoader(dexFile: File, suffix: String): DexClassLoader {
        val optDir = File(requireContext().codeCacheDir, "opt_$suffix")
        if (!optDir.exists()) optDir.mkdirs()
        return DexClassLoader(dexFile.absolutePath, optDir.absolutePath, null, requireContext().classLoader)
    }

    private fun showIsolationDemo() {
        val sb = StringBuilder()
        sb.appendLine("=== 类隔离演示 ===")
        sb.appendLine()
        try {
            val dexFile = prepareDex()
            if (dexFile == null) {
                sb.appendLine("无法准备 DEX 文件")
                showResultDialog("类隔离演示", sb.toString())
                return
            }

            val loader1 = createDexClassLoader(dexFile, "iso_1")
            val loader2 = createDexClassLoader(dexFile, "iso_2")

            sb.appendLine("【实验设置】")
            sb.appendLine("两个独立 DexClassLoader 加载同一个类")
            sb.appendLine()

            val class1 = loader1.loadClass("com.peter.plugin.HelloPlugin")
            val class2 = loader2.loadClass("com.peter.plugin.HelloPlugin")

            sb.appendLine("Loader1 hashCode: ${class1.hashCode()}")
            sb.appendLine("Loader2 hashCode: ${class2.hashCode()}")
            sb.appendLine("Class 对象相等: ${class1 == class2}")
            sb.appendLine()

            // 实例化并执行
            val inst1 = class1.getDeclaredConstructor().newInstance()
            val inst2 = class2.getDeclaredConstructor().newInstance()

            // onCreate 两个实例
            val pluginContextClass1 = loader1.loadClass("com.peter.plugin.PluginContext")
            val ctx1 = pluginContextClass1.getConstructor(ClassLoader::class.java, Map::class.java)
                .newInstance(loader1, mapOf<String, String>("name" to "Instance1"))
            class1.getMethod("onCreate", pluginContextClass1).invoke(inst1, ctx1)
            class1.getMethod("onStart").invoke(inst1)

            val pluginContextClass2 = loader2.loadClass("com.peter.plugin.PluginContext")
            val ctx2 = pluginContextClass2.getConstructor(ClassLoader::class.java, Map::class.java)
                .newInstance(loader2, mapOf<String, String>("name" to "Instance2"))
            class2.getMethod("onCreate", pluginContextClass2).invoke(inst2, ctx2)
            class2.getMethod("onStart").invoke(inst2)

            val res1 = class1.getMethod("execute", Map::class.java).invoke(inst1, mapOf<String, String>()) as String
            val res2 = class2.getMethod("execute", Map::class.java).invoke(inst2, mapOf<String, String>()) as String

            sb.appendLine("【两个实例独立执行】")
            sb.appendLine("Instance1: $res1")
            sb.appendLine()
            sb.appendLine("Instance2: $res2")
            sb.appendLine()
            sb.appendLine("✅ 同一个类，不同 ClassLoader → 完全独立的实例，互不干扰")

        } catch (e: Exception) {
            sb.appendLine("演示失败: ${e.message}")
        }
        showResultDialog("类隔离演示", sb.toString())
    }

    private fun showHotFixDemo() {
        val sb = StringBuilder()
        sb.appendLine("=== 热修复原理 ===")
        sb.appendLine()

        val classLoader = javaClass.classLoader
        sb.appendLine("【PathClassLoader 内部结构】")
        sb.appendLine("├── DexPathList pathList")
        sb.appendLine("│   ├── Element[] dexElements")
        sb.appendLine("│   │   ├── [0] base.apk (主 DEX)")
        sb.appendLine("│   │   └── ...")
        sb.appendLine()

        sb.appendLine("【热修复核心流程】")
        sb.appendLine("1. 创建补丁 DEX (修复后的类)")
        sb.appendLine("2. DexClassLoader 加载补丁 DEX")
        sb.appendLine("3. 反射获取 dexElements 数组")
        sb.appendLine("4. 将补丁 Element 插入数组头部")
        sb.appendLine("5. 补丁类优先加载，原类被\"覆盖\"")
        sb.appendLine()

        sb.appendLine("【代码示例】")
        sb.appendLine("""
val pathListField = BaseDexClassLoader::class.java
    .getDeclaredField("pathList")
pathListField.isAccessible = true
val pathList = pathListField.get(classLoader)

val dexElementsField = pathList.javaClass
    .getDeclaredField("dexElements")
dexElementsField.isAccessible = true
val oldElements = dexElementsField.get(pathList) as Array<*>

// 将 patch elements 合并到 oldElements 前面
val merged = mergeArrays(patchElements, oldElements)
dexElementsField.set(pathList, merged)
        """.trimIndent())
        sb.appendLine()

        sb.appendLine("【主流框架】")
        sb.appendLine("• Tinker (腾讯) — 全量 DEX 替换")
        sb.appendLine("• Sophix (阿里) — 方法级别修复")
        sb.appendLine("• Robust (美团) — Instant Run 方案")

        showResultDialog("热修复原理", sb.toString())
    }

    private fun showPluginDemo() {
        val sb = StringBuilder()
        sb.appendLine("=== 插件化原理 ===")
        sb.appendLine()
        try {
            val dexFile = prepareDex()
            if (dexFile == null) {
                sb.appendLine("无法准备 DEX 文件")
                showResultDialog("插件化原理", sb.toString())
                return
            }

            val pluginLoader = createDexClassLoader(dexFile, "plugin_demo")

            sb.appendLine("【插件加载演示】")
            sb.appendLine("宿主 ClassLoader: ${requireContext().classLoader?.javaClass?.simpleName}")
            sb.appendLine("插件 ClassLoader: ${pluginLoader.javaClass.simpleName}")
            sb.appendLine("父加载器: ${pluginLoader.parent?.javaClass?.simpleName}")
            sb.appendLine()

            // 列出所有插件
            val pluginClasses = listOf(
                "com.peter.plugin.HelloPlugin",
                "com.peter.plugin.CalculatorPlugin",
                "com.peter.plugin.TextProcessorPlugin",
                "com.peter.plugin.MessengerPlugin"
            )

            sb.appendLine("【已发现插件】")
            for (className in pluginClasses) {
                try {
                    val cls = pluginLoader.loadClass(className)
                    val inst = cls.getDeclaredConstructor().newInstance()
                    val meta = cls.getMethod("getMeta").invoke(inst)!!
                    val name = meta.javaClass.getMethod("getName").invoke(meta) as String
                    val desc = meta.javaClass.getMethod("getDescription").invoke(meta) as String
                    val caps = meta.javaClass.getMethod("getCapabilities").invoke(meta) as List<*>
                    sb.appendLine("✅ $name")
                    sb.appendLine("   $desc")
                    sb.appendLine("   能力: ${caps.joinToString()}")
                } catch (e: Exception) {
                    sb.appendLine("❌ $className: ${e.message}")
                }
            }

            sb.appendLine()
            sb.appendLine("【资源加载】")
            sb.appendLine("插件资源通过 AssetManager 反射加载：")
            sb.appendLine("""
val assetManager = AssetManager::class.java.newInstance()
val addAssetPath = AssetManager::class.java
    .getMethod("addAssetPath", String::class.java)
addAssetPath.invoke(assetManager, pluginApkPath)
val resources = Resources(assetManager, metrics, config)
            """.trimIndent())

            sb.appendLine()
            sb.appendLine("【四大组件代理】")
            sb.appendLine("• Activity: 代理 Activity + 欺骗 AMS")
            sb.appendLine("• Service: 动态注册或代理")
            sb.appendLine("• Receiver: 静态转动态注册")
            sb.appendLine("• Provider: ContentProviderClient")

        } catch (e: Exception) {
            sb.appendLine("演示失败: ${e.message}")
        }
        showResultDialog("插件化原理", sb.toString())
    }

    private fun showCompareDemo() {
        val sb = StringBuilder()
        sb.appendLine("=== 类比较与判断 ===")
        sb.appendLine()

        val stringClass = String::class.java
        val activityClass = android.app.Activity::class.java
        val myClass = javaClass

        sb.appendLine("String: ${stringClass.classLoader?.javaClass?.simpleName ?: "Bootstrap"}")
        sb.appendLine("Activity: ${activityClass.classLoader?.javaClass?.simpleName ?: "Bootstrap"}")
        sb.appendLine("${myClass.simpleName}: ${myClass.classLoader?.javaClass?.simpleName ?: "Bootstrap"}")
        sb.appendLine()

        try {
            val dexFile = prepareDex()
            if (dexFile != null) {
                val pluginLoader = createDexClassLoader(dexFile, "compare")
                val pluginClass = pluginLoader.loadClass("com.peter.plugin.HelloPlugin")

                sb.appendLine("【插件 vs 宿主】")
                sb.appendLine("插件加载器: ${pluginClass.classLoader?.javaClass?.simpleName}")
                sb.appendLine("宿主加载器: ${myClass.classLoader?.javaClass?.simpleName}")
                sb.appendLine("加载器相等: ${pluginClass.classLoader == myClass.classLoader}")
                sb.appendLine("插件.parent == 宿主: ${pluginClass.classLoader?.parent == myClass.classLoader}")
                sb.appendLine()
                sb.appendLine("✅ 插件类加载器的 parent 就是宿主类加载器，因此插件可以访问宿主类")
            }
        } catch (e: Exception) {
            sb.appendLine("加载失败: ${e.message}")
        }

        showResultDialog("类比较与判断", sb.toString())
    }

    private fun showLifecycleDemo() {
        val sb = StringBuilder()
        sb.appendLine("=== 插件生命周期演示 ===")
        sb.appendLine()
        try {
            val dexFile = prepareDex()
            if (dexFile == null) {
                sb.appendLine("无法准备 DEX 文件")
                showResultDialog("插件生命周期", sb.toString())
                return
            }

            val loader = createDexClassLoader(dexFile, "lifecycle")
            val pluginClass = loader.loadClass("com.peter.plugin.HelloPlugin")
            val plugin = pluginClass.getDeclaredConstructor().newInstance()

            val pluginContextClass = loader.loadClass("com.peter.plugin.PluginContext")
            val pluginStateClass = loader.loadClass("com.peter.plugin.PluginState")

            sb.appendLine("【插件元数据】")
            val meta = pluginClass.getMethod("getMeta").invoke(plugin)!!
            val name = meta.javaClass.getMethod("getName").invoke(meta) as String
            val version = meta.javaClass.getMethod("getVersion").invoke(meta) as String
            val desc = meta.javaClass.getMethod("getDescription").invoke(meta) as String
            val caps = meta.javaClass.getMethod("getCapabilities").invoke(meta) as List<*>
            sb.appendLine("名称: $name")
            sb.appendLine("版本: $version")
            sb.appendLine("描述: $desc")
            sb.appendLine("能力: ${caps.joinToString()}")
            sb.appendLine()

            // 逐步演示生命周期
            sb.appendLine("【生命周期流转】")
            sb.appendLine()

            val state = { pluginClass.getMethod("getState").invoke(plugin) }
            sb.appendLine("初始状态: $state()")

            // onCreate
            val ctx = pluginContextClass.getConstructor(ClassLoader::class.java, Map::class.java)
                .newInstance(loader, mapOf<String, String>("name" to "Demo"))
            pluginClass.getMethod("onCreate", pluginContextClass).invoke(plugin, ctx)
            sb.appendLine("→ onCreate(ctx) → $state()")

            // onStart
            pluginClass.getMethod("onStart").invoke(plugin)
            sb.appendLine("→ onStart()     → $state()")

            // execute with params
            @Suppress("UNCHECKED_CAST")
            val result1 = pluginClass.getMethod("execute", Map::class.java).invoke(plugin, mapOf("name" to "Android", "lang" to "en") as Map<String, String>) as String
            sb.appendLine("→ execute({name=Android, lang=en})")
            sb.appendLine("  结果: $result1")

            val result2 = pluginClass.getMethod("execute", Map::class.java).invoke(plugin, mapOf("name" to "插件", "lang" to "zh") as Map<String, String>) as String
            sb.appendLine("→ execute({name=插件, lang=zh})")
            sb.appendLine("  结果: $result2")

            // onStop
            pluginClass.getMethod("onStop").invoke(plugin)
            sb.appendLine("→ onStop()      → $state()")

            // onDestroy
            pluginClass.getMethod("onDestroy").invoke(plugin)
            sb.appendLine("→ onDestroy()   → $state()")

            sb.appendLine()
            sb.appendLine("✅ 完整生命周期: CREATED → STARTED → (execute × N) → STOPPED → DESTROYED")

        } catch (e: Exception) {
            sb.appendLine("演示失败: ${e.message}")
        }
        showResultDialog("插件生命周期", sb.toString())
    }

    private fun showInterPluginDemo() {
        val sb = StringBuilder()
        sb.appendLine("=== 插件间通信演示 ===")
        sb.appendLine()
        try {
            val dexFile = prepareDex()
            if (dexFile == null) {
                sb.appendLine("无法准备 DEX 文件")
                showResultDialog("插件间通信", sb.toString())
                return
            }

            // 宿主端创建 MessageBus
            val hostBus = PluginMessageBus()
            sb.appendLine("【步骤1】宿主创建 MessageBus")
            sb.appendLine()

            // 加载两个独立的 MessengerPlugin 实例（不同 ClassLoader 模拟两个插件）
            val loader1 = createDexClassLoader(dexFile, "msg_1")
            val loader2 = createDexClassLoader(dexFile, "msg_2")

            val messengerClass1 = loader1.loadClass("com.peter.plugin.MessengerPlugin")
            val messengerClass2 = loader2.loadClass("com.peter.plugin.MessengerPlugin")

            val plugin1 = messengerClass1.getDeclaredConstructor().newInstance()
            val plugin2 = messengerClass2.getDeclaredConstructor().newInstance()

            // 初始化两个插件
            val pluginContextClass1 = loader1.loadClass("com.peter.plugin.PluginContext")
            val ctx1 = pluginContextClass1.getConstructor(ClassLoader::class.java, Map::class.java)
                .newInstance(loader1, emptyMap<String, String>())
            messengerClass1.getMethod("onCreate", pluginContextClass1).invoke(plugin1, ctx1)
            messengerClass1.getMethod("onStart").invoke(plugin1)

            val pluginContextClass2 = loader2.loadClass("com.peter.plugin.PluginContext")
            val ctx2 = pluginContextClass2.getConstructor(ClassLoader::class.java, Map::class.java)
                .newInstance(loader2, emptyMap<String, String>())
            messengerClass2.getMethod("onCreate", pluginContextClass2).invoke(plugin2, ctx2)
            messengerClass2.getMethod("onStart").invoke(plugin2)

            sb.appendLine("【步骤2】加载两个 MessengerPlugin (不同 ClassLoader)")
            sb.appendLine("Plugin1 ClassLoader: ${messengerClass1.classLoader?.javaClass?.simpleName}")
            sb.appendLine("Plugin2 ClassLoader: ${messengerClass2.classLoader?.javaClass?.simpleName}")
            sb.appendLine("类对象相等: ${messengerClass1 == messengerClass2} (隔离!)")
            sb.appendLine()

            // 注入 MessageBus（宿主通过反射调用 setMessageBus）
            sb.appendLine("【步骤3】宿主注入 MessageBus 到两个插件")
            val iMessageBusClass = loader1.loadClass("com.peter.plugin.IMessageBus")
            messengerClass1.getMethod("setMessageBus", iMessageBusClass, List::class.java)
                .invoke(plugin1, hostBus, listOf("chat", "system"))
            messengerClass2.getMethod("setMessageBus", iMessageBusClass, List::class.java)
                .invoke(plugin2, hostBus, listOf("chat"))
            sb.appendLine("Plugin1 订阅: [chat, system]")
            sb.appendLine("Plugin2 订阅: [chat]")
            sb.appendLine()

            // Plugin1 发布消息
            sb.appendLine("【步骤4】Plugin1 发布消息到 'chat' 主题")
            @Suppress("UNCHECKED_CAST")
            val publishResult1 = messengerClass1.getMethod("execute", Map::class.java)
                .invoke(plugin1, mapOf("action" to "publish", "topic" to "chat", "content" to "Hello from Plugin1") as Map<String, String>) as String
            sb.appendLine(publishResult1)
            sb.appendLine()

            // Plugin2 发布消息
            sb.appendLine("【步骤5】Plugin2 发布消息到 'chat' 主题")
            @Suppress("UNCHECKED_CAST")
            val publishResult2 = messengerClass2.getMethod("execute", Map::class.java)
                .invoke(plugin2, mapOf("action" to "publish", "topic" to "chat", "content" to "Hi from Plugin2") as Map<String, String>) as String
            sb.appendLine(publishResult2)
            sb.appendLine()

            // 检查各自收到的消息
            sb.appendLine("【步骤6】检查 Plugin1 收到的消息")
            @Suppress("UNCHECKED_CAST")
            val history1 = messengerClass1.getMethod("execute", Map::class.java)
                .invoke(plugin1, mapOf("action" to "history") as Map<String, String>) as String
            sb.appendLine(history1)
            sb.appendLine()

            sb.appendLine("【步骤7】检查 Plugin2 收到的消息")
            @Suppress("UNCHECKED_CAST")
            val history2 = messengerClass2.getMethod("execute", Map::class.java)
                .invoke(plugin2, mapOf("action" to "history") as Map<String, String>) as String
            sb.appendLine(history2)
            sb.appendLine()

            sb.appendLine("【关键发现】")
            sb.appendLine("• Plugin1 收到了 Plugin2 发的消息")
            sb.appendLine("• Plugin2 收到了 Plugin1 发的消息")
            sb.appendLine("• 两个插件使用不同 ClassLoader (类隔离)")
            sb.appendLine("• 通过宿主端 MessageBus 桥接实现跨 ClassLoader 通信")
            sb.appendLine("• 消息传递通过宿主中转，绕过 ClassLoader 隔离限制")

        } catch (e: Exception) {
            sb.appendLine("演示失败: ${e.message}")
        }
        showResultDialog("插件间通信", sb.toString())
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
