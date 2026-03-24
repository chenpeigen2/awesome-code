package com.peter.file.demo.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.file.demo.FileAdapter
import com.peter.file.demo.FileItem
import com.peter.file.demo.FileOperationType
import com.peter.file.demo.MainActivity
import com.peter.file.demo.R
import com.peter.file.demo.databinding.FragmentInternalBinding
import kotlinx.coroutines.launch

/**
 * 内部存储 Fragment
 */
class InternalFragment : Fragment() {

    private var _binding: FragmentInternalBinding? = null
    private val binding get() = _binding!!

    private val tabPosition = 0

    companion object {
        fun newInstance() = InternalFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInternalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = createFileItems()
        val mainActivity = requireActivity() as MainActivity
        val adapter = FileAdapter(
            items = items,
            onItemClick = { type -> handleOperation(type) },
            tabColorRes = mainActivity.getTabColor(tabPosition),
            dotDrawableRes = mainActivity.getTabDotDrawable(tabPosition)
        )
        binding.recyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun createFileItems(): List<FileItem> {
        return listOf(
            FileItem(
                type = FileOperationType.INTERNAL_WRITE,
                title = getString(R.string.internal_write),
                description = getString(R.string.internal_write_desc)
            ),
            FileItem(
                type = FileOperationType.INTERNAL_READ,
                title = getString(R.string.internal_read),
                description = getString(R.string.internal_read_desc)
            ),
            FileItem(
                type = FileOperationType.INTERNAL_LIST,
                title = getString(R.string.internal_list),
                description = getString(R.string.internal_list_desc)
            ),
            FileItem(
                type = FileOperationType.INTERNAL_DELETE,
                title = getString(R.string.internal_delete),
                description = getString(R.string.internal_delete_desc)
            ),
            FileItem(
                type = FileOperationType.INTERNAL_CACHE_WRITE,
                title = getString(R.string.internal_cache_write),
                description = getString(R.string.internal_cache_write_desc)
            ),
            FileItem(
                type = FileOperationType.INTERNAL_CACHE_READ,
                title = getString(R.string.internal_cache_read),
                description = getString(R.string.internal_cache_read_desc)
            ),
            FileItem(
                type = FileOperationType.INTERNAL_INFO,
                title = getString(R.string.internal_info),
                description = getString(R.string.internal_info_desc)
            ),
            FileItem(
                type = FileOperationType.INTERNAL_CREATE_DIR,
                title = getString(R.string.internal_create_dir),
                description = getString(R.string.internal_create_dir_desc)
            ),
            FileItem(
                type = FileOperationType.INTERNAL_LIST_DIRS,
                title = getString(R.string.internal_list_dirs),
                description = getString(R.string.internal_list_dirs_desc)
            ),
            FileItem(
                type = FileOperationType.INTERNAL_DELETE_DIR,
                title = getString(R.string.internal_delete_dir),
                description = getString(R.string.internal_delete_dir_desc)
            ),
            FileItem(
                type = FileOperationType.INTERNAL_COPY_FILE,
                title = getString(R.string.internal_copy_file),
                description = getString(R.string.internal_copy_file_desc)
            ),
            FileItem(
                type = FileOperationType.INTERNAL_MOVE_FILE,
                title = getString(R.string.internal_move_file),
                description = getString(R.string.internal_move_file_desc)
            ),
            FileItem(
                type = FileOperationType.INTERNAL_APPEND,
                title = getString(R.string.internal_append),
                description = getString(R.string.internal_append_desc)
            ),
            FileItem(
                type = FileOperationType.INTERNAL_WRITE_BYTES,
                title = getString(R.string.internal_write_bytes),
                description = getString(R.string.internal_write_bytes_desc)
            ),
            FileItem(
                type = FileOperationType.INTERNAL_READ_BYTES,
                title = getString(R.string.internal_read_bytes),
                description = getString(R.string.internal_read_bytes_desc)
            )
        )
    }

    private fun handleOperation(type: FileOperationType) {
        val mainActivity = requireActivity() as MainActivity
        val fileHelper = mainActivity.fileHelper

        when (type) {
            FileOperationType.INTERNAL_WRITE -> {
                showWriteDialog(isCache = false) { fileName, content ->
                    lifecycleScope.launch {
                        val result = fileHelper.writeInternalFile(fileName, content)
                        showMessage(result.getOrNull() ?: result.exceptionOrNull()?.message ?: "写入失败")
                    }
                }
            }

            FileOperationType.INTERNAL_READ -> {
                showReadFileNameDialog(isCache = false) { fileName ->
                    lifecycleScope.launch {
                        val result = fileHelper.readInternalFile(fileName)
                        if (result.isSuccess) {
                            showContentDialog(fileName, result.getOrNull() ?: "")
                        } else {
                            showMessage(result.exceptionOrNull()?.message ?: "读取失败")
                        }
                    }
                }
            }

            FileOperationType.INTERNAL_LIST -> {
                val files = fileHelper.listInternalFiles()
                if (files.isEmpty()) {
                    showMessage("没有文件")
                } else {
                    val fileNames = files.map {
                        "${it.name} (${fileHelper.formatFileSize(it.length())})"
                    }
                    showFileListDialog("内部存储文件", fileNames)
                }
            }

            FileOperationType.INTERNAL_DELETE -> showDeleteFileDialog { fileName ->
                val deleted = fileHelper.deleteInternalFile(fileName)
                showMessage(if (deleted) "文件已删除" else "删除失败")
            }

            FileOperationType.INTERNAL_CACHE_WRITE -> {
                showWriteDialog(isCache = true) { fileName, content ->
                    lifecycleScope.launch {
                        val result = fileHelper.writeCacheFile(fileName, content)
                        showMessage(result.getOrNull() ?: result.exceptionOrNull()?.message ?: "写入失败")
                    }
                }
            }

            FileOperationType.INTERNAL_CACHE_READ -> {
                showReadFileNameDialog(isCache = true) { fileName ->
                    lifecycleScope.launch {
                        val result = fileHelper.readCacheFile(fileName)
                        if (result.isSuccess) {
                            showContentDialog(fileName, result.getOrNull() ?: "")
                        } else {
                            showMessage(result.exceptionOrNull()?.message ?: "读取失败")
                        }
                    }
                }
            }

            FileOperationType.INTERNAL_INFO -> {
                showReadFileNameDialog(isCache = false) { fileName ->
                    val info = fileHelper.getFileInfo(fileName)
                    if (info != null) {
                        val infoText = """
                            文件名: ${info.name}
                            路径: ${info.path}
                            大小: ${fileHelper.formatFileSize(info.size)}
                            修改时间: ${info.lastModified}
                            类型: ${if (info.isDirectory) "目录" else "文件"}
                        """.trimIndent()
                        showContentDialog("文件信息", infoText)
                    } else {
                        showMessage("文件不存在")
                    }
                }
            }

            FileOperationType.INTERNAL_CREATE_DIR -> {
                showDirectoryNameDialog("创建目录") { dirName ->
                    val success = fileHelper.createDirectory(dirName)
                    showMessage(if (success) "目录已创建: $dirName" else "目录创建失败或已存在")
                }
            }

            FileOperationType.INTERNAL_LIST_DIRS -> {
                val dirs = fileHelper.listDirectories()
                if (dirs.isEmpty()) {
                    showMessage("没有目录")
                } else {
                    val dirNames = dirs.map {
                        "${it.name} (${fileHelper.formatFileSize(it.length())})"
                    }
                    showFileListDialog("内部存储目录", dirNames)
                }
            }

            FileOperationType.INTERNAL_DELETE_DIR -> {
                val dirs = fileHelper.listDirectories()
                if (dirs.isEmpty()) {
                    showMessage("没有可删除的目录")
                } else {
                    val dirNames = dirs.map { it.name }
                    AlertDialog.Builder(requireContext())
                        .setTitle("选择要删除的目录")
                        .setItems(dirNames.toTypedArray()) { _, which ->
                            val success = fileHelper.deleteDirectory(dirNames[which])
                            showMessage(if (success) "目录已删除" else "删除失败")
                        }
                        .setNegativeButton("取消", null)
                        .show()
                }
            }

            FileOperationType.INTERNAL_COPY_FILE -> {
                showCopyMoveDialog("复制文件") { sourceName, destName ->
                    lifecycleScope.launch {
                        val result = fileHelper.copyFile(sourceName, destName)
                        showMessage(result.getOrNull() ?: result.exceptionOrNull()?.message ?: "复制失败")
                    }
                }
            }

            FileOperationType.INTERNAL_MOVE_FILE -> {
                showCopyMoveDialog("移动文件") { sourceName, destName ->
                    lifecycleScope.launch {
                        val result = fileHelper.moveFile(sourceName, destName)
                        showMessage(result.getOrNull() ?: result.exceptionOrNull()?.message ?: "移动失败")
                    }
                }
            }

            FileOperationType.INTERNAL_APPEND -> {
                showAppendDialog { fileName, content ->
                    lifecycleScope.launch {
                        val result = fileHelper.appendToFile(fileName, content)
                        showMessage(result.getOrNull() ?: result.exceptionOrNull()?.message ?: "追加失败")
                    }
                }
            }

            FileOperationType.INTERNAL_WRITE_BYTES -> {
                lifecycleScope.launch {
                    // Create sample bytes (0-255 pattern)
                    val bytes = ByteArray(256) { it.toByte() }
                    val fileName = "binary_${System.currentTimeMillis()}.bin"
                    val result = fileHelper.writeBytes(fileName, bytes)
                    showMessage(result.getOrNull() ?: result.exceptionOrNull()?.message ?: "写入失败")
                }
            }

            FileOperationType.INTERNAL_READ_BYTES -> {
                showReadFileNameDialog(isCache = false) { fileName ->
                    lifecycleScope.launch {
                        val result = fileHelper.readBytes(fileName)
                        if (result.isSuccess) {
                            val bytes = result.getOrNull()!!
                            val hexString = bytes.take(128).joinToString(" ") {
                                "%02X".format(it)
                            }
                            val info = """
                                文件大小: ${bytes.size} 字节
                                前128字节 (十六进制):
                                $hexString
                                ${if (bytes.size > 128) "\n..." else ""}
                            """.trimIndent()
                            showContentDialog("二进制内容", info)
                        } else {
                            showMessage(result.exceptionOrNull()?.message ?: "读取失败")
                        }
                    }
                }
            }

            else -> {}
        }
    }

    private fun showWriteDialog(isCache: Boolean = false, onConfirm: (String, String) -> Unit) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_input, null)
        val etFileName = dialogView.findViewById<EditText>(R.id.etFileName)
        val etContent = dialogView.findViewById<EditText>(R.id.etContent)

