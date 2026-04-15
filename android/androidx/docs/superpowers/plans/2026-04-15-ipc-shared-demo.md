# IPC SharedMemory / LocalSocket / FileShare Demo Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Create a single `ipc-demo` module demonstrating three Android cross-process data sharing mechanisms via a multi-process Service.

**Architecture:** Single app module with `BottomNavigationView` + manual Fragment switching (following network-demo pattern). A `IpcRemoteService` runs in `:remote` process, handling SharedMemory, LocalSocket, and File-based IPC. Each Fragment binds to the Service and uses its respective IPC method.

**Tech Stack:** Kotlin, ViewBinding, Material3, Messenger, SharedMemory API, LocalSocket, FileObserver

---

### Task 1: Module scaffolding — build.gradle.kts, manifest, settings

**Files:**
- Create: `ipc-demo/build.gradle.kts`
- Create: `ipc-demo/src/main/AndroidManifest.xml`
- Modify: `settings.gradle.kts` (add `:ipc-demo`)
- Create: `ipc-demo/src/main/res/values/themes.xml`

- [ ] **Step 1: Create build.gradle.kts**

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.peter.ipc.demo"
    compileSdk = libs.versions.compileSdk.get().toInt()
    compileSdkMinor = libs.versions.compileSdkMinor.get().toInt()

    defaultConfig {
        applicationId = "com.peter.ipc.demo"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
}
```

- [ ] **Step 2: Create AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher"
        android:supportsRtl="true"
        android:theme="@style/Theme.IpcDemo"
        tools:targetApi="31">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <service
            android:name=".service.IpcRemoteService"
            android:process=":remote"
            android:exported="false" />

    </application>

</manifest>
```

- [ ] **Step 3: Create themes.xml**

```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <style name="Theme.IpcDemo" parent="Theme.Material3.Light.NoActionBar">
        <item name="android:statusBarColor" tools:targetApi="l">@android:color/transparent</item>
        <item name="android:navigationBarColor">@color/surface</item>
        <item name="colorPrimary">@color/primary</item>
        <item name="android:windowBackground">@color/surface</item>
    </style>
</resources>
```

- [ ] **Step 4: Add `:ipc-demo` to settings.gradle.kts**

Append after the `// ANR Demo` line:

```kotlin
// IPC Demo - SharedMemory / LocalSocket / 文件共享跨进程示例
include(":ipc-demo")
```

- [ ] **Step 5: Commit**

```bash
git add ipc-demo/build.gradle.kts ipc-demo/src/main/AndroidManifest.xml ipc-demo/src/main/res/values/themes.xml settings.gradle.kts
git commit -m "feat(ipc-demo): add module scaffolding with build config and manifest"
```

---

### Task 2: Resource files — colors, strings, menu, color selector

**Files:**
- Create: `ipc-demo/src/main/res/values/colors.xml`
- Create: `ipc-demo/src/main/res/values/strings.xml`
- Create: `ipc-demo/src/main/res/menu/bottom_nav_menu.xml`
- Create: `ipc-demo/src/main/res/color/nav_item_colors.xml`

- [ ] **Step 1: Create colors.xml**

```xml
<resources>
    <color name="primary">#6200EE</color>
    <color name="primary_dark">#3700B3</color>
    <color name="surface">#FAFAFA</color>
    <color name="panel">#F0F0F0</color>
    <color name="text_primary">#212121</color>
    <color name="text_secondary">#757575</color>
    <color name="divider">#E0E0E0</color>

    <color name="nav_item_active">#6200EE</color>
    <color name="nav_item_inactive">#9E9E9E</color>

    <!-- 状态颜色 -->
    <color name="success">#4CAF50</color>
    <color name="error">#F44336</color>
    <color name="info">#2196F3</color>
</resources>
```

- [ ] **Step 2: Create strings.xml**

