package com.peter.constraintlayout.demo.expert

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.helper.widget.Layer
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.peter.constraintlayout.demo.R

/**
 * ConstraintLayout 高级示例
 * 
 * 本示例涵盖以下高级知识点：
 * 1. ConstraintSet - 代码动态修改约束
 * 2. Transition - 约束变化动画
 * 3. Placeholder - 占位符，动态替换视图
 * 4. Layer - 层，对多个视图进行整体变换
 * 5. Circular Positioning - 圆形定位
 * 6. Flow - 虚拟布局，自动换行排列
 * 7. 实战：登录表单、卡片布局、动画切换
 */
class ExpertConstraintLayoutActivity : AppCompatActivity() {

    private var isAlternativeLayout = false

    // Views
    private lateinit var constraintSetContainer: ConstraintLayout
    private lateinit var btnToggleLayout: Button
    private lateinit var circularContainer: ConstraintLayout
    private lateinit var btnRotateCircle: Button
    private lateinit var layerDemo: Layer
    private lateinit var btnRotateLayer: Button
    private lateinit var btnScaleLayer: Button
    private lateinit var btnAlphaLayer: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expert_constraint_layout)

        supportActionBar?.title = "高级示例：ConstraintSet与动画"

        initViews()
        setupConstraintSetDemo()
        setupCircularPositioning()
        setupLayerDemo()
        setupFlowDemo()
    }

    private fun initViews() {
        constraintSetContainer = findViewById(R.id.constraint_set_container)
        btnToggleLayout = findViewById(R.id.btn_toggle_layout)
        circularContainer = findViewById(R.id.circular_container)
        btnRotateCircle = findViewById(R.id.btn_rotate_circle)
        layerDemo = findViewById(R.id.layer_demo)
        btnRotateLayer = findViewById(R.id.btn_rotate_layer)
        btnScaleLayer = findViewById(R.id.btn_scale_layer)
        btnAlphaLayer = findViewById(R.id.btn_alpha_layer)
    }

    /**
     * ConstraintSet 示例
     * 
     * ConstraintSet 允许你：
     * 1. 在代码中创建和修改约束
     * 2. 保存和恢复约束配置
     * 3. 在不同布局之间平滑切换
     */
    private fun setupConstraintSetDemo() {
        // ConstraintSet 使用步骤：
        // 1. 创建 ConstraintSet 对象
        // 2. clone() 从现有布局获取约束
        // 3. 修改约束
        // 4. applyTo() 应用到 ConstraintLayout

        btnToggleLayout.setOnClickListener {
            // 使用 TransitionManager 实现平滑动画
            val constraintSet = ConstraintSet()
            
            if (isAlternativeLayout) {
                // 恢复原始布局
                constraintSet.clone(constraintSetContainer)
                constraintSet.connect(
                    R.id.tv_demo_1,
                    ConstraintSet.END,
                    R.id.tv_demo_2,
                    ConstraintSet.START
                )
                constraintSet.connect(
                    R.id.tv_demo_1,
                    ConstraintSet.START,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.START
                )
                constraintSet.connect(
                    R.id.tv_demo_2,
                    ConstraintSet.START,
                    R.id.tv_demo_1,
                    ConstraintSet.END
                )
                constraintSet.connect(
                    R.id.tv_demo_2,
                    ConstraintSet.END,
                    R.id.tv_demo_3,
                    ConstraintSet.START
                )
                constraintSet.connect(
                    R.id.tv_demo_3,
                    ConstraintSet.START,
                    R.id.tv_demo_2,
                    ConstraintSet.END
                )
                constraintSet.connect(
                    R.id.tv_demo_3,
                    ConstraintSet.END,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.END
                )
            } else {
                // 切换到替代布局
                constraintSet.clone(constraintSetContainer)
                // 将三个视图垂直排列在左侧
                constraintSet.connect(
                    R.id.tv_demo_1,
                    ConstraintSet.START,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.START
                )
                constraintSet.connect(
                    R.id.tv_demo_2,
                    ConstraintSet.START,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.START
                )
                constraintSet.connect(
                    R.id.tv_demo_3,
                    ConstraintSet.START,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.START
                )
                // 设置偏移
                constraintSet.setHorizontalBias(R.id.tv_demo_1, 0.2f)
                constraintSet.setHorizontalBias(R.id.tv_demo_2, 0.5f)
                constraintSet.setHorizontalBias(R.id.tv_demo_3, 0.8f)
            }

            // 使用 TransitionManager 实现动画效果
            TransitionManager.beginDelayedTransition(
                constraintSetContainer,
                ChangeBounds()
            )
            constraintSet.applyTo(constraintSetContainer)
            isAlternativeLayout = !isAlternativeLayout

            // 更新按钮文字
            btnToggleLayout.text = if (isAlternativeLayout) "恢复原始布局" else "切换布局"
        }
    }

    /**
     * 圆形定位示例
     * 
     * 可以将视图定位在相对于另一个视图的圆形轨迹上
     * 使用 center_ 开头的约束属性
     */
    private fun setupCircularPositioning() {
        // 圆形定位参数：
        // app:layout_constraintCircle - 圆心视图ID
        // app:layout_constraintCircleRadius - 半径
        // app:layout_constraintCircleAngle - 角度 (0-360, 0为正上方)

        // 这里的示例在 XML 中已经定义
        // 动态修改圆形定位：
        btnRotateCircle.setOnClickListener {
            val constraintSet = ConstraintSet()
            constraintSet.clone(circularContainer)

            // 动态修改角度
            constraintSet.constrainCircle(
                R.id.tv_circle_1,
                R.id.tv_center,
                80, // 半径
                45f
            )
            constraintSet.constrainCircle(
                R.id.tv_circle_2,
                R.id.tv_center,
                80,
                135f
            )
            constraintSet.constrainCircle(
                R.id.tv_circle_3,
                R.id.tv_center,
                80,
                225f
            )
            constraintSet.constrainCircle(
                R.id.tv_circle_4,
                R.id.tv_center,
                80,
                315f
            )

            TransitionManager.beginDelayedTransition(
                circularContainer,
                ChangeBounds()
            )
            constraintSet.applyTo(circularContainer)
        }
    }

    /**
     * Layer 示例
     * 
     * Layer 可以对多个视图进行整体变换：
     * - 平移
     * - 旋转
     * - 缩放
     * - 改变透明度
     */
    private fun setupLayerDemo() {
        var rotation = 0f
        var scale = 1f
        var alpha = 1f

        btnRotateLayer.setOnClickListener {
            rotation += 15f
            layerDemo.rotation = rotation
        }

        btnScaleLayer.setOnClickListener {
            scale = if (scale == 1f) 0.8f else 1f
            layerDemo.scaleX = scale
            layerDemo.scaleY = scale
        }

        btnAlphaLayer.setOnClickListener {
            alpha = if (alpha == 1f) 0.5f else 1f
            layerDemo.alpha = alpha
        }
    }

    /**
     * Flow 示例
     * 
     * Flow 是一个虚拟布局，可以：
     * 1. 自动排列多个视图
     * 2. 自动换行
     * 3. 支持链式布局的样式
     */
    private fun setupFlowDemo() {
        // Flow 的主要属性：
        // app:flow_wrapMode - 换行模式
        //   - none (默认)：不换行
        //   - chain：换行后形成链
        //   - aligned：换行后对齐
        // app:flow_maxElementsWrap - 每行最大元素数
        // app:flow_horizontalStyle - 水平链样式
        // app:flow_verticalStyle - 垂直链样式
        // app:flow_horizontalGap - 水平间距
        // app:flow_verticalGap - 垂直间距

        // 示例已在 XML 中定义
    }
}