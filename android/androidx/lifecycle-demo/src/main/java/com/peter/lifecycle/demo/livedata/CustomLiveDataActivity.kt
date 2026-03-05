package com.peter.lifecycle.demo.livedata

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.peter.lifecycle.demo.databinding.ActivityCustomLivedataBinding

/**
 * 自定义 LiveData 示例
 * 
 * 知识点：
 * 1. 继承 LiveData 实现自定义数据源
 * 2. onActive / onInactive 管理资源
 * 3. 生命周期感知的资源管理
 * 
 * 使用场景：
 * - 网络状态监听
 * - 位置更新
 * - 传感器数据
 * - 系统广播
 */
class CustomLiveDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomLivedataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomLivedataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 使用自定义 LiveData
        NetworkLiveData.getInstance(this).observe(this) { isConnected ->
            binding.tvNetworkStatus.text = if (isConnected) {
                "网络已连接 ✓"
            } else {
                "网络已断开 ✗"
            }
            binding.tvNetworkStatus.setTextColor(
                if (isConnected) getColor(com.peter.lifecycle.demo.R.color.success)
                else getColor(com.peter.lifecycle.demo.R.color.error)
            )
        }
        
        // 使用电池状态 LiveData
        BatteryLiveData.getInstance(this).observe(this) { level ->
            binding.tvBatteryLevel.text = "电量: $level%"
        }
    }
}

/**
 * 网络状态 LiveData
 * 
 * 特点：
 * - 单例模式，全局共享
 * - 自动注册和注销广播接收器
 * - 只在有观察者时才监听网络状态
 */
class NetworkLiveData private constructor(
    private val context: Context
) : LiveData<Boolean>() {

    companion object {
        @Volatile
        private var instance: NetworkLiveData? = null
        
        fun getInstance(context: Context): NetworkLiveData {
            return instance ?: synchronized(this) {
                instance ?: NetworkLiveData(context.applicationContext).also { 
                    instance = it 
                }
            }
        }
    }

    private val connectivityManager = 
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: android.net.Network) {
            postValue(true)
        }

        override fun onLost(network: android.net.Network) {
            postValue(false)
        }
    }

    /**
     * 当有活跃观察者时调用
     * 在这里注册网络监听
     */
    override fun onActive() {
        super.onActive()
        
        // 获取当前网络状态
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        val isConnected = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) 
            ?: false
        value = isConnected
        
        // 注册网络回调
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    /**
     * 当没有活跃观察者时调用
     * 在这里注销网络监听
     */
    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}

/**
 * 电池状态 LiveData
 */
class BatteryLiveData private constructor(
    private val context: Context
) : LiveData<Int>() {

    companion object {
        @Volatile
        private var instance: BatteryLiveData? = null
        
        fun getInstance(context: Context): BatteryLiveData {
            return instance ?: synchronized(this) {
                instance ?: BatteryLiveData(context.applicationContext).also { 
                    instance = it 
                }
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val level = it.getIntExtra("level", 0)
                postValue(level)
            }
        }
    }

    override fun onActive() {
        super.onActive()
        
        // 注册电池状态广播
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(receiver, filter)
        batteryStatus?.let {
            val level = it.getIntExtra("level", -1)
            value = level
        }
    }

    override fun onInactive() {
        super.onInactive()
        context.unregisterReceiver(receiver)
    }
}

/**
 * 自定义 LiveData 的关键点：
 * 
 * 1. onActive() - 当有观察者（LifecycleOwner 处于活跃状态）时调用
 *    - 在这里开始监听数据源
 * 
 * 2. onInactive() - 当没有活跃观察者时调用
 *    - 在这里停止监听，释放资源
 * 
 * 3. setValue() - 在主线程更新数据
 *    postValue() - 在后台线程更新数据
 * 
 * 4. 单例模式 - 对于全局共享的数据源，使用单例
 */
