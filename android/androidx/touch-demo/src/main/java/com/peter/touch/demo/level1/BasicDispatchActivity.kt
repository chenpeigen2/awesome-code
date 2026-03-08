package com.peter.touch.demo.level1

import android.os.Bundle
import android.view.MotionEvent
import com.peter.touch.demo.BaseTouchActivity
import com.peter.touch.demo.R
import com.peter.touch.demo.databinding.ActivityBasicDispatchBinding

/**
 * Level 1: 基础分发流程
 * 
 * 演示 Android 事件分发的三级结构：
 * Activity → ViewGroup → View
 */
class BasicDispatchActivity : BaseTouchActivity() {

    private lateinit var binding: ActivityBasicDispatchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasicDispatchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupDescription()
        setupDemoViews()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.level1_title)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupDescription() {
        binding.tvDescription.text = """
            |本示例演示 Android 事件分发的基础流程。
            |
            |触摸事件分发顺序：
            |1. Activity.dispatchTouchEvent()
            |2. ViewGroup.dispatchTouchEvent()
            |3. View.dispatchTouchEvent()
            |4. View.onTouchEvent()
            |5. 回传给父View处理
            |
            |点击下方的演示区域，观察悬浮日志面板中的事件流向。
        """.trimMargin()
    }
    
    private fun setupDemoViews() {
        // 配置 ViewGroup
        binding.demoViewGroup.apply {
            tagName = "ViewGroup"
            logCallback = { tag, method, action, result ->
                log(tag, method, action, result)
            }
        }
        
        // 配置子 View
        binding.demoView.apply {
            tagName = "View"
            logCallback = { tag, method, action, result ->
                log(tag, method, action, result)
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val action = getActionString(ev.actionMasked)
        log("Activity", "dispatchTouchEvent", action)
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = getActionString(event.actionMasked)
        log("Activity", "onTouchEvent", action, "true")
        return super.onTouchEvent(event)
    }

    private fun getActionString(action: Int): String {
        return when (action) {
            MotionEvent.ACTION_DOWN -> "DOWN"
            MotionEvent.ACTION_MOVE -> "MOVE"
            MotionEvent.ACTION_UP -> "UP"
            MotionEvent.ACTION_CANCEL -> "CANCEL"
            else -> "UNKNOWN"
        }
    }
}