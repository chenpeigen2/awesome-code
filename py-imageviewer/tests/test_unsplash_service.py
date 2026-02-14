"""Tests for Unsplash API service."""

import pytest

from imageviewer.core.exceptions import APIKeyError
from imageviewer.core.models import SearchQuery
from imageviewer.services.unsplash_service import UnsplashService


class TestUnsplashService:
    def test_init_without_api_key(self, monkeypatch):
        monkeypatch.delenv("IMAGEVIEWER_UNSPLASH_API_KEY", raising=False)
        service = UnsplashService()
        assert service.api_key == ""

    def test_init_with_api_key(self):
        service = UnsplashService(api_key="test_key")
        assert service.api_key == "test_key"

    @pytest.mark.asyncio
    async def test_search_without_api_key(self):
        service = UnsplashService(api_key="")
        query = SearchQuery(query="test")
        with pytest.raises(APIKeyError):
            await service.search_photos(query)

    @pytest.mark.asyncio
    async def test_get_photo_without_api_key(self):
        service = UnsplashService(api_key="")
        with pytest.raises(APIKeyError):
            await service.get_photo("test_id")

    @pytest.mark.asyncio
    async def test_get_random_without_api_key(self):
        service = UnsplashService(api_key="")
        with pytest.raises(APIKeyError):
            await service.get_random_photos()

    def test_parse_photo(self):
        service = UnsplashService(api_key="test")
        photo_data = {
            "id": "test123",
            "urls": {
                "regular": "https://example.com/regular.jpg",
                "small": "https://example.com/small.jpg",
            },
            "description": "Test image",
            "width": 1920,
            "height": 1080,
            "user": {
                "name": "Test User",
                "links": {"html": "https://example.com/user"},
            },
            "tags": [{"title": "nature"}, {"title": "landscape"}],
            "created_at": "2024-01-01T00:00:00Z",
        }
        image = service._parse_photo(photo_data)
        assert image.id == "test123"
        assert image.title == "Test image"
        assert image.width == 1920
        assert image.height == 1080
        assert image.author == "Test User"
        assert len(image.tags) == 2
