# ListView Demo 设计规范

## 概述

创建一个独立的 `listview-demo` 模块，用于学习 Android ListView 及其各种 Adapter 的使用方式。与现有 `recyclerview-demo` 并行，帮助理解传统列表控件的完整功能。

## 项目架构

**模块名称**：`listview-demo`

**包名**：`com.peter.listview.demo`

**目录结构**：
```
listview-demo/
├── build.gradle.kts
├── proguard-rules.pro
├── src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/peter/listview/demo/
│   │   ├── MainActivity.kt                    # 主 Activity，底部导航容器
│   │   ├── adapter/                           # Adapter 实现
│   │   │   ├── SimpleArrayAdapter.kt          # ArrayAdapter 封装
│   │   │   ├── SimpleMapAdapter.kt            # SimpleAdapter 封装
│   │   │   ├── CustomBaseAdapter.kt           # BaseAdapter 实现
│   │   │   ├── CustomCursorAdapter.kt         # CursorAdapter 实现
│   │   │   ├── GroupListAdapter.kt            # 分组列表 Adapter
│   │   │   └── MultiTypeAdapter.kt            # 多类型 Item Adapter
│   │   ├── fragment/                          # 4 个 Tab Fragment
│   │   │   ├── ArrayAdapterFragment.kt        # Tab1: ArrayAdapter & SimpleAdapter
│   │   │   ├── BaseAdapterFragment.kt         # Tab2: BaseAdapter
│   │   │   ├── CursorAdapterFragment.kt       # Tab3: CursorAdapter
│   │   │   └── AdvancedFragment.kt            # Tab4: 进阶功能
│   │   ├── model/                             # 数据模型
│   │   │   └── Models.kt                      # 数据类定义
│   │   ├── helper/                            # 辅助类
│   │   │   └── DatabaseHelper.kt              # SQLite 数据库辅助
│   │   └── widget/                            # 自定义控件
│   │       └── LoadMoreListView.kt            # 支持加载更多的 ListView
│   └── res/
│       ├── layout/
│       │   ├── activity_main.xml              # 主布局（BottomNavigationView + Fragment）
│       │   ├── fragment_array_adapter.xml     # Tab1 布局
│       │   ├── fragment_base_adapter.xml      # Tab2 布局
│       │   ├── fragment_cursor_adapter.xml    # Tab3 布局
│       │   ├── fragment_advanced.xml          # Tab4 布局
│       │   ├── item_simple_text.xml           # 简单文本 Item
│       │   ├── item_with_icon.xml             # 带图标 Item
│       │   ├── item_multi_type_a.xml          # 多类型 Item A
│       │   ├── item_multi_type_b.xml          # 多类型 Item B
│       │   ├── item_group_header.xml          # 分组标题 Item
│       │   ├── loading_footer.xml             # 加载更多 Footer
│       │   └── empty_view.xml                 # 空数据视图
│       ├── values/
│       │   ├── strings.xml
│       │   ├── colors.xml
│       │   └── themes.xml
│       ├── menu/
│       │   └── bottom_nav_menu.xml            # 底部导航菜单
│       └── drawable/
│           ├── ic_launcher_foreground.xml
│           └── ic_launcher_background.xml
└── src/main/res/mipmap-*/
    └── ic_launcher.png                        # 应用图标
```

**依赖配置**：
```kotlin
dependencies {
    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // SwipeRefreshLayout
    implementation(libs.androidx.swiperefreshlayout)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
}
```

## 各 Tab 功能设计

### Tab 1: ArrayAdapter & SimpleAdapter

**功能列表**：

| 功能 | 说明 | 实现方式 |
|------|------|----------|
| ArrayAdapter 基础 | 展示字符串列表 | `ArrayAdapter<String>` |
| ArrayAdapter 自定义布局 | 图标 + 文字布局 | `ArrayAdapter` + 自定义布局资源 |
| SimpleAdapter 基础 | Map 数据绑定 | `SimpleAdapter` + `List<Map<String, Any>>` |
| SimpleAdapter ViewBinder | 自定义绑定逻辑 | `setViewBinder()` 回调 |

**交互**：
- 点击 Item 显示 Toast
- 长按 Item 显示 ContextMenu

---

### Tab 2: BaseAdapter

**功能列表**：

| 功能 | 说明 | 实现方式 |
|------|------|----------|
| BaseAdapter 基础 | 实现 4 个核心方法 | 继承 `BaseAdapter` |
| ViewHolder 模式 | 静态内部类缓存 View | `convertView.setTag()` |
| 多种 Item 类型 | 不同布局类型 | `getItemViewType()` / `getViewTypeCount()` |
| 优化技巧 | 稳定 ID、批量更新 | `setHasStableIds()` / `notifyItemChanged()` 模拟 |

