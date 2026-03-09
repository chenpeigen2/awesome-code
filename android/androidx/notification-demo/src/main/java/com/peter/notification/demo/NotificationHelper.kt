package com.peter.notification.demo

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.content.getSystemService
import com.peter.notification.demo.channel.ChannelManager

/**
 * 通知工具类
 */
class NotificationHelper(private val context: Context) {

    private val notificationManager: NotificationManager =
        context.getSystemService() ?: throw IllegalStateException("NotificationManager not available")

    companion object {
        const val GROUP_KEY_CHAT = "group_chat"
        const val GROUP_KEY_EMAIL = "group_email"
        const val GROUP_KEY_SOCIAL = "group_social"
        const val GROUP_KEY_SYSTEM = "group_system"
    }

    // 普通通知
    fun sendBasicNotification() {
        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_BASIC)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("普通通知")
            .setContentText("这是一条普通通知内容")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1001, notification)
    }

    // 大文本通知
    fun sendBigTextNotification() {
        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_BASIC)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("大文本通知")
            .setContentText("展开查看更多内容...")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("这是一条大文本通知示例。当通知展开时，会显示完整的长文本内容。用户可以向下拉动通知栏来查看更多内容。")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1002, notification)
    }

    // 大图片通知
    fun sendBigPictureNotification() {
        val bitmap = createColorBitmap(800, 600, Color.BLUE)
        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_BASIC)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("大图片通知")
            .setContentText("点击展开查看大图")
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1003, notification)
    }

    // 收件箱样式通知
    fun sendInboxNotification() {
        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_BASIC)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("收件箱")
            .setContentText("5封新邮件")
            .setStyle(
                NotificationCompat.InboxStyle()
                    .setBigContentTitle("5封新邮件")
                    .addLine("张三: 明天的会议记得参加")
                    .addLine("李四: 项目文档已更新")
                    .addLine("王五: 周末有空一起吃饭吗？")
                    .addLine("系统: 您的账户安全提醒")
                    .addLine("订阅: 每周技术周刊")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1004, notification)
    }

    // 消息样式通知
    fun sendMessagingNotification() {
        val user = Person.Builder().setName("我").build()
        val sender = Person.Builder().setName("张三").build()

        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_MESSAGE)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setStyle(
                NotificationCompat.MessagingStyle(user)
                    .setConversationTitle("技术讨论群")
                    .addMessage("大家好！", System.currentTimeMillis() - 60000, sender)
                    .addMessage("我在看，内容很精彩", System.currentTimeMillis(), user)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1005, notification)
    }

    // 进度通知
    private var progressValue = 0
    fun sendProgressNotification() {
        progressValue = (progressValue + 10) % 110
        val isComplete = progressValue >= 100

        val builder = NotificationCompat.Builder(context, ChannelManager.CHANNEL_PROGRESS)
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setContentTitle(if (isComplete) "下载完成" else "下载中...")
            .setContentText(if (isComplete) "100%" else "$progressValue%")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(!isComplete)

        if (!isComplete) {
            builder.setProgress(100, progressValue, false)
        }

        notificationManager.notify(1008, builder.build())
    }

    // 持续活动通知
    fun sendOngoingNotification() {
        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_PROGRESS)
            .setSmallIcon(android.R.drawable.ic_menu_myplaces)
            .setContentTitle("正在运动中")
            .setContentText("已跑步 5.2 公里")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .build()
        notificationManager.notify(1009, notification)
    }

    // 前台服务通知
    fun sendForegroundNotification(): Notification {
        return NotificationCompat.Builder(context, ChannelManager.CHANNEL_PROGRESS)
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setContentTitle("前台服务运行中")
            .setContentText("正在执行后台任务")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    // 媒体通知
    fun sendMediaNotification() {
        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_MEDIA)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle("正在播放")
            .setContentText("示例歌曲 - 示例艺术家")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        notificationManager.notify(1011, notification)
    }

    // 自定义布局通知
    fun sendCustomLayoutNotification() {
        val remoteViews = RemoteViews(context.packageName, R.layout.notification_custom)
        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_ADVANCED)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setCustomContentView(remoteViews)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1012, notification)
    }

    // 定时通知
    fun sendScheduledNotification() {
        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_ADVANCED)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("定时通知")
            .setContentText("这是延迟 5 秒后发送的通知")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1013, notification)
    }

    // 操作按钮通知
    fun sendActionNotification() {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_ADVANCED)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("操作通知")
            .setContentText("点击下方按钮进行操作")
            .addAction(android.R.drawable.ic_menu_send, "回复", pendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "删除", pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1014, notification)
    }

    // 分组通知
    fun sendGroupNotification(groupKey: String, notificationId: Int, title: String, content: String) {
        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_BASIC)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setGroup(groupKey)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(notificationId, notification)
        sendGroupSummary(groupKey)
    }

    private fun sendGroupSummary(groupKey: String) {
        val groupName = when (groupKey) {
            GROUP_KEY_CHAT -> "聊天消息"
            GROUP_KEY_EMAIL -> "邮件"
            GROUP_KEY_SOCIAL -> "社交动态"
            GROUP_KEY_SYSTEM -> "系统提醒"
            else -> "通知"
        }
        val summaryNotification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_BASIC)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(groupName)
            .setContentText("多条新消息")
            .setGroup(groupKey)
            .setGroupSummary(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notificationManager.notify(getSummaryId(groupKey), summaryNotification)
    }

    private fun getSummaryId(groupKey: String): Int {
        return when (groupKey) {
            GROUP_KEY_CHAT -> 2001
            GROUP_KEY_EMAIL -> 2002
            GROUP_KEY_SOCIAL -> 2003
            GROUP_KEY_SYSTEM -> 2004
            else -> 2000
        }
    }

    fun clearGroupNotifications(groupKey: String) {
        notificationManager.cancel(getSummaryId(groupKey))
    }

    fun clearAllNotifications() {
        notificationManager.cancelAll()
    }

    fun sendNotificationByType(type: NotificationType) {
        when (type) {
            NotificationType.NORMAL -> sendBasicNotification()
            NotificationType.BIG_TEXT -> sendBigTextNotification()
            NotificationType.BIG_PICTURE -> sendBigPictureNotification()
            NotificationType.INBOX -> sendInboxNotification()
            NotificationType.MESSAGING -> sendMessagingNotification()
            NotificationType.CONVERSATION -> sendMessagingNotification()
            NotificationType.BUBBLE -> sendMessagingNotification()
            NotificationType.PROGRESS -> sendProgressNotification()
            NotificationType.ONGOING -> sendOngoingNotification()
            NotificationType.FOREGROUND -> { /* 需要服务配合 */ }
            NotificationType.MEDIA -> sendMediaNotification()
            NotificationType.CUSTOM -> sendCustomLayoutNotification()
            NotificationType.SCHEDULED -> sendScheduledNotification()
            NotificationType.ACTION -> sendActionNotification()
        }
    }

    private fun createColorBitmap(width: Int, height: Int, color: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(color)
        return bitmap
    }
}