package com.example.koin

/**
 * Koin功能分类
 */
enum class KoinCategory(val displayName: String) {
    DEFINITIONS("定义"),
    SCOPES("作用域"),
    ADVANCED("高级功能"),
    VIEWMODELS("ViewModel"),
    TESTING("测试")
}

/**
 * Koin功能项数据模型
 */
data class KoinItem(
    val title: String,
    val description: String,
    val codeSnippet: String,
    val category: KoinCategory,
    val action: () -> Unit = {}
)
