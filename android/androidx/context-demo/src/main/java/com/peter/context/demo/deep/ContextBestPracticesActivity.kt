package com.peter.context.demo.deep

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.UserHandle
import android.util.Log
import android.view.Display
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.peter.context.demo.R
import com.peter.context.demo.databinding.ActivityContextBestpracticesBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.ref.WeakReference

/**
 * Context 最佳实践
 * 
 * 本文件总结了 Context 使用的最佳实践和注意事项
 */
class ContextBestPracticesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContextBestpracticesBinding
    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContextBestpracticesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showBestPracticesSummary()
    }

    private fun setupListeners() {
        binding.btnContextSelection.setOnClickListener { showContextSelectionGuide() }
        binding.btnCommonPatterns.setOnClickListener { showCommonPatterns() }
        binding.btnExtensionFunctions.setOnClickListener { showExtensionFunctions() }
        binding.btnTips.setOnClickListener { showTipsAndTricks() }
        binding.btnAntiPatterns.setOnClickListener { showAntiPatterns() }
    }

    private fun showBestPracticesSummary() {
        sb.clear()
        
        sb.appendLine("=== Context 最佳实践总结 ===\n")
        
        sb.appendLine("=== 核心原则 ===\n")
        
        sb.appendLine("1. 选择正确的 Context")
        sb.appendLine("   - UI 操作用 Activity Context")
        sb.appendLine("   - 非 UI 操作用 Application Context")
        sb.appendLine()
        
        sb.appendLine("2. 避免内存泄漏")
        sb.appendLine("   - 静态变量不持有 Activity Context")
        sb.appendLine("   - 单例使用 Application Context")
        sb.appendLine("   - 及时取消注册和移除回调")
        sb.appendLine()
        
        sb.appendLine("3. 使用扩展函数简化代码")
        sb.appendLine("   - 封装常用操作")
        sb.appendLine("   - 提高代码可读性")
        sb.appendLine()
        
        sb.appendLine("4. 遵循生命周期")
        sb.appendLine("   - 使用生命周期感知组件")
        sb.appendLine("   - lifecycleScope / viewModelScope")
        sb.appendLine()
        
        sb.appendLine("点击下方按钮查看详细示例")
        
        binding.tvInfo.text = sb.toString()
    }

    private fun showContextSelectionGuide() {
        sb.clear()
        sb.appendLine("=== Context 选择指南 ===\n")
        
        sb.appendLine("=== 需要使用 Activity Context 的场景 ===\n")
        sb.appendLine("1. 启动 Activity")
        sb.appendLine("   startActivity(Intent(this, TargetActivity::class.java))")
        sb.appendLine()
        sb.appendLine("2. 创建 Dialog")
        sb.appendLine("   AlertDialog.Builder(this).show()")
        sb.appendLine()
        sb.appendLine("3. LayoutInflater")
        sb.appendLine("   LayoutInflater.from(this).inflate(R.layout.xxx, parent)")
        sb.appendLine()
        sb.appendLine("4. 注册 BroadcastReceiver (动态)")
        sb.appendLine("   registerReceiver(receiver, filter)")
        sb.appendLine()
        sb.appendLine("5. 绑定 Service")
        sb.appendLine("   bindService(intent, connection, Context.BIND_AUTO_CREATE)")
        sb.appendLine()
        
        sb.appendLine("=== 可以使用 Application Context 的场景 ===\n")
        sb.appendLine("1. 访问资源")
        sb.appendLine("   context.getString(R.string.xxx)")
        sb.appendLine("   context.getColor(R.color.xxx)")
        sb.appendLine()
        sb.appendLine("2. 访问文件")
        sb.appendLine("   context.openFileInput(name)")
        sb.appendLine("   context.openFileOutput(name, mode)")
        sb.appendLine()
        sb.appendLine("3. SharedPreferences")
        sb.appendLine("   context.getSharedPreferences(name, mode)")
        sb.appendLine()
        sb.appendLine("4. 系统服务")
        sb.appendLine("   context.getSystemService(Context.XXX_SERVICE)")
        sb.appendLine()
        sb.appendLine("5. 数据库")
        sb.appendLine("   context.openOrCreateDatabase(name, mode, factory)")
        sb.appendLine()
        sb.appendLine("6. 发送广播")
        sb.appendLine("   context.sendBroadcast(intent)")
        sb.appendLine()
        sb.appendLine("7. 启动 Service")
        sb.appendLine("   context.startService(intent)")
        sb.appendLine()
        
        sb.appendLine("=== 判断原则 ===\n")
        sb.appendLine("Q: 操作是否需要 Window/主题？")
        sb.appendLine("   是 → Activity Context")
        sb.appendLine("   否 → Application Context")
        sb.appendLine()
        sb.appendLine("Q: Context 是否会被长生命周期对象持有？")
        sb.appendLine("   是 → Application Context")
        sb.appendLine("   否 → Activity Context")
        sb.appendLine()
        
        binding.tvResult.text = sb.toString()
    }

    private fun showCommonPatterns() {
        sb.clear()
        sb.appendLine("=== 常用模式 ===\n")
        
        sb.appendLine("=== 1. 单例模式中获取 Context ===\n")
        sb.appendLine("""
object AppSingleton {
    private lateinit var appContext: Context
    
    fun init(context: Context) {
        appContext = context.applicationContext
    }
    
    fun getContext(): Context = appContext
    
    fun doSomething() {
        // 使用 appContext
        val prefs = appContext.getSharedPreferences("name", Context.MODE_PRIVATE)
    }
}

// 在 Application 中初始化
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppSingleton.init(this)
    }
}
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("=== 2. 在 View 中获取 Context ===\n")
        sb.appendLine("""
class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    // 获取正确的 Context
    private fun getActivityContext(): Activity? {
        var ctx = context
        while (ctx is ContextWrapper) {
            if (ctx is Activity) {
                return ctx
            }
            ctx = ctx.baseContext
        }
        return null
    }
    
    // 或者使用 context 直接访问资源
    private fun loadResources() {
        val color = context.getColor(R.color.xxx)
        val string = context.getString(R.string.xxx)
    }
}
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("=== 3. Fragment 中获取 Context ===\n")
        sb.appendLine("""
class MyFragment : Fragment() {
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 最早可以获取到 Context
    }
    
    private fun doSomething() {
        // 使用 requireContext() (非空)
        val prefs = requireContext().getSharedPreferences("name", Context.MODE_PRIVATE)
        
        // 或者安全获取
        context?.let { ctx ->
            // 使用 ctx
        }
        
        // 获取 Activity
        val activity = requireActivity()
    }
}
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("=== 4. Adapter 中获取 Context ===\n")
        sb.appendLine("""
class MyAdapter(
    private val items: List<Item>
) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    
    // 在 onCreateViewHolder 中获取 Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // holder.itemView.context 获取 Context
        val context = holder.itemView.context
        // ...
    }
}
        """.trimIndent())
        
        binding.tvResult.text = sb.toString()
    }

    private fun showExtensionFunctions() {
        sb.clear()
        sb.appendLine("=== Context 扩展函数 ===\n")
        
        sb.appendLine("=== 推荐的扩展函数 ===\n")
        sb.appendLine("""
