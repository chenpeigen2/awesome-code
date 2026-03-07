package com.peter.components.demo.provider

import android.content.ContentUris
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.databinding.ActivityProviderAdvancedBinding
import androidx.core.net.toUri

/**
 * 自定义 ContentProvider 操作示例
 * 
 * 知识点：
 * 1. ContentResolver.insert() - 插入数据
 * 2. ContentResolver.query() - 查询数据
 * 3. ContentResolver.update() - 更新数据
 * 4. ContentResolver.delete() - 删除数据
 * 5. ContentObserver - 监听数据变化
 */
class ProviderAdvancedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProviderAdvancedBinding
    private val resultBuilder = StringBuilder()

    // ContentProvider Uri
    private val contentUri: Uri = "content://${UserProvider.AUTHORITY}/users".toUri()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderAdvancedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnInsert.setOnClickListener {
            insertUser()
        }

        binding.btnQuery.setOnClickListener {
            queryUsers()
        }

        binding.btnUpdate.setOnClickListener {
            updateUser()
        }

        binding.btnDelete.setOnClickListener {
            deleteUser()
        }

        binding.btnClear.setOnClickListener {
            clearAllUsers()
        }
    }

    private fun insertUser() {
        val name = binding.etName.text.toString()
        val age = binding.etAge.text.toString().toIntOrNull() ?: 0

        if (name.isEmpty()) {
            appendResult("请输入姓名")
            return
        }

        val values = ContentValues().apply {
            put(UserDbHelper.COLUMN_NAME, name)
            put(UserDbHelper.COLUMN_AGE, age)
            put(UserDbHelper.COLUMN_CREATED_AT, System.currentTimeMillis())
        }

        val uri = contentResolver.insert(contentUri, values)
        if (uri != null) {
            appendResult("插入成功: $uri")
            binding.etName.text?.clear()
            binding.etAge.text?.clear()
        } else {
            appendResult("插入失败")
        }
    }

    private fun queryUsers() {
        appendResult("查询所有用户:")
        
        contentResolver.query(
            contentUri,
            null,
            null,
            null,
            "${UserDbHelper.COLUMN_CREATED_AT} DESC"
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndex(UserDbHelper.COLUMN_ID)
            val nameIndex = cursor.getColumnIndex(UserDbHelper.COLUMN_NAME)
            val ageIndex = cursor.getColumnIndex(UserDbHelper.COLUMN_AGE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val name = cursor.getString(nameIndex)
                val age = cursor.getInt(ageIndex)
                appendResult("  ID: $id, 姓名: $name, 年龄: $age")
            }

            appendResult("共 ${cursor.count} 条记录")
        }
    }

    private fun updateUser() {
        val name = binding.etName.text.toString()
        val age = binding.etAge.text.toString().toIntOrNull() ?: 0

        if (name.isEmpty()) {
            appendResult("请输入姓名")
            return
        }

        val values = ContentValues().apply {
            put(UserDbHelper.COLUMN_AGE, age)
        }

        val count = contentResolver.update(
            contentUri,
            values,
            "${UserDbHelper.COLUMN_NAME} = ?",
            arrayOf(name)
        )

        appendResult("更新了 $count 条记录")
    }

    private fun deleteUser() {
        val name = binding.etName.text.toString()

        if (name.isEmpty()) {
            appendResult("请输入要删除的姓名")
            return
        }

        val count = contentResolver.delete(
            contentUri,
            "${UserDbHelper.COLUMN_NAME} = ?",
            arrayOf(name)
        )

        appendResult("删除了 $count 条记录")
    }

    private fun clearAllUsers() {
        val count = contentResolver.delete(contentUri, null, null)
        appendResult("清空了 $count 条记录")
    }

    private fun appendResult(message: String) {
        resultBuilder.append("$message\n")
        binding.tvResult.text = resultBuilder.toString()
    }
}
