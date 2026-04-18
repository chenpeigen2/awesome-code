package com.example.aidl_server

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.example.aidl_common.DownloadInfo
import com.example.aidl_common.IDownloadCallback
import com.example.aidl_common.IDownloadManager
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class DownloadManagerService : Service() {
    companion object {
        private const val TAG = "DownloadManagerService"
    }

    private val idGenerator = AtomicInteger(0)
    private val registeredCallbacks = CopyOnWriteArrayList<IDownloadCallback>()
    private val activeDownloads = ConcurrentHashMap<Int, DownloadInfo>()
    private val activeTasks = ConcurrentHashMap<Int, DownloadTask>()
    private val executor: ExecutorService = Executors.newFixedThreadPool(4)

    private val binder = object : IDownloadManager.Stub() {
        override fun add(a: Int, b: Int): Int {
            Log.d(TAG, "同步调用: $a + $b")
            return a + b
        }

        override fun startDownload(url: String?, fileName: String?) {
            if (url == null || fileName == null) return
            val id = idGenerator.incrementAndGet()
            Log.d(TAG, "oneway 异步下载: id=$id, url=$url, fileName=$fileName")
            launchDownload(id, url, fileName, null)
        }

        override fun downloadWithCallback(
            url: String?,
            fileName: String?,
            callback: IDownloadCallback?
        ) {
            if (url == null || fileName == null || callback == null) return
            val id = idGenerator.incrementAndGet()
            Log.d(TAG, "带回调下载: id=$id, url=$url, fileName=$fileName")
            launchDownload(id, url, fileName, callback)
        }

        override fun registerCallback(callback: IDownloadCallback?) {
            if (callback != null) {
                registeredCallbacks.add(callback)
                Log.d(TAG, "注册回调, 当前数量: ${registeredCallbacks.size}")
            }
        }

        override fun unregisterCallback(callback: IDownloadCallback?) {
            if (callback != null) {
                registeredCallbacks.remove(callback)
                Log.d(TAG, "注销回调, 当前数量: ${registeredCallbacks.size}")
            }
        }

        override fun cancelDownload(downloadId: Int): Boolean {
            val task = activeTasks[downloadId]
            if (task != null) {
                task.isCancelled = true
                activeTasks.remove(downloadId)
                Log.d(TAG, "取消下载: id=$downloadId")
                return true
            }
            Log.w(TAG, "取消下载失败, 未找到: id=$downloadId")
            return false
        }

        override fun getActiveDownloads(): MutableList<DownloadInfo> {
            return ArrayList<DownloadInfo>(this@DownloadManagerService.activeDownloads.values)
        }
    }

    private fun launchDownload(id: Int, url: String, fileName: String, callback: IDownloadCallback?) {
        val fileSize = (1024L * 1024 * (5 + (Math.random() * 95).toLong())) // 5MB ~ 100MB
        val task = DownloadTask(id, url, fileName, fileSize, registeredCallbacks, callback, activeDownloads)
        activeTasks[id] = task
        executor.execute(task)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "下载管理服务已创建")
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "客户端绑定服务")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "客户端解绑服务")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdownNow()
        activeTasks.clear()
        activeDownloads.clear()
        registeredCallbacks.clear()
        Log.d(TAG, "下载管理服务已销毁")
    }
}
