package com.peter.components.demo.service

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.peter.components.demo.databinding.ActivityServiceMainBinding
import com.peter.components.demo.service.basic.SimpleService
import com.peter.components.demo.service.bind.LocalBindService
import com.peter.components.demo.service.bind.RemoteBindService
import com.peter.components.demo.service.foreground.DownloadService
import com.peter.components.demo.service.IRemoteService
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

    // 本地绑定服务
    private var isBound = false
    private var localService: LocalBindService? = null

    // 远程绑定服务 (AIDL)
    private var isRemoteBound = false
    private var remoteService: IRemoteService? = null

    // 通知权限请求
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startForegroundServiceInternal()
            appendLog("通知权限已授予")
        } else {
            appendLog("通知权限被拒绝，通知可能无法显示")
            // 仍然启动服务，前台服务通知会显示但可能没有声音/弹窗
            startForegroundServiceInternal()
        }
    }

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

    // 远程服务连接 (AIDL)
    private val remoteServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            // 将 IBinder 转换为 AIDL 接口
            remoteService = IRemoteService.Stub.asInterface(service)
            isRemoteBound = true
            appendLog("远程服务已绑定 (PID: ${android.os.Process.myPid()})")
            binding.btnCallRemoteMethod.isEnabled = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isRemoteBound = false
            remoteService = null
            appendLog("远程服务已断开")
            binding.btnCallRemoteMethod.isEnabled = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBasicService()
        setupForegroundService()
        setupBindService()
        setupRemoteService()
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
            // Android 13+ 需要请求通知权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                    startForegroundServiceInternal()
                } else {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                startForegroundServiceInternal()
            }
        }

        binding.btnStopForeground.setOnClickListener {
            val intent = Intent(this, DownloadService::class.java).apply {
                action = DownloadService.ACTION_STOP
            }
            startService(intent)
            appendLog("停止前台服务")
        }
    }

    private fun startForegroundServiceInternal() {
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

    /**
     * 远程服务绑定 (AIDL)
     *
     * 知识点：
     * 1. 使用 AIDL 生成的 Stub.asInterface() 转换 IBinder
     * 2. 跨进程调用方法时需要注意线程阻塞
     * 3. 服务可以在独立进程运行 (android:process=":remote")
     */
    private fun setupRemoteService() {
        binding.btnBindRemoteService.setOnClickListener {
            val intent = Intent(this, RemoteBindService::class.java)
            // 使用 BIND_AUTO_CREATE 标志，服务在绑定时自动创建
            bindService(intent, remoteServiceConnection, Context.BIND_AUTO_CREATE)
            appendLog("绑定远程服务请求")
        }

        binding.btnUnbindRemoteService.setOnClickListener {
            if (isRemoteBound) {
                unbindService(remoteServiceConnection)
                isRemoteBound = false
                remoteService = null
                binding.btnCallRemoteMethod.isEnabled = false
                appendLog("解绑远程服务")
            }
        }

        binding.btnCallRemoteMethod.setOnClickListener {
            remoteService?.let { service ->
                // 注意：AIDL 调用是同步的，不应该在主线程执行耗时操作
                // 这里演示简单调用，实际项目中应使用协程或后台线程
                try {
                    val randomNumber = service.getRandomNumber()
                    val uptime = service.getUptimeSeconds()
                    val response = service.sendMessage("Hello from client!")
                    appendLog("远程调用结果:\n- 随机数: $randomNumber\n- 运行时长: ${uptime}秒\n- 响应: $response")
                } catch (e: Exception) {
                    appendLog("远程调用失败: ${e.message}")
                }
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
        if (isRemoteBound) {
            unbindService(remoteServiceConnection)
            isRemoteBound = false
        }
    }
}