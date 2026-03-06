package com.peter.components.demo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.R

/**
 * BroadcastReceiver 示例 - 系统广播
 *
 * ═══════════════════════════════════════════════════════════════
 * BroadcastReceiver 核心概念
 * ═══════════════════════════════════════════════════════════════
 *
 * BroadcastReceiver 是 Android 四大组件之一
 * 用于接收和处理系统或应用发出的广播消息
 *
 * 注册方式：
 * 1. 静态注册：在 Manifest 中声明
 *    - 应用未启动也能接收
 *    - Android 8.0+ 对隐式广播有限制
 *
 * 2. 动态注册：在代码中注册
 *    - 需要在组件销毁时注销
 *    - 可以接收隐式广播
 *
 * ═══════════════════════════════════════════════════════════════
 * 常用系统广播
 * ═══════════════════════════════════════════════════════════════
 *
 * 系统事件：
 * - BOOT_COMPLETED：开机完成
 * - AIRPLANE_MODE：飞行模式切换
 * - BATTERY_CHANGED：电量变化
 * - SCREEN_ON/OFF：屏幕开关
 *
 * 网络相关：
 * - CONNECTIVITY_CHANGE：网络连接变化
 * - WIFI_STATE_CHANGED：WiFi 状态变化
 *
 * 注意：
 * - Android 7.0+ 网络广播不能静态注册
 * - 需要动态注册或使用 NetworkCallback
 */
class SystemBroadcastActivity : AppCompatActivity() {

    private lateinit var tvNetworkStatus: TextView

    /**
     * 网络变化广播接收者
     *
     * 动态注册的 BroadcastReceiver
     */
    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(network)

                val status = when {
                    capabilities == null -> "无网络连接"
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi 连接"
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "移动数据"
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "以太网"
                    else -> "其他网络"
                }

                tvNetworkStatus.text = "网络状态: $status"
            } else {
                @Suppress("DEPRECATION")
                val networkInfo = connectivityManager.activeNetworkInfo
                val status = if (networkInfo?.isConnected == true) {
                    networkInfo.typeName
                } else {
                    "无连接"
                }
                tvNetworkStatus.text = "网络状态: $status"
            }
        }
    }

    /**
     * 自定义广播接收者
     */
    private val customReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.getStringExtra("message") ?: "无消息"
            Toast.makeText(context, "收到自定义广播: $message", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_broadcast)

        tvNetworkStatus = findViewById(R.id.tvNetworkStatus)

        // 注册网络变化广播
        registerNetworkReceiver()

        // 注册自定义广播
        registerCustomReceiver()

        findViewById<Button>(R.id.btnSendBroadcast).setOnClickListener {
            sendCustomBroadcast()
        }

        findViewById<Button>(R.id.btnOrdered).setOnClickListener {
            startActivity(Intent(this, OrderedBroadcastActivity::class.java))
        }

        findViewById<Button>(R.id.btnLocal).setOnClickListener {
            startActivity(Intent(this, LocalBroadcastActivity::class.java))
        }

        // 初始检测网络状态
        checkNetworkStatus()
    }

    /**
     * 动态注册网络变化广播
     *
     * Android 7.0+ 需要 dynamic registration
     */
    private fun registerNetworkReceiver() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, filter)
    }

    /**
     * 注册自定义广播接收者
     */
    private fun registerCustomReceiver() {
        val filter = IntentFilter("com.peter.components.demo.CUSTOM_ACTION")
        registerReceiver(customReceiver, filter)
    }

    /**
     * 发送自定义广播
     *
     * 广播类型：
     * 1. 普通广播：sendBroadcast()
     *    - 异步执行
     *    - 所有接收者同时收到
     *    - 不能被拦截
     *
     * 2. 有序广播：sendOrderedBroadcast()
     *    - 按优先级顺序传递
     *    - 可以被拦截
     *    - 可以修改/添加数据
     *
     * 3. 本地广播：LocalBroadcastManager.sendBroadcast()
     *    - 只在应用内传递
     *    - 更安全高效
     */
    private fun sendCustomBroadcast() {
        val intent = Intent("com.peter.components.demo.CUSTOM_ACTION").apply {
            putExtra("message", "来自 SystemBroadcastActivity")
        }
        sendBroadcast(intent)
    }

    private fun checkNetworkStatus() {
        networkReceiver.onReceive(this, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 注销广播接收者，防止内存泄漏
        unregisterReceiver(networkReceiver)
        unregisterReceiver(customReceiver)
    }
}
