package com.peter.components.demo.receiver.ordered

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * 有序广播接收器 A（优先级 100）
 * 
 * 在 AndroidManifest.xml 中配置：
 * <intent-filter android:priority="100">
 */
class OrderedReceiverA : BroadcastReceiver() {

    companion object {
        private const val TAG = "OrderedReceiverA"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: 最高优先级接收")
        
        // 检查是否需要中断
        if (intent?.getBooleanExtra("abort", false) == true) {
            // 中断广播传播
            abortBroadcast()
            Toast.makeText(context, "ReceiverA 中断了广播", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "广播已被中断")
        } else {
            Toast.makeText(context, "ReceiverA 收到广播", Toast.LENGTH_SHORT).show()
            
            // 可以添加数据传递给下一个接收者
            val extras = getResultExtras(true)
            extras.putString("from_A", "来自 A 的数据")
        }
    }
}
