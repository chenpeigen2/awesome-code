package com.peter.components.demo.activity.advanced

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.peter.components.demo.R

/**
 * Shared Element 转场动画示例
 *
 * ═══════════════════════════════════════════════════════════════
 * Shared Element 转场详解
 * ═══════════════════════════════════════════════════════════════
 *
 * 实现步骤：
 *
 * 1. 在两个 Activity 的布局中为共享元素设置相同的 transitionName
 *    android:transitionName="shared_image"
 *
 * 2. 启动时创建 ActivityOptionsCompat
 *    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
 *        this,
 *        sharedView,
 *        "shared_image"
 *    )
 *
 * 3. 使用 options 启动 Activity
 *    startActivity(intent, options.toBundle())
 *
 * 支持的转场类型：
 * - changeBounds：改变 View 的边界
 * - changeTransform：改变 View 的缩放和旋转
 * - changeClipBounds：改变裁剪边界
 * - changeImageTransform：改变图片的缩放
 *
 * 多个共享元素：
 * val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
 *     this,
 *     Pair(imageView, "shared_image"),
 *     Pair(textView, "shared_text")
 * )
 */
class SharedElementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_element)

        // 启用窗口内容转场
        // 需要在主题中设置或代码中设置
        // window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

        findViewById<ImageView>(R.id.ivShared).setOnClickListener { view ->
            // 在实际应用中，这里会启动另一个 Activity
            // 并使用共享元素转场动画
            showTransitionDemo()
        }
    }

    private fun showTransitionDemo() {
        /**
         * 实际使用示例：
         *
         * val intent = Intent(this, DetailActivity::class.java)
         *
         * // 单个共享元素
         * val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
         *     this,
         *     findViewById(R.id.ivShared),
         *     "shared_image"
         * )
         *
         * startActivity(intent, options.toBundle())
         *
         * // 多个共享元素
         * val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
         *     this,
         *     Pair(findViewById(R.id.ivShared), "shared_image"),
         *     Pair(findViewById(R.id.tvTitle), "shared_title")
         * )
         *
         * startActivity(intent, options.toBundle())
         *
         * // 退出时使用反向转场
         * finishAfterTransition()
         */
    }
}
