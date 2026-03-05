package com.peter.lifecycle.demo.basic

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.peter.lifecycle.demo.databinding.ActivityCustomLifecycleOwnerBinding

/**
 * 自定义 LifecycleOwner 示例
 * 
 * 知识点：
 * 1. LifecycleRegistry - 生命周期注册中心
 * 2. 自定义类实现 LifecycleOwner 接口
 * 3. 手动派发生命周期事件
 * 
 * 适用场景：
 * - 自定义 View 需要感知生命周期
 * - 非 Activity/Fragment 组件需要管理生命周期
 */
class CustomLifecycleOwnerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomLifecycleOwnerBinding
    private lateinit var customOwner: CustomLifecycleOwner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomLifecycleOwnerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customOwner = CustomLifecycleOwner()
        
        // 注册观察者
        customOwner.lifecycle.addObserver(CustomObserver())
        
        setupButtons()
    }

    private fun setupButtons() {
        binding.btnCreate.setOnClickListener {
            customOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            updateStatus()
        }
        
        binding.btnStart.setOnClickListener {
            customOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
            updateStatus()
        }
        
        binding.btnResume.setOnClickListener {
            customOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            updateStatus()
        }
        
        binding.btnPause.setOnClickListener {
            customOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            updateStatus()
        }
        
        binding.btnStop.setOnClickListener {
            customOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
            updateStatus()
        }
        
        binding.btnDestroy.setOnClickListener {
            customOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            updateStatus()
        }
    }

    private fun updateStatus() {
        binding.tvStatus.text = "当前状态: ${customOwner.lifecycle.currentState}"
    }
}

/**
 * 自定义 LifecycleOwner
 */
class CustomLifecycleOwner : LifecycleOwner {

    // 使用 LifecycleRegistry 管理生命周期
    private val lifecycleRegistry = LifecycleRegistry(this)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    /**
     * 处理生命周期事件
     * 
     * 事件转换规则：
     * ON_CREATE -> CREATED
     * ON_START -> STARTED
     * ON_RESUME -> RESUMED
     * ON_PAUSE -> STARTED
     * ON_STOP -> CREATED
     * ON_DESTROY -> DESTROYED
     */
    fun handleLifecycleEvent(event: Lifecycle.Event) {
        lifecycleRegistry.handleLifecycleEvent(event)
    }
    
    /**
     * 直接设置状态（不推荐，建议使用 handleLifecycleEvent）
     */
    fun setCurrentState(state: Lifecycle.State) {
        lifecycleRegistry.currentState = state
    }
}

/**
 * 自定义 Observer
 */
class CustomObserver : DefaultLifecycleObserver {
    private val TAG = "CustomObserver"

    override fun onCreate(owner: LifecycleOwner) {
        Log.d(TAG, "自定义生命周期: onCreate")
    }

    override fun onStart(owner: LifecycleOwner) {
        Log.d(TAG, "自定义生命周期: onStart")
    }

    override fun onResume(owner: LifecycleOwner) {
        Log.d(TAG, "自定义生命周期: onResume")
    }

    override fun onPause(owner: LifecycleOwner) {
        Log.d(TAG, "自定义生命周期: onPause")
    }

    override fun onStop(owner: LifecycleOwner) {
        Log.d(TAG, "自定义生命周期: onStop")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.d(TAG, "自定义生命周期: onDestroy")
    }
}