# 协程测试教程

本文档详细介绍 Kotlin 协程的测试方法，包括 TestDispatcher、虚拟时间控制和 Flow 测试。

## 目录

1. [TestDispatcher](#testdispatcher)
2. [虚拟时间控制](#虚拟时间控制)
3. [Flow 测试](#flow-测试)

---

## TestDispatcher

### 测试依赖

在 `build.gradle` 中添加测试依赖：

```groovy
dependencies {
    testImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3"
    testImplementation "junit:junit:4.13.2"
    testImplementation "app.cash.turbine:turbine:1.0.0"  // Flow 测试库
}
```

### 标准测试调度器

`StandardTestDispatcher` 是用于测试的调度器，它不会立即执行协程，需要手动控制执行。

```kotlin
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest

class MyTest {
    private val testDispatcher = StandardTestDispatcher()

    @Test
    fun testWithStandardDispatcher() = runTest(testDispatcher) {
        // 协程不会立即执行
        val job = launch {
            println("Coroutine started")
        }

        // 手动推进执行
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(job.isCompleted)
    }
}
```

### Unconfined 测试调度器

`UnconfinedTestDispatcher` 会立即执行协程（类似 `Dispatchers.Unconfined`）。

```kotlin
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

class MyTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @Test
    fun testWithUnconfinedDispatcher() = runTest(testDispatcher) {
        // 协程会立即执行
        var executed = false
        launch {
            executed = true
        }
        assertTrue(executed)  // 立即为 true
    }
}
```

### runTest

`runTest` 是测试协程的主入口，它会创建一个测试作用域并等待所有协程完成。

```kotlin
@Test
fun myTest() = runTest {
    // 测试代码
    val result = someSuspendFunction()
    assertEquals(expected, result)
}
```

### 设置 Main 调度器

在 Android 测试中，需要设置 `Dispatchers.Main` 的测试调度器。

```kotlin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.rules.TestWatcher
import org.junit.Rule

class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

class MyViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun testViewModel() = runTest {
        val viewModel = MyViewModel()
        viewModel.loadData()
        assertEquals("Expected", viewModel.data.value)
    }
}
```

### 测试示例

```kotlin
class RepositoryTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @Test
    fun fetchData_returnsData() = runTest(testDispatcher) {
        // Given
        val repository = MyRepository(apiService)

        // When
        val result = repository.fetchData()

        // Then
        assertEquals("Expected Data", result)
    }
}
```

---

## 虚拟时间控制

### TestScope.scheduler

测试调度器提供了 `scheduler` 来控制虚拟时间。

```kotlin
@Test
fun testWithVirtualTime() = runTest {
    val testDispatcher = StandardTestDispatcher()
    var executed = false

    launch(testDispatcher) {
        delay(1000)
        executed = true
    }

    // 此时 executed 为 false
    assertFalse(executed)

    // 推进时间 500ms
    testDispatcher.scheduler.advanceTimeBy(500)
    assertFalse(executed)

    // 推进时间 500ms（总共 1000ms）
    testDispatcher.scheduler.advanceTimeBy(500)
    assertTrue(executed)
}
```

### advanceUntilIdle

执行所有挂起的协程，直到没有更多任务。

```kotlin
@Test
fun testAdvanceUntilIdle() = runTest {
    val testDispatcher = StandardTestDispatcher()

    launch(testDispatcher) {
        delay(100)
        println("Step 1")
        delay(100)
        println("Step 2")
        delay(100)
        println("Step 3")
    }

    // 执行所有任务
    testDispatcher.scheduler.advanceUntilIdle()
    // 输出: Step 1, Step 2, Step 3
}
```

### advanceTimeBy

推进指定的时间。

```kotlin
@Test
fun testAdvanceTimeBy() = runTest {
    val testDispatcher = StandardTestDispatcher()
    var count = 0

    launch(testDispatcher) {
        while (true) {
            delay(100)
            count++
        }
    }

    testDispatcher.scheduler.advanceTimeBy(350)
    assertEquals(3, count)  // 350ms 后执行了 3 次
}
```

### runCurrent

执行当前时刻所有就绪的任务。

```kotlin
@Test
fun testRunCurrent() = runTest {
    val testDispatcher = StandardTestDispatcher()

    launch(testDispatcher) {
        println("Immediate task")
    }

    // 执行当前就绪的任务
    testDispatcher.scheduler.runCurrent()
    // 输出: Immediate task
}
```

### currentTime

获取当前虚拟时间。

```kotlin
@Test
fun testCurrentTime() = runTest {
    val testDispatcher = StandardTestDispatcher()

    println("Start time: ${testDispatcher.scheduler.currentTime}")

    launch(testDispatcher) {
        delay(1000)
    }

    testDispatcher.scheduler.advanceTimeBy(1000)
    println("After delay: ${testDispatcher.scheduler.currentTime}")  // 1000
}
```

### 测试超时

```kotlin
@Test
fun testTimeout() = runTest {
    val testDispatcher = StandardTestDispatcher()

    val result = withTimeoutOrNull(500) {
        launch(testDispatcher) {
            delay(1000)
        }.join()
        "Completed"
    }

    assertNull(result)  // 因为 500ms 内未完成
}
```

---

## Flow 测试

### 基本 Flow 测试

```kotlin
@Test
fun testFlow() = runTest {
    val flow = flow {
        emit(1)
        emit(2)
        emit(3)
    }

    val results = mutableListOf<Int>()
    flow.collect { results.add(it) }

    assertEquals(listOf(1, 2, 3), results)
}
```

### 使用 toList

```kotlin
@Test
fun testFlowToList() = runTest {
    val flow = flowOf(1, 2, 3)

    val results = flow.toList()

    assertEquals(listOf(1, 2, 3), results)
}
```

### 测试带 delay 的 Flow

```kotlin
@Test
fun testFlowWithDelay() = runTest {
    val testDispatcher = StandardTestDispatcher()

    val flow = flow {
        repeat(3) { i ->
            delay(100)
            emit(i)
        }
    }.flowOn(testDispatcher)

    val results = mutableListOf<Int>()
    val job = launch(testDispatcher) {
        flow.collect { results.add(it) }
    }

    // 推进时间
    testDispatcher.scheduler.advanceTimeBy(350)
    job.join()

    assertEquals(listOf(0, 1, 2), results)
}
```

### 测试 StateFlow

```kotlin
@Test
fun testStateFlow() = runTest {
    val stateFlow = MutableStateFlow(0)

    val results = mutableListOf<Int>()
    val collectJob = launch {
        stateFlow.take(3).collect { results.add(it) }
    }

    stateFlow.value = 1
    stateFlow.value = 2

    collectJob.join()
    assertEquals(listOf(0, 1, 2), results)
}
```

### 使用 Turbine 库

Turbine 是一个专门用于测试 Flow 的库，提供了更简洁的 API。

```groovy
// build.gradle
testImplementation "app.cash.turbine:turbine:1.0.0"
```

```kotlin
import app.cash.turbine.test

@Test
fun testFlowWithTurbine() = runTest {
    val flow = flow {
        emit(1)
        delay(100)
        emit(2)
        delay(100)
        emit(3)
    }

    flow.test {
        assertEquals(1, awaitItem())
        assertEquals(2, awaitItem())
        assertEquals(3, awaitItem())
        awaitComplete()
    }
}
```

### Turbine 断言

```kotlin
@Test
fun testTurbineAssertions() = runTest {
    val flow = flowOf(1, 2, 3)

    flow.test {
        // 等待下一个值
        expectMostRecentItem() // 3

        // 或者逐个验证
        assertEquals(1, awaitItem())
        assertEquals(2, awaitItem())
        assertEquals(3, awaitItem())

        // 等待完成
        awaitComplete()
    }
}
```

### 测试异常 Flow

```kotlin
@Test
fun testFlowException() = runTest {
    val flow = flow<Int> {
        emit(1)
        throw RuntimeException("Error!")
    }

    flow.test {
        assertEquals(1, awaitItem())
        val error = awaitError()
        assertTrue(error is RuntimeException)
        assertEquals("Error!", error.message)
    }
}
```

### 测试 ViewModel 中的 Flow

```kotlin
class MyViewModel(private val repository: MyRepository) : ViewModel() {
    private val _state = MutableStateFlow("")
    val state: StateFlow<String> = _state.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _state.value = repository.getData()
        }
    }
}

class MyViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun testDataLoading() = runTest {
        // Given
        val repository = mockk<MyRepository> {
            coEvery { getData() } returns "Test Data"
        }
        val viewModel = MyViewModel(repository)

        // When
        viewModel.loadData()

        // Then
        viewModel.state.test {
            assertEquals("Test Data", awaitItem())
        }
    }
}
```

### 测试冷流与热流

```kotlin
@Test
fun testColdFlow() = runTest {
    // 冷流每次收集都会重新执行
    var collectCount = 0
    val coldFlow = flow {
        collectCount++
        emit(collectCount)
    }

    assertEquals(1, coldFlow.first())
    assertEquals(2, coldFlow.first())  // collectCount 增加了
}

@Test
fun testHotFlow() = runTest {
    // 热流（SharedFlow）所有收集者共享数据
    val sharedFlow = MutableSharedFlow<Int>(replay = 1)

    sharedFlow.emit(1)

    val result1 = sharedFlow.first()
    val result2 = sharedFlow.first()

    assertEquals(1, result1)
    assertEquals(1, result2)  // 相同的值
}
```

---

## 关键点总结

1. **TestDispatcher**: 使用 StandardTestDispatcher 或 UnconfinedTestDispatcher
2. **runTest**: 测试协程的主入口
3. **虚拟时间**: 使用 advanceTimeBy 和 advanceUntilIdle 控制
4. **Flow 测试**: 使用 toList 或 Turbine 库
5. **Main 调度器**: 使用 setMain 和 resetMain 设置

## 测试最佳实践

- [ ] 使用 runTest 作为测试入口
- [ ] 为涉及时间的测试使用 StandardTestDispatcher
- [ ] 为简单测试使用 UnconfinedTestDispatcher
- [ ] 使用 Rule 设置 Main 调度器
- [ ] 使用 Turbine 库测试 Flow
- [ ] 验证 Flow 的完成状态
- [ ] 测试异常情况

## 参考资料

- [Kotlin 协程测试官方文档](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/)
- [Turbine 库](https://github.com/cashapp/turbine)
