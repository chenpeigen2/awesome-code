package com.peter.context.demo.deep

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.peter.context.demo.databinding.ActivityContextWrapperBinding

/**
 * ContextWrapper 详解
 * 
 * ContextWrapper 是 Context 的装饰器模式实现：
 * - 包装一个 Context 对象，并代理其所有方法调用
 * - 允许子类修改特定行为而不影响原始 Context
 * 
 * 继承关系：
 * Context (abstract)
 *   └── ContextWrapper
 *         ├── Application
 *         ├── Service
 *         ├── ContextThemeWrapper
 *         │     └── Activity
 *         └── MutableContextWrapper (可动态切换 base context)
 * 
 * 应用场景：
 * 1. 修改 Context 的行为（如主题、资源）
 * 2. 在非 Activity 中使用 Activity 功能
 * 3. 测试中 Mock Context
 */
class ContextWrapperActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContextWrapperBinding
    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContextWrapperBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showContextWrapperInfo()
    }

    private fun setupListeners() {
        binding.btnAnalyze.setOnClickListener { analyzeContextChain() }
        binding.btnCustomWrapper.setOnClickListener { demonstrateCustomWrapper() }
        binding.btnMutableWrapper.setOnClickListener { demonstrateMutableContextWrapper() }
        binding.btnThemeWrapper.setOnClickListener { demonstrateThemeWrapper() }
    }

    private fun showContextWrapperInfo() {
        sb.clear()
        
        sb.appendLine("=== ContextWrapper 基础 ===\n")
        
        // 1. ContextWrapper 定义
        sb.appendLine("=== 1. ContextWrapper 定义 ===")
        sb.appendLine("""
public class ContextWrapper extends Context {
    Context mBase;  // 被包装的 Context
    
    public ContextWrapper(Context base) {
        mBase = base;
    }
    
    // 所有方法都代理到 mBase
    @Override
    public Resources getResources() {
        return mBase.getResources();
    }
    
    @Override
    public String getPackageName() {
        return mBase.getPackageName();
    }
    // ... 其他方法
}
        """.trimIndent())
        sb.appendLine()
        
        // 2. 继承关系
        sb.appendLine("=== 2. Context 继承关系 ===")
        sb.appendLine("Context (抽象类)")
        sb.appendLine("  └── ContextImpl (Context 的实现)")
        sb.appendLine("  └── ContextWrapper (装饰器)")
        sb.appendLine("        ├── Application")
        sb.appendLine("        ├── Service")
        sb.appendLine("        ├── ContextThemeWrapper")
        sb.appendLine("        │     └── Activity")
        sb.appendLine("        │     └── AppCompatActivity")
        sb.appendLine("        └── MutableContextWrapper")
        sb.appendLine()
        
        // 3. 应用场景
        sb.appendLine("=== 3. 应用场景 ===")
        sb.appendLine("1. 修改 Context 行为")
        sb.appendLine("   - 修改资源访问方式")
        sb.appendLine("   - 修改主题/样式")
        sb.appendLine("   - 添加额外功能")
        sb.appendLine()
        sb.appendLine("2. 在非 Activity 中使用 Activity 功能")
        sb.appendLine("   - View 持有 ContextWrapper")
        sb.appendLine("   - Fragment 持有 ContextWrapper")
        sb.appendLine()
        sb.appendLine("3. 测试")
        sb.appendLine("   - Mock Context")
        sb.appendLine("   - 依赖注入")
        sb.appendLine()
        
        // 4. 核心方法
        sb.appendLine("=== 4. 核心方法 ===")
        sb.appendLine("attachBaseContext(base) - 设置被包装的 Context")
        sb.appendLine("getBaseContext() - 获取被包装的 Context")
        sb.appendLine()
        
        binding.tvInfo.text = sb.toString()
    }

    private fun analyzeContextChain() {
        sb.clear()
        sb.appendLine("=== 分析 Context 链 ===\n")
        
        // 当前 Activity 的 Context 链
        sb.appendLine("Activity Context 链:")
        var ctx: Context? = this
        var level = 0
        while (ctx != null) {
            val className = ctx.javaClass.simpleName
            sb.appendLine("  ${"  ".repeat(level)}└── $className")
            
            if (ctx is ContextWrapper) {
                ctx = ctx.baseContext
                level++
            } else {
                sb.appendLine("  ${"  ".repeat(level + 1)}└── (base: ${ctx.javaClass.name})")
                break
            }
            
            // 防止无限循环
            if (level > 10) {
                sb.appendLine("  ... (层级太深)")
                break
            }
        }
        sb.appendLine()
        
        // Application Context 链
        sb.appendLine("Application Context 链:")
        ctx = applicationContext
        level = 0
        while (ctx != null) {
            val className = ctx.javaClass.simpleName
            sb.appendLine("  ${"  ".repeat(level)}└── $className")
            
            if (ctx is ContextWrapper) {
                ctx = ctx.baseContext
                level++
            } else {
                sb.appendLine("  ${"  ".repeat(level + 1)}└── (base: ${ctx.javaClass.name})")
                break
            }
            
            if (level > 10) {
                sb.appendLine("  ... (层级太深)")
                break
            }
        }
        sb.appendLine()
        
        // baseContext 分析
        sb.appendLine("=== baseContext 分析 ===")
        sb.appendLine("this.baseContext = ${baseContext.javaClass.name}")
        sb.appendLine("applicationContext = ${applicationContext.javaClass.name}")
        sb.appendLine("this.baseContext === applicationContext: ${baseContext === applicationContext}")
        sb.appendLine()
        
        // 检查是否同一实例
        sb.appendLine("=== 实例比较 ===")
        sb.appendLine("this === baseContext: ${this === baseContext}")
        sb.appendLine("this.baseContext === this.getBaseContext(): ${baseContext === this.baseContext}")
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextWrapper", sb.toString())
    }

    private fun demonstrateCustomWrapper() {
        sb.clear()
        sb.appendLine("=== 自定义 ContextWrapper ===\n")
        
        // 创建自定义 ContextWrapper
        val customWrapper = LoggingContextWrapper(this)
        
        // 使用自定义 ContextWrapper
        sb.appendLine("使用自定义 ContextWrapper:")
        sb.appendLine("  包名: ${customWrapper.packageName}")
        sb.appendLine("  资源: ${customWrapper.resources}")
        sb.appendLine()
        
        sb.appendLine("LoggingContextWrapper 源码:")
        sb.appendLine("""
class LoggingContextWrapper(base: Context) : ContextWrapper(base) {
    override fun getResources(): Resources {
        Log.d("CustomWrapper", "getResources called")
        return super.getResources()
    }
    
    override fun getPackageName(): String {
        Log.d("CustomWrapper", "getPackageName called")
        return super.getPackageName()
    }
}
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("自定义 ContextWrapper 的用途:")
        sb.appendLine("  1. 日志/调试")
        sb.appendLine("  2. 性能监控")
        sb.appendLine("  3. 资源替换")
        sb.appendLine("  4. 主题切换")
        sb.appendLine("  5. 依赖注入")
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextWrapper", sb.toString())
    }

    private fun demonstrateMutableContextWrapper() {
        sb.clear()
        sb.appendLine("=== MutableContextWrapper ===\n")
        
        sb.appendLine("MutableContextWrapper 允许动态切换 base context:")
        sb.appendLine()
        
        sb.appendLine("""
// MutableContextWrapper 示例
class MutableContextWrapper(base: Context) : ContextWrapper(base) {
    
    fun setBaseContext(context: Context) {
        mBase = context
    }
}

// 使用场景：View 复用
val mutableWrapper = MutableContextWrapper(context1)
val view = View(mutableWrapper)

// 切换到另一个 Context
mutableWrapper.setBaseContext(context2)

// View 现在使用 context2 的资源
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("应用场景:")
        sb.appendLine("  1. RecyclerView 中的 View 复用")
        sb.appendLine("  2. View 在不同 Context 间移动")
        sb.appendLine("  3. Dialog/PopupWindow 中使用")
        sb.appendLine()
        
        sb.appendLine("注意:")
        sb.appendLine("  - 需要小心管理 Context 的生命周期")
        sb.appendLine("  - 避免内存泄漏")
        sb.appendLine("  - 通常不需要自己实现")
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextWrapper", sb.toString())
    }

    private fun demonstrateThemeWrapper() {
        sb.clear()
        sb.appendLine("=== ContextThemeWrapper ===\n")
        
        sb.appendLine("ContextThemeWrapper 用于修改主题:")
        sb.appendLine()
        
        sb.appendLine("""
// ContextThemeWrapper 示例
val themedContext = ContextThemeWrapper(
    context,                    // 原始 Context
    R.style.MyCustomTheme       // 主题 ID
)

// 使用带主题的 Context 创建 View
val view = LayoutInflater.from(themedContext)
    .inflate(R.layout.my_layout, null)

// View 会使用 MyCustomTheme 的样式
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("Activity 的继承关系:")
        sb.appendLine("  Context -> ContextWrapper -> ContextThemeWrapper -> Activity")
        sb.appendLine()
        
        sb.appendLine("ContextThemeWrapper 的方法:")
        sb.appendLine("  getThemeResId() - 获取主题资源 ID")
        sb.appendLine("  setTheme(resid) - 设置主题")
        sb.appendLine("  getTheme() - 获取 Theme 对象")
        sb.appendLine()
        
        sb.appendLine("应用场景:")
        sb.appendLine("  1. Dialog 使用不同主题")
        sb.appendLine("  2. View 使用自定义主题")
        sb.appendLine("  3. 插件化框架中主题隔离")
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextWrapper", sb.toString())
    }
}

/**
 * 自定义 ContextWrapper - 带日志功能
 */
class LoggingContextWrapper(base: Context) : ContextWrapper(base) {
    
    companion object {
        private const val TAG = "LoggingContextWrapper"
    }

    override fun getResources(): Resources {
        Log.d(TAG, "getResources() called from ${Thread.currentThread().stackTrace[3]}")
        return super.getResources()
    }

    override fun getPackageName(): String {
        Log.d(TAG, "getPackageName() called")
        return super.getPackageName()
    }

    // Note: getString() is final in Context and cannot be overridden
    // Use the wrapper method instead
    fun getStringLogged(resId: Int): String {
        Log.d(TAG, "getString($resId) called")
        return getString(resId)
    }
    
    override fun getSystemService(name: String): Any? {
        Log.d(TAG, "getSystemService($name) called")
        return super.getSystemService(name)
    }
}

/**
 * 可变 ContextWrapper - 可动态切换 base context
 * 用于 View 复用等场景
 */
class MutableContextWrapper(base: Context) : ContextWrapper(base) {
    
    /**
     * 设置新的 base context
     * 注意：应该在主线程调用，并确保旧的 context 不会引起内存泄漏
     */
    fun setBaseContext(context: Context) {
        // ContextWrapper 的 mBase 是 protected，这里需要通过反射设置
        // 或者在自己的实现中维护 mBase
        try {
            val field = ContextWrapper::class.java.getDeclaredField("mBase")
            field.isAccessible = true
            field.set(this, context)
        } catch (e: Exception) {
            Log.e("MutableContextWrapper", "Failed to set base context", e)
        }
    }
}

/**
 * 主题 ContextWrapper - 用于修改主题
 */
class ThemeContextWrapper(
    base: Context,
    private val themeResId: Int
) : ContextWrapper(base) {
    
    private var theme: Resources.Theme? = null

    override fun getTheme(): Resources.Theme {
        if (theme == null) {
            theme = resources.newTheme().apply {
                applyStyle(themeResId, true)
            }
        }
        return theme!!
    }
}
