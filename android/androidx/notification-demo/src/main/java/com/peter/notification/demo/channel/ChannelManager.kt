package com.peter.notification.demo.channel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import com.peter.notification.demo.ChannelItem

/**
 * Channel 管理类
 * 管理 NotificationChannel 的 CRUD 操作
 */
class ChannelManager(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        // Channel IDs
        const val CHANNEL_BASIC = "channel_basic"
        const val CHANNEL_MESSAGE = "channel_message"
        const val CHANNEL_PROGRESS = "channel_progress"
        const val CHANNEL_MEDIA = "channel_media"
        const val CHANNEL_ADVANCED = "channel_advanced"
    }

    /**
     * 创建 Channel
     */
    fun createChannel(
        id: String,
        name: String,
        descriptionText: String = "",
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
        groupId: String? = null,
        soundUri: Uri? = null,
        enableVibration: Boolean = true,
        enableLights: Boolean = true
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, name, importance).apply {
                description = descriptionText
                this.group = groupId
                
                if (soundUri != null) {
                    setSound(soundUri, AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build())
                }
                
                enableVibration(enableVibration)
                enableLights(enableLights)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * 删除 Channel
     */
    fun deleteChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(channelId)
        }
    }

    /**
     * 获取所有 Channel
     */
    fun getAllChannels(): List<NotificationChannel> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return emptyList()
        }
        
        return notificationManager.notificationChannels
    }

    /**
     * 获取指定 Group 的 Channel
     */
    fun getChannelsByGroup(groupId: String): List<NotificationChannel> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return emptyList()
        }
        return notificationManager.notificationChannels.filter { it.group == groupId }
    }

    /**
     * 重置所有 Channel
     */
    fun resetAllChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.notificationChannels.forEach { channel ->
                notificationManager.deleteNotificationChannel(channel.id)
            }
            initializeDefaultChannels()
        }
    }

    /**
     * 初始化默认 Channel
     */
    fun initializeDefaultChannels() {
        createChannel(
            id = CHANNEL_BASIC,
            name = "基础通知",
            descriptionText = "普通通知通道",
            importance = NotificationManager.IMPORTANCE_DEFAULT
        )
        
        createChannel(
            id = CHANNEL_MESSAGE,
            name = "消息通知",
            descriptionText = "消息类型通知",
            importance = NotificationManager.IMPORTANCE_HIGH
        )
        
        createChannel(
            id = CHANNEL_PROGRESS,
            name = "进度通知",
            descriptionText = "进度条通知",
            importance = NotificationManager.IMPORTANCE_LOW
        )
        
        createChannel(
            id = CHANNEL_MEDIA,
            name = "媒体通知",
            descriptionText = "媒体播放通知",
            importance = NotificationManager.IMPORTANCE_LOW
        )
        
        createChannel(
            id = CHANNEL_ADVANCED,
            name = "高级通知",
            descriptionText = "高级功能通知",
            importance = NotificationManager.IMPORTANCE_DEFAULT
        )
    }
}
