package com.peter.touch.demo.level4

import android.os.Bundle
import com.peter.touch.demo.BaseTouchActivity
import com.peter.touch.demo.R
import com.peter.touch.demo.databinding.ActivityMultiTouchBinding

/**
 * Level 4.1: 多指触控
 * 
 * 演示内容：
 * - pointerId 概念
 * - ACTION_POINTER_DOWN/UP 事件
 * - 多点触控场景（双指缩放）
 */
class MultiTouchActivity : BaseTouchActivity() {

    private lateinit var binding: ActivityMultiTouchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultiTouchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupDescription()
        setupTouchVisualizer()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.level4_multi_title)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupDescription() {
        binding.tvDescription.text = """
            |多指触控演示：
            |
            |• pointerId - 每个触摸点的唯一标识
            |• ACTION_POINTER_DOWN - 非第一个手指按下
            |• ACTION_POINTER_UP - 非最后一个手指抬起
            |
            |在下方区域进行多点触摸，观察触摸点的变化。
            |支持双指缩放演示。
        """.trimMargin()
    }
    
    private fun setupTouchVisualizer() {
        binding.touchVisualizer.setOnTouchListener { _, event ->
            handleMultiTouch(event)
            true
        }
    }

    private fun handleMultiTouch(event: android.view.MotionEvent): Boolean {
        val action = event.actionMasked
        val pointerCount = event.pointerCount
        
        when (action) {
            android.view.MotionEvent.ACTION_DOWN -> {
                val pointerId = event.getPointerId(0)
                log("Touch", "DOWN", "Pointer[0]", "id=$pointerId")
            }
            android.view.MotionEvent.ACTION_POINTER_DOWN -> {
                val index = event.actionIndex
                val pointerId = event.getPointerId(index)
                log("Touch", "POINTER_DOWN", "Pointer[$index]", "id=$pointerId, count=$pointerCount")
            }
            android.view.MotionEvent.ACTION_MOVE -> {
                val sb = StringBuilder()
                for (i in 0 until pointerCount) {
                    val pointerId = event.getPointerId(i)
                    val x = event.getX(i).toInt()
                    val y = event.getY(i).toInt()
                    sb.append("P$pointerId($x,$y) ")
                }
                log("Touch", "MOVE", "count=$pointerCount", sb.toString())
            }
            android.view.MotionEvent.ACTION_POINTER_UP -> {
                val index = event.actionIndex
                val pointerId = event.getPointerId(index)
                log("Touch", "POINTER_UP", "Pointer[$index]", "id=$pointerId, count=$pointerCount")
            }
            android.view.MotionEvent.ACTION_UP -> {
                val pointerId = event.getPointerId(0)
                log("Touch", "UP", "Pointer[0]", "id=$pointerId")
                logSeparator()
            }
            android.view.MotionEvent.ACTION_CANCEL -> {
                log("Touch", "CANCEL", "", "")
                logSeparator()
            }
        }
        
        // 更新状态显示
        updateStatus(event)
        return true
    }

    private fun updateStatus(event: android.view.MotionEvent) {
        val pointerCount = event.pointerCount
        
        // 显示当前触摸点数量
        binding.tvPointerCount.text = "当前手指数: $pointerCount"
        
        // 显示每个触摸点的ID和坐标
        val sb = StringBuilder()
        for (i in 0 until pointerCount) {
            val pointerId = event.getPointerId(i)
            val x = event.getX(i).toInt()
            val y = event.getY(i).toInt()
            sb.append("Pointer $pointerId: ($x, $y)\n")
        }
        binding.tvPointerInfo.text = sb.toString().trim()
    }
}