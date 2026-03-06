package com.peter.autodensity.demo

import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.peter.autodensity.api.ActivityDensityAware
import com.peter.autodensity.api.AutoDensity

/**
 * Demo Activity
 * 展示 AutoDensity 的使用效果
 */
class MainActivity : AppCompatActivity(), ActivityDensityAware {

    private lateinit var tvDensityInfo: TextView
    private lateinit var tvDeviceInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDensityInfo = findViewById(R.id.tv_density_info)
        tvDeviceInfo = findViewById(R.id.tv_device_info)

        updateInfo()
    }

    // 启用密度适配
    override fun shouldAdaptDensity(): Boolean = true

    // 返回 -1 表示使用屏幕实际 dp 宽度作为基准（默认行为）
    // 返回 0 表示不限制
    // 返回具体值表示使用该值作为基准
    override fun getBaseWidthDp(): Int = -1

    override fun forceDesignWidth(): Boolean = true

    private fun updateInfo() {
        val dm: DisplayMetrics = resources.displayMetrics
        val config = resources.configuration

        // 获取计算结果（包含原始值和目标值）
        val result = AutoDensity.getLastResult()

        val densityInfo = buildString {
            appendLine("=== 密度对比 ===")

            if (result != null) {
                val original = result.original
                appendLine()
                appendLine("【原始值 (修改前)】")
                appendLine("density:       ${original.density}")
                appendLine("densityDpi:    ${original.densityDpi}")
                appendLine("scaledDensity: ${original.scaledDensity}")
                appendLine("屏幕宽度:      ${String.format("%.2f", original.widthDp)} dp")
                appendLine()
                appendLine("【目标值 (修改后)】")
                appendLine("density:       ${result.targetDensity}")
                appendLine("densityDpi:    ${result.targetDpi}")
                appendLine("scaledDensity: ${result.targetScaledDensity}")
                appendLine("缩放比例:      ${String.format("%.4f", result.scale)}")
                appendLine("baseWidthDp:   ${result.baseWidthDp}")
                appendLine("forceDesign:   ${result.forceDesignWidth}")
                appendLine()
                appendLine("【当前实际值】")
                appendLine("density:       ${dm.density}")
                appendLine("densityDpi:    ${dm.densityDpi}")
                appendLine("scaledDensity: ${dm.scaledDensity}")
            } else {
                appendLine("density: ${dm.density}")
                appendLine("densityDpi: ${dm.densityDpi}")
                appendLine("scaledDensity: ${dm.scaledDensity}")
            }

            appendLine()
            appendLine("widthPixels: ${dm.widthPixels}")
            appendLine("heightPixels: ${dm.heightPixels}")
            appendLine()
            appendLine("=== Configuration ===")
            appendLine("screenWidthDp: ${config.screenWidthDp}")
            appendLine("screenHeightDp: ${config.screenHeightDp}")
            appendLine("fontScale: ${config.fontScale}")
        }

        val deviceInfo = buildString {
            appendLine("=== 设备信息 ===")
            appendLine("品牌: ${android.os.Build.BRAND}")
            appendLine("型号: ${android.os.Build.MODEL}")
            appendLine("设备: ${android.os.Build.DEVICE}")
            appendLine("SDK: ${android.os.Build.VERSION.SDK_INT}")
            appendLine()
            appendLine("=== 计算说明 ===")
            val result = AutoDensity.getLastResult()
            if (result != null) {
                appendLine("designWidthDp = ${result.original.widthPixels / result.targetDensity}")
            }
            appendLine("targetDensity = screenWidth / designWidthDp")
            appendLine("             = ${dm.widthPixels} / ${dm.widthPixels / dm.density}")
            appendLine("             = ${dm.density}")
            appendLine()
            appendLine("当前屏幕宽度:")
            appendLine("${String.format("%.2f", dm.widthPixels / dm.density)} dp")
        }

        tvDensityInfo.text = densityInfo
        tvDeviceInfo.text = deviceInfo
    }
}
