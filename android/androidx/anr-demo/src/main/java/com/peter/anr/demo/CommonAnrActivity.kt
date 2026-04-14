package com.peter.anr.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.peter.anr.demo.databinding.ActivityCommonAnrBinding
import java.io.File

/**
 * 常见 ANR 场景演示
 *
 * 本 Demo 展示实际开发中最容易引发 ANR 的 5 种场景：
 *
 * 1. SharedPreferences commit() — 同步写入阻塞主线程
 * 2. SQLite 数据库操作 — 大数据量查询/插入
 * 3. 文件 I/O 操作 — 主线程读写文件
 * 4. Binder 通信 — 跨进程调用耗时操作
 * 5. 序列化/反序列化 — JSON 解析大文件、Parcelable 传递大数据
 *
 * 每个场景都会展示错误写法（会导致 ANR）和正确写法，
 * 帮助开发者在实际项目中避免这些坑。
 */
class CommonAnrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommonAnrBinding
    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommonAnrBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showCommonAnrInfo()
    }

    private fun setupListeners() {
        binding.btnSpCommit.setOnClickListener { demonstrateSpCommit() }
        binding.btnDatabase.setOnClickListener { demonstrateDatabase() }
        binding.btnFileIO.setOnClickListener { demonstrateFileIO() }
        binding.btnBinder.setOnClickListener { demonstrateBinder() }
        binding.btnSerialization.setOnClickListener { demonstrateSerialization() }
    }

    /**
     * 显示常见 ANR 场景的概览信息
     */
    private fun showCommonAnrInfo() {
        sb.clear()
        sb.appendLine("=== 常见 ANR 场景 ===")
        sb.appendLine()
        sb.appendLine("=== 1. SharedPreferences commit() ===")
        sb.appendLine("SP 的 commit() 是同步写入，在主线程调用会阻塞")
        sb.appendLine()
        sb.appendLine("=== 2. SQLite 数据库操作 ===")
        sb.appendLine("大数据量的查询/插入在主线程执行")
        sb.appendLine()
        sb.appendLine("=== 3. 文件 I/O 操作 ===")
        sb.appendLine("在主线程读写文件")
        sb.appendLine()
        sb.appendLine("=== 4. Binder 通信 ===")
        sb.appendLine("跨进程调用耗时操作（ContentResolver.query 等）")
        sb.appendLine()
        sb.appendLine("=== 5. 序列化/反序列化 ===")
        sb.appendLine("JSON 解析大文件、Parcelable 传递大数据")

        binding.tvInfo.text = sb.toString()
    }

    /**
     * SharedPreferences commit() 演示
     *
     * commit() 是同步写入磁盘的，数据量大时会在主线程阻塞。
     * apply() 是异步写入，不会阻塞主线程。
     */
    private fun demonstrateSpCommit() {
        sb.clear()
        sb.appendLine("=== SharedPreferences commit() ANR ===")
        sb.appendLine()

        sb.appendLine("=== 错误写法 ===")
        sb.appendLine()
        sb.appendLine("// ❌ 错误：主线程 commit 大量数据")
        sb.appendLine("val sp = getSharedPreferences(\"anr_test\", MODE_PRIVATE)")
        sb.appendLine("sp.edit().apply {")
        sb.appendLine("    for (i in 1..1000) {")
        sb.appendLine("        putString(\"key_\$i\", \"value_\$i\".repeat(100))")
        sb.appendLine("    }")
        sb.appendLine("    commit() // 同步写入 → 可能 ANR")
        sb.appendLine("}")
        sb.appendLine()

        sb.appendLine("=== 正确写法 ===")
        sb.appendLine()
        sb.appendLine("// ✓ 正确：使用 apply() 异步写入")
        sb.appendLine("sp.edit().apply {")
        sb.appendLine("    for (i in 1..1000) {")
        sb.appendLine("        putString(\"key_\$i\", \"value_\$i\".repeat(100))")
        sb.appendLine("    }")
        sb.appendLine("    apply() // 异步写入，不会阻塞主线程")
        sb.appendLine("}")
        sb.appendLine()

        // 实际演示：写入少量数据并测量时间
        sb.appendLine("=== 实测对比 ===")
        sb.appendLine()

        val sp = getSharedPreferences("anr_test", MODE_PRIVATE)

        // commit() 测试
        val commitStart = System.currentTimeMillis()
        sp.edit().apply {
            for (i in 1..100) {
                putString("key_$i", "value_$i".repeat(50))
            }
            commit()
        }
        val commitTime = System.currentTimeMillis() - commitStart
        sb.appendLine("commit() 写入 100 条耗时: ${commitTime}ms")
        sb.appendLine()

        // apply() 测试
        val applyStart = System.currentTimeMillis()
        sp.edit().apply {
            for (i in 1..100) {
                putString("key_apply_$i", "value_$i".repeat(50))
            }
            apply()
        }
        val applyTime = System.currentTimeMillis() - applyStart
        sb.appendLine("apply() 写入 100 条耗时: ${applyTime}ms (异步返回)")
        sb.appendLine()

        sb.appendLine("=== 结论 ===")
        sb.appendLine("commit() 同步等待磁盘写入完成，数据量越大越慢")
        sb.appendLine("apply() 立即返回，后台异步写入")
        sb.appendLine("推荐：始终使用 apply()，除非需要确认写入成功")
        sb.appendLine()
        sb.appendLine("注意：apply() 也不是完全没有风险")
        sb.appendLine("Activity.onPause() 时会等待 apply() 完成")
        sb.appendLine("所以也不要在 onPause 中调用大量 apply()")

        binding.tvResult.text = sb.toString()
    }

    /**
     * SQLite 数据库操作演示
     *
     * 在主线程执行大量数据库操作（插入、查询）会阻塞主线程导致 ANR。
     * 推荐使用 Room + 协程进行数据库操作。
     */
    private fun demonstrateDatabase() {
        sb.clear()
        sb.appendLine("=== SQLite 数据库操作 ANR ===")
        sb.appendLine()

        sb.appendLine("=== 错误写法 ===")
        sb.appendLine()
        sb.appendLine("// ❌ 错误：主线程大量数据库操作")
        sb.appendLine("val db = openOrCreateDatabase(\"anr_test\", MODE_PRIVATE, null)")
        sb.appendLine("db.execSQL(\"CREATE TABLE IF NOT EXISTS users (\")")
        sb.appendLine("    + \"id INTEGER PRIMARY KEY, name TEXT, data TEXT)\")")
        sb.appendLine()
        sb.appendLine("// 大量插入")
        sb.appendLine("for (i in 1..10000) {")
        sb.appendLine("    db.execSQL(\"INSERT INTO users VALUES(?, ?, ?)\",")
        sb.appendLine("        arrayOf(i, \"user_\$i\", \"data\".repeat(100)))")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("// 大量查询")
        sb.appendLine("val cursor = db.query(")
        sb.appendLine("    \"users\", null, null, null, null, null, null)")
        sb.appendLine()

        sb.appendLine("=== 正确写法 ===")
        sb.appendLine()
        sb.appendLine("// ✓ 正确：使用 Room + 协程")
        sb.appendLine("@Dao")
        sb.appendLine("interface UserDao {")
        sb.appendLine("    @Insert")
        sb.appendLine("    suspend fun insertAll(users: List<User>)")
        sb.appendLine()
        sb.appendLine("    @Query(\"SELECT * FROM user\")")
        sb.appendLine("    suspend fun getAll(): List<User>")
        sb.appendLine("}")
        sb.appendLine()
        sb.appendLine("// 在 ViewModel 中使用")
        sb.appendLine("viewModelScope.launch(Dispatchers.IO) {")
        sb.appendLine("    userDao.insertAll(users)")
        sb.appendLine("    val all = userDao.getAll()")
        sb.appendLine("}")
        sb.appendLine()

        // 实际演示：创建表并插入少量数据
        sb.appendLine("=== 实测 ===")
        sb.appendLine()

        val db = openOrCreateDatabase("anr_test_db", MODE_PRIVATE, null)
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY, name TEXT, data TEXT)")

        // 插入 100 条测试
        val insertStart = System.currentTimeMillis()
        for (i in 1..100) {
            db.execSQL(
                "INSERT INTO users VALUES(?, ?, ?)",
                arrayOf<Any>(i, "user_$i", "data".repeat(50))
            )
        }
        val insertTime = System.currentTimeMillis() - insertStart
        sb.appendLine("逐条插入 100 条耗时: ${insertTime}ms")
        sb.appendLine()

        // 批量插入（事务）
        val txnStart = System.currentTimeMillis()
        db.beginTransaction()
        try {
            for (i in 101..200) {
                db.execSQL(
                    "INSERT INTO users VALUES(?, ?, ?)",
                    arrayOf<Any>(i, "user_$i", "data".repeat(50))
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        val txnTime = System.currentTimeMillis() - txnStart
        sb.appendLine("事务批量插入 100 条耗时: ${txnTime}ms")
        sb.appendLine()

        // 查询测试
        val queryStart = System.currentTimeMillis()
        val cursor = db.query("users", null, null, null, null, null, null)
        val count = cursor.count
        cursor.close()
        val queryTime = System.currentTimeMillis() - queryStart
        sb.appendLine("查询全部 $count 条耗时: ${queryTime}ms")
        sb.appendLine()

        db.close()

        sb.appendLine("=== 结论 ===")
        sb.appendLine("逐条插入非常慢，使用事务可大幅提升性能")
        sb.appendLine("但无论多快，都不要在主线程做数据库操作")
        sb.appendLine("推荐使用 Room，内置协程支持")

        binding.tvResult.text = sb.toString()
    }

    /**
     * 文件 I/O 操作演示
     *
     * 在主线程进行文件读写会阻塞主线程。
     * 应该使用协程的 Dispatchers.IO 进行文件操作。
     */
    private fun demonstrateFileIO() {
        sb.clear()
        sb.appendLine("=== 文件 I/O 操作 ANR ===")
        sb.appendLine()

        sb.appendLine("=== 错误写法 ===")
        sb.appendLine()
        sb.appendLine("// ❌ 错误：主线程文件操作")
        sb.appendLine("val file = File(cacheDir, \"test.txt\")")
        sb.appendLine("file.writeText(\"...\") // 可能阻塞")
        sb.appendLine("val content = file.readText() // 可能阻塞")
        sb.appendLine()

        sb.appendLine("=== 正确写法 ===")
        sb.appendLine()
        sb.appendLine("// ✓ 正确：使用协程在 IO 线程")
        sb.appendLine("lifecycleScope.launch {")
        sb.appendLine("    withContext(Dispatchers.IO) {")
        sb.appendLine("        val file = File(cacheDir, \"test.txt\")")
        sb.appendLine("        file.writeText(\"...\")")
        sb.appendLine("        val content = file.readText()")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine()

        // 实际演示：写入和读取文件
        sb.appendLine("=== 实测 ===")
        sb.appendLine()

        val file = File(cacheDir, "anr_test_file.txt")

        // 写入测试
        val writeStart = System.currentTimeMillis()
        val largeContent = buildString {
            for (i in 1..10000) {
                appendLine("这是第 $i 行测试数据，用于模拟大文件写入场景。")
            }
        }
        file.writeText(largeContent)
        val writeTime = System.currentTimeMillis() - writeStart
        sb.appendLine("写入 ${largeContent.length / 1024}KB 文件耗时: ${writeTime}ms")
        sb.appendLine()

        // 读取测试
        val readStart = System.currentTimeMillis()
        val readContent = file.readText()
        val readTime = System.currentTimeMillis() - readStart
        sb.appendLine("读取 ${readContent.length / 1024}KB 文件耗时: ${readTime}ms")
        sb.appendLine()

        sb.appendLine("=== 常见文件 I/O 场景 ===")
        sb.appendLine("• 日志文件写入 → 用协程或 HandlerThread")
        sb.appendLine("• 图片文件读写 → 用 Glide/Coil 等库")
        sb.appendLine("• JSON 配置文件 → 用 DataStore 代替")
        sb.appendLine("• 序列化对象到文件 → 用 Room 代替")
        sb.appendLine()
        sb.appendLine("=== 结论 ===")
        sb.appendLine("文件 I/O 是最容易导致 ANR 的操作之一")
        sb.appendLine("磁盘速度不可控，在主线程做 I/O 随时可能 ANR")
        sb.appendLine("始终使用 Dispatchers.IO 或后台线程进行文件操作")

        binding.tvResult.text = sb.toString()
    }

    /**
     * Binder 通信演示
     *
     * ContentResolver.query() 等跨进程调用是同步的 Binder 操作，
     * 如果查询大量数据，可能阻塞主线程导致 ANR。
     */
    private fun demonstrateBinder() {
        sb.clear()
        sb.appendLine("=== Binder 通信 ANR ===")
        sb.appendLine()

        sb.appendLine("=== 原理 ===")
        sb.appendLine()
        sb.appendLine("Android 中很多系统服务通过 Binder IPC 通信:")
        sb.appendLine("  ContentResolver → ContentProvider (跨进程)")
        sb.appendLine("  PackageManager → PMS (跨进程)")
        sb.appendLine("  ActivityManager → AMS (跨进程)")
        sb.appendLine("  WindowManager → WMS (跨进程)")
        sb.appendLine()
        sb.appendLine("Binder 调用默认是同步的，调用方会阻塞等待返回")
        sb.appendLine("如果对方进程繁忙或数据量大，就会长时间阻塞")
        sb.appendLine()

        sb.appendLine("=== 错误写法 ===")
        sb.appendLine()
        sb.appendLine("// ❌ 错误：主线程查询联系人")
        sb.appendLine("val cursor = contentResolver.query(")
        sb.appendLine("    ContactsContract.Contacts.CONTENT_URI,")
        sb.appendLine("    null, null, null, null")
        sb.appendLine(")")
        sb.appendLine()

        sb.appendLine("=== 正确写法 ===")
        sb.appendLine()
        sb.appendLine("// ✓ 正确：在 IO 线程查询")
        sb.appendLine("lifecycleScope.launch {")
        sb.appendLine("    withContext(Dispatchers.IO) {")
        sb.appendLine("        val cursor = contentResolver.query(")
        sb.appendLine("            ContactsContract.Contacts.CONTENT_URI,")
        sb.appendLine("            null, null, null, null")
        sb.appendLine("        )")
        sb.appendLine("        cursor?.use {")
        sb.appendLine("            // 处理数据")
        sb.appendLine("        }")
        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine()

        sb.appendLine("=== 常见 Binder ANR 场景 ===")
        sb.appendLine()
        sb.appendLine("1. ContentResolver.query() 大量数据")
        sb.appendLine("   → 联系人、短信、通话记录等查询")
        sb.appendLine()
        sb.appendLine("2. PackageManager.getInstalledPackages()")
        sb.appendLine("   → 查询所有已安装应用信息")
        sb.appendLine()
        sb.appendLine("3. AIDL 自定义服务调用")
        sb.appendLine("   → 跨进程方法调用耗时过长")
        sb.appendLine()
        sb.appendLine("4. ActivityManager 相关调用")
        sb.appendLine("   → getRunningAppProcesses() 等")
        sb.appendLine()

        sb.appendLine("=== 注意事项 ===")
        sb.appendLine()
        sb.appendLine("• Binder 有数据大小限制（约 1MB）")
        sb.appendLine("• 传输大量数据应使用 ContentProvider + 分页")
        sb.appendLine("• AIDL 接口应设计为轻量级")
        sb.appendLine("• 所有跨进程调用都应在子线程")

        binding.tvResult.text = sb.toString()
    }

    /**
     * 序列化/反序列化演示
     *
     * 在主线程解析大 JSON 或序列化大数据对象会导致 ANR。
     * 应该在 IO 线程进行这些操作。
     */
    private fun demonstrateSerialization() {
        sb.clear()
        sb.appendLine("=== 序列化/反序列化 ANR ===")
        sb.appendLine()

        sb.appendLine("=== 错误写法 ===")
        sb.appendLine()
        sb.appendLine("// ❌ 错误：主线程解析大 JSON")
        sb.appendLine("val data = Gson().fromJson(largeJsonString, MyData::class.java)")
        sb.appendLine()
        sb.appendLine("// ❌ 错误：主线程序列化传递大数据")
        sb.appendLine("intent.putExtra(\"data\", largeParcelableObject)")
        sb.appendLine("// Parcelable 序列化在主线程，数据大时会阻塞")
        sb.appendLine()

        sb.appendLine("=== 正确写法 ===")
        sb.appendLine()
        sb.appendLine("// ✓ 正确：在 IO 线程解析")
        sb.appendLine("lifecycleScope.launch {")
        sb.appendLine("    val data = withContext(Dispatchers.IO) {")
        sb.appendLine("        Gson().fromJson(json, MyData::class.java)")
        sb.appendLine("    }")
        sb.appendLine("    // 使用 data 更新 UI")
        sb.appendLine("}")
        sb.appendLine()

        // 实际演示：模拟 JSON 解析
        sb.appendLine("=== 实测 ===")
        sb.appendLine()

        // 构建大 JSON 字符串
        val jsonStart = System.currentTimeMillis()
        val largeJson = buildString {
            append("[")
            for (i in 1..5000) {
                if (i > 1) append(",")
                append("{\"id\":$i,\"name\":\"user_$i\",")
                append("\"email\":\"user_$i@example.com\",")
                append("\"data\":\"${"x".repeat(50)}\"}")
            }
            append("]")
        }
        val jsonBuildTime = System.currentTimeMillis() - jsonStart
        sb.appendLine("构建 JSON 字符串(${largeJson.length / 1024}KB)耗时: ${jsonBuildTime}ms")
        sb.appendLine()

        // 手动解析模拟（indexOf 方式）
        val parseStart = System.currentTimeMillis()
        val idCount = largeJson.count { it == '{' }.toLong()
        val parseTime = System.currentTimeMillis() - parseStart
        sb.appendLine("简单统计 JSON 对象数($idCount)耗时: ${parseTime}ms")
        sb.appendLine()

        sb.appendLine("=== 常见序列化 ANR 场景 ===")
        sb.appendLine()
        sb.appendLine("1. JSON 解析")
        sb.appendLine("   • Gson.fromJson() 大字符串")
        sb.appendLine("   • JSONObject 构建大对象树")
        sb.appendLine("   → 解决：Dispatchers.IO + 流式解析")
        sb.appendLine()
        sb.appendLine("2. Parcelable 序列化")
        sb.appendLine("   • Intent 传递大数据 Parcelable")
        sb.appendLine("   • Bundle 序列化大对象")
        sb.appendLine("   → 解决：减小数据量 / 使用 ID 引用")
        sb.appendLine()
        sb.appendLine("3. Serializable")
        sb.appendLine("   • 比 Parcelable 更慢，更易 ANR")
        sb.appendLine("   → 解决：优先使用 Parcelable 或 JSON")
        sb.appendLine()
        sb.appendLine("4. XML 解析")
        sb.appendLine("   • 大 XML 文件的 DOM 解析")
        sb.appendLine("   → 解决：使用 SAX/Pull 流式解析")
        sb.appendLine()

        sb.appendLine("=== 最佳实践 ===")
        sb.appendLine()
        sb.appendLine("• 使用 kotlinx.serialization 代替 Gson（编译期生成）")
        sb.appendLine("• Intent 传递大数据用 Bundle + ID 模式")
        sb.appendLine("• 所有解析操作放在 Dispatchers.IO")
        sb.appendLine("• 考虑使用 Protobuf 替代 JSON（更快更小）")

        binding.tvResult.text = sb.toString()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
