package com.peter.listview.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.peter.listview.demo.adapter.CustomBaseAdapter
import com.peter.listview.demo.adapter.IconItem
import com.peter.listview.demo.adapter.IconType
import com.peter.listview.demo.databinding.FragmentBaseAdapterBinding
import com.peter.listview.demo.viewmodel.ListViewDemoViewModel

/**
 * Tab 2: BaseAdapter 演示
 *
 * 展示内容：
 * 1. BaseAdapter 基础实现 - 实现 4 个核心方法
 * 2. ViewHolder 模式 - 静态内部类缓存 View
 * 3. 多种 Item 类型 - 不同布局类型
 * 4. 优化技巧 - 稳定 ID、批量更新
 */
class BaseAdapterFragment : Fragment() {

    private var _binding: FragmentBaseAdapterBinding? = null
    private val viewModel: ListViewDemoViewModel by activityViewModels()
    private lateinit var baseAdapter: CustomBaseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBaseAdapterBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBaseAdapter()
        observeData()
    }

    private fun setupBaseAdapter() {
        // 初始化 BaseAdapter
        val items = generateIconItems()
        baseAdapter = CustomBaseAdapter(requireContext(), items)
        _binding?.listView?.adapter = baseAdapter

        // 点击事件
        _binding?.listView?.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val item = baseAdapter.getItem(position) as IconItem
            Toast.makeText(context, "点击: ${item.title}", Toast.LENGTH_SHORT).show()
        }

        // 长按事件
        _binding?.listView?.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
            // 演示删除操作
            val currentItems = baseAdapter.items.toMutableList()
            if (position < currentItems.size) {
                currentItems.removeAt(position)
                baseAdapter.updateItems(currentItems)
                Toast.makeText(context, "已删除", Toast.LENGTH_SHORT).show()
            }
            true
        }

        // 设置空视图
        _binding?.listView?.emptyView = _binding?.emptyView?.root
    }

    private fun generateIconItems(): List<IconItem> {
        val iconTypes = IconType.values()
        return (1..20).map { i ->
            IconItem(
                id = i.toLong(),
                iconType = iconTypes[i % iconTypes.size],
                title = "Item $i",
                description = "这是第 $i 个列表项的描述"
            )
        }
    }

    private fun observeData() {
        // 观察数据变化
        viewModel.iconItems.observe(viewLifecycleOwner) { items ->
            items?.let {
                baseAdapter.updateItems(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
