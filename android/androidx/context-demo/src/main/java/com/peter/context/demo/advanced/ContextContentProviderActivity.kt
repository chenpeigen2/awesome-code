package com.peter.context.demo.advanced

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.peter.context.demo.databinding.ActivityContextContentproviderBinding

/**
 * Context 与 ContentProvider/ContentResolver
 *
 * Context 在 ContentProvider 中的核心作用：
 * 1. getContentResolver() - 获取 ContentResolver 实例
 * 2. ContentResolver 是访问 ContentProvider 的客户端接口
 * 3. 所有 CRUD 操作都通过 ContentResolver 进行
 *
 * ContentResolver 的作用：
 * - 统一管理不同数据源的访问（联系人、媒体、短信等）
 * - 提供标准的 CRUD 接口（query/insert/update/delete）
 * - 通过 URI 定位数据源
 * - 支持注册 ContentObserver 监听数据变化
 *
 * 常见 ContentProvider：
 * - ContactsContract - 联系人
 * - MediaStore - 媒体文件（图片、视频、音频）
 * - Settings - 系统设置
 * - CallLog - 通话记录
 * - CalendarContract - 日历
 * - UserDictionary - 用户字典
 */
class ContextContentProviderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContextContentproviderBinding
    private val sb = StringBuilder()

    private var contentObserver: ContentObserver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContextContentproviderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showContentProviderInfo()
    }

    private fun setupListeners() {
        binding.btnContentResolverIntro.setOnClickListener { showContentResolverIntro() }
        binding.btnQueryContacts.setOnClickListener { queryContacts() }
        binding.btnQueryMedia.setOnClickListener { queryMediaStore() }
        binding.btnQuerySettings.setOnClickListener { querySettings() }
        binding.btnContentObserver.setOnClickListener { demonstrateContentObserver() }
        binding.btnUnregisterObserver.setOnClickListener { unregisterObserver() }
        binding.btnContextRole.setOnClickListener { showContextRole() }
    }

    private fun showContentProviderInfo() {
        sb.clear()

        sb.appendLine("=== Context 与 ContentProvider ===\n")

        sb.appendLine("=== 1. 什么是 ContentProvider ===")
        sb.appendLine("Android 四大组件之一")
        sb.appendLine("用于在不同应用间共享数据")
        sb.appendLine("提供标准化的 CRUD 接口")
        sb.appendLine()

        sb.appendLine("=== 2. Context 的核心方法 ===")
        sb.appendLine("contentResolver = context.getContentResolver()")
        sb.appendLine("  → 获取 ContentResolver 实例")
        sb.appendLine()
        sb.appendLine("ContentResolver 方法:")
        sb.appendLine("  query(uri, projection, selection, args, sortOrder)")
        sb.appendLine("  insert(uri, values)")
        sb.appendLine("  update(uri, values, selection, args)")
        sb.appendLine("  delete(uri, selection, args)")
        sb.appendLine("  registerContentObserver(uri, notify, observer)")
        sb.appendLine("  unregisterContentObserver(observer)")
        sb.appendLine()

        sb.appendLine("=== 3. URI 结构 ===")
        sb.appendLine("content://authority/path/id")
        sb.appendLine("  ↑          ↑       ↑    ↑")
        sb.appendLine("  前缀    授权者   路径  ID")
        sb.appendLine()
        sb.appendLine("示例:")
        sb.appendLine("  联系人: content://com.android.contacts/contacts")
        sb.appendLine("  媒体:   content://media/external/images/media")
        sb.appendLine("  设置:   content://settings/system")
        sb.appendLine()

        sb.appendLine("=== 4. 权限要求 ===")
        sb.appendLine("  联系人: READ_CONTACTS")
        sb.appendLine("  媒体: READ_MEDIA_IMAGES (Android 13+)")
        sb.appendLine("  通话记录: READ_CALL_LOG")

        binding.tvInfo.text = sb.toString()
    }

    private fun showContentResolverIntro() {
        sb.clear()
        sb.appendLine("=== ContentResolver 详解 ===\n")

        sb.appendLine("=== 1. 获取方式 ===")
        sb.appendLine()
        sb.appendLine("// 在 Activity 中")
        sb.appendLine("val resolver = contentResolver")
        sb.appendLine()
        sb.appendLine("// 在 Fragment 中")
        sb.appendLine("val resolver = requireContext().contentResolver")
        sb.appendLine()
        sb.appendLine("// 在 Service 中")
        sb.appendLine("val resolver = contentResolver")
        sb.appendLine()
        sb.appendLine("// 在任意有 Context 的地方")
        sb.appendLine("val resolver = context.contentResolver")
        sb.appendLine()

        sb.appendLine("=== 2. ContentResolver 的工作原理 ===")
        sb.appendLine()
        sb.appendLine("ContentResolver 是一个代理/中间层:")
        sb.appendLine()
        sb.appendLine("  应用 → ContentResolver → AMS → ContentProvider")
        sb.appendLine()
        sb.appendLine("  1. 调用 ContentResolver 的方法")
        sb.appendLine("  2. 通过 URI 找到对应的 ContentProvider")
        sb.appendLine("  3. AMS 负责跨进程通信（Binder）")
        sb.appendLine("  4. ContentProvider 执行实际操作")
        sb.appendLine("  5. 结果通过 Binder 返回")
        sb.appendLine()

        sb.appendLine("=== 3. 获取当前 ContentResolver ===")
        sb.appendLine()
        val resolver = contentResolver
        sb.appendLine("实例: $resolver")
        sb.appendLine("类型: ${resolver.javaClass.simpleName}")
        sb.appendLine()
        sb.appendLine("注意:")
        sb.appendLine("  • ContentResolver 实例与 Context 绑定")
        sb.appendLine("  • 不同 Context 获取的是同一个 ContentResolver")
        sb.appendLine("  • 因为 ContentResolver 内部是单例模式")

        binding.tvResult.text = sb.toString()
    }

    /**
     * 查询联系人 — 演示通过 ContentResolver 查询系统数据
     *
     * 需要 android.permission.READ_CONTACTS 权限
     */
    private fun queryContacts() {
        sb.clear()
        sb.appendLine("=== 查询联系人 ===\n")

        // 通过 Context 获取 ContentResolver
        val resolver: ContentResolver = contentResolver
        sb.appendLine("ContentResolver: ${resolver.javaClass.simpleName}")
        sb.appendLine()

        sb.appendLine("=== URI ===")
        sb.appendLine("ContactsContract.Contacts.CONTENT_URI")
        sb.appendLine("  → ${ContactsContract.Contacts.CONTENT_URI}")
        sb.appendLine()

        sb.appendLine("=== 代码 ===")
        sb.appendLine("""
val resolver = contentResolver
val uri = ContactsContract.Contacts.CONTENT_URI
val projection = arrayOf(
    ContactsContract.Contacts._ID,
    ContactsContract.Contacts.DISPLAY_NAME
)
val cursor = resolver.query(uri, projection, null, null, "\${ContactsContract.Contacts._ID} DESC")
cursor?.use {
    while (it.moveToNext()) {
        val id = it.getLong(0)
        val name = it.getString(1)
    }
}
        """.trimIndent())
        sb.appendLine()

        sb.appendLine("=== 参数说明 ===")
        sb.appendLine("uri        - 数据源地址")
        sb.appendLine("projection - 要查询的列（null = 所有列）")
        sb.appendLine("selection  - WHERE 条件（null = 无条件）")
        sb.appendLine("selectionArgs - WHERE 参数值")
        sb.appendLine("sortOrder  - 排序方式（null = 默认排序）")
        sb.appendLine()

        // 实际查询
        sb.appendLine("=== 查询结果 ===")
        try {
            val cursor: Cursor? = resolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                arrayOf(
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME
                ),
                null,
                null,
                "${ContactsContract.Contacts._ID} DESC"
            )

            cursor?.use {
                sb.appendLine("总联系人数: ${it.count}")
                sb.appendLine()
                val count = minOf(it.count, 10)
                var displayed = 0
                while (it.moveToNext() && displayed < count) {
                    val id = it.getLong(0)
                    val name = it.getString(1)
                    sb.appendLine("  [$id] $name")
                    displayed++
                }
                if (it.count > 10) {
                    sb.appendLine("  ... 共 ${it.count} 条，仅显示前 10 条")
                }
            }

            if (cursor == null) {
                sb.appendLine("查询返回 null（权限可能未授予）")
            }
        } catch (e: SecurityException) {
            sb.appendLine("⚠ 需要 READ_CONTACTS 权限")
            sb.appendLine("错误: ${e.message}")
        }

        binding.tvResult.text = sb.toString()
    }

    /**
     * 查询媒体文件 — MediaStore
     *
     * Android 13+ 需要 READ_MEDIA_IMAGES 权限
     * Android 12 及以下需要 READ_EXTERNAL_STORAGE 权限
     */
    private fun queryMediaStore() {
        sb.clear()
        sb.appendLine("=== 查询媒体文件 (MediaStore) ===\n")

        val resolver = contentResolver

        sb.appendLine("=== URI ===")
        sb.appendLine("MediaStore.Images.Media.EXTERNAL_CONTENT_URI")
        sb.appendLine("  → ${MediaStore.Images.Media.EXTERNAL_CONTENT_URI}")
        sb.appendLine()

        sb.appendLine("=== 代码 ===")
        sb.appendLine("""
val resolver = contentResolver
val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
val projection = arrayOf(
    MediaStore.Images.Media._ID,
    MediaStore.Images.Media.DISPLAY_NAME,
    MediaStore.Images.Media.SIZE,
    MediaStore.Images.Media.DATE_ADDED
)
val cursor = resolver.query(uri, projection, null, null, "\${MediaStore.Images.Media.DATE_ADDED} DESC")
        """.trimIndent())
        sb.appendLine()

        sb.appendLine("=== MediaStore 常用 URI ===")
        sb.appendLine("MediaStore.Images.Media.EXTERNAL_CONTENT_URI")
        sb.appendLine("  → 外部存储的图片")
        sb.appendLine("MediaStore.Video.Media.EXTERNAL_CONTENT_URI")
        sb.appendLine("  → 外部存储的视频")
        sb.appendLine("MediaStore.Audio.Media.EXTERNAL_CONTENT_URI")
        sb.appendLine("  → 外部存储的音频")
        sb.appendLine("MediaStore.Downloads.EXTERNAL_CONTENT_URI")
        sb.appendLine("  → 下载文件 (Android 10+)")
        sb.appendLine()

        // 实际查询
        sb.appendLine("=== 查询结果 ===")
        try {
            val cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE,
                    MediaStore.Images.Media.DATE_ADDED
                ),
                null,
                null,
                "${MediaStore.Images.Media.DATE_ADDED} DESC"
            )

            cursor?.use {
                sb.appendLine("图片总数: ${it.count}")
                sb.appendLine()
                var displayed = 0
                while (it.moveToNext() && displayed < 5) {
                    val name = it.getString(1)
                    val size = it.getLong(2)
                    sb.appendLine("  $name (${formatSize(size)})")
                    displayed++
                }
                if (it.count > 5) {
                    sb.appendLine("  ... 共 ${it.count} 张，仅显示前 5 张")
                }
            }

            if (cursor == null) {
                sb.appendLine("查询返回 null")
            }
        } catch (e: SecurityException) {
            sb.appendLine("⚠ 需要存储权限")
            sb.appendLine("Android 13+: READ_MEDIA_IMAGES")
            sb.appendLine("Android 12-: READ_EXTERNAL_STORAGE")
            sb.appendLine("错误: ${e.message}")
        }

        binding.tvResult.text = sb.toString()
    }

    /**
     * 查询系统设置 — 不需要特殊权限
     */
    private fun querySettings() {
        sb.clear()
        sb.appendLine("=== 查询系统设置 ===\n")

        val resolver = contentResolver

        sb.appendLine("=== Settings 提供者 ===")
        sb.appendLine()
        sb.appendLine("三个设置命名空间:")
        sb.appendLine("  Settings.System - 通用系统设置")
        sb.appendLine("  Settings.Secure - 安全设置（不可修改）")
        sb.appendLine("  Settings.Global - 全局设置（所有用户共享）")
        sb.appendLine()

        sb.appendLine("=== 读取设置值 ===")
        sb.appendLine()
        sb.appendLine("代码:")
        sb.appendLine("  val value = Settings.System.getString(")
        sb.appendLine("      contentResolver, Settings.System.SCREEN_BRIGHTNESS)")
        sb.appendLine("  // 使用 contentResolver 读取设置")
        sb.appendLine()

        // 实际读取一些设置
        sb.appendLine("=== 系统设置 (Settings.System) ===")
        try {
            val brightness = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS)
            sb.appendLine("  屏幕亮度: $brightness")
        } catch (e: Exception) {
            sb.appendLine("  屏幕亮度: 读取失败")
        }

        try {
            val offTimeout = Settings.System.getInt(resolver, Settings.System.SCREEN_OFF_TIMEOUT)
            sb.appendLine("  屏幕超时: ${offTimeout / 1000} 秒")
        } catch (e: Exception) {
            sb.appendLine("  屏幕超时: 读取失败")
        }
        sb.appendLine()

        sb.appendLine("=== 安全设置 (Settings.Secure) ===")
        try {
            val androidId = Settings.Secure.getString(resolver, Settings.Secure.ANDROID_ID)
            sb.appendLine("  Android ID: $androidId")
        } catch (e: Exception) {
            sb.appendLine("  Android ID: 读取失败")
        }
        sb.appendLine()

        sb.appendLine("=== 全局设置 (Settings.Global) ===")
        try {
            val airplaneMode = Settings.Global.getInt(resolver, Settings.Global.AIRPLANE_MODE_ON)
            sb.appendLine("  飞行模式: ${if (airplaneMode == 1) "开启" else "关闭"}")
        } catch (e: Exception) {
            sb.appendLine("  飞行模式: 读取失败")
        }

        try {
            val wifiOn = Settings.Global.getInt(resolver, Settings.Global.WIFI_ON)
            sb.appendLine("  WiFi 状态: ${if (wifiOn == 1) "开启" else "关闭"}")
        } catch (e: Exception) {
            sb.appendLine("  WiFi 状态: 读取失败")
        }
        sb.appendLine()

        sb.appendLine("注意:")
        sb.appendLine("  • 读取 Settings.System 部分不需要权限")
        sb.appendLine("  • 修改需要 WRITE_SETTINGS 权限")
        sb.appendLine("  • Settings.Secure 只有读取权限，不能修改")
        sb.appendLine("  • 所有 Settings 操作都依赖 contentResolver")

        binding.tvResult.text = sb.toString()
    }

    /**
     * ContentObserver — 监听数据变化
     */
    private fun demonstrateContentObserver() {
        sb.clear()
        sb.appendLine("=== ContentObserver 数据监听 ===\n")

        sb.appendLine("=== 1. 原理 ===")
        sb.appendLine("ContentObserver 基于观察者模式")
        sb.appendLine("当 ContentProvider 数据变化时通知观察者")
        sb.appendLine()
        sb.appendLine("注册流程:")
        sb.appendLine("  1. 创建 ContentObserver 子类")
        sb.appendLine("  2. 通过 contentResolver 注册观察者")
        sb.appendLine("  3. 数据变化时 onChange() 被回调")
        sb.appendLine("  4. 不需要时注销观察者")
        sb.appendLine()

        sb.appendLine("=== 2. 代码 ===")
        sb.appendLine("""
// 创建观察者
val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        // 数据变化时的回调
        Log.d("Observer", "数据发生了变化")
    }
}

// 注册观察者
contentResolver.registerContentObserver(
    Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS),
    true,  // notifyForDescendants
    observer
)

// 注销观察者
contentResolver.unregisterContentObserver(observer)
        """.trimIndent())
        sb.appendLine()

        // 实际注册观察者
        if (contentObserver != null) {
            sb.appendLine("已有观察者在监听，请先注销")
            binding.tvResult.text = sb.toString()
            return
        }

        contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                Log.d("ContentObserver", "屏幕亮度设置发生了变化")
                runOnUiThread {
                    Toast.makeText(
                        this@ContextContentProviderActivity,
                        "检测到屏幕亮度变化",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // 使用 contentResolver 注册
        contentResolver.registerContentObserver(
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS),
            true,
            contentObserver!!
        )

        sb.appendLine("✓ 已注册屏幕亮度观察者")
        sb.appendLine()
        sb.appendLine("请尝试在系统设置中修改屏幕亮度")
        sb.appendLine("修改后会收到 onChange 回调")

        binding.tvResult.text = sb.toString()
    }

    private fun unregisterObserver() {
        if (contentObserver == null) {
            sb.clear()
            sb.appendLine("没有已注册的观察者")
            binding.tvResult.text = sb.toString()
            return
        }

        contentResolver.unregisterContentObserver(contentObserver!!)
        contentObserver = null

        sb.clear()
        sb.appendLine("✓ 观察者已注销")
        binding.tvResult.text = sb.toString()
    }

    private fun showContextRole() {
        sb.clear()
        sb.appendLine("=== ContentProvider 中 Context 的角色 ===\n")

        sb.appendLine("=== 1. 获取 ContentResolver ===")
        sb.appendLine()
        sb.appendLine("Context 是获取 ContentResolver 的唯一入口:")
        sb.appendLine()
        sb.appendLine("  Activity → getContentResolver()")
        sb.appendLine("  Service → getContentResolver()")
        sb.appendLine("  Application → getContentResolver()")
        sb.appendLine("  BroadcastReceiver → context.contentResolver")
        sb.appendLine()
        sb.appendLine("本质上调用的都是 Context.getContentResolver()")
        sb.appendLine()

        sb.appendLine("=== 2. Context 的选择 ===")
        sb.appendLine()
        sb.appendLine("查询数据（query）:")
        sb.appendLine("  → 任意 Context 均可")
        sb.appendLine("  → 因为 query 不涉及 UI")
        sb.appendLine()
        sb.appendLine("注册 ContentObserver:")
        sb.appendLine("  → 推荐 Activity/Service Context")
        sb.appendLine("  → 需要在 onDestroy 取消注册")
        sb.appendLine("  → 避免内存泄漏")
        sb.appendLine()
        sb.appendLine("通知数据变化（notifyChange）:")
        sb.appendLine("  → 通常在 ContentProvider 内部调用")
        sb.appendLine("  → getContext().getContentResolver().notifyChange(uri, null)")
        sb.appendLine()

        sb.appendLine("=== 3. 自定义 ContentProvider ===")
        sb.appendLine()
        sb.appendLine("class MyProvider : ContentProvider() {")
        sb.appendLine("    override fun onCreate(): Boolean {")
        sb.appendLine("        // 这里可以获取 Context")
        sb.appendLine("        val context = context  // 由系统注入")
        sb.appendLine("        return true")
        sb.appendLine("    }")
        sb.appendLine()
        sb.appendLine("    override fun query(...): Cursor? {")
        sb.appendLine("        // 使用 context 获取数据库等")
        sb.appendLine("        val db = context?.let {")
        sb.appendLine("            SQLiteOpenHelper(it).readableDatabase")
        sb.appendLine("        }")
        sb.appendLine("        return db?.query(...)")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine()

        sb.appendLine("=== 4. 线程安全 ===")
        sb.appendLine()
        sb.appendLine("ContentResolver 的 CRUD 操作:")
        sb.appendLine("  • query() - 可能在主线程执行")
        sb.appendLine("  • insert/update/delete - 同上")
        sb.appendLine("  • 推荐在协程或线程中执行耗时查询")
        sb.appendLine()
        sb.appendLine("ContentObserver.onChange():")
        sb.appendLine("  • 回调在注册时指定的 Handler 线程")
        sb.appendLine("  • 通常使用 Handler(Looper.getMainLooper())")

        binding.tvResult.text = sb.toString()
    }

    private fun formatSize(bytes: Long): String = when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> String.format("%.1f KB", bytes / 1024.0)
        bytes < 1024 * 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024))
        else -> String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024))
    }

    override fun onDestroy() {
        super.onDestroy()
        contentObserver?.let {
            contentResolver.unregisterContentObserver(it)
        }
    }
}

private fun Context.toast(msg: String) {
    android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show()
}
