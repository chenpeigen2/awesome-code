package com.peter.autodensity.demo

import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.peter.autodensity.IDensity

/**
 * Demo Activity
 * 展示 AutoDensity 的使用效果
 */
class MainActivity : AppCompatActivity(), IDensity {

    private lateinit var tvDensityInfo: TextView
    private lateinit var tvDeviceInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDensityInfo = findViewById(R.id.tv_density_info)
        tvDeviceInfo = findViewById(R.id.tv_device_info)

        updateInfo()
    }

    override fun shouldAdaptAutoDensity(): Boolean = true

    /**
     * 可选：设置基准宽度，用于等比缩放
     * 例如：以 360dp 为基准进行缩放
     */
    override fun getRatioUiBaseWidthDp(): Int = 360

    private fun updateInfo() {
        val dm: DisplayMetrics = resources.displayMetrics
        val config = resources.configuration

        val densityInfo = buildString {
            appendLine("=== 密度信息 ===")
            appendLine("density: ${dm.density}")
            appendLine("densityDpi: ${dm.densityDpi}")
            appendLine("scaledDensity: ${dm.scaledDensity}")
            appendLine("widthPixels: ${dm.widthPixels}")
            appendLine("heightPixels: ${dm.heightPixels}")
            appendLine("xDpi: ${dm.xdpi}")
            appendLine("yDpi: ${dm.ydpi}")
            appendLine()
            appendLine("=== Configuration ===")
            appendLine("screenWidthDp: ${config.screenWidthDp}")
            appendLine("screenHeightDp: ${config.screenHeightDp}")
            appendLine("densityDpi: ${config.densityDpi}")
            appendLine("fontScale: ${config.fontScale}")
        }

        val deviceInfo = buildString {
            appendLine("=== 设备信息 ===")
            appendLine("品牌: ${android.os.Build.BRAND}")
            appendLine("型号: ${android.os.Build.MODEL}")
            appendLine("设备: ${android.os.Build.DEVICE}")
            appendLine("SDK: ${android.os.Build.VERSION.SDK_INT}")
            appendLine()
            appendLine("计算得出的dp宽度:")
            appendLine("${dm.widthPixels / dm.density} dp")
        }

        tvDensityInfo.text = densityInfo
        tvDeviceInfo.text = deviceInfo
    }
}
