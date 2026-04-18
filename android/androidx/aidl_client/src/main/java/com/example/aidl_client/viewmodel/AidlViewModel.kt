package com.example.aidl_client.viewmodel

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aidl_common.IDownloadManager

class AidlViewModel : ViewModel() {
    companion object {
        private const val TAG = "AidlViewModel"
        private const val SERVER_PACKAGE = "com.example.aidl_server"
    }

    private val _service = MutableLiveData<IDownloadManager?>(null)
    val service: LiveData<IDownloadManager?> = _service

    private val _connected = MutableLiveData(false)
    val connected: LiveData<Boolean> = _connected

    var serviceConnection: ServiceConnection? = null

    fun setService(binder: IDownloadManager?) {
        _service.value = binder
        _connected.value = binder != null
    }

    fun createConnection(): ServiceConnection {
        return object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.d(TAG, "服务已连接")
                _service.value = IDownloadManager.Stub.asInterface(service)
                _connected.value = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                Log.d(TAG, "服务已断开")
                _service.value = null
                _connected.value = false
            }
        }.also { serviceConnection = it }
    }

    fun getServiceIntent(): Intent {
        return Intent().apply {
            component = ComponentName(
                SERVER_PACKAGE,
                "$SERVER_PACKAGE.DownloadManagerService"
            )
        }
    }
}
