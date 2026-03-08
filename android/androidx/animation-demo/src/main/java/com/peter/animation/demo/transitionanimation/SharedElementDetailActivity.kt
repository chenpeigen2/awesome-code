package com.peter.animation.demo.transitionanimation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.peter.animation.demo.databinding.ActivitySharedElementDetailBinding

/**
 * 共享元素详情页面
 */
class SharedElementDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySharedElementDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySharedElementDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            // 使用finishAfterTransition以触发返回转场动画
            finishAfterTransition()
        }
    }

    override fun onBackPressed() {
        finishAfterTransition()
    }
}
