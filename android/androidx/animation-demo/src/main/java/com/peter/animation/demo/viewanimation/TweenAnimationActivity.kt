package com.peter.animation.demo.viewanimation

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.*
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.peter.animation.demo.R
import com.peter.animation.demo.databinding.ActivityTweenAnimationBinding

/**
 * Tween补间动画演示
 *
 * 展示四种基本补间动画类型：
 * - AlphaAnimation: 透明度渐变
 * - ScaleAnimation: 缩放动画
 * - TranslateAnimation: 平移动画
 * - RotateAnimation: 旋转动画
 * - AnimationSet: 组合动画
 */
class TweenAnimationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTweenAnimationBinding

    private var currentAnimation: Animation? = null
    private var duration = 1000L
    private var selectedInterpolator: Interpolator = AccelerateDecelerateInterpolator()

    private val interpolators = listOf(
        "AccelerateDecelerate" to AccelerateDecelerateInterpolator(),
        "Accelerate" to AccelerateInterpolator(),
        "Decelerate" to DecelerateInterpolator(),
        "Linear" to LinearInterpolator(),
        "Anticipate" to AnticipateInterpolator(),
        "Overshoot" to OvershootInterpolator(),
        "AnticipateOvershoot" to AnticipateOvershootInterpolator(),
        "Bounce" to BounceInterpolator()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTweenAnimationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupInterpolatorSpinner()
        setupDurationSeekBar()
        setupAnimationTypeSelector()
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

    private fun setupInterpolatorSpinner() {
        val interpolatorNames = interpolators.map { it.first }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, interpolatorNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerInterpolator.adapter = adapter

        binding.spinnerInterpolator.setSelection(0)
        binding.spinnerInterpolator.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedInterpolator = interpolators[position].second
                updateCodeHint()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })
    }

    private fun setupDurationSeekBar() {
        binding.seekBarDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                duration = (progress + 100).toLong() // 最小100ms
                binding.tvDurationLabel.text = "动画时长: ${duration}ms"
                updateCodeHint()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupAnimationTypeSelector() {
        binding.rbAlpha.isChecked = true
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
        // 取消当前动画
        currentAnimation?.cancel()

        // 根据选择的类型创建动画
        val animation = when {
            binding.rbAlpha.isChecked -> createAlphaAnimation()
            binding.rbScale.isChecked -> createScaleAnimation()
            binding.rbTranslate.isChecked -> createTranslateAnimation()
            binding.rbRotate.isChecked -> createRotateAnimation()
            binding.rbSet.isChecked -> createAnimationSet()
            else -> createAlphaAnimation()
        }

        // 设置通用属性
        animation.duration = duration
        animation.interpolator = selectedInterpolator
        animation.fillAfter = binding.cbFillAfter.isChecked

        // 设置动画监听器
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                binding.btnPlay.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.btnPlay.isEnabled = true
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        currentAnimation = animation
        binding.animationTarget.startAnimation(animation)
    }

    private fun createAlphaAnimation(): Animation {
        return AlphaAnimation(1.0f, 0.0f).apply {
            repeatCount = 1
            repeatMode = Animation.REVERSE
        }
    }

    private fun createScaleAnimation(): Animation {
        return ScaleAnimation(
            1.0f, 2.0f,  // fromX, toX
            1.0f, 2.0f,  // fromY, toY
            Animation.RELATIVE_TO_SELF, 0.5f,  // pivotXType, pivotXValue
            Animation.RELATIVE_TO_SELF, 0.5f   // pivotYType, pivotYValue
        ).apply {
            repeatCount = 1
            repeatMode = Animation.REVERSE
        }
    }

    private fun createTranslateAnimation(): Animation {
        return TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f,  // fromXType, fromXValue
            Animation.RELATIVE_TO_SELF, 1f,  // toXType, toXValue
            Animation.RELATIVE_TO_SELF, 0f,  // fromYType, fromYValue
            Animation.RELATIVE_TO_SELF, 0f   // toYType, toYValue
        ).apply {
            repeatCount = 1
            repeatMode = Animation.REVERSE
        }
    }

    private fun createRotateAnimation(): Animation {
        return RotateAnimation(
            0f, 360f,  // fromDegrees, toDegrees
            Animation.RELATIVE_TO_SELF, 0.5f,  // pivotXType, pivotXValue
            Animation.RELATIVE_TO_SELF, 0.5f   // pivotYType, pivotYValue
        )
    }

    private fun createAnimationSet(): AnimationSet {
        return AnimationSet(true).apply {
            addAnimation(AlphaAnimation(1.0f, 0.5f))
            addAnimation(ScaleAnimation(
                1.0f, 1.5f,
                1.0f, 1.5f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            ))
            addAnimation(RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            ))
        }
    }

    private fun resetAnimation() {
        currentAnimation?.cancel()
        binding.animationTarget.clearAnimation()
        binding.animationTarget.alpha = 1f
        binding.animationTarget.scaleX = 1f
        binding.animationTarget.scaleY = 1f
        binding.animationTarget.translationX = 0f
        binding.animationTarget.translationY = 0f
        binding.animationTarget.rotation = 0f
    }

    private fun updateCodeHint() {
        val interpolatorName = interpolators[binding.spinnerInterpolator.selectedItemPosition].first
        val animType = when {
            binding.rbAlpha.isChecked -> "Alpha"
            binding.rbScale.isChecked -> "Scale"
            binding.rbTranslate.isChecked -> "Translate"
            binding.rbRotate.isChecked -> "Rotate"
            binding.rbSet.isChecked -> "Set"
            else -> "Alpha"
        }

        val code = buildString {
            appendLine("// ${animType}Animation 示例代码")
            appendLine()

            when {
                binding.rbAlpha.isChecked -> {
                    appendLine("val alphaAnim = AlphaAnimation(1.0f, 0.0f)")
                }
                binding.rbScale.isChecked -> {
                    appendLine("val scaleAnim = ScaleAnimation(")
                    appendLine("    1.0f, 2.0f,  // fromX, toX")
                    appendLine("    1.0f, 2.0f,  // fromY, toY")
                    appendLine("    Animation.RELATIVE_TO_SELF, 0.5f,")
                    appendLine("    Animation.RELATIVE_TO_SELF, 0.5f")
                    appendLine(")")
                }
                binding.rbTranslate.isChecked -> {
                    appendLine("val translateAnim = TranslateAnimation(")
                    appendLine("    Animation.RELATIVE_TO_SELF, 0f,")
                    appendLine("    Animation.RELATIVE_TO_SELF, 1f,")
                    appendLine("    Animation.RELATIVE_TO_SELF, 0f,")
                    appendLine("    Animation.RELATIVE_TO_SELF, 0f")
                    appendLine(")")
                }
                binding.rbRotate.isChecked -> {
                    appendLine("val rotateAnim = RotateAnimation(")
                    appendLine("    0f, 360f,  // fromDegrees, toDegrees")
                    appendLine("    Animation.RELATIVE_TO_SELF, 0.5f,")
                    appendLine("    Animation.RELATIVE_TO_SELF, 0.5f")
                    appendLine(")")
                }
                binding.rbSet.isChecked -> {
                    appendLine("val animSet = AnimationSet(true).apply {")
                    appendLine("    addAnimation(AlphaAnimation(1.0f, 0.5f))")
                    appendLine("    addAnimation(ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f,")
                    appendLine("        Animation.RELATIVE_TO_SELF, 0.5f,")
                    appendLine("        Animation.RELATIVE_TO_SELF, 0.5f))")
                    appendLine("    addAnimation(RotateAnimation(0f, 360f,")
                    appendLine("        Animation.RELATIVE_TO_SELF, 0.5f,")
                    appendLine("        Animation.RELATIVE_TO_SELF, 0.5f))")
                    appendLine("}")
                }
            }

            appendLine()
            appendLine("animation.duration = $duration")
            appendLine("animation.interpolator = ${interpolatorName}Interpolator()")
            appendLine("animation.fillAfter = ${binding.cbFillAfter.isChecked}")
            appendLine("view.startAnimation(animation)")
        }

        binding.tvCodeHint.text = code
    }
}
