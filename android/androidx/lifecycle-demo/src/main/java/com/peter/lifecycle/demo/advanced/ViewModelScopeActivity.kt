package com.peter.lifecycle.demo.advanced

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.peter.lifecycle.demo.databinding.ActivityViewmodelScopeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * viewModelScope 示例
 * 
 * 知识点：
 * 1. viewModelScope - ViewModel 的协程作用域
 * 2. 自动取消 - ViewModel 销毁时自动取消协程
 * 3. 线程切换 - withContext 切换线程
 * 
 * 使用场景：
 * - 网络请求
 * - 数据库操作
 * - 耗时计算
 * 
 * 优点：
 * - 不需要手动管理协程生命周期
 * - ViewModel 销毁时自动取消所有协程
 * - 避免内存泄漏
 */
class ViewModelScopeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewmodelScopeBinding
    private val viewModel: ScopeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewmodelScopeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeData()
    }

    private fun setupViews() {
        binding.btnFetchData.setOnClickListener {
            viewModel.fetchData()
        }
        
        binding.btnParallelFetch.setOnClickListener {
            viewModel.parallelFetch()
        }
        
        binding.btnCountdown.setOnClickListener {
            viewModel.startCountdown()
        }
    }

    private fun observeData() {
        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) 
                View.VISIBLE 
            else 
                View.GONE
        }
        
        viewModel.result.observe(this) { result ->
            binding.tvResult.text = result
        }
        
        // 观察 StateFlow
        lifecycleScope.launch {
            viewModel.countdown.collect { count ->
                binding.tvCountdown.text = "倒计时: $count"
            }
        }
    }
}

/**
 * viewModelScope ViewModel
 */
class ScopeViewModel : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    private val _countdown = MutableStateFlow(0)
    val countdown: StateFlow<Int> = _countdown.asStateFlow()

    /**
     * 模拟网络请求
     */
    fun fetchData() {
        // viewModelScope 自动管理协程生命周期
        // ViewModel 销毁时会自动取消
        viewModelScope.launch {
            _loading.value = true
            
            try {
                // 切换到 IO 线程执行网络请求
                val data = withContext(Dispatchers.IO) {
                    // 模拟网络延迟
                    delay(2000)
                    "网络数据 ${System.currentTimeMillis()}"
                }
                
                // 自动切回主线程更新 UI
                _result.value = data
            } catch (e: Exception) {
                _result.value = "错误: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * 并行执行多个任务
     */
    fun parallelFetch() {
        viewModelScope.launch {
            _loading.value = true
            
            try {
                // 使用 async 并行执行
                val deferred1 = async(Dispatchers.IO) {
                    delay(1000)
                    "数据1"
                }
                
                val deferred2 = async(Dispatchers.IO) {
                    delay(1500)
                    "数据2"
                }
                
                val deferred3 = async(Dispatchers.IO) {
                    delay(800)
                    "数据3"
                }
                
                // 等待所有结果
                val results = awaitAll(deferred1, deferred2, deferred3)
                _result.value = "并行结果: ${results.joinToString()}"
            } catch (e: Exception) {
                _result.value = "错误: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * 倒计时
     */
    fun startCountdown() {
        viewModelScope.launch {
            for (i in 10 downTo 0) {
                _countdown.value = i
                delay(1000)
            }
            _countdown.value = 0
        }
    }
    
    /**
     * viewModelScope 生命周期：
     * 
     * ViewModel 创建 -> viewModelScope 创建
     * ViewModel 销毁 -> viewModelScope.cancel() 自动调用
     * 
     * 所有使用 viewModelScope 启动的协程都会被取消
     */
}