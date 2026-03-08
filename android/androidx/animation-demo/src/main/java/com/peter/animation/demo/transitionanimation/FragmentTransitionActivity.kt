package com.peter.animation.demo.transitionanimation

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.peter.animation.demo.R
import com.peter.animation.demo.databinding.ActivityFragmentTransitionBinding

/**
 * Fragment转场动画演示
 *
 * 展示Fragment间转场：
 * - Fragment标准转场
 * - 自定义转场动画
 *
 * 关键知识点：
 * - setCustomAnimations
 * - FragmentTransaction
 * - postponeEnterTransition / startPostponedEnterTransition
 */
class FragmentTransitionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFragmentTransitionBinding
    private var currentFragmentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentTransitionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupButtons()
        showFragment(0)
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

    private fun setupButtons() {
        binding.btnFragment1.setOnClickListener { showFragment(0) }
        binding.btnFragment2.setOnClickListener { showFragment(1) }
        binding.btnFragment3.setOnClickListener { showFragment(2) }
    }

    private fun showFragment(index: Int) {
        if (currentFragmentIndex == index) return

        val fragment = TransitionDemoFragment.newInstance(
            title = "Fragment ${index + 1}",
            color = when (index) {
                0 -> "#4CAF50"
                1 -> "#2196F3"
                else -> "#FF9800"
            }
        )

        supportFragmentManager.beginTransaction()
            .apply {
                // 设置自定义转场动画
                // enter: 新Fragment进入动画
                // exit: 旧Fragment退出动画
                // popEnter: 返回时新Fragment进入动画
                // popExit: 返回时旧Fragment退出动画
                setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
            }
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()

        currentFragmentIndex = index
    }

    private fun updateCodeHint() {
        val code = buildString {
            appendLine("// Fragment转场动画示例")
            appendLine()

            appendLine("// 1. 创建转场动画XML文件")
            appendLine("<!-- res/anim/slide_in_right.xml -->")
            appendLine("<set xmlns:android=\"http://schemas.android.com/apk/res/android\">")
            appendLine("    <translate android:fromXDelta=\"100%\" android:toXDelta=\"0%\"")
            appendLine("        android:duration=\"300\"/>")
            appendLine("</set>")
            appendLine()

            appendLine("// 2. 使用FragmentTransaction设置动画")
            appendLine("supportFragmentManager.beginTransaction()")
            appendLine("    .setCustomAnimations(")
            appendLine("        R.anim.slide_in_right,   // enter")
            appendLine("        R.anim.slide_out_left,   // exit")
            appendLine("        R.anim.slide_in_left,    // popEnter")
            appendLine("        R.anim.slide_out_right   // popExit")
            appendLine("    )")
            appendLine("    .replace(R.id.container, newFragment)")
            appendLine("    .addToBackStack(null)")
            appendLine("    .commit()")
            appendLine()

            appendLine("// 注意:")
            appendLine("// - setCustomAnimations 需要在 replace/add 之前调用")
            appendLine("// - popEnter/popExit 用于返回栈动画")
        }

        binding.tvCodeHint.text = code
    }
}
