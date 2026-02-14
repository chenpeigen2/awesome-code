"""Tests for data models."""

from pathlib import Path

import pytest

from imageviewer.core.models import (
    AppSettings,
    ImageFormat,
    ImageItem,
    ImageSource,
    ImageTransform,
    SearchQuery,
    SearchResult,
    SortOrder,
    ViewMode,
)


class TestImageItem:
    def test_local_image(self):
        item = ImageItem(
            id="test",
            source=ImageSource.LOCAL,
            file_path=Path("/test/image.jpg"),
            title="Test Image",
        )
        assert item.is_local is True
        assert item.is_online is False

    def test_online_image(self):
        item = ImageItem(
            id="test",
            source=ImageSource.ONLINE,
            url="https://example.com/image.jpg",
        )
        assert item.is_local is False
        assert item.is_online is True

    def test_aspect_ratio(self):
        item = ImageItem(
            id="test",
            source=ImageSource.LOCAL,
            width=1920,
            height=1080,
        )
        assert item.aspect_ratio == pytest.approx(16 / 9, rel=0.01)

    def test_aspect_ratio_zero_height(self):
        item = ImageItem(
            id="test",
            source=ImageSource.LOCAL,
            width=100,
            height=0,
        )
        assert item.aspect_ratio == 1.0


class TestSearchQuery:
    def test_default_values(self):
        query = SearchQuery(query="nature")
        assert query.page == 1
        assert query.per_page == 30
        assert query.order_by == "relevant"
        assert query.color is None
        assert query.orientation is None


class TestSearchResult:
    def test_empty_result(self):
        result = SearchResult()
        assert len(result.images) == 0
        assert result.total == 0
        assert result.has_next is False
        assert result.has_previous is False

    def test_with_images(self):
        images = [
            ImageItem(id="1", source=ImageSource.ONLINE),
            ImageItem(id="2", source=ImageSource.ONLINE),
        ]
        result = SearchResult(images=images, total=100, total_pages=10, current_page=1)
        assert len(result.images) == 2
        assert result.has_next is True
        assert result.has_previous is False


class TestImageTransform:
    def test_default_values(self):
        transform = ImageTransform()
        assert transform.rotation == 0
        assert transform.scale == 1.0
        assert transform.flip_horizontal is False
        assert transform.flip_vertical is False

    def test_reset(self):
        transform = ImageTransform(rotation=90, scale=2.0, flip_horizontal=True)
        transform.reset()
        assert transform.rotation == 0
        assert transform.scale == 1.0
        assert transform.flip_horizontal is False


class TestAppSettings:
    def test_default_values(self):
        settings = AppSettings()
        assert settings.max_concurrent_downloads == 3
        assert settings.auto_save_downloads is True
        assert ".jpg" in settings.supported_formats


class TestEnums:
    def test_image_source(self):
        assert ImageSource.LOCAL == "local"
        assert ImageSource.ONLINE == "online"

    def test_view_mode(self):
        assert ViewMode.LOCAL == "local"
        assert ViewMode.ONLINE == "online"

    def test_sort_order(self):
        assert SortOrder.NAME_ASC == "name_asc"
        assert SortOrder.SIZE_DESC == "size_desc"

    def test_image_format(self):
        assert ImageFormat.JPEG == "jpeg"
        assert ImageFormat.PNG == "png"
