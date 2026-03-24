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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
