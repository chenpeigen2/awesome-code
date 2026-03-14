package com.peter.mmkv.demo

import android.app.Application
import com.tencent.mmkv.MMKV

/**
 * MMKV Demo Application
 * 负责初始化 MMKV
 */
class MMKVApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // 初始化 MMKV
        // MMKV.initialize(this) 会使用默认的存储路径（filesDir/mmkv）
        val rootDir = MMKV.initialize(this)
        
        // 打印 MMKV 存储根目录
        android.util.Log.d("MMKV", "MMKV root dir: $rootDir")
    }
}
