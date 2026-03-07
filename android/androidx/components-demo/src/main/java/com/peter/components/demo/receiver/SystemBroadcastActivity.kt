package com.peter.components.demo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.databinding.ActivitySystemBroadcastBinding

/**
 * 系统广播与动态注册示例
 * 
 * 知识点：
 * 1. 动态注册：使用 registerReceiver() 在代码中注册
 * 2. 注销：使用 unregisterReceiver() 在 onDestroy 中注销
 * 3. Android 8.0+ 大部分系统广播需要动态注册
 * 4. 使用 Context.RECEIVER_NOT_EXPORTED 提高安全性
 */
class SystemBroadcastActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySystemBroadcastBinding
    private var isRegistered = false

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateNetworkStatus()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySystemBroadcastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegisterDynamic.setOnClickListener {
            registerNetworkReceiver()
        }

        binding.btnUnregisterDynamic.setOnClickListener {
            unregisterNetworkReceiver()
        }

        updateNetworkStatus()
    }

    private fun registerNetworkReceiver() {
        if (!isRegistered) {
            // 使用自定义 Action 演示动态注册
            // 注意：CONNECTIVITY_ACTION 在 Android 8.0+ 不能静态注册
            val filter = IntentFilter("com.peter.components.demo.NETWORK_CHANGE")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(networkReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                registerReceiver(networkReceiver, filter)
            }
            isRegistered = true
            binding.tvNetworkStatus.text = "网络状态：已注册监听\n(演示模式：监听自定义广播)"
            Log.d("SystemBroadcast", "广播已注册")
        }
    }

    private fun unregisterNetworkReceiver() {
        if (isRegistered) {
            unregisterReceiver(networkReceiver)
            isRegistered = false
            binding.tvNetworkStatus.text = "网络状态：已注销监听"
            Log.d("SystemBroadcast", "网络广播已注销")
        }
    }

    private fun updateNetworkStatus() {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        val capabilities = cm.getNetworkCapabilities(network)
        
        val status = when {
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "WiFi"
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "移动数据"
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "以太网"
            else -> "无网络"
        }
        
        binding.tvNetworkStatus.text = "网络状态：$status"
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkReceiver()
    }
}
