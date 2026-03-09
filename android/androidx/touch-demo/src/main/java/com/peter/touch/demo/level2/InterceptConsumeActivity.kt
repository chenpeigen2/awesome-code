package com.peter.touch.demo.level2

import android.os.Bundle
import android.view.MotionEvent
import com.google.android.material.chip.Chip
import com.peter.touch.demo.BaseTouchActivity
import com.peter.touch.demo.R
import com.peter.touch.demo.databinding.ActivityInterceptConsumeBinding
import androidx.core.graphics.toColorInt

/**
 * Level 2: 拦截与消费
 * 
 * 演示事件拦截机制和消费逻辑：
 * - onInterceptTouchEvent 拦截时机
 * - onTouchEvent 消费含义
 * - requestDisallowInterceptTouchEvent 子View反控
 */
class InterceptConsumeActivity : BaseTouchActivity() {

    private lateinit var binding: ActivityInterceptConsumeBinding

    // 配置状态
    private var outerInterceptDown = false
    private var outerInterceptMove = false
    private var outerInterceptUp = false
    private var outerConsume = false
    private var innerConsume = true
    private var disallowIntercept = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInterceptConsumeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupChips()
        setupDemoArea()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.level2_title)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupChips() {
        // 外层 ViewGroup 拦截 DOWN
        binding.chipInterceptDown.setOnCheckedChangeListener { _, isChecked ->
            outerInterceptDown = isChecked
            updateDemoConfig()
        }
        
        // 外层 ViewGroup 拦截 MOVE
        binding.chipInterceptMove.setOnCheckedChangeListener { _, isChecked ->
            outerInterceptMove = isChecked
            updateDemoConfig()
        }
        
        // 外层 ViewGroup 拦截 UP
        binding.chipInterceptUp.setOnCheckedChangeListener { _, isChecked ->
            outerInterceptUp = isChecked
            updateDemoConfig()
        }
        
        // 外层 ViewGroup 消费事件
        binding.chipOuterConsume.setOnCheckedChangeListener { _, isChecked ->
            outerConsume = isChecked
            updateDemoConfig()
        }
        
        // 内层 View 消费事件
        binding.chipInnerConsume.setOnCheckedChangeListener { _, isChecked ->
            innerConsume = isChecked
            updateDemoConfig()
        }
        
        // 禁止父View拦截
        binding.chipDisallowIntercept.setOnCheckedChangeListener { _, isChecked ->
            disallowIntercept = isChecked
            updateDemoConfig()
        }
        
        // 重置按钮
        binding.btnReset.setOnClickListener {
            resetConfig()
        }
    }

    private fun setupDemoArea() {
        // 配置外层 ViewGroup
        binding.outerViewGroup.apply {
            tagName = "Outer"
            bgColor = "#E8F5E9".toColorInt()
            
            logCallback = { tag, method, action, result ->
                log(tag, method, action, result)
            }
        }
        
        // 配置内层 View
        binding.innerView.apply {
            tagName = "Inner"
            bgColor = "#FFF8E1".toColorInt()
            
            logCallback = { tag, method, action, result ->
                log(tag, method, action, result)
            }
        }
        
        updateDemoConfig()
    }

    private fun updateDemoConfig() {
        binding.outerViewGroup.apply {
            interceptDown = outerInterceptDown
            interceptMove = outerInterceptMove
            interceptUp = outerInterceptUp
            consumeEvent = outerConsume
        }
        
        binding.innerView.consumeEvent = innerConsume
        
        // 如果启用了禁止拦截，则在 DOWN 时请求不拦截
        if (disallowIntercept) {
            binding.innerView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                }
                false
            }
        } else {
            binding.innerView.setOnTouchListener(null)
        }
    }

    private fun resetConfig() {
        outerInterceptDown = false
        outerInterceptMove = false
        outerInterceptUp = false
        outerConsume = false
        innerConsume = true
        disallowIntercept = false
        
        binding.chipInterceptDown.isChecked = false
        binding.chipInterceptMove.isChecked = false
        binding.chipInterceptUp.isChecked = false
        binding.chipOuterConsume.isChecked = false
        binding.chipInnerConsume.isChecked = true
        binding.chipDisallowIntercept.isChecked = false
        
        updateDemoConfig()
        logSeparator("配置已重置")
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