package com.peter.layoutinflater.demo.basic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.peter.layoutinflater.demo.R
import com.peter.layoutinflater.demo.databinding.ActivityBasicInflateBinding

/**
 * LayoutInflater 基本 inflate 方法示例
 * 
 * 演示三种 inflate 方法的使用场景和效果区别
 */
class BasicInflateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBasicInflateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasicInflateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
    }

    private fun setupButtons() {
        // 方式一：最简单的方式，root = null
        binding.btnInflateSimple.setOnClickListener {
            demonstrateSimpleInflate()
        }

        // 方式二：带 root 参数，attachToRoot = false
        binding.btnInflateWithRoot.setOnClickListener {
            demonstrateInflateWithRoot()
        }

        // 方式三：attachToRoot = true
        binding.btnInflateAttach.setOnClickListener {
            demonstrateInflateAttachTrue()
        }
    }

    /**
     * 演示最简单的 inflate 方式
     * 
     * 【特点】
     * - root = null 时，根视图的 layout_xxx 属性会失效
     * - 返回的 View 的 LayoutParams 为 null
     * - 需要手动添加到容器中
     */
    private fun demonstrateSimpleInflate() {
        // 清空容器
        clearContainer()

        // 【inflate 方法 1】最简单形式
        // 当 root = null 时，布局根元素的 layout_width、layout_height 等属性会失效
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.item_sample, null)

        // 查看生成的 View 的 LayoutParams
        val params = view.layoutParams

        // 结果分析
        val resultText = buildString {
            appendLine("=== inflate(resource, null) ===")
            appendLine()
            appendLine("返回的 View: $view")
            appendLine("LayoutParams: $params")
            appendLine()
            appendLine("【分析】")
            appendLine("• LayoutParams = null，因为没有 root 提供参数")
            appendLine("• 布局中设置的 200dp x 100dp 失效")
            appendLine("• View 大小取决于内容或父容器")
            appendLine()
            appendLine("【解决】需要手动设置 LayoutParams 或使用带 root 的方法")
        }

        // 手动添加到容器
        binding.container.addView(view)

        // 显示结果
        binding.tvResult.text = resultText
        showToast("root=null 时 layout 属性会失效")
    }

    /**
     * 演示带 root 参数，attachToRoot = false
     * 
     * 【特点】
     * - root 用于生成正确的 LayoutParams
     * - 布局根元素的 layout_xxx 属性生效
     * - 不会自动添加到 root，需要手动添加
     * 
     * 【推荐用法】RecyclerView、ListView 等 Adapter 中
     */
    private fun demonstrateInflateWithRoot() {
        // 清空容器
        clearContainer()

        // 【inflate 方法 2】推荐方式
        // root 参数用于生成 LayoutParams，但不会自动添加
        val inflater = LayoutInflater.from(this)
        val container: FrameLayout = binding.container
        
        val view = inflater.inflate(R.layout.item_sample, container, false)

        // 查看 LayoutParams
        val params = view.layoutParams

        // 结果分析
        val resultText = buildString {
            appendLine("=== inflate(resource, root, false) ===")
            appendLine()
            appendLine("返回的 View: $view")
            appendLine("LayoutParams: $params")
            appendLine()
            appendLine("【分析】")
            appendLine("• LayoutParams 正确生成，宽=200dp，高=100dp")
            appendLine("• layout_margin 等属性也生效")
            appendLine("• 返回的 View 尚未添加到容器")
            appendLine()
            appendLine("【典型场景】")
            appendLine("• RecyclerView.ViewHolder 创建")
            appendLine("• Fragment.onCreateView 返回")
            appendLine("• AlertDialog 自定义视图")
        }

        // 手动添加到容器
        binding.container.addView(view)

        // 显示结果
        binding.tvResult.text = resultText
        showToast("推荐方式：LayoutParams 正确生成")
    }

    /**
     * 演示 attachToRoot = true
     * 
     * 【特点】
     * - View 会自动添加到 root 容器
     * - 返回值就是 root 本身
     * - 布局参数自动应用
     * 
     * 【典型场景】动态添加 View 到容器
     */
    private fun demonstrateInflateAttachTrue() {
        // 清空容器
        clearContainer()

        // 【inflate 方法 3】自动添加到容器
        // attachToRoot = true 时，View 会自动添加到 root
        // 返回值是 root 而不是加载的 View
        val inflater = LayoutInflater.from(this)
        val container: FrameLayout = binding.container
        
        // 注意：返回值是 container 本身，不是加载的 View
        val returnedView = inflater.inflate(R.layout.item_sample, container, true)

        // 获取刚添加的 View（实际上已添加到容器中）
        val addedView = container.getChildAt(0)
        val params = addedView?.layoutParams

        // 结果分析
        val resultText = buildString {
            appendLine("=== inflate(resource, root, true) ===")
            appendLine()
            appendLine("返回值: $returnedView")
            appendLine("返回值是否等于 container: ${returnedView === container}")
            appendLine()
            appendLine("容器内第一个子 View: $addedView")
            appendLine("子 View LayoutParams: $params")
            appendLine()
            appendLine("【分析】")
            appendLine("• View 已自动添加到容器")
            appendLine("• 返回值是 root 容器本身")
            appendLine("• LayoutParams 正确生成")
            appendLine()
            appendLine("【典型场景】")
            appendLine("• 动态添加 View 到容器")
            appendLine("• Activity.setContentView 替代方案")
        }

        // 不需要手动 addView，已经自动添加了
        binding.tvResult.text = resultText
        showToast("View 已自动添加到容器")
    }

    private fun clearContainer() {
        binding.container.removeAllViews()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
