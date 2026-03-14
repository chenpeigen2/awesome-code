package com.peter.classloader.demo

import dalvik.system.DexClassLoader
import dalvik.system.PathClassLoader
import java.io.File

/**
 * ClassLoader 工具类
 * 提供各种 ClassLoader 相关的工具方法
 */
object ClassLoaderUtils {

    /**
     * 获取当前线程的 ClassLoader
     */
    fun getCurrentClassLoader(): ClassLoader {
        return Thread.currentThread().contextClassLoader 
            ?: ClassLoader.getSystemClassLoader()
    }

    /**
     * 获取 Bootstrap ClassLoader (根类加载器)
     * 在 Android 中，Bootstrap ClassLoader 加载核心类
     */
    fun getBootstrapClassLoader(): ClassLoader? {
        var classLoader: ClassLoader? = getCurrentClassLoader()
        while (classLoader?.parent != null) {
            classLoader = classLoader.parent
        }
        return classLoader
    }

    /**
     * 获取 ClassLoader 链
     * @return ClassLoader 链的描述列表
     */
    fun getClassLoaderChain(): List<ClassLoaderInfo> {
        val result = mutableListOf<ClassLoaderInfo>()
        var classLoader: ClassLoader? = getCurrentClassLoader()
        var level = 0
        
        while (classLoader != null) {
            val info = ClassLoaderInfo(
                level = level,
                className = classLoader.javaClass.name,
                simpleName = classLoader.javaClass.simpleName,
                path = getClassLoaderPath(classLoader),
                description = getClassLoaderDescription(classLoader)
            )
            result.add(info)
            classLoader = classLoader.parent
            level++
        }
        
        return result
    }

    /**
     * 获取 ClassLoader 的路径
     */
    private fun getClassLoaderPath(classLoader: ClassLoader): String {
        return when (classLoader) {
            is PathClassLoader -> {
                try {
                    val pathField = PathClassLoader::class.java.getDeclaredField("path")
                    pathField.isAccessible = true
                    pathField.get(classLoader)?.toString() ?: "unknown"
                } catch (e: Exception) {
                    "unknown"
                }
            }
            is DexClassLoader -> {
                try {
                    val pathField = DexClassLoader::class.java.getDeclaredField("path")
                    pathField.isAccessible = true
                    pathField.get(classLoader)?.toString() ?: "unknown"
                } catch (e: Exception) {
                    "unknown"
                }
            }
            else -> "N/A"
        }
    }

    /**
     * 获取 ClassLoader 的描述
     */
    private fun getClassLoaderDescription(classLoader: ClassLoader): String {
        return when (classLoader.javaClass.name) {
            "dalvik.system.PathClassLoader" -> "Android 应用类加载器，加载应用内的类"
            "dalvik.system.DexClassLoader" -> "Dex 类加载器，可加载外部 dex/apk"
            "dalvik.system.BootClassLoader" -> "启动类加载器，加载核心类库"
            "java.lang.BootClassLoader" -> "Java 启动类加载器"
            else -> "自定义类加载器"
        }
    }

    /**
     * 检查类是否已加载
     */
    fun isClassLoaded(className: String, classLoader: ClassLoader = getCurrentClassLoader()): Boolean {
        return try {
            val findLoadedClassMethod = ClassLoader::class.java.getDeclaredMethod(
                "findLoadedClass",
                String::class.java
            )
            findLoadedClassMethod.isAccessible = true
            val result = findLoadedClassMethod.invoke(classLoader, className)
            result != null
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 加载指定类
     */
    fun loadClass(className: String, classLoader: ClassLoader = getCurrentClassLoader()): Class<*>? {
        return try {
            Class.forName(className, false, classLoader)
        } catch (e: ClassNotFoundException) {
            null
        }
    }

    /**
     * 创建 DexClassLoader
     * @param dexPath dex/apk 文件路径
     * @param optimizedDirectory 优化后的 dex 存储目录
     */
    fun createDexClassLoader(dexPath: String, optimizedDirectory: File): DexClassLoader {
        return DexClassLoader(
            dexPath,
            optimizedDirectory.absolutePath,
            null,
            getCurrentClassLoader()
        )
    }

    /**
     * 获取类的来源
     */
    fun getClassSource(clazz: Class<*>): String {
        val protectionDomain = clazz.protectionDomain
        val codeSource = protectionDomain?.codeSource
        return codeSource?.location?.toString() ?: "Unknown"
    }

    /**
     * 比较两个类的加载器
     */
    fun compareClassLoaders(class1: Class<*>, class2: Class<*>): ClassLoaderComparison {
        val loader1 = class1.classLoader
        val loader2 = class2.classLoader
        
        return ClassLoaderComparison(
            class1Name = class1.name,
            class2Name = class2.name,
            loader1Info = loader1?.javaClass?.name ?: "Bootstrap",
            loader2Info = loader2?.javaClass?.name ?: "Bootstrap",
            sameLoader = loader1 == loader2,
            canCast = try {
                class1.isAssignableFrom(class2)
                true
            } catch (e: Exception) {
                false
            }
        )
    }
}

/**
 * ClassLoader 信息
 */
data class ClassLoaderInfo(
    val level: Int,
    val className: String,
    val simpleName: String,
    val path: String,
    val description: String
)

/**
 * ClassLoader 比较结果
 */
data class ClassLoaderComparison(
    val class1Name: String,
    val class2Name: String,
    val loader1Info: String,
    val loader2Info: String,
    val sameLoader: Boolean,
    val canCast: Boolean
)
