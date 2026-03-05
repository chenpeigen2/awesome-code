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
        
        initViews()
        setupListeners()
    }
    
    private fun initViews() {
        tvStatus = findViewById(R.id.tv_status)
        btnStartService = findViewById(R.id.btn_start_service)
        btnStopService = findViewById(R.id.btn_stop_service)
        updateServiceStatus(false)
    }
    
    private fun setupListeners() {
        btnStartService.setOnClickListener {
            startAidlService()
        }
        
        btnStopService.setOnClickListener {
            stopAidlService()
        }
    }
    
    private fun startAidlService() {
        if (!isServiceRunning) {
            val intent = Intent(this, MyAidlService::class.java)
            startService(intent)
            isServiceRunning = true
            updateServiceStatus(true)
            Log.d(TAG, "AIDL 服务已启动")
        }
    }
    
    private fun stopAidlService() {
        if (isServiceRunning) {
            val intent = Intent(this, MyAidlService::class.java)
            stopService(intent)
            isServiceRunning = false
            updateServiceStatus(false)
            Log.d(TAG, "AIDL 服务已停止")
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
    
    override fun onDestroy() {
        super.onDestroy()
        // 可选：退出时停止服务
        // stopAidlService()
    }
}
