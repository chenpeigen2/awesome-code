package com.peter.notification.demo.sound

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager as SystemRingtoneManager
import android.net.Uri
import androidx.core.net.toUri
import com.peter.notification.demo.R

/**
 * 铃声管理类
 * 处理铃声选择和自定义铃声
 */
class RingtoneManager(private val context: Context) {

    private val ringtoneManager: SystemRingtoneManager = SystemRingtoneManager(context).apply {
        setType(SystemRingtoneManager.TYPE_NOTIFICATION)
    }

    // 当前播放的铃声
    private var currentRingtone: Ringtone? = null

    /**
     * 铃声数据模型
     */
    data class RingtoneInfo(
        val uri: Uri,
        val name: String,
        val isCustom: Boolean = false
    )

    /**
     * 获取系统铃声列表
     */
    fun getSystemRingtones(): List<RingtoneInfo> {
        val ringtones = mutableListOf<RingtoneInfo>()
        val cursor = ringtoneManager.cursor

        while (cursor.moveToNext()) {
            val title = cursor.getString(SystemRingtoneManager.TITLE_COLUMN_INDEX)
            val uri = ringtoneManager.getRingtoneUri(cursor.position)
            ringtones.add(RingtoneInfo(uri, title))
        }

        return ringtones
    }

    /**
     * 获取默认通知铃声
     */
    fun getDefaultNotificationUri(): Uri {
        return SystemRingtoneManager.getDefaultUri(SystemRingtoneManager.TYPE_NOTIFICATION)
    }

    /**
     * 获取自定义铃声列表（从 res/raw 获取）
     */
    fun getCustomRingtones(): List<RingtoneInfo> {
        val customRingtones = mutableListOf<RingtoneInfo>()
        val resources = context.resources

        // 查找 raw 资源中的音频文件
        val fields = R.raw::class.java.fields
        for (field in fields) {
            val name = field.name
            // 过滤掉非音频资源（如 .keep 文件）
            if (name != "keep" && !name.startsWith("_")) {
                val resourceId = resources.getIdentifier(name, "raw", context.packageName)
                val uri = "android.resource://${context.packageName}/$resourceId".toUri()
                customRingtones.add(
                    RingtoneInfo(
                        uri = uri,
                        name = formatRawResourceName(name),
                        isCustom = true
                    )
                )
            }
        }

        return customRingtones
    }

    /**
     * 播放铃声预览
     */
    fun playRingtone(uri: Uri) {
        stopCurrentRingtone()
        currentRingtone = SystemRingtoneManager.getRingtone(context, uri)
        currentRingtone?.play()
    }

    /**
     * 停止当前铃声
     */
    fun stopCurrentRingtone() {
        currentRingtone?.stop()
        currentRingtone = null
    }

    /**
     * 获取铃声名称
     */
    fun getRingtoneName(uri: Uri): String {
        val ringtone = SystemRingtoneManager.getRingtone(context, uri)
        return ringtone?.getTitle(context) ?: "未知铃声"
    }

    /**
     * 格式化 raw 资源名称
     */
    private fun formatRawResourceName(name: String): String {
        // 将下划线替换为空格，首字母大写
        return name.replace("_", " ")
            .split(" ")
            .joinToString(" ") { word ->
                word.replaceFirstChar { it.uppercase() }
            }
    }

    /**
     * 获取所有铃声（系统 + 自定义）
     */
    fun getAllRingtones(): List<RingtoneInfo> {
        val allRingtones = mutableListOf<RingtoneInfo>()
        
        // 添加默认铃声
        allRingtones.add(
            RingtoneInfo(
                uri = getDefaultNotificationUri(),
                name = "默认通知铃声"
            )
        )

        // 添加系统铃声
        allRingtones.addAll(getSystemRingtones())

        // 添加自定义铃声
        allRingtones.addAll(getCustomRingtones())

        return allRingtones
    }

    /**
     * 释放资源
     */
    fun release() {
        stopCurrentRingtone()
    }
}
