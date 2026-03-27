"""
交互式 CLI 功能示例
展示: 交互式提示、进度条、表格、丰富输出

运行方式:
    uv run python -m cli_tools.interactive --help
    uv run python -m cli_tools.interactive survey
    uv run python -m cli_tools.interactive download --files 5
    uv run python -m cli_tools.interactive table
"""

import time
from typing import Annotated, Optional

import typer
from rich.console import Console
from rich.progress import (
    BarColumn,
    Progress,
    SpinnerColumn,
    TaskProgressColumn,
    TextColumn,
    TimeElapsedColumn,
)
from rich.table import Table

app = typer.Typer(help="交互式 CLI 功能示例", no_args_is_help=True)
console = Console()


# ==================== 交互式提示 ====================


@app.command()
def survey() -> None:
    """交互式问卷调查"""
    typer.echo("\n📊 请填写以下问卷\n")

    # 基本文本输入
    name = typer.prompt("您的名字")

    # 选择题
    experience = typer.prompt(
        "您的编程经验",
        type=typer.Choice(["初级", "中级", "高级", "专家"]),
        default="中级",
    )

    # 确认
    likes_python = typer.confirm("您喜欢 Python 吗?", default=True)

    # 多选（通过多次确认实现）
    typer.echo("\n请选择您使用的语言（空格继续）:")
    languages = []
    for lang in ["Python", "JavaScript", "Rust", "Go", "Java"]:
        if typer.confirm(f"  {lang}", default=False):
            languages.append(lang)

    # 评分
    rating = typer.prompt(
        "给这个问卷打分 (1-10)",
        type=int,
        default=8,
    )

    # 显示结果
    console.print("\n[bold green]感谢您的参与![/bold green]\n")
    console.print(f"  姓名: [cyan]{name}[/cyan]")
    console.print(f"  经验: [cyan]{experience}[/cyan]")
    console.print(f"  喜欢 Python: [cyan]{'是' if likes_python else '否'}[/cyan]")
    console.print(f"  使用语言: [cyan]{', '.join(languages) or '无'}[/cyan]")
    console.print(f"  评分: [cyan]{rating}/10[/cyan]")


# ==================== 进度条 ====================


@app.command()
def download(
    files: Annotated[int, typer.Option("--files", "-f", help="模拟文件数量")] = 10,
    size: Annotated[int, typer.Option("--size", "-s", help="每个文件大小 (MB)")] = 100,
) -> None:
    """模拟下载进度"""
    console.print(f"\n[bold]开始下载 {files} 个文件...[/bold]\n")

    with Progress(
        SpinnerColumn(),
        TextColumn("[progress.description]{task.description}"),
        BarColumn(),
        TaskProgressColumn(),
        TimeElapsedColumn(),
        console=console,
    ) as progress:
        # 主任务
        main_task = progress.add_task(
            "[cyan]总进度",
            total=files * size,
        )

        for i in range(files):
            # 子任务
            file_task = progress.add_task(
                f"  文件 {i + 1}/{files}",
                total=size,
            )

            for j in range(size):
                # 模拟下载延迟
                time.sleep(0.02)

                progress.update(file_task, advance=1)
                progress.update(main_task, advance=1)

            progress.remove_task(file_task)

    console.print("\n[bold green]✓ 所有文件下载完成![/bold green]")


# ==================== Spinner 加载动画 ====================


@app.command()
def process(
    duration: Annotated[int, typer.Option("--duration", "-d", help="持续时间（秒）")] = 3,
) -> None:
    """模拟处理任务"""
    steps = [
        "正在初始化...",
        "加载数据...",
        "处理中...",
        "生成报告...",
        "完成!",
    ]

    console.print("\n[bold]处理任务[/bold]\n")

    with console.status("[bold green]正在处理...[/bold green]") as status:
        for step in steps:
            status.update(f"[bold cyan]{step}[/bold cyan]")
            time.sleep(duration / len(steps))

    console.print("\n[bold green]✓ 处理完成![/bold green]")


# ==================== 表格输出 ====================