```xml
<resources>
    <string name="app_name">IPC Demo</string>
    <string name="tab_shared_memory">SharedMemory</string>
    <string name="tab_local_socket">LocalSocket</string>
    <string name="tab_file_share">文件共享</string>

    <string name="hint_input_text">输入要发送的文本…</string>
    <string name="btn_send">发送</string>
    <string name="btn_clear">清空</string>
    <string name="label_result">接收结果：</string>
    <string name="label_status">状态：%s</string>
    <string name="status_disconnected">未连接</string>
    <string name="status_connected">已连接</string>
    <string name="status_sending">发送中…</string>
    <string name="status_success">发送成功 (耗时 %d ms)</string>
    <string name="status_error">错误：%s</string>

    <string name="desc_shared_memory">
通过 SharedMemory 创建共享内存区域，跨进程传递 FileDescriptor，实现零拷贝数据传输。
适用于大数据量的跨进程共享场景。
    </string>
    <string name="desc_local_socket">
通过 LocalSocket/LocalServerSocket 在本地建立 Socket 连接进行跨进程通信。
基于 Linux abstract namespace，适用于流式数据传输场景。
    </string>
    <string name="desc_file_share">
通过共享文件进行跨进程数据交换，使用 FileObserver 监听文件变化。
最简单的跨进程通信方式，适用于对实时性要求不高的场景。
    </string>
</resources>
```

- [ ] **Step 3: Create bottom_nav_menu.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/nav_shared_memory"
        android:icon="@android:drawable/ic_menu_share"
        android:title="@string/tab_shared_memory" />
    <item
        android:id="@+id/nav_local_socket"
        android:icon="@android:drawable/ic_menu_info_details"
        android:title="@string/tab_local_socket" />
    <item
        android:id="@+id/nav_file_share"
        android:icon="@android:drawable/ic_menu_save"
        android:title="@string/tab_file_share" />
</menu>
```

- [ ] **Step 4: Create nav_item_colors.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:color="@color/nav_item_active" android:state_checked="true" />
    <item android:color="@color/nav_item_inactive" />
</selector>
```

- [ ] **Step 5: Commit**

```bash
git add ipc-demo/src/main/res/
git commit -m "feat(ipc-demo): add resource files (colors, strings, menu, color selector)"
```

---

### Task 3: Layout files — activity_main and fragment layouts

**Files:**
- Create: `ipc-demo/src/main/res/layout/activity_main.xml`
- Create: `ipc-demo/src/main/res/layout/fragment_ipc.xml`

- [ ] **Step 1: Create activity_main.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true">

    <FrameLayout
        android:id="@+id/fragment_container"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginBottom="72dp" />

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottom_nav"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom"
        android:background="@color/surface"
        app:itemIconTint="@color/primary"
        app:itemTextColor="@color/nav_item_colors"
        app:labelVisibilityMode="labeled"
        app:menu="@menu/bottom_nav_menu" />

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

- [ ] **Step 2: Create fragment_ipc.xml (shared layout for all 3 fragments)**

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <!-- 说明区 -->
        <TextView
            android:id="@+id/tvDescription"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textColor="@color/text_secondary"
            android:textSize="13sp"
            android:lineSpacingExtra="4dp"
            tools:text="通过 SharedMemory 实现零拷贝数据传输" />

        <!-- 输入区 -->
        <com.google.android.material.textfield.TextInputLayout
            android:id="@+id/tilInput"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            android:hint="@string/hint_input_text">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/etInput"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:minHeight="80dp"
                android:gravity="top|start"
                android:inputType="textMultiLine"
                android:maxLines="5" />

        </com.google.android.material.textfield.TextInputLayout>

        <!-- 按钮区 -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="12dp"
            android:orientation="horizontal">

            <com.google.android.material.button.MaterialButton
                android:id="@+id/btnSend"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="@string/btn_send" />

            <com.google.android.material.button.MaterialButton
                android:id="@+id/btnClear"
                style="@style/Widget.Material3.Button.OutlinedButton"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginStart="8dp"
                android:text="@string/btn_clear" />

        </LinearLayout>

        <!-- 分隔线 -->
        <View
            android:layout_width="match_parent"
            android:layout_height="1dp"
            android:layout_marginTop="16dp"
            android:layout_marginBottom="16dp"
            android:background="@color/divider" />

        <!-- 结果区 -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/label_result"
            android:textColor="@color/text_primary"
            android:textSize="14sp"
            android:textStyle="bold" />

        <TextView
            android:id="@+id/tvResult"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:background="@color/panel"
            android:minHeight="80dp"
            android:padding="12dp"
            android:textColor="@color/text_primary"
            android:textIsSelectable="true"
            android:textSize="14sp" />

        <!-- 状态区 -->
        <TextView
            android:id="@+id/tvStatus"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="12dp"
            android:textColor="@color/text_secondary"
            android:textSize="12sp"
            tools:text="状态：已连接" />

    </LinearLayout>

