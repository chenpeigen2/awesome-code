package com.peter.context.demo.deep

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AppCompatActivity
import com.peter.context.demo.R
import com.peter.context.demo.databinding.ActivityContextConfigurationBinding

/**
 * Context 动态修改 DPI/字体大小示例
 * 
 * 核心原理：
 * 通过修改 Context 的 Configuration 中的 fontScale 来动态改变字体渲染大小
 * sp 单位 = density * fontScale，所以修改 fontScale 可以改变字体大小
 * 
 * 三种实现方式：
 * 1. createConfigurationContext() - 创建新 Context
 * 2. ContextThemeWrapper - 包装 Context
 * 3. Resources.updateConfiguration() - 全局更新（已废弃，但简单有效）
 */
class ContextConfigurationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContextConfigurationBinding
    private val sb = StringBuilder()
    
    // 当前字体缩放比例
    private var currentFontScale = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContextConfigurationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        showDisplayMetrics()
    }

    private fun setupListeners() {
        binding.btnScaleSmall.setOnClickListener { applyFontScale(0.85f) }
        binding.btnScaleNormal.setOnClickListener { applyFontScale(1.0f) }
        binding.btnScaleLarge.setOnClickListener { applyFontScale(1.3f) }
        binding.btnScaleHuge.setOnClickListener { applyFontScale(1.5f) }
    }

    /**
     * 显示当前 DisplayMetrics 信息
     */
    private fun showDisplayMetrics() {
        sb.clear()
        
        val dm = resources.displayMetrics
        val config = resources.configuration
        
        sb.appendLine("=== DisplayMetrics ===")
        sb.appendLine("density: ${dm.density}")
        sb.appendLine("densityDpi: ${dm.densityDpi}")
        sb.appendLine("scaledDensity: ${dm.scaledDensity}")
        sb.appendLine("widthPixels: ${dm.widthPixels}")
        sb.appendLine("heightPixels: ${dm.heightPixels}")
        sb.appendLine()
        
        sb.appendLine("=== Configuration ===")
        sb.appendLine("fontScale: ${config.fontScale}")
        sb.appendLine("densityDpi: ${config.densityDpi}")
        sb.appendLine()
        
        sb.appendLine("=== 核心公式 ===")
        sb.appendLine("1dp = density px")
        sb.appendLine("1sp = density * fontScale px")
        sb.appendLine()
        sb.appendLine("修改 fontScale 即可改变字体大小")
        
        binding.tvDisplayMetrics.text = sb.toString()
        
        updateDemoText()
    }

    /**
     * 更新演示文本
     */
    private fun updateDemoText() {
        val dm = resources.displayMetrics
        val config = resources.configuration
        
        sb.clear()
        sb.appendLine("=== 当前字体渲染 ===")
        sb.appendLine("fontScale: ${config.fontScale}")
        sb.appendLine("scaledDensity: ${dm.scaledDensity}")
        sb.appendLine()
        sb.appendLine("以下文字使用 sp 单位定义：")
        sb.appendLine("会随 fontScale 改变而改变大小")
        sb.appendLine()
        sb.appendLine("14sp 正文文字示例")
        sb.appendLine("16sp 标题文字示例")
        sb.appendLine("18sp 大标题示例")
        
        binding.tvSpDemo.text = sb.toString()
        
        // dp vs sp 对比
        sb.clear()
        sb.appendLine("=== dp 与 sp 对比 ===")
        
        val dp16Px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, dm)
        val sp16Px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16f, dm)
        
        sb.appendLine("16dp = ${dp16Px.toInt()}px (固定)")
        sb.appendLine("16sp = ${sp16Px.toInt()}px (随 fontScale 变化)")
        sb.appendLine("差值: ${(sp16Px - dp16Px).toInt()}px")
        sb.appendLine()
        sb.appendLine("dp: 用于布局尺寸，不受字体设置影响")
        sb.appendLine("sp: 用于字体大小，随用户设置变化")
        
        binding.tvDpVsSp.text = sb.toString()
    }

    /**
     * 应用字体缩放 - 使用三种方式演示
     */
    private fun applyFontScale(scale: Float) {
        currentFontScale = scale
        sb.clear()
        sb.appendLine("=== 应用字体缩放: $scale ===\n")
        
        // ===== 方式1: createConfigurationContext =====
        sb.appendLine("【方式1】createConfigurationContext()")
        val config1 = Configuration(resources.configuration)
        config1.fontScale = scale
        val context1 = createConfigurationContext(config1)
        val dm1 = context1.resources.displayMetrics
        sb.appendLine("  新 Context 的 scaledDensity: ${dm1.scaledDensity}")
        sb.appendLine("  16sp = ${TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16f, dm1).toInt()}px")
        sb.appendLine()
        
        // ===== 方式2: ContextThemeWrapper =====
        sb.appendLine("【方式2】ContextThemeWrapper")
        val config2 = Configuration(resources.configuration)
        config2.fontScale = scale
        val contextWrapper = object : ContextThemeWrapper(this, R.style.Theme_Context) {
            override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
                overrideConfiguration?.fontScale = scale
                super.applyOverrideConfiguration(overrideConfiguration)
            }
        }
        sb.appendLine("  包装后的 Context 可用于 LayoutInflater")
        sb.appendLine("  适合局部 View 使用不同字体大小")
        sb.appendLine()
        
        // ===== 方式3: updateConfiguration (全局生效) =====
        sb.appendLine("【方式3】Resources.updateConfiguration()")
        sb.appendLine("  全局修改，需要 recreate Activity")
        
        // 执行全局更新
        val config3 = Configuration(resources.configuration)
        config3.fontScale = scale
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config3, resources.displayMetrics)
        
        sb.appendLine("  已调用 updateConfiguration()")
        sb.appendLine("  正在 recreate...")
        
        binding.tvResult.text = sb.toString()
        Log.d("ContextConfiguration", sb.toString())
        
        // recreate 以应用新的配置
        recreate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d("ContextConfiguration", "Config changed: fontScale=${newConfig.fontScale}")
        
        // 更新显示
        binding.tvDisplayMetrics.post {
            showDisplayMetrics()
        }
    }
    
    /**
     * 扩展函数：创建指定字体缩放的 Context
     */
    private fun Context.createScaledContext(fontScale: Float): Context {
        val config = Configuration(resources.configuration)
        config.fontScale = fontScale
        return createConfigurationContext(config)
    }
    
    /**
     * 扩展函数：获取指定缩放下的 TextView 尺寸
     */
    private fun Context.getScaledTextSize(sp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            resources.displayMetrics
        )
    }
}

