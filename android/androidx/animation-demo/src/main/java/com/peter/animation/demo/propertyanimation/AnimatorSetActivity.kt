package com.peter.animation.demo.propertyanimation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.MenuItem
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.peter.animation.demo.R
import com.peter.animation.demo.databinding.ActivityAnimatorSetBinding

/**
 * AnimatorSet演示
 *
 * 展示动画集合：
 * - 顺序播放（playSequentially）
 * - 同时播放（playTogether）
 * - 复杂编排（play().with().after()）
 *
 * 关键知识点：
 * - AnimatorSet.Builder
 * - 动画时序控制
 * - 嵌套AnimatorSet
 */
class AnimatorSetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimatorSetBinding

    private var currentAnimatorSet: AnimatorSet? = null
    private var duration = 500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimatorSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDurationSeekBar()
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

    private fun setupDurationSeekBar() {
        binding.seekBarDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                duration = (progress + 100).toLong()
                binding.tvDurationLabel.text = "单个动画时长: ${duration}ms"
                updateCodeHint()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupControls() {
        binding.btnPlay.setOnClickListener {
            playAnimation()
        }

        binding.btnReset.setOnClickListener {
            resetAnimation()
        }
    }

    private fun playAnimation() {
        currentAnimatorSet?.cancel()
        resetAnimation()

        val animatorSet = when {
            binding.rbSequentially.isChecked -> createSequentiallyAnimatorSet()
            binding.rbTogether.isChecked -> createTogetherAnimatorSet()
            binding.rbBuilder.isChecked -> createBuilderAnimatorSet()
            else -> createSequentiallyAnimatorSet()
        }

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                binding.btnPlay.isEnabled = true
            }
            override fun onAnimationStart(animation: Animator) {
                binding.btnPlay.isEnabled = false
            }
        })

        currentAnimatorSet = animatorSet
        animatorSet.start()
    }

    private fun createSequentiallyAnimatorSet(): AnimatorSet {
        // 顺序播放动画
        val alphaAnim = ObjectAnimator.ofFloat(binding.animationTarget1, "alpha", 1f, 0.3f, 1f).apply {
            this.duration = this@AnimatorSetActivity.duration
        }
        val scaleAnim = ObjectAnimator.ofFloat(binding.animationTarget2, "scaleX", 1f, 1.5f, 1f).apply {
            this.duration = this@AnimatorSetActivity.duration
        }
        val rotateAnim = ObjectAnimator.ofFloat(binding.animationTarget3, "rotation", 0f, 360f).apply {
            this.duration = this@AnimatorSetActivity.duration
        }

        return AnimatorSet().apply {
            playSequentially(alphaAnim, scaleAnim, rotateAnim)
        }
    }

    private fun createTogetherAnimatorSet(): AnimatorSet {
        // 同时播放动画
        val alphaAnim = ObjectAnimator.ofFloat(binding.animationTarget1, "alpha", 1f, 0.3f, 1f).apply {
            this.duration = this@AnimatorSetActivity.duration
        }
        val scaleAnim = ObjectAnimator.ofFloat(binding.animationTarget2, "scaleY", 1f, 1.5f, 1f).apply {
            this.duration = this@AnimatorSetActivity.duration
        }
        val rotateAnim = ObjectAnimator.ofFloat(binding.animationTarget3, "rotation", 0f, 360f).apply {
            this.duration = this@AnimatorSetActivity.duration
        }

        return AnimatorSet().apply {
            playTogether(alphaAnim, scaleAnim, rotateAnim)
        }
    }

    private fun createBuilderAnimatorSet(): AnimatorSet {
        // 使用Builder进行复杂编排
        val anim1 = ObjectAnimator.ofFloat(binding.animationTarget1, "alpha", 1f, 0.3f, 1f).apply {
            this.duration = this@AnimatorSetActivity.duration
        }
        val anim2 = ObjectAnimator.ofFloat(binding.animationTarget2, "translationX", 0f, 100f, 0f).apply {
            this.duration = this@AnimatorSetActivity.duration
        }
        val anim3 = ObjectAnimator.ofFloat(binding.animationTarget3, "translationX", 0f, -100f, 0f).apply {
            this.duration = this@AnimatorSetActivity.duration
        }
        val anim4 = ObjectAnimator.ofFloat(binding.animationTarget1, "rotation", 0f, 360f).apply {
            this.duration = this@AnimatorSetActivity.duration
        }

        return AnimatorSet().apply {
            // anim1 先播放
            // anim2 和 anim3 与 anim1 同时播放
            // anim4 在 anim1 之后播放
            play(anim1)
                .with(anim2)
                .with(anim3)
                .before(anim4)
        }
    }

    private fun resetAnimation() {
        listOf(
            binding.animationTarget1,
            binding.animationTarget2,
            binding.animationTarget3
        ).forEach { view ->
            view.alpha = 1f
            view.scaleX = 1f
            view.scaleY = 1f
            view.translationX = 0f
            view.translationY = 0f
            view.rotation = 0f
        }
    }

    private fun updateCodeHint() {
        val code = buildString {
            appendLine("// AnimatorSet 示例代码")
            appendLine()

            when {
                binding.rbSequentially.isChecked -> {
                    appendLine("// 顺序播放 (playSequentially)")
                    appendLine("val alphaAnim = ObjectAnimator.ofFloat(view1, \"alpha\", 1f, 0.3f, 1f)")
                    appendLine("alphaAnim.duration = $duration")
                    appendLine("val scaleAnim = ObjectAnimator.ofFloat(view2, \"scaleX\", 1f, 1.5f, 1f)")
                    appendLine("scaleAnim.duration = $duration")
                    appendLine("val rotateAnim = ObjectAnimator.ofFloat(view3, \"rotation\", 0f, 360f)")
                    appendLine("rotateAnim.duration = $duration")
                    appendLine()
                    appendLine("val animatorSet = AnimatorSet()")
                    appendLine("animatorSet.playSequentially(alphaAnim, scaleAnim, rotateAnim)")
                    appendLine("animatorSet.start()")
                }
                binding.rbTogether.isChecked -> {
                    appendLine("// 同时播放 (playTogether)")
                    appendLine("val alphaAnim = ObjectAnimator.ofFloat(view1, \"alpha\", 1f, 0.3f, 1f)")
                    appendLine("alphaAnim.duration = $duration")
                    appendLine("val scaleAnim = ObjectAnimator.ofFloat(view2, \"scaleY\", 1f, 1.5f, 1f)")
                    appendLine("scaleAnim.duration = $duration")
                    appendLine("val rotateAnim = ObjectAnimator.ofFloat(view3, \"rotation\", 0f, 360f)")
                    appendLine("rotateAnim.duration = $duration")
                    appendLine()
                    appendLine("val animatorSet = AnimatorSet()")
                    appendLine("animatorSet.playTogether(alphaAnim, scaleAnim, rotateAnim)")
                    appendLine("animatorSet.start()")
                }
                binding.rbBuilder.isChecked -> {
                    appendLine("// 复杂编排 (Builder模式)")
                    appendLine("val anim1 = ObjectAnimator.ofFloat(view1, \"alpha\", 1f, 0.3f, 1f)")
                    appendLine("val anim2 = ObjectAnimator.ofFloat(view2, \"translationX\", 0f, 100f, 0f)")
                    appendLine("val anim3 = ObjectAnimator.ofFloat(view3, \"translationX\", 0f, -100f, 0f)")
                    appendLine("val anim4 = ObjectAnimator.ofFloat(view1, \"rotation\", 0f, 360f)")
                    appendLine()
                    appendLine("val animatorSet = AnimatorSet()")
                    appendLine("animatorSet.play(anim1)  // anim1 先播放")
                    appendLine("    .with(anim2)         // anim2 与 anim1 同时")
                    appendLine("    .with(anim3)         // anim3 与 anim1 同时")
                    appendLine("    .before(anim4)       // anim4 在 anim1 之后")
                    appendLine("animatorSet.start()")
                }
            }

            appendLine()
            appendLine("// AnimatorSet.Builder 方法:")
            appendLine("// - play(anim): 设置主要动画")
            appendLine("// - with(anim): 与主动画同时播放")
            appendLine("// - before(anim): 在主动画之前播放")
            appendLine("// - after(anim): 在主动画之后播放")
            appendLine("// - after(delay): 延迟指定时间后播放")
        }

        binding.tvCodeHint.text = code
    }
}
