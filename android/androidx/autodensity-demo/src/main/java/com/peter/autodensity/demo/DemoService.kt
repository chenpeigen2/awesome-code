package com.peter.autodensity.demo

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.peter.autodensity.api.AutoDensity
import com.peter.autodensity.api.ServiceDensityAware

/**
 * Demo Service
 * 展示如何在 Service 中使用 AutoDensity
 */
class DemoService : Service(), ServiceDensityAware {

    companion object {
        private const val TAG = "DemoService"
    }

    override fun onCreate() {
        super.onCreate()

        // 应用密度适配到 Service
        // 会自动读取 ServiceDensityAware 的配置
        AutoDensity.applyToContext(this)

        Log.d(TAG, "Service onCreate - 密度已适配")

        // 显示 Toast（会使用适配后的密度）
        showToast("Service 已启动\n密度适配完成")

        // 打印密度信息
        val dm = resources.displayMetrics
        Log.d(TAG, """
            |Service 密度信息:
            |density = ${dm.density}
            |densityDpi = ${dm.densityDpi}
            |scaledDensity = ${dm.scaledDensity}
            |widthPixels = ${dm.widthPixels}
        """.trimMargin())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showToast("Service 正在运行")
        return START_NOT_STICKY
    }

    // 启用密度适配
    override fun shouldAdaptDensity(): Boolean = true

    // 强制使用 designWidthDp
    override fun forceDesignWidth(): Boolean = true

    override fun onBind(intent: Intent?): IBinder? = null

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
