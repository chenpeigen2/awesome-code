package com.peter.lifecycle.demo.advanced

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.peter.lifecycle.demo.databinding.ActivityProcessLifecycleBinding

/**
 * ProcessLifecycleOwner 示例
 * 
 * 知识点：
 * 1. ProcessLifecycleOwner - 应用级别的生命周期
 * 2. 监听应用前后台切换
 * 3. 区分 Activity 生命周期和应用生命周期
 * 
 * 使用场景：
 * - 统计应用使用时长
 * - 前后台切换时执行操作
 * - 全局状态管理
 */
class ProcessLifecycleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProcessLifecycleBinding
    private val appLifecycleObserver = AppLifecycleObserver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProcessLifecycleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 注册应用生命周期观察者
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)

        setupViews()
    }

    private fun setupViews() {
        updateUI()
        
        binding.btnRefresh.setOnClickListener {
            updateUI()
        }
    }

    private fun updateUI() {
        val currentState = ProcessLifecycleOwner.get().lifecycle.currentState
        binding.tvAppState.text = """
            应用状态: $currentState
            
            ${if (currentState.isAtLeast(Lifecycle.State.RESUMED)) 
                "应用在前台 ✓" 
            else 
                "应用在后台 ✗"}
            
            按Home键查看前后台切换
        """.trimIndent()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onPause() {
        super.onPause()
        updateUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 移除观察者（可选，ProcessLifecycleOwner 生命周期和应用一致）
        ProcessLifecycleOwner.get().lifecycle.removeObserver(appLifecycleObserver)
    }

    inner class AppLifecycleObserver : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onAppForeground() {
            binding.tvLog.append("\n应用进入前台 - ${System.currentTimeMillis()}")
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onAppBackground() {
            binding.tvLog.append("\n应用进入后台 - ${System.currentTimeMillis()}")
        }
    }
}

/**
 * ProcessLifecycleOwner 详解：
 * 
 * 1. 生命周期事件：
 *    - ON_CREATE: 应用启动时（只会触发一次）
 *    - ON_START: 应用进入前台
 *    - ON_RESUME: 应用获得焦点
 *    - ON_PAUSE: 应用失去焦点
 *    - ON_STOP: 应用进入后台
 *    - ON_DESTROY: 永远不会触发
 * 
 * 2. 与 Activity 生命周期的区别：
 *    - Activity: 单个界面的生命周期
 *    - Process: 整个应用的生命周期
 * 
 * 3. 前后台判断：
 *    - ON_START -> ON_STOP: 应用在前台
 *    - ON_STOP -> ON_START: 应用在后台
 * 
 * 4. 典型使用场景：
 * 
 *    // 应用进入前台时重连 WebSocket
 *    ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {
 *        @OnLifecycleEvent(Lifecycle.Event.ON_START)
 *        fun onForeground() {
 *            webSocketManager.reconnect()
 *        }
 *    })
 * 
 *    // 应用进入后台时断开连接
 *    ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {
 *        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
 *        fun onBackground() {
 *            webSocketManager.disconnect()
 *        }
 *    })
 */

/**
 * 应用生命周期示例
 */
object AppLifecycleManager : LifecycleObserver {
    
    private var startTime: Long = 0
    private var isInForeground: Boolean = false

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onAppCreate() {
        startTime = System.currentTimeMillis()
        android.util.Log.d("AppLifecycle", "应用启动")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppStart() {
        isInForeground = true
        android.util.Log.d("AppLifecycle", "应用进入前台")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppStop() {
        isInForeground = false
        android.util.Log.d("AppLifecycle", "应用进入后台")
    }

    fun isForeground(): Boolean = isInForeground

    fun getUptime(): Long {
        return System.currentTimeMillis() - startTime
    }

    fun init() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }
}
