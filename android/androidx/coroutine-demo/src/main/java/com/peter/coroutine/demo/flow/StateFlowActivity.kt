package com.peter.coroutine.demo.flow

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * StateFlow 演示
 *
 * 本 Activity 展示 StateFlow 的使用方法以及与 LiveData 的对比。
 *
 * ## 什么是 StateFlow？
 * StateFlow 是一个热流（Hot Flow），它始终有一个值，并且可以向多个收集者
 * 广播状态更新。它是 LiveData 的协程替代品。
 *
 * ## StateFlow vs LiveData 对比：
 *
 * | 特性 | StateFlow | LiveData |
 * |------|-----------|----------|
 * | 初始值 | 必须提供 | 可选 |
 * | 线程安全 | 是 | 是 |
 * | 背压处理 | 支持 | 不支持 |
 * | 操作符 | 丰富的 Flow 操作符 | 有限的转换 |
 * | 生命周期感知 | 需要配合 repeatOnLifecycle | 内置支持 |
 * | 多收集者 | 支持 | 支持 |
 *
 * ## StateFlow 的特点：
 *
 * ### 1. 热流特性
 * - 始终处于活跃状态，即使没有收集者
 * - 新的收集者会立即收到当前值
 *
 * ### 2. 值的粘性
 * - 新订阅者会立即收到最后一个值
 * - 与 LiveData 的粘性事件类似
 *
 * ### 3. 线程安全
 * - 可以安全地从任何线程更新值
 * - 使用 update {} 函数进行原子更新
 *
 * ## 最佳实践：
 * - 使用 MutableStateFlow 作为私有可变状态
 * - 暴露 StateFlow（不可变）给外部
 * - 配合 repeatOnLifecycle 收集
 *
 * @see kotlinx.coroutines.flow.StateFlow
 * @see kotlinx.coroutines.flow.MutableStateFlow
 */
class StateFlowActivity : AppCompatActivity() {

    private val viewModel: StateFlowViewModel by viewModels()
    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_state_flow)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.state_flow)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)
        val etInput = findViewById<EditText>(R.id.etInput)
        val tvDisplay = findViewById<TextView>(R.id.tvDisplay)

        // 设置输入监听
        etInput.addTextChangedListener { text ->
            viewModel.updateInput(text?.toString() ?: "")
        }

        // 收集输入状态
        lifecycleScope.launch {
            viewModel.inputState.collect { input ->
                tvDisplay.text = "当前输入: $input"
                log("状态更新: \"$input\"")
            }
        }

        // 收集计数状态
        lifecycleScope.launch {
            viewModel.counterState.collect { count ->
                log("计数器更新: $count")
            }
        }

        // 初始日志
        log("========== StateFlow 演示 ==========\n")
        log("StateFlow 是热流，始终有值")
        log("初始值会立即发送给收集者\n")
        log("在输入框输入文字观察状态变化\n")
    }

    private fun log(message: String) {
        logBuilder.append(message).append("\n")
        tvLog.text = logBuilder.toString()
    }
}

/**
 * StateFlow ViewModel 示例
 *
 * 展示如何在 ViewModel 中正确使用 StateFlow：
 * - 私有 MutableStateFlow 用于内部修改
 * - 公开 StateFlow 用于外部观察
 */
class StateFlowViewModel : ViewModel() {

    // 私有可变状态
    private val _inputState = MutableStateFlow("")
    private val _counterState = MutableStateFlow(0)

    // 公开不可变状态
    val inputState: StateFlow<String> = _inputState.asStateFlow()
    val counterState: StateFlow<Int> = _counterState.asStateFlow()

    // 计数器任务
    private var counterJob: Job? = null

    init {
        // 模拟计数器自动更新
        startCounter()
    }

    /**
     * 更新输入状态
     */
    fun updateInput(input: String) {
        // 使用 value 属性直接设置
        _inputState.value = input
    }

    /**
     * 使用 update 函数进行原子更新
     */
    fun incrementCounter() {
        _counterState.update { it + 1 }
    }

    /**
     * 启动自动计数器
     */
    private fun startCounter() {
        counterJob = kotlinx.coroutines.GlobalScope.launch {
            var count = 0
            while (true) {
                delay(2000) // 每2秒更新一次
                count++
                _counterState.value = count
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        counterJob?.cancel()
    }
}