</ScrollView>
```

- [ ] **Step 3: Commit**

```bash
git add ipc-demo/src/main/res/layout/
git commit -m "feat(ipc-demo): add activity_main and fragment layouts"
```

---

### Task 4: IpcRemoteService — multi-process Service

**Files:**
- Create: `ipc-demo/src/main/java/com/peter/ipc/demo/service/IpcRemoteService.kt`

This is the core Service running in `:remote` process. It handles three IPC methods via Messenger:

- **SharedMemory**: Receives a Message with `what=MSG_SHARED_MEMORY`, reads data from the SharedMemory fd, processes it, writes back
- **LocalSocket**: Starts a `LocalServerSocket` on Service create, accepts connections and processes data
- **File**: Receives a Message with `what=MSG_FILE_SHARE`, reads from shared file, processes, writes back

- [ ] **Step 1: Create IpcRemoteService.kt**

```kotlin
package com.peter.ipc.demo.service

import android.app.Service
import android.content.Intent
import android.net.LocalServerSocket
import android.net.LocalSocket
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.ParcelFileDescriptor
import android.os.RemoteException
import android.os.SharedMemory
import android.system.OsConstants
import android.util.Log
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.ByteBuffer
import kotlin.concurrent.thread

class IpcRemoteService : Service() {

    companion object {
        private const val TAG = "IpcRemoteService"

        // SharedMemory 消息类型
        const val MSG_SHARED_MEMORY = 1

        // FileShare 消息类型
        const val MSG_FILE_SHARE = 2

        // LocalSocket 地址
        const val LOCAL_SOCKET_NAME = "ipc.demo.localsocket"

        // Bundle keys
        const val KEY_SHARED_MEMORY = "shared_memory"
        const val KEY_FILE_PATH = "file_path"
        const val KEY_REPLY_MESSENGER = "reply_messenger"
        const val KEY_RESULT = "result"
        const val KEY_ELAPSED = "elapsed"
    }

    private var localServerSocket: LocalServerSocket? = null
    private var serverThreadRunning = false

