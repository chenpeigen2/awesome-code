package com.peter.notification.demo

/**
 * 通知类型枚举
 */
enum class NotificationType {
    NORMAL, BIG_TEXT, BIG_PICTURE, INBOX,
    MESSAGING, CONVERSATION, BUBBLE, REPLY, SOCIAL,
    PROGRESS, ONGOING, FOREGROUND,
    MEDIA, CUSTOM, SCHEDULED, ACTION, CONFIRM, DOWNLOAD
}

/**
 * 通知类型分类
 */
enum class NotificationCategory(val displayName: String) {
    BASIC("基础通知"),
    MESSAGE("消息通知"),
    PROGRESS("进度与状态"),
    MEDIA("媒体通知"),
    ADVANCED("高级功能")
}

/**
 * 通知类型数据模型
 */
data class NotificationItem(
    val type: NotificationType,
    val title: String,
    val description: String,
    val category: NotificationCategory,
    val action: () -> Unit = {}
)

/**
 * Channel 数据模型
 */
data class ChannelItem(
    val id: String,
    val name: String,
    val groupId: String?,
    val importance: Int,
    val hasSound: Boolean = true,
    val hasVibration: Boolean = true,
    val hasLights: Boolean = true
)

/**
 * ChannelGroup 数据模型
 */
data class ChannelGroupItem(
    val id: String,
    val name: String,
    val channelCount: Int = 0
)

/**
 * 铃声数据模型
 */
data class RingtoneItem(
    val uri: String,
    val name: String,
    val isCustom: Boolean = false
)
