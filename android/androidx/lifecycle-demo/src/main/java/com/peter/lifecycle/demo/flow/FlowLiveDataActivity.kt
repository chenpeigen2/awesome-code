package com.peter.lifecycle.demo.flow

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import com.peter.lifecycle.demo.databinding.ActivityFlowLiveDataBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.shareIn

/**
 * Flow 与 LiveData 互转示例
 * 
 * 知识点：
 * 1. Flow.asLiveData() - Flow 转 LiveData
 * 2. LiveData.asFlow() - LiveData 转 Flow
 * 3. stateIn / shareIn - Cold Flow 转 Hot Flow
 * 
 * 使用场景：
 * - 迁移项目时保持兼容
 * - 需要生命周期感知的 Flow
 * - 结合两者的优势
 */
class FlowLiveDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlowLiveDataBinding
    private val viewModel: FlowLiveDataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlowLiveDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeData()
    }

    private fun setupViews() {
        binding.btnUpdateFlow.setOnClickListener {
            viewModel.updateFlowData()
        }
        
        binding.btnUpdateLiveData.setOnClickListener {
            viewModel.updateLiveDataData()
        }
    }

    private fun observeData() {
        // 1. 观察 StateFlow（需要手动处理生命周期）
        lifecycleScope.launch {
            viewModel.stateFlow.collect { value ->
                binding.tvStateFlow.text = "StateFlow: $value"
            }
        }
        
        // 2. 观察 Flow 转换的 LiveData（自动生命周期感知）
        viewModel.flowAsLiveData.observe(this) { value ->
            binding.tvFlowAsLiveData.text = "Flow.asLiveData: $value"
        }
        
        // 3. 观察普通 LiveData
        viewModel.liveData.observe(this) { value ->
            binding.tvLiveData.text = "LiveData: $value"
        }
        
        // 4. 观察 LiveData 转换的 Flow
        lifecycleScope.launch {
            viewModel.liveDataAsFlow.collect { value ->
                binding.tvLiveDataAsFlow.text = "LiveData.asFlow: $value"
            }
        }
    }
}

/**
 * Flow LiveData ViewModel
 */
class FlowLiveDataViewModel : ViewModel() {

    // StateFlow
    private val _stateFlow = MutableStateFlow(0)
    val stateFlow: StateFlow<Int> = _stateFlow.asStateFlow()

    // Flow 转 LiveData
    // 自动获得生命周期感知能力
    val flowAsLiveData: LiveData<Int> = _stateFlow.asLiveData()

    // 普通 LiveData
    private val _liveData = MutableLiveData(0)
    val liveData: LiveData<Int> = _liveData

    // LiveData 转 Flow
    val liveDataAsFlow: Flow<Int> = _liveData.asFlow()

    fun updateFlowData() {
        _stateFlow.value++
    }

    fun updateLiveDataData() {
        _liveData.value = (_liveData.value ?: 0) + 1
    }
}

/**
 * Flow 转 LiveData 的其他方式
 */
class FlowLiveDataExamples {

    /**
     * 1. Flow.asLiveData()
     * 
     * Cold Flow -> LiveData
     * 每次观察都会触发 Flow 执行
     */
    fun flowAsLiveData(): LiveData<String> {
        return flow {
            repeat(10) {
                emit("Item $it")
                delay(1000)
            }
        }.asLiveData()
    }

    /**
     * 2. liveData 构建器
     * 
     * 可以在 liveData 块中使用协程
     * emit() 发送数据
     */
    fun liveDataBuilder(): LiveData<String> = liveData {
        repeat(10) {
            emit("Item $it")
            delay(1000)
        }
    }

    /**
     * 3. Cold Flow 转 Hot Flow
     * 
     * stateIn: 转换为 StateFlow（必须有初始值）
     * shareIn: 转换为 SharedFlow（不需要初始值）
     */
    fun coldToHotFlow(): Pair<StateFlow<String>, Flow<String>> {
        val coldFlow = flow {
            repeat(10) {
                emit("Item $it")
                delay(1000)
            }
        }

        // stateIn 参数:
        // - scope: 协程作用域
        // - started: 何时开始（LAZY, EAGERLY, WhileSubscribed）
        // - initialValue: 初始值
        val stateFlow = coldFlow.stateIn(
            scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default),
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = "Initial"
        )

        // shareIn 参数:
        // - scope: 协程作用域
        // - started: 何时开始
        // - replay: 重播数量
        val sharedFlow = coldFlow.shareIn(
            scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default),
            started = kotlinx.coroutines.flow.SharingStarted.Lazily,
            replay = 1
        )

        return stateFlow to sharedFlow
    }
}
