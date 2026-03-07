package com.peter.components.demo.activity.basic

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.databinding.ActivityImplicitIntentBinding

/**
 * 隐式 Intent 示例
 * 
 * 知识点：
 * 1. Intent Filter 匹配规则 (action, category, data)
 * 2. 系统常用隐式 Intent (网页、拨号、分享)
 * 3. 自定义 Scheme 跳转
 * 4. Intent.createChooser() 创建选择器
 */
class ImplicitIntentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImplicitIntentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImplicitIntentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 打开网页
        binding.btnOpenWeb.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.android.com"))
            startActivity(intent)
        }

        // 拨打电话
        binding.btnDial.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:10086"))
            startActivity(intent)
        }

        // 分享文本
        binding.btnShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "分享的内容：Android 四大组件 Demo")
            }
            startActivity(Intent.createChooser(intent, "选择分享应用"))
        }

        // 自定义 Scheme 跳转
        binding.btnCustomScheme.setOnClickListener {
            try {
                // 对应 AndroidManifest.xml 中的 Intent Filter
                // <data android:scheme="components" android:host="demo" />
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("components://demo"))
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "没有找到匹配的 Activity", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
