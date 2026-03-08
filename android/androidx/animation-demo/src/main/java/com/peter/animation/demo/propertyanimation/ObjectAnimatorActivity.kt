package com.peter.animation.demo.propertyanimation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.peter.animation.demo.R
import com.peter.animation.demo.databinding.ActivityObjectAnimatorBinding

/**
 * ObjectAnimator演示
 *
 * 展示ObjectAnimator的常见用法：
 * - 基础属性动画（alpha, translation, rotation, scale）
 * - PropertyValuesHolder多属性动画
 * - Keyframe关键帧动画
 *
 * 关键知识点：
 * - 属性命名规范
 * - 自定义View支持动画属性
 * - Evaluator（估值器）
 */
class ObjectAnimatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityObjectAnimatorBinding

    private var currentAnimator: ObjectAnimator? = null
    private var duration = 1000L

    private val properties = listOf(
        "alpha" to "透明度",
        "translationX" to "X轴平移",
        "translationY" to "Y轴平移",
        "rotation" to "旋转",
        "rotationX" to "X轴旋转",
        "rotationY" to "Y轴旋转",
        "scaleX" to "X轴缩放",
        "scaleY" to "Y轴缩放",
        "multiProperty" to "多属性(PropertyValuesHolder)",
        "keyframe" to "关键帧(Keyframe)"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityObjectAnimatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupPropertySpinner()
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

    private fun setupPropertySpinner() {
        val propertyNames = properties.map { "${it.first} - ${it.second}" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, propertyNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerProperty.adapter = adapter

        binding.spinnerProperty.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                updateCodeHint()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }
    }

    private fun setupDurationSeekBar() {
        binding.seekBarDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                duration = (progress + 100).toLong()
                binding.tvDurationLabel.text = "动画时长: ${duration}ms"
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
        currentAnimator?.cancel()

        val position = binding.spinnerProperty.selectedItemPosition
        val propertyName = properties[position].first

        val animator = when (propertyName) {
            "alpha" -> createAlphaAnimator()
            "translationX" -> createTranslationXAnimator()
            "translationY" -> createTranslationYAnimator()
            "rotation" -> createRotationAnimator()
            "rotationX" -> createRotationXAnimator()
            "rotationY" -> createRotationYAnimator()
            "scaleX" -> createScaleXAnimator()
            "scaleY" -> createScaleYAnimator()
            "multiProperty" -> createMultiPropertyAnimator()
            "keyframe" -> createKeyframeAnimator()
            else -> createAlphaAnimator()
        }

        animator.duration = duration
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                binding.btnPlay.isEnabled = true
            }
            override fun onAnimationStart(animation: Animator) {
                binding.btnPlay.isEnabled = false
            }
        })

        currentAnimator = animator
        animator.start()
    }

    private fun createAlphaAnimator(): ObjectAnimator {
        return ObjectAnimator.ofFloat(binding.animationTarget, "alpha", 1f, 0f, 1f)
    }

    private fun createTranslationXAnimator(): ObjectAnimator {
        return ObjectAnimator.ofFloat(binding.animationTarget, "translationX", 0f, 200f, -200f, 0f)
    }

    private fun createTranslationYAnimator(): ObjectAnimator {
        return ObjectAnimator.ofFloat(binding.animationTarget, "translationY", 0f, 100f, -100f, 0f)
    }

    private fun createRotationAnimator(): ObjectAnimator {
        return ObjectAnimator.ofFloat(binding.animationTarget, "rotation", 0f, 360f)
    }

    private fun createRotationXAnimator(): ObjectAnimator {
        return ObjectAnimator.ofFloat(binding.animationTarget, "rotationX", 0f, 360f)
    }

    private fun createRotationYAnimator(): ObjectAnimator {
        return ObjectAnimator.ofFloat(binding.animationTarget, "rotationY", 0f, 360f)
    }

    private fun createScaleXAnimator(): ObjectAnimator {
        return ObjectAnimator.ofFloat(binding.animationTarget, "scaleX", 1f, 2f, 0.5f, 1f)
    }

    private fun createScaleYAnimator(): ObjectAnimator {
        return ObjectAnimator.ofFloat(binding.animationTarget, "scaleY", 1f, 2f, 0.5f, 1f)
    }

    private fun createMultiPropertyAnimator(): ObjectAnimator {
        // PropertyValuesHolder 可以同时修改多个属性
        val pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.5f)
        val pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.5f)
        val pvhRotation = PropertyValuesHolder.ofFloat("rotation", 0f, 360f)

        return ObjectAnimator.ofPropertyValuesHolder(
            binding.animationTarget,
            pvhScaleX, pvhScaleY, pvhRotation
        )
    }

    private fun createKeyframeAnimator(): ObjectAnimator {
        // Keyframe 关键帧动画
        val keyframe1 = Keyframe.ofFloat(0f, 0f)
        val keyframe2 = Keyframe.ofFloat(0.25f, 90f)
        val keyframe3 = Keyframe.ofFloat(0.5f, 180f)
        val keyframe4 = Keyframe.ofFloat(0.75f, 270f)
        val keyframe5 = Keyframe.ofFloat(1f, 360f)

        val pvhRotation = PropertyValuesHolder.ofKeyframe(
            "rotation",
            keyframe1, keyframe2, keyframe3, keyframe4, keyframe5
        )

        return ObjectAnimator.ofPropertyValuesHolder(binding.animationTarget, pvhRotation)
    }

    private fun resetAnimation() {
        currentAnimator?.cancel()
        with(binding.animationTarget) {
            alpha = 1f
            translationX = 0f
            translationY = 0f
            rotation = 0f
            rotationX = 0f
            rotationY = 0f
            scaleX = 1f
            scaleY = 1f
        }
    }

    private fun updateCodeHint() {
        val position = binding.spinnerProperty.selectedItemPosition
        val propertyName = properties[position].first

        val code = buildString {
            appendLine("// ObjectAnimator 示例代码 - $propertyName")
            appendLine()

            when (propertyName) {
                "alpha" -> {
                    appendLine("ObjectAnimator.ofFloat(view, \"alpha\", 1f, 0f, 1f)")
                    appendLine("    .apply { duration = $duration }")
                    appendLine("    .start()")
                }
                "translationX" -> {
                    appendLine("ObjectAnimator.ofFloat(view, \"translationX\",")
                    appendLine("    0f, 200f, -200f, 0f)")
                    appendLine("    .apply { duration = $duration }")
                    appendLine("    .start()")
                }
                "translationY" -> {
                    appendLine("ObjectAnimator.ofFloat(view, \"translationY\",")
                    appendLine("    0f, 100f, -100f, 0f)")
                    appendLine("    .apply { duration = $duration }")
                    appendLine("    .start()")
                }
                "rotation" -> {
                    appendLine("ObjectAnimator.ofFloat(view, \"rotation\", 0f, 360f)")
                    appendLine("    .apply { duration = $duration }")
                    appendLine("    .start()")
                }
                "rotationX" -> {
                    appendLine("ObjectAnimator.ofFloat(view, \"rotationX\", 0f, 360f)")
                    appendLine("    .apply { duration = $duration }")
                    appendLine("    .start()")
                }
                "rotationY" -> {
                    appendLine("ObjectAnimator.ofFloat(view, \"rotationY\", 0f, 360f)")
                    appendLine("    .apply { duration = $duration }")
                    appendLine("    .start()")
                }
                "scaleX" -> {
                    appendLine("ObjectAnimator.ofFloat(view, \"scaleX\",")
                    appendLine("    1f, 2f, 0.5f, 1f)")
                    appendLine("    .apply { duration = $duration }")
                    appendLine("    .start()")
                }
                "scaleY" -> {
                    appendLine("ObjectAnimator.ofFloat(view, \"scaleY\",")
                    appendLine("    1f, 2f, 0.5f, 1f)")
                    appendLine("    .apply { duration = $duration }")
                    appendLine("    .start()")
                }
                "multiProperty" -> {
                    appendLine("// PropertyValuesHolder 同时修改多个属性")
                    appendLine("val pvhScaleX = PropertyValuesHolder.ofFloat(\"scaleX\", 1f, 1.5f)")
                    appendLine("val pvhScaleY = PropertyValuesHolder.ofFloat(\"scaleY\", 1f, 1.5f)")
                    appendLine("val pvhRotation = PropertyValuesHolder.ofFloat(\"rotation\", 0f, 360f)")
                    appendLine()
                    appendLine("ObjectAnimator.ofPropertyValuesHolder(view,")
                    appendLine("    pvhScaleX, pvhScaleY, pvhRotation)")
                    appendLine("    .apply { duration = $duration }")
                    appendLine("    .start()")
                }
                "keyframe" -> {
                    appendLine("// Keyframe 关键帧动画")
                    appendLine("val kf0 = Keyframe.ofFloat(0f, 0f)")
                    appendLine("val kf1 = Keyframe.ofFloat(0.25f, 90f)")
                    appendLine("val kf2 = Keyframe.ofFloat(0.5f, 180f)")
                    appendLine("val kf3 = Keyframe.ofFloat(0.75f, 270f)")
                    appendLine("val kf4 = Keyframe.ofFloat(1f, 360f)")
                    appendLine()
                    appendLine("val pvh = PropertyValuesHolder.ofKeyframe(\"rotation\",")
                    appendLine("    kf0, kf1, kf2, kf3, kf4)")
                    appendLine()
                    appendLine("ObjectAnimator.ofPropertyValuesHolder(view, pvh)")
                    appendLine("    .apply { duration = $duration }")
                    appendLine("    .start()")
                }
            }

            appendLine()
            appendLine("// 注意：ObjectAnimator 要求目标对象有对应的 setter 方法")
            appendLine("// 例如 \"alpha\" 需要 setAlpha(float) 方法")
        }

        binding.tvCodeHint.text = code
    }
}
