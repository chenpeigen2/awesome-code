package com.example.koin;

import android.util.Log;
import android.widget.Toast;

import org.koin.core.KoinApplication;

import java.util.HashMap;
import java.util.Map;

import dalvik.system.PathClassLoader;

// 在LogClassLoader中添加监控
class LogClassLoader extends PathClassLoader {
    private static final Map<String, Long> loadTimes = new HashMap<>();

    public LogClassLoader(String dexPath, ClassLoader parent) {
        super(dexPath, parent);
    }

    public LogClassLoader(String dexPath, String librarySearchPath, ClassLoader parent) {
        super(dexPath, librarySearchPath, parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        long start = System.currentTimeMillis();
        try {
            return super.loadClass(name);
        } finally {
            long cost = System.currentTimeMillis() - start;
            loadTimes.put(name, cost);

            // 上报性能数据
            if (cost > 100) { // 超过100ms的类加载

            }
            Log.d("LogClassLoader", "类加载耗时：" + name + " - " + cost);
        }
    }

    // 获取所有类加载耗时统计
    public static Map<String, Long> getLoadTimes() {
        return new HashMap<>(loadTimes);
    }
}
