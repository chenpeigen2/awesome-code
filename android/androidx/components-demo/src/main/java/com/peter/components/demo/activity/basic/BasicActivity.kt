package com.peter.components.demo.activity.basic

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.ParcelableUser
import com.peter.components.demo.R
import com.peter.components.demo.SerializableProduct

/**
 * Activity 基础示例
 *
 * 演示内容：
 * 1. 显式 Intent 启动
 * 2. 隐式 Intent 启动
 * 3. Intent 数据传递（基本类型、Bundle、Parcelable、Serializable）
 *
 * ═══════════════════════════════════════════════════════════════
 * 核心概念：Intent
 * ═══════════════════════════════════════════════════════════════
 *
 * Intent 是 Android 组件间通信的信使，包含：
 *
 * 1. Component（组件）- 显式指定目标组件类名
 * 2. Action（动作）- 描述要执行的操作
 * 3. Data（数据）- 操作的数据 URI
 * 4. Category（类别）- 组件的附加信息
 * 5. Extras（附加数据）- 键值对形式的附加信息
 * 6. Flags（标志）- 控制Activity启动行为
 */
class BasicActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)

        setupButtons()
    }

    private fun setupButtons() {
        // 1. 显式 Intent 启动
        findViewById<Button>(R.id.btnExplicit).setOnClickListener {
            val intent = Intent(this, ExplicitIntentActivity::class.java).apply {
                putExtra("message", "来自 BasicActivity 的问候")
                putExtra("timestamp", System.currentTimeMillis())
            }
            startActivity(intent)
        }

        // 2. 隐式 Intent 启动
        findViewById<Button>(R.id.btnImplicit).setOnClickListener {
            val intent = Intent(this, ImplicitIntentActivity::class.java).apply {
                action = "com.peter.components.demo.IMPLICIT_ACTION"
                addCategory(Intent.CATEGORY_DEFAULT)
            }
            startActivity(intent)
        }

        // 3. 传递基本类型
        findViewById<Button>(R.id.btnPassBasic).setOnClickListener {
            val intent = Intent(this, ExplicitIntentActivity::class.java).apply {
                putExtra("boolean_value", true)
                putExtra("int_value", 42)
                putExtra("long_value", 123456789L)
                putExtra("float_value", 3.14f)
                putExtra("double_value", 2.71828)
                putExtra("string_value", "Hello Parcelable")
                putExtra("int_array", intArrayOf(1, 2, 3, 4, 5))
                putExtra("string_array", arrayOf("A", "B", "C"))
            }
            startActivity(intent)
        }

        // 4. 传递 Bundle
        findViewById<Button>(R.id.btnPassBundle).setOnClickListener {
            val bundle = Bundle().apply {
                putString("name", "张三")
                putInt("age", 25)
                putBoolean("isStudent", false)
                putDouble("score", 95.5)

                val addressBundle = Bundle().apply {
                    putString("city", "北京")
                    putString("street", "朝阳路")
                }
                putBundle("address", addressBundle)
            }

            val intent = Intent(this, ExplicitIntentActivity::class.java).apply {
                putExtra("bundle_data", bundle)
            }
            startActivity(intent)
        }

        // 5. 传递 Parcelable
        findViewById<Button>(R.id.btnPassParcelable).setOnClickListener {
            val user = ParcelableUser(
                id = 1001,
                name = "李四",
                email = "lisi@example.com",
                scores = listOf(90, 85, 95, 88)
            )

            val intent = Intent(this, ExplicitIntentActivity::class.java).apply {
                putExtra("parcelable_user", user)
            }
            startActivity(intent)
        }

        // 6. 传递 Serializable
        findViewById<Button>(R.id.btnPassSerializable).setOnClickListener {
            val product = SerializableProduct(
                id = "P001",
                name = "Android 手机",
                price = 3999.0,
                description = "高性能旗舰手机"
            )

            val intent = Intent(this, ExplicitIntentActivity::class.java).apply {
                putExtra("serializable_product", product)
            }
            startActivity(intent)
        }
    }
}
