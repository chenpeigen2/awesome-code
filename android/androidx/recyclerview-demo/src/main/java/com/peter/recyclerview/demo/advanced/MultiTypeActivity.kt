package com.peter.recyclerview.demo.advanced

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.recyclerview.demo.adapter.MultiTypeAdapter
import com.peter.recyclerview.demo.databinding.ActivityMultiTypeBinding
import com.peter.recyclerview.demo.model.MultiTypeItem

/**
 * 多类型 Item 示例
 * 
 * 功能演示：
 * 1. 使用 sealed class 定义多种类型的数据模型
 * 2. 根据 viewType 创建不同的 ViewHolder
 * 3. 类似聊天列表的 UI 效果
 * 
 * 关键知识点：
 * - sealed class 密封类用于定义有限类型的数据模型
 * - getItemViewType() 返回不同的类型标识
 * - onCreateViewHolder() 根据类型创建不同的 ViewHolder
 * - onBindViewHolder() 根据类型绑定数据
 */
class MultiTypeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMultiTypeBinding
    private lateinit var adapter: MultiTypeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultiTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        loadData()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = MultiTypeAdapter()

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MultiTypeActivity).apply {
                // 从底部开始显示
                stackFromEnd = true
            }
            adapter = this@MultiTypeActivity.adapter
        }
    }

    private fun loadData() {
        val items = mutableListOf<MultiTypeItem>()
        var id = 0

        // 时间分割线
        items.add(MultiTypeItem.TimeDivider(id++, "今天 14:00"))

        // 系统通知
        items.add(MultiTypeItem.SystemNotice(id++, "对方已开启朋友验证"))

        // 聊天消息
        items.add(MultiTypeItem.TextMessage(id++, "你好，在吗？", false, "14:05"))
        items.add(MultiTypeItem.TextMessage(id++, "在的，有什么事吗？", true, "14:06"))
        items.add(MultiTypeItem.TextMessage(id++, "想问问关于 RecyclerView 的问题", false, "14:07"))
        items.add(MultiTypeItem.TextMessage(id++, "好的，请说", true, "14:07"))
        items.add(MultiTypeItem.ImageMessage(id++, "image_url_1", false, "14:08"))
        items.add(MultiTypeItem.TextMessage(id++, "这张图片展示了什么？", false, "14:08"))
        items.add(MultiTypeItem.TextMessage(id++, "这是一个 RecyclerView 的示例图", true, "14:09"))

        // 时间分割线
        items.add(MultiTypeItem.TimeDivider(id++, "今天 15:30"))

        items.add(MultiTypeItem.TextMessage(id++, "明白了，谢谢！", false, "15:30"))
        items.add(MultiTypeItem.TextMessage(id++, "不客气", true, "15:31"))
        items.add(MultiTypeItem.ImageMessage(id++, "image_url_2", true, "15:32"))

        // 系统通知
        items.add(MultiTypeItem.SystemNotice(id++, "对方撤回了一条消息"))

        items.add(MultiTypeItem.TextMessage(id++, "还有其他问题吗？", true, "15:33"))
        items.add(MultiTypeItem.TextMessage(id++, "暂时没有了", false, "15:35"))

        adapter.submitList(items)
    }
}
