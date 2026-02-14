"""Local file system service for browsing images."""

import os
from collections.abc import Iterator
from datetime import datetime
from pathlib import Path

from imageviewer.core.exceptions import FileNotFoundError
from imageviewer.core.models import AppSettings, ImageItem, SortOrder
from imageviewer.utils.image_utils import create_image_item_from_path, is_supported_format
from imageviewer.utils.logger import get_logger

logger = get_logger()


class LocalFileService:
    def __init__(self, settings: AppSettings | None = None):
        self.settings = settings or AppSettings()
        self._current_directory: Path | None = None
        self._image_cache: dict[Path, ImageItem] = {}

    @property
    def current_directory(self) -> Path | None:
        return self._current_directory

    def set_directory(self, path: Path) -> None:
        if not path.exists():
            raise FileNotFoundError(f"Directory not found: {path}")
        if not path.is_dir():
            raise FileNotFoundError(f"Path is not a directory: {path}")
        self._current_directory = path
        self._image_cache.clear()
        logger.info(f"Current directory set to: {path}")

    def get_directories(self, path: Path | None = None) -> list[Path]:
        target = path or self._current_directory
        if not target:
            return []
        try:
            return sorted(
                [p for p in target.iterdir() if p.is_dir() and not p.name.startswith(".")]
            )
        except PermissionError as e:
            logger.warning(f"Permission denied accessing {target}: {e}")
            return []

    def get_drives(self) -> list[Path]:
        if os.name == "nt":
            import string
            return [Path(f"{d}:\\") for d in string.ascii_uppercase if Path(f"{d}:").exists()]
        else:
            return [Path("/")]

    def get_parent_directory(self, path: Path | None = None) -> Path | None:
        target = path or self._current_directory
        if not target:
            return None
        return target.parent if target.parent != target else None

    def scan_images(
        self,
        path: Path | None = None,
        recursive: bool = False,
        sort_order: SortOrder = SortOrder.NAME_ASC,
    ) -> list[ImageItem]:
        target = path or self._current_directory
        if not target:
            return []
        images = list(self._scan_directory(target, recursive))
        return self._sort_images(images, sort_order)

    def _scan_directory(self, directory: Path, recursive: bool) -> Iterator[ImageItem]:
        try:
            for item in directory.iterdir():
                if item.is_file() and is_supported_format(item):
                    if item in self._image_cache:
                        yield self._image_cache[item]
                    else:
                        image_item = create_image_item_from_path(item)
                        if image_item:
                            self._image_cache[item] = image_item
                            yield image_item
                elif recursive and item.is_dir() and not item.name.startswith("."):
                    yield from self._scan_directory(item, recursive)
        except PermissionError as e:
            logger.warning(f"Permission denied scanning {directory}: {e}")

    def _sort_images(self, images: list[ImageItem], sort_order: SortOrder) -> list[ImageItem]:
        sort_functions = {
            SortOrder.NAME_ASC: lambda x: x.title.lower(),
            SortOrder.NAME_DESC: lambda x: x.title.lower(),
            SortOrder.DATE_ASC: lambda x: x.created_at or datetime.min,
            SortOrder.DATE_DESC: lambda x: x.created_at or datetime.min,
            SortOrder.SIZE_ASC: lambda x: x.file_size,
            SortOrder.SIZE_DESC: lambda x: x.file_size,
        }
        reverse_orders = {
            SortOrder.NAME_DESC: True,
            SortOrder.DATE_DESC: True,
            SortOrder.SIZE_DESC: True,
        }
        key_func = sort_functions.get(sort_order, lambda x: x.title.lower())
        reverse = reverse_orders.get(sort_order, False)
        return sorted(images, key=key_func, reverse=reverse)

    def get_image_by_path(self, path: Path) -> ImageItem | None:
        if path in self._image_cache:
            return self._image_cache[path]
        if path.exists() and is_supported_format(path):
            image_item = create_image_item_from_path(path)
            if image_item:
                self._image_cache[path] = image_item
            return image_item
        return None

    def get_recent_directories(self, max_count: int = 10) -> list[Path]:
        home = Path.home()
        common_dirs = [
            home / "Pictures",
            home / "Downloads",
            home / "Desktop",
            home / "Documents",
        ]
        return [d for d in common_dirs if d.exists()][:max_count]

    def clear_cache(self) -> None:
        self._image_cache.clear()
        logger.info("Image cache cleared")
