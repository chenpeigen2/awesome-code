# Koin Demo 设计文档

## 概述

创建一个完整的Koin依赖注入演示模块，覆盖Koin所有核心功能：定义、作用域、高级功能、ViewModel集成和测试。

## 需求确认

| 功能领域 | 选择 |
|---------|------|
| 演示范围 | 完整5个Tab（定义、作用域、高级功能、ViewModel、测试） |
| UI风格 | TabLayout + ViewPager2（跟随notification-demo模式） |
| 交互方式 | 卡片列表 + 演示按钮 |
| 主题色 | 每个Tab独立主题色 |

## 整体架构

### 模块结构
```
koin/src/main/java/com/example/koin/
├── MainActivity.kt              # TabLayout + ViewPager2
├── KoinItem.kt                  # 数据模型
├── KoinAdapter.kt               # RecyclerView适配器
├── ViewPagerAdapter.kt          # Fragment适配器
├── fragments/
│   ├── DefinitionFragment.kt    # Tab 1: 定义
│   ├── ScopeFragment.kt         # Tab 2: 作用域
│   ├── AdvancedFragment.kt      # Tab 3: 高级功能
│   ├── ViewModelFragment.kt     # Tab 4: ViewModel
│   └── TestFragment.kt          # Tab 5: 测试
├── di/
│   ├── DefinitionModule.kt      # 定义演示模块
│   ├── ScopeModule.kt           # 作用域演示模块
│   ├── AdvancedModule.kt        # 高级功能演示模块
│   └── ViewModelModule.kt       # ViewModel模块
├── model/                       # 现有模型
└── MainApplication.kt           # Koin初始化
```

### 核心组件职责
- **MainActivity** - 管理Tab导航和主题色切换
- **KoinAdapter** - 展示Koin功能卡片
- **各Fragment** - 展示对应分类的Koin功能
- **DI Modules** - 提供演示所需的依赖定义

### 技术选型
- ViewBinding 用于视图绑定
- Fragment + ViewPager2 实现Tab导航
- Koin 3.x 依赖注入框架

## 数据模型

### KoinItem.kt
```kotlin
enum class DefinitionType { SINGLE, FACTORY, SCOPED, VIEWMODEL_DEFINITION }
enum class ScopeType { ACTIVITY_SCOPE, LINK_SCOPE, SCOPE_SOURCE, CLOSE_SCOPE }
enum class AdvancedType { NAMED, INJECT_PARAMS, PROPERTY, LAZY, BINDING }
enum class ViewModelType { BASIC, SAVED_STATE, FACTORY_PARAMS, SHARED }
enum class TestType { VERIFY, TEST_RULE, MOCK_REPLACE, CHECK_MODULES }

enum class KoinCategory(val displayName: String) {
    DEFINITIONS("定义"),
    SCOPES("作用域"),
    ADVANCED("高级功能"),
    VIEWMODELS("ViewModel"),
    TESTING("测试")
}

data class KoinItem(
    val title: String,
    val description: String,
    val codeSnippet: String,
    val category: KoinCategory,
    val action: () -> Unit = {}
)
```

## Tab 内容设计

### Tab 1: Definitions (定义)
| Feature | Description | Demo Action |
|---------|-------------|-------------|
| single | 单例依赖，整个应用生命周期只创建一次 | 显示同一个实例的hashCode |
| factory | 每次获取都创建新实例 | 显示不同实例的hashCode |
| scoped | 作用域内单例 | 在scope内显示相同实例 |
| viewModel | Koin ViewModel集成 | 显示ViewModel实例 |

### Tab 2: Scopes (作用域)
| Feature | Description | Demo Action |
|---------|-------------|-------------|
| activityScope | Activity级别作用域 | 创建/显示Activity scope |
| scope linking | 作用域链接 | 链接两个scope共享依赖 |
| scopeSource | 获取作用域源 | 显示scope源信息 |
| closeScope | 关闭作用域 | 关闭并重新创建scope |

