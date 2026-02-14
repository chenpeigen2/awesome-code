"""Bing Image Search API service."""

import asyncio
from datetime import datetime
from typing import Any

import httpx

from imageviewer.core.exceptions import APIError, APIKeyError, APIRateLimitError, APINetworkError
from imageviewer.core.models import ImageItem, ImageSource, SearchResult
from imageviewer.services.base_search_service import BaseSearchService, SearchFilters
from imageviewer.utils.config import get_settings
from imageviewer.utils.logger import get_logger

logger = get_logger()

BING_SEARCH_BASE = "https://api.bing.microsoft.com/v7.0/images/search"


class BingImageService(BaseSearchService):
    def __init__(self, api_key: str | None = None):
        self.api_key = api_key or get_settings().bing_api_key
        self._client: httpx.AsyncClient | None = None

    @property
    def name(self) -> str:
        return "Bing Images"

    @property
    def requires_api_key(self) -> bool:
        return True

    async def _get_client(self) -> httpx.AsyncClient:
        if self._client is None or self._client.is_closed:
            self._client = httpx.AsyncClient(
                headers={"Ocp-Apim-Subscription-Key": self.api_key} if self.api_key else {},
                timeout=30.0,
                follow_redirects=True,
            )
        return self._client

    def _check_api_key(self) -> None:
        if not self.api_key:
            raise APIKeyError(
                "Bing API key not configured. "
                "Set IMAGEVIEWER_BING_API_KEY environment variable. "
                "Get your API key from https://azure.microsoft.com/en-us/services/cognitive-services/bing-image-search-api/"
            )

    async def search(
        self,
        query: str,
        page: int = 1,
        per_page: int = 30,
        filters: SearchFilters | None = None,
    ) -> SearchResult:
        self._check_api_key()
        client = await self._get_client()
        offset = (page - 1) * per_page
        params: dict[str, Any] = {
            "q": query,
            "count": per_page,
            "offset": offset,
            "mkt": "en-US",
        }
        if filters:
            params.update(self._build_filter_params(filters))
        try:
            response = await client.get(BING_SEARCH_BASE, params=params)
            response.raise_for_status()
            data = response.json()
            images = [self._parse_item(item) for item in data.get("value", [])]
            total_results = data.get("totalEstimatedMatches", 0)
            total_pages = (total_results // per_page) + 1
            return SearchResult(
                images=images,
                total=total_results,
                total_pages=min(total_pages, 100),
                current_page=page,
            )
        except httpx.HTTPStatusError as e:
            if e.response.status_code == 401:
                raise APIKeyError("Invalid Bing API key") from None
            elif e.response.status_code == 403:
                raise APIRateLimitError("Bing API quota exceeded") from None
            else:
                raise APIError(f"Bing API error: {e}") from None
        except httpx.RequestError as e:
            raise APINetworkError(f"Network error: {e}") from None

    def _build_filter_params(self, filters: SearchFilters) -> dict[str, Any]:
        params: dict[str, Any] = {}
        if filters.size.value:
            size_map = {
                "large": "Wallpaper",
                "medium": "Medium",
                "small": "Small",
                "icon": "Icon",
            }
            params["size"] = size_map.get(filters.size.value, "All")
        if filters.color.value:
            if filters.color.value == "blackandwhite":
                params["color"] = "Monochrome"
            elif filters.color.value != "color":
                params["color"] = filters.color.value.capitalize()
        if filters.image_type.value:
            type_map = {
                "photo": "Photo",
                "clipart": "Clipart",
                "lineart": "Line",
                "animated": "AnimatedGif",
            }
            params["imageType"] = type_map.get(filters.image_type.value, "All")
        if filters.layout.value:
            layout_map = {
                "horizontal": "Wide",
                "vertical": "Tall",
                "square": "Square",
            }
            params["aspect"] = layout_map.get(filters.layout.value, "All")
        if filters.safe_search.value:
            safe_map = {
                "off": "Off",
                "moderate": "Moderate",
                "strict": "Strict",
            }
            params["safeSearch"] = safe_map.get(filters.safe_search.value, "Moderate")
        if filters.date_range:
            params["freshness"] = filters.date_range
        return params

    def _parse_item(self, data: dict[str, Any]) -> ImageItem:
        return ImageItem(
            id=data.get("imageId", data.get("contentUrl", "")),
            source=ImageSource.ONLINE,
            url=data.get("contentUrl"),
            thumbnail_url=data.get("thumbnailUrl"),
            title=data.get("name", ""),
            description="",
            width=data.get("width", 0),
            height=data.get("height", 0),
            author=data.get("hostPageDisplayUrl", ""),
            author_url=data.get("hostPageUrl"),
            download_url=data.get("contentUrl"),
            tags=[],
            created_at=datetime.now(),
        )

    async def get_image_details(self, image_id: str) -> ImageItem | None:
        return None

    async def close(self) -> None:
        if self._client and not self._client.is_closed:
            await self._client.aclose()
            self._client = None
