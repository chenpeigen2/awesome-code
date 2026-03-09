# Notification Demo 设计文档

## 概述

创建一个完整的Android通知系统演示模块，覆盖所有通知类型、Channel管理、分组、铃声和权限处理。

## 需求确认

| 功能领域 | 选择 |
|---------|------|
| 通知类型 | 高级通知集（基础+媒体+Bubble+Conversation+持续活动等） |
| Channel管理 | Channel分组演示（动态CRUD + ChannelGroup） |
| 通知权限 | 权限状态可视化（专门页面 + 主页指示器） |
| 通知铃声 | 自定义铃声支持（系统选择器 + 自定义铃声文件） |
| 通知分组 | 多分组+堆叠演示 |
| UI风格 | TabLayout + ViewPager2 |

## 整体架构

### 模块结构
```
notification-demo/
├── build.gradle.kts
├── src/main/
│   ├── java/com/peter/notification/demo/
│   │   ├── MainActivity.kt              # 主入口，TabLayout+ViewPager2
│   │   ├── NotificationItem.kt          # 数据模型
│   │   ├── NotificationAdapter.kt       # 列表适配器
│   │   ├── NotificationHelper.kt        # 通知工具类（核心）
│   │   ├── fragments/
│   │   │   ├── TypeFragment.kt          # 通知类型Tab
│   │   │   ├── ChannelFragment.kt       # Channel管理Tab
│   │   │   ├── GroupFragment.kt         # 分组通知Tab
│   │   │   ├── SoundFragment.kt         # 铃声设置Tab
│   │   │   └── PermissionFragment.kt    # 权限管理Tab
│   │   ├── channel/
│   │   │   ├── ChannelManager.kt        # Channel创建/删除逻辑
│   │   │   └── ChannelGroupManager.kt   # ChannelGroup管理
│   │   └── sound/
│   │       └── RingtoneManager.kt       # 铃声管理
│   ├── res/
│   │   ├── layout/
│   │   ├── values/
│   │   ├── raw/                         # 自定义铃声资源
│   │   └── drawable/
│   └── AndroidManifest.xml
```

### 核心组件职责
- **NotificationHelper** - 封装所有通知发送逻辑，提供统一的API
- **ChannelManager** - 管理NotificationChannel的CRUD操作
- **ChannelGroupManager** - 管理NotificationChannelGroup
- **RingtoneManager** - 处理铃声选择和自定义铃声

### 技术选型
- ViewBinding 用于视图绑定
- Fragment + ViewPager2 实现Tab导航
- ViewModel 管理每个Fragment的状态
- SharedPreferences 存储用户自定义的Channel配置

## Tab 1: 通知类型 (TypeFragment)

### 分类结构

**1. 基础通知**
| 类型 | 功能 |
|------|------|
| 普通通知 | 标题+内容+小图标 |
| 大文本通知 | 展开显示长文本 |
| 大图片通知 | 展开显示大图 |
| 收件箱样式 | 多行消息列表 |

**2. 消息通知**
| 类型 | 功能 |
|------|------|
| MessagingStyle | 模拟聊天消息，支持多人对话 |
| Conversation | Android 11+ 对话通知，支持Bubble |
| Bubble通知 | 气泡形式悬浮显示 |

**3. 进度与状态**
| 类型 | 功能 |
|------|------|
| 进度条通知 | 确定/不确定进度，可更新 |
| 持续活动通知 | Android 11+ 健身/导航等场景 |
| 前台服务通知 | 带进度的前台服务 |

**4. 媒体通知**
| 类型 | 功能 |
|------|------|
| 媒体播放通知 | 播放控制按钮，专辑封面 |

**5. 高级功能**
| 类型 | 功能 |
|------|------|
| 自定义布局 | RemoteViews自定义通知UI |
| 定时通知 | setOnlyAlertOnce定时触发 |
| 按钮操作通知 | Action按钮点击响应 |

### 交互设计
- 每个通知类型是一个卡片，包含：标题、描述、发送按钮
- 点击发送后，实际发送通知到系统
- 部分通知支持参数配置（如进度值、图片选择）

## Tab 2: Channel管理 (ChannelFragment)

### 功能布局

**顶部：ChannelGroup管理区**
- 显示所有ChannelGroup列表
- 每个Group显示：名称、包含的Channel数量、删除按钮
- 底部有"新建Group"按钮

**中部：Channel列表**
- 按Group分组显示所有Channel
- 每个Channel卡片显示：名称、ID、重要性级别、状态指示灯、编辑/删除按钮

