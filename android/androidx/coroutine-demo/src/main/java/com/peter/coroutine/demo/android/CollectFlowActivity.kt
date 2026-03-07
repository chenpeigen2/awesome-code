package com.peter.coroutine.demo.android

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Flow 收集最佳实践演示
 *
 * 本 Activity 展示在 Android 中收集 Flow 的几种方式及其区别。
 *
 * ## Flow 收集方式对比：
 *
 * ### 1. 直接在 lifecycleScope 中收集（不推荐）
 * ```kotlin
 * lifecycleScope.launch {
 *     flow.collect { ... }  // 即使 Activity 在后台也会继续收集
 * }
 * ```
 * 问题：Activity 不在前台时也会收集，浪费资源。
 *
 * ### 2. 使用 repeatOnLifecycle（推荐）
 * ```kotlin
 * lifecycleScope.launch {
 *     repeatOnLifecycle(Lifecycle.State.STARTED) {
 *         flow.collect { ... }  // 只在 STARTED 到 STOPPED 之间收集
 *     }
 * }
 * ```
 * 优点：安全且高效，只在生命周期处于指定状态时收集。
 *
 * ### 3. 使用 flowWithLifecycle（推荐）
 * ```kotlin
 * lifecycleScope.launch {
 *     flow.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
 *         .collect { ... }
 * }
 * ```
 * 优点：API 更简洁，功能与 repeatOnLifecycle 相同。
 *
 * ## Lifecycle.State 参数说明：
 * - CREATED: onCreate 到 onDestroy
 * - STARTED: onStart 到 onStop（推荐用于 UI 更新）
 * - RESUMED: onResume 到 onPause
 *
 * ## 最佳实践：
 * - UI 相关的 Flow 收集使用 Lifecycle.State.STARTED
 * - 使用 repeatOnLifecycle 或 flowWithLifecycle
 * - 不要直接在 lifecycleScope.launch 中收集
 *
 * @see androidx.lifecycle.repeatOnLifecycle
 * @see androidx.lifecycle.flowWithLifecycle
 */
class CollectFlowActivity : AppCompatActivity() {

    private val viewModel: FlowViewModel by viewModels()
    private lateinit var tvLog: TextView
    private lateinit var tvRepeatOnLifecycle: TextView
    private lateinit var tvFlowWithLifecycle: TextView
    private lateinit var tvDirectCollect: TextView
    private lateinit var btnToggle: Button
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect_flow)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.collect_flow)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)
        tvRepeatOnLifecycle = findViewById(R.id.tvRepeatOnLifecycle)
        tvFlowWithLifecycle = findViewById(R.id.tvFlowWithLifecycle)
        tvDirectCollect = findViewById(R.id.tvDirectCollect)
        btnToggle = findViewById(R.id.btnToggle)

        // 初始日志
        log("========== Flow 收集最佳实践 ==========\n")
        log("三种收集方式的对比：\n")
        log("1. repeatOnLifecycle (推荐)")
        log("   - 生命周期达到 STARTED 时开始收集")
        log("   - 生命周期低于 STARTED 时停止收集")
        log("   - 安全且高效\n")
        log("2. flowWithLifecycle (推荐)")
        log("   - 功能与 repeatOnLifecycle 相同")
        log("   - API 更简洁\n")
        log("3. 直接 collect (不推荐)")
        log("   - 即使在后台也会继续收集")
        log("   - 可能导致资源浪费和崩溃\n")

        // 演示三种收集方式
        demonstrateRepeatOnLifecycle()
        demonstrateFlowWithLifecycle()
        demonstrateDirectCollect()

        // 切换按钮
        btnToggle.setOnClickListener {
            viewModel.toggleActive()
            log("Flow 激活状态: ${if (viewModel.isActive.value) "是" else "否"}")
        }
    }

    /**
     * 演示 repeatOnLifecycle
     */
    private fun demonstrateRepeatOnLifecycle() {
        lifecycleScope.launch {
            // 当生命周期 >= STARTED 时开始收集
            // 当生命周期 < STARTED 时停止收集
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.counter.collect { value ->
                    tvRepeatOnLifecycle.text = "repeatOnLifecycle: $value"
                    log("[repeatOnLifecycle] 收到值: $value")
                }
            }
        }
    }

    /**
     * 演示 flowWithLifecycle
     */
    private fun demonstrateFlowWithLifecycle() {
        lifecycleScope.launch {
            // 功能与 repeatOnLifecycle 相同，但 API 更简洁
            viewModel.counter
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { value ->
                    tvFlowWithLifecycle.text = "flowWithLifecycle: $value"
                    log("[flowWithLifecycle] 收到值: $value")
                }
        }
    }

    /**
     * 演示直接收集（不推荐）
     */
    private fun demonstrateDirectCollect() {
        lifecycleScope.launch {
            // 直接收集，即使 Activity 在后台也会继续
            // 这可能导致不必要的资源消耗
            viewModel.counter.collect { value ->
                tvDirectCollect.text = "直接 collect: $value"
                // 注释掉日志以避免刷屏
                // log("[直接collect] 收到值: $value (即使后台也收集)")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        log("\n--- Activity onStart ---")
        log("repeatOnLifecycle 和 flowWithLifecycle 开始收集")
    }

    override fun onStop() {
        super.onStop()
        log("\n--- Activity onStop ---")
        log("repeatOnLifecycle 和 flowWithLifecycle 停止收集")
        log("直接 collect 仍在收集 (浪费资源)")
    }

    private fun log(message: String) {
        runOnUiThread {
            logBuilder.append(message).append("\n")
            tvLog.text = logBuilder.toString()
        }
    }
}

/**
 * Flow ViewModel
 */
class FlowViewModel : ViewModel() {

    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter.asStateFlow()

    private val _isActive = MutableStateFlow(true)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    init {
        // 模拟持续发射数据的 Flow
        viewModelScope.launch {
            while (true) {
                delay(1000)
                _counter.value++
            }
        }
    }

    fun toggleActive() {
        _isActive.value = !_isActive.value
    }
}
