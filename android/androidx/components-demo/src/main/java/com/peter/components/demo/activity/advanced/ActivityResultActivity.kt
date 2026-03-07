package com.peter.components.demo.activity.advanced

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.databinding.ActivityActivityResultBinding

/**
 * Activity Result API 示例
 * 
 * 知识点：
 * 1. ActivityResultLauncher - 替代 startActivityForResult
 * 2. 预置 Contract - PickVisualMedia, TakePicture, RequestPermission 等
 * 3. 自定义 Contract - 继承 ActivityResultContract
 * 4. 生命周期安全 - 自动处理 Activity 重建
 */
class ActivityResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActivityResultBinding

    // 选择图片
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            binding.tvResult.text = "选择的图片: $uri"
        } else {
            binding.tvResult.text = "未选择图片"
        }
    }

    // 拍照
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            binding.tvResult.text = "拍照成功"
        } else {
            binding.tvResult.text = "拍照取消"
        }
    }

    // 请求权限
    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            binding.tvResult.text = "权限已授予"
        } else {
            binding.tvResult.text = "权限被拒绝"
        }
    }

    // 自定义 Contract
    private val customContract = registerForActivityResult(CustomStringContract()) { result ->
        binding.tvResult.text = "自定义结果: $result"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPickContent.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnTakePicture.setOnClickListener {
            Toast.makeText(this, "实际项目中需要先创建文件 Uri", Toast.LENGTH_SHORT).show()
        }

        binding.btnRequestPermission.setOnClickListener {
            requestPermission.launch(Manifest.permission.CAMERA)
        }

        binding.btnCustomContract.setOnClickListener {
            customContract.launch("输入数据")
        }
    }
}

/**
 * 自定义 ActivityResultContract 示例
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
            "取消"
        }
    }
}

/**
 * 自定义 Contract 的目标 Activity
 */
class CustomResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val input = intent.getStringExtra("input")
        val result = Intent().apply {
            putExtra("result", "处理后的: $input")
        }
        setResult(Activity.RESULT_OK, result)
        finish()
    }
}