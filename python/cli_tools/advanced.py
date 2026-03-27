"""
高级 CLI 功能示例
展示: 子命令组、上下文共享、配置管理、状态持久化

运行方式:
    uv run python -m cli_tools.advanced --help
    uv run python -m cli_tools.advanced user create alice --email alice@example.com
    uv run python -m cli_tools.advanced user list
    uv run python -m cli_tools.advanced project init myproject --python 3.12
"""

import json
from pathlib import Path
from typing import Annotated, Optional

import typer
from typing_extensions import TypedDict

app = typer.Typer(help="高级 CLI 功能示例", no_args_is_help=True)

# 子命令应用
user_app = typer.Typer(help="用户管理命令")
project_app = typer.Typer(help="项目管理命令")

app.add_typer(user_app, name="user")
app.add_typer(project_app, name="project")


# ==================== 数据类型定义 ====================


class User(TypedDict):
    """用户数据类型"""
    username: str
    email: str
    role: str


class ProjectConfig(TypedDict):
    """项目配置类型"""
    name: str
    python_version: str
    author: str
    dependencies: list[str]


# ==================== 上下文共享 ====================


class AppState:
    """应用状态"""

    def __init__(self) -> None:
        self.config_dir = Path.home() / ".cli_tools"
        self.config_file = self.config_dir / "config.json"
        self.verbose = False
        self._ensure_config()

    def _ensure_config(self) -> None:
        """确保配置目录存在"""
        self.config_dir.mkdir(parents=True, exist_ok=True)
        if not self.config_file.exists():
            self.save_config({"users": {}, "projects": {}})

    def load_config(self) -> dict:
        """加载配置"""
        return json.loads(self.config_file.read_text())

    def save_config(self, config: dict) -> None:
        """保存配置"""
        self.config_file.write_text(json.dumps(config, indent=2, ensure_ascii=False))

    def log(self, message: str) -> None:
        """条件日志输出"""
        if self.verbose:
            typer.echo(f"[DEBUG] {message}")


# 全局状态
state = AppState()


@app.callback()
def app_callback(
    verbose: Annotated[
        bool,
        typer.Option("--verbose", "-v", help="启用详细输出")
    ] = False,
) -> None:
    """全局选项"""
    state.verbose = verbose


# ==================== 用户管理命令 ====================


@user_app.command("create")
def create_user(
    username: Annotated[str, typer.Argument(help="用户名")],
    email: Annotated[str, typer.Option("--email", "-e", help="邮箱地址")],
    role: Annotated[
        str,
        typer.Option("--role", "-r", help="用户角色")
    ] = "user",
) -> None:
    """创建新用户"""
    state.log(f"创建用户: {username}")

    config = state.load_config()

    if username in config["users"]:
        typer.secho(f"用户 '{username}' 已存在!", fg=typer.colors.YELLOW)
        raise typer.Exit(1)

    user: User = {
        "username": username,
        "email": email,
        "role": role,
    }

    config["users"][username] = user
    state.save_config(config)

    typer.secho(f"用户 '{username}' 创建成功!", fg=typer.colors.GREEN)
    typer.echo(f"  邮箱: {email}")
    typer.echo(f"  角色: {role}")


@user_app.command("list")
def list_users(
    role: Annotated[
        Optional[str],
        typer.Option("--role", "-r", help="按角色过滤")
    ] = None,
) -> None:
    """列出所有用户"""
    state.log("列出用户")

    config = state.load_config()
    users = config["users"]

    if not users:
        typer.echo("没有用户")
        return

    typer.echo("用户列表:")
    typer.echo("-" * 50)

    for username, user in users.items():
        if role and user["role"] != role:
            continue

        typer.echo(f"  {username}")
        typer.echo(f"    邮箱: {user['email']}")
        typer.echo(f"    角色: {user['role']}")
        typer.echo()


