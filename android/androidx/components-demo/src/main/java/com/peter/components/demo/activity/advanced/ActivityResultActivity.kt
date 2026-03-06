package com.peter.components.demo.activity.advanced

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.peter.components.demo.R
import java.io.File

/**
 * Activity Result API 示例
 *
 * ═══════════════════════════════════════════════════════════════
 * Activity Result API 详解
 * ═══════════════════════════════════════════════════════════════
 *
 * 替代旧版 API：
 * - startActivityForResult + onActivityResult
 * - requestPermissions + onRequestPermissionsResult
 *
 * 优势：
 * 1. 类型安全
 * 2. 代码更简洁
 * 3. 避免 requestCode 管理
 * 4. 与生命周期感知组件配合更好
 *
 * 核心组件：
 * 1. ActivityResultContract<I, O> - 定义输入输出类型
 * 2. ActivityResultLauncher - 启动器
 * 3. registerForActivityResult() - 注册回调
 *
 * 内置 Contract：
 * - StartActivityForResult
 * - RequestPermission
 * - RequestMultiplePermissions
 * - PickContact
 * - TakePicture
 * - TakePicturePreview
 * - GetContent
 * - CreateDocument
 * - OpenDocument
 * - OpenDocumentTree
 */
class ActivityResultActivity : AppCompatActivity() {

    private lateinit var tvResult: TextView

    // 1. 选择联系人
    private val pickContactLauncher = registerForActivityResult(
        ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        uri?.let { queryContactName(it) }
    }

    // 2. 拍照
    private var photoUri: Uri? = null
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && photoUri != null) {
            tvResult.text = "拍照成功\n图片路径: $photoUri"
        } else {
            tvResult.text = "拍照取消或失败"
        }
    }

    // 3. 请求单个权限
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            tvResult.text = "相机权限已授予"
        } else {
            tvResult.text = "相机权限被拒绝"
        }
    }

    // 4. 请求多个权限
    private val requestMultiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions: Map<String, Boolean> ->
        val result = permissions.entries.joinToString("\n") { (permission, granted) ->
            "${permission.substringAfterLast(".")}: ${if (granted) "已授权" else "已拒绝"}"
        }
        tvResult.text = "权限结果:\n$result"
    }

    // 5. 使用 StartActivityForResult
    private val startActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.getStringExtra("result")
            tvResult.text = "返回结果: $data"
        } else {
            tvResult.text = "操作取消"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activity_result)

        tvResult = findViewById(R.id.tvResult)

        findViewById<Button>(R.id.btnPickContact).setOnClickListener {
            pickContactLauncher.launch(null)
        }

        findViewById<Button>(R.id.btnTakePhoto).setOnClickListener {
            takePhoto()
        }

        findViewById<Button>(R.id.btnRequestPermission).setOnClickListener {
            requestCameraPermission()
        }

        findViewById<Button>(R.id.btnCustomContract).setOnClickListener {
            // 演示使用 StartActivityForResult
            val intent = Intent(this, CustomResultActivity::class.java).apply {
                putExtra("input", "发送的数据")
            }
            startActivityLauncher.launch(intent)
        }
    }

    private fun queryContactName(uri: Uri) {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    val name = it.getString(nameIndex)
                    tvResult.text = "选择的联系人: $name"
                }
            }
        }
    }

    private fun takePhoto() {
        // 创建图片文件
        val photoFile = File(cacheDir, "photo_${System.currentTimeMillis()}.jpg")
        photoUri = FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",
            photoFile
        )
        photoUri?.let { takePictureLauncher.launch(it) }
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                tvResult.text = "相机权限已有"
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}

/**
 * 用于演示自定义 Contract 的简单 Activity
 */
class CustomResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val input = intent?.getStringExtra("input")
        val resultIntent = Intent().putExtra("result", "处理后的: $input")
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}

/**
 * 自定义 ActivityResultContract 示例
 *
 * @param I 输入类型
 * @param O 输出类型
 */
class CustomStringContract : ActivityResultContract<String, String>() {

    override fun createIntent(context: Context, input: String): Intent {
        return Intent(context, CustomResultActivity::class.java).apply {
            putExtra("input", input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String {
        return if (resultCode == Activity.RESULT_OK) {
            intent?.getStringExtra("result") ?: "无结果"
        } else {
            "操作取消"
        }
    }
}
