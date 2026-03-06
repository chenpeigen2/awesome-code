package com.peter.autodensity

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.fragment.app.FragmentManager
import java.lang.ref.WeakReference

/**
 * 自动密度适配核心类
 *
 * 使用方法：
 * 1. 在Application.onCreate中初始化:
 *    AutoDensity.init(this)
 *
 * 2. Activity或Application实现IDensity接口来控制是否启用适配:
 *    class MyActivity : AppCompatActivity(), IDensity {
 *        override fun shouldAdaptAutoDensity() = true
 *    }
 *
 * 3. 可选：配置自定义参数
 *    AutoDensity.setForceDeviceScale(1.1f)
 *    AutoDensity.setForcePPI(400)
 */
class AutoDensity private constructor(private val application: Application) {

    companion object {
        private const val TAG_CONFIG_CHANGE_FRAGMENT = "AutoDensity_ConfigChangeFragment"

        @Volatile
        private var sInstance: AutoDensity? = null
        private var sUpdateSystemResources = true

        /**
         * 初始化自动密度适配
         * @param application Application实例
         * @param updateSystemResource 是否更新系统资源，默认true
         */
        @MainThread
        fun init(application: Application, updateSystemResource: Boolean = true): AutoDensity {
            if (sInstance == null) {
                synchronized(AutoDensity::class.java) {
                    if (sInstance == null) {
                        sUpdateSystemResources = updateSystemResource
                        sInstance = AutoDensity(application)
                    }
                }
            }
            return sInstance!!
        }

        /**
         * 获取实例
         */
        fun getInstance(): AutoDensity? = sInstance

        /**
         * 是否应该更新系统资源
         */
        fun shouldUpdateSystemResource(): Boolean = sUpdateSystemResources

        /**
         * 设置是否更新系统资源
         */
        fun setUpdateSystemRes(update: Boolean) {
            val configManager = DensityConfigManager.getInstance()
            if (update) {
                configManager.targetConfig?.let { DensityUtil.setSystemResources(it) }
            } else {
                configManager.originConfig?.let { DensityUtil.setSystemResources(it) }
            }
        }

        /**
         * 设置强制设备缩放值
         * 需要在init之前调用
         */
        @AnyThread
        fun setForceDeviceScale(deviceScale: Float) {
            DensityConfigManager.getInstance().setUserDeviceScale(deviceScale)
        }

        /**
         * 设置强制PPI
         * 需要在init之前调用
         */
        @AnyThread
        fun setForcePPI(ppi: Int) {
            DensityConfigManager.getInstance().setUserPPI(ppi)
        }

        /**
         * 创建自动密度适配的Context包装器
         */
        @MainThread
        fun createAutoDensityContextWrapper(context: Context): Context {
            return createAutoDensityContextWrapper(context, 0, 0)
        }

        /**
         * 创建自动密度适配的Context包装器
         * @param context 原始Context
         * @param themeId 主题ID
         */
        @MainThread
        fun createAutoDensityContextWrapper(context: Context, themeId: Int): Context {
            return createAutoDensityContextWrapper(context, themeId, 0)
        }

        /**
         * 创建自动密度适配的Context包装器
         * @param context 原始Context
         * @param baseWidthDp 基准宽度(dp)，用于等比缩放
         */
        @MainThread
        fun createAutoDensityContextWrapperWithBaseDp(context: Context, baseWidthDp: Int): Context {
            return createAutoDensityContextWrapper(context, 0, baseWidthDp)
        }

        /**
         * 创建自动密度适配的Context包装器
         */
        @MainThread
        fun createAutoDensityContextWrapper(context: Context, themeId: Int, baseWidthDp: Int): Context {
            val contextConfig = context.resources.configuration
            val originConfig = Configuration(contextConfig)

            val configManager = DensityConfigManager.getInstance()
            if (configManager.targetConfig == null) {
                configManager.init(context)
            }

            val contextWrapper = AutoDensityContextWrapper(context, themeId)
            configManager.updateConfig(context, contextConfig)
            contextWrapper.setOriginConfiguration(originConfig)
            DensityUtil.updateCustomDensity(contextWrapper, baseWidthDp)
            return contextWrapper
        }

        /**
         * 更新Configuration的密度覆盖
         */
        fun updateDensityOverrideConfiguration(context: Context, newConfig: Configuration): Configuration {
            val baseConfig = DensityUtil.getNoDensityOverrideConfiguration(context) ?: newConfig
            if (DensityUtil.shouldUpdateDensityForConfig(baseConfig)) {
                val updatedConfig = Configuration(baseConfig)
                DensityUtil.updateDensityForConfig(context, updatedConfig)
                return updatedConfig
            }
            return newConfig
        }

        /**
         * 更新密度
         */
        fun updateDensity(context: Context?) {
            if (sInstance == null || context == null) return

            val supportAD = when {
                context is Activity && context is IDensity -> context.shouldAdaptAutoDensity()
                context.applicationContext is IDensity -> (context.applicationContext as IDensity).shouldAdaptAutoDensity()
                else -> false
            }

            if (supportAD) {
                forceUpdateDensity(context)
            }
        }

        /**
         * 强制更新密度
         */
        fun forceUpdateDensity(context: Context?) {
            if (sInstance != null && context != null) {
                DensityUtil.updateCustomDensity(context)
            }
        }

        /**
         * 通过Configuration更新密度
         */
        fun updateDensityByConfig(context: Context?, newConfig: Configuration): Boolean {
            val instance = sInstance ?: return false
            if (context == null) return false
            return instance.updateDensityOnConfigChanged(context, newConfig)
        }
    }

