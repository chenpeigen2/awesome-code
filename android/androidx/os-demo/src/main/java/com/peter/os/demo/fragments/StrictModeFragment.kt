package com.peter.os.demo.fragments

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.peter.os.demo.databinding.FragmentStrictModeBinding

/**
 * StrictMode 严格模式示例
 * 
 * StrictMode: 检测主线程上的不当操作
 * - 线程策略: 检测主线程IO、网络等耗时操作
 * - VM策略: 检测内存泄漏、Activity泄漏等
 * - 惩罚方式: 日志、对话框、崩溃、静默
 * 
 * 常用检测项:
 * - DETECT_DISK_READ: 磁盘读取
 * - DETECT_DISK_WRITE: 磁盘写入
 * - DETECT_NETWORK: 网络操作
 * - DETECT_RESOURCE_MISMATCH: 资源不匹配
 */
class StrictModeFragment : Fragment() {

    private var _binding: FragmentStrictModeBinding? = null
    private val binding get() = _binding!!
    
    private var isStrictModeEnabled = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStrictModeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        updateStatus()
    }

    private fun setupViews() {
        // 启用/禁用 StrictMode
        binding.btnToggleStrictMode.setOnClickListener {
            if (isStrictModeEnabled) {
                disableStrictMode()
            } else {
                enableStrictMode()
            }
        }

        // 测试磁盘读取
        binding.btnTestDiskRead.setOnClickListener {
            testDiskRead()
        }

        // 测试磁盘写入
        binding.btnTestDiskWrite.setOnClickListener {
            testDiskWrite()
        }

        // 测试网络操作
        binding.btnTestNetwork.setOnClickListener {
            testNetwork()
        }

        // 测试关闭资源
        binding.btnTestCloseable.setOnClickListener {
            testCloseableLeak()
        }

        // 清除日志
        binding.btnClearLog.setOnClickListener {
            binding.tvLog.text = ""
        }
    }

    private fun enableStrictMode() {
        // 线程策略 - 检测主线程耗时操作
        val threadPolicy = StrictMode.ThreadPolicy.Builder()
            .detectDiskReads()           // 检测磁盘读取
            .detectDiskWrites()          // 检测磁盘写入
            .detectNetwork()             // 检测网络操作
            .detectResourceMismatches()  // 检测资源不匹配 (API 23+)
            .penaltyLog()                // 违规时输出日志
            .penaltyFlashScreen()        // 违规时屏幕闪烁
            .build()

        // VM策略 - 检测内存相关问题
        val vmPolicy = StrictMode.VmPolicy.Builder()
            .detectLeakedSqlLiteObjects()      // 检测 SQLite 对象泄漏
            .detectLeakedClosableObjects()     // 检测 Closeable 对象泄漏
            .detectLeakedRegistrationObjects() // 检测注册对象泄漏 (API 16+)
            .detectActivityLeaks()             // 检测 Activity 泄漏 (API 11+)
            .penaltyLog()                      // 违规时输出日志
            .build()

        StrictMode.setThreadPolicy(threadPolicy)
        StrictMode.setVmPolicy(vmPolicy)
        
        isStrictModeEnabled = true
        updateStatus()
        appendLog("StrictMode 已启用")
        appendLog("线程策略: 磁盘读取/写入, 网络, 资源不匹配")
        appendLog("VM策略: SQLite泄漏, Closeable泄漏, Activity泄漏")
    }

    private fun disableStrictMode() {
        // 恢复默认策略
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX)
        StrictMode.setVmPolicy(StrictMode.VmPolicy.LAX)
        
        isStrictModeEnabled = false
        updateStatus()
        appendLog("StrictMode 已禁用")
    }

    private fun updateStatus() {
        if (isStrictModeEnabled) {
            binding.tvStatus.text = "StrictMode: 已启用"
            binding.viewStatusDot.setBackgroundResource(android.R.color.holo_green_dark)
            binding.btnToggleStrictMode.text = "禁用 StrictMode"
        } else {
            binding.tvStatus.text = "StrictMode: 已禁用"
            binding.viewStatusDot.setBackgroundResource(android.R.color.darker_gray)
            binding.btnToggleStrictMode.text = "启用 StrictMode"
        }
    }

    /**
     * 测试磁盘读取
     * 在主线程读取文件会触发 StrictMode
     */
    private fun testDiskRead() {
        appendLog("测试: 磁盘读取...")
        try {
            // 在主线程读取文件
            val file = requireContext().cacheDir
            val files = file.listFiles()
            appendLog("读取到 ${files?.size ?: 0} 个文件")
        } catch (e: Exception) {
            appendLog("异常: ${e.message}")
        }
    }

    /**
     * 测试磁盘写入
     * 在主线程写入文件会触发 StrictMode
     */
    private fun testDiskWrite() {
        appendLog("测试: 磁盘写入...")
        try {
            // 在主线程写入文件
            val file = java.io.File(requireContext().cacheDir, "strictmode_test.txt")
            file.writeText("StrictMode test at ${System.currentTimeMillis()}")
            appendLog("写入成功: ${file.name}")
        } catch (e: Exception) {
            appendLog("异常: ${e.message}")
        }
    }

    /**
     * 测试网络操作
     * 在主线程执行网络请求会触发 StrictMode
     */
    private fun testNetwork() {
        appendLog("测试: 网络操作...")
        appendLog("注意: 实际网络请求需要在线程执行，这里仅演示")
        // 实际网络操作会在后台线程执行
        Thread {
            try {
                val url = java.net.URL("https://www.google.com")
                val connection = url.openConnection()
                connection.connectTimeout = 5000
                connection.inputStream.close()
                activity?.runOnUiThread {
                    appendLog("网络请求成功 (后台线程)")
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    appendLog("网络异常: ${e.message}")
                }
            }
        }.start()
    }

    /**
     * 测试 Closeable 泄漏
     * 不关闭 Closeable 对象会触发 VM 策略
     */
    @Suppress("IOUnmanagedOpenClose")
    private fun testCloseableLeak() {
        appendLog("测试: Closeable 泄漏...")
        try {
            // 故意不关闭流
            val input = requireContext().openFileInput("test.txt")
            // 不调用 input.close()
            appendLog("创建了未关闭的 InputStream (查看 Logcat)")
            appendLog("VM 策略会在 GC 时检测到泄漏")
        } catch (e: Exception) {
            // 文件不存在是正常的
            appendLog("文件不存在 (正常)")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = StrictModeFragment()
    }
}
