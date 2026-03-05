package com.peter.constraintlayout.demo.advanced

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.peter.constraintlayout.demo.R

/**
 * ConstraintLayout 进阶示例
 * 
 * 本示例涵盖以下进阶知识点：
 * 1. 尺寸控制 - wrap_content, match_constraint, 固定尺寸
 * 2. 宽高比 (Ratio) - 按比例设置视图尺寸
 * 3. 链式布局 (Chains) - 水平链和垂直链
 * 4. Guidelines - 辅助定位线
 * 5. Barriers - 屏障，防止视图重叠
 * 6. Group - 组控制多个视图的可见性
 */
class AdvancedConstraintLayoutActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advanced_constraint_layout)
        
        supportActionBar?.title = "进阶示例：尺寸、比例与链"
    }
}
