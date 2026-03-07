package com.peter.components.demo.receiver.ordered

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * 有序广播接收器 B（优先级 50）
 * 
 * 在 AndroidManifest.xml 中配置：
 * <intent-filter android:priority="50">
 */
class OrderedReceiverB : BroadcastReceiver() {

    companion object {
        private const val TAG = "OrderedReceiverB"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: 较低优先级接收")
        
        // 获取上一个接收者传递的数据
        val extras = getResultExtras(false)
        val fromA = extras?.getString("from_A") ?: "无数据"
        
        Toast.makeText(context, "ReceiverB 收到广播\n来自A: $fromA", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "收到来自 A 的数据: $fromA")
    }
}