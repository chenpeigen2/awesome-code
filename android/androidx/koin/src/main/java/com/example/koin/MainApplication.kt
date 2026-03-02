package com.example.koin

import android.app.Application
import android.util.Log
import dalvik.system.BaseDexClassLoader
import dalvik.system.PathClassLoader
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(sampleModule)
        }
        hook(this)
    }

    fun hook(application: Application) {
        val pathClassLoader = application.classLoader as PathClassLoader

        try {
            // 1. 创建 LogClassLoader，这里必须传入合法参数
            // 注意：Android 8.0+ PathClassLoader 构造函数变了
            val logClassLoader = LogClassLoader("", pathClassLoader.parent)

            // 2. 偷梁换柱：把 PathClassLoader 的 pathList 移给 LogClassLoader
            // 这一步是核心，让 LogClassLoader 拥有查找应用类的能力
            val pathListField = BaseDexClassLoader::class.java.getDeclaredField("pathList")
            pathListField.isAccessible = true
            val pathList = pathListField.get(pathClassLoader)
            pathListField.set(logClassLoader, pathList)

            // 3. 修改 PathClassLoader 的 parent，让它指向 LogClassLoader
            // 此时 LogClassLoader 是 PathClassLoader 类型，系统不会拦截
            val parentField = ClassLoader::class.java.getDeclaredField("parent")
            parentField.isAccessible = true
            parentField.set(pathClassLoader, logClassLoader)

        } catch (e: Exception) {
            Log.e("Hook", "Hook failed", e)
        }
    }

}

