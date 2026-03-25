package com.peter.listview.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.peter.listview.demo.adapter.SimpleArrayAdapter
import com.peter.listview.demo.databinding.FragmentArrayAdapterBinding
import com.peter.listview.demo.viewmodel.ListViewDemoViewModel

/**
 * Tab 1: ArrayAdapter & SimpleAdapter 演示
 *
 * 展示内容：
 * 1. ArrayAdapter 基础用法 - 字符串列表
 * 2. ArrayAdapter 自定义布局 - 图标+文字
 * 3. SimpleAdapter 基础用法 - Map 数据绑定
 * 4. SimpleAdapter ViewBinder - 自定义绑定逻辑
 */
class ArrayAdapterFragment : Fragment() {

    private var _binding: FragmentArrayAdapterBinding? = null
    private val viewModel: ListViewDemoViewModel by activityViewModels()
    private lateinit var arrayAdapter: SimpleArrayAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArrayAdapterBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupArrayAdapter()
        observeData()
    }

    private fun setupArrayAdapter() {
        // ArrayAdapter 基础示例 - 使用 mutableListOf 创建可变列表
        val items = mutableListOf(
            "Android", "iOS", "Windows", "macOS", "Linux", "Chrome OS"
        )
        arrayAdapter = SimpleArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            items
        )
        _binding?.listView?.adapter = arrayAdapter

        // 点击事件
        _binding?.listView?.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val item = arrayAdapter.getItem(position) as String
            Toast.makeText(context, "点击: $item", Toast.LENGTH_SHORT).show()
        }

        // 长按事件
        _binding?.listView?.setOnItemLongClickListener { _, _, position, _ ->
            Toast.makeText(context, "长按: $position", Toast.LENGTH_SHORT).show()
            true
        }

        // 设置空视图
        _binding?.listView?.emptyView = _binding?.emptyView?.root
    }

    private fun observeData() {
        viewModel.simpleStringItems.observe(viewLifecycleOwner) { items ->
            items?.let {
                // 更新数据（这里使用模拟数据）
                // 实际项目中数据来自 ViewModel
                val data = listOf(
                    "Android 14", "Android 15", "Android 16",
                    "Kotlin 1.5", "Kotlin 1.6", "Kotlin 1.7",
                    "Jetpack Compose", "Material Design 3"
                )
                arrayAdapter.clear()
                arrayAdapter.addAll(data)
                arrayAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
