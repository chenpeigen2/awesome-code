# Koin Demo Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build a comprehensive 5-tab Koin dependency injection demo app following the notification-demo architecture pattern.

**Architecture:** TabLayout + ViewPager2 with 5 tabs (Definitions, Scopes, Advanced, ViewModels, Testing). Each tab displays a RecyclerView of Koin feature cards with code snippets and demo buttons.

**Tech Stack:** Kotlin, Koin 3.x, ViewBinding, ViewPager2, Material Components, RecyclerView

---

## Phase 1: Data Model & Resources

### Task 1: Create KoinItem Data Model

**Files:**
- Create: `koin/src/main/java/com/example/koin/KoinItem.kt`

**Step 1: Write the data model**

```kotlin
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
```

**Step 2: Verify file created**

Run: `ls -la koin/src/main/java/com/example/koin/KoinItem.kt`
Expected: File exists

**Step 3: Commit**

```bash
git add koin/src/main/java/com/example/koin/KoinItem.kt
git commit -m "feat(koin): add KoinItem data model"
```

---

### Task 2: Add Color Resources

**Files:**
- Modify: `koin/src/main/res/values/colors.xml`

**Step 1: Add tab theme colors**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Tab Theme Colors - Definitions (Purple) -->
    <color name="tab_definition">#9C27B0</color>
    <color name="tab_definition_container">#F3E5F5</color>

    <!-- Tab Theme Colors - Scopes (Blue) -->
    <color name="tab_scope">#2196F3</color>
    <color name="tab_scope_container">#E3F2FD</color>

    <!-- Tab Theme Colors - Advanced (Orange) -->
    <color name="tab_advanced">#FF9800</color>
    <color name="tab_advanced_container">#FFF3E0</color>

    <!-- Tab Theme Colors - ViewModel (Teal) -->
    <color name="tab_viewmodel">#009688</color>
    <color name="tab_viewmodel_container">#E0F2F1</color>

    <!-- Tab Theme Colors - Testing (Red) -->
    <color name="tab_test">#F44336</color>
    <color name="tab_test_container">#FFEBEE</color>

    <!-- Common Colors -->
    <color name="on_surface_variant">#49454F</color>
    <color name="surface">#FFFBFE</color>
    <color name="card_background">#FFFFFF</color>
    <color name="code_background">#F5F5F5</color>
</resources>
```

**Step 2: Verify build**

Run: `./gradlew :koin:assembleDebug --quiet`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add koin/src/main/res/values/colors.xml
git commit -m "feat(koin): add tab theme colors"
```

---

### Task 3: Add String Resources

**Files:**
- Modify: `koin/src/main/res/values/strings.xml`

**Step 1: Add string resources**

```xml
<resources>
    <string name="app_name">Koin Demo</string>

    <!-- Tab Titles -->
    <string name="tab_definitions">定义</string>
    <string name="tab_scopes">作用域</string>
    <string name="tab_advanced">高级功能</string>
    <string name="tab_viewmodels">ViewModel</string>
    <string name="tab_testing">测试</string>

    <!-- Definition Items -->
    <string name="def_single_title">single - 单例</string>
    <string name="def_single_desc">整个应用生命周期只创建一次实例</string>
    <string name="def_factory_title">factory - 工厂</string>
    <string name="def_factory_desc">每次获取都创建新实例</string>
    <string name="def_scoped_title">scoped - 作用域单例</string>
    <string name="def_scoped_desc">在作用域内是单例</string>
    <string name="def_viewmodel_title">viewModel - ViewModel集成</string>
    <string name="def_viewmodel_desc">Koin与Android ViewModel集成</string>

    <!-- Scope Items -->
    <string name="scope_activity_title">Activity Scope</string>
    <string name="scope_activity_desc">Activity级别的Koin作用域</string>
    <string name="scope_link_title">Scope Linking</string>
    <string name="scope_link_desc">链接两个作用域共享依赖</string>
    <string name="scope_source_title">Scope Source</string>
    <string name="scope_source_desc">获取作用域的源对象</string>
    <string name="scope_close_title">Close Scope</string>
    <string name="scope_close_desc">关闭并释放作用域</string>

    <!-- Advanced Items -->
    <string name="adv_named_title">Named - 命名限定符</string>
    <string name="adv_named_desc">使用名称区分同一类型的不同实例</string>
    <string name="adv_params_title">Injected Parameters</string>
    <string name="adv_params_desc">注入时传递参数</string>
    <string name="adv_property_title">Property Injection</string>
    <string name="adv_property_desc">从Koin属性中获取值</string>
    <string name="adv_lazy_title">Lazy Injection</string>
    <string name="adv_lazy_desc">延迟获取依赖</string>
    <string name="adv_binding_title">Interface Binding</string>
    <string name="adv_binding_desc">通过接口类型获取实现类</string>

    <!-- ViewModel Items -->
    <string name="vm_basic_title">Basic ViewModel</string>
    <string name="vm_basic_desc">基础的ViewModel注入</string>
    <string name="vm_savedstate_title">SavedState ViewModel</string>
    <string name="vm_savedstate_desc">带SavedStateHandle的ViewModel</string>
    <string name="vm_factory_title">Factory ViewModel</string>
    <string name="vm_factory_desc">带参数的ViewModel工厂</string>
    <string name="vm_shared_title">Shared ViewModel</string>
    <string name="vm_shared_desc">多个Fragment共享ViewModel</string>

    <!-- Test Items -->
    <string name="test_verify_title">Module Verification</string>
    <string name="test_verify_desc">验证模块定义的正确性</string>
    <string name="test_rule_title">KoinTestRule</string>
    <string name="test_rule_desc">JUnit测试规则集成</string>
    <string name="test_mock_title">Mock Replacement</string>
    <string name="test_mock_desc">用Mock替换真实依赖</string>
    <string name="test_check_title">Check Modules</string>
    <string name="test_check_desc">检查所有模块配置</string>

    <!-- Actions -->
    <string name="action_demo">演示</string>
</resources>
```

