package com.peter.notification.demo.channel

import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.peter.notification.demo.ChannelGroupItem

/**
 * ChannelGroup 管理类
 */
class ChannelGroupManager(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val GROUP_WORK = "group_work"
        const val GROUP_SOCIAL = "group_social"
        const val GROUP_SYSTEM = "group_system"
    }

    /**
     * 创建 ChannelGroup
     */
    fun createGroup(id: String, name: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val group = NotificationChannelGroup(id, name)
            notificationManager.createNotificationChannelGroup(group)
        }
    }

    /**
     * 删除 ChannelGroup
     */
    fun deleteGroup(groupId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannelGroup(groupId)
        }
    }

    /**
     * 获取所有 ChannelGroup
     */
    fun getAllGroups(): List<ChannelGroupItem> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return emptyList()
        }
        
        return notificationManager.notificationChannelGroups.map { group ->
            val channelsInGroup = notificationManager.notificationChannels
                .count { it.group == group.id }
            ChannelGroupItem(
                id = group.id,
                name = group.name?.toString() ?: "",
                channelCount = channelsInGroup
            )
        }
    }

    /**
     * 初始化默认 Group
     */
    fun initializeDefaultGroups() {
        createGroup(GROUP_WORK, "工作")
        createGroup(GROUP_SOCIAL, "社交")
        createGroup(GROUP_SYSTEM, "系统")
    }
}