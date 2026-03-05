package com.peter.constraintlayout.demo.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.peter.constraintlayout.demo.R

/**
 * ConstraintLayout 基础示例
 * 
 * 本示例涵盖以下基础知识点：
 * 1. 相对定位 (Relative Positioning) - 最基础的约束方式
 * 2. 边距 (Margins) - 设置视图之间的间距
 * 3. 居中定位 (Centering Positioning) - 水平和垂直居中
 * 4. 偏移定位 (Bias) - 在约束范围内按比例偏移
 * 5. 可选边距 (Gone Margins) - 当目标视图 gone 时生效的边距
 * 
 * 核心概念：
 * - ConstraintLayout 使用约束(Constraints)来确定视图的位置和大小
 * - 每个约束连接两个视图的一条边，或者连接视图与父布局的边
 * - 视图必须至少有水平和垂直两个方向的约束才能确定位置
 */
class BasicConstraintLayoutActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_constraint_layout)
        
        supportActionBar?.title = "基础示例：相对定位与边距"
    }
}
