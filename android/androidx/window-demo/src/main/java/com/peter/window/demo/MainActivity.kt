package com.peter.window.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.peter.window.demo.advanced.DialogActivity
import com.peter.window.demo.advanced.FloatingWindowActivity
import com.peter.window.demo.advanced.PopupWindowActivity
import com.peter.window.demo.advanced.WindowMechanismActivity
import com.peter.window.demo.advanced.WindowViewRelationActivity
import com.peter.window.demo.basic.WindowBasicActivity
import com.peter.window.demo.basic.WindowManagerActivity
import com.peter.window.demo.deep.WMSActivity
import com.peter.window.demo.deep.WindowCreateProcessActivity
import com.peter.window.demo.practice.CustomWindowActivity
import com.peter.window.demo.practice.DynamicWindowActivity
import com.peter.window.demo.databinding.ActivityMainBinding

/**
 * Window Demo 主入口
 * 
 * 本Demo涵盖以下内容：
 * 
 * 一、基础篇：Window概念与WindowManager
 *    1. Window基础概念 - Window是什么，Window的类型
 *    2. WindowManager详解 - WindowManager的使用方法
 * 
 * 二、进阶篇：Dialog、PopupWindow、悬浮窗
 *    1. Dialog详解 - 各种Dialog的使用
 *    2. PopupWindow详解 - PopupWindow的使用
 *    3. 悬浮窗实现 - 系统悬浮窗的实现
 *    4. Window内部机制 - Window的内部实现
 *    5. Window与View关系 - Window和View的关系
 * 
 * 三、深入篇：WindowManagerService与Window创建
 *    1. WindowManagerService - WMS的工作原理
 *    2. Window创建过程 - Window的完整创建流程
 * 
 * 四、实战篇：自定义Window与动态窗口
 *    1. 自定义Window - 实现自定义Window
 *    2. 动态窗口 - 动态添加和管理Window
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupChips()
        showOverview()
    }

    private fun setupChips() {
        // 基础篇
        binding.chipWindowBasic.setOnClickListener { startActivity(Intent(this, WindowBasicActivity::class.java)) }
        binding.chipWindowManager.setOnClickListener { startActivity(Intent(this, WindowManagerActivity::class.java)) }
        
        // 进阶篇
        binding.chipDialog.setOnClickListener { startActivity(Intent(this, DialogActivity::class.java)) }
        binding.chipPopup.setOnClickListener { startActivity(Intent(this, PopupWindowActivity::class.java)) }
        binding.chipFloating.setOnClickListener { startActivity(Intent(this, FloatingWindowActivity::class.java)) }
        binding.chipMechanism.setOnClickListener { startActivity(Intent(this, WindowMechanismActivity::class.java)) }
        binding.chipViewRelation.setOnClickListener { startActivity(Intent(this, WindowViewRelationActivity::class.java)) }
        
        // 深入篇
        binding.chipWMS.setOnClickListener { startActivity(Intent(this, WMSActivity::class.java)) }
        binding.chipCreate.setOnClickListener { startActivity(Intent(this, WindowCreateProcessActivity::class.java)) }
        
        // 实战篇
        binding.chipCustomWindow.setOnClickListener { startActivity(Intent(this, CustomWindowActivity::class.java)) }
        binding.chipDynamicWindow.setOnClickListener { startActivity(Intent(this, DynamicWindowActivity::class.java)) }
    }

    private fun showOverview() {
        val sb = StringBuilder()
        sb.appendLine("=== Window 概述 ===\n")
        sb.appendLine("Window 是 Android 中一个抽象的概念，")
        sb.appendLine("它表示一个用于显示内容的窗口。\n")
        sb.appendLine("在 Android 中：")
        sb.appendLine("  • 每个 Activity 都有一个 Window")
        sb.appendLine("  • Dialog 需要依附于 Window")
        sb.appendLine("  • Toast 使用系统 Window")
        sb.appendLine("  • 悬浮窗也是一种 Window\n")
        sb.appendLine("Window 的实现类是 PhoneWindow，")
        sb.appendLine("它内部包含一个 DecorView 作为根视图。\n")
        sb.appendLine("WindowManager 是管理 Window 的接口，")
        sb.appendLine("它提供了添加、更新、删除视图的能力。")
        
        binding.tvInfo.text = sb.toString()
    }
}