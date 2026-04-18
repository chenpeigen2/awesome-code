package com.example.aidl_client.fragment

import android.os.Bundle
import android.os.RemoteException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.aidl_common.IDownloadCallback
import com.example.aidl_client.databinding.FragmentCallbackRegisterBinding
import com.example.aidl_client.viewmodel.AidlViewModel

class CallbackRegisterFragment : Fragment() {
    companion object {
        private const val TAG = "CallbackRegister"
    }

    private var _binding: FragmentCallbackRegisterBinding? = null
    private val viewModel: AidlViewModel by activityViewModels()
    private var isRegistered = false

    private val callback = object : IDownloadCallback.Stub() {
        override fun onProgress(downloadId: Int, progress: Int) {
            activity?.runOnUiThread {
                appendLog("[progress] #$downloadId → $progress%")
            }
        }

        override fun onComplete(downloadId: Int, filePath: String?, fileSize: Long) {
            activity?.runOnUiThread {
                appendLog("[complete] #$downloadId → $filePath (${formatSize(fileSize)})")
            }
        }

        override fun onFailure(downloadId: Int, errorCode: Int, message: String?) {
            activity?.runOnUiThread {
                appendLog("[failure] #$downloadId → code=$errorCode, $message")
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCallbackRegisterBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.btnRegister?.setOnClickListener { registerCallback() }
        _binding?.btnUnregister?.setOnClickListener { unregisterCallback() }
        _binding?.btnBatchDownload?.setOnClickListener { batchDownload() }
    }

    private fun registerCallback() {
        val service = viewModel.service.value
        if (service == null) {
            Toast.makeText(requireContext(), "服务未连接", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            service.registerCallback(callback)
            isRegistered = true
            _binding?.tvRegisterStatus?.text = "回调状态: 已注册"
            _binding?.tvRegisterStatus?.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
            _binding?.btnRegister?.isEnabled = false
            _binding?.btnUnregister?.isEnabled = true
            appendLog("已注册回调监听")
        } catch (e: RemoteException) {
            appendLog("注册失败: ${e.message}")
        }
    }

    private fun unregisterCallback() {
        val service = viewModel.service.value ?: return

        try {
            service.unregisterCallback(callback)
            isRegistered = false
            _binding?.tvRegisterStatus?.text = "回调状态: 未注册"
            _binding?.tvRegisterStatus?.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
            _binding?.btnRegister?.isEnabled = true
            _binding?.btnUnregister?.isEnabled = false
            appendLog("已注销回调监听")
        } catch (e: RemoteException) {
            appendLog("注销失败: ${e.message}")
        }
    }

    private fun batchDownload() {
        val service = viewModel.service.value
        if (service == null) {
            Toast.makeText(requireContext(), "服务未连接", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // 使用 oneway 触发3个并发下载，已注册的回调会收到所有事件
            service.startDownload("https://example.com/file1.zip", "file1.zip")
            service.startDownload("https://example.com/file2.zip", "file2.zip")
            service.startDownload("https://example.com/file3.zip", "file3.zip")
            appendLog("已触发 3 个并发下载")
            if (!isRegistered) {
                appendLog("⚠ 未注册回调，无法接收事件。请先注册回调。")
            }
        } catch (e: RemoteException) {
            appendLog("批量下载失败: ${e.message}")
        }
    }

    private fun appendLog(msg: String) {
        _binding?.tvLog?.append("$msg\n")
    }

    private fun formatSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> "${bytes / (1024 * 1024)} MB"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 如果已注册，注销回调避免泄漏
        if (isRegistered) {
            try {
                viewModel.service.value?.unregisterCallback(callback)
            } catch (_: Exception) {}
        }
        _binding = null
    }
}
