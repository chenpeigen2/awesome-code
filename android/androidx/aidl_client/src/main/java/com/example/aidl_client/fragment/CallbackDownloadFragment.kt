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
import com.example.aidl_client.databinding.FragmentCallbackDownloadBinding
import com.example.aidl_client.viewmodel.AidlViewModel

class CallbackDownloadFragment : Fragment() {
    companion object {
        private const val TAG = "CallbackDownload"
    }

    private var _binding: FragmentCallbackDownloadBinding? = null
    private val viewModel: AidlViewModel by activityViewModels()
    private var currentDownloadId = -1

    private val callback = object : IDownloadCallback.Stub() {
        override fun onProgress(downloadId: Int, progress: Int) {
            activity?.runOnUiThread {
                if (downloadId == currentDownloadId) {
                    _binding?.progressBar?.progress = progress
                    _binding?.tvProgress?.text = "进度: $progress%"
                }
                appendLog("[onProgress] downloadId=$downloadId, progress=$progress%")
            }
        }

        override fun onComplete(downloadId: Int, filePath: String?, fileSize: Long) {
            activity?.runOnUiThread {
                appendLog("[onComplete] downloadId=$downloadId, path=$filePath, size=$fileSize")
                if (downloadId == currentDownloadId) {
                    _binding?.tvProgress?.text = "下载完成: $filePath (${fileSize} bytes)"
                    _binding?.btnStart?.isEnabled = true
                    _binding?.btnCancel?.isEnabled = false
                }
            }
        }

        override fun onFailure(downloadId: Int, errorCode: Int, message: String?) {
            activity?.runOnUiThread {
                appendLog("[onFailure] downloadId=$downloadId, code=$errorCode, msg=$message")
                if (downloadId == currentDownloadId) {
                    _binding?.tvProgress?.text = "下载失败: $message"
                    _binding?.btnStart?.isEnabled = true
                    _binding?.btnCancel?.isEnabled = false
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCallbackDownloadBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.btnStart?.setOnClickListener { startDownload() }
        _binding?.btnCancel?.setOnClickListener { cancelDownload() }
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
            service.downloadWithCallback(url, fileName, callback)
            _binding?.btnStart?.isEnabled = false
            _binding?.btnCancel?.isEnabled = true
            _binding?.progressBar?.progress = 0
            _binding?.tvProgress?.text = "下载中..."
            appendLog("已发起回调下载: $fileName")
        } catch (e: RemoteException) {
            appendLog("发起失败: ${e.message}")
        }
    }

    private fun cancelDownload() {
        val service = viewModel.service.value ?: return
        if (currentDownloadId < 0) return

        try {
            val result = service.cancelDownload(currentDownloadId)
            appendLog("取消下载: ${if (result) "成功" else "失败"}")
            _binding?.btnStart?.isEnabled = true
            _binding?.btnCancel?.isEnabled = false
        } catch (e: RemoteException) {
            appendLog("取消失败: ${e.message}")
        }
    }

    private fun appendLog(msg: String) {
        _binding?.tvLog?.append("$msg\n")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
