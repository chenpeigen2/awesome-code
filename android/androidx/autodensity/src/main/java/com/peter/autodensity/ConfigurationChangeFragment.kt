package com.peter.autodensity

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * Configuration变更监听Fragment
 * 用于在Activity配置变更时更新密度
 */
class ConfigurationChangeFragment : Fragment() {

    private var removeDensityChangeFlag = false
    private var autoDensity: AutoDensity? = null

    fun setAutoDensity(autoDensity: AutoDensity) {
        this.autoDensity = autoDensity
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DebugUtil.printDensityLog("ConfigurationChangeFragment onAttach, config: ${context.resources.configuration}")
        AutoDensity.updateDensity(context)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        val activity = activity
        DebugUtil.printDensityLog("ConfigurationChangeFragment onConfigurationChanged, newConfig: $newConfig")
        autoDensity?.updateDensityOnConfigChanged(activity, newConfig)
        super.onConfigurationChanged(newConfig)

        if (removeDensityChangeFlag) {
            try {
                @Suppress("DEPRECATION")
                val info: ActivityInfo = ReflectionHelper.getFieldValue(
                    Activity::class.java, activity, "mActivityInfo"
                )
                info.configChanges = info.configChanges and ActivityInfo.CONFIG_DENSITY.inv()
                removeDensityChangeFlag = false
            } catch (e: Exception) {
                DebugUtil.printDensityLog("removeDensityChangeFlag failed: $e")
            }
        }
    }

    fun removeDensityChangeFlag() {
        removeDensityChangeFlag = true
    }

    companion object {
        const val TAG = "ConfigurationChangeFragment"

        fun newInstance(): ConfigurationChangeFragment {
            return ConfigurationChangeFragment()
        }
    }
}
