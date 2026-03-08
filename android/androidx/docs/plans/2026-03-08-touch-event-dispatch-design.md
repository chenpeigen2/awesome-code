# Android 触摸事件分发 Demo 设计文档

## 概述

创建一个从简单到复杂的触摸事件分发学习Demo，覆盖基础分发流程、拦截与消费、滑动冲突、高级场景（多指触控、手势检测、NestedScrolling）。

## 目标用户

- 初学者：理解基础事件分发概念
- 有经验开发者：深入理解复杂场景和边界情况

## 展示方式

- Log日志面板：实时显示事件分发链路
- 可视化面板：触摸点、轨迹、事件流向箭头

## 层级设计

### Level 1：基础分发流程

**目标**：理解Android事件分发的三级结构：Activity → ViewGroup → View

**内容**：
- 自定义View层级：Activity包含ViewGroup，ViewGroup包含子View
- 事件日志面板：实时显示 dispatchTouchEvent / onTouchEvent 调用链
- 可视化指示：触摸时高亮当前处理事件的View，箭头显示事件流向
- 交互操作：点击不同区域，观察事件如何从Activity传递到目标View

**知识点**：
- `Activity.dispatchTouchEvent()` 入口
- `ViewGroup.dispatchTouchEvent()` 分发
- `View.dispatchTouchEvent()` 最终处理
- 事件如何向上回传（消费/未消费）

### Level 2：拦截与消费

**目标**：理解事件拦截机制和消费逻辑，掌握控制事件流向的方法

**内容**：
- 可配置的ViewGroup：提供开关控制 `onInterceptTouchEvent` 是否拦截
- 可配置的子View：提供开关控制 `onTouchEvent` 是否消费事件
- requestDisallowInterceptTouchEvent演示：子View可以禁止父View拦截
- 日志面板：显示 `onInterceptTouchEvent` 返回值变化对事件流的影响
- ACTION_DOWN vs 后续事件：演示DOWN事件被拦截后，MOVE/UP事件的流向

**交互设计**：
```
┌─────────────────────────────────┐
│  [拦截DOWN] [拦截MOVE] [拦截UP] │  ← 开关控制
├─────────────────────────────────┤
│         外层 ViewGroup          │
│    ┌─────────────────────┐      │
│    │   [消费事件] 开关    │      │
│    │      内层 View       │      │
│    └─────────────────────┘      │
└─────────────────────────────────┘
```

**知识点**：
- `onInterceptTouchEvent()` 拦截时机
- `onTouchEvent()` 消费含义
- `requestDisallowInterceptTouchEvent()` 子View反控
- 事件序列（DOWN→MOVE→UP）的完整性

### Level 3：滑动冲突

**目标**：理解并掌握常见滑动冲突的解决方案

**内容**：

#### 3.1 同向滑动冲突
- 场景：垂直ScrollView嵌套垂直RecyclerView
- 演示：内部/外部滑动时事件如何分发
- 解决：外部拦截法 - 根据滑动距离/方向决定拦截

#### 3.2 异向滑动冲突
- 场景：水平ViewPager嵌套垂直RecyclerView
- 演示：斜向滑动时的角度判断
- 解决：角度判断法 - 根据滑动角度决定谁来处理

#### 3.3 解决方案对比
| 方案 | 原理 | 适用场景 |
|------|------|----------|
| 外部拦截法 | 父View根据条件拦截 | 同向滑动 |
| 内部拦截法 | 子View通过requestDisallow控制 | 子View需要优先响应 |

**交互设计**：
- 可切换冲突类型（同向/异向）
- 可开关"冲突解决"对比效果
- 显示当前滑动方向/角度的可视化指示器

### Level 4：高级场景

**目标**：掌握多指触控、手势检测和嵌套滚动机制

**内容**：

#### 4.1 多指触控
- pointerId概念：每个触摸点的唯一标识
- ACTION_POINTER_DOWN/UP：多点按下/抬起事件
- 演示：双指缩放、三指截图等场景
- 可视化：显示每个触摸点的轨迹和pointerId

#### 4.2 手势检测
- GestureDetector：单击、双击、长按、滚动、快速滑动
- ScaleGestureDetector：缩放手势
- 演示：实时识别并显示当前手势类型

#### 4.3 NestedScrolling机制
- 协调滚动的现代方案：NestedScrollView、RecyclerView、CoordinatorLayout
- 核心接口：`NestedScrollingChild` / `NestedScrollingParent`
- 演示：AppBarLayout + RecyclerView的联动效果
- 对比：展示传统拦截 vs NestedScrolling的区别

**交互设计**：
```
┌─────────────────────────────────┐
│ [多指触控] [手势检测] [嵌套滚动] │ ← Tab切换
├─────────────────────────────────┤
│                                 │
│         对应演示区域             │
│                                 │
├─────────────────────────────────┤
│     识别结果：双击              │
│     当前手指数：1               │
└─────────────────────────────────┘
```

## 模块结构

```
touch-demo/
├── src/main/java/com/peter/touch/demo/
│   ├── MainActivity.kt              # 入口列表页
│   ├── TouchAdapter.kt              # 列表适配器
│   ├── TouchItem.kt                 # 列表数据模型
│   │
│   ├── level1/                      # 基础分发
│   │   └── BasicDispatchActivity.kt
│   │
│   ├── level2/                      # 拦截与消费
│   │   └── InterceptConsumeActivity.kt
│   │
│   ├── level3/                      # 滑动冲突
│   │   ├── ScrollConflictActivity.kt
│   │   └── ScrollConflictFragment.kt
│   │
│   ├── level4/                      # 高级场景
│   │   ├── MultiTouchActivity.kt
│   │   ├── GestureDetectActivity.kt
│   │   └── NestedScrollingActivity.kt
│   │
│   └── widget/                      # 公共组件
│       ├── LogPanelView.kt          # 日志面板
│       ├── TouchVisualizerView.kt   # 触摸可视化
│       └── InterceptViewGroup.kt    # 可配置拦截的ViewGroup
│
└── src/main/res/
    ├── layout/
    ├── values/
    └── drawable/
```

## 公共组件

| 组件 | 功能 |
|------|------|
| **LogPanelView** | 滚动日志面板，支持过滤、清空、自动滚动 |
| **TouchVisualizerView** | 绘制触摸点、轨迹、事件流向箭头 |
| **InterceptViewGroup** | 可配置拦截策略的自定义ViewGroup |

## 依赖配置

- AndroidX Core KTX
- AppCompat
- Material Components
- ConstraintLayout
- RecyclerView
- Lifecycle Runtime/ViewModel

## 实现顺序

1. 创建touch-demo模块基础结构
2. 实现公共组件（LogPanelView、TouchVisualizerView）
3. 实现Level 1：基础分发
4. 实现Level 2：拦截与消费
5. 实现Level 3：滑动冲突
6. 实现Level 4：高级场景
7. 完善入口MainActivity和适配器
