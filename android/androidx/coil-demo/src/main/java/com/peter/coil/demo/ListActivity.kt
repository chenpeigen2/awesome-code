package com.peter.coil.demo

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.dispose
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.peter.coil.demo.databinding.ActivityListBinding
import com.peter.coil.demo.databinding.ItemImageBinding

/**
 * Coil 列表图片加载演示
 *
 * 本 Demo 展示在 RecyclerView 中使用 Coil 的最佳实践：
 *
 * 1. 加载图片列表 — GridLayoutManager 展示多张图片
 * 2. 预加载 — 提前将图片缓存到内存
 * 3. ViewHolder 模式 — 正确的 Coil + RecyclerView 用法
 */
class ListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListBinding
    private val sb = StringBuilder()

    // 测试图片列表
    private val images = (1..30).map { "https://picsum.photos/200/200?random=$it" }

    // 适配器
    private val imageAdapter = ImageAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
        setupListeners()
        showListInfo()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@ListActivity, 3)
            adapter = imageAdapter
        }
    }

    private fun setupListeners() {
        binding.btnLoadList.setOnClickListener { demonstrateLoadList() }
        binding.btnPrefetch.setOnClickListener { demonstratePrefetch() }
        binding.btnVhPattern.setOnClickListener { demonstrateVhPattern() }
    }

    /**
     * 显示列表图片加载概览信息
     */
    private fun showListInfo() {
        sb.clear()
        sb.appendLine("=== Coil 列表图片加载 ===")
        sb.appendLine()
        sb.appendLine("=== RecyclerView 中使用 ===")
        sb.appendLine("1. 在 onBindViewHolder 中调用 load()")
        sb.appendLine("2. onViewRecycled 中调用 dispose()")
        sb.appendLine("3. 设置固定大小避免重新加载")
        sb.appendLine()
        sb.appendLine("=== 关键优化 ===")
        sb.appendLine("• 设置 ImageView 固定尺寸 → 避免重新测量")
        sb.appendLine("• 使用 crossfade(true) → 流畅过渡")
        sb.appendLine("• dispose() → ViewHolder 回收时取消请求")
        sb.appendLine("• 内存缓存自动管理 → 滑动时秒加载")
        sb.appendLine()
        sb.appendLine("=== 预加载 ===")
        sb.appendLine("imageLoader.enqueue(request)")
        sb.appendLine("→ 不设置 target，只缓存到内存")
        sb.appendLine("→ 适合提前加载下一页图片")
        sb.appendLine()
        sb.appendLine("=== 注意事项 ===")
        sb.appendLine("• 不要在 ViewHolder 中创建 ImageLoader")
        sb.appendLine("• 使用 context.imageLoader 单例")
        sb.appendLine("• 占位图使用轻量 Drawable")
        sb.appendLine("• 列表中避免使用 OriginalSize")

        binding.tvInfo.text = sb.toString()
    }

    /**
     * 加载图片列表演示
     *
     * 使用 GridLayoutManager (spanCount=3) 展示 30 张图片。
     * 每个 item 是一个正方形 ImageView。
     */
    private fun demonstrateLoadList() {
        sb.clear()
        sb.appendLine("=== 加载图片列表 ===")
        sb.appendLine()
        sb.appendLine("使用 GridLayoutManager (spanCount=3)")
        sb.appendLine("共 ${images.size} 张图片")
        sb.appendLine()
        sb.appendLine("// 创建图片 URL 列表")
        sb.appendLine("val images = (1..30).map {")
        sb.appendLine("    \"https://picsum.photos/200/200?random=\$it\"")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("// 设置 RecyclerView")
        sb.appendLine("recyclerView.layoutManager = GridLayoutManager(this, 3)")
        sb.appendLine("recyclerView.adapter = ImageAdapter(images)")
        sb.appendLine()

        // 实际加载列表
        imageAdapter.submitList(images)
        binding.recyclerView.scheduleLayoutAnimation()

        sb.appendLine("--- 已加载 ${images.size} 张图片到 RecyclerView ---")
        sb.appendLine()
        sb.appendLine("图片加载特点:")
        sb.appendLine("  • Coil 自动管理内存缓存")
        sb.appendLine("  • 滑动时自动取消不可见 item 的请求")
        sb.appendLine("  • 回滑时从内存缓存秒加载")
        sb.appendLine("  • 内存不足时自动释放缓存")

        binding.tvResult.text = sb.toString()
    }

    /**
     * 预加载演示
     *
     * 展示如何使用 imageLoader.enqueue() 预加载图片到缓存。
     * 不设置 target，只缓存到内存。
     */
    private fun demonstratePrefetch() {
        sb.clear()
        sb.appendLine("=== 预加载 ===")
        sb.appendLine()
        sb.appendLine("// 预加载到缓存")
        sb.appendLine("val imageLoader = context.imageLoader")
        sb.appendLine("images.forEach { url ->")
        sb.appendLine("    val request = ImageRequest.Builder(context)")
        sb.appendLine("        .data(url)")
        sb.appendLine("        .size(200, 200)")
        sb.appendLine("        .memoryCachePolicy(CachePolicy.ENABLED)")
        sb.appendLine("        .build()")
        sb.appendLine("    imageLoader.enqueue(request) // 加入队列但不设置 target")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("=== 预加载说明 ===")
        sb.appendLine()
        sb.appendLine("imageLoader.enqueue(request)")
        sb.appendLine("  → 不设置 target，只缓存到内存/磁盘")
        sb.appendLine("  → 后续 load() 时直接从缓存读取")
        sb.appendLine()
        sb.appendLine("=== 使用场景 ===")
        sb.appendLine()
        sb.appendLine("1. 打开图片画廊前预加载")
        sb.appendLine("   → 先加载缩略图列表，用户点击后秒开大图")
        sb.appendLine()
        sb.appendLine("2. 预加载下一页内容")
        sb.appendLine("   → 列表滑动到底部前预加载下一页图片")
        sb.appendLine("   → 用户无感知，体验流畅")
        sb.appendLine()
        sb.appendLine("3. 应用启动时预加载关键图片")
        sb.appendLine("   → Splash 页面预加载首页图片")
        sb.appendLine("   → 首页展示时从缓存读取")
        sb.appendLine()

        // 实际预加载
        val prefetchImages = (31..40).map { "https://picsum.photos/200/200?random=$it" }
        val imageLoader = coil.Coil.imageLoader(this)
        sb.appendLine("--- 开始预加载 10 张图片 ---")
        sb.appendLine()

        prefetchImages.forEach { url ->
            val request = ImageRequest.Builder(this)
                .data(url)
                .size(200, 200)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build()
            imageLoader.enqueue(request)
        }

        sb.appendLine("已提交 ${prefetchImages.size} 个预加载请求")
        sb.appendLine("这些图片将被缓存到内存和磁盘")
        sb.appendLine("后续 load() 时可直接从缓存读取")

        binding.tvResult.text = sb.toString()
    }

    /**
     * ViewHolder 模式演示
     *
     * 展示在 RecyclerView 中使用 Coil 的最佳实践代码。
     */
    private fun demonstrateVhPattern() {
        sb.clear()
        sb.appendLine("=== ViewHolder 模式 ===")
        sb.appendLine()
        sb.appendLine("// 正确的 Coil + RecyclerView 用法")
        sb.appendLine("class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {")
        sb.appendLine("    private val urls = mutableListOf<String>()")
        sb.appendLine()
        sb.appendLine("    fun submitList(newUrls: List<String>) {")
        sb.appendLine("        urls.clear()")
        sb.appendLine("        urls.addAll(newUrls)")
        sb.appendLine("        notifyDataSetChanged()")
        sb.appendLine("    }")
        sb.appendLine()
        sb.appendLine("    override fun onCreateViewHolder(")
        sb.appendLine("        parent: ViewGroup, viewType: Int")
        sb.appendLine("    ): ViewHolder {")
        sb.appendLine("        val imageView = ImageView(parent.context).apply {")
        sb.appendLine("            layoutParams = ViewGroup.LayoutParams(")
        sb.appendLine("                ViewGroup.LayoutParams.MATCH_PARENT,")
        sb.appendLine("                ViewGroup.LayoutParams.WRAP_CONTENT")
        sb.appendLine("            )")
        sb.appendLine("            scaleType = ImageView.ScaleType.CENTER_CROP")
        sb.appendLine("            // 设置固定尺寸防止重新加载")
        sb.appendLine("        }")
        sb.appendLine("        return ViewHolder(imageView)")
        sb.appendLine("    }")
        sb.appendLine()
        sb.appendLine("    override fun onBindViewHolder(")
        sb.appendLine("        holder: ViewHolder, position: Int")
        sb.appendLine("    ) {")
        sb.appendLine("        holder.imageView.load(urls[position]) {")
        sb.appendLine("            crossfade(true)")
        sb.appendLine("            placeholder(R.drawable.ic_launcher_foreground)")
        sb.appendLine("            // 设置固定大小避免重复测量")
        sb.appendLine("            size(200, 200)")
        sb.appendLine("        }")
        sb.appendLine("    }")
        sb.appendLine()
        sb.appendLine("    override fun onViewRecycled(holder: ViewHolder) {")
        sb.appendLine("        super.onViewRecycled(holder)")
        sb.appendLine("        // 取消正在进行的请求，释放资源")
        sb.appendLine("        holder.imageView.dispose()")
        sb.appendLine("    }")
        sb.appendLine()
        sb.appendLine("    class ViewHolder(val imageView: ImageView)")
        sb.appendLine("        : RecyclerView.ViewHolder(imageView)")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("=== 关键点详解 ===")
        sb.appendLine()
        sb.appendLine("1. onBindViewHolder 中调用 load()")
        sb.appendLine("   → Coil 自动处理复用和取消逻辑")
        sb.appendLine("   → 同一个 ImageView 多次 load() 会自动取消前一次")
        sb.appendLine()
        sb.appendLine("2. onViewRecycled 中调用 dispose()")
        sb.appendLine("   → 释放 ImageView 关联的请求资源")
        sb.appendLine("   → 避免回收的 ViewHolder 持有旧请求")
        sb.appendLine()
        sb.appendLine("3. 设置固定大小 size(200, 200)")
        sb.appendLine("   → 避免因 View 测量导致的重新加载")
        sb.appendLine("   → RecyclerView item 布局不确定时尤为重要")
        sb.appendLine()
        sb.appendLine("4. 使用 placeholder")
        sb.appendLine("   → 显示占位图，提升用户体验")
        sb.appendLine("   → 推荐使用轻量 Drawable（纯色/简单形状）")

        binding.tvResult.text = sb.toString()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    /**
     * 图片列表适配器
     *
     * 使用 Coil 加载图片到 ImageView。
     * 遵循最佳实践：
     * - onBindViewHolder 中 load()
     * - onViewRecycled 中 dispose()
     * - 设置固定大小
     */
    private inner class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

        private val urls = mutableListOf<String>()

        fun submitList(newUrls: List<String>) {
            urls.clear()
            urls.addAll(newUrls)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemImageBinding.inflate(
                layoutInflater,
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.binding.imageView.load(urls[position]) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                size(200, 200)
            }
        }

        override fun onViewRecycled(holder: ViewHolder) {
            super.onViewRecycled(holder)
            // 取消正在进行的请求，释放资源
            holder.binding.imageView.dispose()
        }

        override fun getItemCount(): Int = urls.size

        inner class ViewHolder(val binding: ItemImageBinding) :
            RecyclerView.ViewHolder(binding.root)
    }
}
