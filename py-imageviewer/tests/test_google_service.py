"""Tests for Google Image Search service."""

import pytest

from imageviewer.core.exceptions import APIKeyError
from imageviewer.services.base_search_service import ImageColor, ImageSize, SafeSearch, SearchFilters
from imageviewer.services.google_service import GoogleImageService


class TestGoogleImageService:
    def test_init_without_api_key(self, monkeypatch):
        monkeypatch.delenv("IMAGEVIEWER_GOOGLE_API_KEY", raising=False)
        monkeypatch.delenv("IMAGEVIEWER_GOOGLE_SEARCH_ENGINE_ID", raising=False)
        service = GoogleImageService()
        assert service.api_key == ""
        assert service.search_engine_id == ""

    def test_init_with_api_key(self):
        service = GoogleImageService(api_key="test_key", search_engine_id="test_id")
        assert service.api_key == "test_key"
        assert service.search_engine_id == "test_id"

    def test_name(self):
        service = GoogleImageService()
        assert service.name == "Google Images"

    def test_requires_api_key(self):
        service = GoogleImageService()
        assert service.requires_api_key is True

    @pytest.mark.asyncio
    async def test_search_without_api_key(self):
        service = GoogleImageService(api_key="", search_engine_id="")
        with pytest.raises(APIKeyError):
            await service.search("test")

    def test_build_filter_params(self):
        service = GoogleImageService()
        filters = SearchFilters(
            size=ImageSize.LARGE,
            color=ImageColor.RED,
            safe_search=SafeSearch.MODERATE,
        )
        params = service._build_filter_params(filters)
        assert "imgSize" in params
        assert "safe" in params

    def test_parse_item(self):
        service = GoogleImageService()
        item_data = {
            "cacheId": "test123",
            "link": "https://example.com/image.jpg",
            "title": "Test Image",
            "snippet": "A test image",
            "image": {
                "width": 1920,
                "height": 1080,
                "thumbnailLink": "https://example.com/thumb.jpg",
                "contextLink": "https://example.com",
            },
            "displayLink": "example.com",
        }
        image = service._parse_item(item_data)
        assert image.id == "test123"
        assert image.title == "Test Image"
        assert image.width == 1920
        assert image.height == 1080
