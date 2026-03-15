package com.peter.os.demo.fragments

import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.os.demo.databinding.FragmentStorageBinding
import java.io.File

/**
 * Environment/StatFs 存储路径示例
 * 
 * Environment: 获取系统标准存储路径
 * - getExternalStorageDirectory(): 外部存储根目录
 * - getDataDirectory(): 数据目录
 * - getDownloadCacheDirectory(): 下载缓存目录
 * - getRootDirectory(): 系统根目录
 * - getExternalStoragePublicDirectory(type): 公共目录
 * 
 * StatFs: 获取存储空间信息
 * - getTotalBytes(): 总空间
 * - getAvailableBytes(): 可用空间
 * - getFreeBytes(): 空闲空间
 */
class StorageFragment : Fragment() {

    private var _binding: FragmentStorageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStorageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        loadStorageInfo()
    }

    private fun setupViews() {
        binding.btnRefresh.setOnClickListener {
            loadStorageInfo()
        }
    }

    private fun loadStorageInfo() {
        val sb = StringBuilder()
        
        // 外部存储状态
        sb.appendLine("=== 外部存储状态 ===")
        val externalState = Environment.getExternalStorageState()
        sb.appendLine("状态: ${getStorageStateText(externalState)}")
        sb.appendLine()
        
        // 基本目录
        sb.appendLine("=== 系统目录 ===")
        sb.appendLine("外部存储: ${Environment.getExternalStorageDirectory().absolutePath}")
        sb.appendLine("数据目录: ${Environment.getDataDirectory().absolutePath}")
        sb.appendLine("下载缓存: ${Environment.getDownloadCacheDirectory().absolutePath}")
        sb.appendLine("根目录: ${Environment.getRootDirectory().absolutePath}")
        sb.appendLine()
        
        // 公共目录
        sb.appendLine("=== 公共目录 ===")
        sb.appendLine("Downloads: ${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath}")
        sb.appendLine("DCIM: ${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}")
        sb.appendLine("Pictures: ${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath}")
        sb.appendLine("Music: ${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath}")
        sb.appendLine("Movies: ${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath}")
        sb.appendLine("Documents: ${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath}")
        sb.appendLine()
        
        // 存储空间信息
        sb.appendLine("=== 外部存储空间 ===")
        try {
            val externalPath = Environment.getExternalStorageDirectory()
            val statFs = StatFs(externalPath.absolutePath)
            
            val totalBytes = statFs.totalBytes
            val availableBytes = statFs.availableBytes
            val freeBytes = statFs.freeBytes
            
            sb.appendLine("总空间: ${formatSize(totalBytes)}")
            sb.appendLine("可用空间: ${formatSize(availableBytes)}")
            sb.appendLine("空闲空间: ${formatSize(freeBytes)}")
            sb.appendLine("已使用: ${formatSize(totalBytes - availableBytes)}")
            sb.appendLine("使用率: ${String.format("%.1f%%", (totalBytes - availableBytes) * 100.0 / totalBytes)}")
        } catch (e: Exception) {
            sb.appendLine("无法获取存储空间: ${e.message}")
        }
        sb.appendLine()
        
        // 应用内部存储
        sb.appendLine("=== 应用内部存储 ===")
        val filesDir = requireContext().filesDir
        val cacheDir = requireContext().cacheDir
        val externalFilesDir = requireContext().getExternalFilesDir(null)
        val externalCacheDir = requireContext().externalCacheDir
        
        sb.appendLine("内部文件目录: ${filesDir?.absolutePath}")
        sb.appendLine("内部缓存目录: ${cacheDir?.absolutePath}")
        sb.appendLine("外部文件目录: ${externalFilesDir?.absolutePath}")
        sb.appendLine("外部缓存目录: ${externalCacheDir?.absolutePath}")
        sb.appendLine()
        
        // 内部存储空间
        sb.appendLine("=== 内部存储空间 ===")
        try {
            val internalStatFs = StatFs(filesDir.absolutePath)
            sb.appendLine("总空间: ${formatSize(internalStatFs.totalBytes)}")
            sb.appendLine("可用空间: ${formatSize(internalStatFs.availableBytes)}")
        } catch (e: Exception) {
            sb.appendLine("无法获取内部存储空间: ${e.message}")
        }
        
        binding.tvStorageInfo.text = sb.toString()
    }

    private fun getStorageStateText(state: String): String {
        return when (state) {
            Environment.MEDIA_MOUNTED -> "已挂载 (可读写)"
            Environment.MEDIA_MOUNTED_READ_ONLY -> "已挂载 (只读)"
            Environment.MEDIA_UNMOUNTED -> "未挂载"
            Environment.MEDIA_REMOVED -> "已移除"
            Environment.MEDIA_BAD_REMOVAL -> "异常移除"
            Environment.MEDIA_NOFS -> "无文件系统"
            Environment.MEDIA_CHECKING -> "检查中"
            Environment.MEDIA_SHARED -> "共享中"
            else -> state
        }
    }

    private fun formatSize(bytes: Long): String {
        val kb = 1024L
        val mb = kb * 1024
        val gb = mb * 1024
        
        return when {
            bytes >= gb -> String.format("%.2f GB", bytes.toDouble() / gb)
            bytes >= mb -> String.format("%.2f MB", bytes.toDouble() / mb)
            bytes >= kb -> String.format("%.2f KB", bytes.toDouble() / kb)
            else -> "$bytes B"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = StorageFragment()
    }
}
