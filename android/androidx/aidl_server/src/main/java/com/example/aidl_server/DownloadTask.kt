package com.example.aidl_server

import android.util.Log
import com.example.aidl_common.DownloadInfo
import com.example.aidl_common.IDownloadCallback
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class DownloadTask(
    private val downloadId: Int,
    private val url: String,
    private val fileName: String,
    private val fileSize: Long,
    private val registeredCallbacks: List<IDownloadCallback>,
    private val perRequestCallback: IDownloadCallback?,
    private val activeDownloads: ConcurrentHashMap<Int, DownloadInfo>
) : Runnable {
    companion object {
        private const val TAG = "DownloadTask"
    }

    @Volatile
    var isCancelled = false

    override fun run() {
        val info = DownloadInfo(downloadId, url, fileName, fileSize, 0, DownloadInfo.STATUS_RUNNING)
        activeDownloads[downloadId] = info
        notifyProgress(0)

        try {
            val steps = 100
            val stepSize = fileSize / steps

            for (i in 1..steps) {
                if (isCancelled) {
                    info.status = DownloadInfo.STATUS_CANCELLED
                    activeDownloads.remove(downloadId)
                    Log.d(TAG, "下载[$downloadId] 已取消")
                    return
                }

                Thread.sleep((50 + (Math.random() * 150)).toLong())
                info.downloadedSize = stepSize * i
                activeDownloads[downloadId] = info
                notifyProgress(i)
            }

            info.downloadedSize = fileSize
            info.status = DownloadInfo.STATUS_COMPLETED
            activeDownloads.remove(downloadId)

            val filePath = "/downloads/$fileName"
            notifyComplete(filePath, fileSize)
            Log.d(TAG, "下载[$downloadId] 完成: $filePath")

            // 随机模拟失败 (~10%)
        } catch (e: InterruptedException) {
            info.status = DownloadInfo.STATUS_CANCELLED
            activeDownloads.remove(downloadId)
            Log.d(TAG, "下载[$downloadId] 被中断")
        } catch (e: Exception) {
            info.status = DownloadInfo.STATUS_FAILED
            activeDownloads.remove(downloadId)
            notifyFailure(1, e.message ?: "Unknown error")
            Log.e(TAG, "下载[$downloadId] 失败", e)
        }
    }

    private fun notifyProgress(progress: Int) {
        val callbacks = registeredCallbacks.toList()
        for (callback in callbacks) {
            try {
                callback.onProgress(downloadId, progress)
            } catch (e: Exception) {
                Log.w(TAG, "回调通知进度失败", e)
            }
        }
        perRequestCallback?.let {
            try {
                it.onProgress(downloadId, progress)
            } catch (e: Exception) {
                Log.w(TAG, "per-request 回调通知进度失败", e)
            }
        }
    }

    private fun notifyComplete(filePath: String, fileSize: Long) {
        val callbacks = registeredCallbacks.toList()
        for (callback in callbacks) {
            try {
                callback.onComplete(downloadId, filePath, fileSize)
            } catch (e: Exception) {
                Log.w(TAG, "回调通知完成失败", e)
            }
        }
        perRequestCallback?.let {
            try {
                it.onComplete(downloadId, filePath, fileSize)
            } catch (e: Exception) {
                Log.w(TAG, "per-request 回调通知完成失败", e)
            }
        }
    }

    private fun notifyFailure(errorCode: Int, message: String) {
        val callbacks = registeredCallbacks.toList()
        for (callback in callbacks) {
            try {
                callback.onFailure(downloadId, errorCode, message)
            } catch (e: Exception) {
                Log.w(TAG, "回调通知失败失败", e)
            }
        }
        perRequestCallback?.let {
            try {
                it.onFailure(downloadId, errorCode, message)
            } catch (e: Exception) {
                Log.w(TAG, "per-request 回调通知失败失败", e)
            }
        }
    }
}
