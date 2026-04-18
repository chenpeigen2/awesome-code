package com.example.aidl_client.fragment

import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.aidl_client.databinding.FragmentAsyncDownloadBinding
import com.example.aidl_client.viewmodel.AidlViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AsyncDownloadFragment : Fragment() {
    companion object {
        private const val TAG = "AsyncDownload"
    }

    private var _binding: FragmentAsyncDownloadBinding? = null
    private val viewModel: AidlViewModel by activityViewModels()
    private var currentDownloadId = -1
    private var pollJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAsyncDownloadBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.btnStart?.setOnClickListener { startDownload() }
        _binding?.btnCancel?.setOnClickListener { cancelDownload() }
        _binding?.btnQuery?.setOnClickListener { queryDownloads() }
    }

    private fun startDownload() {
        val service = viewModel.service.value
        if (service == null) {
            Toast.makeText(requireContext(), "服务未连接", Toast.LENGTH_SHORT).show()
            return
        }

        val url = _binding?.etUrl?.text?.toString() ?: return
        val fileName = _binding?.etFileName?.text?.toString() ?: return

        try {
            // oneway 调用，立即返回，不返回 downloadId
            // 我们通过 getActiveDownloads 来发现新的下载
            service.startDownload(url, fileName)
            appendLog("已发送 oneway 下载请求: $fileName")
            _binding?.btnStart?.isEnabled = false
            _binding?.btnCancel?.isEnabled = true

            startPolling()
        } catch (e: RemoteException) {
            appendLog("启动失败: ${e.message}")
        }
    }

    private fun startPolling() {
        pollJob?.cancel()
        pollJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                delay(500)
                queryDownloadsInternal()
            }
        }
    }

    private fun queryDownloadsInternal() {
        val service = viewModel.service.value ?: return
        try {
            val downloads = service.activeDownloads
            if (downloads.isEmpty()) {
                if (currentDownloadId >= 0) {
                    appendLog("下载已完成或被清除")
                    stopPolling()
                    activity?.runOnUiThread {
                        _binding?.btnStart?.isEnabled = true
                        _binding?.btnCancel?.isEnabled = false
                    }
                }
                return
            }

            for (info in downloads) {
                if (currentDownloadId < 0 || info.id > currentDownloadId) {
                    currentDownloadId = info.id
                }
                if (info.id == currentDownloadId) {
                    val progress = if (info.totalSize > 0)
                        (info.downloadedSize * 100 / info.totalSize).toInt() else 0
                    activity?.runOnUiThread {
                        _binding?.progressBar?.progress = progress
                        _binding?.tvProgress?.text = "进度: $progress%  (${formatSize(info.downloadedSize)}/${formatSize(info.totalSize)})"
                    }
                }
            }
        } catch (e: RemoteException) {
            Log.w(TAG, "查询失败", e)
        }
    }

    private fun queryDownloads() {
        val service = viewModel.service.value
        if (service == null) {
            Toast.makeText(requireContext(), "服务未连接", Toast.LENGTH_SHORT).show()
            return
        }
        queryDownloadsInternal()
        appendLog("手动查询完成")
    }

    private fun cancelDownload() {
        val service = viewModel.service.value ?: return
        if (currentDownloadId < 0) return

        try {
            val result = service.cancelDownload(currentDownloadId)
            appendLog("取消下载: ${if (result) "成功" else "失败"}")
            stopPolling()
            _binding?.btnStart?.isEnabled = true
            _binding?.btnCancel?.isEnabled = false
            currentDownloadId = -1
        } catch (e: RemoteException) {
            appendLog("取消失败: ${e.message}")
        }
    }

    private fun stopPolling() {
        pollJob?.cancel()
        pollJob = null
    }

    private fun appendLog(msg: String) {
        activity?.runOnUiThread {
            _binding?.tvLog?.append("$msg\n")
        }
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
        stopPolling()
        _binding = null
    }
}
