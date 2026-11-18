"""Base search service interface for image search engines."""

from abc import ABC, abstractmethod
from dataclasses import dataclass
from enum import StrEnum
from typing import Any

from imageviewer.core.models import ImageItem, SearchResult


class ImageSize(StrEnum):
    ANY = ""
    LARGE = "large"
    MEDIUM = "medium"
    SMALL = "small"
    ICON = "icon"


class ImageColor(StrEnum):
    ANY = ""
    COLOR = "color"
    BLACK_WHITE = "blackandwhite"
    RED = "red"
    ORANGE = "orange"
    YELLOW = "yellow"
    GREEN = "green"
    BLUE = "blue"
    PURPLE = "purple"
    PINK = "pink"
    WHITE = "white"
    BLACK = "black"


class ImageType(StrEnum):
    ANY = ""
    PHOTO = "photo"
    CLIPART = "clipart"
    LINEART = "lineart"
    ANIMATED = "animated"


class ImageLayout(StrEnum):
    ANY = ""
    HORIZONTAL = "horizontal"
    VERTICAL = "vertical"
    SQUARE = "square"


class SafeSearch(StrEnum):
    OFF = "off"
    MODERATE = "moderate"
    STRICT = "strict"


@dataclass
class SearchFilters:
    size: ImageSize = ImageSize.ANY
    color: ImageColor = ImageColor.ANY
    image_type: ImageType = ImageType.ANY
    layout: ImageLayout = ImageLayout.ANY
    safe_search: SafeSearch = SafeSearch.MODERATE
    date_range: str | None = None
    domain: str | None = None

    def to_dict(self) -> dict[str, Any]:
        return {
            "size": self.size.value,
            "color": self.color.value,
            "image_type": self.image_type.value,
            "layout": self.layout.value,
            "safe_search": self.safe_search.value,
            "date_range": self.date_range,
            "domain": self.domain,
        }


class BaseSearchService(ABC):
    @property
    @abstractmethod
    def name(self) -> str:
        pass

    @property
    @abstractmethod
    def requires_api_key(self) -> bool:
        pass

    @abstractmethod
    async def search(
        self,
        query: str,
        page: int = 1,
        per_page: int = 30,
        filters: SearchFilters | None = None,
    ) -> SearchResult:
        pass

    @abstractmethod
    async def get_image_details(self, image_id: str) -> ImageItem | None:
        pass

    async def close(self) -> None:
        """默认没有需要释放的资源；持有会话的子类可覆写。"""
        return None

    async def __aenter__(self) -> "BaseSearchService":
        return self

    async def __aexit__(self, exc_type, exc_val, exc_tb) -> None:
        await self.close()
