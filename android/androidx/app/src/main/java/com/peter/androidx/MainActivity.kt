package com.peter.androidx

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.peter.dynamicanimation.DynamicAnimationLib
import com.peter.dynamicanimation.animation.AnimationBuilder
import com.peter.dynamicanimation.animation.AnimationConfig
import com.peter.dynamicanimation.interpolator.InterpolatorType

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerInterpolators: Spinner
    private var selectedInterpolator = InterpolatorType.FAST_OUT_SLOW_IN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupInterpolatorSpinner()
        setupPressAnimations()
        setupScaleAnimations()
        setupRotationAnimations()
        setupTranslationAnimations()
        setupAlphaAnimations()
        setupComboAnimation()
        setupInterpolatorTest()
    }

    private fun setupInterpolatorSpinner() {
        spinnerInterpolators = findViewById(R.id.spinnerInterpolators)
        val interpolatorNames = InterpolatorType.values().map { it.displayName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, interpolatorNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerInterpolators.adapter = adapter

        spinnerInterpolators.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedInterpolator = InterpolatorType.values()[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupPressAnimations() {
        // 简单按压动画（使用默认配置）
        findViewById<Button>(R.id.btnSimplePress).apply {
            DynamicAnimationLib.pressAnimation(this, AnimationConfig()).attachToView()
        }

        // 弹跳按压动画
        findViewById<Button>(R.id.btnBouncePress).apply {
            val config = AnimationConfig.Builder()
                .interpolator(InterpolatorType.BOUNCE)
                .duration(500)
                .build()
            DynamicAnimationLib.pressAnimation(this, config).attachToView()
        }

        // 弹性按压动画
        findViewById<Button>(R.id.btnElasticPress).apply {
            val config = AnimationConfig.Builder()
                .interpolator(InterpolatorType.EASE_OUT_ELASTIC)
                .duration(800)
                .scaleDown(0.85f)
                .build()
            DynamicAnimationLib.pressAnimation(this, config).attachToView()
        }
    }

    private fun setupScaleAnimations() {
        findViewById<Button>(R.id.btnScaleUp).setOnClickListener { view ->
            AnimationBuilder.on(view)
                .scale(1.3f)
                .duration(300)
                .interpolator(InterpolatorType.FAST_OUT_SLOW_IN)
                .onAnimationEnd {
                    AnimationBuilder.on(view)
                        .scale(1f)
                        .duration(300)
                        .start()
                }
                .start()
        }

        findViewById<Button>(R.id.btnScaleDown).setOnClickListener { view ->
            AnimationBuilder.on(view)
                .scale(0.7f)
                .duration(300)
                .interpolator(InterpolatorType.FAST_OUT_SLOW_IN)
                .onAnimationEnd {
                    AnimationBuilder.on(view)
                        .scale(1f)
                        .duration(300)
                        .start()
                }
                .start()
        }

        findViewById<Button>(R.id.btnScalePulse).setOnClickListener { view ->
            val pulse = Runnable {
                AnimationBuilder.on(view)
                    .scale(1.2f)
                    .duration(150)
                    .onAnimationEnd {
                        AnimationBuilder.on(view)
                            .scale(1f)
                            .duration(150)
                            .onAnimationEnd {
                                view.postDelayed({  }, 200)
                            }
                            .start()
                    }
                    .start()
            }
            view.post(pulse)
        }
    }

    private fun setupRotationAnimations() {
        findViewById<Button>(R.id.btnRotate).setOnClickListener { view ->
            AnimationBuilder.on(view)
                .rotation(view.rotation + 360f)
                .duration(500)
                .interpolator(InterpolatorType.FAST_OUT_SLOW_IN)
                .start()
        }

        findViewById<Button>(R.id.btnShake).setOnClickListener { view ->
            val shake = Runnable {
                AnimationBuilder.on(view)
                    .translationX(-20f)
                    .duration(50)
                    .onAnimationEnd {
                        AnimationBuilder.on(view)
                            .translationX(20f)
                            .duration(50)
                            .onAnimationEnd {
                                AnimationBuilder.on(view)
                                    .translationX(-20f)
                                    .duration(50)
                                    .onAnimationEnd {
                                        AnimationBuilder.on(view)
                                            .translationX(20f)
                                            .duration(50)
                                            .onAnimationEnd {
                                                AnimationBuilder.on(view)
                                                    .translationX(0f)
                                                    .duration(50)
                                                    .start()
                                            }
                                            .start()
                                    }
                                    .start()
                            }
                            .start()
                    }
                    .start()
            }
            view.post(shake)
        }
    }

    private fun setupTranslationAnimations() {
        findViewById<Button>(R.id.btnMoveLeft).setOnClickListener { view ->
            AnimationBuilder.on(view)
                .translationX(-100f)
                .duration(300)
                .interpolator(InterpolatorType.FAST_OUT_SLOW_IN)
                .onAnimationEnd {
                    AnimationBuilder.on(view)
                        .translationX(0f)
                        .duration(300)
                        .start()
                }
                .start()
        }

        findViewById<Button>(R.id.btnMoveRight).setOnClickListener { view ->
            AnimationBuilder.on(view)
                .translationX(100f)
                .duration(300)
                .interpolator(InterpolatorType.FAST_OUT_SLOW_IN)
                .onAnimationEnd {
                    AnimationBuilder.on(view)
                        .translationX(0f)
                        .duration(300)
                        .start()
                }
                .start()
        }

        findViewById<Button>(R.id.btnMoveUp).setOnClickListener { view ->
            AnimationBuilder.on(view)
                .translationY(-50f)
                .duration(300)
                .interpolator(InterpolatorType.FAST_OUT_SLOW_IN)
                .onAnimationEnd {
                    AnimationBuilder.on(view)
                        .translationY(0f)
                        .duration(300)
                        .start()
                }
                .start()
        }
    }

    private fun setupAlphaAnimations() {
        findViewById<Button>(R.id.btnFadeOut).setOnClickListener { view ->
            AnimationBuilder.on(view)
                .alpha(0.3f)
                .duration(300)
                .interpolator(InterpolatorType.FAST_OUT_SLOW_IN)
                .onAnimationEnd {
                    AnimationBuilder.on(view)
                        .alpha(1f)
                        .duration(300)
                        .start()
                }
                .start()
        }

        findViewById<Button>(R.id.btnFadeIn).setOnClickListener { view ->
            view.alpha = 0.3f
            AnimationBuilder.on(view)
                .alpha(1f)
                .duration(300)
                .interpolator(InterpolatorType.FAST_OUT_SLOW_IN)
                .start()
        }
    }

    private fun setupComboAnimation() {
        findViewById<Button>(R.id.btnCombo).setOnClickListener { view ->
            AnimationBuilder.on(view)
                .scale(1.2f)
                .rotation(360f)
                .alpha(0.7f)
                .duration(600)
                .interpolator(InterpolatorType.EASE_IN_OUT_CUBIC)
                .onAnimationEnd {
                    AnimationBuilder.on(view)
                        .scale(1f)
                        .rotation(view.rotation)
                        .alpha(1f)
                        .duration(600)
                        .interpolator(InterpolatorType.EASE_IN_OUT_CUBIC)
                        .start()
                }
                .start()
        }
    }

    private fun setupInterpolatorTest() {
        findViewById<Button>(R.id.btnTestInterpolator).setOnClickListener { view ->
            val name = selectedInterpolator.displayName
            Toast.makeText(this, "测试插值器: $name", Toast.LENGTH_SHORT).show()

            AnimationBuilder.on(view)
                .scale(1.3f)
                .duration(500)
                .interpolator(selectedInterpolator)
                .onAnimationEnd {
                    AnimationBuilder.on(view)
                        .scale(1f)
                        .duration(500)
                        .interpolator(selectedInterpolator)
                        .start()
                }
                .start()
        }
    }
}