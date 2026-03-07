# 协程进阶原理教程

本文档深入讲解 Kotlin 协程的底层原理，包括 Continuation 机制、状态机原理、自定义 CoroutineScope，以及 Room 与协程的集成。

## 目录

1. [Continuation 原理](#continuation-原理)
2. [状态机原理](#状态机原理)
3. [自定义 CoroutineScope](#自定义-coroutinescope)
4. [Room + 协程](#room--协程)

---

## Continuation 原理

### 什么是 Continuation？

Continuation 是协程挂起和恢复的核心接口，它封装了协程在挂起点的状态，以及挂起后恢复执行所需的上下文信息。

### Continuation 接口定义

```kotlin
public interface Continuation<in T> {
    // 协程上下文，包含 Job、Dispatcher 等信息
    public val context: CoroutineContext

    // 恢复协程执行，传入挂起点的返回值或结果
    public fun resumeWith(result: Result<T>)
}
```

### 挂起函数的编译器转换

当编译器遇到 suspend 函数时，会自动添加一个 Continuation 参数：

```kotlin
// 源码
suspend fun getData(): String {
    delay(1000)
    return "data"
}

// 编译后（伪代码）
fun getData(continuation: Continuation<String>): Any? {
    // 状态机逻辑
    // 返回 COROUTINE_SUSPENDED 或实际值
}
```

返回类型 `Any?` 表示三种可能：
1. `COROUTINE_SUSPENDED` - 协程已挂起
2. 实际返回值 - 协程正常完成
3. 异常 - 协程失败

### 挂起和恢复过程

1. **挂起**: 当协程遇到挂起点（如 `delay`），它会保存当前状态到 Continuation
2. **等待**: 协程释放线程，其他代码可以执行
3. **恢复**: 当条件满足时，调用 `Continuation.resumeWith()` 恢复执行

### Continuation 扩展函数

```kotlin
// 正常恢复
fun <T> Continuation<T>.resume(value: T) {
    resumeWith(Result.success(value))
}

// 异常恢复
fun <T> Continuation<T>.resumeWithException(exception: Throwable) {
    resumeWith(Result.failure(exception))
}
```

### 示例流程

```kotlin
suspend fun example() {
    println("Step 1")
    delay(1000)  // 挂起点 1
    println("Step 2")
    delay(1000)  // 挂起点 2
    println("Step 3")
}

// 编译器转换后（简化）：
// 1. 初始调用：label = 0
// 2. 遇到第一个 delay：保存 label = 1，返回 COROUTINE_SUSPENDED
// 3. delay 完成后调用 resumeWith
// 4. 恢复执行：读取 label = 1，继续执行
// 5. 遇到第二个 delay：保存 label = 2，返回 COROUTINE_SUSPENDED
// 6. delay 完成后调用 resumeWith
// 7. 恢复执行：读取 label = 2，继续执行
// 8. 完成
```

### 相关 Activity

- [ContinuationActivity](../src/main/java/com/peter/coroutine/demo/advanced/ContinuationActivity.kt)

---

## 状态机原理

### 协程状态机概述

Kotlin 编译器会将每个 suspend 函数转换为一个状态机，使用 `label`（标签）来标记不同的执行状态，实现挂起和恢复的逻辑。

### 状态机核心组件

1. **label**: 状态标记，记录当前执行到哪个挂起点
2. **局部变量**: 保存挂起前的局部变量，用于恢复
3. **Continuation**: 保存和恢复状态的容器

### 编译器转换示例

#### 原始协程代码

```kotlin
suspend fun calculate(): Int {
    val a = stepOne()    // 挂起点 1
    val b = stepTwo()    // 挂起点 2
    val c = stepThree()  // 挂起点 3
    return a + b + c
}
```

#### 编译后状态机（简化伪代码）

```kotlin
fun calculate(cont: Continuation): Any? {
    // 状态机类
    class StateMachine(cont: Continuation) : ContinuationImpl(cont) {
        var label = 0
        var a: Int = 0
        var b: Int = 0
        var c: Int = 0

        fun doResume(result: Any?): Any? {
            when (label) {
                0 -> {
                    label = 1
                    a = stepOne(this)  // 传入 this 作为 Continuation
                    if (a == COROUTINE_SUSPENDED) return
                    // fall through to next case
                }
                1 -> {
                    label = 2
                    b = stepTwo(this)
                    if (b == COROUTINE_SUSPENDED) return
                }
                2 -> {
                    label = 3
                    c = stepThree(this)
                    if (c == COROUTINE_SUSPENDED) return
                }
                3 -> {
                    return a + b + c
                }
            }
        }
    }

    // 创建或复用状态机实例
    val sm = cont as? StateMachine ?: StateMachine(cont)
    return sm.doResume(null)
}
```

### 状态转换流程

```
状态 0 (初始)
    ↓
调用 stepOne()
    ↓
挂起? ─────是────→ 返回 COROUTINE_SUSPENDED
    │                    ↓
    否              等待恢复
    ↓                    ↓
状态 1            调用 resumeWith()
    ↓                    ↓
调用 stepTwo()        继续执行
    ↓
   ...
```

### 状态机优化

编译器会进行以下优化：

1. **状态机复用**: 多次调用同一 suspend 函数会复用状态机对象
2. **尾调用优化**: 某些情况下可以避免创建新的状态机
3. **局部变量优化**: 只保存跨挂起点使用的变量

### 相关 Activity

- [StateMachineActivity](../src/main/java/com/peter/coroutine/demo/advanced/StateMachineActivity.kt)

---

## 自定义 CoroutineScope

### CoroutineScope 接口

```kotlin
public interface CoroutineScope {
    public val coroutineContext: CoroutineContext
}
```

CoroutineScope 是协程的执行范围，它定义了协程的上下文（Context），包括 Job、Dispatcher 等元素。

### 创建自定义 Scope 的方式

#### 1. 使用 MainScope()

预配置的 Scope，使用 `SupervisorJob + Main Dispatcher`。

```kotlin
class MyActivity : AppCompatActivity() {
    private val scope = MainScope()

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()  // 必须手动取消
    }
}
```

#### 2. 使用 CoroutineScope() 工厂函数

```kotlin
val customScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

// 需要手动取消
customScope.cancel()
```

#### 3. 实现 CoroutineScope 接口

```kotlin
class MyCustomScope : CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext = job + Dispatchers.IO

    fun destroy() {
        job.cancel()
    }
}
```

### 自定义 Scope 的关键元素

| 元素 | 说明 |
|------|------|
| Job | 控制协程的生命周期，支持取消 |
| SupervisorJob | 子协程失败不会影响其他子协程 |
| Dispatcher | 决定协程在哪个线程执行 |

### 最佳实践

#### 在 Activity 中

```kotlin
class MainActivity : AppCompatActivity() {
    private val scope = MainScope()

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
```

#### 在 ViewModel 中

```kotlin
class MyViewModel : ViewModel() {
    // 直接使用 viewModelScope (推荐)
    fun loadData() {
        viewModelScope.launch {
            // ...
        }
    }

    // 或自定义
    private val customScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
}
```

#### 在 Service 中

```kotlin
class MyService : Service() {
    private val scope = CoroutineScope(SupervisorJob())

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
```

### 生命周期管理

```kotlin
// 取消所有子协程（保留 Scope）
scope.coroutineContext[Job]?.cancelChildren()

// 完全取消 Scope
scope.cancel()
```

### 相关 Activity

- [CustomScopeActivity](../src/main/java/com/peter/coroutine/demo/advanced/CustomScopeActivity.kt)

---

## Room + 协程

### Room 对协程的支持

Room 提供了对 Kotlin 协程的原生支持，主要体现在：

1. **Suspend 函数**: DAO 中的方法可以声明为 suspend，Room 会自动在后台执行
2. **Flow 查询**: 查询方法可以返回 Flow，实现数据的实时监听
3. **事务支持**: 使用 `@Transaction` 注解的方法支持协程事务

### DAO 示例

```kotlin
@Dao
interface UserDao {
    // Suspend 函数
    @Insert
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM users")
    suspend fun getAll(): List<UserEntity>

    @Delete
    suspend fun delete(user: UserEntity)

    // Flow 查询
    @Query("SELECT * FROM users")
    fun getAllFlow(): Flow<List<UserEntity>>

    // 事务
    @Transaction
    suspend fun insertUsers(users: List<UserEntity>) {
        users.forEach { insert(it) }
    }
}
```

### 使用要点

- Room 的 suspend 函数自动在 IO 线程执行
- 不需要手动使用 `withContext(Dispatchers.IO)`
- Flow 查询在数据变化时自动发射新值

### 插入数据

```kotlin
suspend fun insertUser(user: UserEntity) {
    // Room 自动在 IO 线程执行
    val id = userDao.insert(user)
    println("Inserted with id: $id")
}
```

### 查询数据

```kotlin
// 一次性查询
suspend fun queryUsers() {
    val users = userDao.getAll()
    users.forEach { println(it) }
}

// Flow 查询（实时监听）
fun observeUsers() {
    lifecycleScope.launch {
        userDao.getAllFlow()
            .collect { users ->
                updateUI(users)
            }
    }
}
```

### 配合 ViewModel

```kotlin
class UserViewModel(private val userDao: UserDao) : ViewModel() {
    private val _users = MutableStateFlow<List<UserEntity>>(emptyList())
    val users: StateFlow<List<UserEntity>> = _users.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            userDao.getAllFlow()
                .collect { userList ->
                    _users.value = userList
                }
        }
    }

    fun addUser(name: String, email: String) {
        viewModelScope.launch {
            userDao.insert(UserEntity(name = name, email = email))
        }
    }
}
```

### 事务处理

```kotlin
@Dao
interface UserDao {
    @Transaction
    suspend fun replaceAllUsers(newUsers: List<UserEntity>) {
        deleteAll()
        newUsers.forEach { insert(it) }
    }
}
```

### 相关 Activity

- [RoomExampleActivity](../src/main/java/com/peter/coroutine/demo/advanced/RoomExampleActivity.kt)

---

## 关键点总结

1. **Continuation**: 挂起和恢复的核心机制，包含上下文和恢复方法
2. **状态机**: 编译器将 suspend 函数转换为状态机，使用 label 标记状态
3. **自定义 Scope**: 使用 MainScope() 或 CoroutineScope() 创建，需要手动取消
4. **Room + 协程**: suspend 函数自动在 IO 执行，Flow 查询实时更新

## 下一步

- [测试教程](07-testing.md) - 学习协程测试
