package com.peter.autodensity

import android.app.ResourcesManager
import android.content.Context
import android.content.res.CompatibilityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.ResourcesImpl
import android.content.res.ResourcesKey
import android.content.res.loader.ResourcesLoader
import android.graphics.Bitmap
import android.os.Build
import android.util.ArrayMap
import android.util.Log
import android.view.ContextThemeWrapper
import java.lang.ref.WeakReference

/**
 * 密度工具类
 * 核心功能：修改Resources的density配置
 */
object DensityUtil {

    private var sResourcesManager: ResourcesManager? = null
    private var sResourcesImpls: ArrayMap<ResourcesKey, WeakReference<ResourcesImpl>>? = null
    private var sLock: Any? = null

    init {
        try {
            sResourcesManager = ResourcesManager.getInstance()
            @Suppress("UNCHECKED_CAST")
            sResourcesImpls = ReflectionHelper.getFieldValue(
                ResourcesManager::class.java,
                sResourcesManager,
                "mResourceImpls"
            )
            sLock = sResourcesManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                sLock = ReflectionHelper.getFieldValue(
                    ResourcesManager::class.java,
                    sResourcesManager,
                    "mLock"
                )
            }
        } catch (e: Exception) {
            Log.w(DebugUtil.TAG, "ResourcesManager reflection failed: ${e.message}")
        }

