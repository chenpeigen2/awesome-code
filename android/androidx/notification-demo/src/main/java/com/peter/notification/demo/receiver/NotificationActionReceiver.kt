package com.peter.notification.demo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput

/**
 * 通知Action按钮点击事件接收器
 */
class NotificationActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_REPLY = "com.peter.notification.demo.ACTION_REPLY"
        const val ACTION_LIKE = "com.peter.notification.demo.ACTION_LIKE"
        const val ACTION_SHARE = "com.peter.notification.demo.ACTION_SHARE"
        const val ACTION_DELETE = "com.peter.notification.demo.ACTION_DELETE"
        const val ACTION_CONFIRM = "com.peter.notification.demo.ACTION_CONFIRM"
        const val ACTION_CANCEL = "com.peter.notification.demo.ACTION_CANCEL"
        const val ACTION_DOWNLOAD = "com.peter.notification.demo.ACTION_DOWNLOAD"
        const val ACTION_PAUSE = "com.peter.notification.demo.ACTION_PAUSE"
        const val ACTION_PLAY = "com.peter.notification.demo.ACTION_PLAY"
        const val ACTION_PREV = "com.peter.notification.demo.ACTION_PREV"
        const val ACTION_NEXT = "com.peter.notification.demo.ACTION_NEXT"

        const val EXTRA_NOTIFICATION_ID = "notification_id"
        const val KEY_TEXT_REPLY = "key_text_reply"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)

        when (intent.action) {
            ACTION_REPLY -> {
                // 获取回复文本
                val remoteInput = RemoteInput.getResultsFromIntent(intent)
                val replyText = remoteInput?.getCharSequence(KEY_TEXT_REPLY)?.toString() ?: ""

                Toast.makeText(context, "已回复: $replyText", Toast.LENGTH_SHORT).show()

                // 更新通知为已回复状态
                updateNotificationToReplied(context, notificationId, replyText)
            }
            ACTION_LIKE -> {
                Toast.makeText(context, "已点赞", Toast.LENGTH_SHORT).show()
                NotificationManagerCompat.from(context).cancel(notificationId)
            }
            ACTION_SHARE -> {
                Toast.makeText(context, "已分享", Toast.LENGTH_SHORT).show()
            }
            ACTION_DELETE -> {
                Toast.makeText(context, "已删除", Toast.LENGTH_SHORT).show()
                NotificationManagerCompat.from(context).cancel(notificationId)
            }
            ACTION_CONFIRM -> {
                Toast.makeText(context, "已确认", Toast.LENGTH_SHORT).show()
                NotificationManagerCompat.from(context).cancel(notificationId)
            }
            ACTION_CANCEL -> {
                Toast.makeText(context, "已取消", Toast.LENGTH_SHORT).show()
                NotificationManagerCompat.from(context).cancel(notificationId)
            }
            ACTION_DOWNLOAD -> {
                Toast.makeText(context, "开始下载", Toast.LENGTH_SHORT).show()
            }
            ACTION_PAUSE -> {
                Toast.makeText(context, "已暂停", Toast.LENGTH_SHORT).show()
            }
            ACTION_PLAY -> {
                Toast.makeText(context, "继续播放", Toast.LENGTH_SHORT).show()
            }
            ACTION_PREV -> {
                Toast.makeText(context, "上一首", Toast.LENGTH_SHORT).show()
            }
            ACTION_NEXT -> {
                Toast.makeText(context, "下一首", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateNotificationToReplied(context: Context, notificationId: Int, replyText: String) {
        // 这里可以更新通知显示已回复状态
        // 为简化示例，直接取消通知
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
}