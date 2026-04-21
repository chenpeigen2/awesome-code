package com.peter.jni.demo.intermediate

import android.os.Bundle
import com.peter.jni.demo.BaseDemoActivity
import com.peter.jni.demo.R

interface ProgressCallback {
    fun onProgress(percent: Int)
    fun onComplete(result: String)
}

class CallbackActivity : BaseDemoActivity() {

    private lateinit var nativeLib: CallbackNativeLib
    private val logBuffer = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nativeLib = CallbackNativeLib()

        addSection("1. C++ 回调 Java 接口（同步）") {
            logBuffer.clear()
            nativeLib.doWork(object : ProgressCallback {
                override fun onProgress(percent: Int) {
                    logBuffer.append("进度: $percent%\n")
                }
                override fun onComplete(result: String) {
                    logBuffer.append("完成: $result\n")
                }
            })
            appendResult(logBuffer.toString().trimEnd())
        }

        addSection("2. C++ 回调 Java 静态方法") {
            val result = nativeLib.callStaticMethod(42)
            appendResult("C++ 调用静态方法: $result")
        }
    }

    companion object {
        @JvmStatic
        fun staticMethod(value: Int): String = "StaticMethod called with $value"
    }
}

class CallbackNativeLib {
    companion object {
        init {
            System.loadLibrary("jni_demo")
        }
    }

    external fun doWork(callback: ProgressCallback)
    external fun callStaticMethod(value: Int): String
}
