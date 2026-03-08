package com.peter.animation.demo.viewanimation

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.peter.animation.demo.R
import com.peter.animation.demo.databinding.ActivityFrameAnimationBinding

/**
 * Frame帧动画演示
 *
 * 展示AnimationDrawable的使用：
 * - Loading动画
 * - 颜色渐变动画
 * - 形状变换动画
 *
 * 关键知识点：
 * - AnimationDrawable
 * - XML vs Java代码定义
 * - oneShot vs 循环播放
 * - 内存优化考虑
 */
class FrameAnimationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFrameAnimationBinding

    private var currentAnimationDrawable: AnimationDrawable? = null
    private var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFrameAnimationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupAnimationSelector()
        setupControls()
        updateCodeHint()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupAnimationSelector() {
        binding.rbLoading.isChecked = true
        loadAnimation(R.drawable.anim_loading)
    }

    private fun setupControls() {
        binding.rbLoading.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                stopAnimation()
                loadAnimation(R.drawable.anim_loading)
                updateCodeHint()
            }
        }

        binding.rbColorChange.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                stopAnimation()
                loadAnimation(R.drawable.anim_color_change)
                updateCodeHint()
            }
        }

        binding.rbShapeChange.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                stopAnimation()
                loadAnimation(R.drawable.anim_shape_change)
                updateCodeHint()
            }
        }

        binding.cbOneShot.setOnCheckedChangeListener { _, isChecked ->
            // 需要重新加载动画来改变oneShot设置
            val animRes = when {
                binding.rbLoading.isChecked -> R.drawable.anim_loading
                binding.rbColorChange.isChecked -> R.drawable.anim_color_change
                else -> R.drawable.anim_shape_change
            }
            stopAnimation()
            loadAnimation(animRes)
            updateCodeHint()
        }

        binding.btnStart.setOnClickListener {
            startAnimation()
        }

        binding.btnStop.setOnClickListener {
            stopAnimation()
        }

        binding.btnReset.setOnClickListener {
            resetAnimation()
        }
    }

    private fun loadAnimation(animRes: Int) {
        val drawable = ContextCompat.getDrawable(this, animRes)
        if (drawable is AnimationDrawable) {
            currentAnimationDrawable = drawable
            binding.ivAnimationTarget.setImageDrawable(drawable)
        }
    }

    private fun startAnimation() {
        currentAnimationDrawable?.let { anim ->
            if (!isRunning) {
                // Note: oneShot cannot be changed at runtime, it's set in XML
                // anim.oneShot = binding.cbOneShot.isChecked
                anim.start()
                isRunning = true
                updateStatus("播放中")
            }
        }
    }

    private fun stopAnimation() {
        currentAnimationDrawable?.stop()
        isRunning = false
        updateStatus("已停止")
    }

    private fun resetAnimation() {
        stopAnimation()

        // 重新加载动画
        val animRes = when {
            binding.rbLoading.isChecked -> R.drawable.anim_loading
            binding.rbColorChange.isChecked -> R.drawable.anim_color_change
            else -> R.drawable.anim_shape_change
        }
        loadAnimation(animRes)
        updateStatus("已重置")
    }

    private fun updateStatus(status: String) {
        binding.tvStatus.text = "状态: $status"
    }

    private fun updateCodeHint() {
        val animType = when {
            binding.rbLoading.isChecked -> "Loading"
            binding.rbColorChange.isChecked -> "ColorChange"
            else -> "ShapeChange"
        }

        val code = buildString {
            appendLine("// Frame Animation ($animType) 示例代码")
            appendLine()

            appendLine("// XML 定义 (res/drawable/anim_${animType.lowercase()}.xml)")
            appendLine("<!--")
            appendLine("<animation-list xmlns:android=\"http://schemas.android.com/apk/res/android\"")
            appendLine("    android:oneshot=\"${binding.cbOneShot.isChecked}\">")
            appendLine("    <item android:drawable=\"@drawable/frame_1\" android:duration=\"100\" />")
            appendLine("    <item android:drawable=\"@drawable/frame_2\" android:duration=\"100\" />")
            appendLine("    ...")
            appendLine("</animation-list>")
            appendLine("-->")
            appendLine()

            appendLine("// Java/Kotlin 代码")
            appendLine("val imageView = findViewById<ImageView>(R.id.imageView)")
            appendLine("imageView.setImageResource(R.drawable.anim_${animType.lowercase()})")
            appendLine()
            appendLine("val animDrawable = imageView.drawable as AnimationDrawable")
            appendLine("animDrawable.oneShot = ${binding.cbOneShot.isChecked}")
            appendLine("animDrawable.start()  // 开始动画")
            appendLine("animDrawable.stop()   // 停止动画")
            appendLine()

            appendLine("// 注意事项:")
            appendLine("// 1. AnimationDrawable 在 onCreate 中直接调用 start() 无效")
            appendLine("//    需要在 onWindowFocusChanged 或 post{} 中调用")
            appendLine("// 2. 帧数过多会导致内存问题，考虑使用属性动画替代")
            appendLine("// 3. 帧图片建议使用相同尺寸以获得最佳效果")
        }

        binding.tvCodeHint.text = code
    }
}
