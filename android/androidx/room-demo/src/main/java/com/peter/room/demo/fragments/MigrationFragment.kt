package com.peter.room.demo.fragments

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.peter.room.demo.databinding.FragmentMigrationBinding
import com.peter.room.demo.db.MigrationDemoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 数据库迁移演示 Fragment
 * 逐步演示 Room 数据库从 v1 到 v2 的迁移过程
 */
class MigrationFragment : Fragment() {

    private var _binding: FragmentMigrationBinding? = null
    private val binding get() = _binding!!

    private var currentDb: MigrationDemoDatabase? = null
    private var currentVersion = 0 // 0=未创建, 1=v1, 2=v2

    companion object {
        fun newInstance() = MigrationFragment()

        private const val DB_NAME = "migration_demo_database"
    }

    /**
     * 用于创建 v1 版本数据库的 Helper
     * v1 schema: migration_users(id, name, age, email, createdAt) — 没有 phone 列
     */
    private inner class V1DatabaseHelper : SQLiteOpenHelper(
        requireContext().applicationContext, DB_NAME, null, 1
    ) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """CREATE TABLE migration_users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    age INTEGER NOT NULL,
                    email TEXT NOT NULL,
                    createdAt INTEGER NOT NULL
                )"""
            )
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            // v1 helper, no upgrades needed
        }
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
        updateVersionDisplay()
    }

    private fun setupButtons() {
        binding.btnInsertV1.setOnClickListener { insertV1Data() }
        binding.btnMigrate.setOnClickListener { migrateToV2() }
        binding.btnQueryV2.setOnClickListener { queryV2Data() }
        binding.btnReset.setOnClickListener { resetDatabase() }
    }

    /**
     * 步骤 1: 使用 SQLiteOpenHelper 创建 v1 数据库并插入测试数据
     * v1 表结构没有 phone 列
     */
    private fun insertV1Data() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 关闭已有的 Room 数据库
                closeCurrentDb()

                withContext(Dispatchers.IO) {
                    // 使用 SQLiteOpenHelper 创建 v1 数据库
                    val helper = V1DatabaseHelper()
                    val db = helper.writableDatabase

                    // 插入 v1 测试数据
                    val users = listOf(
                        Triple("张三", 25, "zhangsan@example.com"),
                        Triple("李四", 30, "lisi@example.com"),
                        Triple("王五", 28, "wangwu@example.com")
                    )

                    for ((name, age, email) in users) {
                        val values = ContentValues().apply {
                            put("name", name)
                            put("age", age)
                            put("email", email)
                            put("createdAt", System.currentTimeMillis())
                        }
                        db.insert("migration_users", null, values)
                    }

                    // 验证 PRAGMA user_version = 1
                    val cursor = db.rawQuery("PRAGMA user_version", null)
                    cursor.moveToFirst()
                    val version = cursor.getInt(0)
                    cursor.close()

                    helper.close()

                    withContext(Dispatchers.Main) {
                        currentVersion = 1
                        updateVersionDisplay()
                        showResult(buildString {
                            appendLine("✅ v1 数据插入成功!")
                            appendLine()
                            appendLine("数据库版本: $version")
                            appendLine()
                            appendLine("插入数据 (无 phone 列):")
                            appendLine("  张三, 25, zhangsan@example.com")
                            appendLine("  李四, 30, lisi@example.com")
                            appendLine("  王五, 28, wangwu@example.com")
                            appendLine()
                            appendLine("v1 表结构:")
                            appendLine("  CREATE TABLE migration_users (")
                            appendLine("    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,")
                            appendLine("    name TEXT NOT NULL,")
                            appendLine("    age INTEGER NOT NULL,")
                            appendLine("    email TEXT NOT NULL,")
                            appendLine("    createdAt INTEGER NOT NULL")
                            appendLine("  )")
                            appendLine()
                            appendLine("→ 请点击「执行迁移 (v1→v2)」")
                        })
                    }
                }
            } catch (e: Exception) {
                showResult("❌ 插入失败: ${e.message}")
            }
        }
    }

    /**
     * 步骤 2: 执行 v1 → v2 迁移
     * 关闭 v1 数据库，用 Room 重新打开并执行 Migration(1, 2)
     */
    private fun migrateToV2() {
        if (currentVersion != 1) {
            showResult("⚠️ 请先执行步骤 1「插入 v1 数据」")
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 关闭 v1 helper
                closeCurrentDb()

                withContext(Dispatchers.IO) {
                    // 用 Room 打开数据库，携带 MIGRATION_1_2
                    val db = MigrationDemoDatabase.getV2Database(requireContext())
                    currentDb = db

                    // 触发数据库打开，执行迁移
                    db.migrationUserDao().getAll()

                    // 查询迁移后的数据
                    val users = db.migrationUserDao().getAll()

                    withContext(Dispatchers.Main) {
                        currentVersion = 2
                        updateVersionDisplay()
                        showResult(buildString {
                            appendLine("✅ 迁移成功! (v1 → v2)")
                            appendLine()
                            appendLine("执行的迁移 SQL:")
                            appendLine("  ALTER TABLE migration_users")
                            appendLine("  ADD COLUMN phone TEXT NOT NULL DEFAULT ''")
                            appendLine()
                            appendLine("迁移后数据 (phone 列为空字符串):")
                            for (user in users) {
                                appendLine("  ${user.name}, ${user.age}岁, phone=\"${user.phone}\"")
                            }
                            appendLine()
                            appendLine("v2 表结构:")
                            appendLine("  CREATE TABLE migration_users (")
                            appendLine("    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,")
                            appendLine("    name TEXT NOT NULL,")
                            appendLine("    age INTEGER NOT NULL,")
                            appendLine("    email TEXT NOT NULL,")
                            appendLine("    phone TEXT NOT NULL DEFAULT '',  ← 新增")
                            appendLine("    createdAt INTEGER NOT NULL")
                            appendLine("  )")
                            appendLine()
                            appendLine("→ 请点击「查询 v2 数据」查看完整信息")
                        })
                    }
                }
            } catch (e: Exception) {
                showResult("❌ 迁移失败: ${e.message}\n\n${e.stackTraceToString()}")
            }
        }
    }

    /**
     * 步骤 3: 查询 v2 数据，显示包含 phone 字段的完整数据
     */
    private fun queryV2Data() {
        if (currentVersion != 2) {
            showResult("⚠️ 请先执行步骤 2「执行迁移 (v1→v2)」")
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val db = currentDb ?: run {
                    val newDb = MigrationDemoDatabase.getV2Database(requireContext())
                    currentDb = newDb
                    newDb
                }

                val users = withContext(Dispatchers.IO) {
                    db.migrationUserDao().getAll()
                }

                showResult(buildString {
                    appendLine("📋 v2 数据查询结果:")
                    appendLine()
                    appendLine("┌────┬──────┬─────┬─────────────────────────┬─────────┐")
                    appendLine("│ ID │ Name │ Age │ Email                   │ Phone   │")
                    appendLine("├────┼──────┼─────┼─────────────────────────┼─────────┤")
                    for (user in users) {
                        appendLine("│ %3d │ %-4s │ %3d │ %-23s │ %-7s │".format(
                            user.id, user.name, user.age, user.email, user.phone.ifEmpty { "(空)" }
                        ))
                    }
                    appendLine("└────┴──────┴─────┴─────────────────────────┴─────────┘")
                    appendLine()
                    appendLine("共 ${users.size} 条记录")
                    appendLine()
                    appendLine("说明: phone 列是迁移时新增的字段，")
                    appendLine("已有数据的 phone 值为迁移 SQL 中的默认值 \"\"")
                    appendLine()
                    appendLine("→ 点击「重置数据库」可重新演示")
                })
            } catch (e: Exception) {
                showResult("❌ 查询失败: ${e.message}")
            }
        }
    }

    /**
     * 重置: 删除数据库文件，恢复初始状态
     */
    private fun resetDatabase() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                closeCurrentDb()

                withContext(Dispatchers.IO) {
                    MigrationDemoDatabase.deleteDatabase(requireContext())
                }

                currentVersion = 0
                updateVersionDisplay()
                showResult("🔄 数据库已重置\n\n→ 点击「插入 v1 数据」重新开始演示")
            } catch (e: Exception) {
                showResult("❌ 重置失败: ${e.message}")
            }
        }
    }

    private fun closeCurrentDb() {
        currentDb?.close()
        currentDb = null
    }

    private fun updateVersionDisplay() {
        binding.tvVersion.text = when (currentVersion) {
            0 -> "数据库未创建"
            1 -> "当前版本: v1 (无 phone 列)"
            2 -> "当前版本: v2 (含 phone 列)"
            else -> "未知状态"
        }
    }

    private fun showResult(text: String) {
        binding.tvMigrationResult.text = text
        binding.tvMigrationResult.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        closeCurrentDb()
        super.onDestroyView()
        _binding = null
    }
}
