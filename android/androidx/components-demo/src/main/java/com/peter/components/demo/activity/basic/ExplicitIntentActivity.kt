package com.peter.components.demo.activity.basic

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.ParcelableUser
import com.peter.components.demo.R
import com.peter.components.demo.SerializableProduct

/**
 * 显式 Intent 目标 Activity
 *
 * 展示如何接收和处理 Intent 数据
 */
class ExplicitIntentActivity : AppCompatActivity() {

    private lateinit var tvData: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explicit_intent)

        tvData = findViewById(R.id.tvData)
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }

        displayReceivedData()
    }

    private fun displayReceivedData() {
        val sb = StringBuilder()

        // 基本数据
        intent.getStringExtra("message")?.let {
            sb.append("消息: $it\n")
        }

        if (intent.hasExtra("timestamp")) {
            sb.append("时间戳: ${intent.getLongExtra("timestamp", 0)}\n")
        }

        // 基本类型
        if (intent.hasExtra("int_value")) {
            sb.append("\n=== 基本类型 ===\n")
            sb.append("Int: ${intent.getIntExtra("int_value", 0)}\n")
            sb.append("Long: ${intent.getLongExtra("long_value", 0)}\n")
            sb.append("Float: ${intent.getFloatExtra("float_value", 0f)}\n")
            sb.append("Double: ${intent.getDoubleExtra("double_value", 0.0)}\n")
            sb.append("String: ${intent.getStringExtra("string_value")}\n")

            // 数组
            intent.getIntArrayExtra("int_array")?.let {
                sb.append("IntArray: ${it.toList()}\n")
            }
            intent.getStringArrayExtra("string_array")?.let {
                sb.append("StringArray: ${it.toList()}\n")
            }
        }

        // Bundle
        intent.getBundleExtra("bundle_data")?.let { bundle ->
            sb.append("\n=== Bundle 数据 ===\n")
            sb.append("Name: ${bundle.getString("name")}\n")
            sb.append("Age: ${bundle.getInt("age")}\n")
            sb.append("IsStudent: ${bundle.getBoolean("isStudent")}\n")
            sb.append("Score: ${bundle.getDouble("score")}\n")

            bundle.getBundle("address")?.let { address ->
                sb.append("Address: ${address.getString("city")}, ${address.getString("street")}\n")
            }
        }

        // Parcelable
        val user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("parcelable_user", ParcelableUser::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("parcelable_user")
        }
        user?.let {
            sb.append("\n=== Parcelable 对象 ===\n")
            sb.append("ID: ${it.id}\n")
            sb.append("Name: ${it.name}\n")
            sb.append("Email: ${it.email}\n")
            sb.append("Scores: ${it.scores}\n")
        }

        // Serializable
        @Suppress("DEPRECATION")
        val product = intent.getSerializableExtra("serializable_product") as? SerializableProduct
        product?.let {
            sb.append("\n=== Serializable 对象 ===\n")
            sb.append("ID: ${it.id}\n")
            sb.append("Name: ${it.name}\n")
            sb.append("Price: ${it.price}\n")
            sb.append("Description: ${it.description}\n")
        }

        tvData.text = if (sb.isEmpty()) "未接收到数据" else sb.toString()
    }
}