        etFileName.setText(if (isCache) "cache_${System.currentTimeMillis()}.txt" else "test_${System.currentTimeMillis()}.txt")
        etContent.setText("这是测试内容\n时间: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}")

        AlertDialog.Builder(requireContext())
            .setTitle(if (isCache) "写入缓存文件" else "写入文件")
            .setView(dialogView)
            .setPositiveButton("写入") { _, _ ->
                val fileName = etFileName.text.toString()
                val content = etContent.text.toString()
                if (fileName.isNotBlank() && content.isNotBlank()) {
                    onConfirm(fileName, content)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showReadFileNameDialog(isCache: Boolean = false, onConfirm: (String) -> Unit) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_input_filename, null)
        val etFileName = dialogView.findViewById<EditText>(R.id.etFileName)
        val tvHint = dialogView.findViewById<TextView>(R.id.tvHint)

        val files = if (isCache) {
            requireContext().cacheDir.listFiles()?.map { it.name } ?: emptyList()
        } else {
            requireContext().filesDir.listFiles()?.map { it.name } ?: emptyList()
        }

        if (files.isNotEmpty()) {
            tvHint.text = "现有文件: ${files.take(3).joinToString(", ")}${if (files.size > 3) "..." else ""}"
            etFileName.setText(files.first())
        }

        AlertDialog.Builder(requireContext())
            .setTitle(if (isCache) "读取缓存文件" else "读取文件")
            .setView(dialogView)
            .setPositiveButton("读取") { _, _ ->
                val fileName = etFileName.text.toString()
                if (fileName.isNotBlank()) {
                    onConfirm(fileName)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showDeleteFileDialog(onConfirm: (String) -> Unit) {
        val files = requireContext().filesDir.listFiles()?.map { it.name } ?: emptyList()

        if (files.isEmpty()) {
            showMessage("没有可删除的文件")
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("选择要删除的文件")
            .setItems(files.toTypedArray()) { _, which ->
                onConfirm(files[which])
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showFileListDialog(title: String, files: List<String>) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setItems(files.toTypedArray(), null)
            .setPositiveButton("确定", null)
            .show()
    }

    private fun showContentDialog(title: String, content: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(content)
            .setPositiveButton("确定", null)
            .show()
    }

    private fun showMessage(message: String) {
        (requireActivity() as MainActivity).showSnackbar(message)
    }

    private fun showDirectoryNameDialog(title: String, onConfirm: (String) -> Unit) {
        val layout = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val input = android.widget.EditText(requireContext()).apply {
            hint = "目录名"
            setText("new_folder")
        }
        layout.addView(input)

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(layout)
            .setPositiveButton("创建") { _, _ ->
                val dirName = input.text.toString()
                if (dirName.isNotBlank()) {
                    onConfirm(dirName)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showCopyMoveDialog(title: String, onConfirm: (String, String) -> Unit) {
        val files = requireContext().filesDir.listFiles()?.map { it.name } ?: emptyList()

        if (files.isEmpty()) {
            showMessage("没有可操作的文件")
            return
        }

        val layout = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val sourceInput = android.widget.EditText(requireContext()).apply {
            hint = "源文件名"
            if (files.isNotEmpty()) setText(files.first())
        }
        layout.addView(sourceInput)

        val destInput = android.widget.EditText(requireContext()).apply {
            hint = "目标文件名"
            setText("copy_${System.currentTimeMillis()}.txt")
        }
        layout.addView(destInput)

        val hint = TextView(requireContext()).apply {
            text = "现有文件: ${files.take(3).joinToString(", ")}${if (files.size > 3) "..." else ""}"
            textSize = 12f
            setTextColor(resources.getColor(R.color.on_surface_variant, null))
        }
        layout.addView(hint)

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(layout)
            .setPositiveButton("确定") { _, _ ->
                val source = sourceInput.text.toString()
                val dest = destInput.text.toString()
                if (source.isNotBlank() && dest.isNotBlank()) {
                    onConfirm(source, dest)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showAppendDialog(onConfirm: (String, String) -> Unit) {
        val files = requireContext().filesDir.listFiles()?.map { it.name } ?: emptyList()

        val layout = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val fileInput = android.widget.EditText(requireContext()).apply {
            hint = "文件名"
            if (files.isNotEmpty()) setText(files.first())
        }
        layout.addView(fileInput)

        val contentInput = android.widget.EditText(requireContext()).apply {
            hint = "追加内容"
            setText("\n--- 追加于 ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())} ---")
        }
        layout.addView(contentInput)

        if (files.isNotEmpty()) {
            val hint = TextView(requireContext()).apply {
                text = "现有文件: ${files.take(3).joinToString(", ")}"
                textSize = 12f
                setTextColor(resources.getColor(R.color.on_surface_variant, null))
            }
            layout.addView(hint)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("追加写入")
            .setView(layout)
            .setPositiveButton("追加") { _, _ ->
                val fileName = fileInput.text.toString()
                val content = contentInput.text.toString()
                if (fileName.isNotBlank()) {
                    onConfirm(fileName, content)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
