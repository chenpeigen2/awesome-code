package com.peter.components.demo.provider

import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.R

/**
 * ContentProvider 基础使用示例
 *
 * ═══════════════════════════════════════════════════════════════
 * ContentResolver 使用
 * ═══════════════════════════════════════════════════════════════
 *
 * 通过 ContentResolver 访问 ContentProvider：
 *
 * 获取实例：
 * val resolver = context.contentResolver
 *
 * 查询：
 * resolver.query(uri, projection, selection, args, sortOrder)
 *
 * 插入：
 * resolver.insert(uri, values)
 *
 * 更新：
 * resolver.update(uri, values, selection, args)
 *
 * 删除：
 * resolver.delete(uri, selection, args)
 *
 * 批量操作：
 * resolver.applyBatch(authority, operations)
 * resolver.bulkInsert(uri, values)
 */
class ProviderBasicActivity : AppCompatActivity() {

    private lateinit var tvData: TextView
    private var userIdCounter = 0L

    /**
     * ContentObserver 用于监听数据变化
     */
    private val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            onChange(selfChange, null)
        }

        override fun onChange(selfChange: Boolean, uri: Uri?) {
            // 数据变化时刷新列表
            refreshData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_basic)

        tvData = findViewById(R.id.tvData)

        // 注册 ContentObserver
        contentResolver.registerContentObserver(
            UserProvider.CONTENT_URI,
            true,
            contentObserver
        )

        setupButtons()
        refreshData()

        findViewById<Button>(R.id.btnAdvanced).setOnClickListener {
            startActivity(android.content.Intent(this, ProviderAdvancedActivity::class.java))
        }
    }

    private fun setupButtons() {
        // 插入数据
        findViewById<Button>(R.id.btnInsert).setOnClickListener {
            insertUser()
        }

        // 查询数据
        findViewById<Button>(R.id.btnQuery).setOnClickListener {
            refreshData()
        }

        // 更新数据
        findViewById<Button>(R.id.btnUpdate).setOnClickListener {
            updateUser()
        }

        // 删除数据
        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            deleteUser()
        }

        // 清空所有
        findViewById<Button>(R.id.btnClear).setOnClickListener {
            clearAll()
        }
    }

    /**
     * 插入用户数据
     */
    private fun insertUser() {
        userIdCounter++
        val values = android.content.ContentValues().apply {
            put("name", "用户$userIdCounter")
            put("email", "user$userIdCounter@example.com")
            put("age", (20..50).random())
        }

        val uri = contentResolver.insert(UserProvider.CONTENT_URI, values)
        tvData.append("\n插入成功: $uri")
    }

    /**
     * 查询所有用户
     */
    private fun refreshData() {
        val cursor: Cursor? = contentResolver.query(
            UserProvider.CONTENT_URI,
            arrayOf("_id", "name", "email", "age"),
            null,
            null,
            "_id DESC"
        )

        val sb = StringBuilder("用户列表:\n")
        sb.append("═".repeat(40)).append("\n")

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getLong(0)
                val name = it.getString(1)
                val email = it.getString(2)
                val age = it.getInt(3)
                sb.append("ID: $id\n")
                sb.append("姓名: $name\n")
                sb.append("邮箱: $email\n")
                sb.append("年龄: $age\n")
                sb.append("-".repeat(20)).append("\n")
            }
        }

        tvData.text = sb.toString()
    }

    /**
     * 更新第一个用户
     */
    private fun updateUser() {
        val values = android.content.ContentValues().apply {
            put("name", "已更新的用户")
            put("age", 99)
        }

        val count = contentResolver.update(
            UserProvider.CONTENT_URI,
            values,
            "_id = (SELECT MIN(_id) FROM ${UserProvider.TABLE_USERS})",
            null
        )

        tvData.append("\n更新了 $count 条记录")
    }

    /**
     * 删除最后一个用户
     */
    private fun deleteUser() {
        val count = contentResolver.delete(
            UserProvider.CONTENT_URI,
            "_id = (SELECT MAX(_id) FROM ${UserProvider.TABLE_USERS})",
            null
        )

        tvData.append("\n删除了 $count 条记录")
    }

    /**
     * 清空所有数据
     */
    private fun clearAll() {
        val count = contentResolver.delete(UserProvider.CONTENT_URI, null, null)
        tvData.text = "已清空 $count 条记录"
    }

    override fun onDestroy() {
        super.onDestroy()
        // 注销 ContentObserver
        contentResolver.unregisterContentObserver(contentObserver)
    }
}
