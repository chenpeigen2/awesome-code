"""
基础 CLI 命令示例
展示: 命令、参数、选项、类型验证、帮助文本

运行方式:
    uv run python -m cli_tools.basic --help
    uv run python -m cli_tools.basic hello World
    uv run python -m cli_tools.basic greet --name Alice --count 3
"""

from enum import Enum
from pathlib import Path
from typing import Annotated, Optional

import typer

app = typer.Typer(help="基础 CLI 命令示例", no_args_is_help=True)


# ==================== 简单命令 ====================


@app.command()
def hello(name: str = "World") -> None:
    """简单问候命令"""
    typer.echo(f"Hello, {name}!")


@app.command()
def goodbye(name: str = "World") -> None:
    """告别命令"""
    typer.echo(f"Goodbye, {name}!")


# ==================== 带选项的命令 ====================


@app.command()
def greet(
    name: Annotated[
        str,
        typer.Option(
            "--name", "-n",
            help="要问候的名字",
            rich_help_panel="个性化选项"
        )
    ] = "World",
    count: Annotated[
        int,
        typer.Option(
            "--count", "-c",
            min=1, max=10,
            help="问候次数"
        )
    ] = 1,
    formal: Annotated[
        bool,
        typer.Option(
            "--formal", "-f",
            help="使用正式问候语"
        )
    ] = False,
) -> None:
    """带多个选项的问候命令"""
    greeting = "您好" if formal else "你好"

    for i in range(count):
        typer.echo(f"{greeting}, {name}! (第 {i + 1} 次)")


# ==================== 枚举选项 ====================


class Color(str, Enum):
    """颜色枚举"""
    RED = "red"
    GREEN = "green"
    BLUE = "blue"
    YELLOW = "yellow"


@app.command()
def colorize(
    text: Annotated[str, typer.Argument(help="要着色的文本")],
    color: Annotated[
        Color,
        typer.Option(
            "--color", "-c",
            help="文本颜色"
        )
    ] = Color.GREEN,
    bold: Annotated[bool, typer.Option(help="是否加粗")] = False,
) -> None:
    """给文本添加颜色"""
    from rich.console import Console
    from rich.text import Text

    console = Console()
    styled_text = Text(text, style=f"{color.value}{' bold' if bold else ''}")
    console.print(styled_text)


# ==================== 文件路径参数 ====================


@app.command()
def read_file(
    file: Annotated[
        Path,
        typer.Argument(
            exists=True,
            file_okay=True,
            dir_okay=False,
            readable=True,
            help="要读取的文件路径"
        )
    ],
    lines: Annotated[
        Optional[int],
        typer.Option("--lines", "-l", help="只显示前 N 行")
    ] = None,
) -> None:
    """读取并显示文件内容"""
    content = file.read_text(encoding="utf-8")

    if lines:
        content_lines = content.splitlines()[:lines]
        for i, line in enumerate(content_lines, 1):
            typer.echo(f"{i:4d} | {line}")
    else:
        typer.echo(content)


# ==================== 多值选项 ====================


@app.command()
def search(
    query: Annotated[str, typer.Argument(help="搜索关键词")],
    tags: Annotated[
        Optional[list[str]],
        typer.Option("--tag", "-t", help="过滤标签（可多次使用）")
    ] = None,
    exclude: Annotated[
        Optional[list[str]],
        typer.Option("--exclude", "-e", help="排除标签（可多次使用）")
    ] = None,
) -> None:
    """模拟搜索命令"""
    typer.echo(f"搜索关键词: {query}")

    if tags:
        typer.echo(f"包含标签: {', '.join(tags)}")

    if exclude:
        typer.echo(f"排除标签: {', '.join(exclude)}")

    # 模拟搜索结果
    typer.echo("\n搜索结果:")
    for i, tag in enumerate(tags or ["默认"], 1):
        typer.echo(f"  {i}. 找到与 '{tag}' 相关的结果")


# ==================== 布尔标志 ====================


@app.command()
def config(
    verbose: Annotated[bool, typer.Option("--verbose", "-v", help="详细输出")] = False,
    quiet: Annotated[bool, typer.Option("--quiet", "-q", help="静默模式")] = False,
    debug: Annotated[bool, typer.Option("--debug", help="调试模式")] = False,
) -> None:
    """配置命令示例"""
    if quiet and verbose:
        typer.secho("错误: --quiet 和 --verbose 不能同时使用", fg=typer.colors.RED)
        raise typer.Exit(1)

    level = "NORMAL"
    if debug:
        level = "DEBUG"
    elif verbose:
        level = "VERBOSE"
    elif quiet:
        level = "QUIET"

    typer.echo(f"日志级别: {level}")


# ==================== 密码输入 ====================


@app.command()
def login(
    username: Annotated[str, typer.Option("--user", "-u", help="用户名")],
    password: Annotated[
        str,
        typer.Option(
            "--password", "-p",
            prompt=True,
            hide_input=True,
            help="密码"
        )
    ],
) -> None:
    """模拟登录命令"""
    # 模拟验证
    if len(password) < 6:
        typer.secho("密码太短，至少需要 6 个字符", fg=typer.colors.RED)
        raise typer.Exit(1)

    typer.secho(f"用户 '{username}' 登录成功!", fg=typer.colors.GREEN)


# ==================== 版本信息 ====================


def version_callback(value: bool) -> None:
    """版本回调函数"""
    if value:
        typer.echo("cli_tools basic v1.0.0")
        raise typer.Exit()


@app.callback()
def main(
    version: Annotated[
        Optional[bool],
        typer.Option(
            "--version", "-V",
            callback=version_callback,
            is_eager=True,
            help="显示版本信息"
        )
    ] = None,
) -> None:
    """基础 CLI 命令示例的主入口"""
    pass


if __name__ == "__main__":
    app()
