# 图像查看器 (ImageViewer)

一个功能完善的Python图像查看器应用程序，支持本地图片浏览和在线图片搜索功能。

## 功能特性

### 本地图片浏览
- 浏览本地文件系统中的图片文件
- 支持常见图片格式：JPG、PNG、GIF、BMP、WebP、TIFF
- 目录树导航
- 缩略图预览
- 多种排序方式（名称、日期、大小）

### 在线图片搜索
- 集成 Unsplash API 进行图片搜索
- 关键词搜索
- 颜色过滤
- 方向过滤（横向/纵向/正方形）
- 随机图片获取

### 图片查看
- 图片预览
- 缩放（放大/缩小/适应窗口/实际大小）
- 旋转（向左/向右旋转90度）
- 翻转（水平/垂直翻转）
- 鼠标滚轮缩放
- 中键拖拽平移

### 下载功能
- 单张图片下载
- 批量下载
- 自定义保存路径
- 下载进度显示

## 安装

### 前置要求
- Python 3.10+
- uv 包管理器

### 安装步骤

1. 克隆仓库
```bash
git clone <repository-url>
cd py-imageviewer
```

2. 使用 uv 安装依赖
```bash
uv sync
```

3. 配置 API 密钥（可选，用于在线搜索功能）

创建 `.env` 文件或设置环境变量：
```bash
IMAGEVIEWER_UNSPLASH_API_KEY=your_unsplash_api_key
```

获取 Unsplash API 密钥：https://unsplash.com/developers

## 使用方法

### 启动应用
```bash
uv run imageviewer
```

或者：
```bash
uv run python -m imageviewer.main
```

### 快捷键

| 快捷键 | 功能 |
|--------|------|
| Ctrl+O | 打开文件 |
| Ctrl+S | 保存图片 |
| Ctrl++ | 放大 |
| Ctrl+- | 缩小 |
| Ctrl+0 | 实际大小 |
| Ctrl+F | 适应窗口 |
| Ctrl+Left | 向左旋转 |
| Ctrl+Right | 向右旋转 |
| Ctrl+L | 切换到本地浏览 |
| Ctrl+Shift+O | 切换到在线搜索 |

### 鼠标操作

| 操作 | 功能 |
|------|------|
| Ctrl + 滚轮 | 缩放图片 |
| 中键拖拽 | 平移图片 |
| 双击图片 | 查看大图 |

## 项目结构

```
py-imageviewer/
├── src/imageviewer/
│   ├── __init__.py
│   ├── main.py              # 应用入口
│   ├── core/
│   │   ├── models.py        # 数据模型
│   │   └── exceptions.py    # 自定义异常
│   ├── ui/
│   │   ├── main_window.py   # 主窗口
│   │   ├── local_viewer.py  # 本地浏览面板
│   │   ├── online_viewer.py # 在线搜索面板
│   │   ├── image_viewer.py  # 图片查看器
│   │   └── styles.py        # 样式定义
│   ├── services/
│   │   ├── local_service.py   # 本地文件服务
│   │   ├── unsplash_service.py # Unsplash API服务
│   │   └── download_service.py # 下载服务
│   └── utils/
│       ├── config.py        # 配置管理
│       ├── logger.py        # 日志配置
│       └── image_utils.py   # 图片处理工具
├── tests/                   # 测试文件
├── pyproject.toml           # 项目配置
└── README.md
```

## 开发

### 安装开发依赖
```bash
uv sync --extra dev
```

### 运行测试
```bash
uv run pytest
```

### 代码检查
```bash
uv run ruff check src/
uv run mypy src/
```

## 配置

应用支持以下环境变量配置：

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| IMAGEVIEWER_UNSPLASH_API_KEY | Unsplash API 密钥 | - |
| IMAGEVIEWER_DOWNLOAD_DIR | 下载目录 | ~/Pictures/ImageViewer |
| IMAGEVIEWER_THUMBNAIL_SIZE | 缩略图大小 | 200 |
| IMAGEVIEWER_MAX_CONCURRENT_DOWNLOADS | 最大并发下载数 | 3 |
| IMAGEVIEWER_LOG_LEVEL | 日志级别 | INFO |

## 技术栈

- **GUI框架**: PyQt6
- **图片处理**: Pillow
- **HTTP请求**: httpx + aiohttp
- **配置管理**: pydantic-settings
- **日志**: loguru
- **包管理**: uv

## 系统兼容性

- Windows 10/11
- macOS 10.15+
- Linux (主流发行版)

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request！

## 未来计划

- [ ] 图片编辑功能（裁剪、滤镜等）
- [ ] 收藏夹功能
- [ ] 幻灯片播放
- [ ] 更多图片源支持（Pexels、Pixabay等）
- [ ] 图片元数据显示
- [ ] 批量重命名
