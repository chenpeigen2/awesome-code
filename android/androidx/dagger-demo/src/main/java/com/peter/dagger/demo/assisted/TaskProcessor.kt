package com.peter.dagger.demo.assisted

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * TaskProcessor - 任务处理器
 *
 * 演示 @AssistedInject：
 * - scheduler: 由 Dagger 提供
 * - taskId, taskName: 由调用方在运行时传入
 *
 * 使用场景：
 * - 需要运行时参数的对象创建
 * - 工厂模式的 Dagger 集成
 */
class TaskProcessor @AssistedInject constructor(
    // Dagger 提供的依赖
    private val scheduler: TaskScheduler,
    // 运行时传入的参数 - 使用 identifier 区分相同类型
    @Assisted("taskId") private val taskId: String,
    @Assisted("taskName") private val taskName: String
) {
    /**
     * 执行任务
     */
    fun process(): String {
        val scheduleTime = scheduler.getNextScheduleTime()
        return """
            |任务处理完成:
            |- ID: $taskId
            |- 名称: $taskName
            |- 调度时间: $scheduleTime
            |- 线程: ${Thread.currentThread().name}
        """.trimMargin()
    }

    override fun toString(): String {
        return "TaskProcessor(taskId='$taskId', taskName='$taskName', scheduler=$scheduler)"
    }

    /**
     * AssistedFactory - 工厂接口
     *
     * Dagger 会自动生成实现
     */
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("taskId") taskId: String,
            @Assisted("taskName") taskName: String
        ): TaskProcessor
    }
}