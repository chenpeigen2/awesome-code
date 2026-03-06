package com.peter.window.demo.practice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.peter.window.demo.databinding.ActivityDynamicWindowBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 实战篇：动态窗口
 * 
 * 本Activity演示动态管理多个窗口的场景：
 * 
 * 1. 多窗口管理
 * 2. 窗口优先级
 * 3. 窗口动画
 * 4. 窗口生命周期
 * 
 * 动态窗口的应用场景：
 * - 视频悬浮窗
 * - 聊天悬浮球
 * - 系统提示窗
 * - 游戏辅助窗口
 * 
 * 动态窗口的核心设计：
 * 
 * ```
 * WindowController
 *     ├── WindowManager (系统服务)
 *     ├── WindowPool (窗口池)
 *     │       ├── WindowRecord
 *     │       └── View 缓存
 *     └── WindowAnimator (窗口动画)
 * ```
 */
class DynamicWindowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDynamicWindowBinding
    private val sb = StringBuilder()
    
    private var floatingService: FloatingWindowService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as FloatingWindowService.LocalBinder
            floatingService = binder.getService()
            isBound = true
            updateServiceStatus()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            floatingService = null
            isBound = false
            updateServiceStatus()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDynamicWindowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showDynamicWindowInfo()
    }

    private fun setupListeners() {
        binding.btnStartService.setOnClickListener { startFloatingService() }
        binding.btnStopService.setOnClickListener { stopFloatingService() }
        binding.btnShowWindow.setOnClickListener { showFloatingWindow() }
        binding.btnHideWindow.setOnClickListener { hideFloatingWindow() }
        binding.btnShowMultiple.setOnClickListener { showMultipleWindows() }
        binding.btnAnimateWindow.setOnClickListener { animateWindow() }
    }

    private fun showDynamicWindowInfo() {
        sb.clear()
        sb.appendLine("=== 动态窗口 ===\n")
        sb.appendLine("动态窗口的管理要点：\n")
        sb.appendLine("1. 使用 Service 管理窗口")
        sb.appendLine("   - 独立于 Activity 生命周期")
        sb.appendLine("   - 可以跨进程操作\n")
        sb.appendLine("2. 窗口池管理")
        sb.appendLine("   - 复用窗口减少创建开销")
        sb.appendLine("   - 统一管理窗口状态\n")
        sb.appendLine("3. 窗口优先级")
        sb.appendLine("   - 控制窗口显示顺序")
        sb.appendLine("   - 处理窗口冲突\n")
        sb.appendLine("4. 窗口动画")
        sb.appendLine("   - 进入/退出动画")
        sb.appendLine("   - 交互动画\n")
        
        binding.tvInfo.text = sb.toString()
        updateServiceStatus()
    }

    private fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else true
    }

    private fun startFloatingService() {
        if (!hasOverlayPermission()) {
            Toast.makeText(this, "请先获取悬浮窗权限", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, FloatingWindowService::class.java)
        startService(intent)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
        
        binding.tvResult.text = "服务启动中..."
    }

    private fun stopFloatingService() {
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
        
        val intent = Intent(this, FloatingWindowService::class.java)
        stopService(intent)
        floatingService = null
        
        updateServiceStatus()
        binding.tvResult.text = "服务已停止"
    }

    private fun showFloatingWindow() {
        if (!isBound || floatingService == null) {
            Toast.makeText(this, "请先启动服务", Toast.LENGTH_SHORT).show()
            return
        }
        
        floatingService?.showFloatingWindow("示例窗口")
        binding.tvResult.text = "显示悬浮窗"
    }

    private fun hideFloatingWindow() {
        if (!isBound || floatingService == null) {
            Toast.makeText(this, "请先启动服务", Toast.LENGTH_SHORT).show()
            return
        }
        
        floatingService?.hideFloatingWindow()
        binding.tvResult.text = "隐藏悬浮窗"
    }

    private fun showMultipleWindows() {
        if (!isBound || floatingService == null) {
            Toast.makeText(this, "请先启动服务", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 依次显示多个窗口
        lifecycleScope.launch {
            for (i in 1..3) {
                floatingService?.showFloatingWindow("窗口 $i")
                delay(500)
            }
        }
        
        binding.tvResult.text = "显示多个窗口"
    }

    private fun animateWindow() {
        if (!isBound || floatingService == null) {
            Toast.makeText(this, "请先启动服务", Toast.LENGTH_SHORT).show()
            return
        }
        
        floatingService?.animateWindow()
        binding.tvResult.text = "执行窗口动画"
    }

    private fun updateServiceStatus() {
        sb.clear()
        sb.appendLine("服务状态: ${if (isBound) "已绑定" else "未绑定"}")
        sb.appendLine("悬浮窗状态: ${if (floatingService?.isWindowShowing() == true) "显示中" else "隐藏"}")
        binding.tvStatus.text = sb.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
