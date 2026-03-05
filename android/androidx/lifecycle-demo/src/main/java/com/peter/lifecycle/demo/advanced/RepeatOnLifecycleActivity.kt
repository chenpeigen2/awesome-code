package com.peter.lifecycle.demo.advanced

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.peter.lifecycle.demo.databinding.ActivityRepeatOnLifecycleBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * repeatOnLifecycle 示例
 * 
 * 知识点：
 * 1. repeatOnLifecycle - 生命周期感知的流收集
 * 2. launchWhenX vs repeatOnLifecycle（前者已废弃）
 * 3. 最佳实践 - 在什么生命周期收集流
 * 
 * 为什么不用 launchWhenX:
 * - launchWhenStarted: 在 STOPPED 时挂起，但不会停止上游流
 * - repeatOnLifecycle: 在 STOPPED 时取消并重新启动
 * 
 * 最佳实践：
 * - 在 Fragment 的 onViewCreated 中使用 viewLifecycleOwner
 * - 使用 Lifecycle.State.STARTED 收集流
 */
class RepeatOnLifecycleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRepeatOnLifecycleBinding

    // 模拟的数据流
    private val _dataFlow = MutableStateFlow(0)
    val dataFlow: StateFlow<Int> = _dataFlow.asStateFlow()

    // 模拟的周期性数据
    private val periodicFlow = flow {
        var count = 0
        while (true) {
            delay(1000)
            emit(count++)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepeatOnLifecycleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        collectFlows()
        startPeriodicData()
    }

    private fun setupViews() {
        binding.btnUpdateData.setOnClickListener {
            _dataFlow.value++
        }
    }

    private fun collectFlows() {
        // ❌ 错误方式：直接在 lifecycleScope 中收集
        // 问题：Activity 在后台时仍然会收集，浪费资源
        lifecycleScope.launch {
            dataFlow.collect { value ->
                binding.tvWrongWay.text = "错误方式: $value (始终收集)"
            }
        }

        // ✅ 正确方式：使用 repeatOnLifecycle
        // 在 STARTED 状态开始收集，STOPPED 状态停止收集
        lifecycleScope.launch {
            // 在生命周期至少为 STARTED 时运行
            // 低于 STARTED 时挂起并取消上游流
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                dataFlow.collect { value ->
                    binding.tvCorrectWay.text = "正确方式: $value"
                }
            }
        }

        // 收集周期性数据
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                periodicFlow.collect { count ->
                    binding.tvPeriodic.text = "周期数据: $count"
                    appendLog("收到周期数据: $count")
                }
            }
        }
    }

    private fun startPeriodicData() {
        lifecycleScope.launch {
            var count = 0
            while (true) {
                delay(1000)
                _dataFlow.value = count++
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
        binding.tvLog.text = newLog.takeLast(300)
    }
}

/**
 * repeatOnLifecycle 最佳实践总结：
 * 
 * 1. 在 Activity 中：
 *    lifecycleScope.launch {
 *        repeatOnLifecycle(Lifecycle.State.STARTED) {
 *            viewModel.flow.collect { ... }
 *        }
 *    }
 * 
 * 2. 在 Fragment 中（使用 viewLifecycleOwner）：
 *    viewLifecycleOwner.lifecycleScope.launch {
 *        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
 *            viewModel.flow.collect { ... }
 *        }
 *    }
 * 
 * 3. 状态选择：
 *    - CREATED: 最保守，最早开始
 *    - STARTED: 推荐，Activity 可见时开始
 *    - RESUMED: 最严格，Activity 可交互时开始
 * 
 * 4. 与 launchWhenX 的区别：
 *    - launchWhenStarted: 只是挂起，不取消上游
 *    - repeatOnLifecycle(STARTED): 取消并重启上游
 * 
 * 5. 性能优化：
 *    - 在后台时停止不必要的流收集
 *    - 节省 CPU 和电量
 */
