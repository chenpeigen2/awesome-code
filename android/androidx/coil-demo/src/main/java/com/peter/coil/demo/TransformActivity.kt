package com.peter.coil.demo

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.size.Size
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import coil.transform.Transformation
import com.peter.coil.demo.databinding.ActivityTransformBinding

/**
 * Coil 图片变换演示
 *
 * 本 Activity 展示 Coil 的图片变换功能：
 * - CircleCropTransformation: 圆形裁剪
 * - RoundedCornersTransformation: 圆角变换
 * - 自定义 Transformation: 模糊、灰度等
 * - 组合变换: 同时应用多个变换
 *
 * 使用方式：
 *   imageView.load(url) {
 *       transformations(CircleCropTransformation())
 *   }
 *
 * 自定义变换需要继承 Transformation 接口。
 */
class TransformActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransformBinding
    private val resultBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransformBinding.inflate(layoutInflater)
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
        val info = StringBuilder().apply {
            append("=== Coil 图片变换 ===\n")
            append("\n")
            append("=== 内置变换 ===\n")
            append("  * CircleCropTransformation - 圆形裁剪\n")
            append("  * RoundedCornersTransformation - 圆角\n")
            append("\n")
            append("=== 使用方式 ===\n")
            append("  imageView.load(url) {\n")
            append("      transformations(CircleCropTransformation())\n")
            append("  }\n")
            append("\n")
            append("=== 组合变换 ===\n")
            append("  imageView.load(url) {\n")
            append("      transformations(\n")
            append("          RoundedCornersTransformation(20f),\n")
            append("          CircleCropTransformation()\n")
            append("      )\n")
            append("  }\n")
            append("\n")
            append("=== 自定义变换 ===\n")
            append("继承 Transformation 接口实现自定义效果：\n")
            append("  class CustomTransformation : Transformation {\n")
            append("      override val cacheKey = \"custom\"\n")
            append("      override suspend fun transform(\n")
            append("          pool: BitmapPool, input: Bitmap, size: Size\n")
            append("      ): Bitmap {\n")
            append("          // 使用 Canvas + Paint 处理 Bitmap\n")
            append("          return output\n")
            append("      }\n")
            append("  }\n")
        }.toString()
        binding.tvInfo.text = info
    }

    /**
     * 设置按钮点击事件
     */
    private fun setupClickListeners() {
        binding.btnCircleCrop.setOnClickListener { circleCrop() }
        binding.btnRoundedCorners.setOnClickListener { roundedCorners() }
        binding.btnBlur.setOnClickListener { blur() }
        binding.btnGrayscale.setOnClickListener { grayscale() }
        binding.btnMultiple.setOnClickListener { multiple() }
    }

    /**
     * 圆形裁剪
     */
    private fun circleCrop() {
        resultBuilder.clear()
        resultBuilder.apply {
            append("--- 圆形裁剪 CircleCropTransformation ---\n")
            append("\n")
            append("listOf(imageView1, imageView2, imageView3).forEach { iv ->\n")
            append("    iv.load(\"https://picsum.photos/200/200\") {\n")
            append("        transformations(CircleCropTransformation())\n")
            append("        crossfade(true)\n")
            append("    }\n")
            append("}\n")
            append("\n")
            append("// CircleCropTransformation 将图片裁剪为圆形\n")
            append("// 常用于头像显示\n")
        }
        binding.tvResult.text = resultBuilder.toString()

        listOf(binding.imageView1, binding.imageView2, binding.imageView3).forEach { iv ->
            iv.load("https://picsum.photos/200/200") {
                transformations(CircleCropTransformation())
                crossfade(true)
            }
        }
    }

    /**
     * 圆角变换 - 展示不同圆角半径
     */
    private fun roundedCorners() {
        resultBuilder.clear()
        resultBuilder.apply {
            append("--- 圆角 RoundedCornersTransformation ---\n")
            append("\n")
            append("// 不同圆角半径\n")
            append("imageView1: transformations(RoundedCornersTransformation(10f))\n")
            append("imageView2: transformations(RoundedCornersTransformation(20f))\n")
            append("imageView3: transformations(RoundedCornersTransformation(40f))\n")
            append("\n")
            append("// RoundedCornersTransformation 参数：\n")
            append("//  topLeft, topRight, bottomLeft, bottomRight\n")
            append("// 可分别设置四个角的圆角半径\n")
        }
        binding.tvResult.text = resultBuilder.toString()

        val radiusList = listOf(10f, 20f, 40f)
        val imageViews = listOf(binding.imageView1, binding.imageView2, binding.imageView3)
        imageViews.forEachIndexed { index, iv ->
            iv.load("https://picsum.photos/200/200") {
                transformations(RoundedCornersTransformation(radiusList[index]))
                crossfade(true)
            }
        }
    }

    /**
     * 模糊变换 - Coil 2.x 没有内置，需要自定义或使用第三方库
     */
    private fun blur() {
        resultBuilder.clear()
        resultBuilder.apply {
            append("--- 模糊 Blur ---\n")
            append("\n")
            append("// Coil 2.x 没有内置 Blur 变换\n")
            append("// 可以通过以下方式实现：\n")
            append("\n")
            append("// 方式1: 使用第三方库 coil-transformations\n")
            append("// implementation(\"jp.wasabeef:coil-transformations:xxx\")\n")
            append("// transformations(BlurTransformation(context, radius))\n")
            append("\n")
            append("// 方式2: 自定义 Transformation\n")
            append("// 使用 RenderScript 或 Toolkit Blur\n")
            append("class BlurTransformation(\n")
            append("    private val radius: Float = 25f\n")
            append(") : Transformation {\n")
            append("    override val cacheKey = \"blur_\$radius\"\n")
            append("    override suspend fun transform(\n")
            append("        pool: BitmapPool, input: Bitmap, size: Size\n")
            append("    ): Bitmap {\n")
            append("        // 使用 RenderScript 或\n")
            append("        // Android 12+ RenderEffect 处理\n")
            append("        return blurredBitmap\n")
            append("    }\n")
            append("}\n")
        }
        binding.tvResult.text = resultBuilder.toString()

        // 加载普通图片作为对比
        listOf(binding.imageView1, binding.imageView2, binding.imageView3).forEach { iv ->
            iv.load("https://picsum.photos/200/200") {
                crossfade(true)
            }
        }
    }

    /**
     * 灰度变换 - Coil 2.x 没有内置，需要自定义
     */
    private fun grayscale() {
        resultBuilder.clear()
        resultBuilder.apply {
            append("--- 灰度 Grayscale ---\n")
            append("\n")
            append("// Coil 2.x 没有内置 Grayscale 变换\n")
            append("// 自定义实现示例：\n")
            append("\n")
            append("class GrayscaleTransformation : Transformation {\n")
            append("    override val cacheKey = \"grayscale\"\n")
            append("    override suspend fun transform(\n")
            append("        pool: BitmapPool, input: Bitmap, size: Size\n")
            append("    ): Bitmap {\n")
            append("        val output = pool.get(\n")
            append("            input.width, input.height, input.config\n")
            append("        )\n")
            append("        val canvas = Canvas(output)\n")
            append("        val paint = Paint()\n")
            append("        val matrix = ColorMatrix().apply {\n")
            append("            setSaturation(0f)\n")
            append("        }\n")
            append("        paint.colorFilter =\n")
            append("            ColorMatrixColorFilter(matrix)\n")
            append("        canvas.drawBitmap(input, 0f, 0f, paint)\n")
            append("        return output\n")
            append("    }\n")
            append("}\n")
            append("\n")
            append("// 使用方式：\n")
            append("// transformations(GrayscaleTransformation())\n")
        }
        binding.tvResult.text = resultBuilder.toString()

        // 使用自定义灰度变换加载
        listOf(binding.imageView1, binding.imageView2, binding.imageView3).forEach { iv ->
            iv.load("https://picsum.photos/200/200") {
                transformations(GrayscaleTransformation())
                crossfade(true)
            }
        }
    }

    /**
     * 组合变换
     */
    private fun multiple() {
        resultBuilder.clear()
        resultBuilder.apply {
            append("--- 组合变换 ---\n")
            append("\n")
            append("// transformations() 接受 vararg 参数\n")
            append("// 可以同时应用多个变换\n")
            append("\n")
            append("imageView.load(url) {\n")
            append("    transformations(\n")
            append("        RoundedCornersTransformation(20f),\n")
            append("        CircleCropTransformation()\n")
            append("    )\n")
            append("}\n")
            append("\n")
            append("// 注意：变换按顺序依次应用\n")
            append("// 先应用 RoundedCorners 再应用 CircleCrop\n")
        }
        binding.tvResult.text = resultBuilder.toString()

        listOf(binding.imageView1, binding.imageView2, binding.imageView3).forEach { iv ->
            iv.load("https://picsum.photos/200/200") {
                transformations(
                    RoundedCornersTransformation(20f),
                    CircleCropTransformation()
                )
                crossfade(true)
            }
        }
    }

    /**
     * 自定义灰度变换
     */
    private class GrayscaleTransformation : Transformation {
        override val cacheKey: String = "grayscale"

        override suspend fun transform(input: Bitmap, size: Size): Bitmap {
            val output = Bitmap.createBitmap(input.width, input.height, input.config ?: Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)
            val paint = Paint()
            val matrix = ColorMatrix().apply { setSaturation(0f) }
            paint.colorFilter = ColorMatrixColorFilter(matrix)
            canvas.drawBitmap(input, 0f, 0f, paint)
            return output
        }
    }
}
