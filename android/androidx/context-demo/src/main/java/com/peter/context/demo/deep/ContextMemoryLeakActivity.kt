package com.peter.context.demo.deep

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.peter.context.demo.databinding.ActivityContextMemoryleakBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

/**
 * Context 内存泄漏分析与解决
 * 
 * 常见的 Context 内存泄漏场景：
 * 1. 静态变量持有 Activity Context
 * 2. 非静态内部类持有外部类引用
 * 3. Handler 持有 Activity Context
 * 4. 单例模式持有 Activity Context
 * 5. 匿名内部类持有 Activity Context
 * 6. 注册监听器未取消注册
 * 7. AsyncTask 持有 Activity Context
 * 8. 线程持有 Activity Context
 */
class ContextMemoryLeakActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContextMemoryleakBinding
    private val sb = StringBuilder()
    
    // 错误示例：静态变量持有 Context（不要这样写！）
    // private static Context leakedContext;  // 这会导致内存泄漏！
    
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContextMemoryleakBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showMemoryLeakInfo()
    }

    private fun setupListeners() {
        binding.btnStaticLeak.setOnClickListener { demonstrateStaticLeak() }
        binding.btnInnerClassLeak.setOnClickListener { demonstrateInnerClassLeak() }
        binding.btnHandlerLeak.setOnClickListener { demonstrateHandlerLeak() }
        binding.btnSingletonLeak.setOnClickListener { demonstrateSingletonLeak() }
        binding.btnListenerLeak.setOnClickListener { demonstrateListenerLeak() }
        binding.btnCoroutineLeak.setOnClickListener { demonstrateCoroutineLeak() }
        binding.btnCorrectUsage.setOnClickListener { demonstrateCorrectUsage() }
    }

    private fun showMemoryLeakInfo() {
        sb.clear()
        
        sb.appendLine("=== Context 内存泄漏分析 ===\n")
        
        sb.appendLine("=== 1. 内存泄漏原因 ===")
        sb.appendLine("Activity 的生命周期由系统管理")
        sb.appendLine("如果 GC Root 持有 Activity 引用")
        sb.appendLine("GC 无法回收 Activity，导致内存泄漏")
        sb.appendLine()
        
        sb.appendLine("=== 2. GC Root 类型 ===")
        sb.appendLine("• 静态变量")
        sb.appendLine("• 活跃线程")
        sb.appendLine("• 本地方法栈")
        sb.appendLine("• 同步锁")
        sb.appendLine()
        
        sb.appendLine("=== 3. 常见泄漏场景 ===")
        sb.appendLine("1. 静态变量持有 Activity Context")
        sb.appendLine("2. 非静态内部类持有外部类引用")
        sb.appendLine("3. Handler 延迟消息持有 Activity")
        sb.appendLine("4. 单例模式持有 Activity Context")
        sb.appendLine("5. 匿名内部类持有 Activity")
        sb.appendLine("6. 注册监听器未取消注册")
        sb.appendLine("7. 协程/线程持有 Activity")
        sb.appendLine()
        
        sb.appendLine("=== 4. 检测工具 ===")
        sb.appendLine("• LeakCanary - 自动检测泄漏")
        sb.appendLine("• Android Studio Profiler")
        sb.appendLine("• MAT (Memory Analyzer Tool)")
        sb.appendLine("• Dumpsys meminfo")
        
        binding.tvInfo.text = sb.toString()
    }

    private fun demonstrateStaticLeak() {
        sb.clear()
        sb.appendLine("=== 静态变量泄漏 ===\n")
        
        // 错误示例
        sb.appendLine("❌ 错误写法:")
        sb.appendLine("""
// 静态变量持有 Activity Context
class LeakedHolder {
    companion object {
        var context: Context? = null  // 泄漏！
    }
}

// 使用
LeakedHolder.context = this  // Activity 泄漏！
        """.trimIndent())
        sb.appendLine()
        
        // 正确写法
        sb.appendLine("✓ 正确写法:")
        sb.appendLine("""
class SafeHolder {
    companion object {
        var context: Context? = null
        
        fun setContext(ctx: Context) {
            // 使用 Application Context
            context = ctx.applicationContext
        }
    }
}

// 或者使用 WeakReference
class WeakHolder {
    companion object {
        private var weakRef: WeakReference<Context>? = null
        
        fun setContext(ctx: Context) {
            weakRef = WeakReference(ctx)
        }
        
        fun getContext(): Context? = weakRef?.get()
    }
}
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("规则：静态变量永远不要持有 Activity/Service Context")
        sb.appendLine("     如果需要 Context，使用 Application Context")
        
        binding.tvResult.text = sb.toString()
    }

    private fun demonstrateInnerClassLeak() {
        sb.clear()
        sb.appendLine("=== 非静态内部类泄漏 ===\n")
        
        // 错误示例
        sb.appendLine("❌ 错误写法:")
        sb.appendLine("""
class MyActivity : AppCompatActivity() {
    
    // 非静态内部类隐式持有外部类引用
    inner class InnerTask {
        fun doSomething() {
            // 隐式持有 MyActivity.this
            val activity = this@MyActivity
        }
    }
    
    fun startTask() {
        val task = InnerTask()
        // 如果 task 生命周期超过 Activity，会泄漏
        Thread { task.doSomething() }.start()
    }
}
        """.trimIndent())
        sb.appendLine()
        
        // 正确写法
        sb.appendLine("✓ 正确写法:")
        sb.appendLine("""
class MyActivity : AppCompatActivity() {
    
    // 方式1: 使用静态内部类 + WeakReference
    class StaticTask(activity: MyActivity) : Runnable {
        private val activityRef = WeakReference(activity)
        
        override fun run() {
            val activity = activityRef.get() ?: return
            // 使用 activity
        }
    }
    
    // 方式2: 使用独立类
    class IndependentTask(context: Context) : Runnable {
        private val appContext = context.applicationContext
        
        override fun run() {
            // 使用 appContext
        }
    }
    
    fun startTask() {
        Thread(StaticTask(this)).start()
    }
}
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("规则：长生命周期的任务使用静态内部类或独立类")
        sb.appendLine("     需要引用 Activity 时使用 WeakReference")
        
        binding.tvResult.text = sb.toString()
    }

    private fun demonstrateHandlerLeak() {
        sb.clear()
        sb.appendLine("=== Handler 泄漏 ===\n")
        
        // 错误示例
        sb.appendLine("❌ 错误写法:")
        sb.appendLine("""
class MyActivity : AppCompatActivity() {
    
    private val handler = Handler(Looper.getMainLooper())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // 延迟消息持有 Activity
        handler.postDelayed({
            // 匿名 Runnable 持有 Activity
            Toast.makeText(this, "延迟消息", Toast.LENGTH_SHORT).show()
        }, 60000)  // 60秒后执行
    }
    
    // 问题：如果 Activity 在 60 秒内销毁，会泄漏
}
        """.trimIndent())
        sb.appendLine()
        
        // 正确写法
        sb.appendLine("✓ 正确写法:")
        sb.appendLine("""
class MyActivity : AppCompatActivity() {
    
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        runnable = Runnable {
            Toast.makeText(applicationContext, "延迟消息", Toast.LENGTH_SHORT).show()
        }
        handler.postDelayed(runnable!!, 60000)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 移除所有消息和回调
        handler.removeCallbacksAndMessages(null)
        // 或者移除特定回调
        runnable?.let { handler.removeCallbacks(it) }
    }
}
        """.trimIndent())
        sb.appendLine()
        
        // 演示当前代码
        sb.appendLine("=== 实际演示 ===")
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            Toast.makeText(applicationContext, "延迟消息执行了", Toast.LENGTH_SHORT).show()
        }
        handler?.postDelayed(runnable!!, 5000)
        sb.appendLine("已添加 5 秒延迟消息")
        sb.appendLine("退出 Activity 时会自动移除")
        
        binding.tvResult.text = sb.toString()
    }

    private fun demonstrateSingletonLeak() {
        sb.clear()
        sb.appendLine("=== 单例模式泄漏 ===\n")
        
        // 错误示例
        sb.appendLine("❌ 错误写法:")
        sb.appendLine("""
object BadSingleton {
    private var context: Context? = null
    
    fun init(ctx: Context) {
        context = ctx  // 如果传入 Activity Context，会泄漏！
    }
    
    fun doSomething() {
        context?.let {
            // 使用 context
        }
    }
}

// 使用
BadSingleton.init(this)  // Activity 泄漏！
        """.trimIndent())
        sb.appendLine()
        
        // 正确写法
        sb.appendLine("✓ 正确写法:")
        sb.appendLine("""
object GoodSingleton {
    private var appContext: Context? = null
    
    fun init(ctx: Context) {
        // 只保存 Application Context
        appContext = ctx.applicationContext
    }
    
    fun doSomething() {
        appContext?.let {
            // 使用 appContext
        }
    }
}

// 或者让调用者传入 Context
object BetterSingleton {
    fun doSomething(context: Context) {
        // 使用传入的 context，不保存引用
        val appContext = context.applicationContext
        // ...
    }
}
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("规则：单例如果需要 Context，使用 Application Context")
        sb.appendLine("     或者每次调用时传入 Context，不保存引用")
        
        binding.tvResult.text = sb.toString()
    }

    private fun demonstrateListenerLeak() {
        sb.clear()
        sb.appendLine("=== 监听器泄漏 ===\n")
        
        // 错误示例
        sb.appendLine("❌ 错误写法:")
        sb.appendLine("""
class MyActivity : AppCompatActivity() {
    
    private val listener = object : SomeListener {
        override fun onEvent() {
            // 匿名对象持有 Activity
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        SomeManager.registerListener(listener)
        // 忘记取消注册会泄漏
    }
    
    // 缺少 onDestroy 取消注册
}
        """.trimIndent())
        sb.appendLine()
        
        // 正确写法
        sb.appendLine("✓ 正确写法:")
        sb.appendLine("""
class MyActivity : AppCompatActivity() {
    
    private val listener = object : SomeListener {
        override fun onEvent() {
            // ...
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        SomeManager.registerListener(listener)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 必须取消注册！
        SomeManager.unregisterListener(listener)
    }
}

// 或者使用生命周期感知组件
class MyActivity : AppCompatActivity() {
    
    private val listener = LifecycleAwareListener(lifecycle) { event ->
        // 自动在 ON_DESTROY 时取消注册
    }
}
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("常见需要取消注册的监听器:")
        sb.appendLine("• BroadcastReceiver - unregisterReceiver()")
        sb.appendLine("• SensorManager - unregisterListener()")
        sb.appendLine("• LocationManager - removeUpdates()")
        sb.appendLine("• ContentObserver - unregisterContentObserver()")
        sb.appendLine("• SharedPreferences - unregisterOnSharedPreferenceChangeListener()")
        
        binding.tvResult.text = sb.toString()
    }

    private fun demonstrateCoroutineLeak() {
        sb.clear()
        sb.appendLine("=== 协程泄漏 ===\n")
        
        // 错误示例
        sb.appendLine("❌ 错误写法:")
        sb.appendLine("""
class MyActivity : AppCompatActivity() {
    
    private val scope = CoroutineScope(Dispatchers.Main)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        scope.launch {
            // 长时间运行的任务
            delay(60000)
            // 持有 Activity 引用
            Toast.makeText(this@MyActivity, "完成", Toast.LENGTH_SHORT).show()
        }
    }
    
    // 问题：Activity 销毁时协程仍在运行
}
        """.trimIndent())
        sb.appendLine()
        
        // 正确写法
        sb.appendLine("✓ 正确写法:")
        sb.appendLine("""
class MyActivity : AppCompatActivity() {
    
    // 方式1: 使用 lifecycleScope
    override fun onCreate(savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            // Activity 销毁时自动取消
            delay(60000)
            Toast.makeText(applicationContext, "完成", Toast.LENGTH_SHORT).show()
        }
    }
    
    // 方式2: 使用 repeatOnLifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 只在 STARTED 状态收集
                viewModel.flow.collect { }
            }
        }
    }
    
    // 方式3: 手动取消
    private var job: Job? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        job = CoroutineScope(Dispatchers.Main).launch {
            // ...
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("规则：使用 lifecycleScope 或 viewModelScope")
        sb.appendLine("     避免使用自定义 CoroutineScope")
        sb.appendLine("     或者手动管理协程生命周期")
        
        binding.tvResult.text = sb.toString()
    }

    private fun demonstrateCorrectUsage() {
        sb.clear()
        sb.appendLine("=== Context 正确使用总结 ===\n")
        
        sb.appendLine("=== 1. 选择正确的 Context ===")
        sb.appendLine("Activity Context:")
        sb.appendLine("  ✓ 启动 Activity")
        sb.appendLine("  ✓ 创建 Dialog")
        sb.appendLine("  ✓ LayoutInflater")
        sb.appendLine("  ✓ 需要 Activity 主题时")
        sb.appendLine()
        sb.appendLine("Application Context:")
        sb.appendLine("  ✓ 单例模式")
        sb.appendLine("  ✓ 长生命周期对象")
        sb.appendLine("  ✓ 不需要 UI 的操作")
        sb.appendLine()
        
        sb.appendLine("=== 2. 避免泄漏的原则 ===")
        sb.appendLine("1. 静态变量不要持有 Activity Context")
        sb.appendLine("2. 单例模式使用 Application Context")
        sb.appendLine("3. 非静态内部类改为静态内部类")
        sb.appendLine("4. 使用 WeakReference 持有 Activity")
        sb.appendLine("5. Handler 消息要在 onDestroy 移除")
        sb.appendLine("6. 监听器要在 onDestroy 取消注册")
        sb.appendLine("7. 协程使用 lifecycleScope")
        sb.appendLine("8. AsyncTask 已废弃，使用协程")
        sb.appendLine()
        
        sb.appendLine("=== 3. 推荐工具 ===")
        sb.appendLine("LeakCanary 集成:")
        sb.appendLine("""
dependencies {
    debugImplementation 'com.squareup.leakcanary:leakcanary-android:2.12'
}
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("=== 4. 检查清单 ===")
        sb.appendLine("□ 静态变量只存 Application Context")
        sb.appendLine("□ 单例使用 Application Context")
        sb.appendLine("□ Handler 使用静态类 + WeakReference")
        sb.appendLine("□ onDestroy 移除 Handler 消息")
        sb.appendLine("□ onDestroy 取消注册监听器")
        sb.appendLine("□ 协程使用 lifecycleScope")
        sb.appendLine("□ 使用 LeakCanary 检测泄漏")
        
        binding.tvResult.text = sb.toString()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 正确做法：移除 Handler 消息
        handler?.removeCallbacksAndMessages(null)
    }
}

/**
 * 演示用的监听器接口
 */
interface SomeListener {
    fun onEvent()
}

/**
 * 演示用的管理类
 */
object SomeManager {
    private val listeners = mutableListOf<SomeListener>()
    
    fun registerListener(listener: SomeListener) {
        listeners.add(listener)
    }
    
    fun unregisterListener(listener: SomeListener) {
        listeners.remove(listener)
    }
}
