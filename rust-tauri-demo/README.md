# Rust 从简单到复杂 Demo

一个使用 **Tauri** 构建的交互式 Rust 学习教程，通过 10 个级别从基础到高级逐步讲解 Rust 语言特性。

## 功能特点

- **10 个学习级别**：从变量基础到高级并发编程
- **交互式界面**：通过 Tauri 桌面应用展示代码运行结果
- **详细代码示例**：每个概念都有完整的代码示例和解释
- **最佳实践指南**：学习 Rust 社区推荐的编码实践

## 学习路线

### Level 1: 基础
- 变量与常量
- 数据类型
- 字符串操作

### Level 2: 函数
- 函数定义
- 控制流
- 循环

### Level 3: 所有权（Rust 核心）
- 所有权规则
- 借用引用
- 生命周期

### Level 4: 结构体和枚举
- 结构体定义
- 枚举类型
- 模式匹配

### Level 5: Trait 和泛型
- Trait 定义与实现
- 泛型编程
- Trait Bounds

### Level 6: 错误处理
- Result 类型
- Option 类型
- 自定义错误

### Level 7: 集合和迭代器
- Vector
- HashMap
- 迭代器

### Level 8: 并发和异步
- 线程
- 通道
- 异步编程

### Level 9: 文件和序列化
- 文件操作
- JSON 序列化
- 路径处理

### Level 10: 实际应用
- API 客户端
- 缓存系统
- 状态机

## 环境要求

### 1. 安装 Rust

```bash
# Linux/macOS
curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh

# Windows: 下载并运行 https://win.rustup.rs/
```

### 2. 安装 Node.js

```bash
# 使用 nvm 安装
nvm install 22
nvm use 22
```

### 3. 安装 Tauri CLI

```bash
cargo install tauri-cli --version "^2" --locked
```

### 4. 安装系统依赖

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install libwebkit2gtk-4.1-dev \
    build-essential \
    curl \
    wget \
    libssl-dev \
    libgtk-3-dev \
    libayatana-appindicator3-dev \
    librsvg2-dev
```

**macOS:**
```bash
xcode-select --install
```

**Windows:**
```bash
# 安装 Microsoft Visual Studio C++ Build Tools
# 安装 Microsoft Edge WebView2
```

## 项目结构

```
rust-tauri-demo/
├── Cargo.toml           # Rust 依赖配置
├── tauri.conf.json      # Tauri 配置
├── build.rs             # 构建脚本
├── src/
│   ├── main.rs          # 主入口
│   └── examples/        # 示例模块
│       ├── mod.rs
│       ├── level1_basics.rs
│       ├── level2_functions.rs
│       ├── level3_ownership.rs
│       ├── level4_structs.rs
│       ├── level5_traits.rs
│       ├── level6_errors.rs
│       ├── level7_collections.rs
│       ├── level8_concurrency.rs
│       ├── level9_files.rs
│       └── level10_real_world.rs
└── ui/
    ├── index.html       # 主页面
    ├── styles.css       # 样式
    ├── app.js           # 交互逻辑
    └── package.json     # 前端依赖
```

## 快速开始

### 1. 克隆项目

```bash
cd rust-tauri-demo
```

### 2. 安装前端依赖

```bash
cd ui
pnpm install
cd ..
```

### 3. 开发模式运行

```bash
cargo tauri dev
```

### 4. 构建生产版本

```bash
cargo tauri build
```

## 开发说明

### 添加新的 Demo

1. 在 `src/examples/` 中创建新的模块文件
2. 在 `src/examples/mod.rs` 中导出模块
3. 在 `src/main.rs` 中注册 Tauri 命令
4. 在 `ui/app.js` 的 `demoConfig` 中添加配置

### 示例代码模板

```rust
// src/examples/levelX_topic.rs

/// Demo 演示函数
#[tauri::command]
pub fn demo_topic() -> HashMap<String, String> {
    let mut result = HashMap::new();

    result.insert(
        "概念说明".to_string(),
        "这里写概念解释...".to_string()
    );

    result.insert(
        "代码示例".to_string(),
        r#"// Rust 代码
let x = 42;"#.to_string()
    );

    result
}
```

## 技术栈

- **后端**: Rust + Tauri 2.0
- **前端**: 原生 HTML/CSS/JavaScript
- **依赖**:
  - serde: 序列化
  - tokio: 异步运行时
  - anyhow/thiserror: 错误处理
  - chrono: 日期时间
  - regex: 正则表达式

## 常见问题

### Q: 编译报错找不到 tauri
```bash
cargo install tauri-cli --version "^2" --locked
```

### Q: Linux 下 webkit2gtk 错误
```bash
sudo apt install libwebkit2gtk-4.1-dev
```

### Q: 如何单独运行前端开发
```bash
cd ui
npx http-server -p 5173
# 然后在浏览器打开 http://localhost:5173
```

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request！
