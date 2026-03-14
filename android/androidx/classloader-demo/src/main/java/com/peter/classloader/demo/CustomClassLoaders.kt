package com.peter.classloader.demo

import dalvik.system.DexClassLoader
import java.io.File

/**
 * 自定义 ClassLoader 示例
 * 演示如何创建和使用自定义 ClassLoader
 */

/**
 * 简单的自定义 ClassLoader
 * 继承 ClassLoader，重写 findClass 方法
 */
class SimpleClassLoader : ClassLoader() {

    // 模拟类字节码缓存
    private val classBytes = mutableMapOf<String, ByteArray>()

    /**
     * 注册类字节码
     */
    fun registerClass(name: String, bytes: ByteArray) {
        classBytes[name] = bytes
    }

    override fun findClass(name: String): Class<*> {
        val bytes = classBytes[name]
            ?: throw ClassNotFoundException("Class $name not found in SimpleClassLoader")
        
        return defineClass(name, bytes, 0, bytes.size)
    }
}

/**
 * 动态加载 DEX 的 ClassLoader
 * 基于 DexClassLoader 实现
 */
class DynamicDexClassLoader(
    dexPath: String,
    optimizedDirectory: File,
    parent: ClassLoader = ClassLoaderUtils.getCurrentClassLoader()
) : DexClassLoader(dexPath, optimizedDirectory.absolutePath, null, parent) {

    /**
     * 加载指定类
     * @param className 完整类名
     * @return 加载的类，如果加载失败返回 null
     */
    fun loadClassOrNull(className: String): Class<*>? {
        return try {
            loadClass(className)
        } catch (e: ClassNotFoundException) {
            null
        }
    }

    /**
     * 获取所有可加载的类（简化版本，仅列出已知的类）
     */
    fun listLoadableClasses(): List<String> {
        // 实际应用中需要解析 DEX 文件获取类列表
        return emptyList()
    }
}

/**
 * 隔离 ClassLoader
 * 用于类隔离，打破双亲委派模型
 */
class IsolatedClassLoader(
    private val delegate: ClassLoader
) : ClassLoader(null) { // parent 为 null，打破双亲委派

    // 需要隔离的类前缀
    private var isolatedPrefixes = mutableListOf<String>()

    /**
     * 添加需要隔离的包前缀
     */
    fun addIsolatedPrefix(prefix: String) {
        isolatedPrefixes.add(prefix)
    }

    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        // 检查是否已加载
        var clazz = findLoadedClass(name)
        
        if (clazz == null) {
            // 检查是否需要隔离
            val shouldIsolate = isolatedPrefixes.any { name.startsWith(it) }
            
            clazz = if (shouldIsolate) {
                // 隔离加载：不使用父加载器，直接从 delegate 加载
                try {
                    delegate.loadClass(name)
                } catch (e: ClassNotFoundException) {
                    findClass(name)
                }
            } else {
                // 正常加载：先使用父加载器（这里是 null），再使用 delegate
                try {
                    findClass(name)
                } catch (e: ClassNotFoundException) {
                    delegate.loadClass(name)
                }
            }
        }
        
        if (resolve) {
            resolveClass(clazz)
        }
        
        return clazz
    }
}

/**
 * 热修复 ClassLoader
 * 演示热修复的基本原理
 */
class HotFixClassLoader(
    private val patchDexPath: String?,
    private val originalClassLoader: ClassLoader
) : ClassLoader(originalClassLoader) {

    private var patchClassLoader: DexClassLoader? = null

    init {
        patchDexPath?.let { path ->
            val file = File(path)
            if (file.exists()) {
                val optDir = File(file.parent, "opt_dex")
                if (!optDir.exists()) {
                    optDir.mkdirs()
                }
                patchClassLoader = DexClassLoader(
                    path,
                    optDir.absolutePath,
                    null,
                    originalClassLoader.parent
                )
            }
        }
    }

    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        // 1. 检查是否已加载
        var clazz = findLoadedClass(name)
        
        if (clazz == null) {
            // 2. 优先从补丁加载
            clazz = patchClassLoader?.let {
                try {
                    it.loadClass(name)
                } catch (e: ClassNotFoundException) {
                    null
                }
            }
            
            // 3. 补丁中没有，从原始 ClassLoader 加载
            if (clazz == null) {
                clazz = originalClassLoader.loadClass(name)
            }
        }
        
        if (resolve) {
            resolveClass(clazz)
        }
        
        return clazz
    }

    /**
     * 添加新的补丁
     */
    fun addPatch(newPatchPath: String) {
        // 实际实现需要更复杂的逻辑来合并多个补丁
        android.util.Log.d("HotFixClassLoader", "Add patch: $newPatchPath")
    }
}

/**
 * 插件 ClassLoader
 * 用于插件化框架，实现类隔离和资源加载
 */
class PluginClassLoader(
    private val pluginPath: String,
    private val hostClassLoader: ClassLoader
) : ClassLoader(hostClassLoader) {

    private val pluginClassLoader: DexClassLoader by lazy {
        val file = File(pluginPath)
        val optDir = File(file.parent, "plugin_opt")
        if (!optDir.exists()) {
            optDir.mkdirs()
        }
        DexClassLoader(
            pluginPath,
            optDir.absolutePath,
            null,
            hostClassLoader.parent // 使用宿主的 parent，实现部分隔离
        )
    }

    // 插件可以独立加载的包前缀
    private val pluginPackages = mutableListOf<String>()

    /**
     * 添加插件包前缀
     */
    fun addPluginPackage(packageName: String) {
        pluginPackages.add(packageName)
    }

    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        // 1. 检查是否已加载
        var clazz = findLoadedClass(name)
        
        if (clazz == null) {
            // 2. 检查是否是插件类
            val isPluginClass = pluginPackages.any { name.startsWith(it) }
            
            clazz = if (isPluginClass) {
                // 插件类：优先从插件 ClassLoader 加载
                try {
                    pluginClassLoader.loadClass(name)
                } catch (e: ClassNotFoundException) {
                    // 插件中没有，尝试从宿主加载
                    hostClassLoader.loadClass(name)
                }
            } else {
                // 非插件类：正常双亲委派
                super.loadClass(name, resolve)
            }
        }
        
        if (resolve && clazz != null) {
            resolveClass(clazz)
        }
        
        return clazz ?: throw ClassNotFoundException(name)
    }
}
