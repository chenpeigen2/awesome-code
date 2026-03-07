package com.peter.components.demo.provider

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.peter.components.demo.databinding.ActivityProviderBasicBinding

/**
 * ContentProvider 基础用法示例
 * 
 * 知识点：
 * 1. ContentResolver - 操作 ContentProvider 的客户端接口
 * 2. Uri - 资源标识符，格式：content://authority/path/id
 * 3. Cursor - 查询结果的游标
 * 4. 系统内置 Provider：联系人、通话记录、日历等
 * 
 * 常用系统 Provider：
 * - ContactsContract - 联系人
 * - CallLog - 通话记录
 * - CalendarContract - 日历
 * - MediaStore - 媒体文件
 * - Settings - 系统设置
 */
class ProviderBasicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProviderBasicBinding

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            queryContacts()
        } else {
            binding.tvResult.text = "权限被拒绝"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderBasicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnQueryContacts.setOnClickListener {
            checkPermissionAndQuery()
        }

        binding.btnQueryCallLog.setOnClickListener {
            binding.tvResult.text = "通话记录查询需要 READ_CALL_LOG 权限\n示例代码与联系人查询类似"
        }

        binding.btnQueryCalendar.setOnClickListener {
            binding.tvResult.text = "日历查询需要 READ_CALENDAR 权限\n示例代码与联系人查询类似"
        }
    }

    private fun checkPermissionAndQuery() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            queryContacts()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    private fun queryContacts() {
        val resultBuilder = StringBuilder()
        resultBuilder.append("查询联系人:\n\n")

        // 使用 ContentResolver 查询
        contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
            ),
            null,
            null,
            "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val name = cursor.getString(nameIndex)
                resultBuilder.append("ID: $id, 姓名: $name\n")
            }

            resultBuilder.append("\n共 ${cursor.count} 条记录")
        }

        binding.tvResult.text = resultBuilder.toString()
    }
}
