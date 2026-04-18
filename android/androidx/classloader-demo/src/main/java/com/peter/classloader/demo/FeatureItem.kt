package com.peter.classloader.demo

enum class ClassLoaderFeature {
    // 基本概念
    CONCEPT_CLASSLOADER_CHAIN,
    CONCEPT_DELEGATION,
    CONCEPT_CLASS_LOADERS,

    // 自定义 ClassLoader
    CUSTOM_SIMPLE,
    CUSTOM_DEX,
    CUSTOM_IN_MEMORY,

    // 高级功能
    ADVANCED_ISOLATION,
    ADVANCED_HOT_FIX,
    ADVANCED_PLUGIN,
    ADVANCED_COMPARE,
    ADVANCED_LIFECYCLE,
    ADVANCED_INTER_PLUGIN,
}

enum class FeatureCategory(val displayName: String) {
    CONCEPT("基本概念"),
    CUSTOM("自定义 ClassLoader"),
    ADVANCED("高级应用")
}

data class FeatureItem(
    val feature: ClassLoaderFeature,
    val title: String,
    val description: String,
    val category: FeatureCategory
)
