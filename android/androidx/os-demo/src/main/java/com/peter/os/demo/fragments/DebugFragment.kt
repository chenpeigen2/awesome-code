package com.peter.os.demo.fragments

import android.os.Build
import android.os.Bundle
import android.os.Debug
import android.os.StrictMode
import android.os.Trace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.os.demo.databinding.FragmentDebugBinding

/**
 * StrictMode/Trace/Debug 调试工具示例
 */
class DebugFragment : Fragment() {

    private var _binding: FragmentDebugBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDebugBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        loadDebugInfo()
        showStrictModeInfo()
    }

    private fun setupViews() {
        binding.btnRefresh.setOnClickListener {
            loadDebugInfo()
        }

        binding.btnEnableStrictMode.setOnClickListener {
            enableStrictMode()
        }

        binding.btnDisableStrictMode.setOnClickListener {
            disableStrictMode()
        }

        binding.btnTraceDemo.setOnClickListener {
            demonstrateTrace()
        }
    }

    private fun loadDebugInfo() {
        val sb = StringBuilder()
        
        // 内存信息
        sb.appendLine("=== Debug 内存信息 ===")
        
        try {
            val memoryInfo = Debug.MemoryInfo()
            Debug.getMemoryInfo(memoryInfo)
            
            sb.appendLine("总私有内存: ${memoryInfo.totalPrivateDirty} KB")
            sb.appendLine("总共享内存: ${memoryInfo.totalSharedDirty} KB")
            sb.appendLine("总PSS内存: ${memoryInfo.totalPss} KB")
            
            // 详细的内存统计
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    sb.appendLine("Java堆: ${memoryInfo.getMemoryStat("summary.java-heap")} KB")
                    sb.appendLine("Native堆: ${memoryInfo.getMemoryStat("summary.native-heap")} KB")
                    sb.appendLine("代码: ${memoryInfo.getMemoryStat("summary.code")} KB")
                    sb.appendLine("栈: ${memoryInfo.getMemoryStat("summary.stack")} KB")
                    sb.appendLine("图形: ${memoryInfo.getMemoryStat("summary.graphics")} KB")
                } catch (e: Exception) {
                    // 部分统计可能不可用
                }
            }
        } catch (e: Exception) {
            sb.appendLine("(无法获取内存信息)")
        }
        sb.appendLine()
        
        // 堆内存信息
        sb.appendLine("=== 堆内存 ===")
        try {
            val runtime = Runtime.getRuntime()
            sb.appendLine("已用堆: ${formatKB(runtime.totalMemory() - runtime.freeMemory())}")
            sb.appendLine("可用堆: ${formatKB(runtime.freeMemory())}")
            sb.appendLine("总堆: ${formatKB(runtime.totalMemory())}")
            sb.appendLine("最大堆: ${formatKB(runtime.maxMemory())}")
        } catch (e: Exception) {
            sb.appendLine("(无法获取堆内存信息)")
        }
        sb.appendLine()
        
        // Native 堆内存
        sb.appendLine("=== Native 堆内存 ===")
        try {
            Debug.getNativeHeapSize().let { sb.appendLine("Native堆大小: ${formatKB(it)}") }
            Debug.getNativeHeapAllocatedSize().let { sb.appendLine("Native已分配: ${formatKB(it)}") }
            Debug.getNativeHeapFreeSize().let { sb.appendLine("Native可用: ${formatKB(it)}") }
        } catch (e: Exception) {
            sb.appendLine("(无法获取Native堆信息)")
        }
        sb.appendLine()
        
        // 调试器状态
        sb.appendLine("=== 调试状态 ===")
        try {
            sb.appendLine("调试器连接: ${if (Debug.isDebuggerConnected()) "是" else "否"}")
            sb.appendLine("等待调试器: ${if (Debug.waitingForDebugger()) "是" else "否"}")
        } catch (e: Exception) {
            sb.appendLine("(无法获取调试状态)")
        }
        sb.appendLine()
        
        // 线程信息
        sb.appendLine("=== 线程信息 ===")
        try {
            sb.appendLine("当前线程数: ${Thread.activeCount()}")
            sb.appendLine("当前线程: ${Thread.currentThread().name}")
        } catch (e: Exception) {
            sb.appendLine("(无法获取线程信息)")
        }
        
        binding.tvDebugInfo.text = sb.toString()
    }

    private fun showStrictModeInfo() {
        val sb = StringBuilder()
        
        sb.appendLine("=== StrictMode 严格模式 ===")
        sb.appendLine()
        sb.appendLine("【线程策略】检测:")
        sb.appendLine("• 主线程磁盘读写")
        sb.appendLine("• 主线程网络操作")
        sb.appendLine("• 主线程自定义慢调用")
        sb.appendLine()
        sb.appendLine("【VM策略】检测:")
        sb.appendLine("• Activity未关闭的Cursors")
        sb.appendLine("• 未关闭的Closeable对象")
        sb.appendLine("• 未标记tags的Socket")
        sb.appendLine("• Activity泄漏")
        sb.appendLine()
        sb.appendLine("【惩罚方式】")
        sb.appendLine("• Log日志输出")
        sb.appendLine("• Dialog弹窗提示")
        sb.appendLine("• 直接Crash")
        sb.appendLine("• DropBox记录")
        sb.appendLine("• 闪屏显示")
        sb.appendLine()
        sb.appendLine("【使用建议】")
        sb.appendLine("仅 Debug 模式启用，生产环境禁用")
        
        binding.tvStrictModeInfo.text = sb.toString()
    }

    private fun enableStrictMode() {
        try {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .penaltyFlashScreen()
                    .build()
            )

            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .detectActivityLeaks()
                    .penaltyLog()
                    .build()
            )

            appendLog("StrictMode 已启用")
        } catch (e: Exception) {
            appendLog("启用失败: ${e.message}")
        }
    }

    private fun disableStrictMode() {
        try {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX)
            StrictMode.setVmPolicy(StrictMode.VmPolicy.LAX)
            
            appendLog("StrictMode 已禁用")
        } catch (e: Exception) {
            appendLog("禁用失败: ${e.message}")
        }
    }

    private fun demonstrateTrace() {
        try {
            appendLog("开始 Trace 演示...")
            
            // 开始追踪一个代码段
            Trace.beginSection("CustomOperation")
            
            // 模拟操作
            Thread.sleep(100)
            
            // 嵌套追踪
            Trace.beginSection("SubOperation")
            Thread.sleep(50)
            Trace.endSection() // 结束 SubOperation
            
            Thread.sleep(50)
            Trace.endSection() // 结束 CustomOperation
            
            appendLog("Trace 完成 (查看 Systrace/Perfetto)")
            appendLog("追踪的代码段会显示在性能分析工具中")
        } catch (e: Exception) {
            appendLog("Trace 失败: ${e.message}")
        }
    }

    private fun appendLog(log: String) {
        val currentText = binding.tvLog.text.toString()
        val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
        val newLog = "[$time] $log"
        val newText = if (currentText.isEmpty()) newLog else "$currentText\n$newLog"
        binding.tvLog.text = newText
        
        binding.scrollView.post {
            binding.scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun formatKB(bytes: Long): String {
        return String.format("%,d KB", bytes / 1024)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = DebugFragment()
    }
}