package com.peter.coil.demo

import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import coil.load
import com.peter.coil.demo.databinding.ActivityBasicLoadBinding
import java.io.File
import androidx.core.net.toUri

/**
 * Coil 基础图片加载演示
 *
 * 本 Activity 展示 Coil 从不同数据源加载图片：
 * - String (URL): 最常用的网络图片加载方式
 * - Int (Resource ID): 从应用资源加载
 * - File: 从本地文件加载
 * - Uri: 从 Uri 加载
 * - Drawable: 从 Drawable 对象加载
 * - Bitmap: 从 Bitmap 对象加载
 *
 * 核心API：
 *   imageView.load(data) { /* ImageRequest.Builder 配置 */ }
 *
 * Coil 自动识别数据类型，无需手动指定。
 * 底层使用 OkHttp 进行网络请求。
 */
class BasicLoadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBasicLoadBinding
    private val infoBuilder = StringBuilder()
    private val resultBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasicLoadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        showInfo()
        setupClickListeners()
    }

    /**
     * 显示基础信息
     */
    private fun showInfo() {
        infoBuilder.clear()
        infoBuilder.apply {
            append("=== Coil 基础图片加载 ===\n")
            append("\n")
            append("Coil 支持多种数据源：\n")
            append("  * String (URL) - 最常用\n")
            append("  * Int (Resource ID)\n")
            append("  * File - 本地文件\n")
            append("  * Uri - 内容 Uri\n")
            append("  * Drawable\n")
            append("  * Bitmap\n")
            append("\n")
            append("核心 API：\n")
            append("  imageView.load(data) { /* ImageRequest.Builder */ }\n")
            append("\n")
            append("Coil 自动识别数据类型，无需手动指定。\n")
            append("底层使用 OkHttp 进行网络请求。\n")
        }
        binding.tvInfo.text = infoBuilder.toString()
    }

    /**
     * 设置按钮点击事件
     */
    private fun setupClickListeners() {
        binding.btnLoadUrl.setOnClickListener { loadFromUrl() }
        binding.btnLoadResource.setOnClickListener { loadFromResource() }
        binding.btnLoadFile.setOnClickListener { loadFromFile() }
        binding.btnLoadUri.setOnClickListener { loadFromUri() }
        binding.btnLoadDrawable.setOnClickListener { loadFromDrawable() }
        binding.btnLoadBitmap.setOnClickListener { loadFromBitmap() }
    }

    /**
     * 从 URL 加载 - 最常用的方式
     */
    private fun loadFromUrl() {
        resultBuilder.clear()
        resultBuilder.apply {
            append("--- 从 URL 加载 ---\n")
            append("\n")
            append("// 最常用的方式：从 URL 加载\n")
            append("imageView.load(\"https://picsum.photos/400/300\") {\n")
            append("    crossfade(true)\n")
            append("}\n")
            append("\n")
            append("// 等价于\n")
            append("imageView.load(\n")
            append("    ImageRequest.Builder(context)\n")
            append("        .data(\"https://picsum.photos/400/300\")\n")
            append("        .crossfade(true)\n")
            append("        .target(imageView)\n")
            append("        .build()\n")
            append(")\n")
        }
        binding.tvResult.text = resultBuilder.toString()

        // 实际执行加载
        binding.imageView.load("https://picsum.photos/400/300") {
            crossfade(true)
        }
    }

    /**
     * 从 Resource ID 加载
     */
    private fun loadFromResource() {
        resultBuilder.clear()
        resultBuilder.apply {
            append("--- 从 Resource 加载 ---\n")
            append("\n")
            append("// 从资源 ID 加载\n")
            append("imageView.load(R.drawable.ic_launcher_foreground)\n")
        }
        binding.tvResult.text = resultBuilder.toString()

        // 实际执行加载
        binding.imageView.load(R.drawable.ic_launcher_foreground)
    }

    /**
     * 从 File 加载
     */
    private fun loadFromFile() {
        resultBuilder.clear()
        resultBuilder.apply {
            append("--- 从 File 加载 ---\n")
            append("\n")
            append("// 从 File 对象加载\n")
            append("val file = File(cacheDir, \"test_image.jpg\")\n")
            append("imageView.load(file)\n")
            append("\n")
            append("// 说明：需要确保文件存在\n")
            append("// File 对象可以是内部存储、外部存储中的文件\n")
            append("// Coil 会自动读取文件并解码为 Bitmap\n")
        }
        binding.tvResult.text = resultBuilder.toString()

        // 由于可能没有本地文件，使用 Resource 替代演示效果
        val file = File(cacheDir, "test_image.jpg")
        if (file.exists()) {
            binding.imageView.load(file)
        } else {
            // 文件不存在，用 Resource 替代展示
            binding.imageView.load(R.drawable.ic_launcher_foreground)
        }
    }

    /**
     * 从 Uri 加载
     */
    private fun loadFromUri() {
        resultBuilder.clear()
        resultBuilder.apply {
            append("--- 从 Uri 加载 ---\n")
            append("\n")
            append("// 从 Uri 加载\n")
            append("val uri = Uri.parse(\"https://picsum.photos/400/300\")\n")
            append("imageView.load(uri)\n")
        }
        binding.tvResult.text = resultBuilder.toString()

        // 实际执行加载
        val uri = "https://picsum.photos/400/300".toUri()
        binding.imageView.load(uri)
    }

    /**
     * 从 Drawable 加载
     */
    private fun loadFromDrawable() {
        resultBuilder.clear()
        resultBuilder.apply {
            append("--- 从 Drawable 加载 ---\n")
            append("\n")
            append("// 从 Drawable 加载\n")
            append("val drawable = ContextCompat.getDrawable(\n")
            append("    this, R.drawable.ic_launcher_foreground\n")
            append(")\n")
            append("imageView.load(drawable)\n")
        }
        binding.tvResult.text = resultBuilder.toString()

        // 实际执行加载
        val drawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.ic_launcher_foreground)
        if (drawable != null) {
            binding.imageView.load(drawable)
        }
    }

    /**
     * 从 Bitmap 加载
     */
    private fun loadFromBitmap() {
        resultBuilder.clear()
        resultBuilder.apply {
            append("--- 从 Bitmap 加载 ---\n")
            append("\n")
            append("// 从 Bitmap 加载\n")
            append("val bitmap = BitmapFactory.decodeResource(\n")
            append("    resources, R.drawable.ic_launcher_foreground\n")
            append(")\n")
            append("imageView.load(bitmap)\n")
        }
        binding.tvResult.text = resultBuilder.toString()

        // 实际执行加载
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground)
        binding.imageView.load(bitmap)
    }
}
