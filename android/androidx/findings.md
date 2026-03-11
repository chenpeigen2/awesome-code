# Findings & Decisions

## Requirements

### 核心功能
- **基础播放**: 播放/暂停、上一首/下一首、进度条、音量控制
- **歌词显示**: 同步歌词滚动、LRC 格式支持
- **均衡器**: 10段音频均衡器、预设音效
- **在线音乐**: 网易云音乐 API 集成

### 音乐库管理
- **本地扫描**: 扫描本地文件夹、自动识别元数据
- **播放列表**: 创建/编辑播放列表、智能播放列表
- **标签编辑**: 编辑音乐文件元数据（ID3 标签）
- **封面管理**: 自动获取封面、自定义封面

### 目标平台
- Linux (x64)
- macOS (x64, ARM64/Apple Silicon)
- Windows (x64)

### UI 风格
- Material 3 设计语言
- 深色/浅色主题支持

---

## Architecture Design

### 模块结构

```
harmony-player/
├── build.gradle.kts                 # 根项目配置
├── settings.gradle.kts
│
├── composeApp/                      # Compose Desktop 应用模块
│   ├── build.gradle.kts
│   └── src/
│       └── desktopMain/kotlin/
│           └── com/harmony/player/
│               ├── Main.kt          # 应用入口
│               ├── App.kt           # Compose 应用
│               ├── di/              # Koin 模块
│               └── ui/              # UI 组件
│                   ├── theme/       # Material 3 主题
│                   ├── navigation/  # 导航
│                   ├── component/   # 通用组件
│                   └── screen/      # 页面
│
├── core/                            # 核心模块
│   ├── common/                      # 公共工具
│   │   ├── build.gradle.kts
│   │   └── src/kotlin/
│   │       └── com/harmony/player/core/common/
│   │           ├── Result.kt        # Result 封装
│   │           ├── Extensions.kt    # 扩展函数
│   │           └── Logger.kt        # 日志工具
│   │
│   ├── database/                    # 数据库模块
│   │   ├── build.gradle.kts
│   │   └── src/
│   │       └── com/harmony/player/core/database/
│   │           ├── DatabaseFactory.kt
│   │           ├── dao/             # 数据访问对象
│   │           └── entity/          # 数据库实体
│   │
│   ├── model/                       # 领域模型
│   │   ├── build.gradle.kts
│   │   └── src/kotlin/
│   │       └── com/harmony/player/core/model/
│   │           ├── Song.kt
│   │           ├── Album.kt
│   │           ├── Artist.kt
│   │           ├── Playlist.kt
│   │           └── Lyrics.kt
│   │
│   ├── player/                      # 播放器核心
│   │   ├── build.gradle.kts
│   │   └── src/
│   │       ├── commonMain/kotlin/
│   │       │   └── com/harmony/player/core/player/
│   │       │       ├── Player.kt           # 播放器接口
│   │       │       ├── PlayerState.kt      # 播放状态
│   │       │       ├── Equalizer.kt        # 均衡器接口
│   │       │       └── AudioEffect.kt      # 音效
│   │       └── desktopMain/kotlin/
│   │           └── com/harmony/player/core/player/
│   │               └── MpvPlayer.kt        # mpv 实现
│   │
│   └── network/                     # 网络模块
│       ├── build.gradle.kts
│       └── src/kotlin/
│           └── com/harmony/player/core/network/
│               ├── HttpClientFactory.kt
│               └── api/
│                   └── NeteaseMusicApi.kt  # 网易云 API
│
├── feature/                         # 功能模块
│   ├── library/                     # 音乐库
│   │   ├── build.gradle.kts
│   │   └── src/kotlin/
│   │       └── com/harmony/player/feature/library/
│   │           ├── LibraryViewModel.kt
│   │           ├── LibraryScreen.kt
│   │           ├── scanner/         # 文件扫描
│   │           └── metadata/        # 元数据解析
│   │
│   ├── player/                      # 播放器 UI
│   │   ├── build.gradle.kts
│   │   └── src/kotlin/
│   │       └── com/harmony/player/feature/player/
│   │           ├── PlayerViewModel.kt
│   │           ├── PlayerScreen.kt
│   │           ├── MiniPlayer.kt
│   │           ├── QueueScreen.kt
│   │           └── components/
│   │
│   ├── playlist/                    # 播放列表
│   │   ├── build.gradle.kts
│   │   └── src/kotlin/
│   │       └── com/harmony/player/feature/playlist/
│   │           ├── PlaylistViewModel.kt
│   │           └── PlaylistScreen.kt
│   │
│   ├── lyrics/                      # 歌词
│   │   ├── build.gradle.kts
│   │   └── src/kotlin/
│   │       └── com/harmony/player/feature/lyrics/
│   │           ├── LyricsViewModel.kt
│   │           ├── LyricsScreen.kt
│   │           └── parser/          # LRC 解析
│   │
│   ├── equalizer/                   # 均衡器
│   │   ├── build.gradle.kts
│   │   └── src/kotlin/
│   │       └── com/harmony/player/feature/equalizer/
│   │           ├── EqualizerViewModel.kt
│   │           └── EqualizerScreen.kt
│   │
│   └── online/                      # 在线音乐
│       ├── build.gradle.kts
│       └── src/kotlin/
│           └── com/harmony/player/feature/online/
│               ├── OnlineViewModel.kt
│               └── OnlineScreen.kt
│
└── gradle/
    └── libs.versions.toml           # 版本目录
```

