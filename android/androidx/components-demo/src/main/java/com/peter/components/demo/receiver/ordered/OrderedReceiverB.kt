package com.peter.components.demo.receiver.ordered

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * 有序广播接收者 B（低优先级）
 *
 * 优先级：50（低于 ReceiverA）
 * 会接收到 ReceiverA 修改后的数据
 */
class OrderedReceiverB : BroadcastReceiver() {

    companion object {
        private const val TAG = "OrderedReceiverB"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: ${intent?.action}")

        // 获取原始数据
        val originalData = intent?.getStringExtra("original_data")
        Log.d(TAG, "原始数据: $originalData")

        // 获取 ReceiverA 传递的数据
        val resultCode = resultCode
        val resultData = resultData
        val extras = getResultExtras(false)
        val modifiedBy = extras?.getString("modified_by")

        Log.d(TAG, "收到修改后的数据:")
        Log.d(TAG, "  resultCode: $resultCode")
        Log.d(TAG, "  resultData: $resultData")
        Log.d(TAG, "  modified_by: $modifiedBy")

        // 继续传递
        setResultData("被 B 再次修改的数据")
    }
}