**Step 2: Commit**

```bash
git add koin/src/main/res/values/strings.xml
git commit -m "feat(koin): add string resources for all tabs"
```

---

### Task 4: Add Dimension Resources

**Files:**
- Create: `koin/src/main/res/values/dimens.xml`

**Step 1: Create dimens.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Spacing -->
    <dimen name="spacing_xs">4dp</dimen>
    <dimen name="spacing_sm">8dp</dimen>
    <dimen name="spacing_md">16dp</dimen>
    <dimen name="spacing_lg">24dp</dimen>
    <dimen name="spacing_xl">32dp</dimen>

    <!-- Card -->
    <dimen name="card_corner_radius">12dp</dimen>
    <dimen name="card_elevation">2dp</dimen>

    <!-- Text -->
    <dimen name="text_title">16sp</dimen>
    <dimen name="text_body">14sp</dimen>
    <dimen name="text_code">12sp</dimen>

    <!-- Code Block -->
    <dimen name="code_corner_radius">8dp</dimen>
    <dimen name="code_padding">12dp</dimen>
</resources>
```

**Step 2: Commit**

```bash
git add koin/src/main/res/values/dimens.xml
git commit -m "feat(koin): add dimension resources"
```

---

## Phase 2: Layout Files

### Task 5: Update MainActivity Layout

**Files:**
- Modify: `koin/src/main/res/layout/activity_main.xml`

**Step 1: Replace layout with TabLayout + ViewPager2**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/root"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/surface"
    tools:context=".MainActivity">

    <com.google.android.material.appbar.AppBarLayout
        android:id="@+id/appBarLayout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/tab_definition_container">

        <TextView
            android:id="@+id/tvTitle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:paddingHorizontal="@dimen/spacing_md"
            android:paddingTop="@dimen/spacing_lg"
            android:paddingBottom="@dimen/spacing_sm"
            android:text="@string/app_name"
            android:textColor="@color/tab_definition"
            android:textSize="24sp"
            android:textStyle="bold" />

        <com.google.android.material.tabs.TabLayout
            android:id="@+id/tabLayout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@android:color/transparent"
            app:tabIndicatorColor="@color/tab_definition"
            app:tabIndicatorHeight="3dp"
            app:tabMode="scrollable"
            app:tabTextAppearance="@style/TabTextAppearance" />

    </com.google.android.material.appbar.AppBarLayout>

    <androidx.viewpager2.widget.ViewPager2
        android:id="@+id/viewPager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior" />

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

**Step 2: Add TabTextAppearance style to themes.xml**

Add to `koin/src/main/res/values/themes.xml`:

```xml
<style name="TabTextAppearance" parent="TextAppearance.Design.Tab">
    <item name="android:textSize">14sp</item>
    <item name="textAllCaps">false</item>