### 依赖关系图

```
                    ┌─────────────┐
                    │ composeApp  │
                    └──────┬──────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ▼                  ▼                  ▼
┌───────────────┐  ┌───────────────┐  ┌───────────────┐
│feature:library│  │feature:player │  │feature:online │
└───────┬───────┘  └───────┬───────┘  └───────┬───────┘
        │                  │                  │
        └──────────────────┼──────────────────┘
                           │
                    ┌──────┴──────┐
                    │ core:model  │
                    └──────┬──────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ▼                  ▼                  ▼
┌───────────────┐  ┌───────────────┐  ┌───────────────┐
│core:database  │  │ core:player   │  │ core:network  │
└───────┬───────┘  └───────────────┘  └───────────────┘
        │
        ▼
┌───────────────┐
│ core:common   │
└───────────────┘
```

### 数据流架构 (MVVM + Clean Architecture)

```
┌─────────────────────────────────────────────────────────────────┐
│                        Presentation Layer                        │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │   Screen    │───▶│  ViewModel  │───▶│   State     │         │
│  │  (Compose)  │◀───│             │◀───│  (UI State) │         │
│  └─────────────┘    └──────┬──────┘    └─────────────┘         │
└────────────────────────────┼────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                         Domain Layer                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │  UseCase    │───▶│  Repository │◀──▶│   Model     │         │
│  │             │    │  (Interface)│    │  (Entity)   │         │
│  └─────────────┘    └──────┬──────┘    └─────────────┘         │
└────────────────────────────┼────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                          Data Layer                              │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │ Repository  │───▶│ DataSource  │───▶│   Remote/   │         │
│  │    Impl     │    │   (Local)   │    │    Local    │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
└─────────────────────────────────────────────────────────────────┘
```

### 核心接口设计

#### Player 接口
```kotlin
interface Player {
    val state: StateFlow<PlayerState>
    val currentPosition: StateFlow<Long>
    val duration: StateFlow<Long>
    val volume: StateFlow<Float>

    suspend fun load(source: PlaySource)
    fun play()
    fun pause()
    fun stop()
    fun seekTo(position: Long)
    fun setVolume(volume: Float)
    fun setEqualizer(bands: List<Float>)
}

sealed interface PlaySource {
    data class Local(val path: String) : PlaySource
    data class Remote(val url: String, val headers: Map<String, String>) : PlaySource
}

data class PlayerState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentSong: Song? = null,
    val queue: List<Song> = emptyList(),
    val queueIndex: Int = -1,
    val shuffleMode: ShuffleMode = ShuffleMode.OFF,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val error: PlayerError? = null
)
```

#### Repository 接口
```kotlin
interface SongRepository {
    fun getAllSongs(): Flow<List<Song>>
    fun getSongById(id: Long): Flow<Song?>
    fun searchSongs(query: String): Flow<List<Song>>
    suspend fun insertSong(song: Song): Long
    suspend fun updateSong(song: Song)
    suspend fun deleteSong(id: Long)
    suspend fun scanLibrary(paths: List<String>): ScanResult
}

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getPlaylistWithSongs(id: Long): Flow<PlaylistWithSongs?>
    suspend fun createPlaylist(name: String): Long
    suspend fun addToPlaylist(playlistId: Long, songId: Long)
    suspend fun removeFromPlaylist(playlistId: Long, songId: Long)
    suspend fun deletePlaylist(id: Long)
}

interface OnlineRepository {
    suspend fun search(keyword: String): Result<SearchResult>
    suspend fun getSongDetail(id: Long): Result<OnlineSong>
    suspend fun getSongUrl(id: Long): Result<String>
    suspend fun getLyrics(id: Long): Result<Lyrics>
    suspend fun getPlayUrl(id: Long): Result<String>
}
```