// 1. 吐司扩展
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.toast(@StringRes resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

// 使用
context.toast("Hello")
context.toast(R.string.hello)

// 2. SharedPreferences 扩展
fun Context.getPrefs(name: String = "default"): SharedPreferences {
    return getSharedPreferences(name, Context.MODE_PRIVATE)
}

// SharedPreferences 编辑扩展
fun SharedPreferences.edit(block: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.block()
    editor.apply()
}

// 使用
context.getPrefs().edit {
    putString("key", "value")
    putInt("count", 1)
}

// 3. Intent 扩展
inline fun <reified T : Activity> Context.intent(block: Intent.() -> Unit = {}): Intent {
    return Intent(this, T::class.java).apply(block)
}

fun Context.startActivity(block: Intent.() -> Unit) {
    startActivity(Intent().apply(block))
}

// 使用
startActivity<DetailActivity> {
    putExtra("id", 123)
}

// 4. 颜色扩展
fun Context.color(@ColorRes resId: Int): Int {
    return ContextCompat.getColor(this, resId)
}

// 5. Drawable 扩展
fun Context.drawable(@DrawableRes resId: Int): Drawable? {
    return ContextCompat.getDrawable(this, resId)
}

// 6. 尺寸扩展
fun Context.dimen(@DimenRes resId: Int): Float {
    return resources.getDimension(resId)
}

fun Context.dimenPx(@DimenRes resId: Int): Int {
    return resources.getDimensionPixelSize(resId)
}

// 7. dp/sp 转换
fun Context.dpToPx(dp: Float): Float {
    return dp * resources.displayMetrics.density
}

fun Context.pxToDp(px: Float): Float {
    return px / resources.displayMetrics.density
}

fun Context.spToPx(sp: Float): Float {
    return sp * resources.displayMetrics.scaledDensity
}

// 使用
val px = context.dpToPx(16f)
val dp = context.pxToDp(100f)

// 8. 判断是否主线程
fun Context.isMainThread(): Boolean {
    return Looper.myLooper() == Looper.getMainLooper()
}

// 9. 在主线程执行
fun Context.runOnMainThread(block: () -> Unit) {
    if (isMainThread()) {
        block()
    } else {
        Handler(Looper.getMainLooper()).post(block)
    }
}

// 10. 获取 Activity
fun Context.getActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
        """.trimIndent())
        
        binding.tvResult.text = sb.toString()
    }

    private fun showTipsAndTricks() {
        sb.clear()
        sb.appendLine("=== 技巧与窍门 ===\n")
        
        sb.appendLine("=== 1. 获取 Application Context 的多种方式 ===\n")
        sb.appendLine("""
// 方式1: 通过 Activity
val appContext = activity.applicationContext

// 方式2: 通过任意 Context
val appContext = context.applicationContext

// 方式3: 通过 Application 单例 (需要自己实现)
val appContext = MyApp.getInstance()

// 方式4: 通过 ActivityThread (不推荐)
val appContext = ActivityThread.currentApplication()
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("=== 2. 判断 Context 类型 ===\n")
        sb.appendLine("""
fun getContextType(context: Context): String {
    return when (context) {
        is Activity -> "Activity"
        is Service -> "Service"
        is Application -> "Application"
        is ContextWrapper -> {
            val baseContext = context.baseContext
            "ContextWrapper(base=" + getContextType(baseContext) + ")"
        }
        else -> context.javaClass.simpleName
    }
}
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("=== 3. 使用 Context 创建带主题的 View ===\n")
        sb.appendLine("""
// 使用 ContextThemeWrapper 创建带主题的 View
val themedContext = ContextThemeWrapper(context, R.style.MyTheme)
val view = LayoutInflater.from(themedContext)
    .inflate(R.layout.my_layout, parent, false)
// view 会使用 MyTheme 的样式
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("=== 4. 安全的 startActivity ===\n")
        sb.appendLine("""
// 安全启动 Activity (检查是否有应用可以处理)
fun Context.safeStartActivity(intent: Intent): Boolean {
    return try {
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
            true
        } else {
            false
        }
    } catch (e: Exception) {
        Log.e("Context", "Failed to start activity", e)
        false
    }
}
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("=== 5. 检查权限 ===\n")
        sb.appendLine("""
// 简化权限检查
fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == 
           PackageManager.PERMISSION_GRANTED
}

// 使用
if (context.hasPermission(Manifest.permission.CAMERA)) {
    // 有权限
}
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("=== 6. 获取屏幕尺寸 ===\n")
        sb.appendLine("""
fun Context.getScreenWidth(): Int {
    val metrics = resources.displayMetrics
    return metrics.widthPixels
}

fun Context.getScreenHeight(): Int {
    val metrics = resources.displayMetrics
    return metrics.heightPixels
}
        """.trimIndent())
        
        binding.tvResult.text = sb.toString()
    }

    private fun showAntiPatterns() {
        sb.clear()
        sb.appendLine("=== 反模式 (不要这样写) ===\n")
        
        sb.appendLine("=== ❌ 反模式 1: 静态变量持有 Activity ===\n")
        sb.appendLine("""
// 错误
object BadSingleton {
    var activity: Activity? = null  // 泄漏！
}

// 正确
object GoodSingleton {
    var appContext: Context? = null
}
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("=== ❌ 反模式 2: 用 Application Context 创建 Dialog ===\n")
        sb.appendLine("""
// 错误
AlertDialog.Builder(applicationContext)
    .setTitle("Title")
    .show()  // 会抛出 BadTokenException

// 正确
AlertDialog.Builder(this)  // Activity Context
    .setTitle("Title")
    .show()
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("=== ❌ 反模式 3: Application Context 启动 Activity 不加 Flags ===\n")
        sb.appendLine("""
// 错误
val intent = Intent(applicationContext, TargetActivity::class.java)
applicationContext.startActivity(intent)  // 需要 FLAG_ACTIVITY_NEW_TASK

// 正确方式1: 使用 Activity Context
val intent = Intent(this, TargetActivity::class.java)
startActivity(intent)

// 正确方式2: 添加 Flags (如果必须用 Application Context)
val intent = Intent(applicationContext, TargetActivity::class.java).apply {
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}
applicationContext.startActivity(intent)
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("=== ❌ 反模式 4: Handler 不清理 ===\n")
        sb.appendLine("""
// 错误
Handler(Looper.getMainLooper()).postDelayed({
    // 持有 Activity 引用
}, 60000)

// 正确
private val handler = Handler(Looper.getMainLooper())

override fun onDestroy() {
    handler.removeCallbacksAndMessages(null)
}
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("=== ❌ 反模式 5: 监听器不取消注册 ===\n")
        sb.appendLine("""
// 错误
override fun onCreate(savedInstanceState: Bundle?) {
    registerReceiver(receiver, filter)
    // 没有 unregisterReceiver
}

// 正确
private var receiver: BroadcastReceiver? = null

override fun onCreate(savedInstanceState: Bundle?) {
    receiver = MyReceiver()
    registerReceiver(receiver, filter)
}

override fun onDestroy() {
    receiver?.let { unregisterReceiver(it) }
}
        """.trimIndent())
        sb.appendLine()
        
        sb.appendLine("=== ❌ 反模式 6: 在 View 构造函数中使用 this ===\n")
        sb.appendLine("""
// 错误 (在 Activity 中)
val view = View(this)  // 可能导致问题

// 正确
val view = View(this@MainActivity)

// 或者在 Fragment 中
val view = View(requireContext())
        """.trimIndent())
        
        binding.tvResult.text = sb.toString()
    }
}

/**
 * Context 扩展函数集合
 */
object ContextExtensions {
    
    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    fun Context.getLongToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    fun Context.dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }
    
    fun Context.pxToDp(px: Float): Float {
        return px / resources.displayMetrics.density
    }
    
    fun Context.spToPx(sp: Float): Float {
        return sp * resources.displayMetrics.scaledDensity
    }
    
    fun Context.isMainThread(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }
    
    fun Context.getActivity(): Activity? {
        var ctx = this
        while (ctx is ContextWrapper) {
            if (ctx is Activity) return ctx
            ctx = ctx.baseContext
        }
        return null
    }
    
    fun Context.hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == 
               PackageManager.PERMISSION_GRANTED
    }
}
