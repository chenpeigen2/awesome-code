package com.peter.components.demo.activity.lifecycle

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.createLifecycleDialogActivityIntent
import com.peter.components.demo.createLifecycleTranslucentActivityIntent
import com.peter.components.demo.databinding.ActivityLifecycleNormalBinding

/**
 * Activity 生命周期示例
 * 
 * 知识点：
 * 1. onCreate - Activity 创建
 * 2. onStart - Activity 可见
 * 3. onResume - Activity 可交互
 * 4. onPause - Activity 失去焦点
 * 5. onStop - Activity 不可见
 * 6. onDestroy - Activity 销毁
 * 7. onRestart - Activity 重新启动
 * 
 * 特殊场景：
 * - 对话框 Activity：下方 Activity 执行 onPause
 * - 半透明 Activity：下方 Activity 执行 onPause
 * - 分屏模式：非焦点 Activity 执行 onPause
 */
class LifecycleNormalActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LifecycleActivity"
    }

    private lateinit var binding: ActivityLifecycleNormalBinding
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLifecycleNormalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        logLifecycle("onCreate")

        binding.btnDialog.setOnClickListener {
            startActivity(createLifecycleDialogActivityIntent(this))
        }

        binding.btnTranslucent.setOnClickListener {
            startActivity(createLifecycleTranslucentActivityIntent(this))
        }

        binding.btnClear.setOnClickListener {
            logBuilder.clear()
            binding.tvLog.text = "生命周期日志：\n"
        }
    }

    override fun onStart() {
        super.onStart()
        logLifecycle("onStart")
    }

    override fun onResume() {
        super.onResume()
        logLifecycle("onResume")
    }

    override fun onPause() {
        super.onPause()
        logLifecycle("onPause")
    }

    override fun onStop() {
        super.onStop()
        logLifecycle("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        logLifecycle("onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        logLifecycle("onRestart")
    }

    private fun logLifecycle(method: String) {
        Log.d(TAG, method)
        logBuilder.append("$method\n")
        binding.tvLog.text = "生命周期日志：\n$logBuilder"
    }
}
