package com.peter.coroutine.demo.advanced

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 自定义 CoroutineScope 演示
 *
 * 本 Activity 展示如何创建和管理自定义的 CoroutineScope
 *
 * ## CoroutineScope 接口
 * ```kotlin
 * public interface CoroutineScope {
 *     public val coroutineContext: CoroutineContext
 * }
 * ```
 *
 * CoroutineScope 是协程的执行范围，它定义了协程的上下文（Context），
 * 包括 Job、Dispatcher 等元素。
 *
 * ## 创建自定义 Scope 的方式
 *
 * 1. **实现 CoroutineScope 接口**
 *    - 在类中实现 coroutineContext 属性
 *    - 适合需要生命周期管理的场景
 *
 * 2. **使用 CoroutineScope() 工厂函数**
 *    - 直接创建一个 Scope 对象
 *    - 需要手动管理取消
 *
 * 3. **使用 MainScope()**
 *    - 预配置的 Scope，使用 SupervisorJob + Main Dispatcher
 *    - 适合 Android UI 相关场景
 *
 * ## 自定义 Scope 的关键元素
 * - **Job**: 控制协程的生命周期，支持取消
 * - **Dispatcher**: 决定协程在哪个线程执行
 * - **SupervisorJob**: 子协程失败不会影响其他子协程
 *
 * ## 生命周期管理
 * 自定义 Scope 必须在适当时机取消，否则会导致内存泄漏。
 *
 * @see kotlinx.coroutines.CoroutineScope
 * @see kotlinx.coroutines.SupervisorJob
 */
class CustomScopeActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    // 自定义 Scope 1: 使用 MainScope
    private val mainScope = MainScope()

    // 自定义 Scope 2: 使用工厂函数创建
    private val customScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // 自定义 Scope 3: 实现 CoroutineScope 接口的自定义类
    private class MyCustomScope : CoroutineScope {
        private val job = SupervisorJob()
        override val coroutineContext = job + Dispatchers.IO

        fun destroy() {
            job.cancel()
        }
    }

    private val myScope = MyCustomScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.custom_scope)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateCustomScope()
    }

    /**
     * 演示自定义 CoroutineScope 的使用
     */
    private fun demonstrateCustomScope() {
        log("========== 自定义 CoroutineScope 演示 ==========\n")

        // 演示1: MainScope 的使用
        log("--- 演示1: MainScope ---\n")
        demonstrateMainScope()

        // 演示2: 自定义 Scope 配置
        log("\n--- 演示2: 自定义配置的 Scope ---\n")
        demonstrateCustomConfigScope()

        // 演示3: Scope 的生命周期管理
        log("\n--- 演示3: 生命周期管理 ---\n")
        demonstrateLifecycleManagement()

        // 演示4: SupervisorJob 的作用
        log("\n--- 演示4: SupervisorJob ---\n")
        demonstrateSupervisorJob()

        // 打印总结
        log("\n========== 总结 ==========\n")
        printSummary()
    }

    /**
     * 演示 MainScope 的使用
     */
    private fun demonstrateMainScope() {
        log("MainScope 定义:")
        log("  val mainScope = MainScope()")
        log("  // 等价于:")
        log("  // CoroutineScope(SupervisorJob() + Dispatchers.Main)\n")

        mainScope.launch {
            log("MainScope 启动的协程")
            log("  线程: ${Thread.currentThread().name}")
            log("  使用 SupervisorJob，子协程失败不影响其他")
            log("  使用 Dispatchers.Main，适合 UI 操作\n")
        }
    }

    /**
     * 演示自定义配置的 Scope
     */
    private fun demonstrateCustomConfigScope() {
        log("自定义 Scope 配置:")
        log("  val customScope = CoroutineScope(")
        log("      SupervisorJob() + Dispatchers.Default")
        log("  )\n")

        customScope.launch {
            log("customScope 启动的协程")
            log("  线程: ${Thread.currentThread().name}")
            log("  可以自定义 Job 和 Dispatcher 组合\n")
        }
    }

    /**
     * 演示生命周期管理
     */
    private fun demonstrateLifecycleManagement() {
        log("生命周期管理方式:\n")

        log("1. 取消所有子协程 (保留 Scope):")
        log("   scope.coroutineContext[Job]?.cancelChildren()\n")

        log("2. 完全取消 Scope:")
        log("   scope.cancel()\n")

        log("3. 在 Activity/Fragment 中:")
        log("   override fun onDestroy() {")
        log("       super.onDestroy()")
        log("       scope.cancel()")
        log("   }\n")

        // 演示取消操作
        val tempScope = CoroutineScope(Job() + Dispatchers.Default)
        tempScope.launch {
            delay(1000)
            log("这条消息不会显示，因为 Scope 会被取消")
        }

        tempScope.cancel()
        log("临时 Scope 已取消，其协程不会继续执行\n")
    }

    /**
     * 演示 SupervisorJob 的作用
     */
    private fun demonstrateSupervisorJob() {
        log("SupervisorJob vs 普通 Job:\n")

        log("普通 Job: 一个子协程失败，所有子协程都被取消")
        log("SupervisorJob: 子协程独立，一个失败不影响其他\n")

        val supervisorScope = CoroutineScope(SupervisorJob())

        supervisorScope.launch {
            try {
                delay(100)
                throw RuntimeException("子协程1故意失败")
            } catch (e: Exception) {
                log("子协程1捕获异常: ${e.message}")
            }
        }

        supervisorScope.launch {
            delay(200)
            log("子协程2正常完成，不受子协程1影响")
        }

        // 等待协程执行完成后取消
        Thread.sleep(500)
        supervisorScope.cancel()
    }

    /**
     * 打印总结
     */
    private fun printSummary() {
        log("创建自定义 Scope 的最佳实践:\n")

        log("1. 在 Activity/Fragment 中:")
        log("   private val scope = MainScope()")
        log("   override fun onDestroy() { scope.cancel() }\n")

        log("2. 在 ViewModel 中:")
        log("   // 直接使用 viewModelScope (推荐)")
        log("   // 或自定义:")
        log("   private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)\n")

        log("3. 在 Service 中:")
        log("   private val scope = CoroutineScope(SupervisorJob())")
        log("   override fun onDestroy() { scope.cancel() }\n")

        log("4. 实现接口方式:")
        log("   class MyClass : CoroutineScope {")
        log("       private val job = SupervisorJob()")
        log("       override val coroutineContext = job + Dispatchers.IO")
        log("       fun destroy() { job.cancel() }")
        log("   }\n")

        log("==========================================\n")
    }

    /**
     * 添加日志并更新 UI
     */
    private fun log(message: String) {
        if (Thread.currentThread().name == "main") {
            logBuilder.append(message).append("\n")
            tvLog.text = logBuilder.toString()
        } else {
            runOnUiThread {
                logBuilder.append(message).append("\n")
                tvLog.text = logBuilder.toString()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 重要: 在 Activity 销毁时取消所有 Scope
        mainScope.cancel()
        customScope.cancel()
        myScope.destroy()

        logBuilder.append("\n所有自定义 Scope 已在 onDestroy 中取消\n")
    }
}