    private val serviceMessenger = Messenger(object : android.os.Handler(android.os.Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_SHARED_MEMORY -> handleSharedMemory(msg)
                MSG_FILE_SHARE -> handleFileShare(msg)
                else -> super.handleMessage(msg)
            }
        }
    })

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "IpcRemoteService onCreate, pid=${android.os.Process.myPid()}")
        startLocalSocketServer()
    }

    override fun onBind(intent: Intent?): IBinder {
        return serviceMessenger.binder
    }

    override fun onDestroy() {
        super.onDestroy()
        serverThreadRunning = false
        localServerSocket?.close()
    }

    // ==================== SharedMemory ====================

    private fun handleSharedMemory(msg: Message) {
        val startTime = System.currentTimeMillis()
        val replyMessenger: Messenger? = msg.replyTo
        val bundle = msg.obj as? Bundle ?: return

        val sharedMemory = bundle.getParcelable<SharedMemory>(KEY_SHARED_MEMORY) ?: return

        try {
            // 读取主进程写入的数据
            val buffer = sharedMemory.mapReadWrite()
            val inputData = readStringFromBuffer(buffer)

            // 处理数据：追加远程进程信息
            val remotePid = android.os.Process.myPid()
            val result = "$inputData\n---\n[Remote Process pid=$remotePid] 收到数据长度: ${inputData.length} 字符"

            // 写回结果
            buffer.clear()
            writeStringToBuffer(buffer, result)

            val elapsed = System.currentTimeMillis() - startTime
            replyMessenger?.send(Message.obtain().apply {
                what = MSG_SHARED_MEMORY
                data = Bundle().apply {
                    putString(KEY_RESULT, result)
                    putLong(KEY_ELAPSED, elapsed)
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "handleSharedMemory error", e)
            replyMessenger?.send(Message.obtain().apply {
                what = MSG_SHARED_MEMORY
                data = Bundle().apply {
                    putString(KEY_RESULT, "错误: ${e.message}")
                }
            })
        }
    }

    private fun readStringFromBuffer(buffer: ByteBuffer): String {
        val length = buffer.int
        if (length <= 0) return ""
        val bytes = ByteArray(length)
        buffer.get(bytes)
        return String(bytes, Charsets.UTF_8)
    }

    private fun writeStringToBuffer(buffer: ByteBuffer, text: String) {
        val bytes = text.toByteArray(Charsets.UTF_8)
        buffer.putInt(bytes.size)
        buffer.put(bytes)
    }

    // ==================== LocalSocket ====================

    private fun startLocalSocketServer() {
        serverThreadRunning = true
        thread(name = "LocalSocketServer") {
            try {
                localServerSocket = LocalServerSocket(LOCAL_SOCKET_NAME)
                Log.d(TAG, "LocalServerSocket started: $LOCAL_SOCKET_NAME")

                while (serverThreadRunning) {
                    val client = localServerSocket?.accept() ?: break
                    Log.d(TAG, "LocalSocket client connected")
                    handleLocalSocketClient(client)
                }
            } catch (e: Exception) {
                if (serverThreadRunning) {
                    Log.e(TAG, "LocalServerSocket error", e)
                }
            }
        }
    }

    private fun handleLocalSocketClient(client: LocalSocket) {
        try {
            val reader = BufferedReader(InputStreamReader(client.inputStream))
            val writer = BufferedWriter(OutputStreamWriter(client.outputStream))

            // 读取客户端数据（带长度前缀）
            val length = reader.readLine()?.toIntOrNull() ?: return
            val chars = CharArray(length)
            reader.read(chars, 0, length)
            val inputData = String(chars)

            // 处理数据
            val remotePid = android.os.Process.myPid()
            val result = "$inputData\n---\n[Remote Process pid=$remotePid] LocalSocket 收到数据长度: ${inputData.length} 字符"

            // 写回结果（带长度前缀）
            val resultStr = result
            writer.write("${resultStr.toByteArray(Charsets.UTF_8).size}\n")
            writer.write(resultStr)
            writer.newLine()
            writer.flush()
        } catch (e: Exception) {
            Log.e(TAG, "handleLocalSocketClient error", e)
        } finally {
            try { client.close() } catch (_: Exception) {}
        }
    }

    // ==================== FileShare ====================

    private fun handleFileShare(msg: Message) {
        val startTime = System.currentTimeMillis()
        val replyMessenger: Messenger? = msg.replyTo
        val bundle = msg.obj as? Bundle ?: return
        val filePath = bundle.getString(KEY_FILE_PATH) ?: return

        try {
            val file = File(filePath)

            // 读取文件
            val inputData = file.readText()

            // 处理数据
            val remotePid = android.os.Process.myPid()
            val result = "$inputData\n---\n[Remote Process pid=$remotePid] FileShare 收到数据长度: ${inputData.length} 字符"

            // 写回文件
            file.writeText(result)

            val elapsed = System.currentTimeMillis() - startTime
            replyMessenger?.send(Message.obtain().apply {
                what = MSG_FILE_SHARE
                data = Bundle().apply {
                    putString(KEY_RESULT, result)
                    putLong(KEY_ELAPSED, elapsed)
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "handleFileShare error", e)
            replyMessenger?.send(Message.obtain().apply {
                what = MSG_FILE_SHARE
                data = Bundle().apply {
                    putString(KEY_RESULT, "错误: ${e.message}")
                }
            })
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add ipc-demo/src/main/java/com/peter/ipc/demo/service/IpcRemoteService.kt
git commit -m "feat(ipc-demo): add IpcRemoteService with SharedMemory, LocalSocket, FileShare handlers"
```

---

### Task 5: SharedMemoryFragment

**Files:**
- Create: `ipc-demo/src/main/java/com/peter/ipc/demo/sharedmemory/SharedMemoryFragment.kt`

- [ ] **Step 1: Create SharedMemoryFragment.kt**

```kotlin
package com.peter.ipc.demo.sharedmemory

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.ParcelFileDescriptor
import android.os.RemoteException
import android.os.SharedMemory
import android.system.ErrnoException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android:view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.ipc.demo.R
import com.peter.ipc.demo.databinding.FragmentIpcBinding
import com.peter.ipc.demo.service.IpcRemoteService
import java.nio.ByteBuffer

class SharedMemoryFragment : Fragment() {

    private var _binding: FragmentIpcBinding? = null
    private val binding get() = _binding!!

    private var serviceMessenger: Messenger? = null
    private var isBound = false

    private val replyMessenger = Messenger(object : android.os.Handler(android.os.Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == IpcRemoteService.MSG_SHARED_MEMORY) {
                val result = msg.data.getString(IpcRemoteService.KEY_RESULT) ?: ""
                val elapsed = msg.data.getLong(IpcRemoteService.KEY_ELAPSED, 0)
                binding.tvResult.text = result
                binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_success, elapsed))
            }
        }
    })

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceMessenger = Messenger(service)
            isBound = true
            binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_connected))
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceMessenger = null
            isBound = false
            binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_disconnected))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentIpcBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvDescription.text = getString(R.string.desc_shared_memory)
        binding.btnSend.setOnClickListener { sendViaSharedMemory() }
        binding.btnClear.setOnClickListener {
            binding.etInput.text?.clear()
            binding.tvResult.text = ""
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(requireContext(), IpcRemoteService::class.java)
        requireContext().bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            requireContext().unbindService(connection)
            isBound = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sendViaSharedMemory() {
        val messenger = serviceMessenger ?: run {
            binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_disconnected))
            return
        }

        val inputText = binding.etInput.text?.toString()?.trim()
        if (inputText.isNullOrEmpty()) {
            binding.etInput.error = "请输入文本"
            return
        }

        val startTime = System.currentTimeMillis()
        binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_sending))

        try {
            val bytes = inputText.toByteArray(Charsets.UTF_8)
            // 4 bytes for length prefix + data bytes
            val bufferSize = 4 + bytes.size + 4096 // extra space for response

            val sharedMemory = SharedMemory.create("ipc_shm", bufferSize)
            val buffer = sharedMemory.mapReadWrite()

            // Write data: length + bytes
            buffer.putInt(bytes.size)
            buffer.put(bytes)

            // Unmap the buffer from this process (the other process will map it)
            SharedMemory.unmap(buffer)

            val msg = Message.obtain().apply {
                what = IpcRemoteService.MSG_SHARED_MEMORY
                replyTo = replyMessenger
                obj = Bundle().apply {
                    putParcelable(IpcRemoteService.KEY_SHARED_MEMORY, sharedMemory)
                }
            }

            messenger.send(msg)
            sharedMemory.close()
        } catch (e: ErrnoException) {
            Log.e("SharedMemoryFrag", "sendViaSharedMemory error", e)
            binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_error, e.message))
        } catch (e: RemoteException) {
            Log.e("SharedMemoryFrag", "sendViaSharedMemory error", e)
            binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_error, e.message))
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add ipc-demo/src/main/java/com/peter/ipc/demo/sharedmemory/SharedMemoryFragment.kt
git commit -m "feat(ipc-demo): add SharedMemoryFragment for shared memory IPC demo"
```

---

### Task 6: LocalSocketFragment

**Files:**
- Create: `ipc-demo/src/main/java/com/peter/ipc/demo/localsocket/LocalSocketFragment.kt`

- [ ] **Step 1: Create LocalSocketFragment.kt**

```kotlin
package com.peter.ipc.demo.localsocket

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android:view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.ipc.demo.R
import com.peter.ipc.demo.databinding.FragmentIpcBinding
import com.peter.ipc.demo.service.IpcRemoteService
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.LocalSocket
import java.net.LocalSocketAddress
import kotlin.concurrent.thread

