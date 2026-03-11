# Findings & Decisions - Dagger2 Demo

## Requirements

### 用户需求
- **学习目的**: 从零开始学习 Dagger2 基础
- **学习方式**: 单一模块 + 分步示例，由浅入深
- **学习内容**: 基础注入、作用域、限定符、子组件、Android 集成
- **UI 形式**: 传统 XML View

### 设计决策
- **方案选择**: 分页面递进式（单 Activity + 多 Fragment）
- **示例风格**: CoffeeShop + 实用场景

---

## Dagger2 核心概念

### 核心注解
| 注解 | 用途 |
|------|------|
| `@Inject` | 标记需要注入的构造器/字段/方法 |
| `@Module` | 定义提供依赖的类 |
| `@Provides` | 在 Module 中提供实例的方法 |
| `@Component` | 连接 @Inject 和 @Module 的桥梁 |
| `@Binds` | 绑定接口到实现 (比 @Provides 更高效) |

### 作用域注解
| 注解 | 用途 |
|------|------|
| `@Singleton` | 单例 (需要配合 Component 作用域) |
| `@Scope` | 自定义作用域的元注解 |
| `@Reusable` | 可重用作用域 (弱引用缓存) |

### 高级注解
| 注解 | 用途 |
|------|------|
| `@Qualifier` | 自定义限定符的元注解 |
| `@Named` | 字符串限定符 |
| `@Subcomponent` | 子组件，继承父组件的依赖 |
| `@BindsInstance` | 绑定运行时实例到 Component |

---

## Dagger2 vs Hilt vs Koin 对比

| 特性 | Dagger2 | Hilt | Koin |
|------|---------|------|------|
| 编译时检查 | ✓ | ✓ | ✗ |
| 运行时性能 | 最快 | 快 | 较慢 |
| 学习曲线 | 陡峭 | 中等 | 平缓 |
| 样板代码 | 多 | 少 | 最少 |
| 灵活性 | 最高 | 中等 | 高 |
| Android 支持 | 手动 | 自动 | 自动 |

---

## 项目结构

```
dagger-demo/
├── src/main/java/com/peter/dagger/demo/
│   ├── MainActivity.kt           # Fragment 容器
│   ├── DemoApplication.kt        # 初始化 Dagger
│   ├── ui/fragment/              # 5 个学习页面
│   ├── di/                       # Dagger 组件和模块
│   └── model/                    # 示例模型类
└── src/main/res/
    ├── layout/                   # XML 布局
    └── menu/                     # 底部导航菜单
```

---

## 技术版本

| 依赖 | 版本 |
|------|------|
| Kotlin | 2.0.21 |
| Dagger | 2.52 |
| KSP | 2.0.21-1.0.27 |
| minSdk | 33 |
| targetSdk | 36 |

---

## 学习路径

1. **基础注入**: @Inject, @Module, @Component, @Provides
2. **作用域**: @Singleton, 自定义 @Scope
3. **限定符**: @Qualifier, @Named
4. **子组件**: @Subcomponent
5. **Android 集成**: Activity/Fragment/ViewModel 注入

---

## Resources

### 官方文档
- [Dagger 官方指南](https://dagger.dev/)
- [Dagger2 GitHub](https://github.com/google/dagger)
- [Dagger2 官方教程](https://dagger.dev/tutorial/)

---

*Update after every 2 view/browser/search operations*
