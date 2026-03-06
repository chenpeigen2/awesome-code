package com.peter.components.demo.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.peter.components.demo.R
import com.peter.components.demo.service.basic.SimpleService
import com.peter.components.demo.service.bind.LocalBindService
import com.peter.components.demo.service.foreground.DownloadService

/**
 * Service 组件示例主界面
 *
 * ═══════════════════════════════════════════════════════════════
 * Service 核心概念
 * ═══════════════════════════════════════════════════════════════
 *
 * Service 是 Android 四大组件之一，用于执行后台操作
 *
 * 特点：
 * 1. 运行在主线程（需要手动创建子线程）
 * 2. 没有界面
 * 3. 可以在后台长时间运行
 * 4. 具有独立的生命周期
 *
 * Service vs Thread：
 * - Service：组件级，系统管理生命周期，适合长时间后台任务
 * - Thread：代码级，应用管理生命周期，适合短时间异步操作
 *
 * 启动方式：
 * 1. startService：启动后独立运行，需手动停止
 * 2. bindService：绑定生命周期，随绑定者销毁
 * 3. startService + bindService：混合使用
 *
 * ═══════════════════════════════════════════════════════════════
 * Android 8.0+ 后台限制
 * ═══════════════════════════════════════════════════════════════
 *
 * Android 8.0 (API 26) 开始限制后台服务：
 * - 应用进入后台后，几分钟内不能创建后台服务
 * - 需要使用 startForegroundService() 启动前台服务
 * - 前台服务必须在 5 秒内调用 startForeground()
 *
 * 替代方案：
 * - WorkManager：适合可延迟的后台任务
 * - JobScheduler：系统调度后台任务
 * - 前台服务：需要立即执行的任务
 */
class ServiceMainActivity : AppCompatActivity() {

    // 绑定服务相关
    private var boundService: LocalBindService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocalBindService.LocalBinder
            boundService = binder.getService()
            isBound = true
            findViewById<Button>(R.id.btnGetServiceData).isEnabled = true
            findViewById<TextView>(R.id.tvServiceData).text = "服务已绑定"
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            boundService = null
            isBound = false
            findViewById<Button>(R.id.btnGetServiceData).isEnabled = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_main)

        setupBasicService()
        setupForegroundService()
        setupBindService()
    }

    /**
     * 基础 Service 示例
     */
    private fun setupBasicService() {
        findViewById<Button>(R.id.btnStartService).setOnClickListener {
            /**
             * startService 启动服务
             *
             * 生命周期：
             * onCreate → onStartCommand → (running) → onDestroy
             *
             * 特点：
             * - 独立于启动者运行
             * - 多次 startService 只会多次调用 onStartCommand
             * - 需要 stopService 或 stopSelf 停止
             */
            val intent = Intent(this, SimpleService::class.java).apply {
                putExtra("command", "START")
            }
            startService(intent)
            Toast.makeText(this, "服务已启动", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnStopService).setOnClickListener {
            val intent = Intent(this, SimpleService::class.java)
            stopService(intent)
            Toast.makeText(this, "服务已停止", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 前台服务示例
     */
    private fun setupForegroundService() {
        findViewById<Button>(R.id.btnForegroundService).setOnClickListener {
            /**
             * 前台服务
             *
             * 特点：
             * - 必须显示通知
             * - 不受后台限制影响
             * - 需要声明 foregroundServiceType (Android 14+)
             *
             * Android 8.0+ 启动方式：
             * 1. 调用 startForegroundService()
             * 2. 服务在 onCreate/onStartCommand 中调用 startForeground()
             */
            val intent = Intent(this, DownloadService::class.java).apply {
                putExtra("download_url", "https://example.com/file.zip")
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }

            Toast.makeText(this, "下载服务已启动", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 绑定服务示例
     */
    private fun setupBindService() {
        findViewById<Button>(R.id.btnBindService).setOnClickListener {
            /**
             * bindService 绑定服务
             *
             * 生命周期：
             * onCreate → onBind → (connected) → onUnbind → onDestroy
             *
             * 特点：
             * - 随绑定者生命周期销毁
             * - 可以通过 Binder 进行通信
             * - 多个客户端可以同时绑定
             */
            val intent = Intent(this, LocalBindService::class.java)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        findViewById<Button>(R.id.btnUnbindService).setOnClickListener {
            if (isBound) {
                unbindService(serviceConnection)
                isBound = false
                findViewById<Button>(R.id.btnGetServiceData).isEnabled = false
                findViewById<TextView>(R.id.tvServiceData).text = "服务已解绑"
            }
        }

        findViewById<Button>(R.id.btnGetServiceData).setOnClickListener {
            boundService?.let { service ->
                val data = service.getData()
                findViewById<TextView>(R.id.tvServiceData).text = "服务数据: $data"
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // 解绑服务防止内存泄漏
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }
}
