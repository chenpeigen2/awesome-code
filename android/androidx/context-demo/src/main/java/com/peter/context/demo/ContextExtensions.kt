package com.peter.context.demo

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.peter.context.demo.deep.ContextConfigurationActivity

/**
 * Context 扩展函数集合
 * 
 * 提供常用的 Context 扩展函数，简化代码编写
 */

// ==================== Toast 扩展 ====================

/**
 * 显示短时 Toast
 */
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * 显示短时 Toast (资源ID)
 */
fun Context.toast(@StringRes resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

/**
 * 显示长时 Toast
 */
fun Context.longToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

// ==================== SharedPreferences 扩展 ====================

/**
 * 获取 SharedPreferences
 */
fun Context.getPrefs(name: String = "default"): SharedPreferences {
    return getSharedPreferences(name, Context.MODE_PRIVATE)
}

/**
 * SharedPreferences 编辑扩展
 */
fun SharedPreferences.edit(block: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.block()
    editor.apply()
}

// ==================== 资源扩展 ====================

/**
 * 获取颜色
 */
fun Context.color(@ColorRes resId: Int): Int {
    return ContextCompat.getColor(this, resId)
}

/**
 * 获取 Drawable
 */
fun Context.drawable(@DrawableRes resId: Int): android.graphics.drawable.Drawable? {
    return ContextCompat.getDrawable(this, resId)
}

/**
 * 获取尺寸 (float)
 */
fun Context.dimen(@DimenRes resId: Int): Float {
    return resources.getDimension(resId)
}

/**
 * 获取尺寸 (pixel)
 */
fun Context.dimenPx(@DimenRes resId: Int): Int {
    return resources.getDimensionPixelSize(resId)
}

// ==================== 单位转换扩展 ====================

/**
 * dp 转 px
 */
fun Context.dpToPx(dp: Float): Float {
    return dp * resources.displayMetrics.density
}

/**
 * dp 转 px (Int)
 */
fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density + 0.5f).toInt()
}

/**
 * px 转 dp
 */
fun Context.pxToDp(px: Float): Float {
    return px / resources.displayMetrics.density
}

/**
 * sp 转 px
 */
fun Context.spToPx(sp: Float): Float {
    return sp * resources.displayMetrics.scaledDensity
}

/**
 * px 转 sp
 */
fun Context.pxToSp(px: Float): Float {
    return px / resources.displayMetrics.scaledDensity
}

// ==================== 屏幕相关扩展 ====================

/**
 * 获取屏幕宽度 (像素)
 */
fun Context.getScreenWidth(): Int {
    return resources.displayMetrics.widthPixels
}

/**
 * 获取屏幕高度 (像素)
 */
fun Context.getScreenHeight(): Int {
    return resources.displayMetrics.heightPixels
}

/**
 * 获取屏幕密度
 */
fun Context.getScreenDensity(): Float {
    return resources.displayMetrics.density
}

/**
 * 获取屏幕密度 DPI
 */
fun Context.getScreenDensityDpi(): Int {
    return resources.displayMetrics.densityDpi
}

// ==================== 线程相关扩展 ====================

/**
 * 判断是否主线程
 */
fun Context.isMainThread(): Boolean {
    return Looper.myLooper() == Looper.getMainLooper()
}

/**
 * 在主线程执行
 */
fun Context.runOnMainThread(block: () -> Unit) {
    if (isMainThread()) {
        block()
    } else {
        Handler(Looper.getMainLooper()).post(block)
    }
}

/**
 * 延迟在主线程执行
 */
fun Context.runOnMainThreadDelayed(delayMillis: Long, block: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(block, delayMillis)
}

// ==================== Activity 相关扩展 ====================

/**
 * 获取 Activity
 * 从 ContextWrapper 链中查找 Activity
 */
fun Context.getActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) {
            return ctx
        }
        ctx = ctx.baseContext
    }
    return null
}

/**
 * 判断是否为 Activity Context
 */
fun Context.isActivityContext(): Boolean {
    return getActivity() != null
}

// ==================== 权限相关扩展 ====================

/**
 * 检查是否有权限
 */
fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

/**
 * 检查是否有多个权限
 */
fun Context.hasPermissions(vararg permissions: String): Boolean {
    return permissions.all { hasPermission(it) }
}

// ==================== Intent 相关扩展 ====================

/**
 * 安全启动 Activity
 * 检查是否有应用可以处理 Intent
 */
fun Context.safeStartActivity(intent: Intent): Boolean {
    return try {
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
            true
        } else {
            false
        }
    } catch (e: Exception) {
        false
    }
}

// ==================== Context 类型判断 ====================

/**
 * 获取 Context 类型名称
 */
fun Context.getContextTypeName(): String {
    return when (this) {
        is Activity -> "Activity"
        is android.app.Service -> "Service"
        is android.app.Application -> "Application"
        is ContextWrapper -> {
            val base = baseContext
            "ContextWrapper(base=${base.getContextTypeName()})"
        }
        else -> javaClass.simpleName
    }
}

/**
 * 判断是否为 Application Context
 */
fun Context.isApplicationContext(): Boolean {
    return this is android.app.Application || 
           (this is ContextWrapper && baseContext is android.app.Application)
}

// ==================== Activity Intent 扩展 ====================

/**
 * 创建 ContextConfigurationActivity 的 Intent
 */
fun createContextConfigurationIntent(context: Context): Intent {
    return Intent(context, ContextConfigurationActivity::class.java)
}