@user_app.command("delete")
def delete_user(
    username: Annotated[str, typer.Argument(help="要删除的用户名")],
    force: Annotated[
        bool,
        typer.Option("--force", "-f", help="强制删除，不确认")
    ] = False,
) -> None:
    """删除用户"""
    config = state.load_config()

    if username not in config["users"]:
        typer.secho(f"用户 '{username}' 不存在!", fg=typer.colors.RED)
        raise typer.Exit(1)

    if not force:
        confirm = typer.confirm(f"确定要删除用户 '{username}' 吗?")
        if not confirm:
            typer.echo("已取消")
            raise typer.Abort()

    del config["users"][username]
    state.save_config(config)

    typer.secho(f"用户 '{username}' 已删除", fg=typer.colors.GREEN)


# ==================== 项目管理命令 ====================


@project_app.command("init")
def init_project(
    name: Annotated[str, typer.Argument(help="项目名称")],
    python: Annotated[
        str,
        typer.Option("--python", "-p", help="Python 版本")
    ] = "3.11",
    author: Annotated[
        Optional[str],
        typer.Option("--author", "-a", help="作者")
    ] = None,
) -> None:
    """初始化新项目"""
    state.log(f"初始化项目: {name}")

    project_dir = Path.cwd() / name

    if project_dir.exists():
        typer.secho(f"目录 '{name}' 已存在!", fg=typer.colors.RED)
        raise typer.Exit(1)

    # 创建项目结构
    project_dir.mkdir()
    (project_dir / "src" / name).mkdir(parents=True)
    (project_dir / "tests").mkdir()

    # 创建文件
    (project_dir / "src" / name / "__init__.py").write_text(f'"""{name} - A Python project"""\n__version__ = "0.1.0"\n')

    pyproject = f'''[project]
name = "{name}"
version = "0.1.0"
requires-python = ">={python}"

[build-system]
requires = ["hatchling"]
build-backend = "hatchling.build"
'''
    (project_dir / "pyproject.toml").write_text(pyproject)

    # 保存到配置
    config = state.load_config()
    project_config: ProjectConfig = {
        "name": name,
        "python_version": python,
        "author": author or "Unknown",
        "dependencies": [],
    }
    config["projects"][name] = project_config
    state.save_config(config)

    typer.secho(f"项目 '{name}' 初始化成功!", fg=typer.colors.GREEN)
    typer.echo(f"  Python: {python}")
    typer.echo(f"  作者: {author or '未设置'}")
    typer.echo(f"\n下一步:")
    typer.echo(f"  cd {name}")
    typer.echo(f"  uv sync")


@project_app.command("list")
def list_projects() -> None:
    """列出所有项目"""
    config = state.load_config()
    projects = config["projects"]

    if not projects:
        typer.echo("没有项目")
        return

    typer.echo("项目列表:")
    typer.echo("-" * 50)

    for name, project in projects.items():
        typer.echo(f"  {name}")
        typer.echo(f"    Python: {project['python_version']}")
        typer.echo(f"    作者: {project['author']}")
        typer.echo()


# ==================== 配置命令 ====================


@app.command("config")
def manage_config(
    key: Annotated[Optional[str], typer.Argument(help="配置键")] = None,
    value: Annotated[Optional[str], typer.Argument(help="配置值")] = None,
    list_all: Annotated[
        bool,
        typer.Option("--list", "-l", help="列出所有配置")
    ] = False,
) -> None:
    """查看或修改配置"""
    if list_all or key is None:
        config = state.load_config()
        typer.echo(json.dumps(config, indent=2, ensure_ascii=False))
        return

    if value is None:
        # 读取配置
        config = state.load_config()
        keys = key.split(".")
        current = config

        try:
            for k in keys:
                current = current[k]
            typer.echo(json.dumps(current, indent=2, ensure_ascii=False))
        except (KeyError, TypeError):
            typer.secho(f"配置键 '{key}' 不存在", fg=typer.colors.RED)
            raise typer.Exit(1)
    else:
        # 写入配置 - 简化版本，实际需要更复杂的解析
        typer.secho("设置配置需要更复杂的实现", fg=typer.colors.YELLOW)


if __name__ == "__main__":
    app()
