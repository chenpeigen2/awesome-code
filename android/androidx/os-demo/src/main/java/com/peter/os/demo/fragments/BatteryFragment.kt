package com.peter.os.demo.fragments

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.os.demo.R
import com.peter.os.demo.databinding.FragmentBatteryBinding

/**
 * BatteryManager 电池管理示例
 */
class BatteryFragment : Fragment() {

    private var _binding: FragmentBatteryBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var batteryManager: BatteryManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBatteryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        batteryManager = requireContext().getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        setupViews()
        loadBatteryInfo()
    }

    private fun setupViews() {
        binding.btnRefresh.setOnClickListener {
            loadBatteryInfo()
        }
    }

    private fun loadBatteryInfo() {
        val sb = StringBuilder()
        
        // 通过 BatteryManager 获取属性
        sb.appendLine("=== BatteryManager 属性 ===")
        
        // 电量百分比
        val capacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        sb.appendLine("电量百分比: $capacity%")
        
        // 当前电量 (mAh)
        val chargeCounter = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        sb.appendLine("当前电量: $chargeCounter mAh")
        
        // 是否正在充电
        val isCharging = batteryManager.isCharging
        sb.appendLine("正在充电: ${if (isCharging) "是" else "否"}")
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val chargeTimeRemaining = batteryManager.computeChargeTimeRemaining()
            if (chargeTimeRemaining != Long.MAX_VALUE && chargeTimeRemaining > 0) {
                sb.appendLine("预计充满时间: ${formatTime(chargeTimeRemaining)}")
            }
        }
        sb.appendLine()
        
        // 通过 Intent 获取详细电池信息
        sb.appendLine("=== 详细电池信息 ===")
        
        val batteryStatus: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED), Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            requireContext().registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        }
        
        batteryStatus?.let { intent ->
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val statusText = when (status) {
                BatteryManager.BATTERY_STATUS_CHARGING -> "充电中"
                BatteryManager.BATTERY_STATUS_DISCHARGING -> "放电中"
                BatteryManager.BATTERY_STATUS_FULL -> "已充满"
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "未充电"
                else -> "未知($status)"
            }
            sb.appendLine("电池状态: $statusText")
            
            val chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val chargePlugText = when (chargePlug) {
                BatteryManager.BATTERY_PLUGGED_AC -> "交流电"
                BatteryManager.BATTERY_PLUGGED_USB -> "USB"
                BatteryManager.BATTERY_PLUGGED_WIRELESS -> "无线充电"
                else -> "未插电"
            }
            sb.appendLine("充电方式: $chargePlugText")
            
            val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
            val healthText = when (health) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "良好"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "过热"
                BatteryManager.BATTERY_HEALTH_DEAD -> "已损坏"
                else -> "未知($health)"
            }
            sb.appendLine("健康状态: $healthText")
            
            val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
            sb.appendLine("电池温度: ${temperature / 10.0}°C")
            
            val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
            sb.appendLine("电池电压: ${voltage}mV")
            
            val technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
            sb.appendLine("电池技术: $technology")
        }
        
        binding.tvBatteryInfo.text = sb.toString()
        
        val progress = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        binding.progressBattery.progress = progress
        binding.tvBatteryPercent.text = "$progress%"
        
        val color = if (isCharging) {
            resources.getColor(R.color.status_success, null)
        } else if (progress < 20) {
            resources.getColor(R.color.status_error, null)
        } else {
            resources.getColor(R.color.primary, null)
        }
        binding.progressBattery.setIndicatorColor(color)
        binding.tvBatteryPercent.setTextColor(color)
    }

    private fun formatTime(millis: Long): String {
        val minutes = millis / 60000
        val hours = minutes / 60
        val mins = minutes % 60
        return if (hours > 0) "${hours}小时${mins}分钟" else "${mins}分钟"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = BatteryFragment()
    }
}