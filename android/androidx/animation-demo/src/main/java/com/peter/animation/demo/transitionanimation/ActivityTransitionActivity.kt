package com.peter.animation.demo.transitionanimation

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.transition.Slide
import android.view.Gravity
import android.view.MenuItem
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.peter.animation.demo.R
import com.peter.animation.demo.databinding.ActivityActivityTransitionBinding

/**
 * Activity转场动画演示
 *
 * 展示Activity间转场：
 * - explode（爆炸）
 * - slide（滑动）
 * - fade（淡入淡出）
 *
 * 关键知识点：
 * - ActivityOptions.makeSceneTransitionAnimation
 * - Window.requestFeature
 * - 进入/退出转场
 * - TransitionListener
 */
class ActivityTransitionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActivityTransitionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // 启用转场动画
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)

        super.onCreate(savedInstanceState)
        binding = ActivityActivityTransitionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupButtons()
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

    private fun setupButtons() {
        binding.btnExplode.setOnClickListener {
            startTransitionActivity(TransitionType.EXPLODE)
        }

        binding.btnSlide.setOnClickListener {
            startTransitionActivity(TransitionType.SLIDE)
        }

        binding.btnFade.setOnClickListener {
            startTransitionActivity(TransitionType.FADE)
        }
    }

    private fun startTransitionActivity(type: TransitionType) {
        val intent = Intent(this, TransitionTargetActivity::class.java)
        intent.putExtra("transition_type", type.name)

        // 设置退出转场
        when (type) {
            TransitionType.EXPLODE -> {
                window.exitTransition = Explode()
            }
            TransitionType.SLIDE -> {
                window.exitTransition = Slide(Gravity.START)
            }
            TransitionType.FADE -> {
                window.exitTransition = Fade()
            }
        }

        // 使用ActivityOptions启动
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
        startActivity(intent, options.toBundle())
    }

    private fun updateCodeHint() {
        val code = buildString {
            appendLine("// Activity转场动画示例")
            appendLine()

            appendLine("// 1. 在styles.xml或代码中启用转场")
            appendLine("window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)")
            appendLine()

            appendLine("// 2. 设置退出转场效果")
            appendLine("// Explode - 爆炸效果")
            appendLine("window.exitTransition = Explode()")
            appendLine()
            appendLine("// Slide - 滑动效果")
            appendLine("window.exitTransition = Slide(Gravity.START)")
            appendLine()
            appendLine("// Fade - 淡入淡出")
            appendLine("window.exitTransition = Fade()")
            appendLine()

            appendLine("// 3. 启动Activity")
            appendLine("val options = ActivityOptionsCompat")
            appendLine("    .makeSceneTransitionAnimation(this)")
            appendLine("startActivity(intent, options.toBundle())")
            appendLine()

            appendLine("// 目标Activity设置进入转场")
            appendLine("window.enterTransition = Explode()")
            appendLine()

            appendLine("// 注意事项:")
            appendLine("// - 转场动画需要API 21+")
            appendLine("// - 使用finishAfterTransition()代替finish()")
            appendLine("// - 可以通过setDuration()设置转场时长")
        }

        binding.tvCodeHint.text = code
    }

    enum class TransitionType {
        EXPLODE, SLIDE, FADE
    }
}
