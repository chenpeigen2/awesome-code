package com.peter.components.demo.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.peter.components.demo.databinding.ActivityServiceMainBinding
import com.peter.components.demo.service.basic.SimpleService
import com.peter.components.demo.service.bind.LocalBindService
import com.peter.components.demo.service.foreground.DownloadService
import kotlinx.coroutines.launch

/**
 * Service 组件示例主入口
 * 
 * 知识点：
 * 1. 启动服务 - startService/stopService
 * 2. 绑定服务 - bindService/unbindService
 * 3. 前台服务 - startForeground
 * 4. 服务生命周期 - onCreate/onStartCommand/onDestroy
 */
class ServiceMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServiceMainBinding
    private val logBuilder = StringBuilder()
    private var isBound = false
    private var localService: LocalBindService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocalBindService.LocalBinder
            localService = binder.getService()
            isBound = true
            appendLog("服务已绑定")
            binding.btnCallMethod.isEnabled = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
            localService = null
            appendLog("服务已断开")
            binding.btnCallMethod.isEnabled = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBasicService()
        setupForegroundService()
        setupBindService()
        setupLog()

        // 监听前台服务的进度
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                DownloadService.downloadProgress.collect { progress ->
                    if (progress in 0..100) {
                        appendLog("下载进度: $progress%")
                    }
                }
            }
        }
    }

    private fun setupBasicService() {
        binding.btnStartService.setOnClickListener {
            val intent = Intent(this, SimpleService::class.java)
            startService(intent)
            appendLog("启动基础服务")
        }

        binding.btnStopService.setOnClickListener {
            val intent = Intent(this, SimpleService::class.java)
            stopService(intent)
            appendLog("停止基础服务")
        }
    }

    private fun setupForegroundService() {
        binding.btnStartForeground.setOnClickListener {
            val intent = Intent(this, DownloadService::class.java).apply {
                action = DownloadService.ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            appendLog("启动前台服务")
        }

        binding.btnStopForeground.setOnClickListener {
            val intent = Intent(this, DownloadService::class.java).apply {
                action = DownloadService.ACTION_STOP
            }
            startService(intent)
            appendLog("停止前台服务")
        }
    }

    private fun setupBindService() {
        binding.btnBindService.setOnClickListener {
            val intent = Intent(this, LocalBindService::class.java)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            appendLog("绑定服务请求")
        }

        binding.btnUnbindService.setOnClickListener {
            if (isBound) {
                unbindService(serviceConnection)
                isBound = false
                binding.btnCallMethod.isEnabled = false
                appendLog("解绑服务")
            }
        }

        binding.btnCallMethod.setOnClickListener {
            localService?.let { service ->
                val result = service.getRandomNumber()
                appendLog("调用服务方法，结果: $result")
            }
        }
    }

    private fun setupLog() {
        binding.btnClearLog.setOnClickListener {
            logBuilder.clear()
            binding.tvLog.text = ""
        }
    }

    private fun appendLog(message: String) {
        logBuilder.append("$message\n")
        binding.tvLog.text = logBuilder.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }
}