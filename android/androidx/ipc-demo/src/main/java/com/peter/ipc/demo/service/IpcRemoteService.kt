package com.peter.ipc.demo.service

import android.app.Service
import android.content.Intent
import android.net.LocalServerSocket
import android.net.LocalSocket
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.SharedMemory
import android.util.Log
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.File
import java.nio.ByteBuffer
import kotlin.concurrent.thread

class IpcRemoteService : Service() {

    companion object {
        private const val TAG = "IpcRemoteService"

        const val MSG_SHARED_MEMORY = 1
        const val MSG_FILE_SHARE = 2
        const val LOCAL_SOCKET_NAME = "ipc.demo.localsocket"

        const val KEY_SHARED_MEMORY = "shared_memory"
        const val KEY_FILE_PATH = "file_path"
        const val KEY_RESULT = "result"
        const val KEY_ELAPSED = "elapsed"
    }

    private var localServerSocket: LocalServerSocket? = null
    private var serverThreadRunning = false

    private val serviceMessenger = Messenger(object : android.os.Handler(android.os.Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_SHARED_MEMORY -> handleSharedMemory(msg)
                MSG_FILE_SHARE -> handleFileShare(msg)
                else -> super.handleMessage(msg)
            }
        }
    })

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "IpcRemoteService onCreate, pid=${android.os.Process.myPid()}")
        startLocalSocketServer()
    }

    override fun onBind(intent: Intent?): IBinder {
        return serviceMessenger.binder
    }

    override fun onDestroy() {
        super.onDestroy()
        serverThreadRunning = false
        localServerSocket?.close()
    }

    // ==================== SharedMemory ====================

    private fun handleSharedMemory(msg: Message) {
        val startTime = System.currentTimeMillis()
        val replyMessenger: Messenger? = msg.replyTo
        val bundle = msg.obj as? Bundle ?: return

        val sharedMemory = bundle.getParcelable<SharedMemory>(KEY_SHARED_MEMORY) ?: return

        try {
            val buffer = sharedMemory.mapReadWrite()
            val inputData = readStringFromBuffer(buffer)

            val remotePid = android.os.Process.myPid()
            val result = "$inputData\n---\n[Remote Process pid=$remotePid] 收到数据长度: ${inputData.length} 字符"

            buffer.clear()
            writeStringToBuffer(buffer, result)

            val elapsed = System.currentTimeMillis() - startTime
            replyMessenger?.send(Message.obtain().apply {
                what = MSG_SHARED_MEMORY
                data = Bundle().apply {
                    putString(KEY_RESULT, result)
                    putLong(KEY_ELAPSED, elapsed)
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "handleSharedMemory error", e)
            replyMessenger?.send(Message.obtain().apply {
                what = MSG_SHARED_MEMORY
                data = Bundle().apply {
                    putString(KEY_RESULT, "错误: ${e.message}")
                }
            })
        }
    }

    private fun readStringFromBuffer(buffer: ByteBuffer): String {
        val length = buffer.int
        if (length <= 0) return ""
        val bytes = ByteArray(length)
        buffer.get(bytes)
        return String(bytes, Charsets.UTF_8)
    }

    private fun writeStringToBuffer(buffer: ByteBuffer, text: String) {
        val bytes = text.toByteArray(Charsets.UTF_8)
        buffer.putInt(bytes.size)
        buffer.put(bytes)
    }

    // ==================== LocalSocket ====================

    private fun startLocalSocketServer() {
        serverThreadRunning = true
        thread(name = "LocalSocketServer") {
            try {
                localServerSocket = LocalServerSocket(LOCAL_SOCKET_NAME)
                Log.d(TAG, "LocalServerSocket started: $LOCAL_SOCKET_NAME")

                while (serverThreadRunning) {
                    val client = localServerSocket?.accept() ?: break
                    Log.d(TAG, "LocalSocket client connected")
                    handleLocalSocketClient(client)
                }
            } catch (e: Exception) {
                if (serverThreadRunning) {
                    Log.e(TAG, "LocalServerSocket error", e)
                }
            }
        }
    }

    private fun handleLocalSocketClient(client: LocalSocket) {
        try {
            val reader = BufferedReader(InputStreamReader(client.inputStream))
            val writer = BufferedWriter(OutputStreamWriter(client.outputStream))

            val length = reader.readLine()?.toIntOrNull() ?: return
            val chars = CharArray(length)
            reader.read(chars, 0, length)
            val inputData = String(chars)

            val remotePid = android.os.Process.myPid()
            val result = "$inputData\n---\n[Remote Process pid=$remotePid] LocalSocket 收到数据长度: ${inputData.length} 字符"

            writer.write("${result.toByteArray(Charsets.UTF_8).size}\n")
            writer.write(result)
            writer.newLine()
            writer.flush()
        } catch (e: Exception) {
            Log.e(TAG, "handleLocalSocketClient error", e)
        } finally {
            try { client.close() } catch (_: Exception) {}
        }
    }

    // ==================== FileShare ====================

    private fun handleFileShare(msg: Message) {
        val startTime = System.currentTimeMillis()
        val replyMessenger: Messenger? = msg.replyTo
        val bundle = msg.obj as? Bundle ?: return
        val filePath = bundle.getString(KEY_FILE_PATH) ?: return

        try {
            val file = File(filePath)
            val inputData = file.readText()

            val remotePid = android.os.Process.myPid()
            val result = "$inputData\n---\n[Remote Process pid=$remotePid] FileShare 收到数据长度: ${inputData.length} 字符"

            file.writeText(result)

            val elapsed = System.currentTimeMillis() - startTime
            replyMessenger?.send(Message.obtain().apply {
                what = MSG_FILE_SHARE
                data = Bundle().apply {
                    putString(KEY_RESULT, result)
                    putLong(KEY_ELAPSED, elapsed)
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "handleFileShare error", e)
            replyMessenger?.send(Message.obtain().apply {
                what = MSG_FILE_SHARE
                data = Bundle().apply {
                    putString(KEY_RESULT, "错误: ${e.message}")
                }
            })
        }
    }
}
