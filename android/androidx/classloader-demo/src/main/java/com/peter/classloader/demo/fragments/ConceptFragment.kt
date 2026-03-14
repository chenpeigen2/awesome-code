package com.peter.classloader.demo.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.classloader.demo.*
import com.peter.classloader.demo.databinding.FragmentConceptBinding

/**
 * 基本概念 Fragment
 * 演示 ClassLoader 的基本概念和原理
 */
class ConceptFragment : Fragment() {

    private var _binding: FragmentConceptBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = ConceptFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConceptBinding.inflate(inflater, container, false)
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
                feature = ClassLoaderFeature.CONCEPT_CLASSLOADER_CHAIN,
                title = "ClassLoader 链",
                description = "查看当前应用的 ClassLoader 层级结构",
                category = FeatureCategory.CONCEPT
            ),
            FeatureItem(
                feature = ClassLoaderFeature.CONCEPT_DELEGATION,
                title = "双亲委派模型",
                description = "理解类加载的双亲委派机制",
                category = FeatureCategory.CONCEPT
            ),
            FeatureItem(
                feature = ClassLoaderFeature.CONCEPT_CLASS_LOADERS,
                title = "Android ClassLoader",
                description = "Android 中常用的 ClassLoader 类型",
                category = FeatureCategory.CONCEPT
            )
        )
    }

    private fun handleFeatureClick(feature: ClassLoaderFeature) {
        when (feature) {
            ClassLoaderFeature.CONCEPT_CLASSLOADER_CHAIN -> showClassLoaderChain()
            ClassLoaderFeature.CONCEPT_DELEGATION -> showDelegationModel()
            ClassLoaderFeature.CONCEPT_CLASS_LOADERS -> showAndroidClassLoaders()
            else -> {}
        }
    }

    private fun showClassLoaderChain() {
        val chain = ClassLoaderUtils.getClassLoaderChain()
        val sb = StringBuilder()
        sb.appendLine("=== ClassLoader 链 ===")
        sb.appendLine()
        
        chain.forEach { info ->
            sb.appendLine("【Level ${info.level}】${info.simpleName}")
            sb.appendLine("  类名: ${info.className}")
            sb.appendLine("  路径: ${info.path}")
            sb.appendLine("  说明: ${info.description}")
            sb.appendLine()
        }
        
        sb.appendLine("=== 双亲委派模型 ===")
        sb.appendLine("当加载一个类时：")
        sb.appendLine("1. 先检查是否已加载")
        sb.appendLine("2. 委托父加载器加载")
        sb.appendLine("3. 父加载器无法加载时，自己加载")
        
        showResultDialog("ClassLoader 链", sb.toString())
    }

    private fun showDelegationModel() {
        val message = """
=== 双亲委派模型 ===

【工作流程】
当一个类加载器收到类加载请求时：

1. 检查是否已加载
   ↓
2. 委托父加载器加载
   ↓
3. 父加载器递归委托
   ↓
4. 顶层无法加载
   ↓
5. 子加载器尝试加载

【优点】
• 安全性：核心类不会被篡改
• 避免重复加载
• 保证类的唯一性

【Android 中的实现】
BootClassLoader (核心类库)
    ↓
PathClassLoader (应用类)
    ↓
自定义 ClassLoader

【打破双亲委派】
• SPI 机制
• 插件化框架
• 热修复技术
        """.trimIndent()
        
        showResultDialog("双亲委派模型", message)
    }

    private fun showAndroidClassLoaders() {
        val message = """
=== Android ClassLoader 类型 ===

【BootClassLoader】
• 系统启动时创建
• 加载核心类库
• java.*、android.* 等类
• 没有父加载器

【PathClassLoader】
• Android 应用默认加载器
• 加载应用内 classes.dex
• 加载已安装应用的类
• 只能加载已安装的 APK

【DexClassLoader】
• 可加载外部 dex/apk/jar
• 支持动态加载
• 需要指定优化目录
• 插件化、热修复的基础

【InMemoryDexClassLoader】
• Android 8.0+ (API 26)
• 从内存中加载 DEX
• 适用于动态生成代码

【DelegateLastClassLoader】
• Android 8.1+ (API 27)
• 打破双亲委派的实现
• 先自己加载，再委托父类

=== 使用场景 ===
• PathClassLoader：正常应用开发
• DexClassLoader：插件化、热修复
• InMemoryDexClassLoader：动态代码
• DelegateLastClassLoader：类替换
        """.trimIndent()
        
        showResultDialog("Android ClassLoader", message)
    }

    private fun showResultDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext())
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
