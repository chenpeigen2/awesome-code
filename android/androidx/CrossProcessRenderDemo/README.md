# CrossProcessRenderDemo

Android跨进程渲染演示项目，基于 `SurfaceControlViewHost.SurfacePackage` 实现客户端-服务端跨进程Surface传递与渲染。

## 项目架构

```
CrossProcessRenderDemo/
├── common/          # 公共模块 - AIDL接口和数据类
├── client/          # 客户端模块 - 渲染内容提供方
└── server/          # 服务端模块 - 宿主容器提供方
```

## 核心技术

### SurfacePackage 跨进程传递

```
┌─────────────────┐                    ┌─────────────────┐
│   Client 进程    │                    │   Server 进程    │
│                 │                    │                 │
│  ┌───────────┐  │   SurfacePackage   │  ┌───────────┐  │
│  │ View内容   │  │ ─────────────────► │  │ SurfaceView│  │
│  │ (渲染视图)  │  │     (跨进程传递)    │  │ (显示内容) │  │
│  └───────────┘  │                    │  └───────────┘  │
│                 │                    │                 │
│ SurfaceControl  │                    │   HostToken    │
│ ViewHost        │                    │   (IBinder)    │
└─────────────────┘                    └─────────────────┘
```

### 关键API

| API | 作用 |
|-----|------|
| `SurfaceView.getHostToken()` | 获取宿主Token，用于创建SurfaceControlViewHost |
| `SurfaceControlViewHost` | 在客户端创建SurfacePackage |
| `SurfaceView.setChildSurfacePackage()` | 在服务端嵌入客户端的SurfacePackage |

## 模块详解

### Common模块

```
common/
├── src/main/aidl/com/example/common/
│   ├── IRenderService.aidl      # AIDL服务接口
│   └── WindowConfig.aidl        # 窗口配置Parcelable声明
└── src/main/java/com/example/common/
    └── WindowConfig.java        # 窗口配置类
```

**IRenderService接口：**
```java
interface IRenderService {
    IBinder getHostToken();                    // 获取宿主Token
    int getDisplayId();                        // 获取Display ID
    void showWindow(String windowId, WindowConfig config, SurfacePackage surfacePackage);
    void hideWindow(String windowId);
    void updateWindowPosition(String windowId, int x, int y);
    void updateWindowSize(String windowId, int width, int height);
}
```

### Client模块

```
client/
└── src/main/java/com/example/client/
    ├── ClientManager.java      # 客户端管理器（单例）
    ├── ClientActivity.java     # 客户端UI
    └── DemoRenderView.java     # 演示渲染视图
```

**ClientManager核心方法：**
```java
// 获取单例
ClientManager.getInstance()

// 初始化（使用Activity Context）
ClientManager.getInstance().init(context)

// 连接服务
ClientManager.getInstance().connectService()

// 显示窗口
String windowId = ClientManager.getInstance().showWindow(view, width, height, x, y)

// 隐藏窗口
ClientManager.getInstance().hideWindow(windowId)
```

### Server模块

```
server/
└── src/main/java/com/example/server/
    ├── HostManager.java        # 宿主管理器（单例）
    ├── RenderService.java      # AIDL服务实现
    └── HostActivity.java       # 服务端UI
```

**HostManager核心方法：**
```java
// 获取单例
HostManager.getInstance(context)

// 设置容器
HostManager.getInstance(context).setContainer(frameLayout)

// 获取HostToken
HostManager.getInstance(context).getHostToken()

// 显示/隐藏窗口
HostManager.getInstance(context).showWindow(windowId, config, surfacePackage)
HostManager.getInstance(context).hideWindow(windowId)
```

## 工作流程

### 1. 服务端初始化

```
HostActivity.onCreate()
    │
    ├── setupUI() → 创建FrameLayout容器
    │
    └── setupHostManager()
            │
            ├── HostManager.getInstance()
            ├── setCallback() → 设置回调
            └── setContainer() → 设置容器
                    │
                    └── 创建TokenSurfaceView
                            │
                            └── surfaceCreated() → 获取HostToken
```

### 2. 客户端连接

```
ClientActivity.onCreate()
    │
    ├── setupUI()
    │
    └── setupClientManager()
            │
            ├── ClientManager.getInstance()
            ├── init(context)
            ├── setCallback()
            └── connectService()
                    │
                    └── bindService() → 绑定RenderService
                            │
                            └── onServiceConnected() → 连接成功
```

