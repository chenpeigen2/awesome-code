# Android 协程集成教程

本文档详细介绍 Kotlin 协程在 Android 中的集成使用，包括 lifecycleScope、viewModelScope、repeatOnLifecycle 和 flowWithLifecycle。

## 目录

1. [lifecycleScope](#lifecyclescope)
2. [viewModelScope](#viewmodelscope)
3. [repeatOnLifecycle 与 flowWithLifecycle](#repeatonlifecycle-与-flowwithlifecycle)

---

## lifecycleScope

### 什么是 lifecycleScope？

`lifecycleScope` 是 `LifecycleOwner` 的扩展属性，它返回一个与 `Lifecycle` 绑定的 `CoroutineScope`。当 `Lifecycle` 被销毁时，这个 Scope 中的所有协程会自动取消。

### 基本使用

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 在 lifecycleScope 中启动协程
        lifecycleScope.launch {
            // Activity 销毁时自动取消
            val data = fetchData()
            updateUI(data)
        }

        // 指定调度器
        lifecycleScope.launch(Dispatchers.IO) {
            val data = loadDataFromDatabase()
            withContext(Dispatchers.Main) {
                displayData(data)
            }
        }
    }
}
```

### 生命周期状态

| 状态 | 对应生命周期 |
|------|-------------|
| CREATED | onCreate - onDestroy |
| STARTED | onStart - onStop |
| RESUMED | onResume - onPause |
| DESTROYED | 协程会被取消 |

### 使用场景

- 在 Activity/Fragment 中执行异步操作
- 网络请求
- 数据库操作
- UI 动画

### 示例：网络请求

```kotlin
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 网络请求
        lifecycleScope.launch {
            try {
                val result = apiService.getData()
                updateUI(result)
            } catch (e: Exception) {
                showError(e.message)
            }
        }
    }
}
```

### 相关 Activity

- [LifecycleScopeActivity](../src/main/java/com/peter/coroutine/demo/android/LifecycleScopeActivity.kt)

---

## viewModelScope

### 什么是 viewModelScope？

`viewModelScope` 是 `ViewModel` 的扩展属性，它返回一个与 `ViewModel` 生命周期绑定的 `CoroutineScope`。当 `ViewModel` 被清除（`onCleared`）时，这个 Scope 中的所有协程会自动取消。

### ViewModel 生命周期

- ViewModel 在配置更改（如屏幕旋转）后保持存活
- ViewModel 在 Activity/Fragment 真正销毁时才会被清除
- viewModelScope 中的协程会自动在 ViewModel 清除时取消

### 基本使用

```kotlin
class MainViewModel : ViewModel() {
    private val _data = MutableStateFlow("")
    val data: StateFlow<String> = _data.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _data.value = "Loading..."

            try {
                val result = repository.fetchData()
                _data.value = result
            } catch (e: Exception) {
                _data.value = "Error: ${e.message}"
            }
        }
    }

    // ViewModel 清除时自动取消
    override fun onCleared() {
        super.onCleared()
        // viewModelScope 中的协程会自动取消
    }
}
```

### 与 lifecycleScope 的区别

| 特性 | viewModelScope | lifecycleScope |
|------|----------------|----------------|
| 所属 | ViewModel | Activity/Fragment |
| 生命周期 | ViewModel 清除时取消 | Activity/Fragment 销毁时取消 |
| 配置更改 | 保持运行 | 取消后重建 |

### 配置更改示例

```kotlin
class CounterViewModel : ViewModel() {
    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter.asStateFlow()

    init {
        // 旋转屏幕后继续运行
        viewModelScope.launch {
            while (true) {
                delay(1000)
                _counter.value++
            }
        }
    }
}

