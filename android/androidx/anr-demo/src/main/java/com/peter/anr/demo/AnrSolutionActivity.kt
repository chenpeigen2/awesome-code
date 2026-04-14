package com.peter.anr.demo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.peter.anr.demo.databinding.ActivityAnrSolutionBinding

/**
 * ANR 检测与解决方案
 *
 * 本 Demo 展示 ANR 的检测工具、监控方案和预防措施：
 *
 * 1. StrictMode — 开发期检测主线程违规操作
 * 2. Watchdog — 自定义 ANR 监控线程
 * 3. trace 文件分析 — 如何读取和分析 ANR 堆栈
 * 4. 预防清单 — 系统化的 ANR 预防检查项
 * 5. 最佳实践 — 综合的 ANR 防范方案
 */
class AnrSolutionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnrSolutionBinding
    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnrSolutionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showSolutionInfo()
    }

    private fun setupListeners() {
        binding.btnStrictMode.setOnClickListener { demonstrateStrictMode() }
        binding.btnWatchdog.setOnClickListener { demonstrateWatchdog() }
        binding.btnAnalyzeTrace.setOnClickListener { demonstrateAnalyzeTrace() }
        binding.btnPrevention.setOnClickListener { demonstratePrevention() }
        binding.btnBestPractices.setOnClickListener { demonstrateBestPractices() }
    }

    /**
     * 显示 ANR 检测与解决的概览信息
     */
    private fun showSolutionInfo() {
        sb.clear()
        sb.appendLine("=== ANR 检测与解决 ===")
        sb.appendLine()
        sb.appendLine("=== 1. 检测工具 ===")
        sb.appendLine("• StrictMode - 开发期检测主线程违规操作")
        sb.appendLine("• adb logcat - 查看系统日志")
        sb.appendLine("• adb pull /data/anr/traces.txt - 获取 ANR 堆栈")
        sb.appendLine("• LeakCanary - 间接帮助检测")
        sb.appendLine()
        sb.appendLine("=== 2. 监控方案 ===")
        sb.appendLine("• FileObserver 监听 /data/anr/ 目录")
        sb.appendLine("• Watchdog 定时检测主线程阻塞")
        sb.appendLine("• Android vitals (Google Play Console)")
        sb.appendLine()
        sb.appendLine("=== 3. 预防措施 ===")
        sb.appendLine("• 所有耗时操作放到子线程")
        sb.appendLine("• 使用协程简化异步编程")
        sb.appendLine("• 使用 apply() 代替 commit()")
        sb.appendLine("• 使用 Room 代替原生 SQLite")

        binding.tvInfo.text = sb.toString()
    }

    /**
     * StrictMode 配置演示
     *
     * StrictMode 是 Android 提供的开发期检测工具，
     * 可以检测主线程的磁盘读写、网络访问等违规操作。
     */
    private fun demonstrateStrictMode() {
        sb.clear()
        sb.appendLine("=== StrictMode 配置 ===")
        sb.appendLine()

        sb.appendLine("=== 1. 在 Application 中配置 ===")
        sb.appendLine()
        sb.appendLine("class MyApplication : Application() {")
        sb.appendLine("    override fun onCreate() {")
        sb.appendLine("        if (BuildConfig.DEBUG) {")
        sb.appendLine("            StrictMode.setThreadPolicy(")
        sb.appendLine("                StrictMode.ThreadPolicy.Builder()")
        sb.appendLine("                    .detectAll()           // 检测所有违规")
        sb.appendLine("                    .penaltyLog()          // 日志惩罚")
        sb.appendLine("                    .penaltyFlashScreen()  // 屏幕闪烁")
        sb.appendLine("                    .build()")
        sb.appendLine("            )")
        sb.appendLine("            StrictMode.setVmPolicy(")
        sb.appendLine("                StrictMode.VmPolicy.Builder()")
        sb.appendLine("                    .detectAll()")
        sb.appendLine("                    .penaltyLog()")
        sb.appendLine("                    .build()")
        sb.appendLine("            )")
        sb.appendLine("        }")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine()

        sb.appendLine("=== 2. 线程策略检测项 ===")
        sb.appendLine()
        sb.appendLine("detectCustomSlowCalls()")
        sb.appendLine("  → 检测自定义的慢调用")
        sb.appendLine("  → 需要配合 StrictMode.noteSlowCall() 使用")
        sb.appendLine("  → 示例：StrictMode.noteSlowCall(\"slow operation\")")
        sb.appendLine()
        sb.appendLine("detectDiskReads()")
        sb.appendLine("  → 检测主线程磁盘读操作")
        sb.appendLine("  → 包括：File.read(), SharedPreferences.getString() 等")
        sb.appendLine("  → 这是最常见的 ANR 来源之一")
        sb.appendLine()
        sb.appendLine("detectDiskWrites()")
        sb.appendLine("  → 检测主线程磁盘写操作")
        sb.appendLine("  → 包括：File.write(), SharedPreferences.edit().commit() 等")
        sb.appendLine()
        sb.appendLine("detectNetwork()")
        sb.appendLine("  → 检测主线程网络操作")
        sb.appendLine("  → 包括：HttpURLConnection, OkHttp 同步调用等")
        sb.appendLine()

        sb.appendLine("=== 3. 惩罚方式 ===")
        sb.appendLine()
        sb.appendLine("penaltyLog()")
        sb.appendLine("  → 在 Logcat 中输出违规日志")
        sb.appendLine("  → tag: StrictMode")
        sb.appendLine()
        sb.appendLine("penaltyFlashScreen()")
        sb.appendLine("  → 屏幕边缘闪烁红色")
        sb.appendLine("  → 直观提醒开发者")
        sb.appendLine()
        sb.appendLine("penaltyDeath()")
        sb.appendLine("  → 直接崩溃（应用会闪退）")
        sb.appendLine("  → 适合 Debug 构建确保代码质量")
        sb.appendLine()
        sb.appendLine("penaltyDialog()")
        sb.appendLine("  → 弹出对话框提示")
        sb.appendLine()

        sb.appendLine("=== 4. VM 策略检测项 ===")
        sb.appendLine()
        sb.appendLine("detectActivityLeaks()")
        sb.appendLine("  → 检测 Activity 泄漏")
        sb.appendLine()
        sb.appendLine("detectLeakedClosableObjects()")
        sb.appendLine("  → 检测未关闭的 Closeable 对象")
        sb.appendLine()
        sb.appendLine("detectLeakedSqlLiteObjects()")
        sb.appendLine("  → 检测未关闭的 SQLite 对象")
        sb.appendLine()
        sb.appendLine("detectCleartextNetwork()")
        sb.appendLine("  → 检测明文网络传输")
        sb.appendLine()

        sb.appendLine("=== 5. 注意事项 ===")
        sb.appendLine()
        sb.appendLine("• 只在 DEBUG 构建中启用 StrictMode")
        sb.appendLine("• Release 构建中不要启用（会影响性能）")
        sb.appendLine("• 可以使用 permitXXX() 允许特定操作")
        sb.appendLine("• 可以使用 StrictMode.allowThreadDiskReads() 临时允许")

        binding.tvResult.text = sb.toString()
    }

    /**
     * Watchdog ANR 监控演示
     *
     * Watchdog 是一种常用的 ANR 检测方案，
     * 通过定期向主线程发送消息来判断主线程是否阻塞。
     */
    private fun demonstrateWatchdog() {
        sb.clear()
        sb.appendLine("=== Watchdog ANR 监控 ===")
        sb.appendLine()

        sb.appendLine("=== 1. 实现原理 ===")
        sb.appendLine()
        sb.appendLine("Watchdog 的核心思路:")
        sb.appendLine("  1. 后台线程定期向主线程 Handler 发送一个任务")
        sb.appendLine("  2. 任务执行时更新一个计数器")
        sb.appendLine("  3. 后台线程 sleep 一段时间后检查计数器")
        sb.appendLine("  4. 如果计数器没变，说明主线程可能阻塞了")
        sb.appendLine("  5. 获取主线程堆栈进行分析")
        sb.appendLine()

        sb.appendLine("=== 2. 简单实现 ===")
        sb.appendLine()
        sb.appendLine("class ANRWatchdog(")
        sb.appendLine("    private val timeout: Long = 5000")
        sb.appendLine(") : Thread(\"ANR-Watchdog\") {")
        sb.appendLine()
        sb.appendLine("    private var tick = 0L")
        sb.appendLine()
        sb.appendLine("    override fun run() {")
        sb.appendLine("        while (true) {")
        sb.appendLine("            val oldTick = tick")
        sb.appendLine("            // 向主线程发送一个消息")
        sb.appendLine("            Handler(Looper.getMainLooper()).post { tick++ }")
        sb.appendLine()
        sb.appendLine("            sleep(timeout)")
        sb.appendLine()
        sb.appendLine("            if (oldTick == tick) {")
        sb.appendLine("                // 主线程可能阻塞了")
        sb.appendLine("                val stackTrace = Looper.getMainLooper()")
        sb.appendLine("                    .thread.stackTrace")
        sb.appendLine("                Log.e(\"ANR-Watchdog\", \"可能发生 ANR!\")")
        sb.appendLine("                stackTrace.forEach {")
        sb.appendLine("                    Log.e(\"ANR-Watchdog\", it.toString())")
        sb.appendLine("                }")
        sb.appendLine("            }")
        sb.appendLine("        }")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine()

        sb.appendLine("=== 3. 使用方式 ===")
        sb.appendLine()
        sb.appendLine("// 在 Application.onCreate() 中启动")
        sb.appendLine("val watchdog = ANRWatchdog(timeout = 5000)")
        sb.appendLine("watchdog.start()")
        sb.appendLine()

        sb.appendLine("=== 4. 增强: ANR 回调接口 ===")
        sb.appendLine()
        sb.appendLine("interface ANRListener {")
        sb.appendLine("    fun onAnrDetected(")
        sb.appendLine("        duration: Long,")
        sb.appendLine("        stackTrace: Array<StackTraceElement>")
        sb.appendLine("    )")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("增强版 Watchdog 可以:")
        sb.appendLine("  • 支持自定义超时时间")
        sb.appendLine("  • 支持自定义回调处理")
        sb.appendLine("  • 支持忽略特定堆栈（白名单）")
        sb.appendLine("  • 支持连续检测（避免误报）")
        sb.appendLine()

        sb.appendLine("=== 5. 开源方案 ===")
        sb.appendLine()
        sb.appendLine("• ANR-WatchDog (GitHub)")
        sb.appendLine("  → 轻量级 ANR 检测库")
        sb.appendLine("  → 使用方式简单，几行代码集成")
        sb.appendLine()
        sb.appendLine("• BlockCanary")
        sb.appendLine("  → 卡顿检测（类似 LeakCanary 的 UI 展示）")
        sb.appendLine("  → 可以检测主线程任何卡顿")
        sb.appendLine("  → 不局限于 ANR（更短的阻塞也能检测）")
        sb.appendLine()
        sb.appendLine("• Matrix (微信)")
        sb.appendLine("  → 腾讯出品的性能监控框架")
        sb.appendLine("  → 包含 ANR、卡顿、内存、流量等监控")
        sb.appendLine("  → 适合大型项目")

        binding.tvResult.text = sb.toString()
    }

    /**
     * ANR trace 文件分析演示
     *
     * 当 ANR 发生时，系统会将所有线程的堆栈信息写入
     * /data/anr/traces.txt 文件。
     */
    private fun demonstrateAnalyzeTrace() {
        sb.clear()
        sb.appendLine("=== ANR trace 文件分析 ===")
        sb.appendLine()

        sb.appendLine("=== 1. 获取 trace 文件 ===")
        sb.appendLine()
        sb.appendLine("adb pull /data/anr/traces.txt")
        sb.appendLine()

        sb.appendLine("=== 2. trace 文件结构 ===")
        sb.appendLine()
        sb.appendLine("----- pid 12345 at 2024-01-01 12:00:00 -----")
        sb.appendLine("Cmd line: com.example.app")
        sb.appendLine()
        sb.appendLine("\"main\" prio=5 tid=1 RUNNABLE")
        sb.appendLine("  | group=\"main\" sCount=0 dsCount=0 ...")
        sb.appendLine("  at com.example.app.MyClass.slowMethod(MyClass.java:42)")
        sb.appendLine("  at com.example.app.MainActivity.onClick(MainActivity.java:28)")
        sb.appendLine("  ...")
        sb.appendLine()

        sb.appendLine("=== 3. 关键信息 ===")
        sb.appendLine()
        sb.appendLine("• PID - 进程 ID")
        sb.appendLine("• \"DALVIK THREADS\" - 所有线程堆栈")
        sb.appendLine("• 主线程状态:")
        sb.appendLine("  - TIMED_WAITING → sleep() 或 wait(timeout)")
        sb.appendLine("  - WAITING → Object.wait() 或 lock.lock()")
        sb.appendLine("  - RUNNABLE → 正在执行代码（死循环/计算）")
        sb.appendLine("  - BLOCKED → 等待获取锁")
        sb.appendLine("• 持有锁的情况 (locked on)")
        sb.appendLine()

        sb.appendLine("=== 4. 常见主线程状态分析 ===")
        sb.appendLine()
        sb.appendLine("状态: TIMED_WAITING")
        sb.appendLine("  原因: Thread.sleep() 或 Object.wait(timeout)")
        sb.appendLine("  堆栈特征: at java.lang.Thread.sleep(Native Method)")
        sb.appendLine("  解决: 移除 sleep()，使用异步方式")
        sb.appendLine()
        sb.appendLine("状态: WAITING")
        sb.appendLine("  原因: Object.wait() 或 Lock.lock()")
        sb.appendLine("  堆栈特征: at java.lang.Object.wait(Native Method)")
        sb.appendLine("  解决: 避免主线程等待锁，减小锁粒度")
        sb.appendLine()
        sb.appendLine("状态: RUNNABLE")
        sb.appendLine("  原因: 正在执行耗时代码")
        sb.appendLine("  堆栈特征: at xxx.method(SourceFile:xx)")
        sb.appendLine("  解决: 将耗时操作移到子线程")
        sb.appendLine()
        sb.appendLine("状态: BLOCKED")
        sb.appendLine("  原因: 等待获取 synchronized 锁")
        sb.appendLine("  堆栈特征: waiting to lock <0x12345678>")
        sb.appendLine("         (a com.example.MyClass)")
        sb.appendLine("  解决: 避免锁竞争，使用非阻塞方案")
        sb.appendLine()

        sb.appendLine("=== 5. 分析步骤 ===")
        sb.appendLine()
        sb.appendLine("① 找到主线程（main 或 \"main\" tid=1）")
        sb.appendLine("② 查看线程状态")
        sb.appendLine("③ 分析堆栈中的方法调用")
        sb.appendLine("④ 定位耗时操作的具体位置")
        sb.appendLine("⑤ 检查是否有其他线程持有锁导致主线程阻塞")
        sb.appendLine()
        sb.appendLine("提示: 可以搜索 \"waiting to lock\" 找到")
        sb.appendLine("      主线程等待的锁被哪个线程持有")

        binding.tvResult.text = sb.toString()
    }

    /**
     * ANR 预防清单演示
     *
     * 系统化的 ANR 预防检查清单，
     * 帮助开发者在开发过程中避免常见的 ANR 问题。
     */
    private fun demonstratePrevention() {
        sb.clear()
        sb.appendLine("=== ANR 预防清单 ===")
        sb.appendLine()
        sb.appendLine("□ 主线程不做耗时操作")
        sb.appendLine("  □ 不使用 Thread.sleep()")
        sb.appendLine("  □ 不使用 while(true) 循环")
        sb.appendLine("  □ 不做繁重计算")
        sb.appendLine()
        sb.appendLine("□ I/O 操作在子线程")
        sb.appendLine("  □ 文件读写 → Dispatchers.IO")
        sb.appendLine("  □ 数据库操作 → Room + 协程")
        sb.appendLine("  □ SP 使用 apply() 代替 commit()")
        sb.appendLine()
        sb.appendLine("□ 网络请求在子线程")
        sb.appendLine("  □ Retrofit + 协程")
        sb.appendLine("  □ OkHttp 异步调用")
        sb.appendLine()
        sb.appendLine("□ Binder 通信注意")
        sb.appendLine("  □ ContentResolver.query 在 IO 线程")
        sb.appendLine("  □ 减少 AIDL 传输数据量")
        sb.appendLine()
        sb.appendLine("□ 锁使用注意")
        sb.appendLine("  □ 避免在主线程等待锁")
        sb.appendLine("  □ 使用非阻塞算法")
        sb.appendLine("  □ 减小锁的粒度")
        sb.appendLine()
        sb.appendLine("□ 使用工具检测")
        sb.appendLine("  □ StrictMode 开发期检测")
        sb.appendLine("  □ 使用 ANR-Watchdog 监控")
        sb.appendLine()

        sb.appendLine("=== 各类 ANR 超时阈值 ===")
        sb.appendLine()
        sb.appendLine("输入事件 ANR:    5 秒")
        sb.appendLine("  → 触摸/按键事件未在 5 秒内处理完")
        sb.appendLine()
        sb.appendLine("广播 ANR:        前台 10 秒 / 后台 60 秒")
        sb.appendLine("  → onReceive() 未在超时时间内返回")
        sb.appendLine()
        sb.appendLine("Service ANR:     前台 20 秒 / 后台 200 秒")
        sb.appendLine("  → onCreate/onStartCommand 未在超时内完成")
        sb.appendLine("  → Android 12+ 前台 Service 为 10 秒")
        sb.appendLine()
        sb.appendLine("ContentProvider ANR: 10 秒")
        sb.appendLine("  → ContentProvider 未在 10 秒内发布")
        sb.appendLine()

        sb.appendLine("=== 代码审查要点 ===")
        sb.appendLine()
        sb.appendLine("检查以下代码是否在主线程执行:")
        sb.appendLine("  1. new Thread().start() → 考虑用协程替代")
        sb.appendLine("  2. Thread.sleep() → 绝对禁止")
        sb.appendLine("  3. synchronized 块 → 检查是否可能阻塞")
        sb.appendLine("  4. file.read/write → 必须在 IO 线程")
        sb.appendLine("  5. db.query/insert → 必须在 IO 线程")
        sb.appendLine("  6. sp.commit() → 改为 apply()")
        sb.appendLine("  7. contentResolver.query → 必须在 IO 线程")
        sb.appendLine("  8. Gson().fromJson → 大数据必须在 IO 线程")

        binding.tvResult.text = sb.toString()
    }

    /**
     * ANR 最佳实践演示
     *
     * 综合的最佳实践方案，涵盖协程、Jetpack 组件、
     * 性能优化和线上监控等方面。
     */
    private fun demonstrateBestPractices() {
        sb.clear()
        sb.appendLine("=== ANR 最佳实践 ===")
        sb.appendLine()

        sb.appendLine("=== 1. 使用协程简化异步 ===")
        sb.appendLine()
        sb.appendLine("// 在 Activity/Fragment 中")
        sb.appendLine("lifecycleScope.launch(Dispatchers.IO) {")
        sb.appendLine("    val data = fetchData() // 耗时操作")
        sb.appendLine("    withContext(Dispatchers.Main) {")
        sb.appendLine("        updateUI(data) // 更新 UI")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("// 在 ViewModel 中")
        sb.appendLine("viewModelScope.launch(Dispatchers.IO) {")
        sb.appendLine("    val result = repository.loadData()")
        sb.appendLine("    _uiState.value = Result.Success(result)")
        sb.appendLine("}")
        sb.appendLine()

        sb.appendLine("=== 2. 使用 Jetpack 组件 ===")
        sb.appendLine()
        sb.appendLine("• Room 代替 SQLite")
        sb.appendLine("  → 内置协程支持（suspend 函数）")
        sb.appendLine("  → 编译期 SQL 检查")
        sb.appendLine("  → 自动迁移支持")
        sb.appendLine()
        sb.appendLine("• WorkManager 代替 Service")
        sb.appendLine("  → 保证任务执行（即使应用退出）")
        sb.appendLine("  → 自动处理后台限制")
        sb.appendLine("  → 支持约束条件和链式任务")
        sb.appendLine()
        sb.appendLine("• DataStore 代替 SharedPreferences")
        sb.appendLine("  → 基于协程的异步 API")
        sb.appendLine("  → 类型安全（Proto DataStore）")
        sb.appendLine("  → 数据一致性保证")
        sb.appendLine()

        sb.appendLine("=== 3. 减少主线程工作量 ===")
        sb.appendLine()
        sb.appendLine("• 图片加载使用 Glide/Coil")
        sb.appendLine("  → 自动异步加载和缓存")
        sb.appendLine("  → 自动处理生命周期")
        sb.appendLine("  → 自动 downsampling")
        sb.appendLine()
        sb.appendLine("• JSON 解析使用 Moshi/kotlinx.serialization")
        sb.appendLine("  → 编译期生成代码，运行时更快")
        sb.appendLine("  → kotlinx.serialization 基于 Kotlin 编译器插件")
        sb.appendLine("  → 比反射方式（Gson）性能好很多")
        sb.appendLine()
        sb.appendLine("• 列表使用 DiffUtil 增量更新")
        sb.appendLine("  → 只更新变化的项")
        sb.appendLine("  → 计算在后台线程执行")
        sb.appendLine("  → 支持动画过渡")
        sb.appendLine()

        sb.appendLine("=== 4. 监控线上 ANR ===")
        sb.appendLine()
        sb.appendLine("• Firebase Crashlytics")
        sb.appendLine("  → Google 官方崩溃和 ANR 收集")
        sb.appendLine("  → 自动收集 ANR 堆栈")
        sb.appendLine("  → 按设备/版本/用户维度分析")
        sb.appendLine()
        sb.appendLine("• Google Play Console Vitals")
        sb.appendLine("  → ANR 发生率 (ANR rate)")
        sb.appendLine("  → 按设备型号/Android 版本分析")
        sb.appendLine("  → 用户感知的 ANR (user-perceived ANR)")
        sb.appendLine()
        sb.appendLine("• 自建 ANR 监控系统")
        sb.appendLine("  → Watchdog 方案实时检测")
        sb.appendLine("  → FileObserver 监听 /data/anr/ 目录")
        sb.appendLine("  → Signal (SIGQUIT) 捕获（高级方案）")
        sb.appendLine("  → 上报堆栈到自建服务端")
        sb.appendLine()

        sb.appendLine("=== 5. 性能优化 ===")
        sb.appendLine()
        sb.appendLine("• 避免过度布局（扁平化 View 层级）")
        sb.appendLine("  → 使用 ConstraintLayout 减少嵌套")
        sb.appendLine("  → 使用 ViewStub 延迟加载")
        sb.appendLine("  → 使用 merge 标签减少层级")
        sb.appendLine()
        sb.appendLine("• 使用 Profiler 分析耗时方法")
        sb.appendLine("  → CPU Profiler 找到热点方法")
        sb.appendLine("  → System.traceBegin/System.traceEnd 自定义追踪")
        sb.appendLine("  → Perfetto 系统级性能分析")
        sb.appendLine()
        sb.appendLine("• 预加载 + 缓存策略")
        sb.appendLine("  → Application.onCreate 中预加载必要数据")
        sb.appendLine("  → 使用 LRU 缓存减少重复计算")
        sb.appendLine("  → 分页加载避免一次性加载大量数据")
        sb.appendLine()
        sb.appendLine("=== 总结 ===")
        sb.appendLine("ANR 防治是一个系统工程:")
        sb.appendLine("  1. 开发期: StrictMode + 代码审查")
        sb.appendLine("  2. 测试期: Monkey 测试 + 自动化 ANR 检测")
        sb.appendLine("  3. 发布后: Crashlytics + Play Console Vitals")
        sb.appendLine("  4. 持续优化: Profiler 分析 + 性能监控")

        binding.tvResult.text = sb.toString()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