---

## Research Findings

### Compose Multiplatform Desktop

**Gradle 配置要点**:
```kotlin
// build.gradle.kts
plugins {
    kotlin("multiplatform") version "2.0.21"
    id("org.jetbrains.compose") version "1.7.0"
    id("com.android.application") // 可选
}

kotlin {
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
    }

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "HarmonyPlayer"
            packageVersion = "1.0.0"
        }
    }
}
```

**打包命令**:
```bash
./gradlew packageDmg     # macOS
./gradlew packageMsi     # Windows
./gradlew packageDeb     # Linux Debian
./gradlew packageRpm     # Linux RedHat
```

### 音频后端方案对比

| 方案 | 优点 | 缺点 | 推荐度 |
|------|------|------|--------|
| **mpv/libmpv** | 高性能、格式支持广、跨平台 | 需要 JNI 绑定 | ⭐⭐⭐⭐⭐ |
| JavaFX Media | JVM 原生支持 | 格式有限、性能一般 | ⭐⭐⭐ |
| VLCJ | 功能强大 | 依赖重、启动慢 | ⭐⭐⭐ |
| GStreamer | 灵活 | 配置复杂 | ⭐⭐ |

### mpv JNA 集成

```kotlin
interface LibMpv : Library {
    companion object {
        val INSTANCE: LibMpv = Native.load("mpv", LibMpv::class.java)
    }

    fun mpv_create(): Pointer?
    fun mpv_initialize(ctx: Pointer): Int
    fun mpv_terminate_destroy(ctx: Pointer)
    fun mpv_set_option_string(ctx: Pointer, name: String, data: String): Int
    fun mpv_command(ctx: Pointer, args: Array<String?>): Int
    fun mpv_get_property_string(ctx: Pointer, name: String): String?
    fun mpv_set_property_string(ctx: Pointer, name: String, data: String): Int
}

class MpvPlayer : Player {
    private val ctx: Pointer? = LibMpv.INSTANCE.mpv_create()

    init {
        LibMpv.INSTANCE.mpv_initialize(ctx)
        LibMpv.INSTANCE.mpv_set_option_string(ctx, "vo", "null") // 音频-only
    }

    override fun play() {
        LibMpv.INSTANCE.mpv_command(ctx, arrayOf("set", "pause", "no", null))
    }

    override fun pause() {
        LibMpv.INSTANCE.mpv_command(ctx, arrayOf("set", "pause", "yes", null))
    }
}
```

### 网易云音乐 API

**主要接口** (非官方 API):
```kotlin
interface NeteaseMusicApi {
    // 搜索
    @GET("/search")
    suspend fun search(
        @Query("keywords") keywords: String,
        @Query("type") type: Int = 1, // 1:歌曲, 10:专辑, 100:歌手
        @Query("limit") limit: Int = 30
    ): SearchResult

    // 歌曲详情
    @GET("/song/detail")
    suspend fun getSongDetail(@Query("ids") ids: String): SongDetailResult

    // 歌曲URL
    @GET("/song/url")
    suspend fun getSongUrl(@Query("id") id: Long): SongUrlResult

    // 歌词
    @GET("/lyric")
    suspend fun getLyric(@Query("id") id: Long): LyricResult
}
```

