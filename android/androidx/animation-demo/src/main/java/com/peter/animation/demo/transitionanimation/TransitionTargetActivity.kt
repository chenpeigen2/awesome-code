package com.peter.animation.demo.transitionanimation

import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.transition.Slide
import android.view.Gravity
import android.view.MenuItem
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.peter.animation.demo.databinding.ActivityTransitionTargetBinding

/**
 * 转场目标Activity
 */
class TransitionTargetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransitionTargetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)

        super.onCreate(savedInstanceState)
        binding = ActivityTransitionTargetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTransition()
        setupViews()
    }

    private fun setupTransition() {
        val type = intent.getStringExtra("transition_type")
            ?: ActivityTransitionActivity.TransitionType.FADE.name

        val transitionType = try {
            ActivityTransitionActivity.TransitionType.valueOf(type)
        } catch (e: Exception) {
            ActivityTransitionActivity.TransitionType.FADE
        }

        when (transitionType) {
            ActivityTransitionActivity.TransitionType.EXPLODE -> {
                window.enterTransition = Explode()
                binding.tvTransitionType.text = "转场类型: Explode (爆炸)"
            }
            ActivityTransitionActivity.TransitionType.SLIDE -> {
                window.enterTransition = Slide(Gravity.END)
                binding.tvTransitionType.text = "转场类型: Slide (滑动)"
            }
            ActivityTransitionActivity.TransitionType.FADE -> {
                window.enterTransition = Fade()
                binding.tvTransitionType.text = "转场类型: Fade (淡入淡出)"
            }
        }
    }

    private fun setupViews() {
        binding.btnBack.setOnClickListener {
            // 使用finishAfterTransition()以触发返回转场动画
            finishAfterTransition()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finishAfterTransition()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finishAfterTransition()
    }
}
