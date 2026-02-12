# AGENTS.md

本文件为此音乐播放器项目的 AI 代理指导文档，提供项目结构、开发命令和编码规范等信息。

## 项目概述

这是一个使用 **Electron + React + TypeScript** 构建的桌面音乐播放器应用。

### 核心技术栈
- **前端框架**: React 18 + TypeScript
- **桌面框架**: Electron 28
- **构建工具**: Vite 5
- **状态管理**: Zustand (带 persist 中间件)
- **UI 图标**: Lucide React
- **音频播放**: HTML5 Audio API (通过自定义 AudioManager 封装)
- **测试框架**: Vitest + Testing Library

### 主要功能
- 本地音乐文件播放 (支持 mp3, flac, wav, aac, ogg, ape 格式)
- 在线音乐搜索与下载
- 播放列表管理
- 桌面歌词窗口
- 收藏/喜欢功能
- 多种播放模式 (顺序、随机、单曲循环、列表循环)

## 项目结构

```
music-player/
├── electron/                # Electron 主进程代码
│   ├── main.ts             # 主进程入口，窗口管理、IPC 处理
│   ├── preload.ts          # 预加载脚本，暴露安全 API 给渲染进程
│   └── tsconfig.json       # Electron 专用 TypeScript 配置
├── src/
│   ├── main.tsx            # React 应用入口
│   ├── App.tsx             # 根组件
│   ├── components/         # React 组件
│   │   ├── PlayerBar.tsx   # 底部播放控制栏
│   │   ├── Sidebar.tsx     # 左侧导航栏
│   │   ├── MainContent.tsx # 主内容区域
│   │   ├── OnlineMusic.tsx # 在线音乐搜索
│   │   └── Lyrics.tsx      # 歌词显示组件
│   ├── stores/             # Zustand 状态管理
│   │   └── index.ts        # PlayerStore, LibraryStore, SettingsStore
│   ├── types/              # TypeScript 类型定义
│   │   └── index.ts        # Track, Playlist, ElectronAPI 等类型
│   ├── utils/              # 工具函数
│   │   ├── audio.ts        # AudioManager 音频管理类
│   │   ├── lyrics.ts       # 歌词解析
│   │   ├── mp3pm.ts        # 在线音乐 API
│   │   └── downloadManager.ts # 下载管理
│   └── test/               # 测试文件
│       ├── setup.ts        # 测试环境配置
│       └── *.test.ts(x)    # 单元测试和集成测试
├── index.html              # 主窗口 HTML
├── lyrics.html             # 歌词窗口 HTML
├── vite.config.ts          # Vite 配置
├── vitest.config.ts        # Vitest 测试配置
├── tsconfig.json           # TypeScript 配置
└── package.json            # 项目依赖和脚本
```

## 构建与运行命令

### 开发模式

```bash
# 启动 Web 开发服务器 (仅前端)
npm run dev

# 启动 Electron 开发模式 (完整应用)
npm run electron:dev

# 或使用平台特定启动脚本
npm run start:linux   # Linux
npm run start:mac     # macOS
npm run start:win     # Windows (PowerShell)
npm run start:win-cmd # Windows (CMD)
```

### 构建

```bash
# 构建前端
npm run build

# 构建 Electron 主进程代码
npm run build:electron

# 构建桌面应用安装包
npm run electron:build        # 当前平台
npm run electron:build:win    # Windows
npm run electron:build:mac    # macOS
npm run electron:build:linux  # Linux
```

### 测试

```bash
# 运行测试
npm run test

# 监听模式
npm run test:watch

# 生成覆盖率报告
npm run test:coverage
```

### 代码检查

```bash
npm run lint
```

## 编码规范

### TypeScript

- 使用严格模式 (`strict: true`)
- 启用未使用变量检查 (`noUnusedLocals`, `noUnusedParameters`)
- 使用 `@/` 路径别名指向 `src/` 目录
- 接口和类型使用 PascalCase 命名

### React

- 函数组件使用 `function` 声明或箭头函数
- 使用 React Hooks 管理状态和副作用
- 组件文件使用 PascalCase 命名 (如 `PlayerBar.tsx`)
- 样式文件与组件同名 (如 `PlayerBar.css`)

**示例:**
```tsx
import { useRef } from 'react'
import { usePlayerStore } from '@/stores'
import './PlayerBar.css'

function PlayerBar() {
  const { currentTrack, isPlaying, setIsPlaying } = usePlayerStore()
  const progressRef = useRef<HTMLDivElement>(null)

  return (
    <footer className="player-bar">
      {/* 组件内容 */}
    </footer>
  )
}

export default PlayerBar
```

### 状态管理 (Zustand)

- Store 使用 `use` 前缀命名 Hook (如 `usePlayerStore`)
- 需要持久化的 Store 使用 `persist` 中间件
- 状态和操作方法定义在同一接口中

**示例:**
```typescript
interface PlayerStore {
  currentTrack: Track | null
  isPlaying: boolean
  setCurrentTrack: (track: Track | null) => void
  setIsPlaying: (isPlaying: boolean) => void
}

export const usePlayerStore = create<PlayerStore>((set, get) => ({
  currentTrack: null,
  isPlaying: false,
  setCurrentTrack: (track) => set({ currentTrack: track }),
  setIsPlaying: (isPlaying) => set({ isPlaying }),
}))
```

### Electron IPC

- 主进程逻辑在 `electron/main.ts`
- 渲染进程通过 `preload.ts` 暴露的 `window.electronAPI` 调用
- 使用 `ipcRenderer.invoke` / `ipcMain.handle` 进行异步通信
- 使用 `ipcRenderer.on` / `mainWindow.webContents.send` 进行事件推送

**渲染进程调用:**
```typescript
const filePaths = await window.electronAPI.openFiles()
```

**主进程处理:**
```typescript
ipcMain.handle('dialog:openFiles', async () => {
  const result = await dialog.showOpenDialog(mainWindow!, { ... })
  return result.filePaths
})
```

### 测试

- 测试文件放在 `src/test/` 目录
- 使用 Vitest + Testing Library
- Mock Electron API 和 Audio API 在 `setup.ts` 中配置

**示例:**
```typescript
import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import PlayerBar from '@/components/PlayerBar'

describe('PlayerBar', () => {
  it('renders without crashing', () => {
    render(<PlayerBar />)
    expect(screen.getByText('未播放音乐')).toBeInTheDocument()
  })
})
```

## 架构要点

### 音频播放
- 使用 `AudioManager` 类封装 HTML5 Audio API
- 支持本地文件 (`local://` 协议) 和在线 URL
- 提供事件回调: `onPlay`, `onPause`, `onEnd`, `onTimeUpdate`, `onLoad`, `onError`

### 窗口管理
- 主窗口: 无边框 (`frame: false`)，深色主题
- 歌词窗口: 透明、始终置顶、可调整大小

### 数据持久化
- 使用 Zustand persist 中间件 + localStorage
- 持久化内容: 音乐库、播放列表、用户设置、收藏列表

## 注意事项

- Electron 开发需要同时编译主进程和渲染进程
- 修改 `electron/` 目录下的代码后需要运行 `npm run build:electron`
- 本地文件使用 `local://` 自定义协议，在主进程中注册
- 在线音乐功能依赖第三方 API，可能需要处理网络超时和错误
