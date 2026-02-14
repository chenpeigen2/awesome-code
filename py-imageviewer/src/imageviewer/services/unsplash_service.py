"""Unsplash API service for online image search."""

from datetime import datetime

import httpx

from imageviewer.core.exceptions import APIError, APIKeyError, APINetworkError, APIRateLimitError
from imageviewer.core.models import ImageItem, ImageSource, SearchQuery, SearchResult
from imageviewer.utils.config import get_settings
from imageviewer.utils.logger import get_logger

logger = get_logger()

UNSPLASH_API_BASE = "https://api.unsplash.com"


class UnsplashService:
    def __init__(self, api_key: str | None = None):
        self.api_key = api_key or get_settings().unsplash_api_key
        self._client: httpx.AsyncClient | None = None

    async def _get_client(self) -> httpx.AsyncClient:
        if self._client is None or self._client.is_closed:
            self._client = httpx.AsyncClient(
                headers={"Authorization": f"Client-ID {self.api_key}"},
                timeout=30.0,
                follow_redirects=True,
            )
        return self._client

    def _check_api_key(self) -> None:
        if not self.api_key:
            raise APIKeyError("Unsplash API key is not configured. Please set IMAGEVIEWER_UNSPLASH_API_KEY environment variable.")

    async def search_photos(self, query: SearchQuery) -> SearchResult:
        self._check_api_key()
        client = await self._get_client()
        params = {
            "query": query.query,
            "page": query.page,
            "per_page": query.per_page,
            "order_by": query.order_by,
        }
        if query.color:
            params["color"] = query.color
        if query.orientation:
            params["orientation"] = query.orientation
        try:
            response = await client.get(f"{UNSPLASH_API_BASE}/search/photos", params=params)
            response.raise_for_status()
            data = response.json()
            images = [self._parse_photo(photo) for photo in data.get("results", [])]
            return SearchResult(
                images=images,
                total=data.get("total", 0),
                total_pages=data.get("total_pages", 0),
                current_page=query.page,
            )
        except httpx.HTTPStatusError as e:
            if e.response.status_code == 401:
                raise APIKeyError("Invalid Unsplash API key") from None
            elif e.response.status_code == 403:
                raise APIRateLimitError("Unsplash API rate limit exceeded") from None
            else:
                raise APIError(f"Unsplash API error: {e}") from None
        except httpx.RequestError as e:
            raise APINetworkError(f"Network error: {e}") from None

    async def get_photo(self, photo_id: str) -> ImageItem | None:
        self._check_api_key()
        client = await self._get_client()
        try:
            response = await client.get(f"{UNSPLASH_API_BASE}/photos/{photo_id}")
            response.raise_for_status()
            data = response.json()
            return self._parse_photo(data)
        except httpx.HTTPStatusError as e:
            logger.error(f"Failed to get photo {photo_id}: {e}")
            return None
        except httpx.RequestError as e:
            raise APINetworkError(f"Network error: {e}") from None

    async def get_random_photos(self, count: int = 10) -> list[ImageItem]:
        self._check_api_key()
        client = await self._get_client()
        try:
            response = await client.get(f"{UNSPLASH_API_BASE}/photos/random", params={"count": count})
            response.raise_for_status()
            data = response.json()
            return [self._parse_photo(photo) for photo in data]
        except httpx.HTTPStatusError as e:
            logger.error(f"Failed to get random photos: {e}")
            return []
        except httpx.RequestError as e:
            raise APINetworkError(f"Network error: {e}") from None

    async def get_collections(self, page: int = 1, per_page: int = 10) -> list[dict]:
        self._check_api_key()
        client = await self._get_client()
        try:
            response = await client.get(
                f"{UNSPLASH_API_BASE}/collections",
                params={"page": page, "per_page": per_page},
            )
            response.raise_for_status()
            return response.json()
        except httpx.HTTPStatusError as e:
            logger.error(f"Failed to get collections: {e}")
            return []
        except httpx.RequestError as e:
            raise APINetworkError(f"Network error: {e}") from None

    async def get_collection_photos(self, collection_id: str, page: int = 1, per_page: int = 30) -> list[ImageItem]:
        self._check_api_key()
        client = await self._get_client()
        try:
            response = await client.get(
                f"{UNSPLASH_API_BASE}/collections/{collection_id}/photos",
                params={"page": page, "per_page": per_page},
            )
            response.raise_for_status()
            data = response.json()
            return [self._parse_photo(photo) for photo in data]
        except httpx.HTTPStatusError as e:
            logger.error(f"Failed to get collection photos: {e}")
            return []
        except httpx.RequestError as e:
            raise APINetworkError(f"Network error: {e}") from None

    def _parse_photo(self, data: dict) -> ImageItem:
        urls = data.get("urls", {})
        user = data.get("user", {})
        created_at_str = data.get("created_at", "")
        try:
            created_at = datetime.fromisoformat(created_at_str.replace("Z", "+00:00"))
        except (ValueError, TypeError):
            created_at = None
        return ImageItem(
            id=data.get("id", ""),
            source=ImageSource.ONLINE,
            url=urls.get("regular") or urls.get("full"),
            thumbnail_url=urls.get("small") or urls.get("thumb"),
            title=data.get("description") or data.get("alt_description") or "",
            description=data.get("description") or "",
            width=data.get("width", 0),
            height=data.get("height", 0),
            author=user.get("name", ""),
            author_url=user.get("links", {}).get("html"),
            download_url=urls.get("full") or urls.get("raw"),
            tags=[tag.get("title", "") for tag in data.get("tags", [])],
            created_at=created_at,
        )

    async def close(self) -> None:
        if self._client and not self._client.is_closed:
            await self._client.aclose()
            self._client = None

    async def __aenter__(self) -> "UnsplashService":
        return self

    async def __aexit__(self, exc_type, exc_val, exc_tb) -> None:
        await self.close()