class MainActivity : AppCompatActivity() {
    private val viewModel: CounterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 旋转屏幕后重新订阅，但 ViewModel 中的计数继续
        lifecycleScope.launch {
            viewModel.counter.collect { count ->
                textView.text = "Count: $count"
            }
        }
    }
}
```

### 使用场景

- 网络请求
- 数据库操作
- 业务逻辑处理
- Flow 收集

### 相关 Activity

- [ViewModelScopeActivity](../src/main/java/com/peter/coroutine/demo/android/ViewModelScopeActivity.kt)

---

## repeatOnLifecycle 与 flowWithLifecycle

### 为什么需要这些 API？

直接在 `lifecycleScope.launch` 中收集 Flow 有问题：

```kotlin
// 不推荐的方式
lifecycleScope.launch {
    viewModel.flow.collect { value ->
        // 即使 Activity 在后台也会继续收集
        // 浪费资源，可能导致崩溃
        updateUI(value)
    }
}
```

### repeatOnLifecycle

`repeatOnLifecycle` 会在生命周期达到指定状态时启动协程，在生命周期低于指定状态时取消协程。

#### 基本使用

```kotlin
lifecycleScope.launch {
    // 当生命周期 >= STARTED 时开始收集
    // 当生命周期 < STARTED 时停止收集
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.flow.collect { value ->
            updateUI(value)
        }
    }
}
```

#### Lifecycle.State 参数

| 状态 | 范围 | 使用场景 |
|------|------|----------|
| CREATED | onCreate - onDestroy | 很少使用 |
| STARTED | onStart - onStop | 推荐用于 UI 更新 |
| RESUMED | onResume - onPause | 需要交互时 |

### flowWithLifecycle

`flowWithLifecycle` 功能与 `repeatOnLifecycle` 相同，但 API 更简洁。

```kotlin
lifecycleScope.launch {
    viewModel.flow
        .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
        .collect { value ->
            updateUI(value)
        }
}
```

### 三种收集方式对比

#### 1. 直接 collect（不推荐）

```kotlin
lifecycleScope.launch {
    viewModel.flow.collect { value ->
        // 即使在后台也会继续收集
        // 浪费资源
    }
}
```

#### 2. repeatOnLifecycle（推荐）

```kotlin
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.flow.collect { value ->
            // 只在前台收集
        }
    }
}
```

#### 3. flowWithLifecycle（推荐）

```kotlin
lifecycleScope.launch {
    viewModel.flow
        .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
        .collect { value ->
            // 只在前台收集
        }
}
```

### 最佳实践

#### 在 Activity 中

```kotlin
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 推荐方式
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUI(state)
                }
            }
        }
    }
}
```

#### 在 Fragment 中

```kotlin
class MainFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 使用 viewLifecycleOwner
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUI(state)
                }
            }
        }
    }
}
```

### 生命周期感知示例

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onStart() {
        super.onStart()
        // repeatOnLifecycle 开始收集
    }

    override fun onStop() {
        super.onStop()
        // repeatOnLifecycle 停止收集
    }
}
```

### 相关 Activity

- [CollectFlowActivity](../src/main/java/com/peter/coroutine/demo/android/CollectFlowActivity.kt)

---

## 关键点总结

1. **lifecycleScope**: 与 Activity/Fragment 生命周期绑定
2. **viewModelScope**: 与 ViewModel 生命周期绑定，配置更改后保持
3. **repeatOnLifecycle**: 安全的 Flow 收集方式，推荐使用 STARTED
4. **flowWithLifecycle**: API 更简洁，功能与 repeatOnLifecycle 相同
5. **不要直接在 lifecycleScope 中收集**: 会在后台继续运行

## Android 协程最佳实践清单

- [ ] 使用 viewModelScope 处理业务逻辑
- [ ] 使用 lifecycleScope 处理 UI 相关操作
- [ ] 使用 repeatOnLifecycle 或 flowWithLifecycle 收集 Flow
- [ ] 在 Fragment 中使用 viewLifecycleOwner
- [ ] 选择合适的 Lifecycle.State（通常用 STARTED）
- [ ] 配合 StateFlow 管理 UI 状态

## 下一步

- [进阶原理教程](06-advanced.md) - 深入理解协程原理
- [测试教程](07-testing.md) - 学习协程测试
