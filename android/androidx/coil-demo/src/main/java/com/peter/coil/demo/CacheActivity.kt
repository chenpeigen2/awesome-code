package com.peter.coil.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.imageLoader
import coil.load
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.peter.coil.demo.databinding.ActivityCacheBinding

/**
 * Coil 缓存策略演示
 *
 * 本 Demo 展示 Coil 的两级缓存机制：
 *
 * 1. Memory Cache (LRU) - 内存缓存，存储解码后的 Bitmap
 * 2. Disk Cache - 磁盘缓存，存储原始图片数据
 *
 * 以及四种缓存策略：
 * - CachePolicy.ENABLED    - 读写缓存（默认）
 * - CachePolicy.DISABLED   - 不使用缓存
 * - CachePolicy.READ_ONLY  - 只读缓存
 * - CachePolicy.WRITE_ONLY - 只写缓存
 */
class CacheActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCacheBinding
    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCacheBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showCacheInfo()
    }

    private fun setupListeners() {
        binding.btnMemoryCache.setOnClickListener { demonstrateMemoryCache() }
        binding.btnDiskCache.setOnClickListener { demonstrateDiskCache() }
        binding.btnCachePolicy.setOnClickListener { demonstrateCachePolicy() }
        binding.btnClearCache.setOnClickListener { demonstrateClearCache() }
        binding.btnCacheStats.setOnClickListener { demonstrateCacheStats() }
    }

    /**
     * 显示缓存概览信息
     */
    private fun showCacheInfo() {
        sb.clear()
        sb.appendLine("=== Coil 缓存策略 ===")
        sb.appendLine()
        sb.appendLine("=== 两级缓存 ===")
        sb.appendLine("1. Memory Cache (LRU) - 内存缓存，速度快")
        sb.appendLine("   • 存储解码后的 Bitmap")
        sb.appendLine("   • 应用退出后清空")
        sb.appendLine("   • 默认最大 25% 应用可用内存")
        sb.appendLine()
        sb.appendLine("2. Disk Cache - 磁盘缓存")
        sb.appendLine("   • 存储原始图片数据")
        sb.appendLine("   • 应用重启后仍可用")
        sb.appendLine("   • 默认最大 50MB")
        sb.appendLine()
        sb.appendLine("=== 缓存策略 ===")
        sb.appendLine("  memoryCachePolicy - 控制 Memory 缓存")
        sb.appendLine("  diskCachePolicy - 控制 Disk 缓存")
        sb.appendLine()
        sb.appendLine("  CachePolicy.ENABLED    - 读写（默认）")
        sb.appendLine("  CachePolicy.DISABLED   - 不使用")
        sb.appendLine("  CachePolicy.READ_ONLY  - 只读")
        sb.appendLine("  CachePolicy.WRITE_ONLY - 只写")
        sb.appendLine()
        sb.appendLine("=== 缓存 Key ===")
        sb.appendLine("Coil 根据 URL + 参数生成缓存 Key")
        sb.appendLine("相同的 URL + 相同的变换 = 相同的 Key")

        binding.tvInfo.text = sb.toString()
    }

    /**
     * Memory 缓存演示
     *
     * 加载同一张图片两次，对比耗时差异。
     * 第一次从网络加载，第二次从内存缓存加载。
     */
    private fun demonstrateMemoryCache() {
        sb.clear()
        sb.appendLine("=== Memory 缓存演示 ===")
        sb.appendLine()

        val url = "https://picsum.photos/400/300"

        sb.appendLine("=== 代码 ===")
        sb.appendLine()
        sb.appendLine("// 第一次加载（网络）")
        sb.appendLine("val start1 = System.currentTimeMillis()")
        sb.appendLine("imageView.load(url) {")
        sb.appendLine("    memoryCachePolicy(CachePolicy.ENABLED)")
        sb.appendLine("    listener(onSuccess = { request, metadata ->")
        sb.appendLine("        val time = System.currentTimeMillis() - start1")
        sb.appendLine("        sb.appendLine(\"首次加载耗时: \${time}ms\")")
        sb.appendLine("    })")
        sb.appendLine("}")
        sb.appendLine()

        sb.appendLine("=== CachePolicy 说明 ===")
        sb.appendLine("CachePolicy.ENABLED    - 允许读写缓存")
        sb.appendLine("CachePolicy.DISABLED   - 完全禁用缓存")
        sb.appendLine("CachePolicy.READ_ONLY  - 只从缓存读，不写入")
        sb.appendLine("CachePolicy.WRITE_ONLY - 只写入缓存，不读取")
        sb.appendLine()

        // 第一次加载（网络）
        sb.appendLine("=== 执行加载 ===")
        val start1 = System.currentTimeMillis()
        binding.imageView.load(url) {
            memoryCachePolicy(CachePolicy.ENABLED)
            listener(onSuccess = { request, metadata ->
                val time1 = System.currentTimeMillis() - start1
                sb.appendLine("首次加载耗时: ${time1}ms")
                sb.appendLine("缓存来源: ${metadata.memoryCacheKey != null}")

                // 第二次加载（从内存缓存）
                val start2 = System.currentTimeMillis()
                binding.imageView.load(url) {
                    memoryCachePolicy(CachePolicy.ENABLED)
                    listener(onSuccess = { request2, metadata2 ->
                        val time2 = System.currentTimeMillis() - start2
                        sb.appendLine("第二次加载耗时: ${time2}ms")
                        sb.appendLine("缓存来源: ${metadata2.memoryCacheKey != null}")
                        sb.appendLine()
                        if (time1 > 0) {
                            sb.appendLine("=== 结论 ===")
                            sb.appendLine("首次加载: ${time1}ms（网络请求）")
                            sb.appendLine("二次加载: ${time2}ms（内存缓存）")
                            sb.appendLine("速度提升约 ${time1 / maxOf(time2, 1)} 倍")
                        }
                        binding.tvResult.text = sb.toString()
                    })
                }
            })
        }

        binding.tvResult.text = sb.toString()
    }

    /**
     * Disk 缓存演示
     *
     * 展示磁盘缓存的配置方式和工作原理。
     */
    private fun demonstrateDiskCache() {
        sb.clear()
        sb.appendLine("=== Disk 缓存演示 ===")
        sb.appendLine()

        sb.appendLine("=== 磁盘缓存配置 ===")
        sb.appendLine()
        sb.appendLine("val imageLoader = ImageLoader.Builder(context)")
        sb.appendLine("    .diskCache {")
        sb.appendLine("        DiskCache.Builder()")
        sb.appendLine("            .directory(context.cacheDir.resolve(\"image_cache\"))")
        sb.appendLine("            .maxSizeBytes(512L * 1024 * 1024) // 512MB")
        sb.appendLine("            .build()")
        sb.appendLine("    }")
        sb.appendLine("    .build()")
        sb.appendLine("Coil.setImageLoader(imageLoader)")
        sb.appendLine()

        sb.appendLine("=== 当前 ImageLoader 配置 ===")
        val loader = imageLoader
        val diskCache = loader.diskCache
        sb.appendLine("DiskCache: ${if (diskCache != null) "已配置" else "未配置"}")
        if (diskCache != null) {
            sb.appendLine("DiskCache 目录: ${diskCache.directory}")
            sb.appendLine("DiskCache 最大: ${diskCache.maxSize / 1024 / 1024}MB")
        }
        sb.appendLine()

        // 加载图片，触发磁盘缓存写入
        val timestamp = System.currentTimeMillis()
        val url = "https://picsum.photos/400/300?random=$timestamp"
        sb.appendLine("=== 加载图片 ===")
        sb.appendLine("URL: $url")
        sb.appendLine()

        binding.imageView.load(url) {
            diskCachePolicy(CachePolicy.ENABLED)
            listener(
                onStart = {
                    sb.appendLine("开始加载...")
                    binding.tvResult.text = sb.toString()
                },
                onSuccess = { request, metadata ->
                    sb.appendLine("加载成功！")
                    sb.appendLine("Disk 缓存 Key: ${metadata.diskCacheKey}")
                    sb.appendLine("Memory 缓存 Key: ${metadata.memoryCacheKey}")
                    sb.appendLine()
                    sb.appendLine("=== Disk 缓存工作原理 ===")
                    sb.appendLine("1. 首次加载: 网络 → 写入磁盘缓存 → 显示")
                    sb.appendLine("2. 再次加载: 读取磁盘缓存 → 解码 → 显示")
                    sb.appendLine("3. 内存缓存未命中时才查磁盘缓存")
                    sb.appendLine()
                    sb.appendLine("=== 配置要点 ===")
                    sb.appendLine("• directory() - 缓存目录")
                    sb.appendLine("• maxSizeBytes() - 最大容量")
                    sb.appendLine("• DiskCache 使用 LRU 策略自动清理")

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
     * 缓存策略演示
     *
     * 展示所有 CachePolicy 选项及其使用场景。
     */
    private fun demonstrateCachePolicy() {
        sb.clear()
        sb.appendLine("=== 缓存策略详解 ===")
        sb.appendLine()

        sb.appendLine("=== 所有 CachePolicy 选项 ===")
        sb.appendLine()
        sb.appendLine("CachePolicy.ENABLED    - 读写缓存（默认）")
        sb.appendLine("  正常情况使用，兼顾速度和流量")
        sb.appendLine()
        sb.appendLine("CachePolicy.DISABLED   - 不使用缓存")
        sb.appendLine("  适用场景: 需要实时更新的图片（如验证码）")
        sb.appendLine()
        sb.appendLine("CachePolicy.READ_ONLY  - 只读（不写入新缓存）")
        sb.appendLine("  适用场景: 使用已有缓存但不产生新缓存")
        sb.appendLine()
        sb.appendLine("CachePolicy.WRITE_ONLY - 只写（不从缓存读取）")
        sb.appendLine("  适用场景: 预加载图片到缓存中")
        sb.appendLine()

        sb.appendLine("=== 代码示例 ===")
        sb.appendLine()
        sb.appendLine("// 默认策略（读写都启用）")
        sb.appendLine("imageView.load(url) {")
        sb.appendLine("    memoryCachePolicy(CachePolicy.ENABLED)")
        sb.appendLine("    diskCachePolicy(CachePolicy.ENABLED)")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("// 完全禁用缓存（每次从网络加载）")
        sb.appendLine("imageView.load(url) {")
        sb.appendLine("    memoryCachePolicy(CachePolicy.DISABLED)")
        sb.appendLine("    diskCachePolicy(CachePolicy.DISABLED)")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("// 只读缓存（不写入新缓存）")
        sb.appendLine("imageView.load(url) {")
        sb.appendLine("    memoryCachePolicy(CachePolicy.READ_ONLY)")
        sb.appendLine("    diskCachePolicy(CachePolicy.READ_ONLY)")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("// 只写缓存（预加载场景）")
        sb.appendLine("imageView.load(url) {")
        sb.appendLine("    memoryCachePolicy(CachePolicy.WRITE_ONLY)")
        sb.appendLine("    diskCachePolicy(CachePolicy.WRITE_ONLY)")
        sb.appendLine("}")
        sb.appendLine()

        // 实际演示: 禁用缓存加载
        val timestamp = System.currentTimeMillis()
        val url = "https://picsum.photos/400/300?random=$timestamp"
        sb.appendLine("=== 实测: 禁用缓存加载 ===")
        val start = System.currentTimeMillis()
        binding.imageView.load(url) {
            memoryCachePolicy(CachePolicy.DISABLED)
            diskCachePolicy(CachePolicy.DISABLED)
            listener(
                onSuccess = { request, metadata ->
                    val time = System.currentTimeMillis() - start
                    sb.appendLine("禁用缓存加载耗时: ${time}ms")
                    sb.appendLine("Memory Cache Key: ${metadata.memoryCacheKey}")
                    sb.appendLine("Disk Cache Key: ${metadata.diskCacheKey}")
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
     * 清除缓存演示
     *
     * 展示如何手动清除 Memory 和 Disk 缓存。
     */
    private fun demonstrateClearCache() {
        sb.clear()
        sb.appendLine("=== 清除缓存 ===")
        sb.appendLine()

        sb.appendLine("=== 代码 ===")
        sb.appendLine()
        sb.appendLine("// 清除 Memory 缓存")
        sb.appendLine("imageLoader.memoryCache.clear()")
        sb.appendLine()
        sb.appendLine("// 清除 Disk 缓存")
        sb.appendLine("imageLoader.diskCache?.clear()")
        sb.appendLine()

        // 记录清除前的缓存状态
        val memoryCache = imageLoader.memoryCache
        val diskCache = imageLoader.diskCache

        sb.appendLine("=== 清除前 ===")
        sb.appendLine("Memory 缓存大小: ${memoryCache?.size ?: "N/A"}")
        sb.appendLine("Disk 缓存: ${diskCache != null}")
        sb.appendLine()

        // 执行清除
        memoryCache?.clear()
        diskCache?.clear()

        sb.appendLine("=== 清除后 ===")
        sb.appendLine("Memory 缓存大小: ${memoryCache?.size ?: "N/A"}")
        sb.appendLine("Disk 缓存: 已清除")
        sb.appendLine()
        sb.appendLine("=== 结论 ===")
        sb.appendLine("memoryCache.clear() - 清空所有内存缓存")
        sb.appendLine("diskCache?.clear()  - 清空所有磁盘缓存")
        sb.appendLine("清除后下次加载将重新从网络获取")

        binding.tvResult.text = sb.toString()
    }

    /**
     * 缓存状态演示
     *
     * 展示如何获取当前缓存的使用情况。
     */
    private fun demonstrateCacheStats() {
        sb.clear()
        sb.appendLine("=== 缓存状态 ===")
        sb.appendLine()

        sb.appendLine("=== 代码 ===")
        sb.appendLine()
        sb.appendLine("val memoryCache = imageLoader.memoryCache")
        sb.appendLine("val diskCache = imageLoader.diskCache")
        sb.appendLine("sb.appendLine(\"Memory 缓存大小: \${memoryCache.size}\")")
        sb.appendLine("sb.appendLine(\"Memory 缓存最大: \${memoryCache.maxSize}\")")
        sb.appendLine("sb.appendLine(\"Disk 缓存: \${diskCache != null}\")")
        sb.appendLine()

        sb.appendLine("=== 当前缓存状态 ===")
        val memoryCache = imageLoader.memoryCache
        val diskCache = imageLoader.diskCache

        memoryCache?.let { mc ->
            sb.appendLine("Memory 缓存大小: ${mc.size}")
            sb.appendLine("Memory 缓存最大: ${mc.maxSize}")
            sb.appendLine("Memory 使用率: ${if (mc.maxSize > 0) "${mc.size * 100 / mc.maxSize}%" else "N/A"}")
        } ?: sb.appendLine("Memory 缓存: 未配置")
        sb.appendLine()
        sb.appendLine("Disk 缓存: ${diskCache != null}")
        diskCache?.let { dc ->
            sb.appendLine("Disk 缓存目录: ${dc.directory}")
            sb.appendLine("Disk 缓存最大: ${dc.maxSize / 1024 / 1024}MB")
        }
        sb.appendLine()
        sb.appendLine("=== ImageLoader 信息 ===")
        sb.appendLine("ImageLoader 类型: ${imageLoader.javaClass.simpleName}")
        sb.appendLine("Memory Cache 类型: ${memoryCache?.javaClass?.simpleName ?: "null"}")

        binding.tvResult.text = sb.toString()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
