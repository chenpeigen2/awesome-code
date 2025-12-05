package com.example.appdisplayapp

import android.animation.ObjectAnimator
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.display.DisplayManager
import android.os.Handler
import android.os.Looper
import android.view.Display
import android.view.Gravity
import android.view.SurfaceControlViewHost
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView

class RendererService : Service() {

    override fun onBind(intent: Intent?) = binder

    private val binder = object : ISurfacePackageReceiver.Stub() {

        override fun onSurfacePackageReady(surfacePackage: SurfaceControlViewHost.SurfacePackage?) {
            if (surfacePackage == null) return

            Handler(Looper.getMainLooper()).post {
                startRendering(surfacePackage)
            }
        }
    }

    private fun startRendering(surfacePackage: SurfaceControlViewHost.SurfacePackage) {
        val ctx = this
        val display = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val defaultDisplay = display.getDisplay(Display.DEFAULT_DISPLAY)

        val host = SurfaceControlViewHost(ctx, defaultDisplay, binder)
        val root = createRemoteContent()

        host.setView(
            root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // 使用反射调用 setSurfacePackage 方法
        try {
            val method = host::class.java.getMethod(
                "setSurfacePackage",
                SurfaceControlViewHost.SurfacePackage::class.java
            )
            method.invoke(host, surfacePackage)
        } catch (e: Exception) {
            e.printStackTrace()
            // 处理异常
        }
    }

    private fun createRemoteContent(): View {
        return FrameLayout(this).apply {

            setBackgroundColor(Color.TRANSPARENT)

            val tv = TextView(context).apply {
                text = "跨进程渲染成功！"
                textSize = 18f
                setTextColor(Color.WHITE)
                setPadding(30, 30, 30, 30)
                setBackgroundColor(0x6600FF00)
                gravity = Gravity.CENTER
            }

            addView(
                tv,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER
                )
            )

            // 加效果动画
            ObjectAnimator.ofFloat(tv, "alpha", 0f, 1f).apply {
                duration = 800
                start()
            }
        }
    }
}
