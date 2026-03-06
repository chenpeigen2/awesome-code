package com.peter.window.demo.viewroot

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.peter.window.demo.databinding.ActivityViewRootMainBinding
import com.peter.window.demo.viewroot.basic.ViewRootBasicActivity
import com.peter.window.demo.viewroot.advanced.MeasureLayoutDrawActivity
import com.peter.window.demo.viewroot.deep.ChoreographerActivity
import com.peter.window.demo.viewroot.deep.InputEventDispatchActivity
import com.peter.window.demo.viewroot.deep.SurfaceRenderActivity
import com.peter.window.demo.viewroot.deep.ThreadModelActivity
import com.peter.window.demo.viewroot.practice.ViewRootMonitorActivity

/**
 * ViewRootImpl Demo 主入口
 *
 * ViewRootImpl 是 Android GUI 系统中最核心的类之一，
 * 它是连接 View 树和 WindowManagerService 的桥梁。
 *
 * 本 Demo 涵盖以下内容：
 *
 * 一、基础篇：ViewRootImpl 概念
 *    1. ViewRootImpl 是什么
 *    2. 如何获取 ViewRootImpl
 *    3. 核心字段和方法
 *    4. 生命周期
 *
 * 二、进阶篇：测量、布局、绘制
 *    1. performTraversals 详解
 *    2. Measure 测量流程
 *    3. Layout 布局流程
 *    4. Draw 绘制流程
 *    5. MeasureSpec 详解
 *    6. requestLayout vs invalidate
 *
 * 三、深入篇：输入事件与 VSync
 *    1. 输入事件分发机制
 *    2. 触摸事件分发
 *    3. 键盘事件分发
 *    4. InputStage 责任链
 *    5. Choreographer 与 VSync
 *    6. Surface 与渲染
 *    7. 线程模型与 Handler
 *
 * 四、实践篇：性能监控
 *    1. 帧率监控
 *    2. 布局监控
 *    3. 绘制监控
 *    4. View 层级分析
 */
class ViewRootMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewRootMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewRootMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupChips()
        showOverview()
    }

    private fun setupChips() {
        // 基础篇
        addChip("基础概念", "ViewRootImpl 概述、获取方式、核心字段") {
            startActivity(Intent(this, ViewRootBasicActivity::class.java))
        }

        // 进阶篇
        addChip("测量布局绘制", "performTraversals、MeasureSpec、绘制流程") {
            startActivity(Intent(this, MeasureLayoutDrawActivity::class.java))
        }

        // 深入篇
        addChip("输入事件分发", "触摸事件、键盘事件、InputStage") {
            startActivity(Intent(this, InputEventDispatchActivity::class.java))
        }

        addChip("Choreographer", "VSync、帧回调、帧率监控") {
            startActivity(Intent(this, ChoreographerActivity::class.java))
        }

        addChip("Surface与渲染", "Surface、硬件加速、RenderNode、SurfaceFlinger") {
            startActivity(Intent(this, SurfaceRenderActivity::class.java))
        }

        addChip("线程模型", "线程检查、Handler、同步屏障、消息类型") {
            startActivity(Intent(this, ThreadModelActivity::class.java))
        }

        // 实践篇
        addChip("性能监控实践", "帧率监控、布局监控、View层级分析") {
            startActivity(Intent(this, ViewRootMonitorActivity::class.java))
        }
    }

    private fun addChip(label: String, description: String, onClick: () -> Unit) {
        val chip = Chip(this).apply {
            text = label
            isClickable = true
            isCheckable = false
            setOnClickListener { onClick() }
        }
        binding.chipGroup.addView(chip)
    }

    private fun showOverview() {
        val sb = StringBuilder()
        sb.appendLine("=== ViewRootImpl 概述 ===\n")
        
        sb.appendLine("ViewRootImpl 是 Android GUI 系统的核心桥梁\n")
        
        sb.appendLine("【定位】")
        sb.appendLine("┌─────────────────────────────────────┐")
        sb.appendLine("│           Activity                  │")
        sb.appendLine("│              ↓                      │")
        sb.appendLine("│         PhoneWindow                 │")
        sb.appendLine("│              ↓                      │")
        sb.appendLine("│          DecorView                  │")
        sb.appendLine("│              ↓                      │")
        sb.appendLine("│       ┌──────────────┐              │")
        sb.appendLine("│       │ ViewRootImpl │ ← 核心！     │")
        sb.appendLine("│       └──────────────┘              │")
        sb.appendLine("│         ↓         ↓                 │")
        sb.appendLine("│    View 树      WMS                 │")
        sb.appendLine("└─────────────────────────────────────┘\n")
        
        sb.appendLine("【核心职责】")
        sb.appendLine("1. 管理 View 树的测量、布局、绘制")
        sb.appendLine("2. 处理输入事件（触摸、键盘等）")
        sb.appendLine("3. 与 WMS 通信（窗口添加、更新、删除）")
        sb.appendLine("4. 管理 Surface 和 Canvas")
        sb.appendLine("5. 协调 VSync 信号\n")
        
        sb.appendLine("【核心方法】")
        sb.appendLine("- setView()     : 设置 View 树")
        sb.appendLine("- requestLayout(): 请求布局")
        sb.appendLine("- performTraversals(): 执行遍历")
        sb.appendLine("- dispatchTouchEvent(): 分发触摸事件\n")
        
        sb.appendLine("【点击上方按钮查看详细内容】")
        
        binding.tvInfo.text = sb.toString()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
