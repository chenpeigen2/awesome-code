package com.peter.layoutinflater.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.layoutinflater.demo.databinding.ActivityMainBinding
import com.peter.layoutinflater.demo.basic.InflaterInstanceActivity

/**
 * LayoutInflater Demo 主入口
 * 
 * 本 Demo 包含以下内容：
 * 
 * 一、LayoutInflater 基础
 * 1. 基本 inflate 方法 - 最简单的使用方式
 * 2. 获取 LayoutInflater - 四种获取方式对比
 * 
 * 二、LayoutInflater 进阶
 * 1. attachToRoot 参数详解 - 深入理解参数作用
 * 2. LayoutInflaterCompat 与 Factory2 - 设置自定义工厂
 * 
 * 三、LayoutInflater 工厂机制
 * 1. LayoutInflater.Factory - 拦截和替换 View 创建
 * 
 * 四、自定义 LayoutInflater
 * 1. Clone LayoutInflater 并自定义行为
 * 
 * 五、LayoutInflater 实战
 * 1. RecyclerView 中使用 - Adapter 中的正确用法
 * 2. 动态添加 View - 运行时加载布局
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
                item.intent?.let { startActivity(it) }
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
                title = getString(R.string.basic_inflate),
                description = getString(R.string.basic_inflate_desc),
                intent = createBasicInflateIntent(this)
            ),
            MenuItem(
                title = getString(R.string.get_layout_inflater),
                description = getString(R.string.get_layout_inflater_desc),
                intent = createGetLayoutInflaterIntent(this)
            ),
            MenuItem(
                title = getString(R.string.inflater_instance),
                description = getString(R.string.inflater_instance_desc),
                intent = createInflaterInstanceIntent(this)
            ),

            // 进阶示例
            MenuItem(
                title = getString(R.string.advanced_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.attach_to_root),
                description = getString(R.string.attach_to_root_desc),
                intent = createAttachToRootIntent(this)
            ),
            MenuItem(
                title = getString(R.string.layout_inflater_compat),
                description = getString(R.string.layout_inflater_compat_desc),
                intent = createLayoutInflaterCompatIntent(this)
            ),
            MenuItem(
                title = getString(R.string.async_inflate),
                description = getString(R.string.async_inflate_desc),
                intent = createAsyncInflateIntent(this)
            ),

            // 工厂机制
            MenuItem(
                title = getString(R.string.factory_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.factory_example),
                description = getString(R.string.factory_example_desc),
                intent = createFactoryIntent(this)
            ),

            // 自定义 LayoutInflater
            MenuItem(
                title = getString(R.string.custom_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.custom_layout_inflater),
                description = getString(R.string.custom_layout_inflater_desc),
                intent = createCustomLayoutInflaterIntent(this)
            ),
            MenuItem(
                title = getString(R.string.clone_inflater),
                description = getString(R.string.clone_inflater_desc),
                intent = createCloneInflaterIntent(this)
            ),

            // 实战示例
            MenuItem(
                title = getString(R.string.practice_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.recyclerview_example),
                description = getString(R.string.recyclerview_example_desc),
                intent = createRecyclerViewIntent(this)
            ),
            MenuItem(
                title = getString(R.string.dynamic_view),
                description = getString(R.string.dynamic_view_desc),
                intent = createDynamicViewIntent(this)
            )
        )
    }
}