### 3. 跨进程渲染

```
ClientManager.showWindow(view, width, height, x, y)
    │
    ├── getHostToken() ← 从服务端获取Token
    │
    ├── new SurfaceControlViewHost(context, display, hostToken)
    │
    ├── scvh.setView(view, width, height)
    │
    ├── surfacePackage = scvh.getSurfacePackage()
    │
    └── mRenderService.showWindow(windowId, config, surfacePackage)
            │
            └── HostManager.showWindow()
                    │
                    ├── new SurfaceView()
                    │
                    └── surfaceView.setChildSurfacePackage(surfacePackage)
                            │
                            └── 客户端内容显示在服务端
```

## 类图

```
┌─────────────────────────────────────────────────────────────────┐
│                         Common Module                           │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ IRenderService  │  │  WindowConfig   │  │ SurfacePackage  │  │
│  │    (AIDL)       │  │  (Parcelable)   │  │   (Parcelable)  │  │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┴───────────────┐
              ▼                               ▼
┌─────────────────────────┐     ┌─────────────────────────┐
│     Client Module       │     │     Server Module       │
├─────────────────────────┤     ├─────────────────────────┤
│  ┌─────────────────┐    │     │  ┌─────────────────┐    │
│  │ ClientManager   │    │     │  │  HostManager    │    │
│  │  (Singleton)    │    │     │  │  (Singleton)    │    │
│  └────────┬────────┘    │     │  └────────┬────────┘    │
│           │             │     │           │             │
│           ▼             │     │           ▼             │
│  ┌─────────────────┐    │     │  ┌─────────────────┐    │
│  │ ClientActivity  │    │     │  │  HostActivity   │    │
│  │    (UI层)       │    │     │  │    (UI层)       │    │
│  └─────────────────┘    │     │  └─────────────────┘    │
│                         │     │           │             │
│  ┌─────────────────┐    │     │           ▼             │
│  │ DemoRenderView  │    │     │  ┌─────────────────┐    │
│  │  (渲染内容)      │    │     │  │ RenderService   │    │
│  └─────────────────┘    │     │  │   (AIDL实现)    │    │
│                         │     │  └─────────────────┘    │
└─────────────────────────┘     └─────────────────────────┘
```

## 使用示例

### 服务端

```java
public class HostActivity extends AppCompatActivity {
    private HostManager mHostManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        FrameLayout container = findViewById(R.id.container);
        
        mHostManager = HostManager.getInstance(this);
        mHostManager.setCallback(new HostManager.HostCallback() {
            @Override
            public void onTokenReady(IBinder token) {
                // Token已准备好，可以接受客户端连接
            }

            @Override
            public void onWindowCountChanged(int count) {
                // 窗口数量变化
            }
        });
        mHostManager.setContainer(container);
    }
}
```

### 客户端

```java
public class ClientActivity extends AppCompatActivity {
    private ClientManager mClientManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        mClientManager = ClientManager.getInstance();
        mClientManager.init(this);
        mClientManager.setCallback(new ClientManager.ClientCallback() {
            @Override
            public void onServiceConnected() {
                // 服务已连接，可以开始渲染
            }

            @Override
            public void onWindowCreated(String windowId, int windowCount) {
                // 窗口创建成功
            }
            
            // ... 其他回调
        });
        mClientManager.connectService();
    }

    private void showWindow() {
        View contentView = createContentView();
        mClientManager.showWindow(contentView, 300, 300, 50, 50);
    }
}
```

## 注意事项

1. **HostToken生命周期**：HostToken与SurfaceView的生命周期绑定，当服务端Activity进入后台时，Surface可能被销毁导致Token失效

2. **包可见性**：Android 11+需要在AndroidManifest.xml中声明queries：
   ```xml
   <queries>
       <package android:name="com.example.server" />
   </queries>
   ```

3. **最低API要求**：minSdk 29 (Android 10)，因为`SurfaceView.getHostToken()`从API 29开始提供

4. **进程隔离**：客户端和服务端运行在独立进程中，通过AIDL进行IPC通信

## 扩展方向

1. **悬浮窗支持**：使用`SYSTEM_ALERT_WINDOW`权限实现后台渲染
2. **多窗口管理**：支持窗口层级、动画效果
3. **输入事件传递**：实现触摸事件从服务端到客户端的传递
4. **性能监控**：添加帧率、延迟监控