class LocalSocketFragment : Fragment() {

    private var _binding: FragmentIpcBinding? = null
    private val binding get() = _binding!!

    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: android.content.ComponentName?, service: IBinder?) {
            isBound = true
            binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_connected))
        }

        override fun onServiceDisconnected(name: android.content.ComponentName?) {
            isBound = false
            binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_disconnected))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentIpcBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvDescription.text = getString(R.string.desc_local_socket)
        binding.btnSend.setOnClickListener { sendViaLocalSocket() }
        binding.btnClear.setOnClickListener {
            binding.etInput.text?.clear()
            binding.tvResult.text = ""
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(requireContext(), IpcRemoteService::class.java)
        requireContext().bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            requireContext().unbindService(connection)
            isBound = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sendViaLocalSocket() {
        val inputText = binding.etInput.text?.toString()?.trim()
        if (inputText.isNullOrEmpty()) {
            binding.etInput.error = "请输入文本"
            return
        }

        binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_sending))
        binding.btnSend.isEnabled = false

        val startTime = System.currentTimeMillis()

        thread {
            var socket: LocalSocket? = null
            try {
                socket = LocalSocket()
                val address = LocalSocketAddress(IpcRemoteService.LOCAL_SOCKET_NAME, LocalSocketAddress.Namespace.ABSTRACT)
                socket.connect(address)

                val writer = OutputStreamWriter(socket.outputStream)
                val reader = BufferedReader(InputStreamReader(socket.inputStream))

                // 发送数据（带长度前缀）
                val bytes = inputText.toByteArray(Charsets.UTF_8)
                writer.write("${bytes.size}\n")
                writer.write(inputText)
                writer.newLine()
                writer.flush()

                // 读取响应（带长度前缀）
                val responseLength = reader.readLine()?.toIntOrNull() ?: 0
                if (responseLength > 0) {
                    val chars = CharArray(responseLength)
                    reader.read(chars, 0, responseLength)
                    val result = String(chars)
                    val elapsed = System.currentTimeMillis() - startTime

                    activity?.runOnUiThread {
                        binding.tvResult.text = result
                        binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_success, elapsed))
                        binding.btnSend.isEnabled = true
                    }
                }
            } catch (e: Exception) {
                Log.e("LocalSocketFrag", "sendViaLocalSocket error", e)
                val elapsed = System.currentTimeMillis() - startTime
                activity?.runOnUiThread {
                    binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_error, e.message))
                    binding.btnSend.isEnabled = true
                }
            } finally {
                try { socket?.close() } catch (_: Exception) {}
            }
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add ipc-demo/src/main/java/com/peter/ipc/demo/localsocket/LocalSocketFragment.kt
git commit -m "feat(ipc-demo): add LocalSocketFragment for local socket IPC demo"
```

---

### Task 7: FileShareFragment

**Files:**
- Create: `ipc-demo/src/main/java/com/peter/ipc/demo/fileshare/FileShareFragment.kt`

- [ ] **Step 1: Create FileShareFragment.kt**

```kotlin
package com.peter.ipc.demo.fileshare

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.FileObserver
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.ipc.demo.R
import com.peter.ipc.demo.databinding.FragmentIpcBinding
import com.peter.ipc.demo.service.IpcRemoteService
import java.io.File

