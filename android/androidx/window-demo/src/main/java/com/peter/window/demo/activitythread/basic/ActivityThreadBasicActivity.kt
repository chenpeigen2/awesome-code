package com.peter.window.demo.activitythread.basic

import android.app.Activity
import android.app.Application
import android.app.Instrumentation
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.peter.window.demo.R
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * ActivityThread 基础篇
 *
 * === ActivityThread 是什么 ===
 *
 * ActivityThread 是 Android 应用的主线程类，它是整个应用的入口点。
 * 每个应用进程有且只有一个 ActivityThread 实例。
 *
 * 【核心作用】
 * 1. 管理应用进程中所有 Activity 的生命周期
 * 2. 管理应用的 Application 对象
 * 3. 处理主线程的消息循环（Handler + Looper）
 * 4. 与 AMS（ActivityManagerService）通信
 * 5. 分发 UI 事件和系统回调
 *
 * 【类定义】
 * ```java
 * public final class ActivityThread {
 *     // 单例模式
 *     private static ActivityThread sCurrentActivityThread;
 *     
 *     // 主线程 Handler
 *     final H mH = new H();
 *     
 *     // 主线程 Looper
 *     Looper mLooper = Looper.myLooper();
 *     
 *     // Application 对象
 *     Application mInitialApplication;
 *     
 *     // Activity 记录
 *     final ArrayMap<IBinder, ActivityClientRecord> mActivities = new ArrayMap<>();
 *     
 *     // Service 记录
 *     final ArrayMap<IBinder, ServiceClientRecord> mServices = new ArrayMap<>();
 *     
 *     // Provider 记录
 *     final ArrayMap<ProviderKey, ProviderClientRecord> mProviderMap = new ArrayMap<>();
 * }
 * ```
 *
 * 【与普通线程的区别】
 * 
 * 普通 Thread:
 * ```
 * Thread thread = new Thread(() -> {
 *     // 执行任务
 * });
 * thread.start();
 * ```
 *
 * ActivityThread:
 * ```
 * // 在 main() 中
 * Looper.prepareMainLooper();
 * ActivityThread thread = new ActivityThread();
 * thread.attach(false, startSeq);
 * Looper.loop();
 * ```
 */
class ActivityThreadBasicActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ActivityThreadBasic"
    }

    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activitythread_basic)
        
        showBasicInfo()
        showGetInstance()
        showCoreFields()
        showCoreMethods()
        showThreadDifference()
        
        findViewById<TextView>(R.id.tvContent).text = sb.toString()
    }

    /**
     * 展示基础信息
     */
    private fun showBasicInfo() {
        sb.appendLine("═══════════════════════════════════════════════════")
        sb.appendLine("              ActivityThread 基础概念")
        sb.appendLine("═══════════════════════════════════════════════════\n")
        
        sb.appendLine("【什么是 ActivityThread】\n")
        sb.appendLine("ActivityThread 是 Android 应用的主线程类。")
        sb.appendLine("每个应用进程有且只有一个 ActivityThread 实例。\n")
        
        sb.appendLine("【核心职责】\n")
        sb.appendLine("1. 管理所有 Activity 的生命周期")
        sb.appendLine("2. 管理 Application 对象")
        sb.appendLine("3. 运行主线程消息循环（Handler + Looper）")
        sb.appendLine("4. 与 AMS 通信，响应系统调度")
        sb.appendLine("5. 分发 UI 事件和系统回调\n")
        
        sb.appendLine("【进程与 ActivityThread】\n")
        sb.appendLine("┌─────────────────────────────────────┐\n")
        sb.appendLine("│           应用进程                   │\n")
        sb.appendLine("│  ┌───────────────────────────────┐  │\n")
        sb.appendLine("│  │      ActivityThread          │  │\n")
        sb.appendLine("│  │  ┌─────────────────────────┐  │  │\n")
        sb.appendLine("│  │  │   H (Handler)           │  │  │\n")
        sb.appendLine("│  │  │   Looper                │  │  │\n")
        sb.appendLine("│  │  │   MessageQueue          │  │  │\n")
        sb.appendLine("│  │  └─────────────────────────┘  │  │\n")
        sb.appendLine("│  │  ┌─────────────────────────┐  │  │\n")
        sb.appendLine("│  │  │   mActivities           │  │  │\n")
        sb.appendLine("│  │  │   mServices             │  │  │\n")
        sb.appendLine("│  │  │   mProviders            │  │  │\n")
        sb.appendLine("│  │  └─────────────────────────┘  │  │\n")
        sb.appendLine("│  │  ┌─────────────────────────┐  │  │\n")
        sb.appendLine("│  │  │   Application           │  │  │\n")
        sb.appendLine("│  │  │   Instrumentation       │  │  │\n")
        sb.appendLine("│  │  └─────────────────────────┘  │  │\n")
        sb.appendLine("│  └───────────────────────────────┘  │\n")
        sb.appendLine("└─────────────────────────────────────┘\n")
    }

    /**
     * 展示获取 ActivityThread 实例的方式
     */
    private fun showGetInstance() {
        sb.appendLine("\n═══════════════════════════════════════════════════")
        sb.appendLine("           获取 ActivityThread 实例")
        sb.appendLine("═══════════════════════════════════════════════════\n")
        
        sb.appendLine("【方式一：通过 currentActivityThread()】\n")
        sb.appendLine("```java")
        sb.appendLine("// ActivityThread.java")
        sb.appendLine("public static ActivityThread currentActivityThread() {")
        sb.appendLine("    return sCurrentActivityThread;")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【方式二：通过反射获取】\n")
        sb.appendLine("```java")
        sb.appendLine("try {")
        sb.appendLine("    Class<?> activityThreadClass = Class.forName(\"android.app.ActivityThread\");")
        sb.appendLine("    Method currentActivityThreadMethod = ")
        sb.appendLine("        activityThreadClass.getDeclaredMethod(\"currentActivityThread\");")
        sb.appendLine("    ActivityThread activityThread = ")
        sb.appendLine("        (ActivityThread) currentActivityThreadMethod.invoke(null);")
        sb.appendLine("} catch (Exception e) {")
        sb.appendLine("    e.printStackTrace();")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【实际获取并打印信息】\n")
        
        // 实际获取 ActivityThread
        val activityThread = getActivityThread()
        if (activityThread != null) {
            sb.appendLine("✓ 成功获取 ActivityThread 实例")
            sb.appendLine("  类名: ${activityThread.javaClass.name}")
            sb.appendLine("  HashCode: ${activityThread.hashCode()}")
            sb.appendLine("  当前线程: ${Thread.currentThread().name}")
        } else {
            sb.appendLine("✗ 获取 ActivityThread 实例失败")
        }
        
        // 验证主线程
        sb.appendLine("\n【验证主线程】\n")
        sb.appendLine("主线程 Looper: ${Looper.getMainLooper()}")
        sb.appendLine("当前线程 Looper: ${Looper.myLooper()}")
        sb.appendLine("是否在主线程: ${Looper.myLooper() == Looper.getMainLooper()}")
        sb.appendLine("主线程 ID: ${Looper.getMainLooper().thread.id}")
        sb.appendLine("当前线程 ID: ${Thread.currentThread().id}")
    }

    /**
     * 展示核心字段
     */
    private fun showCoreFields() {
        sb.appendLine("\n═══════════════════════════════════════════════════")
        sb.appendLine("              ActivityThread 核心字段")
        sb.appendLine("═══════════════════════════════════════════════════\n")
        
        sb.appendLine("【核心字段定义】\n")
        sb.appendLine("```java")
        sb.appendLine("public final class ActivityThread {")
        sb.appendLine("    // 单例实例")
        sb.appendLine("    private static ActivityThread sCurrentActivityThread;")
        sb.appendLine("    ")
        sb.appendLine("    // 主线程 Handler（内部类 H）")
        sb.appendLine("    final H mH = new H();")
        sb.appendLine("    ")
        sb.appendLine("    // 主线程 Looper")
        sb.appendLine("    Looper mLooper = Looper.myLooper();")
        sb.appendLine("    ")
        sb.appendLine("    // Application 对象")
        sb.appendLine("    Application mInitialApplication;")
        sb.appendLine("    ")
        sb.appendLine("    // 所有 Activity 的记录")
        sb.appendLine("    final ArrayMap<IBinder, ActivityClientRecord> mActivities;")
        sb.appendLine("    ")
        sb.appendLine("    // 所有 Service 的记录")
        sb.appendLine("    final ArrayMap<IBinder, ServiceClientRecord> mServices;")
        sb.appendLine("    ")
        sb.appendLine("    // 所有 ContentProvider 的记录")
        sb.appendLine("    final ArrayMap<ProviderKey, ProviderClientRecord> mProviderMap;")
        sb.appendLine("    ")
        sb.appendLine("    // Instrumentation（负责生命周期回调）")
        sb.appendLine("    Instrumentation mInstrumentation;")
        sb.appendLine("    ")
        sb.appendLine("    // 应用的 APK 信息")
        sb.appendLine("    final ArrayMap<String, LoadedApk> mPackages;")
        sb.appendLine("    ")
        sb.appendLine("    // 资源管理器")
        sb.appendLine("    final ResourcesManager mResourcesManager;")
        sb.appendLine("    ")
        sb.appendLine("    // Application Info")
        sb.appendLine("    Application mApplication;")
        sb.appendLine("    ")
        sb.appendLine("    // 配置信息")
        sb.appendLine("    Configuration mConfiguration;")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【字段说明】\n")
        sb.appendLine("┌─────────────────────┬───────────────────────────────────────┐\n")
        sb.appendLine("│ 字段名              │ 说明                                  │\n")
        sb.appendLine("├─────────────────────┼───────────────────────────────────────┤\n")
        sb.appendLine("│ sCurrentActivityThread │ 单例实例，保存当前进程的 AT        │\n")
        sb.appendLine("│ mH                  │ 主线程 Handler，处理四大组件消息      │\n")
        sb.appendLine("│ mLooper             │ 主线程 Looper                         │\n")
        sb.appendLine("│ mInitialApplication │ Application 对象                      │\n")
        sb.appendLine("│ mActivities         │ 保存所有 ActivityClientRecord         │\n")
        sb.appendLine("│ mServices           │ 保存所有 ServiceClientRecord          │\n")
        sb.appendLine("│ mProviderMap        │ 保存所有 ProviderClientRecord         │\n")
        sb.appendLine("│ mInstrumentation    │ 负责调用生命周期方法                   │\n")
        sb.appendLine("│ mPackages           │ 保存 LoadedApk（APK 信息）            │\n")
        sb.appendLine("└─────────────────────┴───────────────────────────────────────┘\n")
        
        // 实际获取字段值
        sb.appendLine("【实际获取字段值】\n")
        val activityThread = getActivityThread()
        if (activityThread != null) {
            sb.appendLine("✓ mH: ${getHField(activityThread)}")
            sb.appendLine("✓ mLooper: ${getLooperField(activityThread)}")
            sb.appendLine("✓ mInitialApplication: ${getApplicationField(activityThread)}")
            sb.appendLine("✓ mActivities count: ${getActivitiesCount(activityThread)}")
            sb.appendLine("✓ mInstrumentation: ${getInstrumentationField(activityThread)}")
        }
    }

    /**
     * 展示核心方法
     */
    private fun showCoreMethods() {
        sb.appendLine("\n═══════════════════════════════════════════════════")
        sb.appendLine("              ActivityThread 核心方法")
        sb.appendLine("═══════════════════════════════════════════════════\n")
        
        sb.appendLine("【main() - 应用入口】\n")
        sb.appendLine("```java")
        sb.appendLine("public static void main(String[] args) {")
        sb.appendLine("    // 1. 初始化主线程 Looper")
        sb.appendLine("    Looper.prepareMainLooper();")
        sb.appendLine("    ")
        sb.appendLine("    // 2. 创建 ActivityThread 实例")
        sb.appendLine("    ActivityThread thread = new ActivityThread();")
        sb.appendLine("    ")
        sb.appendLine("    // 3. 与 AMS 建立连接")
        sb.appendLine("    thread.attach(false, startSeq);")
        sb.appendLine("    ")
        sb.appendLine("    // 4. 获取 Handler")
        sb.appendLine("    if (sMainThreadHandler == null) {")
        sb.appendLine("        sMainThreadHandler = thread.getHandler();")
        sb.appendLine("    }")
        sb.appendLine("    ")
        sb.appendLine("    // 5. 开启消息循环")
        sb.appendLine("    Looper.loop();")
        sb.appendLine("    ")
        sb.appendLine("    // 6. 循环结束，抛出异常")
        sb.appendLine("    throw new RuntimeException(\"Main thread loop unexpectedly exited\");")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【attach() - 与系统服务连接】\n")
        sb.appendLine("```java")
        sb.appendLine("private void attach(boolean system, long startSeq) {")
        sb.appendLine("    sCurrentActivityThread = this;")
        sb.appendLine("    mSystemThread = system;")
        sb.appendLine("    ")
        sb.appendLine("    if (!system) {")
        sb.appendLine("        // 获取 AMS 的代理对象")
        sb.appendLine("        final IActivityManager mgr = ActivityManager.getService();")
        sb.appendLine("        ")
        sb.appendLine("        try {")
        sb.appendLine("            // 将 ApplicationThread 注册到 AMS")
        sb.appendLine("            mgr.attachApplication(mAppThread, startSeq);")
        sb.appendLine("        } catch (RemoteException ex) {")
        sb.appendLine("            throw ex.rethrowFromSystemServer();")
        sb.appendLine("        }")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【核心方法列表】\n")
        sb.appendLine("┌────────────────────────────────┬────────────────────────────────────┐\n")
        sb.appendLine("│ 方法名                         │ 说明                               │\n")
        sb.appendLine("├────────────────────────────────┼────────────────────────────────────┤\n")
        sb.appendLine("│ main()                         │ 应用入口，初始化 Looper            │\n")
        sb.appendLine("│ attach()                       │ 与 AMS 建立连接                    │\n")
        sb.appendLine("│ performLaunchActivity()        │ 启动 Activity                      │\n")
        sb.appendLine("│ handleResumeActivity()         │ 恢复 Activity                      │\n")
        sb.appendLine("│ handlePauseActivity()          │ 暂停 Activity                      │\n")
        sb.appendLine("│ handleStopActivity()           │ 停止 Activity                      │\n")
        sb.appendLine("│ handleDestroyActivity()        │ 销毁 Activity                      │\n")
        sb.appendLine("│ performCreateService()         │ 创建 Service                       │\n")
        sb.appendLine("│ handleBindService()            │ 绑定 Service                       │\n")
        sb.appendLine("│ installProvider()              │ 安装 ContentProvider               │\n")
        sb.appendLine("│ handleBindApplication()        │ 绑定 Application                   │\n")
        sb.appendLine("│ scheduleLaunchActivity()       │ 调度启动 Activity（来自 AMS）      │\n")
        sb.appendLine("└────────────────────────────────┴────────────────────────────────────┘\n")
    }

    /**
     * 展示与普通线程的区别
     */
    private fun showThreadDifference() {
        sb.appendLine("\n═══════════════════════════════════════════════════")
        sb.appendLine("         ActivityThread vs 普通 Thread")
        sb.appendLine("═══════════════════════════════════════════════════\n")
        
        sb.appendLine("【普通 Thread】\n")
        sb.appendLine("```java")
        sb.appendLine("// 创建并启动普通线程")
        sb.appendLine("Thread thread = new Thread(() -> {")
        sb.appendLine("    // 执行任务")
        sb.appendLine("    System.out.println(\"Running in thread: \" + Thread.currentThread().getName());")
        sb.appendLine("});")
        sb.appendLine("thread.start();")
        sb.appendLine("// 线程执行完毕后自动结束")
        sb.appendLine("```\n")
        
        sb.appendLine("【ActivityThread】\n")
        sb.appendLine("```java")
        sb.appendLine("// 在 main() 方法中")
        sb.appendLine("public static void main(String[] args) {")
        sb.appendLine("    // 创建主线程 Looper（消息队列）")
        sb.appendLine("    Looper.prepareMainLooper();")
        sb.appendLine("    ")
        sb.appendLine("    // 创建 ActivityThread")
        sb.appendLine("    ActivityThread thread = new ActivityThread();")
        sb.appendLine("    ")
        sb.appendLine("    // 与 AMS 连接")
        sb.appendLine("    thread.attach(false);")
        sb.appendLine("    ")
        sb.appendLine("    // 开始无限循环处理消息")
        sb.appendLine("    Looper.loop();")
        sb.appendLine("    ")
        sb.appendLine("    // loop() 不会返回，除非出错")
        sb.appendLine("    throw new RuntimeException(\"Main thread loop unexpectedly exited\");")
        sb.appendLine("}")
        sb.appendLine("```\n")
        
        sb.appendLine("【对比表格】\n")
        sb.appendLine("┌──────────────────┬──────────────────┬───────────────────────┐\n")
        sb.appendLine("│ 特性             │ 普通 Thread      │ ActivityThread        │\n")
        sb.appendLine("├──────────────────┼──────────────────┼───────────────────────┤\n")
        sb.appendLine("│ 生命周期         │ 任务完成即结束   │ 随进程持续运行        │\n")
        sb.appendLine("│ 消息循环         │ 无               │ 有（Looper + Handler）│\n")
        sb.appendLine("│ 运行模式         │ 一次性任务       │ 事件驱动              │\n")
        sb.appendLine("│ 创建数量         │ 任意多个         │ 每进程一个            │\n")
        sb.appendLine("│ 系统交互         │ 无               │ 与 AMS/WMS 交互       │\n")
        sb.appendLine("│ UI 操作          │ 不能             │ 可以                  │\n")
        sb.appendLine("│ 生命周期管理     │ 无               │ 管理四大组件          │\n")
        sb.appendLine("└──────────────────┴──────────────────┴───────────────────────┘\n")
        
        sb.appendLine("【ActivityThread 的特殊性】\n")
        sb.appendLine("1. 单例模式：每个进程只有一个实例")
        sb.appendLine("2. 永不退出：除非进程结束")
        sb.appendLine("3. 消息驱动：通过 Handler 处理所有事件")
        sb.appendLine("4. 系统桥梁：连接 App 和 System Server")
        sb.appendLine("5. UI 线程：可以执行 UI 操作")
    }

    // ==================== 反射工具方法 ====================

    private fun getActivityThread(): Any? {
        return try {
            val clazz = Class.forName("android.app.ActivityThread")
            val method = clazz.getDeclaredMethod("currentActivityThread")
            method.invoke(null)
        } catch (e: Exception) {
            Log.e(TAG, "getActivityThread failed", e)
            null
        }
    }

    private fun getHField(activityThread: Any): String {
        return try {
            val field = activityThread.javaClass.getDeclaredField("mH")
            field.isAccessible = true
            val h = field.get(activityThread)
            h?.javaClass?.simpleName ?: "null"
        } catch (e: Exception) {
            "error: ${e.message}"
        }
    }

    private fun getLooperField(activityThread: Any): String {
        return try {
            val field = activityThread.javaClass.getDeclaredField("mLooper")
            field.isAccessible = true
            val looper = field.get(activityThread) as? Looper
            looper?.toString() ?: "null"
        } catch (e: Exception) {
            "error: ${e.message}"
        }
    }

    private fun getApplicationField(activityThread: Any): String {
        return try {
            val field = activityThread.javaClass.getDeclaredField("mInitialApplication")
            field.isAccessible = true
            val app = field.get(activityThread) as? Application
            app?.javaClass?.simpleName ?: "null"
        } catch (e: Exception) {
            "error: ${e.message}"
        }
    }

    private fun getActivitiesCount(activityThread: Any): Int {
        return try {
            val field = activityThread.javaClass.getDeclaredField("mActivities")
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            val activities = field.get(activityThread) as? Map<*, *>
            activities?.size ?: 0
        } catch (e: Exception) {
            -1
        }
    }

    private fun getInstrumentationField(activityThread: Any): String {
        return try {
            val field = activityThread.javaClass.getDeclaredField("mInstrumentation")
            field.isAccessible = true
            val instrumentation = field.get(activityThread) as? Instrumentation
            instrumentation?.javaClass?.simpleName ?: "null"
        } catch (e: Exception) {
            "error: ${e.message}"
        }
    }
}
