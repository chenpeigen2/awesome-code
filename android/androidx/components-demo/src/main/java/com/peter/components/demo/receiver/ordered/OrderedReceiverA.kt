package com.peter.components.demo.receiver.ordered

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

/**
 * 有序广播接收者 A（高优先级）
 *
 * 优先级在 Manifest 中设置：
 * <intent-filter android:priority="100">
 *
 * 优先级范围：-1000 到 1000
 * 数值越大优先级越高
 */
class OrderedReceiverA : BroadcastReceiver() {

    companion object {
        private const val TAG = "OrderedReceiverA"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: ${intent?.action}")

        // 获取原始数据
        val originalData = intent?.getStringExtra("original_data")
        Log.d(TAG, "原始数据: $originalData")

        // 获取前一个接收者传递的数据
        val resultCode = resultCode
        val resultData = resultData
        Log.d(TAG, "收到: code=$resultCode, data=$resultData")

        // 修改数据传递给下一个接收者
        setResultCode(Activity.RESULT_OK)
        setResultData("被 A 修改的数据")

        // 添加额外数据
        val extras = Bundle().apply {
            putString("modified_by", "ReceiverA")
        }
        setResultExtras(extras)

        // 可以调用 abortBroadcast() 拦截广播
        // abortBroadcast()
        // Log.d(TAG, "广播已被拦截")

        Log.d(TAG, "已修改数据并传递")
    }
}
