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
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.getSystemService
import com.peter.notification.demo.channel.ChannelManager
import com.peter.notification.demo.receiver.NotificationActionReceiver

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

        // 通知ID范围
        private const val ID_BASIC_START = 1001
        private const val ID_MESSAGE_START = 2001
        private const val ID_PROGRESS_START = 3001
        private const val ID_MEDIA_START = 4001
        private const val ID_ADVANCED_START = 5001
    }

    /**
     * 创建点击通知跳转到MainActivity的PendingIntent
     */
    private fun createContentPendingIntent(notificationId: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notification_id", notificationId)
        }
        return PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * 创建Action按钮的PendingIntent
     */
    private fun createActionPendingIntent(action: String, notificationId: Int): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            this.action = action
            putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        return PendingIntent.getBroadcast(
            context,
            notificationId + action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    // ==================== 基础通知 ====================

    // 普通通知
    fun sendBasicNotification() {
        val notificationId = ID_BASIC_START
        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_BASIC)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("普通通知")
            .setContentText("这是一条普通通知内容")
            .setContentIntent(createContentPendingIntent(notificationId))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    // 大文本通知
    fun sendBigTextNotification() {
        val notificationId = ID_BASIC_START + 1
        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_BASIC)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("大文本通知")
            .setContentText("展开查看更多内容...")
            .setContentIntent(createContentPendingIntent(notificationId))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("这是一条大文本通知示例。当通知展开时，会显示完整的长文本内容。用户可以向下拉动通知栏来查看更多内容。这段文字会很长很长，用于演示大文本通知的展开效果。")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    // 大图片通知
    fun sendBigPictureNotification() {
        val notificationId = ID_BASIC_START + 2
        val bitmap = createColorBitmap(800, 600, Color.BLUE)
        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_BASIC)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("大图片通知")
            .setContentText("点击展开查看大图")
            .setContentIntent(createContentPendingIntent(notificationId))
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    // 收件箱样式通知
    fun sendInboxNotification() {
        val notificationId = ID_BASIC_START + 3
        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_BASIC)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("收件箱")
            .setContentText("5封新邮件")
            .setContentIntent(createContentPendingIntent(notificationId))
            .setStyle(
                NotificationCompat.InboxStyle()
                    .setBigContentTitle("5封新邮件")
                    .addLine("张三: 明天的会议记得参加")
                    .addLine("李四: 项目文档已更新")
                    .addLine("王五: 周末有空一起吃饭吗？")
                    .addLine("系统: 您的账户安全提醒")
                    .addLine("订阅: 每周技术周刊")
            )
            .addAction(
                android.R.drawable.ic_menu_view,
                "查看全部",
                createContentPendingIntent(notificationId)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    // ==================== 消息通知 ====================

    // 消息样式通知
    fun sendMessagingNotification() {
        val notificationId = ID_MESSAGE_START
        val user = Person.Builder().setName("我").build()
        val sender = Person.Builder().setName("张三").build()

        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_MESSAGE)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("张三")
            .setContentText("大家好！")
            .setContentIntent(createContentPendingIntent(notificationId))
            .setStyle(
                NotificationCompat.MessagingStyle(user)
                    .setConversationTitle("技术讨论群")
                    .addMessage("大家好！", System.currentTimeMillis() - 60000, sender)
                    .addMessage("我在看，内容很精彩", System.currentTimeMillis(), user)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    // 带直接回复的消息通知
    fun sendReplyNotification() {
        val notificationId = ID_MESSAGE_START + 1

        // 创建RemoteInput用于直接回复
        val remoteInput = RemoteInput.Builder(NotificationActionReceiver.KEY_TEXT_REPLY)
            .setLabel("输入回复...")
            .build()

        // 创建回复Action
        val replyIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_REPLY
            putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        // 注意：带有 RemoteInput 的 PendingIntent 必须是 MUTABLE 的
        // Android 12+ (API 31+) 要求明确指定 MUTABLE
        val replyPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            replyIntent,
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )

        val replyAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_menu_send,
            "回复",
            replyPendingIntent
        ).addRemoteInput(remoteInput).build()

        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_MESSAGE)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("李四")
            .setContentText("这个方案怎么样？")
            .setContentIntent(createContentPendingIntent(notificationId))
            .addAction(replyAction)
            .addAction(
                android.R.drawable.ic_menu_agenda,
                "标记已读",
                createActionPendingIntent(NotificationActionReceiver.ACTION_CONFIRM, notificationId)
            )
            .addAction(
                android.R.drawable.ic_menu_delete,
                "忽略",
                createActionPendingIntent(NotificationActionReceiver.ACTION_DELETE, notificationId)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    // 社交互动通知（点赞、分享）
    fun sendSocialNotification() {
        val notificationId = ID_MESSAGE_START + 2

        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_MESSAGE)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("新动态")
            .setContentText("王五赞了你的帖子：《Android通知最佳实践》")
            .setContentIntent(createContentPendingIntent(notificationId))
            .addAction(
                android.R.drawable.ic_menu_agenda,
                "点赞",
                createActionPendingIntent(NotificationActionReceiver.ACTION_LIKE, notificationId)
            )
            .addAction(
                android.R.drawable.ic_menu_share,
                "分享",
                createActionPendingIntent(NotificationActionReceiver.ACTION_SHARE, notificationId)
            )
            .addAction(
                android.R.drawable.ic_menu_view,
                "查看",
                createContentPendingIntent(notificationId)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    // ==================== 进度与状态 ====================

    private var progressValue = 0
    fun sendProgressNotification() {
        val notificationId = ID_PROGRESS_START
        progressValue = (progressValue + 10) % 110
        val isComplete = progressValue >= 100

        val builder = NotificationCompat.Builder(context, ChannelManager.CHANNEL_PROGRESS)
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setContentTitle(if (isComplete) "下载完成" else "下载中...")
            .setContentText(if (isComplete) "100%" else "$progressValue%")
            .setContentIntent(createContentPendingIntent(notificationId))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(!isComplete)

        if (!isComplete) {
            builder.setProgress(100, progressValue, false)
            builder.addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "取消",
                createActionPendingIntent(NotificationActionReceiver.ACTION_CANCEL, notificationId)
            )
        } else {
            builder.addAction(
                android.R.drawable.ic_menu_view,
                "打开",
                createContentPendingIntent(notificationId)
            )
        }

        notificationManager.notify(notificationId, builder.build())
    }

    // 持续活动通知
    fun sendOngoingNotification() {
        val notificationId = ID_PROGRESS_START + 1
        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_PROGRESS)
            .setSmallIcon(android.R.drawable.ic_menu_myplaces)
            .setContentTitle("正在运动中")
            .setContentText("已跑步 5.2 公里")
            .setContentIntent(createContentPendingIntent(notificationId))
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "结束",
                createActionPendingIntent(NotificationActionReceiver.ACTION_CANCEL, notificationId)
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    // 前台服务通知
    fun sendForegroundNotification(): Notification {
        val notificationId = ID_PROGRESS_START + 2
        return NotificationCompat.Builder(context, ChannelManager.CHANNEL_PROGRESS)
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setContentTitle("前台服务运行中")
            .setContentText("正在执行后台任务")
            .setContentIntent(createContentPendingIntent(notificationId))
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "停止",
                createActionPendingIntent(NotificationActionReceiver.ACTION_CANCEL, notificationId)
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    // ==================== 媒体通知 ====================

    // 媒体通知
    fun sendMediaNotification() {
        val notificationId = ID_MEDIA_START
        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_MEDIA)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle("正在播放")
            .setContentText("示例歌曲 - 示例艺术家")
            .setContentIntent(createContentPendingIntent(notificationId))
            .addAction(
                android.R.drawable.ic_media_previous,
                "上一首",
                createActionPendingIntent(NotificationActionReceiver.ACTION_PREV, notificationId)
            )
            .addAction(
                android.R.drawable.ic_media_pause,
                "暂停",
                createActionPendingIntent(NotificationActionReceiver.ACTION_PAUSE, notificationId)
            )
            .addAction(
                android.R.drawable.ic_media_next,
                "下一首",
                createActionPendingIntent(NotificationActionReceiver.ACTION_NEXT, notificationId)
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    // ==================== 高级功能 ====================

    // 自定义布局通知
    fun sendCustomLayoutNotification() {
        val notificationId = ID_ADVANCED_START
        val remoteViews = RemoteViews(context.packageName, R.layout.notification_custom)
        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_ADVANCED)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(createContentPendingIntent(notificationId))
            .setCustomContentView(remoteViews)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    // 定时通知
    fun sendScheduledNotification() {
        val notificationId = ID_ADVANCED_START + 1
        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_ADVANCED)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("定时通知")
            .setContentText("这是延迟 5 秒后发送的通知")
            .setContentIntent(createContentPendingIntent(notificationId))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    // 操作按钮通知（增强版）
    fun sendActionNotification() {
        val notificationId = ID_ADVANCED_START + 2

        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_ADVANCED)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("操作通知")
            .setContentText("点击下方按钮进行操作")
            .setContentIntent(createContentPendingIntent(notificationId))
            .addAction(
                android.R.drawable.ic_menu_send,
                "回复",
                createActionPendingIntent(NotificationActionReceiver.ACTION_REPLY, notificationId)
            )
            .addAction(
                android.R.drawable.ic_menu_delete,
                "删除",
                createActionPendingIntent(NotificationActionReceiver.ACTION_DELETE, notificationId)
            )
            .addAction(
                android.R.drawable.ic_menu_share,
                "分享",
                createActionPendingIntent(NotificationActionReceiver.ACTION_SHARE, notificationId)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    // 确认对话框通知
    fun sendConfirmNotification() {
        val notificationId = ID_ADVANCED_START + 3

        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_ADVANCED)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("确认操作")
            .setContentText("是否确认删除此文件？")
            .setContentIntent(createContentPendingIntent(notificationId))
            .addAction(
                android.R.drawable.ic_menu_delete,
                "确认删除",
                createActionPendingIntent(NotificationActionReceiver.ACTION_CONFIRM, notificationId)
            )
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "取消",
                createActionPendingIntent(NotificationActionReceiver.ACTION_CANCEL, notificationId)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    // 下载任务通知
    fun sendDownloadNotification() {
        val notificationId = ID_ADVANCED_START + 4

        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_PROGRESS)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("新版本可用")
            .setContentText("v2.0.0 (15.3 MB)")
            .setContentIntent(createContentPendingIntent(notificationId))
            .addAction(
                android.R.drawable.stat_sys_download,
                "下载",
                createActionPendingIntent(NotificationActionReceiver.ACTION_DOWNLOAD, notificationId)
            )
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "稍后提醒",
                createActionPendingIntent(NotificationActionReceiver.ACTION_CANCEL, notificationId)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    // ==================== 分组通知 ====================

    fun sendGroupNotification(groupKey: String, notificationId: Int, title: String, content: String) {
        val notification = NotificationCompat.Builder(context, ChannelManager.CHANNEL_BASIC)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(createContentPendingIntent(notificationId))
            .setGroup(groupKey)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(notificationId, notification)
        sendGroupSummary(groupKey)
    }

    private fun sendGroupSummary(groupKey: String) {
        val summaryId = getSummaryId(groupKey)
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
            .setContentIntent(createContentPendingIntent(summaryId))
            .setGroup(groupKey)
            .setGroupSummary(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notificationManager.notify(summaryId, summaryNotification)
    }

    private fun getSummaryId(groupKey: String): Int {
        return when (groupKey) {
            GROUP_KEY_CHAT -> 6001
            GROUP_KEY_EMAIL -> 6002
            GROUP_KEY_SOCIAL -> 6003
            GROUP_KEY_SYSTEM -> 6004
            else -> 6000
        }
    }

    fun clearGroupNotifications(groupKey: String) {
        notificationManager.cancel(getSummaryId(groupKey))
    }

    fun clearAllNotifications() {
        notificationManager.cancelAll()
    }

    // ==================== 通用方法 ====================

    fun sendNotificationByType(type: NotificationType) {
        when (type) {
            NotificationType.NORMAL -> sendBasicNotification()
            NotificationType.BIG_TEXT -> sendBigTextNotification()
            NotificationType.BIG_PICTURE -> sendBigPictureNotification()
            NotificationType.INBOX -> sendInboxNotification()
            NotificationType.MESSAGING -> sendMessagingNotification()
            NotificationType.CONVERSATION -> sendReplyNotification()
            NotificationType.BUBBLE -> sendSocialNotification()
            NotificationType.REPLY -> sendReplyNotification()
            NotificationType.SOCIAL -> sendSocialNotification()
            NotificationType.PROGRESS -> sendProgressNotification()
            NotificationType.ONGOING -> sendOngoingNotification()
            NotificationType.FOREGROUND -> { /* 需要服务配合 */ }
            NotificationType.MEDIA -> sendMediaNotification()
            NotificationType.CUSTOM -> sendCustomLayoutNotification()
            NotificationType.SCHEDULED -> sendScheduledNotification()
            NotificationType.ACTION -> sendActionNotification()
            NotificationType.CONFIRM -> sendConfirmNotification()
            NotificationType.DOWNLOAD -> sendDownloadNotification()
        }
    }

    private fun createColorBitmap(width: Int, height: Int, color: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(color)
        return bitmap
    }
}
