package com.peter.context.demo.basic

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.peter.context.demo.R
import com.peter.context.demo.databinding.ActivityContextResourcesBinding

/**
 * Context 资源访问
 * 
 * 通过 Context 可以访问各种资源：
 * 1. 字符串资源 - getString()
 * 2. 颜色资源 - getColor()
 * 3. 尺寸资源 - getDimension(), getDimensionPixelSize()
 * 4. 图片资源 - getDrawable()
 * 5. 布局资源 - LayoutInflater
 * 6. 数组资源 - getStringArray(), getIntArray()
 * 7. 布尔值资源 - getBoolean()
 * 8. 整数资源 - getInteger()
 * 
 * Resources 类的获取：
 * - context.resources
 * - context.getResources()
 * - Resources.getSystem() (只能访问系统资源)
 */
class ContextResourcesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContextResourcesBinding
    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContextResourcesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        demonstrateResourceAccess()
    }

    private fun demonstrateResourceAccess() {
        sb.clear()
        
        // 1. 获取 Resources 对象
        sb.appendLine("=== 1. 获取 Resources 对象 ===")
        val resources = this.resources
        sb.appendLine("Resources: $resources")
        sb.appendLine("DisplayMetrics: ${resources.displayMetrics}")
        sb.appendLine("Configuration: ${resources.configuration}")
        sb.appendLine("locale: ${resources.configuration.locales[0]}")
        sb.appendLine()
        
        // 2. 字符串资源
        sb.appendLine("=== 2. 字符串资源 ===")
        val appName = getString(R.string.app_name)
        sb.appendLine("app_name: $appName")
        
        // 带参数的字符串
        val formattedString = getString(R.string.format_string, "Android", 14)
        sb.appendLine("格式化字符串: $formattedString")
        
        // 数量字符串
        val quantityString = resources.getQuantityString(R.plurals.apples, 1, 1)
        sb.appendLine("数量字符串 (1): $quantityString")
        val quantityString2 = resources.getQuantityString(R.plurals.apples, 5, 5)
        sb.appendLine("数量字符串 (5): $quantityString2")
        sb.appendLine()
        
        // 3. 颜色资源
        sb.appendLine("=== 3. 颜色资源 ===")
        val color = getColor(R.color.purple_500)
        sb.appendLine("purple_500 颜色值: #${
            Integer.toHexString(color).uppercase().padStart(8, '0')
        }")
        
        val colorStateList = getColorStateList(R.color.selector_color)
        sb.appendLine("ColorStateList: $colorStateList")
        sb.appendLine()
        
        // 4. 尺寸资源
        sb.appendLine("=== 4. 尺寸资源 ===")
        val dimension = resources.getDimension(R.dimen.padding_large)
        sb.appendLine("padding_large (float): ${dimension}px")
        
        val dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.padding_large)
        sb.appendLine("padding_large (int): ${dimensionPixelSize}px")
        
        val dimensionPixelOffset = resources.getDimensionPixelOffset(R.dimen.padding_large)
        sb.appendLine("padding_large (offset): ${dimensionPixelOffset}px")
        sb.appendLine("区别: getDimension返回float, getDimensionPixelSize四舍五入, getDimensionPixelOffset截断")
        sb.appendLine()
        
        // 5. 图片资源
        sb.appendLine("=== 5. 图片资源 ===")
        val drawable: Drawable? = getDrawable(R.drawable.ic_launcher_foreground)
        sb.appendLine("Drawable: $drawable")
        if (drawable != null) {
            sb.appendLine("IntrinsicWidth: ${drawable.intrinsicWidth}")
            sb.appendLine("IntrinsicHeight: ${drawable.intrinsicHeight}")
        }
        sb.appendLine()
        
        // 6. TypedArray - 样式属性
        sb.appendLine("=== 6. TypedArray (样式属性) ===")
        val typedArray = obtainStyledAttributes(
            intArrayOf(
                android.R.attr.colorPrimary,
                android.R.attr.colorAccent,
                android.R.attr.windowBackground
            )
        )
        val colorPrimary = typedArray.getColor(0, 0)
        val colorAccent = typedArray.getColor(1, 0)
        typedArray.recycle()
        sb.appendLine("colorPrimary: #${Integer.toHexString(colorPrimary)}")
        sb.appendLine("colorAccent: #${Integer.toHexString(colorAccent)}")
        sb.appendLine()
        
        // 7. 数组资源
        sb.appendLine("=== 7. 数组资源 ===")
        val stringArray = resources.getStringArray(R.array.planets)
        sb.appendLine("字符串数组: ${stringArray.joinToString(", ")}")
        
        val intArray = resources.getIntArray(R.array.numbers)
        sb.appendLine("整数数组: ${intArray.joinToString(", ")}")
        sb.appendLine()
        
        // 8. 布局资源
        sb.appendLine("=== 8. 布局资源 (LayoutInflater) ===")
        val inflater = LayoutInflater.from(this)
        sb.appendLine("LayoutInflater: $inflater")
        sb.appendLine("也可以通过: getSystemService(Context.LAYOUT_INFLATER_SERVICE)")
        sb.appendLine()
        
        // 9. Assets 资源
        sb.appendLine("=== 9. Assets 资源 ===")
        sb.appendLine("assets 目录文件访问:")
        sb.appendLine("  assets.list(\"\") - 列出文件")
        sb.appendLine("  assets.open(\"filename\") - 打开文件")
        sb.appendLine("  区别: assets 不生成 ID, res/ 会生成 R.xxx")
        sb.appendLine()
        
        // 10. Raw 资源
        sb.appendLine("=== 10. Raw 资源 ===")
        sb.appendLine("res/raw 目录下的文件:")
        sb.appendLine("  resources.openRawResource(R.raw.xxx)")
        sb.appendLine("  区别: raw 会生成 ID, assets 不会")
        sb.appendLine()
        
        // 11. 系统资源
        sb.appendLine("=== 11. 系统资源 ===")
        sb.appendLine("android.R.string.xxx - 系统字符串")
        sb.appendLine("android.R.color.xxx - 系统颜色")
        sb.appendLine("android.R.drawable.xxx - 系统图片")
        sb.appendLine("示例: ${getString(android.R.string.ok)}")
        sb.appendLine()
        
        // 12. 资源缓存和优化
        sb.appendLine("=== 12. 资源缓存说明 ===")
        sb.appendLine("Resources 对象会被缓存，多次调用 context.resources 返回同一实例")
        sb.appendLine("getDrawable() 返回的 Drawable 可能是共享的，不要修改")
        sb.appendLine("如需修改，使用 drawable.mutate() 创建副本")
        sb.appendLine()
        
        binding.tvContent.text = sb.toString()
        Log.d("ContextResources", sb.toString())
    }
}
