package com.peter.motion.demo.fragment

import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import androidx.appcompat.app.AppCompatActivity
import com.peter.motion.demo.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.enterTransition = Slide()
        window.exitTransition = Fade()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finishAfterTransition() }
    }
}
