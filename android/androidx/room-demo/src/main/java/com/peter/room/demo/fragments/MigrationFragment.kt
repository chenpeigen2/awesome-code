package com.peter.room.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.peter.room.demo.databinding.FragmentMigrationBinding

/**
 * 数据库迁移演示 Fragment
 */
class MigrationFragment : Fragment() {

    private var _binding: FragmentMigrationBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = MigrationFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMigrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
    }

    private fun setupButtons() {
        binding.btnUpgrade.setOnClickListener {
            showMigrationDemo()
        }
    }

    private fun showMigrationDemo() {
        // 迁移演示说明
        val result = """
            迁移演示说明：
            
            1. 当前数据库版本: 1
            
            2. 迁移步骤：
               - 在 Entity 中添加新字段
               - 更新 Database version
               - 创建 Migration 对象
               - 添加到 Room.databaseBuilder()
            
            3. 代码示例：
               val MIGRATION_1_2 = object : Migration(1, 2) {
                   override fun migrate(db: SupportSQLiteDatabase) {
                       db.execSQL("ALTER TABLE users ADD COLUMN phone TEXT")
                   }
               }
               
            注意：实际迁移需要重新创建数据库实例
        """.trimIndent()
        
        binding.tvMigrationResult.text = result
        binding.tvMigrationResult.visibility = View.VISIBLE
        
        Toast.makeText(requireContext(), "查看迁移说明", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
