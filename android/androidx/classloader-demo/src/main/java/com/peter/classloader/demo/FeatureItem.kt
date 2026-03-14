package com.peter.classloader.demo

/**
 * ClassLoader 功能类型枚举
 */
enum class ClassLoaderFeature {
    // 基本概念
    CONCEPT_CLASSLOADER_CHAIN,      // ClassLoader 链
    CONCEPT_DELEGATION,             // 双亲委派模型
    CONCEPT_CLASS_LOADERS,          // Android 中的 ClassLoader
    
    // 自定义 ClassLoader
    CUSTOM_SIMPLE,                  // 简单自定义 ClassLoader
    CUSTOM_DEX,                     // DexClassLoader 动态加载
    CUSTOM_IN_MEMORY,               // 内存中加载类
    
    // 高级功能
    ADVANCED_ISOLATION,             // 类隔离演示
    ADVANCED_HOT_FIX,               // 热修复原理
    ADVANCED_PLUGIN,                // 插件化原理
    ADVANCED_COMPARE,               // 类比较与判断
}

/**
 * 功能分类
 */
enum class FeatureCategory(val displayName: String) {
    CONCEPT("基本概念"),
    CUSTOM("自定义 ClassLoader"),
    ADVANCED("高级应用")
}

/**
 * 功能项数据模型
 */
data class FeatureItem(
    val feature: ClassLoaderFeature,
    val title: String,
    val description: String,
    val category: FeatureCategory
)