</style>
```

**Step 3: Verify build**

Run: `./gradlew :koin:assembleDebug --quiet`
Expected: BUILD SUCCESSFUL

**Step 4: Commit**

```bash
git add koin/src/main/res/layout/activity_main.xml koin/src/main/res/values/themes.xml
git commit -m "feat(koin): update MainActivity layout with TabLayout and ViewPager2"
```

---

### Task 6: Create Koin Item Layout

**Files:**
- Create: `koin/src/main/res/layout/item_koin.xml`

**Step 1: Create item_koin.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginHorizontal="@dimen/spacing_md"
    android:layout_marginVertical="@dimen/spacing_sm"
    app:cardBackgroundColor="@color/card_background"
    app:cardCornerRadius="@dimen/card_corner_radius"
    app:cardElevation="@dimen/card_elevation">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="@dimen/spacing_md">

        <!-- Title -->
        <TextView
            android:id="@+id/tvTitle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textColor="@android:color/black"
            android:textSize="@dimen/text_title"
            android:textStyle="bold" />

        <!-- Description -->
        <TextView
            android:id="@+id/tvDescription"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="@dimen/spacing_sm"
            android:textColor="@color/on_surface_variant"
            android:textSize="@dimen/text_body" />

        <!-- Code Snippet -->
        <TextView
            android:id="@+id/tvCodeSnippet"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="@dimen/spacing_md"
            android:background="@color/code_background"
            android:fontFamily="monospace"
            android:padding="@dimen/code_padding"
            android:textColor="#37474F"
            android:textSize="@dimen/text_code" />

        <!-- Demo Button -->
        <com.google.android.material.button.MaterialButton
            android:id="@+id/btnDemo"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="end"
            android:layout_marginTop="@dimen/spacing_md"
            android:text="@string/action_demo"
            style="@style/Widget.Material3.Button.TonalButton" />

    </LinearLayout>

</com.google.android.material.card.MaterialCardView>
```

**Step 2: Commit**

```bash
git add koin/src/main/res/layout/item_koin.xml
git commit -m "feat(koin): add Koin item card layout"
```

---

### Task 7: Create Fragment Layouts

**Files:**
- Create: `koin/src/main/res/layout/fragment_definition.xml`
- Create: `koin/src/main/res/layout/fragment_scope.xml`
- Create: `koin/src/main/res/layout/fragment_advanced.xml`
- Create: `koin/src/main/res/layout/fragment_viewmodel.xml`
- Create: `koin/src/main/res/layout/fragment_test.xml`

**Step 1: Create fragment_definition.xml (template for all)**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.recyclerview.widget.RecyclerView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/recyclerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:clipToPadding="false"
    android:paddingVertical="@dimen/spacing_sm" />
```

**Step 2: Copy for all fragments**

Run:
```bash
cp koin/src/main/res/layout/fragment_definition.xml koin/src/main/res/layout/fragment_scope.xml
cp koin/src/main/res/layout/fragment_definition.xml koin/src/main/res/layout/fragment_advanced.xml
cp koin/src/main/res/layout/fragment_definition.xml koin/src/main/res/layout/fragment_viewmodel.xml
cp koin/src/main/res/layout/fragment_definition.xml koin/src/main/res/layout/fragment_test.xml
```

**Step 3: Commit**

```bash
git add koin/src/main/res/layout/fragment_*.xml
git commit -m "feat(koin): add fragment layouts for all tabs"
```

---

## Phase 3: Adapter & ViewPagerAdapter

### Task 8: Create KoinAdapter

**Files:**
- Create: `koin/src/main/java/com/example/koin/KoinAdapter.kt`

**Step 1: Create KoinAdapter**

```kotlin
package com.example.koin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.koin.databinding.ItemKoinBinding

