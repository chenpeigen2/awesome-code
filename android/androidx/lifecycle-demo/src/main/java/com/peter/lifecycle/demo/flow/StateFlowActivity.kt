package com.peter.lifecycle.demo.flow

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.peter.lifecycle.demo.databinding.ActivityStateFlowBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * StateFlow 示例
 * 
 * 知识点：
 * 1. StateFlow - 热流，总是有值
 * 2. 与 LiveData 的区别
 * 3. asStateFlow() - 暴露只读 StateFlow
 * 
 * StateFlow vs LiveData:
 * - StateFlow: 需要指定初始值，没有生命周期感知
 * - LiveData: 不需要初始值，自动生命周期感知
 * 
 * 使用场景：
 * - 替代 LiveData 管理状态
 * - 配合 Compose 使用
 * - 需要更强大的 Flow 操作符
 */
class StateFlowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStateFlowBinding
    private val viewModel: StateFlowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStateFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeFlows()
    }

    private fun setupViews() {
        binding.btnIncrement.setOnClickListener {
            viewModel.increment()
        }
        
        binding.btnDecrement.setOnClickListener {
            viewModel.decrement()
        }
        
        binding.btnStartTimer.setOnClickListener {
            viewModel.startTimer()
        }
        
        binding.btnStopTimer.setOnClickListener {
            viewModel.stopTimer()
        }
    }

    private fun observeFlows() {
        // 方式1：使用 lifecycleScope.launch
        // 需要手动处理生命周期
        lifecycleScope.launch {
            viewModel.counter.collect { count ->
                binding.tvCounter.text = "计数: $count"
            }
        }
        
        // 方式2：使用 collectWhileStarted（推荐）
        // 自动在 STARTED 状态开始收集，STOPPED 状态停止
        lifecycleScope.launch {
            viewModel.timer.collect { time ->
                binding.tvTimer.text = "计时器: ${time}s"
            }
        }
    }
}

/**
 * StateFlow ViewModel
 */
class StateFlowViewModel : ViewModel() {

    // 私有可变的 StateFlow
    private val _counter = MutableStateFlow(0)
    
    // 暴露只读的 StateFlow
    // 使用 asStateFlow() 防止外部修改
    val counter: StateFlow<Int> = _counter.asStateFlow()

    // 计时器状态
    private val _timer = MutableStateFlow(0)
    val timer: StateFlow<Int> = _timer.asStateFlow()
    
    // 计时器任务
    private var timerJob: kotlinx.coroutines.Job? = null

    fun increment() {
        // StateFlow.value 获取当前值
        // 使用 value 修改值（相当于 LiveData.setValue）
        _counter.value++
    }

    fun decrement() {
        _counter.value--
    }

    fun startTimer() {
        // 如果已经在运行，先停止
        timerJob?.cancel()
        
        // 使用 viewModelScope，ViewModel 销毁时自动取消
        timerJob = viewModelScope.launch {
            _timer.value = 0
            while (true) {
                delay(1000)
                _timer.value++
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

/**
 * StateFlow 与 Flow 的区别：
 * 
 * 1. Cold Flow vs Hot Flow:
 *    - Flow: 冷流，每个收集者独立执行
 *    - StateFlow: 热流，多个收集者共享同一个流
 * 
 * 2. 状态持有:
 *    - Flow: 不持有状态
 *    - StateFlow: 始终有一个当前值（value 属性）
 * 
 * 3. 重播:
 *    - StateFlow 会向新收集者发送当前值
 */
