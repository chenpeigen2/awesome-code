package com.peter.components.demo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.R

/**
 * 有序广播示例
 *
 * ═══════════════════════════════════════════════════════════════
 * 有序广播 (Ordered Broadcast)
 * ═══════════════════════════════════════════════════════════════
 *
 * 特点：
 * 1. 按优先级顺序传递给接收者
 * 2. 高优先级的接收者可以拦截广播
 * 3. 可以修改广播数据传递给下一个接收者
 * 4. 可以获取最终处理结果
 *
 * 发送方式：
 * sendOrderedBroadcast(intent, permission, resultReceiver, scheduler,
 *                      initialCode, initialData, initialExtras)
 *
 * 参数说明：
 * - permission：接收者需要的权限
 * - resultReceiver：最终接收者（无论是否被拦截）
 * - scheduler：Handler 用于回调
 * - initialCode/initialData/initialExtras：初始数据
 *
 * ═══════════════════════════════════════════════════════════════
 * BroadcastReceiver API
 * ═══════════════════════════════════════════════════════════════
 *
 * 在 onReceive 中可以调用：
 *
 * 1. getResultCode() / setResultCode(code)
 *    获取/设置结果码
 *
 * 2. getResultData() / setResultData(data)
 *    获取/设置结果数据
 *
 * 3. getResultExtras(make) / setResultExtras(extras)
 *    获取/设置额外数据
 *
 * 4. abortBroadcast()
 *    拦截广播，阻止传递给下一个接收者
 *
 * 5. isOrderedBroadcast()
 *    判断是否是有序广播
 */
class OrderedBroadcastActivity : AppCompatActivity() {

    companion object {
        const val ACTION_ORDERED_DEMO = "com.peter.components.ORDERED_DEMO"
    }

    private lateinit var tvResult: TextView

    /**
     * 最终结果接收者
     *
     * 无论广播是否被拦截，最终接收者都会收到
     */
    private val resultReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val resultCode = resultCode
            val resultData = resultData
            val extras = getResultExtras(false)

            val sb = StringBuilder()
            sb.append("最终接收者回调:\n")
            sb.append("ResultCode: $resultCode\n")
            sb.append("ResultData: $resultData\n")
            sb.append("Extras: ${extras?.getString("modified_by")}\n")

            tvResult.text = sb.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ordered_broadcast)

        tvResult = findViewById(R.id.tvResult)

        findViewById<Button>(R.id.btnSendOrdered).setOnClickListener {
            sendOrderedBroadcastSimple()
        }

        findViewById<Button>(R.id.btnSendWithResult).setOnClickListener {
            sendOrderedBroadcastWithResult()
        }
    }

    /**
     * 发送简单的有序广播
     */
    private fun sendOrderedBroadcastSimple() {
        val intent = Intent(ACTION_ORDERED_DEMO).apply {
            putExtra("original_data", "原始数据")
        }
        sendOrderedBroadcast(intent, null)
        tvResult.text = "已发送有序广播\n查看 Logcat 日志"
    }

    /**
     * 发送带最终接收者的有序广播
     */
    private fun sendOrderedBroadcastWithResult() {
        val intent = Intent(ACTION_ORDERED_DEMO).apply {
            putExtra("original_data", "原始数据")
        }

        // 发送有序广播并指定最终接收者
        sendOrderedBroadcast(
            intent,
            null,           // permission
            resultReceiver, // 最终接收者
            null,           // scheduler (Handler)
            RESULT_OK,      // initialCode
            "初始数据",      // initialData
            null            // initialExtras
        )
    }
}
