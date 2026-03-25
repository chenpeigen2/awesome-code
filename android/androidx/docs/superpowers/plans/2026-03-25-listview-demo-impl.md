# listview-demo 实现计划

> **Goal:** 创建一个独立的 ListView 学习 demo 模块，涵盖 ArrayAdapter、SimpleAdapter、BaseAdapter、CursorAdapter 及进阶功能（下拉刷新、上拉加载、Header/Footer、分组列表）

> **Architecture:** 单 Activity + 4 Fragment（BottomNavigationView），ViewModel + LiveData, Coroutines
> **Tech Stack:** Kotlin, AndroidX, Material Design 3, SQLite, SwipeRefreshLayout, Lifecycle, Coroutines

---

## Phase 1: 模块基础搭建

### Task 1: 创建模块配置文件

**Files:**
- `listview-demo/build.gradle.kts`
- `listview-demo/proguard-rules.pro`
- `settings.gradle.kts` (添加模块注册)

**Steps:**

- [ ] **Step 1: 创建 build.gradle.kts**

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

<... existing pattern from recyclerview-demo/build.gradle.kts ...>
```

- [ ] **Step 2: 创建 proguard-rules.pro**

```
proguard-rules.pro 内容（空文件即可）
```

- [ ] **Step 3: 在 settings.gradle.kts 注册模块**

```kotlin
// 在 include(":recyclerview-demo") 后添加
include(":listview-demo")
```

- [ ] **Step 4: 同步 settings.gradle.kts**

```bash
# 验证模块已添加
grep "listview-demo" settings.gradle.kts
```

---

## Phase 2: 资源文件

### Task 2: 创建资源文件

**Files:**
- `listview-demo/src/main/res/values/strings.xml`
- `listview-demo/src/main/res/values/colors.xml`
- `listview-demo/src/main/res/values/themes.xml`
- `listview-demo/src/main/res/menu/bottom_nav_menu.xml`
- `listview-demo/src/main/res/drawable/ic_launcher_foreground.xml`
- `listview-demo/src/main/res/drawable/ic_launcher_background.xml`
- `listview-demo/src/main/res/drawable/ic_tab_*.xml` (4 个 Tab 图标)

**Steps:**

- [ ] **Step 1: 创建 strings.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">ListView Demo</string>
    <string name="tab_array_adapter">ArrayAdapter</string>
    <string name="tab_base_adapter">BaseAdapter</string>
    <string name="tab_cursor_adapter">CursorAdapter</string>
    <string name="tab_advanced">进阶功能</string>
    <string name="empty_list">暂无数据</string>
    <string name="loading">加载中...</string>
    <string name="load_more">加载更多</string>
    <string name="load_complete">加载完成</string>
    <string name="no_more_data">没有更多数据了</string>
</resources>
```

- [ ] **Step 2: 创建 colors.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="primary">#1976D2</color>
    <color name="primary_dark">#1565C0</color>
    <color name="accent">#03DAC6</color>
    <color name="background">#FAFAFA</color>
    <color name="divider">#E0E0E0</color>
    <color name="text_primary">#212121</color>
    <color name="text_secondary">#757575</color>
</resources>
```

- [ ] **Step 3: 创建 themes.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.ListViewDemo" parent="Theme.Material3.Light.NoActionBar">
        <item name="colorPrimary">@color/primary</item>
        <item name="colorPrimaryDark">@color/primary_dark</item>
        <item name="colorAccent">@color/accent</item>
        <item name="android:windowBackground">@color/background</item>
    </style>
</resources>
```

- [ ] **Step 4: 创建 bottom_nav_menu.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/nav_array_adapter"
        android:icon="@drawable/ic_tab_array"
        android:title="@string/tab_array_adapter" />
    <item
        android:id="@+id/nav_base_adapter"
        android:icon="@drawable/ic_tab_base"
        android:title="@string/tab_base_adapter" />
    <item
        android:id="@+id/nav_cursor_adapter"
        android:icon="@drawable/ic_tab_cursor"
        android:title="@string/tab_cursor_adapter" />
    <item
        android:id="@+id/nav_advanced"
        android:icon="@drawable/ic_tab_advanced"
        android:title="@string/tab_advanced" />
</menu>
```

- [ ] **Step 5: 创建 4 个 Tab 图标 drawable**

使用 Material Icons vector drawable 格式：
- `ic_tab_array.xml` - list icon
- `ic_tab_base.xml` - code icon
- `ic_tab_cursor.xml` - database/cursor icon
- `ic_tab_advanced.xml` - settings/advanced icon

- [ ] **Step 6: 创建 launcher 图标**

复制现有 demo 的 launcher 图标结构

---

## Phase 3: 布局文件

### Task 3: 创建布局文件

**Files:**
- `listview-demo/src/main/res/layout/activity_main.xml`
- `listview-demo/src/main/res/layout/fragment_array_adapter.xml`
- `listview-demo/src/main/res/layout/fragment_base_adapter.xml`
- `listview-demo/src/main/res/layout/fragment_cursor_adapter.xml`
- `listview-demo/src/main/res/layout/fragment_advanced.xml`
- `listview-demo/src/main/res/layout/item_simple_text.xml`
- `listview-demo/src/main/res/layout/item_with_icon.xml`
- `listview-demo/src/main/res/layout/item_multi_type_a.xml`
- `listview-demo/src/main/res/layout/item_multi_type_b.xml`
- `listview-demo/src/main/res/layout/item_group_header.xml`
- `listview-demo/src/main/res/layout/loading_footer.xml`
- `listview-demo/src/main/res/layout/empty_view.xml`

**Steps:**

- [ ] **Step 1: 创建 activity_main.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/nav_host_fragment"
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:defaultNavHost="true"
        app:navGraph="@navigation/nav_graph" />

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottom_nav"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom"
        app:menu="@menu/bottom_nav_menu" />

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

**注意**: 需要添加 Navigation 组件依赖，或者改用 FragmentManager 手动切换

- [ ] **Step 2: 创建 fragment_array_adapter.xml** - 包含 ListView + EmptyView

- [ ] **Step 3: 创建 fragment_base_adapter.xml** - 包含 ListView + EmptyView

- [ ] **Step 4: 创建 fragment_cursor_adapter.xml** - 包含 SearchView + ListView + EmptyView

- [ ] **Step 5: 创建 fragment_advanced.xml** - 包含 SwipeRefreshLayout + ListView

- [ ] **Step 6: 创建 item 布局文件** - 简单文本、带图标、多类型等

---

## Phase 4: 数据模型与辅助类

### Task 4: 创建数据模型和辅助类

**Files:**
- `listview-demo/src/main/java/com/peter/listview/demo/model/Models.kt`
- `listview-demo/src/main/java/com/peter/listview/demo/helper/DatabaseHelper.kt`

**Steps:**

- [ ] **Step 1: 创建 Models.kt**

```kotlin
package com.peter.listview.demo.model

