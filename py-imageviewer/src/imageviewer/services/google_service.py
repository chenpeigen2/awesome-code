"""Google Custom Search API service for image search."""

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

GOOGLE_SEARCH_BASE = "https://www.googleapis.com/customsearch/v1"


class GoogleImageService(BaseSearchService):
    def __init__(self, api_key: str | None = None, search_engine_id: str | None = None):
        self.api_key = api_key or get_settings().google_api_key
        self.search_engine_id = search_engine_id or get_settings().google_search_engine_id
        self._client: httpx.AsyncClient | None = None

    @property
    def name(self) -> str:
        return "Google Images"

    @property
    def requires_api_key(self) -> bool:
        return True

    async def _get_client(self) -> httpx.AsyncClient:
        if self._client is None or self._client.is_closed:
            self._client = httpx.AsyncClient(timeout=30.0, follow_redirects=True)
        return self._client

    def _check_api_key(self) -> None:
        if not self.api_key or not self.search_engine_id:
            raise APIKeyError(
                "Google API key or Search Engine ID not configured. "
                "Set IMAGEVIEWER_GOOGLE_API_KEY and IMAGEVIEWER_GOOGLE_SEARCH_ENGINE_ID environment variables. "
                "Get your API key from https://developers.google.com/custom-search/v1/introduction"
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
        start_index = (page - 1) * per_page + 1
        params: dict[str, Any] = {
            "key": self.api_key,
            "cx": self.search_engine_id,
            "q": query,
            "searchType": "image",
            "start": start_index,
            "num": per_page,
        }
        if filters:
            params.update(self._build_filter_params(filters))
        try:
            response = await client.get(GOOGLE_SEARCH_BASE, params=params)
            response.raise_for_status()
            data = response.json()
            images = [self._parse_item(item) for item in data.get("items", [])]
            total_results = int(data.get("searchInformation", {}).get("totalResults", 0))
            total_pages = (total_results // per_page) + 1
            return SearchResult(
                images=images,
                total=total_results,
                total_pages=min(total_pages, 100),
                current_page=page,
            )
        except httpx.HTTPStatusError as e:
            if e.response.status_code == 401:
                raise APIKeyError("Invalid Google API key") from None
            elif e.response.status_code == 403:
                raise APIRateLimitError("Google API quota exceeded") from None
            else:
                raise APIError(f"Google API error: {e}") from None
        except httpx.RequestError as e:
            raise APINetworkError(f"Network error: {e}") from None

    def _build_filter_params(self, filters: SearchFilters) -> dict[str, Any]:
        params: dict[str, Any] = {}
        if filters.size.value:
            size_map = {
                "large": "l",
                "medium": "m",
                "small": "i",
                "icon": "i",
            }
            params["imgSize"] = size_map.get(filters.size.value, "")
        if filters.color.value:
            params["imgColorType"] = "color"
            if filters.color.value not in ("color", "blackandwhite"):
                params["imgDominantColor"] = filters.color.value
        if filters.image_type.value:
            type_map = {
                "photo": "photo",
                "clipart": "clipart",
                "lineart": "lineart",
                "animated": "animated",
            }
            params["imgType"] = type_map.get(filters.image_type.value, "")
        if filters.layout.value:
            params["imgSize"] = filters.layout.value
        if filters.safe_search.value:
            safe_map = {
                "off": "off",
                "moderate": "medium",
                "strict": "high",
            }
            params["safe"] = safe_map.get(filters.safe_search.value, "medium")
        if filters.domain:
            params["siteSearch"] = filters.domain
        return params

    def _parse_item(self, data: dict[str, Any]) -> ImageItem:
        return ImageItem(
            id=data.get("cacheId", data.get("link", "")),
            source=ImageSource.ONLINE,
            url=data.get("link"),
            thumbnail_url=data.get("image", {}).get("thumbnailLink"),
            title=data.get("title", ""),
            description=data.get("snippet", ""),
            width=data.get("image", {}).get("width", 0),
            height=data.get("image", {}).get("height", 0),
            author=data.get("displayLink", ""),
            author_url=data.get("image", {}).get("contextLink"),
            download_url=data.get("link"),
            tags=[],
            created_at=datetime.now(),
        )

    async def get_image_details(self, image_id: str) -> ImageItem | None:
        return None

    async def close(self) -> None:
        if self._client and not self._client.is_closed:
            await self._client.aclose()
            self._client = None
