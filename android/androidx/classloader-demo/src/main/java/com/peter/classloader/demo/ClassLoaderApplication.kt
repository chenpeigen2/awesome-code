package com.peter.classloader.demo

import android.app.Application
import dalvik.system.PathClassLoader

/**
 * ClassLoader Demo Application
 * 演示 ClassLoader 的初始化和基本概念
 */
class ClassLoaderApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // 打印当前线程的 ClassLoader 信息
        logClassLoaderInfo()
    }
    
    private fun logClassLoaderInfo() {
        val classLoader = javaClass.classLoader
        android.util.Log.d("ClassLoader", "=== Application ClassLoader Info ===")
        printClassLoaderChain(classLoader, 0)
    }
    
    private fun printClassLoaderChain(classLoader: ClassLoader?, depth: Int) {
        if (classLoader == null) return
        
        val indent = "  ".repeat(depth)
        android.util.Log.d("ClassLoader", "$indent${classLoader.javaClass.name}")
        
        // 打印 PathClassLoader 的路径信息
        if (classLoader is PathClassLoader) {
            try {
                val pathField = PathClassLoader::class.java.getDeclaredField("path")
                pathField.isAccessible = true
                val path = pathField.get(classLoader)
                android.util.Log.d("ClassLoader", "$indent  path: $path")
            } catch (e: Exception) {
                // 忽略
            }
        }
        
        // 递归打印父 ClassLoader
        try {
            val parentField = ClassLoader::class.java.getDeclaredField("parent")
            parentField.isAccessible = true
            val parent = parentField.get(classLoader) as? ClassLoader
            printClassLoaderChain(parent, depth + 1)
        } catch (e: Exception) {
            // 忽略
        }
    }
}
