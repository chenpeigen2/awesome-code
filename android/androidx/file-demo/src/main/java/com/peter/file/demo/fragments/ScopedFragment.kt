package com.peter.file.demo.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.file.demo.FileAdapter
import com.peter.file.demo.FileItem
import com.peter.file.demo.FileOperationType
import com.peter.file.demo.MainActivity
import com.peter.file.demo.R
import com.peter.file.demo.databinding.FragmentScopedBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 分区存储 Fragment
 */
class ScopedFragment : Fragment() {

    private var _binding: FragmentScopedBinding? = null
    private val binding get() = _binding!!

    private val tabPosition = 3

    // SAF 文件选择器
    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { handlePickedFile(it) }
    }

    // SAF 文档创建
    private val createDocLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
        uri?.let { handleCreatedDocument(it) }
    }

    // 照片选择器 (Android 13+)
    private val photoPickerLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { handlePickedPhoto(it) }
    }

    companion object {
        fun newInstance() = ScopedFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScopedBinding.inflate(inflater, container, false)
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
                type = FileOperationType.SCOPED_PICK_FILE,
                title = getString(R.string.scoped_pick_file),
                description = getString(R.string.scoped_pick_file_desc)
            ),
            FileItem(
                type = FileOperationType.SCOPED_CREATE_DOC,
                title = getString(R.string.scoped_create_doc),
                description = getString(R.string.scoped_create_doc_desc)
            ),
            FileItem(
                type = FileOperationType.SCOPED_QUERY_IMAGES,
                title = getString(R.string.scoped_query_images),
                description = getString(R.string.scoped_query_images_desc)
            ),
            FileItem(
                type = FileOperationType.SCOPED_SAVE_IMAGE,
                title = getString(R.string.scoped_save_image),
                description = getString(R.string.scoped_save_image_desc)
            ),
            FileItem(
                type = FileOperationType.SCOPED_PHOTO_PICKER,
                title = getString(R.string.scoped_photo_picker),
                description = getString(R.string.scoped_photo_picker_desc)
            ),
            FileItem(
                type = FileOperationType.SCOPED_PERMISSION,
                title = getString(R.string.scoped_permission),
                description = getString(R.string.scoped_permission_desc)
            )
        )
    }

    private fun handleOperation(type: FileOperationType) {
        val mainActivity = requireActivity() as MainActivity
        val fileHelper = mainActivity.fileHelper

        when (type) {
            FileOperationType.SCOPED_PICK_FILE -> {
                pickFileLauncher.launch(arrayOf("*/*"))
            }

            FileOperationType.SCOPED_CREATE_DOC -> {
                val fileName = "doc_${System.currentTimeMillis()}.txt"
                createDocLauncher.launch(fileName)
            }

            FileOperationType.SCOPED_QUERY_IMAGES -> {
                if (!mainActivity.hasStoragePermission()) {
                    showMessage("需要存储权限才能查询图片")
                    mainActivity.requestStoragePermission()
                    return
                }

                val images = fileHelper.queryImages()
                if (images.isEmpty()) {
                    showMessage("没有找到图片")
                } else {
                    val imageList = images.take(10).map { img ->
                        "${img.name} (${fileHelper.formatFileSize(img.size)})"
                    }
                    showFileListDialog("最近图片 (${images.size}张)", imageList)
                }
            }

            FileOperationType.SCOPED_SAVE_IMAGE -> {
                if (!mainActivity.hasStoragePermission()) {
                    showMessage("需要存储权限才能保存图片")
                    mainActivity.requestStoragePermission()
                    return
                }
                saveImageToPictures()
            }

            FileOperationType.SCOPED_PHOTO_PICKER -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    photoPickerLauncher.launch(
                        androidx.activity.result.PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                } else {
                    showMessage("照片选择器需要 Android 13+")
                }
            }

            FileOperationType.SCOPED_PERMISSION -> {
                if (mainActivity.hasStoragePermission()) {
                    showPermissionStatusDialog(true)
                } else {
                    AlertDialog.Builder(requireContext())
                        .setTitle("存储权限")
                        .setMessage("需要存储权限才能访问媒体文件。是否请求权限？")
                        .setPositiveButton("请求权限") { _, _ ->
                            mainActivity.requestStoragePermission()
                        }
                        .setNegativeButton("取消", null)
                        .show()
                }
            }

            else -> {}
        }
    }

    private fun handlePickedFile(uri: Uri) {
        try {
            val content = requireContext().contentResolver.openInputStream(uri)?.use { input ->
                input.bufferedReader().readText()
            } ?: "无法读取内容"

            val info = """
                URI: $uri
                文件大小: ${content.length} 字符

                内容预览:
                ${content.take(200)}${if (content.length > 200) "..." else ""}
            """.trimIndent()

            showContentDialog("已选择文件", info)
        } catch (e: Exception) {
            showMessage("读取文件失败: ${e.message}")
        }
    }

    private fun handleCreatedDocument(uri: Uri) {
        try {
            val content = "文档创建于: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}"
            requireContext().contentResolver.openOutputStream(uri)?.use { output ->
                output.write(content.toByteArray())
            }
            showMessage("文档已创建: $uri")
        } catch (e: Exception) {
            showMessage("创建文档失败: ${e.message}")
        }
    }

    private fun handlePickedPhoto(uri: Uri) {
        val info = """
            照片已选择

            URI: $uri

            可以通过 ContentResolver 读取图片数据
        """.trimIndent()

        showContentDialog("照片选择结果", info)
    }

    private fun saveImageToPictures() {
        try {
            // 创建一个简单的彩色位图
            val bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.parseColor("#6750A4")) // 紫色背景

            // 添加一些装饰
            val paint = android.graphics.Paint().apply {
                color = Color.WHITE
                textSize = 40f
                isAntiAlias = true
            }
            canvas.drawText("Demo Image", 100f, 220f, paint)

            // 保存到 Pictures 目录
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "demo_${System.currentTimeMillis()}.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/FileDemo")
                }
            }

            val uri = requireContext().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )

            uri?.let {
                requireContext().contentResolver.openOutputStream(it)?.use { output ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                }
                showMessage("图片已保存到 Pictures/FileDemo")
            } ?: run {
                showMessage("保存图片失败")
            }
        } catch (e: Exception) {
            showMessage("保存图片失败: ${e.message}")
        }
    }

    private fun showPermissionStatusDialog(granted: Boolean) {
        val info = if (granted) {
            """
                存储权限状态: 已授予

                可以访问:
                • 图片 (READ_MEDIA_IMAGES)
                • 视频 (READ_MEDIA_VIDEO)
                • 音频 (READ_MEDIA_AUDIO)

                API 级别: ${Build.VERSION.SDK_INT}
            """.trimIndent()
        } else {
            """
                存储权限状态: 未授予

                需要权限才能:
                • 查询媒体文件
                • 保存图片到相册

                点击"请求权限"按钮获取权限
            """.trimIndent()
        }

        AlertDialog.Builder(requireContext())
            .setTitle("存储权限状态")
            .setMessage(info)
            .setPositiveButton("确定", null)
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
