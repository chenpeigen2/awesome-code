package com.peter.animation.demo.physicsanimation

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.peter.animation.demo.databinding.ActivitySpringAnimationBinding

/**
 * Spring弹性动画演示
 *
 * 展示弹性动画：
 * - 基础Spring动画
 * - 刚度(Stiffness)参数调节
 * - 阻尼比(DampingRatio)调节
 *
 * 关键知识点：
 * - SpringForce
 * - stiffness/dampingRatio配置
 * - start() vs animateToFinalPosition()
 */
class SpringAnimationActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpringAnimationBinding

    private var springAnimX: SpringAnimation? = null
    private var springAnimY: SpringAnimation? = null

    private var stiffness = 200f
    private var dampingRatio = 0.5f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpringAnimationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupSeekBars()
        setupControls()
        setupTouchListener()
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

    private fun setupSeekBars() {
        binding.seekBarStiffness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                stiffness = (progress + 10).toFloat() // 最小10
                binding.tvStiffnessLabel.text = "Stiffness (刚度): ${stiffness.toInt()}"
                updateCodeHint()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.seekBarDamping.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                dampingRatio = progress / 100f
                binding.tvDampingLabel.text = "Damping Ratio (阻尼比): ${String.format("%.2f", dampingRatio)}"
                updateCodeHint()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupControls() {
        binding.btnAnimate.setOnClickListener {
            startSpringAnimation()
        }

        binding.btnReset.setOnClickListener {
            resetAnimation()
        }
    }

    private fun setupTouchListener() {
        // 可以通过拖拽来移动View，然后松手时自动弹回
        binding.animationTarget.setOnTouchListener { view, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    // 取消当前动画
                    springAnimX?.cancel()
                    springAnimY?.cancel()
                    true
                }
                android.view.MotionEvent.ACTION_MOVE -> {
                    view.translationX = event.rawX - view.width / 2 - binding.animationContainer.left - binding.animationContainer.x
                    view.translationY = event.rawY - view.height / 2 - binding.animationContainer.top - binding.animationContainer.y
                    true
                }
                android.view.MotionEvent.ACTION_UP -> {
                    // 松手时弹回原位
                    animateToOrigin()
                    true
                }
                else -> false
            }
        }
    }

    private fun startSpringAnimation() {
        val direction = when {
            binding.rbX.isChecked -> Direction.X
            binding.rbY.isChecked -> Direction.Y
            else -> Direction.BOTH
        }

        when (direction) {
            Direction.X -> animateX()
            Direction.Y -> animateY()
            Direction.BOTH -> animateBoth()
        }
    }

    private fun animateX() {
        springAnimX?.cancel()

        springAnimX = SpringAnimation(binding.animationTarget, DynamicAnimation.TRANSLATION_X).apply {
            spring = SpringForce().apply {
                finalPosition = 200f
                this.stiffness = this@SpringAnimationActivity.stiffness
                this.dampingRatio = this@SpringAnimationActivity.dampingRatio
            }
        }

        springAnimX?.start()
    }

    private fun animateY() {
        springAnimY?.cancel()

        springAnimY = SpringAnimation(binding.animationTarget, DynamicAnimation.TRANSLATION_Y).apply {
            spring = SpringForce().apply {
                finalPosition = 100f
                this.stiffness = this@SpringAnimationActivity.stiffness
                this.dampingRatio = this@SpringAnimationActivity.dampingRatio
            }
        }

        springAnimY?.start()
    }

    private fun animateBoth() {
        animateX()
        animateY()
    }

    private fun animateToOrigin() {
        // X轴弹回
        springAnimX = SpringAnimation(binding.animationTarget, DynamicAnimation.TRANSLATION_X).apply {
            spring = SpringForce().apply {
                finalPosition = 0f
                this.stiffness = this@SpringAnimationActivity.stiffness
                this.dampingRatio = this@SpringAnimationActivity.dampingRatio
            }
        }
        springAnimX?.start()

        // Y轴弹回
        springAnimY = SpringAnimation(binding.animationTarget, DynamicAnimation.TRANSLATION_Y).apply {
            spring = SpringForce().apply {
                finalPosition = 0f
                this.stiffness = this@SpringAnimationActivity.stiffness
                this.dampingRatio = this@SpringAnimationActivity.dampingRatio
            }
        }
        springAnimY?.start()
    }

    private fun resetAnimation() {
        springAnimX?.cancel()
        springAnimY?.cancel()
        binding.animationTarget.translationX = 0f
        binding.animationTarget.translationY = 0f
    }

    private fun updateCodeHint() {
        val code = buildString {
            appendLine("// SpringAnimation 弹性动画示例")
            appendLine()

            appendLine("// 创建SpringAnimation")
            appendLine("val springAnim = SpringAnimation(")
            appendLine("    view,")
            appendLine("    DynamicAnimation.TRANSLATION_X")
            appendLine(")")
            appendLine()

            appendLine("// 配置SpringForce")
            appendLine("springAnim.spring = SpringForce().apply {")
            appendLine("    finalPosition = 200f  // 目标位置")
            appendLine("    stiffness = ${stiffness.toInt()}f  // 刚度 (当前值)")
            appendLine("    dampingRatio = ${String.format("%.2f", dampingRatio)}f  // 阻尼比 (当前值)")
            appendLine("}")
            appendLine()

            appendLine("// 启动动画")
            appendLine("springAnim.start()")
            appendLine()

            appendLine("// 参数说明:")
            appendLine("// - Stiffness (刚度): 值越大，弹簧越硬，振动越快")
            appendLine("//   - STIFFNESS_HIGH = 10_000f")
            appendLine("//   - STIFFNESS_MEDIUM = 1500f")
            appendLine("//   - STIFFNESS_LOW = 200f")
            appendLine("//   - STIFFNESS_VERY_LOW = 50f")
            appendLine()
            appendLine("// - DampingRatio (阻尼比): 控制振动衰减")
            appendLine("//   - DAMPING_RATIO_HIGH_BOUNCY = 0.2f (高弹性)")
            appendLine("//   - DAMPING_RATIO_MEDIUM_BOUNCY = 0.5f (中等)")
            appendLine("//   - DAMPING_RATIO_LOW_BOUNCY = 0.75f (低弹性)")
            appendLine("//   - DAMPING_RATIO_NO_BOUNCY = 1.0f (无弹性)")
            appendLine()

            appendLine("// 提示: 可以拖动绿色圆形来体验弹性效果")
        }

        binding.tvCodeHint.text = code
    }

    private enum class Direction {
        X, Y, BOTH
    }
}
