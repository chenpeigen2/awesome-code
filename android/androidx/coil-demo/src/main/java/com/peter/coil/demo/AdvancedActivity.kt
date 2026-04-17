package com.peter.coil.demo

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.size.Scale
import com.peter.coil.demo.databinding.ActivityAdvancedBinding

/**
 * Coil 高级用法演示
 *
 * 本 Demo 展示 Coil 的高级功能：
 *
 * 1. 自定义 ImageLoader — OkHttp 集成、缓存配置
 * 2. Listener 监听 — 加载状态回调
 * 3. 图片大小控制 — OriginalSize、指定尺寸、View 尺寸
 * 4. 缩放类型 — Scale.FILL、Scale.FIT
 * 5. 请求头和参数 — OkHttp Interceptor
 */
class AdvancedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdvancedBinding
    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdvancedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showAdvancedInfo()
    }

    private fun setupListeners() {
        binding.btnCustomLoader.setOnClickListener { demonstrateCustomLoader() }
        binding.btnListener.setOnClickListener { demonstrateListener() }
        binding.btnSize.setOnClickListener { demonstrateSize() }
        binding.btnScale.setOnClickListener { demonstrateScale() }
        binding.btnHeaders.setOnClickListener { demonstrateHeaders() }
    }

    /**
     * 显示高级用法概览信息
     */
    private fun showAdvancedInfo() {
        sb.clear()
        sb.appendLine("=== Coil 高级用法 ===")
        sb.appendLine()
        sb.appendLine("=== ImageLoader ===")
        sb.appendLine("ImageLoader 是 Coil 的核心组件")
        sb.appendLine("负责创建和管理 ImageRequest")
        sb.appendLine("默认通过 context.imageLoader 获取单例")
        sb.appendLine()
        sb.appendLine("=== 自定义 ImageLoader ===")
        sb.appendLine("Application.onCreate() 中:")
        sb.appendLine("  val imageLoader = ImageLoader.Builder(this)")
        sb.appendLine("      .crossfade(true)")
        sb.appendLine("      .okHttpClient { ... }")
        sb.appendLine("      .diskCache { ... }")
        sb.appendLine("      .memoryCache { ... }")
        sb.appendLine("      .build()")
        sb.appendLine("  Coil.setImageLoader(imageLoader)")
        sb.appendLine()
        sb.appendLine("=== Listener ===")
        sb.appendLine("onStart    → 开始加载")
        sb.appendLine("onSuccess  → 加载成功（含数据源信息）")
        sb.appendLine("onError    → 加载失败")
        sb.appendLine("onCancel   → 加载取消")
        sb.appendLine()
        sb.appendLine("=== 图片大小 ===")
        sb.appendLine("默认使用 ImageView 的实际尺寸")
        sb.appendLine("size(OriginalSize)  → 原始尺寸")
        sb.appendLine("size(200, 200)      → 指定尺寸")
        sb.appendLine()
        sb.appendLine("=== 数据源 ===")
        sb.appendLine("metadata.source 可判断来源:")
        sb.appendLine("  SOURCE_MEMORY_CACHE → 内存缓存")
        sb.appendLine("  SOURCE_DISK_CACHE   → 磁盘缓存")
        sb.appendLine("  SOURCE_NETWORK      → 网络")

        binding.tvInfo.text = sb.toString()
    }

    /**
     * 自定义 ImageLoader 演示
     *
     * 展示如何创建自定义 ImageLoader：
     * - 配置 OkHttp 客户端（超时、拦截器）
     * - 配置磁盘缓存（目录、大小）
     * - 配置内存缓存（百分比）
     * - 添加自定义组件
     */
    private fun demonstrateCustomLoader() {
        sb.clear()
        sb.appendLine("=== 自定义 ImageLoader ===")
        sb.appendLine()
        sb.appendLine("// 在 Application.onCreate() 中配置")
        sb.appendLine("val imageLoader = ImageLoader.Builder(this)")
        sb.appendLine("    .crossfade(true)")
        sb.appendLine("    .okHttpClient {")
        sb.appendLine("        OkHttpClient.Builder()")
        sb.appendLine("            .connectTimeout(30, TimeUnit.SECONDS)")
        sb.appendLine("            .addInterceptor { chain ->")
        sb.appendLine("                // 自定义拦截器（添加 header 等）")
        sb.appendLine("                val request = chain.request().newBuilder()")
        sb.appendLine("                    .addHeader(\"User-Agent\", \"CoilDemo/1.0\")")
        sb.appendLine("                    .build()")
        sb.appendLine("                chain.proceed(request)")
        sb.appendLine("            }")
        sb.appendLine("            .build()")
        sb.appendLine("    }")
        sb.appendLine("    .diskCache {")
        sb.appendLine("        DiskCache.Builder()")
        sb.appendLine("            .directory(cacheDir.resolve(\"coil_cache\"))")
        sb.appendLine("            .maxSizeBytes(256L * 1024 * 1024) // 256MB")
        sb.appendLine("            .build()")
        sb.appendLine("    }")
        sb.appendLine("    .memoryCache {")
        sb.appendLine("        MemoryCache.Builder(this)")
        sb.appendLine("            .maxSizePercent(0.25) // 25% 可用内存")
        sb.appendLine("            .build()")
        sb.appendLine("    }")
        sb.appendLine("    .components {")
        sb.appendLine("        // 可以添加自定义组件")
        sb.appendLine("    }")
        sb.appendLine("    .build()")
        sb.appendLine()
        sb.appendLine("// 设置为全局 ImageLoader")
        sb.appendLine("Coil.setImageLoader(imageLoader)")
        sb.appendLine()
        sb.appendLine("=== 配置说明 ===")
        sb.appendLine()
        sb.appendLine("crossfade(true)")
        sb.appendLine("  → 启用淡入动画，加载完成时平滑过渡")
        sb.appendLine()
        sb.appendLine("okHttpClient { ... }")
        sb.appendLine("  → 自定义 OkHttp 客户端")
        sb.appendLine("  → 可配置超时时间、拦截器、SSL 等")
        sb.appendLine("  → Coil 底层使用 OkHttp 进行网络请求")
        sb.appendLine()
        sb.appendLine("diskCache { ... }")
        sb.appendLine("  → 配置磁盘缓存")
        sb.appendLine("  → directory: 缓存目录")
        sb.appendLine("  → maxSizeBytes: 最大缓存大小（建议 256MB）")
        sb.appendLine()
        sb.appendLine("memoryCache { ... }")
        sb.appendLine("  → 配置内存缓存")
        sb.appendLine("  → maxSizePercent: 使用可用内存的百分比")
        sb.appendLine("  → 默认约为可用内存的 25%")
        sb.appendLine()
        sb.appendLine("components { ... }")
        sb.appendLine("  → 添加自定义组件")
        sb.appendLine("  → 如自定义解码器、Fetcher、Keyer 等")
        sb.appendLine()
        sb.appendLine("注意：不要在 Activity 中创建 ImageLoader")
        sb.appendLine("应在 Application 中创建并设置为全局单例")

        binding.tvResult.text = sb.toString()
    }

    /**
     * Listener 监听演示
     *
     * 展示 Coil 的加载状态回调：
     * - onStart: 开始加载
     * - onSuccess: 加载成功（含数据源信息）
     * - onError: 加载失败
     * - onCancel: 加载取消
     */
    private fun demonstrateListener() {
        sb.clear()
        sb.appendLine("=== Listener 监听 ===")
        sb.appendLine()
        sb.appendLine("// 加载图片并监听所有状态")
        sb.appendLine("imageView.load(\"https://picsum.photos/400/300\") {")
        sb.appendLine("    crossfade(true)")
        sb.appendLine("    listener(")
        sb.appendLine("        onStart = { request -> ... },")
        sb.appendLine("        onSuccess = { request, metadata -> ... },")
        sb.appendLine("        onError = { request, throwable -> ... },")
        sb.appendLine("        onCancel = { request -> ... }")
        sb.appendLine("    )")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("--- 开始实际加载 ---")
        sb.appendLine()

        binding.tvResult.text = sb.toString()

        // 实际执行加载
        val timestamp = System.currentTimeMillis()
        binding.imageView.load("https://picsum.photos/400/300?random=$timestamp") {
            crossfade(true)
            listener(
                onStart = { request ->
                    sb.appendLine("[onStart] 开始加载...")
                    sb.appendLine("[onStart] 数据: ${request.data}")
                    binding.tvResult.text = sb.toString()
                },
                onSuccess = { request, metadata ->
                    sb.appendLine("[onSuccess] 加载成功!")
                    sb.appendLine("[onSuccess] 数据源: ${metadata.memoryCacheKey != null}")
                    sb.appendLine()
                    sb.appendLine("=== 数据源说明 ===")
                    sb.appendLine("SOURCE_MEMORY_CACHE → 从内存缓存加载")
                    sb.appendLine("SOURCE_DISK_CACHE   → 从磁盘缓存加载")
                    sb.appendLine("SOURCE_NETWORK      → 从网络加载")
                    sb.appendLine()
                    sb.appendLine("当前数据源: ${metadata.memoryCacheKey != null}")
                    binding.tvResult.text = sb.toString()
                },
                onError = { request, throwable ->
                    sb.appendLine("[onError] 加载失败: ${throwable.throwable.message}")
                    binding.tvResult.text = sb.toString()
                },
                onCancel = { request ->
                    sb.appendLine("[onCancel] 加载取消")
                    binding.tvResult.text = sb.toString()
                }
            )
        }
    }

    /**
     * 图片大小控制演示
     *
     * 展示 Coil 中控制图片大小的三种方式：
     * - OriginalSize: 原始尺寸（注意内存占用）
     * - 指定具体大小: size(200, 200)
     * - View 大小: 默认使用 ImageView 的尺寸
     */
    private fun demonstrateSize() {
        sb.clear()
        sb.appendLine("=== 图片大小控制 ===")
        sb.appendLine()
        sb.appendLine("// 方式1: 指定原始大小")
        sb.appendLine("imageView.load(url) {")
        sb.appendLine("    size(OriginalSize) // 原始尺寸")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("// 方式2: 指定具体大小")
        sb.appendLine("imageView.load(url) {")
        sb.appendLine("    size(200, 200) // 200x200 px")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("// 方式3: 使用 View 大小（默认）")
        sb.appendLine("imageView.load(url) {")
        sb.appendLine("    size(Size(200, 200))")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("// Coil 默认使用 ImageView 的尺寸")
        sb.appendLine("// 这就是为什么 RecyclerView 中要设置固定大小")
        sb.appendLine()
        sb.appendLine("=== 大小控制说明 ===")
        sb.appendLine()
        sb.appendLine("OriginalSize")
        sb.appendLine("  → 使用图片的原始尺寸")
        sb.appendLine("  → 警告：大图会占用大量内存")
        sb.appendLine("  → 仅在需要完整图片时使用")
        sb.appendLine()
        sb.appendLine("size(width, height)")
        sb.appendLine("  → 指定具体的像素大小")
        sb.appendLine("  → Coil 会自动缩放图片到指定尺寸")
        sb.appendLine("  → 减少内存占用")
        sb.appendLine()
        sb.appendLine("默认（不指定 size）")
        sb.appendLine("  → 使用 ImageView 的实际测量尺寸")
        sb.appendLine("  → 如果 ImageView 尚未测量完成，使用原始尺寸")
        sb.appendLine("  → RecyclerView 中建议设置固定大小")
        sb.appendLine()

        // 实际演示：加载不同大小的图片
        val timestamp = System.currentTimeMillis()
        binding.imageView.load("https://picsum.photos/400/300?random=$timestamp") {
            size(200, 200)
            crossfade(true)
        }

        sb.appendLine("--- 实际加载 size(200, 200) ---")
        sb.appendLine("ImageView 实际尺寸: 200dp x 200dp")

        binding.tvResult.text = sb.toString()
    }

    /**
     * 缩放类型演示
     *
     * 展示 Coil 的 Scale 与 ImageView 的 scaleType 的配合：
     * - Scale.FILL: 填充目标
     * - Scale.FIT: 适应目标（保持比例）
     */
    private fun demonstrateScale() {
        sb.clear()
        sb.appendLine("=== 缩放类型 ===")
        sb.appendLine()
        sb.appendLine("// Coil 的 Scale 和 ImageView 的 scaleType 配合")
        sb.appendLine("imageView.scaleType = ImageView.ScaleType.CENTER_CROP // 最常用")
        sb.appendLine("imageView.load(url) {")
        sb.appendLine("    // scale() 可以覆盖 ImageView 的 scaleType")
        sb.appendLine("    scale(Scale.FILL)     // 填充目标")
        sb.appendLine("    scale(Scale.FIT)      // 适应目标（保持比例）")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("=== Scale 说明 ===")
        sb.appendLine()
        sb.appendLine("Scale.FILL")
        sb.appendLine("  → 缩放图片使其完全填充目标区域")
        sb.appendLine("  → 图片可能被裁剪")
        sb.appendLine("  → 对应 ImageView.ScaleType.CENTER_CROP")
        sb.appendLine()
        sb.appendLine("Scale.FIT")
        sb.appendLine("  → 缩放图片使其完全可见")
        sb.appendLine("  → 保持宽高比，可能留白")
        sb.appendLine("  → 对应 ImageView.ScaleType.FIT_CENTER")
        sb.appendLine()
        sb.appendLine("=== 注意事项 ===")
        sb.appendLine()
        sb.appendLine("1. scale() 在 ImageRequest 中设置")
        sb.appendLine("   → 会覆盖 ImageView 的 scaleType（仅对 Coil 加载生效）")
        sb.appendLine()
        sb.appendLine("2. 如果不设置 scale()")
        sb.appendLine("   → Coil 根据 ImageView 的 scaleType 自动选择")
        sb.appendLine("   → CENTER_CROP → Scale.FILL")
        sb.appendLine("   → 其他 → Scale.FIT")
        sb.appendLine()
        sb.appendLine("3. 推荐：在 XML 中设置 scaleType")
        sb.appendLine("   → 让 Coil 自动选择对应的 Scale")

        // 实际演示
        val timestamp = System.currentTimeMillis()
        binding.imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        binding.imageView.load("https://picsum.photos/400/300?random=$timestamp") {
            crossfade(true)
            scale(Scale.FILL)
        }

        sb.appendLine()
        sb.appendLine("--- 实际加载 Scale.FILL ---")
        sb.appendLine("ImageView scaleType = CENTER_CROP")
        sb.appendLine("Coil scale = Scale.FILL")

        binding.tvResult.text = sb.toString()
    }

    /**
     * 请求头和参数演示
     *
     * 展示如何通过 OkHttp Interceptor 添加自定义请求头：
     * - 全局 Interceptor（在 ImageLoader 中配置）
     * - 应用拦截器（Interceptor）
     * - 自定义缓存 Key
     */
    private fun demonstrateHeaders() {
        sb.clear()
        sb.appendLine("=== 请求头和参数 ===")
        sb.appendLine()
        sb.appendLine("// 通过 OkHttp Interceptor 添加 header")
        sb.appendLine("// 方式1: 全局配置（在自定义 ImageLoader 中）")
        sb.appendLine("val imageLoader = ImageLoader.Builder(context)")
        sb.appendLine("    .okHttpClient {")
        sb.appendLine("        OkHttpClient.Builder()")
        sb.appendLine("            .addInterceptor { chain ->")
        sb.appendLine("                val request = chain.request().newBuilder()")
        sb.appendLine("                    .addHeader(\"User-Agent\", \"MyApp/1.0\")")
        sb.appendLine("                    .addHeader(\"Authorization\", \"Bearer token\")")
        sb.appendLine("                    .addHeader(\"Accept\", \"image/webp\")")
        sb.appendLine("                    .build()")
        sb.appendLine("                chain.proceed(request)")
        sb.appendLine("            }")
        sb.appendLine("            .build()")
        sb.appendLine("    }")
        sb.appendLine("    .build()")
        sb.appendLine()
        sb.appendLine("// 方式2: 在 Application 中配置全局 ImageLoader")
        sb.appendLine("class MyApplication : Application() {")
        sb.appendLine("    override fun onCreate() {")
        sb.appendLine("        super.onCreate()")
        sb.appendLine("        val imageLoader = ImageLoader.Builder(this)")
        sb.appendLine("            .okHttpClient {")
        sb.appendLine("                OkHttpClient.Builder()")
        sb.appendLine("                    .addInterceptor { chain ->")
        sb.appendLine("                        chain.request().newBuilder()")
        sb.appendLine("                            .addHeader(\"User-Agent\", \"CoilDemo/1.0\")")
        sb.appendLine("                            .build()")
        sb.appendLine("                            .let { chain.proceed(it) }")
        sb.appendLine("                    }")
        sb.appendLine("                    .build()")
        sb.appendLine("            }")
        sb.appendLine("            .build()")
        sb.appendLine("        Coil.setImageLoader(imageLoader)")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("=== 常用请求头 ===")
        sb.appendLine()
        sb.appendLine("User-Agent")
        sb.appendLine("  → 标识客户端信息")
        sb.appendLine("  → 某些 CDN 需要合法的 UA")
        sb.appendLine()
        sb.appendLine("Authorization")
        sb.appendLine("  → 访问需要认证的图片资源")
        sb.appendLine("  → Bearer token / Basic auth")
        sb.appendLine()
        sb.appendLine("Accept")
        sb.appendLine("  → 指定接受的图片格式")
        sb.appendLine("  → image/webp, image/png 等")
        sb.appendLine()
        sb.appendLine("Cache-Control")
        sb.appendLine("  → 控制 HTTP 缓存行为")
        sb.appendLine("  → no-cache, max-age 等")
        sb.appendLine()
        sb.appendLine("=== 注意事项 ===")
        sb.appendLine()
        sb.appendLine("1. OkHttp Interceptor 影响所有 Coil 请求")
        sb.appendLine("2. 不同图片源需要不同 header 时，可创建多个 ImageLoader")
        sb.appendLine("3. 请求头中的缓存控制与 Coil 的缓存策略独立")

        binding.tvResult.text = sb.toString()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
