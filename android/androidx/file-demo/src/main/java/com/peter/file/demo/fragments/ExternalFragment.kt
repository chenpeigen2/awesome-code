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
import com.peter.file.demo.databinding.FragmentExternalBinding
import kotlinx.coroutines.launch

/**
 * 外部存储 Fragment
 */
class ExternalFragment : Fragment() {

    private var _binding: FragmentExternalBinding? = null
    private val binding get() = _binding!!

    private val tabPosition = 1

    companion object {
        fun newInstance() = ExternalFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExternalBinding.inflate(inflater, container, false)
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
                type = FileOperationType.EXTERNAL_CHECK,
                title = getString(R.string.external_check),
                description = getString(R.string.external_check_desc)
            ),
            FileItem(
                type = FileOperationType.EXTERNAL_WRITE,
                title = getString(R.string.external_write),
                description = getString(R.string.external_write_desc)
            ),
            FileItem(
                type = FileOperationType.EXTERNAL_READ,
                title = getString(R.string.external_read),
                description = getString(R.string.external_read_desc)
            ),
            FileItem(
                type = FileOperationType.EXTERNAL_LIST,
                title = getString(R.string.external_list),
                description = getString(R.string.external_list_desc)
            ),
            FileItem(
                type = FileOperationType.EXTERNAL_DELETE,
                title = getString(R.string.external_delete),
                description = getString(R.string.external_delete_desc)
            ),
            FileItem(
                type = FileOperationType.EXTERNAL_CACHE,
                title = getString(R.string.external_cache),
                description = getString(R.string.external_cache_desc)
            ),
            FileItem(
                type = FileOperationType.EXTERNAL_PUBLIC_DIRS,
                title = getString(R.string.external_public_dirs),
                description = getString(R.string.external_public_dirs_desc)
            ),
            FileItem(
                type = FileOperationType.EXTERNAL_VOLUMES,
                title = getString(R.string.external_volumes),
                description = getString(R.string.external_volumes_desc)
            )
        )
    }

    private fun handleOperation(type: FileOperationType) {
        val mainActivity = requireActivity() as MainActivity
        val fileHelper = mainActivity.fileHelper

        when (type) {
            FileOperationType.EXTERNAL_CHECK -> {
                val state = fileHelper.getExternalStorageState()
                val stateText = when (state) {
                    com.peter.file.demo.StorageState.MOUNTED -> "可用 (可读写)"
                    com.peter.file.demo.StorageState.READ_ONLY -> "只读"
                    com.peter.file.demo.StorageState.UNAVAILABLE -> "不可用"
                }

                val directories = fileHelper.getStorageDirectories()
                val infoText = buildString {
                    append("外部存储状态: $stateText\n\n")
                    append("存储目录信息:\n\n")
                    directories.forEach { dir ->
                        append("${dir.name}:\n")
                        append("  路径: ${dir.path}\n")
                        append("  总空间: ${fileHelper.formatFileSize(dir.totalSpace)}\n")
                        append("  可用空间: ${fileHelper.formatFileSize(dir.freeSpace)}\n\n")
                    }
                }
                showContentDialog("存储状态", infoText)
            }

            FileOperationType.EXTERNAL_WRITE -> showWriteDialog { fileName, content ->
                lifecycleScope.launch {
                    val result = fileHelper.writeExternalFile(fileName, content)
                    showMessage(result.getOrNull() ?: result.exceptionOrNull()?.message ?: "写入失败")
                }
            }

            FileOperationType.EXTERNAL_READ -> showReadFileNameDialog { fileName ->
                lifecycleScope.launch {
                    val result = fileHelper.readExternalFile(fileName)
                    if (result.isSuccess) {
                        showContentDialog(fileName, result.getOrNull() ?: "")
                    } else {
                        showMessage(result.exceptionOrNull()?.message ?: "读取失败")
                    }
                }
            }

            FileOperationType.EXTERNAL_LIST -> {
                val files = fileHelper.listExternalFiles()
                if (files.isEmpty()) {
                    showMessage("没有外部文件")
                } else {
                    val fileNames = files.map {
                        "${it.name} (${fileHelper.formatFileSize(it.length())})"
                    }
                    showFileListDialog("外部存储文件", fileNames)
                }
            }

            FileOperationType.EXTERNAL_DELETE -> showDeleteFileDialog { fileName ->
                val deleted = fileHelper.deleteExternalFile(fileName)
                showMessage(if (deleted) "文件已删除" else "删除失败")
            }

            FileOperationType.EXTERNAL_CACHE -> showCacheDialog { fileName, content ->
                lifecycleScope.launch {
                    val result = fileHelper.writeExternalCache(fileName, content)
                    showMessage(result.getOrNull() ?: result.exceptionOrNull()?.message ?: "写入失败")
                }
            }

            FileOperationType.EXTERNAL_PUBLIC_DIRS -> {
                val publicDirs = fileHelper.getPublicDirectories()
                val dirInfo = publicDirs.entries.map { (name, file) ->
                    if (file != null && file.exists()) {
                        "$name: ${file.absolutePath}\n  可用空间: ${fileHelper.formatFileSize(file.freeSpace)}"
                    } else {
                        "$name: 不可用"
                    }
                }
                showContentDialog("公共目录", dirInfo.joinToString("\n\n"))
            }

            FileOperationType.EXTERNAL_VOLUMES -> {
                val volumes = fileHelper.getStorageVolumes()
                if (volumes.isEmpty()) {
                    showMessage("无法获取存储卷信息")
                } else {
                    val volumeInfo = volumes.map { vol ->
                        """
                        ${vol.description}
                        UUID: ${vol.uuid ?: "无"}
                        类型: ${if (vol.isRemovable) "可移除存储" else if (vol.isEmulated) "模拟存储" else "内置存储"}
                        主存储: ${if (vol.isPrimary) "是" else "否"}
                        状态: ${vol.state}
                        ${if (vol.totalSpace > 0) "总空间: ${fileHelper.formatFileSize(vol.totalSpace)}\n可用空间: ${fileHelper.formatFileSize(vol.freeSpace)}" else ""}
                        """.trimIndent()
                    }
                    showContentDialog("存储卷 (${volumes.size}个)", volumeInfo.joinToString("\n"))
                }
            }

            else -> {}
        }
    }

    private fun showWriteDialog(onConfirm: (String, String) -> Unit) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_input, null)
        val etFileName = dialogView.findViewById<EditText>(R.id.etFileName)
        val etContent = dialogView.findViewById<EditText>(R.id.etContent)

        etFileName.setText("external_${System.currentTimeMillis()}.txt")
        etContent.setText("这是外部存储测试内容\n时间: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}")

        AlertDialog.Builder(requireContext())
            .setTitle("写入外部文件")
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

    private fun showReadFileNameDialog(onConfirm: (String) -> Unit) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_input_filename, null)
        val etFileName = dialogView.findViewById<EditText>(R.id.etFileName)
        val tvHint = dialogView.findViewById<TextView>(R.id.tvHint)

        val files = requireContext().getExternalFilesDir(null)?.listFiles()?.map { it.name } ?: emptyList()

        if (files.isNotEmpty()) {
            tvHint.text = "现有文件: ${files.take(3).joinToString(", ")}${if (files.size > 3) "..." else ""}"
            etFileName.setText(files.first())
        }

        AlertDialog.Builder(requireContext())
            .setTitle("读取外部文件")
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
        val files = requireContext().getExternalFilesDir(null)?.listFiles()?.map { it.name } ?: emptyList()

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

    private fun showCacheDialog(onConfirm: (String, String) -> Unit) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_input, null)
        val etFileName = dialogView.findViewById<EditText>(R.id.etFileName)
        val etContent = dialogView.findViewById<EditText>(R.id.etContent)

        etFileName.setText("cache_${System.currentTimeMillis()}.txt")
        etContent.setText("这是外部缓存测试内容\n时间: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}")

        AlertDialog.Builder(requireContext())
            .setTitle("写入外部缓存")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
