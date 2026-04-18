package com.example.aidl_server

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "AidlServerMainActivity"
    }

    private lateinit var tvStatus: TextView
    private lateinit var btnStartService: Button
    private lateinit var btnStopService: Button
    private var isServiceRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tv_status)
        btnStartService = findViewById(R.id.btn_start_service)
        btnStopService = findViewById(R.id.btn_stop_service)
        updateServiceStatus(false)

        btnStartService.setOnClickListener { startAidlService() }
        btnStopService.setOnClickListener { stopAidlService() }
    }

    private fun startAidlService() {
        if (!isServiceRunning) {
            startService(Intent(this, DownloadManagerService::class.java))
            isServiceRunning = true
            updateServiceStatus(true)
            Log.d(TAG, "下载管理服务已启动")
        }
    }

    private fun stopAidlService() {
        if (isServiceRunning) {
            stopService(Intent(this, DownloadManagerService::class.java))
            isServiceRunning = false
            updateServiceStatus(false)
            Log.d(TAG, "下载管理服务已停止")
        }
    }

    private fun updateServiceStatus(isRunning: Boolean) {
        if (isRunning) {
            tvStatus.text = "服务状态: 运行中"
            tvStatus.setTextColor(getColor(android.R.color.holo_green_dark))
            btnStartService.isEnabled = false
            btnStopService.isEnabled = true
        } else {
            tvStatus.text = "服务状态: 已停止"
            tvStatus.setTextColor(getColor(android.R.color.holo_red_dark))
            btnStartService.isEnabled = true
            btnStopService.isEnabled = false
        }
    }
}
