# IPC SharedMemory / LocalSocket / FileShare Demo Design

## Overview

Create a single `ipc-demo` module demonstrating three Android cross-process data sharing mechanisms: SharedMemory, LocalSocket, and File-based sharing. The module uses a single app with a multi-process Service to demonstrate IPC patterns.

## Architecture

### Module Structure

```
ipc-demo/
└── src/main/
    ├── java/com/peter/ipc/demo/
    │   ├── MainActivity.kt              # BottomNav + ViewPager2
    │   ├── DemoAdapter.kt               # FragmentStateAdapter
    │   ├── sharedmemory/
    │   │   └── SharedMemoryFragment.kt   # SharedMemory demo UI
    │   ├── localsocket/
    │   │   └── LocalSocketFragment.kt    # LocalSocket demo UI
    │   ├── fileshare/
    │   │   └── FileShareFragment.kt      # File sharing demo UI
    │   └── service/
    │       └── IpcRemoteService.kt       # Runs in :remote process
    ├── res/
    │   ├── layout/
    │   │   ├── activity_main.xml
    │   │   └── fragment_ipc_base.xml     # Shared layout template
    │   ├── menu/
    │   │   └── bottom_nav_menu.xml
    │   └── values/
    │       ├── strings.xml
    │       └── colors.xml
    └── AndroidManifest.xml
```

### Process Layout

- **Main process** (`com.peter.ipc.demo`): UI layer with MainActivity + 3 Fragments
- **Remote process** (`com.peter.ipc.demo:remote`): `IpcRemoteService` handles all three IPC methods

### Communication Channels

- **Messenger**: Control channel for signals and notifications across processes
- **SharedMemory / LocalSocket / File**: Data channels for actual data transfer

## IPC Implementation Details

### 1. SharedMemory

**Flow**:
1. Main process creates `SharedMemory` with text data written to ByteBuffer
2. Passes SharedMemory's FileDescriptor to `:remote` process via Messenger
3. Remote process reads data from SharedMemory, appends timestamp, writes back
4. Main process reads returned data and displays it

**Key APIs**: `android.os.SharedMemory`, `ByteBuffer`, `Messenger`

### 2. LocalSocket

**Flow**:
1. Remote process starts `LocalServerSocket("ipc.demo.localsocket")` on Service creation
2. Main process connects via `LocalSocket` when user sends data
3. Uses `DataOutputStream` / `DataInputStream` for length-prefixed text transfer
4. Remote process processes and returns response

**Key APIs**: `LocalServerSocket`, `LocalSocket`, `LocalSocketAddress`, `DataInputStream`/`DataOutputStream`

### 3. File Sharing

**Flow**:
1. Main process writes text to shared file (`getExternalFilesDir("shared")/ipc_data.txt`)
2. Notifies remote process via Messenger that file is ready
3. Remote process reads file, appends processing info, writes back
4. Main process uses `FileObserver` to detect file changes and display result

**Key APIs**: `File`, `FileObserver`, `Messenger`, `BufferedReader`/`BufferedWriter`

## UI Design

### Main Activity

- `BottomNavigationView` + `ViewPager2` with 3 tabs
- Tab labels: SharedMemory / LocalSocket / FileShare

### Fragment Layout (shared template)

- Top: IPC method title + brief principle description (1-2 lines)
- Middle:
  - EditText input field for text to send
  - Send button
  - Divider
  - ScrollView with TextView for received data
- Bottom: Status info (connection state, elapsed time)

### Styling

- Material Design consistent with existing demo modules (anr-demo, context-demo patterns)
- Color palette from project's Material theme

## Configuration

- **minSdk**: 33 (SharedMemory API 27+, well within range)
- **Namespace**: `com.peter.ipc.demo`
- **ApplicationId**: `com.peter.ipc.demo`
- **Service process**: `android:process=":remote"`

## Files to Create

1. `ipc-demo/build.gradle.kts` - Module build config
2. `ipc-demo/src/main/AndroidManifest.xml` - Manifest with multi-process Service
3. `ipc-demo/src/main/java/com/peter/ipc/demo/MainActivity.kt`
4. `ipc-demo/src/main/java/com/peter/ipc/demo/DemoAdapter.kt`
5. `ipc-demo/src/main/java/com/peter/ipc/demo/service/IpcRemoteService.kt`
6. `ipc-demo/src/main/java/com/peter/ipc/demo/sharedmemory/SharedMemoryFragment.kt`
7. `ipc-demo/src/main/java/com/peter/ipc/demo/localsocket/LocalSocketFragment.kt`
8. `ipc-demo/src/main/java/com/peter/ipc/demo/fileshare/FileShareFragment.kt`
9. `ipc-demo/src/main/res/layout/activity_main.xml`
10. `ipc-demo/src/main/res/layout/fragment_ipc_base.xml`
11. `ipc-demo/src/main/res/menu/bottom_nav_menu.xml`
12. `ipc-demo/src/main/res/values/strings.xml`
13. `ipc-demo/src/main/res/values/colors.xml`
14. Update `settings.gradle.kts` to include `:ipc-demo`
