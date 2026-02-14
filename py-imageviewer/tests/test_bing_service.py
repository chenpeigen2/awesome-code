"""Tests for Bing Image Search service."""

import pytest

from imageviewer.core.exceptions import APIKeyError
from imageviewer.services.base_search_service import ImageColor, ImageSize, SearchFilters
from imageviewer.services.bing_service import BingImageService


class TestBingImageService:
    def test_init_without_api_key(self, monkeypatch):
        monkeypatch.delenv("IMAGEVIEWER_BING_API_KEY", raising=False)
        service = BingImageService()
        assert service.api_key == ""

    def test_init_with_api_key(self):
        service = BingImageService(api_key="test_key")
        assert service.api_key == "test_key"

    def test_name(self):
        service = BingImageService()
        assert service.name == "Bing Images"

    def test_requires_api_key(self):
        service = BingImageService()
        assert service.requires_api_key is True

    @pytest.mark.asyncio
    async def test_search_without_api_key(self):
        service = BingImageService(api_key="")
        with pytest.raises(APIKeyError):
            await service.search("test")

    def test_build_filter_params(self):
        service = BingImageService()
        filters = SearchFilters(
            size=ImageSize.LARGE,
            color=ImageColor.BLUE,
        )
        params = service._build_filter_params(filters)
        assert "size" in params

    def test_parse_item(self):
        service = BingImageService()
        item_data = {
            "imageId": "bing123",
            "contentUrl": "https://example.com/image.jpg",
            "name": "Test Image",
            "width": 1920,
            "height": 1080,
            "thumbnailUrl": "https://example.com/thumb.jpg",
            "hostPageDisplayUrl": "example.com",
            "hostPageUrl": "https://example.com",
        }
        image = service._parse_item(item_data)
        assert image.id == "bing123"
        assert image.title == "Test Image"
        assert image.width == 1920
        assert image.height == 1080
