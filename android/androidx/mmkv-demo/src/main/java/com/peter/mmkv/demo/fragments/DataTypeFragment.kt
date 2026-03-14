package com.peter.mmkv.demo.fragments

import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.mmkv.demo.*
import com.peter.mmkv.demo.databinding.FragmentDataTypeBinding

/**
 * 数据类型 Fragment
 * 演示 MMKV 支持的各种数据类型
 */
class DataTypeFragment : Fragment() {

    private var _binding: FragmentDataTypeBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = DataTypeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataTypeBinding.inflate(inflater, container, false)
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
            FeatureItem(
                feature = MMKVFeature.TYPE_INT,
                title = "Int 类型",
                description = "存储和读取 Int 整数类型数据",
                category = FeatureCategory.DATA_TYPE
            ),
            FeatureItem(
                feature = MMKVFeature.TYPE_LONG,
                title = "Long 类型",
                description = "存储和读取 Long 长整数类型数据",
                category = FeatureCategory.DATA_TYPE
            ),
            FeatureItem(
                feature = MMKVFeature.TYPE_FLOAT,
                title = "Float 类型",
                description = "存储和读取 Float 单精度浮点数",
                category = FeatureCategory.DATA_TYPE
            ),
            FeatureItem(
                feature = MMKVFeature.TYPE_DOUBLE,
                title = "Double 类型",
                description = "存储和读取 Double 双精度浮点数",
                category = FeatureCategory.DATA_TYPE
            ),
            FeatureItem(
                feature = MMKVFeature.TYPE_BOOLEAN,
                title = "Boolean 类型",
                description = "存储和读取 Boolean 布尔值",
                category = FeatureCategory.DATA_TYPE
            ),
            FeatureItem(
                feature = MMKVFeature.TYPE_STRING,
                title = "String 类型",
                description = "存储和读取 String 字符串",
                category = FeatureCategory.DATA_TYPE
            ),
            FeatureItem(
                feature = MMKVFeature.TYPE_STRING_SET,
                title = "Set<String> 类型",
                description = "存储和读取字符串集合",
                category = FeatureCategory.DATA_TYPE
            ),
            FeatureItem(
                feature = MMKVFeature.TYPE_BYTE_ARRAY,
                title = "ByteArray 类型",
                description = "存储和读取字节数组",
                category = FeatureCategory.DATA_TYPE
            )
        )
    }

    private fun handleFeatureClick(feature: MMKVFeature) {
        when (feature) {
            MMKVFeature.TYPE_INT -> showIntDemo()
            MMKVFeature.TYPE_LONG -> showLongDemo()
            MMKVFeature.TYPE_FLOAT -> showFloatDemo()
            MMKVFeature.TYPE_DOUBLE -> showDoubleDemo()
            MMKVFeature.TYPE_BOOLEAN -> showBooleanDemo()
            MMKVFeature.TYPE_STRING -> showStringDemo()
            MMKVFeature.TYPE_STRING_SET -> showStringSetDemo()
            MMKVFeature.TYPE_BYTE_ARRAY -> showByteArrayDemo()
            else -> {}
        }
    }

    private fun showIntDemo() {
        val key = "demo_int"
        val value = 12345
        
        // 写入
        MMKVManager.put(key, value)
        
        // 读取
        val readValue = MMKVManager.getInt(key)
        
        showResultDialog(
            "Int 类型演示",
            """
                写入值: $value
                读取值: $readValue
                类型匹配: ${value == readValue}
                
                使用方法:
                MMKVManager.put("$key", $value)
                MMKVManager.getInt("$key")
            """.trimIndent()
        )
    }

    private fun showLongDemo() {
        val key = "demo_long"
        val value = 9876543210L
        
        // 写入
        MMKVManager.put(key, value)
        
        // 读取
        val readValue = MMKVManager.getLong(key)
        
        showResultDialog(
            "Long 类型演示",
            """
                写入值: $value
                读取值: $readValue
                类型匹配: ${value == readValue}
                
                使用方法:
                MMKVManager.put("$key", ${value}L)
                MMKVManager.getLong("$key")
            """.trimIndent()
        )
    }

    private fun showFloatDemo() {
        val key = "demo_float"
        val value = 3.14159f
        
        // 写入
        MMKVManager.put(key, value)
        
        // 读取
        val readValue = MMKVManager.getFloat(key)
        
        showResultDialog(
            "Float 类型演示",
            """
                写入值: $value
                读取值: $readValue
                类型匹配: ${value == readValue}
                
                使用方法:
                MMKVManager.put("$key", ${value}f)
                MMKVManager.getFloat("$key")
            """.trimIndent()
        )
    }

    private fun showDoubleDemo() {
        val key = "demo_double"
        val value = 3.141592653589793
        
        // 写入
        MMKVManager.put(key, value)
        
        // 读取
        val readValue = MMKVManager.getDouble(key)
        
        showResultDialog(
            "Double 类型演示",
            """
                写入值: $value
                读取值: $readValue
                类型匹配: ${value == readValue}
                
                使用方法:
                MMKVManager.put("$key", $value)
                MMKVManager.getDouble("$key")
            """.trimIndent()
        )
    }

    private fun showBooleanDemo() {
        val key = "demo_boolean"
        val value = true
        
        // 写入
        MMKVManager.put(key, value)
        
        // 读取
        val readValue = MMKVManager.getBoolean(key)
        
        showResultDialog(
            "Boolean 类型演示",
            """
                写入值: $value
                读取值: $readValue
                类型匹配: ${value == readValue}
                
                使用方法:
                MMKVManager.put("$key", $value)
                MMKVManager.getBoolean("$key")
            """.trimIndent()
        )
    }

    private fun showStringDemo() {
        val key = "demo_string"
        val value = "Hello MMKV!"
        
        // 写入
        MMKVManager.put(key, value)
        
        // 读取
        val readValue = MMKVManager.getString(key)
        
        showResultDialog(
            "String 类型演示",
            """
                写入值: $value
                读取值: $readValue
                类型匹配: ${value == readValue}
                
                使用方法:
                MMKVManager.put("$key", "$value")
                MMKVManager.getString("$key")
            """.trimIndent()
        )
    }

    private fun showStringSetDemo() {
        val key = "demo_string_set"
        val value = setOf("Kotlin", "Java", "Android")
        
        // 写入
        MMKVManager.put(key, value)
        
        // 读取
        val readValue = MMKVManager.getStringSet(key)
        
        showResultDialog(
            "Set<String> 类型演示",
            """
                写入值: $value
                读取值: $readValue
                类型匹配: ${value == readValue}
                
                使用方法:
                val set = setOf("Kotlin", "Java")
                MMKVManager.put("$key", set)
                MMKVManager.getStringSet("$key")
            """.trimIndent()
        )
    }

    private fun showByteArrayDemo() {
        val key = "demo_byte_array"
        val value = byteArrayOf(0x48, 0x65, 0x6C, 0x6C, 0x6F) // "Hello"
        
        // 写入
        MMKVManager.put(key, value)
        
        // 读取
        val readValue = MMKVManager.getByteArray(key)
        val readString = readValue?.toString(Charsets.UTF_8) ?: "null"
        
        showResultDialog(
            "ByteArray 类型演示",
            """
                写入值: ${value.toString(Charsets.UTF_8)}
                读取值: $readString
                类型匹配: ${value.contentEquals(readValue)}
                
                使用方法:
                val bytes = "Hello".toByteArray()
                MMKVManager.put("$key", bytes)
                MMKVManager.getByteArray("$key")
            """.trimIndent()
        )
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
