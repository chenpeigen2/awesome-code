package com.peter.anr.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.peter.anr.demo.databinding.ActivityAnrInfoBinding

/**
 * ANR 基础介绍
 *
 * 本页面介绍 ANR 的基本概念、触发机制、四种类型和日志获取方式。
 *
 * 【内容概览】
 *
 * 1. ANR 定义
 *    - Application Not Responding（应用无响应）
 *    - 当主线程在超时时间内无法处理完事件时触发
 *    - 系统弹出对话框或直接杀掉进程
 *
 * 2. ANR 触发机制
 *    - 用户操作 → InputEvent → 主线程消息队列
 *    - AMS 启动超时计时
 *    - 超时未处理 → 弹出 ANR 对话框
 *
 * 3. 四种 ANR 类型
 *    - Input Dispatching Timeout（5 秒）
 *    - BroadcastReceiver Timeout（前台 10 秒 / 后台 60 秒）
 *    - Service Timeout（前台 20 秒 / 后台 200 秒）
 *    - ContentProvider Timeout（10 秒）
 *
 * 4. ANR 日志
 *    - 位置: /data/anr/traces.txt
 *    - 获取: adb pull /data/anr/traces.txt
 *    - 查看: adb logcat -b events | grep am_anr
 */
class AnrInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnrInfoBinding
    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnrInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        showAnrInfo()
    }

    /**
     * 显示 ANR 基础介绍信息
     *
     * 该方法将所有 ANR 相关的基础知识以结构化文本的形式展示：
     * - ANR 的定义和原理
     * - 触发机制的四个步骤
     * - 四种 ANR 类型及其超时阈值
     * - ANR trace 日志的获取方式
     */
    private fun showAnrInfo() {
        sb.clear()

        // ===================== ANR 基础概念 =====================
        sb.appendLine("=== ANR 基础介绍 ===")
        sb.appendLine()

        // 1. 什么是 ANR
        sb.appendLine("=== 1. 什么是 ANR ===")
        sb.appendLine("ANR (Application Not Responding) 应用无响应")
        sb.appendLine("当 Android 系统检测到应用在主线程上执行耗时操作")
        sb.appendLine("超过一定时间后，会弹出\"应用无响应\"对话框")
        sb.appendLine()

        // 2. ANR 触发机制
        sb.appendLine("=== 2. ANR 触发机制 ===")
        sb.appendLine("1. 用户操作 → InputEvent 送入应用主线程消息队列")
        sb.appendLine("2. ActivityManagerService (AMS) 启动超时计时")
        sb.appendLine("3. 如果主线程在超时时间内没有处理完事件")
        sb.appendLine("4. AMS 弹出 ANR 对话框或直接杀掉进程")
        sb.appendLine()

        // 3. 四种 ANR 类型
        sb.appendLine("=== 3. 四种 ANR 类型 ===")

        // Input Dispatching Timeout — 输入事件超时
        sb.appendLine("[Input Dispatching Timeout]")
        sb.appendLine("  超时: 5 秒")
        sb.appendLine("  场景: 主线程无法响应输入事件（触摸、按键）")
        sb.appendLine("  最常见的 ANR 类型")
        sb.appendLine()

        // BroadcastReceiver Timeout — 广播超时
        sb.appendLine("[BroadcastReceiver Timeout]")
        sb.appendLine("  超时: 前台广播 10 秒 / 后台广播 60 秒")
        sb.appendLine("  场景: onReceive() 方法执行时间过长")
        sb.appendLine()

        // Service Timeout — 服务超时
        sb.appendLine("[Service Timeout]")
        sb.appendLine("  超时: 前台服务 20 秒 / 后台服务 200 秒")
        sb.appendLine("  场景: onCreate()/onStartCommand()/onBind() 执行过长")
        sb.appendLine()

        // ContentProvider Timeout — ContentProvider 超时
        sb.appendLine("[ContentProvider Timeout]")
        sb.appendLine("  超时: 10 秒")
        sb.appendLine("  场景: ContentProvider 发布超时")
        sb.appendLine()

        // 4. ANR 日志
        sb.appendLine("=== 4. ANR 日志 ===")
        sb.appendLine("位置: /data/anr/traces.txt")
        sb.appendLine("获取: adb pull /data/anr/traces.txt")
        sb.appendLine("查看: adb logcat -b events | grep am_anr")

        binding.tvInfo.text = sb.toString()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
