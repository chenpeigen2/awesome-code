package com.peter.ipc.demo.sharedmemory

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.SharedMemory
import android.system.ErrnoException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.ipc.demo.R
import com.peter.ipc.demo.databinding.FragmentIpcBinding
import com.peter.ipc.demo.service.IpcRemoteService
import java.nio.ByteBuffer

class SharedMemoryFragment : Fragment() {

    private var _binding: FragmentIpcBinding? = null
    private val binding get() = _binding!!

    private var serviceMessenger: Messenger? = null
    private var isBound = false

    private val replyMessenger = Messenger(object : android.os.Handler(android.os.Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == IpcRemoteService.MSG_SHARED_MEMORY) {
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
        binding.tvDescription.text = getString(R.string.desc_shared_memory)
        binding.btnSend.setOnClickListener { sendViaSharedMemory() }
        binding.btnClear.setOnClickListener {
            binding.etInput.text?.clear()
            binding.tvResult.text = ""
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(requireContext(), IpcRemoteService::class.java)
        requireContext().bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            requireContext().unbindService(connection)
            isBound = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sendViaSharedMemory() {
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
            val bytes = inputText.toByteArray(Charsets.UTF_8)
            val bufferSize = 4 + bytes.size + 4096

            val sharedMemory = SharedMemory.create("ipc_shm", bufferSize)
            val buffer = sharedMemory.mapReadWrite()

            buffer.putInt(bytes.size)
            buffer.put(bytes)

            SharedMemory.unmap(buffer)

            val msg = Message.obtain().apply {
                what = IpcRemoteService.MSG_SHARED_MEMORY
                replyTo = replyMessenger
                obj = Bundle().apply {
                    putParcelable(IpcRemoteService.KEY_SHARED_MEMORY, sharedMemory)
                }
            }

            messenger.send(msg)
            sharedMemory.close()
        } catch (e: ErrnoException) {
            Log.e("SharedMemoryFrag", "sendViaSharedMemory error", e)
            binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_error, e.message))
        } catch (e: android.os.RemoteException) {
            Log.e("SharedMemoryFrag", "sendViaSharedMemory error", e)
            binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_error, e.message))
        }
    }
}
