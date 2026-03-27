"""
文件操作 CLI 工具示例
展示: 文件搜索、批量处理、文件监听

运行方式:
    uv run python -m cli_tools.file_ops --help
    uv run python -m cli_tools.file_ops search . --pattern "*.py"
    uv run python -m cli_tools.file_ops count . --ext .py
    uv run python -m cli_tools.file_ops rename . --find "old" --replace "new" --dry-run
"""

import fnmatch
import re
import shutil
from pathlib import Path
from typing import Annotated, Optional

import typer
from rich.console import Console
from rich.table import Table

app = typer.Typer(help="文件操作 CLI 工具", no_args_is_help=True)
console = Console()


# ==================== 文件搜索 ====================


@app.command("search")
def search_files(
    directory: Annotated[Path, typer.Argument(help="搜索目录")] = Path("."),
    pattern: Annotated[str, typer.Option("--pattern", "-p", help="文件模式 (如 *.py)")] = "*",
    content: Annotated[
        Optional[str],
        typer.Option("--content", "-c", help="搜索文件内容")
    ] = None,
    ignore_case: Annotated[
        bool,
        typer.Option("--ignore-case", "-i", help="忽略大小写")
    ] = False,
    max_depth: Annotated[
        Optional[int],
        typer.Option("--max-depth", "-d", help="最大搜索深度")
    ] = None,
) -> None:
    """搜索文件"""
    if not directory.exists():
        typer.secho(f"目录不存在: {directory}", fg=typer.colors.RED)
        raise typer.Exit(1)

    matches: list[Path] = []

    def should_include(path: Path) -> bool:
        """检查文件是否匹配"""
        # 检查文件名模式
        if not fnmatch.fnmatch(path.name, pattern):
            return False

        # 检查内容
        if content and path.is_file():
            try:
                text = path.read_text(encoding="utf-8", errors="ignore")
                flags = re.IGNORECASE if ignore_case else 0
                if not re.search(content, text, flags):
                    return False
            except Exception:
                return False

        return True

    def scan_dir(path: Path, depth: int = 0) -> None:
        """递归扫描目录"""
        if max_depth is not None and depth > max_depth:
            return

        try:
            for item in path.iterdir():
                # 跳过隐藏文件和常见排除目录
                if item.name.startswith("."):
                    continue
                if item.name in {"__pycache__", "node_modules", ".git", ".venv", "venv"}:
                    continue

                if item.is_file() and should_include(item):
                    matches.append(item)
                elif item.is_dir():
                    scan_dir(item, depth + 1)
        except PermissionError:
            pass

    scan_dir(directory)

    if not matches:
        typer.echo("没有找到匹配的文件")
        return

    # 显示结果
    table = Table(title=f"\n📁 搜索结果 ({len(matches)} 个文件)")
    table.add_column("文件", style="cyan")
    table.add_column("大小", justify="right", style="green")
    table.add_column("修改时间", style="dim")

    for file in sorted(matches)[:50]:  # 限制显示数量
        stat = file.stat()
        size = format_size(stat.st_size)
        mtime = format_time(stat.st_mtime)
        table.add_row(str(file.relative_to(directory)), size, mtime)

    console.print(table)

    if len(matches) > 50:
        typer.echo(f"\n... 还有 {len(matches) - 50} 个文件未显示")


# ==================== 代码统计 ====================


@app.command("count")
def count_lines(
    directory: Annotated[Path, typer.Argument(help="统计目录")] = Path("."),
    ext: Annotated[
        Optional[list[str]],
        typer.Option("--ext", "-e", help="文件扩展名 (如 .py .ts)")
    ] = None,
) -> None:
    """统计代码行数"""
    if not directory.exists():
        typer.secho(f"目录不存在: {directory}", fg=typer.colors.RED)
        raise typer.Exit(1)

    extensions = ext or [".py", ".ts", ".js", ".java", ".go", ".rs", ".c", ".cpp", ".h"]
    stats: dict[str, dict[str, int]] = {}

    def count_file(file: Path) -> None:
        """统计单个文件"""
        ext_name = file.suffix
        if ext_name not in extensions:
            return

        try:
            lines = file.read_text(encoding="utf-8").splitlines()
            total = len(lines)
            blank = sum(1 for line in lines if not line.strip())
            code = total - blank

            if ext_name not in stats:
                stats[ext_name] = {"files": 0, "lines": 0, "code": 0, "blank": 0}

            stats[ext_name]["files"] += 1
            stats[ext_name]["lines"] += total
            stats[ext_name]["code"] += code
            stats[ext_name]["blank"] += blank
        except Exception:
            pass

    def scan_dir(path: Path) -> None:
        """扫描目录"""
        try:
            for item in path.iterdir():
                if item.name.startswith("."):
                    continue
                if item.name in {"__pycache__", "node_modules", ".git", ".venv", "venv", "dist", "build"}:
                    continue

                if item.is_file():
                    count_file(item)
                elif item.is_dir():
                    scan_dir(item)
        except PermissionError:
            pass

    scan_dir(directory)

    if not stats:
        typer.echo("没有找到代码文件")
        return

    # 显示结果
    table = Table(title=f"\n📊 代码统计 - {directory}")
    table.add_column("扩展名", style="cyan")
    table.add_column("文件数", justify="right")
    table.add_column("总行数", justify="right", style="green")
    table.add_column("代码行", justify="right")
    table.add_column("空白行", justify="right", style="dim")

    total_files = 0
    total_lines = 0
    total_code = 0
    total_blank = 0

    for ext_name, data in sorted(stats.items()):
        table.add_row(
            ext_name,
            str(data["files"]),
            str(data["lines"]),
            str(data["code"]),
            str(data["blank"]),
        )
        total_files += data["files"]
        total_lines += data["lines"]
        total_code += data["code"]
        total_blank += data["blank"]

    table.add_section()
    table.add_row(
        "[bold]总计[/bold]",
        f"[bold]{total_files}[/bold]",
        f"[bold]{total_lines}[/bold]",
        f"[bold]{total_code}[/bold]",
        f"[bold]{total_blank}[/bold]",
    )

    console.print(table)


