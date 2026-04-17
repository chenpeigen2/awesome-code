package com.peter.coil.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.peter.coil.demo.databinding.ActivityPlaceholderBinding

/**
 * Coil 占位图与过渡效果演示
 *
 * 本 Demo 展示 Coil 的三种占位图和过渡效果：
 *
 * 1. placeholder - 加载中显示的占位图
 * 2. error       - 加载失败时显示的错误图
 * 3. fallback    - data 为 null 时显示的后备图
 * 4. crossfade   - 淡入过渡效果
 */
class PlaceholderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaceholderBinding
    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceholderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showPlaceholderInfo()
    }

    private fun setupListeners() {
        binding.btnPlaceholder.setOnClickListener { demonstratePlaceholder() }
        binding.btnError.setOnClickListener { demonstrateError() }
        binding.btnFallback.setOnClickListener { demonstrateFallback() }
        binding.btnCrossfade.setOnClickListener { demonstrateCrossfade() }
        binding.btnComplete.setOnClickListener { demonstrateComplete() }
    }

    /**
     * 显示占位图概览信息
     */
    private fun showPlaceholderInfo() {
        sb.clear()
        sb.appendLine("=== Coil 占位图与过渡 ===")
        sb.appendLine()
        sb.appendLine("=== 三种占位图 ===")
        sb.appendLine("placeholder - 加载中显示的图片")
        sb.appendLine("error       - 加载失败显示的图片")
        sb.appendLine("fallback    - data 为 null 时显示的图片")
        sb.appendLine()
        sb.appendLine("=== 过渡效果 ===")
        sb.appendLine("crossfade(true)    - 启用淡入（默认 300ms）")
        sb.appendLine("crossfade(500)     - 自定义淡入时长")
        sb.appendLine("crossfade(true, allowCrossfadeBetweenCallbacks = true)")
        sb.appendLine()
        sb.appendLine("=== 优先级 ===")
        sb.appendLine("1. 如果 data 为 null → 显示 fallback")
        sb.appendLine("2. 正在加载 → 显示 placeholder")
        sb.appendLine("3. 加载成功 → 显示实际图片（带 crossfade）")
        sb.appendLine("4. 加载失败 → 显示 error")
        sb.appendLine()
        sb.appendLine("=== 支持的类型 ===")
        sb.appendLine("占位图支持: Drawable, Int (resource id), Bitmap 等")

        binding.tvInfo.text = sb.toString()
    }

    /**
     * Placeholder 占位图演示
     *
     * 加载时显示占位图，加载完成后替换为实际图片。
     * 使用大图让加载过程更明显。
     */
    private fun demonstratePlaceholder() {
        sb.clear()
        sb.appendLine("=== Placeholder 占位图 ===")
        sb.appendLine()

        sb.appendLine("=== 代码 ===")
        sb.appendLine()
        sb.appendLine("// 加载时显示的占位图")
        sb.appendLine("imageView.load(\"https://picsum.photos/2000/2000\") {")
        sb.appendLine("    placeholder(R.drawable.ic_launcher_foreground)")
        sb.appendLine("    // 加载中显示 ic_launcher_foreground")
        sb.appendLine("}")
        sb.appendLine()

        sb.appendLine("=== 说明 ===")
        sb.appendLine("placeholder 在图片开始加载时立即显示")
        sb.appendLine("加载成功后自动替换为实际图片")
        sb.appendLine("如果命中内存缓存，placeholder 可能不会显示")
        sb.appendLine()

        // 使用大图让 placeholder 可见时间更长
        sb.appendLine("=== 执行: 加载大图 ===")
        binding.imageView.load("https://picsum.photos/2000/2000") {
            placeholder(R.drawable.ic_launcher_foreground)
            listener(
                onStart = {
                    sb.appendLine("开始加载 → 显示 placeholder")
                    binding.tvResult.text = sb.toString()
                },
                onSuccess = { request, metadata ->
                    sb.appendLine("加载成功 → 显示实际图片")
                    sb.appendLine("Memory 缓存: ${metadata.memoryCacheKey != null}")
                    sb.appendLine("Disk 缓存: ${metadata.diskCacheKey != null}")
                    binding.tvResult.text = sb.toString()
                },
                onError = { request, throwable ->
                    sb.appendLine("加载失败: ${throwable.throwable.message}")
                    binding.tvResult.text = sb.toString()
                }
            )
        }

        binding.tvResult.text = sb.toString()
    }

    /**
     * Error 错误图演示
     *
     * 加载失败时显示指定的错误占位图。
     */
    private fun demonstrateError() {
        sb.clear()
        sb.appendLine("=== Error 错误图 ===")
        sb.appendLine()

        sb.appendLine("=== 代码 ===")
        sb.appendLine()
        sb.appendLine("// 加载失败时显示的错误图")
        sb.appendLine("imageView.load(\"https://invalid-url.com/image.jpg\") {")
        sb.appendLine("    placeholder(R.drawable.ic_launcher_foreground)")
        sb.appendLine("    error(android.R.drawable.ic_menu_report_image)")
        sb.appendLine("    // 加载失败显示 error drawable")
        sb.appendLine("}")
        sb.appendLine()

        sb.appendLine("=== 说明 ===")
        sb.appendLine("error 在图片加载失败时显示")
        sb.appendLine("常见的失败原因:")
        sb.appendLine("  • URL 无效或服务器不可达")
        sb.appendLine("  • 网络连接异常")
        sb.appendLine("  • 图片数据损坏")
        sb.appendLine()

        // 使用无效 URL 触发错误
        sb.appendLine("=== 执行: 加载无效 URL ===")
        binding.imageView.load("https://invalid-url-that-does-not-exist.com/image.jpg") {
            placeholder(R.drawable.ic_launcher_foreground)
            error(android.R.drawable.ic_menu_report_image)
            listener(
                onStart = {
                    sb.appendLine("开始加载 → 显示 placeholder")
                    binding.tvResult.text = sb.toString()
                },
                onSuccess = { request, metadata ->
                    sb.appendLine("加载成功（未预期的结果）")
                    binding.tvResult.text = sb.toString()
                },
                onError = { request, throwable ->
                    sb.appendLine("加载失败 → 显示 error 占位图")
                    sb.appendLine("错误信息: ${throwable.throwable.javaClass.simpleName}")
                    sb.appendLine("错误原因: ${throwable.throwable.message}")
                    binding.tvResult.text = sb.toString()
                }
            )
        }

        binding.tvResult.text = sb.toString()
    }

    /**
     * Fallback 后备图演示
     *
     * 当 data 为 null 时显示后备图。
     */
    private fun demonstrateFallback() {
        sb.clear()
        sb.appendLine("=== Fallback 后备图 ===")
        sb.appendLine()

        sb.appendLine("=== 代码 ===")
        sb.appendLine()
        sb.appendLine("// data 为 null 时显示的后备图")
        sb.appendLine("imageView.load(null as String?) {")
        sb.appendLine("    fallback(R.drawable.ic_launcher_foreground)")
        sb.appendLine("    // data 为 null 时显示 fallback")
        sb.appendLine("}")
        sb.appendLine()

        sb.appendLine("=== 说明 ===")
        sb.appendLine("fallback 在 data (图片数据源) 为 null 时显示")
        sb.appendLine("与 placeholder 和 error 的区别:")
        sb.appendLine("  placeholder - 加载过程中显示")
        sb.appendLine("  error       - 加载失败时显示")
        sb.appendLine("  fallback    - 数据源为 null 时显示（不发起加载）")
        sb.appendLine()
        sb.appendLine("适用场景:")
        sb.appendLine("  • 用户头像 URL 可能为 null")
        sb.appendLine("  • 商品图片可能未上传")
        sb.appendLine("  • 用户未设置封面图")
        sb.appendLine()

        // 使用 null 作为 data
        sb.appendLine("=== 执行: 加载 null ===")
        binding.imageView.load(null as String?) {
            fallback(R.drawable.ic_launcher_foreground)
        }
        sb.appendLine("已设置 fallback → 显示后备图")

        binding.tvResult.text = sb.toString()
    }

    /**
     * Crossfade 过渡效果演示
     *
     * 展示淡入过渡效果，从占位图到实际图片的平滑过渡。
     */
    private fun demonstrateCrossfade() {
        sb.clear()
        sb.appendLine("=== Crossfade 过渡效果 ===")
        sb.appendLine()

        sb.appendLine("=== 代码 ===")
        sb.appendLine()
        sb.appendLine("// 淡入过渡效果")
        sb.appendLine("imageView.load(\"https://picsum.photos/400/300\") {")
        sb.appendLine("    crossfade(true)")
        sb.appendLine("    // 等价于 crossfade(300) 默认 300ms")
        sb.appendLine("    crossfade(500) // 自定义时长")
        sb.appendLine("}")
        sb.appendLine()

        sb.appendLine("=== 说明 ===")
        sb.appendLine("crossfade() 在图片加载完成后")
        sb.appendLine("从当前显示内容淡入到新图片")
        sb.appendLine()
        sb.appendLine("参数:")
        sb.appendLine("  crossfade(true)   - 启用，默认 300ms")
        sb.appendLine("  crossfade(500)    - 启用，自定义 500ms")
        sb.appendLine("  crossfade(1000)   - 更慢的淡入效果")
        sb.appendLine()

        // 使用带时间戳的 URL 避免缓存
        val timestamp = System.currentTimeMillis()
        val url = "https://picsum.photos/400/300?random=$timestamp"
        sb.appendLine("=== 执行: 带 Crossfade 的加载 ===")

        binding.imageView.load(url) {
            crossfade(500)
            placeholder(R.drawable.ic_launcher_foreground)
            listener(
                onStart = {
                    sb.appendLine("开始加载 → 显示 placeholder")
                    binding.tvResult.text = sb.toString()
                },
                onSuccess = { request, metadata ->
                    sb.appendLine("加载成功 → Crossfade 500ms 淡入")
                    sb.appendLine("观察 ImageView 的过渡动画效果")
                    binding.tvResult.text = sb.toString()
                },
                onError = { request, throwable ->
                    sb.appendLine("加载失败: ${throwable.throwable.message}")
                    binding.tvResult.text = sb.toString()
                }
            )
        }

        binding.tvResult.text = sb.toString()
    }

    /**
     * 完整示例演示
     *
     * 综合使用所有占位图和过渡效果。
     */
    private fun demonstrateComplete() {
        sb.clear()
        sb.appendLine("=== 完整示例 ===")
        sb.appendLine()

        sb.appendLine("=== 代码 ===")
        sb.appendLine()
        sb.appendLine("// 综合使用所有占位图和过渡")
        sb.appendLine("imageView.load(\"https://picsum.photos/400/300\") {")
        sb.appendLine("    placeholder(R.drawable.ic_launcher_foreground)   // 加载中")
        sb.appendLine("    error(android.R.drawable.ic_menu_report_image)    // 失败")
        sb.appendLine("    fallback(R.drawable.ic_launcher_foreground)       // null")
        sb.appendLine("    crossfade(500)                                     // 淡入 500ms")
        sb.appendLine("}")
        sb.appendLine()

        sb.appendLine("=== 三种占位图配合使用 ===")
        sb.appendLine("1. 加载开始 → 显示 placeholder")
        sb.appendLine("2. 加载成功 → crossfade 显示实际图片")
        sb.appendLine("3. 加载失败 → 显示 error")
        sb.appendLine("4. data 为 null → 显示 fallback")
        sb.appendLine()
        sb.appendLine("=== 注意事项 ===")
        sb.appendLine("• crossfade 只在从非 null drawable 过渡时生效")
        sb.appendLine("• 如果没有设置 placeholder，crossfade 从空白淡入")
        sb.appendLine("• error 和 fallback 可以是不同的 drawable")
        sb.appendLine("• 所有占位图都不会被缓存")
        sb.appendLine()

        // 综合演示
        val timestamp = System.currentTimeMillis()
        val url = "https://picsum.photos/400/300?random=$timestamp"
        sb.appendLine("=== 执行: 完整配置加载 ===")

        binding.imageView.load(url) {
            placeholder(R.drawable.ic_launcher_foreground)
            error(android.R.drawable.ic_menu_report_image)
            fallback(R.drawable.ic_launcher_foreground)
            crossfade(500)
            listener(
                onStart = {
                    sb.appendLine("开始加载 → 显示 placeholder (ic_launcher_foreground)")
                    binding.tvResult.text = sb.toString()
                },
                onSuccess = { request, metadata ->
                    sb.appendLine("加载成功 → Crossfade 500ms 淡入到实际图片")
                    sb.appendLine("Memory 缓存: ${metadata.memoryCacheKey != null}")
                    sb.appendLine("Disk 缓存: ${metadata.diskCacheKey != null}")
                    binding.tvResult.text = sb.toString()
                },
                onError = { request, throwable ->
                    sb.appendLine("加载失败 → 显示 error (ic_menu_report_image)")
                    sb.appendLine("错误: ${throwable.throwable.message}")
                    binding.tvResult.text = sb.toString()
                }
            )
        }

        binding.tvResult.text = sb.toString()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
