# AIDL Async & Callback Demo Design

## Overview

Enhance the existing `aidl_client` / `aidl_server` / `aidl-common` modules to demonstrate AIDL async patterns: `oneway` calls, callback interfaces, callback registration/deregistration, and cancellation. The demo uses a file download manager scenario where the server simulates downloads and clients interact through various AIDL patterns.

## AIDL Interfaces (aidl-common)

### IDownloadCallback.aidl

Callback interface passed from client to server for async notifications:

```aidl
interface IDownloadCallback {
    void onProgress(int downloadId, int progress);
    void onComplete(int downloadId, String filePath, long fileSize);
    void onFailure(int downloadId, int errorCode, String message);
}
```

### DownloadInfo (Parcelable)

```java
@Parcelize
data class DownloadInfo(
    val id: Int,
    val url: String,
    val fileName: String,
    val totalSize: Long,
    val downloadedSize: Long,
    val status: Int  // 0=PENDING, 1=RUNNING, 2=PAUSED, 3=COMPLETED, 4=FAILED, 5=CANCELLED
) : Parcelable
```

### IDownloadManager.aidl

Main service interface combining all patterns:

```aidl
interface IDownloadManager {
    // Sync call (baseline)
    int add(int a, int b);

    // Oneway async — fire-and-forget
    oneway void startDownload(String url, String fileName);

    // Async with per-request callback
    void downloadWithCallback(String url, String fileName, IDownloadCallback callback);

    // Callback registration — server maintains listener list
    void registerCallback(IDownloadCallback callback);
    void unregisterCallback(IDownloadCallback callback);

    // Cancellation
    boolean cancelDownload(int downloadId);

    // Query active downloads
    List<DownloadInfo> getActiveDownloads();
}
```

## Server Architecture (aidl_server)

### DownloadManagerService

Replaces `MyAidlService`. Implements `IDownloadManager.Stub`:

- **Callback list:** `CopyOnWriteArrayList<IDownloadCallback>` for registered listeners
- **Active downloads:** `ConcurrentHashMap<Int, DownloadTask>` keyed by download ID
- **ID generation:** `AtomicInteger` for unique download IDs
- **Thread pool:** `ExecutorService` for running download simulations

Method behavior:
- `startDownload(url, fileName)` — oneway. Assigns ID, launches download thread, returns immediately.
- `downloadWithCallback(url, fileName, callback)` — same as startDownload but tracks a per-request callback alongside registered callbacks.
- `registerCallback(callback)` / `unregisterCallback(callback)` — add/remove from `CopyOnWriteArrayList`. Server notifies all registered callbacks for every download event.
- `cancelDownload(id)` — interrupts download thread, sets status CANCELLED, notifies callbacks.
- `getActiveDownloads()` — returns snapshot of all active download info objects.
- `add(a, b)` — keeps original sync call for baseline comparison.

### DownloadTask

Simulates a file download on a background thread:
- Loops from 0 to 100 progress, sleeping 50-200ms per step
- On each step: notifies registered callbacks + per-request callback with `onProgress()`
- On completion: calls `onComplete()`, removes from active map
- On failure (simulated at random ~10% chance): calls `onFailure()`
- On cancel (thread interrupted): sets status CANCELLED, cleans up

### Server MainActivity

Simplified to show service status and start/stop controls.

## Client Architecture (aidl_client)

### Navigation

Bottom navigation with 4 tabs, each Fragment demonstrating a distinct AIDL pattern.

### BasicSyncFragment — "同步调用"

- Two number inputs, operation buttons, result display
- Calls `add()` synchronously on the main thread (or a coroutine)
- Purpose: baseline comparison — simple blocking call

### AsyncDownloadFragment — "异步下载"

- URL + filename inputs, "开始下载" button
- Calls `startDownload()` (oneway) — fire-and-forget, returns instantly
- Polls `getActiveDownloads()` every 500ms via coroutine to display progress
- Shows contrast: oneway is instant but requires polling for status updates
- Cancel button calls `cancelDownload(id)`

### CallbackDownloadFragment — "回调下载"

- URL + filename inputs, "下载(回调)" button
- Calls `downloadWithCallback()` passing `IDownloadCallback.Stub` implementation
- Progress bar updates directly from `onProgress()` callback
- Result shown from `onComplete()`, error from `onFailure()`
- Demonstrates: no polling, server pushes updates via callback
- Cancel button for in-progress downloads

### CallbackManagerFragment — "回调注册"

- "注册回调" / "注销回调" toggle buttons
- "模拟批量下载" button — triggers 3 concurrent `startDownload()` calls
- Scrollable log showing all callback events received (download ID, progress, status)
- Registered callbacks receive events from ALL downloads on the server
- Demonstrates server-side callback list, broadcast notification

## Key AIDL Patterns Demonstrated

| Pattern | Fragment | AIDL Feature |
|---------|----------|-------------|
| Synchronous blocking | BasicSyncFragment | Normal AIDL method |
| Fire-and-forget async | AsyncDownloadFragment | `oneway` keyword |
| Per-request callback | CallbackDownloadFragment | `IDownloadCallback` parameter |
| Callback registration | CallbackManagerFragment | `registerCallback`/`unregisterCallback` |
| Cancellation | All fragments | `cancelDownload` + thread interrupt |

## Module Changes Summary

**aidl-common:**
- Add `IDownloadCallback.aidl`
- Add `DownloadInfo.aidl` + `DownloadInfo.java` (Parcelable)
- Add `IDownloadManager.aidl`
- Remove `IMyAidlInterface.aidl`

**aidl_server:**
- Replace `MyAidlService.java` with `DownloadManagerService.kt` (Kotlin)
- Add `DownloadTask.kt` for download simulation
- Simplify `MainActivity.kt`

**aidl_client:**
- Refactor to bottom navigation + 4 Fragments
- Add `BasicSyncFragment`, `AsyncDownloadFragment`, `CallbackDownloadFragment`, `CallbackManagerFragment`
- Add navigation graph and menu resources
- Update `MainActivity.kt` as navigation host
