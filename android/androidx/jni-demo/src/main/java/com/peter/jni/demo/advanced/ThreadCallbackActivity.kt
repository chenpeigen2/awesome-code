package com.peter.jni.demo.advanced

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.peter.jni.demo.BaseDemoActivity
import com.peter.jni.demo.R

interface ThreadCallback {
    fun onProgress(step: Int, message: String)
    fun onComplete(result: String)
}

class ThreadCallbackActivity : BaseDemoActivity() {

    private lateinit var nativeLib: ThreadCallbackNativeLib
    private val logBuffer = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nativeLib = ThreadCallbackNativeLib()

        addSection("1. C++ 子线程回调 Java（5 步模拟任务）") {
            logBuffer.clear()
            nativeLib.startBackgroundWork(object : ThreadCallback {
                override fun onProgress(step: Int, message: String) {
                    runOnUiThread {
                        logBuffer.append("Step $step: $message\n")
                        updateLog()
                    }
                }
                override fun onComplete(result: String) {
                    runOnUiThread {
                        logBuffer.append("完成: $result\n")
                        updateLog()
                    }
                }
            })
            appendResult("任务已启动，等待 C++ 线程回调...")
        }

        addSection("2. C++ 多线程并发计算") {
            logBuffer.clear()
            val result = nativeLib.multiThreadCompute(intArrayOf(10, 20, 30, 40, 50))
            appendResult("多线程计算结果: ${result.toList()}")
        }
    }

    private fun updateLog() {
        val container = getContainer()
        val existing = container.findViewWithTag<TextView>("log_area")
        if (existing != null) {
            existing.text = logBuffer.toString().trimEnd()
        } else {
            val lastBtn = (0 until container.childCount).lastOrNull { container.getChildAt(it) is Button }
                ?: return
            TextView(this).apply {
                text = logBuffer.toString().trimEnd()
                textSize = 13f
                setTextColor(0xFF333333.toInt())
                tag = "log_area"
                setPadding(16, 8, 0, 8)
                container.addView(this, lastBtn + 1)
            }
        }
    }
}

class ThreadCallbackNativeLib {
    companion object {
        init {
            System.loadLibrary("jni_demo")
        }
    }

    external fun startBackgroundWork(callback: ThreadCallback)
    external fun multiThreadCompute(data: IntArray): IntArray
}
