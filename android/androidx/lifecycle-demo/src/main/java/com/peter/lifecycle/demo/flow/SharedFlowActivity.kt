package com.peter.lifecycle.demo.flow

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.lifecycleScope
import com.peter.lifecycle.demo.databinding.ActivitySharedFlowBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * SharedFlow 示例
 * 
 * 知识点：
 * 1. SharedFlow - 事件流，可以有多个收集者
 * 2. replay - 重播给新订阅者的历史事件数量
 * 3. 事件总线模式
 * 
 * SharedFlow vs StateFlow:
 * - SharedFlow: 适合事件，不需要初始值，支持重播
 * - StateFlow: 适合状态，必须有初始值，总是有当前值
 * 
 * 使用场景：
 * - 事件总线
 * - 一次性事件（Toast、导航）
 * - 多个观察者订阅同一事件
 */
class SharedFlowActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySharedFlowBinding
    private val viewModel: SharedFlowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySharedFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeEvents()
    }

    private fun setupViews() {
        binding.btnSendEvent.setOnClickListener {
            val event = binding.etEvent.text.toString()
            viewModel.sendEvent(event)
            binding.etEvent.text?.clear()
        }
        
        binding.btnSendNotification.setOnClickListener {
            viewModel.showNotification("这是一条通知消息 ${System.currentTimeMillis()}")
        }
    }

    private fun observeEvents() {
        // 收集事件流
        lifecycleScope.launch {
            viewModel.events.collect { event ->
                appendLog("收到事件: $event")
            }
        }
        
        // 收集通知流
        lifecycleScope.launch {
            viewModel.notifications.collect { message ->
                binding.tvNotification.text = "通知: $message"
                // 3秒后清除
                delay(3000)
                binding.tvNotification.text = ""
            }
        }
    }

    private fun appendLog(message: String) {
        val currentLog = binding.tvLog.text.toString()
        val newLog = if (currentLog.isEmpty()) {
            message
        } else {
            "$currentLog\n$message"
        }
        binding.tvLog.text = newLog.takeLast(500) // 只保留最后500字符
    }
}

/**
 * SharedFlow ViewModel
 */
class SharedFlowViewModel : ViewModel() {

    // 事件流
    // replay = 0: 新订阅者不会收到历史事件（适合一次性事件）
    // replay = 1: 新订阅者会收到最近1个事件
    private val _events = MutableSharedFlow<String>(
        replay = 0, // 不重播
        extraBufferCapacity = 10 // 额外缓冲容量
    )
    val events: SharedFlow<String> = _events.asSharedFlow()

    // 通知流
    // replay = 1: 新订阅者会收到最近的通知
    private val _notifications = MutableSharedFlow<String>(
        replay = 1,
        extraBufferCapacity = 0
    )
    val notifications: SharedFlow<String> = _notifications.asSharedFlow()

    /**
     * 发送事件
     * 
     * emit() vs tryEmit():
     * - emit: 挂起函数，如果缓冲区满会挂起
     * - tryEmit: 非挂起函数，如果缓冲区满会返回 false
     */
    fun sendEvent(event: String) {
        // 使用 tryEmit 发送非挂起
        _events.tryEmit(event)
        
        // 或者使用 emit（需要在协程中）
        // viewModelScope.launch {
        //     _events.emit(event)
        // }
    }

    fun showNotification(message: String) {
        viewModelScope.launch {
            _notifications.emit(message)
        }
    }
}

/**
 * SharedFlow 构造参数详解：
 * 
 * 1. replay: Int = 0
 *    - 新订阅者可以收到多少个历史事件
 *    - 0: 不重播（默认，适合事件）
 *    - 1+: 重播最近n个事件
 * 
 * 2. extraBufferCapacity: Int = 0
 *    - 除 replay 外的额外缓冲容量
 *    - 用于处理背压
 * 
 * 3. onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
 *    - 缓冲区满时的策略
 *    - SUSPEND: 挂起发送者
 *    - DROP_OLDEST: 丢弃最旧的数据
 *    - DROP_LATEST: 丢弃最新的数据
 */

/**
 * 事件总线示例
 */
object EventBus {
    private val _events = MutableSharedFlow<Any>(
        replay = 0,
        extraBufferCapacity = 64
    )
    val events: SharedFlow<Any> = _events.asSharedFlow()

    suspend fun send(event: Any) {
        _events.emit(event)
    }

    fun trySend(event: Any): Boolean {
        return _events.tryEmit(event)
    }
}
