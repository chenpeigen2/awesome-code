package com.peter.dagger.demo.assisted

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TaskScheduler - 任务调度器
 *
 * 由 Dagger 提供的单例
 */
@Singleton
class TaskScheduler @Inject constructor() {

    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    /**
     * 获取下次调度时间
     */
    fun getNextScheduleTime(): String {
        return dateFormat.format(Date())
    }

    /**
     * 调度任务
     */
    fun schedule(taskId: String, delayMs: Long): String {
        return "任务 $taskId 已调度，延迟 ${delayMs}ms"
    }
}
