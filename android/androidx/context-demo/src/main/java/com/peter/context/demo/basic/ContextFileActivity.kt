package com.peter.context.demo.basic

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.peter.context.demo.databinding.ActivityContextFileBinding
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader

/**
 * Context 文件操作示例
 * 
 * Android 文件存储分为：
 * 1. 内部存储 - 私有，不需要权限
 * 2. 外部存储 - 公共，需要权限
 * 3. 缓存目录 - 临时文件，系统可能自动清理
 * 
 * Context 提供的文件操作方法：
 * - openFileInput / openFileOutput - 内部存储
 * - getFileStreamPath - 获取内部存储文件路径
 * - getFilesDir - 获取内部存储目录
 * - getCacheDir - 获取内部缓存目录
 * - getExternalFilesDir - 获取外部存储私有目录
 * - getExternalCacheDir - 获取外部缓存目录
 * - deleteFile - 删除文件
 * - fileList - 列出所有内部文件
 */
class ContextFileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContextFileBinding
    private val sb = StringBuilder()
    
    private val FILE_NAME = "demo_file.txt"
    private val FILE_NAME_PRIVATE = "private_file.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContextFileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showStorageInfo()
    }

    private fun setupListeners() {
        binding.btnInternalWrite.setOnClickListener { writeInternalFile() }
        binding.btnInternalRead.setOnClickListener { readInternalFile() }
        binding.btnInternalDelete.setOnClickListener { deleteInternalFile() }
        binding.btnInternalList.setOnClickListener { listInternalFiles() }
        
        binding.btnCacheWrite.setOnClickListener { writeCacheFile() }
        binding.btnCacheRead.setOnClickListener { readCacheFile() }
        
        binding.btnExternalWrite.setOnClickListener { writeExternalFile() }
        binding.btnExternalRead.setOnClickListener { readExternalFile() }
    }

    private fun showStorageInfo() {
        sb.clear()
        
        sb.appendLine("=== Android 文件存储概述 ===\n")
        
        // 1. 内部存储
        sb.appendLine("=== 1. 内部存储 ===")
        val filesDir = filesDir
        sb.appendLine("filesDir: $filesDir")
        sb.appendLine("路径: /data/data/$packageName/files/")
        sb.appendLine("特点:")
        sb.appendLine("  ✓ 始终可用，不需要权限")
        sb.appendLine("  ✓ 只有本应用可以访问")
        sb.appendLine("  ✓ 卸载应用时删除")
        sb.appendLine("  ✓ 用户无法直接访问（需要 root）")
        sb.appendLine()
        
        // 2. 缓存目录
        sb.appendLine("=== 2. 缓存目录 ===")
        val cacheDir = cacheDir
        sb.appendLine("cacheDir: $cacheDir")
        sb.appendLine("路径: /data/data/$packageName/cache/")
        sb.appendLine("特点:")
        sb.appendLine("  ✓ 存储临时文件")
        sb.appendLine("  ✓ 系统空间不足时可能自动清理")
        sb.appendLine("  ✓ 不保证一定会被清理")
        sb.appendLine("  ✓ 应该自己管理缓存大小")
        sb.appendLine()
        
        // 3. 外部存储
        sb.appendLine("=== 3. 外部存储 ===")
        val externalFilesDir = getExternalFilesDir(null)
        val externalCacheDir = externalCacheDir
        sb.appendLine("externalFilesDir: $externalFilesDir")
        sb.appendLine("externalCacheDir: $externalCacheDir")
        sb.appendLine("特点:")
        sb.appendLine("  ✓ 可能不可用（SD卡被移除等）")
        sb.appendLine("  ✓ 卸载应用时删除")
        sb.appendLine("  ✓ Android 10+ 不需要权限访问私有目录")
        sb.appendLine("  ✓ 其他应用可能可以访问")
        sb.appendLine()
        
        // 4. 公共目录
        sb.appendLine("=== 4. 公共目录 ===")
        sb.appendLine("Environment.getExternalStorageDirectory(): ${Environment.getExternalStorageDirectory()}")
        sb.appendLine("Environment.DIRECTORY_DOWNLOADS: ${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}")
        sb.appendLine("特点:")
        sb.appendLine("  ✓ 卸载应用后保留")
        sb.appendLine("  ✓ 需要 READ/WRITE_EXTERNAL_STORAGE 权限")
        sb.appendLine("  ✓ Android 10+ 使用 MediaStore/SAF")
        sb.appendLine()
        
        // 5. 存储容量
        sb.appendLine("=== 5. 存储容量 ===")
        val filesDirStat = android.os.StatFs(filesDir.path)
        val internalTotal = filesDirStat.totalBytes
        val internalFree = filesDirStat.availableBytes
        
        sb.appendLine("内部存储:")
        sb.appendLine("  总容量: ${formatSize(internalTotal)}")
        sb.appendLine("  可用空间: ${formatSize(internalFree)}")
        
        if (externalFilesDir != null) {
            val externalStat = android.os.StatFs(externalFilesDir.path)
            val externalTotal = externalStat.totalBytes
            val externalFree = externalStat.availableBytes
            sb.appendLine("外部存储:")
            sb.appendLine("  总容量: ${formatSize(externalTotal)}")
            sb.appendLine("  可用空间: ${formatSize(externalFree)}")
        }
        sb.appendLine()
        
        binding.tvInfo.text = sb.toString()
    }

    private fun writeInternalFile() {
        sb.clear()
        sb.appendLine("=== 写入内部存储文件 ===\n")
        
        val content = "Hello, Internal Storage!\n时间: ${System.currentTimeMillis()}"
        
        // 方式1: 使用 openFileOutput (Context 方法)
        sb.appendLine("方式1: openFileOutput()")
        openFileOutput(FILE_NAME, Context.MODE_PRIVATE).use { fos ->
            fos.write(content.toByteArray())
        }
        sb.appendLine("已写入文件: $FILE_NAME")
        
        // MODE 说明
        sb.appendLine("\n模式说明:")
        sb.appendLine("MODE_PRIVATE: 覆盖原文件")
        sb.appendLine("MODE_APPEND: 追加到文件末尾")
        sb.appendLine()
        
        // 方式2: 直接创建 File
        sb.appendLine("方式2: 直接使用 File 对象")
        val file = File(filesDir, FILE_NAME_PRIVATE)
        file.writeText("Private file content")
        sb.appendLine("已写入文件: ${file.absolutePath}")
        
        // 文件路径
        val filePath = getFileStreamPath(FILE_NAME)
        sb.appendLine("\n文件路径: $filePath")
        sb.appendLine("文件大小: ${filePath.length()} bytes")
        
        Toast.makeText(this, "写入成功", Toast.LENGTH_SHORT).show()
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextFile", sb.toString())
    }

    private fun readInternalFile() {
        sb.clear()
        sb.appendLine("=== 读取内部存储文件 ===\n")
        
        // 方式1: 使用 openFileInput
        sb.appendLine("方式1: openFileInput()")
        try {
            openFileInput(FILE_NAME).use { fis ->
                val reader = BufferedReader(InputStreamReader(fis))
                val content = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    content.appendLine(line)
                }
                sb.appendLine("内容:\n$content")
            }
        } catch (e: Exception) {
            sb.appendLine("读取失败: ${e.message}")
        }
        
        // 方式2: 直接读取 File
        sb.appendLine("\n方式2: 直接读取 File 对象")
        val file = File(filesDir, FILE_NAME_PRIVATE)
        if (file.exists()) {
            sb.appendLine("内容: ${file.readText()}")
        } else {
            sb.appendLine("文件不存在")
        }
        
        // 方式3: 获取文件路径后读取
        sb.appendLine("\n方式3: getFileStreamPath()")
        val filePath = getFileStreamPath(FILE_NAME)
        if (filePath.exists()) {
            sb.appendLine("文件存在: ${filePath.absolutePath}")
            sb.appendLine("最后修改: ${java.util.Date(filePath.lastModified())}")
        }
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextFile", sb.toString())
    }

    private fun deleteInternalFile() {
        sb.clear()
        sb.appendLine("=== 删除内部存储文件 ===\n")
        
        // 方式1: Context.deleteFile()
        val deleted = deleteFile(FILE_NAME)
        sb.appendLine("deleteFile(\"$FILE_NAME\"): $deleted")
        
        // 方式2: File.delete()
        val file = File(filesDir, FILE_NAME_PRIVATE)
        if (file.exists()) {
            val fileDeleted = file.delete()
            sb.appendLine("file.delete(): $fileDeleted")
        }
        
        Toast.makeText(this, "删除完成", Toast.LENGTH_SHORT).show()
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextFile", sb.toString())
    }

    private fun listInternalFiles() {
        sb.clear()
        sb.appendLine("=== 列出内部存储文件 ===\n")
        
        // 方式1: Context.fileList()
        val files = fileList()
        sb.appendLine("fileList() 返回 ${files.size} 个文件:")
        files.forEach { sb.appendLine("  - $it") }
        
        // 方式2: File.listFiles()
        sb.appendLine("\nfilesDir.listFiles():")
        filesDir.listFiles()?.forEach { 
            sb.appendLine("  - ${it.name} (${formatSize(it.length())})")
        }
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextFile", sb.toString())
    }

    private fun writeCacheFile() {
        sb.clear()
        sb.appendLine("=== 写入缓存文件 ===\n")
        
        val cacheFile = File(cacheDir, "temp_cache_${System.currentTimeMillis()}.tmp")
        cacheFile.writeText("Temporary cache data")
        
        sb.appendLine("缓存文件: ${cacheFile.absolutePath}")
        sb.appendLine("文件大小: ${cacheFile.length()} bytes")
        
        // 检查缓存大小
        val cacheSize = cacheDir.walkTopDown()
            .filter { it.isFile }
            .map { it.length() }
            .sum()
        sb.appendLine("\n总缓存大小: ${formatSize(cacheSize)}")
        sb.appendLine("\n建议定期清理缓存，保持合理大小")
        
        Toast.makeText(this, "缓存写入成功", Toast.LENGTH_SHORT).show()
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextFile", sb.toString())
    }

    private fun readCacheFile() {
        sb.clear()
        sb.appendLine("=== 读取缓存文件 ===\n")
        
        val cacheFiles = cacheDir.listFiles()
        if (cacheFiles.isNullOrEmpty()) {
            sb.appendLine("缓存目录为空")
        } else {
            sb.appendLine("缓存文件列表:")
            cacheFiles.forEach { file ->
                sb.appendLine("  ${file.name} (${formatSize(file.length())})")
            }
        }
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextFile", sb.toString())
    }

    private fun writeExternalFile() {
        sb.clear()
        sb.appendLine("=== 写入外部存储文件 ===\n")
        
        // 检查外部存储是否可用
        val state = Environment.getExternalStorageState()
        if (state != Environment.MEDIA_MOUNTED) {
            sb.appendLine("外部存储不可用: $state")
            binding.tvResult.text = sb.toString()
            return
        }
        
        // 获取外部存储私有目录
        val externalDir = getExternalFilesDir(null)
        if (externalDir == null) {
            sb.appendLine("无法获取外部存储目录")
            binding.tvResult.text = sb.toString()
            return
        }
        
        sb.appendLine("外部存储目录: $externalDir")
        
        // 写入文件
        val file = File(externalDir, "external_file.txt")
        file.writeText("External storage content\n时间: ${System.currentTimeMillis()}")
        
        sb.appendLine("已写入文件: ${file.absolutePath}")
        sb.appendLine("\n注意:")
        sb.appendLine("- Android 10+ 访问此目录不需要权限")
        sb.appendLine("- 此目录在应用卸载时会被删除")
        sb.appendLine("- 其他应用可能可以访问")
        
        // 特定类型目录
        sb.appendLine("\n特定类型目录:")
        val dirs = listOf(
            Environment.DIRECTORY_DOWNLOADS to "下载",
            Environment.DIRECTORY_PICTURES to "图片",
            Environment.DIRECTORY_MUSIC to "音乐",
            Environment.DIRECTORY_MOVIES to "视频",
            Environment.DIRECTORY_DOCUMENTS to "文档"
        )
        dirs.forEach { (type, name) ->
            val dir = getExternalFilesDir(type)
            sb.appendLine("  $name: $dir")
        }
        
        Toast.makeText(this, "外部存储写入成功", Toast.LENGTH_SHORT).show()
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextFile", sb.toString())
    }

    private fun readExternalFile() {
        sb.clear()
        sb.appendLine("=== 读取外部存储文件 ===\n")
        
        val externalDir = getExternalFilesDir(null)
        if (externalDir == null) {
            sb.appendLine("外部存储不可用")
            binding.tvResult.text = sb.toString()
            return
        }
        
        val file = File(externalDir, "external_file.txt")
        if (file.exists()) {
            sb.appendLine("文件内容:")
            sb.appendLine(file.readText())
        } else {
            sb.appendLine("文件不存在")
        }
        
        // 列出外部存储文件
        sb.appendLine("\n外部存储文件列表:")
        externalDir.listFiles()?.forEach {
            sb.appendLine("  ${it.name} (${formatSize(it.length())})")
        }
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextFile", sb.toString())
    }

    private fun formatSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> String.format("%.2f KB", bytes / 1024.0)
            bytes < 1024 * 1024 * 1024 -> String.format("%.2f MB", bytes / (1024.0 * 1024))
            else -> String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024))
        }
    }
}
