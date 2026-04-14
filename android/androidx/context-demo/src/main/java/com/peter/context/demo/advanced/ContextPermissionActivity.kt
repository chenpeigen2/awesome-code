package com.peter.context.demo.advanced

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.peter.context.demo.databinding.ActivityContextPermissionBinding

/**
 * Context 与运行时权限
 *
 * Context 在权限系统中的核心作用：
 * 1. ContextCompat.checkSelfPermission() - 检查权限状态
 * 2. ActivityCompat.requestPermissions() - 请求权限
 * 3. ContextWrapper 提供权限相关的 Context 方法
 *
 * 权限分类：
 * - 普通权限 - 安装时自动授予（如 INTERNET、VIBRATE）
 * - 危险权限 - 需要运行时请求（如 CAMERA、LOCATION）
 * - 特殊权限 - 需要在系统设置中手动授予（如 悬浮窗、通知）
 *
 * 危险权限分组：
 * - CALENDAR - 日历
 * - CAMERA - 相机
 * - CONTACTS - 联系人
 * - LOCATION - 位置
 * - MICROPHONE - 麦克风
 * - PHONE - 电话
 * - SENSORS - 传感器
 * - SMS - 短信
 * - STORAGE - 存储
 */
class ContextPermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContextPermissionBinding
    private val sb = StringBuilder()

    // 使用 Activity Result API 请求权限（推荐方式）
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        sb.clear()
        sb.appendLine("=== 权限请求结果 ===\n")

        permissions.forEach { (permission, granted) ->
            val status = if (granted) "✓ 已授予" else "✗ 被拒绝"
            val label = getPermissionLabel(permission)
            sb.appendLine("$label: $status")

            if (!granted) {
                // 检查是否应该展示理由
                val shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(
                    this, permission
                )
                sb.appendLine("  shouldShowRationale: $shouldShow")
            }
        }

        binding.tvResult.text = sb.toString()
    }

    // 单个权限请求
    private val singlePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        sb.clear()
        sb.appendLine("=== 单个权限请求结果 ===\n")
        sb.appendLine("结果: ${if (granted) "✓ 已授予" else "✗ 被拒绝"}")
        sb.appendLine()
        sb.appendLine("如果被拒绝:")
        sb.appendLine("  • shouldShowRationale = true → 用户选了拒绝但没选不再询问")
        sb.appendLine("  • shouldShowRationale = false → 用户选了不再询问或首次拒绝")
        binding.tvResult.text = sb.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContextPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showPermissionInfo()
    }

    private fun setupListeners() {
        binding.btnCheckPermission.setOnClickListener { checkPermissionStatus() }
        binding.btnRequestCamera.setOnClickListener { requestCameraPermission() }
        binding.btnRequestLocation.setOnClickListener { requestLocationPermission() }
        binding.btnRequestNotification.setOnClickListener { requestNotificationPermission() }
        binding.btnRequestOverlay.setOnClickListener { requestOverlayPermission() }
        binding.btnRequestAlarmExact.setOnClickListener { requestExactAlarmPermission() }
        binding.btnPermissionCompare.setOnClickListener { showPermissionCompare() }
        binding.btnBestPractices.setOnClickListener { showBestPractices() }
    }

    private fun showPermissionInfo() {
        sb.clear()

        sb.appendLine("=== Context 与运行时权限 ===\n")

        sb.appendLine("=== 1. 权限发展历史 ===")
        sb.appendLine("Android 5.1 及以前: 安装时一次性授予所有权限")
        sb.appendLine("Android 6.0 (M): 引入运行时权限")
        sb.appendLine("Android 10: 后台位置需要单独授权")
        sb.appendLine("Android 11: 单次授权，权限自动回收")
        sb.appendLine("Android 12: 附近设备权限 (BLUETOOTH_SCAN)")
        sb.appendLine("Android 13: 通知权限 (POST_NOTIFICATIONS)")
        sb.appendLine("Android 14: 部分权限需升级为精确授权")
        sb.appendLine()

        sb.appendLine("=== 2. Context 相关方法 ===")
        sb.appendLine("检查权限:")
        sb.appendLine("  ContextCompat.checkSelfPermission(context, permission)")
        sb.appendLine("  返回: PERMISSION_GRANTED / PERMISSION_DENIED")
        sb.appendLine()
        sb.appendLine("请求权限:")
        sb.appendLine("  ActivityCompat.requestPermissions(activity, perms, code)")
        sb.appendLine("  或 Activity Result API (推荐)")
        sb.appendLine()
        sb.appendLine("判断是否需要展示理由:")
        sb.appendLine("  shouldShowRequestPermissionRationale(permission)")
        sb.appendLine()

        sb.appendLine("=== 3. 请求流程 ===")
        sb.appendLine("1. checkSelfPermission() 检查权限")
        sb.appendLine("2. 如果已授予 → 直接使用")
        sb.appendLine("3. 如果未授予 → shouldShowRationale()")
        sb.appendLine("4. 如果需要展示理由 → 向用户解释")
        sb.appendLine("5. requestPermissions() 请求权限")
        sb.appendLine("6. 处理请求结果")

        binding.tvInfo.text = sb.toString()
    }

    /**
     * 检查权限状态
     */
    private fun checkPermissionStatus() {
        sb.clear()
        sb.appendLine("=== 检查权限状态 ===\n")

        sb.appendLine("=== 常用权限检查 ===\n")

        val permissions = listOf(
            "相机" to Manifest.permission.CAMERA,
            "位置(粗略)" to Manifest.permission.ACCESS_COARSE_LOCATION,
            "位置(精确)" to Manifest.permission.ACCESS_FINE_LOCATION,
            "联系人" to Manifest.permission.READ_CONTACTS,
            "麦克风" to Manifest.permission.RECORD_AUDIO,
            "存储" to Manifest.permission.READ_EXTERNAL_STORAGE,
            "电话" to Manifest.permission.READ_PHONE_STATE,
            "日历" to Manifest.permission.READ_CALENDAR,
            "传感器" to Manifest.permission.BODY_SENSORS,
            "短信" to Manifest.permission.READ_SMS
        )

        sb.appendLine("代码:")
        sb.appendLine("  val result = ContextCompat")
        sb.appendLine("      .checkSelfPermission(context, permission)")
        sb.appendLine("  result == PackageManager.PERMISSION_GRANTED")
        sb.appendLine()

        permissions.forEach { (label, permission) ->
            val result = ContextCompat.checkSelfPermission(this, permission)
            val status = if (result == PackageManager.PERMISSION_GRANTED) "✓ 已授予" else "✗ 未授予"
            sb.appendLine("  $label: $status")
        }

        sb.appendLine()

        // Android 13+ 特殊权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            sb.appendLine("=== Android 13+ 权限 ===")
            val notificationPerm = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            )
            sb.appendLine("  通知: ${if (notificationPerm == PackageManager.PERMISSION_GRANTED) "✓ 已授予" else "✗ 未授予"}")

            val mediaImages = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_IMAGES
            )
            sb.appendLine("  媒体图片: ${if (mediaImages == PackageManager.PERMISSION_GRANTED) "✓ 已授予" else "✗ 未授予"}")
        }

        binding.tvResult.text = sb.toString()
    }

    /**
     * 请求相机权限 — 标准危险权限请求流程
     */
    private fun requestCameraPermission() {
        sb.clear()
        sb.appendLine("=== 请求相机权限 ===\n")

        // 第1步：检查当前权限状态
        val hasPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        sb.appendLine("当前状态: ${if (hasPermission) "已授予" else "未授予"}")
        sb.appendLine()

        if (hasPermission) {
            sb.appendLine("✓ 权限已授予，无需再次请求")
            sb.appendLine()
            sb.appendLine("可以直接使用相机功能")
            binding.tvResult.text = sb.toString()
            return
        }

        // 第2步：检查是否需要展示理由
        val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this, Manifest.permission.CAMERA
        )

        sb.appendLine("shouldShowRationale: $shouldShowRationale")
        sb.appendLine()

        if (shouldShowRationale) {
            sb.appendLine("用户之前拒绝过，建议展示使用理由")
            sb.appendLine()
        }

        sb.appendLine("=== 请求代码 ===")
        sb.appendLine()
        sb.appendLine("方式1: Activity Result API（推荐）")
        sb.appendLine("""
// 注册
val launcher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
) { granted ->
    if (granted) {
        // 权限已授予
    } else {
        // 权限被拒绝
    }
}

// 请求
launcher.launch(Manifest.permission.CAMERA)
        """.trimIndent())
        sb.appendLine()

        sb.appendLine("方式2: ActivityCompat（旧方式）")
        sb.appendLine("""
// 请求
ActivityCompat.requestPermissions(
    this,
    arrayOf(Manifest.permission.CAMERA),
    REQUEST_CODE
)

// 回调
override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
) {
    if (requestCode == REQUEST_CODE) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 已授予
        }
    }
}
        """.trimIndent())

        // 使用 Activity Result API 请求
        singlePermissionLauncher.launch(Manifest.permission.CAMERA)

        binding.tvResult.text = sb.toString()
    }

    /**
     * 请求位置权限 — 演示同时请求多个权限
     */
    private fun requestLocationPermission() {
        sb.clear()
        sb.appendLine("=== 请求位置权限 ===\n")

        sb.appendLine("位置权限分为两种:")
        sb.appendLine("  ACCESS_FINE_LOCATION - 精确位置 (GPS)")
        sb.appendLine("  ACCESS_COARSE_LOCATION - 大致位置 (网络/WiFi)")
        sb.appendLine()
        sb.appendLine("Android 12+ 额外要求:")
        sb.appendLine("  后台位置需要单独请求 ACCESS_BACKGROUND_LOCATION")
        sb.appendLine("  且必须在前台位置已授予后才能请求")
        sb.appendLine()

        sb.appendLine("=== 请求代码 ===")
        sb.appendLine("""
val launcher = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
) { permissions ->
    permissions.forEach { (perm, granted) ->
        // 处理每个权限结果
    }
}

launcher.launch(arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
))
        """.trimIndent())
        sb.appendLine()

        sb.appendLine("正在请求位置权限...")

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        binding.tvResult.text = sb.toString()
    }

    /**
     * 通知权限 — Android 13+ 新增的特殊权限
     */
    private fun requestNotificationPermission() {
        sb.clear()
        sb.appendLine("=== 通知权限 (Android 13+) ===\n")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            sb.appendLine("Android 13 引入了 POST_NOTIFICATIONS 权限")
            sb.appendLine("应用必须获得该权限才能发送通知")
            sb.appendLine()

            val hasPermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            sb.appendLine("当前状态: ${if (hasPermission) "已授予" else "未授予"}")
            sb.appendLine()

            if (!hasPermission) {
                sb.appendLine("正在请求通知权限...")
                singlePermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                sb.appendLine("✓ 权限已授予")
            }
        } else {
            sb.appendLine("Android 13 以下版本:")
            sb.appendLine("  不需要 POST_NOTIFICATIONS 权限")
            sb.appendLine("  通知默认可以发送")
            sb.appendLine()
            sb.appendLine("但建议提前适配:")
            sb.appendLine("""
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED) {
        requestPermissionLauncher.launch(
            Manifest.permission.POST_NOTIFICATIONS
        )
    }
}
            """.trimIndent())
        }

        binding.tvResult.text = sb.toString()
    }

    /**
     * 悬浮窗权限 — 特殊权限，需要在设置中手动授予
     */
    private fun requestOverlayPermission() {
        sb.clear()
        sb.appendLine("=== 悬浮窗权限 (特殊权限) ===\n")

        sb.appendLine("SYSTEM_ALERT_WINDOW 是特殊权限")
        sb.appendLine("不能通过标准的 requestPermissions() 请求")
        sb.appendLine("需要引导用户到系统设置中手动开启")
        sb.appendLine()

        // 检查是否已有权限
        val hasPermission = Settings.canDrawOverlays(this)
        sb.appendLine("当前状态: ${if (hasPermission) "✓ 已授予" else "✗ 未授予"}")
        sb.appendLine()

        sb.appendLine("=== 检查方法 ===")
        sb.appendLine("  Settings.canDrawOverlays(context)")
        sb.appendLine()
        sb.appendLine("=== 请求方法 ===")
        sb.appendLine("  val intent = Intent(")
        sb.appendLine("      Settings.ACTION_MANAGE_OVERLAY_PERMISSION,")
        sb.appendLine("      Uri.parse(\"package:\$packageName\")")
        sb.appendLine("  )")
        sb.appendLine("  startActivity(intent)")
        sb.appendLine()

        sb.appendLine("=== 其他特殊权限 ===")
        sb.appendLine("  • SYSTEM_ALERT_WINDOW - 悬浮窗")
        sb.appendLine("  • WRITE_SETTINGS - 修改系统设置")
        sb.appendLine("  • REQUEST_IGNORE_BATTERY_OPTIMIZATIONS - 电池优化白名单")
        sb.appendLine("  • SCHEDULE_EXACT_ALARM - 精确闹钟 (Android 12+)")
        sb.appendLine()

        if (!hasPermission) {
            sb.appendLine("正在打开悬浮窗设置页面...")
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }

        binding.tvResult.text = sb.toString()
    }

    /**
     * 精确闹钟权限 — Android 12+ 特殊权限
     */
    private fun requestExactAlarmPermission() {
        sb.clear()
        sb.appendLine("=== 精确闹钟权限 (Android 12+) ===\n")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val canScheduleExact = alarmManager.canScheduleExactAlarms()

            sb.appendLine("SCHEDULE_EXACT_ALARMS 权限:")
            sb.appendLine("  当前状态: ${if (canScheduleExact) "✓ 已授予" else "✗ 未授予"}")
            sb.appendLine()

            sb.appendLine("检查方法:")
            sb.appendLine("  val am = context.getSystemService(Context.ALARM_SERVICE)")
            sb.appendLine("      as AlarmManager")
            sb.appendLine("  am.canScheduleExactAlarms()")
            sb.appendLine()

            sb.appendLine("请求方法:")
            sb.appendLine("  val intent = Intent(")
            sb.appendLine("      Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)")
            sb.appendLine("  startActivity(intent)")
            sb.appendLine()

            if (!canScheduleExact) {
                sb.appendLine("正在打开精确闹钟设置...")
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        } else {
            sb.appendLine("Android 12 以下:")
            sb.appendLine("  不需要 SCHEDULE_EXACT_ALARMS 权限")
            sb.appendLine("  可以直接使用 setExact() 方法")
        }

        binding.tvResult.text = sb.toString()
    }

    /**
     * 不同 Context 对权限的影响
     */
    private fun showPermissionCompare() {
        sb.clear()
        sb.appendLine("=== 权限中 Context 的选择 ===\n")

        sb.appendLine("=== 1. checkSelfPermission ===")
        sb.appendLine()
        sb.appendLine("任意 Context 都可以检查权限:")
        sb.appendLine("  ContextCompat.checkSelfPermission(activity, perm)")
        sb.appendLine("  ContextCompat.checkSelfPermission(applicationContext, perm)")
        sb.appendLine("  ContextCompat.checkSelfPermission(service, perm)")
        sb.appendLine()
        sb.appendLine("结果相同 — 权限状态是进程级别的，与 Context 无关")
        sb.appendLine()

        sb.appendLine("=== 2. requestPermissions ===")
        sb.appendLine()
        sb.appendLine("必须是 Activity Context:")
        sb.appendLine("  ActivityCompat.requestPermissions(activity, perms, code)")
        sb.appendLine()
        sb.appendLine("✗ 不能使用 Application Context:")
        sb.appendLine("  ActivityCompat.requestPermissions(appContext, ...)")
        sb.appendLine("  → 会崩溃！权限请求对话框是 Activity 级别的")
        sb.appendLine()
        sb.appendLine("✗ 不能在 Service 中直接请求:")
        sb.appendLine("  → Service 没有 UI，无法显示权限对话框")
        sb.appendLine("  → 需要启动一个 Activity 来请求")
        sb.appendLine()

        sb.appendLine("=== 3. shouldShowRequestPermissionRationale ===")
        sb.appendLine()
        sb.appendLine("也必须使用 Activity:")
        sb.appendLine("  activity.shouldShowRequestPermissionRationale(perm)")
        sb.appendLine("  → 返回 true: 用户之前拒绝但没选「不再询问」")
        sb.appendLine("  → 返回 false: 首次请求或用户选了「不再询问」")
        sb.appendLine()

        sb.appendLine("=== 4. 特殊权限 ===")
        sb.appendLine()
        sb.appendLine("特殊权限不使用标准的 requestPermissions 流程:")
        sb.appendLine("  悬浮窗: Settings.canDrawOverlays(context)")
        sb.appendLine("  系统设置: Settings.System.canWrite(context)")
        sb.appendLine("  → 任意 Context 都可以检查")
        sb.appendLine("  → 但请求需要打开系统设置页面 (需要 Activity)")

        binding.tvResult.text = sb.toString()
    }

    private fun showBestPractices() {
        sb.clear()
        sb.appendLine("=== 权限最佳实践 ===\n")

        sb.appendLine("=== 1. 请求时机 ===")
        sb.appendLine("✓ 在用户触发相关功能时请求")
        sb.appendLine("✗ 不要在应用启动时一次性请求所有权限")
        sb.appendLine("✓ 先解释为什么需要该权限")
        sb.appendLine()

        sb.appendLine("=== 2. 处理拒绝 ===")
        sb.appendLine("""
when {
    // 权限已授予
    ContextCompat.checkSelfPermission(ctx, perm)
        == PERMISSION_GRANTED -> {
        // 执行需要权限的操作
    }
    // 需要展示理由
    ActivityCompat.shouldShowRequestPermissionRationale(act, perm) -> {
        // 展示解释对话框
        showRationaleDialog()
    }
    // 首次请求或用户选了不再询问
    else -> {
        // 直接请求权限
        requestPermissionLauncher.launch(perm)
    }
}
        """.trimIndent())
        sb.appendLine()

        sb.appendLine("=== 3. 用户选择「不再询问」后的处理 ===")
        sb.appendLine("""
// 检测用户是否选了「不再询问」
if (!ActivityCompat.shouldShowRequestPermissionRationale(act, perm)
    && ContextCompat.checkSelfPermission(ctx, perm)
        != PERMISSION_GRANTED) {
    // 引导用户到应用设置页面手动开启
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = Uri.parse("package:\$packageName")
    startActivity(intent)
}
        """.trimIndent())
        sb.appendLine()

        sb.appendLine("=== 4. 封装权限请求工具类 ===")
        sb.appendLine("""
class PermissionHelper(private val activity: AppCompatActivity) {

    private val launcher = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val allGranted = results.all { it.value }
        callback?.invoke(allGranted, results)
    }

    private var callback: ((Boolean, Map<String, Boolean>) -> Unit)? = null

    fun request(
        permissions: Array<String>,
        onResult: (Boolean, Map<String, Boolean>) -> Unit
    ) {
        callback = onResult
        launcher.launch(permissions)
    }

    fun hasAll(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(activity, it)
                == PackageManager.PERMISSION_GRANTED
        }
    }
}
        """.trimIndent())

        binding.tvResult.text = sb.toString()
    }

    private fun getPermissionLabel(permission: String): String = when (permission) {
        Manifest.permission.CAMERA -> "相机"
        Manifest.permission.ACCESS_FINE_LOCATION -> "位置(精确)"
        Manifest.permission.ACCESS_COARSE_LOCATION -> "位置(粗略)"
        Manifest.permission.READ_CONTACTS -> "联系人"
        Manifest.permission.RECORD_AUDIO -> "麦克风"
        Manifest.permission.READ_EXTERNAL_STORAGE -> "存储"
        else -> permission.substringAfterLast(".")
    }
}