**交互**：
- 点击 Item 进入详情页或显示 Dialog
- 滑动流畅度对比（ViewHolder vs 无 ViewHolder）

---

### Tab 3: CursorAdapter

**功能列表**：

| 功能 | 说明 | 实现方式 |
|------|------|----------|
| CursorAdapter 基础 | SQLite 数据绑定 | `CursorAdapter` / `newView()` / `bindView()` |
| CursorLoader | 异步加载自动更新 | `LoaderManager` + `CursorLoader` |
| 搜索过滤 | SearchView 过滤 | `setFilterQueryProvider()` |
| 数据 CRUD | 增删改刷新 | ContentProvider 或直接 SQLite |

**简化实现**：
- 使用简单 SQLite 表（如：`users` 表：`_id`, `name`, `phone`）
- 通过 `ViewModel` 管理数据库操作
- 使用 Coroutines 进行异步数据库访问

---

### Tab 4: 进阶功能

**功能列表**：

| 功能 | 说明 | 实现方式 |
|------|------|----------|
| 下拉刷新 | SwipeRefreshLayout | `SwipeRefreshLayout` 包裹 ListView |
| 上拉加载 | 滚动检测加载更多 | `OnScrollListener` + Footer |
| Header/Footer | 头部尾部视图 | `addHeaderView()` / `addFooterView()` |
| 分组列表 | 带标题的分组 | 自定义 Adapter + SectionIndexer |

**交互**：
- 下拉刷新显示加载动画，1-2 秒后更新数据
- 滚动到底部自动加载更多数据
- 分组列表支持快速定位（字母索引）

## 数据流与 UI 交互

**数据流向**：
```
ViewModel (LiveData<List<T>>)
         ↓
    Fragment (观察 LiveData)
         ↓
     Adapter (submitList / notifyDataSetChanged)
         ↓
      ListView (展示数据)
```

**列表状态**：
- **EmptyView**：数据为空时显示占位图 + 提示文字
- **LoadingView**：初始加载时显示进度条
- **ErrorView**：加载失败显示错误提示 + 重试按钮

**公共组件**：

| 组件 | 职责 |
|------|------|
| `BaseListFragment` | Fragment 基类，封装 EmptyView 设置、列表初始化 |
| `ListViewModel` | 管理 Tab 对应的列表数据，提供 LiveData |
| `EmptyView` | 空数据占位视图，支持自定义图标和文字 |
| `LoadingFooter` | 上拉加载 Footer，显示加载中/加载完成/加载失败 |

## 技术细节与注意事项

### ListView 优化要点

1. **convertView 复用**：避免每次 `getView()` 都 inflate 新 View
2. **ViewHolder 模式**：缓存 View 引用，减少 `findViewById()` 调用
3. **图片加载优化**：滑动时暂停图片加载（配合图片库）
4. **批量更新**：避免频繁调用 `notifyDataSetChanged()`
5. **分页加载**：大数据集分批加载，避免一次性加载过多

### CursorAdapter 注意事项

1. **必须有 _id 列**：CursorAdapter 依赖 `_id` 列进行数据定位
2. **Cursor 生命周期**：使用 Loader 或手动管理 Cursor 关闭
3. **异步查询**：大数据集使用 CursorLoader 避免阻塞主线程

### 加载更多实现

```kotlin
listView.setOnScrollListener(object : AbsListView.OnScrollListener {
    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {}

    override fun onScroll(
        view: AbsListView?,
        firstVisibleItem: Int,
        visibleItemCount: Int,
        totalItemCount: Int
    ) {
        // 判断是否滚动到底部
        if (firstVisibleItem + visibleItemCount >= totalItemCount - 1) {
            // 触发加载更多
            loadMore()
        }
    }
})
```

### 分组列表实现

- 实现 `SectionIndexer` 接口：`getSections()` / `getPositionForSection()` / `getSectionForPosition()`
- Adapter 中判断是否显示分组标题（当前 item 的分组与上一个不同时显示）
- 可选：右侧字母快速定位索引

## UI 设计规范

**主题**：Material Design 3

**配色**：
- Primary: 蓝色系（与项目其他 demo 保持一致）
- Background: 浅灰 / 白色
- Accent: 强调色用于选中状态

**底部导航**：
- 4 个 Tab 图标 + 文字
- 选中状态高亮

**列表 Item**：
- 高度：48dp - 72dp
- 内边距：16dp 水平
- 分隔线：1dp 浅灰色

## 成功标准

1. 所有 4 个 Tab 功能完整可运行
2. 代码有详细注释说明各 Adapter 的使用方式
3. CursorAdapter 功能正常，支持搜索和刷新
4. 下拉刷新和上拉加载流畅无卡顿
5. 分组列表滚动流畅，快速定位正常工作
