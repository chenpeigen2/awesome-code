package com.peter.coroutine.demo.advanced

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.peter.coroutine.demo.R
import com.peter.coroutine.demo.data.local.AppDatabase
import com.peter.coroutine.demo.data.local.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Room + 协程演示
 *
 * 本 Activity 展示 Room 数据库与 Kotlin 协程的集成使用。
 *
 * ## Room 对协程的支持
 * Room 提供了对 Kotlin 协程的原生支持，主要体现在：
 *
 * 1. **Suspend 函数**
 *    DAO 中的方法可以声明为 suspend，Room 会自动在后台执行
 *
 * 2. **Flow 查询**
 *    查询方法可以返回 Flow，实现数据的实时监听
 *
 * 3. **事务支持**
 *    使用 @Transaction 注解的方法支持协程事务
 *
 * ## 使用要点
 * - Room 的 suspend 函数自动在 IO 线程执行
 * - 不需要手动使用 withContext(Dispatchers.IO)
 * - Flow 查询在数据变化时自动发射新值
 *
 * ## 示例操作
 * - 插入用户: 使用 suspend 函数
 * - 查询用户: 使用 suspend 函数或 Flow
 * - 删除用户: 使用 suspend 函数
 *
 * @see com.peter.coroutine.demo.data.local.UserDao
 * @see com.peter.coroutine.demo.data.local.AppDatabase
 */
class RoomExampleActivity : AppCompatActivity() {

    private lateinit var binding: androidx.coordinatorlayout.widget.CoordinatorLayout
    private lateinit var tvLog: android.widget.TextView
    private lateinit var etName: com.google.android.material.textfield.TextInputEditText
    private lateinit var etEmail: com.google.android.material.textfield.TextInputEditText

    private val logBuilder = StringBuilder()
    private val userDao by lazy { AppDatabase.getInstance(this).userDao() }
    private var flowJob: Job? = null
    private var isFlowActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_example)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.room_example)
        toolbar.setNavigationOnClickListener { finish() }

        // 初始化视图
        binding = findViewById(R.id.root)
        tvLog = findViewById(R.id.tvLog)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)

        setupButtons()
        showInitialInfo()
    }

    /**
     * 设置按钮点击事件
     */
    private fun setupButtons() {
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnInsert).setOnClickListener {
            insertUser()
        }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnQuery).setOnClickListener {
            queryAllUsers()
        }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnDeleteAll).setOnClickListener {
            deleteAllUsers()
        }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnToggleFlow).setOnClickListener {
            toggleFlowCollection()
        }
    }

    /**
     * 显示初始信息
     */
    private fun showInitialInfo() {
        log("========== Room + 协程演示 ==========\n")
        log("Room 对协程的支持:\n")
        log("1. Suspend 函数: Room 自动在 IO 线程执行")
        log("2. Flow 查询: 数据变化时自动更新")
        log("3. 事务支持: @Transaction 注解\n")
        log("操作说明:")
        log("- 输入用户名和邮箱后点击'插入用户'")
        log("- 点击'查询所有'查看数据库中的用户")
        log("- 点击'开启 Flow 监听'实时监听数据变化")
        log("- 点击'删除所有'清空数据\n")
        log("==========================================\n\n")
    }

    /**
     * 插入用户
     */
    private fun insertUser() {
        val name = etName.text?.toString()?.trim()
        val email = etEmail.text?.toString()?.trim()

        if (name.isNullOrEmpty() || email.isNullOrEmpty()) {
            showSnackbar("请输入用户名和邮箱")
            return
        }

        val user = UserEntity(name = name, email = email)

        lifecycleScope.launch {
            log("--- 插入用户 ---")
            log("开始插入: name=$name, email=$email")

            try {
                // Room 的 suspend 函数会自动在 IO 线程执行
                val id = userDao.insert(user)
                log("插入成功! ID=$id\n")

                // 清空输入
                etName.text?.clear()
                etEmail.text?.clear()

                showSnackbar("用户插入成功")
            } catch (e: Exception) {
                log("插入失败: ${e.message}\n")
                showSnackbar("插入失败: ${e.message}")
            }
        }
    }

    /**
     * 查询所有用户
     */
    private fun queryAllUsers() {
        lifecycleScope.launch {
            log("--- 查询所有用户 ---")

            try {
                // Room 的 suspend 函数会自动在 IO 线程执行
                val users = userDao.getAll()

                if (users.isEmpty()) {
                    log("数据库为空\n")
                } else {
                    log("共 ${users.size} 个用户:")
                    users.forEach { user ->
                        log("  [${user.id}] ${user.name} - ${user.email}")
                    }
                    log("")
                }
            } catch (e: Exception) {
                log("查询失败: ${e.message}\n")
            }
        }
    }

    /**
     * 删除所有用户
     */
    private fun deleteAllUsers() {
        lifecycleScope.launch {
            log("--- 删除所有用户 ---")

            try {
                userDao.deleteAll()
                log("所有用户已删除\n")
                showSnackbar("所有用户已删除")
            } catch (e: Exception) {
                log("删除失败: ${e.message}\n")
                showSnackbar("删除失败: ${e.message}")
            }
        }
    }

    /**
     * 切换 Flow 监听
     */
    private fun toggleFlowCollection() {
        if (isFlowActive) {
            // 停止 Flow 监听
            flowJob?.cancel()
            flowJob = null
            isFlowActive = false
            findViewById<com.google.android.material.button.MaterialButton>(R.id.btnToggleFlow).text = "开启 Flow 监听"
            log("--- Flow 监听已停止 ---\n")
        } else {
            // 开始 Flow 监听
            flowJob = lifecycleScope.launch {
                log("--- 开启 Flow 监听 ---")
                log("数据变化时会自动更新...\n")

                userDao.getAllFlow()
                    .catch { e ->
                        log("Flow 错误: ${e.message}\n")
                    }
                    .collectLatest { users ->
                        log("[Flow 更新] 用户数: ${users.size}")
                        if (users.isNotEmpty()) {
                            log("  最新用户: ${users.first().name}")
                        }
                        log("")
                    }
            }
            isFlowActive = true
            findViewById<com.google.android.material.button.MaterialButton>(R.id.btnToggleFlow).text = "停止 Flow 监听"
        }
    }

    /**
     * 添加日志
     */
    private fun log(message: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            logBuilder.append(message).append("\n")
            tvLog.text = logBuilder.toString()
        }
    }

    /**
     * 显示 Snackbar
     */
    private fun showSnackbar(message: String) {
        Snackbar.make(binding, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 确保 Flow 监听被取消
        flowJob?.cancel()
    }
}
