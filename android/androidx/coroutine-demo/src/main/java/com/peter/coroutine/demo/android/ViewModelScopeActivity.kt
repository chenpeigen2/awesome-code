package com.peter.coroutine.demo.android

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * viewModelScope 演示
 *
 * 本 Activity 展示 viewModelScope 的使用以及 ViewModel 的生命周期特性。
 *
 * ## 什么是 viewModelScope？
 * viewModelScope 是 ViewModel 的扩展属性，它返回一个与 ViewModel 生命周期绑定的
 * CoroutineScope。当 ViewModel 被清除（onCleared）时，这个 Scope 中的所有协程会自动取消。
 *
 * ## ViewModel 生命周期：
 * - ViewModel 在配置更改（如屏幕旋转）后保持存活
 * - ViewModel 在 Activity/Fragment 真正销毁时才会被清除
 * - viewModelScope 中的协程会自动在 ViewModel 清除时取消
 *
 * ## 使用场景：
 * - 网络请求
 * - 数据库操作
 * - 业务逻辑处理
 * - Flow 收集
 *
 * ## 与 lifecycleScope 的区别：
 * | 特性 | viewModelScope | lifecycleScope |
 * |------|----------------|----------------|
 * | 所属 | ViewModel | Activity/Fragment |
 * | 生命周期 | ViewModel 清除时取消 | Activity/Fragment 销毁时取消 |
 * | 配置更改 | 保持运行 | 取消后重建 |
 *
 * ## 最佳实践：
 * ```kotlin
 * class MyViewModel : ViewModel() {
 *     private val _data = MutableStateFlow("")
 *     val data: StateFlow<String> = _data.asStateFlow()
 *
 *     fun loadData() {
 *         viewModelScope.launch {
 *             _data.value = repository.fetchData()
 *         }
 *     }
 * }
 * ```
 *
 * @see androidx.lifecycle.viewModelScope
 * @see androidx.lifecycle.ViewModel
 */
class ViewModelScopeActivity : AppCompatActivity() {

    private val viewModel: CounterViewModel by viewModels()
    private lateinit var tvLog: TextView
    private lateinit var tvCounter: TextView
    private lateinit var btnStart: Button
    private lateinit var btnReset: Button
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewmodel_scope)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.viewmodel_scope)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)
        tvCounter = findViewById(R.id.tvCounter)
        btnStart = findViewById(R.id.btnStart)
        btnReset = findViewById(R.id.btnReset)

        // 初始日志
        log("========== viewModelScope 演示 ==========\n")
        log("viewModelScope 与 ViewModel 生命周期绑定")
        log("ViewModel 清除时协程自动取消\n")
        log("配置更改（如旋转屏幕）后 ViewModel 保持存活")
        log("协程继续运行，状态得以保持\n")

        // 观察 ViewModel 状态
        observeViewModel()

        // 设置按钮监听
        btnStart.setOnClickListener {
            if (viewModel.isCounting.value) {
                viewModel.stopCounting()
                btnStart.text = "开始计数"
                log("\n停止计数")
            } else {
                viewModel.startCounting()
                btnStart.text = "停止计数"
                log("\n开始计数")
            }
        }

        btnReset.setOnClickListener {
            viewModel.reset()
            btnStart.text = "开始计数"
            log("\n重置计数器")
        }
    }

    /**
     * 观察 ViewModel 状态
     */
    private fun observeViewModel() {
        // 收集计数值
        lifecycleScope.launch {
            viewModel.counter.collect { count ->
                tvCounter.text = "计数: $count"
            }
        }

        // 收集计数状态
        lifecycleScope.launch {
            viewModel.isCounting.collect { isCounting ->
                log("计数状态: ${if (isCounting) "运行中" else "已停止"}")
            }
        }

        // 收集日志
        lifecycleScope.launch {
            viewModel.logMessages.collect { message ->
                log(message)
            }
        }
    }

    private fun log(message: String) {
        runOnUiThread {
            logBuilder.append(message).append("\n")
            tvLog.text = logBuilder.toString()
        }
    }
}

/**
 * 计数器 ViewModel
 *
 * 展示 viewModelScope 的使用：
 * - 自动取消：ViewModel 清除时协程自动取消
 * - 配置更改存活：旋转屏幕后协程继续运行
 */
class CounterViewModel : ViewModel() {

    // 计数值
    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter.asStateFlow()

    // 是否正在计数
    private val _isCounting = MutableStateFlow(false)
    val isCounting: StateFlow<Boolean> = _isCounting.asStateFlow()

    // 日志消息
    private val _logMessages = MutableStateFlow("")
    val logMessages: StateFlow<String> = _logMessages.asStateFlow()

    // 计数任务
    private var countJob: Job? = null

    /**
     * 开始计数
     */
    fun startCounting() {
        if (_isCounting.value) return

        _isCounting.value = true
        log("ViewModel: 开始计数")

        countJob = viewModelScope.launch {
            log("viewModelScope.launch 协程启动")
            while (true) {
                delay(1000)
                _counter.value++
                log("计数: ${_counter.value}")
            }
        }
    }

    /**
     * 停止计数
     */
    fun stopCounting() {
        countJob?.cancel()
        countJob = null
        _isCounting.value = false
        log("ViewModel: 停止计数")
    }

    /**
     * 重置计数器
     */
    fun reset() {
        stopCounting()
        _counter.value = 0
        log("ViewModel: 重置完成")
    }

    /**
     * ViewModel 清除时自动调用
     */
    override fun onCleared() {
        super.onCleared()
        // viewModelScope 中的协程会自动取消
        // 这里演示手动取消
        log("ViewModel onCleared: viewModelScope 协程自动取消")
    }

    private fun log(message: String) {
        val currentLog = _logMessages.value
        _logMessages.value = if (currentLog.isEmpty()) message else "$currentLog\n$message"
    }
}
