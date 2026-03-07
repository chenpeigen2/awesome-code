package com.peter.components.demo.activity.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.databinding.ActivityExplicitIntentBinding

/**
 * 显式 Intent 示例
 * 
 * 知识点：
 * 1. Intent(Context, Class) - 明确指定目标组件
 * 2. putExtra() - 传递基本类型数据
 * 3. putExtras(Bundle) - 传递 Bundle 数据
 * 4. Serializable/Parcelable - 传递复杂对象
 */
class ExplicitIntentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExplicitIntentBinding

    companion object {
        const val EXTRA_DATA = "extra_data"
        const val EXTRA_USER = "extra_user"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExplicitIntentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 接收传递过来的数据
        val receivedData = intent.getStringExtra(EXTRA_DATA)
        if (receivedData != null) {
            binding.tvReceived.text = "接收到的数据: $receivedData"
        }

        binding.btnSend.setOnClickListener {
            val input = binding.etData.text.toString()
            if (input.isNotEmpty()) {
                // 方式1：直接 putExtra
                val intent = android.content.Intent(this, ExplicitIntentActivity::class.java)
                intent.putExtra(EXTRA_DATA, input)
                startActivity(intent)
            }
        }
    }
}
