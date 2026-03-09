package com.peter.animation.demo.materialshape

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.peter.animation.demo.R
import com.peter.animation.demo.databinding.ActivityMaterialShapeDrawableBinding

/**
 * MaterialShapeDrawable Demo
 *
 * MaterialShapeDrawable 是 Material Components 库中强大的 Drawable 类，
 * 可以创建具有自定义形状、阴影和边框的背景。
 *
 * 主要特性：
 * 1. ShapeAppearanceModel - 定义形状外观
 * 2. 阴影 - elevation 和 shadow compatibility
 * 3. 描边 - stroke 边框
 * 4. 填充 - 纯色或渐变填充
 * 5. 动态修改 - 运行时改变形状属性
 */
class MaterialShapeDrawableActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMaterialShapeDrawableBinding
    private lateinit var materialShapeDrawable: MaterialShapeDrawable
    private lateinit var materialShapeDrawable2: MaterialShapeDrawable
    private lateinit var materialShapeDrawable3: MaterialShapeDrawable
    private lateinit var materialShapeDrawable4: MaterialShapeDrawable
    private lateinit var materialShapeDrawable5: MaterialShapeDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaterialShapeDrawableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBasicShape()
        setupCornerVariations()
        setupStrokeDemo()
        setupShadowDemo()
        setupInteractiveShape()
    }

    /**
     * 1. 基本形状 - 简单圆角矩形
     */
    private fun setupBasicShape() {
        materialShapeDrawable = MaterialShapeDrawable().apply {
            // 设置填充颜色
            fillColor = ContextCompat.getColorStateList(
                this@MaterialShapeDrawableActivity,
                R.color.purple_500
            )
            // 设置形状外观 - 统一圆角
            shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setAllCorners(CornerFamily.ROUNDED, 24f)
                .build()
        }
        binding.viewBasicShape.background = materialShapeDrawable

        // 代码示例显示
        binding.tvBasicCode.text = """
// 1. 创建 MaterialShapeDrawable
val materialShapeDrawable = MaterialShapeDrawable().apply {
    fillColor = ColorStateList.valueOf(Color.parseColor("#6200EE"))
    shapeAppearanceModel = ShapeAppearanceModel.builder()
        .setAllCorners(CornerFamily.ROUNDED, 24f)
        .build()
}
view.background = materialShapeDrawable
        """.trimIndent()
    }

    /**
     * 2. 各种角形状变体
     */
    private fun setupCornerVariations() {
        // 左圆角右直角
        binding.viewLeftRounded.background = MaterialShapeDrawable().apply {
            fillColor = ContextCompat.getColorStateList(
                this@MaterialShapeDrawableActivity,
                R.color.blue_500
            )
            shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setTopLeftCorner(CornerFamily.ROUNDED, 32f)
                .setBottomLeftCorner(CornerFamily.ROUNDED, 32f)
                .setTopRightCorner(CornerFamily.ROUNDED, 0f)
                .setBottomRightCorner(CornerFamily.ROUNDED, 0f)
                .build()
        }

        // 药丸形状 (Pill)
        binding.viewPill.background = MaterialShapeDrawable().apply {
            fillColor = ContextCompat.getColorStateList(
                this@MaterialShapeDrawableActivity,
                R.color.green_500
            )
            shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setAllCorners(CornerFamily.ROUNDED, 50f) // 50% 圆角
                .build()
        }

        // 切角 (Cut corner)
        binding.viewCutCorner.background = MaterialShapeDrawable().apply {
            fillColor = ContextCompat.getColorStateList(
                this@MaterialShapeDrawableActivity,
                R.color.orange_500
            )
            shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setAllCorners(CornerFamily.CUT, 24f)
                .build()
        }

        // 混合角 (不同角不同处理)
        binding.viewMixedCorner.background = MaterialShapeDrawable().apply {
            fillColor = ContextCompat.getColorStateList(
                this@MaterialShapeDrawableActivity,
                R.color.red_500
            )
            shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setTopLeftCorner(CornerFamily.ROUNDED, 24f)
                .setTopRightCorner(CornerFamily.CUT, 24f)
                .setBottomLeftCorner(CornerFamily.CUT, 24f)
                .setBottomRightCorner(CornerFamily.ROUNDED, 24f)
                .build()
        }

        binding.tvCornerCode.text = """
// 2. 各种角形状
// 左圆角右直角
shapeAppearanceModel = ShapeAppearanceModel.builder()
    .setTopLeftCorner(CornerFamily.ROUNDED, 32f)
    .setBottomLeftCorner(CornerFamily.ROUNDED, 32f)
    .setTopRightCorner(CornerFamily.ROUNDED, 0f)
    .setBottomRightCorner(CornerFamily.ROUNDED, 0f)
    .build()

// 切角 (Cut corner)
shapeAppearanceModel = ShapeAppearanceModel.builder()
    .setAllCorners(CornerFamily.CUT, 24f)
    .build()

// 混合角
shapeAppearanceModel = ShapeAppearanceModel.builder()
    .setTopLeftCorner(CornerFamily.ROUNDED, 24f)
    .setTopRightCorner(CornerFamily.CUT, 24f)
    .build()
        """.trimIndent()
    }

    /**
     * 3. 描边 (Stroke) 示例
     */
    private fun setupStrokeDemo() {
        // 虚线描边
        materialShapeDrawable2 = MaterialShapeDrawable().apply {
            fillColor = ContextCompat.getColorStateList(
                this@MaterialShapeDrawableActivity,
                android.R.color.transparent
            )
            strokeColor = ContextCompat.getColorStateList(
                this@MaterialShapeDrawableActivity,
                R.color.purple_500
            )
            strokeWidth = 4f
            shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setAllCorners(CornerFamily.ROUNDED, 16f)
                .build()
        }
        binding.viewStrokeDashed.background = materialShapeDrawable2

        // 渐变描边效果 (通过多个drawable叠加模拟)
        materialShapeDrawable3 = MaterialShapeDrawable().apply {
            fillColor = ContextCompat.getColorStateList(
                this@MaterialShapeDrawableActivity,
                R.color.teal_200
            )
            strokeColor = ContextCompat.getColorStateList(
                this@MaterialShapeDrawableActivity,
                R.color.teal_700
            )
            strokeWidth = 6f
            shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setAllCorners(CornerFamily.ROUNDED, 24f)
                .build()
        }
        binding.viewStrokeGradient.background = materialShapeDrawable3

        // 双层描边效果
        val outerDrawable = MaterialShapeDrawable().apply {
            fillColor = ContextCompat.getColorStateList(
                this@MaterialShapeDrawableActivity,
                android.R.color.transparent
            )
            strokeColor = ContextCompat.getColorStateList(
                this@MaterialShapeDrawableActivity,
                R.color.blue_500
            )
            strokeWidth = 8f
            shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setAllCorners(CornerFamily.ROUNDED, 20f)
                .build()
        }
        val innerDrawable = MaterialShapeDrawable().apply {
            fillColor = ContextCompat.getColorStateList(
                this@MaterialShapeDrawableActivity,
                R.color.blue_200
            )
            strokeColor = ContextCompat.getColorStateList(
                this@MaterialShapeDrawableActivity,
                R.color.blue_700
            )
            strokeWidth = 2f
            shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setAllCorners(CornerFamily.ROUNDED, 16f)
                .build()
        }
        binding.viewStrokeDouble.apply {
            background = outerDrawable
            foreground = innerDrawable
        }

        binding.tvStrokeCode.text = """
// 3. 描边 (Stroke)
val drawable = MaterialShapeDrawable().apply {
    fillColor = ContextCompat.getColorStateList(context, android.R.color.transparent)
    strokeColor = ContextCompat.getColorStateList(context, R.color.purple_500)
    strokeWidth = 4f  // 描边宽度（像素）
    strokeEdgeWidth = 0f  // 边缘宽度
    shapeAppearanceModel = ShapeAppearanceModel.builder()
        .setAllCorners(CornerFamily.ROUNDED, 16f)
        .build()
}
view.background = drawable

// 注意: strokeWidth 单位是像素，不是 dp
// 需要转换: strokeWidth = 4f * resources.displayMetrics.density
        """.trimIndent()
    }

    /**
     * 4. 阴影 (Shadow) 示例
     */
    private fun setupShadowDemo() {
        // 无阴影
        binding.viewShadowNone.background = MaterialShapeDrawable().apply {
            fillColor = ContextCompat.getColorStateList(
                this@MaterialShapeDrawableActivity,
                R.color.purple_200
            )
            shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setAllCorners(CornerFamily.ROUNDED, 16f)
                .build()
            shadowRadius = 0
            elevation = 0f
        }

        // 轻阴影
        binding.viewShadowLight.background = MaterialShapeDrawable().apply {
            fillColor = ContextCompat.getColorStateList(
                this@MaterialShapeDrawableActivity,
                R.color.purple_300
            )
            shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setAllCorners(CornerFamily.ROUNDED, 16f)
                .build()
            shadowRadius = 8
            elevation = 4f
        }

        // 中等阴影
        binding.viewShadowMedium.background = MaterialShapeDrawable().apply {
            fillColor = ContextCompat.getColorStateList(
                this@MaterialShapeDrawableActivity,
                R.color.purple_400
            )
            shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setAllCorners(CornerFamily.ROUNDED, 16f)
                .build()
            shadowRadius = 16
            elevation = 8f
        }

        // 重阴影
        binding.viewShadowHeavy.background = MaterialShapeDrawable().apply {
            fillColor = ContextCompat.getColorStateList(
                this@MaterialShapeDrawableActivity,
                R.color.purple_500
            )
            shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setAllCorners(CornerFamily.ROUNDED, 16f)
                .build()
            shadowRadius = 24
            elevation = 16f
            // 设置阴影颜色 (API 28+)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                setShadowColor(Color.parseColor("#66000000"))
            }
        }

        binding.tvShadowCode.text = """
// 4. 阴影效果
val drawable = MaterialShapeDrawable().apply {
    fillColor = ContextCompat.getColorStateList(context, R.color.purple_500)
    shapeAppearanceModel = ShapeAppearanceModel.builder()
        .setAllCorners(CornerFamily.ROUNDED, 16f)
        .build()

    // 阴影设置
    shadowRadius = 24  // 阴影半径
    elevation = 16f    // elevation 值

    // 阴影颜色 (API 28+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        setShadowColor(Color.parseColor("#66000000"))
    }
}

// 重要: View 需要设置 outlineProvider
view.outlineProvider = ViewOutlineProvider.BACKGROUND
view.clipToOutline = true
        """.trimIndent()
    }

    /**
     * 5. 交互式形状 - 动态修改
     */
    private fun setupInteractiveShape() {
        materialShapeDrawable4 = MaterialShapeDrawable().apply {
            fillColor = ContextCompat.getColorStateList(
                this@MaterialShapeDrawableActivity,
                R.color.blue_500
            )
            shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setAllCorners(CornerFamily.ROUNDED, 0f)
                .build()
            strokeWidth = 4f
            strokeColor = ContextCompat.getColorStateList(
                this@MaterialShapeDrawableActivity,
                R.color.blue_200
            )
            elevation = 8f
            shadowRadius = 16
        }
        binding.viewInteractive.background = materialShapeDrawable4

        // 圆角滑块
        binding.seekbarCorner.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val cornerRadius = progress.toFloat()
                updateInteractiveShape(cornerRadius)
                binding.tvCornerProgress.text = "圆角: ${cornerRadius.toInt()}px"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 阴影滑块
        binding.seekbarShadow.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                materialShapeDrawable4.shadowRadius = progress
                materialShapeDrawable4.elevation = progress.toFloat()
                binding.viewInteractive.invalidate()
                binding.tvShadowProgress.text = "阴影: ${progress}px"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 描边宽度滑块
        binding.seekbarStroke.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                materialShapeDrawable4.strokeWidth = progress.toFloat()
                binding.viewInteractive.invalidate()
                binding.tvStrokeProgress.text = "描边: ${progress}px"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 切换形状类型按钮
        binding.btnToggleShape.setOnClickListener {
            toggleShapeType()
        }

        binding.tvInteractiveCode.text = """
// 5. 动态修改属性
// 修改圆角
materialShapeDrawable.shapeAppearanceModel = ShapeAppearanceModel.builder()
    .setAllCorners(CornerFamily.ROUNDED, cornerRadius)
    .build()

// 修改阴影
materialShapeDrawable.shadowRadius = shadowRadius
materialShapeDrawable.elevation = elevation

// 修改描边
materialShapeDrawable.strokeWidth = strokeWidth.toFloat()
materialShapeDrawable.strokeColor = ColorStateList.valueOf(color)

// 修改填充色
materialShapeDrawable.fillColor = ColorStateList.valueOf(newColor)

// 切换形状类型 (圆角 vs 切角)
shapeAppearanceModel = ShapeAppearanceModel.builder()
    .setAllCorners(CornerFamily.CUT, cornerSize)  // 切角
    // 或
    .setAllCorners(CornerFamily.ROUNDED, cornerSize)  // 圆角
    .build()

// 记得刷新 View
view.invalidate()
        """.trimIndent()

        // 初始化滑块值
        binding.seekbarCorner.progress = 0
        binding.seekbarShadow.progress = 16
        binding.seekbarStroke.progress = 4
    }

    private var isRoundedCorner = true

    private fun toggleShapeType() {
        val currentCorner = binding.seekbarCorner.progress.toFloat()
        isRoundedCorner = !isRoundedCorner

        materialShapeDrawable4.shapeAppearanceModel = ShapeAppearanceModel.builder()
            .setAllCorners(
                if (isRoundedCorner) CornerFamily.ROUNDED else CornerFamily.CUT,
                currentCorner
            )
            .build()

        binding.viewInteractive.invalidate()
        binding.btnToggleShape.text = if (isRoundedCorner) "切换为切角" else "切换为圆角"
    }

    private fun updateInteractiveShape(cornerRadius: Float) {
        materialShapeDrawable4.shapeAppearanceModel = ShapeAppearanceModel.builder()
            .setAllCorners(
                if (isRoundedCorner) CornerFamily.ROUNDED else CornerFamily.CUT,
                cornerRadius
            )
            .build()
        binding.viewInteractive.invalidate()
    }

    /**
     * 6. 高级用法示例 - 点击切换颜色
     */
    fun onColorCardClick(view: View) {
        val colors = listOf(
            R.color.purple_500,
            R.color.blue_500,
            R.color.green_500,
            R.color.orange_500,
            R.color.red_500
        )
        val randomColor = colors.random()

        materialShapeDrawable5?.fillColor = ContextCompat.getColorStateList(
            this,
            randomColor
        )
        view.invalidate()
    }

    override fun onResume() {
        super.onResume()
        // 初始化高级示例卡片
        if (!::materialShapeDrawable5.isInitialized) {
            materialShapeDrawable5 = MaterialShapeDrawable().apply {
                fillColor = ContextCompat.getColorStateList(
                    this@MaterialShapeDrawableActivity,
                    R.color.purple_500
                )
                shapeAppearanceModel = ShapeAppearanceModel.builder()
                    .setAllCorners(CornerFamily.ROUNDED, 24f)
                    .build()
                elevation = 8f
                shadowRadius = 16
            }
            binding.viewColorCard.background = materialShapeDrawable5
            binding.viewColorCard.setOnClickListener { onColorCardClick(it) }
        }
    }
}