class KoinAdapter(
    private val items: List<KoinItem>
) : RecyclerView.Adapter<KoinAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemKoinBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: KoinItem) {
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description
            binding.tvCodeSnippet.text = item.codeSnippet
            binding.btnDemo.setOnClickListener { item.action() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemKoinBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
```

**Step 2: Verify build**

Run: `./gradlew :koin:assembleDebug --quiet`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add koin/src/main/java/com/example/koin/KoinAdapter.kt
git commit -m "feat(koin): add KoinAdapter for RecyclerView"
```

---

### Task 9: Create ViewPagerAdapter

**Files:**
- Create: `koin/src/main/java/com/example/koin/ViewPagerAdapter.kt`

**Step 1: Create ViewPagerAdapter**

```kotlin
package com.example.koin

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.koin.fragments.AdvancedFragment
import com.example.koin.fragments.DefinitionFragment
import com.example.koin.fragments.ScopeFragment
import com.example.koin.fragments.TestFragment
import com.example.koin.fragments.ViewModelFragment

class ViewPagerAdapter(
    activity: AppCompatActivity
) : FragmentStateAdapter(activity) {

    private val fragments = listOf(
        DefinitionFragment.newInstance(),
        ScopeFragment.newInstance(),
        AdvancedFragment.newInstance(),
        ViewModelFragment.newInstance(),
        TestFragment.newInstance()
    )

    private val titles = listOf(
        "定义", "作用域", "高级功能", "ViewModel", "测试"
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

    fun getTitle(position: Int): String = titles[position]
}
```

**Step 2: Commit**

```bash
git add koin/src/main/java/com/example/koin/ViewPagerAdapter.kt
git commit -m "feat(koin): add ViewPagerAdapter for tab navigation"
```

---

## Phase 4: DI Modules

### Task 10: Create Definition Module

**Files:**
- Create: `koin/src/main/java/com/example/koin/di/DefinitionModule.kt`

**Step 1: Create DefinitionModule**

```kotlin
package com.example.koin.di

import org.koin.dsl.module

// 定义示例类
class SingleRepository {
    val id: Int = (1000..9999).random()
}

class FactoryService {
    val id: Int = (1000..9999).random()
}

class ScopedHelper {
    val id: Int = (1000..9999).random()
}

val definitionModule = module {
    // single - 整个应用生命周期单例
    single { SingleRepository() }

    // factory - 每次获取都创建新实例
    factory { FactoryService() }

    // scoped - 作用域内单例 (需要在scope中声明)
}
```

**Step 2: Commit**

```bash
git add koin/src/main/java/com/example/koin/di/DefinitionModule.kt
git commit -m "feat(koin): add DefinitionModule with single/factory/scoped demos"
```

---

### Task 11: Create Scope Module

**Files:**
- Create: `koin/src/main/java/com/example/koin/di/ScopeModule.kt`

**Step 1: Create ScopeModule**

```kotlin
package com.example.koin.di

import com.example.koin.MainActivity
import org.koin.dsl.module

// 作用域示例类
class ActivityScopedService {
    val id: Int = (1000..9999).random()
}

class LinkedService {
    val id: Int = (1000..9999).random()
}

val scopeModule = module {
    // Activity作用域
    scope<MainActivity> {
        scoped { ActivityScopedService() }
    }
}
```

**Step 2: Commit**

```bash
git add koin/src/main/java/com/example/koin/di/ScopeModule.kt
git commit -m "feat(koin): add ScopeModule for scope demos"
```

---

### Task 12: Create Advanced Module

**Files:**
- Create: `koin/src/main/java/com/example/koin/di/AdvancedModule.kt`

**Step 1: Create AdvancedModule**

```kotlin
package com.example.koin.di

import org.koin.core.qualifier.named
import org.koin.dsl.module

// 命名限定符示例
class NamedService(val name: String)

// 注入参数示例
class ParameterService(val value: String)

// 属性注入示例
class PropertyService(val configValue: String)

// 接口绑定示例
interface Repository {
    fun getData(): String
}

class RealRepository : Repository {
    override fun getData(): String = "Real Data"
}

// 懒加载示例
class LazyService {
    val id: Int = (1000..9999).random()
}

val advancedModule = module {
    // Named - 命名限定符
    single(named("serviceA")) { NamedService("Service A") }
    single(named("serviceB")) { NamedService("Service B") }

    // Factory with parameters - 注入参数
    factory { params -> ParameterService(params.get()) }

    // Property - 从Koin properties获取
    single { PropertyService(getProperty("app.config", "default_value")) }

    // Interface Binding - 接口绑定
    single<Repository> { RealRepository() }

    // Single for lazy demo
    single { LazyService() }
}
```

**Step 2: Commit**

```bash
git add koin/src/main/java/com/example/koin/di/AdvancedModule.kt
git commit -m "feat(koin): add AdvancedModule with named/params/property/binding demos"
```

---

### Task 13: Create ViewModel Module

**Files:**
- Create: `koin/src/main/java/com/example/koin/di/ViewModelModule.kt`
- Create: `koin/src/main/java/com/example/koin/viewmodel/DemoViewModel.kt`

**Step 1: Create DemoViewModel**

```kotlin
package com.example.koin.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class DemoViewModel : ViewModel() {
    val id: Int = (1000..9999).random())
}

class SavedStateViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val id: Int = (1000..9999).random()

    fun saveValue(key: String, value: String) {
        savedStateHandle[key] = value
    }

    fun getValue(key: String): String? {
        return savedStateHandle[key]
    }
}

class FactoryViewModel(
    private val param: String
) : ViewModel() {
    val id: Int = (1000..9999).random()
    val injectedParam: String = param
}

class SharedViewModel : ViewModel() {
    val id: Int = (1000..9999).random()
    private var _data: String = ""
    var data: String
        get() = _data
        set(value) { _data = value }
}
```

**Step 2: Create ViewModelModule**

```kotlin
package com.example.koin.di

import com.example.koin.viewmodel.DemoViewModel
import com.example.koin.viewmodel.FactoryViewModel
import com.example.koin.viewmodel.SavedStateViewModel
import com.example.koin.viewmodel.SharedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    // Basic ViewModel
    viewModel { DemoViewModel() }

    // SavedState ViewModel
    viewModel { SavedStateViewModel(get()) }

    // Factory ViewModel with parameters
    viewModel { params -> FactoryViewModel(params.get()) }

    // Shared ViewModel
    viewModel { SharedViewModel() }
}
```

**Step 3: Commit**

```bash
git add koin/src/main/java/com/example/koin/di/ViewModelModule.kt
git add koin/src/main/java/com/example/koin/viewmodel/DemoViewModel.kt
git commit -m "feat(koin): add ViewModelModule with basic/savedstate/factory/shared demos"
```

---

## Phase 5: Fragments

### Task 14: Create DefinitionFragment

**Files:**
- Create: `koin/src/main/java/com/example/koin/fragments/DefinitionFragment.kt`

**Step 1: Create DefinitionFragment**

```kotlin
package com.example.koin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.koin.KoinAdapter
import com.example.koin.KoinCategory
import com.example.koin.KoinItem
import com.example.koin.databinding.FragmentDefinitionBinding
import com.example.koin.di.FactoryService
import com.example.koin.di.SingleRepository
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.getViewModel

class DefinitionFragment : Fragment() {

    private var _binding: FragmentDefinitionBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = DefinitionFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDefinitionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = listOf(
            KoinItem(
                title = "single - 单例",
                description = "整个应用生命周期只创建一次实例",
                codeSnippet = "single { Repository() }",
                category = KoinCategory.DEFINITIONS,
                action = {
                    val repo1: SingleRepository = get()
                    val repo2: SingleRepository = get()
                    Toast.makeText(
                        requireContext(),
                        "single: 两次获取hashCode相同\n${repo1.id} == ${repo2.id}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "factory - 工厂",
                description = "每次获取都创建新实例",
                codeSnippet = "factory { Service() }",
                category = KoinCategory.DEFINITIONS,
                action = {
                    val service1: FactoryService = get()
                    val service2: FactoryService = get()
                    Toast.makeText(
                        requireContext(),
                        "factory: 两次获取hashCode不同\n${service1.id} != ${service2.id}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "scoped - 作用域单例",
                description = "在作用域内是单例，不同作用域是不同实例",
                codeSnippet = "scope<Activity> {\n  scoped { Helper() }\n}",
                category = KoinCategory.DEFINITIONS,
                action = {
                    val scope = getKoin().getOrCreateScope("demo_scope")
                    val helper1 = scope.get<FactoryService>()
                    val helper2 = scope.get<FactoryService>()
                    Toast.makeText(
                        requireContext(),
                        "scoped: 同一scope内相同\n${helper1.id} == ${helper2.id}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "viewModel - ViewModel集成",
                description = "Koin与Android ViewModel无缝集成",
                codeSnippet = "viewModel { MyViewModel() }",
                category = KoinCategory.DEFINITIONS,
                action = {
                    val vm = getViewModel<com.example.koin.viewmodel.DemoViewModel>()
                    Toast.makeText(
                        requireContext(),
                        "viewModel: ${vm.javaClass.simpleName}\nid: ${vm.id}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = KoinAdapter(items)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

**Step 2: Verify build**

Run: `./gradlew :koin:assembleDebug --quiet`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add koin/src/main/java/com/example/koin/fragments/DefinitionFragment.kt
git commit -m "feat(koin): add DefinitionFragment with single/factory/scoped/viewModel demos"
```

---

### Task 15: Create ScopeFragment

**Files:**
- Create: `koin/src/main/java/com/example/koin/fragments/ScopeFragment.kt`

**Step 1: Create ScopeFragment**

```kotlin
package com.example.koin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.koin.KoinAdapter
import com.example.koin.KoinCategory
import com.example.koin.KoinItem
import com.example.koin.databinding.FragmentScopeBinding
import org.koin.android.ext.android.getKoin

class ScopeFragment : Fragment() {

    private var _binding: FragmentScopeBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = ScopeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScopeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = listOf(
            KoinItem(
                title = "Activity Scope",
                description = "Activity级别的Koin作用域，Activity销毁时自动关闭",
                codeSnippet = "scope<MainActivity> {\n  scoped { Service() }\n}",
                category = KoinCategory.SCOPES,
                action = {
                    Toast.makeText(
                        requireContext(),
                        "Activity Scope:\n在MainActivity实现AndroidScopeComponent\n即可使用activityScope()",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "Scope Linking",
                description = "链接两个作用域共享依赖",
                codeSnippet = "scope.linkTo(otherScope)",
                category = KoinCategory.SCOPES,
                action = {
                    val scope1 = getKoin().getOrCreateScope("scope_1")
                    val scope2 = getKoin().getOrCreateScope("scope_2")
                    scope1.linkTo(scope2)
                    Toast.makeText(
                        requireContext(),
                        "Scope Linking:\nscope1.linkTo(scope2)\n现在scope1可以访问scope2的依赖",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "Scope Source",
                description = "获取作用域的源对象",
                codeSnippet = "scope.getScopeSource<Activity>()",
                category = KoinCategory.SCOPES,
                action = {
                    Toast.makeText(
                        requireContext(),
                        "Scope Source:\n通过scopeSource获取声明scope的组件\n如: scopeSource<MainActivity>()",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "Close Scope",
                description = "手动关闭作用域释放资源",
                codeSnippet = "scope.close()",
                category = KoinCategory.SCOPES,
                action = {
                    val scope = getKoin().getOrCreateScope("temp_scope")
                    Toast.makeText(
                        requireContext(),
                        "Close Scope:\n创建scope: temp_scope\n调用scope.close()释放",
                        Toast.LENGTH_LONG
                    ).show()
                    scope.close()
                }
            )
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = KoinAdapter(items)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

**Step 2: Commit**

```bash
git add koin/src/main/java/com/example/koin/fragments/ScopeFragment.kt
git commit -m "feat(koin): add ScopeFragment with activity/link/source/close demos"
```

---

### Task 16: Create AdvancedFragment

**Files:**
- Create: `koin/src/main/java/com/example/koin/fragments/AdvancedFragment.kt`

**Step 1: Create AdvancedFragment**

```kotlin
package com.example.koin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.koin.KoinAdapter
import com.example.koin.KoinCategory
import com.example.koin.KoinItem
import com.example.koin.databinding.FragmentAdvancedBinding
import com.example.koin.di.NamedService
import com.example.koin.di.ParameterService
import com.example.koin.di.PropertyService
import com.example.koin.di.Repository
import com.example.koin.di.LazyService
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named

class AdvancedFragment : Fragment() {

    private var _binding: FragmentAdvancedBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = AdvancedFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdvancedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = listOf(
            KoinItem(
                title = "Named - 命名限定符",
                description = "使用名称区分同一类型的不同实例",
                codeSnippet = "single(named(\"A\")) { Service(\"A\") }\nget<Service>(named(\"A\"))",
                category = KoinCategory.ADVANCED,
                action = {
                    val serviceA: NamedService = get(named("serviceA"))
                    val serviceB: NamedService = get(named("serviceB"))
                    Toast.makeText(
                        requireContext(),
                        "Named:\nserviceA: ${serviceA.name}\nserviceB: ${serviceB.name}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "Injected Parameters",
                description = "注入时传递参数",
                codeSnippet = "factory { params ->\n  Service(params.get())\n}\nget { parametersOf(\"value\") }",
                category = KoinCategory.ADVANCED,
                action = {
                    val service: ParameterService = get { parametersOf("Hello Koin!") }
                    Toast.makeText(
                        requireContext(),
                        "Injected Params:\n传入参数: \"Hello Koin!\"\n收到值: ${service.value}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "Property Injection",
                description = "从Koin属性中获取配置值",
                codeSnippet = "startKoin {\n  properties(mapOf(\"key\" to \"value\"))\n}\ngetProperty(\"key\")",
                category = KoinCategory.ADVANCED,
                action = {
                    val service: PropertyService = get()
                    Toast.makeText(
                        requireContext(),
                        "Property:\n从Koin获取配置\n值: ${service.configValue}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "Lazy Injection",
                description = "延迟获取依赖，使用时才初始化",
                codeSnippet = "by inject<Service>()\n// 或\nby lazy { get<Service>() }",
                category = KoinCategory.ADVANCED,
                action = {
                    val lazyService: LazyService by lazy { get() }
                    Toast.makeText(
                        requireContext(),
                        "Lazy Injection:\nby lazy { get() }\n首次访问时才创建\nid: ${lazyService.id}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "Interface Binding",
                description = "通过接口类型获取实现类",
                codeSnippet = "single<Repository> { RealRepository() }\nget<Repository>()",
                category = KoinCategory.ADVANCED,
                action = {
                    val repo: Repository = get()
                    Toast.makeText(
                        requireContext(),
                        "Interface Binding:\nget<Repository>()\n返回: ${repo.javaClass.simpleName}\n数据: ${repo.getData()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = KoinAdapter(items)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

**Step 2: Commit**

```bash
git add koin/src/main/java/com/example/koin/fragments/AdvancedFragment.kt
git commit -m "feat(koin): add AdvancedFragment with named/params/property/lazy/binding demos"
```

---

### Task 17: Create ViewModelFragment

**Files:**
- Create: `koin/src/main/java/com/example/koin/fragments/ViewModelFragment.kt`

**Step 1: Create ViewModelFragment**

```kotlin
package com.example.koin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.koin.KoinAdapter
import com.example.koin.KoinCategory
import com.example.koin.KoinItem
import com.example.koin.databinding.FragmentViewmodelBinding
import com.example.koin.viewmodel.DemoViewModel
import com.example.koin.viewmodel.FactoryViewModel
import com.example.koin.viewmodel.SavedStateViewModel
import com.example.koin.viewmodel.SharedViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ViewModelFragment : Fragment() {

    private var _binding: FragmentViewmodelBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModel()

    companion object {
        fun newInstance() = ViewModelFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewmodelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = listOf(
            KoinItem(
                title = "Basic ViewModel",
                description = "基础的ViewModel注入",
                codeSnippet = "viewModel { DemoViewModel() }\nby viewModel<DemoViewModel>()",
                category = KoinCategory.VIEWMODELS,
                action = {
                    val vm: DemoViewModel by viewModel()
                    Toast.makeText(
                        requireContext(),
                        "Basic ViewModel:\n${vm.javaClass.simpleName}\nid: ${vm.id}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "SavedState ViewModel",
                description = "带SavedStateHandle的ViewModel，支持状态保存",
                codeSnippet = "viewModel { SavedStateVM(get()) }",
                category = KoinCategory.VIEWMODELS,
                action = {
                    val vm: SavedStateViewModel by viewModel()
                    vm.saveValue("demo_key", "demo_value")
                    val saved = vm.getValue("demo_key")
                    Toast.makeText(
                        requireContext(),
                        "SavedState ViewModel:\n保存: demo_key=demo_value\n读取: $saved",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "Factory ViewModel",
                description = "带参数的ViewModel工厂",
                codeSnippet = "viewModel { params ->\n  FactoryVM(params.get())\n}\nviewModel { parametersOf(\"param\") }",
                category = KoinCategory.VIEWMODELS,
                action = {
                    val param = "CustomParam_${System.currentTimeMillis() % 1000}"
                    val vm: FactoryViewModel by viewModel { parametersOf(param) }
                    Toast.makeText(
                        requireContext(),
                        "Factory ViewModel:\n传入参数: $param\nViewModel收到: ${vm.injectedParam}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "Shared ViewModel",
                description = "多个Fragment共享同一个ViewModel实例",
                codeSnippet = "// Fragment A & B\nby activityViewModel<SharedVM>()",
                category = KoinCategory.VIEWMODELS,
                action = {
                    val currentTime = System.currentTimeMillis().toString()
                    sharedViewModel.data = "Updated at $currentTime"
                    Toast.makeText(
                        requireContext(),
                        "Shared ViewModel:\nid: ${sharedViewModel.id}\n数据: ${sharedViewModel.data}\n(与Activity共享同一实例)",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = KoinAdapter(items)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

**Step 2: Commit**

```bash
git add koin/src/main/java/com/example/koin/fragments/ViewModelFragment.kt
git commit -m "feat(koin): add ViewModelFragment with basic/savedstate/factory/shared demos"
```

---

### Task 18: Create TestFragment

**Files:**
- Create: `koin/src/main/java/com/example/koin/fragments/TestFragment.kt`

**Step 1: Create TestFragment**

```kotlin
package com.example.koin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.koin.KoinAdapter
import com.example.koin.KoinCategory
import com.example.koin.KoinItem
import com.example.koin.databinding.FragmentTestBinding
import org.koin.android.ext.android.getKoin

class TestFragment : Fragment() {

    private var _binding: FragmentTestBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = TestFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = listOf(
            KoinItem(
                title = "Module Verification",
                description = "验证模块定义的正确性，检查所有依赖是否可解析",
                codeSnippet = "val module = module { ... }\nmodule.verify()",
                category = KoinCategory.TESTING,
                action = {
                    Toast.makeText(
                        requireContext(),
                        "Module Verification:\nmodule.verify()\n检查模块中所有定义是否有效\n确保所有get()依赖都能解析",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "KoinTestRule",
                description = "JUnit测试规则，自动启动和停止Koin",
                codeSnippet = "@get:Rule\nval koinTestRule = KoinTestRule.create {\n  modules(testModule)\n}",
                category = KoinCategory.TESTING,
                action = {
                    Toast.makeText(
                        requireContext(),
                        "KoinTestRule:\n在每个测试前启动Koin\n测试后自动停止\n确保测试隔离",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "Mock Replacement",
                description = "用Mock对象替换真实依赖进行测试",
                codeSnippet = "loadKoinModules(module {\n  single<Repository> { mockRepo }\n})",
                category = KoinCategory.TESTING,
                action = {
                    Toast.makeText(
                        requireContext(),
                        "Mock Replacement:\n使用loadKoinModules()\n覆盖原有定义\n注入Mock对象进行测试",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ),
            KoinItem(
                title = "Check Modules",
                description = "检查所有模块配置是否完整",
                codeSnippet = "checkKoinModules()\n// 或\nkoin.checkModules()",
                category = KoinCategory.TESTING,
                action = {
                    Toast.makeText(
                        requireContext(),
                        "Check Modules:\nkoin.checkModules()\n验证所有模块定义\n确保没有循环依赖",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = KoinAdapter(items)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

**Step 2: Commit**

```bash
git add koin/src/main/java/com/example/koin/fragments/TestFragment.kt
git commit -m "feat(koin): add TestFragment with verify/rule/mock/check demos"
```

---

## Phase 6: MainActivity & Application

### Task 19: Update MainApplication

**Files:**
- Modify: `koin/src/main/java/com/example/koin/MainApplication.kt`

**Step 1: Update MainApplication to load all modules**

```kotlin
package com.example.koin

import android.app.Application
import com.example.koin.di.advancedModule
import com.example.koin.di.definitionModule
import com.example.koin.di.scopeModule
import com.example.koin.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MainApplication)

            // 加载属性配置
            properties(mapOf("app.config" to "Koin Demo Config Value"))

            modules(
                definitionModule,
                scopeModule,
                advancedModule,
                viewModelModule,
                sampleModule
            )
        }
    }
}
```

**Step 2: Verify build**

Run: `./gradlew :koin:assembleDebug --quiet`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add koin/src/main/java/com/example/koin/MainApplication.kt
git commit -m "feat(koin): update MainApplication to load all demo modules"
```

---

### Task 20: Update MainActivity

**Files:**
- Modify: `koin/src/main/java/com/example/koin/MainActivity.kt`

**Step 1: Update MainActivity with TabLayout and theme switching**

```kotlin
package com.example.koin

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.koin.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Tab主题色
    private val tabColors = listOf(
        Pair(R.color.tab_definition, R.color.tab_definition_container),
        Pair(R.color.tab_scope, R.color.tab_scope_container),
        Pair(R.color.tab_advanced, R.color.tab_advanced_container),
        Pair(R.color.tab_viewmodel, R.color.tab_viewmodel_container),
        Pair(R.color.tab_test, R.color.tab_test_container)
    )

    private var currentTabPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupImmersiveStatusBar()
        setupViewPager()
        applyTabTheme(0)
    }

    private fun setupImmersiveStatusBar() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentTabPosition = position
                applyTabTheme(position)
            }
        })
    }

    private fun applyTabTheme(position: Int) {
        val (primaryColorRes, containerColorRes) = tabColors[position]
        val primaryColor = ContextCompat.getColor(this, primaryColorRes)
        val containerColor = ContextCompat.getColor(this, containerColorRes)
        val currentContainerColor = ContextCompat.getColor(this, tabColors[currentTabPosition].second)

        // 动画过渡背景色
        val colorAnimator = ValueAnimator.ofObject(
            ArgbEvaluator(),
            currentContainerColor,
            containerColor
        )
        colorAnimator.duration = 300
        colorAnimator.addUpdateListener { animator ->
            val color = animator.animatedValue as Int
            binding.appBarLayout.setBackgroundColor(color)
        }
        colorAnimator.start()

        // 更新TabLayout指示器颜色
        binding.tabLayout.setSelectedTabIndicatorColor(primaryColor)
        binding.tabLayout.setTabTextColors(
            ContextCompat.getColor(this, R.color.on_surface_variant),
            primaryColor
        )

        // 更新标题颜色
        binding.tvTitle.setTextColor(primaryColor)
    }
}
```

**Step 2: Verify build**

Run: `./gradlew :koin:assembleDebug --quiet`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add koin/src/main/java/com/example/koin/MainActivity.kt
git commit -m "feat(koin): update MainActivity with TabLayout and theme switching"
```

---

### Task 21: Final Build Verification

**Step 1: Clean and build**

Run: `./gradlew :koin:clean :koin:assembleDebug`
Expected: BUILD SUCCESSFUL

**Step 2: Final commit if any fixes needed**

If any files were modified during build fixes:

```bash
git add -A
git commit -m "fix(koin): resolve build issues"
```

---

## Summary

**Total Tasks:** 21
**Estimated Files Created:** 16
**Estimated Files Modified:** 4

**Key Deliverables:**
1. 5-tab Koin demo app with theme switching
2. 17 feature demos covering all Koin core concepts
3. DI modules demonstrating single/factory/scoped/viewModel
4. Advanced features: named, parameters, properties, lazy, binding
5. ViewModel patterns: basic, savedState, factory, shared
6. Testing approaches documentation
