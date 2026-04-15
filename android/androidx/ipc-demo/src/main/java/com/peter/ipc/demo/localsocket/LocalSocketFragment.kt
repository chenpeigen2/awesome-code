package com.peter.ipc.demo.localsocket

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.ipc.demo.R
import com.peter.ipc.demo.databinding.FragmentIpcBinding
import com.peter.ipc.demo.service.IpcRemoteService
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import kotlin.concurrent.thread

class LocalSocketFragment : Fragment() {

    private var _binding: FragmentIpcBinding? = null
    private val binding get() = _binding!!

    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: android.content.ComponentName?, service: IBinder?) {
            isBound = true
            binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_connected))
        }

        override fun onServiceDisconnected(name: android.content.ComponentName?) {
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
        binding.tvDescription.text = getString(R.string.desc_local_socket)
        binding.btnSend.setOnClickListener { sendViaLocalSocket() }
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

    private fun sendViaLocalSocket() {
        val inputText = binding.etInput.text?.toString()?.trim()
        if (inputText.isNullOrEmpty()) {
            binding.etInput.error = "请输入文本"
            return
        }

        binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_sending))
        binding.btnSend.isEnabled = false

        val startTime = System.currentTimeMillis()

        thread {
            var socket: android.net.LocalSocket? = null
            try {
                socket = android.net.LocalSocket()
                val address = android.net.LocalSocketAddress(
                    IpcRemoteService.LOCAL_SOCKET_NAME,
                    android.net.LocalSocketAddress.Namespace.ABSTRACT
                )
                socket.connect(address)

                val writer = BufferedWriter(OutputStreamWriter(socket.outputStream))
                val reader = BufferedReader(InputStreamReader(socket.inputStream))

                val bytes = inputText.toByteArray(Charsets.UTF_8)
                writer.write("${bytes.size}\n")
                writer.write(inputText)
                writer.newLine()
                writer.flush()

                val responseLength = reader.readLine()?.toIntOrNull() ?: 0
                if (responseLength > 0) {
                    val chars = CharArray(responseLength)
                    reader.read(chars, 0, responseLength)
                    val result = String(chars)
                    val elapsed = System.currentTimeMillis() - startTime

                    activity?.runOnUiThread {
                        binding.tvResult.text = result
                        binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_success, elapsed))
                        binding.btnSend.isEnabled = true
                    }
                }
            } catch (e: Exception) {
                Log.e("LocalSocketFrag", "sendViaLocalSocket error", e)
                activity?.runOnUiThread {
                    binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_error, e.message))
                    binding.btnSend.isEnabled = true
                }
            } finally {
                try { socket?.close() } catch (_: Exception) {}
            }
        }
    }
}
