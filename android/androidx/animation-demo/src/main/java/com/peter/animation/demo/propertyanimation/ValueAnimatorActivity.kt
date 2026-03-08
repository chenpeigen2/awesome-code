package com.peter.animation.demo.propertyanimation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.peter.animation.demo.R
import com.peter.animation.demo.databinding.ActivityValueAnimatorBinding
import androidx.core.graphics.toColorInt

/**
 * ValueAnimator演示
 *
 * 展示ValueAnimator的底层用法：
 * - 数值动画（ofInt）
 * - 浮点数动画（ofFloat）
 * - 颜色渐变动画（ofArgb）
 * - 自定义TypeEvaluator（ofObject）
 *
 * 关键知识点：
 * - AnimatorUpdateListener
 * - 自定义估值器
 * - 动画值监听与使用
 */
class ValueAnimatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityValueAnimatorBinding

    private var currentAnimator: ValueAnimator? = null
    private var duration = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityValueAnimatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
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

    private fun setupDurationSeekBar() {
        binding.seekBarDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                duration = (progress + 500).toLong()
                binding.tvDurationLabel.text = "动画时长: ${duration}ms"
                updateCodeHint()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupAnimationTypeSelector() {
        // 默认选中数值动画
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

        val animator = when {
            binding.rbInt.isChecked -> createIntAnimator()
            binding.rbFloat.isChecked -> createFloatAnimator()
            binding.rbColor.isChecked -> createColorAnimator()
            binding.rbCustom.isChecked -> createCustomEvaluatorAnimator()
            else -> createIntAnimator()
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

    private fun createIntAnimator(): ValueAnimator {
        return ValueAnimator.ofInt(0, 100).apply {
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                binding.tvValue.text = value.toString()
            }
        }
    }

    private fun createFloatAnimator(): ValueAnimator {
        return ValueAnimator.ofFloat(0f, 100f).apply {
            addUpdateListener { animator ->
                val value = animator.animatedValue as Float
                binding.tvValue.text = String.format("%.1f", value)
            }
        }
    }

    private fun createColorAnimator(): ValueAnimator {
        return ValueAnimator.ofArgb(
            "#F44336".toColorInt(),
            "#FFEB3B".toColorInt(),
            "#4CAF50".toColorInt(),
            "#2196F3".toColorInt()
        ).apply {
            addUpdateListener { animator ->
                val color = animator.animatedValue as Int
                binding.tvValue.setTextColor(color)
                binding.tvValue.text = "#${Integer.toHexString(color).uppercase()}"
            }
        }
    }

    private fun createCustomEvaluatorAnimator(): ValueAnimator {
        // 自定义估值器：Point对象
        return ValueAnimator.ofObject(PointEvaluator(), Point(0, 0), Point(100, 100)).apply {
            addUpdateListener { animator ->
                val point = animator.animatedValue as Point
                binding.tvValue.text = "(${point.x}, ${point.y})"
            }
        }
    }

    private fun resetAnimation() {
        currentAnimator?.cancel()
        binding.tvValue.text = "0"
        binding.tvValue.setTextColor("#4CAF50".toColorInt())
    }

    private fun updateCodeHint() {
        val animType = when {
            binding.rbInt.isChecked -> "ofInt"
            binding.rbFloat.isChecked -> "ofFloat"
            binding.rbColor.isChecked -> "ofArgb"
            binding.rbCustom.isChecked -> "ofObject"
            else -> "ofInt"
        }

        val code = buildString {
            appendLine("// ValueAnimator 示例代码 - $animType")
            appendLine()

            when {
                binding.rbInt.isChecked -> {
                    appendLine("val animator = ValueAnimator.ofInt(0, 100)")
                    appendLine("animator.duration = $duration")
                    appendLine("animator.addUpdateListener {")
                    appendLine("    val value = it.animatedValue as Int")
                    appendLine("    textView.text = value.toString()")
                    appendLine("}")
                    appendLine("animator.start()")
                }
                binding.rbFloat.isChecked -> {
                    appendLine("val animator = ValueAnimator.ofFloat(0f, 100f)")
                    appendLine("animator.duration = $duration")
                    appendLine("animator.addUpdateListener {")
                    appendLine("    val value = it.animatedValue as Float")
                    appendLine("    textView.text = String.format(\"%.1f\", value)")
                    appendLine("}")
                    appendLine("animator.start()")
                }
                binding.rbColor.isChecked -> {
                    appendLine("val animator = ValueAnimator.ofArgb(")
                    appendLine("    Color.parseColor(\"#F44336\"),")
                    appendLine("    Color.parseColor(\"#FFEB3B\"),")
                    appendLine("    Color.parseColor(\"#4CAF50\"),")
                    appendLine("    Color.parseColor(\"#2196F3\")")
                    appendLine(")")
                    appendLine("animator.duration = $duration")
                    appendLine("animator.addUpdateListener {")
                    appendLine("    val color = it.animatedValue as Int")
                    appendLine("    textView.setTextColor(color)")
                    appendLine("}")
                    appendLine("animator.start()")
                }
                binding.rbCustom.isChecked -> {
                    appendLine("// 自定义估值器")
                    appendLine("class PointEvaluator : TypeEvaluator<Point> {")
                    appendLine("    override fun evaluate(")
                    appendLine("        fraction: Float,")
                    appendLine("        startValue: Point,")
                    appendLine("        endValue: Point")
                    appendLine("    ): Point {")
                    appendLine("        val x = startValue.x + (endValue.x - startValue.x) * fraction")
                    appendLine("        val y = startValue.y + (endValue.y - startValue.y) * fraction")
                    appendLine("        return Point(x.toInt(), y.toInt())")
                    appendLine("    }")
                    appendLine("}")
                    appendLine()
                    appendLine("val animator = ValueAnimator.ofObject(")
                    appendLine("    PointEvaluator(),")
                    appendLine("    Point(0, 0),")
                    appendLine("    Point(100, 100)")
                    appendLine(")")
                    appendLine("animator.duration = $duration")
                    appendLine("animator.addUpdateListener {")
                    appendLine("    val point = it.animatedValue as Point")
                    appendLine("    textView.text = \"(\${point.x}, \${point.y})\"")
                    appendLine("}")
                    appendLine("animator.start()")
                }
            }

            appendLine()
            appendLine("// ValueAnimator 是属性动画的核心")
            appendLine("// ObjectAnimator 是 ValueAnimator 的子类")
            appendLine("// 通过 addUpdateListener 获取动画过程中的值")
        }

        binding.tvCodeHint.text = code
    }

    // 简单的Point类
    data class Point(val x: Int, val y: Int)

    // 自定义估值器
    class PointEvaluator : TypeEvaluator<Point> {
        override fun evaluate(fraction: Float, startValue: Point, endValue: Point): Point {
            val x = startValue.x + (endValue.x - startValue.x) * fraction
            val y = startValue.y + (endValue.y - startValue.y) * fraction
            return Point(x.toInt(), y.toInt())
        }
    }
}
