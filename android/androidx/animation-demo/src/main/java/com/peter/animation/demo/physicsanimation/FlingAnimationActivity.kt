package com.peter.animation.demo.physicsanimation

import android.os.Bundle
import android.view.MenuItem
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FlingAnimation
import com.peter.animation.demo.databinding.ActivityFlingAnimationBinding

/**
 * Fling投掷动画演示
 *
 * 展示投掷动画：
 * - 基础Fling动画
 * - 摩擦力参数调节
 * - 边界处理
 *
 * 关键知识点：
 * - Friction参数
 * - setMinValue/setMaxValue
 * - FlingAnimation监听
 */
class FlingAnimationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlingAnimationBinding

    private var flingAnimX: FlingAnimation? = null
    private var flingAnimY: FlingAnimation? = null

    private var friction = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlingAnimationBinding.inflate(layoutInflater)
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
        binding.seekBarFriction.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                friction = (progress + 1) / 10f // 0.1 ~ 5.1
                binding.tvFrictionLabel.text = "Friction (摩擦力): ${String.format("%.1f", friction)}"
                updateCodeHint()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupControls() {
        binding.btnFlingRight.setOnClickListener {
            startFlingAnimation(2000f)
        }

        binding.btnFlingLeft.setOnClickListener {
            startFlingAnimation(-2000f)
        }

        binding.btnReset.setOnClickListener {
            resetAnimation()
        }
    }

    private fun setupTouchListener() {
        // 可以通过滑动手势触发Fling
        var startX = 0f
        var startY = 0f
        var startTime = 0L

        binding.animationContainer.setOnTouchListener { _, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    startY = event.y
                    startTime = System.currentTimeMillis()
                    flingAnimX?.cancel()
                    flingAnimY?.cancel()
                    true
                }
                android.view.MotionEvent.ACTION_UP -> {
                    val endX = event.x
                    val endY = event.y
                    val endTime = System.currentTimeMillis()

                    val velocityX = (endX - startX) / (endTime - startTime) * 1000
                    val velocityY = (endY - startY) / (endTime - startTime) * 1000

                    // 根据选择的方向启动Fling
                    val direction = when {
                        binding.rbX.isChecked -> Direction.X
                        binding.rbY.isChecked -> Direction.Y
                        else -> Direction.BOTH
                    }

                    when (direction) {
                        Direction.X -> startFlingWithVelocity(velocityX, 0f)
                        Direction.Y -> startFlingWithVelocity(0f, velocityY)
                        Direction.BOTH -> startFlingWithVelocity(velocityX, velocityY)
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun startFlingAnimation(velocity: Float) {
        val direction = when {
            binding.rbX.isChecked -> Direction.X
            binding.rbY.isChecked -> Direction.Y
            else -> Direction.BOTH
        }

        when (direction) {
            Direction.X -> startFlingX(velocity)
            Direction.Y -> startFlingY(velocity)
            Direction.BOTH -> {
                startFlingX(velocity)
                startFlingY(velocity * 0.5f)
            }
        }
    }

    private fun startFlingX(velocity: Float) {
        flingAnimX?.cancel()

        flingAnimX = FlingAnimation(binding.animationTarget, DynamicAnimation.TRANSLATION_X).apply {
            setStartVelocity(velocity)
            friction = this@FlingAnimationActivity.friction

            if (binding.cbEnableBoundary.isChecked) {
                val containerWidth = binding.animationContainer.width.toFloat()
                val targetWidth = binding.animationTarget.width.toFloat()
                val minTrans = -containerWidth / 2 + targetWidth / 2
                val maxTrans = containerWidth / 2 - targetWidth / 2
                setMinValue(minTrans)
                setMaxValue(maxTrans)
            }
        }

        flingAnimX?.start()
    }

    private fun startFlingY(velocity: Float) {
        flingAnimY?.cancel()

        flingAnimY = FlingAnimation(binding.animationTarget, DynamicAnimation.TRANSLATION_Y).apply {
            setStartVelocity(velocity)
            friction = this@FlingAnimationActivity.friction

            if (binding.cbEnableBoundary.isChecked) {
                val containerHeight = binding.animationContainer.height.toFloat()
                val targetHeight = binding.animationTarget.height.toFloat()
                val minTrans = -containerHeight / 2 + targetHeight / 2
                val maxTrans = containerHeight / 2 - targetHeight / 2
                setMinValue(minTrans)
                setMaxValue(maxTrans)
            }
        }

        flingAnimY?.start()
    }

    private fun startFlingWithVelocity(velocityX: Float, velocityY: Float) {
        if (velocityX != 0f) startFlingX(velocityX)
        if (velocityY != 0f) startFlingY(velocityY)
    }

    private fun resetAnimation() {
        flingAnimX?.cancel()
        flingAnimY?.cancel()
        binding.animationTarget.translationX = 0f
        binding.animationTarget.translationY = 0f
    }

    private fun updateCodeHint() {
        val code = buildString {
            appendLine("// FlingAnimation 投掷动画示例")
            appendLine()

            appendLine("// 创建FlingAnimation")
            appendLine("val flingAnim = FlingAnimation(")
            appendLine("    view,")
            appendLine("    DynamicAnimation.TRANSLATION_X")
            appendLine(")")
            appendLine()

            appendLine("// 配置参数")
            appendLine("flingAnim.apply {")
            appendLine("    setStartVelocity(2000f)  // 初始速度")
            appendLine("    friction = ${String.format("%.1f", friction)}f  // 摩擦力 (当前值)")
            appendLine()

            if (binding.cbEnableBoundary.isChecked) {
                appendLine("    // 边界限制")
                appendLine("    setMinValue(-100f)")
                appendLine("    setMaxValue(100f)")
            }
            appendLine("}")
            appendLine()

            appendLine("// 启动动画")
            appendLine("flingAnim.start()")
            appendLine()

            appendLine("// 参数说明:")
            appendLine("// - Friction (摩擦力): 值越大，减速越快")
            appendLine("//   - 默认值: 1.0f")
            appendLine("//   - 建议范围: 0.1f ~ 5.0f")
            appendLine()

            appendLine("// 边界处理:")
            appendLine("// - setMinValue(): 设置最小值")
            appendLine("// - setMaxValue(): 设置最大值")
            appendLine("// - 超出边界时动画会停止")
            appendLine()

            appendLine("// 提示: 在灰色区域滑动可以触发Fling动画")
        }

        binding.tvCodeHint.text = code
    }

    private enum class Direction {
        X, Y, BOTH
    }
}
