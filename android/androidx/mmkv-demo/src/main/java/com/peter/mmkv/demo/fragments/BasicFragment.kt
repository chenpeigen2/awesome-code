package com.peter.mmkv.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.peter.mmkv.demo.*
import com.peter.mmkv.demo.databinding.FragmentBasicBinding

/**
 * 基本操作 Fragment
 * 演示 MMKV 的基本读写删操作
 */
class BasicFragment : Fragment() {

    private var _binding: FragmentBasicBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = BasicFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBasicBinding.inflate(inflater, container, false)
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
            // 基本操作
            FeatureItem(
                feature = MMKVFeature.BASIC_WRITE,
                title = "写入数据",
                description = "演示如何向 MMKV 写入各种类型的数据",
                category = FeatureCategory.BASIC
            ),
            FeatureItem(
                feature = MMKVFeature.BASIC_READ,
                title = "读取数据",
                description = "演示如何从 MMKV 读取已存储的数据",
                category = FeatureCategory.BASIC
            ),
            FeatureItem(
                feature = MMKVFeature.BASIC_DELETE,
                title = "删除数据",
                description = "演示如何删除 MMKV 中存储的数据",
                category = FeatureCategory.BASIC
            ),
            FeatureItem(
                feature = MMKVFeature.BASIC_CONTAINS,
                title = "检查键是否存在",
                description = "演示如何检查某个键是否存在于 MMKV 中",
                category = FeatureCategory.BASIC
            )
        )
    }

    private fun handleFeatureClick(feature: MMKVFeature) {
        when (feature) {
            MMKVFeature.BASIC_WRITE -> showWriteDialog()
            MMKVFeature.BASIC_READ -> showReadDialog()
            MMKVFeature.BASIC_DELETE -> showDeleteDialog()
            MMKVFeature.BASIC_CONTAINS -> showContainsDialog()
            else -> {}
        }
    }

    private fun showWriteDialog() {
        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val keyInput = EditText(context).apply {
            hint = "请输入键名"
        }
        val valueInput = EditText(context).apply {
            hint = "请输入值（字符串）"
        }

        layout.addView(keyInput)
        layout.addView(valueInput)

        MaterialAlertDialogBuilder(context)
            .setTitle("写入数据")
            .setView(layout)
            .setPositiveButton("写入") { _, _ ->
                val key = keyInput.text.toString().trim()
                val value = valueInput.text.toString()
                
                if (key.isEmpty()) {
                    showMessage("键名不能为空")
                    return@setPositiveButton
                }
                
                MMKVManager.put(key, value)
                showMessage("写入成功: $key = $value")
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showReadDialog() {
        val context = requireContext()
        val input = EditText(context).apply {
            hint = "请输入要读取的键名"
        }

        MaterialAlertDialogBuilder(context)
            .setTitle("读取数据")
            .setView(input)
            .setPositiveButton("读取") { _, _ ->
                val key = input.text.toString().trim()
                
                if (key.isEmpty()) {
                    showMessage("键名不能为空")
                    return@setPositiveButton
                }
                
                val value = MMKVManager.getString(key)
                if (value.isEmpty() && !MMKVManager.contains(key)) {
                    showMessage("键 '$key' 不存在")
                } else {
                    showResultDialog("读取结果", "键: $key\n值: $value")
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showDeleteDialog() {
        val context = requireContext()
        
        // 先显示所有键
        val keys = MMKVManager.getAllKeys()
        if (keys.isEmpty()) {
            showMessage("当前没有存储任何数据")
            return
        }

        val items = keys.toList().toTypedArray()
        val checkedItems = BooleanArray(items.size)

        MaterialAlertDialogBuilder(context)
            .setTitle("删除数据（选择要删除的键）")
            .setMultiChoiceItems(items, checkedItems) { _, _, _ -> }
            .setPositiveButton("删除") { _, _ ->
                val keysToDelete = items.filterIndexed { index, _ -> checkedItems[index] }
                if (keysToDelete.isEmpty()) {
                    showMessage("未选择任何项")
                } else {
                    keysToDelete.forEach { MMKVManager.remove(it) }
                    showMessage("已删除 ${keysToDelete.size} 个键")
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showContainsDialog() {
        val context = requireContext()
        val input = EditText(context).apply {
            hint = "请输入要检查的键名"
        }

        MaterialAlertDialogBuilder(context)
            .setTitle("检查键是否存在")
            .setView(input)
            .setPositiveButton("检查") { _, _ ->
                val key = input.text.toString().trim()
                
                if (key.isEmpty()) {
                    showMessage("键名不能为空")
                    return@setPositiveButton
                }
                
                val exists = MMKVManager.contains(key)
                showMessage("键 '$key' ${if (exists) "存在" else "不存在"}")
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showResultDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("确定", null)
            .show()
    }

    private fun showMessage(message: String) {
        (requireActivity() as MainActivity).showMessage(message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