@app.command()
def table(
    rows: Annotated[int, typer.Option("--rows", "-r", help="显示行数")] = 5,
) -> None:
    """显示格式化表格"""
    table = Table(title="\n📊 用户统计", show_header=True, header_style="bold magenta")

    table.add_column("ID", style="cyan", justify="center")
    table.add_column("用户名", style="green")
    table.add_column("邮箱", style="blue")
    table.add_column("状态", justify="center")
    table.add_column("注册时间")

    users = [
        (1, "alice", "alice@example.com", "活跃", "2024-01-15"),
        (2, "bob", "bob@example.com", "离线", "2024-02-20"),
        (3, "charlie", "charlie@example.com", "活跃", "2024-03-10"),
        (4, "diana", "diana@example.com", "待验证", "2024-03-25"),
        (5, "eve", "eve@example.com", "活跃", "2024-04-01"),
    ]

    for user in users[:rows]:
        status_style = {
            "活跃": "bold green",
            "离线": "dim",
            "待验证": "yellow",
        }.get(user[3], "")

        table.add_row(
            str(user[0]),
            user[1],
            user[2],
            f"[{status_style}]{user[3]}[/{status_style}]",
            user[4],
        )

    console.print(table)


# ==================== 树形结构 ====================


@app.command()
def tree() -> None:
    """显示项目结构树"""
    from rich.tree import Tree

    tree = Tree("[bold blue]📁 myproject[/bold blue]")

    src = tree.add("[bold]📁 src[/bold]")
    src.add("[green]📄 __init__.py[/green]")
    src.add("[green]📄 main.py[/green]")
    src.add("[green]📄 utils.py[/green]")

    models = src.add("[bold]📁 models[/bold]")
    models.add("[green]📄 user.py[/green]")
    models.add("[green]📄 product.py[/green]")

    tests = tree.add("[bold]📁 tests[/bold]")
    tests.add("[yellow]📄 test_main.py[/yellow]")
    tests.add("[yellow]📄 test_utils.py[/yellow]")

    tree.add("[cyan]📄 pyproject.toml[/cyan]")
    tree.add("[cyan]📄 README.md[/cyan]")
    tree.add("[cyan]📄 .gitignore[/cyan]")

    console.print(tree)


# ==================== 面板和列布局 ====================


@app.command()
def dashboard() -> None:
    """显示仪表板布局"""
    from rich.columns import Columns
    from rich.panel import Panel

    # 创建多个面板
    panel1 = Panel(
        "[bold green]100%[/bold green]\n\n系统运行正常",
        title="系统状态",
        border_style="green",
    )

    panel2 = Panel(
        "[bold blue]1,234[/bold blue]\n\n活跃用户",
        title="用户统计",
        border_style="blue",
    )

    panel3 = Panel(
        "[bold yellow]56[/bold yellow]\n\n待处理任务",
        title="任务队列",
        border_style="yellow",
    )

    panel4 = Panel(
        "[bold red]3[/bold red]\n\n需要关注",
        title="告警",
        border_style="red",
    )

    console.print("\n")
    console.print(Columns([panel1, panel2, panel3, panel4]))


# ==================== 实时更新 ====================


@app.command()
def monitor(
    refresh: Annotated[float, typer.Option("--refresh", "-r", help="刷新间隔（秒）")] = 1.0,
    count: Annotated[int, typer.Option("--count", "-c", help="更新次数")] = 10,
) -> None:
    """实时监控面板"""
    from rich.live import Live

    def generate_stats() -> Table:
        """生成统计表格"""
        import random

        table = Table(title="📊 实时监控", show_header=True)
        table.add_column("指标", style="cyan")
        table.add_column("值", justify="right")
        table.add_column("状态", justify="center")

        metrics = [
            ("CPU 使用率", f"{random.randint(20, 80)}%", "🟢"),
            ("内存使用", f"{random.randint(4, 12)} GB", "🟢"),
            ("网络流量", f"{random.randint(100, 500)} Mbps", "🟡"),
            ("磁盘 I/O", f"{random.randint(10, 100)} MB/s", "🟢"),
            ("活跃连接", str(random.randint(50, 200)), "🟢"),
        ]

        for metric, value, status in metrics:
            table.add_row(metric, value, status)

        return table

    with Live(generate_stats(), refresh_per_second=4, console=console) as live:
        for _ in range(count):
            time.sleep(refresh)
            live.update(generate_stats())

    console.print("\n[bold green]监控完成[/bold green]")


if __name__ == "__main__":
    app()
