package com.peter.components.demo.activity.startup

import android.util.Log

/**
 * Activity 启动流程追踪器
 *
 * 用于记录和展示 Activity 启动过程中各个关键节点的时间戳
 *
 * 知识点：
 * 1. Activity 启动涉及多个进程间的通信（App ↔ SystemServer）
 * 2. 每个生命周期回调都有特定的调用时机和职责
 * 3. 通过时间戳可以分析启动性能瓶颈
 */
object StartupTracker {

    private const val TAG = "StartupTracker"

    /**
     * 追踪记录项
     * @param timestamp 相对于启动开始的时间戳（毫秒）
     * @param stage 阶段名称
     * @param detail 详细说明（可选）
     */
    data class TrackRecord(
        val timestamp: Long,
        val stage: String,
        val detail: String = ""
    )

    // 记录列表
    private val records = mutableListOf<TrackRecord>()

    // 启动起始时间（第一次调用 logStage 时记录）
    private var startTime: Long = 0

    // 是否已初始化
    private var isInitialized = false

    /**
     * 记录启动阶段
     * @param stage 阶段名称
     * @param detail 详细说明
     */
    fun logStage(stage: String, detail: String = "") {
        val now = System.currentTimeMillis()

        if (!isInitialized) {
            startTime = now
            isInitialized = true
        }

        val timestamp = now - startTime
        val record = TrackRecord(timestamp, stage, detail)
        records.add(record)

        Log.d(TAG, "[${timestamp}ms] $stage ${if (detail.isNotEmpty()) "- $detail" else ""}")
    }

    /**
     * 获取格式化的报告
     */
    fun getReport(): String {
        if (records.isEmpty()) {
            return "暂无启动记录"
        }

        val sb = StringBuilder()
        sb.appendLine("═══════════════════════════════════════")
        sb.appendLine("       Activity 启动流程时间线")
        sb.appendLine("═══════════════════════════════════════")
        sb.appendLine()

        records.forEachIndexed { index, record ->
            val prefix = if (index == records.lastIndex) "└─" else "├─"
            val detailPart = if (record.detail.isNotEmpty()) "\n   └─ ${record.detail}" else ""
            sb.appendLine("${prefix}[${record.timestamp}ms] ${record.stage}$detailPart")
        }

        sb.appendLine()
        sb.appendLine("───────────────────────────────────────")
        sb.appendLine("总耗时: ${records.lastOrNull()?.timestamp ?: 0}ms")
        sb.appendLine("═══════════════════════════════════════")

        return sb.toString()
    }

    /**
     * 获取简要日志（用于界面显示）
     */
    fun getSimpleLog(): String {
        if (records.isEmpty()) {
            return "暂无启动记录"
        }

        return records.joinToString("\n") { record ->
            val detailPart = if (record.detail.isNotEmpty()) " (${record.detail})" else ""
            "[${record.timestamp}ms] ${record.stage}$detailPart"
        }
    }

    /**
     * 重置追踪器
     */
    fun reset() {
        records.clear()
        startTime = 0
        isInitialized = false
        Log.d(TAG, "追踪器已重置")
    }

    /**
     * 获取记录数量
     */
    fun getRecordCount(): Int = records.size
}