    private val displayManager: DisplayManager by lazy {
        application.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }
    private val uiHandler = Handler(Looper.getMainLooper())
    private val densityCallbacks = HashMap<Int, DensityCallback>()

    init {
        prepareInApplication(application)
        application.registerActivityLifecycleCallbacks(AutoDensityLifecycleCallbacks())
        application.registerComponentCallbacks(object : ComponentCallbacks {
            override fun onConfigurationChanged(newConfig: Configuration) {
                processOnAppConfigChanged(application, newConfig)
            }

            override fun onLowMemory() {
                // nothing
            }
        })
    }

    private fun prepareInApplication(application: Application) {
        DebugUtil.printDensityLog("AutoDensity prepareInApplication")
        DensityConfigManager.getInstance().init(application)
        if (isShouldAdaptAutoDensity(application)) {
            DensityUtil.updateCustomDensity(application)
        }
    }

    private fun processOnAppConfigChanged(application: Application, newConfig: Configuration) {
        DebugUtil.printDensityLog("AutoDensity processOnAppConfigChanged")
        DensityConfigManager.getInstance().tryUpdateConfig(application, newConfig)
        if (isShouldAdaptAutoDensity(application)) {
            DensityUtil.updateCustomDensity(application)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                DensityConfigManager.getInstance().targetConfig?.let {
                    newConfig.densityDpi = it.densityDpi
                }
            }
        }
    }

    private fun processOnActivityCreated(activity: Activity) {
        DebugUtil.printDensityLog("processOnActivityCreated")
        val appSupportAD = isShouldAdaptAutoDensity(activity.application)
        var supportAD = appSupportAD

        if (activity is IDensity) {
            supportAD = activity.shouldAdaptAutoDensity()
        }

        updateApplicationDensity(activity.application)

        if (supportAD) {
            DensityUtil.updateCustomDensity(activity)
            registerCallback(activity)
            addForOnConfigurationChange(activity)
        } else if (appSupportAD) {
            DensityUtil.restoreDefaultDensity(activity)
            registerCallback(activity)
            addForOnConfigurationChange(activity)
        }
    }

    private fun processBeforeActivityConfigChanged(activity: Activity, newConfig: Configuration) {
        DebugUtil.printDensityLog("processBeforeActivityConfigChanged")

        val supportAD = if (activity is IDensity) {
            activity.shouldAdaptAutoDensity()
        } else {
            isShouldAdaptAutoDensity(activity.application)
        }

        if (supportAD) {
            DensityUtil.updateCustomDensity(activity)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                changeCurrentConfig(activity)
            }
        }
    }

    private fun processOnActivityDestroyed(activity: Activity) {
        unregisterCallback(activity)
    }

    private fun processOnActivityDisplayChanged(displayId: Int, activity: Activity) {
        DebugUtil.printDensityLog("onDisplayChanged displayId: $displayId, activity: $activity")

        val supportAD = if (activity is IDensity) {
            activity.shouldAdaptAutoDensity()
        } else {
            isShouldAdaptAutoDensity(activity.application)
        }

        if (supportAD) {
            DensityUtil.updateCustomDensity(activity)
        }
    }

    private fun registerCallback(activity: Activity) {
        val key = activity.hashCode()
        if (densityCallbacks.containsKey(key)) return

        val callback = DensityCallback(activity, this)
        densityCallbacks[key] = callback
        displayManager.registerDisplayListener(callback, uiHandler)
        activity.application.registerComponentCallbacks(callback)
        activity.registerComponentCallbacks(callback)
    }

    private fun unregisterCallback(activity: Activity) {
        val key = activity.hashCode()
        val callback = densityCallbacks.remove(key) ?: return

        DebugUtil.printDensityLog("unregisterCallback: $callback")
        displayManager.unregisterDisplayListener(callback)
        activity.application.unregisterComponentCallbacks(callback)
        activity.unregisterComponentCallbacks(callback)
        callback.clear()
    }

    private fun addForOnConfigurationChange(activity: Activity) {
        try {
            val fragmentManager: FragmentManager? = try {
                // 尝试使用androidx FragmentManager
                val fragActivity = activity as? androidx.fragment.app.FragmentActivity
                fragActivity?.supportFragmentManager
            } catch (e: Exception) {
                null
            }

            if (fragmentManager != null) {
                var fragment = fragmentManager.findFragmentByTag(TAG_CONFIG_CHANGE_FRAGMENT)
                        as? ConfigurationChangeFragment
                if (fragment == null) {
                    fragment = ConfigurationChangeFragment.newInstance()
                    fragment.setAutoDensity(this)
                    fragmentManager.beginTransaction()
                        .add(fragment, TAG_CONFIG_CHANGE_FRAGMENT)
                        .commitAllowingStateLoss()
                } else {
                    fragment.setAutoDensity(this)
                }
            }
        } catch (e: Exception) {
            DebugUtil.printDensityLog("addForOnConfigurationChange failed: $e")
        }
    }

    private fun changeCurrentConfig(activity: Activity) {
        try {
            val config: Configuration = ReflectionHelper.getFieldValue(
                Activity::class.java, activity, "mCurrentConfig"
            )
            val target = DensityConfigManager.getInstance().targetConfig ?: return
            config.densityDpi = target.densityDpi

            @Suppress("DEPRECATION")
            val info: ActivityInfo = ReflectionHelper.getFieldValue(
                Activity::class.java, activity, "mActivityInfo"
            )
            if ((info.configChanges and ActivityInfo.CONFIG_DENSITY) == 0) {
                info.configChanges = info.configChanges or ActivityInfo.CONFIG_DENSITY
            }
        } catch (e: Exception) {
            DebugUtil.printDensityLog("changeCurrentConfig failed: $e")
        }
    }

    private fun updateApplicationDensity(application: Application) {
        // Android Q上小窗还原application density
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            if (isShouldAdaptAutoDensity(application)) {
                DensityUtil.updateCustomDensity(application)
            }
        }
    }

    private fun isShouldAdaptAutoDensity(application: Application): Boolean {
        return if (application is IDensity) {
            application.shouldAdaptAutoDensity()
        } else {
            true // 默认启用
        }
    }

    fun updateDensityOnConfigChanged(context: Context?, newConfig: Configuration): Boolean {
        val result = DensityConfigManager.getInstance().tryUpdateConfig(context ?: return false, newConfig)

        if (context is Activity) {
            val application = context.application
            if (isShouldAdaptAutoDensity(application)) {
                updateApplicationDensity(application)
            }
        }

        updateDensity(context)
        return result
    }

    /**
     * Activity生命周期回调
     */
    private inner class AutoDensityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            processOnActivityCreated(activity)
        }

        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {
            processOnActivityDestroyed(activity)
        }
    }

    /**
     * 密度回调，监听Display和Configuration变化
     */
    private inner class DensityCallback(
        activity: Activity,
        private val autoDensity: AutoDensity
    ) : DisplayManager.DisplayListener, ComponentCallbacks {

        private var activityRef: WeakReference<Activity>? = WeakReference(activity)

        override fun onDisplayAdded(displayId: Int) {}
        override fun onDisplayRemoved(displayId: Int) {}

        override fun onDisplayChanged(displayId: Int) {
            val activity = activityRef?.get()
            if (activity != null) {
                autoDensity.processOnActivityDisplayChanged(displayId, activity)
            } else {
                displayManager.unregisterDisplayListener(this)
            }
        }

        override fun onConfigurationChanged(config: Configuration) {
            val activity = activityRef?.get()
            if (activity != null) {
                autoDensity.processBeforeActivityConfigChanged(activity, config)
            } else {
                displayManager.unregisterDisplayListener(this)
            }
        }

        override fun onLowMemory() {}

        fun clear() {
            activityRef?.clear()
            activityRef = null
        }
    }
}
