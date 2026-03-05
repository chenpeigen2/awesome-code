package com.peter.layoutinflater.demo.advanced

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.peter.layoutinflater.demo.R
import com.peter.layoutinflater.demo.databinding.ActivityAttachToRootBinding

/**
 * attachToRoot 参数深度解析
 * 
 * 【核心知识点】
 * attachToRoot 决定了两件事：
 * 1. View 是否自动添加到 root 容器
 * 2. 返回值是什么
 * 
 * 【详细说明】
 * - attachToRoot = false: 返回加载的 View，不添加到 root，需要手动 addView
 * - attachToRoot = true: 返回 root 容器，View 自动添加
 * - root = null: LayoutParams 无法生成，layout 属性失效
 */
class AttachToRootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttachToRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttachToRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        demonstrateAllScenarios()
    }

    private fun demonstrateAllScenarios() {
        demonstrateRootNull()
        demonstrateAttachToRootFalse()
        demonstrateAttachToRootTrue()
    }

    /**
     * 场景一：inflate(resource, null)
     * 
     * 【问题】
     * - LayoutParams 为 null
     * - 布局中的 layout_xxx 属性全部失效
     * - View 大小由内容决定，而不是设定的值
     */
    private fun demonstrateRootNull() {
        binding.containerNull.removeAllViews()

        val inflater = LayoutInflater.from(this)
        
        // 【不推荐】root = null
        val view = inflater.inflate(R.layout.item_attach_demo, null)
        
        // 查看 LayoutParams
        val params = view.layoutParams
        
        // 手动添加到容器
        binding.containerNull.addView(view)

        // 结果说明
        val result = buildString {
            appendLine("【问题分析】")
            appendLine("LayoutParams: $params")
            appendLine()
            appendLine("因为 root=null，LayoutInflater 无法知道：")
            appendLine("• 应该生成什么类型的 LayoutParams")
            appendLine("• layout_width/height 应该如何解析")
            appendLine("• layout_margin 等属性被忽略")
            appendLine()
            appendLine("【后果】")
            appendLine("• 布局设定的 80dp 高度失效")
            appendLine("• margin=8dp 失效")
            appendLine("• View 变成 WRAP_CONTENT 效果")
        }

        binding.tvResultNull.text = result
    }

    /**
     * 场景二：inflate(resource, root, false) - 【推荐】
     * 
     * 【优点】
     * - LayoutParams 正确生成
     * - 所有 layout 属性生效
     * - 可以在添加前对 View 做处理
     * 
     * 【适用场景】
     * - RecyclerView.ViewHolder
     * - Fragment.onCreateView
     * - Dialog 自定义布局
     */
    private fun demonstrateAttachToRootFalse() {
        binding.containerFalse.removeAllViews()

        val inflater = LayoutInflater.from(this)
        val container: FrameLayout = binding.containerFalse
        
        // 【推荐】attachToRoot = false
        val view = inflater.inflate(R.layout.item_attach_demo, container, false)
        
        // 查看 LayoutParams
        val params = view.layoutParams
        
        // 可以在添加前做一些处理
        // view.alpha = 0.5f
        // view.setOnClickListener { ... }
        
        // 手动添加到容器
        binding.containerFalse.addView(view)

        // 结果说明
        val result = buildString {
            appendLine("【LayoutParams】$params")
            appendLine()
            appendLine("【正确的参数解析】")
            appendLine("• width = MATCH_PARENT ✓")
            appendLine("• height = 80dp ✓")
            appendLine("• margins = 8dp ✓")
            appendLine()
            appendLine("【优点】")
            appendLine("• 可以在 addView 前处理 View")
            appendLine("• 可以根据条件决定是否添加")
            appendLine("• 可以设置动画或监听器")
            appendLine()
            appendLine("【典型用法】")
            appendLine("// RecyclerView.Adapter")
            appendLine("override fun onCreateViewHolder(")
            appendLine("  parent: ViewGroup, type: Int): VH {")
            appendLine("  val view = inflater.inflate(")
            appendLine("    R.layout.item, parent, false)")
            appendLine("  return VH(view)")
            appendLine("}")
        }

        binding.tvResultFalse.text = result
    }

    /**
     * 场景三：inflate(resource, root, true)
     * 
     * 【特点】
     * - View 自动添加到 root
     * - 返回值是 root，不是加载的 View
     * - 所有 layout 属性正确应用
     * 
     * 【适用场景】
     * - 动态添加 View 到容器
     * - 需要立即添加的场景
     */
    private fun demonstrateAttachToRootTrue() {
        binding.containerTrue.removeAllViews()

        val inflater = LayoutInflater.from(this)
        val container: FrameLayout = binding.containerTrue
        
        // attachToRoot = true
        // 注意：返回值是 container 本身，不是加载的 View
        val returnedView = inflater.inflate(R.layout.item_attach_demo, container, true)
        
        // 检查返回值
        val isSameContainer = returnedView === container
        
        // 获取加载的 View（已添加到容器中）
        val addedView = container.getChildAt(0)
        val params = addedView?.layoutParams

        // 结果说明
        val result = buildString {
            appendLine("【返回值】$returnedView")
            appendLine("返回值 === container: $isSameContainer")
            appendLine()
            appendLine("【已添加的 View】")
            appendLine("子 View: $addedView")
            appendLine("LayoutParams: $params")
            appendLine()
            appendLine("【特点】")
            appendLine("• View 自动添加到容器")
            appendLine("• 不需要调用 addView")
            appendLine("• LayoutParams 正确生成")
            appendLine()
            appendLine("【注意事项】")
            appendLine("• 返回值是 root 容器")
            appendLine("• 如需操作加载的 View，")
            appendLine("  需通过 container.getChildAt(0) 获取")
            appendLine()
            appendLine("【典型用法】")
            appendLine("// 动态添加 View")
            appendLine("inflater.inflate(")
            appendLine("  R.layout.item, container, true)")
        }

        binding.tvResultTrue.text = result
    }
}
