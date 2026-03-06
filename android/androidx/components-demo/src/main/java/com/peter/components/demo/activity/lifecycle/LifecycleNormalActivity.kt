package com.peter.components.demo.activity.lifecycle

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Activity 生命周期示例
 *
 * ═══════════════════════════════════════════════════════════════
 * Activity 生命周期图解
 * ═══════════════════════════════════════════════════════════════
 *
 *                    ┌──────────────────┐
 *                    │    onCreate()    │ ← 创建
 *                    └────────┬─────────┘
 *                             ↓
 *                    ┌──────────────────┐
 *                    │    onStart()     │ ← 可见但不可交互
 *                    └────────┬─────────┘
 *                             ↓
 *                    ┌──────────────────┐
 *     ┌──────────────│    onResume()    │ ← 可见可交互（前台）
 *     │              └────────┬─────────┘
 *     │                       ↓
 *     │              ┌──────────────────┐
 *     │              │   Running 状态   │
 *     │              └────────┬─────────┘
 *     │                       ↓
 *     │              ┌──────────────────┐
 *     └──────────────│    onPause()     │ ← 失去焦点
 *                    └────────┬─────────┘
 *                             ↓
 *                    ┌──────────────────┐
 *     ┌──────────────│    onStop()      │ ← 不可见
 *     │              └────────┬─────────┘
 *     │                       ↓
 *     │              ┌──────────────────┐
 *     └──────────────│   onRestart()    │ ← 重新启动
 *                    └────────┬─────────┘
 *                             ↓
 *                    ┌──────────────────┐
 *                    │    onDestroy()   │ ← 销毁
 *                    └──────────────────┘
 *
 * ═══════════════════════════════════════════════════════════════
 * 关键场景
 * ═══════════════════════════════════════════════════════════════
 *
 * 1. 正常启动：onCreate → onStart → onResume
 * 2. 正常退出：onPause → onStop → onDestroy
 * 3. 打开新Activity：onPause → onStop
 * 4. 返回当前Activity：onRestart → onStart → onResume
 * 5. 打开对话框：onPause（不会 onStop）
 * 6. 打开透明Activity：onPause（不会 onStop）
 * 7. 旋转屏幕：onPause → onStop → onDestroy → onCreate → onStart → onResume
 * 8. 系统内存不足：可能被杀死，需要保存状态
 */
class LifecycleNormalActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LifecycleDemo"
    }

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()
    private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lifecycle_normal)

        tvLog = findViewById(R.id.tvLog)

        findViewById<Button>(R.id.btnDialog).setOnClickListener {
            // 打开对话框风格的 Activity
            // 会导致当前 Activity 只执行 onPause，不会执行 onStop
            startActivity(android.content.Intent(this, LifecycleDialogActivity::class.java))
        }

        findViewById<Button>(R.id.btnTranslucent).setOnClickListener {
            // 打开透明 Activity
            // 同样只会导致 onPause，不会执行 onStop
            startActivity(android.content.Intent(this, LifecycleTranslucentActivity::class.java))
        }

        findViewById<Button>(R.id.btnFinish).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            logBuilder.clear()
            tvLog.text = ""
        }

        log("onCreate")
    }

    override fun onStart() {
        super.onStart()
        log("onStart")
    }

    override fun onResume() {
        super.onResume()
        log("onResume")
    }

    override fun onPause() {
        super.onPause()
        log("onPause")
    }

    override fun onStop() {
        super.onStop()
        log("onStop")
    }

    override fun onRestart() {
        super.onRestart()
        log("onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
        Log.d(TAG, logBuilder.toString())
    }

    /**
     * onSaveInstanceState 在 onStop 之前调用
     * 用于保存临时状态数据
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        log("onSaveInstanceState")
    }

    /**
     * onRestoreInstanceState 在 onStart 之后调用
     * 用于恢复之前保存的状态
     * 只有在有保存状态时才会调用
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        log("onRestoreInstanceState")
    }

    private fun log(method: String) {
        val time = timeFormat.format(Date())
        val entry = "[$time] $method\n"
        logBuilder.append(entry)
        tvLog.append(entry)

        Log.d(TAG, "$TAG: $method")
    }
}
