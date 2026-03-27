# cli_tools - CLI 工具开发示例

使用 [Typer](https://typer.tiangolo.com/) 框架构建命令行工具的最佳实践示例集合。

## 安装依赖

```bash
uv sync
```

## 模块结构

```
cli_tools/
├── __init__.py        # 模块索引
├── basic.py           # 基础命令示例
├── advanced.py        # 高级功能示例
├── interactive.py     # 交互式功能示例
└── file_ops.py        # 文件操作工具
```

## 快速开始

### basic.py - 基础命令

展示命令、参数、选项、类型验证等基础功能。

```bash
# 查看帮助
uv run python -m cli_tools.basic --help

# 简单命令
uv run python -m cli_tools.basic hello World
uv run python -m cli_tools.basic goodbye

# 带选项的命令
uv run python -m cli_tools.basic greet --name Alice --count 3 --formal
uv run python -m cli_tools.basic greet -n Bob -c 2

# 枚举选项
uv run python -m cli_tools.basic colorize "Hello" --color red --bold

# 文件路径参数（自动验证）
uv run python -m cli_tools.basic read-file README.md --lines 10

# 多值选项
uv run python -m cli_tools.basic search "python" --tag web --tag cli --exclude deprecated

# 密码输入
uv run python -m cli_tools.basic login --user admin
```

**涵盖功能：**
- 命令定义 (`@app.command()`)
- 位置参数 (Argument)
- 选项 (Option)
- 类型验证 (int, Path, Enum)
- 默认值和可选参数
- 布尔标志
- 密码隐藏输入
- 版本信息回调

### advanced.py - 高级功能

展示子命令组、上下文共享、配置管理等高级功能。

```bash
# 查看帮助
uv run python -m cli_tools.advanced --help

# 用户管理
uv run python -m cli_tools.advanced user create alice --email alice@example.com --role admin
uv run python -m cli_tools.advanced user list
uv run python -m cli_tools.advanced user list --role admin
uv run python -m cli_tools.advanced user delete alice --force

# 项目管理
uv run python -m cli_tools.advanced project init myproject --python 3.12 --author "Your Name"
uv run python -m cli_tools.advanced project list

# 配置管理
uv run python -m cli_tools.advanced config --list
uv run python -m cli_tools.advanced config users

# 全局选项
uv run python -m cli_tools.advanced -v user list  # 详细输出
```

**涵盖功能：**
- 子命令组 (`app.add_typer()`)
- 上下文共享 (AppState)
- 配置持久化 (JSON)
- 确认对话框 (`typer.confirm()`)
- 全局回调 (`@app.callback()`)

### interactive.py - 交互式功能

展示交互式提示、进度条、表格等丰富的终端输出。

```bash
# 交互式问卷调查
uv run python -m cli_tools.interactive survey

# 进度条
uv run python -m cli_tools.interactive download --files 10 --size 50

# Spinner 动画
uv run python -m cli_tools.interactive process --duration 5

# 表格输出
uv run python -m cli_tools.interactive table --rows 5

# 目录树
uv run python -m cli_tools.interactive tree

# 仪表板布局
uv run python -m cli_tools.interactive dashboard

# 实时监控
uv run python -m cli_tools.interactive monitor --refresh 1 --count 10
```

**涵盖功能：**
- 交互式提示 (`typer.prompt()`)
- 选择题和确认
- 进度条 (Rich Progress)
- Spinner 动画 (Rich Status)
- 表格 (Rich Table)
- 树形结构 (Rich Tree)
- 面板布局 (Rich Panel/Columns)
- 实时更新 (Rich Live)

### file_ops.py - 文件操作工具

实用的文件操作命令行工具。

```bash
# 文件搜索
uv run python -m cli_tools.file_ops search . --pattern "*.py"
uv run python -m cli_tools.file_ops search . --pattern "*.py" --content "def main"
uv run python -m cli_tools.file_ops search . --pattern "*.md" --ignore-case

# 代码统计
uv run python -m cli_tools.file_ops count .
uv run python -m cli_tools.file_ops count . --ext .py --ext .ts

# 批量重命名（预览模式）
uv run python -m cli_tools.file_ops rename . --find "old" --replace "new" --dry-run
uv run python -m cli_tools.file_ops rename . --find "test" --replace "spec" --pattern "*.py"

# 清理缓存
uv run python -m cli_tools.file_ops clean . --dry-run
uv run python -m cli_tools.file_ops clean . --pycache --dist
uv run python -m cli_tools.file_ops clean . --node-modules
```

**涵盖功能：**
- 文件模式匹配 (glob/fnmatch)
- 文件内容搜索 (正则表达式)
- 递归目录遍历
- 代码行数统计
- 批量文件重命名
- 目录清理

## 关键概念

### 命令定义

```python
import typer

app = typer.Typer()

@app.command()
def hello(name: str = "World"):
    """简单问候命令"""
    typer.echo(f"Hello, {name}!")

if __name__ == "__main__":
    app()
```

### 参数和选项

```python
from typing import Annotated
import typer

@app.command()
def greet(
    name: Annotated[str, typer.Argument(help="用户名")],
    count: Annotated[int, typer.Option("--count", "-c", min=1, max=10)] = 1,
    formal: Annotated[bool, typer.Option("--formal", "-f")] = False,
):
    """带参数和选项的命令"""
    pass
```

### 子命令组

```python
app = typer.Typer()
user_app = typer.Typer(help="用户管理")

app.add_typer(user_app, name="user")

@user_app.command("create")
def create_user(name: str):
    """创建用户"""
    pass
```

### 类型验证

```python
from pathlib import Path
from enum import Enum

class Color(str, Enum):
    RED = "red"
    GREEN = "green"

@app.command()
def process(
    file: Annotated[Path, typer.Argument(exists=True, file_okay=True)],
    color: Annotated[Color, typer.Option()] = Color.GREEN,
):
    pass
```

## 依赖

- `typer` - CLI 框架
- `rich` - 终端富文本输出

## 参考资源

- [Typer 官方文档](https://typer.tiangolo.com/)
- [Rich 官方文档](https://rich.readthedocs.io/)
- [Python CLI 最佳实践](https://clig.dev/)
