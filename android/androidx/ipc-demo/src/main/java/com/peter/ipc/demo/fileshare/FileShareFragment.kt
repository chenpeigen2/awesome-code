package com.peter.ipc.demo.fileshare

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.FileObserver
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.ipc.demo.R
import com.peter.ipc.demo.databinding.FragmentIpcBinding
import com.peter.ipc.demo.service.IpcRemoteService
import java.io.File

class FileShareFragment : Fragment() {

    private var _binding: FragmentIpcBinding? = null
    private val binding get() = _binding!!

    private var serviceMessenger: Messenger? = null
    private var isBound = false
    private var fileObserver: FileObserver? = null
    private lateinit var sharedFile: File

    private val replyMessenger = Messenger(object : android.os.Handler(android.os.Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == IpcRemoteService.MSG_FILE_SHARE) {
                val result = msg.data.getString(IpcRemoteService.KEY_RESULT) ?: ""
                val elapsed = msg.data.getLong(IpcRemoteService.KEY_ELAPSED, 0)
                binding.tvResult.text = result
                binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_success, elapsed))
            }
        }
    })

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceMessenger = Messenger(service)
            isBound = true
            binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_connected))
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceMessenger = null
            isBound = false
            binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_disconnected))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentIpcBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvDescription.text = getString(R.string.desc_file_share)
        binding.btnSend.setOnClickListener { sendViaFile() }
        binding.btnClear.setOnClickListener {
            binding.etInput.text?.clear()
            binding.tvResult.text = ""
        }

        val sharedDir = File(requireContext().getExternalFilesDir(null), "shared")
        sharedDir.mkdirs()
        sharedFile = File(sharedDir, "ipc_data.txt")
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(requireContext(), IpcRemoteService::class.java)
        requireContext().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        startFileObserver()
    }

    override fun onStop() {
        super.onStop()
        stopFileObserver()
        if (isBound) {
            requireContext().unbindService(connection)
            isBound = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startFileObserver() {
        val parentDir = sharedFile.parentFile ?: return
        fileObserver = object : FileObserver(parentDir, MODIFY or CREATE) {
            override fun onEvent(event: Int, path: String?) {
                if (path == sharedFile.name) {
                    try {
                        val content = sharedFile.readText()
                        activity?.runOnUiThread {
                            if (binding.tvResult.text?.toString() != content) {
                                binding.tvResult.text = content
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("FileShareFrag", "readFile error", e)
                    }
                }
            }
        }
        fileObserver?.startWatching()
    }

    private fun stopFileObserver() {
        fileObserver?.stopWatching()
        fileObserver = null
    }

    private fun sendViaFile() {
        val messenger = serviceMessenger ?: run {
            binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_disconnected))
            return
        }

        val inputText = binding.etInput.text?.toString()?.trim()
        if (inputText.isNullOrEmpty()) {
            binding.etInput.error = "请输入文本"
            return
        }

        binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_sending))

        try {
            sharedFile.writeText(inputText)

            val msg = Message.obtain().apply {
                what = IpcRemoteService.MSG_FILE_SHARE
                replyTo = replyMessenger
                obj = Bundle().apply {
                    putString(IpcRemoteService.KEY_FILE_PATH, sharedFile.absolutePath)
                }
            }
            messenger.send(msg)
        } catch (e: android.os.RemoteException) {
            Log.e("FileShareFrag", "sendViaFile error", e)
            binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_error, e.message))
        }
    }
}