**底部：操作区**
- "新建Channel"按钮
- "重置所有Channel"按钮

### Channel属性配置
- Channel名称
- Channel ID
- 所属Group（下拉选择）
- 重要性（紧急/高/中/低/最小）
- 启用声音/震动/指示灯
- 通知音选择

### 默认预设
预创建3个Group和对应的Channel：
1. **工作** Group → "邮件"、"日程" Channel
2. **社交** Group → "消息"、"评论" Channel
3. **系统** Group → "下载"、"更新" Channel

## Tab 3: 分组通知 (GroupFragment)

### 功能布局

**顶部：分组配置区**
- 分组数量选择（2-5个分组）
- "生成测试分组"按钮

**中部：分组演示区**
- 每个分组是一个卡片，显示分组名称、图标、通知数量
- 每个分组有"发送一条"、"发送5条"、"清除"按钮
- 分组之间用颜色区分

**底部：全局操作区**
- "发送所有分组通知"按钮
- "清除所有通知"按钮

### 分组演示场景
| 分组 | 颜色 | 模拟场景 |
|------|------|----------|
| 聊天消息 | 蓝色 | 即时通讯消息 |
| 邮件 | 绿色 | 邮件通知 |
| 社交动态 | 橙色 | 点赞、评论 |
| 系统提醒 | 灰色 | 系统通知 |

### 堆叠效果
- 当同组通知>=3条时，自动展示堆叠样式
- GroupSummary通知始终显示，作为组头

## Tab 4: 铃声设置 (SoundFragment)

### 功能布局

**顶部：当前铃声显示**
- 显示当前选中的铃声名称
- "播放预览"按钮

**中部：系统铃声列表**
- RecyclerView展示系统可用铃声
- 每个铃声项：名称、单选按钮、播放按钮

**底部：自定义铃声区**
- "添加自定义铃声"按钮
- 已添加的自定义铃声列表

### 自定义铃声
```
res/raw/
├── custom_sound_1.mp3
└── custom_sound_2.mp3
```

- 预置2个示例自定义铃声
- 使用MediaPlayer播放预览

## Tab 5: 权限管理 (PermissionFragment)

### 功能布局

**顶部：权限状态卡片**
- 大图标显示当前权限状态（已授权/未授权）
- 状态说明文字

**中部：权限详情**
- Android版本说明
- 当前设备API级别
- 权限请求历史

**底部：操作区**
- "请求通知权限"按钮（未授权时）
- "打开系统设置"按钮（被永久拒绝时）
- "检查权限状态"按钮

### MainActivity权限指示器
```
┌────────────────────────────────────┐
│ 🔔 通知权限：已授权                  │  <-- 绿色背景
└────────────────────────────────────┘
或
┌────────────────────────────────────┐
│ ⚠️ 通知权限：未授权 [去开启]         │  <-- 红色背景，可点击
└────────────────────────────────────┘
```

### 权限处理逻辑
- Android 13+ (API 33) 需要运行时请求 POST_NOTIFICATIONS 权限
- `onResume()` 时重新检查权限状态
- 处理 shouldShowRequestPermissionRationale 三种状态

## 依赖配置

```kotlin
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
}
```

## 文件清单

### Kotlin文件 (13个)
1. MainActivity.kt
2. NotificationItem.kt
3. NotificationAdapter.kt
4. NotificationHelper.kt
5. fragments/TypeFragment.kt
6. fragments/ChannelFragment.kt
7. fragments/GroupFragment.kt
8. fragments/SoundFragment.kt
9. fragments/PermissionFragment.kt
10. channel/ChannelManager.kt
11. channel/ChannelGroupManager.kt
12. sound/RingtoneManager.kt
13. ViewPagerAdapter.kt

### Layout文件 (10+个)
1. activity_main.xml
2. fragment_type.xml
3. fragment_channel.xml
4. fragment_group.xml
5. fragment_sound.xml
6. fragment_permission.xml
7. item_notification_type.xml
8. item_channel.xml
9. item_channel_group.xml
10. item_ringtone.xml
11. dialog_channel_edit.xml

### 资源文件
1. strings.xml
2. colors.xml
3. dimens.xml
4. raw/custom_sound_1.mp3
5. raw/custom_sound_2.mp3

## AndroidManifest配置

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<application>
    <activity android:name=".MainActivity"
              android:exported="true">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
</application>
```
