## Python图像查看器开发计划

### 项目结构

```
py-imageviewer/
├── src/imageviewer/
│   ├── __init__.py
│   ├── main.py                 # 应用入口
│   ├── core/
│   │   ├── models.py          # 数据模型
│   │   └── exceptions.py      # 自定义异常
│   ├── ui/
│   │   ├── main_window.py     # 主窗口
│   │   ├── local_viewer.py    # 本地浏览组件
│   │   ├── online_viewer.py   # 在线搜索组件
│   │   ├── image_viewer.py    # 图片查看组件
│   │   └── styles.py          # 样式定义
│   ├── services/
│   │   ├── local_service.py   # 本地文件服务
│   │   ├── unsplash_service.py # Unsplash API服务
│   │   └── download_service.py # 下载服务
│   └── utils/
│       ├── config.py          # 配置管理
│       ├── logger.py          # 日志配置
│       └── image_utils.py     # 图片处理工具
├── tests/
│   ├── test_local_service.py
│   ├── test_unsplash_service.py
│   └── test_download_service.py
├── config/
│   └── settings.yaml          # 应用配置
├── pyproject.toml
└── README.md
```

### 开发步骤

1. **项目初始化** (已完成)

   * 使用uv创建项目结构

   * 配置pyproject.toml依赖

2. **核心模块开发**

   * 创建数据模型（ImageItem, SearchQuery等）

   * 定义自定义异常类

   * 配置日志系统

3. **服务层开发**

   * 本地文件服务：扫描、过滤图片文件

   * Unsplash API服务：搜索、获取图片

   * 下载服务：单张/批量下载

4. **UI界面开发**

   * 主窗口框架（模式切换、导航）

   * 本地浏览面板（文件树、缩略图）

   * 在线搜索面板（搜索框、结果网格）

   * 图片查看器（缩放、旋转、全屏）

5. **测试与文档**

   * 编写单元测试

   * 编写README文档

### 技术栈

* GUI框架: PyQt6

* 图片处理: Pillow

* HTTP请求: httpx + aiohttp

* 配置管理: pydantic-settings

* 日志: loguru

