package com.peter.context.demo.basic

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.peter.context.demo.ContextDemoApp
import com.peter.context.demo.R
import com.peter.context.demo.databinding.ActivityContextTypeBinding

/**
 * Context 类型介绍
 * 
 * Android 中 Context 的几种类型：
 * 
 * 1. Application Context - 通过 getApplicationContext() 或 Application 类获取
 *    - 生命周期：跟随整个应用进程
 *    - 使用场景：单例、全局配置、不需要 UI 的操作
 *    - 注意：不能用于启动 Activity（需加 FLAG_ACTIVITY_NEW_TASK）、创建 Dialog
 * 
 * 2. Activity Context - Activity 本身就是 Context
 *    - 生命周期：跟随 Activity 生命周期
 *    - 使用场景：启动 Activity、创建 Dialog、LayoutInflater、UI 相关操作
 *    - 注意：可能引起内存泄漏（持有 Activity 引用）
 * 
 * 3. Service Context - Service 本身也是 Context
 *    - 生命周期：跟随 Service 生命周期
 *    - 使用场景：后台任务、绑定服务
 * 
 * 4. BroadcastReceiver Context - onReceive 参数中的 Context
 *    - 生命周期：仅限于 onReceive 方法内有效
 *    - 注意：不能用于绑定服务或启动绑定服务
 * 
 * 5. ContextThemeWrapper - 用于应用主题的 Context 包装器
 *    - Activity 继承自 ContextThemeWrapper
 */
class ContextTypeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContextTypeBinding
    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContextTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        analyzeContextTypes()
    }

    private fun analyzeContextTypes() {
        sb.clear()
        
        // 1. Application Context 分析
        sb.appendLine("=== 1. Application Context ===")
        val appContext = applicationContext
        sb.appendLine("类名: ${appContext.javaClass.simpleName}")
        sb.appendLine("完整类名: ${appContext.javaClass.name}")
        sb.appendLine("hashCode: ${appContext.hashCode()}")
        sb.appendLine("toString: $appContext")
        
        // 通过 Application 单例获取
        val appContextFromSingleton = ContextDemoApp.context
        sb.appendLine("是否同一个实例: ${appContext === appContextFromSingleton}")
        sb.appendLine()
        
        // 2. Activity Context 分析
        sb.appendLine("=== 2. Activity Context ===")
        sb.appendLine("类名: ${this.javaClass.simpleName}")
        sb.appendLine("完整类名: ${this.javaClass.name}")
        sb.appendLine("hashCode: ${this.hashCode()}")
        sb.appendLine("toString: $this")
        
        // Activity 的 base context
        val baseContext = this.baseContext
        sb.appendLine("baseContext 类名: ${baseContext.javaClass.name}")
        sb.appendLine("baseContext hashCode: ${baseContext.hashCode()}")
        sb.appendLine("Activity 与 baseContext 是否相同: ${this === baseContext}")
        sb.appendLine()
        
        // 3. Context 的继承关系
        sb.appendLine("=== 3. 继承关系分析 ===")
        sb.appendLine("Activity 继承链:")
        printInheritanceChain(this.javaClass, "  ")
        sb.appendLine()
        
        // 4. Application Context 继承关系
        sb.appendLine("Application 继承链:")
        printInheritanceChain(appContext.javaClass, "  ")
        sb.appendLine()
        
        // 5. Context 能力的区别
        sb.appendLine("=== 4. Context 能力区别 ===")
        sb.appendLine("Application Context 可以:")
        sb.appendLine("  ✓ 获取资源 (getString, getColor, etc.)")
        sb.appendLine("  ✓ 访问文件 (openFileInput, openFileOutput)")
        sb.appendLine("  ✓ 获取系统服务 (getSystemService)")
        sb.appendLine("  ✓ 创建 SharedPreferences")
        sb.appendLine("  ✓ 启动 Service (startService)")
        sb.appendLine("  ✓ 发送广播 (sendBroadcast)")
        sb.appendLine()
        sb.appendLine("Application Context 不能/不推荐:")
        sb.appendLine("  ✗ 启动 Activity (需要 FLAG_ACTIVITY_NEW_TASK)")
        sb.appendLine("  ✗ 创建 Dialog (必须是 Activity Context)")
        sb.appendLine("  ✗ LayoutInflater (可能丢失主题信息)")
        sb.appendLine("  ✗ 注册 BroadcastReceiver (需要指定 exported)")
        sb.appendLine()
        sb.appendLine("Activity Context 可以:")
        sb.appendLine("  ✓ 所有 Application Context 的能力")
        sb.appendLine("  ✓ 启动 Activity (startActivity)")
        sb.appendLine("  ✓ 创建 Dialog (AlertDialog, Dialog)")
        sb.appendLine("  ✓ LayoutInflater (保留主题信息)")
        sb.appendLine("  ✓ 获取 ActivityManager")
        sb.appendLine()
        
        // 6. 内存分析
        sb.appendLine("=== 5. 内存占用分析 ===")
        sb.appendLine("Application Context 内存占用:")
        sb.appendLine("  - 关联整个 Application，内存占用相对较大")
        sb.appendLine("  - 但只有一个实例，生命周期长")
        sb.appendLine()
        sb.appendLine("Activity Context 内存占用:")
        sb.appendLine("  - 关联 Activity，包含 View 树、资源等")
        sb.appendLine("  - 每个 Activity 都有自己的 Context")
        sb.appendLine("  - 生命周期短，但容易被泄漏")
        sb.appendLine()
        
        // 7. 选择建议
        sb.appendLine("=== 6. Context 选择建议 ===")
        sb.appendLine("使用 Application Context 当:")
        sb.appendLine("  • 单例模式中需要 Context")
        sb.appendLine("  • 不需要 UI 相关操作")
        sb.appendLine("  • 长生命周期对象")
        sb.appendLine()
        sb.appendLine("使用 Activity Context 当:")
        sb.appendLine("  • 需要启动 Activity")
        sb.appendLine("  • 需要创建 Dialog")
        sb.appendLine("  • 需要 LayoutInflater")
        sb.appendLine("  • 需要 Activity 相关的主题/样式")
        sb.appendLine()
        
        binding.tvContent.text = sb.toString()
        Log.d("ContextType", sb.toString())
    }
    
    private fun printInheritanceChain(clazz: Class<*>, prefix: String) {
        var currentClass: Class<*>? = clazz
        while (currentClass != null && currentClass != Any::class.java) {
            sb.appendLine("$prefix${currentClass.simpleName}")
            currentClass = currentClass.superclass
        }
    }
}
