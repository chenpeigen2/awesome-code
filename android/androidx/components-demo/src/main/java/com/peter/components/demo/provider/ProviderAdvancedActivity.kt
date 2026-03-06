package com.peter.components.demo.provider

import android.Manifest
import android.content.ContentProviderOperation
import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.peter.components.demo.R

/**
 * ContentProvider 进阶使用示例
 *
 * ═══════════════════════════════════════════════════════════════
 * ContentProvider 进阶特性
 * ═══════════════════════════════════════════════════════════════
 *
 * 1. 批量操作
 *    - applyBatch：批量执行多个操作
 *    - bulkInsert：批量插入
 *
 * 2. ContentObserver
 *    - 监听数据变化
 *    - 自动刷新 UI
 *
 * 3. 系统 ContentProvider
 *    - Contacts：联系人
 *    - MediaStore：媒体文件
 *    - Calendar：日历
 *    - Settings：设置
 *
 * ═══════════════════════════════════════════════════════════════
 * 批量操作优势
 * ═══════════════════════════════════════════════════════════════
 *
 * 1. 性能更好：减少 IPC 次数
 * 2. 原子性：支持事务
 * 3. 批量回滚：失败时可以回滚所有操作
 */
class ProviderAdvancedActivity : AppCompatActivity() {

    private lateinit var tvResult: TextView
    private lateinit var tvObserverStatus: TextView

    private val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            runOnUiThread {
                tvObserverStatus.text = "数据变化通知: ${uri?.lastPathSegment}\n时间: ${System.currentTimeMillis()}"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_advanced)

        tvResult = findViewById(R.id.tvResult)
        tvObserverStatus = findViewById(R.id.tvObserverStatus)

        // 注册观察者
        contentResolver.registerContentObserver(
            UserProvider.CONTENT_URI,
            true,
            contentObserver
        )

        findViewById<Button>(R.id.btnBatchInsert).setOnClickListener {
            batchInsert()
        }

        findViewById<Button>(R.id.btnQueryContacts).setOnClickListener {
            queryContacts()
        }
    }

    /**
     * 批量插入数据
     *
     * 使用 ContentProviderOperation 构建操作队列
     * 通过 applyBatch 一次性执行
     */
    private fun batchInsert() {
        val operations = ArrayList<ContentProviderOperation>()

        // 构建 10 个插入操作
        for (i in 1..10) {
            val op = ContentProviderOperation.newInsert(UserProvider.CONTENT_URI)
                .withValue("name", "批量用户$i")
                .withValue("email", "batch$i@example.com")
                .withValue("age", (20..50).random())
                .build()
            operations.add(op)
        }

        try {
            // 执行批量操作
            val results = contentResolver.applyBatch(UserProvider.AUTHORITY, operations)
            tvResult.text = "批量插入完成\n成功: ${results.size} 条"
        } catch (e: Exception) {
            tvResult.text = "批量插入失败: ${e.message}"
        }
    }

    /**
     * 查询联系人
     *
     * 使用系统 ContentProvider
     * 需要 READ_CONTACTS 权限
     */
    private fun queryContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "需要联系人读取权限", Toast.LENGTH_SHORT).show()
            return
        }

        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME
        )

        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null,
            "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
        )

        val sb = StringBuilder("联系人列表:\n")
        sb.append("═".repeat(30)).append("\n")

        cursor?.use {
            var count = 0
            while (it.moveToNext() && count < 20) {
                val id = it.getLong(0)
                val name = it.getString(1)
                sb.append("$name (ID: $id)\n")
                count++
            }
            sb.append("\n总计: $count 条")
        }

        tvResult.text = sb.toString()
    }

    /**
     * bulkInsert 批量插入
     *
     * 比 applyBatch 更高效
     * 但只支持插入操作
     */
    private fun bulkInsert() {
        val valuesArray = Array(10) { i ->
            ContentValues().apply {
                put("name", "批量用户${i + 1}")
                put("email", "bulk${i + 1}@example.com")
                put("age", (20..50).random())
            }
        }

        val count = contentResolver.bulkInsert(UserProvider.CONTENT_URI, valuesArray)
        tvResult.text = "bulkInsert 完成: $count 条"
    }

    override fun onDestroy() {
        super.onDestroy()
        contentResolver.unregisterContentObserver(contentObserver)
    }
}
