package com.example.apprenderapp

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.view.SurfaceControl
import android.view.SurfaceControlViewHost
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appdisplayapp.IMyAidlInterface
import com.example.appdisplayapp.ISurfacePackageReceiver


class MainActivity : AppCompatActivity() {

    private var isServiceConnected = false


    private var mService: IMyAidlInterface? = null

    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // 将 IBinder 对象转换为我们定义的 AIDL 接口
            mService = IMyAidlInterface.Stub.asInterface(service)
            isServiceConnected = true
            Log.d("Client", "Service connected.")
            // 连接成功后，即可调用远程方法
            try {
                val result: Int? = mService?.add(5, 3)
                Log.d("Client", "Result from service: " + result)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mService = null
            isServiceConnected = false
            Log.d("Client", "Service disconnected.")
        }
    }

    private var renderer: ISurfacePackageReceiver? = null

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            renderer = ISurfacePackageReceiver.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            renderer = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }




    }
    private fun bindMyService() {
        val intent = Intent()
        // 设置 Action，与服务端 Manifest 中定义的保持一致
        intent.action = "com.example.appdisplayapp.IMyAidlInterface"
        // 设置服务端应用的包名（Android 5.0 及以上必需）
        intent.setPackage("com.example.appdisplayapp")
        val bind = bindService(intent, mServiceConnection, BIND_AUTO_CREATE)
        Log.d("xxxgg", "bindMyService: " + bind)
    }
}