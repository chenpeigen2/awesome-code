package com.peter.touch.demo.level4

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.peter.touch.demo.BaseTouchActivity
import com.peter.touch.demo.R
import com.peter.touch.demo.databinding.ActivityGestureDetectBinding

/**
 * Level 4.2: 手势检测
 * 
 * 演示内容：
 * - GestureDetector：单击、双击、长按、滚动、快速滑动
 * - ScaleGestureDetector：缩放手势
 */
class GestureDetectActivity : BaseTouchActivity() {

    private lateinit var binding: ActivityGestureDetectBinding
    private lateinit var gestureDetector: GestureDetector
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGestureDetectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupGestureDetectors()
        setupDescription()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.level4_gesture_title)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupDescription() {
        binding.tvDescription.text = """
            |手势检测演示：
            |
            |• 单击 - 快速点击
            |• 双击 - 快速双击
            |• 长按 - 按住不放
            |• 滚动 - 滑动
            |• 快速滑动 - 快速滑动并抬起
            |• 缩放 - 双指捏合/张开
            |
            |在下方区域进行手势操作。
        """.trimMargin()
    }

    private fun setupGestureDetectors() {
        // 手势检测器
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                updateGestureInfo("单击", e.x, e.y)
                log("Gesture", "onSingleTapConfirmed", "单击", "(${e.x.toInt()}, ${e.y.toInt()})")
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                updateGestureInfo("双击", e.x, e.y)
                log("Gesture", "onDoubleTap", "双击", "(${e.x.toInt()}, ${e.y.toInt()})")
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                updateGestureInfo("长按", e.x, e.y)
                log("Gesture", "onLongPress", "长按", "(${e.x.toInt()}, ${e.y.toInt()})")
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                updateGestureInfo("滚动", e2.x, e2.y, "dx=${distanceX.toInt()}, dy=${distanceY.toInt()}")
                log("Gesture", "onScroll", "滚动", "dx=${distanceX.toInt()}, dy=${distanceY.toInt()}")
                return true
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                updateGestureInfo("快速滑动", e2.x, e2.y, "vx=${velocityX.toInt()}, vy=${velocityY.toInt()}")
                log("Gesture", "onFling", "快速滑动", "vx=${velocityX.toInt()}, vy=${velocityY.toInt()}")
                return true
            }

            override fun onDown(e: MotionEvent): Boolean {
                return true
            }
        })

        // 缩放手势检测器
        scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
                val focusX = detector.focusX
                val focusY = detector.focusY
                
                updateGestureInfo("缩放", focusX, focusY, "scale=${String.format("%.2f", scaleFactor)}")
                log("Scale", "onScale", "缩放", "scale=${String.format("%.2f", scaleFactor)}")
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                log("Scale", "onScaleBegin", "缩放开始", "")
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                log("Scale", "onScaleEnd", "缩放结束", "")
                logSeparator()
            }
        })
        
        // 设置触摸监听
        binding.gestureArea.setOnTouchListener { _, event ->
            var handled = gestureDetector.onTouchEvent(event)
            handled = scaleGestureDetector.onTouchEvent(event) || handled
            handled
        }
    }

    private fun updateGestureInfo(gesture: String, x: Float, y: Float, extra: String = "") {
        binding.tvGestureType.text = "识别手势: $gesture"
        binding.tvPosition.text = "位置: (${x.toInt()}, ${y.toInt()})"
        if (extra.isNotEmpty()) {
            binding.tvExtraInfo.text = extra
        } else {
            binding.tvExtraInfo.text = ""
        }
    }
}