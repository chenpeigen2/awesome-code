package com.peter.autodensity.demo

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.peter.autodensity.api.AutoDensity
import com.peter.autodensity.api.ServiceDensityAware

/**
 * Demo Service
 * 展示如何在 Service 中使用 AutoDensity
 */
class DemoService : Service(), ServiceDensityAware {

    private var overlayView: View? = null

    override fun onCreate() {
        super.onCreate()

        // 应用密度适配到 Service
        AutoDensity.applyToContext(this)

        showOverlayView()
    }

    // 启用密度适配
    override fun shouldAdaptDensity(): Boolean = true

    // 强制使用 designWidthDp
    override fun forceDesignWidth(): Boolean = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        removeOverlayView()
    }

    private fun showOverlayView() {
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // 使用适配后的 LayoutInflater
        val view = LayoutInflater.from(this).inflate(R.layout.overlay_service, null)

        // 显示密度信息
        val metrics = AutoDensity.getAdaptedMetrics(this)
        view.findViewById<TextView>(R.id.tv_service_density)?.text = buildString {
            appendLine("=== Service 密度信息 ===")
            appendLine("density: ${metrics.density}")
            appendLine("densityDpi: ${metrics.densityDpi}")
            appendLine("scaledDensity: ${metrics.scaledDensity}")
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = 200
        }

        windowManager.addView(view, params)
        overlayView = view
    }

    private fun removeOverlayView() {
        overlayView?.let {
            val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            windowManager.removeView(it)
        }
        overlayView = null
    }
}