data class SimpleItem(
    val id: Long,
    val title: String,
    val subtitle: String? = null
)

data class IconItem(
    val id: Long,
    val iconRes: Int,
    val title: String,
    val description: String
)

data class User(
    val id: Long,
    val name: String,
    val phone: String
)

data class GroupItem<T>(
    val groupTitle: String,
    val items: List<T>
)
```

- [ ] **Step 2: 创建 DatabaseHelper.kt** - SQLite 辅助类用于 CursorAdapter demo

---

## Phase 5: Adapter 实现

### Task 5: 实现 ArrayAdapter 和 SimpleAdapter

**Files:**
- `listview-demo/src/main/java/com/peter/listview/demo/adapter/SimpleArrayAdapter.kt`
- `listview-demo/src/main/java/com/peter/listview/demo/adapter/SimpleMapAdapter.kt`

### Task 6: 实现 BaseAdapter

**Files:**
- `listview-demo/src/main/java/com/peter/listview/demo/adapter/CustomBaseAdapter.kt`
- `listview-demo/src/main/java/com/peter/listview/demo/adapter/MultiTypeAdapter.kt`

### Task 7: 实现 CursorAdapter

**Files:**
- `listview-demo/src/main/java/com/peter/listview/demo/adapter/CustomCursorAdapter.kt`

### Task 8: 实现进阶 Adapter

**Files:**
- `listview-demo/src/main/java/com/peter/listview/demo/adapter/GroupListAdapter.kt`
- `listview-demo/src/main/java/com/peter/listview/demo/widget/LoadMoreListView.kt`

---

## Phase 6: ViewModel

### Task 9: 创建 ViewModel

**Files:**
- `listview-demo/src/main/java/com/peter/listview/demo/viewmodel/ListViewDemoViewModel.kt`

**Steps:**

- [ ] **Step 1: 创建 ViewModel 类**

```kotlin
package com.peter.listview.demo.viewmodel

class ListViewDemoViewModel : ViewModel() {
    // ArrayAdapter demo data
    private val _arrayAdapterItems = MutableLiveData<List<String>>()
    val arrayAdapterItems: LiveData<List<String>> = _arrayAdapterItems

    // BaseAdapter demo data
    private val _baseAdapterItems = MutableLiveData<List<IconItem>>()
    val baseAdapterItems: LiveData<List<IconItem>> = _baseAdapterItems

    // CursorAdapter demo - Cursor
    private val _userCursor = MutableLiveData<Cursor?>()
    val userCursor: LiveData<Cursor?> = _userCursor

    // Advanced demo data
    private val _groupedItems = MutableLiveData<List<GroupItem<SimpleItem>>>()
    val groupedItems: LiveData<List<GroupItem<SimpleItem>>> = _groupedItems

    // Loading states
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            // Load data...
            _isLoading.value = false
        }
    }

    // Refresh, load more, search methods...
}
```

---

## Phase 7: Fragment 实现

### Task 10: 实现 4 个 Fragment

**Files:**
- `listview-demo/src/main/java/com/peter/listview/demo/fragment/ArrayAdapterFragment.kt`
- `listview-demo/src/main/java/com/peter/listview/demo/fragment/BaseAdapterFragment.kt`
- `listview-demo/src/main/java/com/peter/listview/demo/fragment/CursorAdapterFragment.kt`
- `listview-demo/src/main/java/com/peter/listview/demo/fragment/AdvancedFragment.kt`

---

## Phase 8: MainActivity

### Task 11: 实现 MainActivity

**Files:**
- `listview-demo/src/main/java/com/peter/listview/demo/MainActivity.kt`
- `listview-demo/src/main/AndroidManifest.xml`

---

## Phase 9: 验证与收尾

### Task 12: 验证与构建

**Steps:**

- [ ] **Step 1: 构建项目**

```bash
./gradlew :listview-demo:build
```

- [ ] **Step 2: 安装到设备测试**

```bash
./gradlew :listview-demo:installDebug
```

- [ ] **Step 3: 功能测试**
- 所有 4 个 Tab 可切换
- 各列表可滚动
- 下拉刷新/上拉加载正常
- 搜索功能正常

- [ ] **Step 4: 提交代码**

```bash
git add listview-demo/
git commit -m "feat(listview-demo): add ListView demo module with 4 tabs"
```
