package com.peter.os.demo.fragments

import android.os.Bundle
import android.os.FileObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.peter.os.demo.R
import com.peter.os.demo.databinding.FragmentFileObserverBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * FileObserver 文件观察示例
 * 
 * 监听文件或目录的变化事件：
 * - CREATE: 文件创建
 * - DELETE: 文件删除
 * - MODIFY: 文件修改
 * - ACCESS: 文件访问
 * - OPEN: 文件打开
 * - CLOSE_WRITE/CLOSE_NOWRITE: 文件关闭
 * - MOVED_FROM/MOVED_TO: 文件移动
 */
class FileObserverFragment : Fragment() {

    private var _binding: FragmentFileObserverBinding? = null
    private val binding get() = _binding!!
    
    private var fileObserver: FileObserver? = null
    private var isWatching = false
    private var watchPath: String? = null
    
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFileObserverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        updateStatus()
    }

    private fun setupViews() {
        // 选择监听路径 - 默认使用应用缓存目录
        binding.btnSelectPath.setOnClickListener {
            // 使用应用缓存目录作为默认监听路径
            watchPath = requireContext().cacheDir.absolutePath
            binding.tvWatchPath.text = watchPath
            Toast.makeText(requireContext(), "已选择: $watchPath", Toast.LENGTH_SHORT).show()
        }

        // 开始监听
        binding.btnStartWatch.setOnClickListener {
            val path = watchPath ?: requireContext().cacheDir.absolutePath
            startWatching(path)
        }

        // 停止监听
        binding.btnStopWatch.setOnClickListener {
            stopWatching()
        }

        // 清除日志
        binding.btnClearLog.setOnClickListener {
            binding.tvLog.text = ""
        }

        // 测试按钮 - 创建文件
        binding.btnTestCreate.setOnClickListener {
            testCreateFile()
        }

        // 测试按钮 - 修改文件
        binding.btnTestModify.setOnClickListener {
            testModifyFile()
        }

        // 测试按钮 - 删除文件
        binding.btnTestDelete.setOnClickListener {
            testDeleteFile()
        }

        // 默认设置路径
        watchPath = requireContext().cacheDir.absolutePath
        binding.tvWatchPath.text = watchPath
    }

    private fun startWatching(path: String) {
        if (isWatching) {
            stopWatching()
        }

        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }

        // 创建 FileObserver
        // 监听的事件类型
        val events = FileObserver.CREATE or 
                     FileObserver.DELETE or 
                     FileObserver.MODIFY or 
                     FileObserver.ACCESS or
                     FileObserver.OPEN or
                     FileObserver.CLOSE_WRITE or
                     FileObserver.MOVED_FROM or
                     FileObserver.MOVED_TO

        fileObserver = object : FileObserver(file, events) {
            override fun onEvent(event: Int, path: String?) {
                val eventName = getEventName(event)
                val time = dateFormat.format(Date())
                val log = "[$time] $eventName: ${path ?: "null"}"
                
                activity?.runOnUiThread {
                    appendLog(log)
                }
            }
        }

        fileObserver?.startWatching()
        isWatching = true
        watchPath = path
        binding.tvWatchPath.text = path
        updateStatus()
        appendLog("开始监听: $path")
    }

    private fun stopWatching() {
        fileObserver?.stopWatching()
        fileObserver = null
        isWatching = false
        updateStatus()
        appendLog("停止监听")
    }

    private fun updateStatus() {
        if (isWatching) {
            binding.tvStatus.text = getString(R.string.file_observer_watching)
            binding.viewStatusDot.setBackgroundResource(R.drawable.bg_status_active)
            binding.btnStartWatch.isEnabled = false
            binding.btnStopWatch.isEnabled = true
        } else {
            binding.tvStatus.text = getString(R.string.file_observer_not_watching)
            binding.viewStatusDot.setBackgroundResource(R.drawable.bg_status_inactive)
            binding.btnStartWatch.isEnabled = true
            binding.btnStopWatch.isEnabled = false
        }
    }

    private fun getEventName(event: Int): String {
        return when (event and 0x0000FFFF) {  // 取低16位
            FileObserver.ACCESS -> getString(R.string.file_observer_event_access)
            FileObserver.MODIFY -> getString(R.string.file_observer_event_modify)
            FileObserver.CREATE -> getString(R.string.file_observer_event_create)
            FileObserver.DELETE -> getString(R.string.file_observer_event_delete)
            FileObserver.OPEN -> getString(R.string.file_observer_event_open)
            FileObserver.CLOSE_WRITE -> "${getString(R.string.file_observer_event_close)} (写入)"
            FileObserver.CLOSE_NOWRITE -> "${getString(R.string.file_observer_event_close)} (只读)"
            FileObserver.DELETE_SELF -> "删除自身"
            FileObserver.MOVE_SELF -> "移动自身"
            FileObserver.MOVED_FROM -> "${getString(R.string.file_observer_event_moved)} (源)"
            FileObserver.MOVED_TO -> "${getString(R.string.file_observer_event_moved)} (目标)"
            FileObserver.ATTRIB -> "属性修改"
            else -> "未知事件 ($event)"
        }
    }

    private fun appendLog(log: String) {
        val currentText = binding.tvLog.text.toString()
        val newText = if (currentText.isEmpty()) log else "$currentText\n$log"
        binding.tvLog.text = newText
        
        binding.scrollView.post {
            binding.scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    // 测试方法
    private fun testCreateFile() {
        val dir = File(watchPath ?: requireContext().cacheDir.absolutePath)
        val testFile = File(dir, "test_${System.currentTimeMillis()}.txt")
        testFile.writeText("Test content")
        appendLog("测试: 创建文件 ${testFile.name}")
    }

    private fun testModifyFile() {
        val dir = File(watchPath ?: requireContext().cacheDir.absolutePath)
        val files = dir.listFiles()?.filter { it.name.startsWith("test_") }
        if (!files.isNullOrEmpty()) {
            files.first().appendText("\nModified at ${System.currentTimeMillis()}")
            appendLog("测试: 修改文件 ${files.first().name}")
        } else {
            appendLog("测试: 没有可修改的测试文件")
        }
    }

    private fun testDeleteFile() {
        val dir = File(watchPath ?: requireContext().cacheDir.absolutePath)
        val files = dir.listFiles()?.filter { it.name.startsWith("test_") }
        if (!files.isNullOrEmpty()) {
            val toDelete = files.first()
            toDelete.delete()
            appendLog("测试: 删除文件 ${toDelete.name}")
        } else {
            appendLog("测试: 没有可删除的测试文件")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopWatching()
        _binding = null
    }

    companion object {
        fun newInstance() = FileObserverFragment()
    }
}
