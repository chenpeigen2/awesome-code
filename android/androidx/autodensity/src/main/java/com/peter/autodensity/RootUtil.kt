package com.peter.autodensity

import java.io.File

/**
 * Root检测工具
 */
object RootUtil {

    private val sDeviceRooted: Boolean = checkDeviceRooted()

    /**
     * 检测设备是否已Root
     */
    fun isDeviceRooted(): Boolean = sDeviceRooted

    private fun checkDeviceRooted(): Boolean {
        // 检查build tags
        val buildTags = android.os.Build.TAGS
        var rooted = buildTags != null && buildTags.contains("test-keys")

        // 检查su文件
        if (!rooted) {
            val paths = arrayOf("/system/bin/su", "/system/xbin/su", "/sbin/su", "/data/local/xbin/su")
            for (path in paths) {
                if (File(path).exists()) {
                    rooted = true
                    break
                }
            }
        }

        if (rooted) {
            DebugUtil.printDensityLog("Current device is rooted")
        }
        return rooted
    }
}
