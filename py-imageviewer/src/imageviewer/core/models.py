"""Data models for the image viewer application."""

from dataclasses import dataclass, field
from datetime import datetime
from enum import Enum
from pathlib import Path

from pydantic import BaseModel, Field


class ImageSource(str, Enum):
    LOCAL = "local"
    ONLINE = "online"


class ImageFormat(str, Enum):
    JPEG = "jpeg"
    PNG = "png"
    GIF = "gif"
    BMP = "bmp"
    WEBP = "webp"
    TIFF = "tiff"
    UNKNOWN = "unknown"


class ImageItem(BaseModel):
    id: str
    source: ImageSource
    file_path: Path | None = None
    url: str | None = None
    thumbnail_url: str | None = None
    title: str = ""
    description: str = ""
    width: int = 0
    height: int = 0
    file_size: int = 0
    format: ImageFormat = ImageFormat.UNKNOWN
    created_at: datetime | None = None
    author: str = ""
    author_url: str | None = None
    download_url: str | None = None
    tags: list[str] = Field(default_factory=list)

    @property
    def aspect_ratio(self) -> float:
        if self.height == 0:
            return 1.0
        return self.width / self.height

    @property
    def is_local(self) -> bool:
        return self.source == ImageSource.LOCAL

    @property
    def is_online(self) -> bool:
        return self.source == ImageSource.ONLINE


class SearchQuery(BaseModel):
    query: str
    page: int = 1
    per_page: int = 30
    order_by: str = "relevant"
    color: str | None = None
    orientation: str | None = None


class SearchResult(BaseModel):
    images: list[ImageItem] = Field(default_factory=list)
    total: int = 0
    total_pages: int = 0
    current_page: int = 1

    @property
    def has_next(self) -> bool:
        return self.current_page < self.total_pages

    @property
    def has_previous(self) -> bool:
        return self.current_page > 1


class DownloadTask(BaseModel):
    id: str
    image: ImageItem
    save_path: Path
    status: str = "pending"
    progress: float = 0.0
    error_message: str | None = None
    started_at: datetime | None = None
    completed_at: datetime | None = None


class ViewMode(str, Enum):
    LOCAL = "local"
    ONLINE = "online"


class SortOrder(str, Enum):
    NAME_ASC = "name_asc"
    NAME_DESC = "name_desc"
    DATE_ASC = "date_asc"
    DATE_DESC = "date_desc"
    SIZE_ASC = "size_asc"
    SIZE_DESC = "size_desc"


@dataclass
class AppSettings:
    download_dir: Path = field(default_factory=lambda: Path.home() / "Pictures" / "ImageViewer")
    thumbnail_size: tuple[int, int] = (200, 200)
    supported_formats: list[str] = field(
        default_factory=lambda: [".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".tiff", ".tif"]
    )
    max_concurrent_downloads: int = 3
    auto_save_downloads: bool = True
    show_hidden_files: bool = False
    slideshow_interval: int = 5
    unsplash_api_key: str = ""
    pexels_api_key: str = ""


class ImageTransform(BaseModel):
    rotation: int = 0
    scale: float = 1.0
    flip_horizontal: bool = False
    flip_vertical: bool = False

    def reset(self) -> None:
        self.rotation = 0
        self.scale = 1.0
        self.flip_horizontal = False
        self.flip_vertical = False