        if (sResourcesManager == null || sResourcesImpls == null || sLock == null) {
            Log.w(DebugUtil.TAG, "ResourcesManager reflection failed, some features may not work")
        }
    }

    /**
     * 恢复默认密度
     */
    fun restoreDefaultDensity(context: Context?) {
        if (context == null) {
            Log.w(DebugUtil.TAG, "restoreDefaultDensity: context is null")
            return
        }
        if (DensityConfigManager.getInstance().isAutoDensityEnabled) {
            restoreDensity(context.resources)
        }
    }

    /**
     * 更新自定义密度
     */
    fun updateCustomDensity(context: Context?) {
        if (context == null) {
            Log.w(DebugUtil.TAG, "updateCustomDensity: context is null")
            return
        }
        if (DensityConfigManager.getInstance().isAutoDensityEnabled) {
            var baseWidthDp = 0
            if (context is IDensity) {
                baseWidthDp = context.getRatioUiBaseWidthDp()
            }
            changeDensity(context.resources, baseWidthDp)
        }
    }

    /**
     * 更新自定义密度（指定基准宽度）
     */
    fun updateCustomDensity(context: Context?, baseWidthDp: Int) {
        if (context == null) {
            Log.w(DebugUtil.TAG, "updateCustomDensity: context is null")
            return
        }
        if (DensityConfigManager.getInstance().isAutoDensityEnabled) {
            changeDensity(context.resources, baseWidthDp)
        }
    }

    /**
     * 检查是否需要为Configuration更新密度
     */
    fun shouldUpdateDensityForConfig(newConfig: Configuration): Boolean {
        val target = DensityConfigManager.getInstance().targetConfig
        return target != null && newConfig.densityDpi != target.densityDpi
    }

    /**
     * 为Configuration更新密度
     */
    fun updateDensityForConfig(context: Context, newConfig: Configuration): Boolean {
        val target = DensityConfigManager.getInstance().targetConfig ?: return false
        val resources = context.resources
        resources.configuration.setTo(newConfig)
        doChangeDensity(target, resources, 0)
        return true
    }

    /**
     * 查找AutoDensityContextWrapper
     */
    fun findAutoDensityContextWrapper(context: Context): AutoDensityContextWrapper? {
        var currentContext = context
        while (currentContext is ContextThemeWrapper) {
            if (currentContext is AutoDensityContextWrapper) {
                return currentContext
            }
            currentContext = currentContext.baseContext
            if (currentContext !is ContextThemeWrapper) break
        }
        return null
    }

    /**
     * 获取未覆盖密度的Configuration
     */
    fun getNoDensityOverrideConfiguration(context: Context): Configuration? {
        val autoDensityContext = findAutoDensityContextWrapper(context) ?: return null
        return autoDensityContext.getNoOverrideConfiguration()
    }

    private fun restoreDensity(resources: Resources) {
        val defaultConfig = DensityConfigManager.getInstance().originConfig ?: return
        doChangeDensity(defaultConfig, resources, 0)
    }

    private fun changeDensity(resources: Resources, baseWidthDp: Int) {
        val target = DensityConfigManager.getInstance().targetConfig ?: return

        if (baseWidthDp > 0 || resources.displayMetrics.densityDpi != target.densityDpi) {
            doChangeDensity(target, resources, baseWidthDp)
            if (AutoDensity.shouldUpdateSystemResource()) {
                setSystemResources(target)
            }
        }
    }

    /**
     * 执行密度变更
     */
    fun doChangeDensity(target: DisplayConfig, resources: Resources, baseWidthDp: Int) {
        tryToCreateAndSetResourcesImpl(resources, target)

        // 在tryToCreateAndSetResourcesImpl之后获取
        val currentDM = resources.displayMetrics
        val currentConfig = resources.configuration

        var ratio = 1.0f
        val widthDp = currentDM.widthPixels * 1.0f / target.density
        if (baseWidthDp > 0 && widthDp < baseWidthDp) {
            ratio = currentDM.widthPixels / (baseWidthDp * target.density)
        }

        currentDM.densityDpi = (target.densityDpi * ratio).toInt()
        currentDM.density = target.density * ratio
        currentDM.scaledDensity = target.scaledDensity * ratio
        currentConfig.densityDpi = (target.densityDpi * ratio).toInt()
        currentConfig.fontScale = target.fontScale

        DebugUtil.printDensityLog(
            "doChangeDensity: baseWidthDp=$baseWidthDp, ratio=$ratio, " +
                    "densityDpi=${currentDM.densityDpi}, density=${currentDM.density}"
        )
    }

    /**
     * 尝试创建并设置ResourcesImpl
     */
    private fun tryToCreateAndSetResourcesImpl(resources: Resources, target: DisplayConfig) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return
        if (sResourcesManager == null || sResourcesImpls == null || sLock == null) return

        try {
            synchronized(sLock!!) {
                val oldImpl: ResourcesImpl = ReflectionHelper.getFieldValue(
                    Resources::class.java, resources, "mResourcesImpl"
                )
                val oldKey = findResourcesKeyByResourcesImplLocked(oldImpl)
                DebugUtil.printDensityLog("oldKey: $oldKey")

                if (oldKey != null) {
                    val newImpl = findOrCreateResourcesImplForKeyLocked(oldKey, target)
                    if (newImpl != null) {
                        ReflectionHelper.invoke(
                            Resources::class.java, resources, "setImpl",
                            arrayOf(ResourcesImpl::class.java), newImpl
                        )
                        DebugUtil.printDensityLog("setImpl success: $newImpl")
                    }
                }
            }
        } catch (e: Exception) {
            DebugUtil.printDensityLog("tryToCreateAndSetResourcesImpl failed: $e")
        }
    }

    private fun findResourcesKeyByResourcesImplLocked(impl: ResourcesImpl): ResourcesKey? {
        val refCount = sResourcesImpls!!.size
        for (i in 0 until refCount) {
            val weakImplRef = sResourcesImpls!!.valueAt(i)
            val temp = weakImplRef?.get()
            if (impl == temp) {
                return sResourcesImpls!!.keyAt(i)
            }
        }
        return null
    }

    private fun findOrCreateResourcesImplForKeyLocked(
        oldKey: ResourcesKey,
        target: DisplayConfig
    ): ResourcesImpl? {
        return try {
            val config = Configuration()
            val overrideConfig: Configuration = ReflectionHelper.getFieldValue(
                ResourcesKey::class.java, oldKey, "mOverrideConfiguration"
            )
            config.setTo(overrideConfig)
            config.densityDpi = target.densityDpi

            val displayId: Int = ReflectionHelper.getFieldValue(
                ResourcesKey::class.java, oldKey, "mDisplayId"
            )
            val libDirs: Array<String> = ReflectionHelper.getFieldValue(
                ResourcesKey::class.java, oldKey, "mLibDirs"
            )
            val compInfo: CompatibilityInfo = ReflectionHelper.getFieldValue(
                ResourcesKey::class.java, oldKey, "mCompatInfo"
            )

            val paths: Array<String>? = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                ReflectionHelper.getFieldValue(ResourcesKey::class.java, oldKey, "mOverlayDirs")
            } else {
                ReflectionHelper.getFieldValue(ResourcesKey::class.java, oldKey, "mOverlayPaths")
            }

            val newKey: ResourcesKey = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                ReflectionHelper.getConstructorInstance(
                    ResourcesKey::class.java,
                    arrayOf(
                        String::class.java, StringArrayClass, StringArrayClass, StringArrayClass,
                        Int::class.java, Configuration::class.java, CompatibilityInfo::class.java
                    ),
                    oldKey.mResDir, oldKey.mSplitResDirs, paths, libDirs,
                    displayId, config, compInfo
                )
            } else {
                val loaders: Array<ResourcesLoader> = ReflectionHelper.getFieldValue(
                    ResourcesKey::class.java, oldKey, "mLoaders"
                )
                ReflectionHelper.getConstructorInstance(
                    ResourcesKey::class.java,
                    arrayOf(
                        String::class.java, StringArrayClass, StringArrayClass, StringArrayClass,
                        Int::class.java, Configuration::class.java, CompatibilityInfo::class.java,
                        ResourcesLoaderArrayClass
                    ),
                    oldKey.mResDir, oldKey.mSplitResDirs, paths, libDirs,
                    displayId, config, compInfo, loaders
                )
            }

            DebugUtil.printDensityLog("newKey: $newKey")
            ReflectionHelper.invokeObject(
                ResourcesManager::class.java, sResourcesManager,
                "findOrCreateResourcesImplForKeyLocked",
                arrayOf(ResourcesKey::class.java), newKey
            )
        } catch (e: Exception) {
            DebugUtil.printDensityLog("findOrCreateResourcesImplForKeyLocked failed: $e")
            null
        } catch (e: Error) {
            DebugUtil.printDensityLog("findOrCreateResourcesImplForKeyLocked error: $e")
            null
        }
    }

    /**
     * 设置系统资源密度
     */
    fun setSystemResources(target: DisplayConfig) {
        val systemDM = Resources.getSystem().displayMetrics
        val systemConfig = Resources.getSystem().configuration
        systemDM.densityDpi = target.densityDpi
        systemDM.scaledDensity = target.scaledDensity
        systemDM.density = target.density
        systemConfig.densityDpi = target.densityDpi
        systemConfig.fontScale = target.fontScale
        setDefaultBitmapDensity(target.defaultBitmapDensity)
        DebugUtil.printDensityLog("setSystemResources: $systemDM, $systemConfig")
    }

    private fun setDefaultBitmapDensity(targetDpi: Int) {
        try {
            ReflectionHelper.invoke(
                Bitmap::class.java, null, "setDefaultDensity",
                arrayOf(Int::class.java), targetDpi
            )
            DebugUtil.printDensityLog("setDefaultBitmapDensity: $targetDpi")
        } catch (e: Exception) {
            DebugUtil.printDensityLog("setDefaultBitmapDensity failed: $e")
        }
    }

    fun getDefaultBitmapDensity(): Int {
        return try {
            ReflectionHelper.invokeObject(
                Bitmap::class.java, null, "getDefaultDensity", arrayOf()
            )
        } catch (e: Exception) {
            -1
        }
    }

    // 类型辅助
    private val StringArrayClass = Array<String>::class.java
    private val ResourcesLoaderArrayClass = Array<ResourcesLoader>::class.java
}
