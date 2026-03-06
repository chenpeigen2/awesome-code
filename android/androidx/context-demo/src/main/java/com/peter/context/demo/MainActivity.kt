package com.peter.context.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.context.demo.databinding.ActivityMainBinding

/**
 * Context Demo 主入口
 * 
 * 本 Demo 包含以下内容：
 * 
 * 一、Context 基础
 * 1. Context 类型介绍 - Application、Activity、Service Context 的区别
 * 2. 资源访问 - 通过 Context 获取字符串、颜色、尺寸、图片等资源
 * 3. SharedPreferences - 使用 Context 进行轻量级数据存储
 * 4. 文件操作 - 内部存储、外部存储、缓存文件操作
 * 
 * 二、Context 进阶
 * 1. 数据库操作 - 使用 Context 打开或创建数据库
 * 2. 系统服务 - 通过 Context 获取各种系统服务
 * 3. Window 和 Dialog - 使用 Context 创建 Dialog 和 Window
 * 
 * 三、Context 深入
 * 1. ContextWrapper 详解 - Context 的装饰器模式，自定义 Context
 * 2. 内存泄漏分析 - Context 使用不当导致的内存泄漏及解决方案
 * 3. 最佳实践 - Context 使用的最佳实践和注意事项
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = MainAdapter(getMenuItems()) { item ->
                startActivity(item.intent)
            }
        }
    }

    private fun getMenuItems(): List<MenuItem> {
        return listOf(
            // 基础示例
            MenuItem(
                title = getString(R.string.basic_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.context_type),
                description = getString(R.string.context_type_desc),
                intent = createContextTypeIntent(this)
            ),
            MenuItem(
                title = getString(R.string.context_resources),
                description = getString(R.string.context_resources_desc),
                intent = createContextResourcesIntent(this)
            ),
            MenuItem(
                title = getString(R.string.context_shared_preferences),
                description = getString(R.string.context_shared_preferences_desc),
                intent = createContextSharedPreferencesIntent(this)
            ),
            MenuItem(
                title = getString(R.string.context_file),
                description = getString(R.string.context_file_desc),
                intent = createContextFileIntent(this)
            ),

            // 进阶示例
            MenuItem(
                title = getString(R.string.advanced_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.context_database),
                description = getString(R.string.context_database_desc),
                intent = createContextDatabaseIntent(this)
            ),
            MenuItem(
                title = getString(R.string.context_system_services),
                description = getString(R.string.context_system_services_desc),
                intent = createContextSystemServicesIntent(this)
            ),
            MenuItem(
                title = getString(R.string.context_window),
                description = getString(R.string.context_window_desc),
                intent = createContextWindowIntent(this)
            ),

            // 深入示例
            MenuItem(
                title = getString(R.string.deep_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.context_wrapper),
                description = getString(R.string.context_wrapper_desc),
                intent = createContextWrapperIntent(this)
            ),
            MenuItem(
                title = getString(R.string.context_memory_leak),
                description = getString(R.string.context_memory_leak_desc),
                intent = createContextMemoryLeakIntent(this)
            ),
            MenuItem(
                title = getString(R.string.context_best_practices),
                description = getString(R.string.context_best_practices_desc),
                intent = createContextBestPracticesIntent(this)
            )
        )
    }
}
