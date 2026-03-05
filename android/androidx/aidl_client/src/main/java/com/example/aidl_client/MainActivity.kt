package com.example.aidl_client

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aidl_common.IMyAidlInterface

class MainActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "AidlClientMainActivity"
        private const val SERVER_PACKAGE = "com.example.aidl_server"
    }
    
    private lateinit var tvStatus: TextView
    private lateinit var tvResult: TextView
    private lateinit var etNum1: EditText
    private lateinit var etNum2: EditText
    private lateinit var btnConnect: Button
    private lateinit var btnDisconnect: Button
    private lateinit var btnAdd: Button
    private lateinit var btnSubtract: Button
    private lateinit var btnMultiply: Button
    private lateinit var btnDivide: Button
    
    private var mAidlService: IMyAidlInterface? = null
    private var mServiceConnected = false

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "成功连接到AIDL服务")
            mAidlService = IMyAidlInterface.Stub.asInterface(service)
            mServiceConnected = true
            updateConnectionStatus(true)
            Toast.makeText(this@MainActivity, "服务连接成功", Toast.LENGTH_SHORT).show()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "与AIDL服务断开连接")
            mAidlService = null
            mServiceConnected = false
            updateConnectionStatus(false)
            Toast.makeText(this@MainActivity, "服务已断开", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupListeners()
    }
    
    private fun initViews() {
        tvStatus = findViewById(R.id.tv_status)
        tvResult = findViewById(R.id.tv_result)
        etNum1 = findViewById(R.id.et_num1)
        etNum2 = findViewById(R.id.et_num2)
        btnConnect = findViewById(R.id.btn_connect)
        btnDisconnect = findViewById(R.id.btn_disconnect)
        btnAdd = findViewById(R.id.btn_add)
        btnSubtract = findViewById(R.id.btn_subtract)
        btnMultiply = findViewById(R.id.btn_multiply)
        btnDivide = findViewById(R.id.btn_divide)
        
        updateConnectionStatus(false)
    }
    
    private fun setupListeners() {
        btnConnect.setOnClickListener { bindAidlService() }
        btnDisconnect.setOnClickListener { unbindAidlService() }
        
        btnAdd.setOnClickListener { calculate("add") }
        btnSubtract.setOnClickListener { calculate("subtract") }
        btnMultiply.setOnClickListener { calculate("multiply") }
        btnDivide.setOnClickListener { calculate("divide") }
    }
    
    private fun bindAidlService() {
        // 使用显式 Intent 绑定服务（Android 5.0+ 必须使用显式 Intent）
        val intent = Intent().apply {
            component = ComponentName(
                SERVER_PACKAGE,  // 服务端包名
                "$SERVER_PACKAGE.MyAidlService"  // 服务端 Service 完整类名
            )
        }
        
        try {
            val result = bindService(intent, mServiceConnection, BIND_AUTO_CREATE)
            if (result) {
                Log.d(TAG, "开始绑定服务...")
                Toast.makeText(this, "正在连接服务...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "绑定服务失败", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "绑定权限错误", e)
            Toast.makeText(this, "权限错误: ${e.message}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "绑定异常", e)
            Toast.makeText(this, "绑定异常: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun unbindAidlService() {
        if (mServiceConnected) {
            try {
                unbindService(mServiceConnection)
                mServiceConnected = false
                mAidlService = null
                updateConnectionStatus(false)
                Log.d(TAG, "服务已解绑")
            } catch (e: Exception) {
                Log.e(TAG, "解绑失败", e)
            }
        }
    }
    
    private fun calculate(operation: String) {
        if (!mServiceConnected || mAidlService == null) {
            Toast.makeText(this, "请先连接服务", Toast.LENGTH_SHORT).show()
            return
        }
        
        val num1Str = etNum1.text.toString()
        val num2Str = etNum2.text.toString()
        
        if (num1Str.isEmpty() || num2Str.isEmpty()) {
            Toast.makeText(this, "请输入两个数字", Toast.LENGTH_SHORT).show()
            return
        }
        
        val num1 = num1Str.toIntOrNull()
        val num2 = num2Str.toIntOrNull()
        
        if (num1 == null || num2 == null) {
            Toast.makeText(this, "请输入有效的整数", Toast.LENGTH_SHORT).show()
            return
        }
        
        try {
            val result = when (operation) {
                "add" -> {
                    mAidlService?.add(num1, num2)
                    "$num1 + $num2 = ${mAidlService?.add(num1, num2)}"
                }
                "subtract" -> {
                    mAidlService?.subtract(num1, num2)
                    "$num1 - $num2 = ${mAidlService?.subtract(num1, num2)}"
                }
                "multiply" -> {
                    mAidlService?.multiply(num1, num2)
                    "$num1 × $num2 = ${mAidlService?.multiply(num1, num2)}"
                }
                "divide" -> {
                    if (num2 == 0) {
                        Toast.makeText(this, "除数不能为0", Toast.LENGTH_SHORT).show()
                        return
                    }
                    mAidlService?.divide(num1, num2)
                    "$num1 ÷ $num2 = ${mAidlService?.divide(num1, num2)}"
                }
                else -> "未知操作"
            }
            
            tvResult.text = "结果: $result"
            Log.d(TAG, "计算完成: $result")
        } catch (e: Exception) {
            Log.e(TAG, "计算失败", e)
            tvResult.text = "计算错误: ${e.message}"
        }
    }
    
    private fun updateConnectionStatus(isConnected: Boolean) {
        if (isConnected) {
            tvStatus.text = "连接状态: 已连接"
            tvStatus.setTextColor(getColor(android.R.color.holo_green_dark))
            btnConnect.isEnabled = false
            btnDisconnect.isEnabled = true
            enableCalcButtons(true)
        } else {
            tvStatus.text = "连接状态: 未连接"
            tvStatus.setTextColor(getColor(android.R.color.holo_red_dark))
            btnConnect.isEnabled = true
            btnDisconnect.isEnabled = false
            enableCalcButtons(false)
        }
    }
    
    private fun enableCalcButtons(enabled: Boolean) {
        btnAdd.isEnabled = enabled
        btnSubtract.isEnabled = enabled
        btnMultiply.isEnabled = enabled
        btnDivide.isEnabled = enabled
    }
    
    override fun onDestroy() {
        super.onDestroy()
        unbindAidlService()
    }
}
