# Android 四大组件详解

本文档详细介绍 Android 四大组件（Activity、Service、BroadcastReceiver、ContentProvider）的核心概念、生命周期、使用场景和最佳实践。

---

## 目录

1. [Activity 组件](#1-activity-组件)
2. [Service 组件](#2-service-组件)
3. [BroadcastReceiver 组件](#3-broadcastreceiver-组件)
4. [ContentProvider 组件](#4-contentprovider-组件)
5. [最佳实践](#5-最佳实践)

---

## 1. Activity 组件

### 1.1 什么是 Activity

Activity 是 Android 四大组件中最常用的一个，代表一个具有用户界面的单一屏幕。可以将其理解为 MVC 模式中的 View 层。

```
┌─────────────────────────────────┐
│           Activity              │
│  ┌───────────────────────────┐  │
│  │      UI Layout            │  │
│  │  ┌─────────────────────┐  │  │
│  │  │    Views/Widgets    │  │  │
│  │  └─────────────────────┘  │  │
│  └───────────────────────────┘  │
│                                 │
│  ┌───────────────────────────┐  │
│  │   Business Logic          │  │
│  │   - Event Handling        │  │
│  │   - Lifecycle Management  │  │
│  └───────────────────────────┘  │
└─────────────────────────────────┘
```

### 1.2 Activity 生命周期

#### 完整生命周期图

```
                    ┌──────────────────┐
                    │    onCreate()    │  ← 创建：初始化
                    └────────┬─────────┘
                             ↓
                    ┌──────────────────┐
                    │    onStart()     │  ← 可见但不可交互
                    └────────┬─────────┘
                             ↓
                    ┌──────────────────┐
     ┌──────────────│    onResume()    │  ← 可见可交互（前台）
     │              └────────┬─────────┘
     │                       ↓
     │              ┌──────────────────┐
     │              │   Running 状态   │
     │              └────────┬─────────┘
     │                       ↓
     │              ┌──────────────────┐
     └──────────────│    onPause()     │  ← 失去焦点
                    └────────┬─────────┘
                             ↓
                    ┌──────────────────┐
     ┌──────────────│    onStop()      │  ← 不可见
     │              └────────┬─────────┘
     │                       ↓
     │              ┌──────────────────┐
     └──────────────│   onRestart()    │  ← 重新启动
                    └────────┬─────────┘
                             ↓
                    ┌──────────────────┐
                    │    onDestroy()   │  ← 销毁：释放资源
                    └──────────────────┘
```

#### 生命周期场景对照表

| 场景 | 回调顺序 |
|------|----------|
| 首次启动 | onCreate → onStart → onResume |
| 用户返回桌面 | onPause → onStop |
| 用户切换回来 | onRestart → onStart → onResume |
| 打开对话框 Activity | onPause（原 Activity） |
| 关闭对话框 Activity | onResume（原 Activity） |
| 打开普通 Activity | onPause → onStop（原 Activity） |
| 返回原 Activity | onRestart → onStart → onResume |
| 销毁 | onPause → onStop → onDestroy |
| 旋转屏幕 | onPause → onStop → onDestroy → onCreate → onStart → onResume |

### 1.3 Intent 数据传递

#### 显式 Intent vs 隐式 Intent

```kotlin
// 显式 Intent：直接指定目标
val intent = Intent(this, TargetActivity::class.java)
startActivity(intent)

// 隐式 Intent：通过 Action 匹配
val intent = Intent(Intent.ACTION_VIEW)
intent.data = Uri.parse("https://www.example.com")
startActivity(intent)
```

#### 数据传递方式

| 方式 | 适用场景 | 性能 |
|------|----------|------|
| 基本类型 | 简单数据（int, String, boolean） | ⭐⭐⭐⭐⭐ |
| Bundle | 多个数据打包 | ⭐⭐⭐⭐ |
| Parcelable | 复杂对象（推荐） | ⭐⭐⭐⭐ |
| Serializable | 复杂对象（简单但不推荐） | ⭐⭐ |

```kotlin
// Parcelable 实现（Kotlin 推荐）
@Parcelize
data class User(
    val id: Int,
    val name: String,
    val email: String
) : Parcelable

// 使用
intent.putExtra("user", user)
val user = intent.getParcelableExtra<User>("user")
```

### 1.4 启动模式详解

| 模式 | 特点 | 使用场景 |
|------|------|----------|
| **standard** | 每次启动都创建新实例 | 默认模式，普通页面 |
| **singleTop** | 栈顶复用，调用 onNewIntent | 通知打开详情页 |
| **singleTask** | 栈内复用，清除其上所有 Activity | 主页、登录页 |
| **singleInstance** | 全局单实例，独占任务栈 | 拨号器、来电界面 |

```
standard 模式:
A → B → B → B (三个 B 实例)

singleTop 模式:
A → B → B (第二个 B 复用栈顶，调用 onNewIntent)

singleTask 模式:
A → B → C → B (清除 C，栈变为 A → B)
```

### 1.5 Activity Result API

替代旧版 `startActivityForResult`：

```kotlin
// 注册回调
val launcher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    if (result.resultCode == RESULT_OK) {
        val data = result.data?.getStringExtra("result")
    }
}

// 启动
launcher.launch(Intent(this, TargetActivity::class.java))
```

常用内置 Contract：
- `StartActivityForResult` - 通用结果
- `RequestPermission` - 单个权限
- `RequestMultiplePermissions` - 多个权限
- `PickContact` - 选择联系人
- `TakePicture` - 拍照
- `GetContent` - 获取内容

---

## 2. Service 组件

### 2.1 什么是 Service

Service 是没有用户界面的组件，用于执行后台操作。**重要：Service 运行在主线程，耗时操作需要创建子线程。**

```
┌─────────────────────────────────┐
│           Service               │
│                                 │
│  ┌───────────────────────────┐  │
│  │   Main Thread (UI)        │  │
│  │   - onCreate/onStart      │  │
│  │   - onBind                │  │
│  └───────────────────────────┘  │
│                                 │
│  ┌───────────────────────────┐  │
│  │   Worker Thread           │  │
│  │   - Background Tasks      │  │
│  │   - Coroutine/Thread      │  │
│  └───────────────────────────┘  │
└─────────────────────────────────┘
```

### 2.2 Service 启动方式

#### startService 启动

```kotlin
// 启动
val intent = Intent(this, MyService::class.java)
startService(intent)

// 停止
stopService(intent)
// 或在 Service 内部
stopSelf()
```

生命周期：`onCreate() → onStartCommand() → (运行) → onDestroy()`

#### bindService 绑定

```kotlin
private val connection = object : ServiceConnection {
    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        val binder = service as MyService.LocalBinder
        myService = binder.getService()
        isBound = true
    }

    override fun onServiceDisconnected(name: ComponentName) {
        isBound = false
    }
}

// 绑定
bindService(Intent(this, MyService::class.java), connection, Context.BIND_AUTO_CREATE)

// 解绑
unbindService(connection)
```

生命周期：`onCreate() → onBind() → (连接) → onUnbind() → onDestroy()`

### 2.3 前台服务

Android 8.0+ 后台限制，需要使用前台服务：

```kotlin
class DownloadService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 必须在 5 秒内调用 startForeground
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        return START_NOT_STICKY
    }
}
```

前台服务类型（Android 14+）：
- `FOREGROUND_SERVICE_TYPE_DATA_SYNC` - 数据同步
- `FOREGROUND_SERVICE_TYPE_LOCATION` - 位置
- `FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK` - 媒体播放
- `FOREGROUND_SERVICE_TYPE_CAMERA` - 相机

### 2.4 onStartCommand 返回值

| 返回值 | 行为 | 使用场景 |
|--------|------|----------|
| `START_STICKY` | 重建服务，Intent 为 null | 音乐播放 |
| `START_NOT_STICKY` | 不重建服务 | 一次性任务 |
| `START_REDELIVER_INTENT` | 重建服务，重传 Intent | 文件下载 |

### 2.5 Service 替代方案

| 场景 | 推荐方案 |
|------|----------|
| 可延迟的后台任务 | WorkManager |
| 需要立即执行的任务 | 前台服务 |
| 应用内通信 | 绑定服务 |
| 系统调度任务 | JobScheduler |

---

## 3. BroadcastReceiver 组件

### 3.1 什么是 BroadcastReceiver

BroadcastReceiver 用于接收和处理系统或应用发出的广播消息。

```
┌─────────────────┐     Broadcast      ┌─────────────────┐
│   Sender App    │ ──────────────────→│  Receiver App   │
│                 │                     │                 │
│ sendBroadcast() │                     │  onReceive()   │
└─────────────────┘                     └─────────────────┘
```

### 3.2 注册方式对比

| 特性 | 静态注册 | 动态注册 |
|------|----------|----------|
| 注册位置 | AndroidManifest.xml | 代码中 |
| 生命周期 | 应用未启动也能接收 | 跟随注册者 |
| Android 8.0+ | 限制隐式广播 | 可接收隐式广播 |
| 使用场景 | 系统广播 | 应用内广播 |

#### 静态注册

```xml
<receiver android:name=".MyReceiver" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
    </intent-filter>
</receiver>
```

#### 动态注册

```kotlin
private val receiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // 处理广播
    }
}

override fun onResume() {
    super.onResume()
    registerReceiver(receiver, IntentFilter("com.example.ACTION"))
}

override fun onPause() {
    super.onPause()
    unregisterReceiver(receiver)
}
```

### 3.3 广播类型

#### 普通广播

```kotlin
// 发送
val intent = Intent("com.example.ACTION")
sendBroadcast(intent)
```

特点：异步，所有接收者同时收到

#### 有序广播

```kotlin
// 发送
sendOrderedBroadcast(intent, null)

// 接收者按优先级顺序处理
// 高优先级可以修改或拦截广播
```

特点：
- 按优先级顺序传递
- 可以通过 `abortBroadcast()` 拦截
- 可以通过 `setResultData()` 修改数据

```
优先级 100: Receiver A → 修改数据 → 传递
     ↓
优先级 50:  Receiver B → 收到修改后的数据
```

#### 本地广播

```kotlin
// 获取 LocalBroadcastManager
val lbm = LocalBroadcastManager.getInstance(context)

// 注册
lbm.registerReceiver(receiver, IntentFilter("com.example.LOCAL"))

// 发送
lbm.sendBroadcast(Intent("com.example.LOCAL"))

// 注销
lbm.unregisterReceiver(receiver)
```

特点：
- 只在应用内传递
- 更安全，无法被外部应用监听
- 更高效，无需 IPC

### 3.4 常用系统广播

| 广播 | 说明 | 权限 |
|------|------|------|
| `BOOT_COMPLETED` | 开机完成 | RECEIVE_BOOT_COMPLETED |
| `AIRPLANE_MODE` | 飞行模式切换 | 无 |
| `BATTERY_CHANGED` | 电量变化 | 无 |
| `CONNECTIVITY_CHANGE` | 网络变化 | ACCESS_NETWORK_STATE |
| `SCREEN_ON/OFF` | 屏幕开关 | 无 |

---

## 4. ContentProvider 组件

### 4.1 什么是 ContentProvider

ContentProvider 用于在不同应用间共享数据，提供统一的数据访问接口。

```
┌─────────────────┐     ContentProvider     ┌─────────────────┐
│    App A        │ ←─────────────────────→ │    App B        │
│                 │                         │                 │
│ ContentResolver │     ┌───────────────┐   │ ContentResolver │
│                 │     │    SQLite     │   │                 │
│  query()        │     │    Database   │   │  query()        │
│  insert()       │     │               │   │  insert()       │
│  update()       │     └───────────────┘   │  update()       │
│  delete()       │                         │  delete()       │
└─────────────────┘                         └─────────────────┘
```

### 4.2 URI 结构

```
content://authority/path/id
    │         │        │   │
    │         │        │   └── 具体记录 ID（可选）
    │         │        └────── 数据路径
    │         └─────────────── 授权者（唯一标识）
    └───────────────────────── 固定前缀

示例：
content://com.example.provider/users        // 所有用户
content://com.example.provider/users/1       // ID 为 1 的用户
```

### 4.3 实现 ContentProvider

```kotlin
class UserProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/users")

        private const val CODE_USERS = 1
        private const val CODE_USER_ID = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "users", CODE_USERS)
            addURI(AUTHORITY, "users/#", CODE_USER_ID)
        }
    }

    override fun onCreate(): Boolean {
        // 初始化数据库
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?,
                       selection: String?, selectionArgs: Array<String>?,
                       sortOrder: String?): Cursor? {
        // 查询数据
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        // 插入数据
    }

    override fun update(uri: Uri, values: ContentValues?,
                        selection: String?, selectionArgs: Array<String>?): Int {
        // 更新数据
    }

    override fun delete(uri: Uri, selection: String?,
                        selectionArgs: Array<String>?): Int {
        // 删除数据
    }

    override fun getType(uri: Uri): String {
        // 返回 MIME 类型
        return when (uriMatcher.match(uri)) {
            CODE_USERS -> "vnd.android.cursor.dir/vnd.example.user"
            CODE_USER_ID -> "vnd.android.cursor.item/vnd.example.user"
            else -> throw IllegalArgumentException("Unknown URI")
        }
    }
}
```

### 4.4 使用 ContentResolver

```kotlin
// 查询
val cursor = contentResolver.query(
    UserProvider.CONTENT_URI,
    arrayOf("_id", "name", "email"),
    null, null, null
)

// 插入
val values = ContentValues().apply {
    put("name", "张三")
    put("email", "zhangsan@example.com")
}
val uri = contentResolver.insert(UserProvider.CONTENT_URI, values)

// 更新
val values = ContentValues().apply {
    put("name", "李四")
}
val count = contentResolver.update(UserProvider.CONTENT_URI, values,
    "_id = ?", arrayOf("1"))

// 删除
val count = contentResolver.delete(UserProvider.CONTENT_URI,
    "_id = ?", arrayOf("1"))
```

### 4.5 ContentObserver 监听数据变化

```kotlin
private val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
    override fun onChange(selfChange: Boolean, uri: Uri?) {
        // 数据变化时刷新 UI
        refreshData()
    }
}

// 注册
contentResolver.registerContentObserver(UserProvider.CONTENT_URI, true, observer)

// 注销
contentResolver.unregisterContentObserver(observer)
```

### 4.6 批量操作

```kotlin
// applyBatch - 批量执行多个操作
val operations = ArrayList<ContentProviderOperation>()
operations.add(ContentProviderOperation.newInsert(uri).withValue("name", "A").build())
operations.add(ContentProviderOperation.newInsert(uri).withValue("name", "B").build())
contentResolver.applyBatch(UserProvider.AUTHORITY, operations)

// bulkInsert - 批量插入
val valuesArray = Array(10) { i ->
    ContentValues().apply { put("name", "User$i") }
}
contentResolver.bulkInsert(uri, valuesArray)
```

---

## 5. 最佳实践

### 5.1 Activity 最佳实践

1. **避免在 Activity 中处理复杂业务逻辑**，使用 ViewModel + Repository
2. **使用 Activity Result API** 替代 startActivityForResult
3. **合理设置启动模式**，避免任务栈混乱
4. **保存和恢复状态**，处理系统配置变化

```kotlin
override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putString("key", value)
}

override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    value = savedInstanceState.getString("key")
}
```

### 5.2 Service 最佳实践

1. **耗时操作必须使用子线程**（Service 运行在主线程）
2. **Android 8.0+ 使用前台服务**，避免后台限制
3. **绑定服务时注意内存泄漏**，及时解绑
4. **优先使用 WorkManager** 处理可延迟任务

### 5.3 BroadcastReceiver 最佳实践

1. **动态注册时在对应生命周期注销**
2. **本地广播使用 LocalBroadcastManager**
3. **onReceive 中避免耗时操作**（10 秒限制）
4. **Android 8.0+ 注意隐式广播限制**

### 5.4 ContentProvider 最佳实践

1. **使用 UriMatcher 匹配 URI**
2. **实现 getType() 返回正确的 MIME 类型**
3. **通知数据变化** `contentResolver.notifyChange(uri, null)`
4. **批量操作使用 applyBatch 或 bulkInsert**

---

## 6. 模块结构

```
components-demo/
├── src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/peter/components/demo/
│   │   ├── MainActivity.kt              # 主入口
│   │   ├── MenuItem.kt                  # 菜单数据
│   │   ├── MainAdapter.kt               # 菜单适配器
│   │   │
│   │   ├── activity/                    # Activity 示例
│   │   │   ├── basic/                   # 基础
│   │   │   ├── lifecycle/               # 生命周期
│   │   │   ├── launchmode/              # 启动模式
│   │   │   └── advanced/                # 进阶
│   │   │
│   │   ├── service/                     # Service 示例
│   │   │   ├── basic/                   # 基础服务
│   │   │   ├── foreground/              # 前台服务
│   │   │   └── bind/                    # 绑定服务
│   │   │
│   │   ├── receiver/                    # BroadcastReceiver 示例
│   │   │   ├── ordered/                 # 有序广播
│   │   │   └── ...
│   │   │
│   │   └── provider/                    # ContentProvider 示例
│   │       ├── UserProvider.kt          # Provider 实现
│   │       └── Provider*Activity.kt     # 使用示例
│   │
│   └── res/
│       ├── layout/                      # 布局文件
│       └── values/                      # 资源文件
│
└── README.md                            # 本文档
```

---

## 7. 运行方式

```bash
# 构建模块
./gradlew :components-demo:build

# 安装到设备
./gradlew :components-demo:installDebug
```

---

## 8. 参考资料

- [Activity 官方文档](https://developer.android.com/guide/components/activities)
- [Service 官方文档](https://developer.android.com/guide/components/services)
- [BroadcastReceiver 官方文档](https://developer.android.com/guide/components/broadcasts)
- [ContentProvider 官方文档](https://developer.android.com/guide/topics/providers/content-providers)
- [Activity Result API](https://developer.android.com/training/basics/intents/result)
- [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
