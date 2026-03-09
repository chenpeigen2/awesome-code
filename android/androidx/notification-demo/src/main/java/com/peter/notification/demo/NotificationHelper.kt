package com.peter.notification.demo

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
import androidx.core.graphics.drawable.IconCompat
import com.peter.notification.demo.channel.ChannelManager

/**
 * 通知工具类
 */
class NotificationHelper(private val context: Context) {

    private val notificationManager: NotificationManager = 
        context.getSystemService() ?: throw IllegalStateException("NotificationManager not available")

    /**
     * 发送普通通知
     */
    fun sendBasicNotification(
        channelId: String = ChannelManager.CHANNEL_BASIC,
        title: String = "普通通知",
        content: String = "这是一条普通通知内容",
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    /**
     * 发送大文本通知
     */
    fun sendBigTextNotification(
        channelId: String = ChannelManager.CHANNEL_BASIC,
        title: String = "大文本通知",
        bigText: String = """
            这是一条大文本通知示例。
            当通知展开时，会显示完整的长文本内容。
            用户可以向下拉动通知栏来查看更多内容。
            这对于显示较长的消息非常有用。
        """.trimIndent(),
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText("展开查看更多内容...")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(bigText)
            )
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    /**
     * 发送大图片通知
     */
    fun sendBigPictureNotification(
        channelId: String = ChannelManager.CHANNEL_BASIC,
        title: String = "大图片通知",
        content: String = "展开查看大图",
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        val bitmap = createColorBitmap(800, 600, Color.BLUE)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
                    .bigLargeIcon(null)
            )
            .setLargeIcon(bitmap)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    /**
     * 发送收件箱样式通知
     */
    fun sendInboxStyleNotification(
        channelId: String = ChannelManager.CHANNEL_BASIC,
        title: String = "收件箱样式",
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText("您有 5 条新消息")
            .setStyle(
                NotificationCompat.InboxStyle()
                    .setBigContentTitle("新消息")
                    .addLine("张三: 你好吗？")
                    .addLine("李四: 明天一起吃饭")
                    .addLine("王五: 项目进展如何")
                    .addLine("赵六: 周末有活动")
                    .addLine("孙七: 收到请回复")
                    .setSummaryText("5 条未读")
            )
            .setNumber(5)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    /**
     * 发送消息样式通知
     */
    fun sendMessagingStyleNotification(
        channelId: String = ChannelManager.CHANNEL_MESSAGE,
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        val user = Person.Builder()
            .setName("我")
            .build()

        val sender1 = Person.Builder()
            .setName("张三")
            .setIcon(IconCompat.createWithResource(context, android.R.drawable.ic_menu_myplaces))
            .build()

        val sender2 = Person.Builder()
            .setName("李四")
            .setIcon(IconCompat.createWithResource(context, android.R.drawable.ic_menu_myplaces))
            .build()

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("群聊")
            .setStyle(
                NotificationCompat.MessagingStyle(user)
                    .setConversationTitle("项目讨论群")
                    .addMessage("大家好，项目进展如何？", System.currentTimeMillis() - 60000, sender1)
                    .addMessage("正在按计划推进", System.currentTimeMillis() - 50000, user)
                    .addMessage("太好了，预计什么时候完成？", System.currentTimeMillis() - 40000, sender2)
                    .addMessage("下周应该可以完成第一版", System.currentTimeMillis() - 30000, user)
            )
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    /**
     * 发送进度条通知
     */
    fun sendProgressNotification(
        channelId: String,
        title: String,
        progress: Int,
        max: Int = 100,
        indeterminate: Boolean = false,
        notificationId: Int = 1001
    ) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setContentTitle(title)
            .setContentText(if (indeterminate) "处理中..." else "$progress%")
            .setProgress(max, progress, indeterminate)
            .setOngoing(progress < max)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    /**
     * 发送媒体播放通知
     */
    fun sendMediaNotification(
        channelId: String = ChannelManager.CHANNEL_MEDIA,
        title: String = "正在播放",
        artist: String = "未知艺术家",
        notificationId: Int = 2001
    ) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(title)
            .setContentText(artist)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    /**
     * 发送带操作按钮的通知
     */
    fun sendActionNotification(
        channelId: String = ChannelManager.CHANNEL_BASIC,
        title: String = "操作通知",
        content: String = "点击按钮执行操作",
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_menu_send,
                "回复",
                pendingIntent
            )
            .addAction(
                android.R.drawable.ic_menu_delete,
                "删除",
                pendingIntent
            )
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    /**
     * 发送分组通知
     */
    fun sendGroupNotification(
        channelId: String,
        groupKey: String,
        title: String,
        content: String,
        notificationId: Int,
        isSummary: Boolean = false
    ) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setGroup(groupKey)
            .setAutoCancel(true)

        if (isSummary) {
            builder.setGroupSummary(true)
                .setStyle(
                    NotificationCompat.InboxStyle()
                        .setBigContentTitle(title)
                        .setSummaryText(content)
                )
        }

        notificationManager.notify(notificationId, builder.build())
    }

    /**
     * 发送自定义布局通知
     */
    fun sendCustomLayoutNotification(
        channelId: String = ChannelManager.CHANNEL_ADVANCED,
        title: String = "自定义布局",
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        val remoteViews = RemoteViews(context.packageName, R.layout.notification_custom).apply {
            setTextViewText(R.id.tv_title, title)
            setTextViewText(R.id.tv_content, "这是一个自定义布局通知")
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setCustomContentView(remoteViews)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    /**
     * 取消通知
     */
    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    /**
     * 取消所有通知
     */
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }

    /**
     * 创建纯色位图
     */
    private fun createColorBitmap(width: Int, height: Int, color: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(color)
        return bitmap
    }

    companion object {
        const val TAG = "NotificationHelper"
    }
}