### Tab 3: Advanced (高级功能)
| Feature | Description | Demo Action |
|---------|-------------|-------------|
| named | 命名限定符区分同类型 | 获取两个不同命名的实例 |
| injectParams | 注入参数 | 传递参数创建实例 |
| property | 属性注入 | 从Koin properties获取值 |
| lazy | 懒加载注入 | 延迟获取依赖 |
| binding | 接口绑定 | 通过接口获取实现类 |

### Tab 4: ViewModels
| Feature | Description | Demo Action |
|---------|-------------|-------------|
| basic | 基础ViewModel注入 | 显示ViewModel |
| savedState | SavedStateHandle | 显示保存的状态 |
| factoryParams | 带参数的ViewModel | 传递参数创建ViewModel |
| shared | 共享ViewModel | 多Fragment共享 |

### Tab 5: Testing (测试)
| Feature | Description | Demo Action |
|---------|-------------|-------------|
| verify | 模块验证 | 验证模块配置正确 |
| testRule | KoinTestRule | 演示测试规则用法 |
| mockReplace | Mock替换 | 替换依赖为Mock |
| checkModules | 检查模块 | 运行模块检查 |

## UI设计

### MainActivity布局
- AppBarLayout: 主题色背景
- TabLayout: 5个Tab
- ViewPager2: Fragment容器
- 主题色随Tab切换动画过渡

### 主题色方案
| Tab | Primary Color | Container Color |
|-----|--------------|-----------------|
| 定义 | Purple (#9C27B0) | Purple Light (#F3E5F5) |
| 作用域 | Blue (#2196F3) | Blue Light (#E3F2FD) |
| 高级功能 | Orange (#FF9800) | Orange Light (#FFF3E0) |
| ViewModel | Teal (#009688) | Teal Light (#E0F2F1) |
| 测试 | Red (#F44336) | Red Light (#FFEBEE) |

### 卡片布局
```
┌────────────────────────────────────┐
│ 标题                               │
├────────────────────────────────────┤
│ 描述文字                           │
│                                    │
│ // 代码示例                        │
│ single { Repository() }            │
│                                    │
├────────────────────────────────────┤
│              [ 演示 ]              │
└────────────────────────────────────┘
```

## 文件清单

### Kotlin文件 (12个新增)
1. KoinItem.kt - 数据模型
2. KoinAdapter.kt - 列表适配器
3. ViewPagerAdapter.kt - Fragment适配器
4. fragments/DefinitionFragment.kt
5. fragments/ScopeFragment.kt
6. fragments/AdvancedFragment.kt
7. fragments/ViewModelFragment.kt
8. fragments/TestFragment.kt
9. di/DefinitionModule.kt
10. di/ScopeModule.kt
11. di/AdvancedModule.kt
12. di/ViewModelModule.kt

### Layout文件 (8个新增)
1. activity_main.xml (更新)
2. fragment_definition.xml
3. fragment_scope.xml
4. fragment_advanced.xml
5. fragment_viewmodel.xml
6. fragment_test.xml
7. item_koin.xml
8. item_code_snippet.xml

### 资源文件
1. colors.xml (更新)
2. strings.xml (更新)
3. dimens.xml

## 依赖配置

```kotlin
dependencies {
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose) // for viewModel
    implementation(libs.material)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
}
```

## 实现顺序

1. **Phase 1**: 基础架构
   - 更新MainActivity和布局
   - 创建KoinItem数据模型
   - 创建KoinAdapter

2. **Phase 2**: Tab框架
   - 创建ViewPagerAdapter
   - 创建5个Fragment骨架
   - 实现主题色切换

3. **Phase 3**: DI模块
   - 创建DefinitionModule
   - 创建ScopeModule
   - 创建AdvancedModule
   - 创建ViewModelModule

4. **Phase 4**: 功能实现
   - 实现每个Tab的功能演示
   - 添加代码示例显示
   - 完善交互反馈

5. **Phase 5**: 完善测试
   - 实现测试Tab功能
   - 添加测试用例演示
