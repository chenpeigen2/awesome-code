package com.peter.mmkv.demo.fragments

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.mmkv.demo.*
import com.peter.mmkv.demo.databinding.FragmentAdvancedBinding
import com.tencent.mmkv.MMKV

/**
 * 高级功能 Fragment
 * 演示 MMKV 的高级特性：多实例、加密、迁移、备份等
 */
class AdvancedFragment : Fragment() {

    private var _binding: FragmentAdvancedBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = AdvancedFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdvancedBinding.inflate(inflater, container, false)
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
                feature = MMKVFeature.ADVANCED_MULTI_INSTANCE,
                title = "多实例",
                description = "创建多个独立的 MMKV 实例，分别存储不同模块的数据",
                category = FeatureCategory.ADVANCED
            ),
            FeatureItem(
                feature = MMKVFeature.ADVANCED_ENCRYPT,
                title = "加密存储",
                description = "使用加密密钥保护敏感数据",
                category = FeatureCategory.ADVANCED
            ),
            FeatureItem(
                feature = MMKVFeature.ADVANCED_MIGRATE,
                title = "迁移 SharedPreferences",
                description = "将 SharedPreferences 数据迁移到 MMKV",
                category = FeatureCategory.ADVANCED
            ),
            FeatureItem(
                feature = MMKVFeature.ADVANCED_BACKUP,
                title = "备份与恢复",
                description = "导出和导入存储的数据",
                category = FeatureCategory.ADVANCED
            ),
            FeatureItem(
                feature = MMKVFeature.ADVANCED_CLEAR,
                title = "清空数据",
                description = "清除所有已存储的数据",
                category = FeatureCategory.ADVANCED
            ),
            FeatureItem(
                feature = MMKVFeature.ADVANCED_ALL_KEYS,
                title = "获取所有键",
                description = "查看当前存储的所有键名",
                category = FeatureCategory.ADVANCED
            ),
            FeatureItem(
                feature = MMKVFeature.ADVANCED_SIZE,
                title = "存储大小",
                description = "查看存储空间使用情况",
                category = FeatureCategory.ADVANCED
            )
        )
    }

    private fun handleFeatureClick(feature: MMKVFeature) {
        when (feature) {
            MMKVFeature.ADVANCED_MULTI_INSTANCE -> showMultiInstanceDemo()
            MMKVFeature.ADVANCED_ENCRYPT -> showEncryptDemo()
            MMKVFeature.ADVANCED_MIGRATE -> showMigrateDemo()
            MMKVFeature.ADVANCED_BACKUP -> showBackupDemo()
            MMKVFeature.ADVANCED_CLEAR -> showClearDialog()
            MMKVFeature.ADVANCED_ALL_KEYS -> showAllKeys()
            MMKVFeature.ADVANCED_SIZE -> showSizeInfo()
            else -> {}
        }
    }

    private fun showMultiInstanceDemo() {
        // 创建多个 MMKV 实例
        val userInstance = MMKVManager.getInstance("user_data")
        val settingInstance = MMKVManager.getInstance("app_settings")
        
        // 向不同实例写入数据
        userInstance.encode("username", "张三")
        userInstance.encode("age", 25)
        
        settingInstance.encode("theme", "dark")
        settingInstance.encode("language", "zh-CN")
        
        // 读取数据
        val username = userInstance.decodeString("username", "")
        val theme = settingInstance.decodeString("theme", "")
        
        // 获取所有实例
        val userKeys = userInstance.allKeys()?.toList() ?: emptyList()
        val settingKeys = settingInstance.allKeys()?.toList() ?: emptyList()
        
        showResultDialog(
            "多实例演示",
            """
                === user_data 实例 ===
                键列表: $userKeys
                username: $username
                
                === app_settings 实例 ===
                键列表: $settingKeys
                theme: $theme
                
                使用方法:
                val userMMKV = MMKVManager.getInstance("user_data")
                userMMKV.encode("username", "张三")
                
                优点: 不同模块数据隔离，避免键名冲突
            """.trimIndent()
        )
    }

    private fun showEncryptDemo() {
        val mmapID = "encrypted_data"
        val cryptKey = "my_secret_key_123"
        
        // 获取加密实例
        val encryptedMMKV = MMKVManager.getEncryptedInstance(mmapID, cryptKey)
        
        // 写入敏感数据
        encryptedMMKV.encode("password", "MySecretPassword123")
        encryptedMMKV.encode("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        
        // 读取数据
        val password = encryptedMMKV.decodeString("password", "")
        val token = encryptedMMKV.decodeString("token", "")
        
        showResultDialog(
            "加密存储演示",
            """
                === 加密实例 ===
                实例 ID: $mmapID
                加密密钥: $cryptKey
                
                存储的数据:
                password: $password
                token: ${token?.take(30) ?: "null"}...
                
                使用方法:
                val encryptedMMKV = MMKVManager.getEncryptedInstance(
                    "encrypted_data", 
                    "my_secret_key"
                )
                encryptedMMKV.encode("password", "secret")
                
                注意: 加密密钥需要妥善保管，丢失将无法恢复数据
            """.trimIndent()
        )
    }

    private fun showMigrateDemo() {
        // 创建 SharedPreferences 数据
        val sp = requireContext().getSharedPreferences("old_prefs", Context.MODE_PRIVATE)
        sp.edit().apply {
            putString("name", "李四")
            putInt("score", 100)
            putBoolean("is_vip", true)
            putLong("last_login", System.currentTimeMillis())
        }.apply()
        
        // 迁移前统计
        val spKeys = sp.all.keys
        
        // 执行迁移
        val defaultMMKV = MMKVManager.getDefault()
        val migratedCount = MMKVManager.migrateFromSharedPreferences(sp, defaultMMKV)
        
        // 验证迁移结果
        val name = MMKVManager.getString("name")
        val score = MMKVManager.getInt("score")
        val isVip = MMKVManager.getBoolean("is_vip")
        
        showResultDialog(
            "迁移 SharedPreferences 演示",
            """
                === SharedPreferences 原数据 ===
                键列表: $spKeys
                
                === 迁移结果 ===
                迁移键值对数量: $migratedCount
                
                验证读取:
                name: $name
                score: $score
                is_vip: $isVip
                
                使用方法:
                val sp = getSharedPreferences("old_prefs", ...)
                val count = MMKVManager.migrateFromSharedPreferences(sp)
                
                优点: 无缝迁移 SharedPreferences 数据到 MMKV
            """.trimIndent()
        )
    }

    private fun showBackupDemo() {
        // 先写入一些测试数据
        MMKVManager.put("backup_test_1", "value1")
        MMKVManager.put("backup_test_2", "value2")
        MMKVManager.put("backup_test_3", 12345)
        
        // 导出数据
        val exportedJSON = MMKVManager.exportToJSON()
        
        // 模拟恢复（清空后恢复）
        MMKVManager.remove("backup_test_1", "backup_test_2", "backup_test_3")
        val restoredCount = MMKVManager.importFromJSON(exportedJSON)
        
        showResultDialog(
            "备份与恢复演示",
            """
                === 导出的 JSON 数据 ===
                ${exportedJSON.take(200)}${if (exportedJSON.length > 200) "..." else ""}
                
                === 恢复结果 ===
                恢复键值对数量: $restoredCount
                
                使用方法:
                // 导出
                val json = MMKVManager.exportToJSON()
                
                // 恢复
                val count = MMKVManager.importFromJSON(json)
                
                应用场景: 数据备份、跨设备同步、调试
            """.trimIndent()
        )
    }

    private fun showClearDialog() {
        val keys = MMKVManager.getAllKeys()
        if (keys.isEmpty()) {
            showMessage("当前没有存储任何数据")
            return
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("确认清空")
            .setMessage("确定要清空所有 ${keys.size} 个键值对吗？此操作不可恢复。")
            .setPositiveButton("清空") { _, _ ->
                MMKVManager.clearAll()
                showMessage("已清空所有数据")
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showAllKeys() {
        val keys = MMKVManager.getAllKeys()
        
        if (keys.isEmpty()) {
            showResultDialog("所有键", "当前没有存储任何数据")
        } else {
            val keysList = keys.sorted().joinToString("\n") { "• $it" }
            showResultDialog(
                "所有键 (共 ${keys.size} 个)",
                keysList
            )
        }
    }

    private fun showSizeInfo() {
        val defaultMMKV = MMKVManager.getDefault()
        
        val totalSize = MMKVManager.getTotalSize()
        val actualSize = MMKVManager.getActualSize()
        val keyCount = MMKVManager.getAllKeys().size
        
        // 格式化大小
        fun formatSize(bytes: Long): String {
            return if (bytes < 1024) {
                "$bytes B"
            } else if (bytes < 1024 * 1024) {
                String.format("%.2f KB", bytes / 1024.0)
            } else {
                String.format("%.2f MB", bytes / (1024.0 * 1024))
            }
        }
        
        showResultDialog(
            "存储大小信息",
            """
                总分配空间: ${formatSize(totalSize)}
                实际使用空间: ${formatSize(actualSize)}
                键值对数量: $keyCount
                
                使用率: ${if (totalSize > 0) String.format("%.1f%%", actualSize * 100.0 / totalSize) else "0%"}
                
                使用方法:
                val totalSize = MMKVManager.getTotalSize()
                val actualSize = MMKVManager.getActualSize()
                
                注意: MMKV 使用内存映射文件，空间会动态扩展
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
