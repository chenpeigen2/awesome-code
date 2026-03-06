package com.peter.components.demo.activity.launchmode

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.R

/**
 * Activity 启动模式示例 - Standard
 *
 * ═══════════════════════════════════════════════════════════════
 * 四种启动模式详解
 * ═══════════════════════════════════════════════════════════════
 *
 * 1. standard（默认模式）
 *    - 每次启动都创建新实例
 *    - 谁启动就放在谁的栈中
 *    - 示例：A → B → B → B（三个 B 实例）
 *
 * 2. singleTop（栈顶复用）
 *    - 如果目标已在栈顶，则复用（调用 onNewIntent）
 *    - 不在栈顶则创建新实例
 *    - 示例：A → B → B（第二个 B 复用栈顶）
 *    - 示例：A → B → A → B（两个 B 实例）
 *
 * 3. singleTask（栈内复用）
 *    - 如果栈中已存在，则复用并清除其上所有 Activity
 *    - 具有 taskAffinity 指定的独立任务栈
 *    - 示例：A → B → C → B（清除 C，复用 B）
 *
 * 4. singleInstance（单实例）
 *    - 整个系统中只有一个实例
 *    - 独占一个任务栈
 *    - 其他 Activity 启动时会创建新栈
 *
 * ═══════════════════════════════════════════════════════════════
 * Intent Flags
 * ═══════════════════════════════════════════════════════════════
 *
 * 常用 Flags：
 * - FLAG_ACTIVITY_NEW_TASK：在新任务栈中启动
 * - FLAG_ACTIVITY_CLEAR_TOP：清除目标之上的 Activity
 * - FLAG_ACTIVITY_SINGLE_TOP：同 singleTop
 * - FLAG_ACTIVITY_CLEAR_TASK：启动时清除任务栈
 * - FLAG_ACTIVITY_REORDER_TO_FRONT：已存在则移到前台
 */
open class BaseLaunchModeActivity : AppCompatActivity() {

    protected lateinit var tvMode: TextView
    protected lateinit var tvTaskInfo: TextView

    protected open val modeName: String = "Base"
    protected open val modeDescription: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launchmode)

        tvMode = findViewById(R.id.tvMode)
        tvTaskInfo = findViewById(R.id.tvTaskInfo)

        tvMode.text = modeName
        updateTaskInfo()

        setupButtons()
    }

    protected open fun setupButtons() {
        findViewById<Button>(R.id.btnStandard).setOnClickListener {
            startActivity(Intent(this, StandardActivity::class.java))
        }

        findViewById<Button>(R.id.btnSingleTop).setOnClickListener {
            startActivity(Intent(this, SingleTopActivity::class.java))
        }

        findViewById<Button>(R.id.btnSingleTask).setOnClickListener {
            startActivity(Intent(this, SingleTaskActivity::class.java))
        }

        findViewById<Button>(R.id.btnSingleInstance).setOnClickListener {
            startActivity(Intent(this, SingleInstanceActivity::class.java))
        }
    }

    protected fun updateTaskInfo() {
        val taskId = taskId

        val info = StringBuilder()
        info.append("Task ID: $taskId\n")
        info.append("Class: ${this::class.java.simpleName}\n")
        info.append("HashCode: ${hashCode()}\n\n")
        info.append(modeDescription)

        tvTaskInfo.text = info.toString()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        updateTaskInfo()
        // 复用时会调用此方法
        tvTaskInfo.append("\n\n✅ onNewIntent 被调用（复用实例）")
    }
}

/**
 * Standard 模式
 *
 * 默认启动模式，每次启动都创建新实例
 */
class StandardActivity : BaseLaunchModeActivity() {
    override val modeName = "Standard 模式"
    override val modeDescription = """
每次启动都创建新实例
放入启动者的任务栈

示例：A → B → B → B
会产生三个 B 实例

适用场景：
- 普通页面跳转
- 需要保留历史记录
    """.trimIndent()
}

/**
 * SingleTop 模式
 *
 * 栈顶复用模式
 * 如果目标 Activity 已在栈顶，则复用
 */
class SingleTopActivity : BaseLaunchModeActivity() {
    override val modeName = "SingleTop 模式"
    override val modeDescription = """
栈顶复用模式
如果已在栈顶则复用（onNewIntent）
不在栈顶则新建

示例：B(栈顶) → B
复用栈顶 B，调用 onNewIntent

示例：A → B → A → B
第二个 B 是新实例

适用场景：
- 通知打开详情页
- 避免重复点击
    """.trimIndent()
}

/**
 * SingleTask 模式
 *
 * 栈内复用模式
 * 如果栈中已存在，则复用并清除其上所有 Activity
 */
class SingleTaskActivity : BaseLaunchModeActivity() {
    override val modeName = "SingleTask 模式"
    override val modeDescription = """
栈内复用模式
全局单实例（在指定 taskAffinity 的栈中）
如果存在则复用，清除其上所有 Activity

示例：A → B → C → B
清除 C，复用 B，栈变为 A → B

适用场景：
- 主页/首页
- 登录页
- 需要清栈的场景
    """.trimIndent()
}

/**
 * SingleInstance 模式
 *
 * 全局单实例模式
 * 独占一个任务栈
 */
class SingleInstanceActivity : BaseLaunchModeActivity() {
    override val modeName = "SingleInstance 模式"
    override val modeDescription = """
全局单实例模式
独占一个任务栈
整个系统只有一个实例

特点：
- 自己的任务栈
- 不能启动其他 Activity（会在新栈中）
- 被其他 Activity 启动时会显示在原栈上方

适用场景：
- 拨号器
- 来电界面
- 全局唯一页面
    """.trimIndent()
}
