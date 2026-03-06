package com.peter.window.demo.activitythread

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.peter.window.demo.R
import com.peter.window.demo.activitythread.advanced.ActivityLaunchFlowActivity
import com.peter.window.demo.activitythread.basic.ActivityThreadBasicActivity
import com.peter.window.demo.activitythread.deep.HandlerMessageActivity
import com.peter.window.demo.activitythread.deep.InstrumentationActivity
import com.peter.window.demo.activitythread.practice.ActivityThreadHookActivity
import com.peter.window.demo.databinding.ActivityActivitythreadMainBinding

/**
 * ActivityThread Demo 主入口
 *
 * === ActivityThread 完整解析 ===
 *
 * ActivityThread 是 Android 应用进程的核心类，
 * 它是主线程的入口点，管理着应用中所有的组件。
 *
 * 【本 Demo 涵盖内容】
 *
 * 一、基础篇：ActivityThread 概念
 *    - ActivityThread 是什么
 *    - 核心字段详解
 *    - 获取 ActivityThread 实例
 *    - 与普通 Thread 的区别
 *
 * 二、进阶篇：Activity 启动流程
 *    - Launcher → AMS 流程
 *    - AMS → App 进程流程
 *    - App 进程内部流程
 *    - performLaunchActivity 详解
 *
 * 三、深入篇：Handler 消息机制
 *    - H 类详解
 *    - 消息类型大全
 *    - handleMessage 处理逻辑
 *    - 消息流转完整流程
 *
 * 四、深入篇：Instrumentation 生命周期
 *    - Instrumentation 作用
 *    - 生命周期调用链
 *    - Activity 创建过程
 *    - Hook Instrumentation
 *
 * 五、实践篇：Hook 与监控
 *    - 生命周期监控实现
 *    - 启动耗时监控
 *    - Handler Hook 实现
 *    - 卡顿检测原理
 */
class ActivityThreadMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActivitythreadMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActivitythreadMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupChips()
        showOverview()
    }

    private fun setupChips() {
        // 基础篇
        binding.chipBasic.setOnClickListener { 
            startActivity(Intent(this, ActivityThreadBasicActivity::class.java)) 
        }
        
        // 进阶篇
        binding.chipLaunchFlow.setOnClickListener { 
            startActivity(Intent(this, ActivityLaunchFlowActivity::class.java)) 
        }
        
        // 深入篇
        binding.chipHandler.setOnClickListener { 
            startActivity(Intent(this, HandlerMessageActivity::class.java)) 
        }
        binding.chipInstrumentation.setOnClickListener { 
            startActivity(Intent(this, InstrumentationActivity::class.java)) 
        }
        
        // 实践篇
        binding.chipHook.setOnClickListener { 
            startActivity(Intent(this, ActivityThreadHookActivity::class.java)) 
        }
    }

    private fun showOverview() {
        val sb = StringBuilder()
        sb.appendLine("=== ActivityThread 概述 ===\n")
        sb.appendLine("ActivityThread 是 Android 应用的主线程类，")
        sb.appendLine("每个应用进程有且只有一个实例。\n")
        sb.appendLine("【核心职责】")
        sb.appendLine("• 管理所有 Activity 的生命周期")
        sb.appendLine("• 管理 Application 对象")
        sb.appendLine("• 运行主线程消息循环")
        sb.appendLine("• 与 AMS 通信，响应系统调度\n")
        sb.appendLine("【启动流程简图】")
        sb.appendLine("Launcher → AMS → Zygote → ActivityThread")
        sb.appendLine("→ Application → Activity\n")
        sb.appendLine("【关键类】")
        sb.appendLine("• ActivityThread: 主线程管理")
        sb.appendLine("• ApplicationThread: Binder 通信")
        sb.appendLine("• H (Handler): 消息分发")
        sb.appendLine("• Instrumentation: 生命周期调用")
        
        binding.tvInfo.text = sb.toString()
    }
}