# ==================== 批量重命名 ====================


@app.command("rename")
def batch_rename(
    directory: Annotated[Path, typer.Argument(help="目标目录")] = Path("."),
    find: Annotated[str, typer.Option("--find", "-f", help="要替换的文本")] = "",
    replace: Annotated[str, typer.Option("--replace", "-r", help="替换为")] = "",
    pattern: Annotated[str, typer.Option("--pattern", "-p", help="文件模式")] = "*",
    dry_run: Annotated[
        bool,
        typer.Option("--dry-run", help="只显示预览，不实际执行")
    ] = False,
) -> None:
    """批量重命名文件"""
    if not find:
        typer.secho("请指定 --find 参数", fg=typer.colors.RED)
        raise typer.Exit(1)

    if not directory.exists():
        typer.secho(f"目录不存在: {directory}", fg=typer.colors.RED)
        raise typer.Exit(1)

    changes: list[tuple[Path, Path]] = []

    for file in directory.glob(pattern):
        if not file.is_file():
            continue

        new_name = file.name.replace(find, replace)
        if new_name != file.name:
            new_path = file.parent / new_name
            changes.append((file, new_path))

    if not changes:
        typer.echo("没有需要重命名的文件")
        return

    # 显示预览
    table = Table(title=f"\n📝 重命名预览 ({len(changes)} 个文件)")
    table.add_column("原文件名", style="red")
    table.add_column("→", justify="center")
    table.add_column("新文件名", style="green")

    for old, new in changes:
        table.add_row(old.name, "→", new.name)

    console.print(table)

    if dry_run:
        typer.echo("\n[dry-run] 未执行任何更改")
        return

    # 确认执行
    if not typer.confirm(f"\n确定要重命名 {len(changes)} 个文件吗?"):
        typer.echo("已取消")
        return

    # 执行重命名
    success = 0
    for old, new in changes:
        try:
            old.rename(new)
            success += 1
        except Exception as e:
            typer.secho(f"重命名失败: {old.name} - {e}", fg=typer.colors.RED)

    typer.secho(f"\n成功重命名 {success}/{len(changes)} 个文件", fg=typer.colors.GREEN)


# ==================== 清理文件 ====================


@app.command("clean")
def clean_files(
    directory: Annotated[Path, typer.Argument(help="清理目录")] = Path("."),
    pycache: Annotated[bool, typer.Option("--pycache", help="清理 __pycache__")] = True,
    node_modules: Annotated[bool, typer.Option("--node-modules", help="清理 node_modules")] = False,
    dist: Annotated[bool, typer.Option("--dist", help="清理 dist/build")] = True,
    dry_run: Annotated[bool, typer.Option("--dry-run", help="只显示预览")] = False,
) -> None:
    """清理临时文件和缓存"""
    if not directory.exists():
        typer.secho(f"目录不存在: {directory}", fg=typer.colors.RED)
        raise typer.Exit(1)

    to_clean: list[Path] = []

    def find_targets(path: Path) -> None:
        """查找清理目标"""
        try:
            for item in path.iterdir():
                if item.is_dir():
                    if pycache and item.name == "__pycache__":
                        to_clean.append(item)
                    elif node_modules and item.name == "node_modules":
                        to_clean.append(item)
                    elif dist and item.name in {"dist", "build", ".egg-info"}:
                        to_clean.append(item)
                    else:
                        find_targets(item)
        except PermissionError:
            pass

    find_targets(directory)

    if not to_clean:
        typer.echo("没有找到需要清理的目录")
        return

    # 显示预览
    total_size = 0
    table = Table(title=f"\n🗑️ 清理预览")
    table.add_column("目录", style="yellow")
    table.add_column("大小", justify="right", style="red")

    for target in to_clean:
        size = get_dir_size(target)
        total_size += size
        table.add_row(str(target.relative_to(directory)), format_size(size))

    table.add_section()
    table.add_row("[bold]总计[/bold]", f"[bold]{format_size(total_size)}[/bold]")

    console.print(table)

    if dry_run:
        typer.echo("\n[dry-run] 未执行任何更改")
        return

    # 确认执行
    if not typer.confirm(f"\n确定要删除 {len(to_clean)} 个目录 (共 {format_size(total_size)}) 吗?"):
        typer.echo("已取消")
        return

    # 执行清理
    for target in to_clean:
        try:
            shutil.rmtree(target)
            typer.echo(f"已删除: {target}")
        except Exception as e:
            typer.secho(f"删除失败: {target} - {e}", fg=typer.colors.RED)

    typer.secho(f"\n清理完成!", fg=typer.colors.GREEN)


# ==================== 辅助函数 ====================


def format_size(size: int) -> str:
    """格式化文件大小"""
    for unit in ["B", "KB", "MB", "GB"]:
        if size < 1024:
            return f"{size:.1f} {unit}"
        size /= 1024
    return f"{size:.1f} TB"


def format_time(timestamp: float) -> str:
    """格式化时间戳"""
    from datetime import datetime

    dt = datetime.fromtimestamp(timestamp)
    return dt.strftime("%Y-%m-%d %H:%M")


def get_dir_size(path: Path) -> int:
    """获取目录大小"""
    total = 0
    try:
        for item in path.rglob("*"):
            if item.is_file():
                total += item.stat().st_size
    except Exception:
        pass
    return total


if __name__ == "__main__":
    app()
