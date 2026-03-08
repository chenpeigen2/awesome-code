package com.peter.animation.demo.transitionanimation

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.peter.animation.demo.databinding.ActivitySharedElementBinding

/**
 * 共享元素动画演示
 *
 * 展示共享元素动画：
 * - 单个共享元素
 * - 多个共享元素
 *
 * 关键知识点：
 * - transitionName设置
 * - ActivityOptions.makeSceneTransitionAnimation
 * - 共享元素回调
 * - postponeEnterTransition
 */
class SharedElementActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySharedElementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySharedElementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupClickListeners()
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

    private fun setupClickListeners() {
        binding.card1.setOnClickListener {
            startDetailActivity()
        }
    }

    private fun startDetailActivity() {
        val intent = Intent(this, SharedElementDetailActivity::class.java)

        // 创建共享元素配对
        // Pair(共享元素View, transitionName)
        val imagePair = androidx.core.util.Pair.create<View, String>(
            binding.ivImage,
            "shared_image"
        )
        val titlePair = androidx.core.util.Pair.create<View, String>(
            binding.tvTitle,
            "shared_title"
        )
        val descPair = androidx.core.util.Pair.create<View, String>(
            binding.tvDescription,
            "shared_desc"
        )

        // 使用ActivityOptions创建转场选项
        @Suppress("UNCHECKED_CAST")
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            imagePair, titlePair, descPair
        )

        startActivity(intent, options.toBundle())
    }

    private fun updateCodeHint() {
        val code = buildString {
            appendLine("// 共享元素动画示例")
            appendLine()

            appendLine("// 1. 在布局中设置transitionName")
            appendLine("<ImageView")
            appendLine("    android:id=\"@+id/ivImage\"")
            appendLine("    android:transitionName=\"shared_image\" />")
            appendLine()
            appendLine("<TextView")
            appendLine("    android:id=\"@+id/tvTitle\"")
            appendLine("    android:transitionName=\"shared_title\" />")
            appendLine()

            appendLine("// 2. 启动Activity时创建共享元素配对")
            appendLine("val imagePair = Pair.create<View, String>(")
            appendLine("    binding.ivImage, \"shared_image\")")
            appendLine("val titlePair = Pair.create<View, String>(")
            appendLine("    binding.tvTitle, \"shared_title\")")
            appendLine()

            appendLine("// 3. 创建ActivityOptions")
            appendLine("val options = ActivityOptionsCompat")
            appendLine("    .makeSceneTransitionAnimation(")
            appendLine("        this, imagePair, titlePair)")
            appendLine()

            appendLine("// 4. 启动Activity")
            appendLine("startActivity(intent, options.toBundle())")
            appendLine()

            appendLine("// 注意:")
            appendLine("// - 两个Activity中对应View的transitionName必须相同")
            appendLine("// - 目标Activity的View也要设置相同的transitionName")
            appendLine("// - 可以使用postponeEnterTransition延迟转场")
        }

        binding.tvCodeHint.text = code
    }
}
