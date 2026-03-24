package com.peter.file.demo.fragments

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.file.demo.FileAdapter
import com.peter.file.demo.FileItem
import com.peter.file.demo.FileOperationType
import com.peter.file.demo.MainActivity
import com.peter.file.demo.R
import com.peter.file.demo.databinding.FragmentPreferencesBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 偏好设置 Fragment
 */
class PreferencesFragment : Fragment() {

    private var _binding: FragmentPreferencesBinding? = null
    private val binding get() = _binding!!

    private val tabPosition = 2
    private var preferenceListener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    companion object {
        fun newInstance() = PreferencesFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreferencesBinding.inflate(inflater, container, false)
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
                type = FileOperationType.PREF_PUT_STRING,
                title = getString(R.string.pref_put_string),
                description = getString(R.string.pref_put_string_desc)
            ),
            FileItem(
                type = FileOperationType.PREF_GET_STRING,
                title = getString(R.string.pref_get_string),
                description = getString(R.string.pref_get_string_desc)
            ),
            FileItem(
                type = FileOperationType.PREF_PUT_INT,
                title = getString(R.string.pref_put_int),
                description = getString(R.string.pref_put_int_desc)
            ),
            FileItem(
                type = FileOperationType.PREF_GET_INT,
                title = getString(R.string.pref_get_int),
                description = getString(R.string.pref_get_int_desc)
            ),
            FileItem(
                type = FileOperationType.PREF_PUT_BOOLEAN,
                title = getString(R.string.pref_put_boolean),
                description = getString(R.string.pref_put_boolean_desc)
            ),
            FileItem(
                type = FileOperationType.PREF_GET_BOOLEAN,
                title = getString(R.string.pref_get_boolean),
                description = getString(R.string.pref_get_boolean_desc)
            ),
            FileItem(
                type = FileOperationType.PREF_REMOVE,
                title = getString(R.string.pref_remove),
                description = getString(R.string.pref_remove_desc)
            ),
            FileItem(
                type = FileOperationType.PREF_CLEAR,
                title = getString(R.string.pref_clear),
                description = getString(R.string.pref_clear_desc)
            ),
            FileItem(
                type = FileOperationType.PREF_LISTENER,
                title = getString(R.string.pref_listener),
                description = getString(R.string.pref_listener_desc)
            ),
            FileItem(
                type = FileOperationType.PREF_COMMIT_VS_APPLY,
                title = getString(R.string.pref_commit_vs_apply),
                description = getString(R.string.pref_commit_vs_apply_desc)
            ),
            FileItem(
                type = FileOperationType.PREF_EXPORT,
                title = getString(R.string.pref_export),
                description = getString(R.string.pref_export_desc)
            ),
            FileItem(
                type = FileOperationType.PREF_IMPORT,
                title = getString(R.string.pref_import),
                description = getString(R.string.pref_import_desc)
            )
        )
    }

    private fun handleOperation(type: FileOperationType) {
        val mainActivity = requireActivity() as MainActivity
        val fileHelper = mainActivity.fileHelper

        when (type) {
            FileOperationType.PREF_PUT_STRING -> showKeyInputDialog("输入字符串值") { key, value ->
                val success = fileHelper.putString(key, value)
                showMessage(if (success) "已保存: $key = $value" else "保存失败")
            }

            FileOperationType.PREF_GET_STRING -> showKeyOnlyDialog("输入要读取的键") { key ->
                val value = fileHelper.getString(key, "(未找到)")
                showContentDialog("读取结果", "$key = $value")
            }

            FileOperationType.PREF_PUT_INT -> showIntInputDialog { key, value ->
                val success = fileHelper.putInt(key, value)
                showMessage(if (success) "已保存: $key = $value" else "保存失败")
            }

            FileOperationType.PREF_GET_INT -> showKeyOnlyDialog("输入要读取的键") { key ->
                val value = fileHelper.getInt(key, -1)
                showContentDialog("读取结果", "$key = $value")
            }

            FileOperationType.PREF_PUT_BOOLEAN -> showBooleanInputDialog { key, value ->
                val success = fileHelper.putBoolean(key, value)
                showMessage(if (success) "已保存: $key = $value" else "保存失败")
            }

            FileOperationType.PREF_GET_BOOLEAN -> showKeyOnlyDialog("输入要读取的键") { key ->
                val value = fileHelper.getBoolean(key, false)
                showContentDialog("读取结果", "$key = $value")
            }

            FileOperationType.PREF_REMOVE -> showKeySelectDialog("选择要删除的键") { key ->
                val success = fileHelper.removeKey(key)
                showMessage(if (success) "已删除: $key" else "删除失败")
            }

            FileOperationType.PREF_CLEAR -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("清空所有偏好设置")
                    .setMessage("确定要清空所有偏好设置吗？此操作不可撤销。")
                    .setPositiveButton("清空") { _, _ ->
                        val success = fileHelper.clearAll()
                        showMessage(if (success) "已清空所有偏好设置" else "清空失败")
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }

            FileOperationType.PREF_LISTENER -> {
                if (preferenceListener == null) {
                    preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                        showMessage("偏好设置变化: $key")
                    }
                    fileHelper.registerListener(preferenceListener!!)
                    showMessage("监听器已注册，修改偏好设置会触发通知")
                } else {
                    fileHelper.unregisterListener(preferenceListener!!)
                    preferenceListener = null
                    showMessage("监听器已注销")
                }
            }

            FileOperationType.PREF_COMMIT_VS_APPLY -> {
                val results = StringBuilder()

                // Commit (同步)
                val startCommit = System.currentTimeMillis()
                val commitResult = fileHelper.putString("test_commit", "value_${System.currentTimeMillis()}")
                val commitTime = System.currentTimeMillis() - startCommit
                results.append("Commit (同步):\n")
                results.append("  耗时: ${commitTime}ms\n")
                results.append("  结果: ${if (commitResult) "成功" else "失败"}\n\n")

                // Apply (异步)
                val startApply = System.currentTimeMillis()
                fileHelper.applyString("test_apply", "value_${System.currentTimeMillis()}")
                val applyTime = System.currentTimeMillis() - startApply
                results.append("Apply (异步):\n")
                results.append("  耗时: ${applyTime}ms\n")
                results.append("  结果: 立即返回\n\n")

                results.append("结论: Apply 更快，因为是异步操作")

                showContentDialog("Commit vs Apply", results.toString())
            }

            FileOperationType.PREF_EXPORT -> {
                lifecycleScope.launch {
                    val result = fileHelper.exportPreferences()
                    showMessage(result.getOrNull() ?: result.exceptionOrNull()?.message ?: "导出失败")
                }
            }

            FileOperationType.PREF_IMPORT -> {
                val keys = fileHelper.getAllPreferenceKeys()
                if (keys.isEmpty()) {
                    showMessage("没有可导入的偏好设置")
                } else {
                    val content = keys.joinToString("\n") { key ->
                        "$key = ${fileHelper.getString(key, "")}"
                    }
                    showContentDialog("当前偏好设置", content)
                }
            }

            else -> {}
        }
    }

    private fun showKeyInputDialog(title: String, onConfirm: (String, String) -> Unit) {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val keyInput = EditText(requireContext()).apply {
            hint = "键"
        }
        layout.addView(keyInput)

        val valueInput = EditText(requireContext()).apply {
            hint = "值"
        }
        layout.addView(valueInput)

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(layout)
            .setPositiveButton("保存") { _, _ ->
                val key = keyInput.text.toString()
                val value = valueInput.text.toString()
                if (key.isNotBlank()) {
                    onConfirm(key, value)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showKeyOnlyDialog(title: String, onConfirm: (String) -> Unit) {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val keyInput = EditText(requireContext()).apply {
            hint = "键"
        }
        layout.addView(keyInput)

        val mainActivity = requireActivity() as MainActivity
        val keys = mainActivity.fileHelper.getAllPreferenceKeys()
        if (keys.isNotEmpty()) {
            val hint = TextView(requireContext()).apply {
                text = "已有键: ${keys.take(3).joinToString(", ")}${if (keys.size > 3) "..." else ""}"
                textSize = 12f
                setTextColor(resources.getColor(R.color.on_surface_variant, null))
            }
            layout.addView(hint)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(layout)
            .setPositiveButton("读取") { _, _ ->
                val key = keyInput.text.toString()
                if (key.isNotBlank()) {
                    onConfirm(key)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showIntInputDialog(onConfirm: (String, Int) -> Unit) {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val keyInput = EditText(requireContext()).apply {
            hint = "键"
        }
        layout.addView(keyInput)

        val valueInput = EditText(requireContext()).apply {
            hint = "整数值"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        layout.addView(valueInput)

        AlertDialog.Builder(requireContext())
            .setTitle("保存整数")
            .setView(layout)
            .setPositiveButton("保存") { _, _ ->
                val key = keyInput.text.toString()
                val value = valueInput.text.toString().toIntOrNull() ?: 0
                if (key.isNotBlank()) {
                    onConfirm(key, value)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showBooleanInputDialog(onConfirm: (String, Boolean) -> Unit) {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val keyInput = EditText(requireContext()).apply {
            hint = "键"
        }
        layout.addView(keyInput)

        val options = arrayOf("true", "false")

        AlertDialog.Builder(requireContext())
            .setTitle("保存布尔值")
            .setView(layout)
            .setPositiveButton("保存") { _, _ ->
                val key = keyInput.text.toString()
                if (key.isNotBlank()) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("选择布尔值")
                        .setSingleChoiceItems(options, 0) { dialog, which ->
                            onConfirm(key, which == 0)
                            dialog.dismiss()
                        }
                        .show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showKeySelectDialog(title: String, onConfirm: (String) -> Unit) {
        val mainActivity = requireActivity() as MainActivity
        val keys = mainActivity.fileHelper.getAllPreferenceKeys().toList()

        if (keys.isEmpty()) {
            showMessage("没有可选择的键")
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setItems(keys.toTypedArray()) { _, which ->
                onConfirm(keys[which])
            }
            .setNegativeButton("取消", null)
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
        // 注销监听器
        preferenceListener?.let {
            (requireActivity() as MainActivity).fileHelper.unregisterListener(it)
        }
        _binding = null
    }
}