class FileShareFragment : Fragment() {

    private var _binding: FragmentIpcBinding? = null
    private val binding get() = _binding!!

    private var serviceMessenger: Messenger? = null
    private var isBound = false
    private var fileObserver: FileObserver? = null
    private lateinit var sharedFile: File

    private val replyMessenger = Messenger(object : android.os.Handler(android.os.Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == IpcRemoteService.MSG_FILE_SHARE) {
                val result = msg.data.getString(IpcRemoteService.KEY_RESULT) ?: ""
                val elapsed = msg.data.getLong(IpcRemoteService.KEY_ELAPSED, 0)
                binding.tvResult.text = result
                binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_success, elapsed))
            }
        }
    })

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceMessenger = Messenger(service)
            isBound = true
            binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_connected))
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceMessenger = null
            isBound = false
            binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_disconnected))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentIpcBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvDescription.text = getString(R.string.desc_file_share)
        binding.btnSend.setOnClickListener { sendViaFile() }
        binding.btnClear.setOnClickListener {
            binding.etInput.text?.clear()
            binding.tvResult.text = ""
        }

        // 初始化共享文件路径
        val sharedDir = File(requireContext().getExternalFilesDir(null), "shared")
        sharedDir.mkdirs()
        sharedFile = File(sharedDir, "ipc_data.txt")
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(requireContext(), IpcRemoteService::class.java)
        requireContext().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        startFileObserver()
    }

    override fun onStop() {
        super.onStop()
        stopFileObserver()
        if (isBound) {
            requireContext().unbindService(connection)
            isBound = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startFileObserver() {
        val parentDir = sharedFile.parentFile ?: return
        fileObserver = object : FileObserver(parentDir, MODIFY or CREATE) {
            override fun onEvent(event: Int, path: String?) {
                if (path == sharedFile.name) {
                    try {
                        val content = sharedFile.readText()
                        activity?.runOnUiThread {
                            // 仅在文件内容与当前结果不同时更新
                            if (binding.tvResult.text?.toString() != content) {
                                binding.tvResult.text = content
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("FileShareFrag", "readFile error", e)
                    }
                }
            }
        }
        fileObserver?.startWatching()
    }

    private fun stopFileObserver() {
        fileObserver?.stopWatching()
        fileObserver = null
    }

    private fun sendViaFile() {
        val messenger = serviceMessenger ?: run {
            binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_disconnected))
            return
        }

        val inputText = binding.etInput.text?.toString()?.trim()
        if (inputText.isNullOrEmpty()) {
            binding.etInput.error = "请输入文本"
            return
        }

        binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_sending))

        try {
            // 写入文件
            sharedFile.writeText(inputText)

            // 通过 Messenger 通知远程进程读取文件
            val msg = Message.obtain().apply {
                what = IpcRemoteService.MSG_FILE_SHARE
                replyTo = replyMessenger
                obj = Bundle().apply {
                    putString(IpcRemoteService.KEY_FILE_PATH, sharedFile.absolutePath)
                }
            }
            messenger.send(msg)
        } catch (e: RemoteException) {
            Log.e("FileShareFrag", "sendViaFile error", e)
            binding.tvStatus.text = getString(R.string.label_status, getString(R.string.status_error, e.message))
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add ipc-demo/src/main/java/com/peter/ipc/demo/fileshare/FileShareFragment.kt
git commit -m "feat(ipc-demo): add FileShareFragment with FileObserver for file-based IPC demo"
```

---

### Task 8: MainActivity — BottomNav + Fragment switching

**Files:**
- Create: `ipc-demo/src/main/java/com/peter/ipc/demo/MainActivity.kt`

- [ ] **Step 1: Create MainActivity.kt**

```kotlin
package com.peter.ipc.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.peter.ipc.demo.databinding.ActivityMainBinding
import com.peter.ipc.demo.fileshare.FileShareFragment
import com.peter.ipc.demo.localsocket.LocalSocketFragment
import com.peter.ipc.demo.sharedmemory.SharedMemoryFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        if (savedInstanceState == null) {
            binding.bottomNav.selectedItemId = R.id.nav_shared_memory
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_shared_memory -> {
                    switchFragment(SharedMemoryFragment::class.java)
                    true
                }
                R.id.nav_local_socket -> {
                    switchFragment(LocalSocketFragment::class.java)
                    true
                }
                R.id.nav_file_share -> {
                    switchFragment(FileShareFragment::class.java)
                    true
                }
                else -> false
            }
        }
    }

    private fun switchFragment(fragmentClass: Class<out Fragment>) {
        val transaction = supportFragmentManager.beginTransaction()
        currentFragment?.let(transaction::hide)

        val tag = fragmentClass.simpleName
        var fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment == null) {
            fragment = when (fragmentClass) {
                SharedMemoryFragment::class.java -> SharedMemoryFragment()
                LocalSocketFragment::class.java -> LocalSocketFragment()
                FileShareFragment::class.java -> FileShareFragment()
                else -> error("Unknown fragment: $fragmentClass")
            }
            transaction.add(R.id.fragment_container, fragment, tag)
        } else {
            transaction.show(fragment)
        }

        transaction.commitAllowingStateLoss()
        currentFragment = fragment
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add ipc-demo/src/main/java/com/peter/ipc/demo/MainActivity.kt
git commit -m "feat(ipc-demo): add MainActivity with BottomNav and fragment switching"
```

---

### Task 9: Build verification and fix issues

**Files:**
- May need to fix import issues or build errors

- [ ] **Step 1: Sync and build the module**

```bash
./gradlew :ipc-demo:assembleDebug
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 2: Fix any build errors** (common issues: import typos, missing `android:` namespace in layout XML, API level guards)

- [ ] **Step 3: Commit any fixes**

```bash
git add -A ipc-demo/
git commit -m "fix(ipc-demo): resolve build issues"
```

---

## Self-Review

**Spec coverage:**
- Module structure: Task 1
- Resource files: Task 2
- Layout files: Task 3
- IpcRemoteService with 3 IPC methods: Task 4
- SharedMemoryFragment: Task 5
- LocalSocketFragment: Task 6
- FileShareFragment: Task 7
- MainActivity: Task 8
- Build verification: Task 9

**Placeholder scan:** No TBDs, TODOs, or vague instructions found.

**Type consistency:** All Fragment class names, Service constants, layout IDs, and Bundle keys are consistent across tasks.
