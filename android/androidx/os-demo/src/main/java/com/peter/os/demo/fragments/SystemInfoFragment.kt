package com.peter.os.demo.fragments

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.os.demo.R
import com.peter.os.demo.databinding.FragmentSystemInfoBinding

/**
 * Build/Process 系统信息示例
 */
class SystemInfoFragment : Fragment() {

    private var _binding: FragmentSystemInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSystemInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSystemInfo()
        loadProcessInfo()
        loadBundleDemo()
    }

    private fun loadSystemInfo() {
        val sb = StringBuilder()
        
        sb.appendLine("=== 设备信息 ===")
        sb.appendLine("型号: ${Build.MODEL}")
        sb.appendLine("品牌: ${Build.BRAND}")
        sb.appendLine("制造商: ${Build.MANUFACTURER}")
        sb.appendLine("设备代号: ${Build.DEVICE}")
        sb.appendLine("产品名: ${Build.PRODUCT}")
        sb.appendLine("硬件: ${Build.HARDWARE}")
        sb.appendLine("板子: ${Build.BOARD}")
        sb.appendLine()
        
        sb.appendLine("=== 系统信息 ===")
        sb.appendLine("Android版本: ${Build.VERSION.RELEASE}")
        sb.appendLine("SDK版本: ${Build.VERSION.SDK_INT}")
        sb.appendLine("SDK版本: ${Build.VERSION.SDK_INT_FULL}")
        sb.appendLine("安全补丁: ${Build.VERSION.SECURITY_PATCH}")
        sb.appendLine("增量版本: ${Build.VERSION.INCREMENTAL}")
        sb.appendLine("显示版本: ${Build.DISPLAY}")
        sb.appendLine()
        
        sb.appendLine("=== CPU信息 ===")
        sb.appendLine("CPU架构: ${Build.SUPPORTED_ABIS.joinToString(", ")}")
        sb.appendLine("CPU核心数: ${Runtime.getRuntime().availableProcessors()}")
        sb.appendLine()
        
        sb.appendLine("=== 构建信息 ===")
        sb.appendLine("构建类型: ${Build.TYPE}")
        sb.appendLine("构建标签: ${Build.TAGS}")
        sb.appendLine("指纹: ${Build.FINGERPRINT}")
        sb.appendLine("启动加载器: ${Build.BOOTLOADER}")
        sb.appendLine("无线电: ${Build.getRadioVersion()}")
        
        binding.tvSystemInfo.text = sb.toString()
    }

    private fun loadProcessInfo() {
        val sb = StringBuilder()
        
        sb.appendLine("=== 进程信息 ===")
        sb.appendLine("进程ID (PID): ${Process.myPid()}")
        sb.appendLine("用户ID (UID): ${Process.myUid()}")
        sb.appendLine("线程ID (TID): ${Process.myTid()}")
        sb.appendLine("进程名: ${getProcessName()}")
        sb.appendLine()
        
        sb.appendLine("=== UID信息 ===")
        sb.appendLine("是应用UID: ${Process.isApplicationUid(Process.myUid())}")
        sb.appendLine("UID名称: ${getUidName()}")
        sb.appendLine()
        
        sb.appendLine("=== 进程优先级 ===")
        sb.appendLine("当前线程优先级: ${Process.getThreadPriority(Process.myTid())}")
        sb.appendLine()
        
        sb.appendLine("=== 内存信息 ===")
        val runtime = Runtime.getRuntime()
        sb.appendLine("总内存: ${formatSize(runtime.totalMemory())}")
        sb.appendLine("可用内存: ${formatSize(runtime.freeMemory())}")
        sb.appendLine("最大内存: ${formatSize(runtime.maxMemory())}")
        
        binding.tvProcessInfo.text = sb.toString()
    }

    private fun loadBundleDemo() {
        // Bundle 基本使用示例
        val bundle = Bundle().apply {
            putString("name", "张三")
            putInt("age", 25)
            putBoolean("isStudent", true)
            putDouble("score", 95.5)
            putLong("timestamp", System.currentTimeMillis())
        }
        
        val sb = StringBuilder()
        sb.appendLine("=== Bundle 使用示例 ===")
        sb.appendLine("创建Bundle并存入数据:")
        sb.appendLine("  putString(\"name\", \"张三\")")
        sb.appendLine("  putInt(\"age\", 25)")
        sb.appendLine("  putBoolean(\"isStudent\", true)")
        sb.appendLine("  putDouble(\"score\", 95.5)")
        sb.appendLine()
        sb.appendLine("读取数据:")
        sb.appendLine("  name = ${bundle.getString("name")}")
        sb.appendLine("  age = ${bundle.getInt("age")}")
        sb.appendLine("  isStudent = ${bundle.getBoolean("isStudent")}")
        sb.appendLine("  score = ${bundle.getDouble("score")}")
        sb.appendLine()
        sb.appendLine("Bundle包含的键: ${bundle.keySet().joinToString(", ")}")
        sb.appendLine("Bundle大小: ${bundle.size()} 个键值对")
        
        binding.tvBundleDemo.text = sb.toString()
    }

    private fun getProcessName(): String {
        return try {
            val activityManager = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val pid = Process.myPid()
            activityManager.runningAppProcesses?.find { it.pid == pid }?.processName ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }

    private fun getUidName(): String {
        val uid = Process.myUid()
        return when {
            uid == Process.ROOT_UID -> "root"
            uid == Process.SYSTEM_UID -> "system"
            uid == Process.PHONE_UID -> "phone"
            uid == Process.BLUETOOTH_UID -> "bluetooth"
            uid < 10000 -> "system ($uid)"
            else -> "app ($uid)"
        }
    }

    private fun formatSize(bytes: Long): String {
        val mb = 1024 * 1024L
        return String.format("%.2f MB", bytes.toDouble() / mb)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = SystemInfoFragment()
    }
}