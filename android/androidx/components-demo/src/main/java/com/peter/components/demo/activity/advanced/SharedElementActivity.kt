package com.peter.components.demo.activity.advanced

import android.app.ActivityOptions
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.R
import com.peter.components.demo.databinding.ActivitySharedElementBinding

/**
 * 共享元素动画示例
 * 
 * 知识点：
 * 1. android:transitionName - 设置共享元素名称
 * 2. ActivityOptions.makeSceneTransitionAnimation() - 创建转场动画
 * 3. ActivityOptions.makeScaleUpAnimation() - 缩放动画
 * 4. ActivityCompat.startActivity() - 启动并应用动画
 */
class SharedElementActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySharedElementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySharedElementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivShared.setOnClickListener {
            // 创建共享元素转场动画
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this,
                binding.ivShared,
                "shared_image" // transitionName
            )
            // 实际项目中跳转到目标 Activity
            // startActivity(Intent(this, TargetActivity::class.java), options.toBundle())
        }
    }
}