**API 项目参考**: [NeteaseCloudMusicApi](https://github.com/Binaryify/NeteaseCloudMusicApi)

---

## Technical Decisions

| Decision | Rationale |
|----------|-----------|
| Compose Multiplatform 1.7+ | 最新稳定版，支持 Material 3 |
| Kotlin 2.0+ | 性能优化、新特性 |
| JVM Target 17 | 桌面应用标准，性能好 |
| JNA 调用 libmpv | 灵活控制，无额外依赖 |
| SQLDelight 2.0 | 类型安全，跨平台 |
| Koin 3.5+ | 轻量级，Compose 友好 |
| Coroutines + Flow | 响应式数据流 |
| Voyager 导航 | 简单易用的 Compose 导航 |
| 项目位置: androidx/harmony-player | 作为 AndroidX 仓库的新模块 |
| 网易云音乐 API | 曲库丰富，社区有成熟的非官方 API 封装 |

---

## Database Schema

### SQLDelight 表定义

```sql
-- songs.sq
CREATE TABLE songs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    path TEXT NOT NULL UNIQUE,
    title TEXT NOT NULL,
    artist TEXT,
    album TEXT,
    album_artist TEXT,
    duration INTEGER NOT NULL,
    track_number INTEGER,
    disc_number INTEGER,
    year INTEGER,
    genre TEXT,
    cover_path TEXT,
    lyrics_path TEXT,
    bitrate INTEGER,
    sample_rate INTEGER,
    channels INTEGER,
    file_size INTEGER,
    file_modified INTEGER,
    date_added INTEGER NOT NULL,
    last_played INTEGER,
    play_count INTEGER DEFAULT 0,
    favorite INTEGER DEFAULT 0
);

getById:
SELECT * FROM songs WHERE id = ?;

getAll:
SELECT * FROM songs ORDER BY title COLLATE NOCASE;

search:
SELECT * FROM songs
WHERE title LIKE ? OR artist LIKE ? OR album LIKE ?
ORDER BY title COLLATE NOCASE;

getByAlbum:
SELECT * FROM songs WHERE album = ? ORDER BY disc_number, track_number;

getRecent:
SELECT * FROM songs WHERE last_played > 0 ORDER BY last_played DESC LIMIT ?;

getFavorites:
SELECT * FROM songs WHERE favorite = 1;

-- playlists.sq
CREATE TABLE playlists (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    cover_path TEXT,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);

getAll:
SELECT * FROM playlists ORDER BY name;

-- playlist_songs.sq
CREATE TABLE playlist_songs (
    playlist_id INTEGER NOT NULL,
    song_id INTEGER NOT NULL,
    position INTEGER NOT NULL,
    date_added INTEGER NOT NULL,
    PRIMARY KEY(playlist_id, song_id),
    FOREIGN KEY(playlist_id) REFERENCES playlists(id) ON DELETE CASCADE,
    FOREIGN KEY(song_id) REFERENCES songs(id) ON DELETE CASCADE
);

getSongsByPlaylist:
SELECT s.* FROM songs s
INNER JOIN playlist_songs ps ON s.id = ps.song_id
WHERE ps.playlist_id = ?
ORDER BY ps.position;

-- albums.sq (视图)
CREATE VIEW albums AS
SELECT
    album as name,
    album_artist as artist,
    MIN(id) as cover_song_id,
    COUNT(*) as song_count,
    SUM(duration) as total_duration
FROM songs
WHERE album IS NOT NULL AND album != ''
GROUP BY album, album_artist;

-- artists.sq (视图)
CREATE VIEW artists AS
SELECT
    artist as name,
    COUNT(*) as song_count,
    COUNT(DISTINCT album) as album_count,
    SUM(duration) as total_duration
FROM songs
WHERE artist IS NOT NULL AND artist != ''
GROUP BY artist;
```

---

## Issues Encountered

| Issue | Resolution |
|-------|------------|
| (暂无) | - |

---

## Resources

### 官方文档
- [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform)
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
- [Koin](https://insert-koin.io/)

### 参考项目
- [MediaPlayer-KMP](https://github.com/KhubaibKhan4/MediaPlayer-KMP) - KMP 媒体播放器
- [mpv-android](https://github.com/mpv-android/mpv-android) - mpv Android 实现
- [RiMusic](https://github.com/fast4x/RiMusic) - Android 音乐播放器
- [NeteaseCloudMusicApi](https://github.com/Binaryify/NeteaseCloudMusicApi) - 网易云 API

### 第三方库
- [Voyager](https://github.com/adrielcafe/voyager) - 导航库
- [Coil-Compose](https://coil-kt.github.io/coil/) - 图片加载 (需检查 Desktop 支持)
- [Ktor Client](https://ktor.io/) - 网络请求
- [Kamel](https://github.com/Kamel-Media/Kamel) - 跨平台图片加载

---

*Update this file after every 2 view/browser/search operations*
