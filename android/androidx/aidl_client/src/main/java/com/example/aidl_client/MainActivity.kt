package com.example.aidl_client

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {
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

    // 定义Service连接
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "成功连接到AIDL服务")


            // 获取AIDL接口实例
            mAidlService = IMyAidlInterface.Stub.asInterface(service)
            mServiceConnected = true

            updateUI()
            Toast.makeText(this@MainActivity, "服务连接成功", Toast.LENGTH_SHORT).show()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "与AIDL服务断开连接")
            mAidlService = null
            mServiceConnected = false

            updateUI()
            Toast.makeText(this@MainActivity, "服务已断开", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 绑定AIDL服务
     */
    private fun bindAidlService() {
        val intent = Intent()


        // 方法1: 使用隐式Intent（推荐用于跨应用）
        intent.setAction("com.example.appdisplayapp.IMyAidlInterface")


        // Android 5.0+ 必须设置包名
        intent.setPackage("com.example.aidl_server")


        // 方法2: 使用显式Intent（适用于同应用）
        // intent.setComponent(new ComponentName(
        //     "com.example.appdisplayapp",  // 服务端包名
        //     "com.example.appdisplayapp.MyAidlService"  // 服务端Service完整类名
        // ));
        try {
            val result = bindService(intent, mServiceConnection, BIND_AUTO_CREATE)
            if (result) {
                Log.d(TAG, "开始绑定服务...")
            } else {
                Toast.makeText(this, "绑定服务失败", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(this, "绑定权限错误: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }
}